package org.uit.director.report.mainreports;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.uit.director.contexts.WPC;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.db.dbobjects.Attribute;
import org.uit.director.db.dbobjects.BasicAttribute;
import org.uit.director.db.dbobjects.Cnst;
import org.uit.director.db.ejb.DBFlexWorkflowCommon;
import org.uit.director.report.ComponentGenerator;
import org.uit.director.report.ComponentReport;
import org.uit.director.report.WorkflowReport;
import org.uit.director.tasks.ProcessInfo;

import com.vtb.domain.TaskHeader;

public class UserWorksReport extends WorkflowReport {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(UserWorksReport.class.getName());

	@Override
	public void init(WorkflowSessionContext wsc_, List params) {

		nameReport = "Работа подчиненных пользователей (и своя работа)";
		super.init(wsc_, params);
		componentList = new ArrayList<ComponentReport>();
		
		// 0. Подразделение	
		// TODO: если пользователь -- администратор, то показать ВСЕ подразделения
		ComponentReport department  = null;
		if (wsc_.isAdmin())  {
			department = ComponentGenerator.genAllDepartmentsForUser(wsc_.getIdUser());
			department.setAddition("Пользователь");	
		}
			
		else
			department = ComponentGenerator.genCurrentDepartmentsForUser(wsc_.getIdUser()); 

		// 1. Пользователь (точнее, список пользователей, подчиненных данному пользователю)
		// TODO: Добавить НАШ справочник, а не ижевский.
		//ComponentReport user = ComponentGenerator.genWorkflowUsers();
		// TODO: если пользователь -- администратор, то показать ВСЕХ пользователей подразделения
		ComponentReport users = null;
		if (wsc_.isAdmin())  {			
			users = ComponentGenerator.genWorkflowUsers();
			// Добавить как зависимую переменную (завивит от подразделения, в котором просматривает пользователь)
			ComponentGenerator.genScript(ComponentReport.referensType.usersInDepartment);
		} else
			try {
				users = ComponentGenerator.genSubordinateUsers(wsc_.getIdUser());
			} catch (RemoteException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
				e.printStackTrace();
			} //.genScript(ComponentReport.referensType.subordinateUsersForUser, wsc_.getIdUser());			
		
		// 2. Процесс
		ComponentReport process = ComponentGenerator.genTypeProcess(true);
		
		// 3. Даты 
		ComponentReport date = ComponentGenerator.genPeriod();

		// 4. флажок
		ComponentReport flag1 = new ComponentReport("check", "Поиск по активным процессам", Boolean.TRUE);

		// 5. флажок
		ComponentReport flag2 = new ComponentReport("check", "Поиск по завершенным процессам",Boolean.FALSE);


		// Соберем все вместе
		componentList.add(department);
		componentList.add(users);
		componentList.add(process);		
		componentList.add(date);
		componentList.add(flag1);
		componentList.add(flag2);		
		
		if (wsc_.isAdmin())  {			
			// Добавить как зависимую переменную (завивит от подразделения, в котором просматривает пользователь)
			ComponentReport cR = ComponentGenerator.genScript(ComponentReport.referensType.usersInDepartment);
			componentList.add(cR);		
		}		
	}

	@Override
	public void generateReport() {

		try {
			StringBuffer sb = new StringBuffer();
			
			//department
			String idDepartment = ComponentGenerator.getSelectedItem(componentList.get(0));
			logger.info("UserWorksReport. idDepartment: " + idDepartment);			

			// user
			String idUser = ComponentGenerator.getSelectedItem(componentList.get(1));
			Long idUserLong = Long.valueOf(idUser);
			String fullUserName = WPC.getInstance().getUsersMgr().getFullNameWorkflowUser(idUserLong);
			logger.info("UserWorksReport. idUser: " + idUser);
			logger.info("UserWorksReport. fullUserName: " + fullUserName);

			// process
			String typeProcess = ComponentGenerator.getSelectedItem((ComponentReport)componentList.get(2));
			logger.info("UserWorksReport. typeProcess: " + typeProcess);

			// date
			String dateLeft = (String) ((List) componentList.get(3).getValue()).get(0);
			String dateRight = (String) ((List) componentList.get(3).getValue()).get(1);
			dateRight = ComponentGenerator.setRightDate(dateRight);
			logger.info("UserWorksReport. dateLeft: " + dateLeft);
			logger.info("UserWorksReport. dateRight: " + dateRight);
			
			//flag1, flag2 
			boolean isActive = ((Boolean) componentList.get(4).getValue()).booleanValue();
			boolean isCompleted = ((Boolean) componentList.get(5).getValue()).booleanValue();
			logger.info("UserWorksReport. isActive: " + isActive);
			logger.info("UserWorksReport. isCompleted: " + isCompleted);
			
			if (!isActive && !isCompleted) {
				reportHTML = "<br><br>Не заданы условия поиска. Установите признак поиска по завершенным или по законченным процессам<br><br>";
				isReportGenerate = true;
				return;
			}

			sb.append("<br><center>Данные пользователя '").append(fullUserName)
					.append("' в период с:").append(dateLeft).append(" по ")
					.append(dateRight).append("</center><br>");

			sb
					.append("<div class=\"tabledata\">\n")
					.append(
							"<table width=\"98%\" border=\"1\" frame=\"box\" cellpadding=\"1\">");
			sb.append("<CAPTION> <BIG>Выполненная работа</BIG></CAPTION>");
			sb.append("<tr><th>Этап</th><th>Дата</th><th>Событие</th></tr>");

			// новый код
			SimpleDateFormat fromFormat = WPC.getInstance().dateFormat;
			SimpleDateFormat dateFormatDb = new SimpleDateFormat("dd.MM.yyyy HH.mm.ss");

			// Получим отфильтрованный список заявок, обрабатываемых данным пользователем в данный промежуток времени 
			StringBuffer sql = new StringBuffer();

			sql.append(   "select distinct p.id_process, m.crmcode, m.mdtask_number from task_events te, tasks t ")
			   .append(   "inner join processes p on (p.id_process=t.id_process ");
			// если выбран тип процесса (т.е. не запись "Все процессы")
			if ( !( typeProcess.equals("0") || typeProcess.equals("-1")))
					sql.append("and p.id_type_process = ").append(typeProcess);			
			sql.append(   " ) ");					
			   
			sql.append(   "inner join mdtask m on m.id_pup_process = p.id_process ");
			sql.append(   "where t.id_task=te.id_task ");

			if (isActive && !isCompleted) {
				sql.append("and p.id_status=1 ");
			}

			if (!isActive && isCompleted) {
				sql.append("and p.id_status=4 ");
			}

			if (isActive && isCompleted) {
				sql.append("and (p.id_status=4 or p.id_status=1) ");
			}

			sql.append(" and te.id_user=").append(idUserLong.longValue())
					.append(" and (te.DATE_EVENT between TO_DATE('").append(
							dateFormatDb.format(fromFormat.parse(dateLeft)))
					.append("', 'DD.MM.YYYY HH24.MI.SS') and TO_DATE('").append(
							dateFormatDb.format(fromFormat.parse(dateRight)))
					.append("', 'DD.MM.YYYY HH24.MI.SS'))");

			sql.append(" order by 3 desc");
			
			logger.info("UserWorksReport. sql = " + sql.toString());
			DBFlexWorkflowCommon dbManager = getWsc().getDbManager().getDbFlexDirector();
			List res = dbManager.execQuery(sql.toString());
			logger.info("UserWorksReport. results = " + res.size());			
			Map<String, String> orderAttributesByTypeProcess = new HashMap<String, String>();
			
			for (int i = 0; i < res.size(); i++) {
				Map strMap = (Map) res.get(i);
				String idProcess = (String) strMap.get("ID_PROCESS");
				String crmcode = (String) strMap.get("CRMCODE");
				String mdtaskNumber = (String) strMap.get("MDTASK_NUMBER");

				logger.info("UserWorksReport. Results. idProcess: " + idProcess);				
				ProcessInfo info = new ProcessInfo();
				info.init(getWsc(), Long.valueOf(idProcess), idUserLong, false);
				info.execute();
				
				// finds combinedNumber
				String combinedNumber = TaskHeader.generateCombinedNumber(crmcode, mdtaskNumber);

				ArrayList<HashMap<String, Object>> params = new ArrayList<HashMap<String, Object>>();
				HashMap<String, Object> paramInfo = new HashMap<String, Object>();
				paramInfo.put("value", Integer.parseInt(idProcess));
				paramInfo.put("type", java.sql.Types.INTEGER);
				params.add(paramInfo);
				paramInfo = new HashMap<String, Object>();
				paramInfo.put("value", idUser);
				paramInfo.put("type", java.sql.Types.VARCHAR);
				params.add(paramInfo);

				List processInfoList = dbManager.callQuery(params,
						"REPORT_USER_WORKS", new String[] { "O_ID_TASK",
								"O_TYPE_PROCESS", "O_ID_STAGE_TO",
								"O_DATE_EVENT", "O_TASK_TYPE_NAME",
								"O_TASK_STATUS", "O_TYPE_COMPLATION" });

				boolean fl = true;
				for (int j = 0; j < processInfoList.size(); j++) {
					Map map = (Map) processInfoList.get(j);
					long idTypeProcess = ((BigDecimal) map.get("O_TYPE_PROCESS")).longValue();

					long idStage = ((BigDecimal) map.get("O_ID_STAGE_TO")).longValue();

					String date = map.get("O_DATE_EVENT") == null ? ""
							: dateFormatDb.format((Timestamp) map.get("O_DATE_EVENT"));
					String event = map.get("O_TASK_TYPE_NAME") == null ? ""
							: (String) map.get("O_TASK_TYPE_NAME");
					long typeComplation = map.get("O_TYPE_COMPLATION") == null ? -1
							: ((BigDecimal) map.get("O_TYPE_COMPLATION")).longValue();

					String nameStage = (String) WPC.getInstance().getData(Cnst.TBLS.stages, idStage, Cnst.TStages.name);

					if (fl) {
							  sb.append(
										"<tr> <th colspan=3><table><center><caption>Данные процесса <small>")
								.append(
										"<a href=\"report.do?classReport=org.uit.director.report.mainreports.HistoryReport&par1=")
								.append(idProcess)
								.append("\"> (Хронология)</a>")
								.append(
										"<a href=\"report.do?classReport=org.uit.director.report.mainreports.AttributesReport&par1=")
								.append(idProcess)
								.append("&par2=")
								.append(idUser).append("\">(Атрибуты)</a>")
								.append("</small></caption></center><br>");
						Iterator<BasicAttribute> it = info.getAttributes().getIterator();
						String secStr = "";
						sb.append("<tr>");

						while (it.hasNext()) {
							Attribute attrO = it.next().getAttribute();
							if (attrO.isMain()) {
								if ("Заявка №".equals(attrO.getNameVariable())) {
								    sb.append("<th>").append((attrO).getNameVariable()).append("</th>");
                                    secStr += "<td>" + combinedNumber + "</td>";
								} else {
								    sb.append("<th>").append((attrO).getNameVariable()).append("</th>");
								    secStr += "<td>" + (attrO).getValueAttributeString() + "</td>";
								}
							}

						}
						sb.append("</tr><tr>").append(secStr).append("</tr>");

						sb.append("</table></th></tr>");

						fl = false;
					}

					if (event.equalsIgnoreCase("завершение")) {
						if (typeComplation == 0) {
							event += " в срок";
						} else {
							event += " позже срока";
						}
					}

					sb.append("<tr><td>").append(nameStage).append("</td><td>")
							.append(convertDateDbToValid(date)).append(
									"</td><td>").append(event).append(
									"</td></tr>");

				}

			}

			sb.append("</table>");
			sb.append("</div>");

			reportHTML += sb.toString();
			isReportGenerate = true;
		} catch (Exception e) {
			e.printStackTrace();
			reportHTML = "<br><br> <center>Ошибка при выполнении запроса</center>";
		}
	}

	private String convertDateDbToValid(String dateOfComming) {

		SimpleDateFormat dateFormatDb = new SimpleDateFormat(
				"dd.MM.yyyy HH.mm.ss");
		SimpleDateFormat dateFormat = WPC.getInstance().dateTimeFormat;
		try {
			return dateFormat.format(dateFormatDb.parse(dateOfComming));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";

	}

}
