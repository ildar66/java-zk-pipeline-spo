package org.uit.director.db.dbobjects;

import java.util.Comparator;
import java.util.Map;


public class UsersComparator implements Comparator {

        public int compare(Object o1, Object o2) {
            Map m1 = (Map) o1;
            Map m2 = (Map) o2;
            String name1 = (String) m1.get(m1.keySet().iterator().next());
            String name2 = (String) m2.get(m2.keySet().iterator().next());
            return name1.compareTo(name2);
        }
    }

