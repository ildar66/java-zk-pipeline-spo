package org.uit.director.plugins.workAutomationGroup;

import java.util.List;

import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.plugins.PluginInterface;


public class SetCurrUser implements PluginInterface {

    private List params;
    private WorkflowSessionContext wsc;

    public void init(WorkflowSessionContext wsc, List params) {
        this.wsc = wsc;
        this.params = params;

    }

    public String execute() {
        String res = "error";
        try {

            setUserToAttribute("Задание пришло от");

            res = "acceptedTasks";
        } catch (Exception e) {
            e.printStackTrace();
        }


        return res;  //To change body of implemented methods use File | Settings | File Templates.
    }

    protected void setUserToAttribute(String attr) throws Exception {
        long idTask = wsc.getIdCurrTask();
        long idProc = wsc.getTaskList().findTaskInfo(idTask).getIdProcess();

        wsc.getDbManager().getDbFlexDirector().updateAttribute(idProc, attr, wsc.getFullUserName());
    }
}
