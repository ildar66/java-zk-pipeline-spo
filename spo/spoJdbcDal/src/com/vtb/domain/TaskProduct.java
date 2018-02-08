package com.vtb.domain;

import java.sql.Date;

/**
 * VtbObject "вида сделок"
 * 
 * @author IShafigullin
 * 
 */
public class TaskProduct extends VtbObject {

    private static final long serialVersionUID = 5295714699482937457L;
    private String id; //Код вида сделок 
    private String name; //Имя вида сделок
    private String family;
    private Date usedate;    //дата окончания использования ВИДА сделки рамках данного лимита (сублимита)
    private Integer period;  // срок действия ВИДА сделки
    private String periodDimension; //размерность срока
    private String description;  // комментарий к срокам действия
    
    public TaskProduct() {
        super();
    }

    public TaskProduct(String id, String name, String family, Date usedate, Integer period, 
    		String description, String periodDimension) {
        this.id = id;
        this.family = family;
        this.name = name;
        this.usedate = usedate;
        this.period = period;
        this.description = description;
        this.periodDimension=periodDimension;
    }

    public TaskProduct(String id) {
        super();
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof TaskProduct)) {
            return false;
        }
        return ((TaskProduct) obj).getId().equals(id);
    }

    public String getFamily() {
        return family;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("OpportunityType: ");
        sb.append(getId() + "(" + getName() + "), family = ").append(family);
        return sb.toString();
    }

    public Date getUsedate() {
        return usedate;
    }

    public void setUsedate(Date usedate) {
        this.usedate = usedate;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

	/**
	 * @return размерность срока
	 */
	public String getPeriodDimension() {
		return periodDimension;
	}

	/**
	 * @param periodDimension размерность срока
	 */
	public void setPeriodDimension(String periodDimension) {
		this.periodDimension = periodDimension;
	}

}
