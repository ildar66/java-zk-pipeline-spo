package ru.md.spo.ejb;

import com.vtb.domain.*;
import com.vtb.exception.FactoryException;
import com.vtb.exception.MappingException;
import com.vtb.exception.ModelException;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.AttachmentActionProcessor;
import com.vtb.model.ReportBuilderActionProcessor.ReportTemplateParams;
import com.vtb.util.ApplProperties;
import com.vtb.util.CollectionUtils;
import com.vtb.util.Formatter;
import freemarker.template.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;
import ru.masterdm.spo.utils.SBeanLocator;
import ru.md.domain.Currency;
import ru.md.persistence.MdTaskMapper;
import ru.md.persistence.UserMapper;
import ru.md.pup.dbobjects.*;
import ru.md.report.utils.ReportHelper;
import ru.md.spo.dbobjects.*;
import ru.md.spo.report.Expertus;
import ru.md.spo.report.Statistic;
import ru.md.spo.util.Config;
import ru.md.spo.util.ResourceLoader;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.ejb.Timer;
import javax.interceptor.Interceptors;
import javax.persistence.*;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.text.MessageFormat;
import java.util.*;

@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class PupFacade implements PupFacadeLocal {
    private static final Logger LOGGER = LoggerFactory.getLogger(PupFacade.class.getName());

    @EJB
    private TaskFacadeLocal taskFacade;

    @EJB
    private NotifyFacadeLocal notifyFacade;

    @PersistenceUnit(unitName = "flexWorkflowEJBJPA")
    private EntityManagerFactory factory;

    @Resource
    SessionContext cnx;

    @Resource
    TimerService timerService;
    private static final String TIMER_NAME = "on.broken.task.repair";

    @Autowired
    private MdTaskMapper mdTaskMapper;

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public TaskInfoJPA getTask(Long taskId) {
        EntityManager em = factory.createEntityManager();
        return em.find(TaskInfoJPA.class, taskId);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public WorkflowTaskInfo getTaskInfo(long taskid) {
        EntityManager em = factory.createEntityManager();
        TaskInfoJPA taskInfoJPA = em.find(TaskInfoJPA.class, taskid);
        if (taskInfoJPA == null)
            return null;
        WorkflowTaskInfo ti = taskInfoJPA.toWorkflowTaskInfo();
        return ti;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public AssignJPA getAssignbyId(Long idAssign) {
        return factory.createEntityManager().find(AssignJPA.class, idAssign);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public boolean getPUPAttributeBooleanValue(Long idProcess, String nameVar) {
        String val = getPUPAttributeValue(idProcess, nameVar);
        return val.equals("1") || val.equalsIgnoreCase("true") || val.equalsIgnoreCase("y");
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String getPUPAttributeValue(Long idProcess, String nameVar) {
        if (idProcess == null || nameVar == null) return "";
        EntityManager em = factory.createEntityManager();
        Query query = em
                .createNativeQuery("SELECT a.value_var FROM variables v INNER JOIN attributes a ON a.id_var=v.id_var "
                        + "WHERE v.name_var LIKE ? AND a.id_process=?");
        query.setParameter(1, nameVar);
        query.setParameter(2, idProcess);
        List list = query.getResultList();
        if (list.size() == 0)
            return "";
        String res = (String) list.get(0);
        return res == null ? "" : res;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updatePUPAttribute(long idProcess, String nameVar, String valueVar) {
        EntityManager em = factory.createEntityManager();
        //проверим инициирован ли атрибут
        Query query = em.createNativeQuery("SELECT count(*) FROM variables v INNER JOIN attributes a ON a.id_var=v.id_var "
                + "WHERE v.name_var LIKE ? AND a.id_process=?");
        query.setParameter(1, nameVar);
        query.setParameter(2, idProcess);
        if (query.getSingleResult().toString().equals("0")) {//создаем
            query = em.createNativeQuery("INSERT INTO attributes (id_attr,id_var,id_process) " +
                    "SELECT attributes_seq.nextval, v.id_var, p.id_process FROM variables v INNER JOIN processes p ON p.id_type_process=v.id_type_process " +
                    "WHERE p.id_process=? AND v.name_var=?");
            query.setParameter(2, nameVar);
            query.setParameter(1, idProcess);
            query.executeUpdate();
        }

        query = em.createNativeQuery("UPDATE attributes a SET a.value_var=? "
                + "WHERE a.id_var IN (SELECT v.id_var FROM variables v WHERE v.name_var =?) " + "AND a.id_process=?");
        query.setParameter(1, valueVar);
        query.setParameter(2, nameVar);
        query.setParameter(3, idProcess);
        query.executeUpdate();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ArrayList<Long> getWorkList(Long id_user, TaskListType typeList, ProcessSearchParam prms, Long count, Long start) {
        long tstart = System.currentTimeMillis();
        if (id_user == null) return new ArrayList<Long>();
        if (prms == null) prms = new ProcessSearchParam();
        if (prms.parseError) return new ArrayList<Long>();

        ArrayList<Long> res = new ArrayList<Long>();
        EntityManager em = factory.createEntityManager();

        String q = getWorkListQuery(id_user, typeList, prms);
        q = "select id_task,id_process from (select rownum rnum, a.* from (" + q + ") a where rownum <= " + (start + count) + " ) where rnum >= " + (start + 1);
        LOGGER.info("getWorkList query: " + q);
        List<Object[]> list = em.createNativeQuery(q).getResultList();
        for (Object[] elem : list) res.add(((BigDecimal) elem[0]).longValue());
        Long loadTime = System.currentTimeMillis() - tstart;
        LOGGER.warn("*** getWorkList() time " + loadTime);
        return res;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Long getWorkListCount(Long id_user, TaskListType typeList,
                                 ProcessSearchParam prms) {
        long tstart = System.currentTimeMillis();
        if (id_user == null) return 0L;
        if (prms == null) prms = new ProcessSearchParam();
        if (prms.parseError) return 0L;

        EntityManager em = factory.createEntityManager();
        String q = getWorkListQuery(id_user, typeList, prms);
        q = "select count (*) from (" + q + ")";
        BigDecimal obj = (BigDecimal) em.createNativeQuery(q).getSingleResult();
        Long loadTime = System.currentTimeMillis() - tstart;
        LOGGER.warn("*** getWorkListCount(typeList=" + typeList + ", id_user" + id_user + ") time " + loadTime);
        return obj.longValue();
    }

    private String getWorkListQuery(Long id_user, TaskListType typeList,
                                    ProcessSearchParam prms) {
        String q = null;
        EntityManager em = factory.createEntityManager();
        UserJPA user = em.find(UserJPA.class, id_user);
        String scriptName = typeList.name();
        if (typeList.equals(TaskListType.ACCEPT_FOR_REFUSE) && user.isAdmin())
            scriptName += "_admin";
        q = MessageFormat.format(ResourceLoader.getSQL(scriptName),
                id_user.toString(),
                user.getDepartment().getIdDepartment().toString(),
                user.isExpert() ? "1=1" :
                        MessageFormat.format(ResourceLoader.getSQL("childDepartments"),
                                id_user.toString(),
                                user.getDepartment().getIdDepartment().toString()));

        // фильтр
        if (typeList.equals(TaskListType.NOT_ACCEPT) && prms.isHideAssigned())
            q += ResourceLoader.getSQL("HideAssigned");
        if (prms.getExecutorId() != null) q += " and t.id_user = " + prms.getExecutorId();
        if (prms.getNumber() != null && prms.getNumber().length() > 0) {
            try {
                new Integer(prms.getNumber());
                q += " and (MDTASK_NUMBER=" + prms.getNumber() + " or CRMCODE like '%"
                        + prms.getNumber() + "%')";
            } catch (Exception e) {
                q += " and CRMCODE like '%" + prms.getNumber() + "%'";
            }
        }
        if (prms.getProcessTypeID() != null) q += " and p.ID_TYPE_PROCESS=" + prms.getProcessTypeID().toString();
        if (prms.getCurrency() != null) q += " and lower(m.CURRENCY)='" + prms.getCurrency().toLowerCase() + "'";
        if (prms.getSumFrom() != null) q += " and m.MDTASK_SUM>=" + prms.getSumFrom().toString();
        if (prms.getSumTo() != null) q += " and m.MDTASK_SUM<=" + prms.getSumTo().toString();
        if (prms.getContractor() != null)
            q += " and exists (select 1 from r_org_mdtask rom "
                    + "where rom.id_mdtask=m.id_mdtask and rom.id_crmorg in (select '" + prms.getContractor() + "' from dual union all "
                    + "select f.ID_ORG from CRM_FINANCE_ORG f where f.ID_UNITED_CLIENT='" + prms.getContractor() + "')) ";
        if (prms.getType() != null)
            q += " and exists (select 1 from  variables  v inner join "
                    + " attributes a on a.id_var=v.id_var where v.name_var='Тип кредитной заявки' and a.id_process=p.id_process and lower(a.value_var) like '%"
                    + prms.getType().toLowerCase() + "%')";
        if (prms.getStatus() != null)
            q += " and exists (select 1 from  variables  v inner join "
                    + " attributes a on a.id_var=v.id_var where v.name_var='Статус' and a.id_process=p.id_process and lower(a.value_var) like '%"
                    + prms.getStatus().toLowerCase() + "%')";
        if (prms.getPriority() != null)
            q += " and exists (select 1 from  variables  v inner join "
                    + " attributes a on a.id_var=v.id_var where v.name_var='Приоритет' and a.id_process=p.id_process and lower(a.value_var) like '%"
                    + prms.getPriority().toLowerCase() + "%')";
        if (prms.getInitDepartment() != null)
            q += " and lower(d.SHORTNAME) like '%" + prms.getInitDepartment().toLowerCase() + "%'";
        if (prms.getCurrOperation() != null)
            q += " and lower(s.description_stage) like '%" + prms.getCurrOperation().toLowerCase() + "%'";

        if (typeList.equals(TaskListType.NOT_ACCEPT)) {
            //для простого пользователя меньше процессов в ожидающих
            String processType = "0";
            for (Long ptid : getIdBossProcessTypeForUser(id_user))
                processType += ", " + ptid;
            q += " and  p.ID_TYPE_PROCESS in (" + processType + ") ";
        }
        if (prms.getSearch() != null) {// Поиск производится по колонкам №, Контрагент, Сумма, Валюта
            q += getSpecianSearchFilter(prms.getSearch());
        }
        //сортировка и фильтр от битых mdtask
        q += " and m.currency is not null ORDER BY t.id_process desc";
        return q;
    }

    private String getSpecianSearchFilter(String search) {
        String res = "";
        search = search.toLowerCase();
        ArrayList<String> currencyList = new ArrayList<String>();
        ArrayList<String> orgNameList = new ArrayList<String>();
        // если в введенной буквенной подстроке содержатся символы, являющиеся кодами валют, вне зависимости от
        // регистра (RUR/rur/Rur и т.д.), то они отделяются от остальных символов
        // и поиск осуществляется по колонке Валюта;
        for (Currency curr : SBeanLocator.singleton().getCurrencyMapper().getCurrencyList())
            if (search.contains(curr.getCode().toLowerCase())) {
                search = search.replaceAll(curr.getCode().toLowerCase(), "");
                currencyList.add("'" + curr.getCode().toLowerCase() + "'");
            }
        if (currencyList.size() > 0)
            res += " and lower(m.CURRENCY) in (" + CollectionUtils.listJoin(currencyList) + ")";
        for (String substr : new String(search).split(" ")) {
            if (!NumberUtils.isDigits(substr)) {
                orgNameList.add(substr);
                search = search.replaceAll(substr, "");
            }
        }
        if (orgNameList.size() > 0) {
            res += " and exists (select 1 from crm_ek e where m.MAIN_ORG=e.ID";
            for (String orgName : orgNameList)
                res += " and lower(e.NAME) like '%" + orgName.replaceAll("'", "%") + "%'";
            res += ")";
        }
        if (NumberUtils.isDigits(search.replaceAll(" ", ""))) {
            search = search.replaceAll(" ", "");
            res += " and (m.MDTASK_SUM=" + search + " or MDTASK_NUMBER=" + search + ")";
        }
        return res;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Set<UserJPA> getSlave(Long parentId, Long idProcessType) {
        Set<UserJPA> childList = new HashSet<UserJPA>();
        EntityManager em = factory.createEntityManager();
        UserJPA parent = em.find(UserJPA.class, parentId);

        Set<RoleJPA> childRoles = new HashSet<RoleJPA>();

        for (RoleJPA parentRole : parent.getRoles()) {
            if (idProcessType == null || parentRole.getProcess().getIdTypeProcess().equals(idProcessType))
                fillChildRoles(childRoles, parentRole);
        }
        //if(true) return childList;

        Set<DepartmentJPA> departmentList = new HashSet<DepartmentJPA>();
        getChildDepartmentListRecursively(parent.getDepartment(), departmentList);

        for (RoleJPA role : childRoles) {
            for (UserJPA child : role.getUsers()) {
                for (DepartmentJPA department : departmentList) {
                    if (child.getDepartment().getIdDepartment().equals(department.getIdDepartment()) && child.isActive()) {
                        childList.add(child);
                        break;
                    }
                }
            }
        }
        return childList;
    }

    private void getChildDepartmentListRecursively(DepartmentJPA parentDepartment, Set<DepartmentJPA> departmentList) {
        if (!departmentList.contains(parentDepartment)) {
            departmentList.add(parentDepartment);
            for (DepartmentJPA departmentJPA : parentDepartment.getChildDepartmentList()) {
                getChildDepartmentListRecursively(departmentJPA, departmentList);
            }
        }
    }

    private void getParentDepartmentListRecursively(DepartmentJPA childDepartment, Set<DepartmentJPA> departmentList) {
        if (!departmentList.contains(childDepartment)) {
            departmentList.add(childDepartment);
            for (DepartmentJPA departmentJPA : childDepartment.getParentDepartmentList()) {
                getParentDepartmentListRecursively(departmentJPA, departmentList);
            }
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Set<UserJPA> getParentList(Long childId, Long idProcessType) {
        Set<UserJPA> parentList = new HashSet<UserJPA>();
        EntityManager em = factory.createEntityManager();
        UserJPA child = em.find(UserJPA.class, childId);

        Set<RoleJPA> parentRoles = new HashSet<RoleJPA>();
        for (RoleJPA childRole : child.getRoles()) {
            if (idProcessType == null || childRole.getProcess().getIdTypeProcess().equals(idProcessType))
                fillParentRoles(parentRoles, childRole);
        }

        Set<DepartmentJPA> departmentList = new HashSet<DepartmentJPA>();
        getParentDepartmentListRecursively(child.getDepartment(), departmentList);

        for (RoleJPA role : parentRoles) {
            for (UserJPA parent : role.getUsers()) {
                for (DepartmentJPA department : departmentList) {
                    if (parent.getDepartment().getIdDepartment().equals(department.getIdDepartment()) && parent.isActive()) {
                        parentList.add(parent);
                        break;
                    }
                }
            }
        }
        return parentList;
    }

    /**
     * получить список всех подчиненных ролей по иерархии
     */
    private void fillChildRoles(Set<RoleJPA> childRoles, RoleJPA bossRole) {
        for (RoleJPA childRole : bossRole.getChildRoles()) {
            if (!childRoles.contains(childRole)) {
                childRoles.add(childRole);
                fillChildRoles(childRoles, childRole);
            }
        }
        if (!bossRole.getChildRoles().isEmpty())//VTBSPO-727
            childRoles.add(bossRole);
    }

    /**
     * получить список всех вышестоящих ролей по иерархии
     */
    private void fillParentRoles(Set<RoleJPA> parentRoles, RoleJPA childRole) {
        for (RoleJPA parentRole : childRole.getParentRoles()) {
            if (!parentRoles.contains(parentRole)) {
                parentRoles.add(parentRole);
                fillParentRoles(parentRoles, parentRole);
            }
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ArrayList<AssignJPA> getAssignToUsersTasksList(Long idBoss, Long idProcessType) {
        ArrayList<AssignJPA> res = new ArrayList<AssignJPA>();
        // получить список назначений на моих подчиненных для незакрытых заявок
        for (UserJPA slave : getSlave(idBoss, idProcessType)) {
            for (AssignJPA assign : slave.getAssigns())
                if (idProcessType == null || assign.getId_type_process().equals(idProcessType))
                    res.add(assign);
        }
        Collections.sort(res);
        return res;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Set<ProcessTypeJPA> getStartProcessType(Long idUser) {
        long tstart = System.currentTimeMillis();
        Set<ProcessTypeJPA> res = new HashSet<ProcessTypeJPA>();
        EntityManager em = factory.createEntityManager();
        Query query = em.createNativeQuery("SELECT ID_TYPE_PROCESS FROM " +
                "(SELECT DISTINCT tp.ID_TYPE_PROCESS, tp.description_process FROM TYPE_PROCESS tp " +
                "WHERE tp.DESCRIPTION_PROCESS NOT LIKE 'Изменение условий%' " + // по этим БП нельзя создать заявку
                "AND exists (" +
                "SELECT sr.ID_ROLE FROM stages " +
                "INNER JOIN STAGES_IN_ROLE sr ON sr.ID_STAGE=stages.id_stage " +
                "INNER JOIN role_active ra ON ra.ID_ROLE=sr.ID_ROLE " +
                "WHERE stages.id_type_process = tp.ID_TYPE_PROCESS AND stages.active = 1 AND ra.ID_USER=? " +
                "AND stages.id_stage IN ( SELECT e.id_stage_to FROM edges e WHERE e.id_stage_from IS NULL))" +
                " ORDER BY tp.DESCRIPTION_PROCESS)");
        query.setParameter(1, idUser);
        for (BigDecimal idTypeProcess : (List<BigDecimal>) query.getResultList())
            res.add(em.find(ProcessTypeJPA.class, idTypeProcess.longValue()));
        Long loadTime = System.currentTimeMillis() - tstart;
        LOGGER.warn("*** total getProcessTypeForUser() time " + loadTime);
        return res;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Set<ProcessTypeJPA> getProcessTypeForUser(Long idUser, Boolean isRunProcess) {
        long tstart = System.currentTimeMillis();
        Set<ProcessTypeJPA> res = new HashSet<ProcessTypeJPA>();
        EntityManager em = factory.createEntityManager();
        StringBuilder queryString = new StringBuilder();
        queryString.append("select distinct r.ID_TYPE_PROCESS from role_active ra inner join roles r on r.ID_ROLE=ra.ID_ROLE "
                + "inner join TYPE_PROCESS tp on r.id_type_process = tp.id_type_process where ra.ID_USER=?");
        if (isRunProcess != null && isRunProcess) {
            queryString.append("and tp.description_process not like '%Pipeline%'");
        }
        if (isRunProcess != null && !isRunProcess) {
            queryString.append("and tp.description_process like '%Pipeline%'");
        }
        if (isRunProcess == null) {

        }
        Query query = em.createNativeQuery(queryString.toString());
        query.setParameter(1, idUser);
        for (BigDecimal idTypeProcess : (List<BigDecimal>) query.getResultList())
            res.add(em.find(ProcessTypeJPA.class, idTypeProcess.longValue()));
        Long loadTime = System.currentTimeMillis() - tstart;
        LOGGER.warn("*** total getProcessTypeForUser() time " + loadTime);
        return res;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void assign(Long idUser, Long idRole, Long idProcess, Long idWhoAssign) throws Exception {
        assign(idUser, idRole, idProcess, idWhoAssign, false);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void assign(Long idUser, Long idRole, Long idProcess, Long idWhoAssign, boolean auto) throws Exception {
        java.util.Date now = new Date();
        // Проверить, что idWhoAssign начальник idUser
        EntityManager em = factory.createEntityManager();
        UserJPA boss = em.find(UserJPA.class, idWhoAssign);
        UserJPA slave = em.find(UserJPA.class, idUser);
        ProcessJPA process = em.find(ProcessJPA.class, idProcess);
//        if (!auto /* && !getSlave(idWhoAssign, process.getProcessType().getIdTypeProcess()).contains(slave) */)
//            throw new Exception("Нет прав назначить пользователя " + slave.getFullName()
//                    + ". Он не является подчиненным пользователя " + boss.getFullName());
        // убрать старые назначения
        deleteOldAssign(idRole, idProcess, now, idWhoAssign);
        // создать запись в логе
        Long id_process_event = getNextProcessEventID();
        Query query = em.createNativeQuery("INSERT INTO process_events (id_process, id_process_type_event, "
                + "date_event, id_user, id_process_event) " + "VALUES( ?, 6, ?, ?, ?)");
        query.setParameter(1, idProcess);
        query.setParameter(2, now);
        query.setParameter(3, idWhoAssign);
        query.setParameter(4, id_process_event);
        query.executeUpdate();
        // назначить
        query = em.createNativeQuery("INSERT INTO assign ( id_process_event, id_role, id_user_to, "
                + "id_user_from, level_assign ) VALUES(?, ?, ?, ?, 1 )");
        query.setParameter(3, idUser);
        query.setParameter(2, idRole);
        query.setParameter(4, idWhoAssign);
        query.setParameter(1, id_process_event);
        query.executeUpdate();
        query = em
                .createNativeQuery("INSERT INTO assign_history "
                        + "( id_assign, id_process_event, id_role, id_user_to, may_reassign, id_user_from, level_assign ) "
                        + "SELECT ass.id_assign, ass.id_process_event, ass.id_role, ass.id_user_to, ass.may_reassign, ass.id_user_from, ass.level_assign "
                        + "FROM assign ass INNER JOIN process_events pe ON pe.id_process_event=ass.id_process_event "
                        + "WHERE pe.id_process_event= ? ");
        query.setParameter(1, id_process_event);
        query.executeUpdate();
        //отправить уведомление о назначении
        /*if (!auto){
            SPOMessageActionProcessor messageProcessor = (SPOMessageActionProcessor) ActionProcessorFactory.getActionProcessor("SPOMessage");
            TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
            TaskJPA task = taskFacadeLocal.getTaskByPupID(idProcess);
            String bodyMessage=MessageFormat.format("Вы назначены исполнителем по Заявке № "+
                    "<a href=\"{1}/showTaskList.do?typeList=all&searchNumber={2}\">{3}</a> ({4}) процесса {5} для роли {0}",
                    em.find(RoleJPA.class, idRole).getNameRole(), getBaseURL(idUser),
                    task.getMdtask_number(),task.getNumberDisplay(),
                    task.getOrganisation(),process.getProcessType().getDescriptionProcess());
            String subject = MessageFormat.format("Вы назначены исполнителем по роли {0} по Заявке №{1}",
                    em.find(RoleJPA.class, idRole).getNameRole(),task.getNumberDisplay());
            messageProcessor.send(boss.getMailUser(),boss.getFullName(),
                    slave.getMailUser(), subject, bodyMessage);
        }*/
    }

    /**
     * убрать старые назначения.
     *
     * @param idRole
     * @param idProcess
     */
    private void deleteOldAssign(Long idRole, Long idProcess, java.util.Date now, Long idWhoAssign) {
        EntityManager em = factory.createEntityManager();
        // 1. найти старое назначение на эту роль
        Query query = em.createNativeQuery("SELECT id_assign FROM assign a WHERE a.id_role=? "
                + "AND a.id_process_event IN "
                + "(SELECT pe.id_process_event FROM process_events pe WHERE pe.id_process=?)");
        query.setParameter(1, idRole);
        query.setParameter(2, idProcess);
        if (query.getResultList().size() == 0)
            return;
        BigDecimal id_assign = (BigDecimal) query.getSingleResult();
        if (id_assign == null)
            return;
        // 2. добавить в process_events снятие назначения
        Long id_process_event = getNextProcessEventID();
        query = em
                .createNativeQuery("INSERT INTO process_events ( id_process, id_process_type_event, date_event, id_user,id_process_event ) "
                        + "VALUES ( ?, 8, ?, ? ,? )");
        query.setParameter(1, idProcess);
        query.setParameter(2, now);
        query.setParameter(3, idWhoAssign);
        query.setParameter(4, id_process_event);
        query.executeUpdate();
        // 3. добавить в assign_history запись из assign
        query = em
                .createNativeQuery("INSERT INTO assign_history "
                        + "( id_assign, id_process_event, id_role, id_user_to, may_reassign, id_user_from, level_assign ) "
                        + "( SELECT ass.id_assign, ? AS id_process_event, ass.id_role, ass.id_user_to, ass.may_reassign, ass.id_user_from, ass.level_assign "
                        + "FROM assign ass WHERE ass.id_assign = ? )");
        query.setParameter(1, id_process_event);
        query.setParameter(2, id_assign);
        query.executeUpdate();
        // 4. удалить assign
        query = em.createNativeQuery("DELETE FROM assign a WHERE a.id_assign=? ");
        query.setParameter(1, id_assign);
        query.executeUpdate();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public boolean isPermissionEdit(Long pupTaskId, String attrname) {
        EntityManager em = factory.createEntityManager();
        Query query = em.createNativeQuery("SELECT t.id_task,v.name_var,sp.id_stage,sp.id_permission FROM tasks t "
                + "INNER JOIN variables v ON v.id_type_process=t.id_type_process  "
                + "INNER JOIN stages_permissions sp ON sp.id_var=v.id_var AND t.id_stage_to=sp.id_stage "
                + "WHERE t.id_task=? AND v.name_var=?  AND sp.id_permission=3 AND t.id_status=2");
        query.setParameter(1, pupTaskId);
        query.setParameter(2, attrname);
        List list = query.getResultList();
        return list.size() > 0;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public UserJPA getUser(Long idUser) {
        return factory.createEntityManager().find(UserJPA.class, idUser);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public UserJPA getUserByLogin(String login) throws Exception {
        String mainLogin = ((UserMapper) SBeanLocator.singleton().getBean("userMapper")).
                getUserByLogin(login).getLogin();
        EntityManager em = factory.createEntityManager();
        Query query = em.createQuery("SELECT u FROM UserJPA u where LOWER(u.login) = :login");
        query.setParameter("login", mainLogin.toLowerCase());
        List<UserJPA> list = query.getResultList();
        if (list.size() == 0) throw new Exception("нет пользователя с таким логином");
        return list.get(0);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public RoleJPA getRole(Long idRole) {
        return factory.createEntityManager().find(RoleJPA.class, idRole);
    }

    @Override
    public Long createProcess(Long id_type_process, Long idUser) throws ModelException {
        return createProcessWithAccept(id_type_process, idUser, true);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void setStandardPeriodVersion(Long id_process) {
        EntityManager em = factory.createEntityManager();
        ProcessJPA process = em.find(ProcessJPA.class, id_process);
        Query query;
        try {
            query = em.createNativeQuery("SELECT max(v.id_spv) FROM standard_period_version v WHERE v.id_type_process=?");
            query.setParameter(1, process.getProcessType().getIdTypeProcess());
            BigDecimal spv = (BigDecimal) query.getSingleResult();
            if (spv != null) {
                String sql = "update mdtask t set t.standard_period_version=" + spv.longValue() + " where t.id_pup_process=" + id_process;
                em.createNativeQuery(sql).executeUpdate();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void reacceptWork(Long idTask, Long idUser) throws Exception {
        if (idTask == null)
            throw new Exception("idTask не может быть пустым");
        EntityManager em = factory.createEntityManager();
        TaskInfoJPA taskInfoJPA = em.find(TaskInfoJPA.class, idTask);
        if (taskInfoJPA == null)
            throw new Exception("нет задачи с номером " + idTask);
        //проверить, что она существует и со статусом 2
        if (taskInfoJPA.getIdStatus() == null || taskInfoJPA.getIdStatus().intValue() != 2)
            throw new Exception("Эта задача не активна");

        Query q = em.createNativeQuery("UPDATE tasks SET id_user=NULL, id_status = 5 WHERE id_task = :idTask");
        q.setParameter("idTask", idTask);
        q.executeUpdate();
        q = em.createNativeQuery("INSERT INTO task_events(id_task, id_task_type_event, date_event, id_user ) " +
                "VALUES(:idTask, 5, :now, :idUser )");
        q.setParameter("idTask", idTask);
        q.setParameter("now", new java.util.Date());
        q.setParameter("idUser", idUser);
        q.executeUpdate();

        Long newtaskid = getNextSequenceValue("tasks_seq");
        q = em.createNativeQuery("INSERT INTO tasks (id_type_process, id_process, id_stage_to, id_user, type_complation, id_department, id_status, id_task) " +
                "VALUES (:id_type_process, :id_process, :id_stage_to, NULL, NULL, :id_department, 1, :id_task)");
        q.setParameter("id_type_process", taskInfoJPA.getProcessType().getIdTypeProcess());
        q.setParameter("id_process", taskInfoJPA.getProcess().getId());
        q.setParameter("id_stage_to", taskInfoJPA.getStage().getIdStage());
        q.setParameter("id_department", taskInfoJPA.getIdDepartament());
        q.setParameter("id_task", newtaskid);
        q.executeUpdate();

        q = em.createNativeQuery("INSERT INTO task_events (id_task, id_task_type_event, date_event, id_user) VALUES(:idTask, 1, :now, :idUser)");
        q.setParameter("idTask", newtaskid);
        q.setParameter("now", new java.util.Date());
        q.setParameter("idUser", idUser);
        q.executeUpdate();

        q = em.createNativeQuery("INSERT INTO stages_from (id_task, id_stage, come_order) " +
                "(SELECT :newidTask, id_stage, come_order FROM stages_from WHERE id_task = :oldidTask)");
        q.setParameter("newidTask", newtaskid);
        q.setParameter("oldidTask", idTask);
        q.executeUpdate();
    }

    /**
     * Взять в работу.
     *
     * @throws Exception
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void acceptWork(Long idTask, Long idUser) throws Exception {
        EntityManager em = factory.createEntityManager();
        //проверить статус: может не могу взять в работу
        int status = getTaskStatus(idTask);
        if (status == 2)
            throw new Exception("Эта операция уже в работе у пользователя " + getTask(idTask).getExecutorName());
        if (status > 2)
            throw new Exception("Эта операция уже завершена");
        Query query = em.createNativeQuery("UPDATE tasks SET id_user = ?1, id_status = 2 WHERE id_task = ?2");
        query.setParameter(1, idUser);
        query.setParameter(2, idTask);
        query.executeUpdate();
        //вставить строку или обновить
        query = em.createNativeQuery("DELETE FROM task_events WHERE id_task=?1 AND id_task_type_event=2");
        query.setParameter(1, idTask);
        query.executeUpdate();
        query = em.createNativeQuery("INSERT INTO task_events (id_task, id_task_type_event, date_event, id_user )"
                + " VALUES ( ?2, 2, ?3, ?1)");
        query.setParameter(1, idUser);
        query.setParameter(2, idTask);
        query.setParameter(3, new Date());
        query.executeUpdate();
        //если пользователь берет операцию в работу и у него нет на неё назначения,
        /*query = em.createNativeQuery("select count(*) from tasks t inner join stages_in_role sr on sr.id_stage=t.id_stage_to "+
                "where t.id_task=? and exists (select * from assign a "+
                "inner join process_events pe on pe.id_process_event=a.id_process_event "+
                "where a.id_user_to=? and a.id_role=sr.id_role and pe.id_process=t.id_process)");
        query.setParameter(2, idUser);
        query.setParameter(1, idTask);
        BigDecimal cnt = (BigDecimal) query.getSingleResult();
        LOGGER.debug(cnt.toPlainString());
        if (cnt.longValue()==0){
            UserJPA user = getUser(idUser);
            TaskInfoJPA task = getTask(idTask);
            //то СПО должна автоматически назначать пользователя на эту заявку по всем ролям, которые могут выполнять эту
            //операцию от имени самого пользователя (в хронологии будет отображено, что пользователь сам себя назначил).
            for (RoleJPA stageRole : task.getStage().getRoles()){
                for (RoleJPA userRole : user.getRoles()){
                    if (stageRole.equals(userRole)){
                        assign(user.getIdUser(), userRole.getIdRole(), task.getProcess().getId(),
                                user.getIdUser(), true);
                    }
                }
            }
        }*/
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<StageJPA> getFirstStages(Long idTypeProcess) {
        EntityManager em = factory.createEntityManager();
        Query query = em.createNativeQuery("SELECT stages.id_stage FROM stages "
                + "WHERE stages.id_type_process = ? AND stages.active = 1 "
                + "AND stages.id_stage IN ( SELECT e.id_stage_to FROM edges e WHERE e.id_stage_from IS NULL )");
        query.setParameter(1, idTypeProcess);
        List<BigDecimal> list = query.getResultList();
        List<StageJPA> res = new ArrayList<StageJPA>();
        for (BigDecimal id_stage : list)
            res.add(em.find(StageJPA.class, id_stage.longValue()));
        return res;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<AcceptJPA> getAcceptList(Long userId) {
        EntityManager em = factory.createEntityManager();
        Query query = em
                .createQuery("SELECT a FROM AcceptJPA a where a.user.id = :userId and a.acceptDate is null ORDER BY a.initDate DESC");
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    @Override
    public List<AcceptJPA> getAcceptList(Long userId, int startPosition, int maxResult) {
        EntityManager em = factory.createEntityManager();
        Query query = em
                .createQuery("SELECT a FROM AcceptJPA a where a.user.id = :userId and a.acceptDate is null ORDER BY a.initDate DESC");
        query.setParameter("userId", userId);
        query.setMaxResults(maxResult);
        query.setFirstResult(startPosition - 1);
        return query.getResultList();
    }

    @Override
    public Long getAcceptListSize(Long userId) {
        EntityManager em = factory.createEntityManager();
        Query query = em
                .createQuery("SELECT COUNT(a) FROM AcceptJPA a where a.user.id = :userId and a.acceptDate is null");
        query.setParameter("userId", userId);
        return (Long) query.getSingleResult();
    }

    @Override
    public void merge(Object entity) {
        EntityManager em = factory.createEntityManager();
        em.merge(entity);
    }

    @Override
    public void persist(Object entity) {
        EntityManager em = factory.createEntityManager();
        em.persist(entity);
    }

    @Override
    public void deleteAcceptList(Long taskId) {
        EntityManager em = factory.createEntityManager();
        Query query = em.createQuery("DELETE FROM AcceptJPA a WHERE a.taskInfo.idTask = :taskId");
        query.setParameter("taskId", taskId);
        query.executeUpdate();
    }

    @Override
    public List<AcceptJPA> getAcceptList(Long taskId, Long userId) {
        EntityManager em = factory.createEntityManager();
        Query query = em
                .createQuery("SELECT a FROM AcceptJPA a WHERE a.user.idUser = :userId and a.taskInfo.idTask = :taskId");
        query.setParameter("taskId", taskId);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    @Override
    public List<AcceptJPA> getApprovedAcceptList(Long taskId) {
        EntityManager em = factory.createEntityManager();
        Query query = em
                .createQuery("SELECT a FROM AcceptJPA a WHERE a.taskInfo.idTask = :taskId and a.acceptDate is not null ORDER BY a.acceptDate DESC");
        query.setParameter("taskId", taskId);
        return query.getResultList();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ArrayList<UserJPA> getUser4assign(Long stageid, Long userid) {
        EntityManager em = factory.createEntityManager();
        UserJPA boss = em.find(UserJPA.class, userid);
        ArrayList<UserJPA> res = new ArrayList<UserJPA>();
        res.add(boss);//добавим самого себя
        String sql = MessageFormat.format(ResourceLoader.getSQL("User4assign"),
                userid.toString(),
                boss.getDepartment().getIdDepartment().toString(),
                stageid.toString());
        System.out.println("User4assign:" + sql);
        Query query = em.createNativeQuery(sql);
        List<Object[]> list = query.getResultList();
        for (Object[] obj : list) {
            BigDecimal slaveid = (BigDecimal) obj[0];
            if (slaveid.longValue() != boss.getIdUser().longValue()) {
                res.add(em.find(UserJPA.class, slaveid.longValue()));
            }
        }
        return res;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public HashMap<UserJPA, List<RoleJPA>> getAssignedUser(Long idTask) {
        long tstart = System.currentTimeMillis();
        EntityManager em = factory.createEntityManager();
        HashMap<UserJPA, List<RoleJPA>> res = new HashMap<UserJPA, List<RoleJPA>>();
        Query query = em.createNativeQuery(MessageFormat.format(ResourceLoader.getSQL("whoAssigned"),
                idTask.toString()));
        List<Object[]> list = query.getResultList();
        for (Object[] obj : list) {
            UserJPA user = em.find(UserJPA.class, ((BigDecimal) obj[0]).longValue());
            RoleJPA role = em.find(RoleJPA.class, ((BigDecimal) obj[1]).longValue());
            if (res.containsKey(user)) {
                res.get(user).add(role);
            } else {
                List<RoleJPA> roles = new ArrayList<RoleJPA>();
                roles.add(role);
                res.put(user, roles);
            }
        }
        LOGGER.warn("*** PupFacade.getAssignedUser(" + idTask + ") time " + (System.currentTimeMillis() - tstart));
        return res;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Long getProcessListCount(Long id_user, Integer id_department,
                                    ProcessSearchParam processSearchParam) {
        long tstart = System.currentTimeMillis();
        EntityManager em = factory.createEntityManager();
        String query = getProcessListQuery(id_user, id_department, processSearchParam);
        query = "select count (*) from (" + query + ")";
        BigDecimal obj = (BigDecimal) em.createNativeQuery(query).getSingleResult();
        LOGGER.warn("*** PupFacadeLocal.getProcessListCount() time " + (System.currentTimeMillis() - tstart));
        return obj.longValue();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Long getQueryPageNumber(Long id_user, Integer id_department,
                                   ProcessSearchParam processSearchParam, Long idPupProcess, Long idTask, String typeList) {
        String query = null;
        try {
            final Long PAGE_SIZE = 10L;
            long tstart = System.currentTimeMillis();

            LOGGER.debug("======PupFacade.getQueryPageNumber idUser '" + id_user + "', id_department '" + id_department + ", idPupProcess '" + idPupProcess + ", idTask '" + idTask + "', typeList '" + typeList + "'");

            if (typeList == null)
                throw new RuntimeException("typeList is null");
            if (idPupProcess == null)
                throw new RuntimeException("idPupProcess is null");
            if (id_user == null)
                throw new RuntimeException("id_user is null");

            if (TaskListType.isAllMode(typeList)) {
                query = getProcessListQuery(id_user, id_department, processSearchParam);
                query = "select query_page_number from (select id_process, floor( (rownum - 1)/" + PAGE_SIZE + ") query_page_number from (" + query + ")) where id_process= " + idPupProcess +
                        " and rownum = 1 ";
            } else {
                TaskListType taskListType = TaskListType.getByType(typeList);
                query = getWorkListQuery(id_user, taskListType, processSearchParam);
                String taskFilter = (idTask != null ? " and id_task = " + idTask : "");
                query = "select query_page_number from (select id_process, id_task, floor( (rownum - 1)/" + PAGE_SIZE + ") query_page_number from (" + query + ")) where id_process= " + idPupProcess
                        + " and rownum = 1 " + taskFilter;
            }

            EntityManager em = factory.createEntityManager();
            List<BigDecimal> resultList = em.createNativeQuery(query).getResultList();
            LOGGER.warn("*** PupFacadeLocal.getQueryPageNumber() time " + (System.currentTimeMillis() - tstart));
            return (resultList != null && !resultList.isEmpty()) ? resultList.get(0).longValue() : null;
        } catch (RuntimeException e) {
            LOGGER.error("==========PupFacade.getQueryPageNumber idPupProcess '" + idPupProcess
                    + "', idTask + '" + idTask + "', sql: '" + query + " '");

            LOGGER.error(e.getMessage(), e);
            throw e;
        }
    }

    private String getProcessListQuery(Long id_user, Integer id_department, ProcessSearchParam processSearchParam) {
        long tstart = System.currentTimeMillis();
        if (processSearchParam == null) processSearchParam = new ProcessSearchParam();
        ru.md.persistence.UserMapper userMapper = (ru.md.persistence.UserMapper) SBeanLocator.singleton().getBean("userMapper");
        Long userDepId = userMapper.getUserById(id_user).getIdDepartment();
        ArrayList<Long> bossProccessType = new ArrayList<Long>();//Список БП где пользователь начальник
        ArrayList<Long> auditorOrBossProccessType = new ArrayList<Long>();//списоб БП где пользователь аудитор или начальник
        ArrayList<Long> bigAuditorProccessType = new ArrayList<Long>();//списоб БП где пользователь большой аудитор
        String joinTable = "";
        bossProccessType.addAll(getIdBossProcessTypeForUser(id_user));
        auditorOrBossProccessType.addAll(bossProccessType);
        bigAuditorProccessType.addAll(getIdBigAuditorProccessTypeForUser(id_user));
        for (Long ptId : getIdProcessTypeForUser(id_user)) {
            if (bossProccessType.contains(ptId) || bigAuditorProccessType.contains(ptId))
                continue;
            long tstart2 = System.currentTimeMillis();
            List<String> userRoles = userMapper.userRoles(id_user, ptId);
            LOGGER.warn("*** PupFacadeLocal.getProcessListQuery().userRoles() time " + (System.currentTimeMillis() - tstart2));
            if (userRoles.contains("Аудитор департамента")
                    || userRoles.contains("Секретарь"))
                auditorOrBossProccessType.add(ptId);
        }
        bossProccessType.removeAll(bigAuditorProccessType);//в остальные списки процессов не нужно добавлять, и так получим все заявки как большой аудитор
        auditorOrBossProccessType.removeAll(bigAuditorProccessType);
        if (bigAuditorProccessType.isEmpty()) bigAuditorProccessType.add(0L);
        if (auditorOrBossProccessType.isEmpty()) auditorOrBossProccessType.add(0L);
        if (bossProccessType.isEmpty()) bossProccessType.add(0L);

        String query = "process";
        if (id_department == null || processSearchParam.isClosed())
            query = "process_all";//все заявки
        if (processSearchParam.isProjectTeam() || processSearchParam.isPaused())
            query = "process_projectteam";
        if (processSearchParam.isExpertTeam())
            query = "process_expertteam";
        if (processSearchParam.isFavorite())
            query = "process_favorite";

        query = MessageFormat.format(ResourceLoader.getSQL(query),
                id_user.toString(),
                String.valueOf(id_department),
                userDepId.toString(),
                /*3*/StringUtils.join(bossProccessType, ","), /*4*/StringUtils.join(auditorOrBossProccessType, ","), /*5*/ StringUtils.join(bigAuditorProccessType, ","), joinTable);
        query += " and p.id_status in (" + processSearchParam.getProcessStatus() + ") ";
        //разбор фильтра
        if (processSearchParam.isShowOnlyLastVersionTask())// отображение последних одобренных версий для завершенных сделок
            query += " and m.id_mdtask = ("
                    + "select max(m2.id_mdtask) from mdtask m2 join attributes a on m2.id_pup_process = a.id_process "
                    + "inner join variables v on a.id_var=v.id_var "
                    + "where v.name_var='Статус' and m.mdtask_number=m2.mdtask_number "
                    + "and lower(a.value_var) like 'одобрено%') ";
        if (processSearchParam.getNumber() != null && processSearchParam.getNumber().length() > 0) {
            try {
                new Integer(processSearchParam.getNumber());
                query += " and (m.MDTASK_NUMBER=" + processSearchParam.getNumber() + " or m.CRMCODE like '%" + processSearchParam.getNumber() + "%')";
            } catch (Exception e) {
                query += " and m.CRMCODE like '%" + processSearchParam.getNumber() + "%'";
            }
        }
        if (processSearchParam.getProcessTypeID() != null)
            query += " and p.ID_TYPE_PROCESS=" + processSearchParam.getProcessTypeID().toString();
        if (processSearchParam.getCurrency() != null)
            query += " and lower(m.CURRENCY)='" + processSearchParam.getCurrency().toLowerCase() + "'";
        if (processSearchParam.getSumFrom() != null)
            query += " and m.MDTASK_SUM>=" + processSearchParam.getSumFrom().toString();
        if (processSearchParam.getSumTo() != null)
            query += " and m.MDTASK_SUM<=" + processSearchParam.getSumTo().toString();
        if (processSearchParam.getContractor() != null)
            query += " and exists (select 1 from r_org_mdtask rom "
                    + "where rom.id_mdtask=m.id_mdtask and rom.id_crmorg in (select '" + processSearchParam.getContractor() + "' from dual union all "
                    + "select f.ID_ORG from CRM_FINANCE_ORG f where f.ID_UNITED_CLIENT='" + processSearchParam.getContractor() + "')) ";

        if (processSearchParam.getType() != null)
            query += " and exists (select 1 from  variables  v inner join " +
                    " attributes a on a.id_var=v.id_var where v.name_var='Тип кредитной заявки' and a.id_process=p.id_process and lower(a.value_var) like '%" +
                    processSearchParam.getType().toLowerCase() + "%')";
        if (processSearchParam.getStatus() != null)
            query += " and exists (select 1 from  variables  v inner join " +
                    " attributes a on a.id_var=v.id_var where v.name_var='Статус' and a.id_process=p.id_process and lower(a.value_var) like '%" +
                    processSearchParam.getStatus().toLowerCase() + "%')";
        if (processSearchParam.getPriority() != null)
            query += " and exists (select 1 from  variables  v inner join " +
                    " attributes a on a.id_var=v.id_var where v.name_var='Приоритет' and a.id_process=p.id_process and lower(a.value_var) like '%" +
                    processSearchParam.getPriority().toLowerCase() + "%')";
        if (processSearchParam.getInitDepartment() != null)
            query += " and lower(d.SHORTNAME) like '%" + processSearchParam.getInitDepartment().toLowerCase() + "%'";
        if (processSearchParam.isHideApproved())
            query += " and (m.statusreturn is null or m.statusreturn not in (select s.fb_spo_return_id from crm_status_return s where s.status_type=1))";
        if (processSearchParam.getShowImportOnly().equals("bm"))
            query += " and m.IS_IMPORTED_BM is not null";
        if (processSearchParam.getShowImportOnly().equals("access"))
            query += " and m.is_imported is not null";
        if (processSearchParam.getSearch() != null) {// Поиск производится по колонкам №, Контрагент, Сумма, Валюта
            query += getSpecianSearchFilter(processSearchParam.getSearch());
        }
        //сортировка
        query += " order by id_process DESC";
        LOGGER.warn("*** PupFacadeLocal.getProcessListQuery() time " + (System.currentTimeMillis() - tstart));

        return query;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ArrayList<Long> getProcessList(Long id_user, Integer id_department,
                                          ProcessSearchParam processSearchParam, Long count, Long start) {
        if (id_user == null) return new ArrayList<Long>();

        long tstart = System.currentTimeMillis();
        EntityManager em = factory.createEntityManager();
        ArrayList<Long> idProcessList = new ArrayList<Long>();
        String query = getProcessListQuery(id_user, id_department, processSearchParam);
        query = "select id_process from (select rownum rnum, a.* from (" + query + ") a where rownum <= " + (start + count) + " ) where rnum >= " + (start + 1);
        LOGGER.info("getProcessList query: " + query);
        List<BigDecimal> list = em.createNativeQuery(query).getResultList();
        for (BigDecimal elem : list) {
            idProcessList.add(elem.longValue());
        }
        LOGGER.warn("*** PupFacadeLocal.getProcessList() time " + (System.currentTimeMillis() - tstart));
        return idProcessList;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ArrayList<Long> getProcessList(Long id_user, Integer id_department, ProcessSearchParam processSearchParam) {
        return getProcessList(id_user, id_department, processSearchParam, 1000L, 0L);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public DepartmentJPA getDepartmentByName(String name) {
        EntityManager em = factory.createEntityManager();
        Query query = em.createQuery("SELECT a FROM DepartmentJPA a where a.shortName = :name");
        query.setParameter("name", name);
        List<DepartmentJPA> list = query.getResultList();
        if (list.size() == 0) return null;
        return list.get(0);
    }


    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Long> findPlannedCompletionOperations(int status, java.sql.Date date) {
        if (date == null) return null;
        EntityManager em = factory.createEntityManager();
        String sql = "select t.id_task from tasks t where id_status = ?1 and t.dt_plan_completion = ?2";
        LOGGER.debug("query: " + sql);
        Query q = em.createNativeQuery(sql);
        q.setParameter(1, status);
        q.setParameter(2, date);
        List<BigDecimal> list = q.getResultList();
        List<Long> res = new ArrayList<Long>();
        for (BigDecimal elem : list) res.add(Long.valueOf(elem.toString()));
        return res;
    }

    @Override
    public String getConfigProperty(String propertyName) {
        return Config.getProperty(propertyName);
    }

    /**
     * @return базовый URL для FlexWorkFlow. Учитывает разные адреса для пользователей в ГО и филиалах
     * @throws MalformedURLException
     */
    @Override
    public boolean isUseSA() {
        return Config.getProperty("USE_SA").equals("true");
    }

    /**
     * Returns true if application is running on Windows, false otherwise.
     *
     * @return true if application is running on Windows, false otherwise
     */
    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().indexOf("windows") != -1;
    }

    /**
     * Finds all active users with given roles in the given department
     *
     * @param roleId
     * @param depId
     * @return list of user ids
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Long> findDepartmentUsersInRoles(Long roleId, Long depId) {
        List<Long> res = new ArrayList<Long>();
        if ((roleId == null) || (depId == null)) return res;

        EntityManager em = factory.createEntityManager();
        StringBuilder sb = new StringBuilder();
        sb.append("select distinct urr.id_user from user_in_role urr ")
                .append("inner join users u on urr.id_user = u.id_user and u.id_department = ?1 and u.is_active = 1 and urr.status = 'Y' ")
                .append("where urr.id_role = ?2 ");
        LOGGER.debug("query: " + sb.toString());
        Query q = em.createNativeQuery(sb.toString());
        q.setParameter(1, depId);
        q.setParameter(2, roleId);

        List<BigDecimal> list = q.getResultList();
        for (BigDecimal elem : list) res.add(Long.valueOf(elem.toString()));
        return res;
    }

    /**
     * Finds all parent roles of the executor of the operation (to find all the chiefs in the caller method)
     * Takes all roles of the executor, intersect them with roles of the stage. With fond roles go up the hierarchy of roles.
     *
     * @param stageId
     * @param executorId
     * @param depId
     * @return
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private List<Long> findParentRolesOfExecutor(Long stageId, Long executorId) {
        List<Long> res = new ArrayList<Long>();
        if ((stageId == null) || (executorId == null)) return res;
        EntityManager em = factory.createEntityManager();
        StringBuilder sb = new StringBuilder();
        // -- получаем все роли по иерархии ролей (и проверим, что они --того, активные)
        sb.append("select distinct rn.role_parent ")
                .append("from ROLE_NODES rn ")
                .append("inner join roles r on rn.role_parent = r.id_role and r.active = 1 ")
                .append("connect by prior rn.role_parent = rn.role_child ")
                .append("start with rn.role_child in ")
                .append("   (   ")   // роли исполнителя пересекаем с ролями на операции
                .append("     select id_role from stages_in_role sr     ")  // стадии
                .append("     where sr.id_stage = ?1  ")   // stage id (from operation)
                .append("     and sr.id_role in ")
                .append("       ( ")
                .append("           select id_role from user_in_role ur ")
                .append("           where ur.id_user = ?2   ")    //executor id (from operation)
                .append("           and ur.status = 'Y' ")
                .append("       ) ")
                .append("   ) ");

        LOGGER.debug("query: " + sb.toString());
        Query q = em.createNativeQuery(sb.toString());
        q.setParameter(1, stageId);
        q.setParameter(2, executorId);
        List<BigDecimal> list = q.getResultList();
        for (BigDecimal elem : list) res.add(Long.valueOf(elem.toString()));
        return res;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String acceptAttachment(Long idUser, String unid, String sign) throws MappingException {
        if (sign == null || sign.trim().isEmpty())
            return null;
        String newUNID = null;
        EntityManager em = factory.createEntityManager();
        AttachJPA attach = em.find(AttachJPA.class, unid);
        attach.setACCEPT_SIGNATURE(sign.getBytes());
        attach.setWhoAccepted(em.find(UserJPA.class, idUser));
        attach.setISACCEPTED(1L);
        attach.setDATE_OF_ACCEPT(new Date());
        em.merge(attach);

        //Формирование документа по шаблону с записью об ЭЦП при утверждении
        if (attach.getFILENAME() != null && (attach.getFILENAME().toLowerCase().endsWith(".doc") || attach.getFILENAME().toLowerCase().endsWith(".docx"))) {
            //application/msword
            AttachJPA a = new AttachJPA();
            a.setUnid(java.util.UUID.randomUUID().toString());
            a.setID_OWNER(attach.getID_OWNER());
            a.setOWNER_TYPE(attach.getOWNER_TYPE());
            a.setGroup(attach.getGroup());
            a.setDocumentType(attach.getDocumentType());
            a.setFILETYPE(attach.getFILETYPE());
            a.setDATE_OF_EXPIRATION(attach.getDATE_OF_EXPIRATION());
            //если проводится утверждение документа, для которого был установлен признак «Передается на Кредитный комитет», этот признак
            //должен быть установлен для сформированного по шаблону документа, а утвержденного руководителем документа признак должен быть снят.
            a.setFORCC(attach.getFORCC());
            //attach.setFORCC("n");

            a.setDATE_OF_ADDITION(attach.getDATE_OF_ADDITION());
            a.setWhoAdd(attach.getWhoAdd());
            a.setISACCEPTED(1l);
            a.setCONTENTTYPE(attach.getCONTENTTYPE());
            a.setWhoAccepted(attach.getWhoAccepted());
            a.setDATE_OF_ACCEPT(attach.getDATE_OF_ACCEPT());
            //Наименование сформированного по шаблону документа должно формироваться так: «<Название утвержденного документа> (подписан ЭЦП).doc».
            String filename = generatedFileName(attach.getFILENAME(), null); // фиктивное расширение. Подменится далее
            a.setFILENAME(filename);
            //title
            if (attach.getTitle() != null && !attach.getTitle().isEmpty()) {
                String title = generatedFileName(attach.getTitle(), null);
                a.setTitle(title);
            }
            em.persist(a);
            //сам документ
            //generateAcceptReport(attach.getUnid(), a.getUnid(),idUser);
            newUNID = a.getUnid();
        }
        return newUNID;
    }

    @Override
    public void signAttachment(Long idUser, String unid, String sign) throws MappingException {
        if(sign==null|| sign.trim().isEmpty())
            return;
        EntityManager em = factory.createEntityManager();
        AttachJPA attach = em.find(AttachJPA.class, unid);
        attach.setWhoSign(em.find(UserJPA.class, idUser));
        attach.setDate_of_sign(new Date());
        attach.setSIGNATURE(sign.getBytes());
        em.merge(attach);
    }

    private String generatedFileName(String attachName, String newExtension) {
		LOGGER.info("generatedFileName: " + attachName + ", extension: " + newExtension);
		int dot = attachName.lastIndexOf('.');
		String extension = "";
		String fileNameWithoutExtension = "";
		if (dot != -1) {
			// ага. Точка найдена. Какое-то расширение должно быть, хотя бы пустое.
			if (dot < attachName.length() - 1) {
				// точка - не последнний символ в имени (типа Документ.)
				extension = attachName.substring(dot+1);
			} else extension = "";

            if (dot > 0) {
                // точка - не первый символ в имени файла
                fileNameWithoutExtension = attachName.substring(0, dot);
            } else {
                fileNameWithoutExtension = "";
            }
        } else {
            // точка не найдена
            fileNameWithoutExtension = attachName;
            extension = "";
        }

        // реальное расширение файла (определенное по содержимому).
        if (newExtension != null) {
            extension = newExtension;
        }
        LOGGER.info("generatedFileName result: " + fileNameWithoutExtension + " (с отметкой об ЭЦП)." + extension);
        return fileNameWithoutExtension + " (с отметкой об ЭЦП)." + extension;
    }


    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void generateAcceptReport(String originalDocUNID, String newDocUNID, Long idUser) throws MappingException {
        if (newDocUNID == null || originalDocUNID == null)
            return;
        EntityManager em = factory.createEntityManager();
        AttachJPA originalDoc = em.find(AttachJPA.class, originalDocUNID);
        String taskNumber = "";
        if (originalDoc.getOWNER_TYPE().equals(0L)) {
            TaskJPA task = taskFacade.getTaskByPupID(Long.valueOf(originalDoc.getID_OWNER()));
            taskNumber = " " + task.getType() + " " + task.getNumberDisplay();
        }
        UserJPA user = em.find(UserJPA.class, idUser);
        AttachmentActionProcessor processor = (AttachmentActionProcessor) ActionProcessorFactory.getActionProcessor("Attachment");
        byte[] filedata = processor.findAttachmentDataByPK(new AttachmentFile(originalDocUNID)).getFiledata();
        Map<String, String> extraParameters = new HashMap<String, String>();
        extraParameters.put(ReportTemplateParams.SIGNATURE_FIO.getValue(), user.getFullName() + taskNumber);
        extraParameters.put(ReportTemplateParams.SIGNATURE_DATE.getValue(), Formatter.format(originalDoc.getDATE_OF_ACCEPT()));
        try {
            LOGGER.info("attach.getOWNER_TYPE()=" + originalDoc.getOWNER_TYPE());
            LOGGER.info("attach.getID_OWNER()=" + originalDoc.getID_OWNER());
            byte[] reportWithSignature = ReportHelper.generateAcceptReport(taskNumber, user.getFullName(),
                    originalDoc.getDATE_OF_ACCEPT(), filedata);
            String filename = generatedFileName(originalDoc.getFILENAME(), null);
            LOGGER.info("reportWithSignature size ");
            LOGGER.info(reportWithSignature == null ? "0" : String.valueOf(reportWithSignature.length));
            LOGGER.info("reportWithSignature UNID: " + newDocUNID);
            LOGGER.info("reportWithSignature filename: " + filename);

            AttachDataJPA file = em.find(AttachDataJPA.class, newDocUNID);
            file.setFiledata(reportWithSignature);
            em.merge(file);
        } catch (Exception e) {
            LOGGER.error("ERROR " + e.getMessage(), e);
            LOGGER.info("reportWithSignature filename: " + generatedFileName(originalDoc.getFILENAME(), null));
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteAttachment(String unid, Long idUser, String reason) {
        EntityManager em = factory.createEntityManager();
        AttachJPA attach = em.find(AttachJPA.class, unid);
        attach.setFORCC("N");
        attach.setReason(reason);
        if (idUser != null)
            attach.setWhoDel(em.find(UserJPA.class, idUser));
        attach.setDATE_OF_DEL(new Date());
        em.merge(attach);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public HashMap<Long, HashSet<Long>> getRolesPermissions(Long idRole) {
        HashMap<Long, HashSet<Long>> res = new LinkedHashMap<Long, HashSet<Long>>();
        EntityManager em = factory.createEntityManager();
        Query q = em.createNativeQuery("SELECT p.id_var,p.id_permission FROM ROLES_PERMISSIONS p WHERE id_role=?");
        q.setParameter(1, idRole);
        List<Object[]> list = q.getResultList();
        for (Object[] obj : list) {
            Long idVar = ((BigDecimal) obj[0]).longValue();
            Long idPerm = ((BigDecimal) obj[1]).longValue();
            if (!res.containsKey(idVar)) res.put(idVar, new HashSet<Long>());
            res.get(idVar).add(idPerm);
        }
        return res;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public HashMap<Long, HashSet<Long>> getStagesPermissions(Long idStage) {
        EntityManager em = factory.createEntityManager();
        Query q = em.createNativeQuery("SELECT p.id_var,p.id_permission FROM STAGES_PERMISSIONS p WHERE p.id_stage=?");
        q.setParameter(1, idStage);
        List<Object[]> list = q.getResultList();
        HashMap<Long, HashSet<Long>> res = new LinkedHashMap<Long, HashSet<Long>>();
        for (Object[] obj : list) {
            Long idVar = ((BigDecimal) obj[0]).longValue();
            Long idPerm = ((BigDecimal) obj[1]).longValue();
            if (!res.containsKey(idVar)) res.put(idVar, new HashSet<Long>());
            res.get(idVar).add(idPerm);
        }
        return res;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<AttachJPA> findAttachemntByOwnerAndType(String idOwner, Long ownerType) {
        Query query = factory.createEntityManager().createQuery("SELECT c FROM AttachJPA c "
                + "WHERE c.ID_OWNER LIKE ?1 and c.OWNER_TYPE=?2 and c.whoDel is null");
        query.setParameter(1, idOwner);
        query.setParameter(2, ownerType);
        List<AttachJPA> res = query.getResultList();
        return res;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<AttachJPA> findDelAttachemntByOwnerAndType(String idOwner, Long ownerType) {
        Query query = factory.createEntityManager().createQuery("SELECT c FROM AttachJPA c "
                + "WHERE c.ID_OWNER LIKE ?1 and c.OWNER_TYPE=?2 and c.whoDel is not null");
        query.setParameter(1, idOwner);
        query.setParameter(2, ownerType);
        List<AttachJPA> res = query.getResultList();
        return res;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Long findAttachemntCountByOwnerAndType(String idOwner, Long ownerType) {
        Query q = factory.createEntityManager().createNativeQuery(
                "SELECT count(*) FROM appfiles a WHERE a.id_owner=? AND a.owner_type=? AND a.WHO_DEL IS NULL");
        q.setParameter(1, idOwner);
        q.setParameter(2, ownerType);
        return ((BigDecimal) q.getSingleResult()).longValue();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<DocumentGroupJPA> findDocumentGroupByOwnerTYpe(Long ownerType) {
        Query query = factory.createEntityManager().createQuery("SELECT c FROM DocumentGroupJPA c "
                + "WHERE c.GROUP_TYPE=? and c.systems=1 order by c.NAME_DOCUMENT_GROUP");
        query.setParameter(1, ownerType);
        return query.getResultList();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void attachment4cc(String unid, boolean FORCC,Long idMdtask) {
        EntityManager em = factory.createEntityManager();
        if (FORCC){
            Query query = em.createNativeQuery("delete from spo_4cc_appfiles WHERE fileid=? and id_mdtask=?");
            query.setParameter(2, idMdtask);
            query.setParameter(1, unid);
            query.executeUpdate();
            query = em.createNativeQuery("insert into spo_4cc_appfiles(fileid,id_mdtask) values (?,?)");
            query.setParameter(2, idMdtask);
            query.setParameter(1, unid);
            query.executeUpdate();
        } else {
            Query query = em.createNativeQuery("delete from spo_4cc_appfiles WHERE fileid=? and id_mdtask=?");
            query.setParameter(2, idMdtask);
            query.setParameter(1, unid);
            query.executeUpdate();
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public DocumentTypeJPA getDocumentType(Long id) {
        return factory.createEntityManager().find(DocumentTypeJPA.class, id);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public DocumentGroupJPA getDocumentGroup(Long id) {
        return factory.createEntityManager().find(DocumentGroupJPA.class, id);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public AttachJPA getAttachemnt(String unid) {
        return factory.createEntityManager().find(AttachJPA.class, unid);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<String> getL_AttributeList(Long idTypeProcess) {
        EntityManager em = factory.createEntityManager();
        Query query = em.createNativeQuery("SELECT v.name_var FROM variables v WHERE v.name_var LIKE 'L_%' " +
                "AND v.id_type_process=? ORDER BY v.name_var");
        query.setParameter(1, idTypeProcess);
        List<String> list = query.getResultList();
        return list;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<String> getAttributeList(Long idProcessType) {
        EntityManager em = factory.createEntityManager();
        Query query = em.createNativeQuery("SELECT v.name_var FROM variables v WHERE " +
                " v.id_type_process=? ORDER BY v.name_var");
        query.setParameter(1, idProcessType);
        List<String> list = query.getResultList();
        return list;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String getExpertHtmlReport(Long mdtaskId) throws Exception {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("expertus", getExpertReport(mdtaskId));
        Configuration cfg = new Configuration();//Freemarker configuration object
        cfg.setClassForTemplateLoading(this.getClass(), "/frame/");
        StringWriter out = new StringWriter();
        cfg.getTemplate("expertus.ftl", "utf-8").process(data, out);
        LOGGER.info(out.toString());
        return out.toString();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ArrayList<Expertus> getExpertReport(Long mdtaskId) throws FactoryException {
        LOGGER.info("getExpertHtmlReport");
        ArrayList<Expertus> expertus = new ArrayList<Expertus>();
        TaskJPA task = taskFacade.getTask(mdtaskId);
        UserJPA user = getCurrentUser();
        List<String> currentUserRoles = ((UserMapper) SBeanLocator.singleton().getBean("userMapper")).userRoles(user.getIdUser(), task.getIdTypeProcess());
        for (String attrName : getAttributeList(task.getProcess().getProcessType().getIdTypeProcess())) {
            if (!attrName.endsWith("_data_start")) {
                continue;
            }
            if (!attrName.startsWith("Требуется экспертиза")
                    && !attrName.startsWith("Требуется предварительная экспертиза")
                    && !attrName.startsWith("Требуется контроль технической исполнимости")) {
                continue;
            }
            String dataStartList = getPUPAttributeValue(task.getProcess().getId(), attrName);
            if (dataStartList.length() == 0) {
                continue;
            }

            String expName = attrName.substring("Требуется ".length());
            expName = expName.substring(0, expName.length() - "_data_start".length());

            String stageName = getPUPAttributeValue(task.getProcess().getId(), "Требуется " + expName + "_stage");
            for (int i = 0; i < dataStartList.split("\\|").length; i++) {
                String data_start = dataStartList.split("\\|")[i];
                Expertus exp = new Expertus();
                exp.setName(expName);
                exp.setDataStart(data_start);
                if (i == 0)
                    exp.setRowspan(dataStartList.split("\\|").length);
                for (ExpertTeamJPA team : task.getExpertTeam())
                    if (expName.equals(team.getExpname()))
                        if (!exp.getGroup().contains(team.getUser()))
                            exp.getGroup().add(team.getUser());
                String[] dataEndArray = getPUPAttributeValue(task.getProcess().getId(), "Требуется " + expName + "_data_end").split("\\|");
                if (dataEndArray.length == 1 && dataEndArray[0].isEmpty())
                    dataEndArray = new String[0];
                if (dataEndArray.length > i) {
                    exp.setDataEnd(dataEndArray[i]);
                } else {
                    exp.setDataEnd("");
                }
                String[] userLoginArray = getPUPAttributeValue(task.getProcess().getId(), "Требуется " + expName + "_user").split("\\|");
                try {
                    if (userLoginArray.length > i) {
                        String login = userLoginArray[i];
                        if (login.equals("_cptbreak")) {
                            UserJPA u = new UserJPA(null);
                            u.setSurname("экспертиза прервана");
                            u.setLogin(login);
                            exp.setUser(u);
                        } else {
                            exp.setUser(getUserByLogin(login));
                        }
                    } else {
                        exp.setUser(getUserByLogin(getPUPAttributeValue(task.getProcess().getId(), expName + "_user_temp")));
                    }
                } catch (Exception e) {
                    try {
                        exp.setUser(getUserByLogin(getPUPAttributeValue(task.getProcess().getId(), expName + "_user_temp")));
                    } catch (Exception e1) {
                    }
                    LOGGER.error(e.getMessage());
                }
                if (i == 0)
                    try {
                        EntityManager em = factory.createEntityManager();
                        Query query = em.createQuery("SELECT u FROM StageJPA u where u.id_type_process = :idTypeProcess and u.description= :name");
                        query.setParameter("idTypeProcess", task.getProcess().getProcessType().getIdTypeProcess());
                        query.setParameter("name", stageName);
                        List<StageJPA> stages = query.getResultList();
                        for (StageJPA stage : stages)
                            for (RoleJPA role : stage.getRoles())
                                if (role.getChildRoles().size() > 0 && currentUserRoles.contains(role.getNameRole()))
                                    exp.setCanEdit(true);
                        if (dataEndArray.length == dataStartList.split("\\|").length)
                            exp.setCanEdit(false);
                    } catch (Exception e) {
                        LOGGER.warn(e.getMessage(), e);
                    }
                expertus.add(exp);
            }
        }
        return expertus;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public UserJPA getCurrentUser() {
        String userLogin = cnx.getCallerPrincipal().getName();
        if (userLogin.equals("UNAUTHENTICATED")) userLogin = "adminwf";//отладка на сервере без аутентификации
        try {
            UserJPA user = getUserByLogin(userLogin);
            return user;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public String getAdditionVar(Long idTypeProcess, String nameVar) {
        Query query = factory.createEntityManager().
                createNativeQuery("SELECT v.ADDITION_VAR FROM VARIABLES v WHERE v.ID_TYPE_PROCESS=? AND v.NAME_VAR=?");
        query.setParameter(1, idTypeProcess);
        query.setParameter(2, nameVar);
        List<String> list = query.getResultList();
        if (list != null && list.size() > 0)
            return list.get(0);
        return "";
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void convertPMandDoc(Long idOldProcess, Long idNewProcess) {
        EntityManager em = factory.createEntityManager();
        //документы
        em.createNativeQuery("update APPFILES a set a.ID_OWNER='" + idNewProcess.toString() +
                "' where id_owner='" + idOldProcess.toString() + "' and a.OWNER_TYPE=0").executeUpdate();
        //роли
        em.createNativeQuery("update process_events pe set ID_PROCESS=" + idNewProcess +
                " where pe.ID_PROCESS_TYPE_EVENT=6 and pe.ID_PROCESS=" + idOldProcess).executeUpdate();
        em.createNativeQuery("update assign a set a.id_role=nvl((select r.ID_ROLE from roles r where r.ID_TYPE_PROCESS in " +
                " (select ID_TYPE_PROCESS from processes where processes.ID_PROCESS=" + idNewProcess + ") " +
                " and r.NAME_ROLE=(select cr.NAME_ROLE from roles cr where cr.ID_ROLE=a.id_role)),a.id_role) " +
                " where a.ID_PROCESS_EVENT in( " +
                " select pe.ID_PROCESS_EVENT from process_events pe " +
                "where pe.ID_PROCESS_TYPE_EVENT=6 and pe.ID_PROCESS=" + idNewProcess + ")").executeUpdate();

    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void updateCdVersion() {
        EntityManager em = factory.createEntityManager();
        em.createNativeQuery("UPDATE CD_SYSTEM_MODULE SET CURRENT_VERSION  = '" +
                ApplProperties.version + "' WHERE KEY = 'CPPS'").executeUpdate();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Long getNextSublimitNumber(Long idparent) {
        Query query = factory.createEntityManager().
                createNativeQuery("SELECT t.mdtask_number FROM mdtask t WHERE PARENTID=? AND t.id_pup_process IS NULL");
        query.setParameter(1, idparent);
        List<BigDecimal> list = query.getResultList();
        Long number = new Long(1);//initial number
        for (BigDecimal snum : list) {
            if (snum.longValue() >= number)
                number = 1 + snum.longValue();
        }
        return number;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Long getLastSublimitId(Long idparent) {
        Query query = factory.createEntityManager().
                createNativeQuery("SELECT t.id_mdtask FROM mdtask t WHERE PARENTID=?");
        query.setParameter(1, idparent);
        List<BigDecimal> list = query.getResultList();
        Long number = 0L;
        for (BigDecimal snum : list) {
            if (snum.longValue() >= number)
                number = snum.longValue();
        }
        return number;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public boolean isAssigned(Long idUser, Long idRole, Long idProcess) {
        EntityManager em = factory.createEntityManager();
        Query query = em.createNativeQuery("SELECT count(*) FROM assign a " +
                "INNER JOIN process_events pe ON pe.id_process_event=a.id_process_event " +
                "WHERE a.id_role=? AND a.id_user_to=? AND pe.id_process=?");
        query.setParameter(1, idRole);
        query.setParameter(2, idUser);
        query.setParameter(3, idProcess);
        return ((BigDecimal) query.getSingleResult()).longValue() > 0;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public boolean isAssigned(Long idRole, Long idProcess) {
        EntityManager em = factory.createEntityManager();
        Query query = em.createNativeQuery("SELECT count(*) FROM assign a " +
                "INNER JOIN process_events pe ON pe.id_process_event=a.id_process_event " +
                "WHERE a.id_role=? AND pe.id_process=?");
        query.setParameter(1, idRole);
        query.setParameter(2, idProcess);
        return ((BigDecimal) query.getSingleResult()).longValue() > 0;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void startMemorandum(Long idUser, Long idProcess) {
        EntityManager em = factory.createEntityManager();
        UserJPA user = getUser(idUser);
        ProcessJPA processJPA = em.find(ProcessJPA.class, idProcess);
        Query query = em.createNativeQuery("SELECT tasks_seq.NEXTVAL FROM dual");
        Long id_task = ((BigDecimal) query.getResultList().get(0)).longValue();
        query = em.createNativeQuery(
                "insert into tasks (id_task,id_type_process,id_process,id_stage_to,id_department,id_status)" +
                        " select " + id_task + ",s.id_type_process," + idProcess + ",s.id_stage," +
                        user.getDepartment().getIdDepartment() + ",1  from stages s " +
                        "where s.description_stage='Определение необходимости формирования Кредитного меморандума' " +
                        "and s.id_type_process=" + processJPA.getProcessType().getIdTypeProcess());
        query.executeUpdate();
        query = em.createNativeQuery("INSERT INTO task_events(id_task_event,id_task,id_task_type_event,id_user,date_event)" +
                " VALUES(task_events_seq.nextval,?,1,?,?)");
        query.setParameter(1, id_task);
        query.setParameter(2, idUser);
        query.setParameter(3, new java.util.Date());
        query.executeUpdate();
        //отправим уведомления
        try {
            TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
            TaskJPA task = taskFacadeLocal.getTaskByPupID(idProcess);
            query = em.createNativeQuery("SELECT DISTINCT u.id_user FROM  users u " +
                    "INNER JOIN user_in_role ur ON ur.id_user=u.id_user  " +
                    "INNER JOIN stages_in_role sr ON sr.id_role=ur.id_role  " +
                    "INNER JOIN stages s ON s.id_stage=sr.id_stage " +
                    "WHERE lower(ur.status) = 'y' AND u.mail_user IS NOT NULL AND " +
                    "s.description_stage='Определение необходимости формирования Кредитного меморандума' " +
                    "AND ? IN (SELECT dp.id_department_child FROM departments_par dp CONNECT BY PRIOR id_department_child=id_department_par " +
                    "START WITH dp.id_department_par =u.id_department UNION SELECT u.id_department FROM dual)");
            query.setParameter(1, user.getDepartment().getIdDepartment());
            List<BigDecimal> list = query.getResultList();
            for (BigDecimal userid : list) {
                UserJPA to = em.find(UserJPA.class, userid.longValue());
                String subject = MessageFormat.format(SPOMessage.waitSubjectMessageFormat,notifyFacade.getName(task.getId()));
                String bodyMessage = MessageFormat.format(SPOMessage.waitBodyMessageFormat,getBaseURL(userid.longValue()),
                        task.getMdtask_number().toString(),task.getNumberAndVersion(),
                        task.getOrganisation(),"Определение необходимости формирования Кредитного меморандума",
                "Крупный бизнес ГО", task.isProduct()?"сделка":"лимит");
                notifyFacade.send(user.getIdUser(),
                        to.getIdUser(), subject, bodyMessage);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<DepartmentJPA> findDepartmentsForUser(Long idProcessType,
                                                      String roles) {
        String sql = "select d.id_department from departments d " +
                "where exists (select * from users u inner join user_in_role ur on u.id_user=ur.id_user " +
                "inner join roles r on ur.id_role=r.id_role " +
                "where u.id_department=d.id_department and ur.status='Y'";
        if (idProcessType != null) sql += " and r.id_type_process=" + idProcessType.toString();
        if (roles != null && roles != "") sql += " and r.name_role in (" + roles + ")";
        sql += ") order by d.shortname";
        EntityManager em = factory.createEntityManager();
        Query query = em.createNativeQuery(sql);
        List<BigDecimal> list = query.getResultList();
        List<DepartmentJPA> result = new ArrayList<DepartmentJPA>();
        for (BigDecimal b : list) {
            result.add(em.find(DepartmentJPA.class, b.longValue()));
        }
        return result;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void closeMemorandum(Long idProcess, Long idUser) {
        final String memorandumOperations = "('Определение необходимости формирования Кредитного меморандума','Формирование Кредитного меморандума'," +
                "'Утверждение Кредитного меморандума','Формирование Кредитного меморандума завершено','Подготовка документов для Кредитного меморандума')";
        EntityManager em = factory.createEntityManager();
        Date now = new java.util.Date();
        //история прохождения заявки - операции
        Query query = em.createNativeQuery("select id_task from tasks t inner join stages s on s.id_stage=t.id_stage_to " +
                "where t.id_process=" + idProcess + " and t.id_status in (1,2) and s.description_stage in " + memorandumOperations);
        List<BigDecimal> list = query.getResultList();//незавершенные заявки
        for (BigDecimal idTask : list) {
            query = em.createNativeQuery("INSERT INTO task_events(id_task, id_task_type_event, date_event, id_user )"
                    + " VALUES( ?1, 5, ?2, ?3 )");
            query.setParameter(1, idTask);
            query.setParameter(2, now);
            query.setParameter(3, idUser);
            query.executeUpdate();
            em.createNativeQuery("update tasks t set t.id_status = 5 where t.id_task=" + idTask).executeUpdate();
        }

    }

    @Override
    public void closeProcess(Long idProcess, Long idUser) {
        EntityManager em = factory.createEntityManager();
        Date now = new java.util.Date();
        //история прохождения заявки - операции
        Query query = em.createNativeQuery("select id_task from tasks t where t.id_process=" + idProcess + " and t.id_status in (1,2)");
        List<BigDecimal> list = query.getResultList();//незавершенные заявки
        for (BigDecimal idTask : list) {
            query = em.createNativeQuery("INSERT INTO task_events(id_task, id_task_type_event, date_event, id_user )"
                    + " VALUES( ?1, 5, ?2, ?3 )");
            query.setParameter(1, idTask);
            query.setParameter(2, now);
            query.setParameter(3, idUser);
            query.executeUpdate();
        }

        em.createNativeQuery("update tasks t set t.id_status = 5 where t.id_process=" + idProcess + " and t.id_status in (1,2)").executeUpdate();

        //процесс
        em.createNativeQuery("update processes p set p.id_status=4 where p.id_process=" + idProcess).executeUpdate();
        query = em.createNativeQuery("INSERT INTO process_events(id_process, id_process_type_event, date_event, id_user, id_process_event )"
                + " VALUES( ?1, 4, ?2, ?3 , process_events_seq.nextval)");
        query.setParameter(1, idProcess);
        query.setParameter(2, now);
        query.setParameter(3, idUser);
        query.executeUpdate();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteAssign(String nameRole, Long idProcess, Long who) {
        EntityManager em = factory.createEntityManager();
        ProcessJPA process = em.find(ProcessJPA.class, idProcess);
        Query query = em.createQuery("SELECT u FROM RoleJPA u where u.nameRole = :name and u.process.idTypeProcess = :idTypeProcess");
        query.setParameter("name", nameRole);
        query.setParameter("idTypeProcess", process.getProcessType().getIdTypeProcess());
        List<RoleJPA> list = query.getResultList();
        for (RoleJPA role : list) {
            if (isAssigned(role.getIdRole(), idProcess))
                deleteOldAssign(role.getIdRole(), idProcess, new Date(), who);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public RoleJPA getRole(String nameRole, Long idProcessType) {
        EntityManager em = factory.createEntityManager();
        Query query = em.createQuery("SELECT u FROM RoleJPA u where u.nameRole = :name and u.process.idTypeProcess = :idTypeProcess");
        query.setParameter("name", nameRole);
        query.setParameter("idTypeProcess", idProcessType);
        List<RoleJPA> list = query.getResultList();
        if (list.size() > 0) return list.get(0);
        return null;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<StageJPA> getStages(Long idProcessType) {
        EntityManager em = factory.createEntityManager();
        Query query = em.createQuery("SELECT u FROM StageJPA u where u.id_type_process = :idTypeProcess and u.active=1 order by u.description");
        query.setParameter("idTypeProcess", idProcessType);
        return query.getResultList();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Long createStandardPeriodVersion(Long idProcessType) {
        EntityManager em = factory.createEntityManager();
        ProcessTypeJPA pt = em.find(ProcessTypeJPA.class, idProcessType);
        StandardPeriodVersionJPA version = new StandardPeriodVersionJPA();
        version.setDate(new java.util.Date());
        version.setProcessType(pt);
        pt.getStandardPeriodVersions().add(version);
        em.persist(version);
        em.merge(pt);

        StandardPeriodGroupJPA group = new StandardPeriodGroupJPA();
        group.setName("весь процесс");
        group.setDecisionStages(new ArrayList());
        for (StageJPA s : getFirstStages(idProcessType))
            group.getDecisionStages().add(s);
        group.setVersion(version);
        group.setStages(new ArrayList<StageJPA>());
        for (StageJPA stage : getStages(idProcessType)) {
            if (stage.isActive()) group.getStages().add(stage);
        }
        version.setStandardPeriodGroups(new ArrayList<StandardPeriodGroupJPA>());
        version.getStandardPeriodGroups().add(group);
        em.persist(group);
        em.merge(version);
        em.flush();
        return version.getId();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ProcessTypeJPA getProcessTypeById(Long id) {
        return factory.createEntityManager().find(ProcessTypeJPA.class, id);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ProcessJPA getProcessById(Long id) {
        return factory.createEntityManager().find(ProcessJPA.class, id);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void pauseProcess(Long id_process, Long idUser, String cmnt, Date pauseDate) {
        EntityManager em = factory.createEntityManager();
        Query query = em.createNativeQuery("UPDATE processes SET ID_STATUS=2 WHERE ID_PROCESS=?");
        query.setParameter(1, id_process);
        query.executeUpdate();

        query = em.createNativeQuery("UPDATE tasks SET ID_STATUS=8 WHERE ID_PROCESS=? AND ID_STATUS<3");
        query.setParameter(1, id_process);
        query.executeUpdate();

        Long peid = getNextProcessEventID();

        query = em.createNativeQuery("INSERT INTO process_events(id_process_event,id_process,id_process_type_event,date_event,id_user)" +
                " VALUES(?,?,2,?,?)");
        query.setParameter(1, peid);
        query.setParameter(2, id_process);
        query.setParameter(3, new Date());
        query.setParameter(4, idUser);
        query.executeUpdate();

        //сохранить дату и комментарий
        query = em.createNativeQuery("INSERT INTO pauseParam(id,id_process_event,cmnt,dateresume)" +
                " VALUES(PAUSEPARAM_SEQ.nextval,?,?,?)");
        query.setParameter(1, peid);
        query.setParameter(2, cmnt);
        query.setParameter(3, pauseDate);
        query.executeUpdate();
        LOGGER.info(peid.getClass().getName());
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void resumeProcess(Long id_process, Long idUser, String cmnt) {
        EntityManager em = factory.createEntityManager();
        Query query = em.createNativeQuery("UPDATE processes SET ID_STATUS=1 WHERE ID_PROCESS=?");
        query.setParameter(1, id_process);
        query.executeUpdate();

        query = em.createNativeQuery("UPDATE tasks SET ID_STATUS=2 WHERE ID_PROCESS=? AND ID_STATUS=8 AND ID_USER IS NOT NULL");
        query.setParameter(1, id_process);
        query.executeUpdate();
        query = em.createNativeQuery("UPDATE tasks SET ID_STATUS=1 WHERE ID_PROCESS=? AND ID_STATUS=8 AND ID_USER IS NULL");
        query.setParameter(1, id_process);
        query.executeUpdate();

        //history
        Long peid = getNextProcessEventID();

        query = em.createNativeQuery("INSERT INTO process_events(id_process_event,id_process,id_process_type_event,date_event,id_user)" +
                " VALUES(?,?,9,?,?)");
        query.setParameter(1, peid);
        query.setParameter(2, id_process);
        query.setParameter(3, new Date());
        query.setParameter(4, idUser);
        query.executeUpdate();

        //сохранить комментарий
        query = em.createNativeQuery("INSERT INTO pauseParam(id,id_process_event,cmnt)" +
                " VALUES(PAUSEPARAM_SEQ.nextval,?,?)");
        query.setParameter(1, peid);
        query.setParameter(2, cmnt);
        query.executeUpdate();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Long getNextMdTaskNumber() {
        return getNextSequenceValue("mdtask_number_seq");
    }

    private Long getNextProcessEventID() {
        return getNextSequenceValue("process_events_seq");
    }

    private Long getNextSequenceValue(String seq_name) {
        EntityManager em = factory.createEntityManager();
        Query query = em.createNativeQuery("select " + seq_name + ".nextval from dual");
        return Long.valueOf(((BigDecimal) query.getResultList().get(0)).toString());
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<ProcessTypeJPA> findProcessTypeList() {
        return factory.createEntityManager().
                createQuery("SELECT u FROM ProcessTypeJPA u order by u.descriptionProcess").getResultList();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String getBaseURL(Long userid) throws MalformedURLException {
        try {
            return notifyFacade.getBaseURL(userid);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return "";
        }
    }


    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void completeWorks(ArrayList stNext, Long idTaskCompleted) {
        Date now = new Date();
        EntityManager em = factory.createEntityManager();
        TaskInfoJPA taskInfoJPA = em.find(TaskInfoJPA.class, idTaskCompleted);
        Query q = em.createNativeQuery("UPDATE TASKS SET id_status = 3 WHERE ID_TASK = :id");
        q.setParameter("id", idTaskCompleted);
        q.executeUpdate();

        q = em.createNativeQuery("SELECT count(t.id_task) FROM task_events t WHERE t.id_task = :id_task " +
                "AND t.id_task_type_event=3");
        q.setParameter("id_task", idTaskCompleted);
        BigDecimal exist = (BigDecimal) q.getSingleResult();
        if (exist.longValue() == 0) {
            LOGGER.warn("!!! stage " + idTaskCompleted + " already complete");
            q = em.createNativeQuery("INSERT INTO task_events (id_task, id_task_type_event, date_event, id_user,id_task_event) " +
                    "VALUES(:id_task, 3, :now, :id_user,TASK_EVENTS_SEQ.nextval )");
            q.setParameter("id_task", idTaskCompleted);
            q.setParameter("now", now);
            q.setParameter("id_user", taskInfoJPA.getExecutor().getIdUser());
            q.executeUpdate();
        }

        for (Object p : stNext) {
            Object[] param = (Object[]) p;//давно мечтаю отрефакторить весь ижевский код, когда будет время
            Object nextStageObj = param[2];
            Long nextStage = null;
            if (nextStageObj != null && nextStageObj instanceof Long)
                nextStage = (Long) nextStageObj;
            if (nextStage == null)
                continue;
            //смотрим нет ли уже по этой заявке операции на этом этапе
            q = em.createNativeQuery("SELECT count(*) FROM tasks t " +
                    "WHERE t.id_process = :id_process AND t.id_stage_to = :id_stage AND t.id_status IN (1, 2 )");
            q.setParameter("id_stage", nextStage);
            q.setParameter("id_process", taskInfoJPA.getProcess().getId());
            exist = (BigDecimal) q.getSingleResult();
            if (exist.longValue() > 0)
                continue;

            Long idDep = taskInfoJPA.getIdDepartament();
            Object idDepObj = param[5];
            if (idDepObj != null && idDepObj instanceof Long)
                idDep = (Long) idDepObj;
            Long newtaskid = getNextSequenceValue("tasks_seq");
            q = em.createNativeQuery("INSERT INTO tasks (id_task, id_type_process, id_process, id_stage_to, id_status, id_user, type_complation, id_department) " +
                    "VALUES (:idTask, :type_process, :id_process, :id_stage_next, 1, NULL, NULL, :id_department )");
            q.setParameter("id_process", taskInfoJPA.getProcess().getId());
            q.setParameter("type_process", taskInfoJPA.getProcess().getProcessType().getIdTypeProcess());
            q.setParameter("id_department", idDep);
            q.setParameter("idTask", newtaskid);
            q.setParameter("id_stage_next", nextStage);
            q.executeUpdate();

            q = em.createNativeQuery("INSERT INTO task_events (id_task, id_task_type_event, date_event, id_user) " +
                    "VALUES( :idTask, 1, :now, :id_user )");
            q.setParameter("idTask", newtaskid);
            q.setParameter("now", now);
            q.setParameter("id_user", taskInfoJPA.getExecutor().getIdUser());
            q.executeUpdate();
        }
        //если по заявке больше нет активных операций, то завершаем её

        //отправка уведомлений
        TaskJPA taskJPA = taskFacade.getTaskByPupID(taskInfoJPA.getProcess().getId());
        LOGGER.info("++++ идентификатор БП " + taskJPA.getProcessTypeName());

        boolean isConfirmClicked = getPUPAttributeBooleanValue(taskJPA.getProcess().getId(), "Одобрить");

        LOGGER.info("++++ нажали на кнопку \"Одобрить\" " + isConfirmClicked);

        if (isConfirmClicked && taskJPA.isProduct()){
            if (taskJPA.getProcessTypeName().equalsIgnoreCase("Крупный бизнес ГО") ||
                    taskJPA.getProcessTypeName().equalsIgnoreCase("Крупный бизнес ГО (Структуратор за МО)") ||
                    taskJPA.getProcessTypeName().equalsIgnoreCase("Изменение условий Крупный бизнес ГО") ||
                    taskJPA.getProcessTypeName().equalsIgnoreCase("Изменение условий Крупный бизнес ГО (Структуратор за МО)"))
                SBeanLocator.singleton().getPriceService().setCloseProbabilityByStatus(taskJPA.getId(), "Подготовка и оформление КОД");
        }

        if (taskJPA.getProcessTypeName().startsWith("Изменение условий")
                && isConfirmClicked) {
            //отправка уведомлений
            try {
                Long idUser = taskInfoJPA.getExecutor().getIdUser();
                Long taskNumber = taskJPA.getMdtask_number();
                Long version = taskJPA.getVersion();
                String mainBorrowerName = taskJPA.getOrgList().get(0).getOrganizationName();

                notifyFacade.onWorkCompletes(idUser, taskNumber, version, mainBorrowerName);
            } catch (Exception e) {
                LOGGER.error("ERROR " + e.getMessage(), e);
            }
        }

        if (isConfirmClicked && Boolean.TRUE.equals(taskJPA.isProduct()))
            mdTaskMapper.createDealPercentHistoryValue(taskJPA.getId(), getCurrentUser().getIdUser());

        //кредитный меморандум и ПРР не в счет
        q = em.createNativeQuery("SELECT count(t.id_task) FROM tasks t INNER JOIN stages ON t.id_stage_to=stages.id_stage "
                + " WHERE t.id_process = :id_process " +
                "AND t.id_status IN (1, 2, 8 ) AND stages.description_stage NOT LIKE '%еморанд%'"
                + " AND stages.description_stage NOT IN ('Подпроцесс завершен','Акцепт экспертизы подразделения по анализу рыночных рисков','Акцепт экспертизы ПРР')");
        q.setParameter("id_process", taskInfoJPA.getProcess().getId());
        exist = (BigDecimal) q.getSingleResult();
        if (exist.longValue() > 0)
            return;
        closeMemorandum(taskInfoJPA.getProcess().getId(), taskInfoJPA.getExecutor().getIdUser());
        q = em.createNativeQuery("UPDATE processes SET id_status = 4 WHERE id_process = :id_process");
        q.setParameter("id_process", taskInfoJPA.getProcess().getId());
        q.executeUpdate();

        q = em.createNativeQuery("INSERT INTO process_events(id_process, id_process_type_event, date_event, id_user) " +
                "VALUES (:id_process, 4, :now, :id_user )");
        q.setParameter("id_process", taskInfoJPA.getProcess().getId());
        q.setParameter("id_user", taskInfoJPA.getExecutor().getIdUser());
        q.setParameter("now", now);
        q.executeUpdate();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Statistic getStatistic() {
        EntityManager em = factory.createEntityManager();
        Statistic s = new Statistic();
        s.setActive((Long) em.createQuery("SELECT COUNT(p) FROM ProcessJPA p where p.idStatus=1").getSingleResult());
        s.setClosed((Long) em.createQuery("SELECT COUNT(p) FROM ProcessJPA p where p.idStatus=4").getSingleResult());
        s.setPaused((Long) em.createQuery("SELECT COUNT(p) FROM ProcessJPA p where p.idStatus=2").getSingleResult());
        return s;
    }

    @Override
    public List<TaskInfoJPA> getTaskInWork(Long processId) {
        EntityManager em = factory.createEntityManager();
        Query q = em.createQuery("SELECT u FROM TaskInfoJPA u where u.idStatus=2 and u.process.id= :processId");
        q.setParameter("processId", processId);
        return q.getResultList();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public boolean isDocumentGroupTypeActive(Long groupId, Long typeId) {
        if (!getDocumentType(typeId).isActive()) return false;
        if (!getDocumentGroup(groupId).isActive()) return false;
        EntityManager em = factory.createEntityManager();
        Long cnt = ((BigDecimal) em.createNativeQuery("select count(*) from r_document_group where is_active=0 and " +
                "ID_DOCUMENT_GROUP=" + groupId + " and ID_DOCUMENT_TYPE=" + typeId).getSingleResult()).longValue();
        if (cnt > 0) return false;
        return true;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<UserJPA> getUserExpertUser(String expname, Long mdtaskid) throws Exception {
        /*добавлять в экспертную группу сотрудников, расположенных в том же структурном подразделении, что и руководитель,
         * и сотрудников и руководителей - на уровнях ниже руководителя экспертного подразделения, производящего назначение,
		 * согласно иерархической структуре подразделений;
		 */
        TaskJPA task = taskFacade.getTask(mdtaskid);
        String stageName = getPUPAttributeValue(task.getProcess().getId(), "Требуется " + expname + "_stage");
        LOGGER.info("stageName=" + stageName);
        UserJPA user = getCurrentUser();
        ArrayList<UserJPA> list = new ArrayList<UserJPA>();
        EntityManager em = factory.createEntityManager();
        Query query = em.createQuery("SELECT u FROM StageJPA u where u.id_type_process = :idTypeProcess and u.description= :name");
        query.setParameter("idTypeProcess", task.getProcess().getProcessType().getIdTypeProcess());
        query.setParameter("name", stageName);
        List<StageJPA> stages = query.getResultList();
        for (StageJPA stage : stages)
            for (RoleJPA role : stage.getRoles())
                for (UserJPA slave : role.getUsers()) {
                    if (slave.getDepartment().getAllParent().contains(user.getDepartment())
                            || slave.getDepartment().equals(user.getDepartment()) && role.getChildRoles().size() == 0)
                        if (!list.contains(slave))
                            list.add(slave);
                }

        return sort(list, stages);
    }

    /**
     * Отсортируем список, как того требуют заказчики.
     *
     * @param list
     * @return
     */
    private List<UserJPA> sort(List<UserJPA> list, List<StageJPA> stages) {
        if (list.size() == 0 || stages.size() == 0)
            return list;
        final String queryStr =
                "select u.id_user, min(rh.SPECIAL_LEVEL) as role_level, dh.special_level, dh.path_full as dep_name, u.surname, u.name, u.patronymic "
                        + " from  stages st "
                        + " inner join stages_in_role sr on sr.id_stage=st.id_stage "
                        + " inner join roles r on r.id_role = sr.id_role and r.active = 1 "
                        + " inner join user_in_role ur on r.id_role=ur.id_role and ur.status = 'Y' "
                        + " inner join users u on u.id_user = ur.id_user "

                        + " inner join v_role_hierarchy rh on rh.ROLE_CHILD = r.id_role "
                        + " inner join departments_hierarchy dh on dh.id_department = u.id_department "

                        + " where u.is_active = 1 and st.id_stage in (STAGES_PARAM) "
                        + " group by dh.special_level, dh.path_full, u.id_user, u.surname, u.name, u.patronymic "
                        + " order by role_level, dh.special_level, dep_name, surname, u.name, patronymic ";

        EntityManager em = factory.createEntityManager();
        Map<Long, UserJPA> map = new HashMap<Long, UserJPA>();
        Set<Long> stagesSet = new HashSet<Long>();
        for (UserJPA user : list) map.put(user.getIdUser(), user);
        for (StageJPA stage : stages) stagesSet.add(stage.getIdStage());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < stages.size(); i++) {
            if (i == 0) sb.append(stages.get(0).getIdStage());
            else sb.append(", " + stages.get(i).getIdStage());
        }
        Query query = em.createNativeQuery(queryStr.replaceAll("STAGES_PARAM", sb.toString()));
        System.out.println("sort query:" + queryStr.replaceAll("STAGES_PARAM", sb.toString()));
        List<Object[]> users = query.getResultList();
        List<UserJPA> sortedList = new ArrayList<UserJPA>();
        System.out.println("sorted expert list");
        for (Object[] row : users) {
            Long key = ((BigDecimal) row[0]).longValue();
            if (map.containsKey(key)) {
                sortedList.add(map.get(key));

                for (Object column : row) System.out.print(Formatter.str(column) + " ");
                System.out.println();
            }
        }
        return sortedList;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Set<Long> getIdProcessTypeForUser(Long idUser) {
        long tstart = System.currentTimeMillis();
        EntityManager em = factory.createEntityManager();
        Query q = em.createNativeQuery("SELECT DISTINCT r.id_type_process FROM user_in_role ur INNER JOIN roles r ON r.id_role=ur.id_role WHERE ur.id_user=? AND ur.status='Y'");
        q.setParameter(1, idUser);
        Set<Long> res = new HashSet<Long>();
        for (Object r : q.getResultList())
            res.add(((BigDecimal) r).longValue());
        Long loadTime = System.currentTimeMillis() - tstart;
        LOGGER.warn("*** total getIdProcessTypeForUser() time " + loadTime);
        return res;
    }

    /* список БП где пользователь большой аудитор*/
    private List<Long> getIdBigAuditorProccessTypeForUser(Long idUser) {
        long tstart = System.currentTimeMillis();
        EntityManager em = factory.createEntityManager();
        Query q = em.createNativeQuery("SELECT DISTINCT r.id_type_process FROM user_in_role ur "
                + "INNER JOIN roles r ON r.id_role=ur.id_role WHERE ur.id_user=? AND ur.status='Y' AND "
                + "r.name_role IN ('Аудитор', 'Руководитель мидл-офиса', 'Администратор системы', '" + UserJPA.ACCESS_DOWNLOAD + "', '" + UserJPA.ACCESS_DLD_CNTRL + "')");
        q.setParameter(1, idUser);
        ArrayList<Long> res = new ArrayList<Long>();
        for (Object r : q.getResultList())
            res.add(((BigDecimal) r).longValue());
        Long loadTime = System.currentTimeMillis() - tstart;
        LOGGER.warn("*** total getIdBigAuditorProccessTypeForUser() time " + loadTime);
        return res;
    }

    /* список id типов процессов, по которым у пользователя есть руководящая роль*/
    private Set<Long> getIdBossProcessTypeForUser(Long idUser) {
        long tstart = System.currentTimeMillis();
        EntityManager em = factory.createEntityManager();
        Query q = em.createNativeQuery("SELECT DISTINCT r.id_type_process FROM user_in_role ur " +
                "INNER JOIN roles r ON r.id_role=ur.id_role WHERE ur.id_user=? AND ur.status='Y' " +
                "AND (exists (SELECT 1 FROM role_nodes n WHERE n.role_parent=r.id_role) " +
                "OR NOT exists (SELECT 1 FROM role_nodes n WHERE n.role_child=r.id_role))" +
                " AND upper(r.name_role) NOT LIKE '%АУДИТОР%' AND upper(r.name_role) NOT LIKE '%АДМИНИСТРАТОР%' AND upper(r.name_role) NOT LIKE 'РЕДАКТОР НОРМАТИВНЫХ СРОКОВ'");
        q.setParameter(1, idUser);
        Set<Long> res = new HashSet<Long>();
        for (Object r : q.getResultList())
            res.add(((BigDecimal) r).longValue());
        Long loadTime = System.currentTimeMillis() - tstart;
        LOGGER.warn("*** total getIdBossProcessTypeForUser() time " + loadTime);
        return res;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public boolean isAdmin(Long isUser) {
        //
        EntityManager em = factory.createEntityManager();
        Query q = em.createNativeQuery("SELECT count(*) FROM user_in_role ur INNER JOIN roles r ON r.id_role=ur.id_role WHERE r.name_role='Администратор системы' AND ur.id_user=? AND r.active=1 AND ur.status='Y'");
        q.setParameter(1, isUser);
        BigDecimal res = (BigDecimal) q.getSingleResult();
        return res.longValue() > 0;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void setTaskStatus(Long statusId, Long idTaskCompleted) {
        EntityManager em = factory.createEntityManager();
        Query q = em.createNativeQuery("UPDATE TASKS SET id_status = :status WHERE ID_TASK = :id");
        q.setParameter("id", idTaskCompleted);
        q.setParameter("status", statusId);
        q.executeUpdate();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String whoAssignedAs(String roleName, Long idProcess) {
        if (roleName.equalsIgnoreCase("Структуратор") && !whoAssignedAs("Руководитель структуратора", idProcess).isEmpty())
            return whoAssignedAs("Руководитель структуратора", idProcess);
        EntityManager em = factory.createEntityManager();
        Query query = em.createNativeQuery("SELECT u.SURNAME||' '||u.NAME||' '||u.PATRONYMIC FROM assign a " +
                "INNER JOIN process_events pe ON pe.id_process_event=a.id_process_event " +
                "INNER JOIN roles r ON r.id_role=a.id_role " +
                "INNER JOIN users u ON u.ID_USER=a.ID_USER_TO " +
                "WHERE pe.id_process=? AND r.name_role=?");
        query.setParameter(2, roleName);
        query.setParameter(1, idProcess);
        List list = query.getResultList();
        if (list.size() == 0)
            return "";
        else
            return list.get(0).toString();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public boolean currentUserAssignedAs(String roleName, Long idProcess) {
        if (idProcess == null)
            return false;
        UserJPA user = getCurrentUser();
        return userAssignedAs(user.getIdUser(), roleName, idProcess);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public boolean userAssignedAs(Long idUser, String roleName, Long idProcess) {
        EntityManager em = factory.createEntityManager();
        Query query = em.createNativeQuery("SELECT count(*) FROM assign a "
                + "INNER JOIN process_events pe ON pe.id_process_event=a.id_process_event "
                + "INNER JOIN roles r ON r.id_role=a.id_role "
                + "WHERE a.id_user_to=? AND pe.id_process=? AND r.name_role=? "
                + "AND exists (SELECT 1 FROM user_in_role ur WHERE ur.id_user=? "
                + "AND ur.id_role=r.id_role AND ur.status='Y')");
        query.setParameter(1, idUser);
        query.setParameter(4, idUser);
        query.setParameter(3, roleName);
        query.setParameter(2, idProcess);
        return ((BigDecimal) query.getSingleResult()).longValue() > 0;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public boolean isCurrentUserInProjectTeam(Long idMdtask) {
        UserJPA user = getCurrentUser();
        EntityManager em = factory.createEntityManager();
        Query query = em.createNativeQuery("SELECT count(*) FROM project_team t WHERE t.teamtype='p' AND t.id_mdtask=? AND t.id_user=?");
        query.setParameter(2, user.getIdUser());
        query.setParameter(1, idMdtask);
        return ((BigDecimal) query.getSingleResult()).longValue() > 0;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void removeAssignOnExpertiseStage(Long idTaskCompleted, List<Long> nextStages) {
        TaskInfoJPA taskInfo = getTask(idTaskCompleted);
        UserJPA user = getCurrentUser();
        StandardPeriodGroupJPA group = getExpertStGr4stage(taskInfo);
        if (group == null)
            return;
        // проверить что переходим на операцию не из этого этапа (то есть итерация экспертизы закончилась)
        for (Long nextStage : nextStages) {
            boolean outerOperation = true;
            for (StageJPA stage : group.getStages())
                if (stage.getIdStage().equals(nextStage))
                    outerOperation = false;
            if (outerOperation)
                //найти роли по операциям этапа
                for (StageJPA stage : group.getStages())
                    for (RoleJPA role : stage.getRoles())
                        //отозвать назначение
                        deleteOldAssign(role.getIdRole(), taskInfo.getProcess().getId(), new Date(), user.getIdUser());
        }
    }

    /**
     * найти этап нормативных сроков экспертизы для этой операции
     */
    private StandardPeriodGroupJPA getExpertStGr4stage(TaskInfoJPA taskInfo) {
        return getExpertStGr4stage(taskInfo.getStage().getIdStage(), taskInfo.getProcess().getId());
    }

    /**
     * найти этап нормативных сроков экспертизы для этой операции
     */
    private StandardPeriodGroupJPA getExpertStGr4stage(Long idStage, Long idProcess) {
        TaskJPA mdtask = taskFacade.getTaskByPupID(idProcess);
        if (mdtask.getActiveStandardPeriodVersion() == null)
            return null;
        for (StandardPeriodGroupJPA group : mdtask.getActiveStandardPeriodVersion().getStandardPeriodGroups())
            if (group.getStages().contains(getStage(idStage))) {
                //проверить что это этап экспертизы
                for (StageJPA stage : group.getStages())
                    for (AttributeJPA attr : mdtask.getProcess().getAttributes())
                        if (attr.getVariable().getName().startsWith("Требуется")
                                && attr.getVariable().getName().endsWith("_stage")
                                && stage.getDescription().equals(attr.getValue()))
                            return group;
            }
        return null;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public StandardPeriodGroupJPA getGroup4Stage(Long idStage, Long idProcess) {
        if (idStage == null)
            return null;
        TaskJPA mdtask = taskFacade.getTaskByPupID(idProcess);
        if (mdtask.getActiveStandardPeriodVersion() == null)
            return null;
        for (StandardPeriodGroupJPA group : mdtask.getActiveStandardPeriodVersion().getStandardPeriodGroups())
            if (group.getStages().contains(getStage(idStage))) {
                return group;
            }
        return null;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public boolean isExpertiseStage(Long idStage, Long idProcess) {
        if (idStage == null || idProcess == null)
            return false;
        try {
            Query q = factory.createEntityManager().createNativeQuery(
                    "SELECT count(*) FROM stages s WHERE s.id_stage=? AND s.description_stage IN (SELECT stage FROM spo_expert_stage)");
            q.setParameter(1, idStage);
            return ((BigDecimal) q.getSingleResult()).longValue() > 0;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public StageJPA getStage(Long id) {
        EntityManager em = factory.createEntityManager();
        return em.find(StageJPA.class, id);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public boolean isPipelineReadonly(Long idMdTask) {
        return mdTaskMapper.isPipelineReadonly(idMdTask, getCurrentUser().getIdUser());
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void startTimer() {
        try {
            // clear old running timers
            for (Object obj : timerService.getTimers()) {
                Timer timer = (Timer) obj;
                String typeOfTimer = (String) timer.getInfo();
                if (typeOfTimer != null && typeOfTimer.equals(TIMER_NAME)) {
                    LOGGER.info("Таймер " + typeOfTimer + " отключен!");
                    timer.cancel();
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        LOGGER.info("таймер " + TIMER_NAME + " запускается");

        // запускаем по расписанию
        try {
            final long h = 5 * 60 * 1000; // hour in milliseconds.
            timerService.createTimer(new Date(), h, TIMER_NAME);
        } catch (Exception e) {
            LOGGER.error("Couldn't start delinquency timer " + e.getMessage());
        }
    }

    @Timeout
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void onTimeout(Timer timer) {
        try {
            String typeOfTimer = (String) timer.getInfo();
            if (TIMER_NAME.equals(typeOfTimer)) {
                EntityManager em = factory.createEntityManager();
                //проверим инициирован ли атрибут
                Query query = em.createNativeQuery("UPDATE tasks t SET t.id_status=3 " +
                        "WHERE t.id_status=2 AND " +
                        "exists (SELECT 1 FROM task_events e WHERE e.id_task=t.id_task AND e.id_task_type_event IN (3,4,5))");
                query.executeUpdate();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public int getTaskStatus(Long taskId) {
        EntityManager em = factory.createEntityManager();
        Query query = em.createNativeQuery("SELECT ID_STATUS FROM tasks WHERE ID_TASK=?");
        query.setParameter(1, taskId);
        BigDecimal obj = (BigDecimal) query.getSingleResult();
        return obj.intValue();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public boolean isPassStage(Long idPupProcess, String stageName) {
        EntityManager em = factory.createEntityManager();
        Query query = em.createNativeQuery("SELECT count(*) FROM tasks t INNER JOIN stages s ON t.id_stage_to=s.id_stage " +
                "WHERE t.id_status=3 AND s.description_stage=? AND t.id_process=?");
        query.setParameter(1, stageName);
        query.setParameter(2, idPupProcess);
        return !query.getSingleResult().toString().equals("0");
    }

    @Override
    public Long createSpoRouteVersion(Long idProcessType) {
        EntityManager em = factory.createEntityManager();
        ProcessTypeJPA pt = em.find(ProcessTypeJPA.class, idProcessType);
        SpoRouteVersionJPA version = new SpoRouteVersionJPA();
        version.setDate(new java.util.Date());
        version.setProcessType(pt);
        pt.getSpoRouteVersion().add(version);
        em.persist(version);
        em.merge(pt);

		/*StandardPeriodGroupJPA group = new StandardPeriodGroupJPA();
        group.setName("весь процесс");
		for(StageJPA s : getFirstStages(idProcessType))
			group.getDecisionStages().add(s);
		group.setVersion(version);
		group.setStages(new ArrayList<StageJPA>());
		for (StageJPA stage : getStages(idProcessType)){
			if(stage.isActive()) group.getStages().add(stage);
		}
		version.setStandardPeriodGroups(new ArrayList<StandardPeriodGroupJPA>());
		version.getStandardPeriodGroups().add(group);
		em.persist(group);
		em.merge(version);*/
        em.flush();
        return version.getId();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<AttachJPA> findAttachemnt(String idOwner, Long ownerType,
                                          Long docGroupID, Long docTypeID, boolean showOnlyNotExpired) {
        String sql = "SELECT c FROM AttachJPA c "
                + "WHERE c.ID_OWNER LIKE ?1 and c.OWNER_TYPE=?2 and c.group.id=?3 and c.documentType.id=?4 and c.whoDel is null";
        if (showOnlyNotExpired)
            sql += " and (DATE_OF_EXPIRATION is null or DATE_OF_EXPIRATION > ?5)";
        sql += " order by c.DATE_OF_ADDITION";
        Query query = factory.createEntityManager().createQuery(sql);
        query.setParameter(1, idOwner);
        query.setParameter(2, ownerType);
        query.setParameter(3, docGroupID);
        query.setParameter(4, docTypeID);
        if (showOnlyNotExpired)
            query.setParameter(5, new Date());
        return query.getResultList();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<AttachJPA> findAttachemnt(String idOwner, Long ownerType,
                                          Long docGroupID, Long docTypeID) {
        return findAttachemnt(idOwner, ownerType, docGroupID, docTypeID, false);
    }


    @Override
    public List<AttachJPA> findOtherAttachemnt(String idOwner, Long ownerType,
                                               boolean showOnlyNotExpired) {
        String sql = "SELECT c FROM AttachJPA c "
                + "WHERE c.ID_OWNER LIKE ?1 and c.OWNER_TYPE=?2 and (c.group is null or c.documentType is null)  and c.whoDel is null ";
        if (showOnlyNotExpired)
            sql += " and (DATE_OF_EXPIRATION is null or DATE_OF_EXPIRATION > ?3)";
        sql += " order by c.FILETYPE";
        Query query = factory.createEntityManager().createQuery(sql);
        query.setParameter(1, idOwner);
        query.setParameter(2, ownerType);
        if (showOnlyNotExpired)
            query.setParameter(3, new Date());
        return query.getResultList();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<AttachJPA> findOtherAttachemnt(String idOwner, Long ownerType) {
        return findOtherAttachemnt(idOwner, ownerType, false);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public DepartmentJPA getDepartmentById(Long id) {
        return factory.createEntityManager().find(DepartmentJPA.class, id);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String startPRR(Long idUser, Long idProcess) {
        String firstStageName = "Акцепт экспертизы подразделения по анализу рыночных рисков";
        /*должна инициироваться первая операция "автоветки ПРР"
        Наименование такой операции: "Акцепт экспертизы подразделения по анализу рыночных рисков"*/
        EntityManager em = factory.createEntityManager();
        UserJPA user = getUser(idUser);
        ProcessJPA processJPA = em.find(ProcessJPA.class, idProcess);
        for (StageJPA stage : getStages(processJPA.getProcessType().getIdTypeProcess()))
            if (stage.getDescription().equals("Акцепт экспертизы ПРР"))
                firstStageName = "Акцепт экспертизы ПРР";
        for (StageJPA stage : getStages(processJPA.getProcessType().getIdTypeProcess()))
            if (stage.getDescription().equals("Акцепт экспертизы подразделения по анализу рыночных рисков в связи с изменениями"))
                firstStageName = "Акцепт экспертизы подразделения по анализу рыночных рисков в связи с изменениями";
        //Подразделение такой операции=Подразделение операции "Формирование Предварительных параметров Лимита/условий Сделки"
        Long idDep = user.getDepartment().getIdDepartment();
        for (TaskInfoJPA ti : processJPA.getTasks())
            if (ti.getStage().getDescription().equals("Формирование Предварительных параметров Лимита/условий Сделки"))
                idDep = ti.getIdDepartament();
        try {
            Query query = em.createNativeQuery("SELECT tasks_seq.NEXTVAL FROM dual");
            Long id_task = ((BigDecimal) query.getResultList().get(0)).longValue();
            query = em.createNativeQuery(
                    "insert into tasks (id_task,id_type_process,id_process,id_stage_to,id_department,id_status)" +
                            " select " + id_task + ",s.id_type_process," + idProcess + ",s.id_stage," +
                            idDep + ",1  from stages s " +
                            "where s.description_stage='" + firstStageName + "' " +
                            "and s.id_type_process=" + processJPA.getProcessType().getIdTypeProcess());
            query.executeUpdate();
            query = em.createNativeQuery("INSERT INTO task_events(id_task_event,id_task,id_task_type_event,id_user,date_event)" +
                    " VALUES(task_events_seq.nextval,?,1,?,?)");
            query.setParameter(1, id_task);
            query.setParameter(2, idUser);
            query.setParameter(3, new java.util.Date());
            query.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return "Ошибка старта экспертизы ПРР: " + e.getMessage();
        }
        updatePUPAttribute(idProcess, "Экспертиза ПРР", "1");
        /*должны направляться уведомления о поступлении операции "Акцепт экспертизы подразделения по анализу рыночных рисков"
            в обработку всем возможным исполнителям текущего и вышестоящих подразделений в соответствие со структурой доступа*/
        try {
            PupFacadeLocal pupFacade = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
            pupFacade.notifyPrr(idProcess, firstStageName, user, idDep);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return "Ветка процесса 'Экспертиза ПРР' успешно запущена";
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void notifyPrr(Long idProcess, String firstStageName,
                          UserJPA user, Long idDep) {
        try {
            EntityManager em = factory.createEntityManager();
            TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
            TaskJPA task = taskFacadeLocal.getTaskByPupID(idProcess);
            Query query = em.createNativeQuery("select distinct u.id_user from  users u " +
                    "inner join user_in_role ur on ur.id_user=u.id_user  " +
                    "inner join stages_in_role sr on sr.id_role=ur.id_role  " +
                    "inner join stages s on s.id_stage=sr.id_stage " +
                    "where lower(ur.status) = 'y' and u.mail_user is not null and " +
                    "s.description_stage='" + firstStageName + "' " +
                    "and ? in (select dp.id_department_child from departments_par dp CONNECT BY PRIOR id_department_child=id_department_par " +
                    "START WITH dp.id_department_par =u.id_department union select u.id_department from dual)");
            query.setParameter(1, idDep);
            List<BigDecimal> list = query.getResultList();
            for (BigDecimal userid : list) {
                UserJPA to = em.find(UserJPA.class, userid.longValue());
                String subject = MessageFormat.format(SPOMessage.waitSubjectMessageFormat, task.getNumberAndVersion());
                String bodyMessage = MessageFormat.format(SPOMessage.waitBodyMessageFormat, getBaseURL(userid.longValue()),
                        task.getMdtask_number().toString(), task.getNumberAndVersion(),
                        task.getOrganisation(), firstStageName,
                        "Крупный бизнес ГО");
                notifyFacade.send(user.getIdUser(),
                        to.getIdUser(), subject, bodyMessage);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public boolean isHasActiveTask(Long idProcess, Long idStage) {
        if (idStage == null)
            return false;
        EntityManager em = factory.createEntityManager();
        Query q = em.createNativeQuery("SELECT count(*) FROM tasks t " +
                "WHERE t.id_process = :id_process AND t.id_stage_to = :id_stage AND t.id_status IN (1, 2 )");
        q.setParameter("id_stage", idStage);
        q.setParameter("id_process", idProcess);
        BigDecimal exist = (BigDecimal) q.getSingleResult();
        return exist.longValue() > 0;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<TaskInfoJPA> getTaskByProcessId(Long processId) {
        EntityManager em = factory.createEntityManager();
        Query q = em.createQuery("SELECT u FROM TaskInfoJPA u where u.process.id= :processId order by u.idTask desc");
        q.setParameter("processId", processId);
        return q.getResultList();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public boolean isCedEnded(Long idMdtask) {
        /*EntityManager em = factory.createEntityManager();
        Query q = em.createNativeQuery("SELECT CASE WHEN EXISTS(SELECT 1 FROM CED_COMMON_DEAL_CONCLUSION WHERE STATUS = 'PAYMENT_COMPLETED' AND ID_MDTASK =  :idMdtask) THEN 1 ELSE 0 END IS_HAS_COMPLETED_PAYMENT FROM DUAL");
		q.setParameter("idMdtask", idMdtask);
		return ((BigDecimal) q.getSingleResult()).longValue()>0;*/
        return false;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public UserJPA getProcessInitiator(Long idProcess) {
        EntityManager em = factory.createEntityManager();
        Query q = em.createNativeQuery("SELECT e.id_user FROM process_events e "
                + "WHERE e.id_process = :idProcess AND e.id_process_type_event = 1");
        q.setParameter("idProcess", idProcess);
        try {
            BigDecimal idUserB = (BigDecimal) q.getSingleResult();
            if (idUserB != null)
                return getUser(idUserB.longValue());
        } catch (NoResultException e) {
        }
        return null;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Long createProcessWithAccept(Long id_type_process, Long idUser, Boolean acceptWork) throws ModelException {
        EntityManager em = factory.createEntityManager();
        UserJPA user = getUser(idUser);
        // получить список этапов
        // проверить права пользователя на все этапы
        for (StageJPA stage : getFirstStages(id_type_process)) {
            boolean access = false;
            for (RoleJPA role : user.getRoles())
                if (stage.getRoles().contains(role))
                    access = true;
            if (!access)
                throw new ModelException("у пользователя (" + user.getFullName()
                        + ") нет прав на операцию " + stage.getDescription());
        }
        Query query = em.createNativeQuery("SELECT PROCESSES_seq.NEXTVAL FROM dual");
        Long id_process = Long.valueOf(((BigDecimal) query.getResultList().get(0)).toString());
        Long id_process_event = getNextProcessEventID();
        query = em
                .createNativeQuery("INSERT INTO processes (id_status, id_type_process,id_process) VALUES (1, ?1,?2)");
        query.setParameter(1, id_type_process);
        query.setParameter(2, id_process);
        query.executeUpdate();
        query = em
                .createNativeQuery("INSERT INTO process_events( id_process, id_process_type_event, signature, date_event, id_user, id_process_event )"
                        + " VALUES( ?1, 1, empty_blob(), ?2, ?3 , ?4)");
        query.setParameter(1, id_process);
        query.setParameter(2, new java.util.Date());
        query.setParameter(3, idUser);
        query.setParameter(4, id_process_event);
        query.executeUpdate();
        for (StageJPA stage : getFirstStages(id_type_process)) {
            query = em.createNativeQuery("SELECT tasks_seq.NEXTVAL FROM dual");
            Long id_task = Long.valueOf(((BigDecimal) query.getResultList().get(0)).toString());
            query = em
                    .createNativeQuery("INSERT INTO tasks (id_type_process, id_process, id_stage_to, type_complation, id_department, id_status,id_task)"
                            + " VALUES (?1, ?2, ?3, NULL, ?4, 1,?5 )");
            query.setParameter(1, id_type_process);
            query.setParameter(2, id_process);
            query.setParameter(3, stage.getIdStage());
            query.setParameter(4, user.getDepartment().getIdDepartment());
            query.setParameter(5, id_task);
            query.executeUpdate();
            query = em
                    .createNativeQuery("INSERT INTO task_events( id_task, id_task_type_event, signature, date_event, id_user )"
                            + " VALUES( ?1, 1, empty_blob(), ?2, ?3 )");
            query.setParameter(1, id_task);
            query.setParameter(2, new java.util.Date());
            query.setParameter(3, idUser);
            query.executeUpdate();
            // взять в работу
            if (acceptWork)
                try {
                    acceptWork(id_task, idUser);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
        }

        return id_process;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public boolean isHasCed(Long idMdtask) {
        if (Config.getProperty("skip.int").equalsIgnoreCase("true"))// файл workflow.properties
            return false;
        /*CreditEnsuringDocFilter filter = new CreditEnsuringDocFilter();
        filter.setCountOnPage(1L);
		filter.setPageNumber(0L);
		filter.setCreditDealId(idMdtask);
		return ServiceFactory.getService(CedService.class).getDealConclusions(filter).size() > 0;*/
        try {
            EntityManager em = factory.createEntityManager();
            Query q = em.createNativeQuery("SELECT count(*) FROM CED_COMMON_DEAL_CONCLUSION COM WHERE COM.ID_MDTASK = :idMdtask AND COM.TYPE = 'DEAL_CONCLUSION'");
            q.setParameter("idMdtask", idMdtask);
            return ((BigDecimal) q.getSingleResult()).longValue() > 0;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return false;
        }
    }
}
