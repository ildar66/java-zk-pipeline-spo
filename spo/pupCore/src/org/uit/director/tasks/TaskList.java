package org.uit.director.tasks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.db.ejb.DBFlexWorkflowCommon;
import org.uit.director.tasks.comparators.AtributesComparator;
import org.uit.director.tasks.comparators.DateComparator;
import org.uit.director.tasks.comparators.StageNameComparator;
import org.uit.director.tasks.comparators.TypeNameProcessComparator;

import ru.md.spo.ejb.PupFacadeLocal;
import ru.md.spo.util.Config;

import com.vtb.domain.ProcessSearchParam;
import com.vtb.domain.TaskListType;

public class TaskList implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(TaskList.class.getName());

	private TaskListType typeList;
	private List<Map<Long, TaskInfo>> tableTaskList;
	private WorkflowSessionContext wsc;
	private Long idUser;
	private int currPage;
	private int allPages;
	private int tasksOnPage;
	private boolean[] isLoadPageMas;

	// список для своего депертамента (true) или для всех дочерних (false)
	private boolean isOwnDepartmentOnly = true;

	// private int countDec = 10;
	private Map<Integer, String> filter = null;

	// private String filterByNameStage = null;
	// private String filterByAtribute = null;

	public static final int SORT_BY_PROCESS_NAME = 0;
	public static final int SORT_BY_STAGE_NAME = 1;
	public static final int SORT_BY_DATEPOST = 2;
	public static final int SORT_BY_DATEOKON = 3;
	public static final int FILTER_NO = 0;
	public static final int FILTER_BY_NAME_TYPE_PROCESS = 1;
	public static final int FILTER_BY_NAME_STAGE = 2;
	public static final int FILTER_BY_ATRIBUTES = 3;

	public TaskList() {
		tableTaskList = new ArrayList<Map<Long, TaskInfo>>();
		tasksOnPage = 0;
	}

	public void init(TaskListType typeList, WorkflowSessionContext wsc, boolean isOwnDepartment) {
		this.typeList = typeList;
		this.wsc = wsc;
		idUser = wsc.getIdUser();
		filter = new HashMap<Integer, String>();
		isOwnDepartmentOnly = isOwnDepartment;
	}

	public void init(TaskListType typeList, WorkflowSessionContext wsc, Long idUser) {
		this.typeList = typeList;
		this.wsc = wsc;
		this.idUser = idUser;
		filter = new HashMap<Integer, String>();
	}

	public void resetFilter() {
		if (filter != null) {
			filter.clear();
		}
	}

	public void setFilter(int filterId, String filterValue) {
		filter.put(filterId, filterValue);
	}

    public String execute(ProcessSearchParam processSearchParam) {
		long tstart = System.currentTimeMillis();
		String result = "ok";
		try {
			PupFacadeLocal pupFacade = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
			List<Long> idTaskList =pupFacade.getWorkList(idUser, typeList, processSearchParam, 1000L, 0L);

			currPage = 0;
			int allTasks = idTaskList.size();
			if (tasksOnPage == 0) tasksOnPage = Integer.parseInt(Config.getProperty("TASKS_ON_PAGE"));

			allPages = round(allTasks, tasksOnPage);

			isLoadPageMas = new boolean[allPages];
			isLoadPageMas[0] = true;

			for (int i = 0; i < allTasks; i++) {
				Map<Long, TaskInfo> map = new HashMap<Long, TaskInfo>();
				Long idTask = idTaskList.get(i);
				// idTasks.add(idTask);
				map.put(idTask, null);
				tableTaskList.add(map);
				if (i < tasksOnPage) {
					String res = addTaskInfo(i);
					if (res.equalsIgnoreCase("Error")) return "Error";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = "Error";
		}
		Long loadTime = System.currentTimeMillis()-tstart;
		LOGGER.warn("*** TaskList.execute() time  - "+loadTime);
		return result;
	}

	private int round(int allTasks, int tasksOnPage) {
		float res = (float) allTasks / tasksOnPage;
		int intMin = (int) Math.floor(res);
		if (res - intMin > 0 || allTasks == 0) {
			intMin++;
		}

		return intMin;
	}

	public String addTaskInfo(int idx) {

		String result = "error";
		try {

			Map<Long, TaskInfo> map = tableTaskList.get(idx);
			Long idTask = map.keySet().iterator().next();
			if (map.get(idTask) == null) {
				TaskInfo cachInfo = new TaskCachInfo();
				cachInfo.init(wsc, idTask, false);

				String res = "ok";//cachInfo.execute();

				if (!res.equalsIgnoreCase("Error")) {
					map.put(idTask, cachInfo);
					tableTaskList.set(idx, map);
					result = "ok";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();

		}

		return result;
	}

	public String addTaskInfo(Long idTask) {

		String result = "error";
		try {

			TaskInfo cachInfo = new TaskCachInfo();
			// Перед добавлением проверим есть ли такой элемент уже в списке
			Map<Long, TaskInfo> map = findTask(idTask);
			if (map != null) {
				tableTaskList.remove(map);

			}

			cachInfo.init(wsc, idTask, false);

			String res = cachInfo.execute();

			if (!res.equalsIgnoreCase("Error")) {
				map = new HashMap<Long, TaskInfo>();
				map.put(idTask, cachInfo);
				tableTaskList.add(map);
				result = "ok";
			}
		} catch (Exception e) {
			e.printStackTrace();

		}

		return result;
	}

	public void addTaskInfo(ProcessInfo processInfo) {

		if (processInfo != null) {
			if (processInfo instanceof TaskInfo) {

				TaskInfo info = (TaskInfo) processInfo;
				Map<Long, TaskInfo> map = findTask(info.getIdTask());
				if (map != null) {
					tableTaskList.remove(map);

				}
				map = new HashMap<Long, TaskInfo>();
				map.put(info.getIdTask(), info);
				tableTaskList.add(map);
			}
		}
	}

	public String getTypeProcessNameIdx(int idx) {

		return ((TaskInfo) ((Map) tableTaskList.get(idx)).values().iterator()
				.next()).getNameTypeProcess();

	}

	public String getStageNameToIdx(int idx) {

		return ((TaskInfo) ((Map) tableTaskList.get(idx)).values().iterator()
				.next()).getNameStageTo();

	}

	/*public String getStageNameFromIdx(int idx) {

		return ((TaskInfo) ((Map) tableTaskList.get(idx)).values().iterator()
				.next()).getNameStageFrom();

	}*/

	public String getColorIdx(int idx) {

		return ((TaskInfo) ((Map) tableTaskList.get(idx)).values().iterator()
				.next()).getColorTask();

	}

	public String getDateOfCommingIdx(int idx) {

		return ((TaskInfo) ((Map) tableTaskList.get(idx)).values().iterator()
				.next()).getDateOfCommingStr();

	}

	public String getDateOfMustComplete(int idx) {

		return ((TaskInfo) ((Map) tableTaskList.get(idx)).values().iterator()
				.next()).getDateOfMustCompleteStr();

	}

	/**
	 * Сортирует список заданий по одному из значений: SORT_BY_PROCESS_NAME = 0;
	 * SORT_BY_STAGE_NAME = 1; SORT_BY_DATEPOST = 2; SORT_BY_DATEOKON = 3;
	 * 
	 * @param sort
	 */
	@SuppressWarnings("unchecked")
	private void sort(int sort) {

		switch (sort) {
		case SORT_BY_PROCESS_NAME: {
			Collections.sort(tableTaskList, new TypeNameProcessComparator());
			break;
		}
		case SORT_BY_DATEPOST: {
			Collections.sort(tableTaskList, new DateComparator(true));
			break;
		}
		case SORT_BY_DATEOKON: {
			Collections.sort(tableTaskList, new DateComparator(false));
			break;
		}
		case SORT_BY_STAGE_NAME: {
			Collections.sort(tableTaskList, new StageNameComparator());
			break;
		}
		}

	}

	/**
	 * Cортирует список заданий по значению имени атрибута nameAtr
	 * 
	 * @param nameAtr
	 */
	@SuppressWarnings("unchecked")
	private void sort(String nameAtr) {

		Collections.sort(tableTaskList, new AtributesComparator(nameAtr));

	}

	public void sortByParam(String param) {

		if (param.equals("sortByTypeProcess")) {
			this.sort(TaskList.SORT_BY_PROCESS_NAME);
		} else if (param.equals("sortByNameStage")) {
			this.sort(TaskList.SORT_BY_STAGE_NAME);
		} else if (param.equals("sortByDateComming")) {
			this.sort(TaskList.SORT_BY_DATEPOST);
		} else if (param.equals("sortByDateMustComplete")) {
			this.sort(TaskList.SORT_BY_DATEOKON);
		} else {
			this.sort(param);
		}
	}

	public TaskInfo findTaskInfo(long idTask) {

		int col = tableTaskList.size();
		TaskInfo data = null;
		for (int i = 0; i < col; i++) {
			Map map = tableTaskList.get(i);
			long idT = ((Long) map.keySet().iterator().next()).longValue();

			if (idT == idTask) {
				data = (TaskInfo) map.get(idT);
				break;
			}

		}
		return data;
	}

	public Map<Long, TaskInfo> findTask(Long idTask) {

		int col = tableTaskList.size();
		for (int i = 0; i < col; i++) {
			Map<Long, TaskInfo> map = tableTaskList.get(i);
			Long idT = map.keySet().iterator().next();

			if (idT.equals(idTask)) {
				return map;
			}

		}
		return null;

	}

	public TaskInfo getTaskIdx(int i) {

		try {
			return (TaskInfo) ((Map) tableTaskList.get(i)).values().iterator()
					.next();
		} catch (Exception e) {

		}
		return null;
	}

	public int size() {
		return tableTaskList.size();
	}

	public void deleteTaskInfo(long idTask) {

		int col = tableTaskList.size();
		for (int i = 0; i < col; i++) {
			if (((Long) ((Map) tableTaskList.get(i)).keySet().iterator().next())
					.longValue() == idTask) {
				tableTaskList.remove(i);
				return;
			}

		}
	}

	public void nextPage() {
		if (currPage + 1 > allPages) {
			return;
		}
		currPage++;
		loadPage();
		isLoadPageMas[currPage] = true;
	}

	public void previosPage() {
		if (currPage < 1) {
			return;
		}
		currPage--;
		loadPage();
		isLoadPageMas[currPage] = true;
	}

	public void setPage(String navigation) {

		currPage = Integer.parseInt(navigation);
		if (currPage < 0 || currPage > allPages) {
			return;
		}
		loadPage();
		isLoadPageMas[currPage] = true;
	}

	private void loadPage() {

		int allTasks = tableTaskList.size();
		for (int i = currPage * tasksOnPage; i < (currPage + 1) * tasksOnPage; i++) {
			if (i == allTasks) {
				break;
			}
			addTaskInfo(i);
		}
	}

	public int getAllPages() {
		return allPages;
	}

	public int getCurrPage() {
		return currPage;
	}

	public int getTasksOnPage() {
		return tasksOnPage;
	}

	public void setTasksOnPage(int tasksOnPage) {
        this.tasksOnPage = tasksOnPage;
    }
	
	public int rightBoundPage() {

		int res = (currPage + 1) * tasksOnPage;
		if (res > tableTaskList.size()) {
			res = tableTaskList.size();
		}
		return res;
	}

	public int leftBoundPage() {
		return currPage * tasksOnPage;
	}

	public String getNavigation() {
		StringBuilder sb = new StringBuilder();

		sb.append("<div class=\"paging\">");
		if (!(filter.get(FILTER_BY_ATRIBUTES)).startsWith("NULL")
				|| !(filter.get(FILTER_BY_NAME_STAGE)).startsWith("NULL")
				|| !(filter.get(FILTER_BY_NAME_TYPE_PROCESS))
						.startsWith("NULL")) {
			sb.append("Применен фильтр: ");
			if (!(filter.get(FILTER_BY_NAME_TYPE_PROCESS)).startsWith("NULL")) {
				sb.append("по полю Процесс (").append(
						filter.get(FILTER_BY_NAME_TYPE_PROCESS)).append(
						");&nbsp;&nbsp;&nbsp;");
			}
			if (!(filter.get(FILTER_BY_NAME_STAGE)).startsWith("NULL")) {
				sb.append("по полю Этап (").append(
						filter.get(FILTER_BY_NAME_STAGE)).append(
						");&nbsp;&nbsp;&nbsp;");
			}
			if (!(filter.get(FILTER_BY_ATRIBUTES)).startsWith("NULL")) {
				sb.append("по полю Атрибуты (").append(
						filter.get(FILTER_BY_ATRIBUTES)).append(
						");&nbsp;&nbsp;&nbsp;");
			}
			sb.append("<br>");

		}

		String typeListStr = "noAccept";
		if(typeList.equals(TaskListType.ACCEPT)) typeListStr = "accept";
		if(typeList.equals(TaskListType.ASSIGN)) typeListStr = "perform";
		
		
		sb.append("Всего заданий: <b>").append(tableTaskList.size()).append(
				"</b>; страниц: <b>").append(allPages).append(
				"</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
		String hrefPageFormat="<a onClick=\"$('#navigation').val('%s');$('#processGrid').submit()\" href = \"#\" >%s</a> &nbsp";
		if (currPage != 0) {
			sb.append(String.format(hrefPageFormat, "0","Первая"));
		    sb.append(String.format(hrefPageFormat, "previos","Назад"));
		}

		if (allPages > 1) {

			int leftB = leftNavigation();
			int rightB = rightNavigation();

			for (int i = leftB; i < rightB; i++) {
				if (i == currPage) {
					sb.append("<span class=\"selected\">");
					sb.append(i + 1);
					sb.append("</span>&nbsp;");
				} else {
				    sb.append(String.format(hrefPageFormat, i,i+1));
				}
			}
		}

		if (currPage != allPages - 1) {
			sb.append(String.format(hrefPageFormat, "next","Вперед"));
			sb.append(String.format(hrefPageFormat, allPages - 1,"Последняя"));
		}
		sb.append("</div>");

		return sb.toString();
	}

	private int leftNavigation() {
		for (int i = currPage; i >= 0; i--) {
			if (((i + 1) % 10) == 0) {
				return i;
			}
		}
		return 0;

	}

	private int rightNavigation() {

		int start = currPage;
		if (((currPage + 1) % 10) == 0 && currPage != 0) {
			start++;
		}

		for (int i = start; i < allPages; i++) {
			if (((i + 1) % 10) == 0 && i != 0) {
				return i;
			}
		}
		return allPages;
	}

	/** 
	 * Почему нет комментариев к такому важному методу? -- МК
	 * Типа проверяет, были ли загружены ВСЕ страницы? и если хотя бы одна не была загружена, то возвращает false
	 * @return
	 */
	public boolean isLoadAllTaskList() {
		for (boolean b : isLoadPageMas)
			if (!b) return false;
		return true;

	}

	/**
	 * 
	 */
	public void clear() {
		if (tableTaskList != null) {
			tableTaskList.clear();
		}

	}

	public boolean isOwnDepartmentOnly() {
		return isOwnDepartmentOnly;
	}

	public void setOwnDepartmentOnly(boolean isOwnDepartmentOnly) {
		this.isOwnDepartmentOnly = isOwnDepartmentOnly;
	}

	public TaskListType getTypeList() {
		return typeList;
	}
	
}
