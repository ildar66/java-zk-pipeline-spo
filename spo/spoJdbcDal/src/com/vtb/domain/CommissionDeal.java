package com.vtb.domain;

import ru.masterdm.compendium.domain.Currency;
import ru.masterdm.compendium.domain.crm.ComissionSize;
import ru.masterdm.compendium.domain.crm.CommissionType;
import ru.masterdm.compendium.domain.crm.PatternPaidPercentType;
import ru.masterdm.compendium.domain.spo.CalcBase;

import com.vtb.util.Formatter;

/**
 * Комиссия для сделки (не для лимита)
 * 
 * @author Michail Kuznetsov, Andrey Pavlenko
 */

public class CommissionDeal extends VtbObject {
    private static final long serialVersionUID = 2L;
    private Long id;
    private String description;//заметки
    private Currency currency;
    private CommissionType name;//Наименование комиссии из справочника V_SPO_COM_TYPE
    private Double value;
    private PatternPaidPercentType procent_order;//Периодичность оплаты комиссии V_SPO_COM_PERIOD 
    private String payDescription;//Срок оплаты комиссии
    private CalcBase calcBase;//База расчета
    private ComissionSize comissionSize;//Порядок расчета V_SPO_COM_BASE 

    public CommissionDeal() {
        super();
    }
    
    public CommissionDeal(Long id) {
        super();
        this.id = id;
    }

    public CommissionDeal(Long id, String description, Currency currency,
            CommissionType name, Double value,
            PatternPaidPercentType procent_order, CalcBase calcBase,
            ComissionSize comissionSize, String payDescription) {
        super();
        this.id = id;
        this.description = description;
        this.currency = currency;
        this.name = name;
        this.value = value;
        this.procent_order = procent_order;
        this.calcBase = calcBase;
        this.comissionSize = comissionSize;
        this.payDescription = payDescription;
        validate();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        if ((currency == null) || (currency.getCode() == null))
            addError("Комиссия. Валюта не определена");
        if ((name == null) || (name.getId() == null))
            addError("Комиссия. Тип комиссии не определен");
        // if ((procent_order == null) || (procent_order.getId() == null))
        // addError("Комиссия. Порядок уплаты процентов не определен");
        // if ((calcBase == null) || (calcBase.getId() == null))
        // addError("Комиссия. База расчета комиссии не определена");
        // if ((comissionSize == null) || (comissionSize.getId() == null))
        // addError("Комиссия. Порядок расчета размера комиссии не определен");
    }

    public String getDescription() {
        return (description != null) ? description : "";
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Currency getCurrency() {
        return currency;
    }
    public Currency getCurrency2() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public CommissionType getName() {
        return name;
    }

    public void setName(CommissionType name) {
        this.name = name;
    }

    public Double getValue() {
        return value;
    }

    public String getFormattedValue() {
        return Formatter.format(value);
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public PatternPaidPercentType getProcent_order() {
        return procent_order;
    }

    public void setProcent_order(PatternPaidPercentType procent_order) {
        this.procent_order = procent_order;
    }

    public String getPayDescription() {
        return (payDescription != null) ? payDescription : "";
    }

    public void setPayDescription(String payDescription) {
        this.payDescription = payDescription;
    }

    public CalcBase getCalcBase() {
        return calcBase;
    }

    public void setCalcBase(CalcBase calcBase) {
        this.calcBase = calcBase;
    }

    public ComissionSize getComissionSize() {
        return comissionSize;
    }

    public void setComissionSize(ComissionSize comissionSize) {
        this.comissionSize = comissionSize;
    }
}
