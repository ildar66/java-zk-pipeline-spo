package com.vtb.domain;

public class OperationTypeGroup extends VtbObject {
	private static final long serialVersionUID = 1L;
	private Long id;
	private String name;

   public OperationTypeGroup() {
        super();
    }

	public OperationTypeGroup(Long id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
