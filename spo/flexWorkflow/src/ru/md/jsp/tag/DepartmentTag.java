package ru.md.jsp.tag;

import java.util.LinkedHashMap;

import javax.servlet.jsp.JspWriter;

import ru.masterdm.compendium.domain.Department;
import ru.masterdm.compendium.model.CompendiumActionProcessor;

public class DepartmentTag extends PUPTag {
    public LinkedHashMap<String, String> getHashMap() {
        try{
            LinkedHashMap<String,String> hashmap = new LinkedHashMap<String,String>();
            CompendiumActionProcessor compenduim = (CompendiumActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("Compendium");
            Department[] list = compenduim.getDepartmentListAll();
            for (int i=0;i<list.length;i++){
                Department entity = list[i];
                hashmap.put(entity.getId().toString(), entity.getShortName());
            }
            return hashmap;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Error("DepartmentTag doStartTag error " + ex.getMessage());
        }
    }
    private static final long serialVersionUID = 1L;
    private String code="";
    private String value="";
    private boolean onlyInitialDep=false;
    private boolean onlyExecDep=false;
    private String javascript=null;

    /**
     * doStartTag is called by the JSP container when the tag is encountered
     */
    public int doStartTag() {
        try {
            LinkedHashMap<String, String> hashmap = this.getHashMap();
            String name = hashmap.get(this.value);
            if (name == null) name = "не выбрано";
            StringBuffer htmlOut = new StringBuffer("");
            if (this.isReadonly()) {
                htmlOut.append("<span style=\"" + this.getStyle() + "\">");
                if((this.value!=null)&&(!this.value.equals(""))&&(hashmap.get(this.value)!=null)){
                    htmlOut.append(name);
                }
                htmlOut.append(" </span>");
            } else {
            	String url="popup_departments.jsp?someparam=somevalue";
            	if(onlyExecDep)url+="&onlyExecDep=true";
            	if(onlyInitialDep)url+="&onlyInitialDep=true";
            	if(javascript!=null)url+="&javascript="+getJavascript();
                htmlOut.append("<a class=\"fancy\" href=\""+url+"\" "
                		+" onClick=\"fieldChanged(this);document.getElementById('supplyid').value='"
                		+this.getCode()+"'\" >");
                htmlOut.append("<span id=\"sp"+this.getCode()+"\">").append(name).append("</span>");
                htmlOut.append("<input type=\"hidden\" id=\""
                		+this.getCode()+"\" name=\"" + this.getName() + 
                		"\" value=\""+this.value+"\" readonly=\"true\" />");
                htmlOut.append(" </a>");
            }
            JspWriter out = pageContext.getOut();
            out.print(htmlOut.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Error(this.getClass().getName() + " doStartTag error " + ex.getMessage());
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

    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @return the onlyInitialDep
	 */
	public boolean isOnlyInitialDep() {
		return onlyInitialDep;
	}
	/**
	 * @param onlyInitialDep the onlyInitialDep to set
	 */
	public void setOnlyInitialDep(boolean onlyInitialDep) {
		this.onlyInitialDep = onlyInitialDep;
	}
	/**
	 * @return the onlyExecDep
	 */
	public boolean isOnlyExecDep() {
		return onlyExecDep;
	}
	/**
	 * @param onlyExecDep the onlyExecDep to set
	 */
	public void setOnlyExecDep(boolean onlyExecDep) {
		this.onlyExecDep = onlyExecDep;
	}
	/**
	 * @return the javascript
	 */
	public String getJavascript() {
		return javascript;
	}
	/**
	 * @param javascript the javascript to set
	 */
	public void setJavascript(String javascript) {
		this.javascript = javascript;
	}
}
