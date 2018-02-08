package com.vtb.custom;

import java.io.Serializable;
import java.util.List;

/**
 * TO_Object "типов документов"
 * 
 * @author IShafigullin
 * 
 */
public class DocumentTypeTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4720075437712019978L;
	private Integer id = null; //Код типов документов 
	private String name = null; //Имя типов документов 
	private List<String> groupName = null; //группа типов документов 

	@Override
	public boolean equals(Object anObject) {
		if (anObject == null) {
			return false;
		}
		if (!(anObject instanceof DocumentTypeTO)) {
			return false;
		}
		DocumentTypeTO aDocumentsType = (DocumentTypeTO) anObject;
		return aDocumentsType.getId().intValue() == getId().intValue();
	}

	@Override
	public String toString() {
	    StringBuilder sb = new StringBuilder();
		sb.append("DocumentsTypeTO: ");
		sb.append(getId()).append("(").append(getName()).append(")");
		sb.append('\n');
		sb.append(" groupName: ");
		sb.append(getGroupName());

		return sb.toString();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DocumentTypeTO(Integer aId, String aName) {
		setId(aId);
		setName(aName);
		setGroupName(null);
	}
	
	public DocumentTypeTO(Integer aId) {
		setId(aId);
		setName("none");
	}

	public List<String> getGroupName() {
		return groupName;
	}

	public void setGroupName(List<String> groupName) {
		this.groupName = groupName;
	}	

}
