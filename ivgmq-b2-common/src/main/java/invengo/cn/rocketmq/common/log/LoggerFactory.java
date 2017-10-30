package invengo.cn.rocketmq.common.log;


import invengo.cn.rocketmq.common.log.impl.Log4jAdapterImpl;

public class LoggerFactory {
	
	static LoggerAdapter loggerAdapter;
	
	static{
		loggerAdapter = new Log4jAdapterImpl();
	}

/*	public static Logger getLogger(final Class<?> clazz) {
		return loggerAdapter.getLogger();
	}*/
	
	public static Logger getLogger(final Class<?> clazz) {
		return loggerAdapter.getLogger(clazz);
	}
	
}
