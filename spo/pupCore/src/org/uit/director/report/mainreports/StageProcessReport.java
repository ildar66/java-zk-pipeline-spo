package org.uit.director.report.mainreports;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uit.director.contexts.WPC;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.db.dbobjects.Attribute;
import org.uit.director.db.dbobjects.AttributeStruct;
import org.uit.director.db.dbobjects.BasicAttribute;
import org.uit.director.db.dbobjects.Cnst;
import org.uit.director.db.ejb.DBFlexWorkflowCommon;
import org.uit.director.report.ComponentGenerator;
import org.uit.director.report.ComponentReport;
import org.uit.director.report.WorkflowReport;
import org.uit.director.tasks.ProcessInfo;

import ru.md.spo.util.Config;

public class StageProcessReport extends WorkflowReport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *  Инициализация отчета. Зададим все параметры для фильтров
	 */
	@Override
	public void init(WorkflowSessionContext wsc_, List params) {

		nameReport = "Работа на этапе";
		super.init(wsc_, params);
		componentList = new ArrayList<ComponentReport>();

		ComponentReport cR = ComponentGenerator.genTypeProcess();
		// устанавливаем зависымый компонент
		cR.setAddition("Этап");  componentList.add(cR);

		cR = ComponentGenerator.genStages(); componentList.add(cR);

		cR = ComponentGenerator.genPeriod(); componentList.add(cR);

		cR = new ComponentReport("check", "Поиск по активным этапам", Boolean.TRUE); componentList.add(cR);

		cR = new ComponentReport("check", "Поиск по завершенным этапам", Boolean.FALSE); componentList.add(cR);

		cR = ComponentGenerator.genScript(ComponentReport.referensType.stagesInTypProcess); componentList.add(cR);
	}

	/**
	 *  Получим выбранные пользователем значения фильтров. И выполним сам отчет, передав ему
	 *  требуеемы параметры
	 */
	@Override
	public void generateReport() {

		try {
			StringBuffer sb = new StringBuffer();

			String idTypeProcess = ComponentGenerator
					.getSelectedItem(componentList.get(0));
			String idStage = ComponentGenerator.getSelectedItem(componentList.get(1));
			String dateLeft = (String) ((List) componentList.get(2).getValue()).get(0);
			String dateRight = (String) ((List) componentList.get(2).getValue()).get(1);
			String isCompleted = (((Boolean) componentList.get(4).getValue()).booleanValue() ? "1" : "0");
			String isActive = (((Boolean) componentList.get(3).getValue()).booleanValue() ? "1" : "0");
			// dateRight = ComponentGenerator.setRightDate(dateRight);


			if (isActive.equals("0") && isCompleted.equals("0")) {
				reportHTML = "<br><br>Не заданы условия поиска. Установите признак поиска по завершенным или по законченным процессам<br><br>";
				isReportGenerate = true;
				return;
			}

			// старый код
			// String sql = "call DB2ADMIN.REPORT_STAGE_PROCESS(" +
			// idTypeProcess + "," +
			// idStage + ",'" + dateLeft + "','" + dateRight + "'," +
			// isCompleted
			// + "," + isActive + ")";
			DBFlexWorkflowCommon director = getWsc().getDbManager().getDbFlexDirector();
			// старый код
			// List res = director.execQuery(sql);

			// новый код
			SimpleDateFormat fromFormat = new SimpleDateFormat("dd.MM.yyyy");
			SimpleDateFormat toFormat = new SimpleDateFormat("yyyy-MM-dd");
			ArrayList<HashMap<String, Object>> params = new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object> paramInfo = new HashMap<String, Object>();
			Integer idTPInt = Integer.valueOf(idTypeProcess);
			paramInfo.put("value", idTPInt);
			paramInfo.put("type", java.sql.Types.INTEGER);
			params.add(paramInfo);
			paramInfo = new HashMap<String, Object>();
			Long idStL = Long.valueOf(idStage);
			paramInfo.put("value", idStL);
			paramInfo.put("type", java.sql.Types.INTEGER);
			params.add(paramInfo);
			paramInfo = new HashMap<String, Object>();
			paramInfo.put("value", Date.valueOf(toFormat.format(fromFormat
					.parse(dateLeft))));
			paramInfo.put("type", java.sql.Types.DATE);
			params.add(paramInfo);
			paramInfo = new HashMap<String, Object>();
			paramInfo.put("value", Date.valueOf(toFormat.format(fromFormat
					.parse(dateRight))));
			paramInfo.put("type", java.sql.Types.DATE);
			params.add(paramInfo);
			paramInfo = new HashMap<String, Object>();
			paramInfo.put("value", Integer.valueOf(isCompleted));
			paramInfo.put("type", java.sql.Types.INTEGER);
			params.add(paramInfo);
			paramInfo = new HashMap<String, Object>();
			paramInfo.put("value", Integer.valueOf(isActive));
			paramInfo.put("type", java.sql.Types.INTEGER);
			params.add(paramInfo);
			List res = director.callQuery(params, "REPORT_STAGE_PROCESS",
					new String[] { "O_ID_PROCESS", "O_STATUS" });

			String nameTypProcess = (String) WPC.getInstance().getData(
					Cnst.TBLS.typeProcesses, idTPInt.longValue(),
					Cnst.TTypeProc.name);
			String nameStage = (String) WPC.getInstance().getData(
					Cnst.TBLS.stages, idStL, Cnst.TStages.name);
			sb
					.append("<div class=\"tabledata\">\n")
					.append(
							"<table class=\"sort\" width=\"98%\" border=\"1\" frame=\"box\" cellpadding=\"1\">");
			sb.append("<CAPTION> <BIG> Работа на этапе  \"").append(
					nameStage == null ? "-" : nameStage).append(
					"\" для процесса \"").append(nameTypProcess).append(
					"\" в период с ").append(dateLeft).append(" по ").append(
					dateRight).append("</BIG></CAPTION>");

			int count = 1;
			boolean flag = true;
			for (int i = 0; i < res.size(); i++) {
				Map map = (Map) res.get(i);
				// старый код
				// String idProcess = (String) map.get("O_ID_PROCESS");
				// int status = Integer.parseInt((String) map.get("O_STATUS"));
				// новый код
				long idProcess = ((BigDecimal) map.get("O_ID_PROCESS"))
						.longValue();
				int status = ((BigDecimal) map.get("O_STATUS")).intValue();
				String statusStr = "";
				switch (status) {
				case 0: {
					statusStr = "во входящих";
					break;
				}
				case 1: {
					statusStr = "в работе";
					break;
				}
				case 2: {
					statusStr = "завершенный";
				}
				}

				// старый код
				// ProcessInfo info = new ProcessInfo(getWsc(),
				// Long.valueOf(idProcess), idUser, true);
				// новый код
				ProcessInfo info = new ProcessInfo();
				Long idUser = getWsc().getIdUser();
				info.init(getWsc(), idProcess, idUser , false);
				info.execute();

				/*
				 * List allAttrs =
				 * director.getAttributes(Long.parseLong(idProcess), false);
				 * AttributesStructList attribList = new
				 * AttributesStructList(allAttrs); List mainAttrs =
				 * attribList.getMainAttributes(Integer.parseInt(idTypeProcess));
				 */

				if (flag) {
					sb.append("<thead>");
				}
				sb.append("<tr>");
				if (flag) {
					sb.append("<th>№</th>");
				}

				String str = "";
				for (BasicAttribute attrStruct : info.getAttributes()
						.getMainAttributes()) {

					Attribute attribute = attrStruct.getAttribute();
					String CRMClaimName = null;
					if (flag) {
						sb.append("<th>").append(attribute.getNameVariable())
								.append("</th>");
					}
					String attrValue =
					    attribute.getValueAttributeString() != null ? attribute.getValueAttributeString() : "";
					if (attribute.getNameVariable().equalsIgnoreCase("Заявка №")) {
                        try {
                            CRMClaimName = director.findCRMClaimName(attrValue);
                            if ((CRMClaimName != null) && (!CRMClaimName.equals(attrValue)))  attrValue = CRMClaimName + " ("+ attrValue + ")";
                        } catch (Exception e) {
                            CRMClaimName = null;
                        }
                   }
				   str += "<td>" + attrValue + "</td>";
				}

				if (flag) {
					sb.append("<th>Статус</th><th>Атрибуты</th><th>Хронология</th></tr><tr></thead><tbody>");
					flag = false;
				}

				str = "<td>" + (count++) + "</td>" + str;

				sb
						.append(str)
						.append("<td>")
						.append(statusStr)
						.append("</td>")
						.append(
								"<td align=center><a href=\"report.do?classReport=org.uit.director.report.mainreports.AttributesReport&par1=")
						.append(idProcess)
						.append("&par2=")
						.
						// старый код
						// append(idTypeProcess).
						// новый код
						append(idUser.longValue())
						.append(
								"\"><img src=\"resources/ok.gif\" alt=\"Атрибуты процесса\"></a></td>")
						.append(
								"<td align=center><a href=\"report.do?classReport=org.uit.director.report.mainreports.HistoryReport&par1=")
						.append(idProcess)
						.append(
								"\"><img src=\"resources/ok.gif\" alt=\"Хронология выполнения процесса\"></a></td>")
						.append("</tr>");
			}

			sb.append("</tbody></table></div>");

			reportHTML += sb.toString();
			isReportGenerate = true;
		}

		catch (Exception e)

		{
			e.printStackTrace();
			reportHTML = "<br><br> <center>Ошибка при выполнении запроса</center>";
		}
	}
}
