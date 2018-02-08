package com.vtb.util.report.utils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Pattern;

import com.aspose.words.Body;
import com.aspose.words.CompositeNode;
import com.aspose.words.ControlChar;
import com.aspose.words.Document;
import com.aspose.words.Node;
import com.aspose.words.NodeCollection;
import com.aspose.words.NodeType;
import com.aspose.words.Paragraph;
import com.aspose.words.Range;
import com.aspose.words.Run;
import com.aspose.words.Section;
import com.aspose.words.SectionStart;
import com.aspose.words.Table;

/**
 * Класс для для обработки конструкций IF-THEN-ELSE-ENDIF
 *
 * @author slysenkov
 */
public final class IfProcessor {

	//private final static Logger LOGGER = LoggerFactory.getLogger(IfProcessor.class);

	private final static String IF_TAG = "IF";
	private final static String THEN_TAG = "THEN";
	private final static String ELSE_TAG = "ELSE";
	private final static String ENDIF_TAG = "ENDIF";

	private final static String EQ_TAG = "=";
	private final static String GT_TAG = ">";
	private final static String LT_TAG = "<";
	private final static String NE_TAG = "<>";
	private final static String GE_TAG = ">=";
	private final static String LE_TAG = "<=";
	private final static String OR_TAG = " OR ";
	private final static String AND_TAG = " AND ";

	public final static String REGISTERED_MARK = "\u00ae";
	public final static String TRADEMARK = "\u2122";

//	private final static String LEFT_QUOTE = "«";
//	private final static String RIGHT_QUOTE = "»";

	private IfProcessor() {
	}

	/*****************************************************************************************/
	/**
	 *
	 * Обход дерева документа {@link Document документе} и сбор "Run"-полей в список {@link AsposeNodeListElement классов AsposeNodeListElement}
	 *
	 * @param n {@link Node стартовый нод} для обхода дерева документа
	 * @param nodeList результирующий список Run-полей
	 */
	private static void exploreWordDOM(Node n, ArrayList<AsposeNodeListElement> nodeList, int level, fieldLevel fldLevel) {
		NodeCollection<Node> childList = null;
		try {
			childList = ((CompositeNode) n).getChildNodes();
		} catch (Exception e) {
			childList = null;
		}

		if (childList == null) {
			try {
				switch (n.getNodeType()) {
					case NodeType.FIELD_START:
						fldLevel.l++;
						break;
					case NodeType.FIELD_END:
						fldLevel.l--;
						break;
					case NodeType.FIELD_SEPARATOR:
						break;
					default:
						nodeList.add(new AsposeNodeListElement(level, n, n.getText(), fldLevel.l));
						break;
				}
			} catch (Exception e) {
			}
		} else {
			for (Node child : childList) {
				exploreWordDOM(child, nodeList, level + 1, fldLevel);
			}
		}
	}

	private static void ScanWordIf(ArrayList<AsposeNodeListElement> nodeList) {
		int lastFldLevel = 0;
		Boolean ifFlag = false;
		for (AsposeNodeListElement element : nodeList) {
			if (!ifFlag && element.fldLevel != 0) {

				// System.out.println(element.text);
				if (element.text.indexOf("IF") != -1) {
					element.ifField = true;
					ifFlag = true;
				}
			} else if (lastFldLevel != 0 && element.fldLevel == 0) {
				ifFlag = false;
			}
			element.ifField = ifFlag;

			lastFldLevel = element.fldLevel;
		}
	}

	private static int validateIf(String nodeText, int len, int posIf) {
		if (posIf == -1)
			return -1;
		int result = -1;

		try {
			if ((len - posIf) <= IF_TAG.length()) // if в самом конце рана => считаем валидным
				return posIf;
			int nextPos = posIf + IF_TAG.length();
			if (Character.isWhitespace(nodeText.charAt(nextPos))) // если после IF есть разделитель => считаем валидным
				return posIf;
		} catch (Exception ex) {
			result = -1;
		}
		return result;
	}

	/**
	 *
	 * Получение очередного ключевого слова (IF-THEN-ELSE-ENDIF)
	 *
	 * @param nodeList список Run-полей {@link AsposeNodeListElement типа AsposeNodeListElement}
	 * @param state состояние парсера {@link ParserState ParserState}
	 */
	private static Boolean getNextToken(ArrayList<AsposeNodeListElement> nodeList, ParserState state) {
		int posIF, posTHEN, posELSE, posENDIF;
		try {
			AsposeNodeListElement node = nodeList.get(state.idNode);
			if (node.ifField)
				return false;

			String nodeText = node.text;
			int textLen = nodeText.length();

			posIF = nodeText.indexOf(IF_TAG, state.inlinePos);
			posIF = validateIf(nodeText, textLen, posIF);

			posTHEN = nodeText.indexOf(THEN_TAG, state.inlinePos);
			posELSE = nodeText.indexOf(ELSE_TAG, state.inlinePos);
			posENDIF = nodeText.indexOf(ENDIF_TAG, state.inlinePos);

			if (posIF == -1 && posTHEN == -1 && posELSE == -1 && posENDIF == -1)
				return false;

			posIF = (posIF == -1) ? posIF = 100000 : posIF;
			posTHEN = (posTHEN == -1) ? posTHEN = 100000 : posTHEN;
			posELSE = (posELSE == -1) ? posELSE = 100000 : posELSE;
			posENDIF = (posENDIF == -1) ? posENDIF = 100000 : posENDIF;

			int tagLen = 0;

			if (posIF < 100000 && posIF < posTHEN && posIF < posELSE && posIF < posENDIF) {
				state.inlinePos = posIF;
				state.token = IF_TAG;
				tagLen = IF_TAG.length();
			} else if (posTHEN < 100000 && posTHEN < posIF && posTHEN < posELSE && posTHEN < posENDIF) {
				state.inlinePos = posTHEN;
				state.token = THEN_TAG;
				tagLen = THEN_TAG.length();
			} else if (posELSE < 100000 && posELSE < posIF && posELSE < posTHEN && posELSE < posENDIF) {
				state.inlinePos = posELSE;
				state.token = ELSE_TAG;
				tagLen = ELSE_TAG.length();
			} else if (posENDIF < 100000 && posENDIF < posIF && posENDIF < posTHEN && posENDIF < posELSE) {
				state.inlinePos = posENDIF;
				state.token = ENDIF_TAG;
				tagLen = ENDIF_TAG.length();
			}
			node.removeOrder.add(new ModifyOrder(state.inlinePos, state.inlinePos + tagLen));
			node.toRemove = (textLen == tagLen);
			return true;

		} catch (Exception e) {
		}
		return false;
	}

	/**
	 *
	 * Создание нового IF'а
	 *
	 * @param nodeList список Run-полей {@link AsposeNodeListElement типа AsposeNodeListElement}
	 * @param localState текущее состояние парсера {@link ParserState ParserState}
	 * @param thenBranch true - если новый IF вложен в THEN-блок внешнего IF'а или нет внешних IF'ов false - если новый IF вложен в ELSE-блок внешнего IF'а
	 */
	private static void createNewIf(ArrayList<AsposeNodeListElement> nodeList, Stack<ParserState> st, ParserState localState, Boolean thenBranch) {
		ParserState state = st.peek();
		ParserState newState = new ParserState();
		newState.token = IF_TAG;
		newState.idNode = localState.idNode;
		newState.ifLevel = state.ifLevel + 1;
		newState.inlinePos = localState.inlinePos;
		IfObject newIfObj = new IfObject();
		newIfObj.ifAttr = new KeyWordAttributes(localState.token, localState.idNode, localState.inlinePos, localState.inlinePos + localState.token.length());
		newIfObj.domLevel = nodeList.get(localState.idNode).level;
		newIfObj.ifLevel = state.ifLevel + 1;

		newState.ifObj = newIfObj;
		if (thenBranch)
			state.ifObj.trueBranch.add(newIfObj);
		else
			state.ifObj.falseBranch.add(newIfObj);
		st.push(newState);
	}

	/**
	 *
	 * Получение обычного текста (plain-text) по номеру начального и конечного узла и позициям начала текста в текте начального узла, и конца теста в тексте конечного узла Собирается весь текст от
	 * начальной позиции в начальном узле + текст всех промежуточных узлов + текст до позиции конца текста в конечном узле
	 *
	 * @param nodeList список Run-полей {@link AsposeNodeListElement типа AsposeNodeListElement}
	 * @param startIndex индекс начального узла в списке nodeList
	 * @param startPos индекс начала текста в текстовом поле начального узла
	 * @param endIndex индекс конечного узла в списке nodeList
	 * @param endPos индекс конца текста в текстовом поле конечного узла
	 * @param thenBranch true - если новый IF вложен в THEN-блок внешнего IF'а или нет внешних IF'ов false - если новый IF вложен в ELSE-блок внешнего IF'а
	 * @return результирующая строка текста
	 */
	private static String getTextFromNodes(ArrayList<AsposeNodeListElement> nodeList, int startIndex, int startPos, int endIndex, int endPos) {
		String str = "";
		StringBuilder bldr = new StringBuilder("");
		String currentStr = "";
		if (startIndex == endIndex) {
			str = nodeList.get(startIndex).text;
			str = str.substring(startPos, endPos);
			return str;
		}

		for (int i = startIndex; i <= endIndex; i++) {
			currentStr = nodeList.get(i).text;
			if (i == startIndex)
				currentStr = currentStr.substring(startPos);
			else if (i == endIndex)
				currentStr = currentStr.substring(0, endPos);
			bldr.append(currentStr);
			// str += currentStr;
		}
		str = bldr.toString();
		return str;
	}

	/**
	 *
	 * Сравнение левой и правой части условного выражения из IF
	 *
	 * @param left строка-левый операнд для сравнения
	 * @param right строка-правый операнд для сравнения
	 * @param sign строка-знак сравнения
	 * @param result результат операции для возврата кода ошибки и сообщения об ошибке {@link ParseResult типа ParseResult}
	 * @return true - если результат сравнения ИСТИНА
	 */
	private static Boolean compareStrings(String left, String right, String sign, ParseResult result) {
		if (sign.equals(EQ_TAG))
			return left.equals(right);
		if (sign.equals(NE_TAG))
			return (!left.equals(right));

		int l = 0;
		int r = 0;
		try {
			if (left != null && !left.isEmpty())
				l = Integer.parseInt(left);
			if (right != null && !right.isEmpty())
				r = Integer.parseInt(right);
			if (sign.equals(LT_TAG))
				return (l < r);
			if (sign.equals(GT_TAG))
				return (l > r);
			if (sign.equals(LE_TAG))
				return (l <= r);
			if (sign.equals(GE_TAG))
				return (l >= r);

		} catch (Exception e) {
			result.code = 3;
			result.errorMessage = "PARSER: Ошибка интерпретации числа ";
		}
		return true;
	}

	private static Boolean processConditionString(String str, ParseResult result) {
		String leftOperand = "";
		String rightOperand = "";
		String cond = "";

		int posEQ = str.indexOf(EQ_TAG);
		int posLT = str.indexOf(LT_TAG);
		int posGT = str.indexOf(GT_TAG);
		int posNE = str.indexOf(NE_TAG);
		int posLE = str.indexOf(LE_TAG);
		int posGE = str.indexOf(GE_TAG);

		if (posEQ == -1 && posLT == -1 && posGT == -1 && posNE == -1) {
			result.code = 2;
			result.errorMessage = "PARSER: неправильно задано условие сравнения ";
			return false;
		}
		if (posNE != -1) {
			cond = NE_TAG;
			leftOperand = str.substring(0, posNE);
			rightOperand = str.substring(posNE + NE_TAG.length());
		} else if (posGE != -1) {
			cond = GE_TAG;
			leftOperand = str.substring(0, posGE);
			rightOperand = str.substring(posGE + GE_TAG.length());
		} else if (posLE != -1) {
			cond = LE_TAG;
			leftOperand = str.substring(0, posLE);
			rightOperand = str.substring(posLE + LE_TAG.length());
		} else if (posEQ != -1) {
			cond = EQ_TAG;
			leftOperand = str.substring(0, posEQ);
			rightOperand = str.substring(posEQ + EQ_TAG.length());
		} else if (posLT != -1) {
			cond = LT_TAG;
			leftOperand = str.substring(0, posLT);
			rightOperand = str.substring(posLT + LT_TAG.length());
		} else if (posGT != -1) {
			cond = GT_TAG;
			leftOperand = str.substring(0, posGT);
			rightOperand = str.substring(posGT + GT_TAG.length());
		}
		return compareStrings(leftOperand.trim().toLowerCase(), rightOperand.trim().toLowerCase(), cond, result);
	}

	private static Boolean processMultiConditionString(String str, ParseResult result) {
		Boolean res = false;
		try {
			int indexOR = str.indexOf(OR_TAG);
			int indexAND = str.indexOf(AND_TAG);
			String[] conds;
			if (indexOR != -1 && indexAND != -1) {
				result.code = 80;
				result.errorMessage = "PARSER: недопустимо одновременно использовать OR и AND в условии  ";
			} else if (indexOR != -1) {
				conds = str.split(OR_TAG);
				for (String cond : conds) {
					if (processConditionString(cond, result)) {
						res = true;
						break;
					}
				}
			} else if (indexAND != -1) {
				conds = str.split(AND_TAG);
				res = true;
				for (String cond : conds) {
					if (!processConditionString(cond, result)) {
						res = false;
						break;
					}
				}
			} else
				res = processConditionString(str, result);
		} catch (Exception ex) {
			result.code = 20;
			result.errorMessage = "PARSER: ошибка разбора условия ";
		}
		return res;
	}

	/**
	 *
	 * Извлечение текста-условия из промежутка между операторами IF и THEN сохранение операндов, анализ истинности условия
	 *
	 * @param nodeList список Run-полей {@link AsposeNodeListElement типа AsposeNodeListElement}
	 * @param localState текущее состояние парсера {@link ParserState ParserState}
	 * @param result результат операции для возврата кода ошибки и сообщения об ошибке {@link ParseResult типа ParseResult}
	 */
	private static void getConditions(ArrayList<AsposeNodeListElement> nodeList, ParserState state/* IfObject ifobj */, ParseResult result) {
		IfObject ifobj = state.ifObj;
		int statrtNode = ifobj.ifAttr.nodeIndex;
		int startPos = ifobj.ifAttr.endPos;

		int endNode = ifobj.thenAttr.nodeIndex;
		int endPos = ifobj.thenAttr.startPos;

		String str = getTextFromNodes(nodeList, statrtNode, startPos, endNode, endPos);

		ifobj.leftConditionOperand = "";
		ifobj.rightConditionOperand = "";
		ifobj.condition = "";
		// ifobj.expressionResult = processConditionString(str, result);
		ifobj.expressionResult = processMultiConditionString(str, result);
		/*
		 * String leftOperand = ""; String rightOperand = ""; String cond = "";
		 *
		 * int posEQ = str.indexOf(EQ_TAG); int posLT = str.indexOf(LT_TAG); int posGT = str.indexOf(GT_TAG); int posNE = str.indexOf(NE_TAG); int posLE = str.indexOf(LE_TAG); int posGE =
		 * str.indexOf(GE_TAG);
		 *
		 * if (posEQ==-1 && posLT==-1 && posGT==-1 && posNE==-1) { result.code = 2; result.errorMessage = "PARSER: неправильно задано условие сравнения "; return; } if (posNE != -1) { cond = NE_TAG;
		 * leftOperand = str.substring(0, posNE); rightOperand = str.substring(posNE+NE_TAG.length()); } else if (posGE != -1) { cond = GE_TAG; leftOperand = str.substring(0, posGE); rightOperand =
		 * str.substring(posGE+GE_TAG.length()); } else if (posLE != -1) { cond = LE_TAG; leftOperand = str.substring(0, posLE); rightOperand = str.substring(posLE+LE_TAG.length()); } else if (posEQ
		 * != -1) { cond = EQ_TAG; leftOperand = str.substring(0, posEQ); rightOperand = str.substring(posEQ+EQ_TAG.length()); } else if (posLT != -1) { cond = LT_TAG; leftOperand = str.substring(0,
		 * posLT); rightOperand = str.substring(posLT+LT_TAG.length()); } else if (posGT != -1) { cond = GT_TAG; leftOperand = str.substring(0, posGT); rightOperand =
		 * str.substring(posGT+GT_TAG.length()); } ifobj.leftConditionOperand = leftOperand.trim().toLowerCase(); ifobj.rightConditionOperand = rightOperand.trim().toLowerCase(); ifobj.condition =
		 * cond; ifobj.expressionResult = compareStrings(ifobj.leftConditionOperand, ifobj.rightConditionOperand, ifobj.condition, result);
		 */
	}

	/**
	 *
	 * Добавление ключевого слова THEN в дерево условных операторов
	 *
	 * @param nodeList список Run-полей {@link AsposeNodeListElement типа AsposeNodeListElement}
	 * @param st стек состояний парсера {@link ParserState ParserState}
	 * @param localState текущее состояние парсера {@link ParserState ParserState}
	 * @param result результат операции для возврата кода ошибки и сообщения об ошибке {@link ParseResult типа ParseResult}
	 */
	private static void processThen(ArrayList<AsposeNodeListElement> nodeList, Stack<ParserState> st, ParserState localState, ParseResult result) {
		ParserState state = st.peek();
		// ResetResult(localState, result);
		int nodeLevel = nodeList.get(localState.idNode).level;
		if (state.ifObj.domLevel != nodeLevel) {
			result.code = 10;
			result.errorMessage = "PARSER: ключевое слово THEN находится на другом уровне иерархии документа ";
			return;
		}
		state.ifObj.thenAttr = new KeyWordAttributes(localState.token, localState.idNode, localState.inlinePos, localState.inlinePos + localState.token.length());
		getConditions(nodeList, state/* state.ifObj */, result);
		state.token = THEN_TAG;
	}

	/**
	 *
	 * Добавление ключевого слова ELSE в дерево условных операторов
	 *
	 * @param nodeList список Run-полей {@link AsposeNodeListElement типа AsposeNodeListElement}
	 * @param st стек состояний парсера {@link ParserState ParserState}
	 * @param localState текущее состояние парсера {@link ParserState ParserState}
	 * @param result результат операции для возврата кода ошибки и сообщения об ошибке {@link ParseResult типа ParseResult}
	 */
	private static void processElse(ArrayList<AsposeNodeListElement> nodeList, Stack<ParserState> st, ParserState localState, ParseResult result) {
		ParserState state = st.peek();
		// ResetResult(localState, result);
		int nodeLevel = nodeList.get(localState.idNode).level;
		if (state.ifObj.domLevel != nodeLevel) {
			result.code = 10;
			result.errorMessage = "PARSER: ключевое слово ELSE находится на другом уровне иерархии документа ";
			return;
		}
		state.ifObj.elseAttr = new KeyWordAttributes(localState.token, localState.idNode, localState.inlinePos, localState.inlinePos + localState.token.length());
		state.token = ELSE_TAG;
	}

	/**
	 *
	 * Инициализация кода ошибки (==0) и перенос информации об индексе узла и позиции текста в узле в класс-результат
	 *
	 * @param localState текущее состояние парсера {@link ParserState ParserState}
	 * @param result результат {@link ParseResult типа ParseResult}
	 */
	private static void resetResult(ParserState localState, ParseResult result) {
		result.code = 0;
		result.errorMessage = "";
		result.idNode = localState.idNode;
		result.inlinePos = localState.inlinePos;
	}

	/**
	 *
	 * Добавление ключевого слова ENDIF в дерево условных операторов
	 *
	 * @param nodeList список Run-полей {@link AsposeNodeListElement типа AsposeNodeListElement}
	 * @param st стек состояний парсера {@link ParserState ParserState}
	 * @param localState текущее состояние парсера {@link ParserState ParserState}
	 * @param result результат операции для возврата кода ошибки и сообщения об ошибке {@link ParseResult типа ParseResult}
	 */
	private static void processEndif(ArrayList<AsposeNodeListElement> nodeList, Stack<ParserState> st, ParserState localState, ParseResult result) {
		ParserState state = st.peek();
		// ResetResult(localState, result);
		int nodeLevel = nodeList.get(localState.idNode).level;
		if (state.ifObj.domLevel != nodeLevel) {
			result.code = 10;
			result.errorMessage = "PARSER: ключевое слово ENDIF находится на другом уровне иерархии документа ";
			return;
		}
		state.ifObj.endifAttr = new KeyWordAttributes(localState.token, localState.idNode, localState.inlinePos, localState.inlinePos + localState.token.length());
		st.pop();
		state = st.peek();
	}

	/**
	 *
	 * Конечный автомат обработки состояния парсера по приходу очередного ключевого слова (IF-THEN-ELSE-ENDIF)
	 *
	 * @param nodeList список Run-полей {@link AsposeNodeListElement типа AsposeNodeListElement}
	 * @param st стек состояний парсера {@link ParserState ParserState}
	 * @param localState текущее состояние парсера {@link ParserState ParserState}
	 * @param result результат операции для возврата кода ошибки и сообщения об ошибке {@link ParseResult типа ParseResult}
	 */
	private static void processToken(ArrayList<AsposeNodeListElement> nodeList, Stack<ParserState> st, ParserState localState, ParseResult result) {
		ParserState state = st.peek();
		resetResult(localState, result);
		// System.out.println(localState.token + ", line=" + localState.idNode + ", level=" + nodeList.get(localState.idNode).level + ", pos=" + localState.inlinePos);
		if (state.token.isEmpty()) { // состояние "вне if"
			if (localState.token.equals(IF_TAG)) {
				createNewIf(nodeList, st, localState, true);
			} else {
				result.code = -1;
				result.errorMessage = "PARSER: ожидается ключевое слово IF, обнаружено ";
			}
		} else if (state.token.equals(IF_TAG)) { // состояние "после if"
			if (localState.token.equals(THEN_TAG)) {
				processThen(nodeList, st, localState, result);
			} else {
				result.code = -1;
				result.errorMessage = "PARSER: ожидается ключевое слово THEN, обнаружено ";
			}

		} else if (state.token.equals(THEN_TAG)) { // состояние "после then"
			if (localState.token.equals(IF_TAG)) {
				createNewIf(nodeList, st, localState, true);
			} else if (localState.token.equals(ELSE_TAG)) {
				processElse(nodeList, st, localState, result);
			} else if (localState.token.equals(ENDIF_TAG)) {
				processEndif(nodeList, st, localState, result);
			} else {
				result.code = -1;
				result.errorMessage = "PARSER: ожидается ключевое слово ELSE или ENDIF, обнаружено ";
			}

		} else if (state.token.equals(ELSE_TAG)) { // состояние "после else"
			if (localState.token.equals(IF_TAG)) {
				createNewIf(nodeList, st, localState, false);
			} else if (localState.token.equals(ENDIF_TAG)) {
				processEndif(nodeList, st, localState, result);
			} else {
				result.code = -1;
				result.errorMessage = "PARSER: ожидается ключевое слово ENDIF, обнаружено ";
			}
		}
	}

	/**
	 *
	 * Перенос поля сообщения об ошибке из {@link ParseResult result} в узел {@link AsposeNodeListElement AsposeNodeListElement} в котором возникла ошибка
	 *
	 * @param nodeList список Run-полей {@link AsposeNodeListElement типа AsposeNodeListElement}
	 * @param result результат операции для возврата кода ошибки и сообщения об ошибке {@link ParseResult типа ParseResult}
	 */
	private static void fillResultMessageOrder(ArrayList<AsposeNodeListElement> nodeList, ParseResult result) {
		if (result.code == 0)
			return;
		try {
			AsposeNodeListElement element = nodeList.get(result.idNode);
			element.insertOrder.add(new ModifyOrder(result.inlinePos, "-->" + result.errorMessage));
		} catch (Exception e) {
		}
	}

	/**
	 *
	 * Главный цикл разбора дерева ключевых слов (IF-THEN-ELSE-ENDIF)
	 *
	 * @param nodeList список Run-полей {@link AsposeNodeListElement типа AsposeNodeListElement}
	 * @param st стек состояний парсера {@link ParserState ParserState}
	 * @param result результат операции для возврата кода ошибки и сообщения об ошибке {@link ParseResult типа ParseResult}
	 */
	private static void parseNodes(ArrayList<AsposeNodeListElement> nodeList, Stack<ParserState> st, ParseResult result) {
		try {
			ParserState localState = new ParserState();
			Boolean stringHasToken = false;
			int q = nodeList.size();
			for (int i = 0; i < q; i++) {
				localState.idNode = i;
				stringHasToken = getNextToken(nodeList, localState);
				while (stringHasToken) {
					processToken(nodeList, st, localState, result);
					if (result.code != 0)
						break;
					localState.inlinePos += localState.token.length();
					stringHasToken = getNextToken(nodeList, localState);
				}
				if (result.code != 0)
					break;
				localState.inlinePos = 0;
			}
			fillResultMessageOrder(nodeList, result);
		} catch (Exception e) {
		}
	}

	/**
	 *
	 * Маркировка блоков для удаления в списке из {@link AsposeNodeListElement AsposeNodeListElement'ов}
	 *
	 * @param nodeList список Run-полей {@link AsposeNodeListElement типа AsposeNodeListElement}
	 * @param startIndex индекс начального узла в списке nodeList
	 * @param startPos индекс начала текста в текстовом поле начального узла
	 * @param endIndex индекс конечного узла в списке nodeList
	 * @param endPos индекс конца текста в текстовом поле конечного узла
	 */

	private static void markBlock(ArrayList<AsposeNodeListElement> nodeList, int startIndex, int startPos, int endIndex, int endPos) {
		// String str = getTextFromNodes(nodeList, startIndex, startPos, endIndex, endPos);
		// System.out.println();
		// System.out.println("str=" + str);
		AsposeNodeListElement node;
		int textLen;
		if (startIndex == endIndex) {
			node = nodeList.get(startIndex);
			node.removeOrder.add(new ModifyOrder(startPos, endPos));
			node.toRemove = (node.text.length() == (endPos - startPos));
			return;
		}

		for (int i = startIndex; i <= endIndex; i++) {
			// currentStr = nodeList.get(i).text;
			node = nodeList.get(i);
			textLen = node.text.length();
			if (i == startIndex) {
				if (startPos != textLen) {
					node.removeOrder.add(new ModifyOrder(startPos, textLen));
					node.toRemove = (startPos == 0);
				}
			} else if (i == endIndex) {
				if (0 != endPos) {
					node.removeOrder.add(new ModifyOrder(0, endPos));
					node.toRemove = (endPos == textLen);
				}
			} else if (0 != textLen) {
				node.removeOrder.add(new ModifyOrder(0, textLen));
				node.toRemove = true;
			}
			// str += currentStr;
		}

	}

	/**
	 *
	 * Поиск параграфа в следующей секции
	 *
	 * @param node стартовый параграф
	 * @param Node результирующий параграф
	 */
	private static Node GetNextNodeOverSection(Node node, ArrayList<Node> nodeToDelete) {
		if (node == null)
			return null;
		Node current = node;

		// переходим "вверх" по дереву до узла типа SECTION
		int i = 0;
		do {
			current = current.getParentNode();
			i++;
		} while (current != null && current.getNodeType() != NodeType.SECTION);
		if (current == null)
			return null;

		// переходим к следующему узлу типа SECTION
		current = current.getNextSibling();
		if (current == null)
			return null;

		// помечаем следующую секцию как продолжение предыдущей
		((Section) current).getPageSetup().setSectionStart(SectionStart.CONTINUOUS);
		// помечаем следующую секцию к удалению
		nodeToDelete.add(current);

		// переходим "вниз" по дереву на столько же шагов, на сколько шли вверх
		NodeCollection<Node> childList = null;
		while (i > 0) {
			try {
				childList = ((CompositeNode) current).getChildNodes();
				if (childList.getCount() == 0)
					return null;
				current = childList.get(0);
			} catch (Exception e) {
				return null;
			}
			i--;
		}
		return current;
	}

	/**
	 *
	 * Маркировка Sections для удаления SectionBreak
	 *
	 * @param startNode индекс начального узла для удаления
	 * @param endNode индекс конечного узла для удаления
	 * @param nodeToDelete результирующий список узлов для удаления
	 */
	private static void markSectionBreakToDelete(Node startNode, Node endNode, ArrayList<Node> nodeToDelete) {
		try {
			if (startNode == null || endNode == null)
				return;
			Node startParent, endParent;
			startParent = startNode.getParentNode();
			endParent = endNode.getParentNode();
			if (startParent == null || endParent == null)
				return;
			if (startParent.getNodeType() != NodeType.PARAGRAPH || endParent.getNodeType() != NodeType.PARAGRAPH)
				return;

			Node n = startParent;
			Node nTemp;
			do {
				try {
					// if (n.getNodeType()==NodeType.PARAGRAPH) {
					// for (Run run : (Iterable<Run>) ((Paragraph)n).getRuns())
					// {
					// if (run.getText().contains(ControlChar.PAGE_BREAK))
					// run.setText(run.getText().replace(ControlChar.PAGE_BREAK, ""));
					// }
					// if (n.getText().contains(ControlChar.PAGE_BREAK))
					// nodeToDelete.add(n);

					// if (((Paragraph)n).getParagraphFormat().getPageBreakBefore())
					// ((Paragraph)n).getParagraphFormat().setPageBreakBefore(false);
					// }
				} catch (Exception ex) {
				}
				nTemp = n.getNextSibling();
				if (nTemp == null)
					n = GetNextNodeOverSection(n, nodeToDelete);
				else
					n = nTemp;
			} while (n != null && n != endParent);
		} catch (Exception ex) {
		}
	}

	/**
	 *
	 * Маркировка узлов для удаления
	 *
	 * @param nodeList список Run-полей {@link AsposeNodeListElement типа AsposeNodeListElement}
	 * @param nodeToDelete результирующий список узлов для удаления
	 * @param startNode индекс начального узла для удаления в списке nodeList
	 * @param endNode индекс конечного узла для удаления в списке nodeList
	 */
	private static void markNodesToDelete(ArrayList<AsposeNodeListElement> nodeList, ArrayList<Node> nodeToDelete, int startNode, int endNode) {
		if (startNode == endNode)
			return;
		Node currentNode1, currentNode2, previousNode1, previousNode2;

		try {
			previousNode1 = nodeList.get(startNode).node;
			previousNode2 = nodeList.get(endNode).node;
			Boolean found = false;
			markSectionBreakToDelete(previousNode1, previousNode2, nodeToDelete);
			do {
				currentNode1 = previousNode1.getParentNode();
				currentNode2 = previousNode2.getParentNode();

				if (currentNode1 != null && currentNode1 == currentNode2) { // дошли по дереву вверх до общего родителя
					found = true;
					break;
				}

				previousNode1 = currentNode1;
				previousNode2 = currentNode2;

			} while (currentNode1 != null && currentNode2 != null);

			if (found) {
				Node n = previousNode1.getNextSibling();
				while (n != null && n != previousNode2) {
					nodeToDelete.add(n);
					n = n.getNextSibling();
				}
			}

		} catch (Exception ex) {
		}
	}

	/**
	 *
	 * Главный цикл маркировки блоков для удаления в списке из {@link AsposeNodeListElement AsposeNodeListElement'ов}
	 *
	 * @param nodeList список Run-полей {@link AsposeNodeListElement типа AsposeNodeListElement}
	 * @param ifobj корневой {@link IfObject узел} дерева IF'ов
	 */
	private static void markUnnecessaryBlocks(ArrayList<AsposeNodeListElement> nodeList, ArrayList<Node> nodeToDelete, IfObject ifobj) {
		if (ifobj == null || ifobj.processed)
			return;
		try {
			ArrayList<IfObject> listTrue;
			KeyWordAttributes startAttr;
			KeyWordAttributes endAttr;

			if (ifobj.expressionResult) {
				listTrue = ifobj.trueBranch;
				startAttr = ifobj.elseAttr;
				endAttr = ifobj.endifAttr;
			} else {
				listTrue = ifobj.falseBranch;
				startAttr = ifobj.thenAttr;
				if (ifobj.elseAttr == null)
					endAttr = ifobj.endifAttr;
				else
					endAttr = ifobj.elseAttr;
			}

			// delete conditions block
			if (ifobj.ifAttr != null && ifobj.thenAttr != null) {
				markBlock(nodeList, ifobj.ifAttr.nodeIndex, ifobj.ifAttr.endPos, ifobj.thenAttr.nodeIndex, ifobj.thenAttr.startPos);
			}

			// delete false block
			if (startAttr != null && endAttr != null) {
				markBlock(nodeList, startAttr.nodeIndex, startAttr.endPos, endAttr.nodeIndex, endAttr.startPos);
				markNodesToDelete(nodeList, nodeToDelete, startAttr.nodeIndex, endAttr.nodeIndex);
			}

			// explore true block
			if (listTrue != null) {
				for (IfObject listElement : listTrue) {
					markUnnecessaryBlocks(nodeList, nodeToDelete, listElement);
				}
			}
			ifobj.processed = true;
		} catch (Exception e) {
		}
	}

	/**
	 *
	 * Определение попадает ли символ в указанной позиции в "окно" удаления
	 *
	 * @param index индекс позиции
	 * @param orders список заказов на удаление из элементов {@link ModifyOrder типа ModifyOrder}
	 * @return true - если символ попадает в "окно" удаления
	 */
	private static Boolean isMarkedToDelete(int index, ArrayList<ModifyOrder> orders) {
		Boolean result = false;
		for (ModifyOrder order : orders) {
			if (index >= order.startPos && index < order.endPos) {
				result = true;
				break;
			}
		}
		return result;
	}

	/**
	 *
	 * Получение строки для вставки
	 *
	 * @param index индекс позиции вставки
	 * @param orders список заказов на вставку из элементов {@link ModifyOrder типа ModifyOrder}
	 */
	private static String getMarkedToInsert(int index, ArrayList<ModifyOrder> orders) {
		String result = "";
		for (ModifyOrder order : orders) {
			if (index == order.startPos) {
				result = (order.text == null) ? "" : order.text;
				break;
			}
		}
		return result;
	}

	/**
	 *
	 * Создание новой Run-строки с учётом всех удалений
	 *
	 * @param element {@link AsposeNodeListElement AsposeNodeListElement} - описатель поля
	 */
	private static String createNewRunForDelete(AsposeNodeListElement element) {
		int q = element.text.length();
		StringBuilder newstr = new StringBuilder("");
		char ch;
		for (int i = 0; i < q; i++) {
			if (!isMarkedToDelete(i, element.removeOrder)) {
				ch = element.text.charAt(i);
				newstr.append(ch);
			}
		}
		return newstr.toString();
	}

	/**
	 *
	 * Создание новой Run-строки с учётом всех вставок
	 *
	 * @param element {@link AsposeNodeListElement AsposeNodeListElement} - описатель поля
	 */
	private static String createNewRunForInsert(AsposeNodeListElement element) {
		int q = element.text.length();
		StringBuilder newstr = new StringBuilder("");
		char ch;
		String ins = "";
		for (int i = 0; i < q; i++) {
			ins = getMarkedToInsert(i, element.insertOrder);
			if (!ins.isEmpty()) {
				int insLen = ins.length();
				for (int j = 0; j < insLen; j++) {
					ch = ins.charAt(j);
					newstr.append(ch);
				}
			}
			ch = element.text.charAt(i);
			newstr.append(ch);
		}
		return newstr.toString();
	}

	/**
	 *
	 * Удаление Run-а из дерева документа Удаляется Run, и если родительский объект Paragraph, и он не содержит больше дочерних узлов, то удаляем Paragraph
	 *
	 * @param element {@link AsposeNodeListElement AsposeNodeListElement} - описатель поля
	 */
	private static void RemoveRun(Node node) throws Exception {
		CompositeNode parent = node.getParentNode();
		if (parent != null && parent.getNodeType() == NodeType.PARAGRAPH) {
			node.remove();

			NodeCollection<Node> childList = null;
			try {
				childList = parent.getChildNodes();
				if (childList.getCount() == 0)
					parent.remove();
			} catch (Exception e) {
				childList = null;
			}
		} else
			node.remove();
	}

	/**
	 *
	 * Главный цикл удаления блоков по спискам полей удаления из списка {@link AsposeNodeListElement AsposeNodeListElement'ов}
	 *
	 * @param nodeList список Run-полей {@link AsposeNodeListElement типа AsposeNodeListElement}
	 */
	private static void deleteBlocks(ArrayList<AsposeNodeListElement> nodeList, ArrayList<Node> nodeToDelete) {
		try {
			for (AsposeNodeListElement element : nodeList) {
				if (element.toRemove)
					// element.node.remove();
					RemoveRun(element.node);
				else if (element.removeOrder.size() == 0)
					continue;
				else {
					try {
						String newRun = createNewRunForDelete(element);
						if (newRun.isEmpty())
							// element.node.remove();
							RemoveRun(element.node);
						else {
							element.node.getRange().replace(Pattern.compile(".*"), newRun);
						}
					} catch (Exception e) {
					}
				}
			}

			for (Node n : nodeToDelete) {
				try {
					// RemoveSectionBreak(n);
					n.remove();
				} catch (Exception e) {
				}

			}
		} catch (Exception ex) {
		}
	}

	/**
	 *
	 * Главный цикл вставки сообщений по спискам полей вставки из списка {@link AsposeNodeListElement AsposeNodeListElement'ов}
	 *
	 * @param doc {@link Document документ}
	 * @param nodeList список Run-полей {@link AsposeNodeListElement типа AsposeNodeListElement}
	 */
	private static void insertBlocks(Document doc, ArrayList<AsposeNodeListElement> nodeList) {
		for (AsposeNodeListElement element : nodeList) {
			if (element.insertOrder.size() == 0)
				continue;
			else {
				try {
					String newRun = createNewRunForInsert(element);
					if (newRun.isEmpty())
						element.node.remove();
					else {
						// DocumentBuilder builder = new DocumentBuilder(doc);
						// builder.moveTo(element.node);
						// Font font = builder.getFont();
						// font.setColor(Color.RED);
						// builder.write(newRun);
						element.node.getRange().replace(Pattern.compile(".*"), newRun);
						((Run) element.node).getFont().setHighlightColor(Color.RED);
					}
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 *
	 * Проверка соседних узлов {@link AsposeNodeListElement AsposeNodeListElement} на наличие ключевых слов, попадающих на границу узлов
	 *
	 * @param node1 первый описатель узла {@link AsposeNodeListElement типа AsposeNodeListElement}
	 * @param node2 второй описатель узла {@link AsposeNodeListElement типа AsposeNodeListElement}
	 * @param keyWords список ключевых слов для проверки
	 * @param edges результирующий список описателей "разорванных" слов
	 *
	 */
	private static void ScanEdge(AsposeNodeListElement node1, AsposeNodeListElement node2, ArrayList<String> keyWords, ArrayList<edgeElement> edges) {
		try {
			int wordLen, startFrom, endWhere, chunk1Len, chunk2Len, index;
			String chunk;
			edgeElement edge;

			for (String word : keyWords) {
				wordLen = word.length();
				if (wordLen <= 1)
					continue;
				chunk1Len = node1.text.length();
				chunk2Len = node2.text.length();
				if (chunk1Len + chunk2Len < wordLen)
					continue;
				if (chunk1Len < wordLen - 1)
					startFrom = 0;
				else
					startFrom = chunk1Len - (wordLen - 1); // слово не умещается в конце строки на 1 символ
				if (chunk2Len < wordLen - 1)
					endWhere = chunk2Len;
				else
					endWhere = wordLen - 1; // слово не умещается в начале строки на 1 символ
				chunk = node1.text.substring(startFrom) + node2.text.substring(0, endWhere);
				index = chunk.indexOf(word);
				if (index == -1)
					continue;
				edge = new edgeElement();
				edge.startPos = startFrom;
				edge.endPos = endWhere;
				edge.node1 = node1.node;
				edge.text1 = node1.text;
				edge.node2 = node2.node;
				edge.text2 = node2.text;
				edge.keyWord = word;
				edges.add(edge);
			}
		} catch (Exception ex) {
		}
	}

	/**
	 * Найти в документе все блоки IF-THEN-ELSE-ENDIF и выполнить их обработку.
	 *
	 * @param doc {@link Document документ}
	 */
	public static void parseIfEx(Document doc) {
		try {
			long starttime = System.currentTimeMillis();
			MergeBrokenKeyWordsEx(doc);
			MergeBrokenKeyWords(doc);
			parseIfMainPass(doc);

			//объенинение строк с символом (R)
			joinParagraphsWithNonBreakingEx(doc);
		} catch (Exception ex) {}
	}

	/**
	 * Найти и склеить ключевые слова, оказавшиеся в разных Run'ах ("разрубленные" на две части)
	 *
	 * @param doc {@link Document документ}
	 */
	private static void MergeBrokenKeyWords(Document doc) {
		try {
			ArrayList<AsposeNodeListElement> nodeList = new ArrayList<AsposeNodeListElement>();
			ArrayList<edgeElement> edges = new ArrayList<edgeElement>();
			@SuppressWarnings("serial")
			ArrayList<String> keyWords = new ArrayList<String>() {
				{
					add(ENDIF_TAG);
					add(THEN_TAG);
					add(ELSE_TAG);
					add(IF_TAG);
					add(NE_TAG);
					add(GE_TAG);
					add(LE_TAG);
				}
			};

			exploreWordDOM(doc, nodeList, 0/* level */, new fieldLevel(0)); // поиск Run-полей в документе Word и запись их в nodeList
			ScanWordIf(nodeList); // маркировка полей с "вордовым" IF

			// сканирование соседних узлов на предмет попадания на границу между узлами ключевых слов
			AsposeNodeListElement prevNode = null;
			for (AsposeNodeListElement element : nodeList) {
				if (element.ifField)
					continue;
				if (prevNode != null) {
					ScanEdge(prevNode, element, keyWords, edges);
				}
				prevNode = element;
			}

			// склеивание ключевых слов по списку edges
			String newText1, newText2;
			for (edgeElement edge : edges) {
				newText1 = edge.text2.substring(0, edge.endPos);
				newText2 = edge.text2.substring(edge.endPos);
				edge.node1.getRange().replace(Pattern.compile(".*"), edge.text1 + newText1);
				if (newText2.isEmpty())
					edge.node2.remove();
				else
					edge.node2.getRange().replace(Pattern.compile(".*"), newText2);
			}
		} catch (Exception ex) {
		}
	}

	/**
	 * Найти и склеить ключевые слова, оказавшиеся в разных Run'ах ("разрубленные" на несколко частей)
	 *
	 * @param doc {@link Document документ}
	 */
	private static void MergeBrokenKeyWordsEx(Document doc) {
		try {
			long starttime = System.currentTimeMillis();

			Range r = doc.getRange();
			r.replace(Pattern.compile(ENDIF_TAG), ENDIF_TAG);
			r.replace(Pattern.compile(THEN_TAG), THEN_TAG);
			r.replace(Pattern.compile(ELSE_TAG), ELSE_TAG);
		} catch (Exception ex) {
		}
	}

	/**
	 * Разбор и обработка IF-THEN-ELSE-ENDIF
	 *
	 * @param doc {@link Document документ}
	 */
	private static void parseIfMainPass(Document doc) {
		ArrayList<AsposeNodeListElement> nodeList = new ArrayList<AsposeNodeListElement>();
		long starttime = System.currentTimeMillis();

		exploreWordDOM(doc, nodeList, 0/* level */, new fieldLevel(0)); // поиск Run-полей в документе Word и запись их в nodeList
		ScanWordIf(nodeList); // маркировка полей с "вордовым" IF

		// System.out.println();
		// for (AsposeNodeListElement element:nodeList) {
		// System.out.println(element.text + ", l=" + element.level + ", fl=" + element.fldLevel + ", fl=" + element.ifField);
		// }

		ParserState state = new ParserState();
		IfObject ifobj = new IfObject();
		state.ifObj = ifobj;
		ParseResult result = new ParseResult();
		Stack<ParserState> st = new Stack<ParserState>();
		st.push(state);
		ArrayList<Node> NodeToDelete = new ArrayList<Node>();

		parseNodes(nodeList, st, result); // разбор IF-конструкций
		if (result.code == 0) {
			markUnnecessaryBlocks(nodeList, NodeToDelete, state.ifObj); // пометка "ложных" ветвей для удаления
			deleteBlocks(nodeList, NodeToDelete); // удаление "ложных" ветвей и ключевых слов
		} else {
			insertBlocks(doc, nodeList); // вставить в текст документа сообщения об ошибках
		}
	}
	/*****************************************************************************************/

	/**
	 * Найти в документе все символы (R) и объединить абзацы, разделенные этим символом.
	 *
	 * @param n - начальный узел {@link Document документ}
	 * @throws Exception
	 */
	public static void joinParagraphsWithNonBreakingEx(Node n) throws Exception {
		try {
			if (n.getNodeType() == NodeType.PARAGRAPH)
				ProcessParagraph((CompositeNode) n);
			NodeCollection<Node> childList = null;
			try {
				childList = ((CompositeNode) n).getChildNodes();
			} catch (Exception e) {
				childList = null;
			}
			if (childList != null) {
				for (Node child : childList)
					joinParagraphsWithNonBreakingEx(child);
			}
		} catch (Exception ex) {}
	}

	/**
	 * Если в текущем параграфе есть символ склейки (R), то cклеить с текущим параграфом все дочернии элементы следующего параграфа этого уровня. Процесс повторяется пока есть символы склейки
	 *
	 * @param par {@link CompositeNode параграф}
	 * @throws Exception
	 */
	private static void ProcessParagraph(CompositeNode par) throws Exception {
		Boolean processed = true;
		while (processed) {
			processed = false;
			NodeCollection<Node> children = par.getChildNodes();
			for (Node child : children) {
				// Абзац может содержать детей различных типов:
				// цепочки символов,картинки и прочее. Ищем символ
				// фирмы только в цепочке.
				if (child.getNodeType() == NodeType.RUN) {
					Run run = (Run) child;
					if (run.getText().indexOf(REGISTERED_MARK) != -1) {
						run.setText(run.getText().replace(REGISTERED_MARK, ""));
						joinParagraphs((Paragraph) par);
						// начать итерацию с этого же абзаца
						processed = true;
						break;
					}
				}
			}
		}
	}

	/**
	 * Объединяет абзацы (и удаляет добавленный абзац из списка братьев). Внимание! следующий узел также должен быть абзацем
	 *
	 * @param par {@link Paragraph абзац} абзац, с которым нужно объединить следующий за ним.
	 */
	private static void joinParagraphs(Paragraph par) throws Exception {
		if ((par == null) || ((CompositeNode) par.getNextSibling() == null))
			return;

		// Получим следующий узел, идущий за абзацем.
		CompositeNode nextNode = (CompositeNode) par.getNextSibling();
		while (nextNode.hasChildNodes()) {
			par.appendChild(nextNode.getFirstChild());
		}
		nextNode.remove();
	}

	private static void removeMarkedParagraph(CompositeNode body) throws Exception {
		NodeCollection<Node> paragraphList = body.getChildNodes(NodeType.PARAGRAPH, true);
		if (paragraphList == null || paragraphList.getCount() == 0)
			return;
		Node previousParagraph = null;
		for (Node paragraph:paragraphList) {
			if (paragraph.getText().indexOf(TRADEMARK) != -1) {
				previousParagraph = paragraph.getPreviousSibling();
				paragraph.remove();
				break;
			}
		}
		
		//удаление всех предыдущих параграфов этого уровня до таблицы (не параграфа)
		Node nodeToDelete;
		while (previousParagraph != null && previousParagraph.getNodeType() == NodeType.PARAGRAPH) {
			nodeToDelete = previousParagraph; 
			previousParagraph = previousParagraph.getPreviousSibling();
			nodeToDelete.remove();
		}
	}
	
	public static void joinMarkedSections(Node n) throws Exception {
		if (n == null)
			return;
		try {
			Section section = ((Document)n).getFirstSection();
			if (section == null)
				return;
			
			while (section != null) {
				if (section.getText().indexOf(TRADEMARK) != -1) {
					Section nextSection = (Section)(((Node)section).getNextSibling());
					if (nextSection != null) {
						NodeCollection<Node> bodyList = section.getChildNodes(NodeType.BODY, true);
						if (bodyList != null && bodyList.getCount() != 0) {
							CompositeNode body = (CompositeNode)bodyList.get(0);
							removeMarkedParagraph(body);
							NodeCollection<Node> paragraphList = body.getChildNodes(NodeType.PARAGRAPH, true);
							if (paragraphList != null && paragraphList.getCount() != 0) {
								int q = paragraphList.getCount();
								Paragraph paragraph = (Paragraph)paragraphList.get(q-1);
								NodeCollection<Node> tableList = body.getChildNodes(NodeType.TABLE, true);
								if (tableList != null && tableList.getCount() != 0) {
									Table table = (Table)tableList.get(0);
									Node tgtNode = table.getNextSibling();
									if (tgtNode != null && tgtNode.getNodeType() == NodeType.PARAGRAPH) {
										paragraph.remove();
										section.appendContent(nextSection);
										nextSection.remove();
										tgtNode.remove();
									}
								}
							}
						}
					}
				}
				section = (Section)section.getNextSibling();
			}
		} catch (Exception ex) {}
	}
}
