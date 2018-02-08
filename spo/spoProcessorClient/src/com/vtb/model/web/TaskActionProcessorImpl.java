/**
 * 
 */
package com.vtb.model.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import ru.masterdm.compendium.domain.Currency;
import ru.masterdm.compendium.domain.MDCalcHistory;
import ru.masterdm.compendium.domain.crm.Organization;
import ru.masterdm.compendium.value.Page;
import ru.masterdm.integration.CCStatus;
import ru.md.domain.MdTask;

import com.vtb.domain.ApprovedRating;
import com.vtb.domain.CRMLimit;
import com.vtb.domain.Process6;
import com.vtb.domain.ProcessSearchParam;
import com.vtb.domain.Task;
import com.vtb.domain.Task4Rating;
import com.vtb.domain.TaskProduct;
import com.vtb.domain.TaskVersion;
import com.vtb.ejb.TaskActionProcessorFacadeLocal;
import com.vtb.exception.CantChooseProcessType;
import com.vtb.exception.FactoryException;
import com.vtb.exception.MappingException;
import com.vtb.exception.ModelException;
import com.vtb.exception.NoSuchObjectException;
import com.vtb.model.TaskActionProcessor;
import com.vtb.util.EjbLocator;

/**
 * Бизнес-делегат для сессионного бина
 * 
 * @author Andrey Pavlenko
 */
public class TaskActionProcessorImpl implements TaskActionProcessor {

	private final Logger LOGGER = Logger.getLogger(getClass().getName());

	TaskActionProcessor modelFacade = null;

	public TaskActionProcessorImpl() throws Exception {
		try {
			getTaskFacadeLocal();
		} catch (Exception e) {
			try {
				getTaskFacade();
			} catch (Exception e1) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
				e.printStackTrace();

				LOGGER.log(Level.SEVERE, e1.getMessage(), e1);
				e1.printStackTrace();

				throw new Exception("TaskActionProcessorFacade can't found");
			}
		}
	}

	protected void getTaskFacadeLocal() throws Exception {
		try {
			modelFacade = EjbLocator.getInstance().getReference(
					TaskActionProcessorFacadeLocal.class);
		} catch (Exception e) {
			throw e;
		}
	}

	protected void getTaskFacade() throws Exception {
		try {
			modelFacade = EjbLocator.getInstance().getReference(
					TaskActionProcessorFacadeLocal.class);
		} catch (Exception e) {
			throw e;
		}
	}

	public Task createTask(Task task) throws MappingException {
		if (modelFacade != null)
			return modelFacade.createTask(task);
		else
			throw new MappingException("TaskActionProcessorFacade can't found");
	}

	public Task renewTask(Task task) throws MappingException {
		if (modelFacade != null)
			return modelFacade.renewTask(task);
		else
			throw new MappingException("TaskActionProcessorFacade can't found");
	}

	public Task getTask(Task taskWithKeyValues) throws MappingException {
		if (modelFacade != null)
			return modelFacade.getTask(taskWithKeyValues);
		else
			throw new MappingException("TaskActionProcessorFacade can't found");
	}

   public Task getReportData(Task taskWithKeyValues) throws MappingException {
       if (modelFacade != null)
           return modelFacade.getReportData(taskWithKeyValues);
       else
           throw new MappingException("TaskActionProcessorFacade can't found");
    }

	
	public void deleteTask(Task taskWithKeyValues) throws MappingException {
		if (modelFacade != null)
			modelFacade.deleteTask(taskWithKeyValues);
		else
			throw new MappingException("TaskActionProcessorFacade can't found");

	}

	public Task findByPupID(Long pupProcessID, boolean full)
			throws MappingException {
		if (modelFacade != null)
			return modelFacade.findByPupID(pupProcessID, full);
		else
			throw new MappingException("TaskActionProcessorFacade can't found");
	}

	public void updateTask(Task task) throws Exception {
		if (modelFacade != null)
			modelFacade.updateTask(task);
		else
			throw new MappingException("TaskActionProcessorFacade can't found");

	}
	
	@Override
	public void updateTask(Task task, MdTask mdTask) {
	    if (modelFacade != null)
            modelFacade.updateTask(task, mdTask);
        else
            throw new RuntimeException("TaskActionProcessorFacade can't found");
	}
	
	/**
     * {@inheritDoc}
     */
    @Override
    public MdTask getPipelineWithinMdTask(Long mdTaskId) {
        if (modelFacade != null)
            return modelFacade.getPipelineWithinMdTask(mdTaskId);
        else
            throw new RuntimeException("TaskActionProcessorFacade can't found");
    }

    public ArrayList<Task> findTaskByParent(Long pupProcessID, boolean all, boolean full)
			throws ModelException {
		if (modelFacade != null)
			return modelFacade.findTaskByParent(pupProcessID, all, full);
		else
			throw new ModelException("TaskActionProcessorFacade can't found");
	}

	public void export2cc(Long mdtaskid, Long userid) throws ModelException {
		if (modelFacade != null)
			modelFacade.export2cc(mdtaskid, userid);
		else
			throw new ModelException("TaskActionProcessorFacade can't found");
	}

	@Override
	public HashMap<Long, String> findAssignUser(Long idStage, Long idProcess)
			throws ModelException {
		if (modelFacade != null)
			return modelFacade.findAssignUser(idStage, idProcess);
		else
			return null;
	}

	@Override
	public HashMap<Long, String> findUser(Long idStage, Long idProcess)
			throws ModelException {
		if (modelFacade != null)
			return modelFacade.findUser(idStage, idProcess);
		else
			return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Page findCRMLimitByUser(ArrayList<Long> usersid, int start, int count)
			throws ModelException {
		if (modelFacade != null)
			return modelFacade.findCRMLimitByUser(usersid, start, count);
		else
			return null;
	}

	@Override
	public CRMLimit findCRMLimitById(String limitid) throws ModelException {
		if (modelFacade != null)
			return modelFacade.findCRMLimitById(limitid);
		else
			return null;
	}

	@Override
	public boolean isCRMLimitLoaded(String crmid) throws ModelException {
		if (modelFacade != null)
			return modelFacade.isCRMLimitLoaded(crmid);
		else
			return true;
	}

	@Override
	public ArrayList<CRMLimit> findCRMSubLimit(String limitid)
			throws ModelException {
		if (modelFacade != null)
			return modelFacade.findCRMSubLimit(limitid);
		else
			return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Page findCRMProductByUser(ArrayList<Long> logins, int start,
			int count) throws ModelException {
		if (modelFacade != null)
			return modelFacade.findCRMProductByUser(logins, start, count);
		else
			return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Page findSPO6List(int start, int count) throws ModelException {
		if (modelFacade != null)
			return modelFacade.findSPO6List(start, count);
		else
			return null;
	}

	@Override
	public Process6 findSPO6byId(Long id) throws ModelException {
		if (modelFacade != null)
			return modelFacade.findSPO6byId(id);
		else
			return null;
	}

	@Override
	public byte[] getResolution(Long id_template,Long id_mdtask)
			throws ModelException {
		if (modelFacade != null)
			return modelFacade.getResolution(id_template,id_mdtask);
		else
			return null;
	}

	@Override
	public String getReport(Task task, boolean xml, Integer reportid)
			throws Exception, ParserConfigurationException,
			IllegalAccessException {
		if (modelFacade != null)
			return modelFacade.getReport(task, xml, reportid);
		else
			return null;
	}

	@Override
	public boolean isPermissionEdit(long idStage, String varname,
			Integer idTypeProcess) throws MappingException {
		if (modelFacade != null)
			return modelFacade
					.isPermissionEdit(idStage, varname, idTypeProcess);
		else
			return false;
	}

	@Override
	public Task findByCRMid(String crmid) throws ModelException {
		if (modelFacade != null)
			return modelFacade.findByCRMid(crmid);
		else
			return null;
	}

	@Override
	public List<Task> findChildrenOfCRMid(String crmid, boolean full) throws ModelException {
		if (modelFacade != null)
			return modelFacade.findChildrenOfCRMid(crmid, full);
		else
			return null;
	}

	@SuppressWarnings("unchecked")
    @Override
	public Page findRefusableTask(Long userid, Long start, Long count,
			ProcessSearchParam sp) throws ModelException {
		if (modelFacade != null)
			return modelFacade.findRefusableTask(userid, start, count, sp);
		else
			return null;
	}

	@Override
	public ArrayList<Long> findAffiliatedUsers(Long mdtaskid)
			throws ModelException {
		if (modelFacade != null)
			return modelFacade.findAffiliatedUsers(mdtaskid);
		else
			return null;
	}

	@Override
	public void crmlog(String crmid, int i, String message)
			throws ModelException {
		if (modelFacade != null)
			modelFacade.crmlog(crmid, i, message);
	}

	@Override
	public void makeVersion(Long mdtaskid, Long idUser, String stageName,
			String roles) throws ModelException {
		if (modelFacade != null)
			modelFacade.makeVersion(mdtaskid, idUser, stageName, roles);
	}

	@Override
	public ArrayList<TaskVersion> findTaskVersion(Long mdtaskid)
			throws ModelException {
		if (modelFacade != null)
			return modelFacade.findTaskVersion(mdtaskid);
		else
			return null;
	}

	@Override
	public String getVersion(Long idversion) throws ModelException {
		if (modelFacade != null)
			return modelFacade.getVersion(idversion);
		else
			return null;
	}

	@Override
	public HashMap<Long, Long> getProcessAssign(Long id_pup_process)
			throws ModelException {
		if (modelFacade != null)
			return modelFacade.getProcessAssign(id_pup_process);
		else
			return null;
	}

	@Override
	public HashMap<Long, String> getRoles2Assign(Long idUser, Long idTask)
			throws ModelException {
		if (modelFacade != null)
			return modelFacade.getRoles2Assign(idUser, idTask);
		else
			return null;
	}

	@Override
	public Task4Rating getOpportunityInfo(Long mdtaskid) throws Exception {
		if (modelFacade != null)
			return modelFacade.getOpportunityInfo(mdtaskid);
		else
			return null;
	}

	@Override
	public ArrayList<Task4Rating> getListOpportunity(String organizationid)
			throws Exception {
		if (modelFacade != null)
			return modelFacade.getListOpportunity(organizationid);
		else
			return null;
	}

	@Override
	public ArrayList<Currency> findParentCurrency(Long parentTaskId)
			throws ModelException, NoSuchObjectException {
		if (modelFacade != null)
			return modelFacade.findParentCurrency(parentTaskId);
		else
			return null;
	}

	@Override
	public void updateAttribute(long idProcess, String nameVar, String valueVar) {
		if (modelFacade != null)
			modelFacade.updateAttribute(idProcess, nameVar, valueVar);
	}

	@Override
	public String getAttributeValue(Long idProcess, String nameVar) {
		if (modelFacade != null)
			return modelFacade.getAttributeValue(idProcess, nameVar);
		else
			return null;
	}

	@Override
	public void statusNotification(CCStatus status, Long mdtaskid)
			throws Exception {
		if (modelFacade != null)
			modelFacade.statusNotification(status, mdtaskid);
	}

	@Override
	public MDCalcHistory getMDCalcHistory(String orgId, Date dt) {
		if (modelFacade != null)
			return modelFacade.getMDCalcHistory(orgId, dt);
		else
			return null;
	}

    @Override
    public void startTimer(long timeInSecond) {
        if (modelFacade != null)
            modelFacade.startTimer(timeInSecond);
    }

    @Override
    public Long limitLoad(String limitid) throws ModelException,
            FactoryException, MappingException, CantChooseProcessType {
        if (modelFacade != null)
            return modelFacade.limitLoad(limitid);
        else
            return null;
    }

    @Override
    public Long productLoad(String id, Long userid, Long idProcessType) throws Exception, CantChooseProcessType {
        if (modelFacade != null)
            return modelFacade.productLoad(id,userid, idProcessType);
        else
            return null;
    }

    @Override
    public void exportRating2CRM(Long mdtaskid) throws Exception {
        if (modelFacade != null)
            modelFacade.exportRating2CRM(mdtaskid);
    }

    @Override
    public ApprovedRating getApprovedRating(Date date, String orgid) {
        if (modelFacade != null)
            return modelFacade.getApprovedRating(date, orgid);
        else
            return null;
    }

    @Override
    public ArrayList<Task4Rating> getListOpportunity(String organizationid,
            int startRow, int count) throws Exception {
        if (modelFacade != null)
            return modelFacade.getListOpportunity(organizationid, startRow, count);
        else
            return null;
    }

    @Override
    public ru.masterdm.compendium.domain.crm.Rating getRating(String orgId) {
        if (modelFacade != null)
            return modelFacade.getRating(orgId);
        else
            return null;
    }

    @Override
    public Organization getOrganizationFullData(String crmId) {
        if (modelFacade != null)
            return modelFacade.getOrganizationFullData(crmId);
        else return null;
    }

    @Override
    public Set<Long> getProcessTypeList(Organization org, Long userid) throws FactoryException, CantChooseProcessType {
        if (modelFacade != null)
            return modelFacade.getProcessTypeList(org, userid);
        else return null;
    }

	@Override
	public ArrayList<TaskProduct> readProductTypes(Task task) {
        if (modelFacade != null)
            return modelFacade.readProductTypes(task);
        else return null;
	}
	
	@Override
	public void saveProductTypes(Task task) {
        if (modelFacade != null) modelFacade.saveProductTypes(task);
	}
	
	@Override
	public void saveCurrencyList(Task task) {
        if (modelFacade != null) modelFacade.saveCurrencyList(task);
	}
	
	@Override
	public void saveTarget(Task task) {
        if (modelFacade != null) modelFacade.saveTarget(task);
	}
	
	@Override
	public void saveSpecialOtherConditions(Task task) {
        if (modelFacade != null) modelFacade.saveSpecialOtherConditions(task);
	}
	
	@Override
	public void saveParameters(Task task) {
        if (modelFacade != null) modelFacade.saveParameters(task);
	}

	@Override
	public Task getTaskCore(Task taskWithKeyValues) throws MappingException {
		if (modelFacade != null)
	           return modelFacade.getTaskCore(taskWithKeyValues);
	       else
	           throw new MappingException("TaskActionProcessorFacade can't found");
	}

}
