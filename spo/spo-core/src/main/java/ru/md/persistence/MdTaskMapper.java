package ru.md.persistence;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ru.md.domain.Comission;
import ru.md.domain.Department;
import java.util.List;

import ru.md.domain.IndRate;
import ru.md.domain.MdTask;
import ru.md.domain.OtherGoal;
import ru.md.domain.ReportTemplate;
import ru.md.domain.TargetGroupLimit;
import ru.md.domain.User;
import ru.md.domain.dashboard.CCQuestion;
import ru.md.domain.percenthistory.DealPercentHistory;
import ru.md.domain.percenthistory.FactPercentHistory;
import ru.md.domain.percenthistory.IndrateHistory;

/**
 * Маппер работы с заявкой.
 * @author Andrey Pavlenko
 */
@Transactional(propagation = Propagation.SUPPORTS)
public interface MdTaskMapper {
	/**
	 * Возвращает {@link MdTask заявку} по ее {@link Long id}
	 *
	 * @param id {@link Long id} сделки
	 * @return {@link MdTask заявка}
	 */
	MdTask getById(Long id);
    MdTask getById4TaskList(Long id);
	/**
	 * Возвращает {@link List список} {@link ReportTemplate шаблонов отчета}
	 *
	 * @return {@link List список} {@link ReportTemplate шаблонов отчета}
	 */
	List<ReportTemplate> getReportTemplateList();

	/**
	 * Возвращает признак избранной заявки
	 * @param mdTaskId идентификатор заявки
	 * @param userId идентификатор пользователя
	 * @return <code>true</code> если заявка избранна пользователем
	 */
	boolean isFavorite(@Param("mdTaskId") Long mdTaskId, @Param("userId") Long userId);
    /**
     * Проверяет что нет более свежих одобренных версий заявки.
     * @param mdTaskId идентификатор заявки
     * @return <code>true</code> если нет более свежих одобренных версий заявки
     */
    boolean isLastApprovedVersion(@Param("mdTaskId") Long mdTaskId);


    /**
     * Добавление заявки в избранные если ее там нет, и наоборот.
     * @param mdTaskId идентификатор заявки
     * @param userId идентификатор пользователя
     */
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    void favoriteSwitcher(@Param("mdTaskId") Long mdTaskId, @Param("userId") Long userId);

    /**
     * Возвращает заявка с pipeline.
     * @param mdTaskId идентификатор заявки
     * @return заявка с pipeline
     */
    MdTask getPipelineWithinMdTask(@Param("mdTaskId") Long mdTaskId);

    /**
     * Обновление pipeline.
     * @param mdTask заявка
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    void updatePipeline(MdTask mdTask);

    /**
     * Обновление проекта.
     * @param mdTask заявка
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    void updateProject(MdTask mdTask);

    /**
     * Возвращает признак доступа "только просмотр" к секциям "Секция ПМ" и "Pipeline".
     * Пользователю, обладающему ролью «Продуктовый менеджер» с признаком «выполнение операции» в секции «Проектная команда»,
     * необходимо предоставить возможность после открытия на просмотр карточки заявки типа «Сделка»
     * редактировать поля, расположенные в секции «Продуктового менеджера» и сохранять эти изменения.
     * @param mdTaskId идентификатор заявки
     * @param userId идентификатор пользователя
     * @return <code>true</code> если доступ только на просмотр к секциям "Секция ПМ" и "Pipeline"
     */
    boolean isPipelineReadonly(@Param("mdTaskId") Long mdTaskId, @Param("userId") Long userId);

    /**
     * Возвращает заявку для отправки уведомлений при завершении операции.
     * @param number номер заявки
     * @param version версия заявки
     * @return заявка
     */
    MdTask getForOnWorkCompletesNotifications(@Param("number") Long number, @Param("version") Long version);

    /**
     * Изменяет перечень {@link OtherGoal целей кредитования} сделки
     *
     * @param mdTaskId id сделки
     * @param otherGoals перечень {@link OtherGoal целей кредитования} сделки
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    void updateOtherGoals(@Param("mdTaskId") Long mdTaskId, @Param("otherGoals") List<OtherGoal> otherGoals);
    
    /**
     * Возвращает {@link List список} {@link OtherGoal целей кредитования} сделки
     *
     * @param mdTaskId id сделки
     * @return {@link List список} {@link OtherGoal целей кредитования} сделки
     */
    List<OtherGoal> getOtherGoals(@Param("mdTaskId") Long mdTaskId);
    
    /**
     * Возвращает {@link List список} {@link TargetGroupLimit лимитов групп целевых назначений}
     *
     * @param mdTaskId id сделки
     * @return {@link List список} {@link TargetGroupLimit лимитов групп целевых назначений}
     */
    List<TargetGroupLimit> getTargetGroupLimits(@Param("mdTaskId") Long mdTaskId);
    
    /**
     * Изменяет {@link List список} {@link TargetGroupLimit лимитов групп целевых назначений} сделки
     *
     * @param mdTaskId id сделки
     * @param targetGroupLimits {@link List список} {@link TargetGroupLimit лимитов групп целевых назначений}
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    void updateTargetGroupLimits(@Param("mdTaskId") Long mdTaskId, @Param("targetGroupLimits") List<TargetGroupLimit> targetGroupLimits);

    /**
     * Возвращает {@link List список} {@link Comission комиссий} сделки
     *
     * @param mdTaskId id сделки
     * @return {@link List список} {@link Comission комиссий} сделки
     */
    List<Comission> getComissions(@Param("mdTaskId") Long mdTaskId);

	/**
	 * Возвращает {@link List список} {@link DealPercentHistory объектов хронологии изменения процентной ставки сделки}
	 *
	 * @param creditDealNumber {@link Long номер сделки}
	 * @param startDate {@link Date нижняя граница даты} внесения в хронологию
	 * @param endDate {@link Date верхняя граница даты} внесения в хронологию
	 * @param performerId {@link Long id} пользователя, который внес изменение
	 * @param departmentId {@link Long id} подразделения пользователя, который внес изменение
	 * @param isOnlyLatestChange {@link Boolean признак}, что только последнее изменение по сделке
	 * @return {@link List список} {@link DealPercentHistory объектов хронологии изменения процентной ставки сделки}
	 */
    @Transactional(propagation = Propagation.SUPPORTS)
	List<DealPercentHistory> getDealPercentHistories(@Param("creditDealNumber") Long creditDealNumber, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("performerId") Long performerId, @Param("departmentId") Long departmentId, @Param("isOnlyLatestChange") Boolean isOnlyLatestChange);

	/**
	 * Возвращает {@link List список} {@link DealPercentHistory объектов хронологии} изменения процентной ставки сделки, если процентная ставка еще не утверждена
	 *
	 * @param idCreditDeal {@link Long id} сделки
	 * @return {@link List список} {@link DealPercentHistory объектов хронологии изменения процентной ставки сделки}
	 */
    @Transactional(propagation = Propagation.SUPPORTS)
	List<DealPercentHistory> getNotConfirmedDealPercentHistories(@Param("idCreditDeal") Long idCreditDeal);

    /**
     * Создает копию процентной ставки заявки СПО для бизнес-процесса "Изменение процентной ставки" мониторинга. Заносит данные из одного id сделки в другой, еще пустой id сделки
     *
     * @param idCreditDeal {@link Long id} сделки
     * @param idNewCreditDeal {@link Long id} новой версии сделки
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	void createKmPercentVersion(@Param("idNewCreditDeal") Long idNewCreditDeal, @Param("idCreditDeal") Long idCreditDeal);

    /**
     * Создает {@link DealPercentHistory объект хронологии изменения процентной ставки сделки} по {@link Long id} сделки
     *
     * @param idCreditDeal {@link Long id} сделки
     * @param idPerformer {@link Long id} пользователя, который нажал на акцептовать/одобрить
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    void createDealPercentHistoryValue(@Param("idCreditDeal") Long idCreditDeal, @Param("idPerformer") Long idPerformer);

    /**
     * Возвращает {@link List список} {@link User пользователей}, участвовавших в хронологии изменения процентной ставки указанной по {@link Long номеру} сделки
     *
     * @param creditDealNumber {@link Long номер сделки}
     * @return {@link List список} {@link User пользователей}, участвовавших в хронологии изменения процентной ставки указанной по {@link Long номеру} сделки
     */
    List<User> getDealPercentHistoryUsers(@Param("creditDealNumber") Long creditDealNumber);

    /**
     * Возвращает {@link List список} {@link Department подразделений}, участвовавших в хронологии изменения процентной ставки указанной по {@link Long номеру} сделки
     *
     * @param creditDealNumber {@link Long номер сделки}
     * @return {@link List список} {@link Department подразделений}, участвовавших в хронологии изменения процентной ставки указанной по {@link Long номеру} сделки
     */
    List<Department> getDealPercentHistoryDepartments(@Param("creditDealNumber") Long creditDealNumber);

    /**
     * Возвращает {@link Long id} завершенной одобренной выдачи: такой что,
     * <pre>
     * - если в хронологии несколько периодов и системная дата попадает в период, то возвращаем {@link Long id} последней завершенной одобренной выдачи из периодов, куда попала системная дата
     * - если в хронологии несколько периодов и системная дата НЕ попала ни в один период, то возвращаем {@link Long id} последней завершенной одобренной выдачи, которая не попадает ни в один из периодов
     * - если в хронологии один период, то возвращаем {@link Long id} последней завершенной одобренной выдачи
     * </pre>
     *
     * @param creditDealNumber {@link Long номер} сделки
     * @param idMdTaskAudit {@link Long id} объекта хронологии процентной ставки общих данных сделки
     * @return {@link Long id} выдачи
     */
    Long getIdLastPaymentInPeriod(@Param("creditDealNumber") Long creditDealNumber, @Param("idMdTaskAudit") Long idMdTaskAudit);

    /**
     * Возвращает {@link IndrateHistory объект} хронологии изменения индикативной ставки
     *
     * @param idDealPayment {@link Long id} заявки на выдачу
     * @return {@link IndrateHistory объект} хронологии изменения индикативной ставки
     */
    IndrateHistory getIndrateHistoryByIdPayment(@Param("idDealPayment") Long idDealPayment);

    /**
     * Возвращает {@link IndrateHistory объект} хронологии изменения процентного периода
     *
     * @param idDealPayment {@link Long id} заявки на выдачу
     * @return {@link IndrateHistory объект} хронологии изменения процентного периода
     */
    FactPercentHistory getFactPercentHistoryByIdPayment(@Param("idDealPayment") Long idDealPayment);

    /**
     * Возвращает номер КС.
     * @param mdTaskId идентификатор сделки
     * @return номер КС
     */
    String getOficcialNumber(@Param("mdTaskId") Long mdTaskId);

	/**
	 * Возвращает {@link Long id} последней заявки СПО для КОД, для которой существует и одобрена версия СПО для СПО. Если не существует ни одной одобренной, то берется
	 * последняя одобренная версия СПО.
	 *
	 * Из постановки VTBCED-514:
	 * 
	 * <pre>
	 * Пусть у сделки есть три версии 1, 2, 3. Соотстввующие копии данных КОД обозначим 1' 2' и 3'
	 * Допустим нужно создать 3' - откуда копировать данные?
	 * 1) Берем одобренные версии сделки с меньшими номерами и ищем максимальную по которой есть копия данных в КОД. 
	 * Т.е. если 2 - одобрена и 2' существует, то из 2'. Иначе если 1 одобрена и 1' существует, берем из 1'
	 * 2) Если в 1) не нашлось одобренной версии с меньшим номером, для которой существует версия штрих, 
	 * то копируем из копии СПО по текущей версии (также как для первой версии сделки). 
	 * Т.е. например, если 1 и 2 не одобрены, то 3' копируется из 3. Также если например 1 и 2 одобрены, но по ним не успели создать копию КОД, 
	 * то 3' копируется из 3. При этом не играет роли, успели уже одобрить 3 или еще нет - т.к. могут быть и предварительные запросы КОД, по версии сделки, которая еще не одобрена.
	 * </pre>
	 *
	 * @param creditDealNumber номер сделки
	 * @param lessThenVersion номер сделки строго меньше, чем указанный. Необязательный параметр
	 * @return {@link Long id} последней заявки СПО для КОД, для которой существует и одобрена версия СПО для СПО. Если не существует ни одной одобренной, то берется
	 * последняя версия СПО.
	 */
	Long getLastCedConfirmedCreditDealId(@Param("creditDealNumber") Long creditDealNumber);
    /**
     * Копирует данные по заявки в новую копию.
     * @param sourceMdTaskId - оригинальная заявки
     * @param dstMdTaskId - новая заявка
     */
    void copyMyBatisTask(@Param("sourceMdTaskId") Long sourceMdTaskId, @Param("dstMdTaskId") Long dstMdTaskId);
    MdTask getLastCedConfirmedCreditDeal(@Param("creditDealNumber") Long creditDealNumber, @Param("lessThenVersion") Long lessThenVersion);

    HashSet<String> getSectionNotEmpty(@Param("mdTaskId") Long mdTaskId);
	

    Long getCedTaskId(@Param("mdTaskNumber") Long mdTaskNumber, @Param("version") Long version);
    
    /**
     * Возвращает {@link Long id} версии СПО для СПО по переданному {@link Long id} версии СПО для ССКО
     *
     * @param cedTaskId {@link Long id} версии СПО для ССКО
     * @return {@link Long id} версии СПО для СПО
     */
    Long getCppsTaskIdByCedTaskId(@Param("cedTaskId") Long cedTaskId);
    
    /**
     * Возвращает {@link Long id} версии СПО для ССКО по переданному {@link Long id} версии СПО для СПО
     *
     * @param cppsTaskId {@link Long id} версии СПО для СПО
     * @return {@link Long id} версии СПО для ССКО
     */
    Long getCedTaskIdByCppsTaskId(@Param("cppsTaskId") Long cppsTaskId);

    String getNumberAndVersion(@Param("mdTaskId") Long mdTaskId);

	Long getMdTaskNumberById(@Param("mdTaskId") Long mdTaskId);

	Date getStartDate(@Param("mdTaskId") Long mdTaskId);

	/**
	 * Возвращает {@link Long id} последней заявки СПО для СПО. Если одобренных нет, то возвращается {@link Long id} первой версии СПО для СПО
	 *
	 * @param creditDealNumber номер сделки
	 * @return {@link Long id} последней заявки СПО для СПО. Если одобренных нет, то возвращается {@link Long id} первой версии СПО для СПО
	 */
	Long getLastSpoConfirmedCreditDealId(@Param("creditDealNumber") Long creditDealNumber);

	Date getLastUpdateDate(@Param("mdTaskId") Long mdTaskId);
	/** возвращает индикативные ставки по заявке*/
	List<IndRate> getIndRatesByMdtask(@Param("mdTaskId") Long mdTaskId);

	CCQuestion getCCQuestion(@Param("mdTaskId") Long mdTaskId);
	void mergeCCQuestion(@Param("question") CCQuestion question, @Param("questionGroup") Long questionGroup);
	List<Long> getIdMdtaskByQuestionGroup(@Param("questionGroup") Long questionGroup);
	void delMdtask(@Param("mdTaskId") Long mdTaskId);
	void initQuestionGroup(@Param("mdTaskId") Long mdTaskId);
	void syncQuestionGroupTask(@Param("mdTaskId") Long mdTaskId);
	boolean isFORCC(@Param("mdTaskId") Long mdTaskId, @Param("unid") String unid);

	/**
	 * Возвращает список заявок, в которых есть контрагент. как созаемщик, залогодатель, поручитель, гарант.
	 * @param orgid
	 * @return
     */
	List<Long> getIdMdtaskByOrgId(@Param("orgid") String orgid);
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	void setCloseProbabilityByStatusName(@Param("mdTaskId") Long mdTaskId, @Param("status") String status);

	Long getCreateApplicationKzTasksCount(@Param("kzid") String kzid, @Param("type") String type);
	List<MdTask> getCreateApplicationKzTasks(@Param("kzid") String kzid, @Param("type") String type, @Param("page") Long page, @Param("userId") Long userId);
	Long getCreateApplicationGroupTasksCount(@Param("kzid") String kzid, @Param("type") String type);
	List<MdTask> getCreateApplicationGroupTasks(@Param("kzid") String kzid, @Param("type") String type, @Param("page") Long page, @Param("userId") Long userId);
}
