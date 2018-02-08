package ru.md.domain.dashboard;

import java.math.BigDecimal;

import ru.masterdm.spo.list.EDashStatus;

/**
 * Created by Andrey Pavlenko on 27.08.2016.
 * сводный отчет
 */
public class MainReportRow {
    private Long idStatus;
    private String taskType;
    private BigDecimal sumRub;
    private BigDecimal sumUsd;
    private int countAll;
    private Long countUsd;
    private Long countRur;
    private Long countEur;
    private BigDecimal avgPeriodMonth;
    private BigDecimal sumPeriodMonth;
    private BigDecimal wavMargin;
    private BigDecimal sumProfit;
    private BigDecimal avgWal;
    private BigDecimal sumRubProb;
    private BigDecimal avgWeeks;
    private BigDecimal sumWeeks;
    private BigDecimal avgSumRub;
    private BigDecimal sumAvailibleLineVolume;
    private BigDecimal sumLineCount;
    private BigDecimal avgLineCount;

    /**
     * Returns .
     * @return
     */
    public Long getIdStatus() {
        return idStatus;
    }
    public String getStatusName() {
        for (EDashStatus s : EDashStatus.values())
            if (s.getId().equals(idStatus))
                return s.getName();
        return "";
    }

    /**
     * Sets .
     * @param idStatus
     */
    public void setIdStatus(Long idStatus) {
        this.idStatus = idStatus;
    }

    /**
     * Returns .
     * @return
     */
    public String getTaskType() {
        return taskType;
    }

    /**
     * Sets .
     * @param taskType
     */
    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    /**
     * Returns .
     * @return
     */
    public BigDecimal getSumRub() {
        return sumRub;
    }

    /**
     * Sets .
     * @param sumRub
     */
    public void setSumRub(BigDecimal sumRub) {
        this.sumRub = sumRub;
    }

    /**
     * Returns .
     * @return
     */
    public BigDecimal getSumUsd() {
        return sumUsd;
    }

    /**
     * Sets .
     * @param sumUsd
     */
    public void setSumUsd(BigDecimal sumUsd) {
        this.sumUsd = sumUsd;
    }

    /**
     * Returns .
     * @return
     */
    public int getCountAll() {
        return countAll;
    }

    /**
     * Sets .
     * @param countAll
     */
    public void setCountAll(int countAll) {
        this.countAll = countAll;
    }

    /**
     * Returns .
     * @return
     */
    public Long getCountUsd() {
        return countUsd;
    }

    /**
     * Sets .
     * @param countUsd
     */
    public void setCountUsd(Long countUsd) {
        this.countUsd = countUsd;
    }

    /**
     * Returns .
     * @return
     */
    public Long getCountRur() {
        return countRur;
    }

    /**
     * Sets .
     * @param countRur
     */
    public void setCountRur(Long countRur) {
        this.countRur = countRur;
    }

    /**
     * Returns .
     * @return
     */
    public Long getCountEur() {
        return countEur;
    }

    /**
     * Sets .
     * @param countEur
     */
    public void setCountEur(Long countEur) {
        this.countEur = countEur;
    }

    /**
     * Returns .
     * @return
     */
    public BigDecimal getAvgPeriodMonth() {
        return avgPeriodMonth;
    }

    /**
     * Sets .
     * @param avgPeriodMonth
     */
    public void setAvgPeriodMonth(BigDecimal avgPeriodMonth) {
        this.avgPeriodMonth = avgPeriodMonth;
    }

    /**
     * Returns .
     * @return
     */
    public BigDecimal getWavMargin() {
        return wavMargin;
    }

    /**
     * Sets .
     * @param wavMargin
     */
    public void setWavMargin(BigDecimal wavMargin) {
        this.wavMargin = wavMargin;
    }

    /**
     * Returns .
     * @return
     */
    public BigDecimal getSumProfit() {
        return sumProfit;
    }

    /**
     * Sets .
     * @param sumProfit
     */
    public void setSumProfit(BigDecimal sumProfit) {
        this.sumProfit = sumProfit;
    }

    /**
     * Returns .
     * @return
     */
    public BigDecimal getAvgWal() {
        return avgWal;
    }

    /**
     * Sets .
     * @param avgWal
     */
    public void setAvgWal(BigDecimal avgWal) {
        this.avgWal = avgWal;
    }

    /**
     * Returns .
     * @return
     */
    public BigDecimal getSumRubProb() {
        return sumRubProb;
    }

    /**
     * Sets .
     * @param sumRubProb
     */
    public void setSumRubProb(BigDecimal sumRubProb) {
        this.sumRubProb = sumRubProb;
    }

    /**
     * Returns .
     * @return
     */
    public BigDecimal getAvgWeeks() {
        return avgWeeks;
    }

    /**
     * Sets .
     * @param avgWeeks
     */
    public void setAvgWeeks(BigDecimal avgWeeks) {
        this.avgWeeks = avgWeeks;
    }

    /**
     * Returns .
     * @return
     */
    public BigDecimal getAvgSumRub() {
        return avgSumRub;
    }

    /**
     * Sets .
     * @param avgSumRub
     */
    public void setAvgSumRub(BigDecimal avgSumRub) {
        this.avgSumRub = avgSumRub;
    }

    /**
     * Returns .
     * @return
     */
    public BigDecimal getSumAvailibleLineVolume() {
        return sumAvailibleLineVolume;
    }

    /**
     * Sets .
     * @param sumAvailibleLineVolume
     */
    public void setSumAvailibleLineVolume(BigDecimal sumAvailibleLineVolume) {
        this.sumAvailibleLineVolume = sumAvailibleLineVolume;
    }

    /**
     * Returns .
     * @return
     */
    public BigDecimal getSumLineCount() {
        return sumLineCount;
    }

    /**
     * Sets .
     * @param sumLineCount
     */
    public void setSumLineCount(BigDecimal sumLineCount) {
        this.sumLineCount = sumLineCount;
    }

    /**
     * Returns .
     * @return
     */
    public BigDecimal getAvgLineCount() {
        return avgLineCount;
    }

    /**
     * Sets .
     * @param avgLineCount
     */
    public void setAvgLineCount(BigDecimal avgLineCount) {
        this.avgLineCount = avgLineCount;
    }

    /**
     * Returns .
     * @return
     */
    public BigDecimal getSumPeriodMonth() {
        return sumPeriodMonth;
    }

    /**
     * Sets .
     * @param sumPeriodMonth
     */
    public void setSumPeriodMonth(BigDecimal sumPeriodMonth) {
        this.sumPeriodMonth = sumPeriodMonth;
    }

    /**
     * Returns .
     * @return
     */
    public BigDecimal getSumWeeks() {
        return sumWeeks;
    }

    /**
     * Sets .
     * @param sumWeeks
     */
    public void setSumWeeks(BigDecimal sumWeeks) {
        this.sumWeeks = sumWeeks;
    }
}
