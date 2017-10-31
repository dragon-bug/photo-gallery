package net.threeple.pg.api.handler;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.api.model.IRequest;
import net.threeple.pg.shared.constant.Operation;

public abstract class Handler {
	final Logger logger = LoggerFactory.getLogger(Handler.class);
	private Handler nextHandler;
	private Operation operation;
	
	public Handler(Operation _operation) {
		this.operation = _operation;
	}
	
	public final void handle(IRequest request) throws IOException {
		if(this.operation.equals(request.getOperation())) {
			this.response(request);
		} else {
			if(this.nextHandler != null) {
				this.nextHandler.handle(request);
			} else {
				logger.warn("请求{}找不到对应的处理器");
			}
		}
	}
	
	public void setNext(Handler handler) {
		this.nextHandler = handler;
	}
	
	protected abstract void response(IRequest request) throws IOException;
	
}
