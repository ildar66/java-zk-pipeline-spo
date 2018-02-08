package ru.md.dict.dbobjects;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.vtb.util.Formatter;
/**
 * Цель Финансирования
 * @author Andrey Pavlenko
 */
@Entity
@Table(name = "v_cd_pipeline_coeffs")
public class PipelineCoeffsJPA implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Long id_coeffs;
    private Double value;
    private Long id_type;
	public Long getId() {
		return id_coeffs;
	}
	public void setId(Long id) {
		this.id_coeffs = id;
	}
	public String getValue() {
		String s = Formatter.format2point(value);
		if(s.endsWith(",00"))
			return s.replaceAll(",00","");
		return s;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	public Long getId_type() {
		return id_type;
	}
	public void setId_type(Long id_type) {
		this.id_type = id_type;
	}
}
