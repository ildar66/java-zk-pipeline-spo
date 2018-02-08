package ru.masterdm.spo.integration;

import java.io.Serializable;

public class FilialTaskListFilter implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long pageNum;// индекс страницы. Нумерация начинается с 0.
	private Long pageSize;//размер страницы
	
	private String taskNumber;//№ сделки
	private String orgName;//	Наименование Клиента
	private Double sumFrom;//	Сумма от
	private Double sumTo;//	Сумма до
	private String cur;//	Валюта
	private String productName;//	Вид сделки
	private boolean hideClosed=true;//	Скрыть закрытые сделки
	private String userName=null;//	ФИО инициатора сделки(пользователя создавшего сделку)
	/**
	 * индекс страницы. Нумерация начинается с 0.
	 */
	public Long getPageNum() {
		return pageNum;
	}
	/**
	 * индекс страницы. Нумерация начинается с 0.
	 */
	public void setPageNum(Long pageNum) {
		this.pageNum = pageNum;
	}
	public Long getPageSize() {
		return pageSize;
	}
	public Long getMinRowToTetch(){
		if(pageSize==null || pageNum==null)
			return 0L;
		return pageSize*pageNum+1;
	}
	public Long getMaxRowToTetch(){
		if(pageSize==null || pageNum==null)
			return 0L;
		return pageSize*(pageNum+1);
	}
	public void setPageSize(Long pageSize) {
		this.pageSize = pageSize;
	}
	public String getTaskNumber() {
		return taskNumber;
	}
	public void setTaskNumber(String taskNumber) {
		this.taskNumber = taskNumber;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public Double getSumFrom() {
		return sumFrom;
	}
	public void setSumFrom(Double sumFrom) {
		this.sumFrom = sumFrom;
	}
	public Double getSumTo() {
		return sumTo;
	}
	public void setSumTo(Double sumTo) {
		this.sumTo = sumTo;
	}
	public String getCur() {
		return cur;
	}
	public void setCur(String cur) {
		this.cur = cur;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public boolean isHideClosed() {
		return hideClosed;
	}
	public void setHideClosed(boolean hideClosed) {
		this.hideClosed = hideClosed;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
}
