package ru.masterdm.spo.utils;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class CollectStages {
	private static volatile CollectStages instance;
	public final ConcurrentHashMap<Long,HashSet<Long>> cache;

	public static CollectStages singleton() {
		CollectStages localInstance = instance;
		if (localInstance == null) {
			synchronized (CollectStages.class) {
				localInstance = instance;
				if (localInstance == null) {
					instance = localInstance = new CollectStages();
				}
			}
		}
		return localInstance;
	}

	private CollectStages() {
		super();
		cache = new ConcurrentHashMap<Long, HashSet<Long>>();
	}
    public void reset(){
        cache.clear();
    }
}
