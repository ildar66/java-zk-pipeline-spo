function dropFile(btn, namebtn){
	if(document.getElementById) {
		tr = btn;
		while (tr.tagName != 'TR') tr = tr.parentNode;
		tr.parentNode.removeChild(tr);
		checkForLast(namebtn);
	}
}
function addFile(btn, namebtn){
	if(document.getElementById) {
		tr = btn;
		while (tr.tagName != 'TR') tr = tr.parentNode;
		var idSuffix = Math.round(Math.random()*1000);
		var newTr = tr.parentNode.insertBefore(tr.cloneNode(true),tr.nextSibling);
		thisChilds = newTr.getElementsByTagName('td');
		for (var i = 0; i < thisChilds.length; i++){
			if (thisChilds[i].className == 'header') thisChilds[i].innerHTML = '';
			if (thisChilds[i].className == 'files') thisChilds[i].innerHTML = '<input size="32" name="att" class="wideFile" type="file">';
		}
		checkForLast(namebtn);
	}
}
function checkForLast(namebtn){
    btns = document.getElementsByName(namebtn);
	for (i = 0; i < btns.length; i++){
		btns[i].disabled = (btns.length == 1) ? true : false;
	}
}