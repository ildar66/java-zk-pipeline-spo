package com.vtb.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * VtbObject "типов документов"
 * 
 * @author IShafigullin
 * изменил @author Sergey Melnikov
 * 
 */
public class DocumentsType extends VtbObject implements Comparable {

	private static final long serialVersionUID = 4720075437712019978L;
	private Integer id = null; //Id типа документа 
	private String name = null; //Имя типа документа 
	private Boolean forCC=true;
	//private Integer[] groupID = null; //группа типов документов
	private ArrayList<DocumentGroup> groupID = null;

   public DocumentsType(Integer aId, String aName,boolean forCC) {
        setId(aId);
        setName(aName);
        setGroupID(null);
        setForCC(forCC);
        this.groupID = new ArrayList<DocumentGroup>();
    }
    
    public DocumentsType(Integer aId) {
        setId(aId);
        setName("none");
        this.groupID = new ArrayList<DocumentGroup>();
    }

	@Override
	public boolean equals(Object anObject) {
		if (anObject == null) {
			return false;
		}
		if (!(anObject instanceof DocumentsType)) {
			return false;
		}
		DocumentsType aDocumentsType = (DocumentsType) anObject;
		return aDocumentsType.getId().intValue() == getId().intValue();
	}

	@Override
	public String toString() {
	    /*StringBuilder sb = new StringBuilder();
		sb.append("DocumentsType: ");
		sb.append(getId()).append("(" + getName()).append(")");
		sb.append(", groupIDs: ");//.append(Arrays.toString(groupID));
		for (DocumentGroup group : groupID){
			sb.append(group.getName_document_group());
			sb.append(",");
		}

		return sb.toString();*/
		
		return getName();
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

	public List<DocumentGroup> getGroupID() {
		return groupID;
	}

	public void setGroupID(ArrayList<DocumentGroup> groupID) {
		this.groupID = groupID;
	}

	public int compareTo(Object o) {
		String thisVal = this.name;
		String anotherVal = ((DocumentsType)o).name;
		return thisVal.compareTo(anotherVal);
	}


	public Boolean getForCC() {
		return forCC;
	}

	public void setForCC(Boolean forCC) {
		this.forCC = forCC;
	}	
}
