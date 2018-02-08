// @annotations-disabled tagSet="websphere" tagSet="ejb"
package com.vtb.ejb;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;
import org.uit.director.contexts.WPC;

import com.vtb.domain.AbstractSupply;
import com.vtb.domain.ApprovedRating;
import com.vtb.domain.AttachmentFile;
import com.vtb.domain.CRMLimit;
import com.vtb.domain.Commission;
import com.vtb.domain.Deposit;
import com.vtb.domain.Fine;
import com.vtb.domain.Forbidden;
import com.vtb.domain.Guarantee;
import com.vtb.domain.LimitTree;
import com.vtb.domain.Main;
import com.vtb.domain.Process6;
import com.vtb.domain.ProcessSearchParam;
import com.vtb.domain.ProjectTeamMember;
import com.vtb.domain.SPOAcceptType;
import com.vtb.domain.SpoAccount;
import com.vtb.domain.SpoOpportunity;
import com.vtb.domain.SpoOpportunityProduct;
import com.vtb.domain.Task;
import com.vtb.domain.Task4Rating;
import com.vtb.domain.TaskContractor;
import com.vtb.domain.TaskDepartment;
import com.vtb.domain.TaskManager;
import com.vtb.domain.TaskProcent;
import com.vtb.domain.TaskProduct;
import com.vtb.domain.TaskVersion;
import com.vtb.domain.Warranty;
import com.vtb.exception.CantChooseProcessType;
import com.vtb.exception.FactoryException;
import com.vtb.exception.MappingException;
import com.vtb.exception.ModelException;
import com.vtb.exception.NoSuchObjectException;
import com.vtb.exception.VtbException;
import com.vtb.mapping.MapperFactory;
import com.vtb.mapping.jdbc.CRMLimitMapper;
import com.vtb.mapping.jdbc.Process6Mapper;
import com.vtb.mapping.jdbc.SpoAccountMapper;
import com.vtb.mapping.jdbc.SpoOpportunityMapper;
import com.vtb.mapping.jdbc.SpoOpportunityProductMapper;
import com.vtb.mapping.jdbc.TaskMapper;
import com.vtb.mapping.jdbc.TaskMapperSaveHelper;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.AttachmentActionProcessor;
import com.vtb.model.TaskActionProcessor;
import com.vtb.util.Formatter;

import ru.masterdm.compendium.domain.Currency;
import ru.masterdm.compendium.domain.Department;
import ru.masterdm.compendium.domain.MDCalcHistory;
import ru.masterdm.compendium.domain.User;
import ru.masterdm.compendium.domain.cc.CcResolutionStatus;
import ru.masterdm.compendium.domain.cc.QuestionType;
import ru.masterdm.compendium.domain.crm.CommissionType;
import ru.masterdm.compendium.domain.crm.CompanyGovernance;
import ru.masterdm.compendium.domain.crm.CompanyGroup;
import ru.masterdm.compendium.domain.crm.Ensuring;
import ru.masterdm.compendium.domain.crm.Organization;
import ru.masterdm.compendium.domain.crm.StatusReturn;
import ru.masterdm.compendium.domain.rating.ObTypeGroupFactor;
import ru.masterdm.compendium.domain.rating.ObTypeGroupMember;
import ru.masterdm.compendium.domain.spo.Contact;
import ru.masterdm.compendium.domain.spo.ContractorType;
import ru.masterdm.compendium.domain.spo.OrganisationDepartmentMap;
import ru.masterdm.compendium.domain.spo.OrganisationTypeProcessMap;
import ru.masterdm.compendium.domain.spo.Person;
import ru.masterdm.compendium.domain.spo.Shareholder;
import ru.masterdm.compendium.model.CompendiumActionProcessor;
import ru.masterdm.compendium.model.CompendiumCrmActionProcessor;
import ru.masterdm.compendium.model.CompendiumRatingActionProcessor;
import ru.masterdm.compendium.model.CompendiumSpoActionProcessor;
import ru.masterdm.compendium.value.Page;
import ru.masterdm.integration.CCStatus;
import ru.masterdm.integration.cc.CcService;
import ru.masterdm.integration.cc.ws.QuestionImportWso;
import ru.masterdm.integration.rating.RatingService;
import ru.masterdm.integration.rating.ws.ApprovedRatingWso;
import ru.masterdm.integration.rating.ws.CalcHistoryInput;
import ru.masterdm.integration.rating.ws.CalcHistoryOutput;
import ru.masterdm.integration.rating.ws.CalcHistoryWso;
import ru.masterdm.spo.utils.SBeanLocator;
import ru.masterdm.templatetransformer.TemplateTransformFactory;
import ru.masterdm.templatetransformer.core.ITemplateTransform;
import ru.masterdm.templatetransformer.list.ETemplateTransform;
import ru.md.crm.dbobjects.CRMRating;
import ru.md.crm.dbobjects.LimitQueueTO;
import ru.md.crm.dbobjects.ProductQueueJPA;
import ru.md.domain.MdTask;
import ru.md.domain.dashboard.CCQuestion;
import ru.md.persistence.MdTaskMapper;
import ru.md.pup.dbobjects.DepartmentJPA;
import ru.md.pup.dbobjects.ProcessTypeJPA;
import ru.md.spo.dbobjects.ProductGroupJPA;
import ru.md.spo.dbobjects.TaskJPA;
import ru.md.spo.dbobjects.TaskVersionJPA;
import ru.md.spo.ejb.CrmFacadeLocal;
import ru.md.spo.ejb.PupFacadeLocal;
import ru.md.spo.ejb.TaskFacadeLocal;
import ru.md.spo.loader.ProductLoader;
import ru.md.spo.util.Config;

/**
 * Bean implementation class for Session Bean: TaskActionProcessorFacade
 *
 * @ejb.bean name="TaskActionProcessorFacade" type="Stateless"
 *           jndi-name="ejb/com/vtb/ejb/TaskActionProcessorFacadeHome"
 *           view-type="remote" transaction-type="Container"
 *
 * @ejb.home remote-class="com.vtb.ejb.TaskActionProcessorFacadeHome"
 *
 * @ejb.interface remote-class="com.vtb.ejb.TaskActionProcessorFacade"
 * @author AndreyPavlenko formatted by Michael Kuznetsov
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class TaskActionProcessorFacadeBean implements TaskActionProcessorFacadeLocal {
	
    @Resource
    SessionContext mySessionCtx;
    @Resource
    TimerService timerService;

    /*@Resource(name = "jdbc/LOANS")
    private DataSource dataSource;*/

    @Resource(name = "jdbc/CRM")
    private DataSource dataSourceCRM;

    @Autowired
    private MdTaskMapper mdTaskMapper;
    @Autowired
    private DataSource dataSource;

    static final long serialVersionUID = 3206093459760846163L;
    private static final Logger LOGGER = Logger.getLogger(TaskActionProcessorFacadeBean.class.getName());

    private static final String TIMER_CRM_CHECK = "crm.queue";

    public TaskMapper getTaskMapper() {
        try {
            TaskMapper tm = new TaskMapper(dataSource.getConnection());
            return tm;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void startTimer(long timeInSecond) {
        try {
            // clear all running timers
            for (Object obj : timerService.getTimers()) {
                Timer timer = (Timer) obj;
                String typeOfTimer = (String) timer.getInfo();
                if(typeOfTimer!=null && typeOfTimer.equals(TIMER_CRM_CHECK)){
                    LOGGER.info("Таймер " + typeOfTimer + " отключен!");
                    timer.cancel();
                }
            }
        } catch (Exception e) {
        }
        LOGGER.info("таймеры запускаются");
        // запустить таймеры
        timerService.createTimer(timeInSecond * 1000, timeInSecond * 1000, TIMER_CRM_CHECK);
    }

    /**
     * dispatch timer events to the handlers
     */
    @Timeout
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void onTimeout(Timer timer) {
        try {
            String typeOfTimer = null;
            try {
                typeOfTimer = (String) timer.getInfo();
            } catch (Exception e) {
                typeOfTimer = null;
            }

            if (TIMER_CRM_CHECK.equals(typeOfTimer))
                doCRMCheck();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Returns true if application is running on Windows, false otherwise.
     *
     * @return true if application is running on Windows, false otherwise
     */
    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().indexOf("windows") != -1;
    }

    private void doCRMCheck() {
        //LOGGER.info("timer: start CRM check");
        try {
            CrmFacadeLocal crmFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(CrmFacadeLocal.class);
            ProductQueueJPA[] products = crmFacadeLocal.getProductQueue(SPOAcceptType.NOTACCEPT);
            for (ProductQueueJPA productQueue : products) {
                try {
                    loadProductUpdateStatus(productQueue, null, null);
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, e.getMessage(), e);
                    crmFacadeLocal.updateProductQueueStatus(productQueue.getId(), SPOAcceptType.ERROR, e.getMessage());
                }
            }
            for (ProductQueueJPA productQueue : crmFacadeLocal.getProductSPODELETEQueue()) {
                Task task = findByCRMid(productQueue.getId());
                PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
                pupFacadeLocal.closeProcess(task.getId_pup_process(), null);
                crmFacadeLocal.updateProductQueueStatus(productQueue.getId(), SPOAcceptType.ERROR,
                        "заявку аннулирована в CRM");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        //FIXME пока в CRM не готовы лимиты. Поэтому мы их не грузим чтобы не нагружать сервер и не засорять лог
        /*try {
            CrmFacadeLocal crmFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(CrmFacadeLocal.class);
            LimitQueueTO[] limits = crmFacadeLocal.getLimitQueue(SPOAcceptType.NOTACCEPT);
            for (LimitQueueTO limit : limits) {
                LOGGER.info("loading limit " + limit.queue.getId());
                try {
                    crmFacadeLocal.updateLimitQueueStatus(limit.queue.getId(), SPOAcceptType.ACCEPT,
                            "присвоен номер в СПО " + limitLoad(limit).toString());
                    LOGGER.info("limit load complete " + limit.queue.getId());
                } catch (Exception e) {
                    crmFacadeLocal.updateLimitQueueStatus(limit.queue.getId(), SPOAcceptType.ERROR, e.getMessage());
                    LOGGER.warning("limit load error " + e.getMessage());
                }
            }
        } catch (Exception e) {
            LOGGER.warning("cant load limit from CRM: " + e.getMessage());
        }*/
    }

    /**
     * @param crmFacadeLocal
     * @param productQueue
     */
    private Long loadProductUpdateStatus(ProductQueueJPA productQueue, Long userid, Long idProcessType)
            throws Exception, CantChooseProcessType {
        CrmFacadeLocal crmFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(CrmFacadeLocal.class);
        LOGGER.warning("loading product " + productQueue.getId());
        ProductLoader l = findCRMProductById(productQueue.getId());
        Long pupId = productLoad(l, productQueue, userid, idProcessType);
        crmFacadeLocal.updateProductQueueStatus(productQueue.getId(), SPOAcceptType.ACCEPT, "присвоен номер в СПО "
                + pupId.toString());
        LOGGER.warning("product load complete " + productQueue.getId());
        return pupId;
    }

    private Long getProcessType(Organization org, Long userid) throws FactoryException, CantChooseProcessType {
        Set<Long> processTypeList = getProcessTypeList(org, userid);
        if (processTypeList.size() == 0)
            throw new CantChooseProcessType("под условия заявки не подходит ни один бизнес-процесс");
        if (processTypeList.size() > 1) {
            String err = "неоднозначный выбор бизнес-процесса для заявки: ";
            throw new CantChooseProcessType(err);
        }
        return processTypeList.iterator().next();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<Long> getProcessTypeList(Organization org, Long userid)
            throws FactoryException, CantChooseProcessType {
        Set<Long> processTypeList = new java.util.HashSet<Long>();
        CompendiumSpoActionProcessor compenduimSPO = (CompendiumSpoActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory
                .getActionProcessor("CompendiumSpo");
        PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(
                PupFacadeLocal.class);
        ArrayList<OrganisationTypeProcessMap> list4category = (ArrayList<OrganisationTypeProcessMap>) compenduimSPO
                .findOrganisationTypeProcessMapList(0, null, null);
        if (org.getClientCategory().length() < 5)
            throw new CantChooseProcessType(
                    "Ошибка загрузки. Для контрагента неправильно задана категория " +org.getClientCategory()+
                    ". Не могу определить возможные типы процесса.");
        Set<ProcessTypeJPA> userProcessTypeList = pupFacadeLocal.getProcessTypeForUser(userid, null);
        for (ProcessTypeJPA pt : userProcessTypeList) {
            // по всем процессам
            for (OrganisationTypeProcessMap ot : list4category) {
                ArrayList<OrganisationDepartmentMap> maps = (ArrayList<OrganisationDepartmentMap>) ot.getMaps();
                for (OrganisationDepartmentMap odm : maps) {
                    if (pt.getDescriptionProcess().equalsIgnoreCase(ot.getProcessTypeName())
                            && ot.getCategory().toLowerCase().startsWith(
                                    org.getClientCategory().substring(0, 5).toLowerCase())
                            && (ot.getServiceName().equalsIgnoreCase("ГО") && org.getDepartment().startsWith("000") || !ot
                                    .getServiceName().equalsIgnoreCase("ГО")
                                    && !org.getDepartment().startsWith("000"))
                            && org.getCorpBlock().equalsIgnoreCase(odm.getDepName())
                            && odm.getDateFrom().before(new Date())
                            && (odm.getDateTo() == null || odm.getDateTo().after(new Date())))
                        processTypeList.add(pt.getIdTypeProcess());
                }
            }
        }
        return processTypeList;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Long limitLoad(String limitid) throws ModelException, FactoryException, MappingException,
            CantChooseProcessType {
        CrmFacadeLocal flexWorkflowFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(
                CrmFacadeLocal.class);
        return limitLoad(flexWorkflowFacadeLocal.getLimitQueueById(limitid));
    }

    private Long limitLoad(LimitQueueTO limit) throws ModelException, FactoryException, MappingException,
            CantChooseProcessType {
        CompendiumActionProcessor compenduim = (CompendiumActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory
                .getActionProcessor("Compendium");
        CompendiumCrmActionProcessor compenduimCRM = (CompendiumCrmActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory
                .getActionProcessor("CompendiumCrm");
        PupFacadeLocal flexWorkflowFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(
                PupFacadeLocal.class);
        // TODO При осуществлении выгрузки данных о заявке в интерфейсные
        // таблицы, CRM-системой должен вестись журнал аудита

        // проверить существование пользователя и найти его подразделение
        User user;
        try {
            user = compenduim.findUserByLogin(limit.limit.getManager().getLogin()).getVo();
        } catch (Exception e) {
            throw new ModelException("Пользователь с логином " + limit.limit.getManager().getLogin()
                    + " не зарегистрирован в СПО!");
        }
        if (user == null) {
            throw new ModelException("Пользователь с логином " + limit.limit.getManager().getLogin()
                    + " не зарегистрирован в СПО!");
        }
        // определить тип процесса
        if (limit.limit.getAccounts().size() == 0)
            throw new ModelException("у лимита нет ни одной организации");
        Long typeProcess = getProcessType(compenduimCRM.findOrganization(limit.limit.getAccounts().get(0).getId()),
                user.getId());
        // заполнить объект Task
        Task task = limit.toTask();
        task.getHeader().setNumber(flexWorkflowFacadeLocal.getNextMdTaskNumber());// генерация
        // номера
        task.getHeader().getManagers().add(new TaskManager("", user, null));
        task.getHeader().setStartDepartment(new Department(user.getDepartmentID()));
        task.getHeader().getPlaces().add(new Department(user.getDepartmentID()));
        // создать процесс ПУП и взять в работу
        Long pupID = flexWorkflowFacadeLocal.createProcess(typeProcess, user.getId());
        task.setId_pup_process(pupID);
        task.getHeader().setVersion(null);
        createTask(task);
        // обновить атрибут "Заявка №" и Статус
        flexWorkflowFacadeLocal.updatePUPAttribute(pupID, "Заявка №", task.getHeader().getNumber().toString());
        flexWorkflowFacadeLocal.updatePUPAttribute(pupID, "Статус", "Начало работы по заявке");
        flexWorkflowFacadeLocal.updatePUPAttribute(pupID, "Тип кредитной заявки", "Лимит");
        flexWorkflowFacadeLocal.setStandardPeriodVersion(pupID);
        // TODO записать в лог
        return task.getHeader().getNumber();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Long productLoad(String id, Long userid, Long idProcessType) throws Exception, CantChooseProcessType {
        CrmFacadeLocal crmFacade = com.vtb.util.EjbLocator.getInstance().getReference(CrmFacadeLocal.class);
        TaskFacadeLocal taskFacade = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);

        //проверяем не загружена ли уже эта сделка
        ProductQueueJPA productQueue = crmFacade.getProductQueueById(id);
        if (taskFacade.isOpportunityLoaded(productQueue.getOpportunity().getId()))
            throw new Exception("сделка уже была загружена");

        return loadProductUpdateStatus(productQueue, userid, idProcessType);
    }

    private Long productLoad(ProductLoader product, ProductQueueJPA productQueue, Long userid, Long idProcessType)
            throws Exception, CantChooseProcessType {
        CompendiumActionProcessor compenduim = (CompendiumActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory
                .getActionProcessor("Compendium");
        CompendiumCrmActionProcessor compenduimCRM = (CompendiumCrmActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory
                .getActionProcessor("CompendiumCrm");
        PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        // проверить существование пользователя и найти его подразделение
        User user;
        if (userid != null) {
            user = compenduim.getUser(new User(userid));
        } else {
            if (productQueue.getUSERCODE() == null)
                throw new ModelException("логин менеджера не задан");
            try {
                user = compenduim.findUserByLogin(productQueue.getUSERCODE()).getVo();
            } catch (Exception e) {
                throw new ModelException("Пользователь с логином " + productQueue.getUSERCODE()
                        + " не зарегистрирован в СПО!");
            }
            if (user == null) {
                throw new ModelException("Пользователь с логином " + productQueue.getUSERCODE()
                        + " не зарегистрирован в СПО!");
            }
        }
        // определить тип процесса
        if (idProcessType == null)
            idProcessType = getProcessType(compenduimCRM.findOrganization(productQueue.getAccount().getId()), user
                    .getId());
        // заполнить объект Task
        Task task = product.toTask();
        task.getHeader().setCrmQueueId(productQueue.getId());
        Department department = null;
        try {
            DepartmentJPA d = pupFacadeLocal.getDepartmentByName(product.getAccountVO().getRegion());
            department = new Department(new Integer(new Long(d.getIdDepartment()).intValue()));
        } catch (Exception e) {
            LOGGER.warning("Cannot find Departments '" + product.getAccountVO().getRegion() + "' in SPO "
                    + e.getMessage());
        }
        task.getHeader().setStartDepartment(department);
        task.getHeader().setPlace(department);
        task.getHeader().setVersion(null);

        task.getHeader().setNumber(pupFacadeLocal.getNextMdTaskNumber());// генерация
        // номера
        task.getHeader().getManagers().add(new TaskManager("", user, null));

        // создать процесс ПУП и взять в работу
        Long pupID = pupFacadeLocal.createProcess(idProcessType, user.getId());
        task.setId_pup_process(pupID);
        TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
        processor.createTask(task);
        // обновить атрибут "Заявка №" и Статус
        pupFacadeLocal.updatePUPAttribute(pupID, "Заявка №", task.getHeader().getNumber().toString());
        pupFacadeLocal.updatePUPAttribute(pupID, "Тип кредитной заявки", "Сделка");
        pupFacadeLocal.updatePUPAttribute(pupID, "Статус", "Начало работы по заявке");
        pupFacadeLocal.setStandardPeriodVersion(pupID);

        // TODO записать в лог
        return task.getHeader().getNumber();
    }

    /** {@inheritDoc} */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Task createTask(Task anObject) throws MappingException {
        try {
        	Task res = null;
            LOGGER.info("call TaskActionProcessorFacadeBean.createTask ");
    		try {
    			Connection conn = dataSource.getConnection();
    			TaskMapper taskMapper = getTaskMapper();
    			try {
    				if (conn == null)
    					LOGGER.severe("can`t connect to oracle");
    				// создать пустую запись (только id, номер процессаПУП, номер заявки и parent)
    				LOGGER.info("create task step 1.");
    				anObject = taskMapper.createImpl(conn, anObject);
    				// найти айдишник новой записи - эти действия теперь выполняются в insertImpl -> createImpl
    				// сделать апдейт для записи остальных полей
    				LOGGER.info("create task step 2. Update.");
    				updateTask(anObject);
    			}
    			catch (SQLException e) {
    				LOGGER.log(Level.WARNING, "SQL Exception ", e);
    				throw new MappingException(e.getMessage());
    			} catch (Exception e) {
    				LOGGER.log(Level.WARNING, "SQL Exception ", e);
    				throw new MappingException(e.getMessage());
				}    	
    			res = anObject;
    		} catch (SQLException e) {
    			LOGGER.log(Level.WARNING, "SQL Exception ", e);
                throw new MappingException(e.getMessage());
    		}
            return res;
        } catch (MappingException e) {
            mySessionCtx.setRollbackOnly();
            throw e;
        }
    }

    /** {@inheritDoc} */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Task renewTask(Task task) throws MappingException {
        try {
            LOGGER.info("call TaskMapper.insert ");
            Task res = getTaskMapper().renewTask(task);
            updateTask(res);
            return res;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "renewTask Exception ", e);
            mySessionCtx.setRollbackOnly();
            throw new MappingException(e.getMessage());
        }
    }

    /** Получить заявку по ключу */
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Task getTask(Task taskWithKeyValues) throws MappingException {
    	Task task = getTaskInternal(taskWithKeyValues, false, false);
    	return task;
    }

    /** Получить заявку по ключу */
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Task getReportData(Task taskWithKeyValues) throws MappingException {
        return getTaskInternal(taskWithKeyValues, true, false);
    }
	@Override
	public Task getTaskCore(Task taskWithKeyValues) throws MappingException {
		return getTaskInternal(taskWithKeyValues, false, true);
	}

    /**
     * Получить информацию о заявке
     * @param taskWithKeyValues имеющаяся информация о заявке
     * @param reportData признак, получить ли дополнителоьные данные (для отчетов!!!)
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private Task getTaskInternal(Task taskWithKeyValues, boolean reportData, boolean coreData) throws MappingException {
        long tstart=System.currentTimeMillis();
        if(taskWithKeyValues==null || taskWithKeyValues.getId_task()==null){
        	LOGGER.severe("getTaskInternal must specify taskid");
        	return null;
        }
        try {
            Task task = getTaskMapper().findByPrimaryKey(taskWithKeyValues, true);
            if(coreData){
            	LOGGER.info("*** total getTaskInternal() time for core data "+(System.currentTimeMillis()-tstart));
                return task;
            }
            TaskFacadeLocal taskFacade = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
            task.setDisplayNumber(taskFacade.getTask(taskWithKeyValues.getId_task()).getNumberDisplay());
            if (task.getParent() != null)
            	task.getParentData().setSublimitNumber(taskFacade.getNumberDisplayWithRoot(task.getParent()));
            CompendiumCrmActionProcessor compenduimCRM =
                (CompendiumCrmActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumCrm");
            if (task != null && task.getTaskStatusReturn() != null
                    && task.getTaskStatusReturn().getStatusReturn() != null
                    && task.getTaskStatusReturn().getStatusReturn().getId() != null) {
                StatusReturn[] allstatus = compenduimCRM.findStatusReturn(null);
                for (StatusReturn sr : allstatus) {
                    if (sr.getId().trim().equals(task.getTaskStatusReturn().getStatusReturn().getId().trim()))
                        task.getTaskStatusReturn().setStatusReturn(sr);
                }
            }

            try {
                PupFacadeLocal pupFacadeLocal =
                    com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
                task.setGo(pupFacadeLocal.getPUPAttributeValue(task.getId_pup_process(), "ГО"));
                task.setCollegial(pupFacadeLocal.getPUPAttributeValue(task.getId_pup_process(), "Коллегиальный"));

                CompendiumSpoActionProcessor compenduimspo =
                    (CompendiumSpoActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumSpo");
                CompendiumActionProcessor compenduim =
                    (CompendiumActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("Compendium");
                if (task.getCcStatus().getStatus().getId() != null && task.getCcStatus().getStatus().getId().longValue() != 0)
                    task.getCcStatus().setStatus(
                        compenduimspo.findCcResolutionStatusList(
                                          new CcResolutionStatus(task.getCcStatus().getStatus().getId(), null,null, null),
                                          null)
                        .get(0));
                java.util.Date ratingDate = new java.util.Date();// now
                if (task.getTemp().getMeetingDate() != null)// заседание
                    // состоялось
                    ratingDate = task.getTemp().getMeetingDate();
                ArrayList<TaskManager> taskManagers = task.getHeader().getManagers();
                for (TaskManager manager : taskManagers) {
                    manager.setUser(compenduim.findUser(new User(manager.getUser().getId())));
                }

                for (Commission commission : task.getCommissionList()) {
                    if (commission.getName().getId() == null)
                        continue;
                    CommissionType comissionType = new CommissionType(commission.getName().getId());
                    CommissionType found = compenduimCRM.findComissionType(comissionType);
                    commission.setName(found);
                }

                /*
                 * получим полное (а не только Id) значение типа для
                 * периодичности погашения основного долга
                 */
                try {
                    if (!task.isLimit()) {
                        if (task.getPrincipalPay().getPeriodOrder() != null
                                && task.getPrincipalPay().getPeriodOrder().getId() != null)
                            task.getPrincipalPay().setPeriodOrder(
                                    compenduimspo.findCRMRepayment(task.getPrincipalPay().getPeriodOrder()));
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, e.getMessage());
                }
                List<ru.md.domain.ContractorType> contractorTypeList = SBeanLocator.singleton().compendium().findContractorTypeList();
                HashMap<Long, String> contractorTypeMap = new HashMap<Long, String>();
                for (ru.md.domain.ContractorType ct : contractorTypeList) {
                    contractorTypeMap.put(ct.getId(), ct.getName());
                }
                for (TaskContractor tc : task.getContractors()) {
                    for (ContractorType ct : tc.getOrgType()) {
                        ct.setDescription(contractorTypeMap.get(ct.getId()));
                    }
                    // ищем группы
                    CompanyGroup[] group_list = compenduimCRM
                            .findCompanyGroupByOrganisation(new ru.masterdm.compendium.domain.crm.Organization(
                                    true, tc.getOrg().getAccountid()));
                    tc.setGroup(Arrays.asList(group_list));
                }
                /* Рассчитаем процентную ставку */
                try {
                	if(false && Config.enableIntegration()){//процентная ставка в рейтингах не считается. Пока разбираюсь, отключил вызов сервиса
	                	RatingService ratingService = ru.masterdm.integration.ServiceFactory.getService(RatingService.class);
	                	CalcHistoryWso calcHistory = ratingService.getCalcHistoryBySdelkaId(task.getId_task(), task
	                            .getTaskProcent().isRateTypeFixed() ? 0L : 1L, ratingDate);

	                    if (calcHistory == null) {
	                        throw new VtbException("error getting task for sdelkaId = '" + task.getId_task()
	                                + "' rDate = '" + ratingDate + "' tipStavki = '"
	                                + (task.getTaskProcent().isRateTypeFixed() ? 0 : 1) + "'");
	                    }

	                    ratingToTaskProcentSDO(task, calcHistory);
	                    task.getTaskProcent().computeFactForTaskProcent(task);
                	}
                } catch (Exception e) {
                    LOGGER.log(Level.FINE, e.getMessage(), e);
                }

                CompendiumActionProcessor compendium =
                    (CompendiumActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("Compendium");
                /* Найдем все id в справочнике со значением 'Произвольно' */

                List<QuestionType> qtl = compendium.findQuestionTypePage(null, 0, 9000, null).getList();
                HashMap<Integer, QuestionType> questionTypeMap = new HashMap<Integer, QuestionType>();
                for (QuestionType qt : qtl) {
                    questionTypeMap.put(qt.getId(), qt);
                }
                if (task.getCcQuestionType().getId() != null) {
                    task.setCcQuestionType(questionTypeMap.get(task.getCcQuestionType().getId()));
                }
                task.getHeader().getStartDepartment().setShortName(getDepShortNameById(task.getHeader().getStartDepartment().getId()));
                for (TaskDepartment d : task.getHeader().getOtherDepartments()) {
                    d.getDep().setShortName(getDepShortNameById(d.getDep().getId()));
                }
                if (task.getHeader().getPlace() != null && task.getHeader().getPlace().getId() != null
                        && task.getHeader().getPlace().getId().intValue() != 0) {
                    task.getHeader().getPlace().setShortName(getDepShortNameById(task.getHeader().getPlace().getId()));
                }
                for (Department d : task.getHeader().getPlaces()) {
                    d.setShortName(getDepShortNameById(d.getId()));
                }
                CompendiumRatingActionProcessor compenduimRating =
                    (CompendiumRatingActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumRating");
                for (Guarantee guarantee : task.getSupply().getGuarantee()) {
                    if (guarantee.getOrg().getAccountid() != null
                            && !guarantee.getOrg().getAccountid().startsWith("null")) {
                        Organization org = compenduimCRM.findOrganization(guarantee.getOrg().getAccountid());
                        if ((org != null) && (org.getAccountid() != null)) {
                            org = getOrganizationFullData(org.getAccountid());
                            org.setRating(getRating(org.getAccountid()));
                        }
                        guarantee.setOrg(org);

                    }
                    if (guarantee.getPerson().getId() != null) {
                        try {
                            guarantee.setPerson(compenduimspo.findPersonPage(
                                    new Person(guarantee.getPerson().getId()), 0, 1, null).getList().get(0));
                        } catch (Exception e) {
                            guarantee.setPerson(new Person(null));
                            // LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        }
                    }
                    guarantee.setTransRisk(computeTransRisk(compenduimRating, guarantee));
                }
                for (Warranty w : task.getSupply().getWarranty()) {
                    if (w.getOrg().getAccountid() != null && !w.getOrg().getAccountid().startsWith("null")) {
                        Organization org = compenduimCRM.findOrganization(w.getOrg().getAccountid());
                        if ((org != null) && (org.getAccountid() != null)) {
                            org = getOrganizationFullData(org.getAccountid());
                            org.setRating(getRating(org.getAccountid()));
                        }
                        w.setOrg(org);
                    }
                    if (w.getPerson().getId() != null) {
                        try {
                            w.setPerson(compenduimspo.findPersonPage(new Person(w.getPerson().getId()), 0, 1, null)
                                    .getList().get(0));
                        } catch (Exception e) {
                            w.setPerson(new Person(null));
                        }
                    }
                    w.setTransRisk(computeTransRisk(compenduimRating, w));
                }
                for (Deposit d : task.getSupply().getDeposit()) {
                    if (d.getOrg().getId() != null) {
                        Organization org = compenduimCRM.findOrganization(d.getOrg().getId());
                        if ((org != null) && (org.getAccountid() != null)) {
                            org = getOrganizationFullData(org.getAccountid());
                            org.setRating(getRating(org.getAccountid()));
                        }
                        d.setOrg(org);
                    }
                    if (d.getIssuer().getId() != null && !d.getIssuer().getId().startsWith("null")) {
                        Organization org = compenduimCRM.findOrganization(d.getIssuer().getId());
                        if ((org != null) && (org.getAccountid() != null))
                            org = getOrganizationFullData(org.getAccountid());
                        d.setIssuer(org);
                    }
                    if (d.getZalogObject() != null && d.getZalogObject().getId() != null
                            && d.getZalogObject().getId().length() != 0) {
                        Ensuring[] list = compenduimCRM.findEnsuringList(d.getZalogObject(), null);
                        if (list != null && list.length > 0)
                            d.setZalogObject(list[0]);
                    }
                    if (d.getPerson().getId() != null) {
                        try {
                            d.setPerson(compenduimspo.findPersonPage(new Person(d.getPerson().getId()), 0, 1, null)
                                    .getList().get(0));
                        } catch (Exception e) {
                            d.setPerson(new Person(null));
                        }
                    }

                    d.setTransRisk(computeTransRisk(compenduimRating, d));
                }
                // generate sublimits Hierarchy.
                if (task.isLimit() || task.isSubLimit())
                    generateLimitTreeForLimit(compenduimCRM, task);



               /****************************************************************/
               /* получаем все дополнительные данные, необходимые для отчетов! */
               /****************************************************************/


                if (reportData) {

                   // get organization data for contractors
                   for (int i = 0; i < task.getContractors().size(); i++) {
                       try {
                           Organization org = getOrganizationFullData(task.getContractors().get(i).getOrg().getAccountid());
                           task.getContractors().get(i).setOrg(org);
                       } catch (Error e) {}
                   }
                   for(Fine fine :task.getFineList()){
                	   fine.generateColumn2();
                	   fine.generateColumn3();
                   }
                   String productList = "";
                   int cnt = 0;
                   for (ProductGroupJPA pg : taskFacade.getTask(task.getId_task()).getProductGroupList()) {
                	   if (pg.getName() == null || pg.getName().isEmpty())
                		   continue;
                	   if (cnt != 0)
                		   productList += "; ";
               		   productList += pg.getName();
               		   cnt++;
                	   //if (!task.getMain().getProduct_group_names().isEmpty())
                	   //   task.getMain().setProduct_group_names(task.getMain().getProduct_group_names() + "; ");
                	   //task.getMain().setProduct_group_names(task.getMain().getProduct_group_names() + pg.getName());
                   }
                   task.getMain().setProduct_group_names(productList);
               }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
                e.printStackTrace();
            }
            LOGGER.info("*** total getTaskInternal() time "+(System.currentTimeMillis()-tstart));
            LOGGER.info("getTaskInternal() reportData= "+String.valueOf(reportData));
            return task;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            throw new MappingException(e, "Exception caught in TaskActionProcessorFacadeBean.getTask " + e.getMessage());
        }
    }

    private String getDepShortNameById(Integer id){
        if(id==null)
            return "";
        return SBeanLocator.singleton().getDepartmentMapper().getById(id.longValue()).getName();
    }

    @Override
    public Organization getOrganizationFullData(String crmId) {
        // TODO : refactor, move to TaskMapperReadHelper.
        try {
            CompendiumCrmActionProcessor compenduim = (CompendiumCrmActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumCrm");
            Organization org = null;
            try{
                org = compenduim.findOrganization(crmId);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
                e.printStackTrace();
            }

            try{
            	ru.masterdm.compendium.domain.crm.Rating rating = getRating(crmId);
                org.setRating(rating);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
                e.printStackTrace();
            }

            try{
                ru.masterdm.compendium.domain.crm.CompanyGovernance[] govern_list =
                    compenduim.findCompanyGovernanceByOrganisation(org);
                if (govern_list != null) {
                    org.setCompanyGovernances(new ArrayList<CompanyGovernance>(Arrays.asList(govern_list)));
                } else org.setCompanyGovernances(new ArrayList<CompanyGovernance>());
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
                e.printStackTrace();
            }

            try{
                CompanyGroup[] group_list = compenduim.findCompanyGroupByOrganisation(
                    new ru.masterdm.compendium.domain.crm.Organization(true, org.getAccountid()));
                if (group_list != null) {
                    org.setCompanyGroups(new ArrayList<CompanyGroup>(Arrays.asList(group_list)));
                } else org.setCompanyGroups(new ArrayList<CompanyGroup>());
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
                e.printStackTrace();
            }

            try{
                Shareholder[] shareholders = compenduim.findShareholdersByOrganisation(crmId);
                if (shareholders != null) {
                    org.setShareholders(new ArrayList<Shareholder>(Arrays.asList(shareholders)));
                } else org.setShareholders(new ArrayList<Shareholder>());
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
                e.printStackTrace();
            }

            try{
                Contact[] contacts = compenduim.findContactsByOrganisation(crmId);
                if (contacts != null) {
                    org.setContacts(new ArrayList<Contact>(Arrays.asList(contacts)));
                } else org.setContacts(new ArrayList<Contact>());
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
                e.printStackTrace();
            }

            return org;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            return null;
        }
    }

    /** Удалить задачу */
    @Override
    public void deleteTask(Task taskWithKeyValues) throws MappingException {
        try {
            LOGGER.info("remove task " + taskWithKeyValues.getId_task().toString());
            getTaskMapper().remove(taskWithKeyValues);
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
            throw new MappingException(e, "Exception caught in TaskActionProcessorFacadeBean.deleteTask");
        }
    }

    @Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Task findByPupID(Long pupProcessID, boolean full) throws MappingException {
        Task task = null;
        try {
            task = getTask(new Task(getTaskMapper().findByPupID(pupProcessID)));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
        }
        return task;
    }

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void updateTask(Task task) throws Exception {
		try {
	    	long tstart = System.currentTimeMillis();
	        try {
	        	TaskMapper taskMapper = getTaskMapper();
	            Connection conn = dataSource.getConnection();
	            if (conn == null)
	                LOGGER.severe("can`t connect to oracle");
	            
	            try{
	                //  самые основные и общие параметры сделки
	                TaskMapperSaveHelper.saveParameters(conn, task);
	        
	                // контрагенты
	                TaskMapperSaveHelper.saveContragents(conn, task);
	        
	                // основные параметры
	                saveMainParameters(conn, getTaskMapper(), task);
	                
	                //Секция 'Стоимостные условия'
	                TaskMapperSaveHelper.savePriceConditions(conn, task);
	        
	                //Секция   'Обеспечение'
	                TaskMapperSaveHelper.saveSupply(conn, task);
	                
	                // комментарии
	                TaskMapperSaveHelper.saveComments(conn, task);
	                
	                // специальные и остальные условия сделки\лимита
	                TaskMapperSaveHelper.saveSpecialOtherConditions(conn, task);
	        
	                // ответственные подразделения
	                TaskMapperSaveHelper.saveDepartments(conn, task);
	                
	                // EarlyPayment
	                TaskMapperSaveHelper.saveEarlyPayments(conn, task);
	                
	                ////////////////////
	                taskMapper.deleteExtendText(conn, task);
	                taskMapper.insertExtendText(conn, task);
	        
	                //Справка согласования с экспертными подразделениями
	                TaskMapperSaveHelper.saveExpertsData(conn, task);
	                
	                // Секция 'Стоп-Факторы'
	                TaskMapperSaveHelper.saveStopFactors(conn, task);
	                
	                //транши
	                TaskMapperSaveHelper.saveTranches(conn, task);
	                
	                conn.close();
	            } catch (Exception e) {
	                LOGGER.severe("Cant' update Task: " + e.getMessage());
	                e.printStackTrace();
	                throw new MappingException(e, e.getMessage());
	            }
	        } catch (SQLException e) {
	            throw new  MappingException(e, e.getMessage());
	        } catch (MappingException e) {
	            throw new  MappingException(e, e.getMessage());
	        }
	        Long loadTime = System.currentTimeMillis()-tstart;
	        LOGGER.warning("*** TaskMapper.update() time "+loadTime);
		}
		catch (Exception e) {
            mySessionCtx.setRollbackOnly();
			throw e;
		}
		// null, если не было входа в СПО
		if (WPC.getInstance() != null)
			WPC.getInstance().removeFromCacheTaskJDBC(task.getId_task());
	}
	
    /**
     * Сохраняем в базе данных: Секция 'Основные параметры' 
     */
    public void saveMainParameters(Connection conn, TaskMapper taskMapper, Task task) throws MappingException {
        try {
            /*******************************************************************************************************
             *                    Секция 'Основные параметры' Общие для лимита \ сделки                            *
             *******************************************************************************************************/
            PreparedStatement stmn;
            
            TaskMapperSaveHelper.saveProductTypes(conn, task);

            Main main = task.getMain();
            if (main.isMainLoaded()) {
	            mdTaskMapper.updateTargetGroupLimits(task.getId_task(), main.getTargetGroupLimits());
	            mdTaskMapper.updateOtherGoals(task.getId_task(), main.getOtherGoals());
	            
	            // Запрещается предоставление денежных средств на любую из нижеуказанных целей (прямо или косвенно, через третьих лиц)
	            stmn = conn.prepareStatement("DELETE FROM "  + " r_mdtask_forbidden WHERE ID_MDTASK=?");
	            stmn.setObject(1, task.getId_task());
	            stmn.execute();
	            stmn.close();
	            ArrayList<Forbidden> forbiddens = task.getMain().getForbiddens();
	            for (int i = 0; i < forbiddens.size(); i++) {
	                try {
	                    stmn = conn.prepareStatement("INSERT INTO " 
	                            + " r_mdtask_forbidden (id_target, ID_MDTASK, DESCR) " 
	                            + "VALUES (r_mdtask_forbidden_seq.nextval,?,?)");
	                    stmn.setObject(1, task.getId_task());
	                    stmn.setObject(2, forbiddens.get(i).getGoal());
	                    stmn.execute();
	                    stmn.close();
	                } catch (Exception e) {
	                    LOGGER.severe("Cant' insert into r_mdtask_forbidden table" + e.getMessage());
	                    e.printStackTrace();
	                    throw new MappingException(e.getMessage());
	                }
	            }
            }
            
            /*******************************************************************************************************
             *                    Секция 'Основные параметры' Специфичные для лимита \ сделки                      *
             *******************************************************************************************************/
            if (task.isLimit() || task.isSubLimit()) {
                // лимит \ сублимит

                // флажок 'возобновляемый'
            	TaskMapperSaveHelper.updateLimitRenewable(conn, taskMapper, task);

            	TaskMapperSaveHelper.saveCurrencyList(conn, task);

            } else {
                // сделка
            	TaskMapperSaveHelper.saveCurrencyList(conn, task);
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            throw new MappingException(e, e.getMessage());
        }
    }

	
    /**
     * {@inheritDoc}
     */
    @Override
    public void updateTask(Task task, MdTask mdTask) {
        try {
            updateTask(task);

            if (mdTask == null || mdTask.getIdMdtask() == null)
                return;

            mdTaskMapper.updatePipeline(mdTask);
        } catch (Exception e) {
            throw new EJBException(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public MdTask getPipelineWithinMdTask(Long mdTaskId) {
        if (mdTaskId == null)
            return null;

        return mdTaskMapper.getPipelineWithinMdTask(mdTaskId);
    }

    /** {@inheritDoc} */
    @Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ArrayList<Task> findTaskByParent(Long mdtaskid, boolean all, boolean full) throws ModelException {
        ArrayList<Long> taskIDlist;
        ArrayList<Task> tasks = new ArrayList<Task>();
        try {
            taskIDlist = getTaskMapper().findTaskByParent(mdtaskid, all);
            for (int i = 0; i < taskIDlist.size(); i++) {
                tasks.add(this.getTask(new Task(taskIDlist.get(i))));
            }
        } catch (MappingException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
        }
        return tasks;
    }

    /**
     * Экспорт заявки в кредитные комитеты
     * @param mdtaskid - ID заявки
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void export2cc(Long mdtaskid, Long userid) throws ModelException {
        LOGGER.log(Level.INFO, "Starting import questin to CC (export2cc) procedure...");
    	Task task;
        try {
            task = this.getTask(new Task(mdtaskid));
        } catch (Exception e1) {
            e1.printStackTrace();
            LOGGER.warning(e1.getMessage());
            throw new ModelException("не могу получить информацию по заявке: " + e1.getMessage());
        }
        // проверяем, что заполнена сумма и срок
        if (task.getMain().getSum() == null) throw new ModelException("не заполнена сумма заявки");
        if (task.getMain().getPeriod() == null) throw new ModelException("не заполнен срок действия лимита (сделки) в днях");
        LOGGER.info("export2cc");

        ProjectTeamMember initiator = getInitiator(task);

        //нужно сформировать с проверкой на ошибки, а потом передать все
        List<QuestionImportWso> questionImportList = new ArrayList<QuestionImportWso>();
        for (CCQuestion questionTask : getQuestionList(mdtaskid)) {
            QuestionImportWso question = new QuestionImportWso();
            question.setId(questionTask.id);
            question.setInitialDepartmentId(initiator.getIdDepartment());
            question.setInitiatorId(initiator.getIdUser());
            if(task.getTemp().getPlanMeetingDate()!=null)
                question.setProposedMeetingDate(new java.util.Date(task.getTemp().getPlanMeetingDate().getTime()));
            question.setProposedCommitteeId(questionTask.idDep.longValue());
            ru.md.domain.Department fullProposedCommittee = SBeanLocator.singleton().getDepartmentMapper().getById(questionTask.idDep.longValue());
            if (!fullProposedCommittee.isCc())
                throw new ModelException("Подразделение '" + fullProposedCommittee.getName() + "' не является кредитным комитетом");
            question.setDescription(getQuestionDescription(task, questionTask.ccQuestionType));
            question.setContent(buildProjectResolutionReport(questionTask.pkr));
            questionImportList.add(question);
        }
        LOGGER.log(Level.INFO, "Заявка №" + task.getNumberDisplay() + ". Начата передача в КК");
        for (QuestionImportWso question : questionImportList)
            try {
                LOGGER.log(Level.INFO, "Размер передаваемого файла: " + ((question.getContent() != null) ? question.getContent().length : 0) + " байт");
                importQuestion(question);
            } catch (Exception e) {
                e.printStackTrace();
                throw new ModelException(e, "Ошибка экспорта заявки в систему Кредитных Комитетов. Ответ от КК: " + e.getMessage());
            }
        LOGGER.log(Level.INFO, "Import questin to CC (export2cc) procedure finished.");
    }
    private String getQuestionDescription(Task task, Integer ccQuestionType) {
        // тема вопроса: "тип вопроса, контрагент, сумма, валюта, на срок. Заявка СПО №"
        StringBuilder description = new StringBuilder("");

        if (ccQuestionType != null)
            description.append(getQuestionTypeName(ccQuestionType) + " ");

        for (int i = 0; i < task.getContractors().size(); i++) {
            if (i > 0) description.append(", ");
            description.append(task.getContractors().get(i).getOrg().getAccount_name());
        }
        description.append(" на сумму " + task.getMain().getSum().toString() + " ");
        description.append(task.getMain().getCurrency2().getCode() + " на срок " + task.getMain().getPeriod() + " " +
                task.getMain().getPeriodDimension());
        description.append(" Заявка № " + SBeanLocator.singleton().mdTaskMapper().getNumberAndVersion(task.getId_task()) + " СПО");
        return description.toString();
    }
    private String getQuestionTypeName(Integer ccQuestionType) {
        CompendiumActionProcessor compenduim = (CompendiumActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("Compendium");
        return compenduim.findQuestionType(new QuestionType(ccQuestionType)).getName();
    }
    private List<CCQuestion> getQuestionList(Long mdtaskid) {
        MdTaskMapper mapper = SBeanLocator.singleton().mdTaskMapper();
        MdTask mdtask = mapper.getById(mdtaskid);
        List<CCQuestion> questionList = new ArrayList<CCQuestion>();
        if (mdtask.getQuestionGroup() == null)
            questionList.add(mapper.getCCQuestion(mdtaskid));
        else
            for(Long id : mapper.getIdMdtaskByQuestionGroup(mdtask.getQuestionGroup()))
                questionList.add(mapper.getCCQuestion(id));
        return questionList;
    }

    private ProjectTeamMember getInitiator(Task task) throws ModelException {
        // MK : Выбрать активного структуратора из проектной команды (тот, что с галочкой)!
        // он будет являться инициатором
        ArrayList<ProjectTeamMember> structurers = getTaskMapper().readProjectTeam(task, "Структуратор (за МО)");
        if ((structurers == null) || (structurers.isEmpty())) {
            // прочитаем еще руководителей структуратора
            if ((structurers == null) || (structurers.isEmpty()))
                structurers = getTaskMapper().readProjectTeam(task, "Руководитель структуратора (за МО)");
            if ((structurers == null) || (structurers.isEmpty()))
                structurers = getTaskMapper().readProjectTeam(task, "Структуратор");
            if ((structurers == null) || (structurers.isEmpty()))
                structurers = getTaskMapper().readProjectTeam(task, "Руководитель структуратора");
        }
        if ((structurers == null) || (structurers.isEmpty()))
            throw new ModelException("Ни Структуратор, ни Руководитель структуратора не заданы в секции Проектная команда. Инициатор не определён");

        ProjectTeamMember initiator = structurers.get(0);
        return initiator;
    }

    private byte[] buildProjectResolutionReport(String idFile) throws ModelException {
        // получаем отчет проект решения из сохраненных в базе
        byte[] result;
        try {
        	AttachmentActionProcessor processor = (AttachmentActionProcessor) ActionProcessorFactory.getActionProcessor("Attachment");
        	result = processor.findAttachmentDataByPK(new AttachmentFile(idFile)).getFiledata();
        } catch (IllegalArgumentException e) {
        	throw new ModelException(e.getMessage());
        } catch (Exception e1) {
            e1.printStackTrace();
            LOGGER.warning(e1.getMessage());
            throw new ModelException("Не могу получить файл 'Проект кредитого решения': " + e1.getMessage());
        }
        if (result == null) throw new ModelException("Проект кредитого решения содержит пустой файл");
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String getReport(Task task, boolean xml, Integer reportid) throws Exception, ParserConfigurationException, IllegalAccessException {
        try {//подгрузить рейтинги в заявку
        	if(Config.enableIntegration())
	        	for(TaskContractor tc : task.getContractors()){
	        		CalcHistoryInput input = new CalcHistoryInput();
	        		input.setPartnerId(tc.getOrg().getAccountid());
	        		input.setRDate(new java.util.Date());
	        		CalcHistoryOutput output = ru.masterdm.integration.ServiceFactory.getService(RatingService.class).getKEKICalcHistory(input);
	        		if(output!=null){
	        			ru.masterdm.compendium.domain.crm.Rating ratingKEKI = calcHistoryOutput2Rating(output);
	        			ratingKEKI.setType("Рейтинг кредитного подразделения");
	        			tc.getRating().add(ratingKEKI);
	        		}

	        		//output = ServiceFactory.getService(RatingService.class).getSEKZCalcHistory(input);
	        		if(output!=null){
	        			ru.masterdm.compendium.domain.crm.Rating ratingSEKZ = calcHistoryOutput2Rating(output);
	        			ratingSEKZ.setType("Рейтинг подразделения рисков");
	        			tc.getRating().add(ratingSEKZ);
	        		}
	                //подгрузить еще утвержденный рейтинг
	                ApprovedRating ar = getApprovedRating(new Date(),tc.getOrg().getAccountid());
	                ru.masterdm.compendium.domain.crm.Rating r = new ru.masterdm.compendium.domain.crm.Rating();
	                r.setRating(ar.getRating());
	                r.setrDate(ar.getDate());
	                r.setType("утвержденный");
	                tc.getRating().add(r);
	            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
        }
        String xslt = getTaskMapper().getXSLT(reportid);
        ITemplateTransform<Source, Source, StreamResult> templateTransform = (ITemplateTransform<Source, Source, StreamResult>) TemplateTransformFactory
                .newInstance(ETemplateTransform.TRANSFORM_XSLT);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
        org.w3c.dom.Document docData = documentBuilder.newDocument();
        docData.appendChild(task.toXML(docData, "task"));
        DOMSource dataDOMSource = new DOMSource(docData);
        StreamResult resultStream = templateTransform
                .transform(new StreamSource(new StringReader(xslt)), dataDOMSource);
        String projResol = resultStream.getWriter().toString();
        if (!xml)
            return projResol;
        // чистый XML для отладки
        StringWriter stringWriter = new StringWriter();
        Result result = new StreamResult(stringWriter);
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.transform(dataDOMSource, result);
        return stringWriter.getBuffer().toString();
    }

	private ru.masterdm.compendium.domain.crm.Rating calcHistoryOutput2Rating(
			CalcHistoryOutput output) {
		ru.masterdm.compendium.domain.crm.Rating rating = new ru.masterdm.compendium.domain.crm.Rating();
		rating.setBranch(output.getBranch());
		rating.setRegion(output.getRegion());
		rating.setrDate(output.getRDate());
		rating.setRating(output.getRating());
		return rating;
	}

    @Override
    public HashMap<Long, String> findAssignUser(Long idStage, Long idProcess) throws ModelException {
        try {
            return getTaskMapper().findAssignUser(idStage, idProcess);
        } catch (MappingException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            return new HashMap<Long, String>();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            return new HashMap<Long, String>();
        }
    }

    @Override
    public HashMap<Long, String> findUser(Long idStage, Long idDepartament) throws ModelException {
        try {
            return getTaskMapper().findUser(idStage, idDepartament);
        } catch (MappingException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            return new HashMap<Long, String>();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            return new HashMap<Long, String>();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Page findCRMLimitByUser(ArrayList<Long> usersid, int start, int count) throws ModelException {
        LOGGER.info("===findCRMLimitByUser");
        if (usersid == null)
            LOGGER.info("===usersid==null");
        CRMLimitMapper mapper = (CRMLimitMapper) MapperFactory.getSystemMapperFactory().getMapper(CRMLimit.class);
        try {
            Page page = mapper.findLimitByUser(formatLogins2String(usersid), start, count);
            LOGGER.info("limit list. count " + page.getTotalCount());
            ArrayList<CRMLimit> list = (ArrayList<CRMLimit>) page.getList();
            for (CRMLimit limit : list) {
                loadOrganisation(limit);
            }
            return page;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            return Page.EMPTY_PAGE;
        }
    }

    @Override
    public CRMLimit findCRMLimitById(String limitid) throws ModelException {
        CRMLimitMapper mapper = (CRMLimitMapper) MapperFactory.getSystemMapperFactory().getMapper(CRMLimit.class);
        try {
            CRMLimit limit = mapper.findByPrimaryKey(new CRMLimit(limitid));
            return loadOrganisation(limit);
        } catch (NoSuchObjectException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param sprocessor
     * @param limit
     * @throws NoSuchOrganizationException
     * @throws
     */
    private CRMLimit loadOrganisation(CRMLimit limit) {
        limit.setOrganisationFormated("");
        for (String orgid : limit.getOrglist()) {
            try {
                CompendiumCrmActionProcessor compenduimCRM = (CompendiumCrmActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory
                        .getActionProcessor("CompendiumCrm");
                Organization org = compenduimCRM.findOrganization(orgid);
                limit.setOrganisationFormated(limit.getOrganisationFormated() + org.getAccount_name() + " ("
                        + org.getClientCategory() + ")<br />");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
                e.printStackTrace();
            }
        }
        return limit;
    }

    @Override
    public boolean isCRMLimitLoaded(String crmid) throws ModelException {
        try {
            return getTaskMapper().isCRMLimitLoaded(crmid);
        } catch (MappingException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            return true;
        }
    }

    @Override
    public ArrayList<CRMLimit> findCRMSubLimit(String limitid) throws ModelException {
        CRMLimitMapper mapper = (CRMLimitMapper) MapperFactory.getSystemMapperFactory().getMapper(CRMLimit.class);
        try {
            ArrayList<CRMLimit> list = mapper.findCRMSubLimit(limitid);
            for (CRMLimit limit : list) {
                loadOrganisation(limit);
            }
            return list;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            return new ArrayList<CRMLimit>();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Page findCRMProductByUser(ArrayList<Long> usersid, int start, int count) throws ModelException {
    	try {
    		ArrayList<SpoOpportunity> list = null;
    		ArrayList<ProductLoader> result = new ArrayList<ProductLoader>();
    		SpoOpportunityMapper opportunityMapper = new SpoOpportunityMapper(dataSourceCRM.getConnection());
    		Page page;
    		try {
    			page = opportunityMapper.findByFilter("spoSendDate", start, count);
    		} catch (MappingException e1) {
    			throw new ModelException(e1.getMessage());
    		}
    		list = (ArrayList<SpoOpportunity>) page.getList();
    		for (SpoOpportunity spoOpportunity : list) {
    			result.add(getLoaderFromCRM(spoOpportunity));
    		}
    		Page returnPage = new Page(result, start, (start + list.size()) < result.size());
    		returnPage.setTotalCount(page.getTotalCount());
    		return returnPage;
		} catch (Exception e) {
			throw new ModelException(e.getMessage());
		}
    }

    private ProductLoader getLoaderFromCRM(SpoOpportunity spoOpportunity) throws ModelException {
        ProductLoader retVO = new ProductLoader(spoOpportunity);
        SpoOpportunityProductMapper mapper = (SpoOpportunityProductMapper) MapperFactory.getSystemMapperFactory()
                .getMapper(SpoOpportunityProduct.class);
        SpoOpportunityProduct product = null;
        try {
            product = mapper.findByPrimaryKey(new SpoOpportunityProduct(spoOpportunity
                    .getOpportunityID()));
        } catch (Exception e) {
            retVO.setErrorMessage("Ошибка загрузки сделки из CRM с OPPORTUNITYID = "
                    + spoOpportunity.getOpportunityID() + ": " + e.getMessage());
            return retVO;
        }
        SpoAccountMapper amapper = (SpoAccountMapper) MapperFactory.getSystemMapperFactory()
                .getMapper(SpoAccount.class);
        SpoAccount acc = null;
        try {
            acc = amapper.findByPrimaryKey(new SpoAccount(spoOpportunity.getAccountID()));
        } catch (Exception e) {
            retVO
                    .setErrorMessage("Некорректная ссылка в таблице обмена CRM. В представлении V_SPO_ACCOUNT или V_SPO_FB_ACCOUNT нет записи с ACCOUNTID = "
                            + spoOpportunity.getAccountID());
            return retVO;
        }
        retVO.setAccountVO(acc);
        retVO.setProductVO(product);
        return retVO;
    }

    @SuppressWarnings("unchecked")
    private ProductLoader findCRMProductById(String id) throws ModelException {
    	try {
    		// метод не оптимальный, но один ПУП переделывать его потом
    		ArrayList<SpoOpportunity> list = null;
    		com.vtb.mapping.jdbc.SpoOpportunityMapper opportunityMapper = new SpoOpportunityMapper(dataSourceCRM.getConnection());
    		try {
    			Page page = opportunityMapper.findByFilter("spoSendDate", 1, 9000);
    			list = (ArrayList<SpoOpportunity>) page.getList();
    			// очередь для загрузки
    			// предположительно очень маленькая
    			for (SpoOpportunity op : list) {
    				if (op.getId().equals(id))
    					return getLoaderFromCRM(op);
    			}
    		} catch (MappingException e1) {
    			throw new ModelException(e1.getMessage());
    		}
    		return null;
		} catch (Exception e) {
			throw new ModelException(e.getMessage());
		}
    }

    /**
     * Из массива айдишников делает список через запятую логинов
     *
     * @param logins
     * @return
     */
    private String formatLogins2String(ArrayList<Long> usersid) {
        if (usersid == null)
            return null;
        String result = "";
        try {
            CompendiumActionProcessor compenduim = (CompendiumActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory
                    .getActionProcessor("Compendium");

            if (compenduim == null)
                LOGGER.severe("!!!!!!!!!!!!compenduim==null");
            for (int i = 0; i < usersid.size(); i++) {
                if (i > 0)
                    result += ", ";
                result += "'" + compenduim.findUser(new User(usersid.get(i).intValue())).getLogin().toLowerCase() + "'";
            }
        } catch (Exception e1) {
            LOGGER.log(Level.SEVERE, e1.getMessage(), e1);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Page findSPO6List(int start, int count) throws ModelException {
        Process6Mapper mapper = (Process6Mapper) MapperFactory.getSystemMapperFactory().getMapper(Process6.class);
        try {
            return mapper.findAll(start, count);
        } catch (MappingException e) {
            e.printStackTrace();
            LOGGER.severe(e.getMessage());
            throw new ModelException(e.getMessage());
        }
    }

    @Override
    public Process6 findSPO6byId(Long id) throws ModelException {
        Process6Mapper mapper = (Process6Mapper) MapperFactory.getSystemMapperFactory().getMapper(Process6.class);
        try {
            return mapper.findByPrimaryKey(new Process6(id));
        } catch (NoSuchObjectException e) {
            e.printStackTrace();
            LOGGER.severe(e.getMessage());
            throw new ModelException(e.getMessage());
        }
    }

    @Override
    public byte[] getResolution(Long id_template, Long id_mdtask) throws ModelException {
        return getTaskMapper().getResolution(id_template, id_mdtask);
    }

    @Override
    public boolean isPermissionEdit(long idStage, String varname, Integer idTypeProcess) throws MappingException {
        return getTaskMapper().isPermissionEdit(idStage, varname, idTypeProcess);
    }

    @Override
    public Task findByCRMid(String crmid) throws ModelException {
        try {
            return getTaskMapper().findByPrimaryKey(new Task(getTaskMapper().findByCRMid(crmid)));
        } catch (MappingException e) {
            LOGGER.severe(e.getMessage());
            throw new ModelException("Нет такой заявки в СПО с CRMID=" + crmid);
        } catch (SQLException e) {
            LOGGER.severe(e.getMessage());
            throw new ModelException("Нет такой заявки в СПО с CRMID=" + crmid);
        }
    }

    @Override
    public List<Task> findChildrenOfCRMid(String crmid, boolean full) throws ModelException {
        List<Long> taskIDlist;
        ArrayList<Task> tasks = new ArrayList<Task>();
        try {
            taskIDlist = getTaskMapper().findChildrenOfCRMid(crmid);
            for (int i = 0; i < taskIDlist.size(); i++) {
                tasks.add(this.getTask(new Task(taskIDlist.get(i))));
            }
        } catch (MappingException e) {
            LOGGER.severe(e.getMessage());
            throw new ModelException("Ошибка при поиске в базе в методе findChildrenOfCRMid: " + crmid);
        } catch (SQLException e) {
            LOGGER.severe(e.getMessage());
            throw new ModelException("Ошибка при поиске в базе в методе findChildrenOfCRMid: " + crmid);
        }
        return tasks;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Page findRefusableTask(Long userid, Long start, Long count, ProcessSearchParam sp) throws ModelException {
        Page page = getTaskMapper().findRefusableTask(userid, start, count, sp);
        ArrayList<Task> result = new ArrayList<Task>();
        for (Long id : (ArrayList<Long>) page.getList()) {
            try {
                Task task = getTask(new Task(id));  // тут была легкая заявка
                result.add(task);
            } catch (MappingException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        Page returnPage = new Page(result, start.intValue(), (start.intValue() + result.size()) < page.getTotalCount());
        returnPage.setTotalCount(page.getTotalCount());
        return returnPage;
    }

    @Override
    public ArrayList<Long> findAffiliatedUsers(Long mdtaskid) throws ModelException {
        return getTaskMapper().findAffiliatedUsers(mdtaskid);
    }

    @Override
    public void crmlog(String crmid, int i, String message) throws ModelException {
        CRMLimitMapper mapper = (CRMLimitMapper) MapperFactory.getSystemMapperFactory().getMapper(CRMLimit.class);
        try {
            mapper.crmlog(crmid, i, message);
        } catch (MappingException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ArrayList<Task4Rating> getListOpportunity(String organizationid, int startRow, int count) throws Exception {
        ArrayList<Task> tasks = getTaskMapper().findTaskByOrganisation(organizationid, startRow, count);
        ArrayList<Task4Rating> tasks4 = new ArrayList<Task4Rating>();
        for (Task task : tasks) {
            Task4Rating r = new Task4Rating();
            task.getHeader().generateCombinedNumber();
            r.setIdMdTask(task.getId_task());
            r.setNumberDisplay(task.getNumberDisplay());
            r.setPeriod(task.getMain().getPeriod());
            r.setSum(task.getMain().getRurSum());// пересчитать в рубли
            tasks4.add(r);
        }
        return tasks4;
    }

    @Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ArrayList<Task4Rating> getListOpportunity(String organizationid) throws ModelException {
        try {
            ArrayList<Long> taskidList = getTaskMapper().findTaskByOrganisation(organizationid);
            LOGGER.info("getListOpportunity: for organisation " + organizationid + " found processes " + taskidList.size());
            ArrayList<Task4Rating> res = new ArrayList<Task4Rating>();
            int i = 1;
            for (Long mdtaskid : taskidList) {
                try {
                    res.add(getOpportunityInfo(mdtaskid,getTaskMapper()));
                } catch (ModelException e) {
                    // если не удалось загрузить какую-то сделку, то игнорируем её
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                    e.printStackTrace();
                }
                i++;
                if (i > 30)
                    return res;// Лисовский устно просил отдавать ему только 30 свежих сделок
            }
            return res;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            return null;
        }
    }

    @Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Task4Rating getOpportunityInfo(Long mdtaskid) throws Exception {
        return getOpportunityInfo(mdtaskid,getTaskMapper());
    }
    public Task4Rating getOpportunityInfo(Long mdtaskid, TaskMapper tm) throws Exception {
        return tm.getTask4Rating(mdtaskid);
    }

    public ArrayList<Task4Rating> getListOpportunity(Organization organization) throws Exception {
        if (organization == null)
            throw new ModelException("getListOpportunity вызван с параметром null");
        return getListOpportunity(organization.getId());
    }

    public Task4Rating getOpportunityInfo(Task4Rating opportunity) throws Exception {
        if (opportunity == null)
            throw new ModelException("getOpportunityInfo вызван с параметром null");
        return getOpportunityInfo(opportunity.getIdMdTask());
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void makeVersion(Long mdtaskid, Long idUser, String stageName, String roles) throws ModelException {
        try {
            Task task = this.getTask(new Task(mdtaskid));
            String projResol = getReport(task, false, 100);
            CompendiumActionProcessor compendium = (CompendiumActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory
                    .getActionProcessor("Compendium");
            TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
            User user = compendium.findUser(new User(new Integer(idUser.toString())));
            TaskVersionJPA v = new TaskVersionJPA(roles, user.getFullName(), new java.util.Date(), stageName,
                    projResol, mdtaskid);
            taskFacadeLocal.createVersion(v);
            // саблимиты
            ArrayList<Task> sublimits = findTaskByParent(mdtaskid, false, true);
            for (Task sublimit : sublimits) {
                makeVersion(sublimit.getId_task(), idUser, stageName, roles);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            throw new ModelException(e.getMessage());
        }
    }

    @Override
    public ArrayList<TaskVersion> findTaskVersion(Long mdtaskid) throws ModelException {
        return getTaskMapper().findTaskVersion(mdtaskid);
    }

    @Override
    public String getVersion(Long idversion) throws ModelException {
        return getTaskMapper().getVersion(idversion);
    }

    @Override
    public HashMap<Long, Long> getProcessAssign(Long id_pup_process) throws ModelException {
        return getTaskMapper().getProcessAssign(id_pup_process);
    }

    @Override
    public HashMap<Long, String> getRoles2Assign(Long idUser, Long idTask) throws ModelException {
        return getTaskMapper().getRoles2Assign(idUser, idTask);
    }

    /**
     * Import Data for TaskProcent from rating system
     */
    private void ratingToTaskProcentSDO(Task task, CalcHistoryWso o) {
        if ((task == null) || (task.getTaskProcent() == null))
            return;
        TaskProcent tp = task.getTaskProcent();
        if (o != null) {
            tp.setAPC(toDouble(computeARS(task.getMain().getCurrency2().getCode(), o.getArs(), o.getArsEUR(), o.getArsEUR())));
            tp.setComputedRate(toDouble(computeStavka(task.getMain().getCurrency2().getCode(),
            		o.getStavka(), o.getStavkaUSD(), o.getStavkaEUR())));
            tp.setRatingComputedBaseRateType(null); // new BaseRate(new
            // Integer(512))); //
            // tipStavki ???
            tp.setRatingComputedBaseRateTypeAsString(computeBaseRate(task.getMain().getCurrency2().getCode(), o.getIndicator(),
            		o.getIndicatorUSD(), o.getIndicatorEUR()));

            tp.setComputeDate(o.getRDate());

            tp.setBasePremium(toDouble(o.getBazStavka()));
            tp.setTrRiskC1(toDouble(o.getC1()));
            tp.setTrRiskC2(toDouble(o.getC2()));
            tp.setMargin(toDouble(o.getMarzha()));
            tp.setRatingAvailable(true);
        } else {
            tp.setRatingAvailable(false);
            tp
                    .addError("Для данной сделки расчёт фактических значений процентной ставки не проводился либо система расчета рейтинга в данный момент недоступна.");
        }
    }

    private Double toDouble(BigDecimal value) {
        if (value == null)
            return null;
        return value.doubleValue();
    }

    /**
     * Get ARS for different currencies.
     */
    private BigDecimal computeARS(String code, BigDecimal ars, BigDecimal arsUSD, BigDecimal arsEUR) {
        if ((code == null) || (code.equals("")))
            return null;
        if (code.equalsIgnoreCase("RUR"))
            return ars;
        if (code.equalsIgnoreCase("USD"))
            return arsUSD;
        if (code.equalsIgnoreCase("EUR"))
            return arsEUR;
        // ARS for this currency is not computed now.
        return null;
    }

    /**
     * Get Stavka for different currencies.
     */
    private BigDecimal computeStavka(String code, BigDecimal stavka, BigDecimal stavkaUSD, BigDecimal stavkaEUR) {
        if ((code == null) || (code.equals("")))
            return null;
        if (code.equalsIgnoreCase("RUR"))
            return stavka;
        if (code.equalsIgnoreCase("USD"))
            return stavkaUSD;
        if (code.equalsIgnoreCase("EUR"))
            return stavkaEUR;
        // Stavka for this currency is not computed now.
        return null;
    }

    /**
     * Get ARS for different currencies.
     */
    private String computeBaseRate(String code, String indicator, String indicatorUSD, String indicatorEUR) {
        if ((code == null) || (code.equals("")))
            return "";
        if (code.equalsIgnoreCase("RUR"))
            return indicator;
        if (code.equalsIgnoreCase("USD"))
            return indicatorUSD;
        if (code.equalsIgnoreCase("EUR"))
            return indicatorEUR;
        // ARS for this currency is not computed now.
        return "";
    }

    @Override
    public ArrayList<Currency> findParentCurrency(Long parentTaskId) throws ModelException, NoSuchObjectException {
        try {
            com.vtb.mapping.CommissionTypeMapper mapper = (com.vtb.mapping.CommissionTypeMapper) MapperFactory
                    .getSystemMapperFactory().getMapper(com.vtb.domain.CommissionType.class);
            return mapper.findParentCurrency(parentTaskId);
        } catch (Exception e) {
            if (e instanceof NoSuchObjectException)
                throw (NoSuchObjectException) e;
            else
                throw new NoSuchObjectException(e, ("Exception caught in findParentCurrency:" + e.getMessage()));
        }
    }

    @Override
    public void updateAttribute(long idProcess, String nameVar, String valueVar) {
        getTaskMapper().updateAttribute(idProcess, nameVar, valueVar);
    }

    /**
     * Generates LimitTree structure for Limit. Finds all sublimits (with
     * hierarchy in depth??? or not???)
     */
    private void generateLimitTreeForLimit(CompendiumCrmActionProcessor compenduimCRM, Task task) {
        task.getMain().getLimitTreeList().clear();
        try {
            ArrayList<Long> sublimits = getTaskMapper().findTaskByParent(task.getId_task(), false);
            for (Long sublimitId : sublimits) {
                // recursive call. No cycle, if LIGHT Task.
                Task sublimit = getTaskMapper().findByPrimaryKey(new Task(sublimitId), false);
                // not loaded???
                if (sublimit == null)
                    continue;

                // create new LimitTree element and fills it with data from
                // sublimit
                LimitTree element = new LimitTree();
                element.setName(null);
                element.setReferenceId(Formatter.str(sublimitId));

                StringBuilder sb = new StringBuilder();
                String contractors = "";
                boolean groupFlag = false;
                for (int orgindex = 0; orgindex < sublimit.getContractors().size(); orgindex++) {
                    TaskContractor tc = sublimit.getContractors().get(orgindex);
                    Organization org = tc.getOrg();
                    if (orgindex != 0)
                        contractors = contractors + "; ";
                    contractors = contractors + org.getAccount_name();

                    CompanyGroup[] group_list = compenduimCRM
                            .findCompanyGroupByOrganisation(new ru.masterdm.compendium.domain.crm.Organization(true, tc
                                    .getOrg().getAccountid()));
                    tc.setGroup(Arrays.asList(group_list));

                    if (tc.getGroupList() != null)
                        for (CompanyGroup group : tc.getGroupList()) {
                            if (groupFlag)
                                sb.append("; ");
                            sb.append(Formatter.str(group.getName()));
                            groupFlag = true;
                        }
                }
                element.setCompaniesGroup(sb.toString());
                element.setOrganization(contractors);
                element.setLimitVid(sublimit.getHeader().getLimitTypeName());
                element.setSum(sublimit.getMain().getSum());
                element.setCurrency(sublimit.getMain().getCurrency2().getCode());
                element.setValidTo(sublimit.getMain().getValidto());
                if (sublimit.getMain() != null && sublimit.getMain().getPeriod().intValue() != 0)
                    element.setPeriod(sublimit.getMain().getPeriod());
                if (sublimit.getHeader() != null) {
                    element.setCrmstatus(sublimit.getHeader().getCrmstatus());
                    element.setNumber(sublimit.getHeader().getNumber());
                }

                // adds element to LimitTreeList
                task.getMain().getLimitTreeList().add(element);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "SQL Exception in method generateLimitTreeForLimit ", e);
        }
    }

    /**
     * Получаем коэффициенты транзакционного риска
     *
     * @return
     */
    private Double computeTransRisk(CompendiumRatingActionProcessor cR, AbstractSupply supply) {
    	if(!Config.enableIntegration())
			return null;
        // TODO : Kuznetsov : whether we need to take the latest value if not
        // computed???
        try {
            Date dt = new Date(); // TODO : как выбрать дату, узнаем у НН.
            // берем id вида обеспечения
            long id = supply.getOb().getId().longValue();
            // найдем связку вида обеспечения с группой обеспечения.
            ObTypeGroupMember member = cR.getObTypeGroupMember(id, dt);
            // и теперь найдем транзакционный риск для группы обеспечения.
            ObTypeGroupFactor br = cR.getObTypeGroupFactor(member.getGroupId().longValue(), dt);
            if (br == null)
                return null;
            else
                return br.getFactor().doubleValue();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getAttributeValue(Long idProcess, String nameVar) {
        return getTaskMapper().getAttributeValue(idProcess, nameVar);
    }

    @Override
    public void statusNotification(CCStatus status, Long mdtaskid) throws Exception {
        TaskMapper mapper = (TaskMapper) MapperFactory.getSystemMapperFactory().getMapper(Task.class);
        mapper.updateCCStatus(status, mdtaskid);
    }

    @Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ru.masterdm.compendium.domain.crm.Rating getRating(String orgId) {
    	if(!Config.enableIntegration())
    		return new ru.masterdm.compendium.domain.crm.Rating();
        try {
        	ru.masterdm.compendium.domain.crm.Rating rating = new ru.masterdm.compendium.domain.crm.Rating();
    		CalcHistoryInput input = new CalcHistoryInput();
    		input.setPartnerId(orgId);
    		input.setRDate(new java.util.Date());
    		CalcHistoryOutput output = ru.masterdm.integration.ServiceFactory.getService(RatingService.class).getKEKICalcHistory(input);
    		if(output!=null){
    			rating.setBranch(output.getBranch());
    			rating.setRegion(output.getRegion());
    			rating.setrDate(output.getRDate());
    			rating.setRating(output.getRating());
    		}
            return rating;
        } catch (Exception e) {
            LOGGER.log(Level.FINE, e.getMessage(), e);
            return new ru.masterdm.compendium.domain.crm.Rating();
        }
    }
    /**
     * Вместо этого метода следует использовать getRating
     */
    @Override @Deprecated
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public MDCalcHistory getMDCalcHistory(String orgId, Date dt) {
        try {
        	RatingService ratingService = ru.masterdm.integration.ServiceFactory.getService(RatingService.class);
        	CalcHistoryWso calcHistory = ratingService.getCalcHistoryByPartnerId(orgId, 1L, dt);

            MDCalcHistory mdCalcHistory = new MDCalcHistory();
            if (calcHistory == null) {
                LOGGER.warning("error getting calc history for orgId = '" + orgId + "' rDate = '" + dt
                        + "' ratingType = '1'");
            } else {
                mdCalcHistory.setBranch(calcHistory.getBranch());
                mdCalcHistory.setRating(calcHistory.getRating());
                mdCalcHistory.setRDate(calcHistory.getRDate());
                mdCalcHistory.setTotalPoints(calcHistory.getTotalPoints());
            }

            calcHistory = ratingService.getCalcHistoryByPartnerId(orgId, 2L, dt);

            if (calcHistory == null) {
                LOGGER.warning("error getting calc history for orgId = '" + orgId + "' rDate = '" + dt
                        + "' ratingType = '2'");
            } else {
                mdCalcHistory.setExpRating(calcHistory.getRating());
            }
            calcHistory = ratingService.getCalcHistoryByPartnerId(orgId, 3L, dt);

            if (calcHistory == null) {
                LOGGER.warning("error getting calc history for orgId = '" + orgId + "' rDate = '" + dt
                        + "' ratingType = '3'");
            } else {
                mdCalcHistory.setRatingCC(calcHistory.getRating());
            }
            return mdCalcHistory;
        } catch (Exception e) {
            LOGGER.log(Level.FINE, e.getMessage(), e);
            return new MDCalcHistory();
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ApprovedRating getApprovedRating(Date date, String orgid) {
    	if(!Config.enableIntegration())
    		return null;
        try {
        	RatingService ratingService = ru.masterdm.integration.ServiceFactory.getService(RatingService.class);
        	ApprovedRatingWso rdo = ratingService.getApprovedRating(date, orgid);

            if (rdo == null) {
                LOGGER.warning("error getting approved rating for date = '" + date + "'");
                return null;
            }

            ApprovedRating aR = new ApprovedRating();
            aR.setCcId(rdo.getCcId());
            aR.setComment(rdo.getComment());
            aR.setDate(rdo.getDate());
            aR.setId(rdo.getId());
            aR.setName(rdo.getName());
            aR.setPartnerId(rdo.getPartnerId());
            aR.setProtocolNumber(rdo.getProtocolNumber());
            aR.setRating(rdo.getRating());
            aR.setRatingType(rdo.getRatingType());
            aR.setRepDate(rdo.getRepDate());
            aR.setTotalScore(rdo.getTotalScore());
            return aR;

        } catch (Exception e) {
            LOGGER.log(Level.FINE, e.getMessage(), e);
        }
        return null;
    }

    /**
     * @throws ModelException
     * @throws MappingException
     *
     */
    public void comp(Long taskId) throws ModelException, MappingException {
        Task task = getTask(new Task(taskId));   // тут была легка заявка !!!
        @SuppressWarnings("unused")
        ArrayList<Task> brothers = findTaskByParent(task.getParent(), false, false);
    }

    private void importQuestion(QuestionImportWso questionImport) throws Exception {
    	CcService ccService = ru.masterdm.integration.ServiceFactory.getService(CcService.class);
    	ccService.importQuestion(questionImport);
    }


    @Override
    public void exportRating2CRM(Long mdtaskid) throws Exception {
        Task task = getTask(new Task(mdtaskid));
        CRMRating rating = new CRMRating();
        rating.setAccountid(task.getContractors().get(0).getOrg().getId());
        MDCalcHistory h = getMDCalcHistory(rating.getAccountid(), new Date());
        CrmFacadeLocal crmFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(CrmFacadeLocal.class);
        rating.setOpportunityid(task.getHeader().getCrmid());
        rating.setCount_rating(h.getTotalPoints());
        rating.setCount_rating_val(h.getExpRating());
        rating.setCount_rating_date(h.getRDate());
        rating.setCc_rating_val(h.getRatingCC());
        rating.setExpert_rating_val(h.getExpRating());
        crmFacadeLocal.exportRating(rating);
    }

	@Override
	public ArrayList<TaskProduct> readProductTypes(Task task) {
		try {
			return getTaskMapper().readProductTypes(task);
		} catch (Exception e) {
			return new ArrayList<TaskProduct>();
		}
	}

	@Override
	public void saveProductTypes(Task task) {
		try {
			getTaskMapper().saveProductTypes(task);
		} catch (Exception e) {
		}
	}

	@Override
	public void saveCurrencyList(Task task) {
		try {
			getTaskMapper().saveCurrencyList(task);
		} catch (Exception e) {
		}
	}

	@Override
    public void saveSpecialOtherConditions(Task task) {
		try {
			getTaskMapper().saveSpecialOtherConditions(task);
		} catch (Exception e) {
		}
	}

	@Override
	public void saveParameters(Task task) {
		try {
			getTaskMapper().saveParameters(task);
		} catch (Exception e) {
		}
	}

	@Override
	public void saveTarget(Task task) {

	}

}
