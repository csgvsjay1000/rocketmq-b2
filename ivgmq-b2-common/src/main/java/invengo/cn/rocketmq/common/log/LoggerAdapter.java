package invengo.cn.rocketmq.common.log;

public interface LoggerAdapter {

	public Logger getLogger(final Class<?> clazz);
	public Logger getLogger(org.apache.logging.log4j.Logger logger) ;
	
}
