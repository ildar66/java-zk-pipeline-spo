package ru.md.spo.dbobjects;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "v_spo_org")
public class OrgJPA implements Serializable {
    private static final long serialVersionUID = 1L;

    @Transient
    String clientUnited;//для фильтра
    
    @Id
    @Column(name = "CRMID")
    private String id;
    
    @Column(name = "ORGANIZATIONNAME")
    private String organizationName;

    private String clientcategory;
    
    private String inn;
    private String kpp;
    private String industryname;
    private String ogrn;
	@Column(name = "IS_ACTIVE")
	private Boolean isActive;
	@Column(name = "TYPE")
	private String clientType;
	@Column(name = "DEPARTMENT")
	private String department;
	private String division;
	@Column(name = "ID_UNITED_CLIENT")
	private String idUnitedClient;
    
    @ManyToMany(fetch = FetchType.LAZY) @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(name = "v_crm_companiesgroup_linked", joinColumns = @JoinColumn(name = "accountid"), inverseJoinColumns = @JoinColumn(name = "gc_id"))
    private List<OrgGroupJPA> groupList;
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getClientcategory() {
        return clientcategory==null?"":clientcategory;
    }
    public void setClientcategory(String clientcategory) {
        this.clientcategory = clientcategory;
    }
	public String getInn() {
		if(inn==null)
			return "";
		return inn;
	}
	public void setInn(String inn) {
		this.inn = inn;
	}

	public String getOgrn() {
		if (ogrn!=null && !ogrn.isEmpty()) return ogrn;
		if(getGroupList() == null)
			return "";
		for(OrgGroupJPA group : getGroupList())
			if(!group.getOgrn().isEmpty())
				return group.getOgrn();
		return "";
	}

	public void setOgrn(String ogrn) {
		this.ogrn = ogrn;
	}

	public List<OrgGroupJPA> getGroupList() {
		return groupList;
	}

	public void setGroupList(List<OrgGroupJPA> groupList) {
		this.groupList = groupList;
	}

	/**
	 * @return the industry
	 */
	public String getIndustry() {
		if(industryname==null)
			return "";
		return industryname;
	}

	/**
	 * @param industry the industry to set
	 */
	public void setIndustry(String industry) {
		this.industryname = industry;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public String getClientType() {
		return clientType;
	}

	public void setClientType(String clientType) {
		this.clientType = clientType;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getClientUnited() {
		return clientUnited==null?"":clientUnited;
	}

	public void setClientUnited(String clientUnited) {
		this.clientUnited = clientUnited;
	}

	public String getIdUnitedClient() {
		return idUnitedClient;
	}

	public void setIdUnitedClient(String idUnitedClient) {
		this.idUnitedClient = idUnitedClient;
	}

	public String getKpp() {
		return kpp==null?"":kpp;
	}

	public void setKpp(String kpp) {
		this.kpp = kpp;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

}
