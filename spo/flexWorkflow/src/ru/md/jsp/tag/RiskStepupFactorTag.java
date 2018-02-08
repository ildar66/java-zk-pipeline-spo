package ru.md.jsp.tag;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import javax.servlet.jsp.JspWriter;

import ru.md.dict.dbobjects.RiskStepupFactorJPA;
import ru.md.helper.TaskHelper;

/**
 * Abstract class for select jsp tag.
 * @author Andrey Pavlenko
 */
public class RiskStepupFactorTag extends PUPTag {
    private static final long serialVersionUID = 1L;
    
    private String value="";
    private String disabled="false";
    private String idsfx="";
    
    public LinkedHashMap<String,String> getHashMap() {
    	LinkedHashMap<String,String> hashmap = new LinkedHashMap<String,String>();
    	for(RiskStepupFactorJPA r : TaskHelper.dict().findRiskStepupFactor()){
    		hashmap.put(r.getItem_id(), r.getShortname());
    	}
    	return hashmap;
    }
    /**
     * doStartTag is called by the JSP container when the tag is encountered
     */
    public int doStartTag() {
        try {
            LinkedHashMap<String, String> hashmap = this.getHashMap();
            Set<String> set = hashmap.keySet();
            Iterator<String> iter = set.iterator();
            StringBuffer htmlOut = new StringBuffer("");
            String idFound = (this.getId() != null) ? " id=\"" + this.getId()+this.getIdsfx() + "\" " : "";
            String styleClass = (this.getStyleClass() != null) ? " class=\"" + this.getStyleClass() + "\" " : "";
            if (this.isReadonly()) {
                htmlOut.append("<div style=\"" + this.getStyle() +  "\"" + idFound + ">");
                if(this.value!=null && !this.value.equals("") && hashmap.get(this.value.trim())!=null){
                	String stepup = "0";
                    htmlOut.append(hashmap.get(this.value.trim()));
                    for(RiskStepupFactorJPA r : TaskHelper.dict().findRiskStepupFactor())
                    	if(r.getItem_id().equals(this.getValue()))
                    		stepup = r.getText();
                    htmlOut.append("<br>Надбавка&nbsp;<span id='"+this.getId()+this.getIdsfx()+"span'>"+stepup+"</span>&nbsp;%&nbsp;годовых");
                }
                htmlOut.append(" </div>");
            } else {
            	if(this.disabled.equalsIgnoreCase("true")){
            		htmlOut.append("<input type=hidden name=\"" 
            				+ this.getName() + "\"" + idFound +" value=\""+this.value+"\" >"+this.value);
            	}else{
	                htmlOut.append("<select  name=\"" + this.getName() +  "\" " + idFound + " style=\""
	                        + this.getStyle() + "\" " + styleClass + 
	                        " onchange=\"fieldChanged();recalculatePercentRate()" + this.getOnChange()+"\" >");
	                String stepup = "0";
	                while (iter.hasNext()) {
	                    String selected = "";
	                    String code = (String) iter.next();
	                    String displayname = (String) hashmap.get(code);
	                    if (this.value!=null&&code.trim().equalsIgnoreCase(this.value.trim())){
	                        selected = "selected=\"selected\"";
	                        for(RiskStepupFactorJPA r : TaskHelper.dict().findRiskStepupFactor())
	                        	if(r.getItem_id().equals(this.getValue()))
	                        		stepup = r.getText();
	                    }
	                    htmlOut.append("<option " + selected + " value=\"" + code
	                            + "\">" + displayname + "</option>");
	                }
	                htmlOut.append("</select><br>Надбавка&nbsp;<span id='"+this.getId()+this.getIdsfx()+"span'>"+stepup+"</span>&nbsp;%&nbsp;годовых");
            	}
            }
            JspWriter out = pageContext.getOut();
            out.print(htmlOut.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(this.getClass().getName() + " doStartTag error " + ex.getMessage());
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
	public String getDisabled() {
		return disabled;
	}
	public void setDisabled(String disabled) {
		this.disabled = disabled;
	}

    public String getIdsfx() {
        return idsfx;
    }

    public void setIdsfx(String idsfx) {
        this.idsfx = idsfx;
    }

}
