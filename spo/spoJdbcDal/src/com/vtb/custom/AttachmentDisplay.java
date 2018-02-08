package com.vtb.custom;

import com.vtb.domain.Attachment;

public class AttachmentDisplay extends Attachment {
	
	/**
     * @serial 
     */
    private static final long serialVersionUID = 1L;
    
    String downloadLink="";
	String expiration="";
	String addition="";
	String accept="";
    String whoAddName = "";
    String whoAcceptName = "";
	
	public AttachmentDisplay() {
		
	}
	
	public AttachmentDisplay(Attachment attach) {
		super(attach);
	}
	
	public String getDownloadLink() {
		return downloadLink;
	}
	public void setDownloadLink(String downloadLink) {
		this.downloadLink = downloadLink;
	}
	public String getAccept() {
		return accept;
	}
	public void setAccept(String accept) {
		this.accept = accept;
	}
	public String getAddition() {
		return addition;
	}
	public void setAddition(String addition) {
		this.addition = addition;
	}
	public String getExpiration() {
		return expiration;
	}
	public void setExpiration(String expiration) {
		this.expiration = expiration;
	}

	public String getWhoAddName() {
		return whoAddName;
	}

	public void setWhoAddName(String whoAddName) {
		this.whoAddName = whoAddName;
	}

    /**
     * @return whoAcceptName
     */
    public String getWhoAcceptName() {
        return whoAcceptName;
    }

    /**
     * @param whoAcceptName whoAcceptName
     */
    public void setWhoAcceptName(String whoAcceptName) {
        this.whoAcceptName = whoAcceptName;
    }
	
}
