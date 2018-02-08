package com.vtb.system;

/**
 * This describes a class that is TraceCapable. Creation date: (2/4/00 10:10:12 PM)
 * 
 * @author: Administrator
 */
public interface TraceCapable {

    public static int DEBUG_LEVEL = 1;

    public static int INFO_LEVEL = 2;

    public static int ERROR_LEVEL = 3;

    /**
     * Return the current log level Creation date: (2/4/00 10:10:22 PM)
     */
    public int getLevel();

    /**
     * set the current log level Creation date: (2/4/00 10:10:22 PM)
     */
    public void setLevel(int level);

    /**
     * Report message string. Creation date: (2/4/00 10:10:22 PM)
     */
    public void log(int logLevel, String aMessage);

    /**
     * Вывод ошибки в лог
     * @param msg  текст ошибки
     * @param e объект ошибки
     */
    public void error(String msg, Throwable e);
}
