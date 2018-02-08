package ru.md.jsp.tag;

import javax.servlet.jsp.JspWriter;

import com.vtb.util.Formatter;

/**
 * Для ввода и вывода численных значений с разделителями разрядов
 * Округляем до целого значения, выделяем проблеами группы
 * вот так:    34 344 342 534
 * для значений типа 18.07 будет выведено 18
 * @author Michael Kuznetsov
 */
public class MDintegerInputTag  extends PUPTag {
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
                htmlOut.append("<span class=\"" + this.getStyleClass() + "\" style=\""+this.getStyle()+"\">"+round(this.value)+"</span>");
            }else{
                htmlOut.append("<input class=\"" + this.getStyleClass() + "\" style=\""+this.getStyle()
                        +"\" name=\""+this.getName()+"\" onkeyup=\""+this.onkeyup+"\""
                        +" addition=\""+this.addition+"\""
                        +" id=\""+this.getId()+"\""
                        + (this.isDisabled() ? " disabled=\"true\"" : "")
                        +" onClick=\""+this.getOnClick()+"\""
                        +" onChange=\""+this.getOnChange()+"\""
                        +" onFocus=\""+this.getOnFocus()+"\""
                        +" onBlur=\""+this.getOnBlur()+"\""
                        +" value=\""+round(this.value)+"\" onchange=\"fieldChanged()\" />");
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
    
    private String round(String value) {
        return Formatter.toMoneyIntegerFormat(value);
    }
    
    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
