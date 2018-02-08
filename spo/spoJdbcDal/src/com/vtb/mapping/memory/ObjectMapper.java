package com.vtb.mapping.memory;

import java.io.Serializable;
import java.util.ArrayList;
import com.vtb.exception.NoSuchObjectException;
import com.vtb.mapping.Mapper;
/**
 * This class represents a simple, "in-memory" mapper for VtbObjects.
 * It stores the objects in a ArrayList.
 *
 * Creation date: (2/3/00 8:59:24 AM)
 * @author: Administrator
 */
public abstract class ObjectMapper<T> implements Mapper<T>, Serializable {
	//protected ArrayList cache = null;
	/**
	 * ObjectMapper constructor.
	 */
	public ObjectMapper() {
		super();
	}
	
	protected abstract ArrayList<T> getCache();
	
	/**
	 * Remove all entries from the cache.
	 * Required for the JUnit Test Cases.
	 */
	public abstract void clearCache();
	
	/**
	 * Remove this T from the cache.
	 */
	public void remove(T anObject) {
		ArrayList cache = getCache();
		synchronized(this.getClass()) {
			cache.remove(anObject);
		}
	}
	/**
	 * Return a ArrayList of VtbObjects for a Mapper.
	 * This should be overriden for each Mapper that requires
	 * an initial population of objects
	 *
	 * Creation date: (2/3/00 9:15:42 AM)
	 * @return java.util.ArrayList
	 */
	abstract ArrayList initialLoad();

	/**
	 * Insert T into cache, and toggle persistent flag.
	 */
	public void insert(T anObject) {
		synchronized(this.getClass()) {
			ArrayList<T> cache = getCache();
			cache.add(anObject);
		}
	}

	/**
	 * Retrieve all objects in this mapper.
	 * if first access, load cache first.
	 */
	public java.util.ArrayList<T> findAll() {
			ArrayList cache = getCache();
		return (ArrayList<T>) cache.clone();
	}

	/**
	 * Default retrieveSingleObjectMatching() returns null
	 */
	public abstract T findByPrimaryKey(T anObject) throws NoSuchObjectException;
	
	/**
	 * The default update() method does a remove and an insert
	 */
	public void update(T anObject) {
		remove(anObject);
		insert(anObject);
	}
}