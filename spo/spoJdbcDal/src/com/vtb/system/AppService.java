package com.vtb.system;

import java.util.*;

/**
 * This class is a generic collection of Application Services. This includes facilities for Tracing, Exception handling, etc.
 * 
 * Creation date: (2/4/00 10:59:47 PM)
 * 
 * @author: Administrator
 */
public class AppService {
    static public TraceCapable trace = new DefaultTrace(); // Default Trace

    static public StartupCapable startup = null;

    static public PropertyCapable property = null;

    static final private Vector<ExceptionHandler> handlers = new Vector<ExceptionHandler>(); // Exception Handlers...

    static final ExceptionHandler defaultHandler = new DefaultExceptionHandler();

    /**
     * AppService constructor.
     */
    public AppService() {
        super();
    }

    /**
     * Enumerate over installed handlers and delegate ExceptionEvent to handlers....
     */
    public static void handle(Throwable e) {
        Enumeration elements = handlers.elements();
        ExceptionEvent event = new ExceptionEvent(e);
        while (elements.hasMoreElements()) {
            ExceptionHandler h = (ExceptionHandler) elements.nextElement();
            h.handle(event);
        }
        if (defaultHandler != null) {
            defaultHandler.handle(event);
        }
    }

    /**
     * Install ExceptionHandler that will be dispatched a ExceptionEvent . Creation date: (2/5/00 4:03:35 PM)
     */
    public static void install(ExceptionHandler aHandler) {
        handlers.addElement(aHandler);
    }

    /**
     * Return property value for akey. Creation date: (2/5/00 4:03:35 PM)
     */
    public static Object propertyAt(String aKey) {
        return property.at(aKey);
    }

    /**
     * Report a trace message Creation date: (2/5/00 4:03:35 PM)
     */
    public static void log(int level, String aString) {
        trace.log(level, aString);
    }

    /**
     * Вывод ошибки в лог
     * 
     * @param msg текст ошибки
     * @param e объект ошибки
     */
    public static void error(String msg, Throwable e) {
        trace.error(msg, e);
    }

    /**
     * Вывод ошибки в лог на уровне DEBUG
     * @param msg текст ошибки
     */
    public static void debug(String msg) {
        log(TraceCapable.DEBUG_LEVEL, msg);
    }

    /**
     * Вывод ошибки в лог на уровне INFO
     * @param msg текст ошибки
     */
    public static void info(String msg) {
        log(TraceCapable.INFO_LEVEL, msg);
    }


}
