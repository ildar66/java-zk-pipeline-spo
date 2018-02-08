package org.uit.director.tasks.comparators;

import java.util.Date;
import java.util.Map;

import org.uit.director.tasks.TaskInfo;

/**
 * Created by IntelliJ IDEA.
 * User: pd190390
 * Date: 23.09.2005
 * Time: 9:07:29
 * To change this template use File | Settings | File Templates.
 */
public final class DateComparator implements java.util.Comparator {

    public boolean isDateOfComming = true;

    public DateComparator(boolean datePost) {
        isDateOfComming = datePost;
    }

    public int compare(Object o1, Object o2) {
        TaskInfo p1 = (TaskInfo)((Map) o1).values().iterator().next();
        TaskInfo p2 = (TaskInfo)((Map) o2).values().iterator().next();
        Date date1 = (isDateOfComming ? p1.getDateOfComming() : p1.getDateOfMustComplete());
        Date date2 = (isDateOfComming ? p2.getDateOfComming() : p2.getDateOfMustComplete());

        if (date1.after(date2)) {
			return 1;
		}
        if (date1.before(date2)) {
			return -1;
		}
        return 0;


    }
}
