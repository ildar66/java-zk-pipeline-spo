package com.vtb.domain;


/** 
 * Describe the type of attachments in the system; 
 * @author Tormozov M.G.
 */
public class DocumentType extends VtbObject {
	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    long id_document_type;
	String name_document_type;	
	//long id_group;
	private DocumentsType docsType = null;
	
	
	public long getId_document_type() {
		return id_document_type;
	}
	public void setId_document_type(long id_document_type) {
		this.id_document_type = id_document_type;
	}
	public DocumentsType getId_group() {
		return this.docsType;
	}
	public void setId_group(DocumentsType id_group) {
		this.docsType = id_group;
	}
	public String getName_document_type() {
		return name_document_type;
	}
	public void setName_document_type(String name_document_type) {
		this.name_document_type = name_document_type;
	}
	
}
