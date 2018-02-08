package com.vtb.domain;

import ru.masterdm.compendium.domain.crm.Organization;
import ru.masterdm.compendium.domain.spo.Person;

import java.math.BigDecimal;
import java.util.Date;

public abstract class  AbstractSupply extends VtbObject{
	private static final long serialVersionUID = 1L;
	private boolean main;//основное обеспечение
	private boolean posled;//Послед. залог
	private DepositorFinStatus depositorFinStatus;// Фин. Состояние
	private LiquidityLevel liquidityLevel;// Категория обеспечения
	private SupplyType ob;//Группа обеспечения
	private Double supplyvalue;//Степень обеспечения
	private Date fromdate;//с даты
	private Date todate;//по дату
    private Long period;  // срок действия ВИДА сделки
    private String periodDimension; //размерность срока
	private Organization org;// гарант юрлицо
	private Person person;//Физическое лицо
	/**
	 * возвращает сумму залога в валюте сделки
	 * @param exchangeRate курс валюты сделки к рублю
	 * @param mainSum сумма сделки в рублях
	 * @return
	 */
	public abstract BigDecimal getRating_zalog(BigDecimal exchangeRate, BigDecimal mainSum);
	/**
	 * Возвращает код типа обеспечения
	 * @return
	 */
	public abstract String getSupplyType();
	public abstract String getSupplyTypeName();
	/**
	 * @return основное обеспечение
	 */
	public boolean isMain() {
		return main;
	}
	/**
	 * @param main основное обеспечение
	 */
	public void setMain(boolean main) {
		this.main = main;
	}
	/**
	 * @return Фин. Состояние
	 */
	public DepositorFinStatus getDepositorFinStatus() {
		return depositorFinStatus;
	}
	/**
	 * @param depositorFinStatus Фин. Состояние
	 */
	public void setDepositorFinStatus(DepositorFinStatus depositorFinStatus) {
		this.depositorFinStatus = depositorFinStatus;
	}
	/**
	 * @return Категория обеспечения
	 */
	public LiquidityLevel getLiquidityLevel() {
		return liquidityLevel;
	}
	/**
	 * @param liquidityLevel Категория обеспечения
	 */
	public void setLiquidityLevel(LiquidityLevel liquidityLevel) {
		this.liquidityLevel = liquidityLevel;
	}
	/**
	 * @return Группа обеспечения
	 */
	public SupplyType getOb() {
		return ob;
	}
	/**
	 * @param ob Группа обеспечения
	 */
	public void setOb(SupplyType ob) {
		this.ob = ob;
	}
	public Double getSupplyvalue() {
		return supplyvalue;
	}
	public void setSupplyvalue(Double supplyvalue) {
		this.supplyvalue = supplyvalue;
	}
	public Date getFromdate() {
		return fromdate;
	}
	public void setFromdate(Date fromdate) {
		this.fromdate = fromdate;
	}
	public Date getTodate() {
		return todate;
	}
	public java.sql.Date getTodateSQL() {
		if(todate==null)
			return null;
		return new java.sql.Date(todate.getTime());
	}
	public void setTodate(Date todate) {
		this.todate = todate;
	}
	/**
	 * @return the period
	 */
	public Long getPeriod() {
		return period;
	}
	public String getPeriodFormated() {
		if(period==null)
			return "";
		return period.toString();
	}
	/**
	 * @param period the period to set
	 */
	public void setPeriod(Long period) {
		this.period = period;
	}
	/**
	 * @return the periodDimension
	 */
	public String getPeriodDimension() {
		return periodDimension==null?"":periodDimension;
	}
	/**
	 * @param periodDimension the periodDimension to set
	 */
	public void setPeriodDimension(String periodDimension) {
		this.periodDimension = periodDimension;
	}
	public boolean isPosled() {
		return posled;
	}
	public void setPosled(boolean posled) {
		this.posled = posled;
	}
	/**
	 * @return гарант юрлицо
	 */
	public Organization getOrg() {
		return org;
	}
	/**
	 * @param org гарант юрлицо
	 */
	public void setOrg(Organization org) {
		this.org = org;
	}
	/**
	 * @return Физическое лицо
	 */
	public Person getPerson() {
		return person;
	}
	/**
	 * @param person Физическое лицо
	 */
	public void setPerson(Person person) {
		this.person = person;
	}
	public String getContractorName(){
		if(getPerson()!=null && getPerson().getLastName()!=null)
			return getPerson().getLastName();
		return getOrg().getAccount_name();
	}
}
