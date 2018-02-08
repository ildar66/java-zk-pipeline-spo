package ru.md.servlet;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ru.md.helper.TaskHelper;
import ru.md.pup.dbobjects.UserJPA;
import ru.md.spo.dbobjects.ExpertTeamJPA;
import ru.md.spo.dbobjects.TaskJPA;
import ru.md.spo.ejb.NotifyFacadeLocal;
import ru.md.spo.ejb.PupFacadeLocal;
import ru.md.spo.ejb.TaskFacadeLocal;

import com.vtb.exception.FactoryException;

public class DelExpertTeamAction extends Action {
    private Logger logger = Logger.getLogger(this.getClass().getName());
    
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        response.setContentType("text/xml");
        response.setCharacterEncoding("UTF-8");
        
        PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        NotifyFacadeLocal notifyFacade = com.vtb.util.EjbLocator.getInstance().getReference(NotifyFacadeLocal.class);
        
        UserJPA user = pupFacadeLocal.getUser(Long.valueOf(request.getParameter("id")));
        delExpert(Long.valueOf(request.getParameter("mdtaskid")), user, request.getParameter("expname"));
        //отправить уведомление
        String subj = "Вы исключены из состава экспертной группы по " +
                notifyFacade.getNamePraepositionalis(Long.valueOf(request.getParameter("mdtaskid")));
        notifyFacade.send(TaskHelper.getCurrentUser(request).getIdUser(), 
        		user.getIdUser(), subj,
                subj + notifyFacade.getDescriptionTask(Long.valueOf(request.getParameter("mdtaskid"))));
        return null;
    }

	private void delExpert(Long mdtaskid, UserJPA user, String expname) throws FactoryException {
		TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
		TaskJPA task = taskFacadeLocal.getTask(mdtaskid);
        for (ExpertTeamJPA et : task.getExpertTeam()){
            if(et.getUser().getIdUser().equals(user.getIdUser()) && et.getExpname()!=null && et.getExpname().equals(expname)){
                task.getExpertTeam().remove(et);
                taskFacadeLocal.removeExpertTeamJPA(et.getId());
                delExpert(mdtaskid,user,expname);
                return;
            }
        }
	}
}
