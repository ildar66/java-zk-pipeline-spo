package com.vtb.ldap;

import java.util.PropertyResourceBundle;

import javax.naming.CommunicationException;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;

import com.vtb.exception.MappingException;
import com.vtb.system.AppService;
import com.vtb.util.EJBClientHelper;
import com.vtb.value.Name;

/**
 * @author Ildar
 * 
 */
public class LDAP {
	private static final String REFERENCE_NAME_PREFIX = "java:comp/env/";
	// Configuration file:
	private static final String CONFIG_PROP_FILE = "ldapServer";
	private static PropertyResourceBundle properties = (PropertyResourceBundle) PropertyResourceBundle
			.getBundle(CONFIG_PROP_FILE);

	// private static String url = properties.getString("ldap.url");
	// private static String userName = properties.getString("ldap.username");
	// private static String pass = properties.getString("ldap.password");

	private static DirContext context = null;

	// Entry:
	public static class User {
		private String login = null; // Логин
		private Name name = null;// ФИО
		private String eMail = null; // почта оператора

		@Override
		public String toString() {
			return "LDAP User:" + login + ", ФИО =" + name;
		}

		public static String dnPrefix = properties.getString("ldap.user.dn.prefix");
		public static String dnSuffix = properties.getString("ldap.user.dn.suffix");
		public static String login_DN = properties.getString("ldap.user.dn.login");
		public static String last_DN = properties.getString("ldap.user.dn.last");
		public static String first_DN = properties.getString("ldap.user.dn.first");
		public static String middle_DN = properties.getString("ldap.user.dn.middle");
		public static String eMail_DN = properties.getString("ldap.user.dn.eMail");

		public User(String login, String last, String first, String middle, String eMail) {
			super();
			this.login = login;
			this.name = new Name(last, first, middle);
			this.eMail = eMail;
		}

		public String getLogin() {
			return login;
		}

		public String getEMail() {
			return eMail;
		}

		public Name getName() {
			return name;
		}
	}

	/**
	 * Gets a context from the properties specified in the file ldapServer.properties
	 * 
	 * @return the directory context
	 */
	private static DirContext getContext() {
		if (context == null) {
			/**
			 * Hashtable<String, String> env = new Hashtable<String, String>(); env.put(Context.SECURITY_PRINCIPAL,
			 * userName); env.put(Context.SECURITY_CREDENTIALS, pass); DirContext initial = new InitialDirContext(env);
			 * context = (DirContext) initial.lookup(url);
			 */
			InitialContext ic;
			try {
				ic = EJBClientHelper.getInitialContext();
				context = (javax.naming.directory.DirContext) ic.lookup(REFERENCE_NAME_PREFIX + "ldap/vtb");
			} catch (NamingException ne) {
				MappingException e = new MappingException("NamingException: cannot find LDAP source in initialContext");
				AppService.handle(e);
			}
		}
		return context;
	}

	public static User findUser(String uid) throws NamingException {
		String dn = User.dnPrefix + uid + User.dnSuffix;
		Attributes attrs = null;
		try {
			attrs = getContext().getAttributes(dn);
		} catch (NameNotFoundException e) {
			throw e;
		} catch (CommunicationException ce) {
			context = null;
			attrs = getContext().getAttributes(dn);
			ce.printStackTrace();
		}
		String login = get(attrs, User.login_DN);
		String last = get(attrs, User.last_DN);
		String first = get(attrs, User.first_DN);
		String middle = get(attrs, User.middle_DN);
		String eMail = get(attrs, User.eMail_DN);
		User user = new User(login, last, first, middle, eMail);
		return user;
	}

	/**
	 * @param attrs
	 * @return
	 * @throws NamingException
	 */
	private static String get(Attributes attrs, String attrName) throws NamingException {
		Attribute attr = attrs.get(attrName);
		String login = (attr == null) ? "" : (String) attr.get();
		return login;
	}

}
