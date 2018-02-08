package ru.md.spo.dbobjects;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Значения нормативных сроков. 
 * @author Andrey Pavlenko
 */
@Entity
@Table(name = "standard_period_value")
public class StandardPeriodValueJPA implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ID_SPVAL")
    @SequenceGenerator(name = "StandardPeriodValueSequenceGenerator", sequenceName = "standard_period_value_seq", allocationSize = 1)
    @GeneratedValue(generator = "StandardPeriodValueSequenceGenerator", strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="ID_SPG")
    private StandardPeriodGroupJPA group;
    
    private String name;
    
    private Long period;
    
    private String readonly;

    public String getIdStr() {
    	return id.toString();
    }
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public StandardPeriodGroupJPA getGroup() {
		return group;
	}

	public void setGroup(StandardPeriodGroupJPA group) {
		this.group = group;
	}

	public String getName() {
		return name==null?"":name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFormatedPeriod() {
		if (period==null) return "не ограничен";
		return period.toString();
	}
	public Long getPeriod() {
		return period;
	}

	public void setPeriod(Long period) {
		this.period = period;
	}

	public boolean isReadonly() {
		return readonly.equalsIgnoreCase("y");
	}
	public String getReadonly() {
		return readonly;
	}

	public void setReadonly(String readonly) {
		this.readonly = readonly;
	}
}
