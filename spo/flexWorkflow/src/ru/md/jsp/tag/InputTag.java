package ru.md.jsp.tag;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspWriter;

import org.uit.director.db.dbobjects.AttributeStruct;
import org.uit.director.db.dbobjects.VariablesType;
import org.uit.director.tasks.AttributesStructList;

import ru.md.jsp.tag.helpers.ValidatedObject;
/**
 * This is a simple tag example to show how content is added to the
 * output stream when a tag is encountered in a JSP page. 
 */
public class InputTag extends PUPTag {
	private boolean readAsText = false;
	private boolean validate = false;
	private String type="";
	private String pattern="";
	private boolean required = false;
	private String addition="";

	/**
	* doStartTag is called by the JSP container when the tag is encountered
	*/
    @SuppressWarnings("unchecked")
    public int doStartTag() {
    	StringBuffer htmlOut = new StringBuffer("");    	
    	String value = "";
		try {
			JspWriter out = pageContext.getOut();
	        ServletRequest request = pageContext.getRequest();
	        AttributesStructList attrs = (AttributesStructList)request.getAttribute(IConst_PUP.ATTRIBUTES);
	        AttributeStruct attr = null;

			if (attrs!=null) {
				attr = (AttributeStruct) attrs.findAttributeByName(this.getName());
				if (attr != null) {
					List dataList = attr.getAttribute().getValueAttributeList();
					if (this.getIndex() >= 0 && this.getIndex()<dataList.size()) {						
						value = dataList.get(this.getIndex()).toString();
					}
				}
			}

			//It is for dojo component
			if ((value == null || value.equals("")) && this.getDojoType().equalsIgnoreCase("dijit.form.NumberTextBox")) {
				value="0";	
			}
			
			//default value
			this.setStyleClass("text");

			if(attr != null && !attr.getAttribute().isPermissionEdit()){
				//только на чтение pup:input class="text" -> <span class="inputed text">...</span>
				if (this.getStyleClass()==null)this.setStyleClass("inputed");
				else this.setStyleClass(this.getStyleClass()+" inputed");
				htmlOut.append("<span class=\""+this.getStyleClass()+"\"");
				if (this.getStyle() != null && !this.getStyle().equals(""))
					htmlOut.append(" style=\""+this.getStyle()+"\"");
				htmlOut.append(" name=\"" + attr.getAttribute().getName() + "\"");
				htmlOut.append(" id=\"" + attr.getAttribute().getName() + "\"");
				htmlOut.append(">");
				htmlOut.append(value);
				htmlOut.append("</span>");
			} else {
				htmlOut.append("<input type=\"text\" ");
				if ( attr != null && !attr.getAttribute().isPermissionEdit() || this.isReadonly() == true) {
					this.setValidate(false);
					htmlOut.append(" readonly=\"readonly\" ");
				} else 
					htmlOut.append(" name=\""+this.getName()+"\" ");
				if (this.getTitle() != null && !this.getTitle().equals(""))
			    	htmlOut.append(" title=\""+this.getTitle()+"\" ");
				if (this.getId() != null && !this.getId().equals(""))
			    	htmlOut.append(" id=\""+this.getId()+"\" ");
				if (this.getDojoType() != null && !this.getDojoType().equals(""))
						htmlOut.append(" dojoType=\""+this.getDojoType()+"\"");
				if (this.getStyle() != null && !this.getStyle().equals(""))
					htmlOut.append(" style=\""+this.getStyle()+"\"");
				if (this.getStyleClass() != null && !this.getStyleClass().equals(""))
					htmlOut.append(" class=\""+this.getStyleClass()+"\" ");
				if (this.getOnBlur() != null && !this.getOnBlur().equals(""))
					
			    	htmlOut.append("onBlur=\""+this.getOnBlur()+"\" ");
		        if (this.getOnFocus() != null && !this.getOnFocus().equals(""))
			    	htmlOut.append("onFocus=\""+this.getOnFocus()+"\" ");
		        if (this.getOnChange() != null && !this.getOnChange().equals(""))
			    	htmlOut.append("onChange=\""+this.getOnChange()+"\" ");
		        if (this.getOnClick() != null && !this.getOnClick().equals(""))
			    	htmlOut.append("onClick=\""+this.getOnClick()+"\" ");
				if (this.getAddition()!= null && !this.getAddition().equals(""))
					htmlOut.append(" "+this.getAddition()+" ");
				
				if (this.validate){
					attr.getAttribute().getTypeVar();
                    if (attr.getAttribute().getTypeVar().value == VariablesType.FLOAT)
					    htmlOut.append("onkeyup=\"input_autochange(this,'digits')\" ");
					attr.getAttribute().getTypeVar();
                    if (attr.getAttribute().getTypeVar().value == VariablesType.INTEGER)
					    htmlOut.append("onkeyup=\"input_autochange(this,'number')\" ");
					attr.getAttribute().getTypeVar();
                    if (attr.getAttribute().getTypeVar().value == VariablesType.DATE)
					    htmlOut.append("onkeyup=\"input_autochange(this,'date')\" ");
				}
				
				htmlOut.append(" value=\""+value+"\"/>");
				
				if ((attr != null && !attr.getAttribute().isPermissionEdit() || this.isReadonly() == true) && this.isReadAsText() == true) {
					htmlOut.replace(0, htmlOut.length(), value);
				}
	
				if (validate == true) {
					ValidatedObject obj = new ValidatedObject(this.getName(), this.getType(), this.getPattern(), this.isRequired());
					List validList = (List)request.getAttribute(IConst_PUP.VALIDATE_OBJECTS);
		    		if (validList == null) {
		    			validList = new ArrayList();
		    			request.setAttribute(IConst_PUP.VALIDATE_OBJECTS, validList);
		    		}
		    		validList.add(obj);
		    		request.setAttribute(IConst_PUP.VALIDATE_OBJECTS, validList);
				}
			}
			
			out.print(htmlOut.toString());					
		} catch (Exception ex) {
			ex.printStackTrace();
			try{
			JspWriter out = pageContext.getOut();
			out.print("pup:tag error for name="+this.getName()+". error:"+ex.getMessage());
			}catch (Exception e) {}
			//throw new Error("InputTag doStartTag error " + ex.getMessage());
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
	   } catch (Exception ex){
		   ex.printStackTrace();
		   throw new Error("InputTag doEndTag error " + ex.getMessage());
	   }
	   return 0;
	}
	public String getAddition() {
		return addition;
	}
	public void setAddition(String addition) {
		this.addition = addition;
	}

	public boolean isReadAsText() {
		return readAsText;
	}
	public void setReadAsText(boolean readAsText) {
		this.readAsText = readAsText;
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isValidate() {
		return validate;
	}
	public void setValidate(boolean validate) {
		this.validate = validate;
	}
}
