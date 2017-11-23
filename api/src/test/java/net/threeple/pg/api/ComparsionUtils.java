package net.threeple.pg.api;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.security.DigestInputStream;
import java.security.MessageDigest;

import org.apache.commons.codec.binary.Hex;

import net.threeple.pg.shared.config.ClusterConfig;
import net.threeple.pg.shared.util.FileUtils;
import net.threeple.pg.shared.util.PlacementCalculator;

public class ComparsionUtils {
	private static int pgQuantity = ClusterConfig.getPlacementGroupQuantity(); 
	
	public static String digest(byte[] data) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		DigestInputStream dis = new DigestInputStream(new ByteArrayInputStream(data), md);
		dis.read(data, 0, data.length);
		byte[] out = dis.getMessageDigest().digest();
		return Hex.encodeHexString(out);
	}
	
	public static String digest(String filename) throws Exception {
		int pg = PlacementCalculator.calculate(filename, pgQuantity);
		int psdid = pg < 4 ? 0 : 1;
		File file = new File(FileUtils.joinPath(SimplePsdServer.getStoragePath(), 
				"psd" + String.valueOf(psdid), String.valueOf(pg), filename));
		byte[] body = FileUtils.read(file);
		return ComparsionUtils.digest(body);
	}
}
