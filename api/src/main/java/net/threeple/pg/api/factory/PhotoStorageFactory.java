package net.threeple.pg.api.factory;

import java.lang.reflect.Constructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.api.PhotoStorage;
import net.threeple.pg.api.impl.AsyncPhotoStorage;

public class PhotoStorageFactory {
	final static Logger logger = LoggerFactory.getLogger(PhotoStorageFactory.class);
	private static AsyncPhotoStorage photoStorage;
	
	static {
		try {
			Class<?> cl = Class.forName(AsyncPhotoStorage.class.getName());
			Constructor<?> constructor = cl.getDeclaredConstructor();
			constructor.setAccessible(true);
			photoStorage = (AsyncPhotoStorage)constructor.newInstance();
		} catch (Exception e) {
			logger.error("无法实例化PhotoStorage");
		}
	}
	
	public static PhotoStorage getPhotoStorage(boolean sync) {
		if(!sync) {
			return photoStorage;
		} else {
			return null;
		}
	}
}
