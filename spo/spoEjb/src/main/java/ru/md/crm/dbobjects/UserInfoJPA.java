package ru.md.crm.dbobjects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="v_spo_userinfo",schema="sysdba")
public class UserInfoJPA  {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name="USERID")
    private String id;
    @Column(name="USERCODE")
    private String login;
    private String lastname; 
    private String firstname;
    /**
     * @return id
     */
    public String getId() {
        return id;
    }
    /**
     * @param id id
     */
    public void setId(String id) {
        this.id = id;
    }
    /**
     * @return login
     */
    public String getLogin() {
        return login;
    }
    /**
     * @param login login
     */
    public void setLogin(String login) {
        this.login = login;
    }
    /**
     * @return lastname
     */
    public String getLastname() {
        return lastname;
    }
    /**
     * @param lastname lastname
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    /**
     * @return firstname
     */
    public String getFirstname() {
        return firstname;
    }
    /**
     * @param firstname firstname
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
    
}
