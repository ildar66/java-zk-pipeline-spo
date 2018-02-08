package ru.md.domain;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;

/**
 * История данных по заявке со статусом
 * Created by Andrey Pavlenko on 11.08.2016.
 */
public class SpoSumHistory {
    private Long idSumHistory;
    private BigDecimal sum;
    private String currency;
    private Long idMdtask;
    private Date statusDate;
    private Long idStatus;
    private Date saveDate;
    private Long creditDocumentary;
    private Long periodMonth;//'срок, мес.'
    private BigDecimal margin;//'маржа, %'
    private BigDecimal profit;//'Ожидаемая доходность'
    private BigDecimal wal;//'Средневзвешенный срок сделки (WAL), мес.'
    private BigDecimal lineCount;//'Выбранный объем линии'
    private BigDecimal availibleLineVolume;//'Объем линии, доступный для выборки'
    private BigDecimal sumProbability;//'Сумма с учетом вероятности'
    private Long weeks;//'недель в пайплайне'
    private String statusPipeline;//'Стадия'
    private String productName;//'Вид сделки'
    private BigDecimal loanRate;//'Ставка фондирования, %'
    private String tradeDesc;//'Трейдинг Деск'
    private Date proposedDtSigning;//'Плановая дата подписания КОД'
    private Boolean prolongation;//'Пролонгация'
    private String contractor;//'Фондирующий Банк'
    private String vtbContractor;//'Выдающий Банк'
    private Long usePeriodMonth;//'Срок использования, мес.'
    private BigDecimal rate;//'% ставка (1й период)'
    private Boolean pub;//'Возможность залога в ЦБ (312-П)'
    private String cmnt;//'Комментарии'
    private Date planDate;//'Плановая даты выборки'
    private BigDecimal sumLast;//'Оставшаяся сумма к выдаче с учетом вероятности'
    private BigDecimal closeProbability;//'Вероятность закрытия, %'
    private BigDecimal comission;//'Комиссия за выдачу, % годовых'
    private String groupname;//'ГК осн.заемщика'
    private String orgname;//осн.заемщик
    private String initdepartment;
    private String branch;
    private boolean interestRateFixed;
    private boolean interestRateDerivative;
    private List<IndRate> indRates;

    public Long getIdSumHistory() {
        return idSumHistory;
    }

    public void setIdSumHistory(Long idSumHistory) {
        this.idSumHistory = idSumHistory;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Long getIdMdtask() {
        return idMdtask;
    }

    public void setIdMdtask(Long idMdtask) {
        this.idMdtask = idMdtask;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    public Long getIdStatus() {
        return idStatus;
    }

    public void setIdStatus(Long idStatus) {
        this.idStatus = idStatus;
    }

    public Date getSaveDate() {
        return saveDate;
    }

    public void setSaveDate(Date saveDate) {
        this.saveDate = (saveDate == null)?null:DateUtils.truncate(saveDate, Calendar.DATE);
    }

    public Long getCreditDocumentary() {
        return creditDocumentary;
    }

    public void setCreditDocumentary(Long creditDocumentary) {
        this.creditDocumentary = creditDocumentary;
    }

    /**
     * Returns .
     * @return
     */
    public Long getPeriodMonth() {
        return periodMonth;
    }

    /**
     * Sets .
     * @param periodMonth
     */
    public void setPeriodMonth(Long periodMonth) {
        this.periodMonth = periodMonth;
    }

    /**
     * Returns .
     * @return
     */
    public BigDecimal getMargin() {
        return margin;
    }

    /**
     * Sets .
     * @param margin
     */
    public void setMargin(BigDecimal margin) {
        this.margin = margin;
    }

    /**
     * Returns .
     * @return
     */
    public BigDecimal getProfit() {
        return profit;
    }

    /**
     * Sets .
     * @param profit
     */
    public void setProfit(BigDecimal profit) {
        this.profit = profit;
    }

    /**
     * Returns .
     * @return
     */
    public BigDecimal getWal() {
        return wal;
    }

    /**
     * Sets .
     * @param wal
     */
    public void setWal(BigDecimal wal) {
        this.wal = wal;
    }

    /**
     * Returns .
     * @return
     */
    public BigDecimal getLineCount() {
        return lineCount;
    }

    /**
     * Sets .
     * @param lineCount
     */
    public void setLineCount(BigDecimal lineCount) {
        this.lineCount = lineCount;
    }

    /**
     * Returns .
     * @return
     */
    public BigDecimal getAvailibleLineVolume() {
        return availibleLineVolume;
    }

    /**
     * Sets .
     * @param availibleLineVolume
     */
    public void setAvailibleLineVolume(BigDecimal availibleLineVolume) {
        this.availibleLineVolume = availibleLineVolume;
    }

    /**
     * Returns .
     * @return
     */
    public BigDecimal getSumProbability() {
        return sumProbability;
    }

    /**
     * Sets .
     * @param sumProbability
     */
    public void setSumProbability(BigDecimal sumProbability) {
        this.sumProbability = sumProbability;
    }

    /**
     * Returns .
     * @return
     */
    public Long getWeeks() {
        return weeks;
    }

    /**
     * Sets .
     * @param weeks
     */
    public void setWeeks(Long weeks) {
        this.weeks = weeks;
    }

    /**
     * Returns .
     * @return
     */
    public String getStatusPipeline() {
        return statusPipeline;
    }

    /**
     * Sets .
     * @param statusPipeline
     */
    public void setStatusPipeline(String statusPipeline) {
        this.statusPipeline = statusPipeline;
    }

    /**
     * Returns .
     * @return
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Sets .
     * @param productName
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * Returns .
     * @return
     */
    public BigDecimal getLoanRate() {
        return loanRate;
    }

    /**
     * Sets .
     * @param loanRate
     */
    public void setLoanRate(BigDecimal loanRate) {
        this.loanRate = loanRate;
    }

    /**
     * Returns .
     * @return
     */
    public String getTradeDesc() {
        return tradeDesc;
    }

    /**
     * Sets .
     * @param tradeDesc
     */
    public void setTradeDesc(String tradeDesc) {
        this.tradeDesc = tradeDesc;
    }

    /**
     * Returns .
     * @return
     */
    public Date getProposedDtSigning() {
        return proposedDtSigning;
    }

    /**
     * Sets .
     * @param proposedDtSigning
     */
    public void setProposedDtSigning(Date proposedDtSigning) {
        this.proposedDtSigning = proposedDtSigning;
    }

    /**
     * Returns .
     * @return
     */
    public Boolean getProlongation() {
        return prolongation;
    }

    /**
     * Sets .
     * @param prolongation
     */
    public void setProlongation(Boolean prolongation) {
        this.prolongation = prolongation;
    }

    /**
     * Returns .
     * @return
     */
    public String getContractor() {
        return contractor;
    }

    /**
     * Sets .
     * @param contractor
     */
    public void setContractor(String contractor) {
        this.contractor = contractor;
    }

    /**
     * Returns .
     * @return
     */
    public String getVtbContractor() {
        return vtbContractor;
    }

    /**
     * Sets .
     * @param vtbContractor
     */
    public void setVtbContractor(String vtbContractor) {
        this.vtbContractor = vtbContractor;
    }

    /**
     * Returns .
     * @return
     */
    public Long getUsePeriodMonth() {
        return usePeriodMonth;
    }

    /**
     * Sets .
     * @param usePeriodMonth
     */
    public void setUsePeriodMonth(Long usePeriodMonth) {
        this.usePeriodMonth = usePeriodMonth;
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
    public Boolean getPub() {
        return pub;
    }

    /**
     * Sets .
     * @param pub
     */
    public void setPub(Boolean pub) {
        this.pub = pub;
    }

    /**
     * Returns .
     * @return
     */
    public String getCmnt() {
        return cmnt;
    }

    /**
     * Sets .
     * @param cmnt
     */
    public void setCmnt(String cmnt) {
        this.cmnt = cmnt;
    }

    /**
     * Returns .
     * @return
     */
    public Date getPlanDate() {
        return planDate;
    }

    /**
     * Sets .
     * @param planDate
     */
    public void setPlanDate(Date planDate) {
        this.planDate = planDate;
    }

    /**
     * Returns .
     * @return
     */
    public BigDecimal getSumLast() {
        return sumLast;
    }

    /**
     * Sets .
     * @param sumLast
     */
    public void setSumLast(BigDecimal sumLast) {
        this.sumLast = sumLast;
    }

    /**
     * Returns .
     * @return
     */
    public BigDecimal getCloseProbability() {
        return closeProbability;
    }

    /**
     * Sets .
     * @param closeProbability
     */
    public void setCloseProbability(BigDecimal closeProbability) {
        this.closeProbability = closeProbability;
    }

    /**
     * Returns .
     * @return
     */
    public BigDecimal getComission() {
        return comission;
    }

    /**
     * Sets .
     * @param comission
     */
    public void setComission(BigDecimal comission) {
        this.comission = comission;
    }

    /**
     * Returns .
     * @return
     */
    public String getGroupname() {
        return groupname;
    }

    /**
     * Sets .
     * @param groupname
     */
    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    /**
     * Returns .
     * @return
     */
    public String getOrgname() {
        return orgname;
    }

    /**
     * Sets .
     * @param orgname
     */
    public void setOrgname(String orgname) {
        this.orgname = orgname;
    }

    /**
     * Returns .
     * @return
     */
    public String getInitdepartment() {
        return initdepartment;
    }

    /**
     * Sets .
     * @param initdepartment
     */
    public void setInitdepartment(String initdepartment) {
        this.initdepartment = initdepartment;
    }
    /**
     * Возвращает тип ставки фиксированная.
     * @return <code>true</code> если ставка фиксированная
     */
    public boolean isInterestRateFixed() {
        return interestRateFixed;
    }

    /**
     * Устанавливает тип ставки.
     * @param fixedRate тип ставки
     */
    public void setInterestRateFixed(boolean interestRateFixed) {
        this.interestRateFixed = interestRateFixed;
    }

    /**
     * Возвращает тип ставки плавающая.
     * @return <code>true</code> если ставка плавающая
     */
    public boolean isInterestRateDerivative() {
        return interestRateDerivative;
    }

    /**
     * Устанавливает тип ставки.
     * @param fixedRate тип ставки
     */
    public void setInterestRateDerivative(boolean interestRateDerivative) {
        this.interestRateDerivative = interestRateDerivative;
    }

    /**
     * Returns .
     * @return
     */
    public List<IndRate> getIndRates() {
        return indRates;
    }

    /**
     * Sets .
     * @param indRates
     */
    public void setIndRates(List<IndRate> indRates) {
        this.indRates = indRates;
    }

    /**
     * Returns .
     * @return
     */
    public String getBranch() {
        return branch;
    }

    /**
     * Sets .
     * @param branch
     */
    public void setBranch(String branch) {
        this.branch = branch;
    }
}
