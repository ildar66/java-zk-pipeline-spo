package com.vtb.domain;

public class FileRequest extends VtbObject {
	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    public static long CONST_ID_UNKNOWN = 0;
	
	public final static String SEQUENCE_NAME = "ID_REQUEST";
	public final static int REQ_WAITED = 1;
	public final static int REQ_COMPLETED = 2;
	public final static int REQ_NO_REASON = 3;
	
	public final static int REQ_PRIORITY_IMMIDIATE = 1;
	public final static int REQ_PRIORITY_DEFFERED = 2;
	
	
	long id;
	int priority;
	int status;
	String unid;
	int id_department;
	
	public FileRequest() {
		// TODO GENERATE ID REQUEST
		
	}
	
	public FileRequest(long id) {		
		this.id = id;		
	}
	
	public FileRequest(long id, String unid, int id_department) { 
		this.id=id;		
		this.unid = unid;
		this.id_department = id_department;		
	}
	
	public FileRequest(long id, int priority, int status, String unid, int id_department) { 
		this.id=id;
		this.priority = priority;
		this.status = status;
		this.unid = unid;
		this.id_department = id_department;		
	}
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getId_department() {
		return id_department;
	}
	public void setId_department(int id_department) {
		this.id_department = id_department;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getUnid() {
		return unid;
	}
	public void setUnid(String unid) {
		this.unid = unid;
	}
	
	
}
