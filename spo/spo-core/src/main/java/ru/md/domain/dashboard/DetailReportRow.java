package ru.md.domain.dashboard;

import java.math.BigDecimal;
import java.util.Date;

import ru.masterdm.reportsystem.annotation.ReportMark;
import ru.masterdm.reportsystem.annotation.ReportValueFormatter;
import ru.masterdm.reportsystem.list.EFormatterType;

/**
 * @author pmasalov
 */
public class DetailReportRow {
    private Long idSumHistory;
    private Long idMdtask;
    private Long idStatus;
    private String status;
    private String statusPipeline;
    private String initDepartment;
    private String taskType;
    private String taskTypeReport;
    private String currency;
    private BigDecimal sumInRub;
    private BigDecimal sumInUsd;
    private BigDecimal periodMonth;
    private BigDecimal margin;
    private BigDecimal profit;
    private BigDecimal wal;
    private BigDecimal closeProbability;
    private BigDecimal weeks;
    private BigDecimal availibleLineVolume;
    private String availibleLineVolumeReport;
    private BigDecimal lineCount;
    private String lineCountReport;
    private String tradeDesc;
    private BigDecimal mdtaskNumber;
    private BigDecimal version;
    private String groupname;
    private String productName;
    private Boolean prolongation;
    private String contractor;
    private String vtbContractor;
    private BigDecimal rate;
    private Boolean pub;
    private String cmnt;
    private Date planDate;
    private BigDecimal sum;
    private String sumReport;
    private BigDecimal sumLast;
    private String sumLastReport;
    private String orgName;
    private Date dealCreateDate;
    private Date dealChangeDate;
    private String clientManagerReportName;
    private String productManagerReportName;
    private String structuratorReportName;
    private String creditAnalystReportName;
    private Boolean interestRateFixed;
    private Boolean interestRateDerivative;
    private String indRate;
    private String bpName;  //название бизнес-процесса
    private String branch;

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
    @ReportMark(name = "ТипЗаявки")
    public String getTaskTypeReport() {
		return (taskTypeReport == null) ? "" : taskTypeReport;
	}

    /**
     * Sets .
     * @param taskType
     */
	public void setTaskTypeReport(String taskTypeReport) {
		this.taskTypeReport = taskTypeReport;
	}

	/**
     * Returns .
     * @return
     */
    @ReportMark(name = "ВалютаСделки")
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
    @ReportMark(name = "СрокПогашения")
    public BigDecimal getPeriodMonth() {
        return periodMonth;
    }

    /**
     * Sets .
     * @param periodMonth
     */
    public void setPeriodMonth(BigDecimal periodMonth) {
        this.periodMonth = periodMonth;
    }

    /**
     * Returns .
     * @return
     */
    @ReportMark(name = "Маржа")
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
    @ReportMark(name = "ВероятностьЗакрытия")
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
    @ReportMark(name = "ОставшаясяСумма")
    public String getAvailibleLineVolumeReport() {
		return availibleLineVolumeReport;
	}

    /**
     * Sets .
     * @param availibleLineVolumeReport
     */
	public void setAvailibleLineVolumeReport(String availibleLineVolumeReport) {
		this.availibleLineVolumeReport = availibleLineVolumeReport;
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
    @ReportMark(name = "ВыданнаяСумма")
    public String getLineCountReport() {
		return lineCountReport;
	}

    /**
     * Sets .
     * @param lineCountReport
     */
	public void setLineCountReport(String lineCountReport) {
		this.lineCountReport = lineCountReport;
	}

	/**
     * Returns .
     * @return
     */
    @ReportMark(name = "УпрКД")
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
    @ReportMark(name = "НомерСделки")
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
    @ReportMark(name = "Версия")
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
    @ReportMark(name = "ГруппаКомпаний")
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
    @ReportMark(name = "Продукт")
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

    private String createYesNoText(Boolean flag) {
    	if (flag == null)
    		return "";
        return flag ? "да" : "нет";
    }

    /**
     * Returns .
     * @return
     */
    @ReportMark(name = "Пролонгация")
    public String getProlongationString() {
        return createYesNoText(prolongation);
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
    @ReportMark(name = "ФондирующийБанк")
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
    @ReportMark(name = "ВыдающийБанк")
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
    @ReportMark(name = "СтавкаРазмещения")
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
     * Returns .
     * @return
     */
    @ReportMark(name = "ВозможностьЗалога")
    public String getPubString() {
        return createYesNoText(pub);
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
    @ReportMark(name = "Комментарий")
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
    @ReportMark(name = "ПлановаяДата")
    @ReportValueFormatter(formatterType = EFormatterType.DATE)
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
    @ReportMark(name = "ДатаСозданияЗаявки")
    @ReportValueFormatter(formatterType = EFormatterType.DATE)
    public Date getDealCreateDate() {
		return dealCreateDate;
	}

    /**
     * Sets .
     * @param dealCreateDate
     */
	public void setDealCreateDate(Date dealCreateDate) {
		this.dealCreateDate = dealCreateDate;
	}

    /**
     * Returns .
     * @return
     */
    @ReportMark(name = "ДатаИзмененияЗаявки")
    @ReportValueFormatter(formatterType = EFormatterType.DATE)
	public Date getDealChangeDate() {
		return dealChangeDate;
	}

    /**
     * Sets .
     * @param dealChangeDate
     */
	public void setDealChangeDate(Date dealChangeDate) {
		this.dealChangeDate = dealChangeDate;
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
    @ReportMark(name = "СуммаСделки")
    public String getSumReport() {
		return sumReport;
	}

    /**
     * Sets .
     * @param sumReport
     */
	public void setSumReport(String sumReport) {
		this.sumReport = sumReport;
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
    @ReportMark(name = "ОставшаясяСуммаСВероятностью")
    public String getSumLastReport() {
		return sumLastReport;
	}

    /**
     * Sets .
     * @param sumLastReport
     */
	public void setSumLastReport(String sumLastReport) {
		this.sumLastReport = sumLastReport;
	}

	/**
     * Returns .
     * @return
     */
    @ReportMark(name = "ЮрЛицо")
	public String getOrgName() {
		return orgName;
	}

    /**
     * Sets .
     * @param orgName
     */
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	/**
     * Returns .
     * @return
     */
    @ReportMark(name = "Стадия")
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
    @ReportMark(name = "ИнициирующееПодразделение")
    public String getInitDepartment() {
		return initDepartment;
	}

    /**
     * Sets .
     * @param initDepartment
     */
	public void setInitDepartment(String initDepartment) {
		this.initDepartment = initDepartment;
	}

	/**
     * Returns .
     * @return
     */
    @ReportMark(name = "КлиентскийМенеджер")
	public String getClientManagerReportName() {
		return clientManagerReportName;
	}

	/**
     * Sets .
     * @param clientManagerReportName
     */
	public void setClientManagerReportName(String clientManagerReportName) {
		this.clientManagerReportName = clientManagerReportName;
	}

	/**
     * Returns .
     * @return
     */
    @ReportMark(name = "ПродуктовыйМенеджер")
	public String getProductManagerReportName() {
		return productManagerReportName;
	}

    /**
     * Sets .
     * @param productManagerReportName
     */
	public void setProductManagerReportName(String productManagerReportName) {
		this.productManagerReportName = productManagerReportName;
	}

	/**
     * Returns .
     * @return
     */
    @ReportMark(name = "Структуратор")
	public String getStructuratorReportName() {
		return structuratorReportName;
	}

    /**
     * Sets .
     * @param structuratorReportName
     */
	public void setStructuratorReportName(String structuratorReportName) {
		this.structuratorReportName = structuratorReportName;
	}

	/**
     * Returns .
     * @return
     */
    @ReportMark(name = "КредитныйАналитик")
	public String getCreditAnalystReportName() {
		return creditAnalystReportName;
	}

    /**
     * Sets .
     * @param creditAnalystReportName
     */
	public void setCreditAnalystReportName(String creditAnalystReportName) {
		this.creditAnalystReportName = creditAnalystReportName;
	}

	/**
     * Returns .
     * @return
     */
	public Boolean getInterestRateFixed() {
		return (interestRateFixed == null) ? false : interestRateFixed;
	}

    /**
     * Sets .
     * @param interestRateFixed
     */
	public void setInterestRateFixed(Boolean interestRateFixed) {
		this.interestRateFixed = interestRateFixed;
	}

	/**
     * Returns .
     * @return
     */
	public Boolean getInterestRateDerivative() {
		return (interestRateDerivative == null) ? false : interestRateDerivative;
	}

    /**
     * Sets .
     * @param interestRateDerivative
     */
	public void setInterestRateDerivative(Boolean interestRateDerivative) {
		this.interestRateDerivative = interestRateDerivative;
	}

	/**
     * Returns .
     * @return
     */
    @ReportMark(name = "ИндикативнаяСтавка")
	public String getIndRate() {
		return indRate;
	}

    /**
     * Sets .
     * @param indRate
     */
	public void setIndRate(String indRate) {
		this.indRate = indRate;
	}

	/**
     * Returns .
     * @return
     */
    @ReportMark(name = "БизнесПроцесс")
	public String getBpName() {
		return bpName;
	}

    /**
     * Sets .
     * @param bpName
     */
	public void setBpName(String bpName) {
		this.bpName = bpName;
	}

    /**
     * Возвращает название отрасли.
     * @return название отрасли
     */
    @ReportMark(name = "Отрасль")
    public String getBranch() {
        return branch;
    }

    /**
     * Устанавливает название отрасли.
     * @param branch название отрасли
     */
    public void setBranch(String branch) {
        this.branch = branch;
    }
}
