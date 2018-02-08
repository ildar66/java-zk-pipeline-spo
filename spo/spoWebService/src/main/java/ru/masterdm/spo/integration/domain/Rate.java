package ru.masterdm.spo.integration.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Хронология изменения индикативной и фиксированной ставки 
 * @author akirilchev@masterdm.ru
 */
public class Rate implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long idFloatHistory;
    private Long idFixedHistory;
    
    private String rateType;
    private BigDecimal value;
    private String valueComment; //комментарий к ставке  размещения (относится к периоду, не к индикативной ставке)
    private BigDecimal additionValue;
    private BigDecimal fullValueWithSanction;
    private String reason;
    private Date startDate;
    private Date endDate;
    
    private String rateShortType;
    private String rateCrmId;
    private String ratePart1of3; 
    private String ratePart2of3; 
    private String ratePart3of3; 
    
    /**
	 * Возвращает {@link Long первичный ключ} хронологии периода плавающей ставки
	 *
	 * @return {@link Long первичный ключ} хронологии периода плавающей ставки
	 */
	public Long getIdFloatHistory() {
		return idFloatHistory;
	}

	/**
	 * Устанавливает {@link Long первичный ключ} хронологии периода плавающей ставки
	 *
	 * @param idFloatHistory {@link Long первичный ключ} хронологии периода плавающей ставки
	 */
	public void setIdFloatHistory(Long idFloatHistory) {
		this.idFloatHistory = idFloatHistory;
	}
	
	/**
	 * Возвращает {@link Long первичный ключ} хронологии периода фиксированной ставки
	 *
	 * @return {@link Long первичный ключ} хронологии периода фиксированной ставки
	 */
	public Long getIdFixedHistory() {
		return idFixedHistory;
	}
	
	/**
	 * Устанавливает {@link Long первичный ключ} хронологии периода фиксированной ставки
	 *
	 * @param idFixedHistory {@link Long первичный ключ} хронологии периода фиксированной ставки
	 */
	public void setIdFixedHistory(Long idFixedHistory) {
		this.idFixedHistory = idFixedHistory;
	}

	/**
     * Возвращает тип ставки
     *
     * @return тип ставки
     */
    public String getRateType() {
        return rateType;
    }

    /**
     * Устанавливает тип ставки
     *
     * @param rateType тип ставки
     */
    public void setRateType(String rateType) {
        this.rateType = rateType;
    }

    /**
     * Возвращает {@link BigDecimal надбавку} к плавающей ставке
     *
     * @return {@link BigDecimal надбавку} к плавающей ставке
     */
    public BigDecimal getAdditionValue() {
        return additionValue;
    }

    /**
     * Устанавливает {@link BigDecimal надбавку} к плавающей ставке
     *
     * @param additionValue {@link BigDecimal надбавку} к плавающей ставке
     */
    public void setAdditionValue(BigDecimal additionValue) {
        this.additionValue = additionValue;
    }
    
    /**
	 * Возвращает {@link BigDecimal значение ставки} с учетом санкций
	 *
	 * @return {@link BigDecimal значение ставки} с учетом санкций
	 */
	public BigDecimal getFullValueWithSanction() {
		return fullValueWithSanction;
	}

	/**
	 * Устанавливает {@link BigDecimal}
	 *
	 * @param fullValueWithSanction {@link BigDecimal}
	 */
	public void setFullValueWithSanction(BigDecimal fullValueWithSanction) {
		this.fullValueWithSanction = fullValueWithSanction;
	}

	/**
	 * Возвращает комментарий к ставке размещения
	 *
	 * @return комментарий к ставке размещения
	 */
	public String getValueComment() {
		return valueComment;
	}

	/**
	 * Устанавливает комментарий к ставке размещения
	 *
	 * @param valueComment комментарий к ставке размещения
	 */
	public void setValueComment(String valueComment) {
		this.valueComment = valueComment;
	}

	/**
	 * Возвращает {@link BigDecimal значение} ставки (плавающей или фиксированной)
	 *
	 * @return {@link BigDecimal значение} ставки (плавающей или фиксированной)
	 */
	public BigDecimal getValue() {
        return value;
    }

    /**
     * Устанавливает {@link BigDecimal значение} ставки (плавающей или фиксированной)
     *
     * @param value {@link BigDecimal значение} ставки (плавающей или фиксированной)
     */
    public void setValue(BigDecimal value) {
        this.value = value;
    }

    /**
     * Возвращает {@link Date дату} применения с
     *
     * @return {@link Date дата} применения с
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Устанавливает {@link Date дату} применения с
     *
     * @param startDate {@link Date дата} применения с
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    
    /**
	 * Возвращает {@link Date дату} применения до
	 *
	 * @return {@link Date дата} применения до
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Устанавливает {@link Date дату} применения до
	 *
	 * @param endDate {@link Date дата} применения до
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * Возвращает основание
	 *
	 * @return основание
	 */
	public String getReason() {
        return reason;
    }

    /**
     * Устанавливает основание
     *
     * @param reason основание
     */
    public void setReason(String reason) {
        this.reason = reason;
    }
    

	/**
	 * Возвращает {@link String}
	 *
	 * @return {@link String}
	 */
	public String getRateShortType() {
		return rateShortType;
	}

	/**
	 * Устанавливает {@link String}
	 *
	 * @param rateShortType {@link String}
	 */
	public void setRateShortType(String rateShortType) {
		this.rateShortType = rateShortType;
	}

	/**
	 * Возвращает id в системе CRM
	 *
	 * @return id в системе CRM
	 */
	public String getRateCrmId() {
		return rateCrmId;
	}

	/**
	 * Устанавливает id в системе CRM
	 *
	 * @param rateCrmId id в системе CRM
	 */
	public void setRateCrmId(String rateCrmId) {
		this.rateCrmId = rateCrmId;
	}

	/**
	 * Возвращает тип индикативной ставки (часть 1 названия индикативной ставки)
	 *
	 * @return тип индикативной ставки (часть 1 названия индикативной ставки)
	 */
	public String getRatePart1of3() {
		return ratePart1of3;
	}

	/**
	 * Устанавливает тип индикативной ставки (часть 1 названия индикативной ставки)
	 *
	 * @param ratePart1of3 тип индикативной ставки (часть 1 названия индикативной ставки)
	 */
	public void setRatePart1of3(String ratePart1of3) {
		this.ratePart1of3 = ratePart1of3;
	}

	/**
	 * Возвращает срочность-число индикативной ставки (часть 2 названия индикативной ставки)
	 *
	 * @return срочность-число индикативной ставки (часть 2 названия индикативной ставки)
	 */
	public String getRatePart2of3() {
		return ratePart2of3;
	}

	/**
	 * Устанавливает срочность-число индикативной ставки (часть 2 названия индикативной ставки)
	 *
	 * @param ratePart2of3 срочность-число индикативной ставки (часть 2 названия индикативной ставки)
	 */
	public void setRatePart2of3(String ratePart2of3) {
		this.ratePart2of3 = ratePart2of3;
	}

	/**
	 * Возвращает срочность-размерность индикативной ставки (часть 3 названия индикативной ставки)
	 *
	 * @return срочность-размерность индикативной ставки (часть 3 названия индикативной ставки)
	 */
	public String getRatePart3of3() {
		return ratePart3of3;
	}

	/**
	 * Устанавливает срочность-размерность индикативной ставки (часть 3 названия индикативной ставки)
	 *
	 * @param ratePart3of3 срочность-размерность индикативной ставки (часть 3 названия индикативной ставки)
	 */
	public void setRatePart3of3(String ratePart3of3) {
		this.ratePart3of3 = ratePart3of3;
	}

}
