package ru.masterdm.spo.dashboard.domain;

import java.math.BigDecimal;

import ru.masterdm.spo.dashboard.model.SummaryFigure;

/**
 * Элемент данных для показа определённого сводного показателя в "Сводке"
 * @author pmasalov
 */
public class SummaryData {

    public static final BigDecimal _ZERO = new BigDecimal(0);

    private SummaryFigure summaryFigure;

    //private String name;
    private BigDecimal mainValue = _ZERO;
    private BigDecimal mainValueCompare = _ZERO;
    private int compareMain = 0;
    //private String unitOfMeasure;

    private Integer count = null;
    private Integer countCompare = null;

    private boolean selected;

    public SummaryData(boolean selected, SummaryFigure figure, BigDecimal mainValue, BigDecimal mainValueCompare, Integer count,
                       Integer countCompare) {
        this(selected, figure, mainValue, mainValueCompare);
        this.count = count;
        this.countCompare = countCompare;
    }

    public SummaryData(boolean selected, SummaryFigure figure, BigDecimal mainValue, BigDecimal mainValueCompare) {
        this.selected = selected;
        this.summaryFigure = figure;
        //this.name = name;
        this.mainValue = mainValue;
        this.mainValueCompare = mainValueCompare;
        //this.unitOfMeasure = unitOfMeasure;
        cmpMain();
    }

    /**
     * Returns .
     * @return
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Sets .
     * @param selected
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Returns .
     * @return
     */
    public SummaryFigure getSummaryFigure() {
        return summaryFigure;
    }

    public int getCompareMain() {
        return compareMain;
    }

    private void cmpMain() {
        compareMain = mainValue.compareTo(mainValueCompare);
    }

    public int getCompareCount() {
        if (count == null || countCompare == null)
            return 0;
        return count.compareTo(countCompare);
    }

    /**
     * Returns .
     * @return
     */
    public String getName() {
        return summaryFigure.getDescription();
    }

    /**
     * Returns .
     * @return
     */
    public BigDecimal getMainValue() {
        return mainValue;
    }

    /**
     * Sets .
     * @param mainValue
     */
    public void setMainValue(BigDecimal mainValue) {
        this.mainValue = (mainValue == null ? _ZERO : mainValue);
        cmpMain();
    }

    /**
     * Returns .
     * @return
     */
    public BigDecimal getMainValueCompare() {
        return mainValueCompare;
    }

    /**
     * Sets .
     * @param mainValueCompare
     */
    public void setMainValueCompare(BigDecimal mainValueCompare) {
        this.mainValueCompare = (mainValueCompare == null ? _ZERO : mainValueCompare);
        cmpMain();
    }

    /**
     * Returns .
     * @return
     */
    public String getUnitOfMeasure() {
        return summaryFigure.getUnitOfMeasure();
    }

    /**
     * Returns .
     * @return
     */
    public Integer getCount() {
        return count;
    }

    /**
     * Sets .
     * @param count
     */
    public void setCount(Integer count) {
        this.count = count;
    }

    /**
     * Returns .
     * @return
     */
    public Integer getCountCompare() {
        return countCompare;
    }

    /**
     * Sets .
     * @param countCompare
     */
    public void setCountCompare(Integer countCompare) {
        this.countCompare = countCompare;
    }
}
