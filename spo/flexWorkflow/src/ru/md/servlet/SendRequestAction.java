package ru.md.servlet;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.action.AbstractAction;

import ru.md.pup.dbobjects.UserJPA;
import ru.md.spo.dbobjects.RequestLogJPA;
import ru.md.spo.dbobjects.TaskJPA;
import ru.md.spo.ejb.NotifyFacadeLocal;
import ru.md.spo.ejb.PupFacadeLocal;
import ru.md.spo.ejb.TaskFacadeLocal;

public class SendRequestAction extends Action {
    private Logger LOGGER = Logger.getLogger(this.getClass().getName());
    
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
        PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        NotifyFacadeLocal notifyFacade = com.vtb.util.EjbLocator.getInstance().getReference(NotifyFacadeLocal.class);
        TaskJPA task = taskFacadeLocal.getTask(Long.valueOf(request.getParameter("mdtaskid")));
        request.getCharacterEncoding();
        UserJPA from = pupFacadeLocal.getUser(AbstractAction.getWorkflowSessionContext(request).getIdUser());
        LOGGER.info("SendRequestAction mdtaskid="+task.getId().toString());
        String subject = decodeString(request, request.getParameter("requestSubjectSelect"));
        if(subject == null || subject.isEmpty()){
        		subject = decodeString(request, request.getParameter("requestSubjectText"));
        }
        String bodyIns = decodeString(request, request.getParameter("requestText"));
        for(String userid : request.getParameter("users").split(",")){
            String body = "Вам направлен запрос от пользователя " + from.getFullName();
            body += " по "+notifyFacade.getTypeNamePraepositionalis(task.getId())+
                    notifyFacade.getAllContractors(task.getId())+" № <a href=\"" +
                pupFacadeLocal.getBaseURL(Long.valueOf(userid.trim())) + "/showTaskList.do?typeList=all&projectteam=true&searchNumber="+
                task.getMdtask_number().toString()+"\">" + task.getNumberAndVersion()+"</a>";
            body += "<br />Содержание запроса:<br />"+ bodyIns +"<br />"
                + notifyFacade.getDescriptionTask(task.getId());
            //отправить уведомление
            notifyFacade.send(from.getIdUser(), Long.valueOf(userid.trim()), subject, body);
        }
        LOGGER.info("send to users "+ request.getParameter("users"));
        // и залогируем
        try {
            RequestLogJPA l = new RequestLogJPA(); 
            l.setTask(task);
            l.setFrom(from);
            l.setTo(null);
            l.setSubject(subject);
            l.setBody(bodyIns);
            l.setDate(Calendar.getInstance().getTime());
            l.setRecepients(new ArrayList<UserJPA>());
            for(String userid : request.getParameter("users").split(",")){
            	UserJPA to = pupFacadeLocal.getUser(Long.valueOf(userid.trim()));
            	l.getRecepients().add(to);
            }
            taskFacadeLocal.logRequest(l);
        } catch (Exception e) {
        	LOGGER.severe(e.getMessage());
        	e.printStackTrace();
        }
        
        response.getWriter().write("Запрос отправлен " + com.vtb.util.Formatter.formatDateTime(new Date()));
        return null;
    }
    
    private String decodeString(HttpServletRequest request, String str) {
    	try {
    		if (request.getCharacterEncoding() != null) return new String(str.getBytes(request.getCharacterEncoding()), "UTF8");
    		else return new String(str.getBytes("UTF8"), "UTF8");
    	} catch (UnsupportedEncodingException e) {
    		return str;
    	}
    }
}
