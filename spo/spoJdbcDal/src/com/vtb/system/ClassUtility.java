package com.vtb.system;
/**
 * Utility for operations on classes.
 * Creation date: (1/29/2001 2:51:37 PM)
 * @author: ILS User
 */
public class ClassUtility {
/*    private static String PERSISTENCE_MECH_PROP_FILE = "ilsNaming";
    private static PropertyResourceBundle mapperNameProperties = (PropertyResourceBundle) PropertyResourceBundle.getBundle(PERSISTENCE_MECH_PROP_FILE);
    public static String DEFAULT_MAPPER_NAME = mapperNameProperties.getString("ils.default.persistence.name");
    public static String DEFAULT_NAME = DEFAULT_MAPPER_NAME;
    public static String DEFAULT_MODEL = DEFAULT_MAPPER_NAME;
*/
/**
 * ClassUitility constructor comment.
 */
public ClassUtility() {
	super();
}


/**
 * Adapt package name from one packageSuffix to another.
 */
public static String computePackageName(Class target, String originalSuffix, String newSuffix) {
	String s, prefix, answer;
	int index;
	s = target.getName();
	index = s.indexOf(originalSuffix);
	if (index > 0) {
		prefix = s.substring(0, index);
		answer = prefix + newSuffix;
	} else {
		answer = packageName(target); 
	}
	return answer;
}


/**
 * Return the package name w/out the class name
 */
public static String packageName(Class target) {
	String s = target.getName();
	return s.substring(0, s.indexOf(unqualifiedClassName(target)) - 1); 
}


/**
 * Return the class name w/out package name
 */
public static String unqualifiedClassName(Class target) {
	String s = target.getName();
	return s.substring(s.lastIndexOf('.') + 1);
}
}