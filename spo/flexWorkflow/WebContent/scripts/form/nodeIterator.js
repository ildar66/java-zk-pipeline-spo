/**
 * Iterate through HTMLNode in DOMTree, and apply task() function
 */
function HTMLNodeIterator()
{
	//task:function, node:HTML Node, extraParam: extta param passed to task function
	this.iterate = function iterate(task, node, extraParam)
	{
		task(node, extraParam);
		if (node.childNodes.length > 0) 
			for(var x = 0; x < node.childNodes.length; x++)
					this.iterate(task, node.childNodes[x], extraParam);
	}
}

// show or hide all elements in the table with the given className
function showHideElementsInTable(tableId, className) {
	try
	{
   		var tbl = document.getElementById(tableId);
	    var body = tbl.getElementsByTagName("TBODY")[0];
	    // show or hide all elements with the given className
	  	var htmlNodeIterator = new HTMLNodeIterator();
		htmlNodeIterator.iterate(showOrHideElement, body, className);
	}
	catch (Err)
	{
		alert(Err.description)
		return false
	}
} 

//makes element visible by Changing class: removes 'nonverified', adds 'verifying'
function showOrHideElement(node, className) {
	try {
		if (hasClass(node, className)){
			try {
				// show field
				styleDisplay = node.style.display;
				if (styleDisplay == 'none')  {
			    	node.style.display = '';
			    }
			    // hide field
				if (styleDisplay == '')  {
					node.style.display = 'none';	
				}
			} catch (Err ) {}
		}
	} catch (Err ) {}
}

function AddRowToTable(tableId, clearFunc) {
	fieldChanged()
	var script = "DeleteFile"
	try
	{
   		var tbl = document.getElementById(tableId);
	    var body = tbl.getElementsByTagName("TBODY")[0];
		var Rows = body.rows
		var child = Rows[0]
		var myTR = child.cloneNode(true);
		myTR.style.display = "";
	  	body.appendChild(myTR);
	  	// reread after adding
	  	myTR = body.childNodes[body.childNodes.length - 1];
	  	// make all elements of a new added elements visible 
	  	//(for validation purposes. No check performs for invisible elements like first rows, that are copied)
	  	var htmlNodeIterator = new HTMLNodeIterator();
		htmlNodeIterator.iterate(makeVisible, myTR, null);
	}
	catch (Err)
	{
		alert(Err.description)
		return false
	}
}

// makes element visible by Changing class: removes 'nonverified', adds 'verifying'
// extraparam -- not used
function makeVisible(node, extraparam) {
	try {
		if (hasClass(node, 'nonverified')){
			removeClass(node,'nonverified');
			addClass(node, 'verifying');
		}
	} catch (Err ) {}
}

function hasClass(ele,cls) {
	return ele.className.match(new RegExp('(\\s|^)'+cls+'(\\s|$)'));
}
function addClass(ele,cls) {
	if (!this.hasClass(ele,cls)) ele.className += " "+cls;
}
function removeClass(ele,cls) {
	if (hasClass(ele,cls)) {
		var reg = new RegExp('(\\s|^)'+cls+'(\\s|$)');
		ele.className=ele.className.replace(reg,' ');
	}
}
