package com.vtb.domain;

@Deprecated
public class Shareholder extends VtbObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final Long CONST_ID_UNKNOWN = null;
	
	Long id;
	String fio;
	String category;
	String part;
	Long id_contractor;
	
	public Shareholder() {
		// TODO Auto-generated constructor stub		
	}	
	
	public Shareholder(Long id) {
		this.id = id;
	}
	
	public Shareholder(String fio, String part, String category, Long id) {
		this.fio = fio;
		this.part = part;
		this.category = category;
		this.id = id;
	}
	
	public Shareholder(Long id, String fio, String part, String category, Long id_contractor) {
		this.id = id;
		this.fio = fio;
		this.part = part;
		this.category = category;
		this.id_contractor = id_contractor;
	}
	

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getFio() {
		return fio;
	}

	public void setFio(String fio) {
		this.fio = fio;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId_contractor() {
		return id_contractor;
	}

	public void setId_contractor(Long id_contractor) {
		this.id_contractor = id_contractor;
	}

	public String getPart() {
		return part;
	}

	public void setPart(String part) {
		this.part = part;
	}
	
	
	
}
