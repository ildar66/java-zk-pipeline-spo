package com.vtb.domain;

import java.io.Serializable;

/**
 * Инфромация о получателе письма
 * @author Какунин Константин Юрьевич
 *
 */
public class SPOMessageReceiver implements Serializable{
    private static final long serialVersionUID = 1L;
    private long id;
    private boolean isRead;
    private String email;

    /**
     * Уникальный id оператора
     */
    private String operatorLogin;

    public SPOMessageReceiver(String operatorLogin, boolean isRead) {
        this.operatorLogin = operatorLogin;
        this.isRead = isRead;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof SPOMessageReceiver))
            return false;
        SPOMessageReceiver m = (SPOMessageReceiver) obj;
        return (operatorLogin == null && m.operatorLogin == null) || (operatorLogin != null && operatorLogin.equals(m.operatorLogin));

    }

    public String getOperatorLogin() {
        return operatorLogin;
    }

    @Override
    public int hashCode() {
        return operatorLogin == null ? 0 : operatorLogin.hashCode();
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    public void setEmail(String email) {
    	this.email = email;
    }
    
    public String getEmail() {
    	return email;
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("operatorLogin = ").append(operatorLogin);
        s.append(", isRead = ").append(isRead);
        return s.toString();

    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
