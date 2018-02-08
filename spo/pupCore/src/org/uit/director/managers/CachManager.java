/*
 * Created on 27.05.2008
 * 
 */
package org.uit.director.managers;

import java.io.Serializable;
import java.util.Hashtable;

import org.uit.director.tasks.TaskCachInfo;

public abstract class CachManager implements Serializable {
	
	protected Hashtable<Long, TaskCachInfo> cache = null;
	
	

	/**
	 * 
	 */
	public CachManager() {		
		super();
		cache = new Hashtable<Long, TaskCachInfo>();
	}

	public void deleteCacheElement(Long key) {

		if (cache != null) {
			cache.remove(key);
		}
	}
	

	public void deleteAllCach() {

		if (cache != null) {
			{
				cache.clear();
			}

			cache = null;
		}

	}
	
	public void addCache(Long key, TaskCachInfo obj) {
        if (cache == null) {
            cache = new Hashtable<Long, TaskCachInfo>();
        }
        cache.put(key, obj);

    }

    public TaskCachInfo getCacheObj(Long key) {
        if (cache == null) {
			return null;
		}
        return cache.get(key);
    }

    public int getCacheSize() {
        if (cache == null) {
			return 0;
		}
        return cache.size();
    }


    public boolean isEmpty() {

        if (cache != null) {
			return cache.isEmpty();
		}

        return true;

    }

}
