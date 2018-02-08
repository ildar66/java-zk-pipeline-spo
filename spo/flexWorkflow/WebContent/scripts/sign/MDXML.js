var MDXML=function(){
    this.xml=''

    this.createNode=function( node, data, complete){
        var tmp= (complete) ? '/' : ''
        var createNode='<'+node.toUpperCase()+this.Attributes(data)+((complete) ? '/' : '')+'>'
        this.xml+=createNode
        return createNode
    }
    this.Attributes=function(data){
        var Attributes=''
        if(!data)return ''
        for(attribute in data){
            var value=data[attribute].toString()
            if(value){
                Attributes=Attributes+' '+attribute.toUpperCase()+'="'+value.toUpperCase()+'"'
            }
        }
        return Attributes
    }
    this.closeNode=function(node){
        var closeNode='</'+node.toUpperCase()+'>'
        this.xml+=closeNode
        return closeNode
    }
    this.nodeValue=function(value){
        value=value.toString()
        var nodeValue=value
        if((value.indexOf('&')+value.indexOf('<')+value.indexOf('>'))>-3){
            if(value.indexOf('>')==value.length-1){
                nodeValue=nodeValue+' '
            }
            if(!value.indexOf('<')){
                nodeValue=' '+nodeValue
            }
            
            nodeValue='<![CDATA['+nodeValue+']]>'
            
        }
        this.xml+=nodeValue
        return nodeValue
    }
    this.nodeItem=function(node){
        nodeItem=''
        var value = '', nodes, key, id, data=new Array
        if(!node) return nodeItem
        //data['name']= node.name || node.id
		data['name']= node.id || node.name
        switch(node.nodeName){
        case 'SELECT':
            value=node.options[node.selectedIndex].value || node.options[node.selectedIndex].text
//            value=node.options[node.selectedIndex].value
            break
        case 'INPUT':
            switch(node.type){
                case 'file':
                case 'hidden':
                case 'password':
                case 'text':
                    value=node.value
                    break
                case 'checkbox':
                case 'radio':
					id = node.id || node.name
					nodes = document.all(id)
					for (var i = 0; i<nodes.length; i++)
					{
						key = nodes[i].value
						if (nodes[i].checked) value = key
					}
                    break
                case 'button':
                case 'image':
                case 'reset':
                case 'submit':
                    return nodeItem
                default:
                    return nodeItem
            }
            break
        case 'TEXTAREA':
            value=node.value
            break
        default:
            return nodeItem
        }
        
        this.createNode('item',data,false)
        this.createNode('value',null,false)
        this.nodeValue(value)
        this.closeNode('value')
        this.closeNode('item')

        this.xml+=nodeItem
        return nodeItem
    }
}
function getXMLItemValue(doc, item){
	try{
		var value=''
		item=item.toUpperCase()
		if (! item) return ''
		try {
			var nodes=doc.selectNodes("ITEM[@NAME='"+item+"']/VALUE")
			for (var i=0; i<nodes.length; i++){
				value+=nodes[i].text
			}
			if (! value){
				throw(new Error())
			}
			if (nodes.length>1) {
				return nodes
			}
			return value
			
		} catch (Err) {
			return ''
		}
	} catch (Err) {
		alert('getXMLItemValue\n'+Err.description || 'Ошибка!')
		//debug(Err.description,'Ошибка!')

	}
}