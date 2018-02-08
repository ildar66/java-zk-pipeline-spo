package org.uit.director;

import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Конфигуратор проекта
 */
public class ConfigFlexWorkflowEJB {

    static Properties property = new Properties();

    public ConfigFlexWorkflowEJB() {
        System.out.println("FlexWorkflowEJB: Configuration module");
    }

    static {
        ResourceBundle myResources = ResourceBundle.getBundle("DBFlexworkflow");
        addPropertyFromResourceBundle(myResources);
    }

    /**
     * Чтение свойств конфигурационного файла по ключу
     *
     * @param key
     * @return res
     */
    public static String getProperty(String key) {
        return (String) property.get(key);
    }


    public static void setProperty(String key, String value) {
        property.put(key, value);
    }

    public static void addPropertyFromResourceBundle(ResourceBundle rb) {
        Enumeration it = rb.getKeys();
        while (it.hasMoreElements()) {
            String key = (String) it.nextElement();
            String value = rb.getString(key);
            setProperty(key, value);
        }
    }

    public static void addProperty(Properties prop) {
        property.putAll(prop);
    }


}
