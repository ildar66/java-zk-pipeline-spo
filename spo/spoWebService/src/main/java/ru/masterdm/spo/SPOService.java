package ru.masterdm.spo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.vtb.domain.Task;
import com.vtb.domain.Task4Rating;
import com.vtb.domain.integration.MdTask;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.TaskActionProcessor;

import ru.masterdm.integration.CCStatus;
import ru.masterdm.integration.SPOCCIntegrationFacadeRemote;
import ru.masterdm.spo.integration.domain.DealPercent;
import ru.masterdm.spo.integration.domain.MdTaskNumber;
import ru.masterdm.spo.logic.RateLogic;
import ru.md.persistence.MdTaskMapper;
import ru.md.spo.dbobjects.TaskJPA;
import ru.md.spo.ejb.TaskFacadeLocal;
import ru.md.spo.util.TaskVersionHelper;

@WebService(serviceName = "SPOWS", targetNamespace = "http://ws.spo.integration.masterdm.ru")
public class SPOService extends SpringBeanAutowiringSupport {
	@EJB
	private SPOCCIntegrationFacadeRemote spoCCIntegration;
	@EJB
	private TaskFacadeLocal taskFacade;

	@Autowired
	private MdTaskMapper mdTaskMapper;

	private static final Logger LOGGER = LoggerFactory.getLogger(SPOService.class.getName());

	/**
	 * changed status notification.
	 * 
	 * @param status - {@link CCStatus} Credit committee mdtask status
	 * @param mdtaskid - original mdtaskid (not clone)
	 * @throws Exception
	 */
	@WebMethod
	public void statusNotification(@WebParam CCStatus status, @WebParam Long mdtaskid) throws SpoWsException {
		LOGGER.info("call statusNotification. mdtaskid=" + mdtaskid);
		try {
			spoCCIntegration.statusNotification(status, mdtaskid);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new SpoWsException(e.getMessage(), e);
		}
	}

	@WebMethod
	public List<Task4Rating> getOpportunityInfoList(@WebParam Long mdtaskid) {
		Task4Rating t = getOpportunityInfo(mdtaskid);
		if (t == null)
			return null;
		List<Task4Rating> list = new ArrayList<Task4Rating>();
		list.add(t);
		return list;
	}

	/**
	 * Возвращает сделку по её номеру.
	 * 
	 * @param mdtaskid номер сделки
	 * @return сделку
	 * @throws Exception
	 */
	@WebMethod
	@WebResult(name = "output")
	public Task4Rating getOpportunityInfo(@WebParam Long mdtaskid) {
		TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
		try {
			Task4Rating t = processor.getOpportunityInfo(mdtaskid);
			return t;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Возвращает 30 самых свежих сделок, в которых участвует данная организация. Если сделок больше 30, то отдаю только первые 30, остальных не будет! По просьбе Лиса.
	 * 
	 * @param organizationid номер организации
	 * @return список сделок
	 * @throws Exception
	 */
	@WebMethod
	public List<Task4Rating> getListOpportunity(@WebParam String organizationid) {
		TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
		try {
			return processor.getListOpportunity(organizationid);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Возвращает аттрибуты сделки по её id
	 * 
	 * @param organizationid номер организации
	 */
	@WebMethod
	public MdTask getOpportunityAttr(@WebParam Long id) throws SpoWsException {
		LOGGER.info("getOpportunityAttr id=" + id);
		if (id == null)
			return new MdTask();
		try {
			return taskFacade.getOpportunityAttr(id);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new SpoWsException(e.getMessage(), e);
		}
	}

	/**
	 * Возвращает список id и номеров сделок, в которых учавствует в любой роли контрагент с ID = organizationid
	 * 
	 * @param organizationid номер организации
	 */
	@WebMethod
	public List<MdTaskNumber> getListOpportunityNumber(@WebParam String organizationid) throws SpoWsException {
		try {
			List<MdTaskNumber> list = new ArrayList<MdTaskNumber>();
			for (ru.md.spo.dbobjects.MdTaskNumber mdtask : taskFacade.getListOpportunityNumber(organizationid)) {
				MdTaskNumber res = new MdTaskNumber(mdtask.getId(), mdtask.getNumber());
				res.setOrgRole(mdtask.getOrgRole());
				list.add(res);
			}
			return list;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new SpoWsException(e.getMessage(), e);
		}
	}

	/**
	 * Возвращает mdTaskId созданной версии. Может создавать версию СПО для КОД из: - версии СПО для СПО - версии СПО для КОД
	 */
	@WebMethod
	@Deprecated
	public Long createCedMdTaskVersion(@WebParam Long fromMdTaskId) throws SpoWsException {
		try {
			LOGGER.info("webservice createCedMdTaskVersion fromMdTaskId=" + fromMdTaskId);
			Task task = null;
			TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
			task = processor.getTaskCore(new Task(fromMdTaskId));
			TaskJPA newTaskJPA = TaskVersionHelper.createCedVersion(null, task, null, task.getHeader().getVersion(), processor, taskFacade);
			return newTaskJPA.getId();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new SpoWsException(e.getMessage(), e);
		}
	}

	/**
	 * Возвращает mdTaskId созданной версии. Может создавать версию СПО для КОД из: - версии СПО для СПО - версии СПО для КОД
	 */
	@WebMethod
	@Deprecated
	public Long createCedMdTaskVersionSetVersion(@WebParam Long fromMdTaskId, @WebParam Long version) throws SpoWsException {
		try {
			LOGGER.info("webservice createCedMdTaskVersionSetVersion fromMdTaskId=" + fromMdTaskId + " ,version=" + version);
			Task task = null;
			TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
			task = processor.getTaskCore(new Task(fromMdTaskId));
			TaskJPA newTaskJPA = TaskVersionHelper.createCedVersion(null, task, null, version, processor, taskFacade);
			return newTaskJPA.getId();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new SpoWsException(e.getMessage(), e);
		}
	}
	
	/**
	 * Возвращает mdTaskId созданной версии. Может создавать версию СПО для КОД из: - версии СПО для СПО - версии СПО для КОД
	 *
	 * @param idPerformer {@link Long id} пользователя, запустившего создание версии
	 * @param fromMdTaskId {@link Long id} исходной заявки СПО
	 * @param version {@link Long номер} версии заявки СПО
	 * @return {@link Long id} созданной заявки СПО
	 * @throws SpoWsException {@link SpoWsException ошибка}
	 */
	@WebMethod
	public Long createCedMdTaskNewVersion(@WebParam(name = "idPerformer") Long idPerformer, @WebParam(name = "fromMdTaskId") Long fromMdTaskId, @WebParam(name = "version") Long version) throws SpoWsException {
		try {
			LOGGER.info("webservice createCedMdTaskVersionSetVersion idPerformer=" + idPerformer + ", fromMdTaskId=" + fromMdTaskId + " ,version=" + version);
			Task task = null;
			TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
			task = processor.getTaskCore(new Task(fromMdTaskId));
			TaskJPA newTaskJPA = TaskVersionHelper.createCedVersion(idPerformer, task, null, version, processor, taskFacade);
			return newTaskJPA.getId();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new SpoWsException(e.getMessage(), e);
		}
	}

	@WebMethod
	public void removeCedMdTaskVersion(@WebParam Long fromMdTaskId) throws SpoWsException {
		try {
			TaskJPA task = taskFacade.getTask(fromMdTaskId);
			if (task.getProcess() == null)
				taskFacade.remove(TaskJPA.class, fromMdTaskId);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new SpoWsException(e.getMessage(), e);
		}
	}

	/**
	 * Возвращает {@link List список} {@link DealPercent объектов} хронологии процентных ставки сделки
	 *
	 * @param creditDealNumber {@link Long номер} кредитной сделки
	 * @param percentSumSanctionForDeal {@link Long размер санкций} по сделке, если вызываем из модуля Мониторинг
	 * @param idDealPayment {@link Long id} заявки на выдачу, если вызываем из модуля Выдача
	 * @return {@link List список} {@link DealPercent объектов} хронологии процентных ставки сделки
	 * @throws SpoWsException {@link SpoWsException ошибка}
	 */
	@WebMethod
	public List<DealPercent> getDealPercentHistories(@WebParam(name = "creditDealNumber") Long creditDealNumber, @WebParam(name = "percentSumSanctionForDeal") BigDecimal percentSumSanctionForDeal, @WebParam(name = "idDealPayment") Long idDealPayment) throws SpoWsException {
		try {
			RateLogic rateLogic = new RateLogic(mdTaskMapper);
			return rateLogic.getDealPercentHistories(creditDealNumber, percentSumSanctionForDeal, idDealPayment);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new SpoWsException(e.getMessage(), e);
		}	
	}

	/**
	 * Возвращает {@link List список} {@link DealPercent объектов} хронологии процентных ставки сделки для еще не одобренной заявки СПО
	 *
	 * @param idCreditDeal {@link Long id} кредитной заявки
	 * @param percentSumSanctionForDeal {@link Long размер санкций} по сделке, если вызываем из модуля Мониторинг
	 * @param idDealPayment {@link Long id} заявки на выдачу, если вызываем из модуля Выдача
	 * @return {@link List список} {@link DealPercent объектов} хронологии процентных ставки сделки
	 * @throws SpoWsException {@link SpoWsException ошибка}
	 */
	@WebMethod
	public List<DealPercent> getNotConfirmedDealPercentHistories(@WebParam(name = "idCreditDeal") Long idCreditDeal, @WebParam(name = "percentSumSanctionForDeal") BigDecimal percentSumSanctionForDeal, @WebParam(name = "idDealPayment") Long idDealPayment) throws SpoWsException {
		try {
			RateLogic rateLogic = new RateLogic(mdTaskMapper);
			return rateLogic.getNotConfirmedDealPercentHistories(idCreditDeal, percentSumSanctionForDeal, idDealPayment);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new SpoWsException(e.getMessage(), e);
		}
	}

}