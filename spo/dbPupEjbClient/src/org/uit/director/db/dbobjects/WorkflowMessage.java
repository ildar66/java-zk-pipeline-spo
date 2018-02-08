package org.uit.director.db.dbobjects;

import java.util.Date;

/**
 * Created by IntelliJ IDEA. User: pd190390 Date: 23.12.2005 Time: 9:51:23 To
 * change this template use File | Settings | File Templates.
 */
public class WorkflowMessage extends WorkflowObject {

	Long idMessage;
	Date dateMessage;
	String textMessage;
	Long idAttach;
	Long idProcessFrom;
	Long idRoleFrom;
	Long idStageFrom;
	Long idUserFrom;
	Integer idTypeProcessTo;
	Long idProcessTo;
	Long idRoleTo;
	Long idStageTo;
	Long idUserTo;	

	public Date getDateMessage() {
		return dateMessage;
	}

	public void setDateMessage(Date dateMessage) {
		this.dateMessage = dateMessage;
	}

	public Long getIdAttach() {
		return idAttach;
	}

	public void setIdAttach(Long idAttach) {
		this.idAttach = idAttach;
	}

	public Long getIdMessage() {
		return idMessage;
	}

	public void setIdMessage(Long idMessage) {
		this.idMessage = idMessage;
	}

	public Long getIdProcessFrom() {
		return idProcessFrom;
	}

	public void setIdProcessFrom(Long idProcessFrom) {
		this.idProcessFrom = idProcessFrom;
	}

	public Long getIdProcessTo() {
		return idProcessTo;
	}

	public void setIdProcessTo(Long idProcessTo) {
		this.idProcessTo = idProcessTo;
	}

	public Long getIdRoleFrom() {
		return idRoleFrom;
	}

	public void setIdRoleFrom(Long idRoleFrom) {
		this.idRoleFrom = idRoleFrom;
	}

	public Long getIdRoleTo() {
		return idRoleTo;
	}

	public void setIdRoleTo(Long idRoleTo) {
		this.idRoleTo = idRoleTo;
	}

	public Long getIdStageFrom() {
		return idStageFrom;
	}

	public void setIdStageFrom(Long idStageFrom) {
		this.idStageFrom = idStageFrom;
	}

	public Long getIdStageTo() {
		return idStageTo;
	}

	public void setIdStageTo(Long idStageTo) {
		this.idStageTo = idStageTo;
	}

	public Integer getIdTypeProcessTo() {
		return idTypeProcessTo;
	}

	public void setIdTypeProcessTo(Integer idTypeProcessTo) {
		this.idTypeProcessTo = idTypeProcessTo;
	}

	public Long getIdUserFrom() {
		return idUserFrom;
	}

	public void setIdUserFrom(Long idUserFrom) {
		this.idUserFrom = idUserFrom;
	}

	public Long getIdUserTo() {
		return idUserTo;
	}

	public void setIdUserTo(Long idUserTo) {
		this.idUserTo = idUserTo;
	}

	public String getTextMessage() {
		return textMessage;
	}

	public void setTextMessage(String textMessage) {
		this.textMessage = textMessage;
	}

	protected WorkflowMessage(Long id, String name) {
		super(id, name);
		// TODO Auto-generated constructor stub
	}

	public WorkflowMessage() {
		super((long)0, "");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Object getData(String field) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
