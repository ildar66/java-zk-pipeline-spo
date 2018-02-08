package com.vtb.domain;
/**
 * This class describes the type of supply in the rating system
 * 
 * @author Andrey Pavlenko
 * 
 */
public class SupplyType extends VtbObject {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;

    public SupplyType() {
        super();
    }

    public SupplyType(Long id) {
		super();
		this.id = id;
	}

	public SupplyType(Long id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "SupplyType: id="+id+", name="+name;
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
