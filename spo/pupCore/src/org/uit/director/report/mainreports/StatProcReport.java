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


public class StatProcReport extends WorkflowReport {

    @Override
	public void init(WorkflowSessionContext wsc, List params) {

        nameReport = "Статистика процессов";
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
            Integer idTPI = Integer.valueOf(idTypeProcess);
			String nameTypProcess = (String) WPC.getInstance().getData(Cnst.TBLS.typeProcesses, idTPI.longValue(), Cnst.TTypeProc.name);
            //dateRight = ComponentGenerator.setRightDate(dateRight);

            sb.append("<br><center>Статистика процесса \"").
                    append(nameTypProcess).
                    append("\" в период с:").
                    append(dateLeft).append(" по ").
                    append(dateRight).append("</center><br>");

            sb.append("<div class=\"tabledata\">\n").append("<table width=\"98%\" border=\"1\" frame=\"box\" cellpadding=\"1\">");
            sb.append("<CAPTION> <BIG> Общая статистика </BIG></CAPTION>");
            sb.append("<tr><th>Характеристика</th><th>Число</th></tr>");

            //старый код
            //String sql = "call DB2ADMIN.REPORT_COMMON_STAT(" + idTypeProcess + ", '" + dateLeft + "', '" + dateRight + "')";
            DBFlexWorkflowCommon dbManager = getWsc().getDbManager().getDbFlexDirector();
            //старый код
            //List res = dbManager.execQuery(sql);
            
            //новый код
            SimpleDateFormat fromFormat = new SimpleDateFormat("dd.MM.yyyy");
    		SimpleDateFormat toFormat = new SimpleDateFormat("yyyy-MM-dd");
            ArrayList<HashMap<String, Object>> params = new ArrayList<HashMap<String, Object>>();
            HashMap<String, Object> paramInfo = new HashMap<String, Object>();
				paramInfo.put("value", idTPI);
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
            List res = dbManager.callQuery(params, "REPORT_COMMON_STAT", new String[]{"O_DESCRIPTION", "O_NUMBER"});			
            
            for (int i = 0; i < res.size(); i++) {
                Map strMap = (Map) res.get(i);
                sb.append("<tr><td>").
                        append(strMap.get("O_DESCRIPTION")).
                        append("</td><td>").
                        append(strMap.get("O_NUMBER")).
                        append("</td></tr>");


            }

            sb.append("</table><br><br>");

            sb.append("<table class=\"sort\" width=\"98%\" border=\"1\" frame=\"box\" cellpadding=\"1\">");
            sb.append("<CAPTION> <BIG>Статистика этапов</BIG></CAPTION>");
            sb.append("<thead><tr><th>Этап</th><th>Активных процессов на этапе</th><th>Поступивших заданий за период</th>").
                    append("<th>Завершеных заданий за период</th><th>Возвращенных заданий за период</th>").
                    append("<th>Просроченных заданий за период</th></tr></thead><tbody>");
            
            //старый код
            //String sql = "call DB2ADMIN.REPORT_STAGES_STAT(" + idTypeProcess + ", '" + dateLeft + "', '" + dateRight + "')";
            //res = dbManager.execQuery(sql);
            
            //новый код
            res = dbManager.callQuery(params, "REPORT_STAGES_STAT", 
            		new String[]{"O_ID_STAGE", "O_NUMB_ACTIVE", "O_NUMB_ENTER" ,"O_NUMB_LEAVE", "O_NUMB_GOBACK", "O_NUMB_EXPIRED"});
            for (int i = 0; i < res.size(); i++) {
                Map strMap = (Map) res.get(i);
                String nameStage = (String) WPC.getInstance().
                        getData(Cnst.TBLS.stages, ((BigDecimal) strMap.get("O_ID_STAGE")).longValue(), Cnst.TStages.name);

                sb.append("<tr><td>").
                        append(nameStage).
                        append("</td><td>").
                        append(strMap.get("O_NUMB_ACTIVE")).
                        append("</td><td>").
                        append(strMap.get("O_NUMB_ENTER")).
                        append("</td><td>").
                        append(strMap.get("O_NUMB_LEAVE")).
                        append("</td><td>").
                        append(strMap.get("O_NUMB_GOBACK")).
                        append("</td><td>").
                        append(strMap.get("O_NUMB_EXPIRED")).
                        append("</td></tr>");


            }


            sb.append("</tbody></table>");
            sb.append("</div>");


            reportHTML += sb.toString();
            isReportGenerate = true;
        } catch (Exception e) {
            e.printStackTrace();
            reportHTML = "<br><br> <center>Ошибка при выполнении запроса</center>";
        }
    }


}
