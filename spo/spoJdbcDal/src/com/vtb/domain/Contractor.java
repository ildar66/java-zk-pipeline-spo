package com.vtb.domain;

/**
 * Describe all contractor in the system. 
 * This class contains id of contractor and type. 
 * By type you can find other information about contractor.
 * 
 * 
 * @author Tormozov M.G.
 */
public class Contractor extends VtbObject {
	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    long id_contractor;
	int contractor_type;
	
	
	public int getContractor_type() {
		return contractor_type;
	}
	public void setContractor_type(int contractor_type) {
		this.contractor_type = contractor_type;
	}
	public long getId_contractor() {
		return id_contractor;
	}
	public void setId_contractor(long id_contractor) {
		this.id_contractor = id_contractor;
	}
	
	
}
