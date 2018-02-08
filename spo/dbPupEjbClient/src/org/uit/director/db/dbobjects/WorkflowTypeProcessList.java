package org.uit.director.db.dbobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;


public class WorkflowTypeProcessList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ArrayList<WorkflowTypeProcess> typesProcesses;

	private ArrayList groups;

	private double k_comp;

	/**
	 * 
	 * @param tpProcesses
	 * @param k_comp
	 */
	public void init(ArrayList<WorkflowTypeProcess> tpProcesses, double k_comp) {
		this.k_comp = k_comp;
		typesProcesses = tpProcesses;
		// Collections.sort(typesProcesses, new WFObjectComparator());
		// TODO
		Collections.sort(typesProcesses, new Comparator() {

			public int compare(Object o1, Object o2) {
				WorkflowObject object1 = (WorkflowObject) o1;
				WorkflowObject object2 = (WorkflowObject) o2;

				String name1 = object1.getName();
				String name2 = object2.getName();

				if (name1.indexOf("Индустр") != -1) {
					name1 = "aaa1" + name1;
				}
				if (name1.indexOf("Ленин") != -1) {
					name1 = "aaa2" + name1;
				}
				if (name1.indexOf("Октябрь") != -1) {
					name1 = "aaa3" + name1;
				}
				if (name1.indexOf("Первомай") != -1) {
					name1 = "aaa4" + name1;
				}
				if (name1.indexOf("Устинов") != -1) {
					name1 = "aaa5" + name1;
				}

				if (name2.indexOf("Индустр") != -1) {
					name2 = "aaa1" + name2;
				}
				if (name2.indexOf("Ленин") != -1) {
					name2 = "aaa2" + name2;
				}
				if (name2.indexOf("Октябрь") != -1) {
					name2 = "aaa3" + name2;
				}
				if (name2.indexOf("Первомай") != -1) {
					name2 = "aaa4" + name2;
				}
				if (name2.indexOf("Устинов") != -1) {
					name2 = "aaa5" + name2;
				}

				if (!name1.startsWith("aaa")) {

					StringTokenizer s1 = new StringTokenizer(name1, " ");

					if (s1.countTokens() == 2) {
						String nextToken = s1.nextToken();
						name1 = s1.nextToken() + nextToken;
					}

				}

				if (!name2.startsWith("aaa")) {
					StringTokenizer s2 = new StringTokenizer(name2, " ");
					if (s2.countTokens() == 2) {
						String nextToken = s2.nextToken();
						name2 = s2.nextToken() + nextToken;
					}

				}

				return name1.compareToIgnoreCase(name2);
			}

		});
		genereteGroups();

	}

	private void genereteGroups() {
		groups = new ArrayList();

		int countTypes = typesProcesses.size();
		boolean[] flags = new boolean[countTypes];

		for (int i = 0; i < countTypes; i++) {
			if (!flags[i]) {
				ArrayList tp = new ArrayList();
				List namesOfGroup = new ArrayList();
				String name1 = (typesProcesses.get(i))
						.getNameTypeProcess();
				tp.add(typesProcesses.get(i));
				namesOfGroup.add(name1);

				for (int j = i + 1; j < countTypes; j++) {
					if (!flags[j]) {
						String name2 = (typesProcesses
								.get(j)).getNameTypeProcess();
						if (Trigramma.compareStrings(name1, name2) > k_comp) {
							tp.add(typesProcesses.get(j));
							namesOfGroup.add(name2);
							flags[j] = true;
						}
					}
				}

				String common = parsNames(namesOfGroup);
				ArrayList difference = null;

				WorkflowTypeProcessGroup group = new WorkflowTypeProcessGroup(
						tp, common, difference);
				groups.add(group);

			}
		}
	}

	private String parsNames(List namesOfGroup) {

		List matr = new ArrayList();
		String common = "";

		int min = 100;

		for (int i = 0; i < namesOfGroup.size(); i++) {
			String s = (String) namesOfGroup.get(i);

			String[] strMass = getStrMas(s);
			if (strMass.length < min) {
				min = strMass.length;
			}
			matr.add(strMass);
		}

		for (int i = 0; i < min; i++) {

			if (compareColumn(matr, i)) {
				common += ((String[]) matr.get(0))[i] + " ";
			}

		}

		return common;

	}

	private String[] getStrMas(String s) {

		StringTokenizer stok = new StringTokenizer(s, " ");
		String[] res = new String[stok.countTokens()];
		int i = 0;
		while (stok.hasMoreTokens()) {
			res[i++] = stok.nextToken();
		}

		return res;
	}

	private boolean compareColumn(List str, int idx) {

		String s = null;

		for (int i = 0; i < str.size(); i++) {
			String[] strings = (String[]) str.get(i);
			if (s == null) {
				s = strings[idx];
				continue;
			}

			if (!s.equals(strings[idx])) {
				return false;
			}
		}

		return true;

	}

	public List<WorkflowTypeProcess> getTypesProcesses() {
		return typesProcesses;
	}

	/**
	 * Get list of type processes sorted by name 
	 */
	public List<WorkflowTypeProcess> getTypesProcessesSorted() {
	    List<WorkflowTypeProcess> returnList = new ArrayList<WorkflowTypeProcess>(typesProcesses);
	    TypeProcessComparator comparator = new TypeProcessComparator();
	    Collections.sort(returnList, comparator);
	    return returnList;
    }
	
	public List getGroups() {
		return groups;
	}

	 public class TypeProcessComparator implements Comparator<WorkflowTypeProcess> {
    	 public int compare(WorkflowTypeProcess o1, WorkflowTypeProcess o2) { 
    	     return o1.getNameTypeProcess().compareTo(o2.getNameTypeProcess()); 
    	 } 
	}
}
