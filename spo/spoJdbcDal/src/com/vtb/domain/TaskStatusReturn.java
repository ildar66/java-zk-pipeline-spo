package com.vtb.domain;

import java.util.Date;

import ru.masterdm.compendium.domain.crm.StatusReturn;

public class TaskStatusReturn extends VtbObject {
	private static final long serialVersionUID = 1L;

	private StatusReturn statusReturn;
	private String statusReturnText;
	private Date dateReturn;
	private Long idUser;

   public TaskStatusReturn() {
        super();
    }
	
	public TaskStatusReturn(StatusReturn statusReturn, String statusReturnText) {
		super();
		this.statusReturn = statusReturn;
		this.statusReturnText = statusReturnText;
	}

	/**
	 * @return the statusReturn
	 */
	public StatusReturn getStatusReturn() {
		return statusReturn;
	}
	/**
	 * @param statusReturn the statusReturn to set
	 */
	public void setStatusReturn(StatusReturn statusReturn) {
		this.statusReturn = statusReturn;
	}
	/**
	 * @return the statusReturnText
	 */
	public String getStatusReturnText() {
		return statusReturnText==null?"":statusReturnText;
	}
	/**
	 * @param statusReturnText the statusReturnText to set
	 */
	public void setStatusReturnText(String statusReturnText) {
		this.statusReturnText = statusReturnText;
	}
	/**
	 * @return the dateReturn
	 */
	public Date getDateReturn() {
		return dateReturn;
	}
	/**
	 * @param dateReturn the dateReturn to set
	 */
	public void setDateReturn(Date dateReturn) {
		this.dateReturn = dateReturn;
	}

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }
	
}
