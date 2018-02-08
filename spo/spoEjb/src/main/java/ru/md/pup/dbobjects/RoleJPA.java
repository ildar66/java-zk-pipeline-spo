package ru.md.pup.dbobjects;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * JPA object "роли системы СПО"
 * 
 */
@Entity
@Table(name = "Roles")
public class RoleJPA implements Serializable {
    @Id
    @Column(name = "ID_ROLE")
    private Long idRole;

    @Column(name = "NAME_ROLE")
    private String nameRole;

    @ManyToOne
    @JoinColumn(name = "ID_TYPE_PROCESS")
    private ProcessTypeJPA process;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "role_nodes", joinColumns = @JoinColumn(name = "role_parent"), inverseJoinColumns = @JoinColumn(name = "role_child"))
    private List<RoleJPA> childRoles;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "role_nodes", joinColumns = @JoinColumn(name = "role_child"), inverseJoinColumns = @JoinColumn(name = "role_parent"))
    private List<RoleJPA> parentRoles;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "role_active", joinColumns = @JoinColumn(name = "id_role"), inverseJoinColumns = @JoinColumn(name = "id_user"))
    private List<UserJPA> users;

    public String toString() {
        return nameRole + " (" + idRole + ")";
    }

    /**
     * Процесс в СПО
     * 
     * @return
     */
    public ProcessTypeJPA getProcess() {
        return process;
    }

    /**
     * Процесс в СПО
     * 
     * @param process
     */
    public void setProcess(ProcessTypeJPA process) {
        this.process = process;
    }

    private BigDecimal active;

    @Column(name = "IS_ADMIN")
    private BigDecimal isAdmin;

    private static final long serialVersionUID = 1L;

    /**
     * Конструктор JPA object "роли системы СПО"
     */
    public RoleJPA() {
        super();
    }

    
    public RoleJPA(Long idRole, String nameRole, ProcessTypeJPA process) {
		super();
		this.idRole = idRole;
		this.nameRole = nameRole;
		this.process = process;
	}

	/**
     * ID роли СПО
     * 
     * @return
     */
    public Long getIdRole() {
        return this.idRole;
    }

    /**
     * ID роли СПО
     * 
     * @param idRole
     */
    public void setIdRole(Long idRole) {
        this.idRole = idRole;
    }

    /**
     * наименование роли СПО
     * 
     * @return
     */
    public String getNameRole() {
        return this.nameRole;
    }

    /**
     * наименование роли СПО
     * 
     * @param nameRole
     */
    public void setNameRole(String nameRole) {
        this.nameRole = nameRole;
    }

    /**
     * активность роли СПО
     * 
     * @return
     */
    public BigDecimal getActive() {
        return this.active;
    }

    /**
     * активность роли СПО
     * 
     * @param active
     */
    public void setActive(BigDecimal active) {
        this.active = active;
    }

    /**
     * признак роли СПО
     * 
     * @return
     */
    public BigDecimal getIsAdmin() {
        return this.isAdmin;
    }

    /**
     * признак роли СПО
     * 
     * @param isAdmin
     */
    public void setIsAdmin(BigDecimal isAdmin) {
        this.isAdmin = isAdmin;
    }

    /**
     * @return childRoles
     */
    public List<RoleJPA> getChildRoles() {
        return childRoles;
    }

    /**
     * @param childRoles
     *            childRoles
     */
    public void setChildRoles(List<RoleJPA> childRoles) {
        this.childRoles = childRoles;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (idRole ^ (idRole >>> 32));
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RoleJPA other = (RoleJPA) obj;
        if (!idRole.equals(other.idRole))
            return false;
        return true;
    }

    /**
     * @return users
     */
    public List<UserJPA> getUsers() {
        return users;
    }

    /**
     * @param users
     *            users
     */
    public void setUsers(List<UserJPA> users) {
        this.users = users;
    }

    /**
     * @return the parentRoles
     */
    public List<RoleJPA> getParentRoles() {
        return parentRoles;
    }

    /**
     * @param parentRoles
     *            the parentRoles to set
     */
    public void setParentRoles(List<RoleJPA> parentRoles) {
        this.parentRoles = parentRoles;
    }

}
