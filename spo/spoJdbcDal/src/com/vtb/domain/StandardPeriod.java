package com.vtb.domain;

import java.util.ArrayList;
import java.util.Date;

import com.vtb.util.CollectionUtils;
import com.vtb.util.Formatter;

public class StandardPeriod extends VtbObject {
    private static final long serialVersionUID = 1L;
    
    private Long mdtaskId;
    private Long stageId;//Этап
    private String stageName;//Этап
    private ArrayList<String> operationName;//Операции
    private Long standardPeriod;//нормативный срок в рабдн
    private Long factPeriod;//фактический срок в рабдн
    private Date start;
    private Date finish;
    private Date deadline;
    private ArrayList<String> user;//исполнители
    @Deprecated //всегда пусто
    private ArrayList<String> request;//запросы, отправляемые с формы заявки пользователями, выполняющими этап
    //ФИО отправителя, содержание запроса, дата и время, адресат
    @Deprecated //всегда пусто
    private ArrayList<String> document;//приклепленные документы пользователями, которым направлялся запрос
    //тип документа, название, дата и время
    private boolean canEditPeriod=false;
    private String criteria;//выбранный критерий дифференциации нормативного срока или коментарий изменения
    private ArrayList<String> eventsHistory;//История событий
    private ArrayList<String> changeHistory;//История изменений
    private ArrayList<String> comments;//Комментарии Экспертного подразделения
    //для аудитора
    private String lastCommentText="";
    private String lastCommentAuthor="";
    private String lastCommentAuthorDepartment="";
    
    public String getStageName() {
        return stageName;
    }
    
    public void setStageName(String stageName) {
        this.stageName = stageName;
    }
    
    public ArrayList<String> getOperationName() {
        return operationName;
    }
    
    public void setOperationName(ArrayList<String> operationName) {
        this.operationName = operationName;
    }
    
    public Long getStandardPeriod() {
        return standardPeriod;
    }
    public String getStandardPeriodDisplay() {
        if(standardPeriod==null)
            return "";
        return standardPeriod.toString();
    }
    public String getWhoChangeDisplay(){
    	if(changeHistory==null)
    		return "";
    	String res = "";
    	for (String change : changeHistory){
    		res += "<br /><br />"+change;
    	}
    	return res;
    }
    
    public void setStandardPeriod(Long standardPeriod) {
        this.standardPeriod = standardPeriod;
    }
    
    public Long getFactPeriod() {
        return factPeriod;
    }
    public String getFactPeriodDisplay() {
        if(start==null) return "Этап не начат";
        String res = " (" + Formatter.formatDateTime(start) + " - ";
        if(finish==null){
            res += "не завершён";
        } else {
            res += Formatter.formatDateTime(finish);
        }
        res += ")";
        if(factPeriod!=null)
            res = factPeriod.toString() + res;
        if(deadline!=null){
            res += " deadline: "+Formatter.formatDateTime(deadline);
        }
        return res;
    }
    
    public void setFactPeriod(Long factPeriod) {
        this.factPeriod = factPeriod;
    }
    
    public Date getStart() {
        return start;
    }
    
    public void setStart(Date start) {
        this.start = start;
    }
    
    public Date getFinish() {
        return finish;
    }
    
    public void setFinish(Date finish) {
        this.finish = finish;
    }
    
    public ArrayList<String> getUser() {
        return user;
    }
    
    public void setUser(ArrayList<String> user) {
        this.user = user;
    }

    public ArrayList<String> getRequest() {
        return request;
    }

    public void setRequest(ArrayList<String> request) {
        this.request = request;
    }

    public ArrayList<String> getDocument() {
        return document;
    }

    public void setDocument(ArrayList<String> document) {
        this.document = document;
    }
    public String getUsersDisplay(){
        return CollectionUtils.listJoin(user);
    }

    public boolean isCanEditPeriod() {
        return canEditPeriod;
    }

    public void setCanEditPeriod(boolean canEditPeriod) {
        this.canEditPeriod = canEditPeriod;
    }

    public Long getStageId() {
        return stageId;
    }

    public void setStageId(Long stageId) {
        this.stageId = stageId;
    }

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    public boolean isExpired() {
        if(deadline==null)
            return false;
        if(finish!=null){
            return deadline.before(finish);
        } else {
            return deadline.before(new Date());
        }
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public ArrayList<String> getEventsHistory() {
        return eventsHistory;
    }

    public void setEventsHistory(ArrayList<String> eventsHistory) {
        this.eventsHistory = eventsHistory;
    }

    public ArrayList<String> getChangeHistory() {
        return changeHistory;
    }

    public void setChangeHistory(ArrayList<String> changeHistory) {
        this.changeHistory = changeHistory;
    }

    public ArrayList<String> getComments() {
        return comments;
    }

    public void setComments(ArrayList<String> comments) {
        this.comments = comments;
    }

	public StandardPeriod(Long stageId, String stageName, Long standardPeriod,
			String criteria, Long mdtaskId) {
		super();
		this.stageId = stageId;
		this.stageName = stageName;
		this.standardPeriod = standardPeriod;
		this.criteria = criteria;
		this.mdtaskId=mdtaskId;
	}

	public Long getMdtaskId() {
		return mdtaskId;
	}

	public void setMdtaskId(Long mdtaskId) {
		this.mdtaskId = mdtaskId;
	}

	public StandardPeriod() {
		super();
	}

	public String getLastCommentText() {
		return lastCommentText;
	}

	public void setLastCommentText(String lastCommentText) {
		this.lastCommentText = lastCommentText;
	}

	public String getLastCommentAuthor() {
		return lastCommentAuthor;
	}

	public void setLastCommentAuthor(String lastCommentAuthor) {
		this.lastCommentAuthor = lastCommentAuthor;
	}

	public String getLastCommentAuthorDepartment() {
		return lastCommentAuthorDepartment;
	}

	public void setLastCommentAuthorDepartment(String lastCommentAuthorDepartment) {
		this.lastCommentAuthorDepartment = lastCommentAuthorDepartment;
	}

}
