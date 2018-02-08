package org.uit.director.plugins.appointmentPension;

import java.util.List;

import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.plugins.PluginInterface;
import org.uit.director.servletutil.ConvertChars;
import org.uit.director.tasks.TaskInfo;


public class GenZapros implements PluginInterface {

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
            String dateOfDocum = taskInfo.getAttributes().getStringValueByName("Дата подачи последнего недостающего документа");

            //for (int i = 0; i < 2; i++) {
            pageCntx.append("<form action=\"remote.create.process.do\" method=\"POST\">").
                    append("<div class=\"tabledata\"><table border=1><caption>").
                    append("Форма запуска нового процесса 'Запрос о ДИЛС'</caption><tr><th>").
                    append("Страховой номер</th><th>Имя</th><th>Фамилия</th><th>Отчество</th>").
                    append("<th>Район</th><th>Вид пенсии</th><th>Дата права</th>").
                    append("<th>Дата подачи последнего недостающего документа</th></tr>");
            pageCntx.append(" <input type=\"hidden\" name=\"check0\" value=\"on\"/>");
            pageCntx.append("<input type=\"hidden\" name=\"nameTypeProcess\" value=\"Запрос о ДИЛС\"/>");
            pageCntx.append("<input type=\"hidden\" name=\"countProcesses\" value=\"1\"/>");
            pageCntx.append("<input type=\"hidden\" name=\"idUser\" value=\"").
                    append(wsc.getIdUser()).
                    append("\"/>");
            pageCntx.append("<tr><td><input type=\"text\" name=\"").append(ConvertChars.convertToEng("Страховой номер")).append("0\" value=\"").
                    append(insNmbr).
                    append("\"></td>");
            pageCntx.append("<td><input type=\"text\" name=\"").append(ConvertChars.convertToEng("Фамилия")).append("0\" value=\"").
                    append(fam).

                    append("\"></td>");
            pageCntx.append("<td><input type=\"text\" name=\"").append(ConvertChars.convertToEng("Имя")).append("0\" value=\"").
                    append(name).
                    append("\"></td>");
            pageCntx.append("<td><input type=\"text\" name=\"").append(ConvertChars.convertToEng("Отчество")).append("0\" value=\"").
                    append(patr).
                    append("\"></td>");
            pageCntx.append("<td><input type=\"text\" name=\"").
                    append(ConvertChars.convertToEng("Район")).
                    append("0\" value=\"").
                    append(region).
                    append("\"></td>");
            pageCntx.append("<td><input type=\"text\" name=\"").append(ConvertChars.convertToEng("Уточнение типа пенсионного действия")).
                    append("0\" value=\"").append(typePension).append("\"></td>");
            pageCntx.append("<td><input type=\"text\" name=\"").append(ConvertChars.convertToEng("Дата совершения пенсионного действия")).
                    append("0\" value=\"").append(dateOfRight).append("\"></td>");
            pageCntx.append("<td><input type=\"text\" name=\"").
                    append(ConvertChars.convertToEng("Дата подачи последнего недостающего документа")).
                    append("0\" value=\"").append(dateOfDocum).append("\"></td></tr>");
            pageCntx.append("</table></div><br><center><input type=submit value=\"Запустить\"></center>");
            pageCntx.append("</form>");
            //}

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
