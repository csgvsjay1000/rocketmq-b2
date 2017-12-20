package invengo.cn.rocketmq.store.impl;

import java.util.concurrent.atomic.AtomicLong;

public abstract class ReferenceResource {
	protected final AtomicLong refCount = new AtomicLong(1);
	
	protected volatile boolean available = true;
	
	public synchronized boolean hold() {
		if (this.isAvailable()) {
			if (this.refCount.getAndIncrement() > 0) {
				return true;
			}else {
				this.refCount.getAndIncrement();
			}
		}
		return false;
	}
	
	public boolean isAvailable() {
		return available;
	}
	
	public void setAvailable(boolean available) {
		this.available = available;
	} 
	
	
}
