package ru.md.domain;

import java.util.Date;

/**
 * Организация. Юридическое лицо. Блок: Информация о лимите
 * @author Andrey Pavlenko
 */
public class ClientInfoLimit {

	private String id;
	private Long idMdtask;
	private String protocol;
	private String number;
	private String sublimit;
	private String decisionmaker;
	private Date decisionDate;
	private Date validtoDate;

	/**
	 * Returns .
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets .
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Returns .
	 * @return
	 */
	public Long getIdMdtask() {
		return idMdtask;
	}

	/**
	 * Sets .
	 * @param idMdtask
	 */
	public void setIdMdtask(Long idMdtask) {
		this.idMdtask = idMdtask;
	}

	/**
	 * Returns .
	 * @return
	 */
	public String getProtocol() {
		return protocol;
	}

	/**
	 * Sets .
	 * @param protocol
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	/**
	 * Returns .
	 * @return
	 */
	public String getSublimit() {
		return sublimit;
	}

	/**
	 * Sets .
	 * @param sublimit
	 */
	public void setSublimit(String sublimit) {
		this.sublimit = sublimit;
	}

	/**
	 * Returns .
	 * @return
	 */
	public String getDecisionmaker() {
		return decisionmaker;
	}

	/**
	 * Sets .
	 * @param decisionmaker
	 */
	public void setDecisionmaker(String decisionmaker) {
		this.decisionmaker = decisionmaker;
	}

	/**
	 * Returns .
	 * @return
	 */
	public Date getDecisionDate() {
		return decisionDate;
	}

	/**
	 * Sets .
	 * @param decisionDate
	 */
	public void setDecisionDate(Date decisionDate) {
		this.decisionDate = decisionDate;
	}

	/**
	 * Returns .
	 * @return
	 */
	public Date getValidtoDate() {
		return validtoDate;
	}

	/**
	 * Sets .
	 * @param validtoDate
	 */
	public void setValidtoDate(Date validtoDate) {
		this.validtoDate = validtoDate;
	}

	/**
	 * Returns .
	 * @return
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * Sets .
	 * @param number
	 */
	public void setNumber(String number) {
		this.number = number;
	}

}
