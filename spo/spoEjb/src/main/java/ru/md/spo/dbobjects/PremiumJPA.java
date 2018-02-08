package ru.md.spo.dbobjects;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.vtb.util.Formatter;

/**
 * Премия. 
 * @author Andrey Pavlenko
 */
@Entity
@Table(name = "PREMIUM")
public class PremiumJPA implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "PremiumSequenceGenerator", sequenceName = "PREMIUM_SEQ", allocationSize = 1)
    @GeneratedValue(generator = "PremiumSequenceGenerator", strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @ManyToOne @JoinColumn(name="ID_MDTASK")
    private TaskJPA task;
    
    @ManyToOne @JoinColumn(name="type")
    private CdPremiumTypeJPA premiumType; 
    private Double val; //Вознаграждения величина
    private String curr; //Вознаграждения валюта
    private String text;//Вознаграждения формула
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public TaskJPA getTask() {
		return task;
	}
	public void setTask(TaskJPA task) {
		this.task = task;
	}
	public CdPremiumTypeJPA getPremiumType() {
		return premiumType;
	}
	public void setPremiumType(CdPremiumTypeJPA premiumType) {
		this.premiumType = premiumType;
	}
	public Double getVal() {
		return val;
	}
	public void setVal(Double val) {
		this.val = val;
	}
	public String getCurr() {
		return curr;
	}
	public void setCurr(String curr) {
		this.curr = curr;
	}
	public String getText() {
		if(text==null)
			return "";
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
    /**поле Размер вознаграждения*/
    public String getPremiumSizeDisplay(){
        if(premiumType==null)
            return "";
        String type = premiumType.getValue();
        if("Валюта".equals(type) || "Валюта/ %".equals(type)){
            return Formatter.format(val)+" "+curr;
        }
        if("Формула".equals(type)){
            return getText();
        }
        return type;

    }
    /**поле Вознаграждения*/
    public String getPremiumTypeDisplay() {
        if(premiumType==null)
            return "";
        return premiumType.getPremium_name();
    }
    
}
