package com.vtb.domain;

import java.sql.Date;

/**
 * "Контакты для организации" VtbObject.
 * 
 * @author Admin
 * 
 */
@Deprecated
public class Manager extends VtbObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id = null;// идентификатор СПО
	private String reason = null;// На основании чего действует
	private Integer organizationID = null; // внешний ключ на организацию
	private Date birthDay = null;// Год рождения
	private String lastName = null;// Фамилия
	private String firstName = null; // Имя
	private String middleName = null; // Отчество
	private String alumni = null;// ВУЗ
	private String title = null;// Должность
	/**
	 * внешний ключ на адрес. Один адрес создается для одной организации У
	 * адреса поле IDORGANISATION должно быть null.
	 */
	private Address address = null;

	public Manager(Integer id) {
		super();
		this.id = id;
	}

	@Override
	public boolean equals(Object anObject) {
		if (anObject == null) {
			return false;
		}
		if (!(anObject instanceof Manager)) {
			return false;
		}
		Manager aManager = (Manager) anObject;
		return aManager.getId().equals(getId());
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Manager: ");
		sb.append(getId() + "(" + getLastName() + ")");
		// sb.append('\n');
		// sb.append(" IsActive: ");
		// sb.append(getIsActive());

		return sb.toString();
	}

	public String getAlumni() {
		return alumni;
	}

	public void setAlumni(String alumni) {
		this.alumni = alumni;
	}

	public Date getBirthDay() {
		return birthDay;
	}

	public void setBirthDay(Date birthDay) {
		this.birthDay = birthDay;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public Integer getOrganizationID() {
		return organizationID;
	}

	public void setOrganizationID(Integer organizationID) {
		this.organizationID = organizationID;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Address getAddress() {
		if(address == null){
			address = new Address(null);
		}
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

}
