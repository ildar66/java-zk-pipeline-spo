package ru.md.spo.ejb;

import com.vtb.domain.ProcessSearchParam;
import com.vtb.domain.TaskListType;
import com.vtb.domain.WorkflowTaskInfo;
import com.vtb.exception.FactoryException;
import com.vtb.exception.MappingException;
import com.vtb.exception.ModelException;
import ru.md.pup.dbobjects.*;
import ru.md.spo.dbobjects.StandardPeriodGroupJPA;
import ru.md.spo.report.Expertus;
import ru.md.spo.report.Statistic;

import javax.ejb.Local;
import java.net.MalformedURLException;
import java.sql.Date;
import java.util.*;

/**
 * Движек бизнес-процесса.
 *
 * @author Andrey Pavlenko
 */
@Local
public interface PupFacadeLocal {
    /**
     * Проверяет прошла ли заявка этапа с названием. То есть эта операция завершена
     *
     * @param idPupProcess - id процесса
     * @param stageName    название этапа
     */
    boolean isPassStage(Long idPupProcess, String stageName);

    boolean isAdmin(Long isUser);

    Long getNextMdTaskNumber();

    List<Long> findDepartmentUsersInRoles(Long roleId, Long depId);

    DocumentTypeJPA getDocumentType(Long id);

    DocumentGroupJPA getDocumentGroup(Long id);

    /*
     * Проверяет активна ли связь между группой документов и типом
     */
    boolean isDocumentGroupTypeActive(Long groupId, Long typeId);

    List<DocumentGroupJPA> findDocumentGroupByOwnerTYpe(Long ownerType);

    /**
     * Утверждает документ.
     *
     * @param idUser - кто утверждает
     * @param unid   - номер документа
     * @return unid сгенеренного файла, если есть. Иначе null
     */
    String acceptAttachment(Long idUser, String unid, String sign) throws MappingException ;
    void signAttachment(Long idUser, String unid, String sign) throws MappingException ;
    void generateAcceptReport(String originalDocUNID, String newDocUNID, Long idUser) throws MappingException;

    void attachment4cc(String unid, boolean FORCC,Long idMdtask);

    /**
     * Удаляет документ.
     *
     * @param unid - номер документа
     */
    void deleteAttachment(String unid, Long idUser, String reason);

    Long findAttachemntCountByOwnerAndType(String idOwner, Long ownerType);

    List<AttachJPA> findAttachemnt(String idOwner, Long ownerType, Long docGroupID, Long docTypeID);

    List<AttachJPA> findOtherAttachemnt(String idOwner, Long ownerType);

    List<AttachJPA> findAttachemntByOwnerAndType(String idOwner, Long ownerType);

    List<AttachJPA> findAttachemnt(String idOwner, Long ownerType, Long docGroupID, Long docTypeID, boolean showOnlyNotExpired);

    List<AttachJPA> findOtherAttachemnt(String idOwner, Long ownerType, boolean showOnlyNotExpired);

    AttachJPA getAttachemnt(String unid);

    /**
     * Найти удаленные документы по заявке
     */
    List<AttachJPA> findDelAttachemntByOwnerAndType(String idOwner, Long ownerType);

    /**
     * Возвращает информацию о задаче по её номеру.
     *
     * @param taskid
     * @return
     */
    WorkflowTaskInfo getTaskInfo(long taskid);

    /**
     * Получить одно значение атрибута ПУП.
     */
    String getPUPAttributeValue(Long idProcess, String nameVar);

    boolean getPUPAttributeBooleanValue(Long idProcess, String nameVar);

    /**
     * Получить список атрибутов.
     */
    List<String> getAttributeList(Long idProcessType);

    String getExpertHtmlReport(Long mdtaskId) throws Exception;

    /**
     * Получить список атрибутов "L_".
     */
    List<String> getL_AttributeList(Long idProcessType);

    /**
     * Обновить значение атрибута ПУП.
     */
    void updatePUPAttribute(long idProcess, String nameVar, String valueVar);

    /**
     * Получить список идентификаторов заданий id_task для пользователя id_user
     * по фильтрам
     */
    ArrayList<Long> getWorkList(Long id_user, TaskListType typeList, ProcessSearchParam processSearchParam, Long count, Long start);

    Long getWorkListCount(Long id_user, TaskListType typeList, ProcessSearchParam processSearchParam);

    /**
     * Получить список назначений заданий для пользователя по определенному
     * процессу.
     *
     * @param idProcessType - тип процесса. Если null, то по всем
     */
    ArrayList<AssignJPA> getAssignToUsersTasksList(Long idBoss, Long idProcessType);

    /**
     * Получить список назначенных пользователей, у которых эта операция в назначенных мне.
     *
     * @param idTask - номер операции
     * @return список пользователей
     */
    HashMap<UserJPA, List<RoleJPA>> getAssignedUser(Long idTask);

    /**
     * Возвращает список подчиненных, которых пользователь userid пожет назначить на этапе stageid.
     *
     * @param userid
     * @param stageid
     */
    List<UserJPA> getUser4assign(Long stageid, Long userid);

    /**
     * Возвращает список пользователей, которых можно включить в экспертую команду
     *
     * @param expname  - название экспертизы. По ней можно узнать название операции и роли пользователей.
     * @param mdtaskid - id заявки. По нему можно узнать тип БП.
     * @return список пользователей
     */
    List<UserJPA> getUserExpertUser(String expname, Long mdtaskid) throws Exception;

    /**
     * Список типов процесса, по которому у пользователя есть хотя бы одна роль.
     */
    Set<ProcessTypeJPA> getProcessTypeForUser(Long idUser, Boolean isRunProcess);

    /**
     * Список типов процесса, по которому пользователь может созадть заявку.
     */
    Set<ProcessTypeJPA> getStartProcessType(Long idUser);

    /**
     * Список ID типов процесса, по которому у пользователя есть хотя бы одна роль.
     */
    Set<Long> getIdProcessTypeForUser(Long idUser);

    /**
     * получить список подчиненных.
     *
     * @param idBoss
     */
    Set<UserJPA> getSlave(Long idBoss, Long idProcessType);

    DepartmentJPA getDepartmentByName(String name);

    DepartmentJPA getDepartmentById(Long id);

    RoleJPA getRole(Long idRole);

    RoleJPA getRole(String nameRole, Long idProcessType);

    UserJPA getUserByLogin(String login) throws Exception;

    UserJPA getUser(Long idUser);

    /**
     * Назначить.
     *
     * @param auto - автоназначение. Не проверяем права.
     */
    void assign(Long idUser, Long idRole, Long idProcess, Long idWhoAssign, boolean auto) throws Exception;

    void assign(Long idUser, Long idRole, Long idProcess, Long idWhoAssign) throws Exception;

    /**
     * Проверяет можно ли редактировать атрибут attrname на операции pupTaskId.
     */
    boolean isPermissionEdit(Long pupTaskId, String attrname);

    /**
     * Возвращает назначение по номеру.
     */
    AssignJPA getAssignbyId(Long idAssign);

    ProcessTypeJPA getProcessTypeById(Long id);

    ProcessJPA getProcessById(Long id);

    List<ProcessTypeJPA> findProcessTypeList();

    /**
     * Проверяет назначен ли пользователь.
     *
     * @param idUser    - пользователь
     * @param idRole    - роль
     * @param idProcess - заявка
     * @return
     */
    boolean isAssigned(Long idUser, Long idRole, Long idProcess);

    boolean isAssigned(Long idRole, Long idProcess);

    boolean isCurrentUserInProjectTeam(Long idMdtask);

    boolean currentUserAssignedAs(String roleName, Long idProcess);

    String whoAssignedAs(String roleName, Long idProcess);

    void deleteAssign(String nameRole, Long idProcess, Long who);

    boolean userAssignedAs(Long idUser, String roleName, Long idProcess);

    /**
     * завершена ли обработка сделки в рамках бизнес-процесса «Оформление выдачи» по Сделке
     */
    boolean isCedEnded(Long idMdtask);

    /**
     * Есть ли заявки на КОД для данной сделки
     */
    boolean isHasCed(Long idMdtask);

    /**
     * Создает бизнес-процесс и назначает первые задачи пользователю в работу.
     *
     * @param id_type_process тип БП
     * @param idUser          пользователь, от имени которого создается процесс.
     * @return id_process
     */
    Long createProcess(Long id_type_process, Long idUser) throws ModelException;

    /**
     * Возвращает список первых этапов для типа процесса.
     *
     * @param idTypeProcess
     */
    List<StageJPA> getFirstStages(Long idTypeProcess);

    /**
     * Возвращает список начальников по иерархии во всех департаментах
     *
     * @param childId       {@link Long идентификатор} {@link UserJPA пользователя}
     * @param idProcessType {@link Long идентификатор} {@link ProcessJPA процесса}
     * @return ошибка
     */
    Set<UserJPA> getParentList(Long childId, Long idProcessType);

    /**
     * Возвращает список акцептов по идентификатору пользователя
     *
     * @param userId {@link Long идентификатор пользователя}
     * @return {@link List список} {@link AcceptJPA акцептов}
     */
    List<AcceptJPA> getAcceptList(Long userId);

    /**
     * Возвращает список акцептов по идентификатору пользователя
     *
     * @param userId        {@link Long идентификатор пользователя}
     * @param startPosition {@link int страница пейджинга}
     * @param maxResult     {@link int количество записей на странице}
     * @return
     */
    List<AcceptJPA> getAcceptList(Long userId, int startPosition, int maxResult);

    /**
     * Возвращает расмер списка акцептов по идентификатору пользователя
     *
     * @param userId {@link Long идентификатор пользователя}
     * @return {@link Long размер списка}
     */
    Long getAcceptListSize(Long userId);

    /**
     * Возвращает {@link TaskInfoJPA операцию} по {@link Long идентификатору}
     *
     * @param taskId {@link Long идентификатор}
     * @return {@link TaskInfoJPA операцию}
     */
    TaskInfoJPA getTask(Long taskId);

    int getTaskStatus(Long taskId);

    /**
     * Возвращает список операций в работе для данной заявки по pupId.
     */
    List<TaskInfoJPA> getTaskInWork(Long processId);

    /**
     * Удаляет акцепты по {@link Long идентификатору} {@link TaskInfoJPA
     * операции}
     *
     * @param taskId {@link Long идентификатор} {@link TaskInfoJPA операции}
     */
    void deleteAcceptList(Long taskId);

    /**
     * Метод осуществляет слияние данных персистентного объекта
     *
     * @param entity энтити
     */
    void merge(Object entity);

    /**
     * Метод сохранение персистентного объекта
     *
     * @param entity энтити
     */
    void persist(Object entity);

    /**
     * Возвращает список акцептов по идентификатору операции и идентификатору
     * пользователя
     *
     * @param taskId {@link Long идентификатор} {@link TaskInfoJPA операции}
     * @param userId {@link Long идентификатор} {@link UserJPA пользователя}
     * @return {@link List список} {@link AcceptJPA акцептов}
     */
    List<AcceptJPA> getAcceptList(Long taskId, Long userId);

    /**
     * Возвращает список утвержденных акцептов операции
     *
     * @param taskId {@link Long идентификатор} {@link TaskInfoJPA операции}
     * @return {@link List список} {@link AcceptJPA акцептов}
     */
    List<AcceptJPA> getApprovedAcceptList(Long taskId);

    /**
     * Получить список идентификаторов процессов для пользователя id_user.
     *
     * @param id_user            - пользователь
     * @param id_department      id департамента. Если null - то получает список всех
     *                           департаментов
     * @param processSearchParam -  фильтрация по параметрам
     * @return массив id process. В mdtask это pup_process
     */
    ArrayList<Long> getProcessList(Long id_user, Integer id_department, ProcessSearchParam processSearchParam);

    Long getProcessListCount(Long id_user, Integer id_department, ProcessSearchParam processSearchParam);

    Long getQueryPageNumber(Long id_user, Integer id_department, ProcessSearchParam processSearchParam, Long idPupProcess, Long idTask, String typeList);

    ArrayList<Long> getProcessList(Long id_user, Integer id_department, ProcessSearchParam processSearchParam, Long count, Long start);

    /**
     * Get list of task ids of tasks which should have been completed at chosen date 'date' and have the status 'status'
     */
    List<Long> findPlannedCompletionOperations(int status, Date date);

    /**
     * Check whether USE_SA is used in configuration files.
     */
    boolean isUseSA();

    /**
     * Чтение свойств конфигурационного файла по ключу
     * Возвращает пустую строку, если не находит
     */
    String getConfigProperty(String propertyName);

    HashMap<Long, HashSet<Long>> getRolesPermissions(Long idRole);

    HashMap<Long, HashSet<Long>> getStagesPermissions(Long idStage);

    /**
     * Возвращает следующий номер для саблимита.
     *
     * @param idparent - idMdtask лимита.
     */
    Long getNextSublimitNumber(Long idparent);

    Long getLastSublimitId(Long idparent);

    /**
     * Взять в работу.
     */
    void acceptWork(Long idTask, Long idUser) throws Exception;

    /**
     * Отказаться от работы над заявкой.
     */
    void reacceptWork(Long idTask, Long idUser) throws Exception;

    /**
     * начать ветку формирования кредитного меморандума.
     *
     * @param idUser    - пользователь, который начинает. Ветка будет создана в его подразделении.
     * @param idProcess - заявка.
     */
    void startMemorandum(Long idUser, Long idProcess);

    /**
     * начать ветку экспертизы ПРР.
     *
     * @param idUser    - пользователь, который начинает.
     * @param idProcess - заявка.
     */
    String startPRR(Long idUser, Long idProcess);

    /**
     * ищет подразделения в которых есть живые пользователи.
     *
     * @param idProcessType - только для БП с этим айди.
     * @param roles         - только для таких ролей. Роли в одинарных кавычках через запятую.
     */
    List<DepartmentJPA> findDepartmentsForUser(Long idProcessType, String roles);

    /**
     * Завершает операции кредитного меморандума.
     */
    void closeMemorandum(Long idProcess, Long idUser);

    /**
     * Завершает заявку и все её операции.
     *
     * @throws FactoryException
     */
    void closeProcess(Long idProcess, Long idUser) throws FactoryException;

    /**
     * Возвращает операции для БП.
     */
    List<StageJPA> getStages(Long idProcessType);

    StageJPA getStage(Long id);

    /**
     * Создать первую версию нормативных сроков.
     */
    Long createStandardPeriodVersion(Long idProcessType);

    /**
     * Создать первую версию маршрутов БП.
     */
    Long createSpoRouteVersion(Long idProcessType);

    /**
     * закрепить заявку за актуальной версией нормативных сроков
     */
    void setStandardPeriodVersion(Long id_process);

    void resumeProcess(Long id_process, Long idUser, String cmnt);

    void pauseProcess(Long id_process, Long idUser, String cmnt, java.util.Date pauseDate);

    /**
     * @return базовый URL для FlexWorkFlow. Учитывает разные адреса для пользователей в ГО и филиалах
     * @throws MalformedURLException
     */
    String getBaseURL(Long userid) throws MalformedURLException;

    /**
     * Завершает операцию.
     * По традиции ижевцев, в качестве параметра принимаю ArrayList<Object[]>. Убил бы.
     */
    void completeWorks(ArrayList stNext, Long idTaskCompleted);

    void setTaskStatus(Long statusId, Long idTaskCompleted);

    Statistic getStatistic();

    /**
     * Отозвать назначение на экспертизу при завершении
     */
    void removeAssignOnExpertiseStage(Long idTaskCompleted, List<Long> nextStages);

    /**
     * проверяет что эта операция для этой заявки - экспертиза
     */
    boolean isExpertiseStage(Long idStage, Long idProcess);

    boolean isPipelineReadonly(Long idMdTask);

    /**
     * запускает таймер, который исправляет битые операции, у которых статус незавершенных, хотя они завершены.
     */
    void startTimer();

    public void notifyPrr(Long idProcess, String firstStageName, UserJPA user, Long idDep);

    /**
     * возвращает группу нормативных сроков для этапа
     */
    public StandardPeriodGroupJPA getGroup4Stage(Long idStage, Long idProcess);

    /**
     * Проверяет есть ли активные задачи на этой операции
     */
    boolean isHasActiveTask(Long idProcess, Long idStage);

    /**
     * Возвращает список операций в работе для данной заявки по pupId.
     */
    List<TaskInfoJPA> getTaskByProcessId(Long processId);

    /**
     * Возвращает данные для секции "Проведение экспертиз".
     * Требуется для сравнения версий заявок.
     */
    ArrayList<Expertus> getExpertReport(Long mdtaskId) throws FactoryException;

    /**
     * Возвращает инициатора процесса
     *
     * @param idProcess идентификатор процесса
     * @return инициатора процесса
     */
    UserJPA getProcessInitiator(Long idProcess);

    /**
     * Создает бизнес-процесс.
     *
     * @param id_type_process тип БП
     * @param idUser          пользователь, от имени которого создается процесс
     * @param acceptWork      назначать ли первые задачи пользователю в работу
     * @return id_process
     * @throws ModelException
     */
    Long createProcessWithAccept(Long id_type_process, Long idUser, Boolean acceptWork) throws ModelException;

    UserJPA getCurrentUser();

    String getAdditionVar(Long idTypeProcess, String nameVar);

    /**
     * Переносит назначения на роли и документы в заявку когда создаём БП по заявке без БП (который pipeline)
     */
    void convertPMandDoc(Long idOldProcess, Long idNewProcess);

    void updateCdVersion();
}
