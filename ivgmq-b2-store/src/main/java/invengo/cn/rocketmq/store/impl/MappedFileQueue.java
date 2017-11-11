package invengo.cn.rocketmq.store.impl;

public class MappedFileQueue {

	private final String storePath;
	
	private final int mappedFileSize;
	
	public MappedFileQueue(final String storePath,final int mappedFileSize) {
		
		this.storePath = storePath;
		this.mappedFileSize = mappedFileSize;
	}
	
	
	
}
