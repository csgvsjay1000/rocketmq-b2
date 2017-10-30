package invengo.cn.rocketmq.common.log.impl;

import org.apache.logging.log4j.LogManager;

import invengo.cn.rocketmq.common.log.Logger;
import invengo.cn.rocketmq.common.log.LoggerAdapter;

public class Log4jAdapterImpl implements LoggerAdapter{

	public Logger getLogger(Class<?> clazz) {
		return new Log4jImpl(LogManager.getLogger(clazz));
	}

	public Logger getLogger(org.apache.logging.log4j.Logger logger) {
		return new Log4jImpl(logger);
	}
	
}
