package ru.md.spo.ejb;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Local;
import javax.jws.WebParam;
import javax.servlet.ServletRequest;

import ru.md.pup.dbobjects.UserJPA;
import ru.md.spo.dbobjects.*;
import ru.md.spo.loader.TaskLine;

import com.vtb.domain.MainBorrowerChangeLog;
import com.vtb.domain.Task;
import com.vtb.domain.Trance;
import com.vtb.domain.integration.MdTask;
import com.vtb.exception.FactoryException;

/**
 * Интерфейс для работы с данными заявки.
 * @author Andrey Pavlenko
 */
@Local
public interface TaskFacadeLocal {
    /**
     * Сохранить версию заявки.
     * @param version - версия
     */
    void createVersion(TaskVersionJPA version);
    
    /**
     * Возвращает заявку по идентификатору.
     * @param mdtaskid
     * @return Заявка.
     */
    TaskJPA getTask(Long mdtaskid);
	MdTaskTO getTaskFull(Long mdtaskid);

    /**
     * Возвращает заявку по идентификатору ПУП.
     * @return Заявка.
     */
    TaskJPA getTaskByPupID(Long pupid);
    /*
     * Загружает строку для таблицы заявок из базы. Здесь очень важна производительность.
     */
    ArrayList<TaskLine> loadTaskLines(ArrayList<TaskLine> taskLineList, Long currentUserId) throws Exception;
    /**
     * Возвращает ПОСЛЕДНЮЮ ВЕРСИЮ заявки по номеру СПО.
     * @return Заявка.
     */
    TaskJPA getTaskByNumber(Long mdtask_number) throws Exception;
    /**
     * Refresh the state of the instance from the database, overwriting changes made to the entity, if any.
     */
    void refreshTask(Long id);
    /**
     * Получить параметр настройки по ключу.
     * @param key
     * @return
     */
    String getGlobalSetting(String key);
    /**
     * Метод осуществляет слияние данных персистентного объекта
     * 
     * @param entity
     *            энтити
     */
    void merge(Object entity);
    void remove(java.lang.Class cl, java.lang.Object key);
    void persist(Object entity);
    void removeProjectTeamJPA(Long id);
    void removeExpertTeamJPA(Long id);
    /**
     * Проверяет загружена ли сделка из crm.
     */
    boolean isOpportunityLoaded(String crmOpportunityId);
    /**
     * Возвращает id вида сделки по полному наименованию.
     */
    String getProductTypeIdByName(String name);
    /**
     * возвращает список уполномоченных лиц для бизнес процесса
     * @param processTypeId - номе бизнес процесса
     */
    List<AuthorizedPersonJPA> getAuthorizedPersonJPAList(Long processTypeId);
    void setAuthorizedPerson(Long mdtaskId, Long authorizedPersonId);

    /** возвращает список лимитов и сублимитов, в которых участвует контрагент */
    List<Long> findLimitByOrg(String orgid, String inn);

    
    void logRequest(RequestLogJPA log);
    
    /**
     * Returns list of RequestLogJPA connected to the given TaskJPA
     * @param idTask idMdTask
     * @return list of RequestLogJPA connected to the given TaskJPA
     */
    List<RequestLogJPA> getRequestLogList(Long idTask);

    /**
     * Builds list of parent limit data to check on the client
     * @param taskJPA for which task look for data  
     * @param task to which task adds found data
     * @return list of parent limit data as String
     */
    String findParentHash(TaskJPA taskJPA, Task copyTo);
    //справочники//////////////////////////////////////////////////////////////////////////////////////////////////////
    List<ProductTypeJPA> findProductType();
    List<FundDownJPA> findFundDown();
    List<CdAcredetivSourcePaymentJPA> findAcredetivSourcePayment();
    List<CdCreditTurnoverCriteriumJPA> findCdCreditTurnoverCriterium();
    List<CdCreditTurnoverPremiumJPA> findCdCreditTurnoverPremium();
    List<CdRiskpremiumJPA> findCdRiskpremium();
    List<CdPremiumTypeJPA> findRiskpremiumType(CdPremiumTypeJPA.Type type);
    /**Компенсирующий спрэд за фиксацию ставки*/
    List<StavspredJPA> findStavspredJPA(String cur, Long period);
    /**Срок запрета досрочного погашения*/
    List<DependingLoanJPA> findDependingLoan(String cur, Long period);
    /**штрафные санкции*/
    List<PunitiveMeasureJPA> findPunitiveMeasure(String sanction_type);

    /**
     * Получим полное наименование сублимита
     * @param idTask mdtask Id
     * @return полное наименование сублимита
     */
	String getNumberDisplayWithRoot(Long idTask);
	
	void savePeriodObKind(HashMap<Long,HashMap<Long,Double>> map);
	
	TaskJPA updateDataJPA(ServletRequest request, Long updatedTask) throws FactoryException;
	PipelineJPA getPipeline(Long mdtaskid);
	List<String> getPipelineFinTarget(Long mdtaskid);
	void updatePipeline(PipelineJPA pipeline, String[] pipeline_fin_target);
	
	Date getEdcLastUpdate(String ownerType, String ownerId);
	/**
	 * Синхронизирует признак удаления в таблице истории удалений и добавлений контрегентов для ССКО
	 * @param idMdtask
	 */
	void spoContractorSync(Long idMdtask);
	/**
	 * Возвращает список id сделок, в которых учавствует в любой роли контрагент с ID = organizationid 
	 * @param organizationid номер организации
	 */
	List<MdTaskNumber> getListOpportunityNumber(String organizationid);
	/**
	 * Возвращает аттрибуты сделки по её id 
	 * @param organizationid номер организации
	 */
	MdTask getOpportunityAttr(@WebParam(name = "mdtaskid") Long id);
	/**
	 * Возвращает вновь созданную версию заявки (это лишь пример использования. не работает) 
	 * @param mdtaskid идентификатор заявки
	 * @param idTypeProcess тип процесса
	 * @param idUser идентификатор пользователя
	 * @param oldRole прежняя роль в процессе
	 * @param newRole новая роль в процессе
	 * @param newUser пользователь на новой роли
	 * @param comment причина создания версии
	 * @return идентификатор созданной версии заявки
	 * @throws Exception 
	 */
	Long createTaskVersion(Long mdtaskid, Integer idTypeProcess, Long idUser,
			String oldRole, String newRole, UserJPA newUser, String comment, boolean asNextVersion) throws Exception;
	TaskJPA copyJpaFieldsToNewVersion(Long oldTaskId, Long newTaskId, Long idUser,
			String oldRole, String newRole, UserJPA newUser, ArrayList<Trance> trances);
	Task createDomainTaskVersion(Task task, Integer idTypeProcess, Long idUser,
			String comment, boolean asNextVersion, boolean needAssign) throws Exception;
	Task createDomainTaskVersion(Task task, Integer idTypeProcess, Long idUser,
			String comment, boolean needAssign, Long idParent, Long version) throws Exception;
	/**
	 *	Возвращает идентификатор последней одобренной версии заявки
	 * @param mdtaskNumber номер заявки
	 * @return идентификатор последней одобренной версии заявки
	 */
	Long findLastApprovedVersion(Long mdtaskNumber);
	/**
	 *	Возвращает идентификатор сублимита по номеру 
	 * @param parentId идентификатор родителя
	 * @param mdtaskNumber номер сублимита
	 * @return идентификатор сублимита
	 */
	Long findSublimit(Long parentId, Long mdtaskNumber);
	void logMainBorrowerChanged(Long idMdtask, Long userid, String oldorg, String neworg);
	List<MainBorrowerChangeLog> getMainBorrowerChangeLog(Long idMdtask);

	/**
	 * Получение идентификаторов версий сделки
	 * @param mdtaskNumber
	 */
	List<Long> getVersionIds(String mdtaskNumber);

	/**
	 * Получение версий сделки
	 * @param mdtaskNumber
	 */
	List<TaskJPA> getVersions(String mdtaskNumber);

	/**
	 * Получение версии сделки
	 * @param mdtaskNumber
	 */
	TaskJPA getFirstVersion(String mdtaskNumber);

    long getNextVal(String seqName);

    void clearKmDealPercentState(Long idMdtask);
    void updateKmDealPercentState(Long idMdtask, String state);
    Long createPriceConditionVersion(Long mdtaskid);

    /**
     * По нажатию "Акцепт" бизнес-процесса "Изменение процентной ставки" перенос данных в данные связанной заявки, запись в хронологию изменения процентной ставки
     *
     * @param mdtaskid {@link Long id} заявки
     * @return {@link Long id} заявки
     */
    Long approvePriceConditionVersionMonitoringAndCreatePercentHistory(Long mdtaskid);
}
