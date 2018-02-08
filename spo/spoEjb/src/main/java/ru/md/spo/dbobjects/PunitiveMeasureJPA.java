package ru.md.spo.dbobjects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import ru.masterdm.spo.utils.Formatter;

/**
 * Компенсирующий спрэд за фиксацию ставки
 * @author Andrey Pavlenko
 */
@Entity
@Table(name = "punitive_measure")
public class PunitiveMeasureJPA {
    @Id
    @Column(name = "id_measure")
    private Long id;

    private String name_measure;
    private String sumdesc;
    private String sanction_type;
    private Long is_active;
	private Double sum;
	private String currency;
	
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName_measure() {
        return Formatter.strWeb(name_measure);
    }
    public void setName_measure(String name_measure) {
        this.name_measure = name_measure;
    }
    public String getSumdesc() {
        return Formatter.strWeb(sumdesc);
    }
    public void setSumdesc(String sumdesc) {
        this.sumdesc = sumdesc;
    }
    public String getSanction_type() {
        return sanction_type;
    }
    public void setSanction_type(String sanction_type) {
        this.sanction_type = sanction_type;
    }
	public Long getIs_active() {
		return is_active;
	}
	public void setIs_active(Long is_active) {
		this.is_active = is_active;
	}
	/**
	 * @return сумма штрафа
	 */
	public Double getSum() {
		return sum;
	}
	public String getSumFormated() {
		return Formatter.format(sum);
	}

	/**
	 * @param sum сумма штрафа
	 */
	public void setSum(Double sum) {
		this.sum = sum;
	}

	/**
	 * @return валюта штрафа
	 */
	public String getCurrency() {
		return currency==null?"":currency;
	}

	/**
	 * @param currency валюта штрафа
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}
    
}
