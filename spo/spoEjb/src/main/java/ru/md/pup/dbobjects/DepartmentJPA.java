package ru.md.pup.dbobjects;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * JPA object "Подразделение системы СПО"
 * 
 */
@Entity
@Table(name = "DEPARTMENTS")
public class DepartmentJPA implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ID: " + getIdDepartment() + " fullName=" + getFullName();
    }

    @Id
    @Column(name = "ID_DEPARTMENT")
    private Long idDepartment;

    @Column(name = "SHORTNAME")
    private String shortName;

    @Column(name = "FULLNAME")
    private String fullName;
    
	@Column(name="IS_INITIAL_DEP")
	private Boolean isInitialDep;

	@Column(name="IS_INVEST_BLOCK")
	private Boolean isInvestBlock;

	@Column(name="IS_EXEC_DEP")
	private Boolean isExecDep;	
	
    @Column(name="IS_Active")
    private Boolean isActive = false;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "DEPARTMENTS_PAR", 
            joinColumns = { @JoinColumn(name = "ID_DEPARTMENT_CHILD") }, 
            inverseJoinColumns = { @JoinColumn(name = "ID_DEPARTMENT_PAR") })
    private List<DepartmentJPA> parentDepartmentList;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "DEPARTMENTS_PAR", 
            joinColumns = @JoinColumn(name = "ID_DEPARTMENT_PAR"), 
            inverseJoinColumns = @JoinColumn(name = "ID_DEPARTMENT_CHILD"))
    private List<DepartmentJPA> childDepartmentList;
    
    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "func_group_dep", 
            joinColumns = @JoinColumn(name = "ID_DEP"), 
            inverseJoinColumns = @JoinColumn(name = "ID_GROUP"))
    private List<DepTypeJPA> depTypeList;

    /**
     * Конструктор JPA object "Подразделение системы СПО"
     */
    public DepartmentJPA() {
        super();
    }

    /**
     * ID подразделения.
     * 
     * @return
     */
    public Long getIdDepartment() {
        return this.idDepartment;
    }

    /**
     * ID подразделения.
     * 
     * @param idDepartment
     */
    public void setIdDepartment(Long idDepartment) {
        this.idDepartment = idDepartment;
    }

    /**
     * короткое имя подразделения.
     * 
     * @return
     */
    public String getShortName() {
        return this.shortName;
    }

    /**
     * короткое имя подразделения.
     * 
     * @param shortname
     */
    public void setShortName(String shortname) {
        this.shortName = shortname;
    }

    /**
     * полное имя подразделения.
     * 
     * @return
     */
    public String getFullName() {
        return this.fullName;
    }

    /**
     * полное имя подразделения.
     * 
     * @param fullname
     */
    public void setFullName(String fullname) {
        this.fullName = fullname;
    }

    public List<DepartmentJPA> getParentDepartmentList() {
    	return parentDepartmentList;
    }
    public DepartmentJPA getParentDepartment() {
    	if(parentDepartmentList==null || parentDepartmentList.isEmpty())
    		return null;
    	return parentDepartmentList.get(0);
    }
    /**
     * возвращает все вышестоящие подразделения до самого верха
     */
    public Set<DepartmentJPA> getAllParent() {
    	HashSet<DepartmentJPA> set = new HashSet<DepartmentJPA>();
    	for(DepartmentJPA dep : parentDepartmentList){
    		set.add(dep);
    		set.addAll(dep.getAllParent());
    	}
        return set;
    }

    public void setParentDepartmentList(List<DepartmentJPA> parentDepartmentList) {
        this.parentDepartmentList = parentDepartmentList;
    }

    public List<DepartmentJPA> getChildDepartmentList() {
        return childDepartmentList;
    }

    public void setChildDepartmentList(List<DepartmentJPA> childDepartmentList) {
        this.childDepartmentList = childDepartmentList;
    }

    /**
     * @return depTypeList
     */
    public List<DepTypeJPA> getDepTypeList() {
        return depTypeList;
    }

    /**
     * @param depTypeList depTypeList
     */
    public void setDepTypeList(List<DepTypeJPA> depTypeList) {
        this.depTypeList = depTypeList;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((idDepartment == null) ? 0 : idDepartment.hashCode());
        return result;
    }

    /* (non-Javadoc)
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
        DepartmentJPA other = (DepartmentJPA) obj;
        if (idDepartment == null) {
            if (other.idDepartment != null)
                return false;
        } else if (!idDepartment.equals(other.idDepartment))
            return false;
        return true;
    }

    public boolean isAncestor(DepartmentJPA dep){
        if(this.equals(dep))
            return true;
        for(DepartmentJPA parent : parentDepartmentList){
            if(parent.isAncestor(dep))
                return true;
        }
        return false;
    }

	public Boolean getIsInitialDep() {
		return isInitialDep;
	}

	public void setIsInitialDep(Boolean isInitialDep) {
		this.isInitialDep = isInitialDep;
	}

	public Boolean getIsExecDep() {
		return isExecDep;
	}

	public void setIsExecDep(Boolean isExecDep) {
		this.isExecDep = isExecDep;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

    public Boolean isInvestBlock() {
        return isInvestBlock;
    }

    public void setIsInvestBlock(Boolean isInvestBlock) {
        this.isInvestBlock = isInvestBlock;
    }
}
