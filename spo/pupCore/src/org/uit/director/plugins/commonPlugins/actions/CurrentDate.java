package org.uit.director.plugins.commonPlugins.actions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.uit.director.contexts.WPC;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.plugins.PluginInterface;
import org.uit.director.tasks.TaskInfo;

/**
 * Класс заносит знеачение текущей даты в первый атрибут.
 * Параметры определяются в списке params. В список параметров передаются имена атрибутов.
 * Процесс над которым выполняется действие определяется идентификатором задания, который берется
 * методом wsc.getIdCurrTask()
 */
public class CurrentDate implements PluginInterface {

    private List params;
    private WorkflowSessionContext wsc;


    public void init(WorkflowSessionContext wsc, List params) {
        this.wsc = wsc;
        this.params = params;
    }

    public String execute() {
        String res = "error";

        try {
            String par = (String) params.get(0);
            TaskInfo taskInfo = wsc.getTaskList().findTaskInfo(wsc.getIdCurrTask());
            long idProcess = taskInfo.getIdProcess();
            SimpleDateFormat dateFormat = WPC.getInstance().dateFormat;
            String currDate = dateFormat.format(new Date());

            wsc.getDbManager().getDbFlexDirector().updateAttribute(idProcess, par, currDate);
            res = "acceptedTasks";

        } catch (Exception e) {
            e.printStackTrace();
        }


        return res;

    }
}
