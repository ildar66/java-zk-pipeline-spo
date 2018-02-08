package org.uit.director.report.mainreports;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uit.director.contexts.WPC;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.db.dbobjects.Attribute;
import org.uit.director.db.dbobjects.BasicAttribute;
import org.uit.director.db.ejb.DBFlexWorkflowCommon;
import org.uit.director.plugins.commonPlugins.actions.ViewProcessWrapper;
import org.uit.director.report.ComponentGenerator;
import org.uit.director.report.ComponentReport;
import org.uit.director.report.WorkflowReport;
import org.uit.director.tasks.ProcessInfo;

public class SearchProcessReport extends WorkflowReport {

	private boolean isWithEdit = false;

	@Override
	public void init(WorkflowSessionContext wsc_, List params) {

		if (params.size() != 0) {
			if (((String) params.get(0)).equalsIgnoreCase("edit")) {
				isWithEdit = true;
			}
		}
		
		if (isWithEdit) {
			nameReport = "Удаление заявки";
		} else {
			nameReport = "Поиск процессов по атрибутам";
		}
		
		super.init(wsc_, params);

		componentList = new ArrayList<ComponentReport>();

		ComponentReport cR;
		/* old version
		if (isWithEdit) {
			cR = ComponentGenerator.genTypeProcessForUser(wsc_.getIdUser());
		} else {
			cR = ComponentGenerator.genTypeProcess();
		}
		*/
		// new version. Kuznetsov Michael, 05DEC2009
		cR = ComponentGenerator.genTypeProcessForUser(wsc_.getIdUser());
		componentList.add(cR);

		cR = new ComponentReport("string", "Поиск", "");
		componentList.add(cR);

		cR = new ComponentReport("check", "Поиск по активным процессам",
				Boolean.TRUE);
		componentList.add(cR);

	}

	@Override
	public void generateReport() throws SQLException {
		if (isReportGenerate) {
			reportHTML = "";
		} else {
			isReportGenerate = true;
		}

		WorkflowSessionContext wsc = getWsc();
		if (wsc != null) {
			

			StringBuffer sb = new StringBuffer();

			String idTypeProcess = ComponentGenerator
					.getSelectedItem(componentList.get(0));

			boolean isUserAdmin = wsc.isUserAdmin(Integer
					.parseInt(idTypeProcess));
			boolean isExistImage = WPC.getInstance()
					.isExistImage(Integer.valueOf(idTypeProcess));

			String searchString = String
					.valueOf(componentList.get(1)
							.getValue());
			if (searchString != null) searchString = searchString.trim(); 
			String isCompleted = (((Boolean) componentList
					.get(2).getValue()).booleanValue() ? "0" : "1");

			//старый код
			//String sql = "call REPORT_SEARCH_PROCESS(" + idTypeProcess
			//		+ ", '" + searchString + "', " + isCompleted + " )";						

			DBFlexWorkflowCommon flexDb = wsc.getDbManager().getDbFlexDirector();
			
			//старый код
			//List findProcesses = flexDb.execQuery(sql);
			//новый код
			ArrayList<HashMap<String, Object>> params = new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object> paramInfo = new HashMap<String, Object>();
					paramInfo.put("value", Integer.parseInt(idTypeProcess));
					paramInfo.put("type", java.sql.Types.INTEGER);
				params.add(paramInfo);
					paramInfo = new HashMap<String, Object>();
					paramInfo.put("value", searchString);
					paramInfo.put("type", java.sql.Types.VARCHAR);
				params.add(paramInfo);
					paramInfo = new HashMap<String, Object>();
					paramInfo.put("value", Integer.parseInt(isCompleted));
					paramInfo.put("type", java.sql.Types.INTEGER);
				params.add(paramInfo);
			List findProcesses = null;
			String CRMClaimName = null;
			try {
				findProcesses = flexDb.callQuery(params, "REPORT_SEARCH_PROCESS", new String[]{"O_ID_PROCESS"});
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			int count = findProcesses.size();

			// Для отображения идентифицирующих атрибутов найдем все атрибуты
			// первого этапа
			// доступные для просмотра
			// String startStageName =
			// BusinessProcessDecider.getStartStageName((XmlDocument)
			// WPC.getInstance().getSchemaMap().
			// get(idTypeProcess));

			// String idStartStage =
			// WPC.getInstance().getIdStageByDescription(startStageName,
			// Integer.parseInt(idTypeProcess));
			// String orderView = wsc.getWpc().getData("stages", idStartStage,
			// "ORDER_VIEW");
			// String orderView = (String)
			// WPC.getInstance().getData(Cnst.TBLS.stages,
			// Integer.parseInt(idStartStage),
			// Cnst.TStages.orderView);

			sb
					.append("<script>function deleteProcess(idProc) {if (confirm(\"ВНИМАНИЕ!!! Удаляется процесс. Подтвердить удаление? \")){document.location = \"delete.process.do?idProcess=\" + idProc; }  }</script>");
			sb
					.append("<center>Слово '")
					.append(searchString)
					.append(
							"' было найдено в атрибутах следующих процессов: </center><br>");
			sb
					.append("<div class=\"tabledata\">\n")
					.append(
							"<table class=\"sort\" width=\"98%\" border=\"1\" frame=\"box\" cellpadding=\"1\">");
			boolean isFirst = true;
			String secondString = "<tr>";

			for (int i = 0; i < count; i++) {

				if (isFirst) {
					sb.append("<thead>");

				}
				sb.append("<tr>");

				if (isFirst) {
					sb.append("<th>№").append("</th>");
					secondString += "<td>" + (i + 1) + "</td>";
				} else {
					sb.append("<td>").append(i + 1).append("</td>");

				}

				Map procMap = (Map) findProcesses.get(i);
				//старый код
				//String idProcess = (String) procMap.get("O_ID_PROCESS");
				//новый код
				long idProcess = ((BigDecimal) procMap.get("O_ID_PROCESS")).longValue();

				//старый код
				//ProcessInfo info = new ProcessInfo(getWsc(), Long
				//		.valueOf(idProcess), idUser, true);
				//новый код
				ProcessInfo info = new ProcessInfo();
				Long idUser = wsc.getIdUser();
				info.init(getWsc(), idProcess, idUser , false);
				
				info.execute();

				/*
				 * List attr = wsc.getDbManager().getDbFlexDirector()
				 * .getAttributes(Long.parseLong(idProcess), false);
				 * AttributesStructList attribList = new AttributesStructList();
				 * attribList.setAttributes(attr, true); List attrOrder =
				 * attribList.getMainAttributes(Integer
				 * .parseInt(idTypeProcess));
				 */

				for (BasicAttribute attrStruct : info.getAttributes().getMainAttributes()) {
					Attribute aAttrOrder = attrStruct.getAttribute();
					String attrValue =
						aAttrOrder.getValueAttributeString() != null ?
								aAttrOrder.getValueAttributeString() : "";
					if (isFirst) {
						sb.append("<th>").append(aAttrOrder.getNameVariable()).append("</th>");
						if (aAttrOrder.getNameVariable().equalsIgnoreCase("Заявка №")) {
					       try {
                               CRMClaimName = flexDb.findCRMClaimName(attrValue);
                               if ((CRMClaimName != null) && (!CRMClaimName.equals(attrValue)))  attrValue = CRMClaimName + " ("+ attrValue + ")";
                           } catch (Exception e) {
                               CRMClaimName = null;
                           }
						}
						secondString += "<td>" + attrValue + "</td>";

					} else {
                          if (aAttrOrder.getNameVariable().equalsIgnoreCase("Заявка №")) {
                               try {
                                   CRMClaimName = flexDb.findCRMClaimName(attrValue);
                                   if ((CRMClaimName != null) && (!CRMClaimName.equals(attrValue)))  attrValue = CRMClaimName + " ("+ attrValue + ")";
                               } catch (Exception e) {
                                   CRMClaimName = null;
                               }
                          }
						sb.append("<td>").append(attrValue).append("</td>");
					}

				}
				if (isFirst) {
					sb.append("<th>Атрибуты</th><th>Хронология</th>");
					if (isExistImage) {
						sb.append("<th>Схема</th>");
					}
					if (isWithEdit && isUserAdmin) {
						sb.append("<th>Удалить</th>");
					}
					sb.append("</tr></thead><tbody>");
					sb.append(secondString);
					isFirst = false;
				}

				sb
						.append(
								"<td align=center><a href=\"report.do?classReport=org.uit.director.report.mainreports.AttributesReport&par1=")
						.append(idProcess)
						.append("&par2=")
						//старый код
						//.append(idTypeProcess)
						// новый код
						.append(idUser.longValue())
						.append(
								"\"><img src=\"resources/ok.gif\" alt=\"Атрибуты процесса\"></a></td>");
				sb
						.append(
								"<td align=center><a href=\"report.do?classReport=org.uit.director.report.mainreports.HistoryReport&par1=")
						.append(idProcess)
						.append(
								"\"><img src=\"resources/ok.gif\" alt=\"Хронология выполнения процесса\"></a></td>");

				if (isExistImage) {
					sb
							.append(
									"<td align=center><a href=\"javascript: openDoc('plugin.action.do?class=").append(ViewProcessWrapper.class.getName()).append("&idProcess=")
							.append(idProcess)
							.append(
									"')\"><img src=\"resources/ok.gif\" alt=\"Просмотр схемы процесса\"></a></td>");
				}

				if (isWithEdit && isUserAdmin) {
					sb
							.append(
									"<td align=center><a href=\"javascript: deleteProcess(")
							.append(idProcess)
							.append(
									")\"><img src=\"resources/ok.gif\" alt=\"Удалить процесс\"></a></td>");
				}

				sb.append("</tr>");
			}

			sb.append("</tbody></table></div>");

			reportHTML += sb.toString();

		} else {
			reportHTML = "<br><br> <center>Неудачное формирование отчета. Поворите попытку еще раз</center>";
		}

	}

	public boolean isWithEdit() {
		return isWithEdit;
	}
}
