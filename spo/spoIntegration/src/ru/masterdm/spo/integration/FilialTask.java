package ru.masterdm.spo.integration;

import java.io.Serializable;
import java.util.ArrayList;

public class FilialTask implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long idPupTask;
	private Long idMdTask;
	private String number;//номер заявки
	private String mainContractorName;//основной заемщик
	private Double sum;//сумма
	private String cur;//валюта
	private String productTypeName;//вид продукта
	private ArrayList<String> contractors;//названия контрегентов
	private Long period;//срок в днях
	private boolean canCloseTask=false;//можно закрыть заявку
	private boolean canEditTask=false;//можно редактировать заявку
	private String whoWork="";//кто работает над заявкой в данный момент
	
	public FilialTask(Long idPupTask, Long idMdTask) {
		super();
		this.idPupTask = idPupTask;
		this.idMdTask = idMdTask;
	}
	public Long getIdPupTask() {
		return idPupTask;
	}
	public void setIdPupTask(Long idPupTask) {
		this.idPupTask = idPupTask;
	}
	public Long getIdMdTask() {
		return idMdTask;
	}
	public void setIdMdTask(Long idMdTask) {
		this.idMdTask = idMdTask;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getMainContractorName() {
		return mainContractorName;
	}
	public void setMainContractorName(String mainContractorName) {
		this.mainContractorName = mainContractorName;
	}
	public Double getSum() {
		return sum;
	}
	public void setSum(Double sum) {
		this.sum = sum;
	}
	public String getCur() {
		return cur;
	}
	public void setCur(String cur) {
		this.cur = cur;
	}
	public String getProductTypeName() {
		return productTypeName;
	}
	public void setProductTypeName(String productTypeName) {
		this.productTypeName = productTypeName;
	}
	public ArrayList<String> getContractors() {
		return contractors;
	}
	public void setContractors(ArrayList<String> contractors) {
		this.contractors = contractors;
	}
	public Long getPeriod() {
		return period;
	}
	public void setPeriod(Long period) {
		this.period = period;
	}
	
	/**
	 * @return the можно закрыть эту заявку
	 */
	public String getBoolean() {
		return "canCloseTask="+canCloseTask+", canEditTask="+canEditTask;
	}
	public boolean isCanCloseTask() {
		return canCloseTask;
	}
	/**
	 * @param canCloseTask можно закрыть эту заявку
	 */
	public void setCanCloseTask(boolean canCloseTask) {
		this.canCloseTask = canCloseTask;
	}
	/**
	 * @return the можно редактировать эту заявку
	 */
	public boolean isCanEditTask() {
		return canEditTask;
	}
	/**
	 * @param canEditTask можно редактировать эту заявку
	 */
	public void setCanEditTask(boolean canEditTask) {
		this.canEditTask = canEditTask;
	}
	@Override
	public String toString() {
		return "FilialTask [number=" + number + ", mainContractorName="
				+ mainContractorName + "]";
	}
	/**
	 * @return кто работает над заявкой в данный момент
	 */
	public String getWhoWork() {
		return whoWork;
	}
	/**
	 * @param whoWork кто работает над заявкой в данный момент
	 */
	public void setWhoWork(String whoWork) {
		this.whoWork = whoWork;
	}
	
}
