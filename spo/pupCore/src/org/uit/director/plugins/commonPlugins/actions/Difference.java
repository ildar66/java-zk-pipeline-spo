package org.uit.director.plugins.commonPlugins.actions;

import java.util.ArrayList;
import java.util.List;

import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.plugins.PluginInterface;
import org.uit.director.tasks.AttributesStructList;
import org.uit.director.tasks.TaskInfo;

/**
 * Класс вычисляет разность между значениями 2-х параметров процесса и заносит результат в 3-ий параметр.
 * Параметры определяются в списке params. В список параметров передаются имена атрибутов.
 * Процесс над которым выполняется действие определяется идентификатором задания, который берется
 * методом wsc.getIdCurrTask()
 */
public class Difference implements PluginInterface {

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
            String parDiff = (String) params.get(2);
            TaskInfo taskInfo = wsc.getTaskList().findTaskInfo(wsc.getIdCurrTask());
            long idProcess = taskInfo.getIdProcess();
            AttributesStructList atrList = taskInfo.getAttributes();
            String valPar1 = atrList.getStringValueByName(par1);
            String valPar2 = atrList.getStringValueByName(par2);
            if (valPar1 != null && valPar2 != null) {
                float diffFl = Float.parseFloat(valPar1) - Float.parseFloat(valPar2);
                String summStr = String.valueOf(diffFl);
                if (summStr.endsWith(".0")) {
					summStr = summStr.substring(0, summStr.length() - 2);
				}
                wsc.getDbManager().getDbFlexDirector().updateAttribute(idProcess, parDiff, summStr);
                ArrayList<String> ls = new ArrayList<String>();
                ls.add(summStr);
                taskInfo.getAttributes().setValue(parDiff, ls);
                res = "acceptedTasks";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return res;

    }
}
