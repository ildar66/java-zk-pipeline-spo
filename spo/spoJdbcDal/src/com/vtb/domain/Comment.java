package com.vtb.domain;

import java.sql.Timestamp;

public class Comment  extends VtbObject{
	private static final long serialVersionUID = 1L;
	private Long id;
	private String body;
	private String bodyHtml;
	private Operator author;
	private Integer stageid;
	private Timestamp when;
	private String stagename;
	public String getStagename() {
        return stagename;
    }
    public void setStagename(String stagename) {
        this.stagename = stagename;
    }
    public Timestamp getWhen() {
        return when;
    }
    public void setWhen(Timestamp when) {
        this.when = when;
    }
    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public Operator getAuthor() {
		return author;
	}
	public void setAuthor(Operator author) {
		this.author = author;
	}
	public Integer getStageid() {
		return stageid;
	}
	public void setStageid(Integer stageid) {
		this.stageid = stageid;
	}	
    public String getBodyHtml() {
		return bodyHtml;
	}
	public void setBodyHtml(String bodyHtml) {
		this.bodyHtml = bodyHtml;
	}
	public Comment(Long id, String body, String bodyHtml, Operator author, Integer stageid,Timestamp when) {
        super();
        this.id = id;
        this.body = body;
        this.bodyHtml = bodyHtml;
        this.author = author;
        this.stageid = stageid;
        this.when = when;
    }

}