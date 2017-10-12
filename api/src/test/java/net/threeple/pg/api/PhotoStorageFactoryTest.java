package net.threeple.pg.api;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import net.threeple.pg.api.factory.PhotoStorageFactory;

public class PhotoStorageFactoryTest {

	@Test
	public void testCreat() {
		AsyncUploader uploader = PhotoStorageFactory.getPhotoStorage(false);
		assertFalse(uploader == null);
	}
}
