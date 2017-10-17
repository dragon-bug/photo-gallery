package net.threeple.pg.mon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.shared.util.FileUtils;

public class ClusterViewMonitor extends Observable implements Observer, Runnable {
	final Logger logger = LoggerFactory.getLogger(ClusterViewMonitor.class);
	private int port;
	private PsdNode[] psdNodes;
	private PlacementGroup[] pgs;
	private final Executor executor;
	private final AtomicInteger version;

	public ClusterViewMonitor(int _port) {
		this.executor = Executors.newCachedThreadPool();
		this.version = new AtomicInteger(0);
		this.port = _port;
	}

	@Override
	public void run() {
		ServerSocket server = null;
		try {
			server = new ServerSocket(this.port);
			logger.info("集群视图监控节点启动成功, 监听在{}端口", this.port);
			while(true) {
				Socket socket = server.accept();
				Client client = new Client(socket, this);
				executor.execute(client);
			}
		} catch (IOException e) {
			logger.error("集群视图监控节点启动失败, 失败信息: {}", e.getMessage());
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
		ClusterViewMonitor monitor = new ClusterViewMonitor(6661);
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
				BufferedReader reader = null;
				try {
					reader = new BufferedReader(new FileReader(config));
					String line = reader.readLine();
					int eqi = line.indexOf('=');
					String sa = line.substring(eqi + 1, line.length());
					int colon = sa.indexOf(':');
					InetAddress address = InetAddress.getByName(sa.substring(0, colon));
					int port = Integer.parseInt(sa.substring(colon + 1, sa.length()));
					logger.debug("成功读取存储节点#{}的配置信息: 地址={}, 端口={}", id, address.getHostAddress(), port);
					PsdNode psd = new PsdNode(id, address, port);
					logger.debug("成功创建存储节点#{}", id);
					psdNodes[id] = psd;
				} catch (FileNotFoundException e) {
					logger.error("丢失存储节点#{}的配置文件,错误信息:{}", id, e.getMessage());
				} catch (IOException e) {
					logger.error("无法读取存储节点#{}的配置文件,错误信息:{}", id, e.getMessage());
				} finally {
					try {
						reader.close();
					} catch (IOException e) {
						logger.warn("存储节点#{}配置文件读取器关闭失败,失败信息:{}", id, e.getMessage());
					}
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
				BufferedReader reader = null;
				try {
					reader = new BufferedReader(new FileReader(current));
					String line = reader.readLine();
					int eqi = line.indexOf('=');
					int placement = Integer.parseInt(line.substring(eqi + 1, line.length()));
					PlacementGroup pg = new PlacementGroup(id, placement);
					logger.debug("成功创建归置组#{}, 存储位置:{}", id, placement);
					pgs[id] = pg;
				} catch (FileNotFoundException e) {
					logger.error("丢失归置组#{}的配置文件,错误信息:{}", id, e.getMessage());
				} catch (IOException e) {
					logger.error("无法读取归置组#{}的配置文件,错误信息:{}", id, e.getMessage());
				} finally {
					try {
						reader.close();
					} catch (IOException e) {
						logger.warn("归置组#{}配置文件读取器关闭失败,失败信息:{}", id, e.getMessage());
					}
				}
			}
		}
	}
}
