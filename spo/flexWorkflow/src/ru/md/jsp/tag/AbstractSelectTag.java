package ru.md.jsp.tag;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import javax.servlet.jsp.JspWriter;

/**
 * Abstract class for select jsp tag.
 * @author Andrey Pavlenko
 */
public abstract class AbstractSelectTag extends PUPTag {
    private static final long serialVersionUID = 1L;
    
    private String value="";
    private String disabled="false";
    private String idsfx="";
    
    public abstract LinkedHashMap<String,String> getHashMap();
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
                    htmlOut.append(hashmap.get(this.value.trim()));
                }
                htmlOut.append(" </div>");
            } else {
            	if(this.disabled.equalsIgnoreCase("true")){
            		htmlOut.append("<input type=hidden name=\"" 
            				+ this.getName() + "\"" + idFound +" value=\""+this.value+"\" >"+this.value);
            	}else{
	                htmlOut.append("<select  name=\"" + this.getName() +  "\" " + idFound + " style=\""
	                        + this.getStyle() + "\" " + styleClass + 
	                        " onchange=\"fieldChanged();" + this.getOnChange()+"\" >");
	                while (iter.hasNext()) {
	                    String selected = "";
	                    String code = (String) iter.next();
	                    String displayname = (String) hashmap.get(code);
	                    if (this.value!=null&&code.trim().equalsIgnoreCase(this.value.trim()))
	                        selected = "selected=\"selected\"";
	                    htmlOut.append("<option " + selected + " value=\"" + code
	                            + "\">" + displayname + "</option>");
	                }
	                htmlOut.append("</select>");
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
