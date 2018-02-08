package com.vtb.custom;

import com.vtb.domain.Attachment;

public class AttachmentFull extends Attachment {
	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    byte[] filedata;

	public byte[] getFiledata() {
		return filedata;
	}

	public void setFiledata(byte[] filedata) {
		this.filedata = filedata;
	}
}
