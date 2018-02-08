package com.vtb.mapping;

/**
 * Insert the type's description here.
 * Creation date: (1/29/2001 11:32:19 AM)
 * @author: ILS User
 */
public class MemoryMapperFactory extends MapperFactory {
/**
 * EjbMapperFactory constructor comment.
 */
public MemoryMapperFactory() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (1/29/2001 11:32:19 AM)
 * @return java.lang.String
 */
protected String getBackendQualifier() {
	return "memory";
}
}
