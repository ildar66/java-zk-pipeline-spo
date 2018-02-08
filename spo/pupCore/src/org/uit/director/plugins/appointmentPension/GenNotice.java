package org.uit.director.plugins.appointmentPension;

import java.util.Calendar;
import java.util.List;

import org.uit.director.contexts.WPC;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.plugins.PluginInterface;
import org.uit.director.tasks.TaskInfo;


public class GenNotice implements PluginInterface {

	private WorkflowSessionContext wsc;


    public void init(WorkflowSessionContext wsc, List params) {
        this.wsc = wsc;

    }

    public String execute() {

        String res = "start";
        try {
            StringBuffer pageCntx = new StringBuffer();


            Long idTask = wsc.getIdCurrTask();
            wsc.getTaskList().addTaskInfo(idTask);
            TaskInfo taskInfo = wsc.getTaskList().findTaskInfo(wsc.getIdCurrTask());

            String insNmbr = taskInfo.getAttributes().getStringValueByName("Страховой номер");

            String name = taskInfo.getAttributes().getStringValueByName("Имя");
            String fam = taskInfo.getAttributes().getStringValueByName("Фамилия");
            String patr = taskInfo.getAttributes().getStringValueByName("Отчество");
            String region = taskInfo.getAttributes().getStringValueByName("Район");
            String typePension = taskInfo.getAttributes().getStringValueByName("Уточнение типа пенсионного действия");
            String dateOfRight = taskInfo.getAttributes().getStringValueByName("Дата совершения пенсионного действия");
            String organisation = taskInfo.getAttributes().getStringValueByName("Организация");
            String periods = taskInfo.getAttributes().getStringValueByName("Отсутствующие периоды");
            String nameUPFR = taskInfo.getAttributes().getStringValueByName("Наименование органа, осуществляющего пенсионное действие");
            

            int countDay = 3;
            boolean is10Day = false;
            countDay = 9;
            is10Day = true;

            Calendar cal = Calendar.getInstance();
            String date = WPC.getInstance().dateFormat.format(cal.getTime());

            int isDayPlus = Calendar.MONTH;
            if (is10Day) {
				isDayPlus = Calendar.DAY_OF_MONTH;
			}
            cal.add(isDayPlus, countDay);
            String dateEnd = WPC.getInstance().dateFormat.format(cal.getTime());

            for (int i = 0; i < 2; i++) {

                pageCntx.append("<center>");
                pageCntx.append("<strong style=\"font-size:10\">");
                pageCntx.append(" УВЕДОМЛЕНИЕ О СРОКАХ СДАЧИ СВЕДЕНИЙ ИНДИВИДУАЛЬНОГО<br> (ПЕРСОНИФИЦИРОВАННОГО) УЧЕТА <br>");

                pageCntx.append(date);
                pageCntx.append("г.");
                pageCntx.append("</strong>");

                pageCntx.append("</center>");
                pageCntx.append("<p align=\"justify\" style=\"font-size:10\">");
                pageCntx.append("&nbsp&nbsp&nbsp&nbspВ соответствии с п. 4 статьи 11 Федерального закона от 01.04.1996 г. № 27-ФЗ (в ред. Федерального закона от ");
                pageCntx.append("31.12.2002 № 198-ФЗ) «Об индивидуальном (персонифицированном) учете в системе обязательного пенсионного страхования» ");
                pageCntx.append("страхователи представляют в органы Пенсионного фонда РФ о каждом работающем у него застрахованном лице индивидуальные ");
                pageCntx.append("сведения для назначения пенсии на основании имеющихся данных бухгалтерского учета.<br>");
                pageCntx.append("&nbsp&nbsp&nbsp&nbspВ соответствии с инструкцией «О мерах по организации индивидуального (персонифицированного) учета для целей пенсионного ");
                pageCntx.append("страхования», утвержденной Постановлением Правительства от 15.03.1997 г. № 318 (ред. от 12.02.2004) страхователь обязан ");
                pageCntx.append("представить в течение <strong>");
                if (is10Day) {
                    pageCntx.append(countDay + 1);
                    pageCntx.append(" дней ");
                } else {
                    pageCntx.append(countDay);
                    pageCntx.append("- месячный срок ");

                }
                pageCntx.append("</strong>");
                pageCntx.append("со дня подачи застрахованным лицом заявления на пенсию индивидуальные сведения об этом лице, ");
                pageCntx.append("необходимые для назначения пенсии.");
                pageCntx.append("</p><center><strong style=\"font-size:10\">").append(nameUPFR);
                pageCntx.append(" извещает<br><br>");
                pageCntx.append("страхователя:______________________________________________<br></strong>");
                pageCntx.append("<strong style=\"font-size:10\">О СРОКАХ ПРЕДСТАВЛЕНИЯ ИНДИВИДУАЛЬНЫХ СВЕДЕНИЙ");
                pageCntx.append("</strong></center>");
                pageCntx.append("<div class=\"tabledata\"> <table border=\"1\">");
                pageCntx.append("<tr>\n" +
                        "    <th align=\"center\" style=\"font-size:10\">Застрахованное лицо</th>\n" +
                        "    <th align=\"center\" style=\"font-size:10\">Страховой номер</th>\n" +
                        "    <th align=\"center\" style=\"font-size:10\">Вид пенсии</th>\n" +
                        "    <th align=\"center\" style=\"font-size:10\">Дата права</th>\n" +
                        "    <th align=\"center\" style=\"font-size:10\">Дата подачи заявления на пенсию</th>\n" +
                        "    <th align=\"center\" style=\"font-size:10\">Срок представления индивидуальных сведении (межотчетная форма)</th>\n" +
                        "\n" +
                        "</tr>");
                pageCntx.append("<tr>\n" + "    <td align=\"center\" style=\"font-size:10\">").
                        append(fam).
                        append(" ").
                        append(name).
                        append(" ").
                        append(patr).
                        append(" </td>\n" + "    <td align=\"center\" style=\"font-size:10\">").
                        append(insNmbr).
                        append(" </td>\n" + "    <td align=\"center\" style=\"font-size:10\">").
                        append(typePension).
                        append("</td>\n" + "    <td align=\"center\" style=\"font-size:10\">").
                        append(dateOfRight).
                        append("</td>\n" + "    <td align=\"center\" style=\"font-size:10\">").
                        append(date).
                        append("</td>\n" + "    <td align=\"center\" style=\"font-size:10\">").
                        append(dateEnd).
                        append("</td>\n" + "</tr>");
                pageCntx.append("</table></div><p align=\"justify\" style=\"font-size:10\"><strong>");
                pageCntx.append("&nbsp&nbsp&nbsp&nbspПри нарушении срока представления индивидуальных сведений пенсия данному застрахованному лицу БУДЕТ НАЗНАЧЕНА по ");
                pageCntx.append("имеющимся в распоряжении органов Пенсионного фонда РФ сведениям.");
                pageCntx.append("</strong><br>");
                pageCntx.append("&nbsp&nbsp&nbsp&nbspСтраховые взносы, не учтенные при назначении пенсии, будут учтены при перерасчете страховой части пенсии, после подачи ");
                pageCntx.append("застрахованным лицом заявления на перерасчет страховой части пенсии по истечении 12 месяцев после ее назначения ");
                pageCntx.append("(п. 3 статьи 17 Федерального закона от 17.12.2001 г. № 173-ФЗ «О трудовых пенсиях в Российской Федерации»).");
                pageCntx.append("</p>");
                pageCntx.append("<p style=\"font-size:10\"> Специалист (").
                        append(region).
                        append(" р-н)________________________ &nbsp&nbsp&nbsp&nbsp Организация (ЗЛ)________________________</p>");
                pageCntx.append("<br>");
                if (i == 0) {
                    pageCntx.append("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");

                }
            }


            wsc.getCacheManager().deleteCacheElement(Long.valueOf(idTask));
            wsc.getTaskList().addTaskInfo(idTask);
            wsc.setPageData(pageCntx.toString());

            res = "textPage";


        } catch (Exception e) {
            e.printStackTrace();

        }


        return res;

    }
}
