package net.threeple.pg.mon.node;

import static org.junit.Assert.*;

import org.junit.Test;

public class StorageNodeStatusTest {
	private StorageNode node = new StorageNode();
	
	@Test
	public void testInUp() {
		node.setIn();
		node.setUp();
		assertTrue(node.isIn() && node.isUp());
		assertFalse(node.isOut() || node.isDown());
	}
	
	@Test
	public void testInUpToInDown() {
		node.setIn();
		node.setUp();
		node.setDown();
		assertTrue(node.isIn() && node.isDown());
		assertFalse(node.isUp());
	}
	
	@Test
	public void testInUpToOutUp() {
		node.setIn();
		node.setUp();
		node.setOut();
		assertTrue(node.isOut() && node.isUp());
		assertFalse(node.isIn());
	}
}
