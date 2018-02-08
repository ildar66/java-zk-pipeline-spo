package com.vtb.ldap;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.spi.ObjectFactory;

/**
 * @author Admin
 *
 */
public class DirContextFactory implements ObjectFactory {
	/* (non-Javadoc)
	 * @see javax.naming.spi.ObjectFactory#getObjectInstance(java.lang.Object, javax.naming.Name, javax.naming.Context, java.util.Hashtable)
	 */
	public Object getObjectInstance(Object obj, Name n, Context nameCtx, Hashtable environment) throws NamingException {

		Hashtable<String, String> env = new Hashtable<String, String>();
		String url = null;
		Reference ref = (Reference) obj;
		Enumeration addrs = ref.getAll();
		while (addrs.hasMoreElements()) {
			RefAddr addr = (RefAddr) addrs.nextElement();
			String name = addr.getType();
			String value = (String) addr.getContent();
			if (name.equals("URL"))
				url = value;
			else
				env.put(name, value);
		}
		DirContext initial = new InitialDirContext(env);
		if (url == null)
			return initial;
		else
			return initial.lookup(url);
	}
}
