package invengo.cn.rocketmq.namesrv;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.Test;

public class LockTest {

	private static ReentrantLock reentrantLock = new ReentrantLock();
	
	@Test
	public void testReenLock() throws InterruptedException {
		
		int threadNums = 10;
		final CountDownLatch latch = new CountDownLatch(threadNums);
		for (int i = 0; i < threadNums; i++) {
			new Thread(new Runnable() {
				
				public void run() {
					// TODO Auto-generated method stub
					reentrantLock.lock();
					System.out.println("get a lock");
					try {
						Thread.sleep(1000);
						latch.countDown();

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//reentrantLock.unlock();
				}
			}).start();;
		}
		//latch.await();
		//Thread.currentThread().join();
	}
	
}
