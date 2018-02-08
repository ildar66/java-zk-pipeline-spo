package com.vtb.domain;

/**
 * VtbObject "Адресс клиента из системы CRM".
 * 
 * @author IShafigullin
 * 
 */
public class SpoAddress extends VtbObject {

	private static final long serialVersionUID = 1L;

	private String addressID = null;// id
	private String city = null; // Город
	private String country = null; // Страна
	private String county = null; // Округ, область, штат
	private String postalCode = null; // Индекс
	private String state = null; // Регион/область
	private String description = null; // ’юридический’/’фактический’

	/**
	 * 
	 * @param accountID
	 */
	public SpoAddress(String addressID) {
		super();
		this.addressID = addressID;
	}

	@Override
	public boolean equals(Object anObject) {
		if (anObject == null) {
			return false;
		}
		if (!(anObject instanceof SpoAddress)) {
			return false;
		}
		SpoAddress aSpoAccount = (SpoAddress) anObject;
		return aSpoAccount.getAddressID().equals(getAddressID());
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("SpoAccount: ");
		sb
				.append(getAddressID() + "(city=" + getCity() + ", country=" + getCountry() + ", county=" + getCounty()
						+ ")");
		// sb.append('\n');
		// sb.append(" IsActive: ");
		// sb.append(getIsActive());

		return sb.toString();
	}

	public String getAddressID() {
		return addressID;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
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

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
