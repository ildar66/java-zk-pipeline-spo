package com.vtb.model;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vtb.util.ApplProperties;

/**
 * Insert the type's description here.
 * Creation date: (2/27/2001 1:45:50 PM)
 * @author: ILS User
 */

public class ActionProcessorFactory {

    private final static Logger logger = Logger.getLogger(ActionProcessorFactory.class.getName());

    private static final Map<String, Object> processorCachen = new HashMap<String, Object>();;

    /**
     * ActionProcessorFactory constructor comment.
     */
    public ActionProcessorFactory() {
        super();
    }

    /**
     * Derive the name of the package and class based on the pattern
     * Creation date: (3/2/2001 4:40:57 PM)
     * @return String
     */
    public static String computeActionProcessorClassName(String processorType) {
        String packageName = ApplProperties.PACKAGE_PREFIX + ApplProperties.getModelType().toLowerCase();
        String className = processorType + "ActionProcessorImpl";
        return packageName + "." + className;
    }

    /**
     * @return adapter to application Model 
     */
    public static Object getActionProcessor(String name) {
        Object processor = processorCachen.get(name);
        if (processor == null) {
            String classname = computeActionProcessorClassName(name);
            Class<?> aClass = null;
            try {
                aClass = Class.forName(classname);
                processor = aClass.newInstance();
                processorCachen.put(name, processor);
            } catch (ClassNotFoundException e) {
                logger.log(Level.WARNING, "Ошибка при загрузке класса  " + classname, e);
            } catch (InstantiationException e) {
                logger.log(Level.WARNING, "Ошибка при создании объекта класса  " + classname, e);
            } catch (IllegalAccessException e) {
                logger.log(Level.WARNING, "Ошибка при создании объекта класса  " + classname, e);
            }
        }
        return processor;
    }

}