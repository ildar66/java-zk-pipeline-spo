package org.uit.director.tasks.comparators;

import java.util.Map;

import org.uit.director.tasks.TaskInfo;

/**
 * Created by IntelliJ IDEA.
 * User: pd190390
 * Date: 23.09.2005
 * Time: 9:05:31
 * To change this template use File | Settings | File Templates.
 */
public class TypeNameProcessComparator implements java.util.Comparator {

    public int compare(Object o1, Object o2) {
        TaskInfo p1 = (TaskInfo)((Map) o1).values().iterator().next();
        TaskInfo p2 = (TaskInfo)((Map) o2).values().iterator().next();
        return p1.getNameTypeProcess().toLowerCase().trim().compareTo(p2.getNameTypeProcess().toLowerCase().trim());

    }
}
