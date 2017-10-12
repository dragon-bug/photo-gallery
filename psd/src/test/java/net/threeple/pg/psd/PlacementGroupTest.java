package net.threeple.pg.psd;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

public class PlacementGroupTest {

	@Test
	public void testRead() throws IOException, NoSuchAlgorithmException {
		String root = "D:\\psd0";
		PlacementGroup pg = new PlacementGroup(0, root);
		byte[] body = pg.read("design\\photo-gallery-psd.jpg");
		MessageDigest md = MessageDigest.getInstance("MD5");
		DigestInputStream dis = new DigestInputStream(new ByteArrayInputStream(body), md);
		dis.read(body, 0, body.length);
		byte[] output = dis.getMessageDigest().digest();
		dis.close();
		System.out.println(Hex.encodeHexString(output));
		
		File file = new File("D:\\psd0\\0\\design\\photo-gallery-psd.jpg");
		body = new byte[(int) file.length()];
		dis = new DigestInputStream(new FileInputStream(file), md);
		dis.read(body, 0, body.length);
		output = dis.getMessageDigest().digest();
		dis.close();
		System.out.println(Hex.encodeHexString(output));
		
	}
	
	@Test
	public void testWrite() throws IOException, NoSuchAlgorithmException {
		String root = "D:\\psd0";
		PlacementGroup pg = new PlacementGroup(0, root);
		byte[] body = pg.read("design\\photo-gallery-psd.jpg");
		
		String jpg = "design\\bk\\photo-gallery-psd-cp.jpg";
		pg.write(jpg, body);
	}
}
