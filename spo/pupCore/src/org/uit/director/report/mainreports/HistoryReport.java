package org.uit.director.report.mainreports;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uit.director.contexts.WPC;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.db.dbobjects.Cnst;
import org.uit.director.db.ejb.DBFlexWorkflowCommon;
import org.uit.director.report.WorkflowReport;

public class HistoryReport extends WorkflowReport {

	@Override
	public void init(WorkflowSessionContext wsc_, List params) {
		String numberDisplay = String.valueOf(params.get(params.size() - 2));
		String mdTask =  String.valueOf(params.get(params.size() - 1));
		nameReport = "Хронология выполнения заявки № " + numberDisplay;
		super.init(wsc_, params);
	}

	@Override
	public void generateReport() {

		try {
			StringBuffer sb = new StringBuffer();
			String idProcess = (String) params.get(0);
			if (idProcess == null) {
				reportHTML = "Ошибка при формировании отчета";
				return;
			}

			String sign = null;
			String content = null;
			if (params.size() > 3) {
				String[] dataForSign = getSign(idProcess, params);
				sign = dataForSign[0];
				content = dataForSign[1];

				if (sign != null && content != null) {

					sb.append("\n<textarea id=\"content\" style=\"visibility:hidden;\">").append(content);
					sb.append("</textarea>\n");

					sb.append("<textarea id=\"sign\" style=\"visibility:hidden;\">").append(sign);
					sb.append("</textarea>");

					// sb.append("<script>alert(document.getElementById('sign').value);</script>");
					// sb.append("<button onclick=\"call
					// verifyContent(document.getElementById('content').value,
					// document.getElementById('sign').value)\"></button>");
					sb.append("<script>verifyContent(document.getElementById(\"content\").value, document.getElementById(\"sign\").value)</script>");
				}

			}

			// старый код
			// String sql = "call DB2ADMIN.REPORT_HISTORY_PROCESS(" + idProcess
			// + ")";
			// List historyProcessList = getWsc().getDbManager()
			// .getDbFlexDirector().execQuery(sql);
			// DBUsers dbUsers = getWsc().getDbManager().getDbUsers();

			// новый код
			SimpleDateFormat dateFormatTime = WPC.getInstance().dateTimeFormat;

			DBFlexWorkflowCommon dbUsers = getWsc().getDbManager()
					.getDbFlexDirector();

			ArrayList<HashMap<String, Object>> params = new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object> paramInfo = new HashMap<String, Object>();
			paramInfo.put("value", idProcess);
			paramInfo.put("type", java.sql.Types.BIGINT);
			params.add(paramInfo);
			ArrayList<HashMap<String, Object>> historyProcessList = dbUsers.callQuery(params,
					"REPORT_HISTORY_PROCESS", new String[] { "ID_EVENT",
							"EVENT_NAME", "EVENT_ID", "EVENT_DATE",
							"EVENT_USER", "IS_TASK", "ID_TP", "ID_TASK",
							"ID_STAGE_TO", "ID_USER", "TYPE_COMPLATION",
							"ID_DEPARTMENT", "DEPARTMENT_NAME", "ID_STATUS", "ID_ASSIGN",
							"ID_ROLE", "ASS_ID_USER_TO", "MAY_REASSIGN",
							"ASS_ID_USER_FROM", "LEVEL_ASSIGN" });

			sb.append("<div class=\"tabledata\">\n")
			  .append("<table width=\"98%\" border=\"1\" frame=\"box\" cellpadding=\"1\">");
			sb.append("<tr><th>Дата события</th><th>Событие</th><th>Описание</th><th>Подразделение</th></tr>");

			for (int i = 0; i < historyProcessList.size(); i++) {

				HashMap<String, Object> histMap = historyProcessList.get(i);
				// старый код
				// String datRjhjktdbx xneDb = (String) histMap.get("O_DATE");
				// String id_stage = (String) histMap.get("O_ID_STAGE");
				// новый код
				String dateDb = histMap.get("EVENT_DATE") == null ? ""
						: dateFormatTime.format((java.sql.Timestamp) histMap.get("EVENT_DATE"));
				String nameEvent = (String) histMap.get("EVENT_NAME");
				String description = getDesciption(histMap);
				// ЭЦП -- не используется
				//String urlSigum = getUrlSignumEvent(histMap);				
				String departmentName = getDepartmentName(histMap);
				
				sb.append("<tr><td>").append(dateDb).append("</td><td>")
				  .append(nameEvent).append("</td><td>")
				  .append(description).append("</td><td>")
				  .append(departmentName).append("</td></tr>");
			}

			sb.append("</table></div>");
			reportHTML += sb.toString();
			isReportGenerate = true;

		} catch (Exception e) {
			e.printStackTrace();
			reportHTML = "<br><br> <center>Ошибка при выполнении запроса</center>";
		} /*
			 * finally { try { getWsc().getDbManager().closeDBUsers(); } catch
			 * (Exception e) { } }
			 */
	}

	private String getUrlSignumEvent(Map histMap) {

		/*
		 * String nameStage = "-"; if (id_stage != -1) { nameStage = (String)
		 * WPC.getInstance().getData(Cnst.TBLS.stages, id_stage,
		 * Cnst.TStages.name); }
		 * 
		 * int flag = 0; if (nameEvent.indexOf("Принят в работу") == 0) { flag =
		 * 1; }
		 * 
		 * if (nameEvent.indexOf("Работа завершена ") == 0) { flag = 2; }
		 * 
		 * if (flag > 0) {
		 * 
		 * urlSigum = "<a
		 * href=\"report.do?classReport=org.uit.director.report.mainreports.HistoryReport&par1=" +
		 * idProcess + "&par2=" + id_stage + "&par3=" + dateDb + "&par4=" + flag +
		 * "&par5=" + userEventId + "\">Проверить</a>"; }
		 */
		return "";
	}

	private String getDepartmentName(Map histMap) {
		try{
			if (histMap.get("DEPARTMENT_NAME") == null){
				Long userEventId = ((BigDecimal) histMap.get("EVENT_USER")).longValue();
				return WPC.getInstance().getUsersMgr().getInfoUserByIdUser(userEventId).getDepartament().getShortName();
			}
			return (String) histMap.get("DEPARTMENT_NAME");
		}catch(Exception e){
			return "ошибка получения Подразделения "+e.getMessage();
		}
	}
	private String getDesciption(Map histMap) {

		try{
		long id_stage = histMap.get("ID_STAGE_TO") == null ? -1
				: ((BigDecimal) histMap.get("ID_STAGE_TO")).longValue();
		String nameStage = (String) WPC.getInstance().getData(Cnst.TBLS.stages,
				id_stage, Cnst.TStages.name);

		int idTypeEvent = ((BigDecimal) histMap.get("EVENT_ID")).intValue();

		BigDecimal isExpired = (BigDecimal) histMap.get("TYPE_COMPLATION");

		Long userEventId = ((BigDecimal) histMap.get("EVENT_USER")).longValue();
		String userEvent = WPC.getInstance().getUsersMgr()
				.getFullNameWorkflowUser(userEventId);

		BigDecimal idUserTaskBD = (BigDecimal) histMap.get("ID_USER");
		Long idUserTask = idUserTaskBD == null ? null : idUserTaskBD
				.longValue();
		String userTask = null;
		if (idUserTaskBD != null) {
			userTask = WPC.getInstance().getUsersMgr().getFullNameWorkflowUser(
					idUserTask);

		}

		boolean isTaskEvent = ((BigDecimal) histMap.get("IS_TASK")).intValue() == 1 ? true
				: false;
		BigDecimal idRole = (BigDecimal) histMap.get("ID_ROLE");

		BigDecimal assIdUserTo = (BigDecimal) histMap.get("ASS_ID_USER_TO");
		BigDecimal assIdUserFrom = (BigDecimal) histMap.get("ASS_ID_USER_FROM");

		StringBuffer sb = new StringBuffer();

		if (isTaskEvent) {
			switch (idTypeEvent) {
			case 1: { // поступление
				sb.append(" на операцию <i><u>").append(nameStage).append(
						"</i></u>");
				break;
			}
			case 2: { // взятие
				sb.append(" на операцию <i><u>").append(nameStage).append(
						"</i></u> пользователем <i><u>").append(userEvent)
						.append("</i></u> в работу");
				if (userTask != null && !userEvent.equals(userTask)) {
					sb.append(" за пользователя <i><u>").append(userTask)
							.append("</i></u>");
				}
				break;
			}
			case 3: { // завершение
				sb.append(" на этапе <i><u>").append(nameStage).append(
						"</i></u> пользователем <i><u>").append(userEvent)
						.append("</i></u>");
				if (isExpired != null) {
					if (isExpired.intValue() == 0) {
						sb.append(" в срок");
					}
					if (isExpired.intValue() == 1) {
						sb.append(" позже срока");
					}
				}
				if (userTask != null && !userEvent.equals(userTask)) {
					sb.append(" за пользователя <i><u>").append(userTask)
							.append("</i></u>");
				}
				break;
			}

			case 4: // откат
			case 5: {// отмена
				sb.append(" с операции <i><u>").append(nameStage).append(
						"</i></u> пользователем <i><u>").append(userEvent)
						.append("</i></u>");
				if (userTask != null && !userEvent.equals(userTask)) {
					sb.append(" за пользователя <i><u>").append(userTask)
							.append("</i></u>");
				}
				break;
			}
			case 8: { // ожидание
				sb.append(" на  операции '").append(nameStage).append("'");
				break;
			}
			default:

			}
		} else {
			switch (idTypeEvent) {
			case 1: // создание
			case 2: // приостановление
			case 9: // возобновление
			case 3: // отмена
			case 5: // удаление
				sb.append(" пользователем <i><u>").append(userEvent).append(
						"</i></u>");
				break;
			case 4: // завершение
				sb.append(" заявки пользователем <i><u>").append(userEvent)
						.append("</i></u>");
				break;

			case 6: // назначение исполнителя на роль
				String idRoleStr = WPC.getInstance().findRole(
						idRole.longValue()).getName();
				String assIdUserToStr = WPC.getInstance().getUsersMgr()
						.getFullNameWorkflowUser(assIdUserTo.longValue());
				sb.append("назначение пользователем <i><u>").append(userEvent)
						.append("</i></u> исполнителя <i><u>").append(
								assIdUserToStr).append(
								"</i></u> на роль <i><u>").append(idRoleStr)
						.append("</i></u>");
				break;

			case 7: // перенезначение исполнителя на роль
				idRoleStr = WPC.getInstance().findRole(
						idRole.longValue()).getName();
				assIdUserToStr = WPC.getInstance().getUsersMgr()
						.getFullNameWorkflowUser(assIdUserTo.longValue());
				sb.append("назначение пользователем <i><u>").append(userEvent)
						.append("</i></u> исполнителя <i><u>").append(
								assIdUserToStr).append(
								"</i></u> на роль <i><u>").append(idRoleStr)
						.append("</i></u>");
				break;
			case 8: // удаление исполнителя
				assIdUserToStr = assIdUserTo == null ? "" : WPC.getInstance()
						.getUsersMgr().getFullNameWorkflowUser(
								assIdUserTo.longValue());
				idRoleStr = idRole == null ? "" : WPC.getInstance().findRole(
						idRole.longValue()).getName();
				sb.append(" <i><u> ").append(assIdUserToStr).append(
						"</i></u> на роли <i><u>").append(idRoleStr).append(
						"</i></u> пользователем <i><u>").append(userEvent)
						.append("</i></u>");
				break;

			}

		}

		return sb.toString();
		}catch (Exception e){
			return "не удалось получить описание "+e.getMessage();
		}
	}

	private String[] getSign(String idProcess, List params)
			throws SQLException, ParseException, RemoteException {

		String[] res = new String[2];

		String idStage = null;
		String data = null;
		String idUser = null;

		idStage = (String) params.get(1);
		data = (String) params.get(2);
		int flag = Integer.valueOf((String) params.get(3)).intValue();
		idUser = (String) params.get(4);

		String sqlSign = "";

		// задание на принятие
		if (flag == 1) {
			sqlSign = "select t.SIGNATURE_T as SIGNUM from TASKS t where t.ID_PROCESS="
					+ idProcess
					+ " and t.ID_STAGE_TO="
					+ idStage
					+ " and t.DATEOFTAKING='"
					+ data
					+ "' and t.ID_USER='"
					+ idUser + "'";

		}

		// задание на завершение
		if (flag == 2) {
			sqlSign = "select t.SIGNATURE_C as SIGNUM from TASKS t where t.ID_PROCESS="
					+ idProcess
					+ " and t.ID_STAGE_TO="
					+ idStage
					+ " and t.DATEOFCOMPLATION='"
					+ data
					+ "' and t.ID_USER='"
					+ idUser + "'";
		}

		List resSql = getWsc().getDbManager().getDbFlexDirector().execQuery(
				sqlSign);

		if (resSql.size() > 0) {
			SimpleDateFormat dbTimeFormat = WPC.getInstance().dateTimeDBFormat;
			SimpleDateFormat dataFormat = WPC.getInstance().dateFormat;
			Date data_ = dbTimeFormat.parse(data);
			String dataCorr = dataFormat.format(data_);

			res[0] = (String) ((Map) resSql.get(0)).get("SIGNUM");
			res[1] = buildContent(idUser, dataCorr, idProcess);
		}

		return res;
	}

	private String buildContent(String idUser, String data, String idProcess) {

		StringBuffer sb = new StringBuffer();

		sb.append("<DOCUMENT><ITEM NAME=\"USER\"><VALUE>").append(idUser)
				.append("</VALUE></ITEM><ITEM NAME=\"DATA\"><VALUE>").append(
						data).append(
						"</VALUE></ITEM><ITEM NAME=\"IDPROC\"><VALUE>").append(
						idProcess).append("</VALUE></ITEM></DOCUMENT>");

		return sb.toString();
	}

	private String replaseToNameUser(String description) {

		Long userName = extractIdUser(description);
		String fullName = WPC.getInstance().getUsersMgr()
				.getFullNameWorkflowUser(userName);

		return description.replaceFirst("#" + userName + "#", " (" + fullName
				+ ")");

	}

	public static Long extractIdUser(String str) {

		int beg = str.indexOf('#');
		if (beg == -1) {
			return null;
		}
		int end = str.indexOf('#', beg + 1);
		if (end == -1) {
			return null;
		}

		return Long.valueOf(str.substring(beg + 1, end));

	}
}
