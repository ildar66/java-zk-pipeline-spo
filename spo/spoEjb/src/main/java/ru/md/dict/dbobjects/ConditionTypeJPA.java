package ru.md.dict.dbobjects;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
/**
 * @author Andrey Pavlenko
 */
@Entity
@Table(name = "condition_types")
public class ConditionTypeJPA implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private Long id_type;
    private String name;
    private Long sort_order;
	public Long getId_type() {
		return id_type;
	}
	public void setId_type(Long id_type) {
		this.id_type = id_type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getSort_order() {
		return sort_order;
	}
	public void setSort_order(Long sort_order) {
		this.sort_order = sort_order;
	}
	public boolean isHasDict(){
		/*if(id_type.equals(1L) || id_type.equals(2L) || id_type.equals(3L) || id_type.equals(6L) || id_type.equals(9L))
			return true;*/
		return true;
	}
    
}
