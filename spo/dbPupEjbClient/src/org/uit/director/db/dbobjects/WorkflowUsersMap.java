package org.uit.director.db.dbobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkflowUsersMap implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	HashMap<Long, WorkflowUser> wfUserList;

	class WFUserComparator implements Comparator<WorkflowUser> {

		TypeComparator typeComparator; // 0-по имени 1-

		public WFUserComparator(TypeComparator typeC) {
			typeComparator = typeC;
		}

		public int compare(WorkflowUser o1, WorkflowUser o2) {
			if (o1 == null || o2 == null) {
				return 0;
			}
			
			String field1 = "";
			String field2 = "";
			
			Long fieldLong1 = new Long(0);
			Long fieldLong2 = new Long(0);

			switch (typeComparator.value) {
			case TypeComparator.idUser:
				fieldLong1 = o1.getIdUser();
				fieldLong2 = o2.getIdUser();
				break;
			case TypeComparator.name:
				field1 = o1.getName();
				field2 = o2.getName();
				break;
			case TypeComparator.family:
				field1 = o1.getFamily();
				field2 = o2.getFamily();
				break;
			case TypeComparator.patronymic:
				field1 = o1.getPatronymic();
				field2 = o2.getPatronymic();
				break;
			case TypeComparator.departamentShortName:
				field1 = o1.getDepartament().getShortName();
				field2 = o2.getDepartament().getShortName();
				break;
			case TypeComparator.departamentFullName:
				field1 = o1.getDepartament().getFullName();
				field2 = o2.getDepartament().getFullName();
				break;
			case TypeComparator.login:
				field1 = o1.getLogin();
				field2 = o2.getLogin();
				break;
			case TypeComparator.mailUser:
				field1 = o1.getMailUser();
				field2 = o2.getMailUser();
				break;
			case TypeComparator.ipUser:
				field1 = o1.getIpUser();
				field2 = o2.getIpUser();
				break;
			}
			if (!(field1.equals("") && field2.equals(""))) {
				return field1.compareToIgnoreCase(field2);
			} else {
				return fieldLong1.compareTo(fieldLong2);
			}
		}

	}

	public WorkflowUsersMap() {
		super();
		wfUserList = new HashMap<Long, WorkflowUser>();
	}

	public WorkflowUsersMap(HashMap<Long, WorkflowUser> wfUserList) {
		super();
		this.wfUserList = wfUserList;
	}

	public Map<Long, WorkflowUser> getWfUserMap() {
		return wfUserList;
	}

	public void setWfUserMap(HashMap<Long, WorkflowUser> wfUserList) {
		this.wfUserList = wfUserList;
	}

	public List<WorkflowUser> getSortedNameWFUserList() {

		return getSortedWFUserList(new TypeComparator(TypeComparator.name));

	}

	private List<WorkflowUser> getSortedWFUserList(TypeComparator name) {

		List<WorkflowUser> res = new ArrayList<WorkflowUser>();
		res.addAll(wfUserList.values());
		Collections.sort(res, new WFUserComparator(name));

		return res;
	}

	public List<WorkflowUser> getSortedWFUserList(List<WorkflowUser> userList,
												  TypeComparator name) {		
		Collections.sort(userList, new WFUserComparator(name));
		return userList;
	}

}
