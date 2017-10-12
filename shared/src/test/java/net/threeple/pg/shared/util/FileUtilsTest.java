package net.threeple.pg.shared.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FileUtilsTest {

	@Test
	public void testJoinPath() {
		String joined = FileUtils.joinPath("D:\\psds\\", "\\2017\\10\\someon.jpg");
		assertEquals("连接后的路径并非期望值", joined, "D:\\psds\\2017\\10\\someon.jpg");
	}
}
