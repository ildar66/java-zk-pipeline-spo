package com.vtb.domain;

import java.sql.Date;

/**
 * VtbObject "Информация о клиенте из системы CRM".
 * 
 * @author IShafigullin
 * 
 */
public class SpoAccount extends VtbObject {

	private static final long serialVersionUID = 1L;

	private String accountID = null;// id Клиента
	private String account = null;// Предприятие (полное наименование)
	private String industry = null;// Сфера деятельности/Отрасль
	private String corp_block =null;//Закрепление Клиента за Корпоративным блоком
	private String category=null;//Категория клиента
	/**
	 * Организационно-правовая форма собственности
	 */
	private String territory = null;

	/**
	 * Дата и регистрационный номер, наименование регистрирующего органа
	 */
	private String ogrn = null;// Регистрационный номер ОГРН
	private Date ogrnDate = null;// Дата
	private String ogrnPlace = null;// Место получения
	
	private String inn = null;// инн
	private String okpo = null;// ОКПО
	
	private SpoAddress address = null;// адресс клиента
	
	private String region = null; // филиал инициирующий заявку
	
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getAccountID() {
		return accountID;
	}
	public void setAccountID(String accountID) {
		this.accountID = accountID;
	}
	public String getIndustry() {
		return industry;
	}
	public void setIndustry(String industry) {
		this.industry = industry;
	}
	public String getInn() {
		return inn;
	}
	public void setInn(String inn) {
		this.inn = inn;
	}
	public String getOgrn() {
		return ogrn;
	}
	public void setOgrn(String ogrn) {
		this.ogrn = ogrn;
	}
	public Date getOgrnDate() {
		return ogrnDate;
	}
	public void setOgrnDate(Date ogrnDate) {
		this.ogrnDate = ogrnDate;
	}
	public String getOgrnPlace() {
		return ogrnPlace;
	}
	public void setOgrnPlace(String ogrnPlace) {
		this.ogrnPlace = ogrnPlace;
	}
	public String getOkpo() {
		return okpo;
	}
	public void setOkpo(String okpo) {
		this.okpo = okpo;
	}
	public String getTerritory() {
		return territory;
	}
	public void setTerritory(String territory) {
		this.territory = territory;
	}
	/**
	 * 
	 * @param accountID
	 */
	public SpoAccount(String accountID) {
		super();
		this.accountID = accountID;
	}
	

	@Override
	public boolean equals(Object anObject) {
		if (anObject == null) {
			return false;
		}
		if (!(anObject instanceof SpoAccount)) {
			return false;
		}
		SpoAccount aSpoAccount = (SpoAccount) anObject;
		return aSpoAccount.getAccountID().equals(getAccountID());
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("SpoAccount: ");
		sb.append(getAccountID() + "(account=" + getAccount() + ", industry=" + getIndustry()+ ", ogrnDate=" + getOgrnDate() + ")");
		// sb.append('\n');
		// sb.append(" IsActive: ");
		// sb.append(getIsActive());

		return sb.toString();
	}
	public SpoAddress getAddress() {
		return address;
	}
	public void setAddress(SpoAddress address) {
		this.address = address;
	}
	/**
	 * @return the corp_block
	 */
	public String getCorp_block() {
		return corp_block;
	}
	/**
	 * @param corp_block the corp_block to set
	 */
	public void setCorp_block(String corp_block) {
		this.corp_block = corp_block;
	}
	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}
	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}	
}
