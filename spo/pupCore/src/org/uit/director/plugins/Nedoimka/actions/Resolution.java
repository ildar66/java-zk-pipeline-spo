package org.uit.director.plugins.Nedoimka.actions;

//import org.PFR.EJB.underwriters.DBUnderwritersHome;
//import org.PFR.EJB.underwriters.ResolutionObject;
//import org.PFR.EJB.underwriters.DBUnderwriters;
import java.util.List;

import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.plugins.PluginInterface;

/**
 * Created by IntelliJ IDEA.
 * User: PD190379
 * Date: 10.07.2006
 * Time: 16:24:36
 * To change this template use File | Settings | File Templates.
 */
public class Resolution implements PluginInterface {
    private List params;
    private WorkflowSessionContext wsc;

    public void init(WorkflowSessionContext wsc, List params) {
        this.wsc = wsc;
        this.params = params;
    }

    public String execute() {
        /*String res = "error";
        DBMgr dbMgr = wsc.getDbManager();
        try {
            String rainmb = (String) params.get(0);
            String regn = (String) params.get(1);
            String god = (String) params.get(2);
            String kvartal = (String) params.get(3);

            TaskInfo taskInfo = wsc.getTaskList().findTaskInfo(wsc.getIdCurrTask());
            long idProcess = taskInfo.getIdProcess();
            AttributesList atrList = taskInfo.getAttributes();

            Integer[] paramIn = new Integer[4];
            paramIn[0] = new Integer(atrList.getStringValueByName(rainmb));
            paramIn[1] = new Integer(atrList.getStringValueByName(regn));
            paramIn[2] = new Integer(atrList.getStringValueByName(god));
            paramIn[3] = new Integer(atrList.getStringValueByName(kvartal));

            Object ref = EJBUtils.getLocalEJBObject("DBUnderwritersEJB");
            DBUnderwritersHome home = (DBUnderwritersHome) javax.rmi.PortableRemoteObject.narrow(ref, DBUnderwritersHome.class);
            DBUnderwriters resolution = home.create();
            ResolutionObject listRes = resolution.getResolution(paramIn);

            List parameters = new ArrayList();
            boolean isExecToDataBase = true;

            TaskInfo taskInfoClone;
            AttributesList attrs = null;
            if (params.size() > 8) {
                isExecToDataBase = false;
                Object obj = params.get(8);
                taskInfoClone = (TaskInfo) obj;
                attrs = taskInfoClone.getAttributes();
            }


            for (int i = 0; i < 4; i++) {
                Object[] par = new Object[3];
                par[0] = new Long(idProcess);
                switch (i) {
                    case 0:
                        if (isExecToDataBase) {
                            par[1] = params.get(4);
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            Date dat = formatter.parse(listRes.getDateres());
                            formatter = new SimpleDateFormat(Config.getProperty("DATE_FORMAT"));
                        } else {
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            Date dat = formatter.parse(listRes.getDateres());
                            formatter = new SimpleDateFormat(Config.getProperty("DATE_FORMAT"));
                            attrs.setValue((String) params.get(4), formatter.format(dat));
                        }
                        break;
                    case 1:
                        if (isExecToDataBase) {
                            par[1] = params.get(5);
                            par[2] = String.valueOf(listRes.getNomres());
                        } else {
                            attrs.setValue((String) params.get(5), String.valueOf(listRes.getNomres()));
                        }
                        break;
                    case 2:
                        if (isExecToDataBase) {
                            par[1] = params.get(6);
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            Date dat = formatter.parse(listRes.getDatepos());
                            formatter = new SimpleDateFormat(Config.getProperty("DATE_FORMAT"));
                            par[2] = formatter.format(dat);
                        } else {
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            Date dat = formatter.parse(listRes.getDatepos());
                            formatter = new SimpleDateFormat(Config.getProperty("DATE_FORMAT"));
                            attrs.setValue((String) params.get(6), formatter.format(dat));
                        }
                        break;
                    case 3:
                        if (isExecToDataBase) {
                            par[1] = params.get(7);
                            par[2] = String.valueOf(listRes.getNompos());
                        } else {
                            attrs.setValue((String) params.get(7), String.valueOf(listRes.getNompos()));
                        }
                        break;

                }
                parameters.add(par);
            }


            if (isExecToDataBase) {
                dbMgr.getDbFlexDirector().updateAttributes(parameters);
            }

            res = "acceptedTasks";
        }
        catch (Exception e) {
            e.printStackTrace();
        }*/
        return null;//res;
    }
}