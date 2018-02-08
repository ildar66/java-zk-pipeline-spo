package ru.masterdm.spo.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Logger;

/**
 * This class performs simple date formatting
 */
public class Formatter {

	private static Hashtable<String, SimpleDateFormat> formatters;
	private static SimpleDateFormat defaultFormatter;
	private String displayFormat;
	private SimpleDateFormat formatter;
    private static transient final SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
    private static transient final SimpleDateFormat dfDT = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	private static boolean WAR_CONDITIONS = false;  // when on. formatting is disabled.  
	                                                // when off, formatting is enabled.
	private static boolean logging = false;  // logging is on/off.  

	private static final Logger logger = Logger.getLogger(Formatter.class.getName());
    
	/****************************************************************************************************************/
    /*                                                                                                              */
    /*                                   Initializaton and helper methods                                           */
    /*                                                                                                              */
    /****************************************************************************************************************/
	
	// Static initializer to support Singleton
	private static void initialize() {
		formatters = new Hashtable<String, SimpleDateFormat>();
		
		SimpleDateFormat mdy = new SimpleDateFormat("MM/dd/yyyy");
		formatters.put("MM/dd/yyyy", mdy);
		
		SimpleDateFormat ymd = new SimpleDateFormat("yyyy.MM.dd");
		formatters.put("yyyy.MM.dd", ymd);
		
		SimpleDateFormat dmy = new SimpleDateFormat("dd.MM.yyyy");
        formatters.put("dd.MM.yyyy", dmy);
        
        SimpleDateFormat pup1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        formatters.put("yyyy-MM-dd HH:mm:ss.S", pup1);
        
        SimpleDateFormat pup2 = new SimpleDateFormat("yyyy-MM-dd.HH.mm.ss");
        formatters.put("yyyy-MM-dd.HH.mm.ss", pup2);
        
        SimpleDateFormat pup4 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatters.put("yyyy-MM-dd HH:mm:ss", pup4);
        //TODO 2011-3-25.10.39. 58. 0
		defaultFormatter = dmy;
	}
	
	/**
	 * Formatter constructor comment.
	 */
	public Formatter() {
		// return a clone of the default Formatter!
	    SimpleDateFormat defaultForm = getDefaultFormatter();
		formatter = defaultForm;
	}

	public Formatter(String dateFormateStr) {
		formatter = new SimpleDateFormat(dateFormateStr);
		displayFormat = dateFormateStr;
	}

	public static SimpleDateFormat getDefaultFormatter() {
		if (defaultFormatter == null) 
		initialize();
		return defaultFormatter;
	}
	
    /**
     * Gets the displayFormat
     * @return Returns a String
     */
    public String getDisplayFormat() {
        return displayFormat;
    }
    /**
     * Sets the displayFormat
     * @param displayFormat The displayFormat to set
     */
    public void setDisplayFormat(String displayFormat) {
        this.displayFormat = displayFormat;
    }

    /**
     * Gets the formatter
     * @return Returns a SimpleDateFormat
     */
    public SimpleDateFormat getFormatter() {
        return formatter;
    }
    /**
     * Sets the formatter
     * @param formatter The formatter to set
     */
    public void setFormatter(SimpleDateFormat formatter) {
        this.formatter = formatter;
    }

    public static SimpleDateFormat findFormatter(String dateString) {
        if (formatters == null) initialize();
        SimpleDateFormat formatter = (SimpleDateFormat)formatters.get(dateString);
        if (formatter == null)
            return defaultFormatter;
        return formatter;
    }
    
    public static SimpleDateFormat[] getFormatters() {
        if (formatters == null) initialize();
        SimpleDateFormat[] fs = new SimpleDateFormat[formatters.size()];
        Enumeration elems = formatters.elements();
        for (int i=0; elems.hasMoreElements(); i++) {
            fs[i] = (SimpleDateFormat)elems.nextElement();         
        }
        return fs;
    }
    
    /**
     * Returns String representation of the Date, formatted as 'dd.MM.yyyy' 
     * @param value any Object type
     * @return String representation of the Object
     */
    public static String str(Date value) {
        if (value == null) return "";
        String result = null;
        try {
            result = df.format(value);
        } catch (Exception e) {}
        if (result == null) return "";
        return result;
    }
    
    /**
     * Returns String if not null, otherwise "" 
     * @param String str
     * @return String
     */
    public static String str(String str) {
        return (str != null) ? str : "";
    }
    public static String cut(String str,int lenght) {
    	String s = str(str);
    	if(s.length()>lenght)
    		return s.substring(0, lenght);
    	return s;
    }
    
    /**
     * Returns String representation of the Object, if not null. If null, returns "" 
     * @param value any Object type
     * @return String representation of the Object
     */
    public static String str(Object value) {
        if (value == null) return "";
        return value.toString();
    }
    
    public String toString() {
        return getDisplayFormat();
    }

    /**
     * " dsfsdf dfd ere dfdf " -> "dsfsdfdfderedfdf " 
     * Это просто пипец!!! В Java хрен найдешь пробелы. Стандартный regexp не помогает почему-то (\s, например).
     * @param s
     * @return
     */    
    public static String trimSpace(String s){
        if (s == null) return null;
        StringBuilder trimmedString = new StringBuilder();
        for (int i=0; i < s.length( ); i++) {
            int value = (int)(s.charAt(i)); 
            if ((value != 160) && (value != 32)) trimmedString.append(s.charAt(i));
        }
        if (logging) logger.info("trimSpace: String(" + s + ") -> String("+ trimmedString.toString() + ")");
        return trimmedString.toString();
    }
    
    /**
     * "   dsfsdf dfd ere dfdf " -> "   " 
     * Finds necessary prefix. Это просто пипец!!! В Java хрен найдешь пробелы. Стандартный regexp не помогает почему-то (\s, например).
     * @param s
     * @return
     */    
    public static String findPrefix(String s){
        if (s == null) return null;
        StringBuilder prefix = new StringBuilder();
        int i=0;
        while((i<s.length()) && 
              (((int)(s.charAt(i)) == 160) || ((int)(s.charAt(i)) == 32)) ) {
            prefix.append(s.charAt(i));
            i++;
        }
        return prefix.toString();
    }
    
    /****************************************************************************************************************/
    /*                                                                                                              */
    /*                                           Formatting data as Strings                                         */
    /*                                                                                                              */
    /****************************************************************************************************************/
	public static String format(Long val){
		if (val==null)return "";
		else return val.toString();    
	}
    public static String format(Integer val){
		if (val==null)return "";
		else return val.toString();
	}
    public static String format(BigDecimal val){
        if (val==null)return "";
        if (WAR_CONDITIONS) return String.valueOf(val);    
        else return toMoneyFormat(val.doubleValue());    
    }

    /**
     * Два знака после запятой.
     */
    public static String format2point(Double val){
    	if (val == null) return "";
    	Double rounded = Math.round(val * 100.0)/100.0;
    	String result = localeIndependentFormatting("###,##0.00", rounded);
    	String res0 = result.replaceAll("\\.", ",").replaceAll(",", " ").replaceAll(",", " ");
    	StringBuilder my = new StringBuilder(res0);
    	my.setCharAt(res0.length()-3, ',');
    	result = my.toString();
    	return result;
    }

    /**
     * Два знака после запятой.
     */
    public static String format2point(BigDecimal val){
    	if (val == null) return "";
    	Double rounded = Math.round(val.doubleValue() * 100.0)/100.0;
    	String result = localeIndependentFormatting("###,##0.00", rounded);
    	String res0 = result.replaceAll("\\.", ",").replaceAll(",", " ").replaceAll(",", " ");
    	StringBuilder my = new StringBuilder(res0);
    	my.setCharAt(res0.length()-3, ',');
    	result = my.toString();
    	return result;
    }

    /**
     * Три знака после запятой.
     */
    public static String format3point(Double val){
    	if (val == null) return "";
    	Double rounded = Math.round(val * 1000.0)/1000.0;
    	String result = localeIndependentFormatting("###,##0.000", rounded);
        String res0 = result.replaceAll("\\.", ",").replaceAll(",", " ").replaceAll(",", " ");
        StringBuilder my = new StringBuilder(res0);
	   	my.setCharAt(res0.length()-4, ',');
	   	result = my.toString();
    	return result;
    }
    /**
     * Три знака после запятой.
     */
    public static String format3point(BigDecimal val){
    	if (val == null) return "";
    	Double rounded = Math.round(val.doubleValue() * 1000.0)/1000.0;
    	String result = localeIndependentFormatting("###,##0.000", rounded);
        String res0 = result.replaceAll("\\.", ",").replaceAll(",", " ").replaceAll(",", " ");
        StringBuilder my = new StringBuilder(res0);
	   	my.setCharAt(res0.length()-4, ',');
	   	result = my.toString();
    	return result;
    }
    private static String localeIndependentFormatting(String format, Double number) {
        DecimalFormatSymbols independent = new DecimalFormatSymbols();
        independent.setDecimalSeparator(',');
        independent.setGroupingSeparator(' ');
        DecimalFormat formatter = new DecimalFormat(format, independent);
        return formatter.format(number);
    }
    /**
     * Один знак после запятой.
     */
    public static String format1point(Double val){
    	if (val == null) return "";
    	Double rounded = Math.round(val * 10.0)/10.0;
    	String result = localeIndependentFormatting("###,##0.0", rounded);
        String res0 = result.replaceAll("\\.", ",").replaceAll(",", " ").replaceAll(",", " ");
        StringBuilder my = new StringBuilder(res0);
	   	my.setCharAt(res0.length()-2, ',');
	   	result = my.toString();
    	return result;
    }
    public static String format(Double val){
        if (val==null)return "";
        if (WAR_CONDITIONS) return String.valueOf(val);    
        else {
//            NumberFormat df = NumberFormat.getNumberInstance(new Locale("ru","RU"));
//            return trimSpace(df.format(val)).replaceAll(",", ".");
            return toMoneyFormat(val);
        }

        //мы используем российский формат чисел, только точку вместо запятой для разделения дробной части
        //если передумаем и решим использовать запятую, то нужно еще в java-script убрать автоматическую замену запятой на точку
        //и в обработки формы тоже
    }

    /**
    * Округляем до двух знаков, выделяем пробелами группы, меняем точку на запятую
    * вот так:    34 344 342 534,45
    * для значений типа 18.0 будет выведено 18,00
    * для значений типа 18.4356 будет выведено 18,44
    * @param value
    * @return
    */
    public static String toMoneyFormat(Double value) {
        if (value == null) return "";
        return toMoneyFormat(String.valueOf(value));
    }
    
    /**
     * Выделяем пробелами группы
     * @param value
     * @return
     */
    public static String toMoneyFormat(BigDecimal value) {
        if (value == null) return "";
        return toMoneyFormat(String.valueOf(value));
    }

    
    /**
    * Округляем до двух знаков, выделяем пробелами группы, меняем точку на запятую
    * вот так:    34 344 342 534,45
    * для значений типа 18.0 будет выведено 18,00
    * для значений типа 18.4356 будет выведено 18,44
    * @param value
    * If cannot convert, returns empty string ("")
    */
    public static String toMoneyFormat(String value) {
    	if (value == null) return "";
        else {
            // число уже может быть отформатировано. 33 343 983,00   Преобразуем к каноническому виду
            String trimmed = trimSpace(value.trim()).replaceAll(",", ".");
            String result = null;
            try {
                // check whether is number
                Double number = Double.parseDouble(trimmed);
                // round up the number
                Double rounded = Math.round(number * 100.0)/100.0; 
                //if (this.getStyleClass().equals("money")) {
                    // и подменим точку, если есть, на запятую. Чтобы было независимо от локали.
                    result = localeIndependentFormatting("###,##0.00", rounded);
	                String res0 = result.replaceAll("\\.", ",").replaceAll(",", " ").replaceAll(",", " ");
                    StringBuilder my = new StringBuilder(res0);
            	   	my.setCharAt(res0.length()-3, ',');
            	   	result = my.toString();
                //}
            } catch (Exception e) {
                result = "";
            }
            return result;
        }
    }
    
    /****************************************************************************************************************/
    /*                                                                                                              */
    /*                                   Methods of parsing Strings into data                                       */
    /*                                                                                                              */
    /****************************************************************************************************************/
    
    /**
     * Parse string and returns Double. Handles exceptions.
     * @param paramName
     * @return Double value of a string
     * If cannot convert, returns null
     */
    public static Double parseDouble(String value) {
        Double res = null;
        try {
            if (value != null) res = Double.parseDouble(trimSpace(value.trim()).replaceAll(",","."));
        } catch (Exception e) {
            res = null;
        } finally {
            if (logging) logger.info("parseDouble: String(" + value + ") -> Double(" + res + ")");
        }
        return res;
    }
    
    /**
     * Parse string and returns BigDecimal. Handles exceptions.
     * @param paramName
     * @return BigDecimal value of a string
     * If cannot convert, returns null
     */
    public static BigDecimal parseBigDecimal(String value) {
        BigDecimal res = null;
        try {
            if (value != null && !value.equals("")) res = new BigDecimal(trimSpace(value.trim()).replaceAll(",","."));
        } catch (Exception e) {
            res = null;
        } finally {
            if (logging) logger.info("parseBigDecimal: String(" + value + ") -> BigDecimal(" + res+ ")");
        }
        return res;
    }
    /**
     * Parse string and returns Long. Handles exceptions.
     * @param paramName
     * @return Long value of a string
     * If cannot convert, returns null
     */
    public static Long parseLong(String value) {
        Long res = null;
        try {
            if (value != null) res = Long.parseLong(trimSpace(value.trim()).replaceAll(",","."));
        } catch (Exception e) {
            res = null;
        } finally {
            if (logging) logger.info("parseLong: String(" + value + ") -> Long(" + res+ ")");
        }
        return res;
    }
    
    /**
     * Parse string and returns Integer. Handles exceptions.
     * @param paramName
     * @return Integer value of a string
     * If cannot convert, returns null
     */
    public static Integer parseInt(String value) {
        Integer res = null;
        try {
            if (value != null) res = Integer.parseInt(trimSpace(value.trim()).replaceAll(",","."));
        } catch (Exception e) {
            res = null;
        } finally {
            if (logging) logger.info("parseInt: String(" + value + ") -> Integer(" + res+ ")");
        }
        return res;
    }
    
    /**
     * Parse string and returns Date. Handles exceptions.
     * @param paramName
     * @return Integer value of a string
     * If cannot convert, returns null
     */
    public static java.sql.Date parseDate(String value) {
        java.sql.Date res = null; 
        try {
            if (value != null)  res = new java.sql.Date(df.parse(value).getTime());
        } catch (Exception e) {
            res = null;
        } finally {
            if (logging) logger.info("parseDate: String(" + value + ") -> java.sql.Date(" + res+ ")");
        }
        return res;
    } 
    /**
     * Парсит дату всеми возможными форматами. Очень полезный метод для строк из ПУПа.
     */
    public static java.util.Date parseDateRobust(String value) {
        for (SimpleDateFormat f : getFormatters()){
            try{
                return f.parse(value);
            } catch (Exception e){
                //проглатываем ошибку
            }
        }
        logger.warning("date parse error "+value);
        
        return new java.util.Date();
    }
    
    
    public static String format(java.util.Date d){
        if (d == null) return "";
        return df.format(d);
    }
    public static String formatDateTime(java.util.Date d){
        if (d == null) return "";
        return dfDT.format(d);
    }
    public static String getMonthNameRus(Long monthNumber){
    	if(monthNumber==null)
    		return "";
    	if(monthNumber.equals(1L))
    		return "январь";
    	if(monthNumber.equals(2L))
    		return "февраль";
    	if(monthNumber.equals(3L))
    		return "март";
    	if(monthNumber.equals(4L))
    		return "апрель";
    	if(monthNumber.equals(5L))
    		return "май";
    	if(monthNumber.equals(6L))
    		return "июнь";
    	if(monthNumber.equals(7L))
    		return "июль";
    	if(monthNumber.equals(8L))
    		return "август";
    	if(monthNumber.equals(9L))
    		return "сентябрь";
    	if(monthNumber.equals(10L))
    		return "октябрь";
    	if(monthNumber.equals(11L))
    		return "ноябрь";
    	if(monthNumber.equals(12L))
    		return "декабрь";
    	return "";
    }
    
    public static String strWeb(String str) {
    	return str(str).replaceAll("\n", " ").replaceAll("\r", " ").replaceAll("\t", " ").replaceAll("\"", "&quot;").replaceAll("'", "&quot;");
    }
}