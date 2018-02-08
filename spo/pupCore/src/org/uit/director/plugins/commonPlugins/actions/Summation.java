package org.uit.director.plugins.commonPlugins.actions;

import java.util.ArrayList;
import java.util.List;

import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.plugins.PluginInterface;
import org.uit.director.tasks.AttributesStructList;
import org.uit.director.tasks.TaskInfo;

/**
 * Класс суммирует значение 2-х параметров процесса и заносит результат в 3-ий параметр.
 * Параметры определяются в списке params. В список параметров передаются имена атрибутов.
 * Процесс над которым выполняется действие определяется идентификатором задания, который берется
 * методом wsc.getIdCurrTask()
 */
public class Summation implements PluginInterface {

    private List params;
    private WorkflowSessionContext wsc;


    public void init(WorkflowSessionContext wsc, List params) {
        this.wsc = wsc;
        this.params = params;
    }

    public String execute() {
        String res = "error";

        try {
            int countParam = params.size();
            float summ = 0;
            System.out.println("begin summation "+countParam);
            TaskInfo taskInfo = wsc.getTaskList().findTaskInfo(wsc.getIdCurrTask());
            long idProcess = taskInfo.getIdProcess();
            AttributesStructList atrList = taskInfo.getAttributes();
            for(int i=0; i<countParam; i++) {
				System.out.println("begin summation "+params.get(i));
			}
            String parSumm = (String) params.get(countParam - 1);

            for (int i = 0; i < countParam - 1; i++) {
                String par = (String) params.get(i);
                String valPar = atrList.getStringValueByName(par);
                if (valPar.equals("")) {
					valPar = "0";
				}
                summ += Float.parseFloat(valPar);
                System.out.println(par+" "+valPar);
            }

            String summStr = String.valueOf(summ);
            if (summStr.endsWith(".0")) {
				summStr = summStr.substring(0, summStr.length() - 2);
			}
            wsc.getDbManager().getDbFlexDirector().updateAttribute(idProcess, parSumm, summStr);
            ArrayList<String> ls = new ArrayList<String>();
            ls.add(summStr);
            taskInfo.getAttributes().setValue(parSumm, ls);
            res = "acceptedTasks";

            /*    String par1 = (String) params.get(0);
            String par2 = (String) params.get(1);
            String parSumm = (String) params.get(2);


            String valPar1 = atrList.getStringValueByName(par1);
            String valPar2 = atrList.getStringValueByName(par2);
            if (valPar1.equals("")) valPar1 = "0";
            if (valPar2.equals("")) valPar2 = "0";


            if (valPar1 != null && valPar2 != null) {
                float summFl = Float.valueOf(valPar1) + Float.valueOf(valPar2);
                String summStr = String.valueOf(summFl);
                if (summStr.endsWith(".0")) summStr = summStr.substring(0, summStr.length() - 2);
                wsc.getDbManager().getDbFlexDirector().updateAttribute(idProcess, parSumm, summStr);
                taskInfo.getAttributes().setValue(parSumm, summStr);
                res = "acceptedTasks";
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }


        return res;

    }
}
