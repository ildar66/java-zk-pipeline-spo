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
import org.uit.director.db.dbobjects.Cnst;
import org.uit.director.db.ejb.DBFlexWorkflowCommon;
import org.uit.director.report.ComponentGenerator;
import org.uit.director.report.ComponentReport;
import org.uit.director.report.WorkflowReport;

/**
 * Created by IntelliJ IDEA.
 * User: PD190390
 * Date: 07.04.2006
 * Time: 14:26:04
 * To change this template use File | Settings | File Templates.
 */
public class StageStatisticReport extends WorkflowReport {

    @Override
	public void init(WorkflowSessionContext wsc, List params) {


        nameReport = "Статистика этапов";
        super.init(wsc, params);
        componentList = new ArrayList();

        ComponentReport cR = ComponentGenerator.genTypeProcess();
        componentList.add(cR);

        cR = ComponentGenerator.genPeriod();
        componentList.add(cR);


    }

    @Override
	public void generateReport() {

        try {
            StringBuffer sb = new StringBuffer();
            String idTypeProcess = ComponentGenerator.getSelectedItem(componentList.get(0));
            String dateLeft = ComponentGenerator.getDateForPeriod(componentList, "Период", 0);
            String dateRight = ComponentGenerator.getDateForPeriod(componentList, "Период", 1);
            String nameTypProcess = (String) WPC.getInstance().getData(Cnst.TBLS.typeProcesses, Long.valueOf(idTypeProcess), Cnst.TTypeProc.name);
            //dateRight = ComponentGenerator.setRightDate(dateRight);

            sb.append("<br><center>Статистика этапов процесса \"").
                    append(nameTypProcess).
                    append("\" в период с:").
                    append(dateLeft).append(" по ").
                    append(dateRight).append("</center><br>");


            sb.append("<div class=\"tabledata\">\n").append("<table class=\"sort\" width=\"98%\" border=\"1\" frame=\"box\" cellpadding=\"1\">");
            sb.append("<CAPTION> <BIG> Общая статистика </BIG></CAPTION>");
            sb.append("<thead><tr><th>Этап</th><th>Активных процессов на этапе</th><th>Поступило заданий за период</th>")
                    .append("<th>Завершено заданий за период</th><th>Возвращено заданий за период</th>")
                    .append("<th>Просрочено заданий за период</th><th>Максимальный срок</th><th>Минимальный срок</th>")
                    .append("<th>Средний срок</th>")
                    .append("<th>Средний разброс</th><th>Объем выборки</th></tr></thead><tbody>");
            //старый код
            //String sql = "call DB2ADMIN.REPORT_STAGES_STAT(" + idTypeProcess + ", '" + dateLeft + "', '" + dateRight + "')";
            DBFlexWorkflowCommon dbManager = getWsc().getDbManager().getDbFlexDirector();
            //старый код
            //List res = dbManager.execQuery(sql);
            
            //новый код
            SimpleDateFormat fromFormat = new SimpleDateFormat("dd.MM.yyyy");
    		SimpleDateFormat toFormat = new SimpleDateFormat("yyyy-MM-dd");
            ArrayList<HashMap<String, Object>> params = new ArrayList<HashMap<String, Object>>();
            HashMap<String, Object> paramInfo = new HashMap<String, Object>();
				paramInfo.put("value", Integer.parseInt(idTypeProcess));
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
            List res = dbManager.callQuery(params, "REPORT_STAGES_STAT", 
            		new String[]{"O_ID_STAGE", "O_NUMB_ACTIVE", "O_NUMB_ENTER" ,"O_NUMB_LEAVE", "O_NUMB_GOBACK", "O_NUMB_EXPIRED",
            				"O_MAX", "O_MIN", "O_MIDDLEDATE", "O_DISPERSIA", "O_ALL"});
            
            for (int i = 0; i < res.size(); i++) {
                Map strMap = (Map) res.get(i);
                
                String nameStage = (String) WPC.getInstance().
                        getData(Cnst.TBLS.stages, ((BigDecimal) strMap.get("O_ID_STAGE")).longValue(), Cnst.TStages.name);

                sb.append("<tr><td align=center>").
                        append(nameStage).
                        append("</td><td align=center>").
                        append(strMap.get("O_NUMB_ACTIVE")).
                        append("</td><td align=center>").
                        append(strMap.get("O_NUMB_ENTER")).
                        append("</td><td align=center>").
                        append(strMap.get("O_NUMB_LEAVE")).
                        append("</td><td align=center>").
                        append(strMap.get("O_NUMB_GOBACK")).
                        append("</td><td align=center>").
                        append(strMap.get("O_NUMB_EXPIRED")).
                        append("</td><td align=center>").
                        append(strMap.get("O_MAX") != null ? strMap.get("O_MAX") : "").
                        append("</td><td align=center>").
                        append(strMap.get("O_MIN") != null ? strMap.get("O_MIN") : "").
                        append("</td><td align=center>").
                        append(strMap.get("O_MIDDLEDATE")).
                        append("</td><td align=center>").
                        append(strMap.get("O_DISPERSIA")).
                        append("</td><td align=center>").
                        append(strMap.get("O_ALL")).
                        append("</td></tr>");


            }

            sb.append("</tbody></table><br><br>");


            sb.append("</div>");


            reportHTML += sb.toString();
            isReportGenerate = true;
        } catch (Exception e) {
            e.printStackTrace();
            reportHTML = "<br><br> <center>Ошибка при выполнении запроса</center>";
        }
    }


}

