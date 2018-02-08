package ru.md.jsp.tag;

import java.util.LinkedHashMap;

import javax.servlet.jsp.JspWriter;

import ru.masterdm.compendium.domain.spo.StandardPriceCondition;
import ru.masterdm.compendium.model.CompendiumSpoActionProcessor;

public class StandardPriceConditionTag extends PUPTag {
    private static final long serialVersionUID = 1L;
    private String value="";

    public LinkedHashMap<String,String> getHashMap() {
        try{
            LinkedHashMap<String,String> hashmap = new LinkedHashMap<String,String>();
            CompendiumSpoActionProcessor processor = 
                (CompendiumSpoActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumSpo");
            StandardPriceCondition[] list = processor.findStandardPriceConditionList("%", "c.name");
            hashmap.put("-1", " ");
            for (int i=0;i<list.length; i++){
                StandardPriceCondition entity = (StandardPriceCondition)list[i];
                hashmap.put(entity.getId().toString(), entity.getName());
            }
            return hashmap;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Error("ComissionTypeTag doStartTag error " + ex.getMessage());
        }
    }
    /**
     * doStartTag is called by the JSP container when the tag is encountered
     */
    public int doStartTag() {        
        try {
            String code=java.util.UUID.randomUUID().toString();
            LinkedHashMap<String, String> hashmap = this.getHashMap();
            StringBuffer htmlOut = new StringBuffer("");
            String name = this.value.equals("0")?"не выбрана":hashmap.get(this.value);
            if (this.isReadonly()) {
                htmlOut.append("<span style=\"" + this.getStyle() + "\">");
                if((this.value!=null)&&(!this.value.equals(""))&&(hashmap.get(this.value)!=null)){
                    htmlOut.append(name);
                }
                htmlOut.append(" </span>");
            } else {
                htmlOut.append("<a href=\"javascript:;\" "
                        +" onClick=\"$('#supplyid').val('"+code+"');$('#StandardPriceConditionDiv').jqmShow();\" >");
                htmlOut.append("<span id=\"sp"+code+"\">").append(name).append("</span>");
                htmlOut.append("<input type=\"hidden\" id=\""
                        +code+"\" name=\"" + this.getName() + 
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
}
