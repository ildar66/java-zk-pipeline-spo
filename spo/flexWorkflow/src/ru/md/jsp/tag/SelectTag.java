package ru.md.jsp.tag;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

import org.uit.director.db.dbobjects.AttributeStruct;
import org.uit.director.tasks.AttributesStructList;

import ru.masterdm.compendium.model.CompendiumCrmActionProcessor;
import ru.md.helper.TaskHelper;

/**
 * This is a simple tag example to show how content is added to the
 * output stream when a tag is encountered in a JSP page. 
 */
public class SelectTag extends PUPTag {
	private String valueListName = "";
	private boolean valuesFromRequest = false;
	private static final  Logger logger=Logger.getLogger("SelectTag");

	
	/**
	* doStartTag is called by the JSP container when the tag is encountered
	*/
    public int doStartTag() {
    	StringBuffer htmlOut = new StringBuffer("");
    	List valuesList = null;    	
    	String value = "";
		try {
			JspWriter out = pageContext.getOut();
	        ServletRequest request = pageContext.getRequest();
	        AttributesStructList attrs = (AttributesStructList)request.getAttribute(IConst_PUP.ATTRIBUTES);
			if(attrs==null){
				attrs = TaskHelper.getCurrTaskInfo((HttpServletRequest)request).getAttributes();
			}
	        AttributeStruct attr = null;
	        AttributeStruct attrList = null;

	        //1. Get values with index
			if (attrs!=null) {
				attr = (AttributeStruct) attrs.findAttributeByName(this.getName());
				if (attr != null) {
					List dataList = attr.getAttribute().getValueAttributeList();
					if (dataList != null && this.getIndex() >= 0 && this.getIndex()<dataList.size()) {						
						value = dataList.get(this.getIndex()).toString();
					}
				}
			}
			
			//2. Get vales list from request or from attributes
			if (this.getValueListName() != null && !this.getValueListName().equals("") ) {
				if (this.isValuesFromRequest() == true) {
					valuesList = (List)request.getAttribute(this.getValueListName());					
				} else {
					if (attrs != null) {
						attrList = (AttributeStruct) attrs.findAttributeByName(this.getValueListName());						
						if (attrList != null) {
							valuesList = attrList.getAttribute().getValueAttributeList();
						}
					}
				}
			} else {
				valuesList = attr.getAttribute().getOptions();				
			}
			
			
			//3. Draw SELECT if EDIT PERMISSION AVAILABLE
			if (attr != null && attr.getAttribute().isPermissionEdit()) {
				htmlOut.append("<select name=\""+this.getName()+"\" ");
				if (this.getDojoType() != null && !this.getDojoType().equals(""))
					htmlOut.append(" dojoType=\""+this.getDojoType()+"\"");
				if (this.getStyle() != null && !this.getStyle().equals(""))
					htmlOut.append(" style=\""+this.getStyle()+"\"");
				if (this.getTitle() != null && !this.getTitle().equals(""))
			    	htmlOut.append(" title=\""+this.getTitle()+"\" ");
				if (this.getId() != null && !this.getId().equals(""))
			    	htmlOut.append(" id=\""+this.getId()+"\" ");
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
				htmlOut.append(">\n");
			
				//HARDCODE если валюта, то хардкодим
				if(this.getName().equalsIgnoreCase("Валюта премии")||this.getName().equalsIgnoreCase("Валюта санкции")
						||this.getName().equalsIgnoreCase("Валюта Комиссии")||this.getName().equalsIgnoreCase("Валюта"))
				{
					logger.info("hardcode for currency");
					CompendiumCrmActionProcessor compenduimCRM = (CompendiumCrmActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumCrm");
					List<ru.masterdm.compendium.domain.Currency> currencyList = 
					    Arrays.asList(compenduimCRM.findCurrencyList("", null));
					if (!this.getName().equalsIgnoreCase("Валюта")){
					    currencyList.add(0, new ru.masterdm.compendium.domain.Currency("%"));
					}
					for (ru.masterdm.compendium.domain.Currency cur : currencyList){
						String isSelected = "";
						if (value.equals(cur.getCode()))
							isSelected = "selected";
						htmlOut.append("<option "+isSelected+" value=\""+cur.getCode()+"\">"+cur.getCode()+"</option>");
					}
				} else {
    				htmlOut.append("<option value=\"\"></option>\n");
    				if (valuesList != null) {				
    					for(int j=0; j<valuesList.size(); j++) {
    						String valueItem = valuesList.get(j).toString();
    						String isSelected = "";
    						if (value.equals(valueItem))
    							isSelected = "selected";
    						htmlOut.append("\t<option "+isSelected+" value=\""+valueItem+"\">"+valueItem+"</option>\n"); 
    					}
    				}	
				}
				
				htmlOut.append("</select>");						    
			} else {//нет прав редактировать		
				htmlOut.append("<span name=\""+this.getName()+"\" class=\"inputed text\"");
				if (this.getStyle()!=null)htmlOut.append(" style=\""+this.getStyle()+"\"");
				htmlOut.append(" >");
				if (valuesList != null) {					
					for(int j=0; j<valuesList.size(); j++) {
						String valueItem = valuesList.get(j).toString();
						if (value.equals(valueItem))
							htmlOut.append(valueItem);	
					}		
				}				
				htmlOut.append("</span>");	
				
			}
			out.print(htmlOut.toString());
			
		} catch (Exception ex) {
			ex.printStackTrace();
			//throw new Error("SelectTag doStartTag error " + ex.getMessage());
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
		   throw new Error("SelectTag doEndTag error " + ex.getMessage());
	   }
	   return 0;
	}
	
	public String getValueListName() {
		return valueListName;
	}
	
	public void setValueListName(String valueListName) {
		this.valueListName = valueListName;
	}
	
	public boolean isValuesFromRequest() {
		return valuesFromRequest;
	}
	
	public void setValuesFromRequest(boolean valuesFromRequest) {
		this.valuesFromRequest = valuesFromRequest;
	}
}
