package com.vtb.util;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Чтобы было удобнее создавать статические массивы данных
 * @author Andrey Pavlenko
 */
public class CollectionUtils {
    public static Set<String> set(String... ts) {
        return new HashSet<String>(Arrays.asList(ts));
    }
    public static Set<Date> setDate(String... ts) {
        HashSet<Date> res = new HashSet<Date>();
        for (String s : ts){
            res.add(Formatter.parseDate(s));
        }
        return res;
    }
	public static Map<String,String> map(String... m){
		List<String> list = Arrays.asList(m);
		Map<String,String> res = new HashMap<String,String>();
		for(String s : list){
			res.put(s.substring(0, 1), s.substring(1));
		}
		return res;
	}

    /**
     * Строки в сете выводит через запятую
     */
    public static String hashSetJoin(HashSet<String> set){
        if(set==null)
            return "";
        return listJoin(new ArrayList<String>(set));
    }
    public static String setJoin(Set<String> set){
        if(set==null)
            return "";
        return listJoin(new ArrayList<String>(set));
    }
    /**
     * Строки в списке выводит через запятую
     */
    public static String listJoin(List<String> list){
        if(list==null)
            return "";
        String[] arr = list.toArray(new String[list.size()]);
        return StringUtils.join(arr, ", ");
    }
    /**
     * Строки в списке выводит через vertical bar
     */
    public static String listJoinVbar(List<String> list){
        if(list==null)
            return "";
        String[] arr = list.toArray(new String[list.size()]);
        return StringUtils.join(arr, " | ");
    }
}
