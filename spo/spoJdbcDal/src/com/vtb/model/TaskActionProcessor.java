package com.vtb.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import ru.masterdm.compendium.domain.Currency;
import ru.masterdm.compendium.domain.MDCalcHistory;
import ru.masterdm.compendium.domain.crm.Organization;
import ru.masterdm.compendium.value.Page;
import ru.masterdm.flexworkflow.integration.list.ERatingType;
import ru.masterdm.integration.CCStatus;
import ru.md.domain.MdTask;

import com.vtb.domain.ApprovedRating;
import com.vtb.domain.CRMLimit;
import com.vtb.domain.Process6;
import com.vtb.domain.ProcessSearchParam;
import com.vtb.domain.Rating;
import com.vtb.domain.Task;
import com.vtb.domain.Task4Rating;
import com.vtb.domain.TaskProduct;
import com.vtb.domain.TaskVersion;
import com.vtb.exception.CantChooseProcessType;
import com.vtb.exception.FactoryException;
import com.vtb.exception.MappingException;
import com.vtb.exception.ModelException;
import com.vtb.exception.NoSuchObjectException;

/**
 * Интерфейс работы с Заявкой внутри СПО
 * 
 * @author Andrey Pavlenko
 */
public interface TaskActionProcessor {
    /**
     * запускает таймер, который переодически мониторит таблицу очереди CRM.
     */
    void startTimer(long timeInSecond);
	/**
	 * Возвращает {@link MDCalcHistory рейтинг} по номеру контрагента на дату
	 * 
	 * @param orgId
	 *            номер контрагента
	 * @param dt
	 *            дата, на которую рассчитан рейтинг. Если null, то на текущую.
	 * @return {@link MDCalcHistory рейтинг}
	 */
	MDCalcHistory getMDCalcHistory(String orgId, Date dt);
	 /**
     * Возвращает Рейтинг кредитного подразделения по номеру контрагента на текущую дату
     * @param orgId номер контрагента
     * @return {@link Rating рейтинг}
     */
	ru.masterdm.compendium.domain.crm.Rating getRating(String orgId);

	/**
	 * Сохранить рейтинг в crm.
	 * @param mdtaskid
	 */
	void exportRating2CRM(Long mdtaskid) throws Exception;
	/**
	 * Notification about changed status.
	 * 
	 * @param status
	 *            - {@link CCStatus} Credit committee mdtask status
	 * @param mdtaskid
	 *            - origonal mdtaskid (not clone)
	 * @throws Exception
	 */
	void statusNotification(ru.masterdm.integration.CCStatus status,
			Long mdtaskid) throws Exception;

	/**
	 * Обновить значение атрибута ПУП.
	 */
	public void updateAttribute(long idProcess, String nameVar, String valueVar);

	/**
	 * Получить одно значение атрибута ПУП.
	 */
	public String getAttributeValue(Long idProcess, String nameVar);

	/**
	 * Find a list of currencies chosen in the parent task.
	 * 
	 * @param parentTaskId
	 *            parent task identifier
	 * @throws NoSuchObjectException
	 */
	public ArrayList<Currency> findParentCurrency(Long parentTaskId)
			throws ModelException, NoSuchObjectException;

	/**
	 * сохраняет результат загрузки в журнал
	 * 
	 * @param crmid
	 *            - номер лимита или сделки
	 * @param i
	 *            - код успешности. 1 - успешно, 2 - ошибка
	 * @param message
	 *            - сообщение
	 * @throws ModelException
	 */
	public void crmlog(String crmid, int i, String message)
			throws ModelException;

	/**
	 * Список заявок из шестой версии СПО
	 * 
	 * @return
	 * @throws ModelException
	 */
	@SuppressWarnings("unchecked")
	public Page findSPO6List(int start, int count) throws ModelException;

	/**
	 * Ищет заявку из 6 ПУП по pupid
	 * 
	 * @param id
	 * @return
	 * @throws ModelException
	 */
	public Process6 findSPO6byId(Long id) throws ModelException;

	/**
	 * Проверяет загружен ли уже лимит из CRM.
	 * 
	 * @param crmid
	 * @return
	 * @throws ModelException
	 */
	public boolean isCRMLimitLoaded(String crmid) throws ModelException;

	/**
	 * Список заявок для пользователя
	 * 
	 * @param login
	 *            - логины пользователей в нижнем регистре через запятую
	 * @param start
	 *            - начиная с
	 * @param count
	 *            - количество
	 * @return страница лимитов
	 * @throws ModelException
	 */
	@SuppressWarnings("unchecked")
	public Page findCRMLimitByUser(ArrayList<Long> usersid, int start, int count) throws ModelException;

	/**
	 * Ищет сделки в CRM для пользователя с логином login.
	 * 
	 * @return список сделок из CRM
	 * @throws ModelException
	 */
	@SuppressWarnings("unchecked")
	public Page findCRMProductByUser(ArrayList<Long> usersid, int start, int count) throws ModelException;

	/**
	 * Ищет саблимиты в CRM.
	 * 
	 * @param limitid
	 *            - номер лимита
	 * @return список лимитов из CRM
	 * @throws ModelException
	 */
	public ArrayList<CRMLimit> findCRMSubLimit(String limitid) throws ModelException;

	/**
	 * Загружает лимит из CRM по его идентификатору.
	 * 
	 * @param limitid
	 * @return
	 * @throws ModelException
	 */
	public CRMLimit findCRMLimitById(String limitid) throws ModelException;

	/**
	 * Ищет назначенных пользователей для конкретного этапа конкретной заявки
	 * 
	 * @return HashMap список id,email пользователей
	 */
	public HashMap<Long, String> findAssignUser(Long idStage, Long idProcess) throws ModelException;

	/**
	 * Ищет пользователей для конкретного этапа конкретной заявки, у которых
	 * есть права взять её. Но кроме тех, которые по этой операции простые пользователи.
	 * Письмо от Князевой от 20.10.2014 с темой [СПО 16.34fix3]
	 * 
	 * @return HashMap список id,email пользователей
	 */
	public HashMap<Long, String> findUser(Long idStage, Long idDepartament) throws ModelException;

	/**
	 * Экспорт заявки в кредитные комитеты
	 * @param mdtaskid - номер заявки
	 */
	public void export2cc(Long mdtaskid, Long userid) throws ModelException;

	/**
	 * Получить выписку из протокола
	 */
	public byte[] getResolution(Long id_template,Long id_mdtask)
			throws ModelException;

	/**
	 * Список сублимитов
	 * @param mdtaskid - номер родителя сублимита
	 * @param all - вернуть все заявки, даже удаленные
     * @param full - вернуть ПОЛНУЮ информацию о заявке, или только основные параметры
	 */
	public ArrayList<Task> findTaskByParent(Long mdtaskid, boolean all, boolean full) throws ModelException;

	/**
	 * Создание <b>Заявки</b>
	 * 
	 * @param task
	 *            - заявка, которая должна создаться. У заявки должен быть
	 *            уникальный id или null, тогда новый идентификатор сгенерится
	 *            автоматически
	 * @throws MappingException
	 */
	public Task createTask(Task task) throws MappingException;
	/**
	 * Создание <b>Заявки</b> c БП по заявке без БП
	 *
	 * @param task
	 *            - заявка, которая должна создаться. У заявки должен быть
	 *            уникальный id или null, тогда новый идентификатор сгенерится
	 *            автоматически
	 * @throws MappingException
	 */
	public Task renewTask(Task task) throws MappingException;

	/**
	 * Обновление <b>Заявки</b>
	 * 
	 * @param task
	 *            - заявка, которая должна быть перезаписана в БД.
	 */
	public void updateTask(Task task) throws Exception;
	
	/**
	 * Обновляет данные операции и заявки.
	 * @param task операция
	 * @param mdTask заявка
	 */
	public void updateTask(Task task, MdTask mdTask);
	
	/**
     * Возвращает заявка с pipeline.
     * @param mdTaskId идентификатор заявки
     * @return заявка с pipeline
     */
	public MdTask getPipelineWithinMdTask(Long mdTaskId);

	/**
	 * Получение объекта <b>Заявка</b> по его идентификатору
	 * @param taskWithKeyValues
	 *            Объект <b>заявка</b> типа <code>Task</code> с заполненным
	 *            полем <code>id</code>
	 * @return Объект <b>заявка</b> типа <code>Task</code>
	 * @throws MappingException
	 */
	public Task getTask(Task taskWithKeyValues) throws MappingException;

	/**
     * Получение объекта <b>Заявка</b> по его идентификатору. В заявку не помещаются дополнительные данные из других систем (рейтинги, значения справочников). Только основное.
     * @param taskWithKeyValues
     *            Объект <b>заявка</b> типа <code>Task</code> с заполненным
     *            полем <code>id</code>
     * @return Объект <b>заявка</b> типа <code>Task</code>
     * @throws MappingException
     */
    public Task getTaskCore(Task taskWithKeyValues) throws MappingException;
    /**
     * Получение объекта <b>Заявка</b> по его идентификатору. В заявку помещаются дополнительные данные, требуемые для отчетов
     * @param taskWithKeyValues
     *            Объект <b>заявка</b> типа <code>Task</code> с заполненным
     *            полем <code>id</code>
     * @return Объект <b>заявка</b> типа <code>Task</code>
     * @throws MappingException
     */
    public Task getReportData(Task taskWithKeyValues) throws MappingException;
	
	/**
	 * Получение объекта <b>Заявка</b> по идентификатору процесса ПУП
	 * 
	 * @param pupProcessID
	 *            идентификатор процесса ПУП
	 * @param full
	 *            грузить полную заявку или облегченную
	 * @return Объект <b>заявка</b> типа <code>Task</code>
	 * @throws MappingException
	 */
	public Task findByPupID(Long pupProcessID, boolean full)
			throws MappingException;

	/**
	 * Удаление объекта <b>Заявка</b> по его идентификатору
	 * @param taskWithKeyValues
	 *            Объект <b>заявка</b> типа <code>Task</code> с заполненным
	 *            полем <code>id</code>
	 */
	public void deleteTask(Task taskWithKeyValues) throws MappingException;

	public String getReport(Task task, boolean xml, Integer reportid) throws Exception, ParserConfigurationException, IllegalAccessException;

	/**
	 * Проверяет права на редактирование на этапе idStage для аттрибута varname.
	 */
	public boolean isPermissionEdit(long idStage, String varname,
			Integer idTypeProcess) throws MappingException;

	/**
	 * Ищет заявку по crmid
	 */
	public Task findByCRMid(String crmid) throws ModelException;

	/**
	 * Ищет те заявки, которые ссылаются на это crmid (т.е. находит детей по этому crmid).
	 * @param crmid crm identifier
	 * @param full whether to fill all fields of Task or only the necessary ones
	 * @return List<Task> list of tasks
	 * @throws ModelException
	 */
	public List<Task> findChildrenOfCRMid(String crmid, boolean full) throws ModelException;

	/**
	 * Список заявок для списка отказ Клиента
	 */
	@SuppressWarnings("unchecked")
    public Page findRefusableTask(Long userid, Long start, Long count, ProcessSearchParam sp) throws ModelException;

	/**
	 * Ищет заинтересованных в судьбе заявки пользавателей это клиентские
	 * менеджеры и те, у кого она в работе
	 * @param mdtaskid
	 * @return emails
	 * @throws ModelException
	 */
	public ArrayList<Long> findAffiliatedUsers(Long mdtaskid) throws ModelException;

	/**
	 * Сохраняет версию заявки. (включая саблимиты).
	 */
	public void makeVersion(Long mdtaskid, Long idUser, String stageName, String roles) throws ModelException;

	/**
	 * Возвращает версии этого лимита, саблимита, сделки. Без самого отчета.
	 * 
	 * @param mdtaskid
	 *            номер лимита...
	 */
	public ArrayList<TaskVersion> findTaskVersion(Long mdtaskid) throws ModelException;

	/**
	 * возвращает версию (отчет)
	 * @param idversion
	 * @return версию (отчет)
	 * @throws ModelException
	 */
	public String getVersion(Long idversion) throws ModelException;

	/**
	 * Возвращает хешмап <номер роли, номер пользователя>
	 * @param id_pup_process
	 * @return
	 * @throws ModelException
	 */
	public HashMap<Long, Long> getProcessAssign(Long id_pup_process) throws ModelException;

	/**
	 * Возвращает хешмап <номер роли, название роли>
	 * @throws ModelException
	 */
	public HashMap<Long, String> getRoles2Assign(Long idUser, Long idTask) throws ModelException;

	/**
	 * Возвращает сделку по её номеру.
	 * @param mdtaskid
	 *            номер сделки
	 * @return сделку
	 * @throws Exception
	 */
	public Task4Rating getOpportunityInfo(Long mdtaskid) throws Exception;

	/**
	 * Возвращает 30 самых свежих сделок, в которых участвует данная организация.
	 * Если сделок больше 30, то отдаю только первые 30, остальных не будет! По просьбе Лиса.
	 * @param organizationid номер организации
	 * @return список сделок
	 * @throws Exception
	 */
	public ArrayList<Task4Rating> getListOpportunity(String organizationid) throws Exception;
	/**
	 * СУПЕР ЛЁГКИЙ вариант ф-и getListOpportunity для рейтингов (VTBSPO-1392).
	 */
	public ArrayList<Task4Rating> getListOpportunity(String organizationid, int startRow, int count) throws Exception;
	
	Long limitLoad(String limitid) throws ModelException, FactoryException, MappingException, CantChooseProcessType;
	Long productLoad(String id, Long userid, Long idProcessType) throws Exception, CantChooseProcessType;
	
	/**
	 * @param date
	 * @return
	 */
	ApprovedRating getApprovedRating(Date date, String orgid);
	
	/**
	 * Fills Organization with full corresponding data.
	 * @param crmId crmId with which search for organization 
	 */
	Organization getOrganizationFullData(String crmId);
	
	/**
	 * Список процессов на которые можно грузить сделку из CRM для этого пользователя и этой организации.
	 */
	Set<Long> getProcessTypeList(Organization org, Long userid) throws FactoryException, CantChooseProcessType;
	
    /**
     * Начитываем типы сделок в список
     * @param task
     * @return
     */
    public ArrayList<TaskProduct> readProductTypes(Task task);
    
    /**
     * Saves product types
     * @param task
     */
    public void saveProductTypes(Task task);
    
    /**
     * Saves product types
     * @param task
     */
    public void saveCurrencyList(Task task);

    /**
     * Saves targets (Цели  кредитования)
     * @param task
     */
    public void saveTarget(Task task);
    
    /**
     * Saves targets (Цели  кредитования)
     * @param task
     */
    public void saveSpecialOtherConditions(Task task);
    
    /**
     * Saves the most common parameters of the task 
     * @param task
     */
    public void saveParameters(Task task);

}
