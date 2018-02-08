package ru.md.spo.dbobjects;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "trance")
public class TranceJPA implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "TranceSequenceGenerator", sequenceName = "TRANCE_SEQ", allocationSize = 1)
    @GeneratedValue(generator = "TranceSequenceGenerator", strategy = GenerationType.SEQUENCE)
    private Long id;
        
    private Double sum;
    private String currency;
    private Date usedatefrom;
    private Date usedateto;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Double getSum() {
		return sum;
	}
	public void setSum(Double sum) {
		this.sum = sum;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public Date getUsedatefrom() {
		return usedatefrom;
	}
	public void setUsedatefrom(Date usedatefrom) {
		this.usedatefrom = usedatefrom;
	}
	public Date getUsedateto() {
		return usedateto;
	}
	public void setUsedateto(Date usedateto) {
		this.usedateto = usedateto;
	}
	public TranceJPA() {
		super();
	}
	public TranceJPA(Long id) {
		super();
		this.id = id;
	}
    
}
