package ru.masterdm.spo.integration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FilialTaskList implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String returnPortalServletUrl = "returnToPortal.html";
	public static final String portalUrl = "/cps/filialtaskspage";
	public static final String kodUrl = "/ced/pages/filialDealConclusion.jsf?action=CREATE&idCreditDeal=";
	private String userLogin;
	private Long totalCount=0L;//всего заявок по фильтру
	private boolean showCreateLink=false;
	private List<FilialTask> list=new ArrayList<FilialTask>();
	
	public String getLogin() {
		return userLogin;
	}
	public void setLogin(String message) {
		this.userLogin = message;
	}
	public List<FilialTask> getList() {
		return list;
	}
	/**
	 * всего заявок по фильтру
	 */
	public Long getTotalCount() {
		return totalCount;
	}
	/**
	 * всего заявок по фильтру
	 */
	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
	}
	/**
	 * @return показать ссылку "Создать сделку"
	 */
	public boolean isShowCreateLink() {
		return showCreateLink;
	}
	/**
	 * @param showCreateLink показать ссылку "Создать сделку"
	 */
	public void setShowCreateLink(boolean showCreateLink) {
		this.showCreateLink = showCreateLink;
	}
	
}
