package com.vtb.util.report.utils;

import java.util.ArrayList;

import com.aspose.words.Node;

public class AsposeNodeListElement {
	int level;
	Node node;
	String text;
	int fldLevel;
	Boolean ifField;
	Boolean toRemove;
	ArrayList<ModifyOrder> removeOrder;
	ArrayList<ModifyOrder> insertOrder;
	
	AsposeNodeListElement() {
	}
	
	AsposeNodeListElement(int l, Node n, String t) {
		level = l;
		node = n;
		text = t;
		toRemove = false;
		removeOrder = new ArrayList<ModifyOrder>();
		insertOrder = new ArrayList<ModifyOrder>();
		fldLevel = 0;
		ifField = false;
	}
	AsposeNodeListElement(int l, Node n, String t, int fl) {
		level = l;
		node = n;
		text = t;
		toRemove = false;
		removeOrder = new ArrayList<ModifyOrder>();
		insertOrder = new ArrayList<ModifyOrder>();
		fldLevel = fl;
		ifField = false;
	}
}


class ModifyOrder {
	int startPos;
	int endPos;
	String text;
	public ModifyOrder(int start, int end) {
		startPos = start;
		endPos = end;
		text = null;
	}
	public ModifyOrder(int start, String txt) {
		startPos = start;
		endPos = start;
		text = txt;
	}
}

class KeyWordAttributes {
	String name;
	int nodeIndex;
	int startPos;
	int endPos;
	
	public KeyWordAttributes(String n, int index, int start, int end) {
		name = n;
		nodeIndex = index;
		startPos = start;
		endPos = end;
	}
}

class IfObject {
	KeyWordAttributes ifAttr;
	KeyWordAttributes thenAttr;
	KeyWordAttributes elseAttr;
	KeyWordAttributes endifAttr;
	int domLevel;
	int ifLevel;
	String leftConditionOperand;
	String rightConditionOperand;
	String condition;
	Boolean expressionResult;
	Boolean processed;
	ArrayList<IfObject> trueBranch;
	ArrayList<IfObject> falseBranch;
	
	public IfObject() {
		ifAttr = null;
		thenAttr = null;
		elseAttr = null;
		endifAttr = null;
		domLevel = 0;
		ifLevel = 0;
		leftConditionOperand = "";
		rightConditionOperand = "";
		condition = "";
		expressionResult = true;
		processed = false;
		trueBranch = new ArrayList<IfObject>();
		falseBranch = new ArrayList<IfObject>();
	}
}

class ParserState {
	String token;
	//int domLevel;
	int ifLevel;
	int idNode;
	int inlinePos;
	IfObject ifObj;
	
	public ParserState() {
		token = "";
		ifLevel = 0;
		idNode = 0;
		inlinePos = 0;
		ifObj = null;
	}
}

class ParseResult {
	int code;
	String errorMessage;
	int idNode;
	int inlinePos;
	
	public ParseResult() {
		code = 0;
		errorMessage = "";
		idNode = 0;
		inlinePos = 0;
	}
}

class fieldLevel {
	int l;
	fieldLevel(int lvl) {
		l = lvl;
	}
}

class edgeElement {
	Node node1;
	String text1;
	int startPos;
	Node node2;
	String text2;
	int endPos;
	String keyWord;
	//int text1NewLen;
	//int text2NewLen;
}

