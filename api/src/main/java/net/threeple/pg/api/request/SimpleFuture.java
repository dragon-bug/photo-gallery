package net.threeple.pg.api.request;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleFuture<V> implements Future<V> {
	private volatile Object result;
	private final ReentrantLock lock = new ReentrantLock();
	
	static final class AltResult { // See above
        final Throwable ex;        // null only for NIL
        AltResult(Throwable x) { this.ex = x; }
    }
	
	final boolean internalComplete(Object r) { 
		boolean c = false;
		lock.lock(); // block until condition holds
		try {
			if (this.result == null) {
				this.result = r;
				c = true;
			} 
		} finally {
			lock.unlock();
		}
		return c;
    }
	
	private Object waitingGet(boolean interruptible) {
		int spins = -1;
		Object r;
		 while ((r = result) == null) {
	            if (spins < 0)
	                spins = (Runtime.getRuntime().availableProcessors() > 1) ?
	                    1 << 8 : 0; // Use brief spin-wait on multiprocessors
	            else if (spins > 0) {
	                if (ThreadLocalRandom.current().nextLong() >= 0)
	                    --spins;
	            } else {
	            	return waitingGet(true);
	            }
		 }
		return r;
	}
	
	private Object timedGet(long nanos) throws TimeoutException {
		if (Thread.interrupted())
            return null;
        if (nanos <= 0L)
            throw new TimeoutException();
        long d = System.nanoTime() + nanos;
        Object r;
        int spins = -1;
        while ((r = this.result) == null) {
        	if((nanos = d - System.nanoTime()) > 0) {
        		if (spins < 0)
	                spins = (Runtime.getRuntime().availableProcessors() > 1) ?
	                    1 << 8 : 0; // Use brief spin-wait on multiprocessors
	            else if (spins > 0) {
	                if (ThreadLocalRandom.current().nextLong() >= 0)
	                    --spins;
	            } else {
	            	return timedGet(nanos);
	            }
        	} else {
        		throw new TimeoutException();
        	}
        }
		return r;
	}
	
	private V reportGet(Object r) throws InterruptedException, ExecutionException {
		if (r == null) // by convention below, null means interrupted
            throw new InterruptedException();
        if (r instanceof AltResult) {
            Throwable x, cause;
            if ((x = ((AltResult)r).ex) == null)
                return null;
            if (x instanceof CancellationException)
                throw (CancellationException)x;
            if ((x instanceof CompletionException) &&
                (cause = x.getCause()) != null)
                x = cause;
            throw new ExecutionException(x);
        }
        @SuppressWarnings("unchecked") V t = (V) r;
        return t;
	}
	
	@Override
	public boolean cancel(boolean arg0) {
		boolean canceld = (this.result == null) &&
	            internalComplete(new AltResult(new CancellationException()));
		return canceld || isCancelled();
	}

	@Override
	public V get() throws InterruptedException, ExecutionException {
		Object r;
        return reportGet((r = this.result) == null ? waitingGet(true) : r);
	}

	@Override
	public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		Object r;
        long nanos = unit.toNanos(timeout);
        return reportGet((r = this.result) == null ? timedGet(nanos) : r);
	}

	@Override
	public boolean isCancelled() {
		Object r;
        return ((r = this.result) instanceof AltResult) &&
            (((AltResult)r).ex instanceof CancellationException);
	}

	@Override
	public boolean isDone() {
		return result != null;
	}
	
	public boolean complete(V _value) {
		return internalComplete(_value);
	}

}
