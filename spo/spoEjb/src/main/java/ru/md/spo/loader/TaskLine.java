package ru.md.spo.loader;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.md.pup.dbobjects.RoleJPA;
import ru.md.pup.dbobjects.UserJPA;
import ru.md.spo.ejb.PupFacadeLocal;

import com.vtb.exception.FactoryException;
import com.vtb.util.ApplProperties;

public class TaskLine {
	private static final Logger LOGGER = LoggerFactory.getLogger(TaskLine.class.getName());
	private String typeList;
	private Long idProcess = 0l;
	private String numberZ = "0";
	private String numberpup;
	private String trClass = "";
	private String url = "#";
	private long idTask = 0;
	private Long idTaskDepartment = new Long(0);
	private Long idMDTask = null;
	private String contractors = "";
	private String group = "";
	private BigDecimal sum;
	private String currency;
	private HashMap<UserJPA, List<RoleJPA>> assignedUsers = new HashMap<UserJPA, List<RoleJPA>>();
	private String department = "";
	private String descriptionProcess = "";
	private String nameStageTo;
	private String dateOfTakingStr;
	private String dateOfMustCompleteStr;
	private String idUser;
	private String priority="";
	private String status="";
	private String processType="";

	private String nameIspoln = "";
	private String statusReturnType = "";
	private boolean isShowEditConditionLink;
	private String version;
	private Long idTypeProcess = null;
	private Long incomplete;
	private boolean isAccessImported = false;
	private boolean isBmImported = false;
	private boolean favorite;

	public TaskLine(String typeList) {
		super();
		this.typeList = typeList;
		isShowEditConditionLink = false;
	}

	public Long getIdProcess() {
		return idProcess;
	}

	public void setIdProcess(Long idProcess) {
		this.idProcess = idProcess;
	}

	public String getNumberZ() {
		return numberZ;
	}

	/**
	 * Возвращает url на отчет Активные операции
	 */
	public String getActiveStageUrl() {
		String report = "file:///" + ApplProperties.getReportsPath() + "Audit/active_stages.rptdesign";
		return "reportPrintFormRenderAction.do?__format=html&notused=off&__report=" + report + "&isDelinquency=-1&correspondingDeps=on"
				+ "&p_idDepartment=-1&id_ClaimFromList=" + numberpup + "&mdtaskId=" + getIdMDTask();
	}

	public void setNumberZ(String numberZ) {
		this.numberZ = numberZ;
	}

	/**
	 * url на карточку заявки.
	 */
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getIdTask() {
		return idTask;
	}

	public void setIdTask(long idTask) {
		this.idTask = idTask;
	}

	public String getContractors() {
		return contractors;
	}

	public void setContractors(String contractors) {
		this.contractors = contractors;
	}

	public String getSum() {
		if (sum == null)
			return "";
		DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("ru", "RU", ""));
		DecimalFormat decFormat = new DecimalFormat("###,###,###,###.##", symbols);
		return decFormat.format(sum);
	}

	public BigDecimal getSumOrig() {
		return this.sum;
	}

	public void setSum(BigDecimal sum) {
		this.sum = sum;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getDepartment() {
		if (department == null)
			return "не установлено подразделение!!!";
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getDescriptionProcess() {
		return descriptionProcess;
	}

	public void setDescriptionProcess(String descriptionProcess) {
		this.descriptionProcess = descriptionProcess;
	}

	public String getNameStageTo() {
		return nameStageTo;
	}

	public void setNameStageTo(String nameStageTo) {
		this.nameStageTo = nameStageTo;
	}

	public String getDateOfMustCompleteStr() {
		return dateOfMustCompleteStr;
	}

	public void setDateOfMustCompleteStr(String dateOfMustCompleteStr) {
		this.dateOfMustCompleteStr = dateOfMustCompleteStr;
	}

	public String getIdUser() {
		return idUser;
	}

	public void setIdUser(String idUser) {
		this.idUser = idUser;
	}

	public String getTrClass() {
		return trClass;
	}

	public void setTrClass(String trClass) {
		this.trClass = trClass;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getProcessType() {
		if (isAccessImported)
			return processType+" из access";
		if (isBmImported)
			return processType+" из БМ";
		return processType;
	}

	public void setProcessType(String processType) {
		this.processType = processType;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getPriority() {
		return priority;
	}

	/**
	 * @return the numberpup
	 */
	public String getNumberpup() {
		return numberpup;
	}

	/**
	 * @param numberpup the numberpup to set
	 */
	public void setNumberpup(String numberpup) {
		this.numberpup = numberpup;
	}

	/**
	 * @return the idMDTask
	 */
	public Long getIdMDTask() {
		return idMDTask;
	}

	/**
	 * @param idMDTask the idMDTask to set
	 */
	public void setIdMDTask(Long idMDTask) {
		this.idMDTask = idMDTask;
	}

	/**
	 * @return the opportunity
	 */
	public boolean isOpportunity() {
		return this.processType.equalsIgnoreCase("сделка");
	}

	/**
	 * @return idTaskDepartment
	 */
	public Long getIdTaskDepartment() {
		return idTaskDepartment;
	}

	/**
	 * @param idTaskDepartment idTaskDepartment
	 */
	public void setIdTaskDepartment(Long idTaskDepartment) {
		this.idTaskDepartment = idTaskDepartment;
	}

	public String getDateOfTakingStr() {
		return dateOfTakingStr;
	}

	public void setDateOfTakingStr(String dateOfTakingStr) {
		this.dateOfTakingStr = dateOfTakingStr;
	}

	public String getNameIspoln() {
		return nameIspoln;
	}

	public void setNameIspoln(String nameIspoln) {
		this.nameIspoln = nameIspoln;
	}

	private boolean showDealConclusionCreateLink = false;// показывать кнопку Создать запрос на
																												// заключение КОД
	private boolean showPreDealConclusionCreateLink = false;// показывать кнопку Создать запрос на
																													// предварительное оформление КОД

	public boolean isShowDealConclusionCreateLink() {
		return showDealConclusionCreateLink;
	}

	public void setShowDealConclusionCreateLink(boolean showDealConclusionCreateLink) {
		this.showDealConclusionCreateLink = showDealConclusionCreateLink;
	}

	public boolean isShowPreDealConclusionCreateLink() {
		return showPreDealConclusionCreateLink;
	}

	public void setShowPreDealConclusionCreateLink(boolean showPreDealConclusionCreateLink) {
		this.showPreDealConclusionCreateLink = showPreDealConclusionCreateLink;
	}

	private boolean showFundingCreateLink = false;// показывать кнопку создать заявку на фондирование

	public boolean isShowFundingCreateLink() {
		return showFundingCreateLink;
	}

	public void setShowFundingCreateLink(boolean b) {
		showFundingCreateLink = b;
	}

	private boolean showFundingCreateN6Link = false;// показывать кнопку создать заявку на N6

	public boolean isShowFundingCreateN6Link() {
		return showFundingCreateN6Link;
	}

	public void setShowFundingCreateN6Link(boolean b) {
		showFundingCreateN6Link = b;
	}

	/**
	 * @return assignedUsers
	 */
	public HashMap<UserJPA, List<RoleJPA>> getAssignedUsers() {
		return assignedUsers;
	}

	public String getAssignedMessage() {
		if (getAssignedUsers().size() == 0)
			return "";
		String res = "";
		for (UserJPA user : getAssignedUsers().keySet()) {
			String roles = "Назначен по роли ";
			for (RoleJPA role : getAssignedUsers().get(user)) {
				roles += role.getNameRole() + "; ";
			}
			res += "<acronym title=\"" + roles + "\">" + user.getFullName() + "</acronym><br />";
		}
		return res + "<br />";
	}

	/**
	 * @param assignedUsers assignedUsers
	 */
	public void setAssignedUsers(HashMap<UserJPA, List<RoleJPA>> assignedUsers) {
		this.assignedUsers = assignedUsers;
	}

	private boolean hasFunds = false;
	private boolean hasN6 = false;

	public void setHasFunds(boolean b) {
		hasFunds = b;
	}

	public void setHasN6(boolean b) {
		hasN6 = b;
	}

	public boolean isHasFunds() {
		return hasFunds;
	}

	public boolean isHasN6() {
		return hasN6;
	}

	public boolean isShowCed(HttpServletRequest request) throws FactoryException {
		return isOpportunity()
				&& (request.getParameter("projectteam") != null || request.getParameter("closed") != null
						&& getStatusReturnType().equals("1")) && isHasCed(idMDTask);
	}

	public static boolean isHasCed(Long mdtaskid) {
		try {
			PupFacadeLocal pup = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
			return pup.isHasCed(mdtaskid);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return false;
		}
	}

	/**
	 * @return Показывать ли кнопку взять в работу
	 */
	public boolean isShowAcceptButton() {
		if (typeList.equals("accept") || typeList.equals("all"))
			return false;// уже в работе и все заявки, не показываем
		if (typeList.equals("perform"))
			return true;// в назначенных всегда показываем, даже если могу переназначить
		// заявки точно в ожидающих
		if (getAssignedUsers().size() > 0)
			return false;// заявка у кого-то в назначенных мне
		return true;// Если есть кого назначить, то всё равно покажем. Так как это долго проверять и
								// будем проверять аяксом
	}

	public String getStatusReturnType() {
		if (statusReturnType == null)
			return "";
		return statusReturnType;
	}

	public void setStatusReturnType(String statusReturnType) {
		this.statusReturnType = statusReturnType;
	}

	/**
	 * Возвращает boolean признак наличия незавершенных версий
	 *
	 * @return boolean признак наличия незавершенных версий
	 */
	public boolean isShowEditConditionLink() {
		return isShowEditConditionLink;
	}

	/**
	 * Устанавливает boolean признак наличия незавершенных версий
	 *
	 * @param isShowEditConditionLink boolean признак наличия незавершенных версий
	 */
	public void setShowEditConditionLink(boolean isShowEditConditionLink) {
		this.isShowEditConditionLink = isShowEditConditionLink;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Long getIdTypeProcess() {
		return idTypeProcess;
	}

	public void setIdTypeProcess(Long idTypeProcess) {
		this.idTypeProcess = idTypeProcess;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public Long getIncomplete() {
		return incomplete;
	}

	public void setIncomplete(Long incomplete) {
		this.incomplete = incomplete;
	}

	public void setImportedBm(boolean isImported) {
		this.isBmImported = isImported;
	}
	public void setImportedAccess(boolean isImported) {
		this.isAccessImported = isImported;
	}

	/**
     * Возвращает признак избранной заявки
     * @return <code><b>true</b></code> если это избранная заявка
     */
    public boolean isFavorite() {
        return favorite;
    }
    
    /**
     * Устанавливает признак избранной заявки
     * @param favorite признак избранной заявки
     */
    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
