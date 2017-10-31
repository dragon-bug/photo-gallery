package net.threeple.pg.api.impl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;

import net.threeple.pg.api.PhotoStorage;
import net.threeple.pg.api.model.IRequest;
import net.threeple.pg.api.model.Response;
import net.threeple.pg.shared.exception.OperationNotSupportedException;

public abstract class AbstractPhotoStorage implements PhotoStorage {

	@Override
	public byte[] download(String uri) {
		throw new OperationNotSupportedException();
	}

	@Override
	public int upload(String uri, byte[] body) {
		throw new OperationNotSupportedException();
	}

	@Override
	public Future<Response> asyncDownload(String uri) {
		throw new OperationNotSupportedException();
	}

	@Override
	public Future<Response> asyncUpload(String uri, byte[] body) {
		throw new OperationNotSupportedException();
	}

	@Override
	public abstract BlockingQueue<IRequest> getQueue();

}
