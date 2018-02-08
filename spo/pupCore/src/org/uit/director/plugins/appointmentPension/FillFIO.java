package org.uit.director.plugins.appointmentPension;

import java.util.List;

import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.managers.DBMgr;
import org.uit.director.plugins.PluginInterface;
import org.uit.director.plugins.appointmentPension.insnmbr.InsuranceNumber;
import org.uit.director.tasks.TaskInfo;


public class FillFIO implements PluginInterface {

    private WorkflowSessionContext wsc;


    public void init(WorkflowSessionContext wsc, List params) {
        this.wsc = wsc;

    }

    public String execute() {

        String res = "start";
        try {

            Long idTask = wsc.getIdCurrTask();
            wsc.getTaskList().addTaskInfo(idTask);
            TaskInfo taskInfo = wsc.getTaskList().findTaskInfo(wsc.getIdCurrTask());

            String insNmbr = taskInfo.getAttributes().getStringValueByName("Страховой номер");
            InsuranceNumber number = new InsuranceNumber(insNmbr);
            DBMgr dbMan = wsc.getDbManager();

//            List fullFIO = dbMan.getDbAnketa().getFIO(number.getMainNumber());
//            dbMan.closeDBAnketa();

//            String fam = (String) ((Map) fullFIO.get(0)).get("PRNFAM");
//            String name = (String) ((Map) fullFIO.get(0)).get("PRNNAM");
//            String patr = (String) ((Map) fullFIO.get(0)).get("PRNPTR");

//            dbMan.getDbFlexDirector().updateAttribute(taskInfo.getIdProcess(), "Имя", name);
//            dbMan.getDbFlexDirector().updateAttribute(taskInfo.getIdProcess(), "Фамилия", fam);
//            dbMan.getDbFlexDirector().updateAttribute(taskInfo.getIdProcess(), "Отчество", patr);
//            wsc.getCacheManager().deleteCacheElement(idTask);
//            wsc.getTaskList().addTaskInfo(idTask);

            res = "pageContextTask";


        } catch (Exception e) {
            e.printStackTrace();

        }


        return res;

    }
}
