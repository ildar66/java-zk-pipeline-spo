package org.uit.director.plugins.futurePens;

import java.util.List;

import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.plugins.PluginInterface;

/**
 * Created by IntelliJ IDEA.
 * User: PD190379
 * Date: 06.06.2006
 * Time: 16:13:31
 * To change this template use File | Settings | File Templates.
 */
public class ReturnFuturePens implements PluginInterface {
    private List params;
    private WorkflowSessionContext wsc;

    public void init( WorkflowSessionContext wsc, List params ) {
        this.wsc = wsc;
        this.params = params;
    }

    public String execute() {
        String res = "error";
        /*DBMgr dbMgr = null;
        try {


            TaskInfo taskInfo = wsc.getTaskList().findTaskInfo( wsc.getIdCurrTask() );
            AttributesList atrList = taskInfo.getAttributes();
            String insnmb = ( String ) atrList.getValueByName( "Страховой номер" );

            Object[] paramIn = new Object[2];
            InsuranceNumber number = new InsuranceNumber( insnmb );
            paramIn[0] = new Long( number.getMainNumber() );
            paramIn[1] = wsc.getIdUser();

            dbMgr = wsc.getDbManager();
            dbMgr.getDbAnketa().returnFuturePens( paramIn );

            dbMgr.getDbFlexDirector().deleteProcess( String.valueOf( taskInfo.getIdProcess() ) );
            wsc.getCacheManager().deleteCacheElement( String.valueOf( taskInfo.getId_task() ) );


            res = "acceptedTasks";

        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            try {
                dbMgr.closeDBAnketa();
            } catch ( Exception e ) {
            }

        }*/


        return res;
    }
}
