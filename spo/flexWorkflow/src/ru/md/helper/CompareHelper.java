package ru.md.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uit.director.action.AbstractAction;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.tasks.TaskCachInfo;
import org.uit.director.tasks.TaskInfo;

import ru.masterdm.compendium.domain.crm.FloatPartOfActiveRate;
import ru.masterdm.compendium.domain.spo.ContractorType;
import ru.masterdm.compendium.model.ActionProcessorFactory;
import ru.masterdm.compendium.model.CompendiumActionProcessor;
import ru.masterdm.compendium.model.CompendiumCrmActionProcessor;
import ru.masterdm.compendium.model.CompendiumSpoActionProcessor;
import ru.md.compare.CompareTaskHelper;
import ru.md.compare.CompareTaskKeys;
import ru.md.compare.CompareTaskKeys.CompareTaskBlock;
import ru.md.compare.CompareTaskKeys.ContextKey;
import ru.md.compare.HeaderElement;
import ru.md.compare.ObjectElement;
import ru.md.compare.Result;
import ru.md.compare.ResultElement;
import ru.md.pup.dbobjects.TaskInfoJPA;
import ru.md.spo.dbobjects.TaskJPA;
import ru.md.spo.ejb.PupFacadeLocal;
import ru.md.spo.ejb.StandardPeriodBeanLocal;
import ru.md.spo.ejb.TaskFacadeLocal;

import com.vtb.domain.Task;
import com.vtb.model.AttachmentActionProcessor;
import com.vtb.model.TaskActionProcessor;
import com.vtb.util.EjbLocator;

/**
 * Класс-помощник для сравнения объектов
 * @author rislamov
 */
public class CompareHelper {

	private static final String EMPTY_VALUE = "<отсутствует>";
	private static final Logger LOGGER = LoggerFactory.getLogger(CompareHelper.class.getName());

	/**
	 * Основной метод сравнения
	 * @param strIds идентификаторы объектов
	 * @param objType тип объектов
	 * @param blockName имя блока
	 * @param current номер текущей версии
	 * @param request request
	 * @return результат сравнения
	 * @throws Exception ошибка
	 */
	public static Result compare(String strIds, String objType, String blockName, String current,
			HttpServletRequest request) throws Exception {
		List<ObjectElement> objs = new ArrayList<ObjectElement>();
		List<Object> origObjects = new ArrayList<Object>();
		List<Long> ids = new ArrayList<Long>();
		TaskActionProcessor processor = (TaskActionProcessor) com.vtb.model.ActionProcessorFactory
				.getActionProcessor("Task");
		PupFacadeLocal pup = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
		StandardPeriodBeanLocal spLocal = EjbLocator.getInstance().getReference(
				StandardPeriodBeanLocal.class);
		CompendiumActionProcessor compendium = (CompendiumActionProcessor) ActionProcessorFactory
				.getActionProcessor("Compendium");
		AttachmentActionProcessor attachmentProcessor = (AttachmentActionProcessor) com.vtb.model.ActionProcessorFactory
				.getActionProcessor("Attachment");
		CompendiumCrmActionProcessor compendiumCrm = (CompendiumCrmActionProcessor) ActionProcessorFactory
				.getActionProcessor("CompendiumCrm");
		TaskFacadeLocal taskFacadeLocal = EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
		if (strIds != null) {
			for (String id : strIds.split("\\|")) {
				if (!id.isEmpty())
					ids.add(Long.parseLong(id));
                else
                    ids.add(0L);
			}
		}
		CompareTaskBlock block = CompareTaskBlock.MAIN;
		HeaderElement header = new HeaderElement();
		Result result = new Result(ids);
		boolean firstFlag = false;
		boolean secondFlag = false;
		if (objType != null) {
			if (objType.equals("product") || objType.equals("limit") || objType.equals("sublimit")) {
				if (blockName != null && !blockName.isEmpty())
					block = CompareTaskBlock.getBlock(blockName);
				ContractorType[] allct = null;
				LinkedHashMap<String, String> limitTypes = new LinkedHashMap<String, String>();
				FloatPartOfActiveRate[] floatPartOfActiveRate = null;
				if (block.equals(CompareTaskBlock.CONTRACTORS)) {
					CompendiumSpoActionProcessor compenduimSPO = (CompendiumSpoActionProcessor) ActionProcessorFactory
							.getActionProcessor("CompendiumSpo");
					ContractorType filter = new ContractorType();
					filter.setGroupKey("BORROWERS"); // для получения заемщиков
					filter.setIsActive(1); // получить только активные
					allct = compenduimSPO.getContractorTypeLike(filter, null);
				}
				if (block.equals(CompareTaskBlock.PARAMETERS)) {
					CompendiumSpoActionProcessor compenduimSPO = (CompendiumSpoActionProcessor) ActionProcessorFactory
							.getActionProcessor("CompendiumSpo");
					for (ru.masterdm.compendium.domain.spo.LimitType entity : compenduimSPO
							.findLimitTypeList("", null)) {
						limitTypes.put(entity.getId().toString(), entity.getName());
					}
				}
				if (block.equals(CompareTaskBlock.PRICE_CONDITIONS)) {
					floatPartOfActiveRate = compendiumCrm.findFloatPartOfActiveRateList(
							new FloatPartOfActiveRate(), null);
				}
				long currentId = !ids.isEmpty() ? ids.get(0) : -1l;
				if (current != null && !current.isEmpty() && !current.equals("null")) {
					currentId = ids.get(Integer.parseInt(current));
				}
				for (Long id : ids) {
					Task task = new Task();
					if (id > 0) {
						task = processor.getTask(new Task(id));
					}
					origObjects.add(task);
				}
				for (Object obj : origObjects) {
					Task task = (Task) obj;
					if (task.getId_task() == null)
						result.getHeaders().add("Версия отсутствует");
					else if (task.getId_task().equals(currentId))
						result.getHeaders()
								.add("Текущая версия (версия " + task.getHeader().getVersion() + ")");
					else
						result.getHeaders().add("Версия " + task.getHeader().getVersion());
					Map<ContextKey, Object> context = new HashMap<ContextKey, Object>();
					try {
						TaskJPA taskJPA = null;
						TaskJPA parentJPA = null;
						Long idProcess = task.getId_pup_process();
						if (task.getId_task() != null) {
							taskJPA = taskFacadeLocal.getTask(task.getId_task());
							firstFlag = taskJPA.isProduct();
							if (firstFlag) {
								secondFlag = (taskJPA.getFactPercents().size() == 1);
							}
							else {
								secondFlag = taskJPA.isSublimit();
							}
							context.put(ContextKey.JPA, taskJPA);
							if (taskJPA.isSublimit()) {
								parentJPA = taskJPA;
								while (parentJPA.isSublimit() && parentJPA.getParent() != null)
									parentJPA = parentJPA.getParent();
								idProcess = parentJPA.getIdProcess();
							}
						}
						if (block.equals(CompareTaskBlock.MAIN)) {
							if (idProcess != null) {
								List<TaskInfoJPA> tasks = pup.getTaskByProcessId(idProcess);
								if (tasks.size() > 0) {
									WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
									TaskInfo cachInfo = new TaskCachInfo();
									cachInfo.init(wsc, tasks.get(0).getIdTask(), false);
									String res = cachInfo.execute();
									if (!res.equalsIgnoreCase("Error")) {
										context.put(ContextKey.TASK_INFO, cachInfo);
									}
								}
							}
						}
						if (block.equals(CompareTaskBlock.MAIN)) {
							if (idProcess != null)
								context.put(ContextKey.PUP_STATUS, pup.getPUPAttributeValue(idProcess, "Статус"));
						}
						if (block.equals(CompareTaskBlock.MAIN)) {
							if (idProcess != null)
								context.put(ContextKey.PUP_PRIORITY, pup.getPUPAttributeValue(idProcess,
										"Приоритет"));
						}
						if (block.equals(CompareTaskBlock.CONTRACTORS)) {
							context.put(ContextKey.ALL_CONTRACTOR_TYPES, allct);
						}
						if (block.equals(CompareTaskBlock.CONTRACTORS)) {
							context.put(ContextKey.COMPENDIUM_CRM, compendiumCrm);
						}
						if (block.equals(CompareTaskBlock.CONTRACTORS)
								|| block.equals(CompareTaskBlock.EXPERTUS)
								|| block.equals(CompareTaskBlock.DOCUMENTS)
								|| block.equals(CompareTaskBlock.PROJECT_TEAM)
								|| block.equals(CompareTaskBlock.RETURN_STATUS)) {
							context.put(ContextKey.PUP_FACADE_LOCAL, pup);
						}
						if (block.equals(CompareTaskBlock.PARAMETERS)) {
							context.put(ContextKey.LIMIT_TYPES, limitTypes);
						}
						if (block.equals(CompareTaskBlock.PRICE_CONDITIONS)) {
							context.put(ContextKey.FLOAT_PART_OF_ACTIVE_RATE_LIST, floatPartOfActiveRate);
						}
						if (block.equals(CompareTaskBlock.CONDITIONS)) {
							if (task.getMain() != null && task.getMain().getCurrency2() != null)
								context.put(ContextKey.DEPENDING_LOANS, taskFacadeLocal.findDependingLoan(task
										.getMain().getCurrency2().getCode(), task.getMain().getPeriodInDay()));
						}
						if (block.equals(CompareTaskBlock.STANDARD_PERIOD)) {
							context.put(ContextKey.STANDARD_PERIOD_LOCAL, spLocal);
						}
						if (block.equals(CompareTaskBlock.CONCLUSION)
								|| block.equals(CompareTaskBlock.DEPARTMENT)) {
							context.put(ContextKey.COMPENDIUM, compendium);
						}
						if (block.equals(CompareTaskBlock.CONCLUSION)) {
							if (idProcess != null) {
								Map<String, String> map = attachmentProcessor.findByOwnerAndKeyType(idProcess,
										"credit_decision_project");
								context.put(ContextKey.CREDIT_DECISION_PROJECT_MAP, map);
							}
						}
						if (block.equals(CompareTaskBlock.PIPELINE)) {
							if (task.getId_task() != null)
								context.put(ContextKey.PIPELINE, taskFacadeLocal.getPipeline(task.getId_task()));
						}
						if (block.equals(CompareTaskBlock.PIPELINE)) {
							if (task.getId_task() != null)
								context.put(ContextKey.PIPELINE_FIN_TARGET, taskFacadeLocal
										.getPipelineFinTarget(task.getId_task()));
						}
						if (block.equals(CompareTaskBlock.DEPARTMENT)) {
							if (idProcess != null)
								context.put(ContextKey.PUP_TYPE, pup.getPUPAttributeValue(idProcess,
										"Тип кредитной заявки"));
						}

					}
					catch (Exception e) {
						LOGGER.warn(e.getMessage(), e);
						e.printStackTrace();
					}
					objs.add(CompareTaskHelper.toCompareObject(task, block, context));
				}
				header = CompareTaskKeys.getTaskHeader(block, firstFlag, secondFlag);
			}
			else {
				// здесь предполагается преобразование других типов объектов, если потребуется
			}
			LOGGER.info("Header for compare " + objType + " " + strIds + ":\n" + header.toString());
			LOGGER.info("Objects for compare " + objType + " " + strIds + ":\n");
			for (ObjectElement obj : objs)
				LOGGER.info("Object : " + obj.toString());
			Result mainResult = compare(objs, header, 0, null);
			mainResult.setBlockName(blockName);
			if (mainResult != null && mainResult.getResultObjects() != null)
				result.add(mainResult);
		}
		return result;
	}

	public static Result compare(List<ObjectElement> objects, HeaderElement head, int level,
			String headTitle) {
		Result res = new Result(objects.size() + 1);
		if (objects.size() > 0)
			for (HeaderElement hElem : head.getKeys()) {
				if (hElem.isKey()) {
					/** ОБРАБОТКА ОДНОГО ПОЛЯ */
					// шапка
					res.getResultObjects().get(0).getResults().add(
							new ResultElement(false, headTitle == null ? hElem.getKey() : headTitle, level, "", ""));
					// оригинальное значение
					String origValue = "";
					for (int i = 0; i < objects.size(); i++) {
						String val = "";
						String htmlName = "";
						String list = "";
						if (objects.get(i) != null && objects.get(i).getStructure() != null
								&& objects.get(i).getStructure().containsKey(hElem.getKey())) {
							val = objects.get(i).getStructure().get(hElem.getKey()).getValue();
							htmlName = objects.get(i).getStructure().get(hElem.getKey()).getHtmlName();
							list = objects.get(i).getStructure().get(hElem.getKey()).getHtmlList();
						}
						if (i == 0)
							origValue = val;
						// если значение пустое, а начальное не пустое - <отсутствует>
						if ((val == null || val.isEmpty()) && origValue != null && !origValue.isEmpty())
							val = EMPTY_VALUE;
						// если значение не пустое, а начальное пустое - у всех предшествующих пустых
						// <отсутствует>
						if (!isEmpty(val) && (origValue == null || origValue.isEmpty())) {
							origValue = EMPTY_VALUE;
							for (int k = 0; k < i; k++)
								res.getResultObjects().get(k + 1).getResults().get(
										res.getResultObjects().get(k + 1).getResults().size() - 1)
										.setValue(EMPTY_VALUE);
						}
						res.getResultObjects().get(i + 1).getResults().add(
								new ResultElement(!val.equals(origValue), val, level, htmlName, list));
					}
				}
				else {
					/** ОБРАБОТКА ДОЧЕРНЕГО ОБЪЕКТА */

					// отработанные объекты
					List<String> workedObjects = new ArrayList<String>();
					// структуры для сравнения из объектов по заданному ключу
					List<ObjectElement> rootElems = new ArrayList<ObjectElement>();
					// результирующая структура заголовка блока - с пустыми значениями
					Result headParentResult = new Result(objects.size() + 1);
					// наименование заголовка блока
					String parentName = "";

					/** формирование заголовка */
					for (int i = 0; i < objects.size(); i++) {
						if (objects.get(i).getStructure().containsKey(hElem.getKey())) {
							rootElems.add(objects.get(i).getStructure().get(hElem.getKey()));
							parentName = objects.get(i).getStructure().get(hElem.getKey()).getValue();
						}
						else
							rootElems.add(new ObjectElement());
						headParentResult.getResultObjects().get(i + 1).getResults().add(
								new ResultElement(false, "", level, "", ""));
					}
					headParentResult.getResultObjects().get(0).getResults().add(
							new ResultElement(false, parentName, level, "", ""));
					if (!hElem.isHidden())
						res.add(headParentResult);

					/** формирование результата */
					for (int i = 0; i < rootElems.size(); i++) {
						// упорядочиваем множество значений ключевых полей для текущего объекта
						ArrayList<String> keys = new ArrayList<String>();
						keys.addAll(rootElems.get(i).getStructure().keySet());

						/** обработка конкретного дочернего объекта */
						// entryKey - ключевое значение
						for (String entryKey : keys) {
							if (!workedObjects.contains(entryKey)) {
								/** получение простейшего объекта ключ-значение, где ключ будет в первом столбце */
								boolean isSimpleObject = (hElem.getKeys().size() == 1 && hElem.getKeys().get(0)
										.isKey());
								// заголовок дочернего объекта - ключевое поле!
								Result headCompareResult = new Result(rootElems.size() + 1);
								headCompareResult.getResultObjects().get(0).getResults().add(
										new ResultElement(false, hElem.getKey(), level + 1, "", ""));
								/** получение неключевых полей дочерних объектов */
								List<ObjectElement> subObjects = new ArrayList<ObjectElement>();
								String origHeadValue = EMPTY_VALUE;
								for (int j = 0; j < rootElems.size(); j++) {
									String value = EMPTY_VALUE;
									String htmlName = "";
									String htmlList = "";
									if (rootElems.get(j).getStructure().containsKey(entryKey)) {
										subObjects.add(rootElems.get(j).getStructure().get(entryKey));
										value = entryKey;
										htmlName = rootElems.get(j).getStructure().get(entryKey).getHtmlName();
										htmlList = rootElems.get(j).getStructure().get(entryKey).getHtmlList();
										if (j == 0)
											origHeadValue = value;
									}
									else {
										subObjects.add(new ObjectElement());
									}
									headCompareResult.getResultObjects().get(j + 1).getResults().add(
											new ResultElement(!origHeadValue.equals(value), value, level + 1, htmlName, htmlList));
								}
								if (!isSimpleObject)
									res.add(headCompareResult);
								res.add(compare(subObjects, hElem, level + (isSimpleObject ? 1 : 2),
										isSimpleObject ? entryKey : null));
								workedObjects.add(entryKey);
							}
						}
					}
				}
			}
		return res;
	}

	/**
	 * Преобразование логической переменной в строку
	 * @param b булева переменная
	 * @return булева переменная строкой
	 */
	public static String boolToString(boolean b) {
		return b ? "да" : "нет";
	}

	/**
	 * Проверка на пустое значение
	 * @param value значение
	 * @return является ли значение пустым
	 */
	public static boolean isEmpty(String value) {
		return value == null || value.isEmpty() || value.equals(EMPTY_VALUE);
	}

}
