package invengo.cn.rocketmq.remoting.common;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class SemaphoreReleaseOnlyOnce {

	private final Semaphore semaphore;
	
	private AtomicBoolean once = new AtomicBoolean(false);
	
	public SemaphoreReleaseOnlyOnce(final Semaphore semaphore) {
		this.semaphore = semaphore;
	}
	
	public void release() {
		if (this.semaphore != null) {
			if (once.compareAndSet(false, true)) {
				this.semaphore.release();
			}
		}
	}
	
}
