package com.vtb.domain;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * лимит из CRM
 * @author Andrey Pavlenko
 *
 */
public class CRMLimit extends VtbObject {
	private static final long serialVersionUID = -7121278709244656751L;

	private String limitid;//номер лимита в CRM
	private Date createDate;//дата создания
	private String managerlogin;//логин менеджера в системе
	private String limitname;//название лимита
	private BigDecimal sum;//сумма лимита
	private String currencycode;//код валюты
	private String currencylist;//список кодов допустимых валют через запятую
	private boolean islimit;//лимит или саблимит
	private String parentlimitid;//родитель саблимита
	private String limit_vid;//вид лимита
	private String status;//статус
	private String code;//номер лимита
	private ArrayList<String> orglist=new ArrayList<String>();//список id организаций
	private String organisationFormated;
	private String userName="";
	public CRMLimit(String limitid) {
		super();
		this.limitid = limitid;
	}
	/**
	 * @return номер лимита в CRM
	 */
	public String getLimitid() {
		return limitid;
	}
	/**
	 * @param номер лимита в CRM
	 */
	public void setLimitid(String limitid) {
		this.limitid = limitid;
	}
	/**
	 * @return дата создания
	 */
	public Date getCreateDate() {
		return createDate;
	}
	public String getCreateDateFormated() {
		SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
		return df.format(createDate);
	}
	/**
	 * @param дата создания
	 */
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	/**
	 * @return логин менеджера в системе
	 */
	public String getManagerlogin() {
		return managerlogin;
	}
	/**
	 * @param логин менеджера в системе
	 */
	public void setManagerlogin(String managerlogin) {
		this.managerlogin = managerlogin;
	}
	/**
	 * @return название лимита
	 */
	public String getLimitname() {
		return limitname;
	}
	/**
	 * @param название лимита
	 */
	public void setLimitname(String limitname) {
		this.limitname = limitname;
	}
	/**
	 * @return сумма лимита
	 */
	public BigDecimal getSum() {
		return sum;
	}
	public String getSumFormated(){
		DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("ru", "RU", ""));
		DecimalFormat decFormat = new DecimalFormat("###,###,###,###.##", symbols);
		return decFormat.format(sum);
	}
	/**
	 * @param сумма лимита
	 */
	public void setSum(BigDecimal sum) {
		this.sum = sum;
	}
	/**
	 * @return код валюты
	 */
	public String getCurrencycode() {
		return currencycode;
	}
	/**
	 * @param код валюты
	 */
	public void setCurrencycode(String currencycode) {
		this.currencycode = currencycode;
	}
	/**
	 * @return список кодов допустимых валют через запятую
	 */
	public String getCurrencylist() {
		return currencylist;
	}
	/**
	 * @param список кодов допустимых валют через запятую
	 */
	public void setCurrencylist(String currencylist) {
		this.currencylist = currencylist;
	}
	/**
	 * @return лимит или саблимит
	 */
	public boolean isIslimit() {
		return islimit;
	}
	/**
	 * @param лимит или саблимит
	 */
	public void setIslimit(boolean islimit) {
		this.islimit = islimit;
	}
	/**
	 * @return вид лимита
	 */
	public String getLimit_vid() {
		return limit_vid==null?"":limit_vid;
	}
	/**
	 * @param вид лимита
	 */
	public void setLimit_vid(String limit_vid) {
		this.limit_vid = limit_vid;
	}
	/**
	 * @return статус
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param статус
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return номер лимита
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param номер лимита
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @return список id организаций
	 */
	public ArrayList<String> getOrglist() {
		return orglist;
	}
	public String getOrganisationFormated() {
		return organisationFormated;
	}
	/**
	 * @param список id организацийt
	 */
	public void setOrglist(ArrayList<String> orglist) {
		this.orglist = orglist;
	}
	/**
	 * @param organisationFormated the organisationFormated to set
	 */
	public void setOrganisationFormated(String organisationFormated) {
		this.organisationFormated = organisationFormated;
	}
	/**
	 * @return the parentlimitid
	 */
	public String getParentlimitid() {
		return parentlimitid;
	}
	/**
	 * @param parentlimitid the parentlimitid to set
	 */
	public void setParentlimitid(String parentlimitid) {
		this.parentlimitid = parentlimitid;
	}
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
}
