package com.vtb.domain;

public class AttachmentFile extends VtbObject {

    private static final long serialVersionUID = 1L;
    
    String unid;
	String filename;
	byte[] filedata;
	
	
	public AttachmentFile() {
	}
	
	public AttachmentFile(String unid) {
		this.unid = unid;
	}
	
	
	public byte[] getFiledata() {
		return filedata;
	}

	public void setFiledata(byte[] filedata) {
		this.filedata = filedata;
	}

	public String getUnid() {
		return unid;
	}
	public void setUnid(String unid) {
		this.unid = unid;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}	
}
