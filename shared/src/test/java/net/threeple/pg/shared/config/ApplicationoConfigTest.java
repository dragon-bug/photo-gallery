package net.threeple.pg.shared.config;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ApplicationoConfigTest {

	@Test
	public void testGetAll() {
		assertEquals("hamilton1861@icloud.com", ApplicationConfig.getAdminEmail());
	}

}
