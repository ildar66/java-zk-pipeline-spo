package ru.md.compare;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.masterdm.compendium.domain.crm.CompanyGroup;
import ru.masterdm.compendium.domain.crm.FloatPartOfActiveRate;
import ru.masterdm.compendium.domain.spo.ContractorType;
import ru.masterdm.compendium.model.CompendiumCrmActionProcessor;
import ru.masterdm.integration.ServiceFactory;
import ru.masterdm.integration.funding.ws.FundingRequest;
import ru.masterdm.integration.funding.ws.N6Request;
import ru.masterdm.integration.rating.RatingService;
import ru.masterdm.integration.rating.ws.CalcHistoryInput;
import ru.masterdm.integration.rating.ws.CalcHistoryOutput;
import ru.md.compare.CompareTaskKeys.CompareTaskBlock;
import ru.md.compare.CompareTaskKeys.ContextKey;
import ru.md.dict.dbobjects.ConditionTypeJPA;
import ru.md.dict.dbobjects.RiskStepupFactorJPA;
import ru.md.domain.Withdraw;
import ru.md.helper.CompareHelper;
import ru.md.helper.TaskHelper;
import ru.md.pup.dbobjects.AttachJPA;
import ru.md.pup.dbobjects.RoleJPA;
import ru.md.pup.dbobjects.UserJPA;
import ru.md.spo.dbobjects.*;
import ru.md.spo.ejb.PupFacadeLocal;
import ru.md.spo.report.Expertus;
import ru.md.spo.util.Config;

import com.vtb.domain.ApprovedRating;
import com.vtb.domain.Comment;
import com.vtb.domain.Commission;
import com.vtb.domain.CommissionDeal;
import com.vtb.domain.DepartmentAgreement;
import com.vtb.domain.Deposit;
import com.vtb.domain.EarlyPayment;
import com.vtb.domain.Fine;
import com.vtb.domain.Guarantee;
import com.vtb.domain.OtherCondition;
import com.vtb.domain.PaymentSchedule;
import com.vtb.domain.StandardPeriod;
import com.vtb.domain.Task;
import com.vtb.domain.TaskContractor;
import com.vtb.domain.Trance;
import com.vtb.domain.Warranty;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.TaskActionProcessor;
import com.vtb.util.Formatter;

/**
 * Класс-помощник для сравнения заявок
 * Предоставляет методы для преобразования объектов
 * @author rislamov
 */
public class CompareTaskHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(CompareHelper.class.getName());

	/**
	 * Метод возвращает объект для сравнения
	 * @param task исходный объект
	 * @param block имя блока для преобразования
	 * @param context дополнительные объекты
	 * @return объект для сравнения
	 */
	public static ObjectElement toCompareObject(Task task, CompareTaskBlock block,
			Map<ContextKey, Object> context) {
		ObjectElement res = new ObjectElement();
		if (task.getId_task() == null)
			return res;
		switch (block) {
			case MAIN:
				res = CompareTaskBlockHelper.mainToCompareObject(task, context);
			break;
			case CONTRACTORS:
				res = CompareTaskBlockHelper.contractorsToCompareObject(task, context);
			break;
			case IN_LIMIT:
				res = CompareTaskBlockHelper.inLimitToCompareObject(task, context);
			break;
			case PARAMETERS:
				res = CompareTaskBlockHelper.parametersToCompareObject(task, context);
			break;
			case PRICE_CONDITIONS:
				res = CompareTaskBlockHelper.priceConditionsToCompareObject(task, context);
			break;
			case CONTRACTS:
				res = CompareTaskBlockHelper.contractsToCompareObject(task, context);
			break;
			case GRAPH:
				res = CompareTaskBlockHelper.graphToCompareObject(task, context);
			break;
			case GRAPH_LIMIT:
				res = CompareTaskBlockHelper.graphToCompareObject(task, context);
			break;
			case CONDITIONS:
				res = CompareTaskBlockHelper.conditionsToCompareObject(task, context);
			break;
			case SUPPLY:
				res = CompareTaskBlockHelper.supplyToCompareObject(task, context);
			break;
			case PIPELINE:
				res = CompareTaskBlockHelper.pipelineToCompareObject(task, context);
			break;
			case CONCLUSION:
				res = CompareTaskBlockHelper.conclusionToCompareObject(task, context);
			break;
			case RETURN_STATUS:
				res = CompareTaskBlockHelper.returnStatusToCompareObject(task, context);
			break;
			case DEPARTMENT:
				res = CompareTaskBlockHelper.departmentToCompareObject(task, context);
			break;
			case PROJECT_TEAM:
				res = CompareTaskBlockHelper.projectTeamToCompareObject(task, context);
			break;
			case STANDARD_PERIOD:
				res = CompareTaskBlockHelper.standardPeriodToCompareObject(task, context);
			break;
			case STOP_FACTORS:
				res = CompareTaskBlockHelper.stopFactorsToCompareObject(task, context);
			break;
			case DOCUMENTS:
				res = CompareTaskBlockHelper.documentsToCompareObject(task, context);
			break;
			case ACTIVE_DECISION:
				res = CompareTaskBlockHelper.activeDecisionToCompareObject(task, context);
			break;
			case EXPERTUS:
				res = CompareTaskBlockHelper.expertusToCompareObject(task, context);
			break;
			case DEPARTMENT_AGREEMENT:
				res = CompareTaskBlockHelper.departmentAgreementToCompareObject(task, context);
			break;
			case FUNDS:
				res = CompareTaskBlockHelper.fundsToCompareObject(task, context);
			break;
			case N6:
				res = CompareTaskBlockHelper.n6ToCompareObject(task, context);
			break;
			case COMMENTS:
				res = CompareTaskBlockHelper.commentsToCompareObject(task, context);
			break;
			default:
			break;
		}
		return res;
	}

	public static ObjectElement toCompareObject(PromissoryNoteJPA promNote, HeaderElement head,
			Map<ContextKey, Object> context, String mapKey) {
		ObjectElement res = new ObjectElement();
		res.setHtmlList("#compare_list_supply_promissory");
		res.setHtmlName("#compare_supply_promissory"+promNote.getId());
		res.setValue(mapKey);
		for (HeaderElement key : head.getKeys()) {
			String value = "";
			String htmlName = "";
			if (key.isKey() && key.getKey() != null) {
				if ("Номинал (вексельная сумма) векселя Банка".equals(key.getKey())) {
					value = Formatter.toMoneyFormat(promNote.getVal());
					htmlName = res.getHtmlName()+"_val";
				}
				else if ("Валюта векселя".equals(key.getKey())) {
					value = promNote.getCurrency();
					htmlName = res.getHtmlName()+"_currency";
				}
				else if ("Процентная оговорка по векселю Банка".equals(key.getKey())) {
					value = Formatter.toMoneyFormat(promNote.getPerc());
					htmlName = res.getHtmlName()+"_per";
				}
				else if ("Срок платежа по векселю Банка".equals(key.getKey())) {
					value = Formatter.str(promNote.getMaxdate());
					htmlName = res.getHtmlName()+"_date";
				}
				else if ("Место платежа по векселю Банка".equals(key.getKey())) {
					value = promNote.getPlace();
					htmlName = res.getHtmlName()+"_place";
				}
			}
			res.getStructure().put(key.getKey(), new ObjectElement(value, htmlName));
		}

		return res;
	}

	public static ObjectElement toCompareObject(Guarantee guarantee, HeaderElement head,
			Map<ContextKey, Object> context, String mapKey) {
		ObjectElement res = new ObjectElement();
		res.setHtmlList("#compare_list_supply_garant");
		res.setHtmlName("#compare_supply_garant"+guarantee.getCode());
		res.setValue(mapKey);
		for (HeaderElement key : head.getKeys()) {
			String value = "";
			String htmlName = "";
			if (key.isKey() && key.getKey() != null) {
				if ("Основное обеспечение".equals(key.getKey())) {
					value = CompareHelper.boolToString(guarantee.isMain());
					htmlName = res.getHtmlName()+"_main";
				}
				else if ("Дополнительное обеспечение".equals(key.getKey())) {
					value = CompareHelper.boolToString(!guarantee.isMain());
					htmlName = res.getHtmlName()+"_main";
				}
				else if ("На всю сумму обязательств".equals(key.getKey())) {
					value = CompareHelper.boolToString(guarantee.isFullSum());
					htmlName = res.getHtmlName()+"_fullsum";
				}
				else if ("Сумма гарантии".equals(key.getKey())) {
					value = Formatter.format(guarantee.getSum());
					htmlName = res.getHtmlName()+"_sum";
				}
				else if ("Валюта".equals(key.getKey())) {
					value = guarantee.getCurrency().getCode();
					htmlName = res.getHtmlName()+"_sum";
				}
				else if ("Категория обеспечения".equals(key.getKey())) {
					if (guarantee.getLiquidityLevel() != null)
						value = guarantee.getLiquidityLevel().getName();
					htmlName = res.getHtmlName()+"_LIQUIDITY_LEVEL";
				}
				else if ("Финансовое состояние гаранта".equals(key.getKey())) {
					if (guarantee.getDepositorFinStatus() != null)
						value = guarantee.getDepositorFinStatus().getName();
					htmlName = res.getHtmlName()+"_DEPOSITOR_FIN_STATUS";
				}
				else if ("Группа обеспечения".equals(key.getKey())) {
					if (guarantee.getOb() != null)
						value = guarantee.getOb().getName();
					htmlName = res.getHtmlName()+"_type";
				}
				else if ("Степень обеспечения, %".equals(key.getKey())) {
					value = Formatter.format(guarantee.getSupplyvalue());
					htmlName = res.getHtmlName()+"_supplyvalue";
				}
				else if ("Срок гарантии".equals(key.getKey())) {
					value = guarantee.getPeriodFormated() + " " + guarantee.getPeriodDimension();
					htmlName = res.getHtmlName()+"_period";
				}
				else if ("по дату".equals(key.getKey())) {
					value = Formatter.format(guarantee.getTodate());
					htmlName = res.getHtmlName()+"_period";
				}
			}
			res.getStructure().put(key.getKey(), new ObjectElement(value, htmlName));
		}
		return res;
	}

	public static ObjectElement toCompareObject(Warranty warranty, HeaderElement head,
			Map<ContextKey, Object> context, String mapKey, int number) {
		ObjectElement res = new ObjectElement();
		res.setHtmlList("#compare_list_supply_warranty");
		res.setHtmlName("#compare_supply_warranty"+number);
		res.setValue(mapKey);
		for (HeaderElement key : head.getKeys()) {
			ObjectElement subRes = new ObjectElement();
			if (key.isKey() && key.getKey() != null) {
				String value = "";
				String htmlName = "";
				if ("Основное обеспечение".equals(key.getKey())) {
					value = CompareHelper.boolToString(warranty.isMain());
					htmlName = res.getHtmlName()+"_main";
				}
				else if ("Дополнительное обеспечение".equals(key.getKey())) {
					value = CompareHelper.boolToString(!warranty.isMain());
					htmlName = res.getHtmlName()+"_main";
				}
				else if ("На всю сумму обязательств".equals(key.getKey())) {
					value = CompareHelper.boolToString(warranty.isFullSum());
					htmlName = res.getHtmlName()+"_fullsum";
				}
				else if ("Предел ответственности".equals(key.getKey())) {
					value = Formatter.toMoneyFormat(warranty.getSum()) + " "
							+ warranty.getCurrency().getCode();
					htmlName = res.getHtmlName()+"_sum";
				}
				else if ("Вид поручительства".equals(key.getKey())) {
					if (warranty.getKind() != null && !warranty.getKind().equals("-1"))
						value = warranty.getKind();
					htmlName = res.getHtmlName()+"_kind";
				}
				else if ("Дополнительные обязательства по Поручителю".equals(key.getKey())) {
					value = warranty.getAdd();
					htmlName = res.getHtmlName()+"_add";
				}
				else if ("Категория обеспечения".equals(key.getKey())) {
					value = warranty.getLiquidityLevel().getName();
					htmlName = res.getHtmlName()+"_LIQUIDITY_LEVEL";
				}
				else if ("Финансовое состояние поручителя".equals(key.getKey())) {
					value = warranty.getDepositorFinStatus().getName();
					htmlName = res.getHtmlName()+"_DEPOSITOR_FIN_STATUS";
				}
				else if ("Группа обеспечения".equals(key.getKey())) {
					if (warranty.getOb() != null)
						value = warranty.getOb().getName();
					htmlName = res.getHtmlName()+"_type";
				}
				else if ("Степень обеспечения, %".equals(key.getKey())) {
					value = Formatter.format(warranty.getSupplyvalue());
					htmlName = res.getHtmlName()+"_supplyvalue";
				}
				else if ("Срок поручительства".equals(key.getKey())) {
					value = warranty.getPeriodFormated() + " " + warranty.getPeriodDimension();
					htmlName = res.getHtmlName()+"_period";
				}
				else if ("По дату".equals(key.getKey())) {
					value = Formatter.format(warranty.getTodate());
					htmlName = res.getHtmlName()+"_period";
				}
				subRes = new ObjectElement(value, htmlName);
			}
			else {
				if ("Наименование санкции (Тип штрафной санкции)".equals(key.getKey())) {
					for (Fine fine : warranty.getFineList())
						subRes.getStructure().put(fine.getPunitiveMeasure(),
								toCompareObject(fine, key, context, "warranty"));
					subRes.setValue("Штрафные санкции к Поручителю");
				}
				else if ("Тип ответственности".equals(key.getKey())) {
					for (String resp : warranty.getResponsibility())
						subRes.getStructure().put(Warranty.ResponsibilityValues.get(resp), 
								new ObjectElement(Warranty.ResponsibilityValues.get(resp), res.getHtmlName() + "_resp" + resp,
										"#compare_list_supply_warranty" + number + "_resp"));
					subRes.setValue("Распределение ответственности");
				}
			}
			res.getStructure().put(key.getKey(), subRes);
		}
		return res;
	}

	public static ObjectElement toCompareObject(Deposit deposit, HeaderElement head,
			Map<ContextKey, Object> context, int number, String mapKey) {
		ObjectElement res = new ObjectElement();
		res.setHtmlList("#compare_list_supply_depositor");
		res.setHtmlName("#compare_supply_depositor"+number);
		res.setValue(mapKey);
		for (HeaderElement key : head.getKeys()) {
			String value = "";
			String htmlName = "";
			if (key.isKey() && key.getKey() != null) {
				if ("Основное обеспечение".equals(key.getKey())) {
					value = CompareHelper.boolToString(deposit.isMain());
					htmlName = "#compare_supply_depositor" + number + "_main";
				}
				else if ("Дополнительное обеспечение".equals(key.getKey())) {
					value = CompareHelper.boolToString(!deposit.isMain());
					htmlName = "#compare_supply_depositor" + number + "_main";
				}
				else if ("Послед. залог".equals(key.getKey())) {
					value = CompareHelper.boolToString(deposit.isPosled());
					htmlName = "#compare_supply_depositor" + number + "_posled";
				}
				else if ("Вид залога".equals(key.getKey())) {
					value = deposit.getType();
					htmlName = "#compare_supply_depositor" + number + "_type";
				}
				else if ("Предмет залога".equals(key.getKey())) {
					if (deposit.getZalogObject() != null)
						value = deposit.getZalogObject().getText();
					htmlName = "#compare_supply_depositor" + number + "_zalogobject";
				}
				else if ("Наименование и характеристики предмета залога".equals(key.getKey())) {
					value = deposit.getZalogDescription();
					htmlName = "#compare_supply_depositor" + number + "_zalogdesc";
				}
				else if ("Рыночная стоимость предмета залога (руб.)".equals(key.getKey())) {
					value = Formatter.toMoneyFormat(deposit.getZalogMarket());
					htmlName = "#compare_supply_depositor" + number + "_zalogmarket";
				}
				else if ("Порядок определения рыночной стоимости".equals(key.getKey())) {
					value = deposit.getOrderDescription();
					htmlName = "#compare_supply_depositor" + number + "_orderdesc";
				}
				else if ("Коэффициент залогового дисконтирования".equals(key.getKey())) {
					value = Formatter.toMoneyFormat(deposit.getDiscount());
					htmlName = "#compare_supply_depositor" + number + "_discount";
				}
				else if ("Описание залоговой сделки".equals(key.getKey())) {
					value = deposit.getOppDescription();
					htmlName = "#compare_supply_depositor" + number + "_oppdesc";
				}
				else if ("Ликвидационная стоимость предмета залога (руб.)".equals(key.getKey())) {
					value = Formatter.toMoneyFormat(deposit.getZalogTerminate());
					htmlName = "#compare_supply_depositor" + number + "_zalogterminate";
				}
				else if ("Залоговая стоимость".equals(key.getKey())) {
					value = Formatter.toMoneyFormat(deposit.getZalog());
					htmlName = "#compare_supply_depositor" + number + "_zalog";
				}
				else if ("Категория обеспечения (уровень ликвидности залога)".equals(key.getKey())) {
					if (deposit.getLiquidityLevel() != null)
						value = deposit.getLiquidityLevel().getName();
					htmlName = "#compare_supply_depositor" + number + "_liqlevel";
				}
				else if ("Финансовое состояние залогодателя".equals(key.getKey())) {
					if (deposit.getDepositorFinStatus() != null)
						value = deposit.getDepositorFinStatus().getName();
					htmlName = "#compare_supply_depositor" + number + "_finstatus";
				}
				else if ("Группа обеспечения".equals(key.getKey())) {
					if (deposit.getOb() != null)
						value = deposit.getOb().getName();
					htmlName = "#compare_supply_depositor" + number + "_ob";
				}
				else if ("Степень обеспечения, %".equals(key.getKey())) {
					value = Formatter.format(deposit.getSupplyvalue());
					htmlName = "#compare_supply_depositor" + number + "_supplyvalue";
				}
				else if ("Срок залога".equals(key.getKey())) {
					value = deposit.getPeriodFormated() + " " + deposit.getPeriodDimension();
					htmlName = "#compare_supply_depositor" + number + "_period";
				}
				else if ("По дату".equals(key.getKey())) {
					value = Formatter.format(deposit.getTodate());
					htmlName = "#compare_supply_depositor" + number + "_todate";
				}
				else if ("Максимально возможная доля необеспеченной части сублимита".equals(key.getKey())) {
					value = Formatter.toMoneyFormat(deposit.getMaxpart());
					htmlName = "#compare_supply_depositor" + number + "_maxpart";
				}
				else if ("Удельный вес вида залога".equals(key.getKey())) {
					value = Formatter.toMoneyFormat(deposit.getWeightBD());
					htmlName = "#compare_supply_depositor" + number + "_weight";
				}
				else if ("Условия страхования".equals(key.getKey())) {
					value = deposit.getCond();
					htmlName = "#compare_supply_depositor" + number + "_cond";
				}
			}
			ObjectElement subRes = new ObjectElement(value, htmlName);
			res.getStructure().put(key.getKey(), subRes);
		}
		return res;
	}

	public static ObjectElement toCompareObject(EarlyPayment earlyPayment, HeaderElement head,
			Map<ContextKey, Object> context, int number) {
		ObjectElement res = new ObjectElement();
		res.setHtmlList("#compare_list_earlypaymentcondition");
		res.setHtmlName("#compare_list_earlypaymentcondition"+number);
		res.setValue(Integer.toString(number));
		for (HeaderElement key : head.getKeys()) {
			String value = "";
			String htmlName = "";
			if (key.isKey() && key.getKey() != null) {
				if ("Условие досрочного погашения".equals(key.getKey())) {
					value = earlyPayment.getPermissionValue();
					htmlName = "#compare_earlypaymentcondition_permission"+number;
				}
				if ("Комиссия".equals(key.getKey())) {
					if (earlyPayment.getCommission() != null)
						value = (earlyPayment.getCommission().equalsIgnoreCase("y") ? "" : "не ") + "взимается";
					htmlName = "#compare_earlypaymentcondition_commission"+number;
				}
				else if ("Комментарий".equals(key.getKey())) {
					value = earlyPayment.getCondition();
					htmlName = "#compare_earlypaymentcondition_EarlyPayment"+number;
				}
				else if ("За сколько дней Заемщик должен уведомить Банк о досрочном погашении".equals(key.getKey())) {
					value = earlyPayment.getDaysBeforeNotifyBankDisplay();
					htmlName = "#compare_earlypaymentcondition_periodType"+number;
				}
			}
			ObjectElement subRes = new ObjectElement(value, htmlName);
			res.getStructure().put(key.getKey(), subRes);
		}
		return res;
	}

	public static ObjectElement toCompareObject(OtherCondition otherCond, HeaderElement head,
			Map<ContextKey, Object> context, String mapKey, String sectionName) {
		long number = otherCond.getId();
		ObjectElement res = new ObjectElement();
		res.setHtmlList("#compare_list_"+sectionName+"_condition");
		res.setHtmlName("#compare_"+sectionName+"_condition"+number);
		res.setValue(mapKey);
		//compare_othercondition_condition<%=c.getType()%>
		for (HeaderElement key : head.getKeys()) {
			String value = "";
			String htmlName = "";
			if (key.isKey() && key.getKey() != null) {
				if ("Описание условия".equals(key.getKey())) {
					value = otherCond.getBody();
					htmlName="#compare_"+sectionName+"_condition"+number;
				}
			}
			ObjectElement subRes = new ObjectElement(value, htmlName);
			res.getStructure().put(key.getKey(), subRes);
		}
		return res;
	}

	public static ObjectElement toCompareObject(ArrayList<PaymentSchedule> paymentScheduleList,
			HeaderElement head, Map<ContextKey, Object> context, String tranceIdStr) {
		ObjectElement res = new ObjectElement();
		for (HeaderElement key : head.getKeys()) {
			ObjectElement subRes = new ObjectElement();
			if (!key.isKey()) {
				if ("Номер платежа".equals(key.getKey())) {
					int i = 0;
					for (PaymentSchedule pSchedule : paymentScheduleList) {
						if (!tranceIdStr.isEmpty()
								&& (pSchedule.getTranceId() == null || !tranceIdStr.equals(pSchedule.getTranceId()
										.toString()))) {
							continue;
						}
						subRes.getStructure().put("Платеж №" + (++i), toCompareObject(pSchedule, key, context, pSchedule.getId()));
					}

					subRes.setValue("Платежи по траншу");
				}
			}
			res.getStructure().put(key.getKey(), subRes);
		}
		return res;
	}

	public static ObjectElement toCompareObject(PaymentSchedule pSchedule, HeaderElement head,
			Map<ContextKey, Object> context, long number) {
		ObjectElement res = new ObjectElement();
		TaskJPA taskJPA = (TaskJPA) context.get(ContextKey.JPA);
		res.setHtmlList("#compare_list_graph");
		res.setHtmlName("#compare_list_graph"+number);
		res.setValue(Long.toString(number));
		for (HeaderElement key : head.getKeys()) {
			String value = "";
			String htmlName = "";
			if (key.isKey() && key.getKey() != null) {
				if ("Сумма платежа".equals(key.getKey())) {
					value = Formatter.toMoneyFormat(pSchedule.getAmount());
					htmlName = "#compare_graph_sum"+number;
				}
				else if ("Валюта платежа".equals(key.getKey())) {
					value = pSchedule.getCurrency().getCode();
					htmlName = "#compare_graph_curr"+number;
				}
				else if ("Период оплаты (с даты)".equals(key.getKey())) {
					if (!taskJPA.isAmortized_loan())
						value = pSchedule.getFromDateFormatted();
					htmlName = "#compare_graph_datefrom"+number;
				}
				else if ("Период оплаты (по дату)".equals(key.getKey())) {
					if (!taskJPA.isAmortized_loan())
						value = pSchedule.getToDateFormatted();
					htmlName = "#compare_graph_date"+number;
				}
				else if ("Порядок расчета".equals(key.getKey())) {
					if (!taskJPA.isAmortized_loan())
						value = pSchedule.getComBase();
					htmlName = "#compare_graph_combase"+number;
				}
				else if ("Дата оплаты".equals(key.getKey())) {
					if (taskJPA.isAmortized_loan())
						value = pSchedule.getFromDateFormatted();
					htmlName = "#compare_graph_fondrate"+number;
				}
				else if ("Срок периода".equals(key.getKey())) {
					if (taskJPA.isAmortized_loan())
						value = pSchedule.getPeriodStr();
					htmlName = "#compare_graph_period"+number;
				}
				else if ("Ставка фондирования по периоду платежа".equals(key.getKey())) {
					if (taskJPA.isAmortized_loan())
						value = Formatter.format(pSchedule.getFONDRATE());
					htmlName = "#compare_graph_fondrate"+number;
				}
				else if ("Описание периода оплаты".equals(key.getKey())) {
					value = pSchedule.getDesc();
					htmlName = "#compare_graph_pmn_desc"+number;
				}

			}
			
			ObjectElement subRes = new ObjectElement(value, htmlName);
			res.getStructure().put(key.getKey(), subRes);
		}
		return res;
	}

	public static ObjectElement toCompareObject(Fine fine, HeaderElement head,
			Map<ContextKey, Object> context, String sectionName) {
		ObjectElement res = new ObjectElement();
		res.setValue(fine.getPunitiveMeasure());
		res.setHtmlName("#compare_"+sectionName+"_fine" + fine.getId());
		res.setHtmlList("#compare_list_"+sectionName+"_fine");
		for (HeaderElement key : head.getKeys()) {
			String value = "";
			String htmlName = "";
			if (key.isKey() && key.getKey() != null) {
				if ("Описание санкции".equals(key.getKey())) {
					value = Formatter.str(fine.getDescription());
					htmlName = res.getHtmlName() + "_desc";
				}
				else if ("Величина санкции".equals(key.getKey())) {
					value = Formatter.str(fine.getFormattedValue());
					htmlName = res.getHtmlName() + "_value";
				}
				else if ("Валюта / %".equals(key.getKey())) {
					value = fine.getCurrencyCode();
					htmlName = res.getHtmlName() + "_curr";
				}
				else if ("Период оплаты".equals(key.getKey())) {
					value = fine.getFormattedPeriod()
							+ (fine.getPeriontype().equals("workdays") ? " рабочих дней" : "")
							+ (fine.getPeriontype().equals("alldays") ? " календарных дней" : "");
					htmlName = res.getHtmlName() + "_period";
				}
				else if ("Увеличивает ставку по сделке".equals(key.getKey())) {
					value = CompareHelper.boolToString(fine.isProductRateEnlarge());
					htmlName = res.getHtmlName() + "_rate_enlarge";
				}
			}
			res.getStructure().put(key.getKey(), new ObjectElement(value, htmlName));
		}
		return res;
	}

	public static ObjectElement toCompareObject(Commission com, HeaderElement head,
			Map<ContextKey, Object> context) {
		ObjectElement res = new ObjectElement();
		res.setValue(com.getName().getName());
		res.setHtmlName("#compare_limitprice_comission" + com.getId());
		res.setHtmlList("#compare_list_limitprice_comission");
		for (HeaderElement key : head.getKeys()) {
			String value = "";
			String htmlName = "";
			if (key.isKey() && key.getKey() != null) {
				if ("Описание комиссий".equals(key.getKey())) {
					value = Formatter.str(com.getDescription());
					htmlName = res.getHtmlName() + "_descr";
				}
				else if ("Порядок уплаты комиссий".equals(key.getKey())) {
					if (com.getCommissionLimitPayPattern() != null)
						value = Formatter.str(com.getCommissionLimitPayPattern().getName());
					htmlName = res.getHtmlName() + "_paypattern";
				}
				else if ("Величина комиссии".equals(key.getKey())) {
					value = com.getFormattedValue() + " " + com.getCurrencyCode();
					htmlName = res.getHtmlName() + "_value";
				}
			}
			res.getStructure().put(key.getKey(), new ObjectElement(value, htmlName));
		}
		return res;
	}

	public static ObjectElement toCompareObject(PremiumJPA premium, HeaderElement head,
			Map<ContextKey, Object> context, int i) {
		ObjectElement res = new ObjectElement();
		res.setValue("Вознаграждение №" + i);
		res.setHtmlName("#compare_limitprice_premium" + premium.getId());
		res.setHtmlList("#compare_list_limitprice_premium");
		for (HeaderElement key : head.getKeys()) {
			String value = "";
			String htmlName = "";
			if (key.isKey() && key.getKey() != null) {
				if ("Тип вознаграждения".equals(key.getKey())) {
					value = premium.getPremiumType().getPremium_name();
					htmlName = "#compare_limitprice_premium" + premium.getId();
				}
				else if ("Размер вознаграждения".equals(key.getKey())) {
					if (premium != null) {
						if (premium.getVal() != null)
							value = Formatter.toMoneyFormat(premium.getVal()) + " "
									+ Formatter.str(premium.getCurr()) + " " + premium.getText();
						else if (premium.getText() != null && !premium.getText().isEmpty())
							value = premium.getText();
						else if (premium.getPremiumType() != null)
							value = premium.getPremiumType().getValue();
					}
					// используется существующий идентификатор
					htmlName = "#premiumSizeTr" + premium.getId();
				}
			}
			res.getStructure().put(key.getKey(), new ObjectElement(value, htmlName));
		}
		return res;
	}

	public static ObjectElement toCompareObject(CommissionDeal commission, HeaderElement head,
			Map<ContextKey, Object> context) {
		ObjectElement res = new ObjectElement();
		res.setValue(commission.getName().getName());
		res.setHtmlName("#compare_prodprice_commission" + commission.getId());
		res.setHtmlList("#compare_list_prodprice_commission");
		for (HeaderElement key : head.getKeys()) {
			String value = "";
			String htmlName = "";
			if (key.isKey() && key.getKey() != null) {
				if ("Величина комиссии".equals(key.getKey())) {
					value = Formatter.str(commission.getFormattedValue());
					htmlName = res.getHtmlName() + "_value";
				}
				else if ("Валюта".equals(key.getKey())) {
					value = commission.getCurrency2().getCode();
					htmlName = res.getHtmlName() + "_curr";
				}
				else if ("Комментарии".equals(key.getKey())) {
					value = Formatter.str(commission.getDescription());
					htmlName = res.getHtmlName() + "_descr";
				}
				else if ("Периодичность оплаты комиссии".equals(key.getKey())) {
					if (commission.getProcent_order() != null)
						value = Formatter.str(commission.getProcent_order().getName());
					htmlName = res.getHtmlName() + "_procentorder";
				}
				else if ("База расчета".equals(key.getKey())) {
					if (commission.getCalcBase() != null)
						value = Formatter.str(commission.getCalcBase().getName());
					htmlName = res.getHtmlName() + "_calcbase";
				}
				else if ("Порядок расчета".equals(key.getKey())) {
					if (commission.getComissionSize() != null)
						value = Formatter.str(commission.getComissionSize().getName());
					htmlName = res.getHtmlName() + "_size";
				}
				else if ("Срок оплаты комиссии".equals(key.getKey())) {
					if (commission.getPayDescription() != null)
						value = Formatter.str(commission.getPayDescription());
					htmlName = res.getHtmlName() + "_paydescr";
				}
			}
			res.getStructure().put(key.getKey(), new ObjectElement(value, htmlName));
		}
		return res;
	}

	public static ObjectElement toCompareObject(FactPercentJPA per, HeaderElement head,
			Map<ContextKey, Object> context, int i, String type) {
		ObjectElement res = new ObjectElement();
		if ("trance".equals(type))
			res.setValue("Транш №" + i);
		else
			res.setValue("Период №" + i);
		res.setHtmlName("#compare_prodprice_" + type + per.getId());
		res.setHtmlList("#compare_list_prodprice_" + type);
		for (HeaderElement key : head.getKeys()) {
			String value = "";
			String htmlName = "";
			if (key.isKey() && key.getKey() != null) {
				if ("Период с".equals(key.getKey())) {
					value = Formatter.format(per.getStart_date());
					htmlName = res.getHtmlName() + "_startdate";
				}
				else if ("Период по".equals(key.getKey())) {
					value = Formatter.format(per.getEnd_date());
					htmlName = res.getHtmlName() + "_enddate";
				}
				else if ("Индикативная ставка".equals(key.getKey())) {
					String indRates = "";
					FloatPartOfActiveRate[] floatPartOfActiveRateArray = (FloatPartOfActiveRate[]) context
							.get(ContextKey.FLOAT_PART_OF_ACTIVE_RATE_LIST);
					for (FloatPartOfActiveRate fpar : floatPartOfActiveRateArray)
						for (IndrateMdtaskJPA indrate : per.getTask().getIndrates()){
							if(indrate.getIdFactpercent()==null || !indrate.getIdFactpercent().equals(per.getId()) || !per.isInterestRateDerivative()){continue;}
							if (fpar.getId().equals(indrate.getIndrate()))
								indRates += fpar.getText() + "<br />";
						}
					value = indRates;
					htmlName = res.getHtmlName() + "_indrate";
				}
				else if ("Тип ставки фиксированная".equals(key.getKey())) {
					value = per.isInterestRateFixed() ? "да"
							: "нет";
					htmlName = "#percentFact" + per.getId() + "_interestRateFixed";
				}
				else if ("Тип ставки плавающая".equals(key.getKey())) {
					value = per.isInterestRateDerivative() ? "да"
							: "нет";
					htmlName = "#percentFact" + per.getId() + "_interestRateDerivative";
				}
				else if ("Расчетная ставка".equals(key.getKey())) {
					value = Formatter.format(per.getCalcRate1());
					htmlName = "#percentFact" + per.getId() + "calcRate";
				}
				else if ("Расчетная защищенная ставка".equals(key.getKey())) {
					value = Formatter.format(per.getCalcRate2());
					htmlName = "#percentFact" + per.getId() + "calcRateProtected";
				}
				else if ("Ставка фондирования".equals(key.getKey())) {
					value = Formatter.format(per.getFondrate());
					htmlName = res.getHtmlName() + "_fondrate";
				}
				else if ("Тип премии за кредитный риск".equals(key.getKey())) {
					value = per.getRiskpremiumTypeDisplay();
					htmlName = res.getHtmlName() + "_riskpremium_type";
				}
				else if ("Величина изменения".equals(key.getKey())) {
					value = Formatter.format(per.getRiskpremium_change());
					htmlName = res.getHtmlName() + "_riskpremium_change";
				}
				else if ("Премия за кредитный риск".equals(key.getKey())) {
					value = Formatter.format(per.getRiskpremium());
					htmlName = res.getHtmlName() + "_riskpremium";
				}
				else if ("Индивидуальные условия".equals(key.getKey())) {
					value = Formatter.str(per.getIndcondition());
					htmlName = res.getHtmlName() + "_indcond";
				}
				else if ("Плата за экономический капитал".equals(key.getKey())) {
					value = Formatter.format(per.getRate3());
					htmlName = res.getHtmlName() + "_rate3";
				}
				else if ("Повыш. коэфф. за риск".equals(key.getKey())) {
					for (RiskStepupFactorJPA r : TaskHelper.dict().findRiskStepupFactor())
						if (r.getItem_id().equals(per.getRiskStepupFactorID()))
							value = r.getText();
					htmlName = res.getHtmlName() + "_riskstepupfactor";
				}
				else if ("Ставка размещения".equals(key.getKey())) {
					value = Formatter.format(per.getRate4());
					htmlName = res.getHtmlName() + "_rate4";
				}
				else if ("Надбавка к плавающей ставке".equals(key.getKey())) {
					String indRates = "";
					FloatPartOfActiveRate[] floatPartOfActiveRateArray = (FloatPartOfActiveRate[]) context
							.get(ContextKey.FLOAT_PART_OF_ACTIVE_RATE_LIST);
					for (FloatPartOfActiveRate fpar : floatPartOfActiveRateArray)
						for (IndrateMdtaskJPA indrate : per.getTask().getIndrates()){
							if(indrate.getIdFactpercent()==null || !indrate.getIdFactpercent().equals(per.getId()) || !per.isInterestRateDerivative()){continue;}
							if (fpar.getId().equals(indrate.getIndrate()))
								indRates += fpar.getText() + ": " + (indrate.getRate() == null ?
										"не установлена<br />" :
										indrate.getRate() + " % годовых<br />");
						}
					value = indRates;
					htmlName = res.getHtmlName() + "_rate_ind_rate";
				}
				else if ("Комментарий".equals(key.getKey())) {
					value = per.getRate4Desc();
					htmlName = res.getHtmlName() + "_rate4desc";
				}
				else if ("Эффективная ставка".equals(key.getKey())) {
					value = Formatter.format(per.getCalcRate3());
					htmlName = "#percentFact" + per.getId() + "effRate";
				}
				else if ("КТР".equals(key.getKey())) {
					value = Formatter.format(per.getRate11());
					htmlName = res.getHtmlName() + "_rate11";
				}
				else if ("Обеспечение по периоду".equals(key.getKey())) {
					value = per.getSupply();
					htmlName = res.getHtmlName() + "_supply";
				}
				
				else if ("Компенсирующий спрэд за фиксацию процентной ставки".equals(key.getKey())) {
					value = Formatter.format(per.getRate5());
					htmlName = res.getHtmlName() + "_rate5";
				}
				else if ("Компенсирующий спрэд за досрочное погашение".equals(key.getKey())) {
					value = Formatter.format(per.getRate6());
					htmlName = res.getHtmlName() + "_rate6";
				}
				else if ("Покрытие прямых расходов".equals(key.getKey())) {
					TaskJPA taskJPA = (TaskJPA) context.get(ContextKey.JPA);
					if (taskJPA != null)
						value = Formatter.format(taskJPA.getRate7());
					htmlName = res.getHtmlName() + "_rate7";
				}
				else if ("Покрытие общебанковских расходов".equals(key.getKey())) {
					TaskJPA taskJPA = (TaskJPA) context.get(ContextKey.JPA);
					if (taskJPA != null)
						value = Formatter.format(taskJPA.getRate8());
					htmlName = res.getHtmlName() + "_rate8";
				}
				else if ("Комиссия за выдачу".equals(key.getKey())) {
					value = Formatter.format(per.getRate9());
					htmlName = res.getHtmlName() + "_rate9";
				}
				else if ("Комиссия за сопровождение".equals(key.getKey())) {
					value = Formatter.format(per.getRate10());
					htmlName = res.getHtmlName() + "_rate10";
				}

			}
			res.getStructure().put(key.getKey(), new ObjectElement(value, htmlName));
		}
		return res;
	}

	public static ObjectElement toCompareObject(ProductGroupJPA prodGroup, HeaderElement head,
			Map<ContextKey, Object> context) {
		ObjectElement res = new ObjectElement();
		res.setValue(prodGroup.getName());
		res.setHtmlList("#compare_list_limitparam_prodperiod");
		res.setHtmlName("#compare_limitparam_prodperiod" + prodGroup.getId());
		for (HeaderElement key : head.getKeys()) {
			String value = "";
			String htmlName = "";
			if (key.isKey() && key.getKey() != null) {
				if ("До".equals(key.getKey())) {
					value = Formatter.format(prodGroup.getPeriod()) + " дн.";
					htmlName = "#compare_limitparam_prodperiod" + prodGroup.getId() + "_period";
				}
				else if ("Комментарий по сроку сделки".equals(key.getKey())) {
					value = prodGroup.getCmnt();
					htmlName = "#compare_limitparam_prodperiod" + prodGroup.getId() + "_cmnt";
				}
			}
			res.getStructure().put(key.getKey(), new ObjectElement(value, htmlName));
		}
		return res;
	}

	public static ObjectElement toCompareObject(Trance trance, HeaderElement head,
			Map<ContextKey, Object> context, int i) {
		ObjectElement res = new ObjectElement();
		res.setValue(String.valueOf(i));
		res.setHtmlName("#compare_prodparam_trance" + String.valueOf(i));
		res.setHtmlList("#compare_list_prodparam_trance");
		for (HeaderElement key : head.getKeys()) {
			ObjectElement subRes = new ObjectElement();
			if (!key.isKey()) {
				if ("Номер выдачи".equals(key.getKey())) {
					int j = 0;
					for (Withdraw withdraw : trance.getWithdraws()) {
						subRes.getStructure().put("№" + String.valueOf(++j), toCompareObject(withdraw, key, context,
								res.getHtmlList() + String.valueOf(j) + "_withdraw", j));
					}
					subRes.setValue("Выдачи");
				}
			}
			res.getStructure().put(key.getKey(), subRes);
		}
		return res;
	}

	public static ObjectElement toCompareObject(Withdraw withdraw, HeaderElement head,
			Map<ContextKey, Object> context, String listName, int j) {
		ObjectElement res = new ObjectElement();
		res.setValue(String.valueOf(j));
		res.setHtmlName("#compare_prodparam_withdraw" + withdraw.getId());
		res.setHtmlList(listName);
		for (HeaderElement key : head.getKeys()) {
			String value = "";
			String htmlName = "";
			if (key.isKey() && key.getKey() != null) {
				if ("Сумма".equals(key.getKey())) {
					value = withdraw.getSumFormated();
					htmlName = res.getHtmlName() + "_sum";
				}
				else if ("Валюта транша".equals(key.getKey())) {
					value = withdraw.getCurrency();
					htmlName = res.getHtmlName() + "_sum";
				}
				else if ("С даты".equals(key.getKey())) {
					value = Formatter.str(withdraw.getUsedatefrom());
					htmlName = res.getHtmlName() + "_period";
				}
				else if ("По дату".equals(key.getKey())) {
					value = Formatter.str(withdraw.getUsedateto());
					htmlName = res.getHtmlName() + "_period";
				}
			}
			res.getStructure().put(key.getKey(), new ObjectElement(value, htmlName));
		}
		return res;
	}

	public static ObjectElement toCompareObjectParent(TaskJPA parent, HeaderElement head,
			Map<ContextKey, Object> context, boolean isProduct) {
		ObjectElement res = new ObjectElement();
		res.setValue(Long.toString(parent.getId()));
		res.setHtmlName("#compare_in_limit"+parent.getId());
		res.setHtmlList("#compare_list_in_limit");
		for (HeaderElement key : head.getKeys()) {
			String value = "";
			String htmlName = "";
			if (key.isKey() && key.getKey() != null) {
				if ("Контрагент".equals(key.getKey())) {
					value = parent.getOrganisation();
					htmlName = "org"+parent.getId();
				}
				else if ("Сумма".equals(key.getKey())) {
					value = parent.getSumWithCurrency();
					htmlName = "sum"+parent.getId();
				}
				else if ("Срок, дней".equals(key.getKey())) {
					if (parent.getPeriod() != null)
					value = parent.getPeriod().toString() + " " + (parent.getPeriodDimension() != null ? 
							parent.getPeriodDimension() : "дн.");
					htmlName = "period"+parent.getId();
				}
				else if ("Сделка сублимита".equals(key.getKey())) {
					if (isProduct) {
						boolean hasSublimits = false;
						for (TaskJPA c : parent.getChilds()) {
							if (!c.isDeleted() && !c.isProduct())
								hasSublimits = true;
						}
						value = CompareHelper.boolToString(!hasSublimits && !"Сделка".equals(parent.getType()));
					}
				}
			}
			res.getStructure().put(key.getKey(), new ObjectElement(value, htmlName));
		}
		return res;
	}

	/**
	 * Метод для преобразования контрагента в объект для сравнения
	 * @param cntr контрагент
	 * @param head перечень полей
	 * @param context дополнительные объекты
	 * @param isFirst является основным
	 * @return объект для сравнения
	 */
	public static ObjectElement toCompareObject(TaskContractor cntr, HeaderElement head,
			Map<ContextKey, Object> context, boolean isFirst) {
		String org = cntr.getOrg().getAccountid();
		Date ratingDate = new java.util.Date();
		RatingService ratingService = null;
		if (Config.enableIntegration())
			ratingService = ru.masterdm.integration.ServiceFactory.getService(RatingService.class);
		CalcHistoryInput input = new CalcHistoryInput();
		input.setPartnerId(org);
		input.setRDate(ratingDate);
		CalcHistoryOutput outputKeki = null;
		try {
			if (ratingService != null)
				outputKeki = ratingService.getKEKICalcHistory(input);
		}
		catch (Exception e) {
			LOGGER.error("Ошибка при получении рейтинга", e);
		}
		ObjectElement res = new ObjectElement();
		String accountId = cntr.getOrg().getAccountid();
		res.setHtmlName("#" + accountId);
		res.setHtmlList("#compare_list_contractor");
		for (HeaderElement key : head.getKeys()) {
			ObjectElement subRes = new ObjectElement();
			if (key.isKey() && key.getKey() != null) {
				String value = "";
				String htmlName = "";
				if ("Наименование Контрагента".equals(key.getKey())) {
					value = cntr.getOrg().getAccount_name();
					htmlName = "#ClientName" + accountId;
				}
				else if ("Класс Контрагента".equals(key.getKey())) {
					CompendiumCrmActionProcessor compendiumCrm = (CompendiumCrmActionProcessor) context
							.get(ContextKey.COMPENDIUM_CRM);
					value = compendiumCrm.findOrganization(cntr.getOrg().getAccountid()).getClientCategory();
					htmlName = "#ClientCategory" + accountId;
				}
				else if ("Отрасль экономики СРР".equals(key.getKey())) {
					if (outputKeki != null)
						value = outputKeki.getBranch();
					htmlName = "#Branch" + accountId;
				}
				else if ("Регион СРР".equals(key.getKey())) {
					if (outputKeki != null)
						value = outputKeki.getRegion();
					htmlName = "#Region" + accountId;
				}
				else if ("Рейтинг кредитного подразделения".equals(key.getKey())) {
					if (outputKeki != null)
						value = outputKeki.getRating();
					htmlName = "#Ratingcalculated" + accountId;
				}
				else if ("Рейтинг подразделения рисков".equals(key.getKey())) {
					CalcHistoryOutput output = null;
					if (ratingService != null) {
						try {
							output = ratingService.getSEKZCalcHistory(input);
						}
						catch (Exception e) {
							LOGGER.error("Ошибка при получении рейтинга", e);
						}
						if (output != null)
							value = output.getRating();
					}
					htmlName = "#Ratingexp" + accountId;
				}
				else if ("Утвержденный рейтинг".equals(key.getKey())) {
					TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory
							.getActionProcessor("Task");
					ApprovedRating ar = processor.getApprovedRating(ratingDate, org);
					if (ar != null) {
						value = ar.getRating();
					}
					htmlName = "#RatingApproved" + accountId;
				}
				else if ("Рейтинг ПКР".equals(key.getKey())) {
					value = cntr.getRatingPKR();
					htmlName = "#RatingPKR" + accountId;
				}
				else if ("Прикр. док.".equals(key.getKey())) {
					value = ((PupFacadeLocal) context.get(ContextKey.PUP_FACADE_LOCAL))
							.findAttachemntCountByOwnerAndType(cntr.getOrg().getAccountid(), 1l).toString();
					htmlName = "#Attach" + accountId;
				}
				subRes = new ObjectElement(value, htmlName);
			}
			else {
				if ("Наименование типа".equals(key.getKey())) {
					if (isFirst)
						subRes.getStructure().put("Основной заемщик", new ObjectElement(null, ""));
					for (ContractorType ct : (ContractorType[]) context.get(ContextKey.ALL_CONTRACTOR_TYPES)) {
						if (isFirst && ct.getId().longValue() == 1)
							continue;
						if (cntr.getOrgType().contains(ct)) {
							subRes.getStructure().put(ct.getDescription(), new ObjectElement("", "#compare_contractor" 
									+ cntr.getId() + "_type" + ct.getId().toString(), "#compare_list_contractor" 
											+ cntr.getOrg().getId() + "_type"));
						}
					}
					subRes.setValue("Тип контрагента");
					subRes.setHtmlName("#ClientType" + accountId);
				}
			}
			res.getStructure().put(key.getKey(), subRes);
		}
		return res;
	}

	public static ObjectElement toCompareObject(CompanyGroup group, HeaderElement head,
			Map<ContextKey, Object> context) {
		ObjectElement res = new ObjectElement();
		res.setHtmlName("#CompanyGroup" + group.getId());
		res.setHtmlList("#compare_list_contractor_group");
		for (HeaderElement key : head.getKeys()) {
			ObjectElement subRes = new ObjectElement();
			if (key.isKey() && key.getKey() != null) {
				String value = "";
				if ("Отрасль экономики СРР".equals(key.getKey())) {
					value = "";
				}
				else if ("Регион СРР".equals(key.getKey())) {
					value = "";
				}
				else if ("Рейтинг КО".equals(key.getKey())) {
					value = "";
				}
				else if ("Экспертный рейтинг".equals(key.getKey())) {
					value = "";
				}
				else if ("Расчетный рейтинг".equals(key.getKey())) {
					value = "";
				}
				else if ("Дата рейтинга".equals(key.getKey())) {
					value = "";
				}
				subRes = new ObjectElement(value);
			}
			res.getStructure().put(key.getKey(), subRes);
		}
		return res;
	}

	public static ObjectElement toCompareObject(N6Request req, HeaderElement head,
			Map<ContextKey, Object> context) {
		ObjectElement res = new ObjectElement();
		for (HeaderElement key : head.getKeys()) {
			ObjectElement subRes = new ObjectElement();
			if (key.isKey() && key.getKey() != null) {
				String value = "";
				if ("Сумма сделки".equals(key.getKey())) {
					value = Formatter.toMoneyFormat(req.getAmount()) + " "
							+ Formatter.str(req.getAmountCurrency());
				}
				else if ("Плановые даты".equals(key.getKey())) {
					value = "c " + Formatter.format(req.getPlannedN6StartDate()) + " по "
							+ Formatter.format(req.getPlannedN6EndDate());
				}
				else if ("Статус".equals(key.getKey())) {
					value = Formatter.str(req.getStatus());
				}
				subRes = new ObjectElement(value);
			}
			res.getStructure().put(key.getKey(), subRes);
		}
		return res;
	}

	public static ObjectElement toCompareObject(FundingRequest req, HeaderElement head,
			Map<ContextKey, Object> context) {
		ObjectElement res = new ObjectElement();
		for (HeaderElement key : head.getKeys()) {
			ObjectElement subRes = new ObjectElement();
			if (key.isKey() && key.getKey() != null) {
				String value = "";
				if ("Тип".equals(key.getKey())) {
					value = Formatter.str(req.getType());
				}
				else if ("Категория".equals(key.getKey())) {
					value = Formatter.str(req.getCategory());
				}
				else if ("Сумма фондир.".equals(key.getKey())) {
					value = Formatter.toMoneyFormat(req.getAmount()) + " "
							+ Formatter.str(req.getAmountCurrency());
				}
				else if ("Период выдач".equals(key.getKey())) {
					value = "c " + Formatter.format(req.getPaymentPeriodStartDate()) + " по "
							+ Formatter.format(req.getPaymentPeriodEndDate());
				}
				else if ("Статус".equals(key.getKey())) {
					value = Formatter.str(req.getStatus());
				}
				else if ("Заявка действительна до".equals(key.getKey())) {
					value = Formatter.format(req.getExpirationDate());
				}
				subRes = new ObjectElement(value);
			}
			res.getStructure().put(key.getKey(), subRes);
		}
		return res;
	}

	public static ObjectElement toCompareObject(DepartmentAgreement da, HeaderElement head,
			Map<ContextKey, Object> context) {
		ObjectElement res = new ObjectElement();
		for (HeaderElement key : head.getKeys()) {
			ObjectElement subRes = new ObjectElement();
			if (key.isKey() && key.getKey() != null) {
				String value = "";
				if ("Отклоненные замечания и предложения".equals(key.getKey())) {
					value = Formatter.str(da.getRemark());
				}
				else if ("Комментарий (мотивы отклонения)".equals(key.getKey())) {
					value = Formatter.str(da.getComment());
				}
				subRes = new ObjectElement(value);
			}
			res.getStructure().put(key.getKey(), subRes);
		}
		return res;
	}

	public static ObjectElement toCompareObject(Expertus expertus, HeaderElement head,
			Map<ContextKey, Object> context) {
		ObjectElement res = new ObjectElement();
		for (HeaderElement key : head.getKeys()) {
			ObjectElement subRes = new ObjectElement();
			if (key.isKey() && key.getKey() != null) {
				String value = "";
				if ("Начало".equals(key.getKey())) {
					value = Formatter.str(expertus.getDataStart());
				}
				else if ("Окончание".equals(key.getKey())) {
					value = Formatter.str(expertus.getDataEnd());
				}
				else if ("Эксперт".equals(key.getKey())) {
					value = Formatter.str(expertus.getUser().getFullName());
				}
				subRes = new ObjectElement(value);
			}
			else {
				if ("Участник".equals(key.getKey())) {
					for (UserJPA user : expertus.getGroup())
						subRes.getStructure().put(user.getFullName(), new ObjectElement());
					subRes.setValue("Экспертная группа");
				}
			}
			res.getStructure().put(key.getKey(), subRes);
		}
		return res;
	}

	public static ObjectElement toCompareObject(AttachJPA attach, HeaderElement head,
			Map<ContextKey, Object> context, String branch) {
		ObjectElement res = new ObjectElement();
		for (HeaderElement key : head.getKeys()) {
			ObjectElement subRes = new ObjectElement();
			if (key.isKey() && key.getKey() != null) {
				String value = "";
				if ("Заголовок".equals(key.getKey())) {
					value = attach.getTitle();
				}
				else if ("Передается на Кредитный Комитет".equals(key.getKey())) {
					value = CompareHelper.boolToString(attach.isFORCC());
				}
				else if ("Срок действия".equals(key.getKey())) {
					value = Formatter.format(attach.getDATE_OF_EXPIRATION());
				}
				else if ("Добавил".equals(key.getKey())) {
					value = (attach.getWhoAdd() != null ? attach.getWhoAdd().getFullName() : "ЭДК") + ", "
							+ Formatter.format(attach.getDATE_OF_ADDITION());
				}
				else if ("Утвердил".equals(key.getKey())) {
					if (attach.getWhoAccepted() != null)
						value = attach.getWhoAccepted().getFullName() + ", "
								+ Formatter.format(attach.getDATE_OF_ACCEPT());
				}
				else if ("ЭЦП".equals(key.getKey())) {
					value = attach.getSIGNATURE().equals("") ? "не подписан" : "подписан";
				}
				else if ("Ветка".equals(key.getKey())) {
					value = branch;
				}
				subRes = new ObjectElement(Formatter.str(value));
			}
			res.getStructure().put(key.getKey(), subRes);
		}
		return res;
	}

	public static ObjectElement toCompareObject(StandardPeriod sp, HeaderElement head,
			Map<ContextKey, Object> context) {
		ObjectElement res = new ObjectElement();
		for (HeaderElement key : head.getKeys()) {
			ObjectElement subRes = new ObjectElement();
			if (key.isKey() && key.getKey() != null) {
				String value = "";
				if ("Нормативный срок (дни)".equals(key.getKey())) {
					value = sp.getStandardPeriodDisplay() + "\n" + sp.getWhoChangeDisplay();
				}
				else if ("Фактический срок (дни)".equals(key.getKey())) {
					value = sp.getFactPeriodDisplay();
				}
				else if ("Критерий дифференциации срока".equals(key.getKey())) {
					value = sp.getCriteria();
				}
				else if ("Исполнители, роли".equals(key.getKey())) {
					value = sp.getUsersDisplay();
				}
				subRes = new ObjectElement(Formatter.str(value));
			}
			res.getStructure().put(key.getKey(), subRes);
		}
		return res;
	}

	public static ObjectElement toCompareObject(ProjectTeamJPA team, HeaderElement head,
			Map<ContextKey, Object> context, String section) {
		ObjectElement res = new ObjectElement();
		TaskJPA taskJPA = (TaskJPA) context.get(ContextKey.JPA);
		for (HeaderElement key : head.getKeys()) {
			ObjectElement subRes = new ObjectElement();
			if (key.isKey() && key.getKey() != null) {
				String value = "";
				if ("Подразделение".equals(key.getKey())) {
					value = team.getUser().getDepartment().getShortName();
				}
				subRes = new ObjectElement(value);
			}
			else {
				if ("Роль".equals(key.getKey())) {
					for (RoleJPA role : team.getUser().getRoles()) {
						if (!role.getProcess().getIdTypeProcess().equals(taskJPA.getIdTypeProcess())) {
							continue;
						}
						if (section.equals("p")
								&& !TaskHelper.dict().findProjectTeamRoles().contains(role.getNameRole())) {
							continue;
						}
						if (section.equals("m")
								&& !TaskHelper.dict().findMiddleOfficeRoles().contains(role.getNameRole())) {
							continue;
						}
						if (section.equals("m")
								|| (section.equals("p") && ((PupFacadeLocal) context
										.get(ContextKey.PUP_FACADE_LOCAL)).isAssigned(team.getUser().getIdUser(),
										role.getIdRole(), taskJPA.getProcess().getId()))) {
							subRes.getStructure().put(role.getNameRole(), new ObjectElement());
						}
					}
					subRes.setValue("Роли пользователя");
				}
			}
			res.getStructure().put(key.getKey(), subRes);
		}
		return res;
	}

	public static ObjectElement toCompareObject(ConditionTypeJPA type,
			ArrayList<OtherCondition> otherConditions, HeaderElement head, Map<ContextKey, Object> context, String sectionName) {
		
		ObjectElement res = new ObjectElement();
		for (HeaderElement key : head.getKeys()) {
			ObjectElement subRes = new ObjectElement();
			if (!key.isKey()) {
				if ("Условие".equals(key.getKey())) {
					int j = 0;
					for (OtherCondition c : otherConditions)
						if (c.getType().equals(type.getId_type()) && c.getSupplyCode() == null) {
							String k = type.getName() + " №" + String.format("%02d", ++j);
							subRes.getStructure().put(k, toCompareObject(c, key, context, k, sectionName));
						}
					subRes.setValue("Условия");
				}
			}
			res.getStructure().put(key.getKey(), subRes);
		}
		return res;
	}

	public static ObjectElement toCompareObject(Comment comment, HeaderElement head,
			Map<ContextKey, Object> context) {
		ObjectElement res = new ObjectElement();
		for (HeaderElement key : head.getKeys()) {
			ObjectElement subRes = new ObjectElement();
			if (key.isKey() && key.getKey() != null) {
				String value = "";
				if ("Автор".equals(key.getKey())) {
					value = comment.getAuthor().getName();
				}
				else if ("Дата".equals(key.getKey())) {
					value = Formatter.format(new Date(comment.getWhen().getTime()));
				}
				else if ("С операции".equals(key.getKey())) {
					value = comment.getStagename();
				}
				else if ("Текст".equals(key.getKey())) {
					value = comment.getBody();
				}
				subRes = new ObjectElement(value);
			}
			res.getStructure().put(key.getKey(), subRes);
		}
		return res;
	}

	/**
	 *
	 *
	 * @param operDecisionJPA
	 * @param key
	 * @param context
	 * @return
	 */
	public static ObjectElement toCompareObject(OperDecisionJPA operDecisionJPA, HeaderElement head,
			Map<ContextKey, Object> context, int i) {
		ObjectElement res = new ObjectElement();
		res.setValue("Порядок принятия решения №" + (i + 1));
		res.setHtmlName("#compare_limitparam_operdecision" + i);
		res.setHtmlList("#compare_list_limitparam_operdecision");
		for (HeaderElement key : head.getKeys()) {
			ObjectElement subRes = new ObjectElement();
			if (key.isKey() && key.getKey() != null) {
				String value = "";
				String htmlName = "";
				if ("принимаются".equals(key.getKey())) {
					value = operDecisionJPA.getAccepted();
					htmlName = "#compare_limitparam_operdecision" + i + "_accepted";
				}
				else if ("особенности принятия решений".equals(key.getKey())) {
					value = operDecisionJPA.getSpecials();
					htmlName = "#compare_limitparam_operdecision" + i + "_specials";
				}
				subRes = new ObjectElement(value, htmlName);
			}
			else {
				if ("Решения о/об".equals(key.getKey())) {
					for(OperDecisionDescriptionJPA desc : operDecisionJPA.getDescriptions())
						subRes.getStructure().put(desc.getDescr(), new ObjectElement(desc.getDescr(),
								"#compare_limitparam_operdecision" + i + "_descr_" + desc.getId(),
								"#compare_list_limitparam_operdecision" + i + "_descr"));
					subRes.setValue("Решения");
				}
			}
			res.getStructure().put(key.getKey(), subRes);
		}
		return res;
	}

}