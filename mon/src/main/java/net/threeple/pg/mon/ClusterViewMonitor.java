package net.threeple.pg.mon;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.shared.util.FileUtils;

public class ClusterViewMonitor extends Observable implements Observer, Runnable {
	final Logger logger = LoggerFactory.getLogger(ClusterViewMonitor.class);
	private String name;
	private int port;
	private PsdNode[] psdNodes;
	private PlacementGroup[] pgs;
	private final Executor executor;
	private final AtomicInteger version;

	public ClusterViewMonitor(String _name, int _port) {
		this.name = _name;
		this.executor = Executors.newCachedThreadPool();
		this.version = new AtomicInteger(0);
		this.port = _port;
	}

	@Override
	public void run() {
		start();
	}
	
	public void start() {
		ServerSocket server = null;
		try {
			server = new ServerSocket(this.port);
			logger.info("集群视图监控节点#{}启动成功, 监听在{}端口", this.name, this.port);
			while(true) {
				Socket socket = server.accept();
				Client client = new Client(socket, this);
				executor.execute(client);
			}
		} catch (IOException e) {
			logger.error("集群视图监控节点#{}启动失败, 失败信息: {}", this.name, e.getMessage());
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}
	
	public void init(String path) {
		new Initiator(path).init();
	}
	
	public static void main(String[] args) {
		ClusterViewMonitor monitor = new ClusterViewMonitor("mon0", 6661);
		monitor.init("D:\\mons\\mon0");
	}

	public PsdNode[] getPsdNodes() {
		return psdNodes;
	}
	
	public int getVersion() {
		return this.version.get();
	}
	
	public PlacementGroup[] getPgs() {
		return pgs;
	}

	public void setPsdNodes(PsdNode[] psdNodes) {
		this.psdNodes = psdNodes;
	}
	
	private class Initiator {
		private final String dataDir;
		
		private Initiator(String _dataDir) {
			this.dataDir = _dataDir;
		}
		
		public void init() {
			createPsds();
			createPgs();
		}
		
		private void createPsds() {
			File psdsDir = new File(FileUtils.joinPath(this.dataDir, "psds"));
			File[] psdDirs = psdsDir.listFiles(new FileFilter() {

				@Override
				public boolean accept(File pathname) {
					return pathname.isDirectory();
				}
				
			});
			
			psdNodes = new PsdNode[psdDirs.length];
			for(File psdDir : psdDirs) {
				int id = Integer.parseInt(psdDir.getName());
				File config = new File(FileUtils.joinPath(psdDir.getAbsolutePath(), "config"));
				Properties prope = new Properties();
				try {
					prope.load(new FileInputStream(config));
					String address = prope.getProperty("address");
					int colon = address.indexOf(':');
					InetAddress iaddress = InetAddress.getByName(address.substring(0, colon));
					int port = Integer.parseInt(address.substring(colon + 1, address.length()));
					logger.debug("成功读取存储节点#{}的配置信息: 地址={}, 端口={}", id, iaddress.getHostAddress(), port);
					PsdNode psd = new PsdNode(id, iaddress, port);
					logger.debug("成功创建存储节点#{}", id);
					psdNodes[id] = psd;
				} catch (FileNotFoundException e1) {
					logger.error("丢失存储节点#{}的配置文件,错误信息:{}", id, e1.getMessage());
				} catch (IOException e1) {
					logger.error("无法读取存储节点#{}的配置文件,错误信息:{}", id, e1.getMessage());
				}
			}
		}
		
		private void createPgs() {
			File pgsDir = new File(FileUtils.joinPath(this.dataDir, "pgs"));
			File[] pgDirs = pgsDir.listFiles(new FileFilter() {

				@Override
				public boolean accept(File pathname) {
					return pathname.isDirectory();
				}
				
			});
			pgs = new PlacementGroup[pgDirs.length];
			for(File pgDir : pgDirs) {
				int id = Integer.parseInt(pgDir.getName());
				File current = new File(FileUtils.joinPath(pgDir.getAbsolutePath(), "current"));
				
				Properties prope = new Properties();
				try {
					prope.load(new FileInputStream(current));
					int placement = Integer.parseInt(prope.getProperty("psd"));
					PlacementGroup pg = new PlacementGroup(id, placement);
					logger.debug("成功创建归置组#{}, 存储位置:{}", id, placement);
					pgs[id] = pg;
				} catch (IOException e1) {
					logger.error("无法读取归置组#{}的配置文件,错误信息:{}", id, e1.getMessage());
				}
			}
		}
	}
}
