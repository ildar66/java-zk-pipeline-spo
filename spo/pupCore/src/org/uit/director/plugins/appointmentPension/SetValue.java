package org.uit.director.plugins.appointmentPension;

import java.util.List;

import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.plugins.PluginInterface;
import org.uit.director.tasks.TaskInfo;

/**
 * Created by IntelliJ IDEA.
 * User: pd199933
 * Date: 29.11.2005
 * Time: 12:35:45
 * Класс присваивает новые значения параметрам процесса.
 * Параметры определяются в списке params. В список параметров передаются имена атрибутов.
 * Процесс над которым выполняется действие определяется идентификатором задания, который берется
 * методом wsc.getIdCurrTask()
 */
public class SetValue implements PluginInterface {

    private List params;
    private WorkflowSessionContext wsc;


    public void init(WorkflowSessionContext wsc, List params) {
        this.wsc = wsc;
        this.params = params;
    }

    public String execute() {
        String res = "error";

        try {
            String par1 = (String) params.get(0);
            String par2 = (String) params.get(1);
            TaskInfo taskInfo = wsc.getTaskList().findTaskInfo(wsc.getIdCurrTask());
            long idProcess = taskInfo.getIdProcess();

            wsc.getDbManager().getDbFlexDirector().updateAttribute(idProcess, par1, par2);
            
            taskInfo.getAttributes().setValue(par1, par2);
            res = "acceptedTasks";

        } catch (Exception e) {
            e.printStackTrace();
        }


        return res;

    }
}
