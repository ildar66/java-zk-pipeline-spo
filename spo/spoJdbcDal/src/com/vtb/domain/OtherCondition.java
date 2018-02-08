package com.vtb.domain;

import ru.masterdm.spo.utils.Formatter;

/**
 * @author Andrew Pavlenko
 */
public class OtherCondition  extends VtbObject{
	private static final long serialVersionUID = 1L;
	private Long type;         // type of the condition (from CONDITION_TYPE table)
	private String body;       // transferred by the user or edited manually value of the condition
	private Long id;           // unique id of the condition. (don't know, whether it's needed here at all)
	private Long idCondition;  // id of the corresponding condition (from CONDITION table)
	private String supplyCode;
	
	public OtherCondition() {
        super();
    }

    public OtherCondition(Long type, Long idCondition, String body, Long id,String supplyCode) {
        super();
        this.type = type;
        this.idCondition = idCondition;
        this.body = body;
        this.id = id;
        this.supplyCode=supplyCode;
    }
    
   public OtherCondition(Long type, String body) {
        super();
        this.type = type;
        this.body = body;
    }

	public Long getType() {
		return type;
	}
	public void setType(Long type) {
		this.type = type;
	}
	public String getBody() {
		return Formatter.cut(body, 4000);
	}
	public void setBody(String body) {
		this.body = body;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

    public Long getIdCondition() {
        return idCondition;
    }

    public void setIdCondition(Long idCondition) {
        this.idCondition = idCondition;
    }

	/**
	 * @return the supplyCode
	 */
	public String getSupplyCode() {
		return supplyCode;
	}

	/**
	 * @param supplyCode the supplyCode to set
	 */
	public void setSupplyCode(String supplyCode) {
		this.supplyCode = supplyCode;
	}
    
}
