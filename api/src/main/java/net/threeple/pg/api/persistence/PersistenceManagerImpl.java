package net.threeple.pg.api.persistence;

import java.util.concurrent.Future;

import net.threeple.pg.api.PhotoStorage;
import net.threeple.pg.api.factory.PhotoStorageFactory;
import net.threeple.pg.api.model.Response;

public class PersistenceManagerImpl implements PersistenceManager {
	private PhotoStorage storage = PhotoStorageFactory.getPhotoStorage(false);
	
	@Override
	public Future<Response> persist(String uri, byte[] body) {
		return storage.asyncUpload(uri, body);
	}

	@Override
	public Future<Response> get(String uri) {
		return storage.asyncDownload(uri);
	}

	@Override
	public Future<Response> destroy(String uri) {
		return storage.asyncDelete(uri);
	}

}
