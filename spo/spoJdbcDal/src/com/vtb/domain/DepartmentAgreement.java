package com.vtb.domain;
import java.sql.Date;
/**
 * Справка согласования с экспертными подразделениями.
 * 
 * @author Andrey Pavlenko
 */
public class DepartmentAgreement extends VtbObject {
    private static final long serialVersionUID = 1L;
    private String department;
    private String remark;
    private Date remarkDate;
    private String comment;

    public DepartmentAgreement() {
        super();
    }

    public DepartmentAgreement(String department, String remark, String comment) {
        super();
        this.department = department;
        this.remark = remark;
        this.comment = comment;
    }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getRemark() { return (remark==null)?"":remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public Date getRemarkDate() { return remarkDate; }
    public void setRemarkDate(Date remarkDate) { this.remarkDate = remarkDate;}
    public String getComment() { return (comment==null)?"":comment;}
    public void setComment(String comment) { this.comment = comment;}
}
