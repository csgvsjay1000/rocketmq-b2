package invengo.cn.rocketmq.remoting;

public interface RemotingService {
	
	void start();

	void shutdown();
	
	void registerRPCHook(RPCHook rpcHook);
	
}
