package com.vtb.system;

/**
 * This describes a class that is PropertyCapable
 * Creation date: (2/4/00 10:13:17 PM)
 * @author: Administrator
 */
public interface PropertyCapable {
/**
 * Apply file path to a File name and return 
 * with full path
 * Creation date: (2/4/00 10:26:05 PM)
 */
public String applyFilePath(String aFileName);
/**
 * Apply file path to a File name based upon a qualifying value.
 * Return full file path
 * Creation date: (2/4/00 10:26:05 PM)
 */
public String applyFilePath(String aFileName,String aQualifier);
/**
 * Return value for specified key.
 * Creation date: (2/4/00 10:13:28 PM)
 */
public Object at(String aKey);
}
