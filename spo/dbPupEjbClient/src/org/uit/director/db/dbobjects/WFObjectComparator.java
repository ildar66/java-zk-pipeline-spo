package org.uit.director.db.dbobjects;

import java.util.Comparator;

/**
 * Created by IntelliJ IDEA.
 * User: pd190390
 * Date: 28.12.2005
 * Time: 11:57:13
 * To change this template use File | Settings | File Templates.
 */
public class WFObjectComparator implements Comparator {

    public int compare(Object o1, Object o2) {
        WorkflowObject object1 = (WorkflowObject) o1;
        WorkflowObject object2 = (WorkflowObject) o2;
        return object1.getName().compareToIgnoreCase(object2.getName());
    }
}
