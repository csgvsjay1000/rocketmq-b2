package invengo.cn.rocketmq.store.impl;

import java.nio.ByteBuffer;

public class SelectMappedBufferResult {

	private final long startOffset;
	
	private final ByteBuffer byteBuffer;
	
	private int size;
	
	private MappedFile mappedFile;
	
	public SelectMappedBufferResult(final long startOffset,final ByteBuffer byteBuffer,int size,MappedFile mappedFile) {
		this.startOffset = startOffset;
		this.byteBuffer = byteBuffer;
		this.size = size;
		this.mappedFile = mappedFile;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public MappedFile getMappedFile() {
		return mappedFile;
	}

	public void setMappedFile(MappedFile mappedFile) {
		this.mappedFile = mappedFile;
	}

	public long getStartOffset() {
		return startOffset;
	}

	public ByteBuffer getByteBuffer() {
		return byteBuffer;
	}
	
	
}
