package ru.md.helper;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import com.vtb.domain.TaskListType;

import ru.masterdm.compendium.model.CompendiumActionProcessor;
import ru.md.spo.loader.TaskLine;

public class TaskPage {
	private String typeList;
	private int leftBound = 0;
	private int rightBound = 0;
	private Long count = 0L;
	private Long pageCount = 0L;
	private Long curr = 0L;
	private String navigation;
	private ArrayList<TaskLine> taskLineList;
	private String nameIspoln;

	public TaskPage() {
		super();
		taskLineList = new ArrayList<TaskLine>();
	}
	/** typeList = 	    "all"         -- Показать ВСЕ заявки (пункт меню Все заявки)
  				"noAccept"    -- Заявки, ожидающие моей обработки
  				"accept"      -- Заявки, находящиеся у меня в работе
  				"perform"     -- Заявки, назначенные мне		
	 * @return
	 */
	public String getTypeList() {
		if (typeList == null) {
			typeList = "noAccept";
		}
		return typeList;
	}
	public TaskListType getTaskListType(){
		if (getTypeList().equals("refuse")) return TaskListType.ACCEPT_FOR_REFUSE;
		if (getTypeList().equals("accept")) return TaskListType.ACCEPT;
		if (getTypeList().equals("perform")) return TaskListType.ASSIGN;
		return TaskListType.NOT_ACCEPT;
	}
	public void setTypeList(String typeList) {
		this.typeList = typeList;
	}
	/**
	 * возращает заголовок страницы.
	 */
	public String getH1(HttpServletRequest request) {
	    if (request.getParameter("projectteam")!=null) return "Работа проектной команды";
	    if (request.getParameter("paused")!=null) return "Приостановленные заявки";
		if (this.getTypeList().equalsIgnoreCase("noAccept")) return "Заявки, ожидающие моей обработки";
		if (this.getTypeList().equalsIgnoreCase("accept")) return "Заявки, находящиеся у меня в работе";
		if (this.getTypeList().equalsIgnoreCase("perform")) return "Заявки, назначенные мне";
		if (this.getTypeList().equalsIgnoreCase("all")){
		    if(request.getParameter("favorite")!=null) return "Избранные заявки";
			if(request.getParameter("closed")!=null) return "Завершенные заявки";
			String dep = "всех департаментов";
			if (request.getParameter("idDepartment")!=null &&!request.getParameter("idDepartment").equals("")){
				try {
					CompendiumActionProcessor compenduim = (CompendiumActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("Compendium");
				    dep=compenduim.findDepartmentFullPath(Integer.valueOf(request.getParameter("idDepartment")), true);
				} catch (Exception e) {
					
				}
			}
			return "Все заявки "+dep;
		}
		return "ошибка. Неправильный режим просмотра "+this.getTypeList();
	}
	/**
	 * флаг режима 'все заявки'
	 */
	public boolean isAllMode() {
		return (this.getTypeList().equalsIgnoreCase("all"));
	}
	public int getLeftBound() {
		return leftBound;
	}
	public void setLeftBound(int leftBound) {
		this.leftBound = leftBound;
	}
	public int getRightBound() {
		return rightBound;
	}
	public void setRightBound(int rightBound) {
		this.rightBound = rightBound;
	}
	public String getNavigation() {
		return navigation;
	}
	public void setNavigation(String navigation) {
		this.navigation = navigation;
	}
	public ArrayList<TaskLine> getTaskLineList() {
		return taskLineList;
	}
	public boolean isShowAssignableUsers() {
		return typeList.equalsIgnoreCase("noAccept") || typeList.equalsIgnoreCase("perform");
	}
	public String getNameIspoln() {
		return nameIspoln;
	}
	public void setNameIspoln(String nameIspoln) {
		this.nameIspoln = nameIspoln;
	}
	public Long getCount() {
		return count;
	}
	public void setCount(Long count) {
		this.count = count;
	}
	public Long getCurr() {
		return curr == null ? 0L : curr;
	}
	public void setCurr(Long curr) {
		this.curr = curr;
	}
	public void setCurr(String nav) {
		if(nav!=null && !nav.isEmpty())
			try {
				curr = Long.valueOf(nav);
			} catch (Exception e) {
			}
	}
	public void setTaskLineList(ArrayList<TaskLine> taskLineList) {
		this.taskLineList = taskLineList;
	}

	public Long getPageCount() {
		return pageCount;
	}

	public void setPageCount(Long pageCount) {
		this.pageCount = pageCount;
	}
}
