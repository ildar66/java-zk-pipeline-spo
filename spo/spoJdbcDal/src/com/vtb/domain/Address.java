package com.vtb.domain;

/**
 * VtbObject "адрес"
 * 
 * @author IShafigullin
 * 
 */
public class Address extends VtbObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id = null; // id "адреса"
	private String city = null; // город
	private String postalCode = null; // индекс
	private String country = null; // страна
	private String county = null; // округ
	private String description = null;// юридический или фактический.
	private String address1 = null;//
	private String address2 = null;//
	private String address3 = null;//
	private String address4 = null;//
	private Integer organizationID = null; // внешний ключ на организацию

	@Override
	public boolean equals(Object anObject) {
		if (anObject == null) {
			return false;
		}
		if (!(anObject instanceof Address)) {
			return false;
		}
		Address aAddress = (Address) anObject;
		return aAddress.getId().intValue() == getId().intValue();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Address: ");
		sb.append(getId() + "(city=" + getCity() + ", country=" + getCountry() + ")");

		// sb.append('\n');
		// sb.append(" IsActive: ");
		// sb.append(getIsActive());

		return sb.toString();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String name) {
		this.city = name;
	}

	public Address(Integer aId, Integer aOrganizationID) {
		setId(aId);
		setOrganizationID(aOrganizationID);
	}

	public Address(Integer aId) {
		setId(aId);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAddress3() {
		return address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public String getAddress4() {
		return address4;
	}

	public void setAddress4(String address4) {
		this.address4 = address4;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public Integer getOrganizationID() {
		return organizationID;
	}

	public void setOrganizationID(Integer organizationID) {
		this.organizationID = organizationID;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

}
