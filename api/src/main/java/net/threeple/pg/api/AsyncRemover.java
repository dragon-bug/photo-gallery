package net.threeple.pg.api;

import java.util.concurrent.Future;

import net.threeple.pg.api.model.Response;

public interface AsyncRemover {
	public Future<Response> asyncDelete(String uri);
}
