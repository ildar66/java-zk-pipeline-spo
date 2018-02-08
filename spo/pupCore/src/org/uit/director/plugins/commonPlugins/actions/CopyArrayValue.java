package org.uit.director.plugins.commonPlugins.actions;


import java.util.ArrayList;
import java.util.List;

import org.uit.director.contexts.WPC;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.db.ejb.DBFlexWorkflowCommon;
import org.uit.director.plugins.PluginInterface;
import org.uit.director.tasks.AttributesStructList;
import org.uit.director.tasks.TaskInfo;

/**
 * Класс передает значение одного параметра в другой.
 * Параметры определяются в списке params. В список параметров передаются имена атрибутов.
 * Процесс над которым выполняется действие определяется идентификатором задания, который берется
 * методом wsc.getIdCurrTask()
 */
public class CopyArrayValue implements PluginInterface {

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

            // объект для операций с базой
            DBFlexWorkflowCommon dbFlexDirector = wsc.getDbManager().getDbFlexDirector();

            //найти объект задание
            TaskInfo taskInfo = wsc.getTaskList().findTaskInfo(wsc.getIdCurrTask());
            
            long idProcess = taskInfo.getIdProcess();
            AttributesStructList atrList = taskInfo.getAttributes();

            //получаем значение атрибута массива по его имени par1
            List valPar1 = (List) atrList.getValueByName(par1);

            // удаляем значения атрибута, в который будем копировать
            dbFlexDirector.deleteAttributeByName(taskInfo.getIdProcess(), par2);

            // создадим список параметров для обновления атрибута param2
            ArrayList param = new ArrayList();

            // для всех элементов массива par1
            for (int i = 0; i < valPar1.size(); i++) {



                // приводим значение элемента массива par1 к строковому типу
                String strValue = (String) valPar1.get(i);

                // Элемент списка param, состоящий из 3 значений: id процесса, имя атрибута, значение атрибута
                Object[] par = new Object[3];
                par[0] = new Long(idProcess);
                par[1] = par2;
                par[2] = strValue;
                param.add(par);

            }

            dbFlexDirector.updateAttributes(param, WPC.getInstance().getControlType(taskInfo.getIdTypeProcess()) );
            res = "acceptedTasks";
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
}
