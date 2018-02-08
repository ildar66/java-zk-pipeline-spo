package ru.masterdm.spo.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.md.domain.Department;
import ru.md.domain.DepartmentExt;
import ru.md.domain.IndRate;
import ru.md.domain.Org;
import ru.md.domain.TaskKz;
import ru.md.domain.User;
import ru.md.domain.dashboard.DashboardEvent;
import ru.md.domain.MdTask;
import ru.md.domain.SpoSumHistory;
import ru.md.domain.TaskTiming;
import ru.md.domain.dashboard.DetailReportRow;
import ru.md.domain.dashboard.MainReportRow;
import ru.md.domain.dashboard.PipelineTradingDesk;
import ru.md.domain.dashboard.Sum;
import ru.md.domain.dashboard.TaskListParam;
import ru.md.domain.dashboard.TaskTypeStatus;
import ru.md.domain.dashboard.SpoClientReport;
import ru.md.domain.dashboard.TopReportRow;
import ru.md.persistence.CompendiumMapper;
import ru.md.persistence.DashboardMapper;
import ru.md.persistence.DepartmentMapper;
import ru.md.persistence.MdTaskMapper;
import ru.md.persistence.UserMapper;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import ru.masterdm.integration.ServiceFactory;
import ru.masterdm.integration.rating.RatingService;
import ru.masterdm.integration.rating.ws.CalcHistoryInput;
import ru.masterdm.integration.rating.ws.CalcHistoryOutput;
import ru.masterdm.spo.list.EDashStatus;
import ru.masterdm.spo.utils.Formatter;

/**
 * Сервис для сохранения историчного всего.
 * Created by Andrey Pavlenko on 11.08.16.
 */
@Service
public class DashboardService implements IDashboardService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardService.class);
    public static final int TOP_REPORT_LIMIT = 10;
    @Autowired
    private MdTaskMapper mdTaskMapper;
    @Autowired
    private DashboardMapper dashboardMapper;
    @Autowired
    private CompendiumMapper compendiumMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DepartmentMapper departmentMapper;
    @Autowired
    private IPriceService priceService;

    @Override
    public void logTask(Long idMdtask) {
        //определить какое сейчас состояние
        DashboardEvent status = getCurrentStatus(idMdtask);
        if (status.getStatus() == null)
            return;
        //записать состояние
        MdTask mdTask = mdTaskMapper.getPipelineWithinMdTask(idMdtask);
        SpoSumHistory h = toSpoSumHistory(mdTask);
        h.setIdStatus(status.getStatus().getId());
        h.setStatusDate(status.getEventDate());
        h.setSaveDate(new Date());
        dashboardMapper.updateSpoSumHistory(h);
    }

    @Override
    public void recalculateOldTasks() {
        LOGGER.info("recalculateOldTasks() start");
        //по договоренности для старых сделок пишет актуальное состояние как историческое
        recalculateOldTasksCreate();//новые все типы заявок
        recalculateOldTasksLost();//Потерянные все типы заявок
        for (DashboardEvent event : dashboardMapper.getOldProduct2fix())//сделки Заключенные сделки
            fixEvent(event, EDashStatus.PRODUCT_FIX.getId());
        for (DashboardEvent event : dashboardMapper.getOldProduct2trance())//сделки Выданные ден. средства
            fixEvent(event, EDashStatus.PRODUCT_TRANCE.getId());
        //лимиты - Анализ и структурирование; Проведение экспертиз;  Одобрено
        for (DashboardEvent event : dashboardMapper.getOldLimitStruct())
            fixEvent(event, EDashStatus.LIMIT_INPROGRESS.getId());
        /*for (DashboardEvent event : dashboardMapper.getOldLimitExper())
            fixEvent(event, EDashStatus.LIMIT_EXPER.getId());*/
        for (DashboardEvent event : dashboardMapper.getOldLimitAccept())
            fixEvent(event, EDashStatus.LIMIT_ACCEPT.getId());
        //Изменения сделок и кросс-селл - «Заключенные сделки» - значение поля «Стадии сделки» в «Секции ПМ» равно «КОД подписана».
        //я не могу для старых сделок узнать дату когда она была заключена. Так что это не пишем
        dashboardMapper.fixClientRefused();
    }

    @Override
    public DashboardEvent getCurrentStatus(Long idMdtask) {
        MdTask mdTask = mdTaskMapper.getById(idMdtask);
        TaskTiming timing = dashboardMapper.getTaskTiming(idMdtask);
        if (timing == null)
            return new DashboardEvent();
        if (mdTask.isProduct())
            if (mdTask.getVersion() != null && mdTask.getVersion() > 1) {//изменения
                mdTask = mdTaskMapper.getPipelineWithinMdTask(idMdtask);
                if (timing.getRefuseDate() != null)
                    return new DashboardEvent(idMdtask, timing.getRefuseDate(), EDashStatus.WAIVER_LOST);
                if (mdTask.getPipeline() != null && mdTask.getPipeline().getStatus() != null
                        && mdTask.getPipeline().getStatus().equalsIgnoreCase("КОД подписана"))
                    return new DashboardEvent(idMdtask, new Date(), EDashStatus.WAIVER_FIX);
                if (timing.getCreateDate() != null)
                    return new DashboardEvent(idMdtask, timing.getCreateDate(), EDashStatus.WAIVER_NEW);
            } else {//сама сделка
                if (timing.getRefuseDate() != null)
                    return new DashboardEvent(idMdtask, timing.getRefuseDate(), EDashStatus.PRODUCT_LOST);
                if (timing.getTranche() != null)
                    return new DashboardEvent(idMdtask, timing.getTranche(), EDashStatus.PRODUCT_TRANCE);
                if (timing.getFixDate() != null)
                    return new DashboardEvent(idMdtask, timing.getFixDate(), EDashStatus.PRODUCT_FIX);
                if (timing.getCreateDate() != null)
                    return new DashboardEvent(idMdtask, timing.getCreateDate(), EDashStatus.PRODUCT_NEW);
            }
        if (mdTask.isLimit()) {
            if (timing.getRefuseDate() != null)
                return new DashboardEvent(idMdtask, timing.getRefuseDate(), EDashStatus.LIMIT_LOST);
            if (timing.getAcceptDate() != null)
                return new DashboardEvent(idMdtask, timing.getAcceptDate(), EDashStatus.LIMIT_ACCEPT);
            if (timing.getExper() != null || timing.getStruct() != null)
                return new DashboardEvent(idMdtask, min(timing.getStruct(),timing.getExper()), EDashStatus.LIMIT_INPROGRESS);
            if (timing.getCreateDate() != null)
                return new DashboardEvent(idMdtask, timing.getCreateDate(), EDashStatus.LIMIT_NEW);
        }
        if (mdTask.isCrossSell()) {
            mdTask = mdTaskMapper.getPipelineWithinMdTask(idMdtask);
            if (timing.getRefuseDate() != null)
                return new DashboardEvent(idMdtask, timing.getRefuseDate(), EDashStatus.CROSS_SELL_LOST);
            if (mdTask.getPipeline() != null && mdTask.getPipeline().getStatus() != null
                    //кросс-селл - «Заключенные сделки» - значение поля «Стадии сделки» в «Секции ПМ» равно «КОД подписана».
                    && mdTask.getPipeline().getStatus().equalsIgnoreCase("КОД подписана"))
                return new DashboardEvent(idMdtask, new Date(), EDashStatus.CROSS_SELL_ACCEPT);
            if (timing.getCreateDate() != null)
                return new DashboardEvent(idMdtask, timing.getCreateDate(), EDashStatus.CROSS_NEW);
        }
        return new DashboardEvent();
    }
    private Date min(Date d1, Date d2){
        if(d2==null)
            return d1;
        if(d1==null)
            return d2;
        return d1.after(d2)?d2:d1;
    }

    @Override
    public List<MainReportRow> getMainReport(Date startDate, Date endDate, String taskType, Integer creditDocumentary,
                                             Set<PipelineTradingDesk> tradingDesks,
                                             boolean tradingDeskOthers,
                                             Collection<? extends Department> departments) {
        if (startDate == null)
            startDate = new Date();
        if (endDate == null)
            endDate = new Date();

        return dashboardMapper.getMainReport(startDate, endDate, taskType, creditDocumentary, tradingDesks, tradingDeskOthers, departments);
    }

    @Override
    public List<DetailReportRow> getDetailReport(Date startDate, Date endDate, String taskType, Integer creditDocumentary,
                                                 Set<PipelineTradingDesk> tradingDesks,
                                                 boolean tradingDeskOthers,
                                                 Collection<? extends Department> departments,
                                                 Integer idStatus, String branch) {
        if (startDate == null)
            startDate = new Date();
        if (endDate == null)
            endDate = new Date();

        return dashboardMapper
                .getDetailReport(startDate, endDate, taskType, creditDocumentary, tradingDesks, tradingDeskOthers, departments, idStatus, branch);
    }

    @Override
    public List<TopReportRow> getTopReport(Date startDate, Date endDate, String taskType, Integer creditDocumentary,
                                           Set<PipelineTradingDesk> tradingDesks,
                                           boolean tradingDeskOthers,
                                           Collection<? extends Department> departments) {
        if (startDate == null)
            startDate = new Date();
        if (endDate == null)
            endDate = new Date();

        return dashboardMapper.getTopReport(startDate, endDate, taskType, creditDocumentary, tradingDesks, tradingDeskOthers, departments,
                                            TOP_REPORT_LIMIT);
    }

    @Override
    public List<TaskTypeStatus> getTaskTypeStatusesInOrder(String taskType) {
        if (taskType == null)
            taskType = "product";
        return dashboardMapper.getTaskTypeStatuses(taskType);
    }

    @Override
    public List<PipelineTradingDesk> getPipelineTradingDesk() {
        return dashboardMapper.getPipelineTradingDesk();
    }

    @Override
    public void clearOldClientReport() {
        dashboardMapper.clearOldClientReport();
    }

    private SpoClientReport loadSpoClientReport(Long idMdtask, Date reportDate) {
        LOGGER.info("idMdtask=" + idMdtask);
        DashboardEvent status = getCurrentStatus(idMdtask);
        //записать состояние
        MdTask mdTask = mdTaskMapper.getPipelineWithinMdTask(idMdtask);
        MdTask m = mdTaskMapper.getById(idMdtask);
        SpoSumHistory h = toSpoSumHistory(mdTask);
        SpoClientReport report = new SpoClientReport();
        report.setIdMdtask(idMdtask);
        report.setSaveDate(reportDate);
        report.setMdtaskNumber(mdTask.getMdtaskNumber().toString());
        report.setVersion(mdTask.getVersion().toString());
        report.setCategory(mdTask.getType());
        if (status!=null && status.getStatus()!=null)
            report.setState(status.getStatus().getName());
        report.setTaskType(mdTask.getType());
        report.setTaskSum(h.getSum());
        report.setCurrency(h.getCurrency());
        if (h.getCurrency().equalsIgnoreCase("rur"))
            report.setSumRub(h.getSum());
        if (h.getCurrency().equalsIgnoreCase("usd"))
            report.setSumUsd(h.getSum());
        report.setPeriodMonth(h.getPeriodMonth());
        report.setMargin(Formatter.format(h.getMargin()));
        report.setProfit(h.getProfit());
        report.setWeeks(h.getWeeks());
        report.setCreateDate(mdTask.getCreationDate());
        report.setProposedDtSigning(h.getProposedDtSigning());
        report.setPlanDate(h.getPlanDate());
        report.setUpdateDate(mdTaskMapper.getLastUpdateDate(idMdtask));
        for (TaskKz tkz : compendiumMapper.getTaskKzByMdtask(idMdtask))
            if (tkz.isMainOrg()) {
                report.setIdKz(tkz.getKzid());
                Org kz = compendiumMapper.getOrgById(tkz.getKzid());
                report.setIdOrg(kz.getIdUnitedClient());
                report.setIndustry(kz.getIndustry());
                report.setKzName(kz.getName());
                if (!Formatter.str(kz.getIdUnitedClient()).isEmpty()) {
                    Org ek = compendiumMapper.getEkById(kz.getIdUnitedClient());
                    report.setIdGroup(compendiumMapper.getEkGroupId(report.getIdOrg()));
                    report.setGroupName(ek.getGroupname());
                    report.setMainOrg(ek.getName());
                }
            }
        //            report.setSupplyOrg();
        report.setStatus(mdTask.getPipeline().getStatus());
        report.setCloseProbability(h.getCloseProbability());
        report.setProductName(m.getProductName());
        report.setSupply(mdTask.getPipeline().getSupply());
        //            report.setEnsurings();
        //            report.setTargets();
        report.setDescription(mdTask.getPipeline().getDescription());
        report.setCmnt(h.getCmnt());
        report.setAdditionBusiness(mdTask.getPipeline().getAdditionalBusiness());
        report.setUsePeriodMonth(h.getUsePeriodMonth());
        if (h.getWal() != null)
            report.setWal(h.getWal().longValue());
        //            report.setFixedFloat();
        //            report.setBaseRate();
        //            report.setFixrate();
        //            report.setLoanRate();
        //            report.setComission();
        report.setPcDer(mdTask.getPipeline().getPcDerivative());
        report.setPcTotal(mdTask.getPipeline().getPcTotal());
        report.setLineCount(h.getLineCount());
        report.setAvailibleLineVolume(h.getAvailibleLineVolume());
        report.setPub(h.getPub());
        report.setPriority(mdTask.getPipeline().isManagementPriority());
        report.setNewClient(mdTask.getPipeline().isNewClient());
        report.setFlowInvestment(mdTask.getPipeline().getFlowInvestment());
        report.setProductManager(listJoin(userMapper.getProjectTeamAssignedAs(idMdtask, "Продуктовый менеджер")));
        report.setAnalyst(listJoin(userMapper.getProjectTeamAssignedAs(idMdtask, "Кредитный аналитик")));
        report.setClientManager(listJoin(userMapper.getProjectTeamAssignedAs(idMdtask, "Клиентский менеджер")));
        report.setStructurator(listJoin(userMapper.getProjectTeamAssignedAs(idMdtask, "Структуратор")));
        report.setGss("");//FIXME
        report.setContractor(mdTask.getPipeline().getFundCompany());
        report.setVtbContractor(mdTask.getPipeline().getVtbContractor());
        report.setTradeDesc(h.getTradeDesc());
        report.setProlongation(h.getProlongation());
        report.setProjectName(m.getProjectName());
        return report;
    }

    @Override
    public void generatePipelineClientReport() {
        Date reportDate = DateUtils.truncate((new DateTime(new Date()).minusDays(1)).toDate(), Calendar.DATE);
        for (Long idMdtask : dashboardMapper.getYesterdayTask(reportDate))
            dashboardMapper.insertSpoClientReport(loadSpoClientReport(idMdtask, reportDate));
    }

    private void fixEvent(DashboardEvent event, Long idStatus) {
        if (event.getEventDate() == null)
            return;
        MdTask mdTask = mdTaskMapper.getPipelineWithinMdTask(event.getIdMdtask());
        SpoSumHistory h = toSpoSumHistory(mdTask);
        h.setIdStatus(idStatus);
        h.setStatusDate(event.getEventDate());
        h.setSaveDate(h.getStatusDate());
        dashboardMapper.updateSpoSumHistory(h);
    }

    private void recalculateOldTasksLost() {
        for (Long idMdtask : dashboardMapper.getAllLostTask()) {
            MdTask mdTask = mdTaskMapper.getPipelineWithinMdTask(idMdtask);
            SpoSumHistory h = toSpoSumHistory(mdTask);
            //посчитать статус (тип заявки) и даты статуса
            if (mdTask.isLimit())
                h.setIdStatus(EDashStatus.LIMIT_LOST.getId());
            if (mdTask.isCrossSell())
                h.setIdStatus(EDashStatus.CROSS_SELL_LOST.getId());
            if (mdTask.isProduct() && mdTask.getVersion().equals(1L))
                h.setIdStatus(EDashStatus.PRODUCT_LOST.getId());
            if (mdTask.isProduct() && mdTask.getVersion().longValue() > 1)
                h.setIdStatus(EDashStatus.WAIVER_LOST.getId());
            h.setStatusDate(dashboardMapper.getLostDate(idMdtask));
            h.setSaveDate(h.getStatusDate());
            //вмержить в базу историю
            if (h.getStatusDate() != null && h.getIdStatus() != null)
                dashboardMapper.updateSpoSumHistory(h);
        }
    }

    private void recalculateOldTasksCreate() {
        for (DashboardEvent event : dashboardMapper.getAllNewTask()) {
            MdTask mdTask = mdTaskMapper.getPipelineWithinMdTask(event.getIdMdtask());
            SpoSumHistory h = toSpoSumHistory(mdTask);
            //посчитать статус (тип заявки) и даты статуса
            h.setIdStatus(EDashStatus.PRODUCT_NEW.getId());//default
            if (mdTask.isLimit())
                h.setIdStatus(EDashStatus.LIMIT_NEW.getId());
            if (mdTask.isCrossSell())
                h.setIdStatus(EDashStatus.CROSS_NEW.getId());
            if (mdTask.isProduct() && mdTask.getVersion().equals(1L))
                h.setIdStatus(EDashStatus.PRODUCT_NEW.getId());
            if (mdTask.isProduct() && mdTask.getVersion().longValue() > 1)
                h.setIdStatus(EDashStatus.WAIVER_NEW.getId());
            h.setStatusDate(event.getEventDate());
            h.setSaveDate(h.getStatusDate());
            //вмержить в базу историю
            if (mdTask.getCreationDate() != null && h.getIdStatus() != null)
                dashboardMapper.updateSpoSumHistory(h);
        }
    }

    private SpoSumHistory toSpoSumHistory(MdTask mdTask) {
        MathContext mc = new MathContext(2, RoundingMode.HALF_UP);
        SpoSumHistory res = new SpoSumHistory();
        res.setSaveDate(new Date());
        res.setSum(mdTask.getMdtaskSum());
        res.setCurrency(mdTask.getCurrency());
        res.setIdMdtask(mdTask.getIdMdtask());
        res.setContractor(mdTask.getPipeline().getFundCompany());
        res.setVtbContractor(mdTask.getPipeline().getVtbContractor());
        res.setWal(mdTask.getPipeline().getWal());
        res.setPlanDate(mdTask.getPipeline().getPlanDate());
        res.setTradeDesc(mdTask.getPipeline().getTradingDesk());
        res.setCloseProbability(mdTask.getPipeline().getCloseProbability());
        res.setProductName(mdTask.getProductName());
        if (!Formatter.str(mdTask.getCrossSellName()).isEmpty())
            res.setProductName(mdTask.getCrossSellName());
        res.setStatusPipeline(mdTask.getPipeline().getStatus());
        res.setAvailibleLineVolume(mdTask.getPipeline().getAvailibleLineVolume());
        res.setProposedDtSigning(mdTask.getProposedDtSigning());
        res.setMargin(mdTask.getPipeline().getMargin());
        if (res.getSum() != null && mdTask.getPipeline().getCloseProbability() != null)
            res.setSumProbability(mdTask.getPipeline().getCloseProbability().multiply(res.getSum()));
        res.setPeriodMonth(mdTask.getMaturityInMonth());
        if (res.getMargin() != null && res.getSum() != null && res.getPeriodMonth() != null && res.getPeriodMonth().longValue() > 0)
            res.setProfit(res.getMargin().multiply(res.getSum()).multiply(BigDecimal.valueOf(0.01)).
                    multiply(BigDecimal.valueOf(res.getPeriodMonth()).divide(BigDecimal.valueOf(12.0), mc), mc));
        res.setLineCount(mdTask.getPipeline().getSelectedLineVolume());
        res.setWeeks(mdTask.getPipeline().getWeeksNumber());
        res.setUsePeriodMonth(mdTask.getDrawdownDateInMonthCalculated());
        res.setPub(mdTask.getPipeline().isPublicDeal());
        res.setProlongation(mdTask.getPipeline().isProlongation());
        res.setCmnt(mdTask.getPipeline().getNote());
        res.setSumLast(res.getSumProbability());//'Оставшаяся сумма к выдаче с учетом вероятности'
        res.setIndRates(new ArrayList<IndRate>());
        if (mdTask.getInterestRates().size() > 0) {
            res.setLoanRate(mdTask.getInterestRates().get(0).getFundingRate());//'Ставка фондирования, %'
            res.setRate(mdTask.getInterestRates().get(0).getLoanRate());//% ставка
            List<IndRate> indRates = mdTaskMapper.getIndRatesByMdtask(mdTask.getIdMdtask());
            if (mdTask.getInterestRates().size() > 1) {//нам нужны данные по первому периоду
                //фиксированная и плавающая
                res.setInterestRateFixed(mdTask.getInterestRates().get(0).isInterestRateFixed());
                res.setInterestRateDerivative(mdTask.getInterestRates().get(0).isInterestRateDerivative());
                //индикативные
                Long firstPeriodId = getFirstPeriodId(indRates);
                if (firstPeriodId != null)
                    for(IndRate i : indRates)
                        if (i.getIdFactpercent() != null && i.getIdFactpercent().equals(firstPeriodId))
                            res.getIndRates().add(i);
            } else {//нам нужны данные по сделке в целом
                //фиксированная и плавающая
                res.setInterestRateFixed(mdTask.isInterestRateFixed());
                res.setInterestRateDerivative(mdTask.isInterestRateDerivative());
                //индикативные
                for(IndRate i : indRates)
                    if (i.getIdFactpercent()==null)
                        res.getIndRates().add(i);
            }
        }
        if (mdTask.getMainOrganization() != null) {
            res.setGroupname(mdTask.getMainOrganization().getGroupname());
            res.setOrgname(mdTask.getMainOrganization().getName());
            res.setBranch(mdTask.getMainOrganization().getBranch());
        }
        res.setComission(priceService.getComissionZaVidSum(mdTask.getIdMdtask()));
        res.setCreditDocumentary(mdTask.getCreditDocumentary());
        res.setInitdepartment(mdTask.getInitdep());
        return res;
    }

    private Long getFirstPeriodId(List<IndRate> indRates){
        Long res = null;
        for(IndRate i : indRates)
            if(i.getIdFactpercent()!= null && (res == null || i.getIdFactpercent() < res) )
                res = i.getIdFactpercent();
        return res;
    }

    /**
     * Строки в списке выводит через запятую
     */
    public static String listJoin(List<String> list) {
        if (list == null)
            return "";
        String[] arr = list.toArray(new String[list.size()]);
        return StringUtils.join(arr, ", ");
    }

    @Override
    public List<DepartmentExt> getDepartmentsExtForTree(User user) {
        List<DepartmentExt> l;
        //LOGGER.info("Get departments for {}", user);
        // для пользователя с такими ролями доступно всё дерево
        if (user == null || userMapper.isUserInRoleName(user.getId(), "Аудитор ДКАБ", "Администратор системы")) {
            l = departmentMapper.getDepartmentsExtForTree(null);
        } else {
            l = departmentMapper.getDepartmentsExtForTree(user.getIdDepartment());
        }
        return l;
    }

    @Override
    public String getRurSumFormated(Double res) {
        if (res >= 100000000.0)
            return Formatter.format1point(res / 1000000000) + " млрд. руб.";
        if (res >= 100000.0)
            return Formatter.format1point(res / 1000000) + " млн. руб.";
        return Formatter.format1point(res) + " руб.";
    }

    @Override
    public String getNativeCurrencySumFormated(Double res) {
    	if (res == null)
    		return "";
        return Formatter.format3point(res/1000000);
    	//return String.valueOf(res/1000000);
    }

    @Override
    public Double toRur(Sum s, Date to) {
        BigDecimal rate = compendiumMapper.getCurrencyRate4Date(s.getCurrency(), to);
        if (s.getCurrency() == null || s.getCurrency().equalsIgnoreCase("rur"))
            return s.getValue();
        if (rate != null && s.getValue()!=null)
            return s.getValue() * rate.doubleValue();
        return 0.0;
    }

    @Override
    public String getTitleTypeByCode(String code){
        if (code == null) return "";
        if (code.equals("product")) return "Сделки";
        if (code.equals("limit")) return "Лимиты";
        if (code.equals("waiver")) return "Измененные и вейверы";
        if (code.equals("cross-sell")) return "Кросс селлы";
        return "";
    }

    private String getCreditDocumentaryPrefix(Long creditDocumentary) {
    	if (creditDocumentary == null)
    		return "";
        if (creditDocumentary.equals(1L)) return " (кредитные) ";
        if (creditDocumentary.equals(2L)) return " (документарные) ";
        return "";
    }

    private String getCreditDocumentaryFilePrefix(Long creditDocumentary) {
        String result = "(CRED/DOC)_";
        if (creditDocumentary == null)
            return result;
        if (creditDocumentary.equals(1L)) return "(CRED)_";
        if (creditDocumentary.equals(2L)) return "(DOC)_";
        return result;
    }

    private static List<Integer> exclude(List<Integer> list, Long exclude) {
        List<Integer> clone = new ArrayList<Integer>(list.size());
        for (Integer item : list)
            if(item.longValue() != exclude.longValue())
                clone.add(item);
        return clone;
    }
    private String getTitleSum(List<Integer> statusids, Date from, Date to, Long creditDocumentary, Long[] departments,
                               Long[] tradingDeskSelected, Boolean isTradingDeskOthers){
        LOGGER.info("statusids = "+statusids);
        TaskListParam param = new TaskListParam();
        param.creditDocumentary = creditDocumentary;
        param.departmentsIds = departments;
        param.tradingDeskSelectedIds = tradingDeskSelected;
        param.isTradingDeskOthers = isTradingDeskOthers?"true":"false";
        List<Sum> sum = new ArrayList<Sum>();
        if (statusids.contains(EDashStatus.PRODUCT_TRANCE.getId().intValue()))
            sum.addAll(dashboardMapper.getTaskListCedSum(EDashStatus.PRODUCT_TRANCE.getId(), from,  to, param));
        List<Integer> notCed = exclude(statusids, EDashStatus.PRODUCT_TRANCE.getId());
        if (!notCed.isEmpty())
            sum.addAll(dashboardMapper.getTaskListSum(notCed, from,  to, param));
        Double res = 0.0;
        for (Sum s : sum)
            res += toRur(s, to);
        return  getRurSumFormated(res);
    }
    @Override
    public List<Integer> getStatusidsByTaskType(String taskType){
        ArrayList<Integer> statusids = new ArrayList<Integer>();
        for (TaskTypeStatus s : dashboardMapper.getTaskTypeStatuses(taskType))
            statusids.add(s.getIdStatus());
        return statusids;
    }

    @Override
    public String getTitle(Long statusid, String taskType, Date from, Date to, Long creditDocumentary, Long[] departments,
                           Long[] tradingDeskSelected, Boolean isTradingDeskOthers,Boolean mainPeriod, String industry) {
        List<Integer> statusids = new ArrayList<Integer>();
        if (statusid == null)
            statusids = getStatusidsByTaskType(taskType);
        else
            statusids.add(statusid.intValue());
        return "Список заявок по категории " +
                getTitleTypeByCode(statusid == null? taskType : dashboardMapper.getTaskTypeStatus(statusid).getTaskType()) +
                getCreditDocumentaryPrefix(creditDocumentary) +
                (statusid == null ? "" : " со статусом '" + dashboardMapper.getTaskTypeStatus(statusid).getStatus() + "' ")+
                (Formatter.str(industry).isEmpty()?"":" по отрасли '" + industry + "'")+
                " за "+ ((mainPeriod==null || mainPeriod) ?"отчетный":"сравнительный")+
                " период с " + Formatter.format(from) + " по " +
                Formatter.format(to) + " на сумму " + getTitleSum(statusids, from, to, creditDocumentary, departments,
                                                                  tradingDeskSelected, isTradingDeskOthers);
    }

    @Override
    public String getFileName(Long statusid, String taskType, Date from, Date to, Long creditDocumentary,
                              Long[] departments, Long[] tradingDeskSelected, Boolean isTradingDeskOthers,
                              Boolean mainPeriod) {
        List<Integer> statusids = new ArrayList<Integer>();
        if (statusid == null)
            statusids = getStatusidsByTaskType(taskType);
        else
            statusids.add(statusid.intValue());
        String shortStatusName = "";
        if (statusid != null) {
            shortStatusName = dashboardMapper.getTaskTypeStatus(statusid).getStatus();
            if (shortStatusName != null)
                shortStatusName = "_" + shortStatusName.replace(" сделки", "").replace(" ", "_").toLowerCase();
        }
        return "Отчет_Dashboards." +
                getTitleTypeByCode(statusid == null? taskType : dashboardMapper.getTaskTypeStatus(statusid).getTaskType()) +
                (statusid == null ? "" : shortStatusName) +
                getCreditDocumentaryFilePrefix(creditDocumentary) +
                Formatter.format(from) + "-" +
                Formatter.format(to);
    }

    @Override
    public Date getDealCreateDate(Long idMdtask) {
    	return dashboardMapper.getDealCreateDate(idMdtask);
    }

    @Override
    public Date getDealChangeDate(Long idMdtask) {
    	return dashboardMapper.getDealChangeDate(idMdtask);
    }
    
    @Override
    public String getDealTeamMemberByRoleName(Long idMdtask, String roleName) {
       	return dashboardMapper.getDealTeamMemberByRoleName(idMdtask, roleName);
    }

    /**
     * Возвращает список ставок.
     */
	@Override
	public List<IndRate> getRate(Long id) {
       	return dashboardMapper.getRate(id);
	}

    @Override
    public String getOrganizationBranch(String organizationId) {
        RatingService ratingService = null;
        try {
            ratingService = ServiceFactory.getService(RatingService.class);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        if (ratingService == null)
            return null;

        CalcHistoryInput input = new CalcHistoryInput();
        input.setPartnerId(organizationId);
        input.setRDate(new Date());

        CalcHistoryOutput output = null;
        try {
            output = ratingService.getKEKICalcHistory(input);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }

        if (output != null)
            return output.getBranch();

        return null;
    }

    @Override
    public void savePipelineSettings(Long idUser, String settingJson) {
        if (idUser == null || settingJson == null)
            return;
        //LOGGER.info("idUser="+idUser+", settingJson="+settingJson);
        try {
            dashboardMapper.savePipelineSettings(idUser, settingJson);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

    }
}
