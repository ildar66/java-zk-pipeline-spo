package org.uit.director.report.mainreports;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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

import com.vtb.domain.TaskHeader;

public class PerformanceReport extends WorkflowReport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void init(WorkflowSessionContext wsc, List params) {

		if (nameReport.equals("Стандартный отчет")) {
			nameReport = "Производительность процессов";
		}
		super.init(wsc, params);

		componentList = new ArrayList<ComponentReport>();

		ComponentReport cR = ComponentGenerator.genTypeProcess();
		cR.setAddition("Анализировать по этап");
		componentList.add(cR);

		cR = ComponentGenerator.genStages();
		cR.setDescription("Анализировать по этап");
		componentList.add(cR);

		cR = ComponentGenerator.genPeriod();
		componentList.add(cR);

		cR = new ComponentReport("string", "Срок выполнения (дней)", "10");
		componentList.add(cR);

		cR = new ComponentReport("check", "Завершенные процессы", Boolean.TRUE);
		componentList.add(cR);

		cR = ComponentGenerator.genScript(ComponentReport.referensType.stagesInTypProcess);
		componentList.add(cR);

	}

	@Override
	public void generateReport() {
		genReportByProcedure("DB2ADMIN.REPORT_PERFOMANCE_PROCESS");
	}

	@SuppressWarnings("unchecked")
	protected void genReportByProcedure(String proc) {

		try {
			String idTypeProcess = ComponentGenerator
					.getSelectedItem(ComponentGenerator.getItemByName(
							componentList, "Тип процесса"));
			String dateLeft = 
			    ComponentGenerator.getDateForPeriod(componentList, "Период", 0);
			String dateRight = 
			    ComponentGenerator.getDateForPeriod(componentList, "Период", 1);
			String period = String.valueOf(
			    ComponentGenerator.getItemByName(componentList, "Срок выполнения (дней)").getValue());
			String isCompleted = ((Boolean) ComponentGenerator.getItemByName(
					componentList, "Завершенные процессы").getValue())
					.booleanValue() ? "1" : "0";
			String idStage = ComponentGenerator
					.getSelectedItem(ComponentGenerator.getItemByName(
							componentList, "Анализировать по этап"));

			StringBuffer sb = new StringBuffer();
			sb
					.append("<SCRIPT language=JavaScript>")
					.append(
							"dd=document;NS=(dd.layers)?1:0;IE=(dd.all)?1:0;DOM=(dd.getElementById)?1:0;\t")
					.append("flagDelay=false;flagNotDelay=false;\t")
					.append(
							"function setob(L){if (IE)obg=dd.all[''+L+''].style;else if (DOM)obg=dd.getElementById(''+L+'').style;}\t")
					.append(
							"function dsh(L){if (!NS){setob(L);obg.display='block'}}\t")
					.append(
							"function dhd(L){if (!NS){setob(L);obg.display='none'}}\t")
					.append(
							"function doHide(L) {var flag;if (L=='delay') flag = flagDelay;else flag = flagNotDelay;\t")
					.append(
							"if (flag==true) {dsh(L);} else {dhd(L);}if (L=='delay') flagDelay = !flagDelay;\n")
					.append("\telse flagNotDelay = !flagNotDelay;}").append(
							"</SCRIPT>");

			DBFlexWorkflowCommon dbManager = getWsc().getDbManager()
					.getDbFlexDirector();
			// старый код
//			StringBuffer sql = new StringBuffer().append("call ").append(proc)
//					.append("(").append(idTypeProcess).append(",").append(
//							idStage).append(", '").append(dateLeft).append(
//							"', '").append(dateRight).append("', ").append(
//							period).append(", ").append(isCompleted).append(
//							", 1)");
			// новый код
            SimpleDateFormat fromFormat = new SimpleDateFormat("dd.MM.yyyy");
    		SimpleDateFormat toFormat = new SimpleDateFormat("yyyy-MM-dd");
    		ArrayList<HashMap<String, Object>> params = new ArrayList<HashMap<String, Object>>();
    		HashMap<String, Object> paramInfo = new HashMap<String, Object>();
				Integer idTypeProcessInt = Integer.valueOf(idTypeProcess);
				paramInfo.put("value", idTypeProcessInt);
				paramInfo.put("type", java.sql.Types.INTEGER);
			params.add(paramInfo);
				paramInfo = new HashMap<String, Object>();
				paramInfo.put("value", Integer.valueOf(idStage));
				paramInfo.put("type", java.sql.Types.INTEGER);
			params.add(paramInfo);
				paramInfo = new HashMap<String, Object>();
				paramInfo.put("value", Date.valueOf( toFormat.format( fromFormat.parse(dateLeft) ) ));
				paramInfo.put("type", java.sql.Types.DATE);
			params.add(paramInfo);
				paramInfo = new HashMap<String, Object>();
				paramInfo.put("value", Date.valueOf( toFormat.format( fromFormat.parse(dateRight) ) ));
				paramInfo.put("type", java.sql.Types.DATE);
			params.add(paramInfo);
				paramInfo = new HashMap<String, Object>();
				paramInfo.put("value", Integer.valueOf(period));
				paramInfo.put("type", java.sql.Types.INTEGER);
			params.add(paramInfo);
				paramInfo = new HashMap<String, Object>();
				paramInfo.put("value", Integer.valueOf(isCompleted));
				paramInfo.put("type", java.sql.Types.INTEGER);
			params.add(paramInfo);
				paramInfo = new HashMap<String, Object>();
				paramInfo.put("value", 1);
				paramInfo.put("type", java.sql.Types.INTEGER);
			params.add(paramInfo);
			List delay = dbManager.callQuery(params, "REPORT_PERFOMANCE_PROCESS", new String[]{"O_ID_PROCESS", "O_DAYS"});
			
			// просроченные процессы
			
			// старый код
			//List delay = dbManager.execQuery(sql.toString());
			
			Object[] arrDelay = delay.toArray();
			Arrays.sort(arrDelay, new Compare());

			// старый код
//			sql = new StringBuffer().append("call ").append(proc).append("(")
//					.append(idTypeProcess).append(",").append(idStage).append(
//							", '").append(dateLeft).append("', '").append(
//							dateRight).append("', ").append(period)
//					.append(", ").append(isCompleted).append(", 0)");
			
			// новый код
            params.remove(params.size()-1);			
				paramInfo = new HashMap<String, Object>();
				paramInfo.put("value", 0);
				paramInfo.put("type", java.sql.Types.INTEGER);
			params.add(paramInfo);
			List notDelay = dbManager.callQuery(params, "REPORT_PERFOMANCE_PROCESS", new String[]{"O_ID_PROCESS", "O_DAYS"});
			
			// непросроченные процессы
			// старый код
			//List notDelay = dbManager.execQuery(sql.toString());
			Object[] arrNotDelay = notDelay.toArray();
			Arrays.sort(arrNotDelay, new Compare());

			int countNotDelay = notDelay.size();
			int countDelay = delay.size();

			String nameTypProcess = (String) WPC.getInstance()
					.getData(Cnst.TBLS.typeProcesses, idTypeProcessInt.longValue(),
							Cnst.TTypeProc.name);

			sb
					.append("<div class=\"tabledata\">\n")
					.append("<CENTER></CENTER>")
					.append(
							"<table width=\"98%\" border=\"1\" frame=\"box\" cellpadding=\"1\">");
			sb
					.append("<CAPTION> <BIG> Производительность  процесса \"")
					.append(nameTypProcess)
					.append("\" в период с ")
					.append(dateLeft)
					.append(" по ")
					.append(dateRight)
					.append(" в срок выполнения ")
					.append(period)
					.append(" дней по ")
					.append(
							isCompleted.equals("1") ? "завершенным"
									: "активным")
					.append(" процессам </BIG></CAPTION>")
					.append(
							"<tr><th>Просроченные</th><th>Непросроченные</th></tr>")
					.append("<tr><td align=center>")
					.append(countDelay)
					.append(" (")
					.append(
							Math
									.round(((float) countDelay / (countDelay + countNotDelay)) * 100))
					.append(" %)").append("</td>");
			sb
					.append("<td align=center>")
					.append(countNotDelay)
					.append(" (")
					.append(
							Math
									.round(((float) countNotDelay / (countDelay + countNotDelay)) * 100))
					.append(" %)")
					.append("</td></tr>")
					.append(
							"<tr><td align=center><INPUT onclick=\"doHide('delay');\" type=button value=\"Показать\\Скрыть\"></td>")
					.append(
							"<td align=center><INPUT onclick=\"doHide('notDelay');\" type=button value=\"Показать\\Скрыть\"></td></tr>");
			sb.append("</table><br><br>");

			sb.append("<p id=\"delay\" style=DISPLAY: block>");

			sb
					.append("<table class=\"sort\" width=\"98%\" border=\"1\" frame=\"box\" cellpadding=\"1\">");
			sb.append("<CAPTION> <BIG>Просроченные процессы</BIG></CAPTION>");

			Long idUser = getWsc().getIdUser();
			sb.append(getRecordProcessData(arrDelay, idTypeProcess, idUser));
			sb.append("</tbody></table>");

			sb.append("</p><script>doHide('delay');</script>");

			sb.append("<p id=\"notDelay\" style=DISPLAY: block>");
			// append("Не просроченные процессы").
			sb
					.append("<table class=\"sort\" width=\"98%\" border=\"1\" frame=\"box\" cellpadding=\"1\">");
			sb.append("<CAPTION> <BIG>Непросроченные процессы</BIG></CAPTION>");
			// sb.append("<tr><th>Процесс</th><th>Число дней</th></tr>");
			sb.append(getRecordProcessData(arrNotDelay, idTypeProcess, idUser));

			sb.append("</tbody></table>");
			sb.append("</p><script>doHide('notDelay');</script>");
			sb.append("</div>");

			reportHTML += sb.toString();
			isReportGenerate = true;
		} catch (Exception e) {
			e.printStackTrace();
			reportHTML = "<br><br> <center>Ошибка при выполнении запроса</center>";
		}

	}

	protected String getRecordProcessData(Object[] arrDelay,
			 String idTypeProcess, Long idUser)
			throws RemoteException {

		StringBuffer sb = new StringBuffer();
		boolean isFirst = true;
		int idx = 1;

		for (Object element : arrDelay) {

			Map dbMap = (Map) element;

			// старый код
			// String idProcess = (String) dbMap.get("O_ID_PROCESS");
			// новый код
			long idProcess = ((BigDecimal) dbMap.get("O_ID_PROCESS")).longValue();

			// finds combinedNumber
			StringBuffer sql = new StringBuffer();
            sql.append(   "select distinct m.crmcode, m.mdtask_number from mdtask m ");
            sql.append(   "where  m.id_pup_process = " + idProcess);
            String combinedNumber = null;
            try {
                DBFlexWorkflowCommon dbManager = getWsc().getDbManager().getDbFlexDirector();
                List res = dbManager.execQuery(sql.toString());
                if (!res.isEmpty()) {
                    String crmcode = (String) ((Map)res.get(0)).get("CRMCODE");
                    String mdtaskNumber = (String) ((Map)res.get(0)).get("MDTASK_NUMBER");
                    combinedNumber = TaskHeader.generateCombinedNumber(crmcode, mdtaskNumber);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            
			// старый код			
			//ProcessInfo info = new ProcessInfo(getWsc(), Long.valueOf(idProcess), idUser, true);
			// новый код
			ProcessInfo info = new ProcessInfo();
			info.init(getWsc(), idProcess, idUser, false);
			info.execute();
			
			List<BasicAttribute> attrOrdered = info.getAttributes().getMainAttributes();

			//String days = (String) dbMap.get("O_DAYS");
			long days = ((BigDecimal) dbMap.get("O_DAYS")).longValue();

			String secStr = "";
			if (isFirst) {
				sb.append("<thead>");
			}
			sb.append("<tr>");
			if (isFirst) {
				sb.append("<th>№</th>");
			}
			for (int j = 0; j < attrOrdered.size(); j++) {
				Attribute attribute = attrOrdered.get(j).getAttribute();

				if (isFirst) {
					sb.append("<th>").append((attribute).getNameVariable())
							.append("</th>");
				}

				if ("Заявка №".equals(attribute.getNameVariable()))
                     secStr += "<td>" + combinedNumber + "</td>";
				else secStr += "<td>" + (attribute).getValueAttributeString() + "</td>";
			}
			secStr = "<td>" + (idx++) + "</td>" + secStr;

			if (isFirst) {
				sb
						.append("<th>Все атрибуты</th><th>Хронология</th><th>Число дней</th></tr></thead><tr><tbody>");
				isFirst = false;
			}

			sb.append(secStr);
			sb
					.append(
							"<td align=center><a href=\"report.do?classReport=org.uit.director.report.mainreports.AttributesReport&par1=")
					.append(idProcess)
					.append("&par2=")
					.append(idTypeProcess)
					.append(
							"\"><img src=\"resources/ok.gif\" alt=\"Атрибуты процесса\"></a></td>");
			sb
					.append(
							"<td align=center><a href=\"report.do?classReport=org.uit.director.report.mainreports.HistoryReport&par1=")
					.append(idProcess)
					.append(
							"\"><img src=\"resources/ok.gif\" alt=\"История процесса\"></a></td>");

			sb.append("<td align=center><big>").append(days).append(
					"</big></td></tr>");

		}

		return sb.toString();

	}

	public class Compare implements Comparator {

		public int compare(Object o1, Object o2) {
			Map m1 = (Map) o1;
			Map m2 = (Map) o2;

			int i1 = 0;
			if (m1.get("O_DAYS") instanceof String) {
				i1 = Integer.parseInt((String) m1.get("O_DAYS"));
			} else {
				i1 = ((BigDecimal) m1.get("O_DAYS")).intValue();
			}

			int i2 = 0;

			if (m2.get("O_DAYS") instanceof String) {
				i2 = Integer.parseInt((String) m2.get("O_DAYS"));
			} else {
				i2 = ((BigDecimal) m2.get("O_DAYS")).intValue();
			}

			if (i1 > i2) {
				return 0;
			}
			return 1;
		}
	}
}
