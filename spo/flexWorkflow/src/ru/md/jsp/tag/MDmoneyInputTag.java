package ru.md.jsp.tag;

import javax.servlet.jsp.JspWriter;

import com.vtb.util.Formatter;

/**
 * Для ввода и вывода численных значений типа money
 * Округляем до двух знаков, выделяем проблеами группы, меняем точку на запятую
 * вот так:    34 344 342 534,45
 * для значений типа 18.0 будет выведено 18,00
 * Для вывода целочисленных значений нужен отдельный таг...
 * @author Michael Kuznetsov
 */
public class MDmoneyInputTag  extends PUPTag {
    private static final long serialVersionUID = 1L;
    private String value="";
    private String onkeyup="";
    private String addition="";
    private String idsfx="";
    private String attrs="";
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
            if(this.getStyleClass()==null || this.getStyleClass().isEmpty()){
                this.setStyleClass("money");
            }
            if(this.getOnBlur()==null || this.getOnBlur().isEmpty()){
                this.setOnBlur("input_autochange(this,'money')");
            }
            
            if(this.isReadonly()) {
                htmlOut.append("<span class=\"" + this.getStyleClass() + "\" style=\""+this.getStyle()+"\">"+round(this.value)+"</span>");
            }else{
                htmlOut.append("<input class=\"" + this.getStyleClass() + "\" style=\""+this.getStyle()
                        +"\" name=\""+this.getName()+"\" onkeyup=\""+this.onkeyup+"\""
                        +" addition=\""+this.addition+"\""
                        +" id=\""+this.getId()+this.getIdsfx()+"\""
                        + (this.isDisabled() ? " disabled=\"true\"" : "")
                        +" onClick=\""+this.getOnClick()+"\""
                        +" onChange=\""+this.getOnChange()+"\""
                        +" onFocus=\""+this.getOnFocus()+"\""
                        +" onBlur=\""+this.getOnBlur()+"\""
                        +" "+attrs+" "
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
        return Formatter.toMoneyFormat(value);
    }
    
    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
    public String getIdsfx() {
        return idsfx;
    }
    public void setIdsfx(String idsfx) {
        this.idsfx = idsfx;
    }
	/**
	 * @return the attrs
	 */
	public String getAttrs() {
		return attrs;
	}
	/**
	 * @param attrs the attrs to set
	 */
	public void setAttrs(String attrs) {
		this.attrs = attrs;
	}
    
}
