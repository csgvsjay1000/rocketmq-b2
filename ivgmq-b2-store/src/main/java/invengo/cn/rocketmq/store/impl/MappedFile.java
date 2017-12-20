package invengo.cn.rocketmq.store.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MappedFile extends ReferenceResource{

	private static Logger logger = LogManager.getLogger(MappedFile.class);
	
	private static AtomicLong TOTAL_MAPPED_VIRTUAL_MEMORY = new AtomicLong(0);
    private static final AtomicInteger TOTAL_MAPPED_FILES = new AtomicInteger(0);
    
    protected final AtomicInteger wrotePosition = new AtomicInteger(0);
    protected final AtomicInteger committedPosition = new AtomicInteger(0);
    private final AtomicInteger flushedPosition = new AtomicInteger(0);

	private String fileName;
	
	private int fileSize;
	private File file;
	private long fileFromOffset;
	private FileChannel fileChannel;
	
	private MappedByteBuffer mappedByteBuffer;
	protected ByteBuffer writeBuffer = null;
	
	public MappedFile(final String fileName,final int fileSize) throws IOException {
		init(fileName, fileSize);
	}
	
	public static void ensureDirOK(String dirName) {
		if (null != dirName) {
			File file = new File(dirName);
			if (!file.exists()) {
				boolean result = file.mkdirs();
				logger.info(dirName+" mkdir "+(result?"OK":"Failed"));
			}
		}
	}
	
	private void init(final String fileName, final int fileSize) throws IOException {
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.file = new File(fileName);
		this.fileFromOffset = Long.parseLong(this.file.getName());
		
		ensureDirOK(file.getParent());
		boolean ok = false;
		try {
			this.fileChannel = new RandomAccessFile(this.file, "rw").getChannel();
			this.mappedByteBuffer = this.fileChannel.map(MapMode.READ_WRITE, 0, fileSize);
			TOTAL_MAPPED_VIRTUAL_MEMORY.addAndGet(fileSize);
			TOTAL_MAPPED_FILES.incrementAndGet();
			ok = true;
		} catch (FileNotFoundException e) {
			logger.error("create file channel "+this.fileName+" failed.",e);
			throw e;
		}catch (IOException e) {
			logger.error("map file "+this.fileName+" failed.",e);
			throw e;
		}finally {
			if (!ok && this.fileChannel != null) {
				this.fileChannel.close();
			}
		}
		
	}
	
	public boolean appendMessage(final byte[] data) {
		int currentPos = this.wrotePosition.get();
		
		if ((currentPos + data.length) < this.fileSize) {
			try {
				this.fileChannel.position(currentPos);
				this.fileChannel.write(ByteBuffer.wrap(data));
			} catch (IOException e) {
				logger.error("Error occurred when append message to mappedFile.",e);
			}
			this.wrotePosition.addAndGet(data.length);
			return true;
		}
		
		return false;
	}
	
	public SelectMappedBufferResult selectMappedBuffer(int pos) {
		int readPosition = getReadPosition();
		if (pos < readPosition && pos >= 0) {
			if (this.hold()) {
				ByteBuffer byteBuffer = this.mappedByteBuffer.slice();
				byteBuffer.position(pos);
				int size = readPosition - pos;
				ByteBuffer byteBufferNew = byteBuffer.slice();
				byteBufferNew.limit(size);
				return new SelectMappedBufferResult(this.fileFromOffset+pos, byteBufferNew, size, this);
			}
		}
		
		return null;
	}
	
	private int getReadPosition(){
		return this.writeBuffer == null? this.wrotePosition.get() : this.committedPosition.get();
	}
	
	
	
}
