package ru.md.jsp.tag;
import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import org.uit.director.db.dbobjects.AttributeStruct;
import org.uit.director.tasks.AttributesStructList;
/**
 * This is a simple tag example to show how content is added to the
 * output stream when a tag is encountered in a JSP page. 
 */
public class CheckboxTag extends PUPTag {
	/**
	* doStartTag is called by the JSP container when the tag is encountered
	*/
    public int doStartTag() {
    	StringBuffer htmlOut = new StringBuffer("");
    	String value = "";    	 
    	
    	ServletRequest request = pageContext.getRequest();    	
    	    	
    	try {
    		JspWriter out = pageContext.getOut();    
    		AttributesStructList attrs = (AttributesStructList)request.getAttribute(IConst_PUP.ATTRIBUTES);
    		AttributeStruct attr = null;
        	if (attrs != null) {
        		attr = (AttributeStruct) attrs.findAttributeByName(this.getName()); 
    			if (attr != null) {
    				List list = attr.getAttribute().getValueAttributeList();
    				if (this.getIndex()>=0 && this.getIndex()<list.size()) {
    					value = list.get(this.getIndex()).toString();   					
    				}    				
    				if (attr.getAttribute().isPermissionEdit()) {
    					this.setReadonly(false);
    				}
    			}
        	}     	
        	//htmlOut.append("<div>\n");		

        	//if (attr != null && attr.getAttribute().isPermissionEdit()) {
		        htmlOut.append("<input type=\"checkbox\" onclick=\" if(this.checked) this.parentNode.childNodes[2].value = 'TRUE'; else this.parentNode.childNodes[2].value = 'FALSE'; ");
		        if (this.getOnClick() != null && !this.getOnClick().equals(""))
			    	htmlOut.append(this.getOnClick());
		        htmlOut.append("\"");
		        if (this.getDojoType() != null && !this.getDojoType().equals(""))
			    	htmlOut.append("dojoType=\""+this.getDojoType()+"\" ");
		        if (this.getStyle() != null && !this.getStyle().equals(""))
			    	htmlOut.append("style=\""+this.getStyle()+"\" ");
		        if (this.getTitle() != null && !this.getTitle().equals(""))
			    	htmlOut.append("title=\""+this.getTitle()+"\" ");
		        if (this.getId() != null && !this.getId().equals(""))
			    	htmlOut.append("id=\""+this.getId()+"\" ");
		        if (this.getStyleClass() != null && !this.getStyleClass().equals(""))
					htmlOut.append(" class=\""+this.getStyleClass()+"\" ");
		        
		        if (this.getOnBlur() != null && !this.getOnBlur().equals(""))
			    	htmlOut.append("onBlur=\""+this.getOnBlur()+"\" ");
		        if (this.getOnFocus() != null && !this.getOnFocus().equals(""))
			    	htmlOut.append("onFocus=\""+this.getOnFocus()+"\" ");
		        if (this.getOnChange() != null && !this.getOnChange().equals(""))
			    	htmlOut.append("onChange=\""+this.getOnChange()+"\" ");
		        
		        if (value != null && value.equalsIgnoreCase("true"))
			    	htmlOut.append("checked ");
		        else	
			    	value = "FALSE";
		        
			    if (this.isReadonly() == true)
			    	htmlOut.append(" disabled=\"disabled\" ");
			    htmlOut.append("/>\n");
			    htmlOut.append("<input type=\"hidden\" name=\""+(this.isReadonly()?"":this.getName())+"\" value=\""+value+"\"/>\n");
    	    //}else{    	    }

		    //htmlOut.append("</div>");
		    
	        out.print(htmlOut.toString());
		 } catch (Exception ex) {
			 ex.printStackTrace();
			 throw new Error("CheckboxTag doStartTag error " + ex.getMessage());
		 }
		 // Must return SKIP_BODY because we are not supporting a body for this 
		 // tag.
		 return SKIP_BODY;
    }
    
	/**
	 * doEndTag is called by the JSP container when the tag is closed
	 */
	public int doEndTag(){
	   try {
		   JspWriter out = pageContext.getOut();
	       out.print("");
	       return 0;
	   } catch (Exception ex){
		   ex.printStackTrace();
		   throw new Error("CheckboxTag doEndTag error " + ex.getMessage());
	   }
	}
}