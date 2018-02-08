package ru.md.jsp.tag;

import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspWriter;

import org.uit.director.db.dbobjects.AttributeStruct;
import org.uit.director.tasks.AttributesStructList;

/**
 * Tag render checkbox's list.
 * @author drone
 */
public class CheckListTag  extends PUPTag {
    private static final long serialVersionUID = 1L;
    private String attributeName;
    private String sourceName;
 
    /**
     * doStartTag is called by the JSP container when the tag is encountered
     */
     public int doStartTag() {
         StringBuffer htmlOut = new StringBuffer("");
         ServletRequest request = pageContext.getRequest();      
                 
         try {
             JspWriter out = pageContext.getOut();
             
             AttributesStructList attrs = (AttributesStructList)request.getAttribute(IConst_PUP.ATTRIBUTES);
             List list = (List) attrs.findAttributeByName(sourceName).getAttribute().getValueAttributeList();
             AttributeStruct attr = (AttributeStruct) attrs.findAttributeByName(attributeName);
             if (list != null && attr!=null) {
                 List listValue = attr.getAttribute().getValueAttributeList();
                 for (int i=0; i<list.size(); i++) {
                     String value = (String)list.get(i);
                     String isChecked="";    
                     if (listValue != null) {
                         for (int j=0; j<listValue.size(); j++) {
                             if (listValue.get(j).equals(value)) {
                                 isChecked = "checked";
                             }
                         }
                     }
                     htmlOut.append("<input type=\"checkbox\" "+ isChecked
                             +" name=\""+attr.getAttribute().getNameVariable()
                             +"\" value=\""+value+"\" />");
                     htmlOut.append("<label for=\"cb\">"+value+"</label>");
                     htmlOut.append("<br />");
                 }
             }
             out.print(htmlOut.toString());
          } catch (Exception ex) {
              ex.printStackTrace();
              throw new Error("CheckboxListTag doStartTag error " + ex.getMessage());
          }
          // Must return SKIP_BODY because we are not supporting a body for this 
          // tag.
          return SKIP_BODY;
     }
    /**
     * doEndTag is called by the JSP container when the tag is closed
     */
    public int doEndTag(){
       return 0; //do nothing
    }


    public String getAttributeName() {
        return attributeName;
    }


    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }


    public String getSourceName() {
        return sourceName;
    }


    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
}
