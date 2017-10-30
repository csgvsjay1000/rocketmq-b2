package invengo.cn.rocketmq.common.log.impl;

import invengo.cn.rocketmq.common.log.Logger;

public class Log4jImpl implements Logger{

	private org.apache.logging.log4j.Logger logger;
	
	public Log4jImpl(org.apache.logging.log4j.Logger logger) {
		this.logger = logger;
	}
	
	public org.apache.logging.log4j.Logger getLogger() {
		return logger;
	}
	
}
