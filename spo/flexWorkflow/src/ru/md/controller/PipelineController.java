package ru.md.controller;

import java.util.*;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vtb.util.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ru.masterdm.integration.ServiceFactory;
import ru.masterdm.integration.rating.RatingService;
import ru.masterdm.integration.rating.ws.CalcHistoryInput;
import ru.masterdm.integration.rating.ws.CalcHistoryOutput;
import ru.masterdm.spo.service.IDashboardService;
import ru.masterdm.spo.service.IPriceService;
import ru.masterdm.spo.utils.SBeanLocator;
import ru.md.domain.MdTask;
import ru.md.domain.Org;
import ru.md.domain.Pipeline;
import ru.md.domain.TaskKz;
import ru.md.domain.dict.FlowInvestment;
import ru.md.domain.dict.PipelineCoeffType;
import ru.md.domain.dict.PipelineLaw;
import ru.md.domain.dict.PipelineStatus;
import ru.md.domain.dict.PipelineSupply;
import ru.md.domain.dict.Priority;
import ru.md.helper.TaskHelper;
import ru.md.persistence.*;
import ru.md.pup.dbobjects.UserJPA;
import ru.md.spo.dbobjects.FactPercentJPA;
import ru.md.spo.dbobjects.PipelineJPA;
import ru.md.spo.dbobjects.ProjectTeamJPA;
import ru.md.spo.dbobjects.TaskJPA;
import ru.md.spo.ejb.DictionaryFacadeLocal;
import ru.md.spo.ejb.PupFacadeLocal;
import ru.md.spo.ejb.TaskFacadeLocal;

import com.vtb.exception.FactoryException;
import com.vtb.util.Formatter;
 
/**
 * Pipeline @ KO
 */
@Controller
public class PipelineController {
	private static final Logger LOGGER = LoggerFactory.getLogger(PipelineController.class.getName());
	
	@Autowired
	private MdTaskMapper mdTaskMapper;
	@Autowired
    private ProductMapper productMapper;
	@Autowired
    private CurrencyMapper currencyMapper;
	@Autowired
    private CompendiumMapper compendiumMapper;
	@Autowired
    private UserMapper userMapper;
	@Autowired
	private IDashboardService dashboardService;
	@Autowired
	private DashboardMapper dashboardMapper;
	@Autowired
	private IPriceService priceService;

	@RequestMapping(value = "/frame/pm_section.html")
	public String frame_pm(@ModelAttribute("model") ModelMap model,
						@RequestParam("mdtaskid") Long mdTaskId,
						HttpServletRequest request, HttpServletResponse response) throws Exception {
						
		response.addHeader("Pragma", "no-cache");
        response.addHeader("Expires", "-1");
        response.addHeader("Cache-control", "no-cache");
        
        PupFacadeLocal pupEJB = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        boolean readonly = pupEJB.isPipelineReadonly(mdTaskId);

        MdTask mdTask = mdTaskMapper.getPipelineWithinMdTask(mdTaskId);
        if (mdTask == null)
            throw new RuntimeException("mdTask by id '" + mdTaskId + "' is null");
        
        if (mdTask.getPipeline() == null)
            mdTask.setPipeline(new Pipeline());
        
        Org mainOrganization = mdTask.getMainOrganization(); 
        if (mainOrganization != null && !StringUtils.isEmpty(mainOrganization.getId())) 
            mainOrganization.setBranch(getOrganizationBranch(mainOrganization.getId()));

		for (TaskKz kz : SBeanLocator.singleton().compendium().getTaskKzByMdtask(mdTaskId))
			if (kz.isMainOrg())
				model.addAttribute("ek_name", SBeanLocator.singleton().getDictService().getEkNameByOrgId(kz.getKzid()));
        model.addAttribute("products", productMapper.getProducts());
        model.addAttribute("currencies", currencyMapper.getCurrencies());
        model.addAttribute("baseRates", compendiumMapper.getBaseRates());
        model.addAttribute("tradeFinanceList", compendiumMapper.getTradeFinance());
        model.addAttribute("supplies", PipelineSupply.values());
        model.addAttribute("crossSellTypes", compendiumMapper.getCrossSellTypes());
        model.addAttribute("statuses", compendiumMapper.getPipelineStage());
        model.addAttribute("laws", PipelineLaw.values());
        model.addAttribute("financingObjectives", compendiumMapper.getFinancingObjectives());
        model.addAttribute("productTypeFactors", compendiumMapper.getPipelineCoeffs(PipelineCoeffType.PRODUCT_TYPE_FACTOR.getValue()));
        model.addAttribute("periodFactors", compendiumMapper.getPipelineCoeffs(PipelineCoeffType.PERIOD_FACTOR.getValue()));
        model.addAttribute("fundCompanies", compendiumMapper.getFundCompanies());
        model.addAttribute("tradingDesks", compendiumMapper.getTradingDesks());
        model.addAttribute("flowInvestmentValues", CollectionUtils.set("Импорт", "Экспорт"));
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("fixingRateSpreads", compendiumMapper.getFixingRateSpreads(mdTask.getPeriodInDays(), mdTask.getCurrency()));
		model.addAttribute("markup", getMarkup(Long.valueOf(mdTaskId)));
		model.addAttribute("managementFee", priceService.getComissionZaVidSum(mdTaskId));
        if (mdTask.getEarlyRepaymentBan() != null && mdTask.getEarlyRepaymentBan())
            model.addAttribute("earlyRepaymentSpreads", compendiumMapper.moratoriumRateSpreads(mdTask.getPeriodInDays(),
                                                                                               mdTask.getEarlyRepaymentBanPeriod(),
                                                                                               mdTask.getCurrency()));
        else
            model.addAttribute("earlyRepaymentSpreads", compendiumMapper.earlyRepaymentSpreads(mdTask.getPeriodInDays(), mdTask.getCurrency()));
        
        model.addAttribute("readonly", readonly);
        model.addAttribute("mdTask", mdTask);
        model.addAttribute("lastUpdateDate", dashboardMapper.getLastUpdateDate(mdTaskId));

		//стоимостные условия
		TaskFacadeLocal taskEJB = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
		TaskJPA taskJPA = taskEJB.getTask(mdTaskId);
		if(taskJPA.getPeriods().size()>1){//в сделке есть периоды
			FactPercentJPA first = taskJPA.getPeriods().get(0);
			model.addAttribute("mdtaskBaseRatesMessage","<span class='mdtaskBaseRatesMessageRed'>!!!</span> Значения индикативных ставок из периода № 1 с " + Formatter.format(first.getStart_date()) +
					" по " + Formatter.format(first.getEnd_date()) + ". Отредактировать данные можно в секции Стоимостные условия - Процентная ставка");
			if(first.isInterestRateDerivative())
				model.addAttribute("mdtaskBaseRatesDisplay", taskJPA.getIndratesDisplay(first.getId()));
			model.addAttribute("interestRateFixed",first.isInterestRateFixed());
			model.addAttribute("interestRateDerivative",first.isInterestRateDerivative());
			model.addAttribute("fixedRateDisplay",first.getFixedRateDisplay());
		} else { //в сделке нет периодов
			model.addAttribute("mdtaskBaseRatesMessage","<span class='mdtaskBaseRatesMessageRed'>!!!</span> Значения индикативных ставок по сделке в целом. Отредактировать данные можно в секции Стоимостные условия - Процентная ставка");
			if(mdTask.isInterestRateDerivative()){//если ставка плавающая, то нам ещё нужна базовая ставка (индикативная)
				model.addAttribute("mdtaskBaseRatesDisplay", taskJPA.getIndratesDisplay());
			}
			model.addAttribute("interestRateFixed",mdTask.isInterestRateFixed());
			model.addAttribute("interestRateDerivative",mdTask.isInterestRateDerivative());
			model.addAttribute("fixedRateDisplay",mdTask.getFixedRateDisplay());
		}
		//состав проектной команды
        HashMap<String, Object> pt = addProjectTeam(taskJPA);
        for(String key : pt.keySet())
            model.addAttribute(key, pt.get(key));

		return "pm_section";
	}

	private HashMap<String, Object> addProjectTeam(TaskJPA taskJPA) {
        HashMap<String, Object> res = new HashMap<String, Object>();
		ArrayList<String> sales = new ArrayList<String>();
		ArrayList<String> credit = new ArrayList<String>();
		ArrayList<String> gss = new ArrayList<String>();
		for (ProjectTeamJPA team : taskJPA.getProjectTeam("p")) {
			UserJPA user = team.getUser();
			List<String> roles = userMapper.userRoles(user.getIdUser(),taskJPA.getIdTypeProcess());
            if(roles.contains("Продуктовый менеджер"))
                sales.add(user.getFullName());
            if(roles.contains("Кредитный аналитик") &&
					TaskHelper.pup().userAssignedAs(user.getIdUser(),"Кредитный аналитик",taskJPA.getIdProcess()))
                credit.add(user.getFullName());
            if(user.getGss())
                gss.add(user.getFullName());
		}
        Collections.sort(sales);
        Collections.sort(credit);
        Collections.sort(gss);
        res.put("sales", sales);
        res.put("credit", credit);
        res.put("gss", gss);
        return res;
	}

	/**
	 * Возвращает отрасль.
	 * @param organizationId идентификатор организации
	 * @return отрасль
	 */
	private String getOrganizationBranch(String organizationId) {
		return dashboardService.getOrganizationBranch(organizationId);
	}
	
	@RequestMapping(value = "/frame/pipeline.html")
	public String frame_pipeline(@ModelAttribute("model") ModelMap model,
						@RequestParam("mdtaskid") String mdtaskid,
						HttpServletRequest request, HttpServletResponse response) throws Exception {
		LOGGER.info("PipelineController. mdtaskid="+mdtaskid);
		response.addHeader("Pragma", "no-cache");
		response.addHeader("Expires", "-1");
		response.addHeader("Cache-control", "no-cache");
		model.addAttribute("mdtaskid", mdtaskid);
        MdTask mdTask = mdTaskMapper.getPipelineWithinMdTask(Long.valueOf(mdtaskid));
        model.addAttribute("mdTask", mdTask);
		PupFacadeLocal pupEJB = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
		TaskFacadeLocal taskEJB = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
		DictionaryFacadeLocal dictEJB = com.vtb.util.EjbLocator.getInstance().getReference(DictionaryFacadeLocal.class);
        /*Пользователю, обладающему ролью «Продуктовый менеджер» с признаком «выполнение операции» в секции «Проектная команда»,
         * необходимо предоставить возможность после открытия на просмотр карточки заявки типа «Сделка»
         * редактировать поля, расположенные в секции «Продуктового менеджера» и сохранять эти изменения. */
		boolean readonly = pupEJB.isPipelineReadonly(Long.valueOf(mdtaskid));
		
		model.addAttribute("supplies", PipelineSupply.values());
		model.addAttribute("statuses", compendiumMapper.getPipelineStage());
		model.addAttribute("laws", PipelineLaw.values());
        model.addAttribute("flowInvestmentValues", CollectionUtils.set("Импорт", "Экспорт"));
        model.addAttribute("tradeFinanceList", compendiumMapper.getTradeFinance());
		model.addAttribute("readonly", String.valueOf(readonly));
		model.addAttribute("pipeline", taskEJB.getPipeline(Long.valueOf(mdtaskid)));
		model.addAttribute("pipeline_fin_target", taskEJB.getPipelineFinTarget(Long.valueOf(mdtaskid)));
		model.addAttribute("pipeline_financial_goal", dictEJB.findPipelineFinancialGoal());
		model.addAttribute("pipeline_coeffs1", dictEJB.findPipelineCoeffs(PipelineCoeffType.PERIOD_FACTOR.getValue()));
		model.addAttribute("pipeline_coeffs0", dictEJB.findPipelineCoeffs(PipelineCoeffType.PRODUCT_TYPE_FACTOR.getValue()));
		model.addAttribute("pipeline_funding_company", dictEJB.findPipelineFundingCompany());
		model.addAttribute("pipeline_trading_desk", dictEJB.findPipelineTradingDesk());
		model.addAttribute("effrate", getEffrate(Long.valueOf(mdtaskid)));
		model.addAttribute("markup", getMarkup(Long.valueOf(mdtaskid)));
		model.addAttribute("close_probability_can_edit", isCanEdit(Long.valueOf(mdtaskid)) ? "y" : "n");
		model.addAttribute("type", "сублимита");
		if(mdTask.isLimit()) model.addAttribute("type", "лимита");
		if(mdTask.isProduct()) model.addAttribute("type", "сделки");
		if(mdTask.isCrossSell()) model.addAttribute("type", "кросс-селл");
        HashMap<String, Object> pt = addProjectTeam(taskEJB.getTask(Long.valueOf(mdtaskid)));
        for(String key : pt.keySet())
            model.addAttribute(key, pt.get(key));
		return readonly?"pipelineReadonly":"pipeline";
	}
	private boolean isCanEdit(Long mdtaskid) throws FactoryException{
		return true;
		//Согласно новым требованиям
		//ССКО12 ч.1. Поле "Вероятность закрытия" по завершенной и одобренной сделке должно редактироваться VTBSPO-195
		/*TaskFacadeLocal taskEJB = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
		TaskJPA task = taskEJB.getTask(mdtaskid);
		return !(task.getParent()==null && task.getProcess().getIdStatus().equals(4L) && 
				task.getStatusReturn()!=null && task.getStatusReturn().getStatus_type().equals("1"));*/
	}
	/**
	 * вызывается на завершении операции
	 * @param pupid
	 * @throws FactoryException 
	 */
	public static void onTaskComplete(String stageNameTo, Long pupid, String stageNameFrom) throws FactoryException{
		if(stageNameTo==null || pupid==null)
			return;
		LOGGER.info("onTaskComplete("+stageNameTo+","+pupid+","+stageNameFrom+")");
        TaskFacadeLocal taskEJB = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
        if(stageNameFrom.startsWith("Акцепт перечня экспертиз")) {
            TaskJPA task = taskEJB.getTaskByPupID(pupid);
            SBeanLocator.singleton().getPriceService().setCloseProbabilityByStatus(task.getId(), "Согласование с экспертными подразделениями");
        }
        if(stageNameFrom.equalsIgnoreCase("Формирование измененного проекта Кредитного решения")) {
            TaskJPA task = taskEJB.getTaskByPupID(pupid);
            if (task.getProcessTypeName().equalsIgnoreCase("Изменение условий Крупный бизнес ГО") ||
                    task.getProcessTypeName().equalsIgnoreCase("Изменение условий Крупный бизнес ГО (Структуратор за МО)"))
                SBeanLocator.singleton().getPriceService().setCloseProbabilityByStatus(task.getId(), "Подготовка ПРКК/ПРУЛ");
        }
	}
	public static void onSaveCheckCloseProbability(ServletRequest request,Long mdtaskid) throws FactoryException {
		LOGGER.info("inlimitID="+request.getParameter("inlimitID"));
		TaskFacadeLocal taskEJB = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
		TaskJPA task = taskEJB.getTask(mdtaskid);
		if(request.getParameter("clearInLimit")!=null && request.getParameter("clearInLimit").equals("y")
				&& task.getParent()!=null){
			/*PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        	//был в лимите, теперь - нет. Нужно поменять вероятность закрытия
			TaskJPA parent = task;
        	while(parent.getParent()!=null) parent=parent.getParent();//лимит самого высокого уровня или сама заявка
        	String decision = pupFacadeLocal.getPUPAttributeValue(parent.getProcess().getId(), "Decision");
        	if(decision.equals("1"))
        		updateCloseProbability(50.0, task.getId());
			if(task.getParent().getStatusReturn()==null){
				if(request.getParameter("id")!=null)
					try {
						String stageName = pupFacadeLocal.getTask(Long.valueOf(request.getParameter("id"))).getStage().getDescription();
						LOGGER.info(stageName);
						if(stageName.equals("Формирование проекта Кредитного решения"))
							updateCloseProbability(40.0, task.getId());
						if(stageName.equals("Формирование Предварительных параметров Лимита/условий Сделки"))
							updateCloseProbability(30.0, task.getId());
					} catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
					}
			}*/
        }
	}
	public static void updateCloseProbability(Double val,Long mdtaskid) throws FactoryException {
		LOGGER.info("mdtaskid="+mdtaskid+", val="+val);
		TaskFacadeLocal taskEJB = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
		PipelineJPA p = taskEJB.getPipeline(mdtaskid);
		p.setClose_probability(val);
		List<String> ftlist = taskEJB.getPipelineFinTarget(mdtaskid);
		taskEJB.updatePipeline(p, ftlist.toArray(new String[ftlist.size()]));
	}
	
	private String getEffrate(Long mdtaskid) throws FactoryException{
		TaskFacadeLocal taskEJB = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
		List<FactPercentJPA> factprocents = taskEJB.getTask(mdtaskid).getFactPercents();
		if(factprocents!=null && factprocents.size()>0)
			for(FactPercentJPA fp : factprocents)
				if(fp.getTranceId()==null)
					return Formatter.format(fp.getCalcRate3());
		return "";
	}
	public static String getMarkup(Long mdtaskid) throws Exception {
		TaskFacadeLocal taskEJB = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
		if(!taskEJB.getTask(mdtaskid).isProduct())
			return taskEJB.getPipeline(mdtaskid).getMarkup();
		Double hr = taskEJB.getPipeline(mdtaskid).getHurdleRateValue();
		if(hr==null)
			hr = 0.0;
		List<FactPercentJPA> factprocents = taskEJB.getTask(mdtaskid).getFactPercents();
		if(factprocents!=null && factprocents.size()>0)
			for(FactPercentJPA fp : factprocents)
				if(fp.getTranceId()==null)
					return Formatter.format3point(fp.getCalcRate3(PriceController.getComissionSum(mdtaskid)-hr));
		return "";
	}
	
	@RequestMapping(value = "/ajax/save_pipeline.html") @ResponseBody
    public String save(HttpServletRequest request,@ModelAttribute("model") ModelMap model, 
    		@RequestParam("mdtaskid") String mdtaskid, @RequestParam("pipeline_status") String status,
    		@RequestParam("plan_date") String plan_date, 
    		@RequestParam("pipeline_close_probability") String close_probability, @RequestParam("pipeline_wal") String wal, @RequestParam("pipeline_hurdle_rate") String hurdle_rate,
					   @RequestParam("pipeline_margin") String margin, @RequestParam("pipeline_markup") String markup, @RequestParam("pipeline_pc_cash") String pc_cash, @RequestParam("pipeline_pc_res") String pc_res,
    		@RequestParam("pipeline_pc_der") String pc_der, @RequestParam("pipeline_pc_total") String pc_total, @RequestParam("pipeline_line_count") String line_count, 
    		@RequestParam("pipeline_factor_product_type") String factor_product_type, @RequestParam("pipeline_factor_period") String factor_period,
    		@RequestParam("pipeline_syndication") String syndication, @RequestParam("pipeline_pub") String pub, @RequestParam("pipeline_statusManual") String statusManual,
    		@RequestParam("pipeline_priority") String priority, @RequestParam("pipeline_new_client") String new_client, 
    		@RequestParam("pipeline_prolongation") String prolongation, @RequestParam("pipeline_hideinreport") String hideinreport,
                       @RequestParam("pipeline_hideinreporttraders") String hideinreporttraders,
    		@RequestParam("pipeline_law") String law,@RequestParam("pipeline_geography") String geography,@RequestParam("pipeline_description") String description,
    		@RequestParam("pipeline_cmnt") String cmnt,@RequestParam("pipeline_addition_business") String addition_business,@RequestParam("pipeline_syndication_cmnt") String syndication_cmnt,
    		@RequestParam("pipeline_rating") String rating,@RequestParam("pipeline_contractor") String contractor,@RequestParam("pipeline_vtb_contractor") String vtb_contractor,
    		@RequestParam("pipeline_trade_desc") String trade_desc,@RequestParam("supply") String supply,@RequestParam("flow_investment") String flow_investment,
                       @RequestParam("trade_finance_id") Integer trade_finance_id) throws FactoryException {
		LOGGER.info("PipelineController.save mdtaskid="+mdtaskid);
		String[] ft = request.getParameterValues("pipeline_fin_target[]");
		try {
			TaskFacadeLocal taskEJB = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
			PipelineJPA p = new PipelineJPA();
			p.setId_mdtask(Long.valueOf(mdtaskid));
			p.setStatus(status);
			p.setPlan_date(Formatter.parseDate(plan_date));
			p.setClose_probability(Formatter.parseDouble(close_probability));
			p.setWal(Formatter.parseDouble(wal));
			p.setHurdle_rate(Formatter.parseDouble(hurdle_rate));
			p.setMarkup(Formatter.parseDouble(markup));
			p.setMargin(Formatter.parseDouble(margin));
			p.setPc_cash(Formatter.parseDouble(pc_cash));
			p.setPc_res(Formatter.parseDouble(pc_res));
			p.setPc_der(Formatter.parseDouble(pc_der));
			p.setPc_total(Formatter.parseDouble(pc_total));
			p.setLine_count(Formatter.parseDouble(line_count));
			p.setFactor_product_type(Formatter.parseDouble(factor_product_type));
			p.setFactor_period(Formatter.parseDouble(factor_period));
			p.setSyndication(syndication.equals("true")?"y":"n");
			p.setPub(pub.equals("true")?"y":"n");
			p.setStatusManual(statusManual.equals("true")?"y":"n");
			p.setNew_client(new_client.equals("true")?"y":"n");
			p.setPriority(priority.equals("true")?"y":"n");
			p.setProlongation(prolongation.equals("true")?"y":"n");
			p.setHideinreport(hideinreport.equals("true")?"y":"n");
			p.setHideinreporttraders(hideinreporttraders.equals("true")?"y":"n");
			p.setLaw(law);
			p.setGeography(geography);
			p.setDescription(description);
			p.setCmnt(cmnt);
			p.setAddition_business(addition_business);
			p.setSyndication_cmnt(syndication_cmnt);
			p.setRating(rating);
			p.setContractor(contractor);
			p.setVtb_contractor(vtb_contractor);
			p.setTrade_desc(trade_desc);
			p.setSupply(supply);
			p.setFlow_investment(flow_investment);
			p.setTrade_finance(trade_finance_id);

			taskEJB.updatePipeline(p, ft);
			
			return "OK";
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return e.getMessage();
		}
	}
}
