package ru.md.spo.dbobjects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.vtb.util.Formatter;

/**
 * Компенсирующие спрэды в зависимости от срока кредита и моратория.
 * @author Andrey Pavlenko
 */
@Entity
@Table(name = "cd_depending_loan")
public class DependingLoanJPA {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id")
    private Long id;

    private String id_currency;
    private Long days_from;
    private Long days_to;
    private Long days_ban_to;
    private Double spread;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getId_currency() {
        return id_currency;
    }
    public void setId_currency(String id_currency) {
        this.id_currency = id_currency;
    }
    public Long getDays_from() {
        return days_from;
    }
    public void setDays_from(Long days_from) {
        this.days_from = days_from;
    }
    public Long getDays_to() {
        return days_to;
    }
    public void setDays_to(Long days_to) {
        this.days_to = days_to;
    }
    public Long getDays_ban_to() {
        return days_ban_to;
    }
    public void setDays_ban_to(Long days_ban_to) {
        this.days_ban_to = days_ban_to;
    }
    public Double getSpread() {
        return spread;
    }
    public void setSpread(Double spread) {
        this.spread = spread;
    }
    public String getSpredFormatedInPercent() {
        return Formatter.format(100*spread);
    }
    
}
