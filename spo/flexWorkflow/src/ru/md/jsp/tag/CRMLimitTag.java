package ru.md.jsp.tag;

import javax.servlet.jsp.JspWriter;

import com.vtb.util.LimitTreeBuilder;
import com.vtb.util.PrintHTMLLimitTreeBuilder;

public class CRMLimitTag extends PUPTag {
	private static final long serialVersionUID = 1L;
	
	private String limitid = null;  // передавать не сублимит сюда, а ЛИМИТ.
	private String taskid = null;   // id Task
    /**
     * doStartTag is called by the JSP container when the tag is encountered
     */
    public int doStartTag() {
        LimitTreeBuilder builder = new PrintHTMLLimitTreeBuilder(new StringBuffer(""), pageContext.getOut(), limitid, taskid, SKIP_BODY);
        return builder.makeOutput();
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
    
	/**
	 * @return the limitid
	 */
	public String getLimitid() {
		return limitid;
	}
	
	/**
	 * @param limitid the limitid to set
	 */
	public void setLimitid(String limitid) {
		this.limitid = limitid;
	}
	
	/**
     * @return the taskid
     */
    public String getTaskid() {
        return taskid;
    }
    
    /**
     * @param taskid the taskid to set
     */
    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

}
