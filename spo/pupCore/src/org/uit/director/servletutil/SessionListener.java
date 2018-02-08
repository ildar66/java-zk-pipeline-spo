package org.uit.director.servletutil;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.uit.director.contexts.WorkflowSessionContext;


public class SessionListener implements HttpSessionBindingListener {

    private WorkflowSessionContext wsc;

    public void valueBound(HttpSessionBindingEvent httpSessionBindingEvent) {
        wsc = (WorkflowSessionContext) httpSessionBindingEvent.getSession().getAttribute("workflowContext");
        System.out.println("FlexWorkflow: Session is creating.....");
    }

    public void valueUnbound(HttpSessionBindingEvent httpSessionBindingEvent) {
        try {        	
        	System.out.println("FlexWorkflow: Session is remove");
            wsc.release();
            wsc = null;
        } catch (Exception e) {
//            e.printStackTrace();
        } catch (Throwable throwable) {
//            throwable.printStackTrace();
        }
    }
}
