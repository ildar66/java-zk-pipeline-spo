package org.uit.director.contexts;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.QueueConnection;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;

import org.apache.crimson.tree.XmlDocument;
import org.hsqldb.lib.StringInputStream;
import org.uit.director.db.dbobjects.Attribute;
import org.uit.director.db.dbobjects.AttributeStruct;
import org.uit.director.db.dbobjects.BasicAttribute;
import org.uit.director.db.dbobjects.Cnst;
import org.uit.director.db.dbobjects.ProcessControlType;
import org.uit.director.db.dbobjects.TypeComparator;
import org.uit.director.db.dbobjects.VarsPermissions;
import org.uit.director.db.dbobjects.VarsPermissions.Permission;
import org.uit.director.db.dbobjects.WFObjectComparator;
import org.uit.director.db.dbobjects.WorkflowDepartament;
import org.uit.director.db.dbobjects.WorkflowObject;
import org.uit.director.db.dbobjects.WorkflowProcessParameters;
import org.uit.director.db.dbobjects.WorkflowRoles;
import org.uit.director.db.dbobjects.WorkflowStages;
import org.uit.director.db.dbobjects.WorkflowStatusProcess;
import org.uit.director.db.dbobjects.WorkflowSubProcess;
import org.uit.director.db.dbobjects.WorkflowTypeProcess;
import org.uit.director.db.dbobjects.WorkflowTypeProcessList;
import org.uit.director.db.dbobjects.WorkflowUser;
import org.uit.director.db.dbobjects.WorkflowVariables;
import org.uit.director.db.dbobjects.graph.MGraph;
import org.uit.director.db.ejb.DBFlexWorkflowCommon;
import org.uit.director.decider.BusinessProcessDecider;
import org.uit.director.managers.UsersManager;
import org.xml.sax.SAXException;

import ru.masterdm.compendium.custom.UserTO;
import ru.masterdm.compendium.domain.Department;
import ru.masterdm.compendium.domain.Role;
import ru.masterdm.compendium.domain.User;
import ru.masterdm.compendium.model.CompendiumActionProcessor;
import ru.md.pup.dbobjects.RoleJPA;
import ru.md.pup.dbobjects.UserJPA;
import ru.md.spo.ejb.NotifyFacadeLocal;
import ru.md.spo.ejb.PupFacadeLocal;
import ru.md.spo.ejb.StandardPeriodBeanLocal;
import ru.md.spo.util.Config;

import com.vtb.domain.Task;
import com.vtb.domain.TaskHeader;
import com.vtb.exception.FactoryException;
import com.vtb.exception.NoSuchDepartmentException;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.TaskActionProcessor;

public class WPC {
    private static Logger LOGGER = Logger.getLogger(WPC.class.getName());
    private static long hashTTL = 600000;//время жизни хеша 10 минут
    
    private static final String PROCESS_DEFINITION_XML = "processDefinition.xml";

    public static int VERSION_DB = 4;
    public static int VERSION_MODULE = 1;
    public static int VERSION_FORMATE = 6;

    private WorkflowTypeProcessList typeProcesses;
    private List<WorkflowStages> stages;
    private List<WorkflowRoles> roles;
    private List<WorkflowStatusProcess> statusProcesses;
    private List<WorkflowVariables> variables;
    private Map<Long, ArrayList<Long>> subordinateUsers; // список подчиненных для каждого пользователя
    private Map<Long, List<Long>> stagesInRole; // список этапов для заданной роли
    private List<WorkflowProcessParameters> processParameters;

    /**
     * Key-idUser, value-Полное имя
     */
    private UsersManager usersMgr; // менеджер списком пользователей системы
    private boolean isCorrectInit = false;

    private Map<Integer, List<Long>> stagesInTypeProcess; // список принадлежности этапов (значение элемента Map)
                                                          // к типу процесса (идентификаторов - ключ элемента Map)
    private Map<Integer, List<Long>> rolesInTypeProcess; // список принадлежности ролей (значение элемента Map)
                                                         // к типу процесса (идентификаторов - ключ элемента Map)
    private Map<Integer, List<Long>> usersInTypeProcess; // список принадлежности пользователей процессу
    private List<WorkflowSubProcess> subProcesses;
    private Map<Long, List<Long>> varConnections; // данные иерархии атрибутов
                                                  // (смежность, дочерние атрибуты атрибута )
    private Map<Long, List<Long>> varNodes; // данные иерархии атрибутов
    private Map<Long, List<Long>> rolesNodes; // данные из таблицы иерархии ролей типа PARENT CHILD;
                                              // (дочерний набор атрибутов в структуре )
    private MGraph<Long> attributesGraph;   // (значение элемента Map)
                                            // к типу процесса (идентификаторов - ключ элемента Map)
                                            // private Map<Integer, Map> usersInStage;
    private Map<Integer, XmlDocument> schemaMap;
    private Map<Integer, String> schemaImageMap;
    private List<WorkflowDepartament> departments; // отделы
    private Map<Long, List<Long>> departmentsPar; // иерархии отделов
    private Map<String, String> departmentsHierarchy; // иерархии отделов с именами с отступами (эмуляция дерева) для отображения в списках
    
    public SimpleDateFormat dateTimeFormat = new SimpleDateFormat(Config.getProperty("DATE_TIME_FORMAT"));
    public SimpleDateFormat dateTimeFormatUser = new SimpleDateFormat(Config.getProperty("DATE_TIME_FORMAT_USER"));
    public SimpleDateFormat dateTimeDBFormat = new SimpleDateFormat(Config.getProperty("DATE_TIME_DB_FORMAT"));
    public SimpleDateFormat dateFormat = new SimpleDateFormat(Config.getProperty("DATE_FORMAT"));

    private static WPC wpc = null;

    public List<String> directVars;
    private QueueSession queueSession;
    private QueueReceiver queueReceiverCreateProcess;
    private QueueReceiver queueReceiverExecutePlugin;
    private QueueConnection queueConnection;

    public static final Integer INCLUDE_NONE = 3;
    public static final Integer INCLUDE_SUBORDINATE = 2;
    public static final Integer INCLUDE_ALL = 1;

    public static WPC getInstance() {
        return wpc;

    }

    public static String PROCESS_DEFINITION = PROCESS_DEFINITION_XML;
    public static String ATTRIBUTES_DEFINITION = "attributesDefinition.xml";
    public static String STAGES_DEFINITION = "stagesDefinition.xml";
    public static String ROLES_DEFINITION = "rolesDefinition.xml";
    public static String SUB_PROCESS_DEFINITION = "subProcessDefinition.xml";
    public static String IMAGE_DEFINITION = "imageDefinition.xml";

    public static String init(DBFlexWorkflowCommon flexWorkflow) {

        String res = "error";
        try {
        if (wpc == null) {

            int dbVersion = flexWorkflow.getVersionDB();            
            if (dbVersion != VERSION_DB) {
                res = "Версия базы (№ " + dbVersion
                        + ") не соответсвует версии модуля (№ " + VERSION_DB
                        + ")";

            } else {
                wpc = new WPC(flexWorkflow);
                // wpc.startListenerCreateProcess();
                // wpc.startListenerExecutePlugin();
                wpc.setCorrectInit(true);
                res = "ok";
            }

        } else {
            if (!wpc.isCorrectInit()) {
                try {
                    wpc = new WPC(flexWorkflow);
                    wpc.setCorrectInit(true);
                    res = "ok";
                } catch (Exception e) {
                    res = e.getMessage();
                    e.printStackTrace();
                }

            } else {
                res = "ok";
            }
        }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
    
    /**
     * Получить все дочерние филиалы
     * @param departmentId
     */
    public Set<Long> getAllChildrenOfDeparment(long departmentId) {
        Set<Long> allChildren = new HashSet<Long>();
        getChildrenOfDeparment(departmentId, allChildren);
        return allChildren;
    }

    /**
     * Получить все дочерние департаменты 
     * @param departmentId материнский филиал
     * @param allChildren результат, список добавленных филиалов 
     */
    public void getChildrenOfDeparment(Long departmentId, Set<Long> allChildren) {      
        List<Long> children = departmentsPar.get(departmentId);
        if (children != null) {
            for (Long id : children) {
                allChildren.add(id);
                getChildrenOfDeparment(id, allChildren);
            }
        }
    }

    private void initDirectVars() {
        try {
            directVars = Config.getProperties("COMMON_VAR");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private WPC(DBFlexWorkflowCommon flexWorkflow) {
        if (typeProcesses == null && stages == null && roles == null
                && statusProcesses == null) {
            initDirectVars();
            reloadStaticTables(flexWorkflow);
            if (schemaMap == null) {
                setSchema();
            }
            if (schemaImageMap == null) {
                setSchemaImageMap();
            }
        }
        try {
        	NotifyFacadeLocal notifyFacade = com.vtb.util.EjbLocator.getInstance().getReference(NotifyFacadeLocal.class);
        	notifyFacade.startTimer();//уведомления о просроченных заявках
            StandardPeriodBeanLocal spBean = com.vtb.util.EjbLocator.getInstance().getReference(StandardPeriodBeanLocal.class);
            spBean.startTimer();
            TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
            processor.startTimer(1800);//CRM check
            //spBean.recalculateDeadline(null);//при старте пересчитываем сроки по всем заявкам
            PupFacadeLocal pupFacade = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
            pupFacade.startTimer();
            pupFacade.updateCdVersion();
        } catch (Exception e) {
            LOGGER.warning("не могу запустить таймер" + e.getMessage());
        }
    }

    public boolean isCorrectInit() {
        return isCorrectInit;
    }

    public void setCorrectInit(boolean correctInit) {
        isCorrectInit = correctInit;
    }

    public static synchronized void reload(DBFlexWorkflowCommon flexWorkflow) {

        if (wpc != null) {
            wpc.typeProcesses = null;
            wpc.stages = null;
            wpc.roles = null;
            wpc.statusProcesses = null;
            wpc.variables = null;
            wpc.schemaMap = null;
            wpc.processParameters = null;
            wpc.departments = null;
            wpc.departmentsPar = null;
            wpc.departmentsHierarchy = null;
            wpc = null;
        }
        init(flexWorkflow);
    }

    @SuppressWarnings( { "unchecked" })
    public synchronized void reloadStaticTables(
            DBFlexWorkflowCommon dbFlexDirector) {

        try {
            subordinateUsers = new HashMap<Long, ArrayList<Long>>();
            usersMgr = new UsersManager(dbFlexDirector);
            HashMap data = dbFlexDirector.getStaticWorkflowData();
            ArrayList<WorkflowTypeProcess> tpProcesses = (ArrayList<WorkflowTypeProcess>) data
                    .get("TYPE_PROCESS");
            typeProcesses = new WorkflowTypeProcessList();
            typeProcesses.init(tpProcesses, Double.valueOf(
                    Config.getProperty("K_COMP")).doubleValue());
            stages = (List<WorkflowStages>) data.get("STAGES");

            Collections.sort(stages, new WFObjectComparator());
            roles = (List) data.get("ROLES");
            Collections.sort(roles, new WFObjectComparator());
            statusProcesses = (List) data.get("PROCESS_STATUS");
            variables = (List) data.get("VARIABLES");
            stagesInRole = (Map<Long, List<Long>>) data.get("STAGES_IN_ROLE");
            processParameters = (List<WorkflowProcessParameters>) data.get("PROCESS_PARAMETERS");
            
            stagesInTypeProcess = (Map<Integer, List<Long>>) data.get("STAGES_IN_TYPE_PROCESS");
            rolesInTypeProcess = (Map<Integer, List<Long>>) data.get("ROLES_IN_TYPE_PROCESS");
            usersInTypeProcess = (Map<Integer, List<Long>>) data.get("USERS_IN_TYPE_PROCESS");
        
            subProcesses = (List<WorkflowSubProcess>) data.get("SUB_PROCESSES");
            
            varConnections = (Map<Long, List<Long>>) data.get("VAR_CONNECTIONS");
            varNodes = (Map<Long, List<Long>>) data.get("VAR_NODES");

            rolesNodes = (Map<Long, List<Long>>) data.get("ROLES_NODES");
            attributesGraph = (MGraph<Long>) data.get("ATTRIBUTES_GRAPH");

            departments = (List<WorkflowDepartament>) data.get("DEPARTMENTS");
            departmentsPar = (Map<Long, List<Long>>) data.get("DEPARTMENTS_PAR");

            departmentsHierarchy = new LinkedHashMap<String, String>();
            List<Map<String, String>> departmentsHierarchyList = (List<Map<String, String>>) data.get("DEPARTMENTS_HIERARCHY");
            for (Map<String, String> map : departmentsHierarchyList)
                departmentsHierarchy.put(map.get("ID_DEPARTMENT"), 
                    map.get("NAME").replaceAll(" "," "));  // change 20 to UTF invisible space
            departmentsHierarchyList = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Универсальный метод получения данных из справочных таблиц
     * 
     * @return res
     */
    public synchronized Object getData(int table, long id, String field) {

        Object data = null;
        List dataList = null;

        switch (table) {
        case Cnst.TBLS.stages: {
            dataList = stages;
            break;
        }
        case Cnst.TBLS.roles: {
            dataList = roles;
            break;
        }
        case Cnst.TBLS.statusProcesses: {
            dataList = statusProcesses;
            break;
        }
        case Cnst.TBLS.typeProcesses: {
            dataList = typeProcesses.getTypesProcesses();
            break;
        }
        case Cnst.TBLS.variables: {
            dataList = variables;
            break;
        }

        }

        for (int i = 0; i < dataList.size(); i++) {
            WorkflowObject wfObj = (WorkflowObject) dataList.get(i);
            if (wfObj.getId() == id) {
                data = wfObj.getData(field);
                break;
            }
        }

        return data;
    }

    /**
     * Получить идентификатор этапа, зная его наименование
     * 
     * @return res
     */
    public synchronized Long getIdStageByDescription(String description,
            int idTypeProcess) {

        for (int i = 0; i < stages.size(); i++) {
            WorkflowStages workflowStages = stages.get(i);
            if (workflowStages.getNameStage().trim().equalsIgnoreCase(description.trim())
                    && workflowStages.getIdTypeProcess() == idTypeProcess) {
                return workflowStages.getIdStage();
            }
        }

        return null;

    }

    /**
     * Получить идентификатор типа процесса, зная его наименование
     * 
     * @return res
     */
    public synchronized Integer getIdTypeProcessByDescription(String description) {

        for (int i = 0; i < typeProcesses.getTypesProcesses().size(); i++) {
            WorkflowTypeProcess workflowTypeProcess = typeProcesses.getTypesProcesses().get(i);
            if (workflowTypeProcess.getNameTypeProcess().equalsIgnoreCase(description)) {
                return workflowTypeProcess.getIdTypeProcess();
            }
        }

        /*
         * int count = typeProcesses.size();
         * 
         * for (int i = 0; i < count; i++) { Map st = (Map)
         * typeProcesses.get(i); if (((String)
         * st.get("DESCRIPTION_PROCESS")).equalsIgnoreCase(description)) return
         * (String) st.get("ID_TYPE_PROCESS"); }
         */

        return null;

    }


    /**
     * Получить имя типа процесса, зная его id
     * Непонятно, зачем тут synchroniaed. Менять же пракчтически не будем. Только считываем... 
     * @return res
     */
    public synchronized WorkflowTypeProcess getTypeProcessById(int id) {
        for (int i = 0; i < typeProcesses.getTypesProcesses().size(); i++) {
            WorkflowTypeProcess workflowTypeProcess = typeProcesses.getTypesProcesses().get(i);
            if (workflowTypeProcess.getIdTypeProcess() == id) {
                return workflowTypeProcess;
            }
        }

        /*
         * int count = typeProcesses.size();
         * 
         * for (int i = 0; i < count; i++) { Map st = (Map)
         * typeProcesses.get(i); if (((String)
         * st.get("DESCRIPTION_PROCESS")).equalsIgnoreCase(description)) return
         * (String) st.get("ID_TYPE_PROCESS"); }
         */

        return null;

    }

    
    @SuppressWarnings("deprecation")
    private synchronized String setSchema() {
        String res = "ok";

        try {

            schemaMap = new HashMap<Integer, XmlDocument>();

            for (int i = 0; i < typeProcesses.getTypesProcesses().size(); i++) {
                WorkflowTypeProcess wftp = typeProcesses.getTypesProcesses()
                        .get(i);
                String schema = wftp.getSchema();
                InputStream stream = new StringInputStream(schema);
                XmlDocument doc = XmlDocument.createXmlDocument(stream, false);
                schemaMap.put(wftp.getIdTypeProcess(), doc);

            }

        } catch (IOException e) {
            e.printStackTrace();
            res = "IO error";
        } catch (SAXException e) {
            e.printStackTrace();
            res = "SAX error";
        }

        return res;

    }

    private synchronized String setSchemaImageMap() {
        String res = "ok";

        schemaImageMap = new HashMap<Integer, String>();

        for (int i = 0; i < typeProcesses.getTypesProcesses().size(); i++) {
            WorkflowTypeProcess wftp = typeProcesses.getTypesProcesses().get(i);
            String schema = wftp.getSchemaImage();
            if (schema != null && !schema.equals("")) {
                schemaImageMap.put(wftp.getIdTypeProcess(), schema);
            }

        }

        return res;
    }

    public synchronized Map<Integer, XmlDocument> getSchemaMap() {
        return schemaMap;
    }

    public synchronized Long getIdVariableByDescription(String attrName,
            int idTypeProcess) {

        for (int i = 0; i < variables.size(); i++) {
            WorkflowVariables wfVar = variables.get(i);
            if (wfVar.getNameVariable().equalsIgnoreCase(attrName)
                    && wfVar.getIdTypeProcess() == idTypeProcess) {
                return wfVar.getIdVariable();
            }
        }

        return null;

    }

    // получить список объектов WorkflowRoles доступных для просмота
    // пользователя
    @SuppressWarnings("unchecked")
    public List<WorkflowRoles> getRoles(Long idUser) {

        List<WorkflowRoles> res = new ArrayList<WorkflowRoles>();
        for (int i = 0; i < getTypeProcessesList(idUser).size(); i++) {
            WorkflowTypeProcess workflowTypeProcess = getTypeProcessesList(
                    idUser).get(i);
            Integer idTP = workflowTypeProcess.getIdTypeProcess();
            List usersInTP = usersInTypeProcess.get(idTP);

            if (usersInTP.contains(idUser)) {

                for (int j = 0; j < rolesInTypeProcess.get(idTP).size(); j++) {
                    Long idRole = rolesInTypeProcess.get(idTP).get(j);
                    WorkflowRoles wfRole = findRole(idRole);
                    if (!res.contains(wfRole) && wfRole.isActive()) {
                        res.add(wfRole);
                    }
                }

            }
        }

        Collections.sort(res, new WFObjectComparator());
        return res;
    }

    /*
     * public List getStagesSortByName() {
     * 
     * List res = new ArrayList();
     * 
     * List nameStagesList = new ArrayList(); for (WorkflowStages wfSt : stages) {
     * nameStagesList.add(wfSt.getNameStage()); }
     * 
     * Object[] nameStagesArray = nameStagesList.toArray();
     * Arrays.sort(nameStagesArray); for (Object o : nameStagesArray) { String
     * idStage = getIdStageByDescription((String) o); Map map = new HashMap();
     * map.put("id", idStage); map.put("name", o); res.add(map); }
     * 
     * return res; }
     */

    /**
     * @param idTypeProcess
     * @param nameParameter
     * @return значение параметра процесса
     */
    public synchronized String getProcessParameter(Integer idTypeProcess,
            String nameParameter) {

        for (int i = 0; i < processParameters.size(); i++) {
            WorkflowProcessParameters wfPp = processParameters.get(i);
            Integer id_type_process = wfPp.getIdTypeProcess();
            String type_parameter = wfPp.getNameParameter();
            if (id_type_process != null && type_parameter != null) {
                if (id_type_process.intValue() == idTypeProcess.intValue()) {
                    if (type_parameter.equalsIgnoreCase(nameParameter)) {
                        return wfPp.getValue();
                    }

                }

            }
        }

        return "";
    }

    public String getProcessParameterForGroup(List typesProcesses,
            String nameParameter) {

        for (int i = 0; i < typesProcesses.size(); i++) {
            WorkflowTypeProcess workflowTypeProcess = (WorkflowTypeProcess) typesProcesses
                    .get(i);
            String res = getProcessParameter(workflowTypeProcess
                    .getIdTypeProcess(), nameParameter);
            if (res != null && !res.equals("")) {
                return res;
            }

        }

        return null;

    }

    public synchronized UsersManager getUsersMgr() {
        return usersMgr;
    }

    // получить карту пользователей, доступных для пользователя idUser
    @SuppressWarnings("unchecked")
    public Map<Long, WorkflowUser> getWorkflowUsers(Long idUser) {

        Map<Long, WorkflowUser> res = new HashMap<Long, WorkflowUser>();
        WorkflowUser user = null;
        for (int i = 0; i < typeProcesses.getTypesProcesses().size(); i++) {
            WorkflowTypeProcess tp = typeProcesses.getTypesProcesses().get(i);
            int idTP = tp.getIdTypeProcess();
            List<Long> usersInTP = usersInTypeProcess.get(idTP);
            if (usersInTP.contains(idUser)) {
                for (int j = 0; j < usersInTP.size(); j++) {
                    Long id = usersInTP.get(j);
                    if (!res.containsKey(id)) {
                        user = usersMgr.getInfoUserByIdUser(id);
                        if (user != null)
                            res.put(id, user);
                        else
                            LOGGER.warning("WorkflowUser is null by id = " + id);
                    }
                }
            }

        }

        return res;
    }

    public WorkflowTypeProcessList getTypeProcessesList() {
        return typeProcesses;
    }

    /** получить список процессов, доступных для пользователя user
     * @param idUser - номер пользователя
     * @return - список типов процессов
     */
    @SuppressWarnings("unchecked")
    public List<WorkflowTypeProcess> getTypeProcessesList(Long idUser) {

        List<WorkflowTypeProcess> res = new ArrayList<WorkflowTypeProcess>();
        for (int i = 0; i < typeProcesses.getTypesProcesses().size(); i++) {
            WorkflowTypeProcess tP = typeProcesses.getTypesProcesses().get(i);
            int id = tP.getIdTypeProcess();
            if (wpc.getUsersInTypeProcess().containsKey(id)&&wpc.getUsersInTypeProcess().get(id).contains(idUser)) {
                res.add(tP);
            }
        }

        Collections.sort(res, new WFObjectComparator());
        return res;
    }

    /**
     * Вернуть список активных этапов
     * 
     * @return
     */
    public List<WorkflowStages> getStages() {
        List<WorkflowStages> res = new ArrayList<WorkflowStages>();

        for (int i = 0; i < stages.size(); i++) {
            WorkflowStages st = stages.get(i);
            if (st.isActive()) {
                res.add(st);
            }

        }

        return res;
    }

    // получить список этапов, доступных для пользователя user
    @SuppressWarnings("unchecked")
    public List<WorkflowStages> getStages(Long idUser) {

        List<WorkflowStages> res = new ArrayList<WorkflowStages>();

        for (int i = 0; i < getTypeProcessesList(idUser).size(); i++) {
            WorkflowTypeProcess workflowTypeProcess = getTypeProcessesList(
                    idUser).get(i);
            Integer idTP = workflowTypeProcess.getIdTypeProcess();
            List usersInTP = usersInTypeProcess.get(idTP);

            if (usersInTP.contains(idUser)) {

                for (int j = 0; j < stagesInTypeProcess.get(idTP).size(); j++) {
                    Long idStage = stagesInTypeProcess.get(idTP).get(j);
                    WorkflowStages wfStage = findStage(idStage);
                    if (!res.contains(wfStage) && wfStage.isActive()) {
                        res.add(wfStage);
                    }
                }

            }
        }

        Collections.sort(res, new WFObjectComparator());

        return res;
    }

    public WorkflowStages findStage(Long idStage) {
        for (int i = 0; i < stages.size(); i++) {
            WorkflowStages workflowStages = stages.get(i);
            if (workflowStages.getIdStage() == idStage) {
                return workflowStages;
            }
        }

        return null;
    }

    public List<WorkflowRoles> getRoles() {

        List<WorkflowRoles> res = new ArrayList<WorkflowRoles>();
        for (int i = 0; i < roles.size(); i++) {
            WorkflowRoles st = roles.get(i);
            if (st.isActive()) {
                res.add(st);
            }

        }

        return res;
    }

    public WorkflowRoles findRole(Long idRole) {
        for (int i = 0; i < roles.size(); i++) {
            WorkflowRoles workflowRoles = roles.get(i);
            if (workflowRoles.getIdRole() == idRole) {
                return workflowRoles;
            }
        }
        return null;
    }

    public List<WorkflowStatusProcess> getStatusProcesses() {
        return statusProcesses;
    }

    public List<WorkflowVariables> getVariables() {
        return variables;
    }

    public List<WorkflowProcessParameters> getProcessParameters() {
        return processParameters;
    }

    public String getRoleName(Long idRole) {

        for (int i = 0; i < roles.size(); i++) {
            WorkflowRoles workflowRoles = roles.get(i);
            if (workflowRoles.getIdRole() == idRole) {
                return workflowRoles.getNameRole();
            }
        }
        return "";
    }

    public Map<Integer, List<Long>> getStagesInTypeProcess() {
        return stagesInTypeProcess;
    }

    public Map<Integer, List<Long>> getRolesInTypeProcess() {
        return rolesInTypeProcess;
    }

    public Map<Integer, List<Long>> getUsersInTypeProcess() {
        return usersInTypeProcess;
    }
    
    /**
     * @return
     */
    public int getLimitDayProcess(int idTypeProcess) {
        return BusinessProcessDecider
                .getLimitDays(schemaMap.get(idTypeProcess));
    }

    public Integer getIdTypeProcessByIdRole(Long idRole) {

        Iterator<Integer> it = rolesInTypeProcess.keySet().iterator();
        while (it.hasNext()) {
            Integer idTp = it.next();
            if (rolesInTypeProcess.get(idTp).contains(idRole)) {
                return idTp;
            }
        }

        return null;

    }

    /**
     * @return the schemaImageMap
     */
    public Map<Integer, String> getSchemaImageMap() {
        return schemaImageMap;
    }

    public boolean isExistImage(int idTypeProcess) {

        if (getSchemaImageMap().get(idTypeProcess) != null) {
            return true;
        }
        return false;

    }

    public String convertIdStageListToNamesStageList(String value) {

        if (value == null || value.equals("")) {
            return "";
        }

        StringTokenizer tok = new StringTokenizer(value, ",");
        StringBuffer sb = new StringBuffer();
        while (tok.hasMoreTokens()) {
            String id = tok.nextToken().trim();
            String name = (String) getData(Cnst.TBLS.stages, Long.valueOf(id),
                    Cnst.TStages.name);
            sb.append(name).append(",  ");
        }

        String toString = sb.toString();
        return toString.substring(0, toString.length() - 3);
    }

    /**
     * @return the directVars
     */
    public List<String> getDirectVars() {
        return directVars;
    }

    /**
     * @param directVars
     *            the directVars to set
     */
    public void setDirectVars(List<String> directVars) {
        this.directVars = directVars;
    }

    public List<Long> getUsersInStages(Integer idTypeProcess, String stages) {
        List<Long> res = new ArrayList<Long>();
        StringTokenizer stok = new StringTokenizer(stages, ",");

        Set<Long> usersSet = new HashSet<Long>();
        while (stok.hasMoreTokens()) {
            String nameStage = stok.nextToken();
            Long idStage = getIdStageByDescription(nameStage, idTypeProcess
                    .intValue());
            List<Long> users = getUsersInStage(idStage);
            usersSet.addAll(users);

        }

        res.addAll(usersSet);
        Collections.sort(res);
        return res;
    }

    public List<Long> getUsersInStage(Long idStage) {
        CompendiumActionProcessor compendium = (CompendiumActionProcessor) 
            ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("Compendium");
        List<Long> res = new ArrayList<Long>();

        Iterator<Long> it = stagesInRole.keySet().iterator();

        while (it.hasNext()) {

            Long idRole = it.next();
            if (stagesInRole.get(idRole).contains(idStage)) {
                //ищем пользователей для этой роли
                List<User> users = compendium.findActiveUserInActiveRolePage(null, 
                        new Role(idRole.intValue()), 0, 9000, null).getList();
                for(User user : users){
                    if (!res.contains(new Long(user.getId()))){
                        res.add(user.getId().longValue());
                    }
                }
            }
        }
        return res;
    }

    public WorkflowVariables findVariableByName(String name, int idTypeProcess) {

        for (int i = 0; i < variables.size(); i++) {
            WorkflowVariables v = variables.get(i);
            if (v.getIdTypeProcess() == idTypeProcess
                    && v.getNameVariable().equals(name)) {
                return v;
            }
        }
        return null;
    }

    public boolean isVariableDirectVar(String nameVar) {

        if (getDirectVars().contains(nameVar)) {
            return true;
        }
        return false;

    }

    public String formatDateTimeDBToDateTime(String date) {
        String res = "";

        try {
            Date d = dateTimeDBFormat.parse(date);
            res = dateTimeFormat.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return res;

    }

    public String formatDateTimeDBToDate(String date) {
        String res = "";

        try {
            Date d = dateTimeDBFormat.parse(date);
            res = dateFormat.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return res;
    }

    public String formatDateTimeToDateTimeDB(String date) {
        String res = "";

        try {
            Date d = dateTimeFormat.parse(date);
            res = dateTimeDBFormat.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return res;
    }

    /**
     * Форматировать формат даты dd.MM.yyyy в формат базы данных приближая
     * слева. (01.01.2000 -> 2000-01-01 00:00:00)
     * 
     * @param date
     * @return
     */
    public String formatDateToDateTimeDBLeft(String date) {
        String res = "";

        try {
            Date d = dateFormat.parse(date);
            res = dateTimeDBFormat.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return res;
    }

    /**
     * Форматировать формат даты dd.MM.yyyy в формат базы данных приближая
     * справа. (01.01.2000 -> 2000-01-01 59:59:59)
     * 
     * @param date
     * @return
     */
    public String formatDateToDateTimeDBRight(String date) {
        String res = "";

        try {
            Date d = dateFormat.parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            cal.add(Calendar.DAY_OF_MONTH, 1);
            cal.add(Calendar.MILLISECOND, -1);
            res = dateTimeDBFormat.format(cal.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return res;
    }

    public ProcessControlType getControlType(Integer idTypeProcess) {

        XmlDocument document = schemaMap.get(idTypeProcess);
        String typeStr = BusinessProcessDecider.getControlType(document);
        ProcessControlType pti = new ProcessControlType(ProcessControlType.NONE);

        if (typeStr.equalsIgnoreCase("disableUpdateForActive")) {
            pti.value = ProcessControlType.DISABLE_FOR_ACTIVE;
        }
        if (typeStr.equalsIgnoreCase("disableUpdateForAll")) {
            pti.value = ProcessControlType.DISABLE_FOR_ALL;
        }
        if (typeStr.equalsIgnoreCase("onlyMessageForActive")) {
            pti.value = ProcessControlType.INFORM_FOR_ACTIVE;
        }
        if (typeStr.equalsIgnoreCase("onlyMessageForAll")) {
            pti.value = ProcessControlType.INFORM_FOR_ALL;
        }

        return pti;

    }

    public WorkflowTypeProcess findTypeProcessByName(String nameProcess) {

        for (WorkflowTypeProcess wtp : getInstance().typeProcesses
                .getTypesProcesses()) {
            if (wtp.getNameTypeProcess().equals(nameProcess)) {
                return wtp;
            }
        }
        return null;
    }

    public WorkflowSubProcess findSubProcess(int idTypeProcess,
            int idTypeProcessParent) {
        for (WorkflowSubProcess sp : getInstance().subProcesses) {
            if (sp.getIdTypeProcess() == idTypeProcess
                    && sp.getIdParentTypeProcess() == idTypeProcessParent) {
                return sp;
            }
        }
        return null;

    }

    public VarsPermissions getRolesPermissions(Long idRole) {
        PupFacadeLocal pupFacadeLocal;
        try {
            pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        } catch (FactoryException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
        return toVarsPermissions(pupFacadeLocal.getRolesPermissions(idRole));
    }

    /**
     * @param perm
     * @return
     */
    private VarsPermissions toVarsPermissions(HashMap<Long, HashSet<Long>> perm) {
        VarsPermissions vp = new VarsPermissions();
        for(Long idVar : perm.keySet()){
            HashSet<Permission> p = new HashSet<VarsPermissions.Permission>();
            for(Long idPerm : perm.get(idVar)){
                p.add(getPermissionByID(idPerm));
            }
            vp.getVarPermissions().put(idVar, p);
        }
        return vp;
    }

    /**
     * @param p
     * @param idPerm
     */
    private Permission getPermissionByID(Long idPerm) {
        switch (idPerm.intValue()) {
            case 0:
                return new Permission(Permission.NOT);
            case 1:
                return new Permission(Permission.VIEW_ADDITION);
            case 2:
                return new Permission(Permission.VIEW_MAIN);
            case 3:
                return new Permission(Permission.EDIT);
        }
        return new Permission(Permission.NOT); 
    }

    public VarsPermissions getStagesPermissions(Long idStage) {
        PupFacadeLocal pupFacadeLocal;
        try {
            pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        } catch (FactoryException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
        return toVarsPermissions(pupFacadeLocal.getStagesPermissions(idStage));
    }

    /**
     * Найти переменную процесса по идентификатору
     * 
     * @param idVar
     * @return
     */
    public WorkflowVariables findVariableById(Long idVar) {

        for (WorkflowVariables v : variables) {
            if (v.getIdVariable().equals(idVar)) {
                return v;
            }
        }
        return null;
    }

    /**
     * Cфомировать список структур атрибутов (иерархию) из списка атрибутов
     * 
     * @param reqAttrs
     * @return
     */
    public List<BasicAttribute> createAttributesStructList(
            List<Attribute> reqAttrs) {

        List<BasicAttribute> res = new ArrayList<BasicAttribute>();

        for (Attribute a : reqAttrs) {
            Long idAttr = a.getId();
            MGraph<Long> grapfForAttr = attributesGraph.getSubMgraph(idAttr);
            HashMap<Long, Attribute> mapStructAttributes = getMapStructAttributes(
                    idAttr, reqAttrs);
            AttributeStruct struct = new AttributeStruct(mapStructAttributes,
                    grapfForAttr, idAttr);

            res.add(struct);
        }

        return res;

    }

    private HashMap<Long, Attribute> getMapStructAttributes(Long idAttr,
            List<Attribute> reqAttrs) {

        HashMap<Long, Attribute> res = new HashMap<Long, Attribute>();

        List<Long> listCh = varNodes.get(idAttr);
        if (listCh != null) {
            for (Long idCh : listCh) {
                res.put(idCh, findAttributeInList(idCh, reqAttrs));
            }
        }

        res.put(idAttr, findAttributeInList(idAttr, reqAttrs));
        return res;

    }

    private Attribute findAttributeInList(Long idCh, List<Attribute> reqAttrs) {
        for (Attribute a : reqAttrs) {
            if (a.getId().equals(idCh)) {
                return a;
            }
        }
        return null;
    }

    public Map<Long, List<Long>> getVarConnections() {
        return varConnections;
    }

    public Map<Long, List<Long>> getVarNodes() {
        return varNodes;
    }

    public List<WorkflowVariables> getVariablesForTypeProcess(
            Integer idTypeProcess) {
        List<WorkflowVariables> res = new ArrayList<WorkflowVariables>();
        for (WorkflowVariables v : variables) {
            if (v.getIdTypeProcess().equals(idTypeProcess)) {
                res.add(v);
            }
        }
        return res;
    }

    /**
     *  Получим список пользователей ТОГО же подразделения, что и ПОЛЬЗОВАТЕЛЬ, которые могут  
     *  выполнять ДАННУЮ операцию
     *  @param idStage  -- id этапа
     *  @param mgrId  -- id пользователя
     *  @return  users  -- список пользователей
     */
    public List<WorkflowUser> getAssignableForStageUsersByDepartment(Long mgrId, Long stageId) {
        
        // Получим список пользователей, которых может назначить пользователь на выполнение данного этапа 
        // ТОЛЬКО подчиненные и ТОЛЬКО из того же подразделения
        List<WorkflowUser> users = getUsersToAssign(mgrId, stageId);

        // MK -- 27 ОКТ 2009. ВООбще-то код ниже -- излишний, но пусть остается как задумано неизвестными авторами, удалять не буду

        // id департамента, к которому принадлежит пользователь
        Long mgrDepartmentId = getUsersMgr().getInfoUserByIdUser(mgrId).getDepartament().getId(); 
        
        // отфильтруем список пользователей, оставив только пользователей ТОГО же департамента, что и текущий пользователь
        ListIterator<WorkflowUser> lit = users.listIterator();
        while (lit.hasNext()) {
            WorkflowUser user = lit.next();
            if (user.getDepartament().getId().longValue() != mgrDepartmentId.longValue()) {
                lit.remove();
            }
        }
        return users;
    }
    
    /**
     * получить список пользователей, которых имеет право назначать на
     * исполнение пользователь idUser. Из списка исключаются пользователи, не
     * имеющие доступ на этап idStage и пользователи других департаментов
     * @param idUser
     * @param idStage
     * @return
     */
    public List<WorkflowUser> getUsersToAssign(Long idUser, Long idStage) {
        List<WorkflowUser> res = new ArrayList<WorkflowUser>();
        // роли руководителя
        List<Long> rolesRuk = getIDRolesForUser(idUser);
        for (Long idRole : rolesRuk) {
            List<Long> childRoles = rolesNodes.get(idRole);
            if (childRoles != null) {
                for (Long idRoleCh : childRoles) {
                    List<Long> users = getIDUserForRole(idRoleCh);
                    for (Long user : users){
                        if (!user.equals(idUser)//не равен самому себе (самому себе нельзя назначть)
                                && isPermissionForStage(user, idRoleCh,
                                idStage)){
                            WorkflowUser workflowUser = usersMgr.getInfoUserByIdUser(user);
                            if (!res.contains(workflowUser)
                                    && workflowUser != null) {
                                //проверяем активность
                            	UserJPA u = null;
                                try {
                                	PupFacadeLocal pupFacade = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
                                	u = pupFacade.getUserByLogin(workflowUser.getLogin());
								} catch (Exception e) {
									LOGGER.log(Level.SEVERE, e.getMessage(), e);
								}
                                if (u != null && u.isActive()) {
                                    res.add(workflowUser);
                                }
                            }
                        }
                    }
                }
            }
        }
        usersMgr.getWorkflowUsers().getSortedWFUserList(res,
                new TypeComparator(TypeComparator.name));
        return res;
    }

    public List<WorkflowUser> getUsersToAssign(String idUser) {
        List<WorkflowUser> res = new ArrayList<WorkflowUser>();
        CompendiumActionProcessor compendium = (CompendiumActionProcessor) 
            ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("Compendium");
        // роли руководителя
        List<Long> rolesRuk = getIDRolesForUser(new Long(idUser));

        for (Long idRole : rolesRuk) {
            List<Long> childRoles = rolesNodes.get(idRole);

            if (childRoles != null) {

                for (Long idRoleCh : childRoles) {
                    List<User> users = compendium.findActiveUserInActiveRolePage(null, new Role(idRoleCh.intValue()), 
                            0, 9000, null).getList();
                    for (User user : users){
                        if (!user.getId().toString().equals(idUser)){
                            //не равен самому себе (самому себе нельзя назначть)
                            WorkflowUser workflowUser = usersMgr.getInfoUserByIdUser(user.getId().longValue());
                            if (!res.contains(workflowUser)
                                    && workflowUser != null) {
                                res.add(workflowUser);
                            }
                        }
                    }
                }
            }

        }

        usersMgr.getWorkflowUsers().getSortedWFUserList(res,
                new TypeComparator(TypeComparator.name));

        return res;

    }

    // имеет ли пользователь idUserFind доступ на операцию idStage через роль
    // idRoleCh
    private boolean isPermissionForStage(Long idUserFind, Long idRoleCh,
            Long idStage) {

        // for (Long idRole: rolesInUsers.get(idUserFind)) {

        List<Long> stages = stagesInRole.get(idRoleCh);
        if (stages != null && stages.contains(idStage)) {
            return true;
            // }
        }

        return false;
    }

    /**
     *  Получим список ролей, которых может назначить исполнителем ПОЛЬЗОВАТЕЛЬ на ДАННОМ этапе 
     *  т.е. для каждой роли, находящейся НИЖЕ роли данного пользователя в иерархии ролей, 
     *  могущих выполнить ДАННЫЙ этап
     *  @param idStage  -- id этапа
     *  @param idUser  -- id пользователя
     *  @return  res  -- список ролей
     */
    public List<Long> getRolesToAssign(long idStage, Long idUser) {
        List<Long> res = new ArrayList<Long>();
        
        // список ролей, который выполняет данный пользователь
        List<Long> rolesParent = getIDRolesForUser(idUser);

        // список подчиненных ролей тем ролям, которые играет пользователь
        List<Long> resRolesParent = new ArrayList<Long>();

        for (Long idRole : rolesParent) {

            //получим список ролей, подчиненных ролям, которые играет пользователь  
            List<Long> childRoles = rolesNodes.get(idRole);
            
            // и добавляем его в список всех подчиненных ролей
            if (childRoles != null) {
                for (Long idRoleCh : childRoles) {
                    resRolesParent.add(idRoleCh);
                }
            }
        }
        
        // отфильтруем список получившихся ролей, выбрав только те, которые участвуют на данном этапе
        for (Long idRole : resRolesParent) {
            List<Long> stages = stagesInRole.get(idRole);
            if (stages != null) {
                if (stages.contains(idStage)) {
                    res.add(idRole);
                }
            }
        }

        return res;
    }
    
    public List<WorkflowUser> getUsersForRole(Long idRole) {
        CompendiumActionProcessor compendium = (CompendiumActionProcessor) 
            ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("Compendium");
        List<User> users = compendium.findActiveUserInActiveRolePage(null, 
                new Role(idRole.intValue()), 0, 9000, null).getList();
        List<WorkflowUser> res = new ArrayList<WorkflowUser>();
        for(User user : users){
            WorkflowUser infoUser = usersMgr.getInfoUserByIdUser(user.getId().longValue());
            res.add(infoUser);
        }
        return res;
    }
    
    public List<WorkflowUser> getUsersForRole(Long idRole, Long idDepartment) {
        CompendiumActionProcessor compendium = (CompendiumActionProcessor) 
            ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("Compendium");
        User filter = new User();
        filter.setDepartmentID(idDepartment.intValue());
        List<User> users = compendium.findActiveUserInActiveRolePage(filter, 
                new Role(idRole.intValue()), 0, 9000, null).getList();
        List<WorkflowUser> res = new ArrayList<WorkflowUser>();
        for(User user : users){
            WorkflowUser infoUser = usersMgr.getInfoUserByIdUser(user.getId().longValue());
            res.add(infoUser);
        }
        return res;
    }
        

    public List<WorkflowDepartament> getDepartments() {
        return departments;
    }

    public Map<String, String> getDepartmentsHierarchy() {
        return departmentsHierarchy;
    }
    
    public WorkflowDepartament findDepartamentByName(String name) {
        
        for (WorkflowDepartament dep : departments) {
            if (dep.getName().equalsIgnoreCase(name)
                    || dep.getFullName().equalsIgnoreCase(name)) {
                return dep;
            }

        }
        return null;
    }
    
    
    /**
     * Получить найти департамент по id 
     * @param departmentId -- Id департамента 
     * @return WorkflowDepartament найденный департамент
     * TODO: оптимизировать для поиска (binarySearch или TreeList) 
     */
    public WorkflowDepartament findDepartmentById(Long departmentId) {
        for (WorkflowDepartament dep : departments) {
            if (dep.getId().equals(departmentId)) {
                return dep;
            }
        }
        return null;
    }

    /**
     * Получить имя департамента в иерархии (с отступом) по id 
     * @param departmentId -- Id департамента 
     * @return имя департамента 
     */
    public String findDepartmentHierarchyName(Long departmentId) {
        if (departmentsHierarchy.containsKey(String.valueOf(departmentId))) 
            return  departmentsHierarchy.get(String.valueOf(departmentId));
        else return "";
    }

    
    /**
     * Проверяет, есть ли хотя бы одна роль ДАННОГО пользователя, связанная с этапом и не имеющая родительской роли
     * Т.е. это ЕГО роль, а не других пользователей
     * 
     * @param idUser id пользователя
     * @param idStage id этапа
     * @return <code>false</code>, если хотя бы одна роль пользователя, связанная с этапом, не имеет родительские роли
     *         <code>true</code> в противном случае (если ВСЕ роли пользователя -- подчиненные, т.е. у них есть родитель)
     */
    public boolean hasParentRole(Long idUser, Long idStage) {
        //мой департамент
        Integer myDepartment = getDepartmentByUser(idUser);
        if(myDepartment==null)return false;
        //список всех ролей ДАННОГО пользователя
        //ArrayList<Long> roles = new ArrayList<Long>(getIDRolesForUser(idUser));
        ArrayList<Long> roles = getIDRolesForUser(idUser);      

        // список всех ролей для ДАННОГО этапа 
        ArrayList<Long> stageRoles = (ArrayList<Long>)getIDRolesByIDStage(idStage);
        
        if (roles == null || stageRoles == null || roles.size() == 0 || stageRoles.size() == 0) {
            LOGGER.warning("list of roles or list of roles by stage are null");         
            return false;
        }
        
        // список ролей ДАННОГО пользователя для ДАННОГО этапа
        roles.retainAll(stageRoles);
        roles.trimToSize();
        
        //поищем родителей полученных ролей 
        HashMap<Long, Boolean> rolesHasParents = new HashMap<Long, Boolean>();
                
        for (int i = 0; i < roles.size(); i++) {
            rolesHasParents.put(roles.get(i), false);
            
            Iterator<Entry<Long, List<Long>>> iterator = rolesNodes.entrySet().iterator();
            Entry<Long, List<Long>> entry = null;
            while (iterator.hasNext()) {
                entry = iterator.next();
                if (entry.getValue().contains(roles.get(i))) {
                    /*есть пользователь с такой ролью в департаменте?*/
                    ArrayList<Long> parentsCandidat = getIDUserForRole(entry.getKey());
                    for(Long parentid:parentsCandidat){
                        if(getUsersInDepartment().get(myDepartment).contains(parentid))
                            rolesHasParents.put(roles.get(i), true);
                    }
                }
            }
        }
        
        // если найдена хотя бы одна роль, для которой нет родителя, вернем false. А зачем?
        Iterator<Entry<Long, Boolean>> rolesIterator = rolesHasParents.entrySet().iterator();
        Entry<Long, Boolean> rolesEntry = null;
        while (rolesIterator.hasNext()) {
            rolesEntry = rolesIterator.next();
            if (rolesEntry.getValue().equals(false)) {                  
                return false;
            }
        }
        return true;
    }

    /**
     * @param idUser
     * @return
     */
    private Integer getDepartmentByUser(Long idUser) {
        Integer myDepartment = null;
        Iterator<Entry<Integer, List<Long>>> depiterator = getUsersInDepartment().entrySet().iterator();
        while (depiterator.hasNext()){
            Entry<Integer, List<Long>> e = depiterator.next();
            if(e.getValue().contains(idUser))
                myDepartment = e.getKey();
        }
        return myDepartment;
    }
    
    
    /**
     *  Является ли наш пользователь начальником или подчиненным в контексте выполнения операции? 
     *  Получим список ролей, которые выполняет пользователь на операции. 
     *  Если для какой-либо из таких ролей есть подчиненные роли (не обязательно выполняющие работу на данной операции),
     *  то он -- начальник 
     *  В противном случае -- у него нет подчиненных
     *  @param idStage  -- id этапа
     *  @param idUser  -- id пользователя
     *  @return  true of false -- список ролей
     */
    public boolean hasChildRolesForOperation(Long idUser, Long idStage) {       
        
        // список ВСЕХ ролей, который выполняет данный пользователь
        ArrayList<Long> userRoles = getIDRolesForUser(idUser);

        // список всех ролей для ДАННОГО этапа 
        ArrayList<Long> stageRoles = (ArrayList<Long>)getIDRolesByIDStage(idStage);

        // получим список ролей данного ПОЛЬЗОВАТЕЛЯ, которые могут выполнять операции на данном этапе
        userRoles.retainAll(stageRoles);        
                
        //поиск ролей пользователя на данной операции, для которых СУЩЕСТВУЮТ подчиненные роли (необязательно эти роли
        //должны иметь возможность работать на данной операции). Просто убедиться, что в этом контексте пользователь -- начальник
        //или не имеет подчиненных (то есть выбираем те иерархии ролей, которые что-то выполняют на данной операции [может быть, и не все.
        // члены данной иерархии]
        for (Long idRole : userRoles) {
            //получим список ролей, подчиненных этой роли  
            List<Long> childRoles = rolesNodes.get(idRole);
                    
            if (childRoles != null)  { 
                // отлично. Наш пользователь -- один из начальников начальник в данной иерархии
                return true;            
            }
        }
        // нет таких ролей. Пользователь -- либо подчиненный во всех данных иерархиях, либо сам по себе (иерархия состоит из него одного)       
        return false;       
    }

    
    /**
     * Получает список id ролей по id этапа
     * 
     * @param idStage id этапа
     * @return список id ролей
     */
    public List<Long> getIDRolesByIDStage(Long idStage) {   
        ArrayList<Long> roles = new ArrayList<Long>();
        Iterator<Entry<Long, List<Long>>> iterator = stagesInRole.entrySet().iterator();
        Entry<Long, List<Long>> entry = null;
        while (iterator.hasNext()) {
            entry = iterator.next();
            if (entry.getValue().contains(idStage)) {
                roles.add(entry.getKey());
            }
        }   
        
        roles.trimToSize();
        return roles;
    }

    public Map<Long, List<Long>> getStagesInRole() {
        return stagesInRole;
    }

    /**
     * Установить [для пользователя idUser] перечень ВСЕХ департаментов для вывода в jsp-странице [INCLUDE_ALL]
     * либо только дочерних департаментов [INCLUDE_SUBORDINATE]
     * либо никаких [INCLUDE_NONE](тогда обычно дается includeSelf) 
     * В первом случае (для всех департаментов) добавить для департаментов слова 'Все департаменты' [includeAllWords = true]
     * либо нет. 
     * Во втором случае (для подчиненных департаментов) добавить для департаментов слова 'Все департаменты' [includeAllWords = true]
     * либо нет.
     * В последнем случае [когда включаем только дочерних] -- включить сам департамент, в котором 
     * работает пользователь [includeSelf = true], либо нет [includeSelf = false].
     * @param includeFlag  -- установить перечень всех департаментов, подчиненных департаментов или никаких подразделений
     * @param includeAllWords включать (true) или не включать слова "все департаменты" (в случае, если includeFlag = true)
     * @param includeSelf -- добавить в список само подразделение, к которому принадлежит пользователь, или нет.
     * @param idUser  -- id пользователя, для которого получаем список департаментов.
     * 
     * TODO: позднее аккуратно слить с процедурой setDepartmensToRequest для выбора департамента [там возвращается только один департамент]. 
     */
    public static Map<String, String> setDepartmentsForUser(Integer includeFlag, boolean includeAllWords, boolean includeSelf, Long idUser) throws NoSuchDepartmentException, RemoteException {
        
        Set<Long> childrenDepartments = null; 
        WorkflowDepartament currentDepartment = null;
        
        WPC wpc = getInstance();
        Map<String, String> departments = null;
        Map<String, String> departmentMap = null;               
    
        // Получим список ВСЕХ департаментов 
        if ( (includeFlag == WPC.INCLUDE_ALL) || (includeFlag == WPC.INCLUDE_SUBORDINATE)) {                    
            departments = wpc.getDepartmentsHierarchy();
            departmentMap = new LinkedHashMap<String, String>(departments.size() + 2);              
        }
        else
        {
            departments = new LinkedHashMap<String, String>();
            departmentMap = new LinkedHashMap<String, String>();
        }
        
        // получим множество дочерних подразделений
        if ((includeFlag == WPC.INCLUDE_SUBORDINATE) || (includeFlag == WPC.INCLUDE_NONE) )  {
    
            //получение подразделения нашего пользователя.
            try{
                WorkflowUser userInfo = getInstance().getUsersMgr().getInfoUserByIdUser(idUser); // wsc.getCurrentUserInfo();         
                currentDepartment = userInfo.getDepartament();
            }catch(Exception e)
            { // по каким-то причинам не смогли найти пользователя либо его подразделение
              currentDepartment = null;
            }
            // Получим список всех дочерних подразделений 
            if ((currentDepartment !=null) && (includeFlag == WPC.INCLUDE_SUBORDINATE)) 
                childrenDepartments = wpc.getAllChildrenOfDeparment( currentDepartment.getIdDepartament());
        }
        
        // поместим в результат строку "Все департаменты"
        if (includeAllWords && ((includeFlag == WPC.INCLUDE_ALL) || (includeFlag == WPC.INCLUDE_SUBORDINATE)))
            departmentMap.put("-1", "Все подразделения");

        // добавим подразделение, в котором работает пользователь
        if ( ((includeFlag == WPC.INCLUDE_SUBORDINATE) || (includeFlag == WPC.INCLUDE_NONE)) && includeSelf && (currentDepartment !=null))  {
            departmentMap.put(currentDepartment.getIdDepartament().toString(), currentDepartment.getShortName());
        }

        // поместим департаменты (ВСЕ или только те, что есть в дочерних) в Map для отображения в списке в jsp-странице.
        Iterator<Entry<String, String>> it = departments.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, String> entry = it.next();
            // Если ВСЕ подразделения, то просто помещаем в список 
            if (includeFlag == WPC.INCLUDE_ALL) 
                departmentMap.put(entry.getKey(), entry.getValue());
            else
                if (includeFlag == WPC.INCLUDE_SUBORDINATE)
            // Если только дочерние подразделения, то проверим, что имеются в множестве дочерних подразделений
              if (currentDepartment !=null && (childrenDepartments != null))
                if (childrenDepartments.contains(Long.valueOf(entry.getKey()))) 
                    departmentMap.put(entry.getKey(), entry.getValue());            
        }
        return departmentMap;
    }

    /**
     * @return подчиненные пользователи
     */
    public Map<Long, ArrayList<Long>> getSubordinateUsers() {
        return subordinateUsers;
    }
    
    /** получить список идентификаторов ролей, доступных для пользователя*/
    public synchronized ArrayList<Long> getIDRolesForUser(Long idUser) {
        ArrayList<Long> res = new ArrayList<Long>();
        try {
            PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
            for (RoleJPA role : pupFacadeLocal.getUser(idUser).getRoles()){
                res.add(role.getIdRole());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return res;
    }

    /** получить список идентификаторов пользователей, назначенных на роль*/
    public synchronized ArrayList<Long> getIDUserForRole(Long idRole) {
        ArrayList<Long> res = new ArrayList<Long>();
        try {
            PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
            for (UserJPA user : pupFacadeLocal.getRole(idRole).getUsers()){
                res.add(user.getIdUser());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return res;
    }
    
    private Map<Integer, List<Long>> usersInDepartment = new LinkedHashMap<Integer, List<Long>>(); 
    // список принадлежности пользователей департаменту
    private long usersInDepartmentTime=0;
    public synchronized Map<Integer, List<Long>> getUsersInDepartment() {
        long now = System.currentTimeMillis();
        if(now-usersInDepartmentTime > hashTTL){
            usersInDepartment = new LinkedHashMap<Integer, List<Long>>();
        } else {return usersInDepartment;}
        try{
            CompendiumActionProcessor compendium = (CompendiumActionProcessor) 
                ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("Compendium");
            Department[] departments = compendium.getDepartmentListAll();
            for(Department department : departments){
                List<UserTO> users = compendium.findUsersInDepartment(department.getId());
                ArrayList<Long> userids = new ArrayList<Long>();
                for(UserTO user : users) userids.add(user.getVo().getId().longValue());
                usersInDepartment.put(department.getId(), userids);
            }
            usersInDepartmentTime = System.currentTimeMillis();
        }catch(Exception e){
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
        }
        return usersInDepartment;
    }
    
    private Map<Long, Task> mdtaskCache = new LinkedHashMap<Long, Task>();
    public synchronized void removeFromCacheTaskJDBC(Long idmdtask){
    	if(mdtaskCache.containsKey(idmdtask))
    		mdtaskCache.remove(idmdtask);
    }
    public synchronized void putTaskJDBC(Task mdtask){
    	if(mdtaskCache.size()>200)
    		mdtaskCache.clear();
    	removeFromCacheTaskJDBC(mdtask.getId_task());
    	mdtaskCache.put(mdtask.getId_task(), mdtask);
    }
    public synchronized Task getTaskJDBC(Long idmdtask){
    	if(mdtaskCache.containsKey(idmdtask))
    		return mdtaskCache.get(idmdtask);
    	return null;
    }
}
