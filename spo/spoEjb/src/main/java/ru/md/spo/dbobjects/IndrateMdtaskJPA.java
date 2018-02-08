package ru.md.spo.dbobjects;

import com.vtb.util.Formatter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Индикативная ставка
 * @author Andrey Pavlenko
 */
@Entity
@Table(name = "INDRATE_MDTASK")
public class IndrateMdtaskJPA {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "IndrateMdtaskSequenceGenerator", sequenceName = "INDRATE_MDTASK_seq", allocationSize = 1)
    @GeneratedValue(generator = "IndrateMdtaskSequenceGenerator", strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne @JoinColumn(name="ID_MDTASK")
    private TaskJPA task;

    @Column(name="id_factpercent")
    private Long idFactpercent;

    @Column(name = "IND_RATE")
    private String indrate;

    @Column(name = "rate")
    private BigDecimal rate;//Надбавка к плавающей ставке

    @Column(name = "value")
    private BigDecimal value;//Значение плавающей ставки

    private Date usefrom;//Применяется с

    private String reason;//Основание


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

    public String getIndrate() {
        return indrate;
    }

    public void setIndrate(String indrate) {
        this.indrate = indrate;
    }

    public Long getIdFactpercent() {
        return idFactpercent;
    }

    public void setIdFactpercent(Long idFactpercent) {
        this.idFactpercent = idFactpercent;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Date getUsefrom() {
        return usefrom;
    }

    public void setUsefrom(Date usefrom) {
        this.usefrom = usefrom;
    }

    public String getReason() {
        return Formatter.str(reason);
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public int hashCode() {
        int result = indrate.hashCode();
        result = 31 * result + (rate != null ? rate.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (usefrom != null ? usefrom.hashCode() : 0);
        result = 31 * result + (reason != null ? reason.hashCode() : 0);
        return result;
    }
}
