package org.uit.director.report.mainreports;

import java.util.Comparator;

import org.uit.director.db.dbobjects.Attribute;


public class AttributesComparator implements Comparator {

    private String order;

    public AttributesComparator(String order) {

        this.order = order;
    }

    public int compare(Object o1, Object o2) {

        Attribute om1 = (Attribute) o1;
        Attribute om2 = (Attribute) o2;
        int result = 0;
        int idx1 = order.indexOf(om1.getNameVariable());
        int idx2 = order.indexOf(om2.getNameVariable());
        if (idx1 < idx2) {
			result = -1;
		} else if (idx1 > idx2) {
			result = 1;
		}
        return result;
    }
}
