package org.uit.director.plugins.Nedoimka.actions;

import java.util.List;

import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.plugins.PluginInterface;

/**
 * Created by IntelliJ IDEA.
 * User: PD190379
 * Date: 15.03.2006
 * Time: 13:37:26
 * Класс заносит суммы уплаты из базы "Аванс" в атрибуты процесса.
 * Параметры определяются в списке params. В список параметров передаются имена атрибутов.
 * Процесс над которым выполняется действие определяется идентификатором задания, который берется
 * методом wsc.getIdCurrTask()
 */
public class Uplata implements PluginInterface {

    private List params;
    private WorkflowSessionContext wsc;

    public void init(WorkflowSessionContext wsc, List params) {
        this.wsc = wsc;
        this.params = params;
    }

    public String execute() {
        String res = "error";
        /*DBMgr dbMgr = null;
        try {
            String rainmb = (String) params.get(0);
            String regn = (String) params.get(1);
            String god = (String) params.get(2);
            String kvartal = (String) params.get(3);
            String trebnmb = (String) params.get(4);

            TaskInfo taskInfo = wsc.getTaskList().findTaskInfo(wsc.getIdCurrTask());
            long idProcess = taskInfo.getIdProcess();
            AttributesList atrList = taskInfo.getAttributes();

            Integer[] paramIn = new Integer[5];
            paramIn[4] = new Integer(atrList.getStringValueByName(rainmb));
            paramIn[3] = new Integer(atrList.getStringValueByName(regn));
            paramIn[2] = new Integer(atrList.getStringValueByName(god));
            paramIn[1] = new Integer(atrList.getStringValueByName(kvartal));
            paramIn[0] = new Integer(atrList.getStringValueByName(trebnmb));

            dbMgr = wsc.getDbManager();
            List uplataAvans = dbMgr.getDbUnderwriters().getUplata(paramIn);


            List parameters = new ArrayList();
            boolean isExecToDataBase = true;

            TaskInfo taskInfoClone;
            AttributesList attrs = null;
            if (params.size() > 9) {
                isExecToDataBase = false;
                Object obj = params.get(9);
                taskInfoClone = (TaskInfo) obj;
                attrs = taskInfoClone.getAttributes();
            }


            for (int i = 0; i < 4; i++) {
                Object[] par = new Object[3];
                par[0] = new Long(idProcess);
                switch (i) {
                    case 0:
                        if (isExecToDataBase) {
                            par[1] = params.get(5);
                            par[2] = ((Map) uplataAvans.get(0)).get("UPLVS");
                        } else {
                            attrs.setValue((String) params.get(5), (String) ((Map) uplataAvans.get(0)).get("UPLVS"));
                        }
                        break;
                    case 1:
                        if (isExecToDataBase) {
                            par[1] = params.get(6);
                            par[2] = ((Map) uplataAvans.get(0)).get("UPLVN");
                        } else {
                            attrs.setValue((String) params.get(6), (String) ((Map) uplataAvans.get(0)).get("UPLVN"));
                        }
                        break;
                    case 2:
                        if (isExecToDataBase) {
                            par[1] = params.get(7);
                            par[2] = ((Map) uplataAvans.get(0)).get("UPLPS");
                        } else {
                            attrs.setValue((String) params.get(7), (String) ((Map) uplataAvans.get(0)).get("UPLPS"));
                        }
                        break;
                    case 3:
                        if (isExecToDataBase) {
                            par[1] = params.get(8);
                            par[2] = ((Map) uplataAvans.get(0)).get("UPLPN");
                        } else {
                            attrs.setValue((String) params.get(8), (String) ((Map) uplataAvans.get(0)).get("UPLPN"));
                        }
                        break;

                }
                parameters.add(par);
            }


            if (isExecToDataBase) {
                dbMgr.getDbFlexDirector().updateAttributes(parameters);
            }

//            dbMgr.getDbFlexDirector().updateAttribute(idProcess, (String) params.get(5), (String) ((Map) uplataAvans.get(0)).get("UPLVS"));
//            dbMgr.getDbFlexDirector().updateAttribute(idProcess, (String) params.get(6), (String) ((Map) uplataAvans.get(0)).get("UPLVN"));
//            dbMgr.getDbFlexDirector().updateAttribute(idProcess, (String) params.get(7), (String) ((Map) uplataAvans.get(0)).get("UPLPS"));
//            dbMgr.getDbFlexDirector().updateAttribute(idProcess, (String) params.get(8), (String) ((Map) uplataAvans.get(0)).get("UPLPN"));

            res = "acceptedTasks";

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                dbMgr.closeDBUnderwriters();
            } catch (Exception e) {
            }

        }*/


        return res;
    }
}
