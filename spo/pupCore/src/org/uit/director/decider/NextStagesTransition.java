/*
 * Created on 26.09.2008
 * 
 */
package org.uit.director.decider;

import org.uit.director.decider.NextStagesInfo.Statuses;

public class NextStagesTransition {

	Long idStage;
	String nameStage;
	Statuses status;
	boolean isLate;
	public boolean isAutoStage;

	TransitionAction action;
	String message = "";
	Long idDepartament = null;

	public boolean isAutoStage() {
		return isAutoStage;
	}

	public void setAutoStage(boolean isAutoStage) {
		this.isAutoStage = isAutoStage;
	}

	public Long getIdStage() {
		return idStage;
	}

	public boolean isLate() {
		return isLate;
	}

	public String getNameStage() {
		return nameStage;
	}

	public Statuses getStatus() {
		return status;
	}

	public void setIdStage(Long idStage) {
		this.idStage = idStage;
	}

	public void setLate(boolean isLate) {
		this.isLate = isLate;
	}

	public void setNameStage(String nameStage) {
		this.nameStage = nameStage;
	}

	public void setStatus(Statuses status) {
		this.status = status;
	}

	public TransitionAction getAction() {
		return action;
	}

	public void setAction(TransitionAction action) {
		this.action = action;
	}

	public String getMessage() {

		if (message.equals("")) {
			StringBuffer sb = new StringBuffer();

			switch (getStatus()) {
			case COMPLETE:
				sb.append(
					"<td>является заключительным. После подтверждения действия процесс завершится.")
					.append("(").append(isLate ? "поздняя" : "мгновенная")
					.append(" отправка)  </td>");

				break;

			case SEND:
				sb.append("<td>будет передано на операцию: <strong>").append(
						getNameStage()).append("</strong>")
						.append(" (").append(isLate ? "поздняя" : "мгновенная").append(" отправка)  </td>");
				break;

			case SEND_SUB_PROCESS:
				sb.append("<td>будет передано на подпроцесс: <strong>").append(
						getNameStage()).append("</strong>").append(" (")
						.append(isLate ? "поздняя" : "мгновенная").append(
								" отправка)  </td>");
				break;
			}
			message = sb.toString();
		}
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Long getIdDepartament() {
		return idDepartament;
	}

	public void setIdDepartament(Long idDepartament) {
		this.idDepartament = idDepartament;
	}
	

}
