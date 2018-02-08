package ru.md.compare;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.uit.director.tasks.TaskInfo;

import ru.masterdm.compendium.domain.Department;
import ru.masterdm.compendium.domain.crm.CompanyGroup;
import ru.masterdm.compendium.domain.crm.FloatPartOfActiveRate;
import ru.masterdm.compendium.model.CompendiumActionProcessor;
import ru.masterdm.integration.ServiceFactory;
import ru.masterdm.integration.funding.FundingService;
import ru.masterdm.integration.funding.ws.FundingRequest;
import ru.masterdm.integration.funding.ws.FundingRequestFilter;
import ru.masterdm.integration.funding.ws.N6Request;
import ru.md.compare.CompareTaskKeys.CompareTaskBlock;
import ru.md.compare.CompareTaskKeys.ContextKey;
import ru.md.controller.PipelineController;
import ru.md.dict.dbobjects.ConditionTypeJPA;
import ru.md.dict.dbobjects.RiskStepupFactorJPA;
import ru.md.domain.OtherGoal;
import ru.md.helper.CompareHelper;
import ru.md.helper.TaskHelper;
import ru.md.pup.dbobjects.AttachJPA;
import ru.md.pup.dbobjects.DepartmentJPA;
import ru.md.pup.dbobjects.DocumentGroupJPA;
import ru.md.pup.dbobjects.DocumentTypeJPA;
import ru.md.pup.dbobjects.UserJPA;
import ru.md.spo.dbobjects.*;
import ru.md.spo.ejb.PupFacadeLocal;
import ru.md.spo.ejb.StandardPeriodBeanLocal;
import ru.md.spo.report.Expertus;

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
import com.vtb.domain.TaskCurrency;
import com.vtb.domain.TaskProduct;
import com.vtb.domain.TaskStopFactor;
import com.vtb.domain.Trance;
import com.vtb.domain.Warranty;
import com.vtb.exception.FactoryException;
import com.vtb.util.Formatter;

/**
 * Класс-помощник для преобразования заявок
 * @author rislamov
 */
public class CompareTaskBlockHelper {

	private static Logger logger = Logger.getLogger(CompareTaskBlockHelper.class.getCanonicalName());

	public static ObjectElement mainToCompareObject(Task task, Map<ContextKey, Object> context) {
		HeaderElement head = CompareTaskKeys.getTaskHeader(CompareTaskBlock.MAIN,
				task.getHeader() != null ? task.getHeader().isOpportunity() : false);
		ObjectElement resMain = new ObjectElement();
		for (HeaderElement key : head.getKeys()) {
			if (key.isKey() && key.getKey() != null) {
				String value = "";
				/** MAIN */
				if ("Номер заявки".equals(key.getKey())) {
					if (context.get(ContextKey.JPA) != null)
						value = ((TaskJPA) context.get(ContextKey.JPA)).getNumberDisplayWithRoot();
				}
				else if ("Бизнес-процесс".equals(key.getKey())) {
					if (context.get(ContextKey.JPA) != null) {
						TaskJPA taskJPA = (TaskJPA) context.get(ContextKey.JPA);
						while (taskJPA.isSublimit() && taskJPA.getParent() != null)
							taskJPA = taskJPA.getParent();
						if (taskJPA.getProcess() != null)
							value = taskJPA.getProcess().getProcessType().getDescriptionProcess();
					}
				}
				else if ("Статус".equals(key.getKey())) {
					if (context.get(ContextKey.PUP_STATUS) != null)
						value = (String) context.get(ContextKey.PUP_STATUS);
				}
				else if ("Операция".equals(key.getKey())) {
					TaskInfo taskInfo = (TaskInfo) context.get(ContextKey.TASK_INFO);
					if (taskInfo != null)
						value = (taskInfo.getNameStageTo() != null) ? taskInfo.getNameStageTo() : "";
				}
				else if ("Принята к обработке".equals(key.getKey())) {
					TaskInfo taskInfo = (TaskInfo) context.get(ContextKey.TASK_INFO);
					if (taskInfo != null)
						value = taskInfo.getDateOfCommingStr();
				}
				else if ("В рамках лимита".equals(key.getKey())) {
					value = CompareHelper.boolToString(task.getParent() != null);
				}
				else if ("Лимит включает Сублимиты".equals(key.getKey())) {
					if (context.get(ContextKey.JPA) != null)
						value = CompareHelper.boolToString(((TaskJPA) context.get(ContextKey.JPA))
								.isWithSublimit());
				}
				else if ("Индивидуальные условия".equals(key.getKey())) {
					if (context.get(ContextKey.JPA) != null)
						value = CompareHelper.boolToString(((TaskJPA) context.get(ContextKey.JPA))
								.isIndcondition());
				}
				else if ("Приоритет".equals(key.getKey())) {
					if (context.get(ContextKey.PUP_PRIORITY) != null)
						value = (String) context.get(ContextKey.PUP_PRIORITY);
				}
				resMain.getStructure().put(key.getKey(), new ObjectElement(value));
			}
		}
		return resMain;
	}

	public static ObjectElement contractorsToCompareObject(Task task, Map<ContextKey, Object> context) {
		HeaderElement head = CompareTaskKeys.getTaskHeader(CompareTaskBlock.CONTRACTORS, task
				.getHeader() != null ? task.getHeader().isOpportunity() : false);
		ObjectElement resMain = new ObjectElement();
		for (HeaderElement key : head.getKeys()) {
			if (key.isKey() && key.getKey() != null) {
				String value = "";
				String htmlName = "";
				/** CONTRAGENTS */
				if ("Распространяется на третьи лица".equals(key.getKey())) {
					value = CompareHelper.boolToString(task.isFaces3());
					htmlName = "#compare_3faces";
				}
				else if ("Страновая принадлежность".equals(key.getKey())) {
					if (task.getMain() != null) {
						value = task.getMain().getCountry();
						htmlName = "#compare_main_country";
					}
				}
				resMain.getStructure().put(key.getKey(), new ObjectElement(value, htmlName));
			}
			else {
				// извлечение структурированных блоков
				ObjectElement res = new ObjectElement();
				/** CONTRAGENTS */
				if ("Наименование Контрагента".equals(key.getKey())) {
					if (task.getContractors() != null)
						for (int i = 0; i < task.getContractors().size(); i++) {
							res.getStructure().put(
									task.getContractors().get(i).getOrg().getAccount_name(),
									CompareTaskHelper.toCompareObject(task.getContractors().get(i), key, context,
											i == 0));
						}
					res.setValue("Контрагенты");
				}
				else if ("Наименование группы".equals(key.getKey())) {
					if (task.getContractors() != null)
						for (TaskContractor cntr : task.getContractors())
							for (CompanyGroup group : cntr.getGroupList())
								res.getStructure().put(group.getName(),
										CompareTaskHelper.toCompareObject(group, key, context));
					res.setValue("Группы");
				}
				resMain.getStructure().put(key.getKey(), res);
			}
		}
		return resMain;
	}

	public static ObjectElement inLimitToCompareObject(Task task, Map<ContextKey, Object> context) {
		HeaderElement head = CompareTaskKeys.getTaskHeader(CompareTaskBlock.IN_LIMIT,
				task.getHeader() != null ? task.getHeader().isOpportunity() : false);
		ObjectElement resMain = new ObjectElement();
		for (HeaderElement key : head.getKeys()) {
			if (key.isKey() && key.getKey() != null) {
				String value = "";
				/** IN_LIMIT */
				if ("Сделка проводится в рамках лимита".equals(key.getKey())) {
					if (context.get(ContextKey.JPA) != null
							&& ((TaskJPA) context.get(ContextKey.JPA)).isProduct())
						value = CompareHelper
								.boolToString(((TaskJPA) context.get(ContextKey.JPA)).getParent() != null);
				}
				resMain.getStructure().put(key.getKey(), new ObjectElement(value));
			}
			else {
				// извлечение структурированных блоков
				ObjectElement res = new ObjectElement();
				/** IN_LIMIT */
				if ("Номер лимита/сублимита/сделки".equals(key.getKey())) {
					TaskJPA taskJPA = (TaskJPA) context.get(ContextKey.JPA);
					if (taskJPA != null) {
						TaskJPA root = taskJPA;
						if (taskJPA.isProduct())
							root = taskJPA.getParent();
						while (root != null && root.getParent() != null) {
							root = root.getParent();
						}
						takeOutLimitTree(root, res, key, context, "Сделка".equals(taskJPA.getType()));
					}
					res.setValue("Лимит/сублимит/сделка");
				}
				resMain.getStructure().put(key.getKey(), res);
			}
		}
		return resMain;
	}

	private static void takeOutLimitTree(TaskJPA parent, ObjectElement res, HeaderElement key,
			Map<ContextKey, Object> context, boolean isProduct) {
		if (parent != null && !parent.isDeleted()) {
			res.getStructure().put(parent.getType() + " " + parent.getNumberDisplay(),
					CompareTaskHelper.toCompareObjectParent(parent, key, context, isProduct));
			if (parent.getChilds() != null)
				for (TaskJPA child : parent.getChilds())
					takeOutLimitTree(child, res, key, context, isProduct);
		}
	}

	public static ObjectElement parametersToCompareObject(Task task, Map<ContextKey, Object> context) {
		boolean isOpp = false;
		boolean secFlag = true;
		if (task.getHeader() != null) {
			isOpp = task.getHeader().isOpportunity();
			if (!isOpp)
				secFlag = task.isSubLimit();
		}
		HeaderElement head = CompareTaskKeys.getTaskHeader(CompareTaskBlock.PARAMETERS, isOpp, secFlag);
		ObjectElement resMain = new ObjectElement();
		for (HeaderElement key : head.getKeys()) {
			if (key.isKey() && key.getKey() != null) {
				String value = "";
				String htmlName = "";
				/** PARAMETERS - общее */
				if ("Категория сделки - проектное финансирование".equals(key.getKey())
						|| "Категория лимита - проектное финансирование".equals(key.getKey())) {
					value = CompareHelper.boolToString(task.getMain().isProjectFin());
					htmlName = "#compare_param_project_fin";
				}
				else if ("Сумма сделки".equals(key.getKey())
						|| "Cумма Лимита/Сублимита".equals(key.getKey())) {
					value = Formatter.format(task.getMain().getSum());
					htmlName = "#compare_param_sum";
				}
				else if ("Валюта сделки".equals(key.getKey()) || "Валюта Лимита".equals(key.getKey())) {
					value = task.getMain().getCurrency2().getCode();
					htmlName = "#compare_param_sum_curr";
				}
				else if ("Сумма лимита выдачи".equals(key.getKey())) {
					value = Formatter.format(task.getMain().getLimitIssueSum());
					htmlName = "#compare_param_limit_sum";
				}
				else if ("Валюта лимита выдачи".equals(key.getKey())) {
					value = task.getMain().getCurrency2().getCode();
					htmlName = "#compare_param_limit_sum_curr";
				}
				else if ("Сумма лимита задолженности".equals(key.getKey())) {
					value = Formatter.format(task.getMain().getDebtLimitSum());
					htmlName = "#compare_param_debt_limit_sum";
				}
				else if ("Валюта лимита задолженности".equals(key.getKey())) {
					value = task.getMain().getCurrency2().getCode();
					htmlName = "#compare_param_debt_limit_sum_curr";
				}
				else if ("Категория качества ссуды".equals(key.getKey())) {
					value = Formatter.str(task.getGeneralCondition().getQuality_category());
					htmlName = "#compare_param_qualitycategory";
				}
				else if ("Описание категории качества".equals(key.getKey())) {
					value = Formatter.str(task.getGeneralCondition().getQuality_category_desc());
					htmlName = "#compare_param_qualitycategory_descr";
				}
				/** PARAMETERS - сделка */
				else if ("Вид продукта (сделки)".equals(key.getKey())) {
					if (task.getHeader().getOpportunityTypes().size() != 0) {
						TaskProduct ot = task.getHeader().getOpportunityTypes().get(0);
						value = Formatter.str(ot.getName());
					}
					htmlName = "#compare_param_opptype";
				}
				else if ("Кредитная линия с лимитом выдачи".equals(key.getKey())) {
					value = CompareHelper.boolToString(task.getMain().isLimitIssue());
					htmlName = "#opPrmIsLimitIssue";
				}
				else if ("Кредитная линия с лимитом задолженности".equals(key.getKey())) {
					value = CompareHelper.boolToString(task.getMain().isDebtLimit());
					htmlName = "#opPrmIsDebtLimit";
				}
				else if ("Нестандартная сделка".equals(key.getKey())) {
					value = CompareHelper.boolToString(task.getMain().isIrregular());
					htmlName = "#opIrregularSpan";
				}
				else if ("Вид продукта сделки (Нестандарт)".equals(key.getKey())) {
					if (task.getMain().isIrregular())
						value = Formatter.str(task.getMain().getProduct_name());
					htmlName = "#compare_prodparam_productname";
				}
				else if ("Контракт".equals(key.getKey())) {
					if (task.getMain().isGuaranteeType())
						value = task.getMain().getContract();
					htmlName = "#compare_prodparam_contract";
				}
				else if ("Предмет гарантии".equals(key.getKey())) {
					if (task.getMain().isGuaranteeType())
						value = task.getMain().getWarrantyItem();
					htmlName = "#compare_prodparam_warranty_item";
				}
				else if ("Бенефициар".equals(key.getKey())) {
					if (task.getMain().isGuaranteeType())
						value = task.getMain().getBeneficiary();
					htmlName = "#compare_prodparam_beneficiary";
				}
				else if ("ОГРН".equals(key.getKey())) {
					if (task.getMain().isGuaranteeType())
						value = task.getMain().getBeneficiaryOGRN();
					htmlName = "#compare_prodparam_beneficiary_ogrn";
				}
				else if ("Планируемая дата подписания кредитного соглашения".equals(key.getKey())) {
					value = Formatter.str(task.getMain().getProposedDateSigningAgreement());
					htmlName = "#compare_prodparam_proposed_signdate";
				}
				else if ("Срок сделки".equals(key.getKey()) || "Срок сделок до".equals(key.getKey())) {
					if (task.getMain().getPeriod() != null)
						value = Formatter.str(task.getMain().getPeriod()) + " "
								+ task.getMain().getPeriodDimension();
					htmlName = "#compare_param_period";
				}
				else if ("Срок сделки до даты".equals(key.getKey())) {
					value = Formatter.str(task.getMain().getValidto());
					htmlName = "#compare_prodparam_validto";
				}
				else if ("Комментарий по сроку сделки".equals(key.getKey())) {
					value = Formatter.str(task.getMain().getPeriodComment());
					htmlName = "#compare_prodparam_periodcomment";
				}
				else if ("Срок использования".equals(key.getKey())) {
					value = Formatter.str(task.getMain().getUseperiod());
					htmlName = "#compare_prodparam_useperiod";
				}
				else if ("Срок использования до даты".equals(key.getKey())) {
					value = Formatter.str(task.getMain().getUsedate());
					htmlName = "#compare_prodparam_usedate";
				}
				else if ("Комментарий по сроку использования".equals(key.getKey())) {
					value = Formatter.str(task.getMain().getUseperiodtype());
					htmlName = "#compare_prodparam_useperiodtype";
				}
				else if ("Комментарий по графику использования".equals(key.getKey())) {
					value = Formatter.str(task.getTranceComment());
					htmlName = "#compare_prodparam_trancecomment";
				}
				/** PARAMETERS - лимит */
				else if ("Вид Лимита/Сублимита".equals(key.getKey())) {
					if (task.getHeader().getIdLimitType() != null) {
						LinkedHashMap<String, String> types = (LinkedHashMap<String, String>) context
								.get(ContextKey.LIMIT_TYPES);
						if (types != null && types.containsKey(task.getHeader().getIdLimitType().toString()))
							value = types.get(task.getHeader().getIdLimitType().toString());
					}
					htmlName = "#compare_limitparam_limittype";
				}
				else if ("Возобновляемый Лимит/Сублимит".equals(key.getKey())) {
					value = CompareHelper.boolToString(task.getMain().isRenewable());
					htmlName = "#compare_limitparam_renewable";
				}
				else if ("Перераспределение остатков между Сублимитами".equals(key.getKey())) {
					value = CompareHelper.boolToString(task.getMain().isRedistribResidues());
					htmlName = "#compare_limitparam_redistrib_residues";
				}
				else if ("Срок заключения сделок".equals(key.getKey())) {
					value = Formatter.str(task.getMain().getValidto());
					htmlName = "#compare_limitparam_mdtask_date";
				}
				resMain.getStructure().put(key.getKey(), new ObjectElement(value, htmlName));
			}
			else {
				// извлечение структурированных блоков
				ObjectElement res = new ObjectElement();
				/** PARAMETERS - сделка */
				if ("Номер транша".equals(key.getKey())) {
					for (int i = 0; i < task.getTranceList().size(); i++) {
						res.getStructure().put("Транш №" + String.valueOf(i+1),
								CompareTaskHelper.toCompareObject(task.getTranceList().get(i), key, context, i));
					}
					res.setValue("График траншей");
				}
				/** PARAMETERS - лимит */
				else if ("Наименование группы видов сделок".equals(key.getKey())) {
					TaskJPA taskJPA = (TaskJPA) context.get(ContextKey.JPA);
					if (taskJPA != null)
						for (ProductGroupJPA prodGroup : taskJPA.getProductGroupList())
							res.getStructure().put(prodGroup.getName(), new ObjectElement(prodGroup.getName(), 
									"#compare_limitparam_prodgroup" + prodGroup.getId(), "#compare_list_limitparam_prodgroup"));
					res.setValue("Группы видов сделок");
				}
				else if ("Номер порядка принятия решения".equals(key.getKey())) {
					TaskJPA taskJPA = (TaskJPA) context.get(ContextKey.JPA);
					if (taskJPA != null)
						for (int i = 0; i < taskJPA.getOperDecision().size(); i += 1)
							res.getStructure()
									.put(
											"Порядок принятия решения №" + (i + 1),
											CompareTaskHelper.toCompareObject(taskJPA.getOperDecision().get(i), key,
													context, i));
					res.setValue("Порядок принятия решения о проведении операций в рамках сублимита");
				}
				else if ("Наименование группы вида сделки".equals(key.getKey())) {
					TaskJPA taskJPA = (TaskJPA) context.get(ContextKey.JPA);
					if (taskJPA != null)
						for (ProductGroupJPA prodGroup : taskJPA.getProductGroupList())
							res.getStructure().put(prodGroup.getName(),
									CompareTaskHelper.toCompareObject(prodGroup, key, context));
					res.setValue("Сроки сделок");
				}
				else if ("Валюта".equals(key.getKey())) {
					for (TaskCurrency curr : task.getCurrencyList())
						res.getStructure().put(curr.getCurrency().getCode(), new ObjectElement(curr.getCurrency().getCode(),
								"#compare_param_currency_" + curr.getCurrency().getCode(), "#compare_list_param_currency"));
					res.setValue(task.isOpportunity() ? "Валюта, в которой могут проводиться операции"
							: "Валюта, в которой могут проводиться сделки");
				}
				/** PARAMETERS - общее */
				else if ("Наименование целевого назначения".equals(key.getKey())) {
					int i = 0;
					for (OtherGoal otherGoal : task.getMain().getOtherGoals()) {
						res.getStructure().put(otherGoal.getGoal(), new ObjectElement(otherGoal.getGoal(),
								"#compare_param_othergoal_" + (i++), "#compare_list_param_othergoal"));
					}
					res.setValue("Целевые назначения");
				}
				resMain.getStructure().put(key.getKey(), res);
			}
		}
		return resMain;
	}

	public static ObjectElement priceConditionsToCompareObject(Task task,
			Map<ContextKey, Object> context) {
		ObjectElement resMain = new ObjectElement();
		TaskJPA taskJPA = (TaskJPA) context.get(ContextKey.JPA);
		boolean firstFlag = taskJPA.isProduct();
		boolean secondFlag = firstFlag || taskJPA.isSublimit();
		HeaderElement head = CompareTaskKeys.getTaskHeader(CompareTaskBlock.PRICE_CONDITIONS,
				firstFlag, secondFlag);
		FactPercentJPA onlyPeriod = null;
		boolean isFirst = true;
		for (FactPercentJPA per : taskJPA.getFactPercents()) {
			if (per.getTranceId() != null) {
				continue;
			}
			if (isFirst) {
				isFirst = false;
				onlyPeriod = per;
			}
			else {
				onlyPeriod = null;
				break;
			}
		}
		for (HeaderElement key : head.getKeys()) {
			if (key.isKey() && key.getKey() != null) {
				String value = "";
				String htmlName = "";
				/** PRICE_CONDITIONS - сделка */
				if (task.isOpportunity()) {
					if ("Тип ставки фиксированная".equals(key.getKey())) {
						value = ((TaskJPA) context.get(ContextKey.JPA)).isInterestRateFixed() ? "да"
								: "нет";
						htmlName = "#compare_prodprice_interestRateFixed";
					}
					else if ("Тип ставки плавающая".equals(key.getKey())) {
						value = ((TaskJPA) context.get(ContextKey.JPA)).isInterestRateDerivative() ? "да"
								: "нет";
						htmlName = "#compare_prodprice_interestRateDerivative";
					}
					else if ("Индикативная ставка".equals(key.getKey())) {
						String indRates = "";
						FloatPartOfActiveRate[] floatPartOfActiveRateArray = (FloatPartOfActiveRate[]) context
								.get(ContextKey.FLOAT_PART_OF_ACTIVE_RATE_LIST);
						for (FloatPartOfActiveRate fpar : floatPartOfActiveRateArray)
							for (IndrateMdtaskJPA indrate : taskJPA.getIndrates()){
								if(indrate.getIdFactpercent()!=null || !taskJPA.isInterestRateDerivative()){continue;}
								if (fpar.getId().equals(indrate.getIndrate())){
									String s = fpar.getText();
									String details = "";
									if (indrate.getRate() != null)
										details += Formatter.format(indrate.getRate()) + " % годовых";
									if (indrate.getUsefrom() != null)
										details += " Применяется с " + Formatter.format(indrate.getUsefrom());
									if (!Formatter.str(indrate.getReason()).isEmpty())
										details += " Основание: " + indrate.getReason();
									if (!details.isEmpty())
										s += " (" + details + ")";
									indRates += s + "<br />";
								}
							}
						value = indRates;
						htmlName = "#compare_prodprice_indrate";
					}
					else if ("Надбавка к плавающей ставке".equals(key.getKey())) {
						String indRates = "";
						if(taskJPA.getPeriods().size() < 2) {
							FloatPartOfActiveRate[] floatPartOfActiveRateArray = (FloatPartOfActiveRate[]) context
									.get(ContextKey.FLOAT_PART_OF_ACTIVE_RATE_LIST);
							for (FloatPartOfActiveRate fpar : floatPartOfActiveRateArray)
								for (IndrateMdtaskJPA indrate : taskJPA.getIndrates()) {
									if (indrate.getIdFactpercent() != null || !taskJPA.isInterestRateDerivative()) {
										continue;
									}
									if (fpar.getId().equals(indrate.getIndrate()))
										indRates += fpar.getText() + ": " + (indrate.getRate() == null ?
												"не установлена<br />" :
												indrate.getRate() + " % годовых<br />");
								}
						}
						value = indRates;
						htmlName = "#compare_prodprice_indrate" + "_rate_ind_rate";
					}
					else if ("Ставка зафиксирована".equals(key.getKey())) {
						value = CompareHelper.boolToString(taskJPA.isFixrate());
						htmlName = "#compare_prodprice_isfixed";
					}
					else if ("Дата фиксации процентной ставки".equals(key.getKey())) {
						value = Formatter.format(taskJPA.getFixratedate());
						htmlName = "#compare_prodprice_fixrate_date";
					}
					else if ("Решение о понижении ставки".equals(key.getKey())) {
						value = taskJPA.getRate_desc_decision();
						htmlName = "#compare_prodprice_ratedesc_decision";
					}
					else if ("Компенсирующий спрэд за фиксацию процентной ставки".equals(key.getKey())) {
						value = Formatter.format(taskJPA.getRate5());
						htmlName = "#compare_prodprice_rate5";
					}
					else if ("Компенсирующий спрэд за досрочное погашение".equals(key.getKey())) {
						value = Formatter.format(taskJPA.getRate6());
						htmlName = "#compare_prodprice_rate6";
					}
					else if ("Покрытие прямых расходов".equals(key.getKey())) {
						value = Formatter.format(taskJPA.getRate7());
						htmlName = "#compare_prodprice_rate7";
					}
					else if ("Покрытие общебанковских расходов".equals(key.getKey())) {
						value = Formatter.format(taskJPA.getRate8());
						htmlName = "#compare_prodprice_rate8";
					}
					else if ("Комиссия за выдачу".equals(key.getKey())) {
						value = Formatter.format(taskJPA.getRate9());
						htmlName = "#compare_prodprice_rate9";
					}
					else if ("Комиссия за сопровождение".equals(key.getKey())) {
						value = Formatter.format(taskJPA.getRate10());
						htmlName = "#compare_prodprice_rate10";
					}
					// вынесено в атрибуты заявки, если период один
					else if ("Расчетная ставка".equals(key.getKey())) {
						if (onlyPeriod != null) {
							value = Formatter.format(onlyPeriod.getCalcRate1());
							htmlName = "#percentFact" + onlyPeriod.getId() + "calcRate";
						}
					}
					else if ("Расчетная защищенная ставка".equals(key.getKey())) {
						if (onlyPeriod != null) {
							value = Formatter.format(onlyPeriod.getCalcRate2());
							htmlName = "#percentFact" + onlyPeriod.getId() + "calcRateProtected";
						}
					}
					else if ("Ставка фондирования".equals(key.getKey())) {
						if (onlyPeriod != null) {
							value = Formatter.format(onlyPeriod.getFondrate());
							htmlName = "#compare_prodprice_period" + onlyPeriod.getId() + "_fondRate";
						}
					}
					else if ("Тип премии за кредитный риск".equals(key.getKey())) {
						if (onlyPeriod != null) {
							value = onlyPeriod.getRiskpremiumTypeDisplay();
							htmlName = "#compare_prodprice_period" + onlyPeriod.getId() + "_riskpremium_type";
						}
					}
					else if ("Величина изменения".equals(key.getKey())) {
						if (onlyPeriod != null) {
							value = Formatter.format(onlyPeriod.getRiskpremium_change());
							htmlName = "#compare_prodprice_period" + onlyPeriod.getId() + "_riskpremium_change";
						}
					}
					else if ("Премия за кредитный риск".equals(key.getKey())) {
						if (onlyPeriod != null) {
							value = Formatter.format(onlyPeriod.getRiskpremium());
							htmlName = "#compare_prodprice_period" + onlyPeriod.getId() + "_riskpremium";
						}
					}
					else if ("Индивидуальные условия".equals(key.getKey())) {
						if (onlyPeriod != null) {
							value = Formatter.str(onlyPeriod.getIndcondition());
							htmlName = "#compare_prodprice_period" + onlyPeriod.getId() + "_indcond";
						}
					}
					else if ("Плата за экономический капитал".equals(key.getKey())) {
						if (onlyPeriod != null) {
							value = Formatter.format(onlyPeriod.getRate3());
							htmlName = "#compare_prodprice_period" + onlyPeriod.getId() + "_rate3";
						}
					}
					else if ("Повыш. коэфф. за риск".equals(key.getKey())) {
						if (onlyPeriod != null) {
							for (RiskStepupFactorJPA r : TaskHelper.dict().findRiskStepupFactor())
								if (r.getItem_id().equals(onlyPeriod.getRiskStepupFactorID()))
									value = r.getText();
							htmlName = "#compare_prodprice_period" + onlyPeriod.getId() + "_riskstepupfactor";
						}
					}
					else if ("Ставка размещения".equals(key.getKey())) {
						if (onlyPeriod != null) {
							value = Formatter.format(onlyPeriod.getRate4());
							/*if (!Formatter.str(onlyPeriod.getRate4Desc()).isEmpty())
								value += " " + onlyPeriod.getRate4Desc();*/
							htmlName = "#compare_prodprice_period" + onlyPeriod.getId() + "_rate4";
						}
					}
					else if ("Комментарий".equals(key.getKey())) {
						if (onlyPeriod != null) {
							value = onlyPeriod.getRate4Desc();
							htmlName = "#compare_prodprice_period" + onlyPeriod.getId() + "_rate4desc";
						}
					}
					else if ("Эффективная ставка".equals(key.getKey())) {
						if (onlyPeriod != null) {
							value = Formatter.format(onlyPeriod.getCalcRate3());
							htmlName = "#percentFact" + onlyPeriod.getId() + "effRate";
						}
					}
					else if ("КТР".equals(key.getKey())) {
						if (onlyPeriod != null) {
							value = Formatter.format(onlyPeriod.getRate11());
							htmlName = "#compare_prodprice_period" + onlyPeriod.getId() + "_rate11";
						}
					}
					else if ("Обеспечение по периоду".equals(key.getKey())) {
						if (onlyPeriod != null) {
							value = onlyPeriod.getSupply();
							htmlName = "#compare_prodprice_period" + onlyPeriod.getId() + "_supply";
						}
					}
				}
				else {
					/** PRICE_CONDITIONS - лимит */
					if ("Тип премии за кредитный риск".equals(key.getKey())) {
						value = Formatter.str(taskJPA.getRiskpremiumDisplay());
						htmlName = "#compare_limitprice_riskpremium_display";
					}
					else if ("Величина изменения".equals(key.getKey())) {
						value = Formatter.format(taskJPA.getRiskpremium_change());
						htmlName = "#compare_limitprice_riskpremium_change";
					}
					else if ("Премия за кредитный риск".equals(key.getKey())) {
						if (task.getTaskProcent() != null)
							value = task.getTaskProcent().getFormattedRiskpremium();
						htmlName = "#compare_limitprice_riskpremium_value";
					}
					else if ("Индивидуальные условия".equals(key.getKey())) {
						if (task.getTaskProcent() != null)
							value = Formatter.str(task.getTaskProcent().getPriceIndCondition());
						htmlName = "#compare_limitprice_ind_codition";
					}
					else if ("Плата за экономический капитал".equals(key.getKey())) {
						if (task.getTaskProcent() != null)
							value = Formatter.str(task.getTaskProcent().getCapitalPay());
						htmlName = "#compare_limitprice_capital_pay";
					}
					else if ("Порядок уплаты процентов".equals(key.getKey())) {
						if (task.getTaskProcent() != null)
							value = Formatter.str(task.getTaskProcent().getPay_int());
						htmlName = "#compare_limitprice_pay_int";
					}
					else if (("Порядок уплаты процентов по кредиту/кредитной линии с "
							+ "лимитом выдачи на цели формирования покрытия для осуществления платежей по аккредитивам")
							.equals(key.getKey())) {
						value = "ежемесячно";
					}
					else if ("КТР".equals(key.getKey())) {
						if (task.getTaskProcent() != null)
							value = Formatter.str(task.getTaskProcent().getKTR());
						htmlName = "#compare_limitprice_ktr";
					}
				}
				/** PRICE_CONDITIONS - общее */
				if (("Надбавка к процентной ставке за поддержание кредитовых"
						+ " оборотов менее установленного размера").equals(key.getKey())) {
					if (task.isOpportunity())
						value = Formatter.format(taskJPA.getRate2());
					else
						value = Formatter.format(taskJPA.getRate2());
					htmlName = "#compare_price_turnover";
				}
				resMain.getStructure().put(key.getKey(), new ObjectElement(value, htmlName));
			}
			else {
				// извлечение структурированных блоков
				ObjectElement res = new ObjectElement();
				/** PRICE_CONDITIONS - сделка */
				if ("Период".equals(key.getKey())) {
					if (taskJPA != null && onlyPeriod == null) {
						int i = 0;
						for (FactPercentJPA per : taskJPA.getFactPercents()) {
							if (per.getTranceId() != null) {
								continue;
							}
							res.getStructure().put("Период №" + (++i), CompareTaskHelper.toCompareObject(per,
									key, context, i, "period"));
						}
					}
					res.setValue("Периоды сделки");
				}
				else if ("Транш".equals(key.getKey())) {
					int i = 1;
					for (Trance trance : task.getTranceList()) {
						FactPercentJPA per = null;
						for (FactPercentJPA taskper : taskJPA.getFactPercents()) {
							if (taskper.getTranceId() != null
									&& taskper.getTranceId().longValue() == trance.getId().longValue()) {
								per = taskper;
							}
						}
						if (per == null) {
							per = new FactPercentJPA();
							per.setId(i * (-1L));
						}
						res.getStructure().put("Транш №" + i,
								CompareTaskHelper.toCompareObject(per, key, context, i++, "trance"));
					}
					res.setValue("Транши");
				}
				else if ("Наименование комиссии".equals(key.getKey())) {
					for (CommissionDeal com : task.getCommissionDealList()) {
						res.getStructure().put(com.getName().getName(),
								CompareTaskHelper.toCompareObject(com, key, context));
					}
					res.setValue("Комиссии");
				}
				/** PRICE_CONDITIONS - лимит */
				else if ("Номер вознаграждения".equals(key.getKey())) {
					if (taskJPA != null) {
						int i = 1;
						for (PremiumJPA p : taskJPA.getPremiumList()) {
							res.getStructure().put("Вознаграждение №" + i,
									CompareTaskHelper.toCompareObject(p, key, context, i++));
						}
						res.setValue("Типы вознаграждения");
					}
				}
				else if ("Тип комиссии".equals(key.getKey())) {
					for (Commission com : task.getCommissionList()) {
						res.getStructure().put(com.getName().getName(),
								CompareTaskHelper.toCompareObject(com, key, context));
					}
					res.setValue("Типы комиссии");
				}
				/** PRICE_CONDITIONS - общее */
				else if ("Наименование санкции (Тип штрафной санкции)".equals(key.getKey())) {
					for (Fine fine : task.getFineList()) {
						res.getStructure().put(fine.getPunitiveMeasure(),
								CompareTaskHelper.toCompareObject(fine, key, context, "price"));
					}
					res.setValue("Санкции (неустойки, штрафы, пени и т.д.)");
				}
				resMain.getStructure().put(key.getKey(), res);
			}
		}
		return resMain;
	}

	public static ObjectElement contractsToCompareObject(Task task, Map<ContextKey, Object> context) {
		HeaderElement head = CompareTaskKeys.getTaskHeader(CompareTaskBlock.CONTRACTS,
				task.getHeader() != null ? task.getHeader().isOpportunity() : false);
		ObjectElement resMain = new ObjectElement();
		for (HeaderElement key : head.getKeys()) {
			if (!key.isKey()) {
				// извлечение структурированных блоков
				ObjectElement res = new ObjectElement();
				/** CONTRACTS */
				if ("Текст договора".equals(key.getKey())) {
					TaskJPA taskJPA = (TaskJPA) context.get(ContextKey.JPA);
					if (taskJPA != null) {
						for (ContractJPA contract : taskJPA.getContracts()) {
							res.getStructure().put(contract.getContract(), new ObjectElement(contract.getContract(),
									"#compare_contract" + contract.getId(), "#compare_list_contract"));
						}
						res.setValue("Договоры");
					}
				}
				resMain.getStructure().put(key.getKey(), res);
			}
		}
		return resMain;
	}

	public static ObjectElement graphToCompareObject(Task task, Map<ContextKey, Object> context) {
		CompareTaskBlock block = (task.getHeader() != null ? task.getHeader().isOpportunity() : false) ? CompareTaskBlock.GRAPH
				: CompareTaskBlock.GRAPH_LIMIT;
		HeaderElement head = CompareTaskKeys.getTaskHeader(block, task.getHeader() != null ? task
				.getHeader().isOpportunity() : false);
		ObjectElement resMain = new ObjectElement();
		for (HeaderElement key : head.getKeys()) {
			if (key.isKey() && key.getKey() != null) {
				String value = "";
				String htmlName = "";
				/** GRAPH - сделка */
				if ("Периодичность погашения основного долга".equals(key.getKey())) {
					if (task.getPrincipalPay().getPeriodOrder() != null)
						value = task.getPrincipalPay().getPeriodOrder().getName();
					htmlName = "#compare_graph_principal_period";
				}
				else if ("Первый платеж в месяце выдачи".equals(key.getKey())) {
					value = CompareHelper.boolToString(task.getPrincipalPay().isFirstPay());
					htmlName = "#compare_graph_principal_isfirstPay";
				}
				else if ("От даты (дата первой оплаты)".equals(key.getKey())) {
					value = task.getPrincipalPay().getFirstPayDateFormatted();
					htmlName = "#compare_graph_principal_firstPay";
				}
				else if ("Дата окончательного погашения ОД".equals(key.getKey())) {
					value = task.getPrincipalPay().getFinalPayDateFormatted();
					htmlName = "#compare_graph_principal_finalDay";
				}
				else if ("Сумма платежа".equals(key.getKey())) {
					value = Formatter.toMoneyFormat(task.getPrincipalPay().getAmount());
					htmlName = "#compare_graph_principal_sum";
				}
				else if ("Сумма платежа зависит от задолженности на дату окончания срока использования"
						.equals(key.getKey())) {
					value = CompareHelper.boolToString(task.getPrincipalPay().isDepended());
					htmlName = "#compare_graph_principal_finalPay";
				}
				else if ("Амортизация ставки".equals(key.getKey())) {
					value = CompareHelper.boolToString(((TaskJPA) context.get(ContextKey.JPA))
							.isAmortized_loan());
					htmlName = "#compare_graph_principal_amortized_loan";
				}
				else if ("Порядок погашения".equals(key.getKey())) {
					value = Formatter.str(task.getPrincipalPay().getDescription());
					htmlName = "#compare_graph_principal_order_desc";
				}
				else if ("Комментарий к графику погашения".equals(key.getKey())) {
					value = Formatter.str(task.getPrincipalPay().getComment());
					htmlName = "#compare_graph_principal_comment";
				}
				/** Раздел График погашения процентов */
				else if ("Периодичность".equals(key.getKey())) {
					value = Formatter.str(task.getInterestPay().getPay_int());
					htmlName = "#compare_graph_percent_period";
				}
				else if ("От даты (дата первой оплаты процентов)".equals(key.getKey())) {
					value = task.getInterestPay().getFirstPayDateFormatted();
					htmlName = "#compare_graph_percent_firstDay";
				}
				else if ("До даты (дата окончательного погашения)".equals(key.getKey())) {
					value = task.getInterestPay().getFinalPayDateFormatted();
					htmlName = "#compare_graph_percent_finalDay";
				}
				else if ("Число уплаты процентов".equals(key.getKey())) {
					value = task.getInterestPay().getNumDayAsString();
					htmlName = "#compare_graph_percent_int_pay";
				}
				else if ("Порядок погашения процентов".equals(key.getKey())) {
					value = task.getInterestPay().getDescription();
					htmlName = "#compare_graph_percent_order_pay";
				}
				else if ("Последняя оплата в дату фактического погашения задолженности по основному долгу"
						.equals(key.getKey())) {
					value = CompareHelper.boolToString(task.getInterestPay().isFinalPay());
					htmlName = "#compare_graph_percent_finalDay";
				}
				else if ("Комментарий к графику погашения процентов".equals(key.getKey())) {
					value = Formatter.str(task.getInterestPay().getComment());
					htmlName = "#compare_graph_percent_comment";
				}
				else if ("Порядок погашения задолженности".equals(key.getKey())) {
					value = ((TaskJPA) context.get(ContextKey.JPA)).getPmnOrder();
					htmlName = "#compare_graph_pmn_order";
				}
				resMain.getStructure().put(key.getKey(), new ObjectElement(value, htmlName));
			}
			else {
				// извлечение структурированных блоков
				ObjectElement res = new ObjectElement();
				/** GRAPH - общее */
				if ("Транш".equals(key.getKey())) {
					int i = 0;
					for (Trance trance : task.getTranceList()) {
						res.getStructure().put(
								"Транш №" + (++i),
								CompareTaskHelper.toCompareObject(task.getPaymentScheduleList(), key, context,
										trance.getIdStr()));
					}
					res.setValue("График платежей по траншам");
				}
				if ("Номер платежа".equals(key.getKey())) {
					if (task.getTranceList().size() == 0) {
						int i = 0;
						for (PaymentSchedule pSchedule : task.getPaymentScheduleList()) {
							res.getStructure().put("Платеж №" + (++i),
									CompareTaskHelper.toCompareObject(pSchedule, key, context, pSchedule.getId()));
						}
					}
					res.setValue("График платежей");
				}
				if ("Порядок погашения задолженности".equals(key.getKey())) {
					resMain.getStructure().put(key.getKey(), new ObjectElement(((TaskJPA) context.get(ContextKey.JPA)).getPmnOrder(),
							"#compare_graph_pmn_order"));
				}
				resMain.getStructure().put(key.getKey(), res);
			}
		}
		return resMain;
	}

	public static ObjectElement conditionsToCompareObject(Task task, Map<ContextKey, Object> context) {
		HeaderElement head = CompareTaskKeys.getTaskHeader(CompareTaskBlock.CONDITIONS, task
				.getHeader().isOpportunity());
		ObjectElement resMain = new ObjectElement();
		for (HeaderElement key : head.getKeys()) {
			if (key.isKey() && key.getKey() != null) {
				String value = "";
				String htmlName = "";
				/** CONDITIONS */
				if ("Измененные и дополненные условия".equals(key.getKey())) {
					htmlName = "#compare_conditions_changedConditions";
					value = Formatter.str(task.getMain().getChangedConditions());
				}
				else if ("Общие условия лимита".equals(key.getKey())) {
					htmlName = "#compare_conditions_generalcondition";
					value = ((TaskJPA) context.get(ContextKey.JPA)).getGeneralcondition();
				}
				else if ("Определения".equals(key.getKey())) {
					htmlName = "#compare_conditions_definition";
					value = ((TaskJPA) context.get(ContextKey.JPA)).getDefinition();
				}
				else if ("С запретом".equals(key.getKey())) {
					value = CompareHelper.boolToString(((TaskJPA) context.get(ContextKey.JPA))
							.isEarly_payment_prohibition());
				}
				else if ("Срок запрета".equals(key.getKey())) {
					for (DependingLoanJPA loan : (List<DependingLoanJPA>) context
							.get(ContextKey.DEPENDING_LOANS)) {
						if (loan.getId().equals(
								((TaskJPA) context.get(ContextKey.JPA)).getEarly_payment_proh_per()))
							value = loan.getDays_ban_to().toString() + " дней";
					}
				}
				ObjectElement subRes = new ObjectElement(value, htmlName);
				resMain.getStructure().put(key.getKey(), subRes);
			}
			else {
				// извлечение структурированных блоков
				ObjectElement res = new ObjectElement();
				/** CONDITIONS */
				if ("Номер условия".equals(key.getKey())) {
					int i = 0;
					for (EarlyPayment ep : task.getEarlyPaymentList()) {
						res.getStructure().put("Условие №" + String.format("%02d", ++i),
								CompareTaskHelper.toCompareObject(ep, key, context, i));
					}
					res.setValue("Досрочное погашение");
				}
				else if ("Тип условия".equals(key.getKey())) {
					for (ConditionTypeJPA type : TaskHelper.dict().findConditionTypes()) {
						res.getStructure().put(type.getName(),
								CompareTaskHelper.toCompareObject(type, task.getOtherCondition(), key, context, "othercondition"));
					}
					res.setValue("Дополнительные / Отлагательные / Индивидуальные и прочие условия");
				}
				resMain.getStructure().put(key.getKey(), res);
			}
		}
		return resMain;
	}

	public static ObjectElement supplyToCompareObject(Task task, Map<ContextKey, Object> context) {
		HeaderElement head = CompareTaskKeys.getTaskHeader(CompareTaskBlock.SUPPLY, task.getHeader()
				.isOpportunity());
		ObjectElement resMain = new ObjectElement();
		for (HeaderElement key : head.getKeys()) {
			if (key.isKey() && key.getKey() != null) {
				String value = "";
				String htmlName = "";
				/** SUPPLY */
				if ("Обеспечние предусмотрено".equals(key.getKey())) {
					value = CompareHelper.boolToString(task.getSupply().isExist());
					htmlName = "#compare_supply_supply_exist_check";
				}
				else if ("Тип операции (для целей определения транзакционного риска)".equals(key.getKey())) {
					if (task.getSupply().isExist())
						value = task.getHeader().getOperationtype().getName();
					htmlName = "#compare_supply_operationtype";
				}
				else if ("Применяемый курс при пересчете в руб.".equals(key.getKey())) {
					if (task.getSupply().isExist() && task.getMain().getCurrency2() != null
							&& !task.getMain().getCurrency2().getCode().equalsIgnoreCase("RUR"))
						value = task.getMain().getFormatedExchangeRate();
					htmlName = "#compare_supply_exchangerate";
				}
				else if ("Дата курса".equals(key.getKey())) {
					if (task.getSupply().isExist() && task.getMain().getCurrency2() != null
							&& !task.getMain().getCurrency2().getCode().equalsIgnoreCase("RUR"))
						value = Formatter.format(((TaskJPA) context.get(ContextKey.JPA)).getExchangedate());
					htmlName = "#compare_supply_exchangedate";
				}
				else if ("Обеспечение из CRM".equals(key.getKey())) {
					if (task.getSupply().isExist())
						value = task.getSupply().getAdditionSupply();
					htmlName = "#compare_supply_additionsupply";
				}
				else if ("Расчетный коэффициент транзакционного риска обеспечения".equals(key.getKey())) {
					if (task.getSupply().isExist())
						value = Formatter.toMoneyFormat(task.getTaskProcent().getTrRiskC1());
					htmlName = "#compare_supply_trriskc1";
				}
				else if ("Дата расчета коэффициента".equals(key.getKey())) {
					if (task.getSupply().isExist())
						value = task.getTaskProcent().getComputeDateFormatted();
					htmlName = "#compare_supply_computedate";
				}
				else if ("Фактический коэффициент транзакционного риска".equals(key.getKey())) {
					if (task.getSupply().isExist())
						value = Formatter.toMoneyFormat(task.getSupply().getCfact());
					htmlName = "#compare_supply_cfact";
				}
				ObjectElement subRes = new ObjectElement(value, htmlName);
				resMain.getStructure().put(key.getKey(), subRes);
			}
			else {
				// извлечение структурированных блоков
				ObjectElement res = new ObjectElement();
				/** SUPPLY */
				if ("Залогодатель".equals(key.getKey())) {
					if (task.getSupply().isExist()) {
						int number = 0;
						for (Deposit d : task.getSupply().getDeposit()) {
							String mapKey = d.getOrg().getAccount_name();
							if (d.getPerson().getId() != null && d.getPerson().getId().longValue() != 0)
								mapKey = d.getPerson().getLastName();
							res.getStructure().put(mapKey, CompareTaskHelper.toCompareObject(d, key, context, ++number, mapKey));
						}
					}
					res.setValue("Залоги");
				}
				else if ("Индивидуальные условия залоговых сделок".equals(key.getKey())) {
					int i = 0;
					if (task.getSupply().isExist())
						for (OtherCondition otherCondition : task.getOtherCondition())
							if (otherCondition.getSupplyCode() != null
									&& otherCondition.getSupplyCode().equals("d")){
								String mapKey = "Индивидуальные условия " + (++i);
								res.getStructure().put(mapKey,
										CompareTaskHelper.toCompareObject(otherCondition, key, context, mapKey, "depositor"));
							}
					res.setValue("Индивидуальные условия залоговых сделок");
				}
				else if ("Поручитель".equals(key.getKey())) {
					if (task.getSupply().isExist()) {
						int number = 0;
						for (Warranty w : task.getSupply().getWarranty()) {
							number++;
							String mapKey = "";
							if (w.getPerson().getId() != null && w.getPerson().getId().longValue() != 0)
								mapKey = w.getPerson().getLastName();
							else
								mapKey = w.getOrg().getAccount_name();
							res.getStructure().put(mapKey, CompareTaskHelper.toCompareObject(w, key, context, mapKey, number));
						}
					}
					res.setValue("Поручительство");
				}
				else if ("Индивидуальные условия поручительства".equals(key.getKey())) {
					int i = 0;
					if (task.getSupply().isExist())
						for (OtherCondition otherCondition : task.getOtherCondition())
							if (otherCondition.getSupplyCode() != null
									&& otherCondition.getSupplyCode().equals("w")) {
								String mapKey = "Индивидуальные условия " + (++i);
								res.getStructure().put(mapKey,
										CompareTaskHelper.toCompareObject(otherCondition, key, context, mapKey, "warranty"));
							}
					res.setValue("Индивидуальные условия поручительства");
				}
				else if ("Гарант".equals(key.getKey())) {
					if (task.getSupply().isExist())
						for (Guarantee guarantee : task.getSupply().getGuarantee()) {
							String mapKey = "";
							if (guarantee.getPerson().getId() != null
									&& guarantee.getPerson().getId().longValue() != 0)
								mapKey = guarantee.getPerson().getLastName();
							else
								mapKey = guarantee.getOrg().getAccount_name();
							res.getStructure().put(mapKey,
									CompareTaskHelper.toCompareObject(guarantee, key, context, mapKey));
						}
					res.setValue("Гарантии");
				}
				else if ("Индивидуальные условия гарантии".equals(key.getKey())) {
					int i = 0;
					if (task.getSupply().isExist())
						for (OtherCondition otherCondition : task.getOtherCondition())
							if (otherCondition.getSupplyCode() != null
									&& otherCondition.getSupplyCode().equals("g")) {
								String mapKey = "Индивидуальные условия " + (++i);
								res.getStructure().put(mapKey,
										CompareTaskHelper.toCompareObject(otherCondition, key, context, mapKey, "garant"));
							}
					res.setValue("Индивидуальные условия гарантии");
				}
				else if ("Векселедержатель векселя Банка".equals(key.getKey())) {
					TaskJPA taskJPA = (TaskJPA) context.get(ContextKey.JPA);
					if (task.getSupply().isExist() && taskJPA != null) {
						for (PromissoryNoteJPA pn : taskJPA.getPromissoryNotes())
							res.getStructure().put(pn.getHolder(),
									CompareTaskHelper.toCompareObject(pn, key, context, pn.getHolder()));
					}
					res.setValue("Вексель");
				}
				resMain.getStructure().put(key.getKey(), res);
			}
		}
		return resMain;
	}

	public static ObjectElement pipelineToCompareObject(Task task, Map<ContextKey, Object> context) {
		HeaderElement head = CompareTaskKeys.getTaskHeader(CompareTaskBlock.PIPELINE, task.getHeader()
				.isOpportunity());
		ObjectElement resMain = new ObjectElement();
		PipelineJPA pipeline = (PipelineJPA) context.get(ContextKey.PIPELINE);
		if (pipeline == null)
			return resMain;
		for (HeaderElement key : head.getKeys()) {
			if (key.isKey() && key.getKey() != null) {
				String value = "";
				String htmlName = "";
				/** PIPELINE */
				if ("Плановая Дата Выборки".equals(key.getKey())) {
					value = pipeline.getPlan_date();
					htmlName = "#compare_pipeline_plandate";
				}
				else if ("Статус сделки".equals(key.getKey())) {
					value = pipeline.getStatus();
					htmlName = "#compare_pipeline_status";
				}
				else if ("Вероятность Закрытия".equals(key.getKey())) {
					value = pipeline.getClose_probability();
					htmlName = "#compare_pipeline_closeprobability";
				}
				else if ("Применимое Право".equals(key.getKey())) {
					value = pipeline.getLaw();
					htmlName = "#compare_pipeline_law";
				}
				else if ("География".equals(key.getKey())) {
					value = pipeline.getGeography();
					htmlName = "#compare_pipeline_geography";
				}
				else if ("Обеспечение".equals(key.getKey())) {
					value = pipeline.getSupply();
					htmlName = "#compare_pipeline_supply";
				}
				else if ("Описание Сделки, Включая Структуру, Деривативы и т.д.".equals(key.getKey())) {
					value = pipeline.getDescription();
					htmlName = "#compare_pipeline_descr";
				}
				else if ("Комментарии по Статусу Сделки, Следующие Шаги".equals(key.getKey())) {
					value = pipeline.getCmnt();
					htmlName = "#compare_pipeline_cmnt";
				}
				else if ("Дополнительный Бизнес, Сроки, Примерный Объем в млн. дол. США".equals(key
						.getKey())) {
					value = pipeline.getAddition_business();
					htmlName = "#compare_pipeline_additionbusiness";
				}
				else if ("Возможность Синдикации".equals(key.getKey())) {
					value = CompareHelper.boolToString(pipeline.getSyndication() != null
							&& pipeline.getSyndication().equalsIgnoreCase("y"));
					htmlName = "#compare_pipeline_synd";
				}
				else if ("Комментарии по Синдикации".equals(key.getKey())) {
					value = pipeline.getSyndication_cmnt();
					htmlName = "#compare_pipeline_syndcmnt";
				}
				else if ("Средневзвешенный Срок Погашения (WAL)".equals(key.getKey())) {
					value = pipeline.getWal();
					htmlName = "#compare_pipeline_wal";
				}
				else if ("Минимальная Ставка (Hurdle Rate)".equals(key.getKey())) {
					value = pipeline.getHurdle_rate();
					htmlName = "#compare_pipeline_hurdlerate";
				}
				else if ("Маркап".equals(key.getKey())) {
					try {
						value = PipelineController.getMarkup(task.getId_task());
					}
					catch (Exception e) {
						logger.log(Level.SEVERE, e.getMessage(), e);
					}
					htmlName = "#compare_pipeline_markup";
				}
				else if ("PCs: Кеш, млн. дол. США".equals(key.getKey())) {
					value = pipeline.getPc_cash();
					htmlName = "#compare_pipeline_pccash";
				}
				else if ("PCs: Резервы, млн. дол. США".equals(key.getKey())) {
					value = pipeline.getPc_res();
					htmlName = "#compare_pipeline_pcres";
				}
				else if ("PCs: Деривативы, млн. дол. США".equals(key.getKey())) {
					value = pipeline.getPc_der();
					htmlName = "#compare_pipeline_pcder";
				}
				else if ("PCs: Всего, млн. дол. США".equals(key.getKey())) {
					value = pipeline.getPc_total();
					htmlName = "#compare_pipeline_pctotal";
				}
				else if ("Выбранный Объем Линии, млн. дол. США".equals(key.getKey())) {
					value = pipeline.getLine_count();
					htmlName = "#compare_pipeline_linecount";
				}
				else if ("Публичная Сделка".equals(key.getKey())) {
					value = CompareHelper.boolToString(pipeline.getPub().equalsIgnoreCase("y"));
					htmlName = "#compare_pipeline_pub";
				}
				else if ("Приоритет Менеджмента".equals(key.getKey())) {
					value = CompareHelper.boolToString(pipeline.getPriority().equalsIgnoreCase("y"));
					htmlName = "#compare_pipeline_priority";
				}
				else if ("Новый клиент".equals(key.getKey())) {
					value = CompareHelper.boolToString(pipeline.getNew_client().equalsIgnoreCase("y"));
					htmlName = "#compare_pipeline_newclient";
				}
				else if ("Сделка Flow / Investment".equals(key.getKey())) {
					value = pipeline.getFlow_investment();
					htmlName = "#compare_pipeline_flowinvestment";
				}
				else if ("Коэффициент Типа Сделки".equals(key.getKey())) {
					value = pipeline.getFactor_product_type();
					htmlName = "#compare_pipeline_producttype";
				}
				else if ("Коэффициент по Сроку Погашения".equals(key.getKey())) {
					value = pipeline.getFactor_period();
					htmlName = "#compare_pipeline_factorperiod";
				}
				else if ("Фондирующая Компания".equals(key.getKey())) {
					value = pipeline.getContractor();
					htmlName = "#compare_pipeline_contractor";
				}
				else if ("Контрагент со стороны Группы ВТБ".equals(key.getKey())) {
					value = pipeline.getVtb_contractor();
					htmlName = "#compare_pipeline_vtbcontractor";
				}
				else if ("Трейдинг Деск".equals(key.getKey())) {
					value = pipeline.getTrade_desc();
					htmlName = "#compare_pipeline_tradedesc";
				}
				else if ("Пролонгация".equals(key.getKey())) {
					value = CompareHelper.boolToString(pipeline.getProlongation().equalsIgnoreCase("y"));
					htmlName = "#compare_pipeline_prolongation";
				}
				else if ("Не показывать в отчете".equals(key.getKey())) {
					value = CompareHelper.boolToString(pipeline.getHideinreport().equalsIgnoreCase("y"));
					htmlName = "#compare_pipeline_hideinreport";
				}
				resMain.getStructure().put(key.getKey(), new ObjectElement(value, htmlName));
			}
			else {
				// извлечение структурированных блоков
				ObjectElement res = new ObjectElement();
				/** PIPELINE */
				if ("Наименование Цели".equals(key.getKey())) {
					List<String> finTargets = (List<String>) context.get(ContextKey.PIPELINE_FIN_TARGET);
					for (String target : finTargets) {
						res.getStructure().put(target, new ObjectElement(target,
								"#compare_pipeline_target_" + target.replaceAll(" ",""),
								"#compare_list_pipeline_target"));
					}
					res.setValue("Цели Финансирования");
				}
				resMain.getStructure().put(key.getKey(), res);
			}
		}
		return resMain;
	}

	public static ObjectElement conclusionToCompareObject(Task task, Map<ContextKey, Object> context) {
		HeaderElement head = CompareTaskKeys.getTaskHeader(CompareTaskBlock.CONCLUSION, task
				.getHeader().isOpportunity());
		ObjectElement resMain = new ObjectElement();
		for (HeaderElement key : head.getKeys()) {
			if (key.isKey() && key.getKey() != null) {
				String value = "";
				/** CONCLUSION */
				if ("Решение принимает".equals(key.getKey())) {
					if (task.getCollegial().equals("Y"))
						value = "Уполномоченный орган";
					if (task.getCollegial().equals("N"))
						value = "Уполномоченное лицо";
				}
				else if ("Желаемая дата рассмотрения".equals(key.getKey())) {
					value = Formatter.format(task.getTemp().getPlanMeetingDate());
				}
				else if ("Уполномоченный орган".equals(key.getKey())) {
					CompendiumActionProcessor compenduim = (CompendiumActionProcessor) context
							.get(ContextKey.COMPENDIUM);
					if (task.getAuthorizedBody() != null) {
						try {
							Department dep = compenduim.getDepartment(new Department(task.getAuthorizedBody()));
							if (dep != null)
								value = dep.getNominativeCaseName();
						}
						catch (Exception e) {
							logger.log(Level.SEVERE, e.getMessage(), e);
						}
					}
				}
				else if ("Дата заседания Комитета".equals(key.getKey())) {
					if (task.getCcStatus() != null && task.getCcStatus().getMeetingDate() != null)
						value = Formatter.format(task.getCcStatus().getMeetingDate());
				}
				else if ("Классификация вопроса для УО".equals(key.getKey())) {
					if (task.getCcQuestionType() != null)
						value = task.getCcQuestionType().getName();
				}
				else if ("Проект кредитного решения".equals(key.getKey())) {
					Map<String, String> decisions = (Map<String, String>) context
							.get(ContextKey.CREDIT_DECISION_PROJECT_MAP);
					if (task.getCreditDecisionProject() != null)
						value = Formatter.str(decisions.get(task.getCreditDecisionProject()));
				}
				else if ("Статус".equals(key.getKey())) {
					if (task.getCcStatus() != null && task.getCcStatus().getStatus() != null)
						value = task.getCcStatus().getStatus().getDisplayName();
				}
				else if ("Номер протокола".equals(key.getKey())) {
					value = task.getCcStatus().getProtocol() + "\n"
							+ task.getCcStatus().getResolution(task.getId_task());
				}
				resMain.getStructure().put(key.getKey(), new ObjectElement(value));
			}
		}
		return resMain;
	}

	public static ObjectElement returnStatusToCompareObject(Task task, Map<ContextKey, Object> context) {
		HeaderElement head = CompareTaskKeys.getTaskHeader(CompareTaskBlock.RETURN_STATUS, task
				.getHeader().isOpportunity());
		ObjectElement resMain = new ObjectElement();
		for (HeaderElement key : head.getKeys()) {
			if (key.isKey() && key.getKey() != null) {
				String value = "";
				/** RETURN_STATUS */
				if ("Дата принятия решения".equals(key.getKey())) {
					value = Formatter.format(task.getTaskStatusReturn().getDateReturn());
				}
				else if ("Уполномоченное лицо".equals(key.getKey())) {
					Long userid = task.getTaskStatusReturn().getIdUser();
					String authorizedPerson = "";
					TaskJPA taskJPA = (TaskJPA) context.get(ContextKey.JPA);
					if (taskJPA.getAuthorizedPerson() != null)
						authorizedPerson = taskJPA.getAuthorizedPerson().getDisplayName();
					if ((authorizedPerson == null || authorizedPerson.isEmpty()) && userid != null
							&& userid > 0l) {
						UserJPA user = ((PupFacadeLocal) context.get(ContextKey.PUP_FACADE_LOCAL))
								.getUser(userid);
						authorizedPerson = user.getFullName() + " (" + user.getDepartment().getShortName()
								+ ")";
					}
					value = authorizedPerson;
				}
				else if ("Решение уполномоченного органа".equals(key.getKey())) {
					value = task.getTaskStatusReturn().getStatusReturn().getType();
				}
				else if ("Статус решения (детализация)".equals(key.getKey())) {
					value = task.getTaskStatusReturn().getStatusReturn().getDescription();
				}
				else if ("Комментарий к решению".equals(key.getKey())) {
					value = task.getTaskStatusReturn().getStatusReturnText();
				}
				resMain.getStructure().put(key.getKey(), new ObjectElement(value));
			}
		}
		return resMain;
	}

	public static ObjectElement departmentToCompareObject(Task task, Map<ContextKey, Object> context) {
		HeaderElement head = CompareTaskKeys.getTaskHeader(CompareTaskBlock.DEPARTMENT, task
				.getHeader().isOpportunity());
		ObjectElement resMain = new ObjectElement();
		TaskJPA taskJPA = (TaskJPA) context.get(ContextKey.JPA);
		for (HeaderElement key : head.getKeys()) {
			if (key.isKey() && key.getKey() != null) {
				String value = "";
				/** DEPARTMENT */
				if ("Инициирующее Подразделение".equals(key.getKey())) {
					value = taskJPA.getInitDepartment().getShortName();
				}
				resMain.getStructure().put(key.getKey(), new ObjectElement(value));
			}
			else {
				// извлечение структурированных блоков
				ObjectElement res = new ObjectElement();
				/** DEPARTMENT */
				if ("Менеджер".equals(key.getKey())) {
					for (ManagerJPA manager : taskJPA.getManagers()) {
						if (manager.getStartDepartment() != null) {
							continue;
						}
						res.getStructure().put(manager.getUser().getFullName(), new ObjectElement());
					}
					res.setValue("Менеджеры");
				}
				else if ("Место".equals(key.getKey())) {
					if (taskJPA.getProcess() == null
							|| !((String) context.get(ContextKey.PUP_TYPE)).equalsIgnoreCase("сделка")) {
						for (DepartmentJPA d : taskJPA.getPlaceList())
							res.getStructure().put(d.getShortName(), new ObjectElement());
					}
					else {
						CompendiumActionProcessor compenduim = (CompendiumActionProcessor) context
								.get(ContextKey.COMPENDIUM);
						Department dep = null;
						if (taskJPA.getPlaceId() != null)
							dep = compenduim.getDepartment(new Department(taskJPA.getPlaceId().intValue()));
						if (dep != null)
							res.getStructure().put(dep.getShortName(), new ObjectElement());
					}
					res.setValue("Место проведения сделки");
				}
				resMain.getStructure().put(key.getKey(), res);
			}
		}
		return resMain;
	}

	public static ObjectElement projectTeamToCompareObject(Task task, Map<ContextKey, Object> context) {
		HeaderElement head = CompareTaskKeys.getTaskHeader(CompareTaskBlock.PROJECT_TEAM, task
				.getHeader().isOpportunity());
		ObjectElement resMain = new ObjectElement();
		TaskJPA taskJPA = (TaskJPA) context.get(ContextKey.JPA);
		for (HeaderElement key : head.getKeys()) {
			if (!key.isKey()) {
				// извлечение структурированных блоков
				ObjectElement res = new ObjectElement();
				/** PROJECT_TEAM */
				if ("Участник Проектной команды".equals(key.getKey())) {
					for (ProjectTeamJPA team : taskJPA.getProjectTeam("p")) {
						res.getStructure().put(team.getUser().getFullName(),
								CompareTaskHelper.toCompareObject(team, key, context, "p"));
					}
					res.setValue("Проектная команда");
				}
				else if ("Работник мидл-офиса".equals(key.getKey())) {
					for (ProjectTeamJPA team : taskJPA.getProjectTeam("m")) {
						res.getStructure().put(team.getUser().getFullName(),
								CompareTaskHelper.toCompareObject(team, key, context, "m"));
					}
					res.setValue("Мидл-офис");
				}
				resMain.getStructure().put(key.getKey(), res);
			}
		}
		return resMain;
	}

	public static ObjectElement standardPeriodToCompareObject(Task task,
			Map<ContextKey, Object> context) {
		HeaderElement head = CompareTaskKeys.getTaskHeader(CompareTaskBlock.STANDARD_PERIOD, task
				.getHeader().isOpportunity());
		ObjectElement resMain = new ObjectElement();
		for (HeaderElement key : head.getKeys()) {
			if (!key.isKey()) {
				// извлечение структурированных блоков
				ObjectElement res = new ObjectElement();
				/** STANDARD_PERIOD */
				if ("Этап заявки".equals(key.getKey())) {
					ArrayList<StandardPeriod> splist = new ArrayList<StandardPeriod>();
					try {
						splist = ((StandardPeriodBeanLocal) context.get(ContextKey.STANDARD_PERIOD_LOCAL))
								.getStandartPeriodReport(task.getId_task());
					}
					catch (Exception e) {
						logger.log(Level.SEVERE, e.getMessage(), e);
					}
					for (StandardPeriod sp : splist) {
						res.getStructure().put(sp.getStageName(),
								CompareTaskHelper.toCompareObject(sp, key, context));
					}
					res.setValue("Сроки прохождения этапов заявки");
				}
				resMain.getStructure().put(key.getKey(), res);
			}
		}
		return resMain;
	}

	public static ObjectElement stopFactorsToCompareObject(Task task, Map<ContextKey, Object> context) {
		HeaderElement head = CompareTaskKeys.getTaskHeader(CompareTaskBlock.STOP_FACTORS, task
				.getHeader().isOpportunity());
		ObjectElement resMain = new ObjectElement();
		for (HeaderElement key : head.getKeys()) {
			if (!key.isKey()) {
				// извлечение структурированных блоков
				ObjectElement res = new ObjectElement();
				/** STOP_FACTORS */
				if ("Наименование стоп-фактора клиентского менеджера".equals(key.getKey())) {
					for (TaskStopFactor tsf : task.getTaskClientStopFactorList()) {
						res.getStructure().put(tsf.getStopFactor().getDescription(), new ObjectElement());
					}
					res.setValue("Клиентский менеджер");
				}
				else if ("Наименование стоп-фактора безопасности".equals(key.getKey())) {
					for (TaskStopFactor tsf : task.getTaskSecurityStopFactorList()) {
						res.getStructure().put(tsf.getStopFactor().getDescription(), new ObjectElement());
					}
					res.setValue("Безопасность");
				}
				else if ("Наименование стоп-фактора подразделения подготовки кредитных заявок".equals(key
						.getKey())) {
					for (TaskStopFactor tsf : task.getTaskStopFactor3List()) {
						res.getStructure().put(tsf.getStopFactor().getDescription(), new ObjectElement());
					}
					res.setValue("Подразделение подготовки кредитных заявок");
				}
				resMain.getStructure().put(key.getKey(), res);
			}
		}
		return resMain;
	}

	public static ObjectElement documentsToCompareObject(Task task, Map<ContextKey, Object> context) {
		HeaderElement head = CompareTaskKeys.getTaskHeader(CompareTaskBlock.DOCUMENTS, task.getHeader()
				.isOpportunity());
		ObjectElement resMain = new ObjectElement();
		PupFacadeLocal pupFacadeLocal = (PupFacadeLocal) context.get(ContextKey.PUP_FACADE_LOCAL);
		for (HeaderElement key : head.getKeys()) {
			if (!key.isKey()) {
				// извлечение структурированных блоков
				ObjectElement res = new ObjectElement();
				/** DOCUMENTS */
				if ("Наименование файла".equals(key.getKey())) {
					Long docType = 0L;
					for (DocumentGroupJPA docGroup : pupFacadeLocal.findDocumentGroupByOwnerTYpe(docType)) {
						for (DocumentTypeJPA type : docGroup.getTypes()) {
							List<AttachJPA> list = pupFacadeLocal.findAttachemnt(task.getId_pup_process()
									.toString(), docType, docGroup.getId(), type.getId());
							if (!pupFacadeLocal.isDocumentGroupTypeActive(docGroup.getId(), type.getId())
									&& list.size() == 0) {
								continue;
							}
							for (AttachJPA attach : list) {
								res.getStructure().put(
										attach.getFILENAME(),
										CompareTaskHelper.toCompareObject(attach, key, context, docGroup
												.getNAME_DOCUMENT_GROUP()
												+ " \\ " + type.getName()));
							}
						}
					}
					List<AttachJPA> list = pupFacadeLocal.findOtherAttachemnt(task.getId_pup_process()
							.toString(), docType);
					for (AttachJPA attach : list) {
						res.getStructure().put(
								attach.getFILENAME(),
								CompareTaskHelper.toCompareObject(attach, key, context, "без группы \\ "
										+ attach.getFILETYPE()));
					}
					res.setValue("Документы по заявке");
				}
				resMain.getStructure().put(key.getKey(), res);
			}
		}
		return resMain;
	}

	public static ObjectElement activeDecisionToCompareObject(Task task,
			Map<ContextKey, Object> context) {
		HeaderElement head = CompareTaskKeys.getTaskHeader(CompareTaskBlock.ACTIVE_DECISION, task
				.getHeader().isOpportunity());
		ObjectElement resMain = new ObjectElement();
		for (HeaderElement key : head.getKeys()) {
			if (key.isKey() && key.getKey() != null) {
				String value = "";
				/** ACTIVE_DECISION */
				if ("Действующие решения".equals(key.getKey())) {
					value = Formatter.str(((TaskJPA) context.get(ContextKey.JPA)).getActive_decision());
				}
				resMain.getStructure().put(key.getKey(), new ObjectElement(value));
			}
		}
		return resMain;
	}

	public static ObjectElement expertusToCompareObject(Task task, Map<ContextKey, Object> context) {
		HeaderElement head = CompareTaskKeys.getTaskHeader(CompareTaskBlock.EXPERTUS, task.getHeader()
				.isOpportunity());
		ObjectElement resMain = new ObjectElement();
		for (HeaderElement key : head.getKeys()) {
			if (!key.isKey()) {
				// извлечение структурированных блоков
				ObjectElement res = new ObjectElement();
				/** EXPERTUS */
				if ("Экспертиза".equals(key.getKey())) {
					List<Expertus> experts = new ArrayList<Expertus>();
					try {
						experts = ((PupFacadeLocal) context.get(ContextKey.PUP_FACADE_LOCAL))
								.getExpertReport(task.getId_task());
					}
					catch (FactoryException e) {
						logger.log(Level.SEVERE, e.getMessage(), e);
					}
					for (Expertus expertus : experts)
						res.getStructure().put(expertus.getName(),
								CompareTaskHelper.toCompareObject(expertus, key, context));
					res.setValue("Проведение экспертиз");
				}
				resMain.getStructure().put(key.getKey(), res);
			}
		}
		return resMain;
	}

	public static ObjectElement departmentAgreementToCompareObject(Task task,
			Map<ContextKey, Object> context) {
		HeaderElement head = CompareTaskKeys.getTaskHeader(CompareTaskBlock.DEPARTMENT_AGREEMENT, task
				.getHeader().isOpportunity());
		ObjectElement resMain = new ObjectElement();
		for (HeaderElement key : head.getKeys()) {
			if (!key.isKey()) {
				// извлечение структурированных блоков
				ObjectElement res = new ObjectElement();
				/** DEPARTMENT_AGREEMENT */
				if ("Наименование экспертного подразделения".equals(key.getKey())) {
					for (DepartmentAgreement da : task.getDepartmentAgreements())
						res.getStructure().put(da.getDepartment(),
								CompareTaskHelper.toCompareObject(da, key, context));
					res.setValue("Справка об отклоненных замечаниях и предложениях Экспертных подразделений");
				}
				resMain.getStructure().put(key.getKey(), res);
			}
		}
		return resMain;
	}

	public static ObjectElement fundsToCompareObject(Task task, Map<ContextKey, Object> context) {
		HeaderElement head = CompareTaskKeys.getTaskHeader(CompareTaskBlock.FUNDS, task.getHeader()
				.isOpportunity());
		ObjectElement resMain = new ObjectElement();
		for (HeaderElement key : head.getKeys()) {
			if (!key.isKey()) {
				// извлечение структурированных блоков
				ObjectElement res = new ObjectElement();
				/** FUNDS */
				if ("Номер заявки на фондирование".equals(key.getKey())) {
					List<FundingRequest> funds = new ArrayList<FundingRequest>();
					try {
						FundingRequestFilter filter = new FundingRequestFilter();
						filter.setMdTaskId(task.getId_task());
						funds = ru.masterdm.integration.ServiceFactory.getService(FundingService.class).getFundingRequests(filter);
					}
					catch (Throwable e) {
						logger.log(Level.SEVERE, e.getMessage(), e);
					}
					for (FundingRequest req : funds) {
						res.getStructure().put(req.getId().toString(),
								CompareTaskHelper.toCompareObject(req, key, context));
					}
					res.setValue("Заявки на фондирование");
				}
				resMain.getStructure().put(key.getKey(), res);
			}
		}
		return resMain;
	}

	public static ObjectElement n6ToCompareObject(Task task, Map<ContextKey, Object> context) {
		HeaderElement head = CompareTaskKeys.getTaskHeader(CompareTaskBlock.N6, task.getHeader()
				.isOpportunity());
		ObjectElement resMain = new ObjectElement();
		for (HeaderElement key : head.getKeys()) {
			if (!key.isKey()) {
				// извлечение структурированных блоков
				ObjectElement res = new ObjectElement();
				/** N6 */
				if ("Номер заявки на Н6".equals(key.getKey())) {
					List<N6Request> n6Reqs = new ArrayList<N6Request>();
					try {
						FundingRequestFilter filter = new FundingRequestFilter();
						filter.setMdTaskId(task.getId_task());

						n6Reqs = ru.masterdm.integration.ServiceFactory.getService(FundingService.class).getN6Requests(filter);
					}
					catch (Throwable e) {
						logger.log(Level.SEVERE, e.getMessage(), e);
					}
					for (N6Request req : n6Reqs) {
						res.getStructure().put(req.getId().toString(),
								CompareTaskHelper.toCompareObject(req, key, context));
					}
					res.setValue("Заявки на Н6");
				}
				resMain.getStructure().put(key.getKey(), res);
			}
		}
		return resMain;
	}

	public static ObjectElement commentsToCompareObject(Task task, Map<ContextKey, Object> context) {
		HeaderElement head = CompareTaskKeys.getTaskHeader(CompareTaskBlock.COMMENTS, task.getHeader()
				.isOpportunity());
		ObjectElement resMain = new ObjectElement();
		for (HeaderElement key : head.getKeys()) {
			if (!key.isKey()) {
				// извлечение структурированных блоков
				ObjectElement res = new ObjectElement();
				/** COMMENTS */
				if ("Номер комментария".equals(key.getKey())) {
					int i = 0;
					for (Comment comment : task.getComment())
						if (comment.getBody() != null)
							res.getStructure().put("Комментарий №" + (++i),
									CompareTaskHelper.toCompareObject(comment, key, context));
					res.setValue("Комментарии");
				}
				resMain.getStructure().put(key.getKey(), res);
			}
		}
		return resMain;
	}

}
