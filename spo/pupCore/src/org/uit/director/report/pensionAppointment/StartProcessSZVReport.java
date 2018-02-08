package org.uit.director.report.pensionAppointment;


import java.util.ArrayList;
import java.util.List;

import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.report.ComponentGenerator;
import org.uit.director.report.ComponentReport;
import org.uit.director.report.WorkflowReport;


public class StartProcessSZVReport extends WorkflowReport {


    @Override
	public void init(WorkflowSessionContext wsc, List params) {


        nameReport = "Использование данных конвертации для назначения пенсии";
        super.init(wsc, params);

        componentList = new ArrayList();

        ComponentReport cR = ComponentGenerator.genTypeProcess();
        componentList.add(cR);

        cR = ComponentGenerator.genPeriod();
        componentList.add(cR);

        cR = new ComponentReport("check", "Поиск по активным процессам", Boolean.TRUE);
        componentList.add(cR);

        cR = new ComponentReport("check", "Поиск по завершенным процессам", Boolean.FALSE);
        componentList.add(cR);


    }

    @Override
	public void generateReport() {
      /*  try {

           StringBuffer sb = new StringBuffer();

            String idTypeProcess = ComponentGenerator.getSelectedItem((ComponentReport) componentList.get(0));
            String dateLeft = ComponentGenerator.getDateForPeriod(componentList, "Период", 0);
            String dateRight = ComponentGenerator.getDateForPeriod(componentList, "Период", 1);
            
            boolean isActive = ((Boolean) ((ComponentReport)componentList.get(2)).getValue()).booleanValue();
            boolean isCompleted = ((Boolean) ((ComponentReport)componentList.get(3)).getValue()).booleanValue();

            dateRight = ComponentGenerator.setRightDate(dateRight);

            String sql = "select p.ID_PROCESS from DB2ADMIN.PROCESSES p  where p.ID_TYPE_PROCESS=" + idTypeProcess +
                    " and (DATE (p.DATEOFCOMMING)) between '" + dateLeft + "' and '" + dateRight + "' and p.ID_STATUS<>4 ";
            if (isActive && !isCompleted) {
                sql += "and p.DATEOFCOMPLETION is null ";
            }

            if (isCompleted && !isActive) {
                sql += "and p.DATEOFCOMPLETION is not null ";
            }

            if (!isCompleted && !isActive) {
                reportHTML = "<br><br> <center>Укажите критерий поиска (по активным или по завершенным)</center>";
                isReportGenerate = true;
                return;

            }


            DBFlexWorkflowLocal dbFlex = getWsc().getDbManager().getDbFlexDirector();
            List res = dbFlex.execQuery(sql);
            String nameTypProcess = (String) WPC.getInstance().getData(Cnst.TBLS.typeProcesses, idTypeProcess, Cnst.TTypeProc.name);

            sb.append("<div class=\"tabledata\">\n").append("<table width=\"98%\" border=\"1\" frame=\"box\" cellpadding=\"1\">");
            sb.append("<CAPTION> <BIG> Запущенные процессы \"").
                    append(nameTypProcess).
                    append("\" в период с ").
                    append(dateLeft).
                    append(" по ").
                    append(dateRight).
                    append("</BIG></CAPTION>");


            int count = 1;
            boolean flag = true;
            StringBuffer sbWithSZV = new StringBuffer();
            StringBuffer sbWithoutSZV = new StringBuffer();
            int countMainAttrs = 0;
            int countWithSZV = 0;
            int countWithoutSZV = 0;


            for (int i = 0; i < res.size(); i++) {
                  Map map = (Map) res.get(i);
                String idProcess = (String) map.get("ID_PROCESS");

                AttributesStructList allAttrs = new AttributesStructList(dbFlex.getAttributes(Long.parseLong(idProcess),false));

                List attr = allAttrs.getMainAttributes(Integer.parseInt(idTypeProcess));
                countMainAttrs = attr.size();

                StringBuffer sbTmp = null;
                String val = allAttrs.getStringValueByName("Использованы сведения СЗВ-К");

                if (val != null) {
                    if (val.equalsIgnoreCase("НЕТ")) {
                        sbTmp = sbWithoutSZV;
                        countWithoutSZV++;
                    } else {
                        sbTmp = sbWithSZV;
                        countWithSZV++;
                    }
                }


                sbTmp.append("<tr>");
                if (flag) sbTmp.append("<th>№</th>");

                String str = "";
                for (int j = 0; j < attr.size(); j++) {
                   Attribute attribute = (Attribute) attr.get(j);
                    if (flag) {

                        sbTmp.append("<th>").
                                append(attribute.getNameVariable()).
                                append("</th>");
                    }

                    str += "<td>" + attribute.getValueAttributeString() + "</td>";

                }

                if (flag) {
                    sbTmp.append("</tr><tr>");
                    flag = false;
                }

                str = "<td>" + (count++) + "</td>" + str;
                sbTmp.append(str).append("</tr>");


            }

            sb.append("<tr><th align=center colspan=").
                    append(countMainAttrs + 1).
                    append(">Использованы сведения СЗВ-К</th></tr>");
            sb.append(sbWithSZV.toString());


            sb.append("<tr><th align=center colspan=").
                    append(countMainAttrs + 1).
                    append(">Не использованы сведения СЗВ-К</th></tr>");
            sb.append(sbWithoutSZV.toString());


            sb.append("</table>").
                    append("<br><br><table>").
                    append("<tr><th>Всего процессов с использованием сведений СЗВ-К</th>").
                    append("<th>Всего процессов без использования сведений СЗВ-К</th><th>Всего</th></tr>").
                    append("<tr><td align=center>").
                    append(countWithSZV).
                    append("</td><td align=center>").
                    append(countWithoutSZV).
                    append("</td><td align=center>").
                    append(res.size()).
                    append("</td></tr>").
                    append("</table></div>");

            reportHTML += sb.toString();
            isReportGenerate = true;
        } catch (Exception e) {
            e.printStackTrace();
            reportHTML = "<br><br> <center>Ошибка при выполнении запроса</center>";
        }*/

    }

}
