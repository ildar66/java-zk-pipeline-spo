package com.vtb.custom;

import java.io.Serializable;

/**
 * VtbObject "Пользователи системы ПО"
 * 
 * @author IShafigullin
 * 
 */
public class OperatorTO implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Integer id = null; // id
    private String login = null; // Логин
    private String pass = null; // password
    private String fieldFA = null;// ФИО
    private String fieldIM = null;// ФИО
    private String fieldOT = null;// ФИО
    private Integer departmentID = null;// id филиала
    private String eMail = null; // почта оператора
    private String depName = null; // имя филиала

    @Override
    public boolean equals(Object anObject) {
        if (anObject == null) {
            return false;
        }
        if (!(anObject instanceof OperatorTO)) {
            return false;
        }
        OperatorTO aOperators = (OperatorTO) anObject;
        return aOperators.getId().equals(getId());
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Operator: ");
        sb.append(getId() + "(" + getLogin() + ")");
        // sb.append('\n');
        // sb.append(" IsActive: ");
        // sb.append(getIsActive());

        return sb.toString();
    }

    public OperatorTO(Integer aId, String aLogin) {
        super();
        setId(aId);
        setLogin(aLogin);
    }

    public Integer getDepartmentID() {
        return departmentID;
    }

    public void setDepartmentID(Integer departmentID) {
        this.departmentID = departmentID;
    }

    public String getFieldFA() {
        return (fieldFA != null) ? fieldFA : "";
    }

    public void setFieldFA(String fieldFA) {
        this.fieldFA = fieldFA;
    }

    public String getFieldIM() {
        return (fieldIM != null) ? fieldIM : "";
    }

    public void setFieldIM(String fieldIM) {
        this.fieldIM = fieldIM;
    }

    public String getFieldOT() {
        return (fieldOT != null) ? fieldOT : "";
    }

    public void setFieldOT(String fieldOT) {
        this.fieldOT = fieldOT;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public OperatorTO(Integer id) {
        super();
        this.id = id;
    }

    public String getEMail() {
        return eMail;
    }

    public void setEMail(String mail) {
        eMail = mail;
    }

    public String getName() {
        return getFieldFA() + " " + getFieldIM() + " " + getFieldOT();
    }

    public String getDepName() {
        return depName;
    }

    public void setDepName(String depName) {
        this.depName = depName;
    }

}
