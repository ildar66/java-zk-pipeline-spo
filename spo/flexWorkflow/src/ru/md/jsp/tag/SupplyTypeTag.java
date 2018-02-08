package ru.md.jsp.tag;

import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.jsp.JspWriter;

import ru.md.dict.dbobjects.SupplyTypeJPA;
import ru.md.helper.TaskHelper;

/**
 * Select supply with modal window.
 * @author Andrey Pavlenko
 */
public class SupplyTypeTag extends PUPTag {
    private static final long serialVersionUID = 1L;
    private String code="";
    private String value="";

    public LinkedHashMap<String,String> getHashMap() {
        try{
            LinkedHashMap<String,String> hashmap = new LinkedHashMap<String,String>();
            
            List<SupplyTypeJPA> list=TaskHelper.dict().findSupplyType();
            for (int i=0;i<list.size();i++){
                SupplyTypeJPA entity = list.get(i);
                hashmap.put(entity.getId().toString(), entity.getName());
            }
            return hashmap;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Error("SupplyType doStartTag error " + ex.getMessage());
        }
    }

    /**
     * doStartTag is called by the JSP container when the tag is encountered
     */
    public int doStartTag() {    	 
        try {
            LinkedHashMap<String, String> hashmap = this.getHashMap();
            StringBuffer htmlOut = new StringBuffer("");
            String formattedValue = (this.value == null ||this.value.equals("")||this.value.equals("0")||this.value.equals("-1"))?"-1" : this.value; 
            String typename = (formattedValue.equals("-1"))?"не выбрана":hashmap.get(this.value);
            
            if (this.isReadonly()) {
                htmlOut.append("<span style=\"" + this.getStyle() + "\">");
                htmlOut.append(typename);
                htmlOut.append(" </span>");
            } else {
                htmlOut.append("<a class=\"dialogActivator\" dialogId=\"supplySelection\" href=\"javascript:;\" "
                		+" onClick=\"fieldChanged();document.getElementById('supplyid').value='"
                		+this.getCode()+"'\" >");
                htmlOut.append("<span id=\"sp"+this.getCode()+"\">").append(typename).append("</span>");
                htmlOut.append("<input type=\"hidden\" id=\""
                		+this.getCode()+"\" name=\"" + this.getName() + 
                		"\" value=\""+formattedValue+"\" readonly=\"" + /*this.isReadonly()*/ "false" + "\" />");
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

}
