package com.vtb.domain;

import java.util.ArrayList;
import java.util.List;

import ru.masterdm.compendium.domain.crm.CompanyGroup;
import ru.masterdm.compendium.domain.crm.Organization;
import ru.masterdm.compendium.domain.spo.ContractorType;

public class TaskContractor extends VtbObject{
	private static final long serialVersionUID = 1L;
	private Organization org;
	private ArrayList<ru.masterdm.compendium.domain.crm.Rating> rating = new ArrayList<ru.masterdm.compendium.domain.crm.Rating>();
	private ArrayList<ContractorType> orgType;
	private List<CompanyGroup> group= new ArrayList<CompanyGroup>();
	private Long id;
	private boolean mainBorrower = false;  // основной заемщик 
	private String ratingPKR;

   public void setId(Long id) {
        this.id = id;
   }
    
    public TaskContractor() {
        super();
    }
    
    public TaskContractor(Organization org, ArrayList<ContractorType> orgType, Long id) {
        super();
        this.org = org;
        this.orgType = orgType;
        this.id = id;
        this.mainBorrower = false;
    }
	
    public TaskContractor(Organization org, ArrayList<ContractorType> orgType,
			Long id, String ratingPKR) {
		super();
		this.org = org;
		this.orgType = orgType;
		this.id = id;
		this.ratingPKR = ratingPKR;
	}

	public Long getId() {return id;}
	public Organization getOrg() {return org;}
	public void setOrg(Organization org) {this.org = org;}
	public ArrayList<ContractorType> getOrgType() {return orgType;}
	public void setOrgType(ArrayList<ContractorType> orgType) {this.orgType = orgType;}
	public List<CompanyGroup> getGroupList() {return group;}
	public void setGroup(List<CompanyGroup> group) {this.group = group;}
    public boolean isMainBorrower() { return mainBorrower; }
    public void setMainBorrower(boolean mainBorrower) { this.mainBorrower = mainBorrower; }

    public ArrayList<ru.masterdm.compendium.domain.crm.Rating> getRating() {
        return rating;
    }
    public void setRating(ArrayList<ru.masterdm.compendium.domain.crm.Rating> rating) {
        this.rating = rating;
    }

	public String getRatingPKR() {
		if(ratingPKR==null)
			return "";
		return ratingPKR;
	}

	public void setRatingPKR(String ratingPKR) {
		this.ratingPKR = ratingPKR;
	}
    
}
