package ru.md.domain;

import java.math.BigDecimal;

/**
 * Индикативная ставка
 * @author Andrey Pavlenko
 */
public class IndRate {

    private Long id;
    private BigDecimal rate;
    private String name;
    private Long idFactpercent;

    /**
     * Возвращает идентификатор.
     * @return идентификатор
     */
    public Long getId() {
        return id;
    }
    
    /**
     * Устанавливает идентификатор.
     * @param id идентификатор
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns .
     * @return
     */
    public BigDecimal getRate() {
        return rate;
    }

    /**
     * Sets .
     * @param rate
     */
    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    /**
     * Returns .
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Sets .
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns .
     * @return
     */
    public Long getIdFactpercent() {
        return idFactpercent;
    }

    /**
     * Sets .
     * @param idFactpercent
     */
    public void setIdFactpercent(Long idFactpercent) {
        this.idFactpercent = idFactpercent;
    }
}
