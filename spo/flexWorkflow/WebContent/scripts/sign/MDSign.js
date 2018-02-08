var xmlStructure=new ActiveXObject('Msxml2.DOMDocument.4.0')
var xmlContent=new ActiveXObject('Msxml2.DOMDocument.4.0')
var structureXMLPath = 'scripts/sign/structure.xml'
//var CAName = "CA win";  

var SIGNATURE_CERT_INFO_SUBJECT_SIMPLE_NAME = 0;
var SIGNATURE_CERT_INFO_SUBJECT_FULL_NAME = 1;
var SIGNATURE_CERT_INFO_ISSUER_SIMPLE_NAME = 2;
var SIGNATURE_CERT_INFO_ISSUER_FULL_NAME = 3;
var SIGNATURE_CERT_INFO_PUBLICKEY = 4;
var SIGNATURE_CERT_INFO_ALGORITHM = 5;
var SIGNATURE_CERT_INFO_SERIAL = 6;
var SIGNATURE_CERT_INFO_DATE_FROM = 7;
var SIGNATURE_CERT_INFO_DATE_TO = 8;
var SIGNATURE_CERT_INFO_SUBJECT_O = 9;

function signContent(content) {
	try {
		mdSignDataObject.initSign(validateSign, false, cryptoIssuers);
	} catch (err) {
		return "";
	}
	try {
		var sign = mdSignDataObject.certSign(content);
		if (validateSign && sign == "") {
			alert("Подпись не проставлена");
		}
		return sign;
	} catch (err) {
		alert("Ошибка при установке электронной подписи.\nЭлектронная подпись не будет поставлена.\n\n'" + ((err.description == undefined) ? err : err.description) + "', stack '" + err.stack + "'");
		return "";
	}
}

function verifyContent(content, signature) {
	try {
		if (signature == '') {
			alert('Подпись отсутствует');
			return false;
		}
		return mdSignDataObject.certVerifySign(signature, content);
	} catch(err) {
		alert("Ошибка при проверке электронной подписи.\n\n'" + ((err.description == undefined) ? err : err.description) + "'\n, stack '" + err.stack + "'");
		return false;
	}
}


function initStructure(){
	try{
		with (xmlStructure){
			async=false
			resolveExternals=false
			validateOnParse=false
			load(structureXMLPath)
			return (xml!='')
		}
	}
	catch (e){
		return false
	}
	
}

function buildContent(parentNode){
	if (! (initStructure() && xmlContent)) return null
	var mdxml=new MDXML()
	var data=new Array
	mdxml.createNode('document',null,false)
	var nodes=xmlStructure.selectNodes("//ITEM[PROPERTIES[@ISSIGNED='true']]")
	for (var i=0; i<nodes.length; i++){
		var item=nodes[i].selectSingleNode('NAME')
		if(item){
			data['name']=item.text
			mdxml.nodeItem( getNode(parentNode, data['name']))
		}
	}
	mdxml.closeNode('document')

	////Контент
	//alert("content =   " + mdxml.xml)
	///

	return mdxml.xml
	//xmlContent.loadXML('<?xml version="1.0" encoding="Windows-1251"?>'+mdxml.xml)
	//return xmlContent.xml
}
function signSelected() {
	var collection = document.all('signdata');
	var fieldChk, fieldSign;
	var signature, content;

	var colLen = 1;
	var currObj;

	if (collection.nodeName + ""!= "undefined") {
		currObj = collection;
	} else {
		colLen = collection.length
		currObj = collection[0];
	}

	for (var i = 0; i < colLen; i++){
		fieldChk = getNode(currObj,'check');
		if (getValue(fieldChk)) {
			fieldSign = getNode(currObj,'sign', 'input');
			if (fieldSign) {
				setValue( fieldSign, signContent(buildContent(currObj)) );
			}
		}
		try {
			currObj = collection[i + 1];
		} catch (e) {}
	}			
}



function verify() {
	var collection = document.all('signdata');
	var fieldChk, fieldSign;
	var signature, content;

	var colLen = 1;
	var currObj;

	if (collection.nodeName + ""!= "undefined") {
		currObj = collection;
	} else {
		colLen = collection.length
		currObj = collection[0];
	}

	for (var i = 0; i < colLen; i++){
		fieldChk = getNode(currObj,'check');
		if (getValue(fieldChk)) {
			fieldSign = getNode(currObj,'sign', 'input');
			if (fieldSign) {
				//verifyContent(buildContent(collection[i])), getValue(fieldSign));
				content = buildContent(currObj);
				//alert(content);
				signature = getValue(fieldSign);
				//alert(signature);
				verifyContent(content, signature);
			}
		}
		try {
			currObj = collection[i + 1];
		} catch (e) {}
	}
}


function submitSignedForm() {
	var collection = document.all('signdata');
	var fieldChk;
	var nodes;
	var count = 0;

var colLen = 1;
	var currObj;

	if (collection.nodeName + ""!= "undefined") {
		currObj = collection;
	} else {
		colLen = collection.length
		currObj = collection[0];
	}

	for (var i = 0; i < colLen; i++){
		fieldChk = getNode(currObj,'check');
		if (!getValue(fieldChk)) {
			nodes = currObj.all;
			for (var j = 0; j < nodes.length; j++) {
				//nodes[j].name = "";
				nodes[j].removeNode(true);
			}
		} else {
			nodes = currObj.all;
			for (var k = 0; k < nodes.length; k++) {
				nodes[k].name = nodes[k].id + (count + "");
			}
			count++;
		}

		try {
			currObj = collection[i + 1];
		} catch (e) {}
		
	}
}
