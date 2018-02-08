package org.uit.director.decider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.crimson.tree.XmlDocument;
import org.nfunk.jep.JEP;
import org.uit.director.contexts.WPC;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.db.dbobjects.Attribute;
import org.uit.director.db.dbobjects.BasicAttribute;
import org.uit.director.db.dbobjects.Cnst;
import org.uit.director.db.dbobjects.VariablesType;
import org.uit.director.db.dbobjects.WorkflowTypeProcess;
import org.uit.director.db.dbobjects.graph.MGraph;
import org.uit.director.plugins.commonPlugins.PluginActionImpl;
import org.uit.director.tasks.AttributesStructList;
import org.uit.director.tasks.TaskInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Класс парсит бизнес процесс и отвечает за переходы по этапам.
 * 
 * @author Ижевцы.
 * 
 */
public class BusinessProcessDecider {

	private static Logger LOGGER = Logger.getLogger(BusinessProcessDecider.class.getName());

	public enum NextStageStatus {
		finalStage, stage, autoStage, subProcess, notDefine
	};

	public static NextStagesInfo getNextStageAfterComplation(WorkflowSessionContext wsc, Long idTask, String sign) throws Exception {

		TaskInfo taskInfo = new TaskInfo();
		taskInfo.init(wsc, idTask, true);

		LOGGER.info("taskInfo init complete");

		taskInfo.execute();

		LOGGER.info("taskInfo execute complete");
		String nameStage = (String) WPC.getInstance().getData(Cnst.TBLS.stages, taskInfo.getIdStageTo(), Cnst.TStages.name);

		LOGGER.info("taskInfo IdStageTo: " + taskInfo.getIdStageTo());

		return getNextStageAfterComplation(wsc, nameStage, taskInfo, sign);
	}

	/**
	 * Возвращает список следующих этапов после завершения задания с номером
	 * idTask или по имени этапа.
	 * 
	 * @param nameStage
	 * @return res
	 */
	public static NextStagesInfo getNextStageAfterComplation(
			WorkflowSessionContext wsc, String nameStage, TaskInfo taskInfo,
			String sign) throws Exception {

		NextStagesInfo nextStagesInfo = new NextStagesInfo();

		nextStagesInfo.setIdTask(taskInfo.getIdTask());
		nextStagesInfo.setExpired(taskInfo.isExpired());

		Integer idTypeProcess = taskInfo.getIdTypeProcess();
		Document schema = (Document) WPC.getInstance().getSchemaMap().get(
				idTypeProcess);

		NodeList stagesTagList = schema.getElementsByTagName("state");

		Node findNodeState = getNodeByAttributeName(stagesTagList, nameStage);

		

		/* Найдем все ветвления от этапа */
		List<Node> transitions = getElements(findNodeState, "transition");

		for (Node tr : transitions) {
			boolean isAutoLateSend = false;
			NamedNodeMap trAttrs = tr.getAttributes();

			// информация о действиях на переходах. Соберем ее во время проверки
			// условия перехода
			TransitionAction action = new TransitionAction();
			boolean isSplit = trAttrs.getNamedItem("split").getNodeValue().equalsIgnoreCase("true");
			/**
			 * выполняются ли условия перехода
			 */
			boolean isTransitionExec = isTransitionExecute(tr, taskInfo.getAttributes(), action);

			/* Если условия перехода выполняются */
			if (isTransitionExec) {
				String stageToTransaction = trAttrs.getNamedItem("to").getNodeValue();

				// проверим возможен ли переход, установлено ли ограничение по
				// сбору с других этапов
				List<String> activeStages = isMayComplete(taskInfo, tr, idTypeProcess);
				NextStagesTransition stInfo = new NextStagesTransition();

				// если завершение невозможно
				if (activeStages.size() != 0) {

					// если текущий автоэтап, то выставляем для него
					// признак поздней отправки
					if (findNodeState.getAttributes().getNamedItem("auto").getNodeValue().equals("true")) {
						isAutoLateSend = true;
					} else {
						// иначе, указываем причину невозможности завершения
						stInfo = new NextStagesTransition();
						stInfo.setStatus(NextStagesInfo.Statuses.NOT_SEND);
						stInfo.setMessage(getMessageNotSendForActiveStages(activeStages));
						nextStagesInfo.addStageInfo(stInfo);
						nextStagesInfo.setResult(1);
						continue;
					}
				}

				NextStageStatus stateNextStage = defineNextStage(schema, stagesTagList, stageToTransaction, idTypeProcess);

				stInfo.setAction(action);

				switch (stateNextStage) {
				case stage: {

					stInfo.setIdStage(WPC.getInstance().getIdStageByDescription(stageToTransaction, idTypeProcess));
					stInfo.setNameStage(stageToTransaction);
					stInfo.setStatus(NextStagesInfo.Statuses.SEND);
					if (isAutoLateSend) {
						stInfo.setLate(true);
					} else {
						stInfo.setLate(isLateSend(nameStage, stagesTagList, stageToTransaction));
					}
					nextStagesInfo.addStageInfo(stInfo);
					nextStagesInfo.setResult(0);
					break;
				}
				case autoStage: {

					TaskInfo taskInfoClone = (TaskInfo) taskInfo.clone();
					wsc.setIdCurrTask(taskInfo.getIdTask().longValue());
					PluginActionImpl.executePluginAction(
					        wsc, 
					        WPC.getInstance().getIdStageByDescription(stageToTransaction, idTypeProcess), 
					        Cnst.TStages.classEntry,
							taskInfoClone);
					PluginActionImpl.executePluginAction(
					        wsc, 
					        WPC.getInstance().getIdStageByDescription(stageToTransaction,idTypeProcess), 
					        Cnst.TStages.classExit,
							taskInfoClone);

					// рекурсивно находим следующие этапы
					NextStagesInfo nextAuto = getNextStageAfterComplation(
					        wsc, stageToTransaction, taskInfo, sign);
					nextStagesInfo.addStageInfo(nextAuto.getStages());

					stInfo.setIdStage(
					        WPC.getInstance().getIdStageByDescription(stageToTransaction, idTypeProcess));
					stInfo.setNameStage(stageToTransaction);
					stInfo.setAutoStage(true);
					nextStagesInfo.addStageInfo(stInfo);

					nextStagesInfo.setResult(nextAuto.getResult());
					break;
				}
				case finalStage: {
					stInfo.setStatus(NextStagesInfo.Statuses.COMPLETE);
					stInfo.setLate(Boolean.valueOf(isAutoLateSend));
					nextStagesInfo.addStageInfo(stInfo);
					nextStagesInfo.setResult(0);

					/*List<Long> activeStages2 = taskInfo.getActiveStages();
					// проверим можно ли завершить операцию (есть ли другие
					// активные операции)
					if (activeStages2.size() > 1) {
						stInfo.setStatus(NextStagesInfo.Statuses.NOT_SEND);
						StringBuffer sb = new StringBuffer();
						sb
								.append("<td>Задание является заключительным и не может быть завершено, так как есть активные операции: <br>");
						for (Long st : activeStages2) {
							if (!st.equals(taskInfo.getIdStageTo())) {
								sb.append(" - ").append(
										WPC.getInstance().findStage(st)
												.getName()).append("</td>");
							}
						}
						stInfo.setMessage(sb.toString());
						nextStagesInfo.addStageInfo(stInfo);
						nextStagesInfo.setResult(1);
					} else {
					}*/
					break;
				}

				case subProcess: {
					stInfo.setIdStage(WPC.getInstance()
							.getIdStageByDescription(stageToTransaction,
									idTypeProcess));
					stInfo.setNameStage(stageToTransaction);
					stInfo.setStatus(NextStagesInfo.Statuses.SEND_SUB_PROCESS);

					// Object[] params_ = { taskInfo, schema, stageToTransaction
					// };
					stInfo.setLate(false);
					nextStagesInfo.addStageInfo(stInfo);
					nextStagesInfo.setResult(0);
					break;
				}

				}
			}

			/*
			 * Если этап не является расщипляющим, то другие ветвления не
			 * рассматриваем
			 */
			if (!isSplit && isTransitionExec) {
				break;
			}

		}

		if (nextStagesInfo.getStages().size() == 0) {
			StringBuffer message = new StringBuffer("<td>не может быть отправлено. Не выполнено ни одно условие перехода.");
			
			NextStagesTransition stInfo = new NextStagesTransition();
			stInfo.setStatus(NextStagesInfo.Statuses.NOT_SEND);
			
			BasicAttribute messageAttribute = taskInfo.getAttributes().findAttributeByName("Сообщение");
			if (messageAttribute != null) {
				LOGGER.info("Attribute \"message\" exists");
				
				if (messageAttribute.getAttribute()!=null && messageAttribute.getAttribute().getValueAttributeString()!=null
				        &&!messageAttribute.getAttribute().getValueAttributeString().equalsIgnoreCase("")) {
					LOGGER.info("Attribute \"message\" not empty");
					
					message.append("<br/><hr/><br/><b>").append(taskInfo.getAttributes().findAttributeByName("Сообщение").getAttribute().getValueAttributeString()).append("</b>");					
				}
			}
				
			message.append("</td>");
			
			stInfo.setMessage(message.toString());
			nextStagesInfo.addStageInfo(stInfo);
			nextStagesInfo.setResult(1);

		}

		nextStagesInfo.setSign(sign);
		return nextStagesInfo;

	}

	/**
	 * Сформировать сообщение о невозможности отправки задания по причине
	 * активных собирающих этапов
	 * 
	 * @param activeStages
	 * @return
	 */
	private static String getMessageNotSendForActiveStages(List<String> activeStages) {
		StringBuffer sb = new StringBuffer();
		sb.append("<td>не может быть отправлено пока есть активные задания на операциях: <br><strong>");
		for (String stName : activeStages) {
			sb.append(stName).append("<br>");
		}
		sb.append("</strong>");
		return sb.toString();
	}

	private static NextStageStatus defineNextStage(Document schema, NodeList nl, String stageTo, int idTypeProcess) {
		try {
			if (getNodeByAttributeName(nl, stageTo).getAttributes().getNamedItem("auto").getNodeValue().equals("true")) {
				return NextStageStatus.autoStage;
			}
		} catch (Exception e) {
		}

		Long idStageTo = WPC.getInstance().getIdStageByDescription(stageTo, idTypeProcess);
		/* Если существует следующий этап */
		if (idStageTo != null) {

			if (isNextSubProcess(idTypeProcess, stageTo)) {
				return NextStageStatus.subProcess;
			}

			return NextStageStatus.stage;
		}

		/* иначе пытаемся найти следующий этап в терминальных вершинах */
		if (isNextEndState(schema, stageTo)) {
			return NextStageStatus.finalStage;

		}

		return NextStageStatus.notDefine;

	}

	private static boolean isNextSubProcess(int idTypeProcess, String stageTo) {

		WorkflowTypeProcess process = WPC.getInstance().findTypeProcessByName(stageTo);

		if (process != null) {
			if (WPC.getInstance().findSubProcess((int) process.getIdTypeProcess(), idTypeProcess) != null) {
				return true;
			}
		}

		return false;
	}

	private static boolean isNextEndState(Document schema, String stageTo) {

		NodeList stopStages = schema.getElementsByTagName("end-state");
		int countStop = stopStages.getLength();
		for (int j = 0; j < countStop; j++) {
			Node endState = stopStages.item(j);
			String nameEndState = endState.getAttributes().getNamedItem("name").getNodeValue();
			if (nameEndState.equalsIgnoreCase(stageTo)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Проверяет, является ли отправка на следующий этап поздней
	 * 
	 * @param nameStage
	 * @param stagesTagList
	 * @param stageTo
	 * @return
	 */
	private static boolean isLateSend(String nameStage, NodeList stagesTagList, String stageToTransaction) {

		// ищем собирающий этап stageToTransaction
		for (int i = 0; i < stagesTagList.getLength(); i++) {
			Node state = stagesTagList.item(i);

			if (state.getAttributes().getNamedItem("name").getNodeValue().equals(stageToTransaction)) {

				// для собирающего этапа просмотрим все вхождения
				for (Node tr : getElements(state, "transition ")) {

					// для каждого вхождения просмотрим собирающие этапы
					for (Node coll : getElements(tr, "collect")) {

						NamedNodeMap attributes = coll.getAttributes();
						// если собирающий этап является исходным (nameStage)
						if (attributes.getNamedItem("name").getNodeValue().equals(nameStage)) {
							if (attributes.getNamedItem("type").getNodeValue().equals("late")) {
								return true;
							} else {
								return false;
							}

						}
					}

				}
			}

		}

		return false;
	}

	/**
	 * проверка возможности завершения задания
	 * 
	 * @param taskInfo
	 * @param tr
	 *            - Узел transaction XML схемы
	 * @param idTypeProcess
	 * @return Список этапов из-за которых не возможно завершение. Size == 0 -
	 *         завершение возможно
	 */

	private static List<String> isMayComplete(TaskInfo taskInfo, Node trNode, Integer idTypeProcess) {

		List<String> res = new ArrayList<String>();
		int countActivStages = taskInfo.getCountActiveStages();
		if (countActivStages > 1) {

			List<Long> activStages = taskInfo.getActiveStages();

			// список этапов которые должны собраться на текущем этапе
			List<Node> nodesCollect = getElements(trNode, "collect");

			for (Node tr : nodesCollect) {
				String nameStageCollect = tr.getAttributes().getNamedItem("name").getNodeValue();
				Long idStageCollect = WPC.getInstance().getIdStageByDescription(nameStageCollect, idTypeProcess);

				for (Long actSt : activStages) {
					if (!actSt.equals(taskInfo.getIdStageTo()) && actSt.equals(idStageCollect)) {
						res.add((String) WPC.getInstance().getData(Cnst.TBLS.stages, actSt, Cnst.TStages.name));
					}
				}
			}
		}
		return res;
	}

	/**
	 * * Проверяет, можно ли данному этапу завершаться
	 * 
	 * @return res
	 */

	/**
	 * Получить список дочерних узлов узла node с именем nameTag
	 * 
	 * @param node
	 * @param nameTag
	 * @return res
	 */
	public static List<Node> getElements(Node node, String nameTag) {

		List<Node> res = new ArrayList<Node>();
		NodeList ch = node.getChildNodes();

		for (int i = 0; i < ch.getLength(); i++) {

			Node tr = ch.item(i);

			/* Если элемент не текстовый, а узловой */
			if (tr instanceof Element) {
				if (tr.getNodeName().equals(nameTag)) {
					res.add(tr);
				}
			}
		}

		return res;
	}

	/**
	 * Выполняются ли условия перехода элемента tr. Еще этот метод, возможно,
	 * делает изменение атрибутов на переходе.
	 * 
	 * @param tr
	 *            - одна из веток, на которую переходит заявка (кусок XML)
	 * @param attributes
	 * @return res
	 */
	private static boolean isTransitionExecute(Node tr, AttributesStructList attributes, TransitionAction transitionAction) throws Exception {

		/* Найдем все условия ветвления */
		List<Node> expressions = getElements(tr, "expression");

		for (Node exp : expressions) {
			String exprCondition = exp.getAttributes().getNamedItem("condition").getNodeValue();
			exprCondition = editExpretion(exprCondition);

			JEP parser = new JEP();

			List<Node> vars = getElements(exp, "var");

			// сохраним данные о переменных для последующего выполнения действий
			// на переходах
			Map<String, Object[]> varsData = new HashMap<String, Object[]>();

			for (Node var : vars) {

				String nameVar = var.getAttributes().getNamedItem("name").getNodeValue();
				String expVar = var.getAttributes().getNamedItem("exp").getNodeValue();

				LOGGER.info("attributes findAttributeByName: " + nameVar);

				Attribute attr = attributes.findAttributeByName(nameVar).getAttribute();

				String valueVar = attr.getValueAttributeString();
				if (valueVar == null)
					valueVar = "";

				VariablesType type = attr.getTypeVar();
				Object[] ob = new Object[3];
				ob[0] = expVar;
				ob[1] = type;
				ob[2] = valueVar;

				varsData.put(nameVar, ob);

				/*if (attr.isArray()) {// TODO понять
				    try {
				        exprCondition = setArray(parser, attr.getValueAttributeList(), exprCondition, expVar, type);
                    } catch (Exception e) {
                        LOGGER.warning("attr="+attr.toString());
                        LOGGER.log(Level.WARNING, e.getMessage(), e);
                    }
				}*/
				//if (!attr.isArray()) {
					switch (type.value) {
						case VariablesType.STRING:
						case VariablesType.DATE:
						case VariablesType.USER:
						case VariablesType.URL:
							parser.addVariable(expVar, valueVar);
							break;
						case VariablesType.SELECT:
							parser.addVariable(expVar, valueVar);
							break;
						case VariablesType.INTEGER:
							if (valueVar.equals("")) {
								valueVar = "0";
							}
							parser.addVariable(expVar, Integer.valueOf(valueVar));
							break;
						case VariablesType.FLOAT:
							if (valueVar.equals("")) {
								valueVar = "0";
							}
							parser.addVariable(expVar, Double.valueOf(valueVar));
							break;
						case VariablesType.BOOLEAN:
							if (valueVar.equals(""))
								valueVar = "false";
							Boolean b = Boolean.valueOf(valueVar.toLowerCase());
							parser.addVariable(expVar, (b.booleanValue() ? 1 : 0));
							break;

					}
				/*} else {
					parser.addVariable(expVar, null);
				}*/
			}
			transitionAction.setJep(parser);
			String actionExpression = exp.getAttributes().getNamedItem("action").getNodeValue();

			actionExpression = editExpretion(actionExpression);
			transitionAction.setActionExpression(actionExpression);
			transitionAction.setVarsData(varsData);

			if (exprCondition.trim().equals(""))
				return true;
			parser.parseExpression(exprCondition);
			if (parser.getErrorInfo() == null) {
				if (parser.getValue() == 0) {
					return false;
				}
				if (parser.getValue() == 1) {
					return true;
				}

			}

			return false;

		}

		return true;

	}

	private static String editExpretion(String exprCondition) {
		String res = new String(exprCondition);
		res = res.replaceAll("##", "&&");
		res = res.replaceAll("TRUE", "1");
		res = res.replaceAll("FALSE", "0");
		res = res.replaceAll("'TRUE'", "1");
		res = res.replaceAll("'FALSE'", "0");
		res = res.replaceAll("true", "1");
		res = res.replaceAll("false", "0");
		res = res.replaceAll("'true'", "1");
		res = res.replaceAll("'false'", "0");
		res = res.replaceAll("'", "\"");
		return res;
	}

	/**
	 * Метод, в котором иногда появляется исключение. Что за параметры и что за
	 * строку возвращает пока непонятно.
	 * 
	 * @param parser
	 * @param valueVar
	 * @param expression
	 * @param expVar
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static String setArray(JEP parser, List<String> valueVar, String expression, String expVar, VariablesType type) {

		List arr = (List) valueVar;
		String result = expression;

		try {

			String regTempl = "(" + expVar + ")\\s*([|=]=)\\s*([^|\\(\\)<>=&!]+)";
			Pattern reg = Pattern.compile(regTempl);

			while (reg.matcher(result).matches()) {

				Matcher m = reg.matcher(result);// TODO здесь ошибка
				String chExpr = m.group(0);

				String operator = m.group(2);
				String value = m.group(3);
				String subExpression = "(";

				for (int k = 0; k < arr.size(); k++) {
					String val = (String) arr.get(k);
					String idxExpVar = expVar + "___" + k;
					subExpression += idxExpVar + " == " + value;

					if (k != arr.size() - 1) {
						if (operator.startsWith("=")) {
							subExpression += " && ";
						} else {
							subExpression += " || ";
						}
					}

					switch (type.value) {
						case VariablesType.STRING:
							parser.addVariable(idxExpVar, val);
							break;
						case VariablesType.SELECT:
							parser.addVariable(idxExpVar, val);
							break;
						case VariablesType.INTEGER:
							parser.addVariable(idxExpVar, Integer.valueOf(val));
							break;
						case VariablesType.FLOAT:
							parser.addVariable(idxExpVar, Double.valueOf(val));
							break;
						case VariablesType.BOOLEAN:
							Boolean b = Boolean.valueOf((val).toLowerCase());
							parser.addVariable(idxExpVar, (b.booleanValue() ? 1 : 0));
							break;

					}

				}

				subExpression += ")";

				result = result.replaceAll(chExpr, subExpression);

			}

			return result;
		} catch (PatternSyntaxException e) {
			e.printStackTrace();
			LOGGER.severe(e.getMessage());
		}
		return "";
	}

	private static Node getNodeByAttributeName(NodeList nodeList, String attrName) {

		Node resNode = null;
		int countNodes = nodeList.getLength();

		for (int i = 0; i < countNodes; i++) {
			Node node = nodeList.item(i);
			NamedNodeMap attr = node.getAttributes();
			String name = attr.getNamedItem("name").getNodeValue().trim(); 
			if (name.equals(attrName.trim())) {
				return node;
			}

		}

		return resNode;

	}

	/**
	 * Возвращает идентификатор первого этапа
	 * 
	 * @param idTypeProc
	 * @return res
	 */
	public static Long getStartIdStage(Integer idTypeProc) {

		LOGGER.info("getStartIdStage start");
		Document schema = (Document) WPC.getInstance().getSchemaMap().get(idTypeProc);

		NodeList nl = schema.getElementsByTagName("start-state");
		for (int i = 0; i < nl.getLength(); i++) {
			Node state = nl.item(i);
			NodeList transition = state.getChildNodes();

			for (int j = 0; j < transition.getLength(); j++) {

				Node tr = transition.item(j);

				/* Если элемент не текстовый, а узловой */
				if (tr.getNodeType() == 1) {
					if (tr.getNodeName().equals("transition")) {
						String stageTo = tr.getAttributes().getNamedItem("to").getNodeValue();
						LOGGER.info("start stage is " + stageTo);

						return WPC.getInstance().getIdStageByDescription(stageTo, idTypeProc);

					}
				}

			}

		}
		return null;

	}

	public static ArrayList<Object[]> getParamsForLoadAttributes(XmlDocument attributesDef) throws Exception {

		NodeList attrs = attributesDef.getElementsByTagName("attribute");
		int countAttr = attrs.getLength();

		ArrayList<Object[]> params = new ArrayList<Object[]>();
		// List namesOrderList = new ArrayList();

		for (int i = 0; i < countAttr; i++) {
			Node nodeAtr = attrs.item(i);
			String name = nodeAtr.getAttributes().getNamedItem("name").getNodeValue();
			String description = nodeAtr.getAttributes().getNamedItem("description").getNodeValue();
			// namesOrderList.add(name);
			String type = nodeAtr.getAttributes().getNamedItem("type").getNodeValue();
			String addition = nodeAtr.getAttributes().getNamedItem("addition").getNodeValue();
			String defaultValue = nodeAtr.getAttributes().getNamedItem("default").getNodeValue();
			String isId = "false";
			try {
				isId = nodeAtr.getAttributes().getNamedItem("is_id").getNodeValue();
			} catch (Exception e) {
			}
			String isMain = "false";
			try {
				isMain = nodeAtr.getAttributes().getNamedItem("is_main").getNodeValue();

			} catch (Exception e) {

			}
			String name_ds = "";
			try {
				name_ds = nodeAtr.getAttributes().getNamedItem("ds").getNodeValue();
			} catch (Exception e) {
			}
			Object[] param = new Object[11];
			param[0] = name.trim();
			param[1] = description.trim();
			param[2] = type.trim();
			param[3] = addition.trim();
			param[4] = defaultValue.trim();
			param[5] = new Integer(isId.equalsIgnoreCase("true") ? 1 : 0);
			param[6] = new Integer(isMain.equalsIgnoreCase("true") ? 1 : 0);
			param[7] = "ID_TYPE_PROCESS";
			param[8] = "ID_TRANSACTION";
			param[9] = (i + 1);
			param[10] = name_ds;
			params.add(param);
		}

		return params;

	}

	public static Object[] getParamsForLoadEdges(XmlDocument processDef) {

		Object[] res = new Object[2];
		List<Object[]> paramsEdges = new ArrayList<Object[]>();
		List<Object[]> paramsEdgesVars = new ArrayList<Object[]>();
		getEdgeParams(processDef, "state", paramsEdges, paramsEdgesVars);
		getEdgeParams(processDef, "start-state", paramsEdges, paramsEdgesVars);
		res[0] = paramsEdges;
		res[1] = paramsEdgesVars;

		return res;
	}

	private static void getEdgeParams(XmlDocument processDef, String nametag, List<Object[]> paramsEdges, List<Object[]> paramsEdgesVars) {

		NodeList stages = processDef.getElementsByTagName(nametag);
		int countStages = stages.getLength();

		for (int i = 0; i < countStages; i++) {
			Node nodeStager = stages.item(i);

			String stageFrom = nodeStager.getAttributes().getNamedItem("name").getNodeValue();

			NodeList child = nodeStager.getChildNodes();
			int order = 0;
			for (int p = 0; p < child.getLength(); ++p) {

				Object[] param = new Object[8];
				if (nametag.equalsIgnoreCase("start-state")) {
					param[0] = "NULL_String";
				} else {
					param[0] = stageFrom;
				}
				Node nodeChild = child.item(p);
				if (nodeChild.getNodeName() == "transition") {
					String toStageName = nodeChild.getAttributes().getNamedItem("to").getNodeValue();
					if (isEndStage(processDef, toStageName)) {
						param[1] = "NULL_String";
					} else {
						param[1] = toStageName;
					}
					boolean isSplit = nodeChild.getAttributes().getNamedItem("split").getNodeValue().equalsIgnoreCase("true");
					param[2] = isSplit;
					param[3] = ++order;
					param[4] = "";// condition
					param[5] = "";// action
					param[6] = "ID_TYPE_PROCESS";
					String edgeNameID = nodeChild.getAttributes().getNamedItem("name").getNodeValue();
					param[7] = edgeNameID;
					// looking for action node
					NodeList transitionChilds = nodeChild.getChildNodes();
					for (int childNum = 0; childNum < transitionChilds.getLength(); ++childNum) {

						Node expression = transitionChilds.item(childNum);
						if (expression.getNodeName() == "expression") {
							param[4] = expression.getAttributes().getNamedItem("condition").getNodeValue();
							param[5] = expression.getAttributes().getNamedItem("action").getNodeValue();

							setParamsEdgesVars(paramsEdgesVars, edgeNameID, expression);

						}
					}
					paramsEdges.add(param);
				}

			}

		}
	}

	private static void setParamsEdgesVars(List<Object[]> paramsEdgesVars, String edgeNameID, Node expression) {
		NodeList expressionChilds = expression.getChildNodes();
		for (int w = 0; w < expressionChilds.getLength(); ++w) {
			Node var = expressionChilds.item(w);
			if (var.getNodeName() == "var") {
				Object[] paramVars = new Object[4];
				paramVars[0] = edgeNameID;
				paramVars[1] = var.getAttributes().getNamedItem("name").getNodeValue();
				paramVars[2] = var.getAttributes().getNamedItem("exp").getNodeValue();
				paramVars[3] = "ID_TYPE_PROCESS";

				paramsEdgesVars.add(paramVars);
			}
		}
	}

	private static boolean isEndStage(XmlDocument processDef, String toStageName) {
		NodeList stages = processDef.getElementsByTagName("end-state");
		for (int i = 0; i < stages.getLength(); i++) {
			String name = stages.item(i).getAttributes().getNamedItem("name").getNodeValue();
			if (toStageName.equalsIgnoreCase(name))
				return true;
		}
		return false;
	}

	/**
	 * Read parameters to load stages from xml file
	 * 
	 * @param stagesDef
	 * @return
	 */
	public static ArrayList<Object[]> getParamsForLoadStages(XmlDocument stagesDef) {

		NodeList stages = stagesDef.getElementsByTagName("stage");
		int countStages = stages.getLength();

		ArrayList<Object[]> params = new ArrayList<Object[]>();

		for (int i = 0; i < countStages; i++) {
			Node nodeStager = stages.item(i);
			String name = nodeStager.getAttributes().getNamedItem("name").getNodeValue();

			NodeList child = nodeStager.getChildNodes();
			Object[] param = new Object[8];
			param[0] = name;

			for (int j = 0; j < child.getLength(); j++) {

				Node nodeChild = child.item(j);

				if (nodeChild instanceof Element) {
					String nameChild = nodeChild.getNodeName();
					Node firstCh = nodeChild.getFirstChild();

					if (nameChild.equals("duration")) {

						param[1] = new Integer(nodeChild.getAttributes().getNamedItem("count").getNodeValue());
						param[2] = new Integer(nodeChild.getAttributes().getNamedItem("types").getNodeValue());

					} else if (nameChild.equals("attentionTime")) {
						if (firstCh == null)
							param[3] = new Integer(1);
						else
							param[3] = new Integer(firstCh.getNodeValue());
					} else if (nameChild.equals("actionBeforEnter")) {
						if (firstCh == null)
							param[4] = "";
						else
							param[4] = firstCh.getNodeValue();
					} else if (nameChild.equals("actionAfterExit")) {
						if (firstCh == null)
							param[5] = "";
						else
							param[5] = firstCh.getNodeValue();
					}
					param[6] = "ID_TYPE_PROCESS";
					param[7] = "ID_TRANSACTION";

				}

			}

			params.add(param);
		}

		return params;

	}

	/**
	 * возвращает параметры, которые будут переданы в
	 * packetBean.setParamsForLoadRoles.
	 * 
	 * @param rolesDef
	 *            документ rolesDefinition.xml
	 * @param nameTypeProcess
	 *            - имя процесса
	 * @param idAdmin
	 *            идентификатор администратора процесса для новых процессов. При
	 *            загрузки нового процесса этот пользователь будет автоматически
	 *            назначен на роль Администратор процесса.
	 * @return Сложная непонятная структура. Без 100 грамм мозга не разберешься.
	 *         Возвращает пару. Первый элемент - это какой-то
	 *         ArrayList<Object[]>. Который потом пойдет в хранимку LOAD_ROLES
	 * @author Ижевцы
	 */
	public static Object[] getParamsForLoadRoles(XmlDocument rolesDef, String nameTypeProcess, Long idAdmin) {

		NodeList rolesList = rolesDef.getElementsByTagName("role");
		int countRoles = rolesList.getLength();

		List<Object[]> params = new ArrayList<Object[]>();
		List<Object[]> roleNodesParams = new ArrayList<Object[]>();

		for (int i = 0; i < countRoles; i++) {

			Node nodeRole = rolesList.item(i);

			String name/* название роли */= nodeRole.getAttributes().getNamedItem("name").getNodeValue();
			Object[] par = new Object[5];/* название роли, ?, ?,?,? */
			par[0] = name;// i_name_role
			par[1] = idAdmin;// i_id_user
			par[2] = "ID_TYPE_PROCESS";// i_id_type_process
			par[3] = new Integer(0);// i_is_admin
			par[4] = "ID_TRANSACTION";// i_id_transaction

			params.add(par);

			try {
				List<Node> rolesCh = getElements(getElements(nodeRole, "childs").get(0), "child");
				for (Node node : rolesCh) {
					Object[] parCh = new Object[3];
					parCh[0] = name;
					parCh[1] = node.getAttributes().getNamedItem("name").getNodeValue();
					parCh[2] = "ID_TRANSACTION";
					roleNodesParams.add(parCh);

				}

			} catch (Exception e) {

			}

		}

		// добавляем еще одну роль Администратор системы
		Object[] par = new Object[5];
		par[0] = "Администратор системы";
		par[1] = idAdmin;
		par[2] = "ID_TYPE_PROCESS";
		par[3] = new Integer(1);
		par[4] = "ID_TRANSACTION";
		params.add(par);

		Object[] res = new Object[2];
		res[0] = params;
		res[1] = roleNodesParams;

		return res;

	}

	@SuppressWarnings("unchecked")
	public static List getListStagesForRole(int i, XmlDocument rolesDef) {

		List res = new ArrayList();
		NodeList roles = rolesDef.getElementsByTagName("role");

		List<Node> childs = getElements(getElements(roles.item(i), "stages").get(0), "stage");

		for (int j = 0; j < childs.size(); j++) {
			Node nodeChild = childs.get(j);
			if (nodeChild instanceof Element) {
				if (nodeChild.getNodeName().equals("stage")) {
					res.add(nodeChild.getAttributes().getNamedItem("name").getNodeValue());
				}
			}

		}

		return res;

	}

	/**
	 * Возвращает для каждого идентификатора роли список этапов для этой роли
	 * 
	 * @param rolesDef
	 * @return res
	 */
	@SuppressWarnings("unchecked")
	public static Map getStagesInRole(XmlDocument rolesDef) {

		Map res = new HashMap();
		NodeList roles = rolesDef.getElementsByTagName("role");
		for (int i = 0; i < roles.getLength(); i++) {
			List childs = getListStagesForRole(i, rolesDef);
			res.put(roles.item(i).getAttributes().getNamedItem("name").getNodeValue(), childs);
		}

		return res;

	}

	/**
	 * Получить имя первого этапа для заданного типа процесса
	 * 
	 * @param xml
	 * @return res
	 */
	public static String getStartStageName(XmlDocument xml) {

		Node startState = xml.getElementsByTagName("start-state").item(0);
		NodeList chNodes = startState.getChildNodes();
		for (int i = 0; i < chNodes.getLength(); i++) {
			Node node = chNodes.item(i);
			if (node instanceof Element) {
				if (node.getNodeName().equalsIgnoreCase("transition")) {
					return node.getAttributes().getNamedItem("to").getNodeValue();
				}
			}

		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public static String getOrderAttributes(List params) {

		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < params.size(); i++) {
			Object[] paramsMass = (Object[]) params.get(i);
			sb.append(paramsMass[0]).append('>');

		}

		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	public static List getNameStages(XmlDocument xml) {

		List res = new ArrayList();
		NodeList stages = xml.getElementsByTagName("state");

		for (int i = 0; i < stages.getLength(); i++) {
			String nameStage = stages.item(i).getAttributes().getNamedItem("name").getNodeValue();
			res.add(nameStage);
		}

		stages = xml.getElementsByTagName("transition");
		NodeList endStages = xml.getElementsByTagName("end-state");

		List endStagesStr = new ArrayList();
		for (int j = 0; j < endStages.getLength(); j++) {
			endStagesStr.add(endStages.item(j).getAttributes().getNamedItem("name").getNodeValue());
		}

		for (int i = 0; i < stages.getLength(); i++) {
			String nameStage = stages.item(i).getAttributes().getNamedItem("to").getNodeValue();
			if (!endStagesStr.contains(nameStage) /*
												 * &&
												 * !autoStages.contains(nameStage
												 * )
												 */) {
				res.add(nameStage);
			}

		}

		return res;

	}

	@SuppressWarnings("unchecked")
	public static List getNameVariables(XmlDocument xml) {

		List res = new ArrayList();
		NodeList stages = xml.getElementsByTagName("var");
		for (int i = 0; i < stages.getLength(); i++) {
			String nameVar = stages.item(i).getAttributes().getNamedItem("name").getNodeValue();
			res.add(nameVar);
		}

		return res;
	}

	/**
	 * @param rolesDef
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<Object[]> getParamsForLoadStagesInRole(XmlDocument rolesDef) {
		Map stagesInRoleMap = getStagesInRole(rolesDef);
		ArrayList<Object[]> params = new ArrayList<Object[]>();
		Iterator it = stagesInRoleMap.keySet().iterator();
		while (it.hasNext()) {
			String nameRole = (String) it.next();
			List stages = (List) stagesInRoleMap.get(nameRole);
			for (int i = 0; i < stages.size(); i++) {
				String nameStage = (String) stages.get(i);
				Object[] par = new Object[4];
				par[0] = nameRole;
				par[1] = nameStage;
				par[2] = "ID_TYPE_PROCESS";
				par[3] = "ID_TRANSACTION";
				params.add(par);
			}
		}

		return params;
	}

	/**
	 * @param object
	 * @return
	 */
	public static int getLimitDays(XmlDocument doc) {
		String limit = doc.getElementsByTagName("process-definition").item(0).getAttributes().getNamedItem("limit").getNodeValue();
		return Integer.parseInt(limit);
	}

	public static String getControlType(XmlDocument document) {

		String res = "none";

		NodeList node = document.getElementsByTagName("process-definition");
		if (node != null && node.getLength() == 1) {
			Node ct = node.item(0).getAttributes().getNamedItem("controlType");
			if (ct != null) {
				res = ct.getNodeValue();
			}

		}

		return res;
	}

	/**
	 * 1 Map : ключ - ИмяDS:ТипDS значение-Map параметров 2 Map : ключ- имя
	 * парметра, значение -значение параметра
	 * 
	 * @param attributesDef
	 * @return
	 */
	public static HashMap<String, HashMap<String, String>> getParamsForLoadDataSources(XmlDocument attributesDef) {

		HashMap<String, HashMap<String, String>> res = new HashMap<String, HashMap<String, String>>();

		NodeList ds = attributesDef.getElementsByTagName("ds");
		int countDs = ds.getLength();

		for (int i = 0; i < countDs; i++) {

			Node nodeDs = ds.item(i);
			String idDS = nodeDs.getAttributes().getNamedItem("id").getNodeValue();
			NodeList attributes = nodeDs.getChildNodes();
			String typeDS = "jdbc";

			HashMap<String, String> attrsDS = new HashMap<String, String>();

			for (int j = 0; j < attributes.getLength(); j++) {
				Node attr = attributes.item(j);
				if (attr instanceof Element) {
					Element atrTeg = (Element) attr;
					String nameAttr = atrTeg.getTagName();
					if (nameAttr.equalsIgnoreCase("jndi")) {
						typeDS = "jndi";
					}
					String valueAttr = atrTeg.getChildNodes().item(0).getNodeValue();
					attrsDS.put(nameAttr, valueAttr);

				}
			}

			res.put(idDS + ":" + typeDS, attrsDS);
		}

		return res;
	}

	public static String isExistNameTypeProcess(XmlDocument processDef) {
		String nameProcess = processDef.getElementsByTagName("process-definition").item(0).getAttributes().getNamedItem("name").getNodeValue();
		if (WPC.getInstance().findTypeProcessByName(nameProcess) == null) {
			return null;
		}
		return nameProcess;
	}

	public static List<Object[]> getParamsForLoadMapVars(Node sb, Integer idTypeProcess, String nameSP, boolean isInVars) {

		ArrayList<Object[]> res = new ArrayList<Object[]>();
		String nameTag = "varsMapIn";
		if (!isInVars) {
			nameTag = "varsMapOut";
		}

		List<Node> varsList = getElements(getElements(getElements(sb, "varsMap").get(0), nameTag).get(0), "varMap");
		for (Node node : varsList) {
			String parNameVar = node.getAttributes().getNamedItem("parentName").getNodeValue();
			String chNameVar = node.getAttributes().getNamedItem("childName").getNodeValue();
			Object[] el = new Object[5];
			el[0] = parNameVar;
			el[1] = chNameVar;
			el[2] = isInVars ? new Integer(0) : new Integer(1);
			el[3] = idTypeProcess;
			el[4] = nameSP;
			res.add(el);

		}

		return res;

	}

	public static List<Object[]> getParamsForLoadMapRoles(Node sb, Integer idTypeProcess, String nameSP) {

		List<Object[]> res = new ArrayList<Object[]>();

		List<Node> rolesList = getElements(getElements(sb, "rolesMap").get(0), "role");
		for (Node node : rolesList) {
			String parNameRole = node.getAttributes().getNamedItem("parentName").getNodeValue();
			String chNameRole = node.getAttributes().getNamedItem("childName").getNodeValue();
			Object[] el = new Object[4];
			el[0] = parNameRole;
			el[1] = chNameRole;
			el[2] = idTypeProcess;
			el[3] = nameSP;
			res.add(el);

		}

		return res;
	}

	public static String checkExistsNameProcesses(ZipFile zipfile, XmlDocument processDef, XmlDocument subProcessDef) throws IOException, SAXException {
		String res = "";

		String nameProcess = isExistNameTypeProcess(processDef);
		if (nameProcess != null) {
			res = "Процесс с именем '" + nameProcess + "' загружен в систему. <br>";
		}

		if (subProcessDef != null) {
			NodeList sbTags = subProcessDef.getElementsByTagName("subProcess");
			for (int i = 0; i < sbTags.getLength(); i++) {
				Node sb = sbTags.item(i);
				String fileName = sb.getAttributes().getNamedItem("fileName").getNodeValue();

				InputStream archStream = zipfile.getInputStream(zipfile.getEntry(fileName));

				ZipFile zipTmpFile = writeToZipFile(archStream);

				Map<String, Object> xmlDefinitions = new HashMap<String, Object>();
				getMapXMLDefinitions(zipTmpFile, xmlDefinitions);

				res += checkExistsNameProcesses(zipTmpFile, (XmlDocument) xmlDefinitions.get(WPC.PROCESS_DEFINITION), (XmlDocument) xmlDefinitions.get(WPC.SUB_PROCESS_DEFINITION));

			}
		}
		return res;
	}

	public static ArrayList<Object[]> getParamsForLoadRolesPermissions(XmlDocument rolesDef) {

		ArrayList<Object[]> res = new ArrayList<Object[]>();

		NodeList roles = rolesDef.getElementsByTagName("role");

		for (int i = 0; i < roles.getLength(); i++) {
			Node role = roles.item(i);
			String nameRole = role.getAttributes().getNamedItem("name").getNodeValue();
			List<Node> vars = getElements(getElements(role, "vars").get(0), "var");
			int order = 0;
			for (Node v : vars) {
				String varName = v.getAttributes().getNamedItem("name").getNodeValue();
				if (!WPC.getInstance().isVariableDirectVar(varName)) {
					Object[] el = new Object[5];
					el[0] = nameRole;
					el[1] = varName;
					el[2] = new Integer(0); // права доступа на просмотр
					el[3] = order++;
					el[4] = "ID_TRANSACTION";
					res.add(el);
				}

			}

		}

		return res;

	}

	public static ArrayList<Object[]> getParamsForLoadStagesPermissions(XmlDocument stagesDef) {

		ArrayList<Object[]> res = new ArrayList<Object[]>();

		NodeList stages = stagesDef.getElementsByTagName("stage");
		for (int i = 0; i < stages.getLength(); i++) {
			Node stage = stages.item(i);
			res.addAll(getParamsStagePermissions(stage, "orderView"));
			res.addAll(getParamsStagePermissions(stage, "orderExpand"));
			res.addAll(getParamsStagePermissions(stage, "orderEdit"));
		}

		return res;
	}

	private static List<Object[]> getParamsStagePermissions(Node stage, String nameTag) {

		List<Object[]> res = new ArrayList<Object[]>();

		List<Node> vars = getElements(getElements(stage, nameTag).get(0), "var");

		Integer permissionValue = 0;

		if (nameTag.equals("orderExpand")) {
			permissionValue = 1;
		} else if (nameTag.equals("orderView")) {
			permissionValue = 2;
		} else if (nameTag.equals("orderEdit")) {
			permissionValue = 3;
		}

		int order = 0;
		for (Node v : vars) {
			String nameVar = v.getAttributes().getNamedItem("name").getNodeValue();
			// не включаем директивную переменную
			if (!WPC.getInstance().isVariableDirectVar(nameVar)) {
				Object[] el = new Object[5];
				el[0] = stage.getAttributes().getNamedItem("name").getNodeValue();

				el[1] = nameVar;
				el[2] = permissionValue;
				el[3] = order++;
				el[4] = "ID_TRANSACTION";
				res.add(el);
			}

		}

		return res;

	}

	public static ArrayList<Object[]> getParamsForLoadVarConnections(XmlDocument attributesDef) {

		ArrayList<Object[]> res = new ArrayList<Object[]>();
		NodeList attrs = attributesDef.getElementsByTagName("attribute");

		for (int i = 0; i < attrs.getLength(); i++) {
			Node attr = attrs.item(i);
			String namePar = attr.getAttributes().getNamedItem("name").getNodeValue();
			List<Node> childs = getElements(getElements(attr, "childs").get(0), "child");
			for (Node ch : childs) {
				String nameCh = ch.getAttributes().getNamedItem("name").getNodeValue();
				Object[] el = new Object[4];
				el[0] = namePar;
				el[1] = nameCh;
				el[2] = childs.indexOf(ch) + 1;
				el[3] = "ID_TRANSACTION";
				res.add(el);
			}
		}

		return res;
	}

	public static ArrayList<Object[]> getParamsForLoadVarNodes(XmlDocument attributesDef) {

		ArrayList<Object[]> res = new ArrayList<Object[]>();

		NodeList attrs = attributesDef.getElementsByTagName("attribute");

		List<Map<String, String>> inDataGraph = new ArrayList<Map<String, String>>();

		for (int i = 0; i < attrs.getLength(); i++) {
			Node attr = attrs.item(i);
			String namePar = attr.getAttributes().getNamedItem("name").getNodeValue();

			List<Node> childs = getElements(getElements(attr, "childs").get(0), "child");
			for (Node ch : childs) {
				String nameCh = ch.getAttributes().getNamedItem("name").getNodeValue();
				Map<String, String> inMapData = new HashMap<String, String>();
				inMapData.put("id_var_parrent", namePar);
				inMapData.put("id_var_child", nameCh);
				inMapData.put("order", String.valueOf(childs.indexOf(ch) + 1));
				inDataGraph.add(inMapData);
			}
		}

		MGraph<String> graph = new MGraph<String>(inDataGraph, "id_var_parrent", "id_var_child", "order");
		res = (ArrayList<Object[]>) graph.getVarNodes();
		return res;
	}

	public static ArrayList<Object[]> getParamsForLoadSelectVarValues(XmlDocument attributesDef) {

		ArrayList<Object[]> res = new ArrayList<Object[]>();

		NodeList attrs = attributesDef.getElementsByTagName("attribute");

		for (int i = 0; i < attrs.getLength(); i++) {
			Node attr = attrs.item(i);
			String nameVar = attr.getAttributes().getNamedItem("name").getNodeValue();

			List<Node> options = getElements(getElements(attr, "select").get(0), "option");
			for (Node op : options) {
				String option = op.getAttributes().getNamedItem("value").getNodeValue();

				Object[] el = new Object[3];
				el[0] = nameVar;
				el[1] = option;
				el[2] = "ID_TRANSACTION";
				res.add(el);

			}
		}

		return res;
	}

	/**
	 * по id схемы процесса и имени ЭТАПА возвращает все этапы, с которых
	 * возможен переход на ЭТАП
	 * 
	 * @param String
	 *            idTypeProcess - id типа процесса
	 * @param String
	 *            stageName - этап, ссылки на который надо найти
	 * @return список этапов, имеющих переход на указанный
	 */
	public static List<String> getPrecedingStages(Integer idTypeProcess, String stageName) {

		XmlDocument schema = (XmlDocument) WPC.getInstance().getSchemaMap().get(idTypeProcess);
		Element parent = schema.getDocumentElement();
		NodeList states = parent.getElementsByTagName("state");
		List<String> stageNames = new Vector<String>();
		for (int i = 0; i < states.getLength(); i++) {
			Element el = (Element) states.item(i);
			NodeList transitions = el.getElementsByTagName("transition");
			for (int j = 0; j < transitions.getLength(); j++) {
				Element trel = (Element) transitions.item(j);
				if (trel.getAttribute("to").equals(stageName)) {
					stageNames.add(el.getAttribute("name"));
					break;
				};
			}
		}
		return stageNames;
	}

	public static ZipFile writeToZipFile(InputStream stream) {

		File out;
		ZipFile zipFile = null;
		try {
			out = File.createTempFile("arh", "tmp.zip");

			out.deleteOnExit();
			FileOutputStream fw = new FileOutputStream(out);

			int b;
			while ((b = stream.read()) != -1) {
				fw.write(b);
			}
			fw.close();
			zipFile = new ZipFile(out);
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.severe("temp file problem: " + e.getMessage());
		}

		return zipFile;

	}

	@SuppressWarnings("deprecation")
	public static String getMapXMLDefinitions(ZipFile zipfile, Map<String, Object> xmlDefinitions) throws IOException, SAXException {
		String res = "ok";

		XmlDocument doc;

		String fileDef = WPC.PROCESS_DEFINITION;
		ZipEntry entry = zipfile.getEntry(fileDef);
		if (entry == null) {
			return "Отсутствует XML файл " + fileDef;
		} else {
			doc = XmlDocument.createXmlDocument(zipfile.getInputStream(entry), false);
			xmlDefinitions.put(fileDef, doc);
		}

		fileDef = WPC.ATTRIBUTES_DEFINITION;
		entry = zipfile.getEntry(fileDef);
		if (entry == null) {
			return "Отсутствует XML файл " + fileDef;
		} else {
			doc = XmlDocument.createXmlDocument(zipfile.getInputStream(entry), false);
			xmlDefinitions.put(fileDef, doc);
		}

		fileDef = WPC.STAGES_DEFINITION;
		entry = zipfile.getEntry(fileDef);
		if (entry == null) {
			return "Отсутствует XML файл " + fileDef;
		} else {
			doc = XmlDocument.createXmlDocument(zipfile.getInputStream(entry), false);
			xmlDefinitions.put(fileDef, doc);
		}

		fileDef = WPC.ROLES_DEFINITION;
		entry = zipfile.getEntry(fileDef);
		if (entry == null) {
			return "Отсутствует XML файл " + fileDef;
		} else {
			doc = XmlDocument.createXmlDocument(zipfile.getInputStream(entry), false);
			xmlDefinitions.put(fileDef, doc);
		}

		fileDef = WPC.SUB_PROCESS_DEFINITION;
		entry = zipfile.getEntry(fileDef);
		if (entry != null) {
			doc = XmlDocument.createXmlDocument(zipfile.getInputStream(entry), false);
			xmlDefinitions.put(fileDef, doc);
		}

		fileDef = WPC.IMAGE_DEFINITION;
		entry = zipfile.getEntry(fileDef);

		if (entry != null) {

			InputStream stream = zipfile.getInputStream(entry);
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
			StringBuilder sb = new StringBuilder();

			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
				e.printStackTrace();
			} finally {
				try {
					stream.close();
				} catch (IOException e) {
					LOGGER.log(Level.SEVERE, e.getMessage(), e);
					e.printStackTrace();
				}
			}

			// для XML всегда utf-8
			xmlDefinitions.put(fileDef, new String(sb.toString().getBytes("UTF-8"), "UTF-8"));
		}

		return res;
	}

	public static boolean checkValue(Attribute attribut, String attrFromPage) {

		VariablesType typeVar = attribut.getTypeVar();
		String pattern = attribut.getAddition();

		return checkValue(typeVar, pattern, attrFromPage);

	}

	public static boolean checkValue(VariablesType typeVar, String pattern, Object valueAttr) {
	    if (pattern!=null && pattern.equals("215"))
	        return true;

		switch (typeVar.value) {
			case VariablesType.BOOLEAN:
				return true;
			case VariablesType.SELECT:
				return true;
			case VariablesType.USER:
				return true;
			case VariablesType.ACTION:
				return true;
            case VariablesType.DATA_SOURCE:
                return true;
		}

		boolean res = false;

		if (pattern.equalsIgnoreCase("")) {
			if (typeVar.value == VariablesType.FLOAT) {
				pattern = "^-?\\d+\\.*\\d*$";
			}
			if (typeVar.value == VariablesType.INTEGER) {
				pattern = "^-?\\d+$";
			}
		}

		if (!pattern.equals("")) {

			// if (typeVar.equalsIgnoreCase("date"))
			if (valueAttr.equals("")) {
				return true;
			}

			try {
				Pattern reg = Pattern.compile(pattern);
				Matcher m = reg.matcher((String) valueAttr);
				res = m.matches();
			} catch (PatternSyntaxException e) {
				e.printStackTrace();
				LOGGER.severe(e.getMessage());
			}

		} else {
			res = true;
		}

		return res;

	}
}
