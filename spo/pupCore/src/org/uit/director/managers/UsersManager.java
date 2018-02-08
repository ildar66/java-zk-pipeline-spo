package org.uit.director.managers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uit.director.db.dbobjects.WorkflowDepartament;
import org.uit.director.db.dbobjects.WorkflowUser;
import org.uit.director.db.dbobjects.WorkflowUsersMap;
import org.uit.director.db.ejb.DBFlexWorkflowCommon;

import ru.masterdm.compendium.custom.UserTO;
import ru.masterdm.compendium.model.CompendiumActionProcessor;
import ru.md.pup.dbobjects.UserJPA;
import ru.md.spo.ejb.PupFacadeLocal;

public class UsersManager implements Serializable{
	private static final Logger LOGGER = LoggerFactory.getLogger(UsersManager.class.getName());

	private static final long serialVersionUID = 1L;
	
	private WorkflowUsersMap workflowUsers;
	
	public UsersManager(DBFlexWorkflowCommon dbFlexDirector) {
		initWorkflowUsers(dbFlexDirector);
	}

	private void initWorkflowUsers(DBFlexWorkflowCommon dbFlexDirector) {

		try {
			HashMap<Long, WorkflowUser> wfUsersInfo = dbFlexDirector.getUsersInfo();

			WorkflowUsersMap wfUsersMap = new WorkflowUsersMap(wfUsersInfo);
			setWorkflowUsers(wfUsersMap);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getFIOUser(Long idUser) {
		try {
			PupFacadeLocal pupFacade = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
			UserJPA user = pupFacade.getUser(idUser);
			if(user==null) return "";
			return user.getFullName();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return "";

	}

	public synchronized void setWorkflowUsers(WorkflowUsersMap workflowUsers) {
		this.workflowUsers = workflowUsers;
	}

	public synchronized String getFullNameWorkflowUser(Long idUser) {
		return getFIOUser(idUser);
	}
	
	public void deleteWorkflowUser(Long idUser) {
		workflowUsers.getWfUserMap().remove(idUser);
	}

	public Long getActiveIdUserByLogin(String login){
		try {
			PupFacadeLocal pupFacade = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
			UserJPA user = pupFacade.getUserByLogin(login);
			if(user==null) return null;
			return user.getIdUser();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
	
	public Map getNoActiveIdUserByLogin (String login){
		Map<Long, Boolean> idUserMap = new HashMap<Long, Boolean>();
		//WorkflowUsersMap wfUsersMap = WPC.getInstance().getWorkflowUsers();
		Map wfUserList = workflowUsers.getWfUserMap();
		Set keySet = wfUserList.keySet();
		Iterator iter = keySet.iterator();
		while (iter.hasNext()){
			WorkflowUser wfUser = (WorkflowUser)wfUserList.get(iter.next());
			if (!wfUser.getLogin().equalsIgnoreCase(login)) {
				continue;
			}
			idUserMap.put(wfUser.getIdUser(), wfUser.isActive());
		}
		return idUserMap;
	}
	
	public String getLoginByIdUser(Long idUser) {
        String login = null;
        Map<Long, WorkflowUser> wfUserList = workflowUsers.getWfUserMap();
        WorkflowUser wfUser = wfUserList.get(idUser);
        if (wfUser.isActive()) {
            login = wfUserList.get(idUser).getLogin().toLowerCase();
        }
        return login;
    }

	public WorkflowUser getInfoUserByIdUser (Long idUser){
		try {
			PupFacadeLocal pupFacade = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
			UserJPA user = pupFacade.getUser(idUser);
			if(user==null) return null;
			WorkflowUser wu = new WorkflowUser(user.getIdUser(), user.getSurname(), user.getName(), user.getPatronymic(), 
					new WorkflowDepartament(user.getDepartment().getIdDepartment(), user.getDepartment().getShortName(),user.getDepartment().getFullName()), 
					"", user.getLogin(), user.getMailUser(), "", user.isActive());
			return wu;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return workflowUsers.getWfUserMap().get(idUser);
	}
	
	public WorkflowUser getActiveInfoUserByLogin (String loginUser){
		WorkflowUser wfu = null;
		Map<Long, WorkflowUser> wfUserList = workflowUsers.getWfUserMap();
		Set keySet = wfUserList.keySet();
		Iterator iter = keySet.iterator();
		while (iter.hasNext()){
			WorkflowUser wfUser = (WorkflowUser)wfUserList.get(iter.next());
			if (wfUser.getLogin().equalsIgnoreCase(loginUser) && wfUser.isActive()){
				wfu = wfUser;
				break;
			}
		}
		return wfu;
			
	}

	public WorkflowUsersMap getWorkflowUsers() {
		return workflowUsers;
	}

}
