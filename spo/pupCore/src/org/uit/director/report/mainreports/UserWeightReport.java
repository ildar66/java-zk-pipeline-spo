package org.uit.director.report.mainreports;


import java.math.BigDecimal;
import java.sql.Date;
import java.text.NumberFormat;
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


public class UserWeightReport extends WorkflowReport {

    @Override
	public void init(WorkflowSessionContext wsc_, List params) {


        nameReport = "Распределение работы";
        super.init(wsc_, params);
        componentList = new ArrayList();


        ComponentReport cR = ComponentGenerator.genTypeProcess();
        // устанавливаем зависымый компонент
        cR.setAddition("Этап");
        componentList.add(cR);

//        cR = new ComponentReport("select", "Этап", null);
        cR = ComponentGenerator.genStages();
        componentList.add(cR);

        cR = ComponentGenerator.genPeriod();
        componentList.add(cR);

        cR = ComponentGenerator.genScript(ComponentReport.referensType.stagesInTypProcess);
        componentList.add(cR);

    }

    @Override
	public void generateReport() {

        try {
            StringBuffer sb = new StringBuffer();

            String idTypeProcess = ComponentGenerator.getSelectedItem(componentList.get(0));
            String idStage = ComponentGenerator.getSelectedItem(componentList.get(1));
            String dateLeft = (String) ((List) (componentList.get(2)).getValue()).get(0);
            String dateRight = (String) ((List)(componentList.get(2)).getValue()).get(1);
            //dateRight = ComponentGenerator.setRightDate(dateRight);

            Integer idTPI = Integer.valueOf(idTypeProcess);
			String nameTypProcess = (String) WPC.getInstance().getData(Cnst.TBLS.typeProcesses, idTPI.longValue(), Cnst.TTypeProc.name);
            Long idStL = Long.valueOf(idStage);
			String nameStage = (String) WPC.getInstance().getData(Cnst.TBLS.stages, idStL, Cnst.TStages.name);

            sb.append("<div class=\"tabledata\">\n").append("<table class=\"sort\" width=\"98%\" border=\"1\" frame=\"box\" cellpadding=\"1\">");
            sb.append("<CAPTION> <BIG>Распределение заданий на этапе \"").
                    append(nameStage == null ? "-" : nameStage).
                    append("\" для процесса \"").
                    append(nameTypProcess).
                    append("\" в период с ").
                    append(dateLeft).
                    append(" по ").
                    append(dateRight).
                    append("</BIG></CAPTION>");
            sb.append("<thead><tr><th>Пользователь</th><th>Число принятых заданий</th><th>Число завершенных заданий</th>").
                    append("<th>Число возвращенных заданий</th><th>Доля принимаемых заданий (%)</th>").
                    append("<th>Доля завершаемых заданий (%)</th>").
                    append("<th>Доля возвращенных заданий (%)</th></tr></thead><tbody>");

            // старый код
//           StringBuffer sql = new StringBuffer();
//            sql.append("call DB2ADMIN.REPORT_USER_WEIGHT(").
//                    append(idTypeProcess).
//                    append(",").
//                    append(idStage).
//                    append(",").
//                    append("'").
//                    append(dateLeft).
//                    append("','").
//                    append(dateRight).
//                    append("')");

            DBFlexWorkflowCommon dbManager = getWsc().getDbManager().getDbFlexDirector();
            //новый код
            SimpleDateFormat fromFormat = new SimpleDateFormat("dd.MM.yyyy");
    		SimpleDateFormat toFormat = new SimpleDateFormat("yyyy-MM-dd");
            ArrayList<HashMap<String, Object>> params = new ArrayList<HashMap<String, Object>>();
            HashMap<String, Object> paramInfo = new HashMap<String, Object>();
				paramInfo.put("value", idTPI);
				paramInfo.put("type", java.sql.Types.INTEGER);
			params.add(paramInfo);
				paramInfo = new HashMap<String, Object>();
				paramInfo.put("value", idStL);
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
			List res = dbManager.callQuery(params, "REPORT_USER_WEIGHT", new String[]{"O_USER_NAME", "O_NUM_TAKING", "O_NUM_COMPLETED", "O_NUM_BACK_TASK"});
            
			// старый код
			//List res = dbManager.execQuery(sql.toString());

            int allTaking = 0;
            int allCompleted = 0;
            int allBack = 0;

            for (int i = 0; i < res.size(); i++) {
                Map strMap = (Map) res.get(i);
                // старый код
                //int numTaking = Integer.parseInt((String) strMap.get("O_NUM_TAKING"));
                //int numCompleted = Integer.parseInt((String) strMap.get("O_NUM_COMPLETED"));
                //int numBackTask = Integer.parseInt((String) strMap.get("O_NUM_BACK_TASK"));
                
                // новый код
                int numTaking = ((BigDecimal) strMap.get("O_NUM_TAKING")).intValue();
                int numCompleted = ((BigDecimal) strMap.get("O_NUM_COMPLETED")).intValue();
                int numBackTask = ((BigDecimal) strMap.get("O_NUM_BACK_TASK")).intValue();

                allTaking += numTaking;
                allCompleted += numCompleted;
                allBack += numBackTask;

            }

            NumberFormat numberFormat = NumberFormat.getInstance();
            for (int i = 0; i < res.size(); i++) {
                Map strMap = (Map) res.get(i);
                sb.append("<tr>");                
                long idUser = ((BigDecimal) strMap.get("O_USER_NAME")).longValue();
                // старый код
                //int numTaking = Integer.parseInt((String) strMap.get("O_NUM_TAKING"));
                //int numCompleted = Integer.parseInt((String) strMap.get("O_NUM_COMPLETED"));
                //int numBackTask = Integer.parseInt((String) strMap.get("O_NUM_BACK_TASK"));
                
                // новый код
                int numTaking = ((BigDecimal) strMap.get("O_NUM_TAKING")).intValue();
                int numCompleted = ((BigDecimal) strMap.get("O_NUM_COMPLETED")).intValue();
                int numBackTask = ((BigDecimal) strMap.get("O_NUM_BACK_TASK")).intValue();

                String fullName = WPC.getInstance().getUsersMgr().getFullNameWorkflowUser(idUser);


                sb.append("<td align=center>").
                        append(fullName).
                        append("</td><td align=center>").append(numTaking).
                        append("</td><td align=center>").append(numCompleted).
                        append("</td><td align=center>").append(numBackTask).
                        append("</td><td align=center>").append((allTaking == 0 ? "" :
                        numberFormat.format((float) 100 * numTaking / allTaking))).
                        append("</td><td align=center>").append(allCompleted == 0 ? "" :
                        numberFormat.format((float) 100 * numCompleted / allCompleted)).
                        append("</td><td align=center>").append(allBack == 0 ? "" :
                        numberFormat.format((float) 100 * numBackTask / allBack)).
                        append("</td>");


                sb.append("</tr>");

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
