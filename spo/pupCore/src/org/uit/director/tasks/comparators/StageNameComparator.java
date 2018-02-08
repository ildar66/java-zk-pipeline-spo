package org.uit.director.tasks.comparators;

import java.util.Map;

import org.uit.director.tasks.TaskInfo;

/**
 * Created by IntelliJ IDEA.
 * User: PD190390
 * Date: 02.02.2006
 * Time: 13:44:48
 * To change this template use File | Settings | File Templates.
 */
public class StageNameComparator implements java.util.Comparator {

    public int compare(Object o1, Object o2) {
        TaskInfo p1 = (TaskInfo)((Map) o1).values().iterator().next();
        TaskInfo p2 = (TaskInfo)((Map) o2).values().iterator().next();

        String attr1 = p1.getNameStageTo();
        String attr2 = p2.getNameStageTo();

        if (attr1 == null || attr2 == null) {
			return 0;
		}
        return attr1.compareTo(attr2);

    }
}
