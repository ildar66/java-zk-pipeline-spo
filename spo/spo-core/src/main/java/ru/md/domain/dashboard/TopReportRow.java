package ru.md.domain.dashboard;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author pmasalov
 */
public class TopReportRow { // класс очень похож на DetailReportRow
    private int nn;
    private Long idSumHistory;
    private Long idMdtask;
    private Long idStatus;
    private String status;
    private String statusPipeline;
    private String taskType;
    private String currency;
    private BigDecimal sumInRub;
    private BigDecimal sumInUsd;
    private Integer periodMonth;
    private BigDecimal margin;
    private BigDecimal profit;
    private BigDecimal wal;
    private BigDecimal closeProbability;
    private BigDecimal weeks;
    private BigDecimal weeksCalc;
    private BigDecimal availibleLineVolumeRub;
    private BigDecimal lineCountRub;
    private String tradeDesc;
    private BigDecimal mdtaskNumber;
    private BigDecimal version;
    private String groupname;
    private String productName;
    private Boolean prolongation;
    private String contractor;
    private String vtbContractor;
    private BigDecimal rate;
    private BigDecimal loanRate;
    private Boolean pub;
    private String cmnt;
    private Date planDate;
    private BigDecimal sum;
    private BigDecimal sumLastRub;
    private String orgname;
    private Integer usePeriodMonth;
    private BigDecimal comission;
    private String inn;
    private String slxCodeEk;
    private String slxCodeKz;
    private String industry;
    private Date planDateCod;
    private Date fixDate;
    private Date proposedDtSigning;

    /**
     * Returns .
     * @return
     */
    public Long getIdSumHistory() {
        return idSumHistory;
    }

    /**
     * Sets .
     * @param idSumHistory
     */
    public void setIdSumHistory(Long idSumHistory) {
        this.idSumHistory = idSumHistory;
    }

    /**
     * Returns .
     * @return
     */
    public Long getIdMdtask() {
        return idMdtask;
    }

    /**
     * Sets .
     * @param idMdtask
     */
    public void setIdMdtask(Long idMdtask) {
        this.idMdtask = idMdtask;
    }

    /**
     * Returns .
     * @return
     */
    public Long getIdStatus() {
        return idStatus;
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
    public String getStatus() {
        return status;
    }

    /**
     * Sets .
     * @param status
     */
    public void setStatus(String status) {
        this.status = status;
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
    public String getCurrency() {
        return currency;
    }

    /**
     * Sets .
     * @param currency
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * Returns .
     * @return
     */
    public BigDecimal getSumInRub() {
        return sumInRub;
    }

    /**
     * Sets .
     * @param sumInRub
     */
    public void setSumInRub(BigDecimal sumInRub) {
        this.sumInRub = sumInRub;
    }

    /**
     * Returns .
     * @return
     */
    public BigDecimal getSumInUsd() {
        return sumInUsd;
    }

    /**
     * Sets .
     * @param sumInUsd
     */
    public void setSumInUsd(BigDecimal sumInUsd) {
        this.sumInUsd = sumInUsd;
    }

    /**
     * Returns .
     * @return
     */
    public Integer getPeriodMonth() {
        return periodMonth;
    }

    /**
     * Sets .
     * @param periodMonth
     */
    public void setPeriodMonth(Integer periodMonth) {
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
    public BigDecimal getWeeks() {
        return weeks;
    }

    /**
     * Sets .
     * @param weeks
     */
    public void setWeeks(BigDecimal weeks) {
        this.weeks = weeks;
    }

    /**
     * Returns .
     * @return
     */
    public BigDecimal getAvailibleLineVolumeRub() {
        return availibleLineVolumeRub;
    }

    /**
     * Sets .
     * @param availibleLineVolumeRub
     */
    public void setAvailibleLineVolumeRub(BigDecimal availibleLineVolumeRub) {
        this.availibleLineVolumeRub = availibleLineVolumeRub;
    }

    /**
     * Returns .
     * @return
     */
    public BigDecimal getLineCountRub() {
        return lineCountRub;
    }

    /**
     * Sets .
     * @param lineCountRub
     */
    public void setLineCountRub(BigDecimal lineCountRub) {
        this.lineCountRub = lineCountRub;
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
    public BigDecimal getMdtaskNumber() {
        return mdtaskNumber;
    }

    /**
     * Sets .
     * @param mdtaskNumber
     */
    public void setMdtaskNumber(BigDecimal mdtaskNumber) {
        this.mdtaskNumber = mdtaskNumber;
    }

    /**
     * Returns .
     * @return
     */
    public BigDecimal getVersion() {
        return version;
    }

    /**
     * Sets .
     * @param version
     */
    public void setVersion(BigDecimal version) {
        this.version = version;
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
    public BigDecimal getSum() {
        return sum;
    }

    /**
     * Sets .
     * @param sum
     */
    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    /**
     * Returns .
     * @return
     */
    public BigDecimal getSumLastRub() {
        return sumLastRub;
    }

    /**
     * Sets .
     * @param sumLastRub
     */
    public void setSumLastRub(BigDecimal sumLastRub) {
        this.sumLastRub = sumLastRub;
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
    public Integer getUsePeriodMonth() {
        return usePeriodMonth;
    }

    /**
     * Sets .
     * @param usePeriodMonth
     */
    public void setUsePeriodMonth(Integer usePeriodMonth) {
        this.usePeriodMonth = usePeriodMonth;
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
    public String getInn() {
        return inn;
    }

    /**
     * Sets .
     * @param inn
     */
    public void setInn(String inn) {
        this.inn = inn;
    }

    /**
     * Returns .
     * @return
     */
    public String getSlxCodeEk() {
        return slxCodeEk;
    }

    /**
     * Sets .
     * @param slxCodeEk
     */
    public void setSlxCodeEk(String slxCodeEk) {
        this.slxCodeEk = slxCodeEk;
    }

    /**
     * Returns .
     * @return
     */
    public String getSlxCodeKz() {
        return slxCodeKz;
    }

    /**
     * Sets .
     * @param slxCodeKz
     */
    public void setSlxCodeKz(String slxCodeKz) {
        this.slxCodeKz = slxCodeKz;
    }

    /**
     * Returns .
     * @return
     */
    public String getIndustry() {
        return industry;
    }

    /**
     * Sets .
     * @param industry
     */
    public void setIndustry(String industry) {
        this.industry = industry;
    }

    /**
     * Returns .
     * @return
     */
    public Date getPlanDateCod() {
        return planDateCod;
    }

    /**
     * Sets .
     * @param planDateCod
     */
    public void setPlanDateCod(Date planDateCod) {
        this.planDateCod = planDateCod;
    }

    /**
     * Returns .
     * @return
     */
    public Date getFixDate() {
        return fixDate;
    }

    /**
     * Sets .
     * @param fixDate
     */
    public void setFixDate(Date fixDate) {
        this.fixDate = fixDate;
    }

    /**
     * Returns .
     * @return
     */
    public int getNn() {
        return nn;
    }

    /**
     * Sets .
     * @param nn
     */
    public void setNn(int nn) {
        this.nn = nn;
    }

    /**
     * Returns .
     * @return
     */
    public BigDecimal getWeeksCalc() {
        return weeksCalc;
    }

    /**
     * Sets .
     * @param weeksCalc
     */
    public void setWeeksCalc(BigDecimal weeksCalc) {
        this.weeksCalc = weeksCalc;
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
}
