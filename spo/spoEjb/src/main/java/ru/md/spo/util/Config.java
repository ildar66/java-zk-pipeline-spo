package ru.md.spo.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class Config {

    static ResourceBundle myResources;
    
    static private Logger logger = Logger.getLogger(Config.class.getName());

    public Config() {
        logger.info("Configuration project");
    }

    static {
        myResources = ResourceBundle.getBundle("workflow");
    }

    public static String getProperty(String key) {
        if (!myResources.containsKey(key)) return "";
        return myResources.getString(key).trim();
    }

    @SuppressWarnings("unchecked")
    public static Properties getProperties() {
        Properties p = new Properties();
        Enumeration it = myResources.getKeys();
        while (it.hasMoreElements()) {
            String key = (String) it.nextElement();
            String value = myResources.getString(key);
            p.put(key, value);
        }
        return p;
    }

    public static ArrayList<String> getProperties(String templateKey) throws Exception {
        ArrayList<String> values = new ArrayList<String>();
       
        Enumeration<String> keys  = myResources.getKeys();
        String key = null;
        String value = null;
        while (keys.hasMoreElements()) {
            key = keys.nextElement();
            if (key.startsWith(templateKey)) {
                value = new String(myResources.getString(key).getBytes("ISO-8859-1")); 
                values.add(value);
            }
        }
        
        if (values.size() == 0) {
            logger.severe("invalid key \"" + templateKey + "\"");
                
            return null;
        }
        
        values.trimToSize();
        return values;
    }
    public static boolean devMode(){
        try {
            final String DEV_MODE = System.getenv("DEV_MODE");
            if(DEV_MODE!=null && DEV_MODE.equalsIgnoreCase("true")){
                logger.info("DEV_MODE");
                return true;
            }
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
        return false;
    }
    public static boolean enableIntegration(){
        if(devMode())
            return false;
		return !getProperty("skip.int").equalsIgnoreCase("true");//файл workflow.properties
	}
}
