package ru.md.jsp.tag;

import java.util.List;

import javax.servlet.jsp.JspWriter;

import ru.masterdm.spo.utils.SBeanLocator;
import ru.md.domain.ContractorType;

public class ContractorTypeTag extends PUPTag {
    private static final long serialVersionUID = 1L;
    private String value="";
    private String disabled="false";

    public int doStartTag() {    	 
        try {
        	List<Long> ids = SBeanLocator.singleton().compendium().getContractorTypeIdByIdR(new Long(value));
        	List<ru.md.domain.ContractorType> allct = SBeanLocator.singleton().compendium().findContractorTypeList();
            StringBuffer htmlOut = new StringBuffer("");
            if(this.isReadonly()){
	            for (ContractorType ct : allct)
		            if(ids.contains(ct.getId())){
	            		htmlOut.append("<label id=\"compare_contractor" + value + "_type" + ct.getId().toString() + "\">");
		            	htmlOut.append(ct.getName() + "</label><br />");
		            }
            }else{
                htmlOut.append("<div><input name=\"ContractorTypeValidateDiv\" type=\"hidden\">");
            	for(ContractorType ct : allct){
            		htmlOut.append("<label id=\"compare_contractor" + value + "_type" + ct.getId().toString() + "\">");
            		htmlOut.append("<input type=\"checkbox\" onclick=\"fieldChanged(this)\" name=\"");
            		htmlOut.append(this.getName()+"\" value=\"");
            		htmlOut.append(ct.getId().toString()+"\" "
            				+(ids.contains(ct.getId())?"checked":"")+">&nbsp;"+ct.getName()+"</label><br />");
            	}
            	htmlOut.append("</div>");
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
}
