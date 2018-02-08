package ru.md.domain.dashboard;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Отчёт клиентская справка V_CLIENT_REPORT.
 * Created by Andrey Pavlenko on 05.09.2016.
 */
public class SpoClientReport {
    private Long idMdtask;//ID заявки
    private Date saveDate;//Отчетная дата
    private String mdtaskNumber;//Номер Заявки
    private String version;//Версия Заявки
    private String category;//Категория объединяющая заявки (Сделки, лимиты, Кросс-селл, Изменение сделок и Вейверы)
    private String state	;//Состояние заявки
    private String taskType;//Тип заявки
    private BigDecimal taskSum;//Сумма заявки в валюте
    private String currency;//Валюта суммы заявки
    private BigDecimal curRate;//Значение курса валюты заявки по отношению к рублю при пересчете
    private Date rateDate;//Дата курса для пересчета
    private BigDecimal sumRub;//Сумма, руб
    private BigDecimal sumUsd;//Сумма, дол. США
    private Long periodMonth;//Срок, мес.
    private String margin;//Маржа, %
    private BigDecimal profit;//Ожидаемая доходность, руб.
    private Long weeks;//Кол-во Недель в Пайплайне
    private Date createDate;//Дата начала работы над сделкой
    private Date proposedDtSigning;//Плановая Дата Подписания КОД
    private Date planDate;//Плановая Дата Выборки
    private Date updateDate;//Дата последнего обновления
    private String industry;//Отрасль
    private String idGroup;//группа компании ID
    private String groupName;//Группа Компаний. Название
    private String idOrg;//ID ЕК компании - основного заемщика
    private String idKz	;//ID КЗ основного заемщика
    private String mainOrg;//Наименование Компания – основной заемщик (ЕК)
    private String kzName;//Наименование Компании – основного заемщика по месту проведения сделки (КЗ)
    private String supplyOrg;//Поручители, Гаранты и др. Участники Сделки
    private String status;//Стадия Сделки
    private BigDecimal closeProbability;//Вероятность Закрытия
    private String productName;//Вид Сделки
    private String supply;//Обеспечение
    private String ensurings;//Предмет Залога
    private String targets;//Цель Финансирования
    private String description;//Описание Сделки, Включая Структуру, Деривативы, и т. д.
    private String cmnt;//Комментарии по Статусу Сделки, Следующие Шаги
    private String additionBusiness;//Дополнительный Бизнес, Сроки, Примерный Объём в млн. дол. США
    private Long usePeriodMonth;//Срок Использования, мес.
    private Long wal;//Средневзвешенный Срок Погашения, (WAL), мес.
    private String fixedFloat;//Фиксированная / Плавающая Ставка
    private BigDecimal baseRate;//Базовая Ставка (если Плавающая)
    private BigDecimal fixrate;//Спред или Фиксированная Ставка, %
    private BigDecimal loanRate;//Ставка Фондирования, %
    private BigDecimal comission;//Комиссия за Выдачу, %
    private BigDecimal pcDer;//FX Rates, млн. дол. США
    private BigDecimal pcTotal;//Commodities, млн. дол. США
    private BigDecimal lineCount;//Выбранный Объём Линии,  в Валюте Сделки
    private BigDecimal availibleLineVolume;//Объём Линии, Доступный для Выборки, в Валюте Сделки
    private Boolean pub;//312 П
    private Boolean priority;//Приоритет Менеджмента
    private Boolean newClient;//Новый / Существующий Клиент
    private String flowInvestment;//ТЭФ Импорт/ Экспорт
    private String productManager;//Продуктовый менеджер
    private String analyst;//Кредитный Аналитик
    private String clientManager;//Клиентский Менеджер
    private String structurator;//Структуратор
    private String gss;//Менеджер ГСС
    private String contractor;//Фондирующий Банк
    private String vtbContractor;//Выдающий Банк
    private String tradeDesc;//Трейдинг Деск
    private Boolean prolongation;//Пролонгация
    private String projectName;//Наименование проекта/Контрагент

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
    public Date getSaveDate() {
        return saveDate;
    }

    /**
     * Sets .
     * @param saveDate
     */
    public void setSaveDate(Date saveDate) {
        this.saveDate = saveDate;
    }

    /**
     * Returns .
     * @return
     */
    public String getMdtaskNumber() {
        return mdtaskNumber;
    }

    /**
     * Sets .
     * @param mdtaskNumber
     */
    public void setMdtaskNumber(String mdtaskNumber) {
        this.mdtaskNumber = mdtaskNumber;
    }

    /**
     * Returns .
     * @return
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets .
     * @param version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Returns .
     * @return
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets .
     * @param category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Returns .
     * @return
     */
    public String getState() {
        return state;
    }

    /**
     * Sets .
     * @param state
     */
    public void setState(String state) {
        this.state = state;
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
    public BigDecimal getTaskSum() {
        return taskSum;
    }

    /**
     * Sets .
     * @param taskSum
     */
    public void setTaskSum(BigDecimal taskSum) {
        this.taskSum = taskSum;
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
    public BigDecimal getCurRate() {
        return curRate;
    }

    /**
     * Sets .
     * @param curRate
     */
    public void setCurRate(BigDecimal curRate) {
        this.curRate = curRate;
    }

    /**
     * Returns .
     * @return
     */
    public Date getRateDate() {
        return rateDate;
    }

    /**
     * Sets .
     * @param rateDate
     */
    public void setRateDate(Date rateDate) {
        this.rateDate = rateDate;
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
    public String getMargin() {
        return margin;
    }

    /**
     * Sets .
     * @param margin
     */
    public void setMargin(String margin) {
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
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * Sets .
     * @param createDate
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
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
    public Date getUpdateDate() {
        return updateDate;
    }

    /**
     * Sets .
     * @param updateDate
     */
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
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
    public String getIdGroup() {
        return idGroup;
    }

    /**
     * Sets .
     * @param idGroup
     */
    public void setIdGroup(String idGroup) {
        this.idGroup = idGroup;
    }

    /**
     * Returns .
     * @return
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * Sets .
     * @param groupName
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * Returns .
     * @return
     */
    public String getIdOrg() {
        return idOrg;
    }

    /**
     * Sets .
     * @param idOrg
     */
    public void setIdOrg(String idOrg) {
        this.idOrg = idOrg;
    }

    /**
     * Returns .
     * @return
     */
    public String getIdKz() {
        return idKz;
    }

    /**
     * Sets .
     * @param idKz
     */
    public void setIdKz(String idKz) {
        this.idKz = idKz;
    }

    /**
     * Returns .
     * @return
     */
    public String getMainOrg() {
        return mainOrg;
    }

    /**
     * Sets .
     * @param mainOrg
     */
    public void setMainOrg(String mainOrg) {
        this.mainOrg = mainOrg;
    }

    /**
     * Returns .
     * @return
     */
    public String getKzName() {
        return kzName;
    }

    /**
     * Sets .
     * @param kzName
     */
    public void setKzName(String kzName) {
        this.kzName = kzName;
    }

    /**
     * Returns .
     * @return
     */
    public String getSupplyOrg() {
        return supplyOrg;
    }

    /**
     * Sets .
     * @param supplyOrg
     */
    public void setSupplyOrg(String supplyOrg) {
        this.supplyOrg = supplyOrg;
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
    public String getSupply() {
        return supply;
    }

    /**
     * Sets .
     * @param supply
     */
    public void setSupply(String supply) {
        this.supply = supply;
    }

    /**
     * Returns .
     * @return
     */
    public String getEnsurings() {
        return ensurings;
    }

    /**
     * Sets .
     * @param ensurings
     */
    public void setEnsurings(String ensurings) {
        this.ensurings = ensurings;
    }

    /**
     * Returns .
     * @return
     */
    public String getTargets() {
        return targets;
    }

    /**
     * Sets .
     * @param targets
     */
    public void setTargets(String targets) {
        this.targets = targets;
    }

    /**
     * Returns .
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets .
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
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
    public String getAdditionBusiness() {
        return additionBusiness;
    }

    /**
     * Sets .
     * @param additionBusiness
     */
    public void setAdditionBusiness(String additionBusiness) {
        this.additionBusiness = additionBusiness;
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
    public Long getWal() {
        return wal;
    }

    /**
     * Sets .
     * @param wal
     */
    public void setWal(Long wal) {
        this.wal = wal;
    }

    /**
     * Returns .
     * @return
     */
    public String getFixedFloat() {
        return fixedFloat;
    }

    /**
     * Sets .
     * @param fixedFloat
     */
    public void setFixedFloat(String fixedFloat) {
        this.fixedFloat = fixedFloat;
    }

    /**
     * Returns .
     * @return
     */
    public BigDecimal getBaseRate() {
        return baseRate;
    }

    /**
     * Sets .
     * @param baseRate
     */
    public void setBaseRate(BigDecimal baseRate) {
        this.baseRate = baseRate;
    }

    /**
     * Returns .
     * @return
     */
    public BigDecimal getFixrate() {
        return fixrate;
    }

    /**
     * Sets .
     * @param fixrate
     */
    public void setFixrate(BigDecimal fixrate) {
        this.fixrate = fixrate;
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
    public BigDecimal getPcDer() {
        return pcDer;
    }

    /**
     * Sets .
     * @param pcDer
     */
    public void setPcDer(BigDecimal pcDer) {
        this.pcDer = pcDer;
    }

    /**
     * Returns .
     * @return
     */
    public BigDecimal getPcTotal() {
        return pcTotal;
    }

    /**
     * Sets .
     * @param pcTotal
     */
    public void setPcTotal(BigDecimal pcTotal) {
        this.pcTotal = pcTotal;
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
    public Boolean getPriority() {
        return priority;
    }

    /**
     * Sets .
     * @param priority
     */
    public void setPriority(Boolean priority) {
        this.priority = priority;
    }

    /**
     * Returns .
     * @return
     */
    public Boolean getNewClient() {
        return newClient;
    }

    /**
     * Sets .
     * @param newClient
     */
    public void setNewClient(Boolean newClient) {
        this.newClient = newClient;
    }

    /**
     * Returns .
     * @return
     */
    public String getFlowInvestment() {
        return flowInvestment;
    }

    /**
     * Sets .
     * @param flowInvestment
     */
    public void setFlowInvestment(String flowInvestment) {
        this.flowInvestment = flowInvestment;
    }

    /**
     * Returns .
     * @return
     */
    public String getProductManager() {
        return productManager;
    }

    /**
     * Sets .
     * @param productManager
     */
    public void setProductManager(String productManager) {
        this.productManager = productManager;
    }

    /**
     * Returns .
     * @return
     */
    public String getAnalyst() {
        return analyst;
    }

    /**
     * Sets .
     * @param analyst
     */
    public void setAnalyst(String analyst) {
        this.analyst = analyst;
    }

    /**
     * Returns .
     * @return
     */
    public String getClientManager() {
        return clientManager;
    }

    /**
     * Sets .
     * @param clientManager
     */
    public void setClientManager(String clientManager) {
        this.clientManager = clientManager;
    }

    /**
     * Returns .
     * @return
     */
    public String getStructurator() {
        return structurator;
    }

    /**
     * Sets .
     * @param structurator
     */
    public void setStructurator(String structurator) {
        this.structurator = structurator;
    }

    /**
     * Returns .
     * @return
     */
    public String getGss() {
        return gss;
    }

    /**
     * Sets .
     * @param gss
     */
    public void setGss(String gss) {
        this.gss = gss;
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
    public String getProjectName() {
        return projectName;
    }

    /**
     * Sets .
     * @param projectName
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
