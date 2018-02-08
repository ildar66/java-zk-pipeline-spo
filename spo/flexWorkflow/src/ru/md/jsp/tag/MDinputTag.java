package ru.md.jsp.tag;

import javax.servlet.jsp.JspWriter;

/*<md:input name="Дата действия лимита" value="<%=mdtask_date %>" 
    addition="<%=dateAddition%>" readonly="<%=readOnly %>" id="termOfLimit" onClick="popCalInFrame(this);return false;" 
    class="text data" style="width:6em;" onkeyup="input_autochange(this,'date')" />*/
public class MDinputTag  extends PUPTag {
    private static final long serialVersionUID = 1L;
    private String value="";
    private String onkeyup="";
    private String addition="";
    private boolean disabled = false;

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
    /**
     * doStartTag is called by the JSP container when the tag is encountered
     */
     public int doStartTag() {
        try {
            StringBuffer htmlOut = new StringBuffer("");
            if(this.isReadonly()) {
                htmlOut.append("<span class=\"" + this.getStyleClass() + "\" style=\""+this.getStyle()+"\">"
                        +this.value+"</span>");
            }else{
                htmlOut.append("<input class=\"" + this.getStyleClass() + "\" style=\""+this.getStyle()
                        +"\" name=\""+this.getName()+"\" onkeyup=\""+this.onkeyup+"\""
                        +" addition=\""+this.addition+"\""
                        + (this.isDisabled() ? " disabled=\"true\"" : "")
                        +" id=\""+this.getId()+"\""
                        +" onClick=\""+this.getOnClick()+"\""
                        +" onChange=\""+this.getOnChange()+"\""
                        +" onFocus=\""+this.getOnFocus()+"\""
                        +" onBlur=\""+this.getOnBlur()+"\""
                        +" value=\""+this.value+"\" onchange=\"fieldChanged()\" />");
            }
            JspWriter out = pageContext.getOut();
            out.print(htmlOut.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Error("CurrencyTag doStartTag error " + ex.getMessage());
        }
        // Must return SKIP_BODY because we are not supporting a body for this
        // tag.
        return SKIP_BODY;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }

    public String getOnkeyup() {
        return onkeyup;
    }
    public void setOnkeyup(String onkeyup) {
        this.onkeyup = onkeyup;
    }
    public String getAddition() {
        return addition;
    }
    public void setAddition(String addition) {
        this.addition = addition;
    }
    
    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
