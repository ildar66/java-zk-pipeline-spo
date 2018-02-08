package ru.md.domain;

import java.util.Date;

/**
 * История места проведения сделки
 * @author Sergey Lysenkov
 */
public class PlaceHistory {
	private Long idmdtask;
	private Long idOldPlace;
	private String oldPlaceName;
	private Long idNewPlace;
	private String newPlaceName;
	private Long idPerformer;
	private String performerLogin;
	private String performerName;
	private Date changeDate;
	private String version;
	
	public Long getIdmdtask() {
		return idmdtask;
	}
	public void setIdmdtask(Long idmdtask) {
		this.idmdtask = idmdtask;
	}
	public Long getIdOldPlace() {
		return idOldPlace;
	}
	public void setIdOldPlace(Long idOldPlace) {
		this.idOldPlace = idOldPlace;
	}
	public String getOldPlaceName() {
		if (oldPlaceName == null || oldPlaceName.isEmpty())
			return "не определено";
		return oldPlaceName;
	}
	public void setOldPlaceName(String oldPlaceName) {
		this.oldPlaceName = oldPlaceName;
	}
	public Long getIdNewPlace() {
		return idNewPlace;
	}
	public void setIdNewPlace(Long idNewPlace) {
		this.idNewPlace = idNewPlace;
	}
	public String getNewPlaceName() {
		return newPlaceName;
	}
	public void setNewPlaceName(String newPlaceName) {
		this.newPlaceName = newPlaceName;
	}
	public Long getIdPerformer() {
		return idPerformer;
	}
	public void setIdPerformer(Long idPerformer) {
		this.idPerformer = idPerformer;
	}
	public String getPerformerLogin() {
		return performerLogin;
	}
	public void setPerformerLogin(String performerLogin) {
		this.performerLogin = performerLogin;
	}
	public String getPerformerName() {
		return performerName;
	}
	public void setPerformerName(String performerName) {
		this.performerName = performerName;
	}
	public Date getChangeDate() {
		return changeDate;
	}
	public void setChangeDate(Date changeDate) {
		this.changeDate = changeDate;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
}
