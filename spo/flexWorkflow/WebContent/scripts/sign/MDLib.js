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
    if(!node) return
    tag= tag || node.nodeName
    if(node.nodeName==tag.toUpperCase() && (node.id==ID || node.name==ID)) return node
}
function getValue(node){
	try{
		if(!node) return ""
		if (typeof(node) != 'object'){
			node=getNode(null,node)
		}
		if(!node) return ""
		switch(node.nodeName){
		case 'SELECT':
			if (node.selectedIndex == -1) return ""
			return node.options[node.selectedIndex].value || node.options[node.selectedIndex].text
	//        return node.options[node.selectedIndex].value
		case 'INPUT':
			switch(node.type){
				case 'file':
				case 'hidden':
				case 'password':
				case 'text':
					return node.value
				case 'checkbox':
				case 'radio':
					return ((node.checked) ? node.value : "")
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
		for (var i = 0; i<node.options.length; i++)
		{
			key = node.options[i].value || node.options[i].text
			if (key == value) node.options[i].selected = true
		}
    case 'INPUT':
        switch(node.type){
            case 'file':
            case 'hidden':
            case 'password':
            case 'text':
                node.value = value
            case 'checkbox':
            case 'radio':
                nodes = document.all(id)
				for (var i = 0; i<nodes.length; i++)
				{
					key = nodes[i].value
					if (key == value) nodes[i].checked = true
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
        node.value = value
    default:
        return
    }
}