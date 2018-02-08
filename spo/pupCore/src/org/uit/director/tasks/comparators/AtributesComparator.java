package org.uit.director.tasks.comparators;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.uit.director.contexts.WPC;
import org.uit.director.db.dbobjects.Attribute;
import org.uit.director.tasks.TaskInfo;

/**
 * Created by IntelliJ IDEA.
 * User: pd190390
 * Date: 23.09.2005
 * Time: 9:11:40
 * To change this template use File | Settings | File Templates.
 */
public final class AtributesComparator implements java.util.Comparator {

    public String name;

    public AtributesComparator(String nameAttr) {
        name = nameAttr;
    }

    public int compare(Object o1, Object o2) {
        TaskInfo p1 = (TaskInfo)((Map) o1).values().iterator().next();

        TaskInfo p2 = (TaskInfo)((Map) o2).values().iterator().next();

        Attribute attr1 = p1.getAttributes().findAttributeByName(name).getAttribute();
        Attribute attr2 = p2.getAttributes().findAttributeByName(name).getAttribute();

        if (attr1 == null || attr2 == null) {
			return 0;
		}

        if (attr1.getTypeVar().equals("date") && attr2.getTypeVar().equals("date")) {
            SimpleDateFormat dateFormat = WPC.getInstance().dateTimeFormat;

            try {
                Date d1 = dateFormat.parse(attr1.getValueAttributeString());
                Date d2 = dateFormat.parse(attr2.getValueAttributeString());
                return d1.after(d2) ? 0 : 1;
            } catch (ParseException e) {
                return 0;
            }

        } else {
            Object value1 = attr1.getValueAttributeString();
            Object value2 = attr2.getValueAttributeString();

            if (value1 instanceof String && value2 instanceof String) {
				return ((String) value1).toLowerCase().trim().
                        compareTo(
                                ((String) value2).toLowerCase().trim()
                        );
			}
        }

        return 0;
    }
}
