package com.vtb.util;

import java.util.PropertyResourceBundle;

public class ApplProperties {
	private static String getVer() {
		return version+"/"+timestamp;
	}
	public static final String EJB_MODEL = "EJB";
	public static final String LOCAL_MODEL = "WEB";
	
	public static final String PACKAGE_PREFIX =	"com.vtb.model.";
	public static final String COMMAND_PACKAGE_PREFIX = "com.vtb.command.";	

	public static final String JPA_MAPPER = "JPA";
	public static final String EJB_MAPPER = "EJB";
	public static final String JDBC_MAPPER = "JDBC";
	public static final String MEMORY_MAPPER = "MEMORY";

	// Configuration file
    private static final String CONFIG_PROP_FILE = "vtbconfig";
    
    private static PropertyResourceBundle properties;
    
    private static final String webcontextprefix;
    private static final String crmlimitdatefrom;
    private static final String showcount;
    private static final String currentMapperName;
    private static final String reserveMapperName;
    private static final String modelType;
    private static final String datasourceJndiName;
    private static final String datasourceJndiName6;
    public static final String version;
    public static final String required_version_compendium;
    public static final String required_version_cc;
    public static final String timestamp;
    private static final String datasourceJndiNameCRM;
    private static final String MAIL_HOST;
    private static final String REPORTS_PATH;
    
    static {
	    properties = (PropertyResourceBundle) PropertyResourceBundle.getBundle(CONFIG_PROP_FILE);
	    
	    webcontextprefix = properties.containsKey("vtb.webcontextprefix")?properties.getString("vtb.webcontextprefix"):"not found prefix";
	    crmlimitdatefrom = properties.containsKey("vtb.crm.limit.date.from")?properties.getString("vtb.crm.limit.date.from"):"01.01.2010";
	    showcount = properties.containsKey("vtb.showcount")?properties.getString("vtb.showcount"):"";
	    currentMapperName = properties.containsKey("vtb.persistence.name")?properties.getString("vtb.persistence.name"):"";
	    reserveMapperName = properties.containsKey("vtb.persistence.reserve.name")?properties.getString("vtb.persistence.reserve.name"):"";
	    modelType = properties.containsKey("vtb.model.impl.name")?properties.getString("vtb.model.impl.name"):"";
	    datasourceJndiName = properties.containsKey("vtb.datasource.name")?properties.getString("vtb.datasource.name"):"";
	    datasourceJndiName6 = properties.containsKey("vtb.datasource.name6")?properties.getString("vtb.datasource.name6"):"";
	    version = properties.containsKey("vtb.version")?properties.getString("vtb.version"):"unknown version";
	    required_version_compendium = properties.containsKey("required.version.compendium")?properties.getString("required.version.compendium"):"0";
	    required_version_cc = properties.containsKey("required.version.cc")?properties.getString("required.version.cc"):"0";
	    datasourceJndiNameCRM = properties.containsKey("vtb.datasource.crm.name")?properties.getString("vtb.datasource.crm.name"):"";
	    MAIL_HOST = properties.containsKey("MAIL_HOST")?properties.getString("MAIL_HOST"):"";
	    REPORTS_PATH = properties.containsKey("REPORTS_PATH")?properties.getString("REPORTS_PATH"):"";
	    timestamp = properties.containsKey("vtb.timestamp")?properties.getString("vtb.timestamp"):"";
    }

	/**
	 * Gets the currentMapperName
	 * @return Returns a String
	 */
	public static String getCurrentMapperName() {
		return currentMapperName;
	}
	
	/**
	 * Gets the reserveMapperName
	 * @return Returns a String
	 */
	public static String getReserveMapperName() {
		return reserveMapperName;
	}		
	
	/**
	 * Gets the modelType
	 * @return Returns a String
	 */
	public static String getModelType() {
		return modelType;
	}

	/**
	 * Gets the datasourceJndiName
	 * @return Returns a String
	 */
	public static String getDatasourceJndiName() {
		return datasourceJndiName;
	}
	public static String getDatasourceJndiName6() {
		return datasourceJndiName6;
	}

	/**
	 * Gets the datasourceJndiName for CRM
	 * @return Returns a String
	 */
	public static String getDatasourceJndiNameCRM() {
		return datasourceJndiNameCRM;
	}

	public static String getMailHost() {
		return MAIL_HOST;
	}

	public static String getReportsPath() {
		return REPORTS_PATH;
	}
	public static String getwebcontextFWF() {
		return webcontextprefix+"flexWorkflow";
	}
	public static String getwebcontextDict() {
		return webcontextprefix+"DictionaryApp";
	}
	public static String getVersion() {
		return getVer();
	}
	public static boolean isShowCount() {
		return showcount.equals("true");
	}

	public static String getCrmLimitDateFrom() {
		return crmlimitdatefrom;
	}
}

