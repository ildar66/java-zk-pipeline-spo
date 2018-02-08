package org.uit.director.report.pensionAppointment;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.uit.director.calendar.BusinessCalendar;
import org.uit.director.calendar.Duration;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.report.ComponentGenerator;
import org.uit.director.report.ComponentReport;
import org.uit.director.report.mainreports.PerformanceReport;


public class PerformancePensReport1 extends PerformanceReport {
    @Override
	public void init(WorkflowSessionContext wsc, List params) {

        if (nameReport.equals("Стандартный отчет")) {
			nameReport = "Производительность процессов с учетом выходных дней";
		}
        super.init(wsc, params);
        componentList = new ArrayList();

        ComponentReport cR = ComponentGenerator.genTypeProcess();
        componentList.add(cR);

        cR = ComponentGenerator.genPeriod();
        componentList.add(cR);

        cR = new ComponentReport("string", "Срок выполнения (дней)", "10");
        componentList.add(cR);

        cR = new ComponentReport("check", "Завершенные процессы", Boolean.TRUE);
        componentList.add(cR);


    }

    @Override
	public void generateReport() {
        genByCallProcedure(0);
    }

    protected void genByCallProcedure(int proc) {
        /*try {
            String idTypeProcess = ComponentGenerator.getSelectedItem((ComponentReport) componentList.get(0));
            String dateLeft = ComponentGenerator.getDateForPeriod(componentList, "Период", 0);
            String dateRight = ComponentGenerator.getDateForPeriod(componentList, "Период", 1);
            String period = String.valueOf(((ComponentReport)componentList.get(2)).getValue());
            String isCompleted = (((Boolean) ((ComponentReport)componentList.get(3)).getValue()).booleanValue() ? "1" : "0");
            //dateRight = ComponentGenerator.setRightDate(dateRight);

            StringBuffer sb = new StringBuffer();


            sb.append("<SCRIPT language=JavaScript>").
                    append("dd=document;NS=(dd.layers)?1:0;IE=(dd.all)?1:0;DOM=(dd.getElementById)?1:0;\t").
                    append("flagDelay=false;flagNotDelay=false;\t").
                    append("function setob(L){if (IE)obg=dd.all[''+L+''].style;else if (DOM)obg=dd.getElementById(''+L+'').style;}\t").
                    append("function dsh(L){if (!NS){setob(L);obg.display='block'}}\t").
                    append("function dhd(L){if (!NS){setob(L);obg.display='none'}}\t").
                    append("function doHide(L) {var flag;if (L=='delay') flag = flagDelay;else flag = flagNotDelay;\t").
                    append("if (flag==true) {dsh(L);} else {dhd(L);}if (L=='delay') flagDelay = !flagDelay;\n").
                    append("\telse flagNotDelay = !flagNotDelay;}").
                    append("</SCRIPT>");

            DBFlexWorkflowCommon dbManager = getWsc().getDbManager().getDbFlexDirector();
            StringBuffer sql = new StringBuffer().
                    append("call DB2ADMIN.REPORT_PERFOMANCE_PENS_PROCESS1").
                    append("(").
                    append(idTypeProcess).
                    append(", '").
                    append(dateLeft).
                    append("', '").
                    append(dateRight).
                    append("', ").
                    append(isCompleted).
                    append(",").
                    append(proc).
                    append(")");

            List allProcess = dbManager.execQuery(sql.toString());
            editCountDays(allProcess);


            Collections.sort(allProcess, new PerformanceReport.Compare());


            int periodInt = Integer.parseInt(period);
            int allTasks = allProcess.size();
            // индекс границы между просроченными и не просроченными делами
            int border = allTasks;
            for (int i = 0; i < allTasks; i++) {

                int days = Integer.parseInt((String) ((Map)allProcess.get(i)).get("O_DAYS"));
                if (days <= periodInt) {
                    border = i;
                    break;
                }
            }

            String nameTypProcess = (String) WPC.getInstance().getData(Cnst.TBLS.typeProcesses, idTypeProcess, Cnst.TTypeProc.name);

            sb.append("<div class=\"tabledata\">\n").
                    append("<CENTER></CENTER>").
                    append("<table width=\"98%\" border=\"1\" frame=\"box\" cellpadding=\"1\">");
            sb.append("<CAPTION> <BIG> Производительность  процесса \"").
                    append(nameTypProcess).
                    append("\" в период с ").
                    append(dateLeft).
                    append(" по ").
                    append(dateRight).
                    append(" в срок выполнения ").
                    append(period).
                    append(" дней по ").
                    append(isCompleted.equals("1") ? "завершенным" : "активным").
                    append(" процессам с учетом выходных дней</BIG></CAPTION>").
                    append("<tr><th>Просроченные</th><th>Непросроченные</th></tr>").
                    append("<tr><td align=center>").
                    append(border).
                    append(" (").
                    append(Math.round(((float) (border) / allTasks) * 100)).
                    append(" %)").
                    append("</td>");
            sb.append("<td align=center>").
                    append(allTasks - border).
                    append(" (").
                    append(Math.round((1 - ((float) (border) / allTasks)) * 100)).
                    append(" %)").
                    append("</td></tr>").
                    append("<tr><td align=center><INPUT onclick=\"doHide('delay');\" type=button value=\"Показать\\Скрыть\"></td>").
                    append("<td align=center><INPUT onclick=\"doHide('notDelay');\" type=button value=\"Показать\\Скрыть\"></td></tr>");
            sb.append("</table><br><br>");


            sb.append("<p id=\"delay\" style=DISPLAY: block>");

            sb.append("<table class=\"sort\" width=\"98%\" border=\"1\" frame=\"box\" cellpadding=\"1\">");
            sb.append("<CAPTION> <BIG>Просроченные процессы</BIG></CAPTION>");

            if (border != 0) sb.append(getRecordProcessData(allProcess.subList(0, border).toArray(), dbManager, idTypeProcess));
            sb.append("</tbody></table>");

            sb.append("</p><script>doHide('delay');</script>");

            sb.append("<p id=\"notDelay\" style=DISPLAY: block>");
//                    append("Не просроченные процессы").
            sb.append("<table class=\"sort\" width=\"98%\" border=\"1\" frame=\"box\" cellpadding=\"1\">");
            sb.append("<CAPTION> <BIG>Непросроченные процессы</BIG></CAPTION>");
//            sb.append("<tr><th>Процесс</th><th>Число дней</th></tr>");
            if (border != allTasks) sb.append(getRecordProcessData(allProcess.subList(border, allTasks).toArray(), dbManager, idTypeProcess));

            sb.append("</tbody></table>");
            sb.append("</p><script>doHide('notDelay');</script>");
            sb.append("</div>");

            reportHTML += sb.toString();
            isReportGenerate = true;
        } catch (Exception e) {
            e.printStackTrace();
            reportHTML = "<br><br> <center>Ошибка при выполнении запроса</center>";
        }*/
    }

    private void editCountDays(List allProcess) {
        BusinessCalendar bCalendar = new BusinessCalendar();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = 0; i < allProcess.size(); i++) {
             Map map = (Map) allProcess.get(i);
            try {
                int days = Integer.parseInt((String) map.get("O_DAYS"));
//                String dateStart = (String) map.get("O_DATE_START");
                String dateComplation = (String) map.get("O_DATE_COMPLATION");
//                Date dateStartD = format.parse(dateStart);
                Date dateComplationtD = format.parse(dateComplation);

//                Date dateRes = bCalendar.add(dateComplationtD, new Duration("-1 day"));
                int minus = 0;
//                if (bCalendar.isHoliday(dateRes)) {
//                    minus++;
                while (bCalendar.isHoliday(bCalendar.add(dateComplationtD, new Duration("-" + (minus + 1) + " day")))) {
                    minus++;
                }
                days -= minus;
                map.put("O_DAYS", String.valueOf(days));

//                }


            } catch (ParseException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }


        }
    }


}
