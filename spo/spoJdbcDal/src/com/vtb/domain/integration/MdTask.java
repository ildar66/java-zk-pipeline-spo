package com.vtb.domain.integration;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Сделка СПО.
 * @author Andrey Pavlenko
 */
public class MdTask implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	private String number;
	private Date dateCreate;//дата создания
	private String numberSogl;//номер кредитного соглашения
	private Date dateSogl;//дата договора
	private String kind;//вид продукта
	private String regAcc;//всегда null
	private String cur;//валюта
	private BigDecimal sum;//сумма
	private String status;//статус сделки из мониторинга, 
	private Date statusAccepted;//дата решения, 
	private String statusWho;//кто решил, 
	private String statusNotes;//комментарий к решению
	private String taskUrl;//url на просмотр сделки
	
	private String decision;// решение
	private Date endDate; // срок окончания
	private String endPeriod; // тоже срок окончания
	
	public MdTask() {
		super();
	}
	/**
	 * Возвращает id_mdtask.
	 * @return idid_mdtask
	 */
	public Long getId() {
		return id;
	}
	/**
	 * Устанавливает id_mdtask.
	 * @param id id_mdtask
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * Возвращает отображаемый номер.
	 * @return отображаемый номер
	 */
	public String getNumber() {
		return number;
	}
	/**
	 * Устанавливает отображаемый номер.
	 * @param number отображаемый номер
	 */
	public void setNumber(String number) {
		this.number = number;
	}
	/**
	 * @param id id_mdtask
	 * @param number отображаемый номер
	 */
	public MdTask(Long id, String number) {
		super();
		this.id = id;
		this.number = number;
	}
	/**
	 * Возвращает дата создания.
	 * @return дата создания
	 */
	public Date getDateCreate() {
		return dateCreate;
	}
	/**
	 * Устанавливает дата создания.
	 * @param dateCreate дата создания
	 */
	public void setDateCreate(Date dateCreate) {
		this.dateCreate = dateCreate;
	}
	/**
	 * Возвращает номер кредитного соглашения.
	 * @return номер кредитного соглашения
	 */
	public String getNumberSogl() {
		return numberSogl;
	}
	/**
	 * Устанавливает номер кредитного соглашения.
	 * @param numberSogl номер кредитного соглашения
	 */
	public void setNumberSogl(String numberSogl) {
		this.numberSogl = numberSogl;
	}
	/**
	 * Возвращает дата договора.
	 * @return дата договора
	 */
	public Date getDateSogl() {
		return dateSogl;
	}
	/**
	 * Устанавливает дата договора.
	 * @param dateSogl дата договора
	 */
	public void setDateSogl(Date dateSogl) {
		this.dateSogl = dateSogl;
	}
	/**
	 * Возвращает вид продукта.
	 * @return вид продукта
	 */
	public String getKind() {
		return kind;
	}
	/**
	 * Устанавливает вид продукта.
	 * @param kind вид продукта
	 */
	public void setKind(String kind) {
		this.kind = kind;
	}
	/**
	 * Возвращает всегда null.
	 * @return всегда null
	 */
	public String getRegAcc() {
		return regAcc;
	}
	/**
	 * Устанавливает regAcc.
	 * @param regAcc regAcc
	 */
	public void setRegAcc(String regAcc) {
		this.regAcc = regAcc;
	}
	/**
	 * Возвращает валюта.
	 * @return валюта
	 */
	public String getCur() {
		return cur;
	}
	/**
	 * Устанавливает валюта.
	 * @param cur валюта
	 */
	public void setCur(String cur) {
		this.cur = cur;
	}
	/**
	 * Возвращает сумма.
	 * @return сумма
	 */
	public BigDecimal getSum() {
		return sum;
	}
	/**
	 * Устанавливает сумма.
	 * @param sum сумма
	 */
	public void setSum(BigDecimal sum) {
		this.sum = sum;
	}
	/**
	 * Возвращает статус сделки из мониторинга.
	 * @return статус сделки из мониторинга
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * Устанавливает статус сделки из мониторинга.
	 * @param status статус сделки из мониторинга
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * Возвращает дата решения.
	 * @return дата решения
	 */
	public Date getStatusAccepted() {
		return statusAccepted;
	}
	/**
	 * Устанавливает дата решения.
	 * @param statusAccepted дата решения
	 */
	public void setStatusAccepted(Date statusAccepted) {
		this.statusAccepted = statusAccepted;
	}
	/**
	 * Возвращает кто решил.
	 * @return кто решил
	 */
	public String getStatusWho() {
		return statusWho;
	}
	/**
	 * Устанавливает кто решил.
	 * @param statusWHo кто решил
	 */
	public void setStatusWho(String statusWho) {
		this.statusWho = statusWho;
	}
	/**
	 * Возвращает комментарий к решению.
	 * @return комментарий к решению
	 */
	public String getStatusNotes() {
		return statusNotes;
	}
	/**
	 * Устанавливает комментарий к решению.
	 * @param statusNotes комментарий к решению
	 */
	public void setStatusNotes(String statusNotes) {
		this.statusNotes = statusNotes;
	}
	/**
	 * Возвращает url на просмотр сделки.
	 * @return url на просмотр сделки
	 */
	public String getTaskUrl() {
		return taskUrl;
	}
	/**
	 * Устанавливает url на просмотр сделки.
	 * @param taskUrl url на просмотр сделки
	 */
	public void setTaskUrl(String taskUrl) {
		this.taskUrl = taskUrl;
	}
	/**
	 * @return решение
	 */
	public String getDecision() {
		return decision;
	}
	/**
	 * @param decision решение
	 */
	public void setDecision(String decision) {
		this.decision = decision;
	}
	/**
	 * @return срок окончания
	 */
	public Date getEndDate() {
		return endDate;
	}
	/**
	 * @param endDate срок окончания
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	/**
	 * @return тоже срок окончания
	 */
	public String getEndPeriod() {
		return endPeriod;
	}
	/**
	 * @param endPeriod тоже срок окончания
	 */
	public void setEndPeriod(String endPeriod) {
		this.endPeriod = endPeriod;
	}
	
}
