var img_dir = "/ProdflexWorkflow/resources/"; // папка с картинками
// вспомогательная ф-ция, выдирающая из дочерних узлов весь текст
function getConcatenedTextContent(node) {
	var _result = "";
	if (node == null) {
		return _result;
	}
	var childrens = node.childNodes;
	var i = 0;
	while (i < childrens.length) {
		var child = childrens.item(i);
		switch (child.nodeType) {
			case 1: // ELEMENT_NODE
			case 5: // ENTITY_REFERENCE_NODE
				_result += getConcatenedTextContent(child);
				break;
			case 3: // TEXT_NODE
			case 2: // ATTRIBUTE_NODE
			case 4: // CDATA_SECTION_NODE
				_result += convertNumber(child.nodeValue);
				break;
			case 6: // ENTITY_NODE
			case 7: // PROCESSING_INSTRUCTION_NODE
			case 8: // COMMENT_NODE
			case 9: // DOCUMENT_NODE
			case 10: // DOCUMENT_TYPE_NODE
			case 11: // DOCUMENT_FRAGMENT_NODE
			case 12: // NOTATION_NODE
			// skip
			break;
		}
		i++;
	}
	return _result;
}function convertNumber(number) {
		var len = number.length;
	var numbRes = number;
	var f = Number(number);
		if (f) {
			if (len < 10) {
			for (var i=0; i < (10-len); i++){
				numbRes = '0' + numbRes;
			}
			}
	} else {
		if (len==10) {
			if (number.substring(2,3) == '.' && number.substring(5,6) == '.')  {
				dd=number.substring(0,2);
				mm=number.substring(3,5);
				yyyy=number.substring(6,10);
				numbRes=yyyy+mm+dd;
			}
		}
	}
		return numbRes;
}// флаг сортировки
var up = false;
// суть скрипта
function sort(e) {
		var el = window.event ? window.event.srcElement : e.currentTarget;
	if (el.tagName == "IMG") el = el.parentNode;
	var a = new Array();
	var name = el.lastChild.nodeValue;
	var dad = el.parentNode;
	var node, arrow, curcol;
	for (var i = 0; (node = dad.getElementsByTagName("th").item(i)); i++) {
		if (node.lastChild.nodeValue == name){
			curcol = i;
			if (node.className == "curcol"){
				arrow = node.firstChild;
				up = Number(!up);
				arrow.src = img_dir + up + ".gif";
			}else{
				node.className = "curcol";
				arrow = node.insertBefore(document.createElement("img"),node.firstChild);
				up = false;
				arrow.src = img_dir + Number(up) + ".gif";
			}
		}else{
			if (node.className == "curcol"){
				node.className = "";
				if (node.firstChild) node.removeChild(node.firstChild);
			}
		}
	}
	var tbody = dad.parentNode.parentNode.getElementsByTagName("tbody").item(0);
	for (var i = 0; (node = tbody.getElementsByTagName("tr").item(i)); i++) {
		a[i] = new Array();
		a[i][0] = getConcatenedTextContent(node.getElementsByTagName("td").item(curcol));
		a[i][1] = getConcatenedTextContent(node.getElementsByTagName("td").item(1));
		a[i][2] = getConcatenedTextContent(node.getElementsByTagName("td").item(0));
		a[i][3] = node;
	}
	a.sort();
	if (up) a.reverse();
	for (var i = 0; i < a.length; i++) {
		tbody.appendChild(a[i][3]);
	}
}// ф-ция инициализации всего процесса
function init(e) {
	if (!document.getElementsByTagName("thead").item(0)) return;
	var thead = document.getElementsByTagName("thead").item(0);
	var node;
	for (var i = 0; (node = thead.getElementsByTagName("th").item(i)); i++)
	{
		if (node.addEventListener) node.addEventListener("click", sort, false);
		else if (node.attachEvent) node.attachEvent("onclick",sort);
		node.title = "Нажмите на заголовок, чтобы отсортировать колонку"; // задаём подсказку у заголовка таблицы
	}
	thead.getElementsByTagName("th").item(0).click(); // эмулируем клик на первом заголовке. в NN6/Mozilla не работает.
}// запускаем ф-цию init() при возникновении события load
var root = window.addEventListener || window.attachEvent ? window : document.addEventListener ? document : null;
if (root){
	if (root.addEventListener) root.addEventListener("load", init, false);
	else if (root.attachEvent) root.attachEvent("onload", init);
}