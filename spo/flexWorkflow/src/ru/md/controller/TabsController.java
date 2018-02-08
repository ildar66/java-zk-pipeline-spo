package ru.md.controller;

import com.google.gson.Gson;
import com.vtb.domain.Task;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.TaskActionProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.md.domain.MdTask;
import ru.md.helper.FormSubTab;
import ru.md.helper.FormTab;
import ru.md.helper.TaskHelper;
import ru.md.persistence.*;
import ru.md.spo.ejb.PupFacadeLocal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashSet;

@Controller
public class TabsController {
	private static final Logger LOGGER = LoggerFactory.getLogger(TabsController.class);

	@Autowired
	private MdTaskMapper mdTaskMapper;

	@RequestMapping(value = "/tabs.html")
    public String home(@ModelAttribute("model") ModelMap model,
					   @RequestParam("mdtaskid") Long mdtaskid,
					   HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.addHeader("Pragma", "no-cache");
		response.addHeader("Expires", "-1");
		response.addHeader("Cache-control", "no-cache");
		long tstart = System.currentTimeMillis();

		MdTask mdtask = mdTaskMapper.getById(mdtaskid);
		HashSet<String> sectionNotEmpty = mdTaskMapper.getSectionNotEmpty(mdtaskid);
		for(String s : sectionNotEmpty)
			LOGGER.info("sectionNotEmpty : " + s);
		PupFacadeLocal pupFacade = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
		Gson gson = new Gson();
		ArrayList<FormTab> tabs = new ArrayList<FormTab>();
		ArrayList<FormSubTab> subtabs =  new ArrayList<FormSubTab>();

		tabs.add(new FormTab("Заемщики","contractor","frame_contractor.jsp?", "notempty"));
		if(!mdtask.isSublimit() && mdtask.getIdPupProcess() != null){
			subtabs =  new ArrayList<FormSubTab>();
			subtabs.add(new FormSubTab("Проектная команда",0L));
			subtabs.add(new FormSubTab("Мидл-офис",1L));
			subtabs.add(new FormSubTab("Формирование запроса",2L));
			subtabs.add(new FormSubTab("История запросов",3L));
			tabs.add(new FormTab("Проектная команда","projectteam","frame/projectTeam.jsp?",subtabs,"project_team_tabs",
					sectionNotEmpty.contains("projectteam")?"notempty":""));
		}
		if (!mdtask.isCrossSell())
            tabs.add(new FormTab("Структура Лимита","inLimit","frame/inLimit.jsp?", sectionNotEmpty.contains("inLimit")?"notempty":""));
		if(mdtask.isProduct() || mdtask.isCrossSell()){
			subtabs =  new ArrayList<FormSubTab>();
			subtabs.add(new FormSubTab("Основные параметры",0L));
			subtabs.add(new FormSubTab("Целевое назначение",1L));
			subtabs.add(new FormSubTab("График использования",2L));
			tabs.add(new FormTab(mdtask.isCrossSell()?"Основные параметры Кросс-селл":"Основные параметры Сделки",
					"opportunityParam","frame_opportunityParam.jsp?",subtabs,"tabs_opportunity_param", "notempty"));
			subtabs =  new ArrayList<FormSubTab>();
			subtabs.add(new FormSubTab("Процентная ставка",0L));
			subtabs.add(new FormSubTab("Комиссии/вознаграждения",1L));
			subtabs.add(new FormSubTab("Санкции (неустойки, штрафы, пени и т.д.)",2L));
			tabs.add(new FormTab("Стоимостные условия","priceConditionProduct","frame_priceConditionProduct.jsp?",subtabs,"price_condition_product_tabs",
					sectionNotEmpty.contains("priceConditionProduct")?"notempty":""));
		} else {
			subtabs =  new ArrayList<FormSubTab>();
			subtabs.add(new FormSubTab("Основные параметры",0L));
			subtabs.add(new FormSubTab("Группы видов сделок",1L));
			subtabs.add(new FormSubTab("Целевое назначение",2L));
			subtabs.add(new FormSubTab("Порядок принятия решения о проведении операций в рамках сублимита",3L));
			tabs.add(new FormTab("Основные параметры Лимита","limitParam","frame_limitParam.jsp?",subtabs,"limitparam_tabs", "notempty"));
			tabs.add(new FormTab("Стоимостные условия","priceConditionLimit","frame_priceConditionLimit.jsp?",
                    sectionNotEmpty.contains("priceConditionLimit")?"notempty":""));
		}
        if (!mdtask.isCrossSell())
            tabs.add(new FormTab("Договоры","contract","frame/contract.jsp?", sectionNotEmpty.contains("contract")?"notempty":""));
		if(mdtask.isProduct()){
			subtabs =  new ArrayList<FormSubTab>();
			subtabs.add(new FormSubTab("Погашение основного долга",0L));
			subtabs.add(new FormSubTab("График платежей",1L));
			subtabs.add(new FormSubTab("График погашения процентов",2L));
			tabs.add(new FormTab("Графики платежей","graph","frame/graph.jsp?",subtabs,"graph_tabs",
					sectionNotEmpty.contains("graph")?"notempty":""));
		} else {
            if (!mdtask.isCrossSell())
                tabs.add(new FormTab("Погашение основного долга","graph","frame/graph.jsp?",
					sectionNotEmpty.contains("graph")?"notempty":""));
		}
		subtabs =  new ArrayList<FormSubTab>();
		subtabs.add(new FormSubTab("Условия",0L));
		subtabs.add(new FormSubTab("Условия досрочного погашения",1L));
		subtabs.add(new FormSubTab("Дополнительные/Отлагательные/ Индивидуальные и прочие условия",2L));
        if (!mdtask.isCrossSell())
            tabs.add(new FormTab("Условия","conditions","frame_conditions.jsp?",subtabs,"condition_tabs",
				sectionNotEmpty.contains("conditions")?"notempty":""));
		subtabs =  new ArrayList<FormSubTab>();
		subtabs.add(new FormSubTab("Обеспечение",0L));
		if (mdtask.isSupplyexist()) {
			subtabs.add(new FormSubTab("Залоги",1L));
			subtabs.add(new FormSubTab("Поручительство",2L));
			subtabs.add(new FormSubTab("Гарантии",3L));
			subtabs.add(new FormSubTab("Вексель",4L));
		}
		tabs.add(new FormTab("Обеспечение","supply","frame/supply.jsp?",subtabs,"supply_tabs",
				sectionNotEmpty.contains("supply")?"notempty":""));
		tabs.add(new FormTab("Pipeline","pipeline","frame/pipeline.html?",
				sectionNotEmpty.contains("pipeline")?"notempty":""));
		if(!mdtask.isSublimit())
		    tabs.add(new FormTab("Решение уполномоченного органа","conclusion","frame/conclusion.jsp?",
					pupFacade.getPUPAttributeValue(mdtask.getIdPupProcess(),"Коллегиальный").isEmpty()?"":"notempty"));
		//Статус Решения по заявке
		subtabs =  new ArrayList<FormSubTab>();
		if(TaskHelper.showSectionReturnStatus(mdtask) || TaskHelper.showSectionReturnStatusCC(mdtask)
				|| mdtask.getIdPupProcess()!=null && pupFacade.getPUPAttributeValue(mdtask.getIdPupProcess(),"Статус").equalsIgnoreCase("Одобрено")
				|| mdtask.getIdStatus()!=null && mdtask.getIdStatus().equals(4L))
			subtabs.add(new FormSubTab("Статус Решения по заявке",0L));
		if(TaskHelper.showSpecialDecision(mdtask))
			subtabs.add(new FormSubTab("Особые условия решения",1L));
		if(!subtabs.isEmpty())
		    tabs.add(new FormTab("Статус Решения","decision","frame/decision.jsp?",subtabs,"decision_tabs",
					mdtask.isAdditionalContract()||mdtask.isProductMonitoring()
							||mdtask.getCcCacheStatusid()!=null||mdtask.getStatusreturn()!=null?"notempty":""));

		tabs.add(new FormTab("Ответственные подразделения","department","frame/department.jsp?", "notempty"));
		if(!mdtask.isSublimit() && mdtask.getIdPupProcess() != null && !mdtask.isCrossSell())
			tabs.add(new FormTab("Сроки прохождения этапов заявки","standardPeriod","frame/standardPeriod.jsp?", "notempty"));
		if(!mdtask.isSublimit() && mdtask.getIdPupProcess() != null && !mdtask.isCrossSell())
			tabs.add(new FormTab("Общий лист оценки стоп-факторов","stopFactors","frame_stopFactors.jsp?",
				sectionNotEmpty.contains("stopFactors")?"notempty":""));
		if(!mdtask.isSublimit()){
			String  ownerid= mdtask.getIdPupProcess() != null ? mdtask.getIdPupProcess().toString() : ("mdtaskid" + mdtaskid);
			tabs.add(new FormTab("Документы по заявке", "docs", "frame/documents.jsp?mdtaskid=" + ownerid + "&pupTaskId=0",
					sectionNotEmpty.contains("docs")?"notempty":""));
		}
		if(!mdtask.isSublimit() && !mdtask.isCrossSell())
			tabs.add(new FormTab("Действующие решения","active_decision","frame/active_decision.jsp?",
					sectionNotEmpty.contains("active_decision")?"notempty":""));
		if(mdtask.getIdTypeProcess()!=null
                && pupFacade.getAttributeList(mdtask.getIdTypeProcess()).contains("R_Результаты экспертиз")
                && !mdtask.isCrossSell())
			tabs.add(new FormTab("Проведение экспертиз", "expertus", "frame/expertus.jsp?",
					sectionNotEmpty.contains("expertus")?"notempty":""));
		if(mdtask.getIdPupProcess() != null && !mdtask.isCrossSell())
		    tabs.add(new FormTab("Справка об отклоненных замечаниях и предложениях Экспертных подразделений","frame_departmentAgreement","frame_departmentAgreement.jsp?",
				sectionNotEmpty.contains("department_agreement")?"notempty":""));
        if (!mdtask.isCrossSell())
            tabs.add(new FormTab("Заявки на фондирование","frame_funds","ajax/fundList.do?", ""));
        if (!mdtask.isCrossSell())
		    tabs.add(new FormTab("Заявки на Н6","frame_n6","ajax/n6List.do?", ""));
		tabs.add(new FormTab("Комментарии","comments","frame_comments.jsp?", sectionNotEmpty.contains("comments")?"notempty":""));
		if(!mdtask.isSublimit())
			tabs.add(new FormTab("Версии","versionsList","frame_versionsList.jsp?", "notempty"));

		model.addAttribute("msg", gson.toJson(tabs));

		Long loadTime = System.currentTimeMillis()-tstart;
		LOGGER.warn("*** tabs.html time " + loadTime);
        return "utf8";
    }

	@RequestMapping(value = "/tabs_view.html")
	public String tabs_view(@ModelAttribute("model") ModelMap model,
					   @RequestParam("mdtaskid") Long mdtaskid,
					   HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.addHeader("Pragma", "no-cache");
		response.addHeader("Expires", "-1");
		response.addHeader("Cache-control", "no-cache");

		Gson gson = new Gson();
		ArrayList<FormTab> tabs = new ArrayList<FormTab>();
		tabs.add(new FormTab("Секция ПМ","pm_section","frame/pm_section.html?", "notempty"));
		tabs.add(new FormTab("Контрагенты","allcontr","frame/allcontractor.jsp?", "notempty"));
		model.addAttribute("msg", gson.toJson(tabs));
		return "utf8";
	}
}
