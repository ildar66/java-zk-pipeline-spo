package ru.md.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.*;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.uit.director.action.AbstractAction;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.tasks.ProcessInfo;

import com.vtb.domain.AbstractSupply;
import com.vtb.domain.Comment;
import com.vtb.domain.Commission;
import com.vtb.domain.CommissionDeal;
import com.vtb.domain.DepartmentAgreement;
import com.vtb.domain.Deposit;
import com.vtb.domain.DepositorFinStatus;
import com.vtb.domain.EarlyPayment;
import com.vtb.domain.ExtendText;
import com.vtb.domain.Fine;
import com.vtb.domain.Forbidden;
import com.vtb.domain.Guarantee;
import com.vtb.domain.InterestPay;
import com.vtb.domain.LiquidityLevel;
import com.vtb.domain.Main;
import com.vtb.domain.OperationType;
import com.vtb.domain.OtherCondition;
import com.vtb.domain.PaymentSchedule;
import com.vtb.domain.PrincipalPay;
import com.vtb.domain.SpecialCondition;
import com.vtb.domain.SupplyType;
import com.vtb.domain.Task;
import com.vtb.domain.TaskContractor;
import com.vtb.domain.TaskCurrency;
import com.vtb.domain.TaskHeader;
import com.vtb.domain.TaskProcent;
import com.vtb.domain.TaskProduct;
import com.vtb.domain.TaskStopFactor;
import com.vtb.domain.TaskSupply;
import com.vtb.domain.Trance;
import com.vtb.domain.Warranty;
import com.vtb.exception.FactoryException;
import com.vtb.exception.ModelException;
import com.vtb.exception.NoSuchOrganizationException;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.TaskActionProcessor;
import com.vtb.util.EjbLocator;
import com.vtb.util.Formatter;

import ru.masterdm.compendium.domain.Currency;
import ru.masterdm.compendium.domain.Department;
import ru.masterdm.compendium.domain.cc.QuestionType;
import ru.masterdm.compendium.domain.crm.ComissionSize;
import ru.masterdm.compendium.domain.crm.CommissionType;
import ru.masterdm.compendium.domain.crm.Ensuring;
import ru.masterdm.compendium.domain.crm.Organization;
import ru.masterdm.compendium.domain.crm.PatternPaidPercentType;
import ru.masterdm.compendium.domain.spo.CRMRepayment;
import ru.masterdm.compendium.domain.spo.CalcBase;
import ru.masterdm.compendium.domain.spo.ContractorType;
import ru.masterdm.compendium.domain.spo.Person;
import ru.masterdm.compendium.domain.spo.StandardPriceCondition;
import ru.masterdm.compendium.domain.spo.StopFactor;
import ru.masterdm.compendium.model.CompendiumCrmActionProcessor;
import ru.masterdm.compendium.model.CompendiumSpoActionProcessor;
import ru.masterdm.integration.ced.CedService;
import ru.masterdm.spo.service.IDashboardService;
import ru.masterdm.spo.utils.SBeanLocator;

import ru.md.compare.MdTaskComparer;
import ru.md.controller.PipelineController;
import ru.md.domain.InterestRate;
import ru.md.domain.MdTask;
import ru.md.domain.Org;
import ru.md.domain.OtherGoal;
import ru.md.domain.Pipeline;
import ru.md.domain.Product;
import ru.md.domain.StatusReturn;
import ru.md.domain.TargetGroupLimit;
import ru.md.domain.TargetGroupLimitType;
import ru.md.domain.Withdraw;
import ru.md.domain.dashboard.CCQuestion;
import ru.md.domain.dict.CommonDictionary;
import ru.md.domain.dict.InterestRateChange;
import ru.md.helper.TaskHelper;
import ru.md.persistence.MdTaskMapper;
import ru.md.spo.dbobjects.LimitTypeJPA;
import ru.md.spo.dbobjects.MdTaskTO;
import ru.md.spo.dbobjects.ProductTypeJPA;
import ru.md.spo.dbobjects.TaskJPA;
import ru.md.spo.ejb.CrmFacadeLocal;
import ru.md.spo.ejb.NotifyFacadeLocal;
import ru.md.spo.ejb.PupFacadeLocal;
import ru.md.spo.ejb.TaskFacadeLocal;

/**
 * Filter to save attributes to MD dinamic model.
 *
 * @author Andrey Pavlenko
 */
public class MDTaskFilter implements Filter {
    private TaskActionProcessor processor = null;
    private static final Logger LOGGER = LoggerFactory.getLogger(MDTaskFilter.class.getName());
    private WorkflowSessionContext wsc;

    /*
     * (non-Java-doc)
     *
     * @see java.lang.Object#Object()
     */
    public MDTaskFilter() {
        super();
    }

    /*
     * (non-Java-doc)
     *
     * @see javax.servlet.Filter#init(FilterConfig arg0)
     */
    @Override
    public void init(FilterConfig arg0) throws ServletException {
        processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
    }

    private void recalculateDrawdownDateInMonth(ServletRequest request, Long idMdTask) {
        Integer mdTask_usePeriod = Formatter.parseInt(request.getParameter("mdTask_usePeriod"));
        if (mdTask_usePeriod == null)
            return;
        //срок использования изменился. Нужно менять поля
        try {
            TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
            Task task = processor.getTask((new Task(idMdTask)));
            //меняем Срок использования срок дней
            task.getMain().setUseperiod(mdTask_usePeriod);
            processor.updateTask(task);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Сохраняет измененные атрибуты заявки в базу данных
     *
     * @param rq
     * @return Заявка
     * @throws ParseException
     */
    public Task updateData(ServletRequest rq) throws Exception {
        Task task = null;
        CompendiumSpoActionProcessor spoProcessor = (CompendiumSpoActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumSpo");
        PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        wsc = AbstractAction.getWorkflowSessionContext((HttpServletRequest) rq);
        ProcessInfo pi = wsc.getCurrTaskInfo(false);
        Long idprocess = pi.getIdProcess();

        LOGGER.debug("MDTaskFILTER " + idprocess);
        try {
            //VTBSPO-1292 обновляем аттрибут сами ибо нормально разобраться с движком ПУП не успеваю
        	if (rq.getParameter("Решение о дальнейшей работе со сделкой по итогам проведения экспертиз201")!=null)
        		pupFacadeLocal.updatePUPAttribute(idprocess,
        				"Решение о дальнейшей работе со сделкой по итогам проведения экспертиз",
        				rq.getParameter("Решение о дальнейшей работе со сделкой по итогам проведения экспертиз201"));
            if (rq.getParameter("Требуется обновление списка дополнительных экспертиз219")!=null)
                pupFacadeLocal.updatePUPAttribute(idprocess,
                        "Требуется обновление списка дополнительных экспертиз",
                        rq.getParameter("Требуется обновление списка дополнительных экспертиз219"));

            String mdtaskid=rq.getParameter("mdtaskid");
            if (mdtaskid == null) {
                task = processor.findByPupID(idprocess,true);
            } else {
                LOGGER.info("============ mdtaskid= " + rq.getParameter("mdtaskid"));
                task = processor.getTask(new Task(new Long(rq.getParameter("mdtaskid"))));
            }

            //возможно нужно перезаписать pipeline
            try {
            	PipelineController.onSaveCheckCloseProbability(rq, task.getId_task());
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
            //проверяем не изменились ли срок и дата по сделке
            Integer periodBefore = task.getMain().getPeriod();
            Date validtoBefore = task.getMain().getValidto();
            String periodDimensionBefore = task.getMain().getPeriodDimension();

            if(task.getHeader().getNumber().longValue()==0){
            	task.getHeader().setNumber(pupFacadeLocal.getNextSublimitNumber(task.getParent()));
            }

            TaskHeader header = task.getHeader();
            // Секция 'Основные параметры'
            LOGGER.info("============ update main parameters");
            updateMainParameters(rq, task, header);
            //Секция 'Ответственные подразделения'
            updateDepartments(rq, task, header);
            //Секция 'Контрагенты'
            updateContragents(rq, task);
            //Секция 'Стоимостные условия'
            updatePriceConditions(rq, task);
            //Секция 'Обеспечение'
            updateSupply(rq, task);
            //Секция 'Условия'
            updateConditions(rq, task);
            //Секция 'Стоп-факторы'
            updateStopFactors(rq, task, spoProcessor);
            //Секция 'Комментарии'
            updateComments(rq, task);
            // Передача на Кредитный Комитет
            update4CC(rq, task);
            //Статус возврата
            updateReturnStatus(rq, task);
            // Транши
            updateTranches(rq, task);
            // Заключения экспертных подразделений
            updateExpertConclusions(rq, task);

            //Секция ПМ
            MdTask mdTask = fillPmSectionData(rq, task);

            task.setDeleted(false);
            processor.updateTask(task, mdTask);
            TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
            taskFacadeLocal.refreshTask(task.getId_task());
            if(rq.getParameter("abody")!=null) {
            	taskFacadeLocal.setAuthorizedPerson(task.getId_task(),
            			rq.getParameter("abody").equals("")?null:Long.valueOf(rq.getParameter("abody")));
            }

            //DEBUG
            //processor.makeVersion(task.getId_task(), wsc.getIdUser(), "debug stage","debug roles");

            //удаление саблимита
            if (rq.getParameterValues("active_sublimit") != null) {
                try {
                    TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
                    ArrayList<Task> sublimits = processor.findTaskByParent(task.getId_task(), false, true);
                    for(Task sublimit: sublimits){
                        boolean find=false;
                        for(String sublimitid: rq.getParameterValues("active_sublimit")){
                            if(sublimit.getId_task().toString().equals(sublimitid)){
                                find=true;
                                break;
                            }
                        }
                        if(!find){
                            sublimit.setDeleted(true);
                            processor.updateTask(sublimit);
                        }
                    }
                } catch (NumberFormatException e) {
                    LOGGER.error(e.getMessage());
                } catch (ModelException e) {
                    LOGGER.error(e.getMessage());
                }
            }

            updateDecision(task);

            Integer periodAfter = task.getMain().getPeriod();
            Date validtoAfter = task.getMain().getValidto();
            String periodDimensionAfter = task.getMain().getPeriodDimension();
            if(periodBefore==null&&periodAfter!=null|| periodBefore!=null&&periodAfter==null
            		|| periodBefore!=null&&!periodBefore.equals(periodAfter)
            		|| validtoBefore==null && validtoAfter!=null || validtoBefore!=null && validtoAfter==null
            		|| validtoBefore!=null&&!validtoBefore.equals(validtoAfter)
            		|| !periodDimensionAfter.equals(periodDimensionBefore)){
            	LOGGER.info("recalculate supply period");
            	TaskJPA taskJPA=taskFacadeLocal.getTask(task.getId_task());
            	for(AbstractSupply s : task.getSupply().getAllSupply()){
            		s.setPeriod(taskJPA.getPeriod4newSupply(s.getSupplyType()));
            		s.setPeriodDimension(taskJPA.getPeriodDimension());
            		s.setTodate(taskJPA.getToDate4newSupply(s.getSupplyType()));
            	}
            	processor.updateTask(task);
            }

            try {
                updateProject(rq, task.getId_task());
            } catch (Exception e){
                LOGGER.error(e.getMessage(), e);
            }
            recalculateDrawdownDateInMonth(rq, task.getId_task());

            return task;
        } catch (Exception e) {
        	LOGGER.error(e.getMessage(), e);
            e.printStackTrace();
            throw e;
        }
    }

    private void updateProject(ServletRequest rq,Long mdtaskid) throws Exception {
        if(rq.getParameter("mdtaskprojectName") != null){
            MdTask mdTask = SBeanLocator.singleton().mdTaskMapper().getById(mdtaskid);
            mdTask.setProjectName(rq.getParameter("mdtaskprojectName"));
            mdTask.setProjectClass(rq.getParameter("projectClass"));
            mdTask.setProjectIndustry(rq.getParameter("projectIndustry"));
            mdTask.setProjectRegion(rq.getParameter("projectRegion"));
            mdTask.setProjectRating1(rq.getParameter("projectRating1"));
            mdTask.setProjectRating2(rq.getParameter("projectRating2"));
            mdTask.setProjectRating3(rq.getParameter("projectRating3"));
            mdTask.setProjectRating4(rq.getParameter("projectRating4"));
            SBeanLocator.singleton().mdTaskMapper().updateProject(mdTask);
        }
    }

    /**
     * Заполнение заявки из http-запроса.
     * @param request http-запрос
     * @param task операция
     * @param mdTask заявка
     */
    private MdTask fillPmSectionData(ServletRequest request, Task task) throws Exception {
        if (StringUtils.isEmpty(request.getParameter("updatePmSection")))
            return null;

        MdTask mdTask = processor.getPipelineWithinMdTask(task.getId_task());

        if (mdTask == null)
            return null;

        TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
        TaskJPA taskJPA = taskFacadeLocal.getTask(task.getId_task());

        Pipeline pipeline = mdTask.getPipeline();
        pipeline.setPlanDate(Formatter.parseDate(request.getParameter("mdTask_pipeline_planDate"), null));
        pipeline.setStatus(request.getParameter("mdTask_pipeline_status"));
        pipeline.setMargin(Formatter.parseBigDecimal(request.getParameter("mdTask_pipeline_margin")));
        pipeline.setCloseProbability(Formatter.parseBigDecimal(request.getParameter("mdTask_pipeline_closeProbability")));
        pipeline.setLaw(request.getParameter("mdTask_pipeline_law"));
        pipeline.setGeography(request.getParameter("mdTask_pipeline_geography"));
        pipeline.setSupply(request.getParameter("mdTask_pipeline_supply"));
        if (request.getParameterValues("mdTask_pipeline_financingObjectives") != null)
            pipeline.setFinancingObjectives(Arrays.asList(request.getParameterValues("mdTask_pipeline_financingObjectives")));
        else
            pipeline.setFinancingObjectives(null);
        pipeline.setDescription(request.getParameter("mdTask_pipeline_description"));
        pipeline.setNote(request.getParameter("mdTask_pipeline_note"));
        pipeline.setAdditionalBusiness(request.getParameter("mdTask_pipeline_additionalBusiness"));
        pipeline.setSyndication(request.getParameter("mdTask_pipeline_syndication") != null);
        pipeline.setSyndicationNote(request.getParameter("mdTask_pipeline_syndicationNote"));
        pipeline.setWal(Formatter.parseBigDecimal(request.getParameter("mdTask_pipeline_wal")));
        pipeline.setHurdleRate(Formatter.parseBigDecimal(request.getParameter("mdTask_pipeline_hurdleRate")));
        pipeline.setMarkup(Formatter.parseBigDecimal(request.getParameter("mdTask_pipeline_markup")));
        pipeline.setPcCash(Formatter.parseBigDecimal(request.getParameter("mdTask_pipeline_pcCash")));
        pipeline.setPcReserve(Formatter.parseBigDecimal(request.getParameter("mdTask_pipeline_pcReserve")));
        pipeline.setPcDerivative(Formatter.parseBigDecimal(request.getParameter("mdTask_pipeline_pcDerivative")));
        pipeline.setPcTotal(Formatter.parseBigDecimal(request.getParameter("mdTask_pipeline_pcTotal")));
        pipeline.setSelectedLineVolume(Formatter.parseBigDecimal(request.getParameter("mdTask_pipeline_selectedLineVolume")));
        pipeline.setManagerPriority(request.getParameter("mdTask_pipeline_managerPriority") != null);
        pipeline.setPublicDeal(request.getParameter("mdTask_pipeline_publicDeal") != null);
        pipeline.setNewClient(request.getParameter("mdTask_pipeline_newClient") != null);
        pipeline.setManagementPriority(request.getParameter("mdTask_pipeline_managementPriority") != null);
        pipeline.setStatusManual(request.getParameter("mdTask_pipeline_status_manual") != null);
        pipeline.setFlowInvestment(request.getParameter("mdTask_pipeline_flowInvestment"));
        pipeline.setTradeFinance(Formatter.parseInt(request.getParameter("mdTask_trade_finance")));
        pipeline.setProductTypeFactor(Formatter.parseBigDecimal(request.getParameter("mdTask_pipeline_productTypeFactor")));
        pipeline.setPeriodFactor(Formatter.parseBigDecimal(request.getParameter("mdTask_pipeline_periodFactor")));
        pipeline.setFundCompany(request.getParameter("mdTask_pipeline_fundCompany"));
        pipeline.setVtbContractor(request.getParameter("mdTask_pipeline_vtbContractor"));
        pipeline.setTradingDesk(request.getParameter("mdTask_pipeline_tradingDesk"));
        pipeline.setProlongation(request.getParameter("mdTask_pipeline_prolongation") != null);
        pipeline.setHideInReport(request.getParameter("mdTask_pipeline_hideInReport") != null);
        pipeline.setHideInReportTraders(request.getParameter("mdTask_pipeline_hideInReportTraders") != null);

        Org mainOrganization = mdTask.getMainOrganization();
        if (mainOrganization != null) {
            mainOrganization.setPreliminaryRating(request.getParameter("mdTask_mainOrganization_preliminaryRating"));
        }

        CommonDictionary<String> baseRate = new CommonDictionary<String>();
        baseRate.setId(request.getParameter("mdTask_baseRate"));

        InterestRate interestRate = new InterestRate();
        interestRate.setId(Formatter.parseLong(request.getParameter("mdTask_interestRates_id")));
        interestRate.setLoanRate(Formatter.parseBigDecimal(request.getParameter("mdTask_interestRates_loanRate")));
        interestRate.setFundingRate(Formatter.parseBigDecimal(request.getParameter("mdTask_interestRates_fundingRate")));
        List<InterestRate> interestRates = new ArrayList<InterestRate>();
        interestRates.add(interestRate);

        Product productType;
        if (mdTask.isProduct()) {
            productType = new Product();
            productType.setProductid(request.getParameter("mdTask_productTypeId"));
            productType.setName(request.getParameter("mdTask_productTypeName"));
        } else
            productType = null;

        mdTask.setPriority(request.getParameter("mdTask_priority"));
        mdTask.setProposedDtSigning(Formatter.parseDate(request.getParameter("mdTask_proposedDtSigning"), null));
        mdTask.setUsePeriod(Formatter.parseLong(request.getParameter("mdTask_usePeriod")));
        mdTask.setCurrency(request.getParameter("mdTask_currency"));
        mdTask.setMdtaskSum(Formatter.parseBigDecimal(request.getParameter("mdTask_mdtaskSum")));
        if(taskJPA.getPeriods().size()>1){
            mdTask.setInterestRateFixed(taskJPA.isInterestRateFixed());
            mdTask.setInterestRateDerivative(taskJPA.isInterestRateDerivative());
            //обновление по первому периоду идёт в классе TaskFacade
        } else {//в сделке нет периодов
            mdTask.setInterestRateFixed(request.getParameter("mdTask_fixedRate")!=null);
            mdTask.setInterestRateDerivative(request.getParameter("mdTask_floatRate")!=null);
        }
        mdTask.setBaseRate(baseRate);
        mdTask.setInterestRates(interestRates);
        mdTask.setFixingRateSpread(Formatter.parseBigDecimal(request.getParameter("mdTask_fixingRateSpread")));
        mdTask.setEarlyRepaymentSpread(Formatter.parseBigDecimal(request.getParameter("mdTask_earlyRepaymentSpread")));
        mdTask.setProductType(productType);
        mdTask.setProductName((productType == null) ? null : productType.getName());
        mdTask.setCrossSellId(Formatter.parseLong(request.getParameter("mdTask_pipeline_supply_cross_sell_type")));
        //mdTask.setMargin(Formatter.parseBigDecimal(request.getParameter("mdTask_margin")));

        return mdTask;
    }

    /**
     * если есть какое-то решение, обновить аттрибут Decision
     */
    private void updateDecision(Task task) {
        if(task.getTaskStatusReturn().getStatusReturn()!=null
                &&task.getTaskStatusReturn().getStatusReturn().getId()!=null
                &&!task.getTaskStatusReturn().getStatusReturn().getId().equals("0")){//есть статус CRM
        	List<StatusReturn> allstatus = SBeanLocator.singleton().getCompendiumMapper().findStatusReturnList();
            for(StatusReturn sr : allstatus){
                if(sr.getId().trim().equals(task.getTaskStatusReturn().getStatusReturn().getId().trim())){
                    processor.updateAttribute(task.getId_pup_process().longValue(),
                            "Decision",
                            sr.getType());
                }
            }
            return;
        }
        if(task.getCcStatus().getStatus().getId()!=null){//есть статус КК
            processor.updateAttribute(task.getId_pup_process().longValue(),
                    "Decision",
                    String.valueOf(SBeanLocator.singleton().getCompendiumMapper().getCcResolutionStatusCategoryId(task.getCcStatus().getStatus().getId())));
        }
    }
    /**проинициировать question_group */
    private Long initQuestionGroup(Long idMdTask) {
        MdTaskMapper mapper = SBeanLocator.singleton().mdTaskMapper();
        Long questionGroup = mapper.getById(idMdTask).getQuestionGroup();
        if (questionGroup != null)
            return questionGroup;
        mapper.initQuestionGroup(idMdTask);
        return mapper.getById(idMdTask).getQuestionGroup();
    }
    private void saveConclusion(ServletRequest request, Long idMdTask){
        if (request.getParameter("question_id") == null)
            return;
        MdTaskMapper mapper = SBeanLocator.singleton().mdTaskMapper();
        Long questionGroup = initQuestionGroup(idMdTask);
        //удалить лишние
        List<Long> allId = new ArrayList<Long>();
        for (String idS : request.getParameterValues("question_id"))
            if (!idS.startsWith("newquestion"))
                allId.add(Long.valueOf(idS));
        for (Long id : mapper.getIdMdtaskByQuestionGroup(questionGroup))
            if (!allId.contains(id) && !id.equals(idMdTask))
                mapper.delMdtask(id);
        for (int i=0; i < request.getParameterValues("question_id").length; i++) {
            CCQuestion q = new CCQuestion();
            String question_id = request.getParameterValues("question_id")[i];
            q.id = question_id.startsWith("newquestion") ? null : Long.valueOf(question_id);
            q.idDep = Formatter.parseInt(request.getParameterValues("assigneeAuthority")[i]);
            q.ccQuestionType = Formatter.parseInt(request.getParameterValues("CcQuestionType")[i]);
            q.pkr = request.getParameterValues("creditDecisionProject")[i];
            mapper.mergeCCQuestion(q, questionGroup);
        }
        //обновлять поля у группы вопросов при сохранении
        mapper.syncQuestionGroupTask(idMdTask);
    }

    /*
     * (non-Java-doc)
     *
     * @see javax.servlet.Filter#doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain next) throws IOException, ServletException {
        if (request.getParameter("isWithComplete")==null) {//это фантомный запрос только в FireFox. Пропускаем его
            next.doFilter(request, response);
        }
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpServletRequest rq = (HttpServletRequest) request;
        WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(rq);
        try {
        	TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
            MdTaskComparer mdTaskComparer = new MdTaskComparer(taskFacadeLocal.getTaskFull(TaskHelper.getIdMdTask(rq)));
        	Task updatedTask = this.updateData(request);
            updateDataJPA(request, updatedTask.getId_task());
            updatePupAttr(wsc);
            PupFacadeLocal pupEJB = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
            TaskJPA taskJPA = taskFacadeLocal.getTask(updatedTask.getId_task());
            try {
            	if(taskJPA.isProduct()) taskFacadeLocal.savePeriodObKind(taskJPA.getPeriodObKind(updatedTask));
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
            try {
            	String status = pupEJB.getPUPAttributeValue(taskJPA.getProcess().getId(), "Статус");
            	if(request.getParameter("StatusReturn")!=null && !status.equals("Отказано") &&!status.equals("Одобрено")) {
            		taskJPA.setStatus_backup(status);
            		taskFacadeLocal.merge(taskJPA);
            	}
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
            if(request.getParameter("section_conclusion") != null)
                saveConclusion(request, updatedTask.getId_task());
            taskFacadeLocal.spoContractorSync(updatedTask.getId_task());
            String save_action = request.getParameter("save_action");if(save_action==null)save_action="";
            if(save_action.equals("ced_approve")){
            	LOGGER.info("===========================ced_approve==================================");
            	taskJPA.setCed_approve_date(new Date());
            	taskJPA.setCed_approve_login(wsc.getCurrentUserInfo().getLogin());
            	taskFacadeLocal.merge(taskJPA);
            }
            if(save_action.equals("ced")){
            	LOGGER.info("===========================CED INTEGRATION==================================");
            	try {
                    ru.masterdm.integration.ServiceFactory.getService(CedService.class).completeDealConclusionEditConditionTask(request.getParameter("ced_id"));
            	} catch (Exception e) {
            		LOGGER.error(e.getMessage(), e);
            	}
            	resp.sendRedirect("close.htm");
            }
            if(updateMiu2Status(save_action, taskJPA.getId()))
                resp.sendRedirect("/km-web/buildingevent/bydeal");

            String status = request.getParameter("status");
            if (status != null && !status.isEmpty()){
                pupEJB.updatePUPAttribute(taskJPA.getIdProcess(), "Статус",status);
                if(status.equals("Одобрено")){
                    taskJPA.setStatusReturn(TaskHelper.dict().getApprovedStatusReturn());
                    taskFacadeLocal.merge(taskJPA);
                    
                    Long currentUserId = wsc.getCurrentUserInfo().getIdUser();
                    
                    SBeanLocator.singleton().mdTaskMapper().createDealPercentHistoryValue(taskJPA.getId(), currentUserId);
                }
            }
            IDashboardService dashboardService = (IDashboardService) SBeanLocator.singleton().getBean("dashboardService");
            dashboardService.logTask(taskJPA.getId());

            //В случае, когда в СПО редактируются и сохраняются атрибуты сделки
            //Работнику мидл-офиса (КОД), включенному в секцию «Участники» активного [1] запроса КОД по этой сделке, необходимо направлять уведомление
            NotifyFacadeLocal notifyFacade = com.vtb.util.EjbLocator.getInstance().getReference(NotifyFacadeLocal.class);
            MdTaskTO taskAfter = taskFacadeLocal.getTaskFull(TaskHelper.getIdMdTask(rq));
            try {
                notifyFacade.notifyTaskChange(mdTaskComparer.getSectionDiff(taskAfter), taskAfter);
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
        } catch (Exception e1) {
        	LOGGER.error(e1.getMessage(), e1);
        	//вывести сообщение об ошибке
            wsc.setErrorMessage("Ошибка при сохранении заявки: "+e1.getMessage());
            resp.sendRedirect("errorPage.jsp");
        }
        // проверяем нужно ли создать саблимит
        String sublimitFlag = request.getParameter("sublimit");
        if (sublimitFlag != null && !sublimitFlag.equalsIgnoreCase("0")) {
            LOGGER.info("param sublimit = " + sublimitFlag);
            try {
                this.createSublimit(sublimitFlag);
            } catch (FactoryException e) {
                LOGGER.error(e.getMessage());
            }
        }

        next.doFilter(request, response);
    }

    /**
     * Обновление статуса МИУ2.
     * @param button выбор пользователя (нажатие кнопки)
     * @param mdtaskid идентификатор заявки СПО
     * @return признак обновления статуса
     * @throws Exception ошибка
     */
    private boolean updateMiu2Status(String button, Long mdtaskid) throws Exception {
        TaskFacadeLocal taskFacadeLocal = EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
        NotifyFacadeLocal notifyFacadeLocal = EjbLocator.getInstance().getReference(NotifyFacadeLocal.class);

        if(button.equals("Направить на акцепт")){
            TaskJPA taskJPA = taskFacadeLocal.getTask(mdtaskid);
            taskJPA.setMonitoringMode("Акцепт изменений");
            taskJPA.setMonitoringPriceUserId((wsc.getCurrentUserInfo().getIdUser()));
            taskJPA.setMonitoringUserWorkId(null);
            taskFacadeLocal.merge(taskJPA);
            try {
                //при переходе на Акцепт ты по ID_MDTASK в поле PERCENT_STATE_NAME прописываешь 'Акцепт изменений'
                taskFacadeLocal.updateKmDealPercentState(taskJPA.getId(),"Акцепт изменений");
                notifyFacadeLocal.interestRateChangeNotification(mdtaskid, InterestRateChange.TO_ACCEPT);
            } catch (Exception e){}
            return true;
        }
        if (button.equals("Акцептовать") || button.equals("Отправить на доработку") || button.equals("Отказать в акцепте")){
            TaskJPA taskJPA = taskFacadeLocal.getTask(mdtaskid);
            taskJPA.setMonitoringMode("Редактирование ставки");
            taskJPA.setMonitoringUserWorkId(null);
            if(button.equals("Отказать в акцепте"))
                taskJPA.setMonitoringMdtask(null);
            taskFacadeLocal.merge(taskJPA);
            if (button.equals("Акцептовать")) {
            	taskFacadeLocal.approvePriceConditionVersionMonitoringAndCreatePercentHistory(taskJPA.getId());
                try {
                    notifyFacadeLocal.interestRateChangeNotification(mdtaskid, InterestRateChange.ACCEPTED);
                } catch (Exception e){
                    LOGGER.error(e.getMessage(), e);
                }            	
            }
            if (button.equals("Акцептовать") || button.equals("Отказать в акцепте"))
                try {
                    //при Акцепте и Отказе ты из этой таблицы по ID_MDTASK удаляешь записи
                    taskFacadeLocal.clearKmDealPercentState(taskJPA.getId());
                } catch (Exception e){}
            if (button.equals("Отправить на доработку"))
                try {
                    //при Возвращении на доработку ты по ID_MDTASK в поле PERCENT_STATE_NAME прописываешь 'Редактирование ставки'
                    taskFacadeLocal.updateKmDealPercentState(taskJPA.getId(),"Редактирование ставки");
                    notifyFacadeLocal.interestRateChangeNotification(mdtaskid, InterestRateChange.RETURN);
                } catch (Exception e){}
            return true;
        }
        return false;
    }

    private TaskJPA updateDataJPA(ServletRequest request, Long updatedTask) throws FactoryException {
        TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
        try {
        	return taskFacadeLocal.updateDataJPA(request, updatedTask);
		} catch (Exception e) {
			try {
				return taskFacadeLocal.updateDataJPA(request, updatedTask);
			} catch (Exception e1) {
				LOGGER.error(e1.getMessage());
				return taskFacadeLocal.updateDataJPA(request, updatedTask);
			}
		}
    }

    private void updatePupAttr(WorkflowSessionContext wsc) throws FactoryException {
		try {
		    PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
		    pupFacadeLocal.updatePUPAttribute(wsc.getCurrTaskInfo(false).getIdProcess(),
		            "Текущий пользователь", wsc.getCurrentUserInfo().getLogin());
		    pupFacadeLocal.updatePUPAttribute(wsc.getCurrTaskInfo(false).getIdProcess(),
		            "Текущая дата", Formatter.formatDateTime(new Date()));
		} catch (Exception e) {
		    LOGGER.error(e.getMessage(), e);
		}
    }

    /**
     * @param parent - номер mdtaskid для которого создать саблимит
     * @throws FactoryException
     */
    public void createSublimit(String parentid) throws FactoryException {
        LOGGER.info("create sublimits "+parentid);
        //создаем нашу модель данных
        TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
        PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        try {
            Task task = new Task(null);
            Task parent = processor.getTask(new Task(new Long(parentid)));
            task.getHeader().setNumber(pupFacadeLocal.getNextSublimitNumber(parent.getId_task()));
            task.setParent(parent.getId_task());
            for (int i=0;i<parent.getContractors().size();i++){//copy contractor to sublimit
                task.getContractors().add(parent.getContractors().get(i));
            }
            //по умолчанию для нового саблимита берем ежемесячные уплата процентов
            //task.getGeneralCondition().setProcent_order(new PatternPaidPercentType(1));
            task.getHeader().setIdLimitType(1);
            task.getHeader().setStartDepartment(parent.getHeader().getStartDepartment());
            task.getHeader().setPlace(parent.getHeader().getPlace());
            task.getMain().setCurrency(parent.getMain().getCurrency2());
            task.setDeleted(true);//саблимит не будет живым, пока не нажмем сохранить
            // для сублимита значения наследуются из значений вышестоящего лимита
            if (parent.getMain().isRenewable()) {
                task.getMain().setRenewable(true);
                task.getMain().setMayBeRenewable(true);
            } else {
                task.getMain().setRenewable(false);
                task.getMain().setMayBeRenewable(false);
            }
            // VTBSPO-204 версия сублимита берется из родительского лимита
            task.getHeader().setVersion(parent.getHeader().getVersion());
            task=processor.createTask(task);
            task.getHeader().setNumber(new Long(0));
            processor.updateTask(task);
        } catch (Exception e) {
        	LOGGER.error(e.getMessage(), e);
        	e.printStackTrace();
        }
    }

    /*
     * (non-Java-doc)
     *
     * @see javax.servlet.Filter#destroy()
     */
    @Override
    public void destroy() {
        // Do nothing
    }


    /*******************************************************************************************************
     *                                   Секция 'Основные параметры'
     *
     *******************************************************************************************************/
    private void updateMainParameters(ServletRequest rq, Task task, TaskHeader header) throws FactoryException {
        // если доступна для редактирования сама секция Основные параметры
        // если же недоступна, то не обнуляем параметры в task (). И те же параметры и будут отписаны в базу (в неизмененном виде).
    	if (rq.getParameter("3faces") != null)
            task.setFaces3(rq.getParameter("3faces").equals("y"));
    	if (rq.getParameter("operationtype") != null)
            header.setOperationtype(new OperationType(Formatter.parseLong(rq.getParameter("operationtype")), null));
        Main main = task.getMain();
        if (rq.getParameter("projectName") != null)
            main.setProjectName(rq.getParameter("projectName"));
        if (rq.getParameter("exchangerate") != null)
            main.setExchangeRate(Formatter.parseBigDecimal(value(rq, "exchangerate")));
        if (rq.getParameter("supplyexist") != null)
            task.getSupply().setExist(rq.getParameter("supplyexist").equals("y"));
        
        if(value(rq,"Секция_основные параметры") != null) {
        	main.setMainLoaded(true);
        	main.setTargetTypeComment(rq.getParameter("targetTypeComment"));
            boolean isCanEditFund = false;
						try {
							isCanEditFund = TaskHelper.isCanEditFund((HttpServletRequest)rq);
						}
						catch (Exception e1) {
							LOGGER.error(e1.getMessage(), e1);
						}

            /************     общие для сделки и лимита параметры   ***********************************/
            // Цели кредитования
            main.getOtherGoals().clear();
            if (rq.getParameterValues("main Иные цели") != null) {
                for (int i = 0; i < rq.getParameterValues("main Иные цели").length; i++)
                    if (numericValue(rq,"main Иные цели",i) != null) { 
                    	Long idTarget = null;
                    	String idKey = "main Иные цели id";
                    	if (rq.getParameterValues(idKey) != null && rq.getParameterValues(idKey).length > i && rq.getParameterValues(idKey)[i] != null && !rq.getParameterValues(idKey)[i].trim().isEmpty())
                    		idTarget = new Long(rq.getParameterValues(idKey)[i].trim());
                    	main.getOtherGoals().add(new OtherGoal(idTarget, rq.getParameterValues("main Иные цели")[i], rq.getParameterValues("main Иные цели условие")[i]));
                    }
            }
            
            // Запрещается предоставление денежных средств на любую из нижеуказанных целей (прямо или косвенно, через третьих лиц)            
            main.getForbiddens().clear();
            if (rq.getParameterValues("main Forbiddens") != null) {
                for (int i = 0; i < rq.getParameterValues("main Forbiddens").length; i++) {
                    if (numericValue(rq,"main Forbiddens",i) != null) { 
                    	main.getForbiddens().add(new Forbidden(rq.getParameterValues("main Forbiddens")[i]));
                    }
                }
            }
            
            /************     Контроль целевого использования   ***********************************/
            main.getTargetGroupLimits().clear();
            String targetGroupLimitAmountCurrencyName = "targetGroupLimitAmountCurrency";
            Long targetGroupLimitCount = rq.getParameterValues(targetGroupLimitAmountCurrencyName) != null ? rq.getParameterValues(targetGroupLimitAmountCurrencyName).length :0L;
            if (targetGroupLimitCount > 0) 
            	for (int i = 0; i < targetGroupLimitCount; i++) {
            		String limitAmountCurrency = numericValue(rq,targetGroupLimitAmountCurrencyName, i);
            		
            		String limitAmountStr = numericValue(rq, "targetGroupLimitAmount", i); 
        			BigDecimal limitAmount = Formatter.parseBigDecimal(limitAmountStr);
        			
        			String idStr = numericValue(rq, "targetGroupLimitId", i);
        			Long id = idStr != null && !idStr.trim().isEmpty() ? new Long(idStr.trim()) : null;
        			
        			String note = rq.getParameterValues("targetGroupLimitNote")[i];

        			TargetGroupLimit targetGroupLimit = new TargetGroupLimit(id, limitAmount, limitAmountCurrency, note);
        			
        			String targetGroupLimitGuid = rq.getParameterValues("targetGroupLimitGuid") != null && rq.getParameterValues("targetGroupLimitGuid").length > i ? rq.getParameterValues("targetGroupLimitGuid")[i] : null;
        			if (targetGroupLimitGuid != null) {
        				targetGroupLimit.setTargetGroupLimitTypes(new ArrayList<TargetGroupLimitType>());
        				
        				String nameKey = targetGroupLimitGuid + "_name"; 
        				String[] targetGroupLimitNames = rq.getParameterValues(nameKey);
        				
        				Long typeCount = targetGroupLimitNames != null ? targetGroupLimitNames.length : 0L;
        				
        				for(int j = 0; j < typeCount; j++) {
        					String idKey = targetGroupLimitGuid+"_id";
        					String idTargetGroupLimitTypeStr = rq.getParameterValues(idKey) != null && rq.getParameterValues(idKey).length > j && !rq.getParameterValues(idKey)[j].trim().isEmpty() ? rq.getParameterValues(idKey)[j] : null;
        					Long idTargetGroupLimitType = null;
        					if (idTargetGroupLimitTypeStr != null)
        						idTargetGroupLimitType = new Long(idTargetGroupLimitTypeStr);
        					
        					String idTargetKey = targetGroupLimitGuid+"_id_target";
        					String idTargetStr = rq.getParameterValues(idTargetKey) != null && rq.getParameterValues(idTargetKey).length > j && !rq.getParameterValues(idTargetKey)[j].trim().isEmpty() ? rq.getParameterValues(idTargetKey)[j] : null;
        					Long idTarget = null;
        					if (idTargetStr != null)
        						idTarget = new Long(idTargetStr); 
        					
        					String targetTypeName = rq.getParameterValues(nameKey)[j];
        					
        					TargetGroupLimitType newTargetGroupLimitType = new TargetGroupLimitType(idTargetGroupLimitType, idTarget, targetTypeName);
            				
        					if (idTarget != null)
        						targetGroupLimit.getTargetGroupLimitTypes().add(newTargetGroupLimitType);
        				}
        			}
        			
        			if (targetGroupLimit.getAmount() != null || (targetGroupLimit.getAmountCurrency() != null && !targetGroupLimit.getAmountCurrency().trim().isEmpty()) || (targetGroupLimit.getNote() != null && !targetGroupLimit.getNote().trim().isEmpty()) || (targetGroupLimit.getTargetGroupLimitTypes() != null && !targetGroupLimit.getTargetGroupLimitTypes().isEmpty()))
        				main.getTargetGroupLimits().add(targetGroupLimit);
            	}

            /************     специфичные для лимита и сублимита параметры   ***********************************/
            if (task.isLimit() || task.isSubLimit()) {
                boolean renewable, projectFin, isRedistribResidues;
                if (rq.getParameter("main Возобновляемый Лимит") == null) renewable = false;
                else renewable = rq.getParameter("main Возобновляемый Лимит").equalsIgnoreCase("y");
                main.setRenewable(renewable);

                if (rq.getParameter("main Категория Сделок - Проектное финансирование") == null) projectFin = false;
                else projectFin = rq.getParameter("main Категория Сделок - Проектное финансирование").equalsIgnoreCase("y");
                main.setProjectFin(projectFin);

                if (task.isLimit()) {
                    if (rq.getParameter("main Перераспределение остатков между Сублимитами") == null) isRedistribResidues = false;
                    else isRedistribResidues = rq.getParameter("main Перераспределение остатков между Сублимитами").equalsIgnoreCase("y");
                    main.setRedistribResidues(isRedistribResidues);
                }


                if (task.isSubLimit()) {
                    if (rq.getParameter("Дополнительная информация по сумме") != null)
                        main.setExtraSumInfo(rq.getParameter("Дополнительная информация по сумме"));
                }

                if (rq.getParameter("Описание категории качества") != null)
                    task.getGeneralCondition().setQuality_category_desc(rq.getParameter("Описание категории качества"));
                if (rq.getParameter("Категория качества ссуды") != null)
                    task.getGeneralCondition().setQuality_category(rq.getParameter("Категория качества ссуды"));

                if (rq.getParameter("Сумма лимита") != null)
                    main.setSum(Formatter.parseBigDecimal(value(rq, "Сумма лимита")));
                if (rq.getParameter("Валюта") != null) main.setCurrency(new Currency(rq.getParameter("Валюта")));
                // Kuznetsov: пока убрал. Когда ВТБ попросит, вернем.
//                if (rq.getParameter("Период использования") != null) main.setUseperiod(Formatter.parseInt(value(rq, "Период использования")));
//                main.setUsedate(Formatter.parseDate(value(rq,"Дата использования лимита")));

                if (rq.getParameter("Дата действия лимита") != null) main.setValidto(Formatter.parseDate(value(rq,"Дата действия лимита")));
                if (rq.getParameter("Срок действия лимита") != null) main.setPeriod(Formatter.parseInt(value(rq,"Срок действия лимита")));
                if (rq.getParameter("periodDimension") != null) main.setPeriodDimension(value(rq,"periodDimension"));

//                if (rq.getParameter("Инициирующее подразделение") != null)
//                    header.setStartDepartment(new Department(Formatter.parseInt(rq.getParameter("Инициирующее подразделение"))));
//                if (rq.getParameter("Место проведения сделки") != null)
//                    header.setPlace(new Department(Formatter.parseInt(rq.getParameter("Место проведения сделки"))));
                if((rq.getParameter("Вид лимита") != null) && !(rq.getParameter("Вид лимита").equals("-1")))
                    header.setIdLimitType(Formatter.parseInt(rq.getParameter("Вид лимита")));
                else header.setIdLimitType(null);
                if(task.isLimit()&&rq.getParameterValues("with_sublimit")!=null) header.setIdLimitType(null);
                if (rq.getParameter("Менеджер сделки") != null)
                    header.setManager(rq.getParameter("Менеджер сделки"));

                // Виды сделок
                header.getOpportunityTypes().clear();
                //String limitType = (header.getIdLimitType() != null) ? String.valueOf(header.getIdLimitType()) : "";
                if(rq.getParameterValues("productType")!=null
                		&&(header.getIdLimitType()!=null||rq.getParameterValues("with_sublimit")!=null)) {
                    for (int i=0;i<rq.getParameterValues("productType").length;i++){
                        if (numericValue(rq, "productType",i)!= null) {
                            String productID = rq.getParameterValues("productType")[i];
                            header.getOpportunityTypes().add(
                                new TaskProduct(
                                        productID,"","",
                                    null,
                                    Formatter.parseInt(rq.getParameter("productTypePeriod"+productID)),
                                    rq.getParameter("productTypeCmnt"+productID),
                                    rq.getParameter("productTypeDimension"+productID)));
                        }
                    }
                }

                if(header.getIdLimitType()!=null){
                	TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);

                	for(ProductTypeJPA ptype : taskFacadeLocal.findProductType()){
                		Iterator<TaskProduct> it = header.getOpportunityTypes().iterator();
                		while (it.hasNext()) {
                			TaskProduct tp = it.next();
                			if (tp.getId().equals(ptype.getId())) {
                				// ага, нашли.
                				boolean presentInLimitType = false;
                				for(LimitTypeJPA ltype : ptype.getLimitList()){  //список видов лимита
                					if (header.getIdLimitType()!=null && ltype.getId().intValue() == header.getIdLimitType().intValue()) {
                						presentInLimitType = true;
                						break;
                					}
                				}
                				if (!presentInLimitType) {
                					// данный вид сделки не присутствует в видах сделки для данного вида лимита
                					it.remove();
                				}
                			}
                		}
                	}
                }

                if (rq.getParameter("Погашение основного долга") != null)
                    task.getGeneralCondition().setDischarge(rq.getParameter("Погашение основного долга"));
                if (rq.getParameter("RateTypeFixed") != null)
                    task.getTaskProcent().setRateTypeFixed(rq.getParameter("RateTypeFixed").equals("y"));
                if (rq.getParameter("supplyexist") != null)
                    task.getSupply().setExist(rq.getParameter("supplyexist").equals("y"));

                // Валюта Лимита \ Сублимита
                task.getCurrencyList().clear();
                if(rq.getParameterValues("main_currencyList")!=null) {
                    for (int i=0;i<rq.getParameterValues("main_currencyList").length;i++){
                        task.getCurrencyList().add(
                            new TaskCurrency(null,true,
                                new Currency(rq.getParameterValues("main_currencyList")[i])));
                    }
                }

            } else {
                /************     специфичные для сделки параметры   ***********************************/
                if (value(rq,"isGuarantee") != null) {
                    // установим все требуемые поля для гарантий
                    main.setGuaranteeType("YES".equals(value(rq,"isGuarantee")));
                    if (rq.getParameter("Контракт") != null)
                        main.setContract(rq.getParameter("Контракт"));
                    if (rq.getParameter("Предмет_гарантии") != null)
                        main.setWarrantyItem(rq.getParameter("Предмет_гарантии"));
                    if (rq.getParameter("Бенефициар") != null)
                    	main.setBeneficiary(rq.getParameter("Бенефициар"));
                    if (rq.getParameter("БенефициарОГРН") != null)
                        main.setBeneficiaryOGRN(rq.getParameter("БенефициарОГРН"));
                } else {
                    main.setGuaranteeType(false);
                    main.setContract(null);
                    main.setWarrantyItem(null);
                    main.setBeneficiary(null);
                }

                if (!isCanEditFund)
                	main.setProposedDateSigningAgreement(Formatter.parseDate(value(rq, "Планируемая дата подписания Кредитного соглашения")));

                boolean projectFin=false;
                if (rq.getParameter("main Категория Сделок - Проектное финансирование") == null) projectFin = false;
                else projectFin = rq.getParameter("main Категория Сделок - Проектное финансирование").equalsIgnoreCase("y");
                main.setProjectFin(projectFin);

                if (!isCanEditFund) {
	                task.getCurrencyList().clear();
	                if(rq.getParameterValues("main_currencyList")!=null) {
	                    for (int i=0;i<rq.getParameterValues("main_currencyList").length;i++){
	                        task.getCurrencyList().add(
	                            new TaskCurrency(null,true,
	                                new Currency(rq.getParameterValues("main_currencyList")[i])));
	                    }
	                }
                }

                /* всегда задаем значения (ходят парой!) Лишние проверки могут лишь не записать null, когда его реально нужно записывать! */
                main.setValidto(Formatter.parseDate(value(rq, "Дата действия сделки")));
                main.setPeriod(Formatter.parseInt(value(rq,"Срок действия сделки")));
                main.setPeriodDimension(value(rq,"periodDimension"));
                if (rq.getParameter("Комментарий по сроку сделки") != null)
                    main.setPeriodComment(rq.getParameter("Комментарий по сроку сделки"));

                if (value(rq,"Сумма сделки") != null) main.setSum(Formatter.parseBigDecimal(value(rq,"Сумма сделки")));
                else main.setSum(Formatter.parseBigDecimal("0.0"));
                if (rq.getParameter("exchangerate") != null) main.setExchangeRate(Formatter.parseBigDecimal(value(rq,"exchangerate")));
                if (rq.getParameter("Валюта сделки") != null) main.setCurrency(new Currency(value(rq,"Валюта сделки")));

                if (rq.getParameter("Сумма лимита выдачи") != null) main.setLimitIssueSum(Formatter.parseBigDecimal(value(rq,"Сумма лимита выдачи")));
                else main.setLimitIssueSum(null);
                if (rq.getParameter("Сумма лимита задолженности") != null) main.setDebtLimitSum(Formatter.parseBigDecimal(value(rq,"Сумма лимита задолженности")));
                else main.setDebtLimitSum(null);
                if (rq.getParameter("isLimitIssue") != null
                		|| rq.getParameter("isLimitIssueRO")!=null && rq.getParameter("isLimitIssueRO").equalsIgnoreCase("y")) main.setLimitIssue(true);
                else main.setLimitIssue(false);
                if (rq.getParameter("isDebtLimit") != null
                		|| rq.getParameter("isDebtLimitRO")!=null && rq.getParameter("isDebtLimitRO").equalsIgnoreCase("y")) main.setDebtLimit(true);
                else main.setDebtLimit(false);
                if (!isCanEditFund) {
	                if (rq.getParameter("opIrregular") != null) main.setIrregular(true);
	                else main.setIrregular(false);
                }

                main.setUseperiod(Formatter.parseInt(value(rq, "Период использования сделки")));
                main.setUsedate(Formatter.parseDate(value(rq, "Дата использования сделки")));
                if (rq.getParameter("Комментарий по сроку использования сделки") != null)
                    main.setUseperiodtype(rq.getParameter("Комментарий по сроку использования сделки"));

                if (rq.getParameter("Категория качества ссуды") != null)
                    task.getGeneralCondition().setQuality_category(rq.getParameter("Категория качества ссуды"));
                if (rq.getParameter("Описание категории качества") != null)
                    task.getGeneralCondition().setQuality_category_desc(rq.getParameter("Описание категории качества"));
                if (!isCanEditFund)
                	main.setProduct_name(rq.getParameter("product_name"));
                // Вид продукта (для сделки)
            	header.getOpportunityTypes().clear();
                if((rq.getParameterValues("Вид кредитной сделки")!=null)
                    && (!rq.getParameterValues("Вид кредитной сделки")[0].equals("-1"))) {
                    header.getOpportunityTypes().add(
                        new TaskProduct(
                            rq.getParameterValues("Вид кредитной сделки")[0],"","",
                            null,null,null,null
                     ));
                    try {
                    	if(!main.isIrregular()){
                    		String productid = rq.getParameterValues("Вид кредитной сделки")[0];
                    		if(productid != null){
                    			ru.md.domain.Product product = SBeanLocator.singleton().getCompendiumMapper().getProductById(productid);
                    			if(product!=null)
                    				main.setProduct_name(product.getName());
                    		}
                    	}
					} catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
					}
                }
                if(!main.isIrregular() && rq.getParameterValues("Вид кредитной сделки")!=null && rq.getParameterValues("Вид кредитной сделки")[0].equals("-1"))
                	main.setProduct_name("");

//                if (rq.getParameter("Инициирующее подразделение") != null)
//                    header.setStartDepartment(new Department(Formatter.parseInt(rq.getParameter("Инициирующее подразделение"))));
//                if (rq.getParameter("Место проведения сделки") != null)
//                    header.setPlace(new Department(Formatter.parseInt(rq.getParameter("Место проведения сделки"))));
                if ((rq.getParameter("Вид лимита") != null) && !(rq.getParameter("Вид лимита").equals("-1")))
                    header.setIdLimitType(Formatter.parseInt(rq.getParameter("Вид лимита")));
                if (rq.getParameter("Менеджер сделки") != null)
                    header.setManager(rq.getParameter("Менеджер сделки"));

                if (rq.getParameter("Погашение основного долга") != null)
                    task.getGeneralCondition().setDischarge(rq.getParameter("Погашение основного долга"));
                if (rq.getParameter("assigneeAuthority") != null)
                    task.setAuthorizedBody(Formatter.parseInt((rq.getParameter("assigneeAuthority"))));
                if (rq.getParameter("Дополнительное обеспечение") != null)
                    task.getSupply().setAdditionSupply(rq.getParameter("Дополнительное обеспечение"));
                if (rq.getParameter("RateTypeFixed") != null)
                    task.getTaskProcent().setRateTypeFixed(rq.getParameter("RateTypeFixed").equals("y"));

                if (rq.getParameter("3faces") != null)
                    task.setFaces3(rq.getParameter("3faces").equals("y"));

            }
        } // проверка секции Основные параметры
    }

    /*******************************************************************************************************
     *                                   Секция 'Ответственные подразделения'                              *
     *******************************************************************************************************/
    private void updateDepartments(ServletRequest rq, Task task, TaskHeader header) {
    	
    	//place
    	String selectedPlaceId = rq.getParameter("selectedPlaceId");
    	String currentUserId = rq.getParameter("currentUserId");
    	String mdtaskid = rq.getParameter("mdtaskid");
    	if (currentUserId != null && !currentUserId.isEmpty() && mdtaskid != null && selectedPlaceId != null && !selectedPlaceId.isEmpty()) {
    		Integer intSelectedPlaceId = Formatter.parseInt(selectedPlaceId);
			Integer oldPlace = null;
			if (header.getPlace() != null && header.getPlace().getId() != null && !header.getPlace().getId().equals(0))
				oldPlace = header.getPlace().getId();
			
    		if (oldPlace == null || !oldPlace.equals(intSelectedPlaceId)) {
        		header.setPlace(new Department(intSelectedPlaceId, null, null,null));

        		Integer intMdtaskId = Formatter.parseInt(mdtaskid);

        		SBeanLocator.singleton().getPlaceHistoryMapper().setPlaceHistory(intMdtaskId, oldPlace, intSelectedPlaceId,
        																	     Formatter.parseInt(currentUserId), new Date());
    		}
            //добавить смену КЗ по полю
            if(rq.getParameter("selectedCRMId")!=null && task.getContractors().size()>0){
                task.getContractors().get(0).getOrg().setId(rq.getParameter("selectedCRMId"));
                task.getContractors().get(0).setId(null);
                //записать историю изменения
                try {
                    TaskHelper.taskFacade().logMainBorrowerChanged(task.getId_task(), TaskHelper.getCurrentUser((HttpServletRequest) rq).getIdUser(),
                            task.getContractors().get(0).getOrg().getAccountid(), rq.getParameter("selectedCRMId"));
                } catch (Exception e) {
                    LOGGER.warn(e.getMessage(), e);
                }
            }
        }
    	//init department
    	String newDepartment = rq.getParameter("selectedDeartmentId");
    	if (mdtaskid != null && currentUserId != null && !currentUserId.isEmpty() && header.getStartDepartment() != null && newDepartment != null && !newDepartment.isEmpty()) {
    		Integer oldDepartmentId = header.getStartDepartment().getId();
    		Integer newDepartmentId = Formatter.parseInt(newDepartment);
    		
    		if (newDepartmentId != null && !newDepartmentId.equals(oldDepartmentId)) {
    			header.setStartDepartment(new Department(newDepartmentId, null, null ,null));

    			SBeanLocator.singleton().getDepartmentHistoryMapper().setDepartmentHistory(Formatter.parseInt(mdtaskid), oldDepartmentId, newDepartmentId,
    																					   Formatter.parseInt(currentUserId), new Date());
    		}
    	}
    }

    /*******************************************************************************************************
     *                                   Секция 'Контрагенты'                                              *
     *******************************************************************************************************/
    private void updateContragents(ServletRequest rq, Task task) throws NoSuchOrganizationException, RemoteException {
        if (rq.getParameterValues("IDCRM_Contractors") != null) {
        	if(rq.getParameterValues("IDCRM_Contractors").length>0 && task.getContractors()!=null
        			&& task.getContractors().size()>0 && !rq.getParameterValues("IDCRM_Contractors")[0].equals(task.getContractors().get(0).getOrg().getAccountid())
                    && (rq.getParameter("selectedPlaceId") == null || rq.getParameter("selectedPlaceId").isEmpty())){
        		LOGGER.info("Смена основного заемщика");
        		try {
        			TaskHelper.taskFacade().logMainBorrowerChanged(task.getId_task(), TaskHelper.getCurrentUser((HttpServletRequest) rq).getIdUser(),
        					task.getContractors().get(0).getOrg().getAccountid(), rq.getParameterValues("IDCRM_Contractors")[0]);
                    //поменять ещё и место проведения сделки
        	    	String contractorPlaceId = rq.getParameter("contractorPlaceId");
                    //Long depid = SBeanLocator.singleton().getDepartmentMapper().getDepId4kz(rq.getParameterValues("IDCRM_Contractors")[0]);
        	    	Long depid = Formatter.parseLong(contractorPlaceId);
                    if (depid != null) {
                    	
            			Integer oldPlace = null;
            			if (task.getHeader().getPlace() != null && task.getHeader().getPlace().getId() != null && !task.getHeader().getPlace().getId().equals(0))
            				oldPlace = task.getHeader().getPlace().getId();

            			if (oldPlace == null || oldPlace.intValue() != depid.intValue()) {
            				//записать место проведения сделки
            				task.getHeader().setPlace(new Department(depid.intValue(), null, null, null));
            				//записать историю изменения места проведения сделки
            				SBeanLocator.singleton().getPlaceHistoryMapper().setPlaceHistory(task.getId_task().intValue(),
            								oldPlace, depid.intValue(),
            								TaskHelper.getCurrentUser((HttpServletRequest)rq).getIdUser().intValue(), new Date());
            			}
                    }
				} catch (Exception e) {
					LOGGER.warn(e.getMessage(), e);
				}
        	}
            task.getContractors().clear();
            for (int i = 0; i < rq.getParameterValues("IDCRM_Contractors").length; i++) {
                ArrayList<ContractorType> types = new ArrayList<ContractorType>();
                if(i==0 && !SBeanLocator.singleton().mdTaskMapper().getById(task.getId_task()).isPipelineProcess())
                    types.add(new ContractorType(new Long(1)));//первый контрагент всегда зяемщик, но на форме поля нет
                String[] param = rq.getParameterValues("contractorType"+rq.getParameterValues("IDCRM_Contractors")[i]);
                if(param!=null){
                    for(String ctypeid : param){
                        types.add(new ContractorType(Formatter.parseLong(ctypeid)));
                    }
                }
                Long idr = null;
                try {
                	idr = Long.valueOf(rq.getParameterValues("IDCRM_Contractors_idr")[i]);
				} catch (Exception e) {
					LOGGER.warn(e.getMessage(), e);
				}
                String idCRM = rq.getParameterValues("IDCRM_Contractors")[i];
                if(rq.getParameter("selectedPlaceId") != null && !rq.getParameter("selectedPlaceId").isEmpty() && i == 0){//автоматически ставим из-за смены места проведения сделки
                	if (rq.getParameter("selectedCRMId") != null && !rq.getParameter("selectedCRMId").isEmpty()) {
                		idCRM = rq.getParameter("selectedCRMId");
                		idr = null;
                	}
                }
                task.getContractors().add(
                        new TaskContractor(new Organization(idCRM),
                                types,
                                idr,
                                rq.getParameterValues("ratingPKR")[i]));
                if(value(rq,"main_country") != null) {
                	task.getMain().setCountry(rq.getParameter("main_country").trim());
            }
           }
        }
    }

    /*******************************************************************************************************
     *                                   Секция 'Стоимостные условия'                                      *
     *******************************************************************************************************/
    private void updatePriceConditions(ServletRequest rq, Task task) {
        try{
              // если доступна для редактирования сама секция Стоимостные условия
              // если же недоступна, то не меняем параметры в task. И они же и будут отписаны в базу (в неизмененном виде).
              if(value(rq,"percentStavka_section") != null) {
                  // Процентная ставка (лимит)
                  if (task.isLimit() || task.isSubLimit()) {
                      percentStavka_limit(rq, task);
                  } else {
                      // Процентная ставка (Сделка) обновляется через JPA
                  }
              }
              if(value(rq,"commission_section") != null) {
                    // комиссии Лимит
                    if (task.isLimit() || task.isSubLimit()) {
                        commission_limit(rq, task);
                    }
                    else { // комиссии Сделка
                        commission_product(rq, task);
                    }
              }
              if(value(rq,"graph_section") != null) {
                    graph(rq, task);
              }
              if(value(rq,"fineList_section") != null) {
                    fineList(rq, task);
              }
        } catch(Exception e) {
            LOGGER.error(e.getMessage(), e);
            e.printStackTrace();
        }
    }

	private void fineList(ServletRequest rq, Task task) {
		// Штрафные санкции
		boolean isCanEditFund = false;
		try {
			isCanEditFund = TaskHelper.isCanEditFund((HttpServletRequest)rq);
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		if (!isCanEditFund) {
			task.getFineList().clear();
			if (rq.getParameterValues("Штрафные санкции") != null) {
				for (int i = 0; i < rq.getParameterValues("Штрафные санкции").length; i++) {
					if (!rq.getParameterValues("Штрафные санкции")[i].equals("")) {
						Fine f = new Fine(null, rq.getParameterValues("descPunitiveMeasure")[i], new Currency(
								numericValue(rq, "fine_currency", i)), Formatter.parseDouble(numericValue(rq, "fine_value", i)), rq
								.getParameterValues("Штрафные санкции")[i], null, null, rq
								.getParameterValues("fine_value_text")[i], Formatter.parseLong(rq
								.getParameterValues("fine_id_punitive_measure")[i]), Formatter.parseLong(rq
								.getParameterValues("fine_period")[i]), rq.getParameterValues("fine_periodtype")[i],
								rq.getParameterValues("fine_productrate")[i].equals("y"));
						task.getFineList().add(f);
					}
				}
			}
		}
	}

	private void graph(ServletRequest rq, Task task) {
		boolean isCanEditFund = false;
		try {
			isCanEditFund = TaskHelper.isCanEditFund((HttpServletRequest)rq);
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		// График погашения основного долга
		if (!task.isLimit() || task.isSubLimit()) {
			PrincipalPay oldPP = null;
			if (isCanEditFund)
				oldPP = task.getPrincipalPay();
			task.setPrincipalPay(null);
			if (value(rq, "graph_section") != null) {
				CRMRepayment pt = oldPP != null ? oldPP.getPeriodOrder() : new CRMRepayment(value(rq, "prncp Периодичность погашения ОД"));
				if (pt.getId() == null || pt.getId().equals("-1"))
					pt = null; // пустая строка выбрана из справочника.
				boolean firstPay = false;
				boolean isDepended = false;
				if (rq.getParameter("prncp Первый платеж в месяце выдачи") != null)
					firstPay = rq.getParameter("prncp Первый платеж в месяце выдачи").equals("y");
				if (rq.getParameter("prncp Сумма платежа зависит от задолженности "
						+ "на дату окончания срока использования") != null)
					isDepended = rq.getParameter("prncp Сумма платежа зависит от задолженности "
							+ "на дату окончания срока использования").equals("y");
				PrincipalPay pp = new PrincipalPay(null, pt,
						oldPP != null ? oldPP.getFirstPayDate() :
								Formatter.parseDate(value(rq, "prncp Дата первой оплаты ОД")),
						oldPP != null ? oldPP.getFinalPayDate() :
								Formatter.parseDate(value(rq, "prncp Дата окончательного погашения ОД")),
						oldPP != null ? oldPP.getAmount() :
								Formatter.parseDouble(value(rq, "prncp Сумма платежа ОД")), isDepended,
						oldPP != null ? oldPP.getDescription() :
								rq.getParameter("prncp Комментарии к графику погашения ОД"), firstPay,
						oldPP != null ? oldPP.getComment() : rq.getParameter("prncp Комментарии"),
						oldPP != null ? oldPP.getCurrency() : rq.getParameter("prncp Валюта"));
				if (!pp.hasErrors())
					task.setPrincipalPay(pp);
			}
		}

		// График платежей
		task.getPaymentScheduleList().clear();
		if (rq.getParameterValues("pmn Сумма платежа") != null) {
			// Сохраняем информацию только в случае, если выбрано значение "Произвольно" в списке
			// периодичности поашения платежей блока График погашения основного долга
			// и сам график погашения основного долга -- не пустой.
			for (int i = 0; i < rq.getParameterValues("pmn Сумма платежа").length; i++) {
				PaymentSchedule p = new PaymentSchedule(null, Formatter.parseDouble(numericValue(rq,
						"pmn Сумма платежа", i)), Formatter.parseDouble(numericValue(rq, "pmn_fondrate", i)),
						Formatter.parseDate(numericValue(rq, "pmn Период оплаты (с даты)", i)), Formatter
								.parseDate(numericValue(rq, "pmn Период оплаты (по дату)", i)),
						numericValue(rq, "pmn валюта", i), Formatter.parseLong(numericValue(rq, "pmnperiod", i)), numericValue(rq,
								"pmn_fondrate_manual", i).equals("y"), Formatter.parseLong(numericValue(rq, "pmntrid", i)),
								textValue(rq, "pmn_desc", i), textValue(rq, "pmn Порядок расчета", i));
				if (!p.hasErrors())
					task.getPaymentScheduleList().add(p);
			}
		}

		// График погашения процентов по кредиту
		if (!task.isLimit() || task.isSubLimit()) {
			InterestPay oldIP = null;
			if (isCanEditFund)
				oldIP = task.getInterestPay();
			task.setInterestPay(null);
			if (value(rq, "graph_section") != null) {
				boolean isFinalPay = false;
				if (rq.getParameter("int_pay Последняя оплата в дату фактического погашения задолженности") != null)
					isFinalPay = rq.getParameter(
							"int_pay Последняя оплата в дату фактического погашения задолженности").equals("y");
				Long numPayLong = oldIP != null ? oldIP.getNumDay() :Formatter.parseLong(value(rq, "int_pay Число уплаты процентов"));
				InterestPay ip = new InterestPay(null,
						oldIP != null ? oldIP.getFirstPayDate() :
								Formatter.parseDate(value(rq, "int_pay Дата первой оплаты процентов")),
						oldIP != null ? oldIP.getFinalPayDate() :
								Formatter.parseDate(value(rq, "int_pay Дата окончательного погашения процентов")),
						numPayLong, isFinalPay,
						oldIP != null ? oldIP.getDescription() :
								rq.getParameter("int_pay Комментарии к графику погашения процентов"),
						oldIP != null ? oldIP.getPay_int() :
								rq.getParameter("pay_int"),
						oldIP != null ? oldIP.getComment() :
								rq.getParameter("int_pay Комментарии"),
						oldIP != null ? oldIP.getFirstPayDateComment() :		
							rq.getParameter("FirstPayDateComment"));
				if (!ip.hasErrors())
					task.setInterestPay(ip);
			}
		}
	}

    private void commission_product(ServletRequest rq, Task task) {
    	if(rq.getParameter("commission_section") == null)
    		return;
        task.getCommissionDealList().clear();
        if (rq.getParameter("Значение Комиссии") == null)
            return;
        for (int i = 0; i < rq.getParameterValues("Значение Комиссии").length; i++) {
                PatternPaidPercentType type = new PatternPaidPercentType(numericValue(rq,"Порядок уплаты процентов Комиссии",i));
                if ("-1".equals(type.getId())) type = null;  // не выбран (пустая запись, короче говоря)
                CalcBase cb = new CalcBase(Formatter.parseLong(numericValue(rq,"База расчета Комиссии",i)));
                if (cb.getId().equals(-1L)) cb = null; // не выбран (пустая запись, короче говоря)
                ComissionSize cz = new ComissionSize(numericValue(rq,"Порядок расчета Комиссии", i));
                if ("-1".equals(cz.getId())) cz = null; // не выбран (пустая запись, короче говоря)
                CommissionDeal comission = new CommissionDeal(null,
                    rq.getParameterValues("Описание Комиссии")[i],
                    new Currency(numericValue(rq,"Валюта Комиссии",i)),
                    new CommissionType(numericValue(rq,"Наименование комиссии",i)), 
                    Formatter.parseDouble(numericValue(rq,"Значение Комиссии",i)),
                    type, cb, cz, 
                    rq.getParameterValues("Срок оплаты комиссии")[i]);
                if (!comission.hasErrors()) task.getCommissionDealList().add(comission);
        }
    }

    private void commission_limit(ServletRequest rq, Task task) {
        task.getCommissionList().clear();
        if (rq.getParameterValues("limit_Наименование комиссии") != null) {
         // комиссии Лимит
            for (int i = 0; i < rq.getParameterValues("limit_Наименование комиссии").length; i++) {
                if ((numericValue(rq,"limit_Наименование комиссии",i) != null)
                    && (!"-1".equals(numericValue(rq,"limit_Наименование комиссии",i)))) {        
                    PatternPaidPercentType com = null;
                        if ((numericValue(rq,"limit_Порядок уплаты процентов Комиссии",i) != null)
                            && (!"-1".equals(numericValue(rq,"limit_Порядок уплаты процентов Комиссии",i)))) {        
                            com = new PatternPaidPercentType(numericValue(rq,"limit_Порядок уплаты процентов Комиссии",i), null);
                        }
                    Commission comission = new Commission(null,
                        rq.getParameterValues("limit_Сумма - описание комиссии")[i],
                        new Currency(numericValue(rq,"limit_commiss_currency",i)), 
                        new CommissionType(numericValue(rq,"limit_Наименование комиссии",i)), 
                        Formatter.parseDouble(numericValue(rq,"limit_commiss_value",i)), null,
                        com);
                    if (!comission.hasErrors()) task.getCommissionList().add(comission);
                }
            }
        }
    }

    private void percentStavka_limit(ServletRequest rq, Task task) {
        task.setTaskProcent(null);
          TaskProcent tp = new TaskProcent(null, rq.getParameter("limit_Описание процентной ставки"),
                  null, null, null,
                  Formatter.parseDouble(value(rq,"limit_Премия за риск")),
                  null, true, null, null, null, null, null, rq.getParameter("sublimit_capitalPay"), rq.getParameter("limit_KTR"),rq.getParameter("priceIndCondition"),
                  rq.getParameter("pay_int"));
          // стандартные стоимостные условия
          tp.getStandardPriceConditionList().clear();
          if(rq.getParameter("Стандартные стоимостные условия") != null) {
              for (int i = 0; i < rq.getParameterValues("Стандартные стоимостные условия").length; i++) {
                  if ((numericValue(rq,"Стандартные стоимостные условия",i) != null)
                          && (!"-1".equals(numericValue(rq,"Стандартные стоимостные условия",i)))) {
                      StandardPriceCondition st = 
                          new StandardPriceCondition(Formatter.parseLong(numericValue(rq,"Стандартные стоимостные условия",i))); 
                      tp.getStandardPriceConditionList().add(st);
                  }
              }
          }
          if (!tp.hasErrors()) task.setTaskProcent(tp);
    }


    /*******************************************************************************************************
     *                                   Секция 'Обеспечение'                                              *
     *******************************************************************************************************/
    @SuppressWarnings("deprecation")
    private void updateSupply(ServletRequest rq, Task task) {
        try{
            TaskSupply supply = task.getSupply();
            if(rq.getParameter("supply_cfact")!=null)
                supply.setCfact(Formatter.parseDouble(rq.getParameter("supply_cfact")));

            //залог
              if (rq.getParameter("section_depositor") != null) {
                supply.getDeposit().clear();
                if (rq.getParameter("d_contractor")!=null){
                for (int i = 0; i < rq.getParameterValues("d_contractor").length; i++) {
                    Deposit d = new Deposit();
                    boolean isPerson = false;
                    if(!rq.getParameterValues("d_person")[i].equals("")) {
                        d.setPerson(new Person(Formatter.parseLong(rq.getParameterValues("d_person")[i])));
                        isPerson = true;
                    } else {
                    	d.setOrg(new Organization(rq.getParameterValues("d_contractor")[i]));
                    }
                    String idWho =isPerson ? rq.getParameterValues("d_person")[i] : rq.getParameterValues("d_contractor")[i];
                    d.setMain(rq.getParameter("d_main"+idWho)!=null
                            &&rq.getParameter("d_main"+idWho).equals("y"));
                    d.setPosled(rq.getParameter("d_posled"+idWho)!=null);
                    d.setDepositorFinStatus(
                            new DepositorFinStatus(Formatter.parseLong(rq.getParameterValues("d_DEPOSITOR_FIN_STATUS")[i]),""));
                    d.setLiquidityLevel(
                            new LiquidityLevel(Formatter.parseLong(rq.getParameterValues("d_LIQUIDITY_LEVEL")[i]),""));
                    d.setOppDescription(rq.getParameterValues("d_oppDescription")[i]);
                    d.setOrderDescription(rq.getParameterValues("d_orderDescription")[i]);
                    d.setZalogDescription(rq.getParameterValues("d_zalog_desc")[i]);
                    d.setType(rq.getParameterValues("d_type")[i]);
                    d.setOb(new SupplyType(Formatter.parseLong(rq.getParameterValues("d_SupplyType")[i])));
                    if(rq.getParameterValues("d_zalogMarket")[i]!=null
                            && Formatter.trimSpace(rq.getParameterValues("d_zalogMarket")[i]) != null
                            && Formatter.trimSpace(rq.getParameterValues("d_zalogMarket")[i]).length()>0)
                        d.setZalogMarket(Formatter.parseBigDecimal(rq.getParameterValues("d_zalogMarket")[i]));
                    if(rq.getParameterValues("d_zalogTerminate")[i]!=null
                            && Formatter.trimSpace(rq.getParameterValues("d_zalogTerminate")[i]) != null
                            && Formatter.trimSpace(rq.getParameterValues("d_zalogTerminate")[i]).length()>0)
                        d.setZalogTerminate(Formatter.parseBigDecimal(rq.getParameterValues("d_zalogTerminate")[i]));
                    if(rq.getParameterValues("d_zalog")[i]!=null
                            && Formatter.trimSpace(rq.getParameterValues("d_zalog")[i]) != null
                            && Formatter.trimSpace(rq.getParameterValues("d_zalog")[i]).length()>0)
                        d.setZalog(Formatter.parseBigDecimal(rq.getParameterValues("d_zalog")[i]));
                    if(rq.getParameterValues("d_discount")[i]!=null
                            && Formatter.trimSpace(rq.getParameterValues("d_discount")[i]) != null
                            && Formatter.trimSpace(rq.getParameterValues("d_discount")[i]).length()>0)
                        d.setDiscount(Formatter.parseBigDecimal(rq.getParameterValues("d_discount")[i]));
                    d.setIssuer(new Organization(rq.getParameterValues("d_issuer")[i]));
                    d.setZalogObject(new Ensuring(rq.getParameterValues("d_zalogObject")[i],"",""));
                    if(d.getLiquidityLevel().getId().longValue()==0) d.getLiquidityLevel().setId(null);
                    if(d.getDepositorFinStatus().getId().longValue()==0) d.getDepositorFinStatus().setId(null);
                    if(d.getOb().getId().longValue()==0) d.getOb().setId(null);
                    d.setCond(rq.getParameterValues("d_cond")[i]);
                    d.setMaxpart(Formatter.parseDouble(rq.getParameterValues("d_maxpart")[i]));
                    d.setWeight(Formatter.parseBigDecimal(rq.getParameterValues("d_weight")[i]));
                    d.setSupplyvalue(Formatter.parseDouble(rq.getParameterValues("dsupplyvalue")[i]));
                    d.setTodate(Formatter.parseDate(rq.getParameterValues("dtodate")[i]));
                    d.setPeriod((Formatter.parseLong(rq.getParameterValues("dperiod")[i])));
                    d.setPeriodDimension(rq.getParameterValues("dperiodDimension")[i]);
                    supply.getDeposit().add(d);
                }}
                supply.getDepositKeyValue().clear();
                for (int i = 1; i < rq.getParameterValues("d_key").length; i++) {
                    if(rq.getParameterValues("d_key")[i].equals("")&&rq.getParameterValues("d_value")[i].equals(""))
                        continue;
                    supply.getDepositKeyValue().put(
                            rq.getParameterValues("d_key")[i],
                            rq.getParameterValues("d_value")[i]);
                }
                fillSupplyCondition(rq, task,"d");
              }

            //гарант
          if (rq.getParameter("section_guarantee") != null) {
            supply.getGuarantee().clear();
            if (rq.getParameter("guarantee_contractor") != null)
            for (int i = 0; i < rq.getParameterValues("guarantee_contractor").length; i++) {
                    Guarantee guarantee = new Guarantee();
                    boolean isPerson = false;
                    if(!rq.getParameterValues("guarantee_person")[i].equals("")) {
                        guarantee.setPerson(new Person(Formatter.parseLong(rq.getParameterValues("guarantee_person")[i])));
                        isPerson = true;
                    }
                    String idWho =isPerson ? rq.getParameterValues("guarantee_person")[i] : rq.getParameterValues("guarantee_contractor")[i];
                    guarantee.setCurrency(new Currency(rq.getParameterValues("guarantee_cur")[i]));
                    guarantee.setDepositorFinStatus(
                            new DepositorFinStatus(Formatter.parseLong(rq.getParameterValues("guarantee_DEPOSITOR_FIN_STATUS")[i]),""));
                    guarantee.setDescription(rq.getParameterValues("guarantee_desc")[i]);
                    guarantee.setLiquidityLevel(new LiquidityLevel(Formatter.parseLong(rq.getParameterValues("guarantee_LIQUIDITY_LEVEL")[i]),""));
                    guarantee.setMain( rq.getParameter("guarantee_main"+idWho) !=null && rq.getParameter("guarantee_main"+idWho).equalsIgnoreCase("y") );
                    guarantee.setOb(new SupplyType(Formatter.parseLong(rq.getParameterValues("guarantee_type")[i])));
                    if(!rq.getParameterValues("guarantee_contractor")[i].equals(""))
                        guarantee.setOrg(new Organization(rq.getParameterValues("guarantee_contractor")[i]));
                    guarantee.setSum(Formatter.parseBigDecimal(rq.getParameterValues("guarantee_sum")[i]));
                    if(guarantee.getLiquidityLevel().getId().longValue()==0)
                        guarantee.getLiquidityLevel().setId(null);
                    if(guarantee.getDepositorFinStatus().getId().longValue()==0)
                        guarantee.getDepositorFinStatus().setId(null);
                    if(guarantee.getOb().getId().longValue()==0)
                        guarantee.getOb().setId(null);
                    guarantee.setPeriod(Formatter.parseLong(rq.getParameterValues("gperiod")[i]));
                    guarantee.setPeriodDimension(rq.getParameterValues("gperiodDimension")[i]);
                    guarantee.setFullSum(rq.getParameter("g_FullSum"+idWho)!=null);
                    guarantee.setSupplyvalue(Formatter.parseDouble(rq.getParameterValues("gsupplyvalue")[i]));
                    guarantee.setTodate(Formatter.parseDate(rq.getParameterValues("gtodate")[i]));
                    supply.getGuarantee().add(guarantee);
            }
            fillSupplyCondition(rq, task,"g");
          }
          //поручители
          if(rq.getParameter("section_warranty")!=null){
              supply.getWarranty().clear();
              if(rq.getParameter("w_guid")!=null)
              for (int i = 0; i < rq.getParameterValues("w_guid").length; i++) {
                  Warranty w = new Warranty();
                  String guid = rq.getParameterValues("w_guid")[i];
                  if(!rq.getParameterValues("w_person")[i].equals("")) {
                        w.setPerson(new Person(Formatter.parseLong(rq.getParameterValues("w_person")[i])));
                    }
                    w.setCurrency(new Currency(rq.getParameterValues("w_cur")[i]));
                    w.setDepositorFinStatus(
                            new DepositorFinStatus(Formatter.parseLong(rq.getParameterValues("w_DEPOSITOR_FIN_STATUS")[i]),""));
                    w.setDescription(rq.getParameterValues("w_desc")[i]);
                    w.setLiquidityLevel(new LiquidityLevel(Formatter.parseLong(rq.getParameterValues("w_LIQUIDITY_LEVEL")[i]),""));
                    w.setOb(new SupplyType(Formatter.parseLong(rq.getParameterValues("w_type")[i])));
                    if(!rq.getParameterValues("w_contractor")[i].equals(""))
                        w.setOrg(new Organization(rq.getParameterValues("w_contractor")[i]));
                    w.setAdd(rq.getParameterValues("w_add")[i]);
                    w.setFullSum(rq.getParameter("w_FullSum"+guid)!=null);

                    if (rq.getParameter("w_FullSum"+guid)==null) {
                        // не на всю сумму обязательств
                        w.setSum(Formatter.parseDouble(rq.getParameterValues("w_sum")[i]));
                    } else {
                        // на всю сумму обязательств. Пересчитаем данные.
                        BigDecimal exchangeRate = (rq.getParameter("exchangerate") != null) ? Formatter.parseBigDecimal(value(rq,"exchangerate")) : null;
                        String currencyCode = null;
                        BigDecimal sumInCurrency = null;
                        if (task.isOpportunity()) {
                            // если сделка
                            currencyCode = (rq.getParameter("Валюта сделки") != null) ? value(rq,"Валюта сделки") : null;
                            sumInCurrency = (rq.getParameter("Сумма сделки") != null) ? Formatter.parseBigDecimal(value(rq,"Сумма сделки")) : null;
                        } else {
                            // если лимит \ сублимит
                            currencyCode = (rq.getParameter("Валюта") != null) ? rq.getParameter("Валюта") : null;
                            sumInCurrency = (rq.getParameter("Сумма лимита") != null) ?  Formatter.parseBigDecimal(value(rq,"Сумма лимита")) : null;
                        }
                        w.setSum(Formatter.convertSumToRUR(currencyCode, sumInCurrency, exchangeRate));
                    }

                    w.setKind(rq.getParameterValues("w_kind")[i]);
                    w.setMain(rq.getParameter("w_main"+guid)!=null&&rq.getParameter("w_main"+guid).equals("y"));
                    if(rq.getParameterValues("w_resp"+guid)!=null)
                        for(String s : rq.getParameterValues("w_resp"+guid)){
                            w.getResponsibility().add(s);
                        }
                    if(w.getLiquidityLevel().getId().longValue()==0)
                        w.getLiquidityLevel().setId(null);
                    if(w.getDepositorFinStatus().getId().longValue()==0)
                        w.getDepositorFinStatus().setId(null);
                    if(w.getOb().getId().longValue()==0)
                        w.getOb().setId(null);
                    //Если поле «На всю сумму обязательств» заполнено, то автоматически вносить значение «все обязательства». VTBSPO-755
                    if(w.isFullSum()&&!w.getResponsibility().contains("a"))
                        w.getResponsibility().add("a");
                    w.setSupplyvalue(Formatter.parseDouble(rq.getParameterValues("wsupplyvalue")[i]));
                    w.setTodate(Formatter.parseDate(rq.getParameterValues("wtodate")[i]));
                    w.setPeriod(Formatter.parseLong(rq.getParameterValues("wperiod")[i]));
                    w.setPeriodDimension(rq.getParameterValues("wperiodDimension")[i]);

                    // прочитаем штрафные санкции для поручителя
                    savePunitiveMeasureForWarranty(rq, w,guid);

                    supply.getWarranty().add(w);
              }
              fillSupplyCondition(rq, task,"w");
          }
        }catch(Exception e){
            LOGGER.error(e.getMessage(), e);
            e.printStackTrace();
        }
    }

	public void fillSupplyCondition(ServletRequest rq, Task task, String section_code) {
		ArrayList<OtherCondition> list = new ArrayList<OtherCondition>();
		for(OtherCondition c : task.getOtherCondition())
			if (c.getSupplyCode()==null || !c.getSupplyCode().equals(section_code))
				list.add(c);
		task.setOtherCondition(list);
		if (rq.getParameter(section_code + "_special_condition") != null)
			for(String special_condition : rq.getParameterValues(section_code+"_special_condition"))
				task.getOtherCondition().add(new OtherCondition(4L, null, special_condition, null, section_code));
	}

    /**
     * Сохраним штрафные санкции для поручителя
     *
     */
    private void savePunitiveMeasureForWarranty(ServletRequest rq, Warranty w, String guid) {
        w.getFineList().clear();
        if (rq.getParameterValues("Штрафные санкции" + guid) != null) {
            for (int i = 0; i < rq.getParameterValues("Штрафные санкции" + guid).length; i++) {
                if (!rq.getParameterValues("Штрафные санкции" + guid)[i].equals("")) {
                    Long idPerson = (w.getPerson()!=null&&w.getPerson().getId()!=null&&!w.getPerson().getId().equals(0l))?w.getPerson().getId():null;
                    String orgid = w.getOrg()!=null?w.getOrg().getId():null;
                    Fine f = new Fine(null, rq.getParameterValues("descPunitiveMeasure" + guid)[i], 
                            new Currency(numericValue(rq, "fine_currency" + guid, i)), Formatter.parseDouble(numericValue(rq, "fine_value" + guid,i)),
                        rq.getParameterValues("Штрафные санкции" + guid)[i], idPerson, orgid,rq.getParameterValues("fine_value_text" + guid)[i],
                        Formatter.parseLong(rq.getParameterValues("fine_id_punitive_measure" + guid)[i]),
                        Formatter.parseLong(rq.getParameterValues("fine_period" + guid)[i]),rq.getParameterValues("fine_periodtype" + guid)[i],
                        rq.getParameterValues("fine_productrate" + guid)[i].equals("y"));
                    w.getFineList().add(f);
                }
            }
        }
    }


    /*******************************************************************************************************
     *                                   Секция 'Условия'                                                  *
     *******************************************************************************************************/
    private void updateConditions(ServletRequest rq, Task task) {
    	if (task.isOpportunity() && (rq.getParameter("changedConditions")!=null)) {
    		task.getMain().setChangedConditions(rq.getParameter("changedConditions"));
    	}
    	if (rq.getParameterValues("otherTemplateHiddenInput") != null) {
            ArrayList<OtherCondition> list = new ArrayList<OtherCondition>();
            for(OtherCondition c : task.getOtherCondition())
            	if (c.getSupplyCode()!=null)
            		list.add(c);
            task.setOtherCondition(list);
            for(ru.md.dict.dbobjects.ConditionTypeJPA type : TaskHelper.dict().findConditionTypes()) {
            	if (rq.getParameter("condition"+type.getId_type())!=null)
            		for (int i = 0; i < rq.getParameterValues("condition"+type.getId_type()).length; i++) {
            			if (!rq.getParameterValues("condition"+type.getId_type())[i].equals("")) {
                            LOGGER.info("save condition "+rq.getParameterValues("condition"+type.getId_type())[i].length()+
                                " bytes " + rq.getParameterValues("condition"+type.getId_type())[i]);
            				String idCond = rq.getParameterValues("idCond_"+type.getId_type())[i];
            				task.getOtherCondition().add(
            						new OtherCondition(Long.valueOf(type.getId_type()), Formatter.parseLong(idCond),
            								rq.getParameterValues("condition"+type.getId_type())[i],
            								Formatter.parseLong(rq.getParameterValues("condition"+type.getId_type()+"id")[i]),null));
            			}
            		}
            }
        }
    	//END OtherCondition
        if (rq.getParameterValues("earlyPaymentTemplate") != null) {
            task.getEarlyPaymentList().clear();
            if (rq.getParameter("Условие досрочного погашения")!=null){
            for (int i = 0;i < rq.getParameterValues("Условие досрочного погашения").length; i++) {
                    String condition=rq.getParameterValues("Условие досрочного погашения")[i];
                    task.getEarlyPaymentList().add(
                            new EarlyPayment(rq.getParameterValues("Разрешение досроч.погаш.")[i], null,
                                    rq.getParameterValues("Взимание комиссии досроч.погаш.")[i],
                                    ((condition==null)||(condition.equals("null")))
                                    ?"":rq.getParameterValues("Условие досрочного погашения")[i], 
                                    (task.isOpportunity())?rq.getParameterValues("Тип периода")[i]:null, 
                                    (task.isOpportunity())?Formatter.parseLong(rq.getParameterValues("Количество дней до предупреждения банка")[i]):null));
            }}
        }
        if (rq.getParameterValues("SpecialConditionBody") != null) {
            task.getSpecialCondition().clear();
            for (int i = 1/*строка 0 - скрытая. Нужна для javascript добавления*/;
                i < rq.getParameterValues("SpecialConditionBody").length; i++) {
                    task.getSpecialCondition().add(
                            new SpecialCondition(rq.getParameterValues("SpecialConditionType")[i],
                                    rq.getParameterValues("SpecialConditionBody")[i]));
            }
        }
    }

    /*******************************************************************************************************
     *                                   Секция 'Стоп-Факторы'                                             *
     *******************************************************************************************************/
    private void updateStopFactors(ServletRequest rq, Task task,
            CompendiumSpoActionProcessor spoProcessor) {
     // Доступна ли для редактирования секкция стоп-факторов клиентских
     if(value(rq,"Секция_stop_factors_Client") != null) {
        if (rq.getParameterValues("stopfactorsClient") != null) {
            task.getTaskClientStopFactorList().clear();
            for (int i = 0; i < rq.getParameterValues("stopfactorsClient").length; i++) {
                if (!"hidden".equals(numericValue(rq,"stopfactorsClient", i))) {
                    StopFactor vo = new StopFactor(numericValue(rq, "stopfactorsClient", i));  
                    StopFactor found = spoProcessor.findStopFactor(vo);
                    TaskStopFactor ts = new TaskStopFactor(true, found);
                    if (!ts.hasErrors()) task.getTaskClientStopFactorList().add(ts);
                }
            }
        }
     }
     // Доступна ли для редактирования секция стоп-факторов секьюрити
     if(value(rq,"Секция_stop_factors_Security") != null) {
         if (rq.getParameterValues("stopfactorsSecurity") != null) {
             task.getTaskSecurityStopFactorList().clear();
             for (int i = 0; i < rq.getParameterValues("stopfactorsSecurity").length; i++) {
                 if (!"hidden".equals(numericValue(rq,"stopfactorsSecurity", i))) {
                     StopFactor vo = new StopFactor(numericValue(rq, "stopfactorsSecurity", i));  
                     StopFactor found = spoProcessor.findStopFactor(vo);
                     TaskStopFactor ts = new TaskStopFactor(true, found);
                     if (!ts.hasErrors()) task.getTaskSecurityStopFactorList().add(ts);
                 }
             }
         }
     }
     if(value(rq, "Секция_stop_factors_3") != null) {
        if (rq.getParameterValues("stopfactors3") != null) {
            task.getTaskStopFactor3List().clear();
            for (int i = 0; i < rq.getParameterValues("stopfactors3").length; i++) {
                if (!"hidden".equals(numericValue(rq,"stopfactors3", i))) {
                    StopFactor vo = new StopFactor(numericValue(rq, "stopfactors3", i));  
                    StopFactor found = spoProcessor.findStopFactor(vo);
                    TaskStopFactor ts = new TaskStopFactor(true, found);
                    if (!ts.hasErrors()) task.getTaskStopFactor3List().add(ts);
                }
            }
        }
     }
    }

    /**
     * Возвращает {@link String строку} "очищенную" от НТМL форматирования
     * @param comment исходная строка
     * @return {@link String строка} "очищенная" от НТМL форматирования
     */
    private String clearHtmlTags(String src) {
    	if (src == null || src.isEmpty())
    		return null;
		String result = src.replace("![CDATA[", "")
				           .replace("]]", "")
				           .replace("</p>", "\n")
				           .replace("<br>", "\n")
				           .replace("</h1>", "\n")
				           .replace("</h2>", "\n")
				           .replace("</h3>", "\n")
				           .replace("</h4>", "\n")
				           .replace("</h5>", "\n")
				           .replace("</h6>", "\n")
				           .replace("</td>", " ")
				           .replace("</tr>", "\n")
				           .replace("&amp;", "")
				           .replace("&nbsp;", " ")
				           .replace("&sect;", "")
				           .replace("&mdash;", "-")
				           .replace("&laquo;", "\"")
				           .replace("&raquo;", "\"")
				           .replace("&reg;", "(R)")
				           .replace("&para;", "\n")
				           .replace("&copy;", "(C)")
				           .replace("&quot;", "\"")
				           //.replaceAll("<(?!p)[\\/\\!]*?[^<>]*?>", "")
				           .replaceAll("(\\<(/?[^>]+)>)", "")
				           .replaceAll("([\\r\\n])[\\s]+", "");
    	return result;
    }

    /**
     * Возвращает {@link String строку}, учитывая то, как сохраняет пустую строку tiny MCE
     * @param comment исходная строка
     * @return {@link String строка} учитывая то, как сохраняет пустую строку tiny MCE
     */
    public String clearEmptyComment(String comment) {
        if (comment == null || comment.trim().isEmpty())
        	return null;
        String trimmedStr = comment.replaceAll("<br .?mce_bogus=\"1\">", "").replaceAll("<.?p>", "").replaceAll("<.?br>", "").replaceAll(" ", "")
                                   .replaceAll("&nbsp;", "").replaceAll("<.?html.?>", "").trim();
            if (trimmedStr.isEmpty())
                return null;
        return comment;
    }
    
    /*******************************************************************************************************
     *                                   Секция 'Комментарии'                                              *
     *******************************************************************************************************/
    private void updateComments(ServletRequest rq, Task task) {
        if (rq.getParameter("comment") != null) {
        	String comment = clearEmptyComment(rq.getParameter("comment"));
            task.getComment().add(new Comment(null, clearHtmlTags(comment), comment,
                    new com.vtb.domain.Operator(Formatter.parseInt(rq.getParameter("comment_author")),""),
                    Formatter.parseInt(rq.getParameter("comment_stage")),
                    new java.sql.Timestamp(new java.util.Date().getTime())));
        }
    }

    /*******************************************************************************************************
     *                                   Секция 'Передача на КК'                                           *
     *******************************************************************************************************/
    private void update4CC(ServletRequest rq, Task task) throws ParseException {
        if(rq.getParameter("SPOStatusReturn")!=null)
            task.getTaskStatusReturn().setStatusReturn(
                    new ru.masterdm.compendium.domain.crm.StatusReturn(rq.getParameter("SPOStatusReturn"),null,null));
        if (rq.getParameter("assigneeAuthority") != null)
            task.setAuthorizedBody(Formatter.parseInt(rq.getParameter("assigneeAuthority")));
        if (rq.getParameterValues("Согласование подразделение") != null) {
            task.getDepartmentAgreements().clear();
            for(int i=0;i<rq.getParameterValues("Согласование подразделение").length;i++){
                if(!rq.getParameterValues("Согласование подразделение")[i].equals("")){
                    task.getDepartmentAgreements().add(
                            new DepartmentAgreement(rq.getParameterValues("Согласование подразделение")[i],
                                    rq.getParameterValues("Согласование Замечания")[i],
                                    rq.getParameterValues("Согласование Комментарий")[i]));
                }
            }
        }
        //временные параметры кредитного комитета. Позже они будут браться из самого КК
        if (rq.getParameter("Решение по кредитной сделке") != null)
            task.getTemp().setResolution(rq.getParameter("Решение по кредитной сделке"));
        try{
            if ((rq.getParameter("Дата принятия решения") != null)
                    &&(!rq.getParameter("Дата принятия решения").equals("")))
                task.getTemp().setMeetingDate(Formatter.parseDate(rq.getParameter("Дата принятия решения")));
            if ((rq.getParameter("Дата заседания Комитета") != null)
                &&(!rq.getParameter("Дата заседания Комитета").equals("")))
                task.getTemp().setPlanMeetingDate(Formatter.parseDate(rq.getParameter("Дата заседания Комитета")));
        } catch (Exception e) {
            LOGGER.warn("wrong date format: " + e.getMessage(), e);
            e.printStackTrace();
        }

        //преамбула и прочее
        if (rq.getParameter("CcPreambulo") != null)
            task.setCcPreambulo(rq.getParameter("CcPreambulo"));
        if (rq.getParameter("CcQuestionType") != null)
            task.setCcQuestionType(new QuestionType(Formatter.parseInt(rq.getParameter("CcQuestionType"))));
        if (rq.getParameter("creditDecisionProject") != null) {
        	if (!rq.getParameter("creditDecisionProject").equals(""))
        		task.setCreditDecisionProject(rq.getParameter("creditDecisionProject"));
        	else task.setCreditDecisionProject(null);
        }
        if (rq.getParameter("section_conclusion") != null){
        	LOGGER.info("Уполномоченный орган=" + rq.getParameter("Уполномоченный орган"));
            task.getTemp().setPlanMeetingDate(Formatter.parseDate(rq.getParameter("planmeetingDate")));
            String coll = rq.getParameter("Коллегиальный");
            try{
                PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
                pupFacadeLocal.updatePUPAttribute(task.getId_pup_process(),"Коллегиальный",coll);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

    }

    private String findStatusReturnId(String name) {
        CompendiumCrmActionProcessor compenduim = (CompendiumCrmActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumCrm");
        for (ru.masterdm.compendium.domain.crm.StatusReturn statusReturn : compenduim.findStatusReturn("0"))
            if(statusReturn.getDescription().equals(name))
                return statusReturn.getId();
        return null;
    }
    /*******************************************************************************************************
     *                                   Секция 'Статус возврата'                                          *
     *******************************************************************************************************/
    private void updateReturnStatus(ServletRequest rq, Task task) {
        //отказ или одобрить
        if(rq.getParameter("StatusReturn")!=null || rq.getParameter("nameStatusReturn")!=null) {
            String id = rq.getParameter("StatusReturn");
            if (id==null){
                id = findStatusReturnId(rq.getParameter("nameStatusReturn"));
                if (task != null && task.getId_pup_process() != null)
                    TaskHelper.pup().updatePUPAttribute(task.getId_pup_process(), "Статус", rq.getParameter("nameStatusReturn"));
            }
            task.getTaskStatusReturn().setStatusReturn(new ru.masterdm.compendium.domain.crm.StatusReturn(id,null,null));
            task.getTaskStatusReturn().setIdUser(wsc.getIdUser());
            //уведомить crm
            try {
                CrmFacadeLocal crmFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(CrmFacadeLocal.class);
                if (task.isLimit()){
                    crmFacadeLocal.exportLimit(task);
                } else {
                    crmFacadeLocal.exportProduct(task);
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        if(rq.getParameter("refuse_date")!=null)
            task.getTaskStatusReturn().setDateReturn(Formatter.parseDate(rq.getParameter("refuse_date")));
        if(rq.getParameter("StatusReturnText")!=null)
            task.getTaskStatusReturn().setStatusReturnText(rq.getParameter("StatusReturnText"));
    }

    /*******************************************************************************************************
     *                                   Секция 'Транши'                                                   *
     *******************************************************************************************************/
    private void updateTranches(ServletRequest rq, Task task) {
        //trance
        if(rq.getParameter("Секция_основные параметры") == null) return;
        if(rq.getParameter("trance_comment")!=null)
            task.setTranceComment(rq.getParameter("trance_comment"));
        List<Withdraw> taskwithdraws = new ArrayList<Withdraw>();
        if(rq.getParameter("withdraw_sum") != null)
	        for(int j=0;j<rq.getParameterValues("withdraw_sum").length;j++){
	        	if(rq.getParameterValues("withdraw_trance_id")[j].isEmpty())
	        		taskwithdraws.add(new Withdraw(task.getId_task(),null,
	        				ru.masterdm.spo.utils.Formatter.parseDouble(rq.getParameterValues("withdraw_sum")[j]),rq.getParameterValues("withdraw_cur")[j],
	        				ru.masterdm.spo.utils.Formatter.parseDate(rq.getParameterValues("withdraw_from")[j]),
	        				ru.masterdm.spo.utils.Formatter.parseDate(rq.getParameterValues("withdraw_to")[j]),Formatter.parseLong(rq.getParameterValues("withdraw_month")[j]),
	        				Formatter.parseLong(rq.getParameterValues("withdraw_quarter")[j]),Formatter.parseLong(rq.getParameterValues("withdraw_year")[j]),
	        				Formatter.parseLong(rq.getParameterValues("withdraw_hyear")[j]), rq.getParameterValues("withdraw_sumScope")[j], 
	        				rq.getParameterValues("withdraw_periodDimensionFrom")[j], rq.getParameterValues("withdraw_periodDimensionBefore")[j],
	        				Formatter.parseLong(rq.getParameterValues("withdraw_fromPeriod")[j]), Formatter.parseLong(rq.getParameterValues("withdraw_beforePeriod")[j])));
	        	
	        }
        task.setWithdraws(taskwithdraws);

        task.getTranceList().clear();
        if(rq.getParameter("trance_id") == null) return;
        for(int i=0;i<rq.getParameterValues("trance_id").length;i++){
            Long tranceid = null;
            String trance_id = rq.getParameterValues("trance_id")[i];
            if(!trance_id.startsWith("new")){
                tranceid=Long.valueOf(trance_id);
            }
            Trance trance = new Trance(tranceid, null, null, null,null);
            List<Withdraw> withdraws = new ArrayList<Withdraw>();
            if(rq.getParameter("withdraw_sum") != null)
	            for(int j=0;j<rq.getParameterValues("withdraw_sum").length;j++){
	            	if(trance_id!=null && trance_id.equals(rq.getParameterValues("withdraw_trance_id")[j]))
	            		withdraws.add(new Withdraw(task.getId_task(),tranceid,
	            			ru.masterdm.spo.utils.Formatter.parseDouble(rq.getParameterValues("withdraw_sum")[j]),rq.getParameterValues("withdraw_cur")[j],
	            			ru.masterdm.spo.utils.Formatter.parseDate(rq.getParameterValues("withdraw_from")[j]),
	            			ru.masterdm.spo.utils.Formatter.parseDate(rq.getParameterValues("withdraw_to")[j]),Formatter.parseLong(rq.getParameterValues("withdraw_month")[j]),
	            			Formatter.parseLong(rq.getParameterValues("withdraw_quarter")[j]),Formatter.parseLong(rq.getParameterValues("withdraw_year")[j]),
	            			Formatter.parseLong(rq.getParameterValues("withdraw_hyear")[j]), rq.getParameterValues("withdraw_sumScope")[j],
	            			rq.getParameterValues("withdraw_periodDimensionFrom")[j], rq.getParameterValues("withdraw_periodDimensionBefore")[j],
	        				Formatter.parseLong(rq.getParameterValues("withdraw_fromPeriod")[j]), Formatter.parseLong(rq.getParameterValues("withdraw_beforePeriod")[j])));
	            }
            trance.setWithdraws(withdraws);
            task.getTranceList().add(trance);
        }

    }

    /*******************************************************************************************************
     *                          Секция 'Заключения экспертных подразделений'                               *
     *******************************************************************************************************/
    @SuppressWarnings("unchecked")
    private void updateExpertConclusions(ServletRequest rq, Task task) {
        //blobs
        Enumeration en = rq.getParameterNames();
        while (en.hasMoreElements()) {
            String paramName = (String) en.nextElement();
            if(paramName.startsWith("attributeL_")){
                //пришло обновление заключения
                //удаляем старое заключение, если оно есть
                for(ExtendText et:task.getExtendTexts()){
                    if(et.getDescriptionWithPrefix().equalsIgnoreCase(paramName.substring("attribute".length()))){
                        task.getExtendTexts().remove(et);
                        break;
                    }
                }
                ExtendText et = new ExtendText();
                et.setContext(rq.getParameter(paramName));
                et.setDescription(paramName.substring("attribute".length()));
                task.getExtendTexts().add(et);
            }
        }
    }

    /**
     * Checks whether the parameter exist and not equals to empty string
     * If so, returns its value, replacing all whitespaces.
     * @param request
     * @param paramName
     * @return true if conditions are met; false otherwise.
     */
    private String value(ServletRequest request, String paramName) {
        if (request.getParameter(paramName) != null
               && (!request.getParameter(paramName).equals(""))) {
            String res = request.getParameter(paramName).replaceAll(" ", "");
            if (res.equals("")) return null;
            else return res;
        }
        else return null;
    }

    /**
     * Checks whether the parameter exist and not equals to empty string (if there exists an array of params)
     * If so, returns its value, replacing all whitespaces.
     * Otherwise returns null.
     * @param request
     * @param paramName
     * @param i index in array
     * @return true if conditions are met; false otherwise.
     */
    private String numericValue(ServletRequest request, String paramName, int idx) {
        try {
        	String value = textValue(request, paramName, idx);
        	if (value != null) {
        		value = value.replaceAll(" ", "");
        	}
            return value;
        } catch (Exception e) {
           return null;
       }
    }

    /**
     * Checks whether the parameter exist and not equals to empty string (if there exists an array of params)
     * If so, returns its value.
     * Otherwise returns null. 
     * @param request
     * @param paramName
     * @param i index in array
     * @return true if conditions are met; false otherwise.
     */
    private String textValue(ServletRequest request, String paramName, int idx) {
        try {
            if (request.getParameterValues(paramName)[idx] != null
                   && (!request.getParameterValues(paramName)[idx].equals(""))) {
                String res = request.getParameterValues(paramName)[idx];  
                if (res.equals("")) return null;
                else return res;
            }
            else return null;
        } catch (Exception e) {
           return null;
       }
    }
}
