package ru.md.controller;


import com.google.gson.Gson;
import com.vtb.domain.CommissionDeal;
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
import ru.masterdm.compendium.domain.crm.PatternPaidPercentType;
import ru.masterdm.compendium.model.CompendiumCrmActionProcessor;
import ru.masterdm.spo.service.IDashboardService;
import ru.masterdm.spo.service.IPriceService;
import ru.masterdm.spo.utils.Formatter;
import ru.masterdm.spo.utils.SBeanLocator;
import ru.md.domain.*;
import ru.md.domain.dict.ComissionPeriod;
import ru.md.spo.dbobjects.FactPercentJPA;
import ru.md.spo.ejb.TaskFacadeLocal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * контролер возвращает значения справочников для ajax.
 */
@Controller
public class PriceController {
	private static final Logger LOGGER = LoggerFactory.getLogger(PriceController.class.getName());
	@Autowired
	private IPriceService priceService;

	@RequestMapping(value = "/ajax/calc_commission.html")
	public String calcCommission(@ModelAttribute("model") ModelMap model,
								 @RequestParam("value") String value, @RequestParam("curr") String curr,@RequestParam("id") String id,
								 @RequestParam("procent_order") String procent_order,@RequestParam("mdtaskid") Long mdtaskid,
								 @RequestParam("comissionType") String comissionType,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.addHeader("Pragma", "no-cache");
		response.addHeader("Expires", "-1");
		response.addHeader("Cache-control", "no-cache");
		HashMap<String,String> res = new HashMap<String, String>();
		res.put("id", id);
		try {
			String val = getAnnualValue(value, curr, procent_order, mdtaskid);
			res.put("val", val);
			String isUsedInEffStavRecalc = SBeanLocator.singleton().compendium().isUsedInEffStavRecalc(comissionType);
			if(isUsedInEffStavRecalc!=null && isUsedInEffStavRecalc.equalsIgnoreCase("t"))
				res.put("eff",val.replaceAll(",","."));
			else
				res.put("eff","0.0");
		} catch (Exception e) {
			res.put("val", e.getMessage());
			res.put("eff", "");
		}
		model.addAttribute("msg", new Gson().toJson(res));
		return "utf8";
	}
	public static String getAnnualValue(String value, String curr,String procent_order_id,Long mdtaskid){
		try{
			if(curr == null) curr ="";
			if(curr.equals("%годовых")){
				return Formatter.format3point(Formatter.parseBigDecimal(value));
			}

			PatternPaidPercentType ppt = getPatternPaidPercentTypeById(procent_order_id);
			LOGGER.info("procent_order=" + procent_order_id);
			LOGGER.info("ppt=" + ppt);
			ComissionPeriod comissionPeriod = ppt==null?null:ComissionPeriod.find(ppt.getName());
			if(comissionPeriod == null)
				return "";
			MdTask mdTask = SBeanLocator.singleton().mdTaskMapper().getById(mdtaskid);
			if(curr.equals("%")){
				if (comissionPeriod.getCoeff() != null)
					return Formatter.format3point(Formatter.parseBigDecimal(value).multiply(BigDecimal.valueOf(comissionPeriod.getCoeff())));
				else {
					BigDecimal periodInYears;
					if (mdTask == null
							|| (periodInYears = mdTask.getPeriodInYears()) == null
							|| periodInYears.equals(BigDecimal.ZERO))
						return "Расчет не может быть произведен, т.к.срок сделки не определен";
					LOGGER.info("periodInYears="+periodInYears);
					if(comissionPeriod == ComissionPeriod.WITH_PERCENT)
						periodInYears = getPayIntCoeff(mdtaskid,periodInYears);
					return Formatter.format3point(Formatter.parseBigDecimal(value).divide(periodInYears, MathContext.DECIMAL128));
				}
			} else {
				if(mdTask.getMdtaskSum()==null || mdTask.getCurrency() ==null)
					return "Не задана сумма сделки";
				BigDecimal sumInRur = getCurrencyRate2RUR(mdTask.getCurrency()).multiply(mdTask.getMdtaskSum());
				if(sumInRur.equals(BigDecimal.ZERO))
					return "Сумма сделки равна 0";
				BigDecimal valInRur = getCurrencyRate2RUR(curr).multiply(Formatter.parseBigDecimal(value));
				if (comissionPeriod.getCoeff() != null) {
					return Formatter.format3point(valInRur.multiply(BigDecimal.TEN.pow(2))
							.multiply(BigDecimal.valueOf(comissionPeriod.getCoeff()))
							.divide(sumInRur, MathContext.DECIMAL128));
				} else {
					BigDecimal periodInYears;
					if (mdTask == null
							|| (periodInYears = mdTask.getPeriodInYears()) == null
							|| periodInYears.equals(BigDecimal.ZERO))
						return "Расчет не может быть произведен, т.к.срок сделки не определен";
					LOGGER.info("periodInYears="+periodInYears);
					if(comissionPeriod == ComissionPeriod.WITH_PERCENT)
						periodInYears = getPayIntCoeff(mdtaskid,periodInYears);
					return Formatter.format3point(valInRur.multiply(BigDecimal.TEN.pow(2))
							.divide(sumInRur.multiply(periodInYears), MathContext.DECIMAL128));
				}
			}
		} catch (Exception e){
			LOGGER.error(e.getMessage(), e);
			return e.getMessage();
		}
	}
	private static BigDecimal getPayIntCoeff(Long mdtaskid, BigDecimal periodInYears){
		try{
			String payInt = SBeanLocator.singleton().mdTaskMapper().getById(mdtaskid).getPayInt();
			if(payInt.equalsIgnoreCase("Ежеквартально") || payInt.startsWith("3 мес"))
				return BigDecimal.valueOf(0.25);
			if(payInt.startsWith("6 мес"))
				return BigDecimal.valueOf(0.5);
			if(payInt.startsWith("1 мес"))
				return BigDecimal.valueOf(0.08333333333333);
			if(payInt.equalsIgnoreCase("ежегодно"))
				return BigDecimal.ONE;
		} catch (Exception e){
			LOGGER.error(e.getMessage(), e);
		}
		//В конце срока или любой другой текст
		return periodInYears;
	}
	/** Возвращает текущий курс валюты к рублю
	 */
	private static BigDecimal getCurrencyRate2RUR(String currency){
		if(currency==null || currency.isEmpty() || currency.equalsIgnoreCase("RUR"))
			return BigDecimal.ONE;
		BigDecimal rate = SBeanLocator.singleton().compendium().getCurrencyRate(currency);
		if(rate==null)
			return BigDecimal.ONE;
		else
			return rate;
	}
	private static PatternPaidPercentType getPatternPaidPercentTypeById(String id){
		if(id == null)
			return null;
		CompendiumCrmActionProcessor processor =
				(CompendiumCrmActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumCrm");
		PatternPaidPercentType[] list=processor.findPatternPaidPercentTypeList("", "c.name");
		for(PatternPaidPercentType ppt : list){
			if(ppt.getId().equals(id))
				return ppt;
		}
		return null;
	}
	public static Double getComissionSum(Long mdtaskid) throws Exception {
		Double comissionSum = 0d;
		TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
		Task taskJDBC=processor.getTask(new Task(mdtaskid));
		ArrayList<CommissionDeal> commissionList = taskJDBC.getCommissionDealList();
		for (CommissionDeal comission : commissionList) {
			String isUsedInEffStavRecalc = SBeanLocator.singleton().compendium().isUsedInEffStavRecalc(comission.getName().getId());
			if (isUsedInEffStavRecalc!=null && isUsedInEffStavRecalc.equalsIgnoreCase("t")) {
				try {
					comissionSum = comissionSum + ru.masterdm.spo.utils.Formatter.parseDouble(PriceController.getAnnualValue(comission.getFormattedValue(),
							comission.getCurrency2().getCode(), comission.getProcent_order().getId(), taskJDBC.getId_task()));
				} catch (Exception e){}
			}
		}
		return comissionSum;
	}
}
