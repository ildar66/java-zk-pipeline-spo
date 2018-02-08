package ru.md.dict.dbobjects;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.vtb.util.Formatter;

/**
 * Компенсирующий спрэд за досрочное погашение в зависимости от срока кредита
 * @author Andrey Pavlenko
 */
@Entity
@Table(name = "crm_early_repayment")
public class EarlyRepaymentJPA {
    private static final long serialVersionUID = 1L;
    @Id
    private String id;

    @Column(name = "id_currency")
    private String currency;
    private Double spread;//Значение спреда - значение ставки
    private Date activedate;//Актуально с  - для выбранного­ диапазона действия ставки берутся значения с наиболее свежей датой активности
    private Long days_from;//Срок сделки с (дней)  - срок, с которого будет действовать ставка
    private Long days_to;//Срок сделки по (дней)  - срок, по который будет действовать ставка
    
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getSpredFormatedInPercent() {
        return Formatter.format(100*spread);
    }
    public Date getActivedate() {
        return activedate;
    }
    public void setActivedate(Date activedate) {
        this.activedate = activedate;
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
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    public Double getSpread() {
        return spread;
    }
    public void setSpread(Double spread) {
        this.spread = spread;
    }
    
    
}
