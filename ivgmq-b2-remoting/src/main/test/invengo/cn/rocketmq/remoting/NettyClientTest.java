package invengo.cn.rocketmq.remoting;

import java.util.concurrent.atomic.AtomicInteger;

import invengo.cn.rocketmq.common.log.Logger;
import invengo.cn.rocketmq.common.log.LoggerFactory;
import invengo.cn.rocketmq.remoting.exception.RemotingSendRequestException;
import invengo.cn.rocketmq.remoting.exception.RemotingTimeoutException;
import invengo.cn.rocketmq.remoting.netty.NettyClientConfig;
import invengo.cn.rocketmq.remoting.netty.NettyRemotingClient;
import invengo.cn.rocketmq.remoting.protocol.RemotingCommand;

public class NettyClientTest {
	
	private static Logger logger = LoggerFactory.getLogger(NettyClientTest.class);

	public static void main(String[] args) {
		final NettyRemotingClient client = new NettyRemotingClient(new NettyClientConfig());
		client.start();
		new Thread(new Runnable() {
			
			public void run() {
				while (true) {
					RemotingCommand request = RemotingCommand.createRequestCommand(1, null);
					request.setBody("hello".getBytes());
					try {
						client.invokeSync("127.0.0.1:8888", request, 3000);
						Thread.sleep(10000);
					} catch (RemotingTimeoutException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (RemotingSendRequestException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		}).start();
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            private volatile boolean hasShutdown = false;
            private AtomicInteger shutdownTimes = new AtomicInteger(0);

            public void run() {
                synchronized (this) {
                	logger.getLogger().info("Shutdown hook was invoked, {}", this.shutdownTimes.incrementAndGet());
                    if (!this.hasShutdown) {
                    	client.shutdown();
                        this.hasShutdown = true;
                        long beginTime = System.currentTimeMillis();
                        long consumingTimeTotal = System.currentTimeMillis() - beginTime;
                        logger.getLogger().info("Shutdown hook over, consuming total time(ms): {}", consumingTimeTotal);
                    }
                }
            }
        }, "ShutdownHook"));
	}
	
}
