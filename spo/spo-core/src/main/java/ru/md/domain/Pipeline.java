package ru.md.domain;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import ru.masterdm.spo.utils.Formatter;
import ru.md.domain.dict.CommonDictionary;
import ru.md.domain.dict.ProcessType;

/**
 * Pipeline.
 * @author Sergey Valiev
 */
public class Pipeline {
    
    private Long id;
    private MdTask mdTask;
    private Date planDate;
    private String status;
    private BigDecimal closeProbability;
    private String law; 
    private String geography;
    private String supply;
    private List<String> financingObjectives;
    private String description;
    private String note;
    private String additionalBusiness;
    private boolean syndication;
    private String syndicationNote;
    private BigDecimal wal;
    private BigDecimal hurdleRate;
    private BigDecimal markup;
    private BigDecimal pcCash;
    private BigDecimal pcReserve;
    private BigDecimal pcDerivative;
    private BigDecimal pcTotal;
    private BigDecimal selectedLineVolume;
    private boolean publicDeal;
    private boolean managementPriority;
    private boolean statusManual;
    private boolean newClient;
    private String flowInvestment;
    private Integer tradeFinance;
    private String tradeFinanceName;
    private BigDecimal productTypeFactor;
    private BigDecimal periodFactor;
    private String fundCompany;
    private String vtbContractor;
    private String tradingDesk;
    private boolean prolongation;
    private boolean hideInReport;
    private boolean hideInReportTraders;
    private boolean managerPriority;
    private User productManager;
    private User creditAnalyst;
    private User structureInspector;
    private User clientManager;
    private BigDecimal margin;
    private List<CommonDictionary<String>> ensurings;
    
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
     * Возвращает заявку.
     * @return заявка
     */
    public MdTask getMdTask() {
        return mdTask;
    }
    
    /**
     * Устанавливает заявку.
     * @param mdTask заявка
     */
    public void setMdTask(MdTask mdTask) {
        this.mdTask = mdTask;
    }
    
    /**
     * Возвращает плановая дата выборки.
     * @return плановая дата выборки
     */
    public Date getPlanDate() {
        return planDate;
    }
    
    /**
     * Устанавливает плановая дата выборки.
     * @param planDate плановая дата выборки
     */
    public void setPlanDate(Date planDate) {
        this.planDate = planDate;
    }

    /**
     * Возвращает статус сделки.
     * @return статус сделки
     */
    public String getStatus() {
        return status;
    }

    /**
     * Устанавливает статус сделки.
     * @param status статус сделки 
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Возвращает вероятность закрытия.
     * @return вероятность закрытия
     */
    public BigDecimal getCloseProbability() {
        return closeProbability;
    }

    /**
     * Устанавливает вероятность закрытия.
     * @param closeProbability вероятность закрытия
     */
    public void setCloseProbability(BigDecimal closeProbability) {
        this.closeProbability = closeProbability;
    }

    /**
     * Возвращает применимое право.
     * @return применимое право
     */
    public String getLaw() {
        return law;
    }

    /**
     * Устанавливает применимое право.
     * @param law применимое право
     */
    public void setLaw(String law) {
        this.law = law;
    }

    /**
     * Возвращает география.
     * @return география
     */
    public String getGeography() {
        return geography;
    }

    /**
     * Устанавливает география.
     * @param geography география
     */
    public void setGeography(String geography) {
        this.geography = geography;
    }

    /**
     * Возвращает обеспечение.
     * @return обеспечение
     */
    public String getSupply() {
        return supply;
    }

    /**
     * Устанавливает обеспечение.
     * @param supply обеспечение
     */
    public void setSupply(String supply) {
        this.supply = supply;
    }

    /**
     * Возвращает цели финансирования.
     * @return цели финансирования
     */
    public List<String> getFinancingObjectives() {
        return financingObjectives;
    }

    /**
     * Устанавливает цели финансирования.
     * @param financingObjectives цели финансирования
     */
    public void setFinancingObjectives(List<String> financingObjectives) {
        this.financingObjectives = financingObjectives;
    }

    /**
     * Возвращает описание.
     * @return описание
     */
    public String getDescription() {
        return description;
    }

    /**
     * Устанавливает описание.
     * @param description описание
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Возвращает комментарий.
     * @return комментарий
     */
    public String getNote() {
        return note;
    }

    /**
     * Устанавливает комментарий.
     * @param note комментарий
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * Возвращает дополнительный бизнес.
     * @return дополнительный бизнес
     */
    public String getAdditionalBusiness() {
        return additionalBusiness;
    }

    /**
     * Устанавливает дополнительный бизнес.
     * @param additionalBusiness дополнительный бизнес
     */
    public void setAdditionalBusiness(String additionalBusiness) {
        this.additionalBusiness = additionalBusiness;
    }

    /**
     * Возвращает возможность синдикации.
     * @return возможность синдикации
     */
    public boolean isSyndication() {
        return syndication;
    }

    /**
     * Устанавливает возможность синдикации.
     * @param syndication возможность синдикации
     */
    public void setSyndication(boolean syndication) {
        this.syndication = syndication;
    }

    /**
     * Возвращает комментарии по синдикации.
     * @return комментарии по синдикации
     */
    public String getSyndicationNote() {
        return syndicationNote;
    }

    /**
     * Устанавливает комментарии по синдикации.
     * @param syndicationNote комментарии по синдикации
     */
    public void setSyndicationNote(String syndicationNote) {
        this.syndicationNote = syndicationNote;
    }

    /**
     * Возвращает средневзвешенный срок погашения.
     * @return средневзвешенный срок погашения
     */
    public BigDecimal getWal() {
        return wal;
    }

    /**
     * Устанавливает средневзвешенный срок погашения.
     * @param wal средневзвешенный срок погашения
     */
    public void setWal(BigDecimal wal) {
        this.wal = wal;
    }

    /**
     * Возвращает минимальная ставка.
     * @return минимальная ставка
     */
    public BigDecimal getHurdleRate() {
        return hurdleRate;
    }

    /**
     * Устанавливает минимальная ставка.
     * @param hurdleRate минимальная ставка
     */
    public void setHurdleRate(BigDecimal hurdleRate) {
        this.hurdleRate = hurdleRate;
    }

    /**
     * Возвращает маркап.
     * @return маркап
     */
    public BigDecimal getMarkup() {
        return markup;
    }

    /**
     * Устанавливает маркап.
     * @param markup маркап
     */
    public void setMarkup(BigDecimal markup) {
        this.markup = markup;
    }

    /**
     * Возвращает PCs кеш.
     * @return PCs кеш
     */
    public BigDecimal getPcCash() {
        return pcCash;
    }

    /**
     * Устанавливает PCs кеш.
     * @param pcCash PCs кеш
     */
    public void setPcCash(BigDecimal pcCash) {
        this.pcCash = pcCash;
    }

    /**
     * Возвращает PCs резервы.
     * @return PCs резервы
     */
    public BigDecimal getPcReserve() {
        return pcReserve;
    }

    /**
     * Устанавливает PCs резервы.
     * @param pcReserve PCs резервы
     */
    public void setPcReserve(BigDecimal pcReserve) {
        this.pcReserve = pcReserve;
    }

    /**
     * Возвращает PCs деривативы.
     * @return PCs деривативы
     */
    public BigDecimal getPcDerivative() {
        return pcDerivative;
    }

    /**
     * Устанавливает PCs деривативы.
     * @param pcDerivative PCs деривативы
     */
    public void setPcDerivative(BigDecimal pcDerivative) {
        this.pcDerivative = pcDerivative;
    }

    /**
     * Возвращает PCs всего.
     * @return PCs всего
     */
    public BigDecimal getPcTotal() {
        return pcTotal;
    }

    /**
     * Устанавливает PCs всего.
     * @param pcTotal PCs всего
     */
    public void setPcTotal(BigDecimal pcTotal) {
        this.pcTotal = pcTotal;
    }

    /**
     * Возвращает выбранный объём линии (млн. дол. США).
     * @return выбранный объём линии (млн. дол. США)
     */
    public BigDecimal getSelectedLineVolume() {
        return selectedLineVolume;
    }

    /**
     * Устанавливает выбранный объём линии (млн. дол. США).
     * @param selectedLineVolume выбранный объём линии (млн. дол. США)
     */
    public void setSelectedLineVolume(BigDecimal selectedLineVolume) {
        this.selectedLineVolume = selectedLineVolume;
    }

    /**
     * Возвращает публичная сделка.
     * @return публичная сделка
     */
    public boolean isPublicDeal() {
        return publicDeal;
    }

    /**
     * Устанавливает публичная сделка.
     * @param publicDeal публичная сделка
     */
    public void setPublicDeal(boolean publicDeal) {
        this.publicDeal = publicDeal;
    }

    /**
     * Возвращает приоритет менеджмента.
     * @return приоритет менеджмента
     */
    public boolean isManagementPriority() {
        return managementPriority;
    }

    /**
     * Устанавливает приоритет менеджмента.
     * @param managementPriority приоритет менеджмента
     */
    public void setManagementPriority(boolean managementPriority) {
        this.managementPriority = managementPriority;
    }

    /**
     * Возвращает новый клиент.
     * @return новый клиент
     */
    public boolean isNewClient() {
        return newClient;
    }

    /**
     * Устанавливает новый клиент.
     * @param newClient новый клиент
     */
    public void setNewClient(boolean newClient) {
        this.newClient = newClient;
    }

    /**
     * Возвращает сделка Flow / Investment.
     * @return сделка Flow / Investment
     */
    public String getFlowInvestment() {
        return flowInvestment;
    }

    /**
     * Устанавливает сделка Flow / Investment.
     * @param flowInvestment сделка Flow / Investment
     */
    public void setFlowInvestment(String flowInvestment) {
        this.flowInvestment = flowInvestment;
    }

    /**
     * Возвращает коэффициент типа сделки.
     * @return коэффициент типа сделки
     */
    public BigDecimal getProductTypeFactor() {
        return productTypeFactor;
    }

    /**
     * Устанавливает коэффициент типа сделки.
     * @param productTypeFactor коэффициент типа сделки
     */
    public void setProductTypeFactor(BigDecimal productTypeFactor) {
        this.productTypeFactor = productTypeFactor;
    }

    /**
     * Возвращает коэффициент по сроку погашения.
     * @return коэффициент по сроку погашения
     */
    public BigDecimal getPeriodFactor() {
        return periodFactor;
    }

    /**
     * Устанавливает коэффициент по сроку погашения.
     * @param periodFactor коэффициент по сроку погашения 
     */
    public void setPeriodFactor(BigDecimal periodFactor) {
        this.periodFactor = periodFactor;
    }

    /**
     * Возвращает фондирующая компания.
     * @return фондирующая компания
     */
    public String getFundCompany() {
        return fundCompany;
    }

    /**
     * Устанавливает фондирующая компания.
     * @param fundCompany фондирующая компания
     */
    public void setFundCompany(String fundCompany) {
        this.fundCompany = fundCompany;
    }

    /**
     * Возвращает контрагент со стороны группы ВТБ.
     * @return контрагент со стороны группы ВТБ
     */
    public String getVtbContractor() {
        return vtbContractor;
    }

    /**
     * Устанавливает контрагент со стороны группы ВТБ.
     * @param vtbContractor контрагент со стороны группы ВТБ
     */
    public void setVtbContractor(String vtbContractor) {
        this.vtbContractor = vtbContractor;
    }

    /**
     * Возвращает трейдинг деск.
     * @return трейдинг деск
     */
    public String getTradingDesk() {
        return tradingDesk;
    }

    /**
     * Устанавливает трейдинг деск.
     * @param tradingDesk трейдинг деск
     */
    public void setTradingDesk(String tradingDesk) {
        this.tradingDesk = tradingDesk;
    }

    /**
     * Возвращает пролонгация.
     * @return пролонгация
     */
    public boolean isProlongation() {
        return prolongation;
    }

    /**
     * Устанавливает пролонгация.
     * @param prolongation пролонгация
     */
    public void setProlongation(boolean prolongation) {
        this.prolongation = prolongation;
    }

    /**
     * Возвращает не показывать в отчете.
     * @return не показывать в отчете
     */
    public boolean isHideInReport() {
        return hideInReport;
    }

    /**
     * Устанавливает не показывать в отчете.
     * @param hideInReport не показывать в отчете
     */
    public void setHideInReport(boolean hideInReport) {
        this.hideInReport = hideInReport;
    }
    
    /**
     * Возвращает приоритет менеджера.
     * @return приоритет менеджера
     */
    public boolean isManagerPriority() {
        return managerPriority;
    }

    /**
     * Устанавливает приоритет менеджера.
     * @param managerPriority приоритет менеджера
     */
    public void setManagerPriority(boolean managerPriority) {
        this.managerPriority = managerPriority;
    }
    
    /**
     * Возвращает продуктовый менеджер.
     * @return продуктовый менеджер
     */
    public User getProductManager() {
        return productManager;
    }

    /**
     * Устанавливает продуктовый менеджер.
     * @param productManager продуктовый менеджер
     */
    public void setProductManager(User productManager) {
        this.productManager = productManager;
    }
    
    /**
     * Возвращает кредитный аналитик.
     * @return кредитный аналитик
     */
    public User getCreditAnalyst() {
        return creditAnalyst;
    }

    /**
     * Устанавливает кредитный аналитик.
     * @param creditAnalyst кредитный аналитик
     */
    public void setCreditAnalyst(User creditAnalyst) {
        this.creditAnalyst = creditAnalyst;
    }
    
    /**
     * Возвращает структуратор.
     * @return структуратор
     */
    public User getStructureInspector() {
        return structureInspector;
    }

    /**
     * Устанавливает структуратор.
     * @param structureInspector структуратор
     */
    public void setStructureInspector(User structureInspector) {
        this.structureInspector = structureInspector;
    }
    
    /**
     * Возвращает клиентский менеджер.
     * @return клиентский менеджер
     */
    public User getClientManager() {
        return clientManager;
    }

    /**
     * Устанавливает клиентский менеджер.
     * @param clientManager клиентский менеджер
     */
    public void setClientManager(User clientManager) {
        this.clientManager = clientManager;
    }
    
    /**
     * Возвращает список предметов залога залогодателей.
     * @return список предметов залога залогодателей
     */
    public List<CommonDictionary<String>> getEnsurings() {
        return ensurings;
    }

    /**
     * Устанавливает список предметов залога залогодателей.
     * @param ensurings список предметов залога залогодателей
     */
    public void setEnsurings(List<CommonDictionary<String>> ensurings) {
        this.ensurings = ensurings;
    }

    /**
     * Возвращает количество недель.
     * @return количество недель
     */
    public Long getWeeksNumber() {
        if (mdTask == null || mdTask.getCreationDate() == null)
            return null;
        
        return BigDecimal.valueOf((new Date()).getTime() - mdTask.getCreationDate().getTime())
                         .divide(BigDecimal.valueOf(DateUtils.MILLIS_PER_DAY), MathContext.DECIMAL32)
                         .divide(BigDecimal.valueOf(7), 0, RoundingMode.HALF_UP)
                         .longValue();
    }
    
    /**
     * Возвращает кредитная линия (млн. дол. США).
     * @return кредитная линия (млн. дол. США)
     */
    public BigDecimal getLineVolume() {
        if (mdTask == null
                || mdTask.getMdTaskSumCalculated() == null
                || StringUtils.isEmpty(mdTask.getProcessname())
                || mdTask.getCurrentCurrencyRate() == null
                || mdTask.getUsdCurrencyRate() == null)
            return null;
        
        return mdTask.getMdTaskSumCalculated()
                .multiply(mdTask.getCurrentCurrencyRate())
                .divide(mdTask.getUsdCurrencyRate(), MathContext.DECIMAL128)
                .divide(BigDecimal.TEN.pow(6));
    }
    
    /**
     * Возвращает объем линии, доступной для выборки (млн. дол. США).
     * @return объем линии, доступной для выборки (млн. дол. США)
     */
    public BigDecimal getAvailibleLineVolume() {
        BigDecimal lineVolume;
        if (selectedLineVolume == null || mdTask == null
                || (lineVolume = mdTask.getMdTaskSumCalculated()) == null)
            return null;
        
        return lineVolume.subtract(selectedLineVolume);
    }
    
    /**
     * Возвращает всего ожидаемых (млн. дол. США).
     * @return всего ожидаемых (млн. дол. США)
     */
    public BigDecimal getExpectedValue() {
        if (pcTotal == null || closeProbability == null)
            return null;
        
        return pcTotal.multiply(closeProbability).divide(BigDecimal.valueOf(100));
    }
    
    /**
     * Возвращает маркап для сделки.
     * @return маркап для сделки
     */
    public BigDecimal getCreditDealMarkup() {
        if (mdTask == null
                || !mdTask.isProduct())
            return null;
        
        BigDecimal loanRate = BigDecimal.ZERO;
        
        if (mdTask.getInterestRates() != null
                && mdTask.getInterestRates().size() > 0
                && mdTask.getInterestRates().get(0) != null
                && mdTask.getInterestRates().get(0).getLoanRate() != null)
            loanRate = mdTask.getInterestRates().get(0).getLoanRate();

        BigDecimal arrangementFee = mdTask.getArrangementFee() != null ? mdTask.getArrangementFee(): BigDecimal.ZERO;
        BigDecimal supportComission = mdTask.getSupportComission() != null ? mdTask.getSupportComission() : BigDecimal.ZERO;
        BigDecimal hurdleRate = this.hurdleRate != null ? this.hurdleRate : BigDecimal.ZERO;
        
        return loanRate.add(arrangementFee).add(supportComission).subtract(hurdleRate);
    }
    /**
     * Возвращает маржа.
     * @return маржа
     */
    public BigDecimal getMargin() {
        return margin;
    }

    /**
     * Устанавливает маржа.
     * @param margin маржа
     */
    public void setMargin(BigDecimal margin) {
        this.margin = margin;
    }

    /**
     * Returns .
     * @return
     */
    public Integer getTradeFinance() {
        return tradeFinance;
    }

    /**
     * Sets .
     * @param tradeFinance
     */
    public void setTradeFinance(Integer tradeFinance) {
        this.tradeFinance = tradeFinance;
    }

    /**
     * Returns .
     * @return
     */
    public String getTradeFinanceName() {
        return tradeFinanceName;
    }

    /**
     * Sets .
     * @param tradeFinanceName
     */
    public void setTradeFinanceName(String tradeFinanceName) {
        this.tradeFinanceName = tradeFinanceName;
    }

    /**
     * Returns .
     * @return
     */
    public boolean isHideInReportTraders() {
        return hideInReportTraders;
    }

    /**
     * Sets .
     * @param hideInReportTraders
     */
    public void setHideInReportTraders(boolean hideInReportTraders) {
        this.hideInReportTraders = hideInReportTraders;
    }

    /**
     * Returns .
     * @return
     */
    public boolean isStatusManual() {
        return statusManual;
    }

    /**
     * Sets .
     * @param statusManual
     */
    public void setStatusManual(boolean statusManual) {
        this.statusManual = statusManual;
    }
}
