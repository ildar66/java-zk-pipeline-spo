package com.vtb.domain;



/**
 * Describe the group of attachment's type in the system; 
 * 
 * @author Tormozov M.G.
 *
 */
public class DocumentGroup extends VtbObject implements Comparable {
	public int compareTo(Object o) {
		String thisVal = this.name_document_group;
		String anotherVal = ((DocumentGroup)o).name_document_group;
		return thisVal.compareTo(anotherVal);
	}
	
	public static final int TYPE_CONTRACTOR = 0;
	public static final int TYPE_TASK = 1;
	
    private static final long serialVersionUID = 1L;
    private long id_document_group;
	private String name_document_group;
	private int type;
	
	public int getType(){
		return type;
	}
	
	public void setType(int value){
		type = value;
	}
	
	public String GetTextType(){
		String result = "";
		if (this.type == TYPE_CONTRACTOR){
			result = "Документы по контрагенту";
		} else if (this.type == TYPE_TASK){
			result = "Документы по сделке";
		}
		
		return result;
	}
	
	public long getId_document_group() {
		return id_document_group;
	}
	public void setId_document_group(long id_document_group) {
		this.id_document_group = id_document_group;
	}
	public String getName_document_group() {
		return name_document_group;
	}
	public void setName_document_group(String name_document_group) {
		this.name_document_group = name_document_group;
	}
	public DocumentGroup(long id_document_group) {
		super();
		this.id_document_group = id_document_group;
	}
	
	@Override
	public boolean equals(Object obj){
		if (obj == null)
			return false;
		
		if (!( obj instanceof DocumentGroup))
			return false;
		
		DocumentGroup aDocumentGroup = (DocumentGroup) obj;
		return aDocumentGroup.getId_document_group() == this.id_document_group;
	}
}
