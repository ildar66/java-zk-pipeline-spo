package com.vtb.mapping;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.masterdm.compendium.value.Page;
import ru.masterdm.integration.CCStatus;

import com.vtb.domain.ProcessSearchParam;
import com.vtb.domain.ProjectTeamMember;
import com.vtb.domain.Task;
import com.vtb.domain.Task4Rating;
import com.vtb.domain.TaskProduct;
import com.vtb.domain.TaskVersion;
import com.vtb.exception.MappingException;
import com.vtb.exception.ModelException;

/**
 * маппер для заявок
 * Расширение интерфейса <code>{@link com.vtb.mapping.Mapper Mapper}</code>
 * @author Andrey Pavlenko
 */
public interface TaskMapper extends Mapper<Task> {
    /**
     * Обновляет наш кеш статуса КК.
     * @throws SQLException
     */
    public void updateCCStatus(CCStatus status, Long mdtaskid) throws SQLException;
    /**
     * unit test method.
     */
    CCStatus getStatus(Long mdtaskid) throws Exception;
    /**
     * Обновить значение атрибута ПУП.
     */
    public void updateAttribute(long idProcess, String nameVar, String valueVar);
    /**
     * Получить одно значение атрибута ПУП.
     */
    public String getAttributeValue(Long idProcess, String nameVar);
	/**
	 * XSLT для печатной формы заявки
	 * @return
	 */
	public String getXSLT(Integer templateid);
	/**
	 * Получить выписку из протокола
	 */
	public byte[] getResolution(Long id_template,Long id_mdtask);
	/**
	 * Проверяет загружен ли уже лимит из CRM.
	 * @param crmid
	 * @return
	 * @throws ModelException
	 * @throws RemoteException
	 */
	public boolean isCRMLimitLoaded(String crmid)  throws MappingException, SQLException;
	/**
	 * Выборка всех заявок. Фильтр по назначению <code>sender</code>
	 * 
	 * @param operator Оператор, для которого ищем заявки
	 * @param orderBy Список полей, подлещащих сортировке в SQL формате 
	 * @return Список заявок отфильтрованных по оператору <code>{@link java.util.ArrayList ArrayList}</code>
	 * @throws SQLException 
	 * @throws MappingException
	 */
	public ArrayList<Task> findByOperator(String operator, String orderBy) throws MappingException, SQLException;

	 /**
     * Найти заявку по номеру задачи ПУП
     * 
     * @param pupProcessID номер задачи ПУП
     * @return заявкa
     * @throws SQLException 
     * @throws MappingException
     */
    public Long findByPupID(Long pupProcessID) throws MappingException, SQLException;

    /**
	 * Проверяет права на редактирование на этапе idStage для аттрибута varname.
	 * @param idStage
	 * @param varname
	 * @return
	 * @throws MappingException
	 */
	public boolean isPermissionEdit(long idStage, String varname,Integer idTypeProcess) throws MappingException;
	
	/**
	 * Ищет id заявки по crmid
	 * @param crmid
	 * @return
	 * @throws MappingException
	 * @throws SQLException
	 */
	public Long findByCRMid(String crmid) throws MappingException,	SQLException;
	
	/**
     * Ищет те заявки, которые ссылаются на это crmid (т.е. находит детей по этому crmid).
     * @param crmid crm identifier
     * @return List<Long> id заявок (id_mdtask)
     * @throws MappingException
     * @throws SQLException
     */
    public List<Long> findChildrenOfCRMid(String crmid) throws MappingException, SQLException;
	
	/**
	 * Список заявок для списка отказ Клиента
	 */
    @SuppressWarnings("rawtypes")
    public Page findRefusableTask(Long userid, Long start, Long count, ProcessSearchParam sp) throws ModelException;
	
	/**
	 * Ищет заинтересованных в судьбе заявки пользавателей
	 * это клиентские менеджеры и те, у кого она в работе
	 * @param mdtaskid
	 * @return emails
	 * @throws ModelException
	 */
	public ArrayList<Long> findAffiliatedUsers(Long mdtaskid) throws ModelException;
	
	/**
	 * Ищет все заявки, где участвовал контрагент
	 * @param orgid
	 * @return
	 * @throws ModelException
	 */
	public ArrayList<Task> findTaskByOrganisation(String orgid, int startRow, int count) throws ModelException;
	/**
	 * Ищет все заявки, где участвовал контрагент
	 * @param orgid
	 * @return
	 * @throws ModelException
	 */
	public ArrayList<Long> findTaskByOrganisation(String orgid) throws ModelException;
	
	/**
	 * сохраняет в базу версию.
	 * @param idmdtask
	 * @param version
	 */
	public void createVersion(Long idmdtask,TaskVersion version);
	
	/**
	 * Возвращает версии этого лимита, саблимита, сделки. Без самого отчета.
	 * @param mdtaskid номер лимита...
	 */
	public ArrayList<TaskVersion> findTaskVersion(Long mdtaskid);
	
	/**
	 * возвращает версию (отчет)
	 * @param idversion
	 * @return версию (отчет)
	 * @throws ModelException
	 */
	public String getVersion(Long idversion);
	
	/**
	 * Получаем заявку, в которой заполненны только интересные рейтингам поля.
	 * @param mdtaskid - это mdtaskid
	 * @return заявка
	 */
	public Task4Rating getTask4Rating(Long mdtaskid);
	
	/**
	 * Возвращает хешмап <номер роли, номер пользователя>
	 * @param id_pup_process
	 */
	public HashMap<Long,Long> getProcessAssign(Long id_pup_process);
	
	/**
	 * Возвращает хешмап <номер роли, название роли>
	 * @throws ModelException
	 */
	public HashMap<Long,String> getRoles2Assign(Long idUser, Long idTask);
	
    /**
     * Читаем из базы данных секцию 'Проектная команда'. Только не всех, а с назначениями на роль (см. нижнюю часть скрипта)   
     * @param type имя роли, например, Структуратор
     * @return список пользователей с данной ролью, присутствующие в секции Проектная команда для данной заявки
     * Примечание: только с назначениями (assign). Сейчас это означает, что такой пользователь у нас будет только один.
     */    
    public ArrayList<ProjectTeamMember> readProjectTeam(Task task, String type);
    
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
    public void saveSpecialOtherConditions(Task task);

    /**
     * Saves the most common parameters of the task 
     * @param task
     */
    public void saveParameters(Task task);

}
