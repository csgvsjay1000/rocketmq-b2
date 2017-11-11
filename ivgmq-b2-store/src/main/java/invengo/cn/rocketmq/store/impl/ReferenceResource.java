package invengo.cn.rocketmq.store.impl;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class ReferenceResource {
	protected final AtomicLong refCount = new AtomicLong(0);
	protected volatile boolean available = true; 
	
}
