package org.uit.director.plugins.workAutomationGroup;

import java.util.List;

import org.uit.director.contexts.WorkflowSessionContext;


public class SetInitUser extends SetCurrUser {

    private List params;
    private WorkflowSessionContext wsc;

    @Override
	public void init(WorkflowSessionContext wsc, List params) {
        super.init(wsc, params);
    }

    @Override
	public String execute() {
        String res = "error";
        try {
            setUserToAttribute("Запустил задание");
            res = "acceptedTasks";
        } catch (Exception e) {
            e.printStackTrace();
        }


        return res;
    }
}
