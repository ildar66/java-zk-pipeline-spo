var ERR_USER_DEFINED=1000

function debug(msg,src){
	alert(src+'\n'+msg)
}
function getParentNode(node,tag){
	if(! (node && tag)) return
	tag=tag.toUpperCase()
	while (node.nodeName!=tag){
		node=node.parentElement
		if(!node) return
	}
	return node
}

function getNode(parent,ID,tag){
	var script='getNode'
	try 
	{
		script+='0'
		parent= parent || document
		if(!ID) return
		var node,nodes
		try{
			node=parent.getElementById(ID)
		} catch (e){
			if(tag){
				nodes=parent.getElementsByTagName(tag)
				if(!nodes) return
				if(!nodes.length){
					node=nodes
				}else{
					for (var i=0; i<nodes.length; i++){
						with (nodes[i]){
							tag= tag || nodeName
							if(nodes[i].id==ID || name==ID) return nodes[i]
						}
					}
				}
			}else{
				//alert('parent.all='+parent.all)
				nodes=parent.all(ID)
				if(!nodes) return
				if(!nodes.length){
					node=nodes
				}else{
					for (var i=0; i<nodes.length; i++){
						with (nodes[i]){
							tag= tag || nodeName
							if(nodeName==tag.toUpperCase()) return nodes[i]
						}
					}
				}
			}
		}
		script+='1'
		if(!node) return
		tag= tag || node.nodeName
			script+='2'
		if(node.nodeName==tag.toUpperCase() && (node.id==ID || node.name==ID)) return node
			script+='3'
	}
	catch (Err)
	{
		debug(Err.description, script + " :: " + ID)
		return false
	}
}
function getValue(node, index){
	try{
//		index = index || -1
		if(!node) return ""
		if (typeof(node) != 'object'){
			node=getNode(null, node)
		}
		if(!node) return ""

		var index= (arguments.length==1) ? -1 : arguments[1]

		var nodes = document.all(node.id || node.name)

		switch(node.nodeName){
		case 'SELECT':
			if (node.selectedIndex == -1) return ""
			return node.options[node.selectedIndex].value //|| node.options[node.selectedIndex].text
	//		return node.options[node.selectedIndex].value
		case 'INPUT':
			switch(node.type){
				case 'file':
				case 'hidden':
				case 'password':
				case 'text':
					return node.value
				case 'checkbox':
					var arr = new Array()
					if (nodes.length)
					{
						for (var i = 0; i<nodes.length; i++)
						{
							if(index >= 0)
							{
								if (index == i) return (nodes[i].checked) ? nodes[i].value : ""
							}
							else if(nodes[i].checked) arr[arr.length] = nodes[i].value
						}
						return arr.join(";")
					}
					else
					{
						return (node.checked) ? node.value : ""
					}
				case 'radio':
					if (nodes.length)
					{
						for (var i = 0; i<nodes.length; i++)
						{
							if (nodes[i].checked)
							{	
								return nodes[i].value
							}
						}
					}
					else
					{
						if (node.checked) return node.value
					}
				case 'button':
				case 'image':
				case 'reset':
				case 'submit':
					return
				default:
					return
			}
		case 'TEXTAREA':
			return node.value
		default:
			return (node.innerText || node.innerHTML) || ""
		}
	} catch (Err){
		debug(Err.description,'getValue')
		return ""
	}

}
function setValue(node, value){
	try{
		var key, id
		if(!node) return
		value= value || ''
		if (typeof(node) != 'object'){
			id=node
			node = getNode(null, node)
		} else {
			id= node.id || node.name
		}
		if(!node) return
		var nodes
		switch(node.nodeName){
		case 'SELECT':
			for (var i = 0; i<node.options.length; i++){
				key = node.options[i].value //|| node.options[i].text
				if (key == value) node.options[i].selected = true
			}
			break
		case 'INPUT':
			switch(node.type){
				case 'file':
				case 'hidden':
				case 'password':
				case 'text':
					node.value = value
					break
				case 'checkbox':
				case 'radio':
					nodes = document.all(id)
					for (var i = 0; i<nodes.length; i++){
						key = nodes[i].value
						if (key == value) nodes[i].checked = true
					}
					break
				case 'button':
				case 'image':
				case 'reset':
				case 'submit':
					return
				default:
					return
			}
		case 'TEXTAREA':
			node.value = value
			break
		default:
			return
		}
	} catch (Err){
		debug(Err.description,'setValue')
		return
	}
}