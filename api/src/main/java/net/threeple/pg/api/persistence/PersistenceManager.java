package net.threeple.pg.api.persistence;

import java.util.concurrent.Future;

import net.threeple.pg.api.model.Response;

public interface PersistenceManager {
	public Future<Response> persist(String uri, byte[] body);
	public Future<Response> get(String uri);
	public Future<Response> destroy(String uri);
}
