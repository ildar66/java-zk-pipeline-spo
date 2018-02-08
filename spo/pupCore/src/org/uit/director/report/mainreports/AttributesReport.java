package org.uit.director.report.mainreports;

import java.util.List;

import org.uit.director.contexts.WPC;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.db.dbobjects.Attribute;
import org.uit.director.db.dbobjects.BasicAttribute;
import org.uit.director.db.dbobjects.Cnst;
import org.uit.director.db.dbobjects.VariablesType;
import org.uit.director.db.dbobjects.WorkflowMessage;
import org.uit.director.db.ejb.DBFlexWorkflowCommon;
import org.uit.director.report.WorkflowReport;
import org.uit.director.tasks.AttributesStructList;
import org.uit.director.tasks.ProcessInfo;

public class AttributesReport extends WorkflowReport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void init(WorkflowSessionContext wsc_, List params) {
		nameReport = "Список атрибутов процесса";
		super.init(wsc_, params);

	}

	@Override
	public void generateReport() {

		try {
			StringBuffer sb = new StringBuffer();
			String idProcess = (String) params.get(0);
			String idUserS = (String) params.get(1);
			Long idUserL = Long.valueOf(idUserS);
			if (idProcess == null) {
				reportHTML = "Ошибка при формировании отчета";
				return;
			}

			WorkflowSessionContext wsc = getWsc();
			DBFlexWorkflowCommon dbFlex = wsc.getDbManager().getDbFlexDirector();

			ProcessInfo info = new ProcessInfo();
			info.init(wsc, Long.valueOf(idProcess), idUserL, false);
			info.execute();

			/*
			 * List attr = dbFlex.getAttributes(Long.parseLong(idProcess),
			 * true); AttributesStructList atrList = new AttributesStructList();
			 * atrList.setAttributes(attr, true);
			 */

			sb
					.append("<div class=\"tabledata\">\n")
					.append(
							"<table width=\"98%\" border=\"1\" frame=\"box\" cellpadding=\"1\">");

			AttributesStructList attrs = info.getAttributes();
			
			for (BasicAttribute attr : attrs.getAttributesOrder()) {			
				Attribute atrMap = attr.getAttribute();
				String nameAttribute = atrMap.getNameVariable();
				if (!WPC.getInstance().isVariableDirectVar(nameAttribute)) {

					VariablesType type = atrMap.getTypeVar();
					sb.append("<tr><th align=\"left\">").append(nameAttribute)
							.append("</th><td>");
					Object value = atrMap.getValueAttributeString();
					if (value instanceof String) {
						if (((String) value).equalsIgnoreCase("true")) {
							value = "ДА";
						}
						if (((String) value).equalsIgnoreCase("false")) {
							value = "НЕТ";
						}

						if (type.value == VariablesType.URL && !value.equals("")) {
							value = "<a href=\"" + value + "\">Перейти</a>";
						}

					}
					if (value==null) value = "";
					sb.append(value).append("</td></tr>");
				}

			}
			sb.append("</table><br><br>");

			List<WorkflowMessage> comments = dbFlex.getComments(Long
					.parseLong(idProcess));

			sb
					.append("<table width=\"98%\" border=\"1\" frame=\"box\" cellpadding=\"1\">");
			sb
					.append("<CAPTION> <BIG> Комментарии к процессу </BIG></CAPTION>");
			sb
					.append("<tr><th>С этапа</th><th>Отправитель</th><th>Комментарий</th></tr>");

			for (WorkflowMessage wm : comments) {
				// старый код
				// String idStage = (String) map.get("O_ID_STAGE_FROM");
				// новый код
				long idStage = wm.getIdStageFrom();
				Long idUser = wm.getIdUserFrom();
				String comm = wm.getTextMessage();

				String nameStage = (String) WPC.getInstance().getData(
						Cnst.TBLS.stages, idStage, Cnst.TStages.name);
				sb.append("<tr><td>").append(nameStage).append("</td><td>")
						.append(idUser).append("</td><td>").append(comm)
						.append("</td></tr>");

			}

			sb.append("</table></div>");

			reportHTML += sb.toString();
			isReportGenerate = true;
		} catch (Exception e) {
			e.printStackTrace();
			reportHTML = "<br><br> <center>Ошибка при выполнении запроса</center>";
		}

	}

	/*
	 * private List getValidOrder(List attr, String order) { List res = new
	 * ArrayList(); Object[] mass = attr.toArray(); Arrays.sort(mass, new
	 * AttributesComparator(order)); for (Object map : mass) { res.add(map); }
	 * return res; }
	 */

}
