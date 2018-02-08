package com.vtb.domain;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;



/**
 * Объект, описывает прикреплённые файлы в системе.* 
 *
 */
public class Attachment extends VtbObject {
    private static final long serialVersionUID = 1L;
    
    private static LinkedHashMap<String,String> hashmap = null;
    
    public static Long CONST_CONTRACTOR_FILE = Long.valueOf(1);
	public static Long CONST_APPLICATION_FILE = Long.valueOf(0);
	public static Long CONST_IS_ACCEPTED = Long.valueOf(1);
	public static Long CONST_NOT_ACCEPTED = Long.valueOf(0);
	
	private String unid;
	private String filegroup;   // группа документа.
	private String filetype;    // тип документа
	private String filename;    // имя документа.  Полная запись такова: группа документа.тип документа.имя документа. 
	private String contentType;
	private String signature;
	private Boolean forCC;
	private String idGroup; //Binding group ID
	private String idOwner; //Binding ID
	private Long ownerType; //File bind with CONTRACTOR or LOAN APPLICATION
	private Long whoAdd;	//ID User, who add file 
	private Date dateOfAddition; //Date, when user added file
	private Date dateOfExpiration;
	//This fields are needed for accept(sign) files by 
	private Long isAccepted;
	private Long whoAccept;	
	private Date dateOfAccept;
	private Long idType;   // id of document type
	
	public static LinkedHashMap<String,String> extentionContentTypeMap(){
		if (hashmap!=null)return hashmap;
		hashmap = new LinkedHashMap<String,String>();
		hashmap.put("doc", "application/msword");
		hashmap.put("docx", "application/msword");
		hashmap.put("xls", "application/vnd.ms-excel");
		hashmap.put("xlsx", "application/vnd.ms-excel");
		hashmap.put("jpeg", "image/jpeg");
		hashmap.put("jpg", "image/jpeg");
		hashmap.put("bmp", "image/bmp");
		hashmap.put("gif", "image/gif");
		hashmap.put("tiff", "image/tiff");
		hashmap.put("tif", "image/tiff");
		hashmap.put("png", "image/png");
		hashmap.put("pdf", "application/pdf");
		hashmap.put("rar", "application/rar");
		hashmap.put("zip", "application/zip");
		hashmap.put("arj", "application/arj");
		return hashmap;
	}
	public Attachment() {	
	
	}
	public Attachment(Attachment attach) {	
		this.unid = attach.unid;
		
		this.filegroup = attach.filegroup;		
		this.filetype = attach.filetype;
		this.filename = attach.filename;		
		this.idGroup = attach.idGroup;
		this.idOwner = attach.idOwner;
		this.ownerType = attach.ownerType;
		this.signature = attach.signature;
		
		this.whoAdd = attach.whoAdd;
		this.dateOfAddition = attach.dateOfAddition;
		this.dateOfExpiration = attach.dateOfExpiration;
		
		this.isAccepted = attach.isAccepted;
		this.whoAccept = attach.whoAccept;
		this.dateOfAccept = attach.dateOfAccept;
		this.idType = attach.idType;
	}
	
	public Attachment(String unid) {		
		this.unid = unid;
	}
	
	public Attachment(String idOwner, Long ownerType) {
		this.idOwner = idOwner;
		this.ownerType = ownerType;
	}
	
	public Attachment(String unid, String filename, String filetype, String filegroup, 
	                  String idGroup, String idOwner, Long ownerType, Long idType) {
		this.unid = unid;
		this.filegroup = filegroup;
		this.filetype = filetype;
		this.filename = filename;
		this.idGroup = idGroup;
		this.idOwner = idOwner;
		this.ownerType = ownerType;
		this.signature="";
		this.forCC=true;
		this.contentType="application/octet-stream";
		this.idType =idType;
	}
	
	
	public Date getDateOfAccept() {
		return dateOfAccept;
	}
	public void setDateOfAccept(Date dateOfAccept) {
		this.dateOfAccept = dateOfAccept;
	}
	public Date getDateOfAddition() {
		return dateOfAddition;
	}
	public void setDateOfAddition(Date dateOfAddition) {
		this.dateOfAddition = dateOfAddition;
	}
	public Date getDateOfExpiration() {
		return dateOfExpiration;
	}
	public void setDateOfExpiration(Date dateOfExpiration) {
		this.dateOfExpiration = dateOfExpiration;
	}
	
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getFiletype() {
		return filetype;
	}
	public void setFiletype(String filetype) {
		this.filetype = filetype;
	}
	public String getIdOwner() {
		return idOwner;
	}
	public void setIdOwner(String idOwner) {
		this.idOwner = idOwner;
	}
	public Long isAccepted() {
		return isAccepted;
	}
	public void setAccepted(Long isAccepted) {
		this.isAccepted = isAccepted;
	}
	public Long getOwnerType() {
		return ownerType;
	}
	public void setOwnerType(Long ownerType) {
		this.ownerType = ownerType;
	}
	public String getUnid() {
		return unid;
	}
	public void setUnid(String unid) {
		this.unid = unid;
	}
	public Long getWhoAccept() {
		return whoAccept;
	}
	public void setWhoAccept(Long whoAccept) {
		this.whoAccept = whoAccept;
	}
	public Long getWhoAdd() {
		return whoAdd;
	}
	public void setWhoAdd(Long whoAdd) {
		this.whoAdd = whoAdd;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public Boolean getForCC() {
		return forCC;
	}

	public void setForCC(Boolean forCC) {
		this.forCC = forCC;
	}
	public String getForCCChecked() {
		return this.getForCC()?"Checked":"";
	}

	/**
	 * @return the contentType
	 */
	public String getContentType() {
		if (contentType!=null) return contentType;
		String result="application/octet-stream";
		LinkedHashMap<String,String> hashmap=extentionContentTypeMap();
		Set<String> set = hashmap.keySet();
        Iterator<String> iter = set.iterator();
        while (iter.hasNext()) {
            String extention = (String) iter.next();
            if(filename.endsWith(extention))
            	result=(String) hashmap.get(extention);
        }
		return result;
	}

	/**
	 * @param contentType the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
    public String getFilegroup() {
        return filegroup;
    }
    public void setFilegroup(String filegroup) {
        this.filegroup = filegroup;
    }
    public String getIdGroup() {
        return idGroup;
    }
    public void setIdGroup(String idGroup) {
        this.idGroup = idGroup;
    }
    public Long getIdType() {
        return idType;
    }
    public void setIdType(Long idType) {
        this.idType = idType;
    }
}
