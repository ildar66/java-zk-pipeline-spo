package org.uit.director.report;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.uit.director.contexts.WPC;
import org.uit.director.db.dbobjects.WFObjectComparator;
import org.uit.director.db.dbobjects.WorkflowRoles;
import org.uit.director.db.dbobjects.WorkflowStages;
import org.uit.director.db.dbobjects.WorkflowTypeProcess;
import org.uit.director.db.dbobjects.WorkflowUser;
import org.uit.director.db.ejb.DBFlexWorkflowCommon;
import org.uit.director.managers.DBMgr;

public class ComponentGenerator {

	private static final Logger logger = Logger.getLogger(ComponentGenerator.class.getName());
	
	static class ListMapComparator implements Comparator {

		public int compare(Object o1, Object o2) {
			Map m1 = (Map) o1;
			Map m2 = (Map) o2;
			String v1 = (String) m1.get(m1.keySet().iterator().next());
			String v2 = (String) m2.get(m2.keySet().iterator().next());

			return v1.compareToIgnoreCase(v2);
		}

	}

	/**
	 * Сгенерировать список типов процесса для отображения в отчете в виде выпадающего списка
	 * (без дополнительного параметра по умолчанию)
	 * @return ComponentReport
	 */
	public static ComponentReport genTypeProcess() {
		return genTypeProcess(false);
	}

	/**
	 * Сгенерировать список типов процесса для отображения в отчете в виде выпадающего списка
	 * @param flag -- добавлять ли параметр (-1, "Все процессы").
	 * @return ComponentReport
	 */
	public static ComponentReport genTypeProcess(boolean flag) {

		List typeProcesses = WPC.getInstance().getTypeProcessesList().getTypesProcesses();
		// Map -- это ключ-значение (id и описание).
		// а вот список содержит эти maps, причем в каждой map - по одному значению. Зачем так странно? 
		List<Map<String, String>> sel = new ArrayList<Map<String, String>>();

		if (flag) {
			Map<String, String> elem = new HashMap<String, String>();
			elem.put("-1", "Все процессы");
			sel.add(elem);
		}
		for (int i = 0; i < typeProcesses.size(); i++) {
			WorkflowTypeProcess typeProcess = (WorkflowTypeProcess) typeProcesses.get(i);
			String description = typeProcess.getNameTypeProcess();
			String id = String.valueOf(typeProcess.getIdTypeProcess());
			Map<String, String> elem = new HashMap<String, String>();
			elem.put(id, description);
			sel.add(elem);
		}

		return new ComponentReport("select", "Тип процесса", sel);

	}

	
	/**
	 * Сгенерировать список типов процесса, ДОСТУПНЫХ для текущего пользователя, 
	 * для отображения в отчете в виде выпадающего списка
	 * @param idUser -- id пользователя, для которого выдаем список. 
	 * @return ComponentReport
	 */
	public static ComponentReport genTypeProcessForUser(Long idUser) {

		List typeProcesses = WPC.getInstance().getTypeProcessesList(idUser);
		List<Map<String, String>> sel = new ArrayList<Map<String, String>>();
        Map<String, String> allProcesses = new HashMap<String, String>();
        allProcesses.put("-1", "Все процесссы");
        sel.add(allProcesses);
		for (int i = 0; i < typeProcesses.size(); i++) {
			WorkflowTypeProcess typeProcess = (WorkflowTypeProcess) typeProcesses
					.get(i);
			String description = typeProcess.getNameTypeProcess();
			String id = String.valueOf(typeProcess.getIdTypeProcess());
			Map<String, String> elem = new HashMap<String, String>();
			elem.put(id, description);
			sel.add(elem);
		}

		return new ComponentReport("select", "Тип процесса", sel);

	}

	
	/**
	 * Сгенерировать список ВСЕХ департаментов для отображения в отчете в виде выпадающего списка 
	 * @return ComponentReport
	 */
	public static ComponentReport genAllDepartmentsForUser(Long idUser) {

		List<Map<String, String>> sel = new ArrayList<Map<String, String>>();
		try{
			// Покажем все депратаменты
			// Вызываем с null-значением request (он нам не нужен).
			// TODO: поом включить департамент -1 (Все департаменты)
			Map<String, String> departments = WPC.setDepartmentsForUser(WPC.INCLUDE_ALL, false, false, idUser);			

			// А теперь переложим результат в том виде, в котором он нам нужен.	
			Iterator<String> it = departments.keySet().iterator();			
			while (it.hasNext()) {
	           String id = it.next();
	           String description = departments.get(id);
	           Map<String, String> elem = new HashMap<String, String>();
	           elem.put(id, description);
	           sel.add(elem);
			}
		}catch (Exception e){
			logger.severe(e.getMessage());
			e.printStackTrace();
			sel = null;
		}
		return new ComponentReport("select", "Подразделение", sel);		
	}
	
	
	/**
	 * Сгенерировать список департаментов (включая ТОЛЬКО подчиненные департаменты), 
	 * доступных для текущего пользователя, 
	 * для отображения в отчете в виде выпадающего списка 
	 * @return ComponentReport
	 */
	public static ComponentReport genSubordinateDepartmentsForUser(Long idUser) {

		List<Map<String, String>> sel = new ArrayList<Map<String, String>>();
		try{
			// Покажем только дочерние департаменты и департамент самого пользователя.
			// Вызываем с null-значением request (он нам не нужен).
			Map<String, String> subordinatesDepartments = WPC.setDepartmentsForUser(WPC.INCLUDE_SUBORDINATE, false, false, idUser);			

			// А теперь переложим результат в том виде, в котором он нам нужен.	
			Iterator<String> it = subordinatesDepartments.keySet().iterator();			
			while (it.hasNext()) {
	           String id = it.next();
	           String description = subordinatesDepartments.get(id);
	           Map<String, String> elem = new HashMap<String, String>();
	           elem.put(id, description);
	           sel.add(elem);
			}
		}catch (Exception e){
			logger.severe(e.getMessage());
			e.printStackTrace();
			sel = null;
		}
		return new ComponentReport("select", "Подчиненные подразделения", sel);		
	}
	
	/**
	 * Сгенерировать список департаментов (включая ТОЛЬКО департамент самого пользователя)	 
	 * для отображения в отчете в виде выпадающего списка 
	 * @return ComponentReport
	 */
	public static ComponentReport genCurrentDepartmentsForUser(Long idUser) {
		List<Map<String, String>> sel = new ArrayList<Map<String, String>>();
		try{
			// Покажем только текущий департамент (департамент самого пользователя).
			// Вызываем с null-значением request (он нам не нужен).
			Map<String, String> currentDepartment = WPC.setDepartmentsForUser( WPC.INCLUDE_NONE, false, true, idUser);			

			// А теперь переложим результат в том виде, в котором он нам нужен.	
			Iterator<String> it = currentDepartment.keySet().iterator();			
			while (it.hasNext()) {
	           String id = it.next();
	           String description = currentDepartment.get(id);
	           Map<String, String> elem = new HashMap<String, String>();
	           elem.put(id, description);
	           sel.add(elem);
			}
		}catch (Exception e){
			logger.severe(e.getMessage());
			e.printStackTrace();
			sel = null;
		}
		return new ComponentReport("select", "Текущее подразделение", sel);		
	}
	
	
	public static String setRightDate(String date) {

		SimpleDateFormat format = WPC.getInstance().dateFormat;

		Calendar cal = Calendar.getInstance();
		try {
			cal.setTime(format.parse(date));
			cal.add(Calendar.DAY_OF_MONTH, 1);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return format.format(cal.getTime());
	}

	public static ComponentReport genPeriod() {

		SimpleDateFormat dateFormat = WPC.getInstance().dateFormat;
		String dateleft = "01.01.2009";
		String dateRight = dateFormat.format(new Date());
		List<String> period = new ArrayList<String>();
		period.add(dateleft);
		period.add(dateRight);

		return new ComponentReport("period", "Период", period);
	}

	/**
	 * Вытаскивает значение выбранного компонента отчета
	 * 
	 * @param componentReport
	 * @return res
	 */
	public static String getSelectedItem(ComponentReport componentReport) {
		Map m = (Map) ((List) componentReport.getValue()).get(componentReport
				.getIndexSelect());
		return String.valueOf(m.keySet().iterator().next());

	}

	public static ComponentReport genWorkflowUsers() {

		WPC wpc = WPC.getInstance();
		List<Map<String, String>> users = new ArrayList<Map<String, String>>();
		Iterator<Long> it = wpc.getUsersMgr().getWorkflowUsers().getWfUserMap()
				.keySet().iterator();

		while (it.hasNext()) {
			Map<String, String> mapGen = new HashMap<String, String>();
			Long idUser = it.next();
			mapGen.put(String.valueOf(idUser), wpc.getUsersMgr().getFIOUser(
					idUser));
			users.add(mapGen);
		}
		return new ComponentReport("select", "Пользователь", users);
	}

	/*
	 * public static String getFirstDescription(ComponentReport componentReport) {
	 * 
	 * List l = ((List) componentReport.getValue()); Map m = (Map) l.get(0);
	 * return String.valueOf(m.get(m.keySet().iterator().next())); }
	 */

	/**
	 * Сгенерируем список операций [всех операций по всем этапам]. 
	 *  
	 * @return ComponentReport("select", "Этап", sel)
	 */
	public static ComponentReport genStages() {

		List stages = WPC.getInstance().getStages();

		List<Map> sel = new ArrayList<Map>();
		Map<String, String> elem = new HashMap<String, String>();
		elem.put("0", "-");
		sel.add(elem);

		for (int i = 0; i < stages.size(); i++) {
			WorkflowStages stage = (WorkflowStages) stages.get(i);
			String id = String.valueOf(stage.getIdStage());
			String name = stage.getNameStage();
			elem = new HashMap<String, String>();
			elem.put(id, name);
			sel.add(elem);
		}

		return new ComponentReport("select", "Этап", sel);

	}

	
	/**
	 * Сгенерируем javaScript, обрабатывающий разные типы списков  
	 * Вариант для запуска с одним аргументом
	 * @param  int stagesInTypProcess -- 
	 *      ==  stagesInTypProcess  -- список операции для данного типа процесса
	 *                                 список операций [всех операций по всем этапам???].
	 *      ==  usersInTypeProcess   -- список пользователей для данного процесса
	 *      ==  rolesInTypeProcess   -- список ролей для данного типа процесса
	 *      ==  usersInDepartment =    -- no realization yet!!!
	 *      ==  subordinateUsersInDepartment  -- подчиненные данному пользователю пользователи в департаменте.	 * 
	 * @return ComponentReport("script", "", sb.toString())
	 */
	public static ComponentReport genScript(int stagesInTypProcess) {
		return genScript(stagesInTypProcess, null);
	}
		
	/**
	 * Сгенерируем javaScript, обрабатывающий разные типы списков  
	 *  
	 * @param  int stagesInTypProcess -- 
	 *      ==  stagesInTypProcess  -- список операции для данного типа процесса
	 *                                 список операций [всех операций по всем этапам???].
	 *      ==  usersInTypeProcess   -- список пользователей для данного процесса
	 *      ==  rolesInTypeProcess   -- список ролей для данного типа процесса
	 *      ==  usersInDepartment =    -- no realization yet!!!
	 *      ==  subordinateUsersInDepartment  -- подчиненные данному пользователю пользователи в департаменте.
	 * @param  id  -- необязательный параметр, передающий дополнительную информацию. 
	 * @return ComponentReport("script", "", sb.toString())
	 */
	@SuppressWarnings("unchecked")
	public static ComponentReport genScript(int stagesInTypProcess, Long id) {
		StringBuffer sb = new StringBuffer();
		WPC wpc = WPC.getInstance();
		
		addFunctionsScript(sb);
		
		try{						
			switch (stagesInTypProcess) {
			case ComponentReport.referensType.stagesInTypProcess: {
				
				// добавим ВСЕ операции всех процессов в список в виде 
				// var childData = new Array("new Child()")
				sb.append("var childData = new Array(");
				List stages = wpc.getStages();
				// Collections.sort(stages, new WFObjectComparator());
				sb.append("new Child(").append(0).append(",'").append("-").append(
						"'),");
				for (int i = 0; i < stages.size(); i++) {
					WorkflowStages workflowStages = (WorkflowStages) stages.get(i);
					sb.append("new Child(").append(workflowStages.getIdStage())
							.append(",'").append(workflowStages.getNameStage())
							.append("'),");
				}
				sb.deleteCharAt(sb.length() - 1);
				sb.append(");");
	
				// добавим операции каждого процесса в виде
				// var childInParent = new Array(new Parent(<id типа процесса>,[0,<id операции>,<id операции>...]),
				// new Array(new Parent(<id типа процесса>,[0,<id операции>,<id операции>...])...
				sb.append(" var childInParent = new Array(");
				
				// получим набор списков операций по каждому процессу 
				Map<Integer, List<Long>> stInTp = wpc.getStagesInTypeProcess();
	
				Iterator<Integer> it = stInTp.keySet().iterator();
				while (it.hasNext()) {
					Integer idTypeProc = it.next();
					List<Long> stagesList = stInTp.get(idTypeProc);
					String stagesArray = "[0,";
	
					List<WorkflowStages> stagesForSort = new ArrayList<WorkflowStages>();
					for (int i = 0; i < stagesList.size(); i++) {
						Long idStage = stagesList.get(i);
						stagesForSort.add(WPC.getInstance().findStage(idStage));
					}
					Collections.sort(stagesForSort, new WFObjectComparator());
	
					for (int i = 0; i < stagesList.size(); i++) {
						stagesArray += stagesForSort.get(i).getIdStage() + ",";
					}
	
					stagesArray = stagesArray
							.substring(0, stagesArray.length() - 1)
							+ "]";
					sb.append("new Parent(").append(idTypeProc).append(",").append(
							stagesArray).append("),");
				}
				sb.deleteCharAt(sb.length() - 1);
				sb.append(");");
				break;
			}
	
			case ComponentReport.referensType.usersInTypeProcess: {
				sb.append("var childData = new Array(");
				List users = (List) genWorkflowUsers().getValue();
				// Collections.sort(users, new UsersComparator());
				for (int i = 0; i < users.size(); i++) {
					Map workflowUsers = (Map) users.get(i);
					String idUser = (String) workflowUsers.keySet().iterator().next();
					sb.append("new Child('").append(idUser).append("','")
					  .append(workflowUsers.get(idUser)).append("'),");
				}
				sb.deleteCharAt(sb.length() - 1);
				sb.append(");");
	
				sb.append(" var childInParent = new Array(");
				// map ВСЕХ пользователей (по типу процесса)
				Map stInTp = wpc.getUsersInTypeProcess();
				Iterator it = stInTp.keySet().iterator();
				while (it.hasNext()) {
					Integer idTypeProc = (Integer) it.next();

					// список пользователей в данном процессе
					List usersList = (List) stInTp.get(idTypeProc);						
					List<Map<Long, String>> userListForSort = new ArrayList<Map<Long, String>>();	
					for (int i = 0; i < usersList.size(); i++) {
						Long idUser = (Long) usersList.get(i);
						String fullName = WPC.getInstance().getUsersMgr().getFullNameWorkflowUser(idUser);
						Map<Long, String> map = new HashMap<Long, String>();
						map.put(idUser, fullName);
						userListForSort.add(map);
					}
	
					Collections.sort(userListForSort, new ListMapComparator());
	
					String usersArray = "[";
					for (int i = 0; i < userListForSort.size(); i++) {
						Map<Long, String> map = userListForSort.get(i);
						usersArray += 
								+ map.keySet().iterator().next().longValue() + ",";
					}
	
					usersArray = usersArray.substring(0, usersArray.length() - 1) + "]";
					sb.append("new Parent(").append(idTypeProc).append(",")
					  .append(usersArray).append("),");
				}
				sb.deleteCharAt(sb.length() - 1);
				sb.append(");");
	
				break;
			}
	
			case ComponentReport.referensType.rolesInTypeProcess: {
				sb.append("var childData = new Array(");
				List roles = wpc.getRoles();
				// Collections.sort(roles, new WFObjectComparator());
				for (int i = 0; i < roles.size(); i++) {
					WorkflowRoles workflowRoles = (WorkflowRoles) roles.get(i);
					long idRole = workflowRoles.getIdRole();
					sb.append("new Child(").append(idRole).append(",'").append(
							workflowRoles.getNameRole()).append("'),");
				}
				sb.deleteCharAt(sb.length() - 1);
				sb.append(");");
	
				sb.append(" var childInParent = new Array(");
				Map<Integer, List<Long>> stInTp = wpc.getRolesInTypeProcess();
				Iterator<Integer> it = stInTp.keySet().iterator();
				while (it.hasNext()) {
					Integer idTypeProc = it.next();
					List<Long> rolesList = stInTp.get(idTypeProc);
					String rolesArray = "[";
	
					List<WorkflowRoles> rolesForSort = new ArrayList<WorkflowRoles>();
	
					for (int i = 0; i < rolesList.size(); i++) {
	
						Long idRole = rolesList.get(i);
						WorkflowRoles wfRole = WPC.getInstance().findRole(idRole);
						rolesForSort.add(wfRole);
	
					}
	
					Collections.sort(rolesForSort, new WFObjectComparator());
					for (int i = 0; i < rolesList.size(); i++) {
						long idRole = rolesForSort.get(i).getIdRole();
						rolesArray += idRole + ",";
					}
	
					rolesArray = rolesArray.substring(0, rolesArray.length() - 1)
							+ "]";
					sb.append("new Parent(").append(idTypeProc).append(",").append(
							rolesArray).append("),");
				}
				sb.deleteCharAt(sb.length() - 1);
				sb.append(");");
	
				break;
			}
												
			case ComponentReport.referensType.subordinateUsersForUser: {						
				// сгенерировать список пользователей, подчиненных ОДНОМУ пользователю (передается id). 
				// Получим список пользователей.
				sb.append("var childData = new Array(");
				List users = (List) genWorkflowUsers().getValue();
				// Collections.sort(users, new UsersComparator());
				for (int i = 0; i < users.size(); i++) {
					Map workflowUsers = (Map) users.get(i);
					String idUser = (String) workflowUsers.keySet().iterator().next();
					sb.append("new Child('").append(idUser).append("','")
					  .append(workflowUsers.get(idUser)).append("'),");
				}
				sb.deleteCharAt(sb.length() - 1);
				sb.append(");");
	
				sb.append(" var childInParent = new Array(");
		
				// список пользователей, подчиненных данному пользователю
				List usersList = null;
				DBMgr dbManager = new DBMgr();
				DBFlexWorkflowCommon dbWorkflow = dbManager.getDbFlexDirector();		
			
				// список пользователей, подчиненных данному пользователю
				if (id != null) usersList = dbWorkflow.getSubordinateUsersInDepartment(id);

				List<Map<Long, String>> userListForSort = new ArrayList<Map<Long, String>>();
				
				if (usersList != null) {
					for (int i = 0; i < usersList.size(); i++) {
						Long idUser = (Long) usersList.get(i);
						String fullName = WPC.getInstance().getUsersMgr().getFullNameWorkflowUser(idUser);
						Map<Long, String> map = new HashMap<Long, String>();
						map.put(idUser, fullName);
						userListForSort.add(map);
					}
	
					Collections.sort(userListForSort, new ListMapComparator());
	
					String usersArray = "[";
					for (int i = 0; i < userListForSort.size(); i++) {
						Map<Long, String> map = userListForSort.get(i);
						usersArray += 
								+ map.keySet().iterator().next().longValue() + ",";
					}
	
					usersArray = usersArray.substring(0, usersArray.length() - 1) + "]";
					sb.append("new Parent(").append(id).append(",").append(usersArray).append("),");
					sb.deleteCharAt(sb.length() - 1);
					sb.append(");");			
				}
				else 
					// у пользователя нет подчиненных пользователей
					sb.append("new Parent(-1,0));");  // передадим фиктивный массив с одним значением, равным нулю. 
				
				break;
			}
				
			case ComponentReport.referensType.usersInDepartment: {

				// Получим список пользователей в данном департаменте. Для каждого департамента
				sb.append("var childData = new Array(");
				List users = (List) genWorkflowUsers().getValue();
				// Collections.sort(users, new UsersComparator());
				for (int i = 0; i < users.size(); i++) {
					Map workflowUsers = (Map) users.get(i);
					String idUser = (String) workflowUsers.keySet().iterator().next();
					sb.append("new Child('").append(idUser).append("','")
					  .append(workflowUsers.get(idUser)).append("'),");
				}
				sb.deleteCharAt(sb.length() - 1);
				sb.append(");");
	
				sb.append(" var childInParent = new Array(");

				// map ВСЕХ пользователей (по департаменту)				
				Map stInTp = wpc.getUsersInDepartment();
				Iterator it = stInTp.keySet().iterator();
				while (it.hasNext()) {
					Integer idDepartment = (Integer) it.next();

					// список пользователей в данном департаменте
					List usersList = (List) stInTp.get(idDepartment);
	
					List<Map<Long, String>> userListForSort = new ArrayList<Map<Long, String>>();
	
					for (int i = 0; i < usersList.size(); i++) {
						Long idUser = (Long) usersList.get(i);
						String fullName = WPC.getInstance().getUsersMgr().getFullNameWorkflowUser(idUser);
						Map<Long, String> map = new HashMap<Long, String>();
						map.put(idUser, fullName);
						userListForSort.add(map);
					}
	
					Collections.sort(userListForSort, new ListMapComparator());
	
					String usersArray = "[";
					for (int i = 0; i < userListForSort.size(); i++) {
						Map<Long, String> map = userListForSort.get(i);
						usersArray += 
								+ map.keySet().iterator().next().longValue() + ",";
					}
	
					if (usersArray.length() > 1)  // not empty list
					    usersArray = usersArray.substring(0, usersArray.length() - 1);
					usersArray += "]";
					sb.append("new Parent(").append(idDepartment).append(",")
					  .append(usersArray).append("),");
				}
				sb.deleteCharAt(sb.length() - 1);
				sb.append(");");
	
				break;
			}
				
			case ComponentReport.referensType.variablesInTypProcess:
			default: 
				// такие скрипты не реализованы. Бросаем ошибку.
				throw new IndexOutOfBoundsException ("switch case for argument = " + stagesInTypProcess + "not implemented");
			}

		}catch (Exception e){
			logger.severe(e.getMessage());
			e.printStackTrace();			
		}
		return new ComponentReport("script", "", sb.toString());
	}

	private static void addFunctionsScript(StringBuffer sb) {
		sb
				.append("function onCh(parent, child) {\n")
				.append("        var st = child.options;\n")
				.append(
						"		 var oldVal = null;\n")
				.append("		 try{\n")
				.append("		 	oldVal = st[child.selectedIndex].value;\n")
				.append("		 }catch(err){}\n")
				
				.append("        removeOptions(st);\n")
				.append(
						"        var idParent = parent.options[parent.selectedIndex].value;\n")
				.append(
						"        for (var i = 0; i < childInParent.length; i++) {\n")
				.append("            var stTp = childInParent[i];\n")
				.append("            if (stTp.id == idParent) {\n")
				.append("                var idchildren = stTp.children;\n")
				.append(
						"                for (var j = 0; j < idchildren.length; j ++) {\n")
				.append(
						"                    var stageInTP = findChild(idchildren[j]);\n")
				.append("                    if (stageInTP != null)\n")
				.append(
						"                        st.add(new Option(stageInTP.text, stageInTP.id));\n")
				.append("                }\n")
				.append("            }\n")
				.append("        }\n")
				.append("		for(var t = 0; t < st.length; t++) {\n")
				.append("			if (st[t].value == oldVal)\n")
				.append("				st[t].selected = true;\n")
				.append("		}\n")				
				.append("    }\n")
				.append("    function removeOptions(st) {\n")
				.append("        var len = st.length;\n")
				.append("        for (var i = 0; i < len; i++) {\n")
				.append("            st.remove(0);\n")
				.append("        }\n")
				.append("    }\n")
				.append("    function findChild(idStage) {\n")
				.append(
						"        for (var i = 0; i < childData.length; i++) {\n")
				.append("            if (childData[i].id == idStage)\n")
				.append("                return childData[i];\n").append(
						"        }\n").append("        return null;\n").append(
						"    }\n").append("    function Child(id, text) {\n")
				.append("        this.id = id;\n").append(
						"        this.text = text;\n").append("    }\n")
				.append("    function Parent(id, children) {\n").append(
						"        this.id = id;\n").append(
						"        this.children = children;\n")
				.append("    }\n");
	}

	public static ComponentReport getItemByName(List componentList, String name) {

		for (int i = 0; i < componentList.size(); i++) {
			ComponentReport componentReport = (ComponentReport) componentList
					.get(i);
			if (componentReport.getDescription().equals(name)) {
				return componentReport;
			}

		}
		return null;

	}

	/**
	 * Получить строковое значение даты для компонента тип "period".
	 * 
	 * @param componentList
	 *            Список компонентов отчета
	 * @param name
	 *            имя компонента типа "period"
	 * @param idx
	 *            0-левое значение периода, 1-правое
	 * @return
	 */
	public static String getDateForPeriod(List componentList, String name,
			int idx) {
		ComponentReport componentReport = getItemByName(componentList, name);
		if (componentReport != null
				&& componentReport.getValue() instanceof List) {
			return (String) ((List) componentReport.getValue()).get(idx);

		}

		return null;

	}

	public static String genScript(Map<Object, String> childs,
			Map<Object, List> parents) {
		StringBuffer sb = new StringBuffer();
		addFunctionsScript(sb);

		sb.append("var childData = new Array(");

		Iterator<Object> it = childs.keySet().iterator();

		while (it.hasNext()) {
			Object key = it.next();
			String value = childs.get(key);

			sb.append("new Child('").append(key).append("','").append(value)
					.append("'),");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(");");

		sb.append(" var childInParent = new Array(");

		it = parents.keySet().iterator();
		while (it.hasNext()) {
			Object key = it.next();
			List list = parents.get(key);

			List<Map<Object, String>> listsort = new ArrayList<Map<Object, String>>();

			for (Object keyList : list) {
				Map<Object, String> map = new HashMap<Object, String>();
				map.put(keyList, childs.get(keyList));
				listsort.add(map);

			}
			Collections.sort(listsort, new ListMapComparator());

			String keysArray = "[";
			for (int i = 0; i < listsort.size(); i++) {
				Object keyList = ((Map) listsort.get(i)).keySet().iterator()
						.next();
				boolean isStr = false;
				if (keyList instanceof String) {
					isStr = true;
				}

				keysArray += (isStr ? "'" : "") + keyList + (isStr ? "'" : "")
						+ ",";
			}

			keysArray = ((listsort.size() > 0) ? keysArray.substring(0,
					keysArray.length() - 1) : keysArray)
					+ "]";
			sb.append("new Parent(").append(key).append(",").append(keysArray)
					.append("),");
		}
		if (!parents.isEmpty())
			sb.deleteCharAt(sb.length() - 1);
		sb.append(");");

		return sb.toString();
	}

	public static Map getUsers() {
		Map res = new HashMap();
		Map<Long, WorkflowUser> wfUserMap = WPC.getInstance().getUsersMgr()
				.getWorkflowUsers().getWfUserMap();
		Iterator<Long> it = wfUserMap.keySet().iterator();

		while (it.hasNext()) {
			Long idUser = it.next();
			String fullName = wfUserMap.get(idUser).getFIO();
			res.put(idUser, fullName);
		}

		return res;

	}

	/**
	 * Возвращаем список пользователей, подчиненных данному пользователю 
	 * (обычно нет смысла прописывать в качестве childInParent). Если есть, то используем genScript (см. выше).
	 * @param idBoss -- id пользователя, для которого ищем подчиненных.
	 * @return Map 
	 * @throws RemoteException 
	 */
	@SuppressWarnings("unchecked")
	public static Map getSubordinateUsers(Long idBoss) throws RemoteException {
		WPC wpc = WPC.getInstance();
		Map res = new HashMap();
		DBMgr dbManager = new DBMgr();
		DBFlexWorkflowCommon dbWorkflow = dbManager.getDbFlexDirector();		
	
		// список пользователей, подчиненных данному пользователю
		List usersList = dbWorkflow.getSubordinateUsersInDepartment(idBoss);
		if (usersList != null) {
			for (int i = 0; i < usersList.size(); i++) {
				Long idUser = (Long) usersList.get(i);
				String fullName = wpc.getUsersMgr().getFullNameWorkflowUser(idUser);				
				res.put(idUser, fullName);
			}
		}
		return res;
	}

	/**
	 * Возвращаем список пользователей, подчиненных данному пользователю 
	 * в качестве компонента отчета
	 * @param idUser -- id пользователя, для которого ищем подчиненных.
	 * @return Map 
	 * @throws RemoteException 
	 */
		
	public static ComponentReport genSubordinateUsers(Long idUser) throws RemoteException {

		List<Map<String, String>> sel = new ArrayList<Map<String, String>>();
		Map map = getSubordinateUsers(idUser);
	
		if (!isAdministrator())  {
			// добавим самого пользователя. Пусть смотрит свою статистику
			Map<String, String> elem = new HashMap<String, String>();
			String description = WPC.getInstance().getUsersMgr().getFullNameWorkflowUser(idUser);
	        elem.put(idUser.toString(), description);
	        sel.add(elem);
		}
		
		// А теперь переложим результат в том виде, в котором он нам нужен.	
		Iterator<Long> it = (Iterator<Long>) map.keySet().iterator();			
		while (it.hasNext()) {
           Long id = (Long) it.next();
           String description = (String) map.get(id);
           Map<String, String> elem = new HashMap<String, String>();
           elem.put(id.toString(), description);
           sel.add(elem);
		}
					
		return new ComponentReport("select", "Подчиненные пользователи (и сам пользователь)", sel);		
	}
	
	public static boolean isAdministrator() {
	    // TODO: implement!!!
		if (false)
			return true;
	    else return false;
	}
	
	
	
	
	public static Map getUsersInRoles(List<Long> roles, Long idUser) {
		Map res = new HashMap();

		for (Long r : roles) {

			List<Long> usersList = new ArrayList<Long>();
			List<WorkflowUser> users = WPC.getInstance().getUsersForRole(r);
			if (users != null) {

				for (WorkflowUser u : users) {
					Long idUser2 = u.getIdUser();
					if (!idUser2.equals(idUser)
							) {
						usersList.add(idUser2);
					}
				}
			}

			res.put(r, usersList);

		}

		return res;
	}

}
