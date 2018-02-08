package ru.md.spo.dbobjects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * группы видов сделок для лимита
 * @author Andrey Pavlenko
 */
@Entity
@Table(name = "R_PRODUCT_GROUP_MDTASK") @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class ProductGroupJPA {
//ID NUMBER NOT NULL , ID_MDTASK NUMBER NOT NULL , NAME VARCHAR2(400)
	@Id
    @SequenceGenerator(name = "ProductGroupSequenceGenerator", sequenceName = "R_PRODUCT_GROUP_MDTASK_SEQ", allocationSize = 1)
    @GeneratedValue(generator = "ProductGroupSequenceGenerator", strategy = GenerationType.SEQUENCE)
    private Long id;
	
	private String name;
	@ManyToOne @JoinColumn(name="ID_MDTASK")
	private TaskJPA task;
	private String cmnt;
	private Long period;
	
	public ProductGroupJPA() {
		super();
	}
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the task
	 */
	public TaskJPA getTask() {
		return task;
	}
	/**
	 * @param task the task to set
	 */
	public void setTask(TaskJPA task) {
		this.task = task;
	}
	public String getCmnt() {
		return cmnt==null?"":cmnt;
	}
	public void setCmnt(String cmnt) {
		this.cmnt = cmnt;
	}
	public Long getPeriod() {
		return period;
	}
	public void setPeriod(Long period) {
		this.period = period;
	}
}
