package net.threeple.pg.api.factory;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import net.threeple.pg.api.AsyncUploader;
import net.threeple.pg.api.factory.PhotoStorageFactory;

public class PhotoStorageFactoryTest {

	@Test
	public void testCreat() {
		AsyncUploader uploader = PhotoStorageFactory.getPhotoStorage(false);
		assertNotNull(uploader);
	}
}
