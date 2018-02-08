package org.uit.director.servletutil;


import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;


public class EJBUtils {

    public static Object getLocalEJBObject(String jndiName) {
        return getObj("localhost", jndiName);
    }

    public static Object getRemouteEJBObject(String host, String jndiName) {
        return getObj(host, jndiName);
    }

    private static Object getObj(String host, String jndiName) {

        Properties jndiProperties = new Properties();
        jndiProperties.put("java.naming.provider.url", "jnp://" + host + ":1099");
        InitialContext jndiCtx;
        try {
            jndiCtx = new InitialContext(jndiProperties);
            return jndiCtx.lookup(jndiName);

        } catch (NamingException e) {
            e.printStackTrace();
        }

        return null;

    }

}
