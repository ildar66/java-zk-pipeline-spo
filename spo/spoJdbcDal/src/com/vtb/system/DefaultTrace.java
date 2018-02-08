package com.vtb.system;

/**
 * Default trace capability, trace statements are output to console. tracing can be toggled on/off.
 */
public class DefaultTrace implements TraceCapable {
    private int currentLevel = 0;

    /**
     * This method was created in VisualAge.
     */
    public void setLevel(int level) {
        currentLevel = level;
    }

    public int getLevel() {
        return currentLevel;
    }

    /**
     * Output a String to the console....
     * 
     * @param aString
     *            java.lang.String
     */
    public void log(int loglevel, String aString) {
        if (loglevel >= currentLevel) {
            System.out.println(aString);
        }
    }

    /**
     * Вывод ошибки в лог
     */
    public void error(String msg, Throwable e) {
        if (currentLevel <= ERROR_LEVEL) {
            log(ERROR_LEVEL, msg);
            e.printStackTrace();
        }
    }

}
