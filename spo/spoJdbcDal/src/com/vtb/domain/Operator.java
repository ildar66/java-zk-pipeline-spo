package com.vtb.domain;

/**
 * VtbObject "Пользователи системы ПО"
 * 
 * @author IShafigullin
 * 
 */
public class Operator extends VtbObject {
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
    private Integer departmentID = null;//
    private String eMail = null; // почта оператора

    @Override
    public boolean equals(Object anObject) {
        if (anObject == null) {
            return false;
        }
        if (!(anObject instanceof Operator)) {
            return false;
        }
        Operator aOperators = (Operator) anObject;
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

    public Operator(Integer aId, String aLogin) {
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
        return (fieldFA != null)?fieldFA:"";
    }

    public void setFieldFA(String fieldFA) {
        this.fieldFA = fieldFA;
    }

    public String getFieldIM() {
        return (fieldIM != null)?fieldIM:"";
    }

    public void setFieldIM(String fieldIM) {
        this.fieldIM = fieldIM;
    }

    public String getFieldOT() {
        return (fieldOT != null)?fieldOT:"";
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

    public Operator(Integer id) {
        super();
        this.id = id;
    }

    public String getEMail() {
        return eMail;
    }

    public void setEMail(String mail) {
        eMail = mail;
    }
    public String getName(){
        return getFieldFA() + " " + getFieldIM() + " " + getFieldOT();
    }

}
