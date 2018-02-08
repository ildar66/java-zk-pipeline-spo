package org.uit.director.plugins.futurePens;

import java.util.List;

import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.plugins.PluginInterface;

/**
 * Created by IntelliJ IDEA.
 * User: PD190379
 * Date: 06.06.2006
 * Time: 16:03:48
 * To change this template use File | Settings | File Templates.
 */
public class CompleteFuturePens implements PluginInterface {
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
            String insnmb = ( String ) params.get( 0 );

            TaskInfo taskInfo = wsc.getTaskList().findTaskInfo( wsc.getIdCurrTask() );
            AttributesList atrList = taskInfo.getAttributes();

            Object[] paramIn = new Object[2];

            InsuranceNumber number = new InsuranceNumber( atrList.getStringValueByName( insnmb ) );
            paramIn[0] = new Long( number.getMainNumber() );
            paramIn[1] = wsc.getIdUser();

            dbMgr = wsc.getDbManager();
            dbMgr.getDbAnketa().completeFuturePens( paramIn );


            res = "acceptedTasks";

        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            try {
                dbMgr.closeDBUnderwriters();
            } catch ( Exception e ) {
            }
            try {
                dbMgr.closeDBAnketa();
            } catch ( Exception e ) {
            }


        }
*/

        return res;
    }
}
