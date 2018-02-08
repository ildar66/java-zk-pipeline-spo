package ru.md.persistence;

import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ru.md.domain.BranchStatistic;
import ru.md.domain.Department;
import ru.md.domain.ReportSetting;
import ru.md.domain.dashboard.DashboardEvent;
import ru.md.domain.IndRate;
import ru.md.domain.SpoSumHistory;
import ru.md.domain.TaskTiming;
import ru.md.domain.dashboard.DetailReportRow;
import ru.md.domain.dashboard.MainReportRow;
import ru.md.domain.dashboard.PipelineTradingDesk;
import ru.md.domain.dashboard.Sum;
import ru.md.domain.dashboard.TaskListFilter;
import ru.md.domain.dashboard.TaskListParam;
import ru.md.domain.dashboard.TaskTypeStatus;
import ru.md.domain.dashboard.SpoClientReport;
import ru.md.domain.dashboard.TopReportRow;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Отчёты.
 * @author Andrey Pavlenko
 */
public interface DashboardMapper {

    /**
     * Пересчитывает заявки, по которым нет даных о создании в отчёте dashboard
     */
    List<DashboardEvent> getAllNewTask();

    List<Long> getAllLostTask();

    /**
     * Ошибочно заведенные заявки не относятся к Потерянным http://jira.masterdm.ru/browse/VTBSPO-1272
     */
    void fixClientRefused();

    Date getLostDate(Long idMdtask);

    /**
     * Возвращает старые сделки, у которых не отмечено в истории время 'Заключенные сделки'
     */
    List<DashboardEvent> getOldProduct2fix();

    /**
     * Возвращает старые сделки, у которых не отмечено в истории время 'Выданные ден. средства'
     */
    List<DashboardEvent> getOldProduct2trance();

    TaskTiming getTaskTiming(Long idMdtask);

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    void updateSpoSumHistory(SpoSumHistory history);

    List<DashboardEvent> getOldLimitStruct();

    List<DashboardEvent> getOldLimitExper();

    List<DashboardEvent> getOldLimitAccept();

    List<MainReportRow> getMainReport(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("taskType") String taskType,
                                      @Param("creditDocumentary") Integer creditDocumentary,
                                      @Param("tradingDesk") Set<PipelineTradingDesk> tradingDesk,
                                      @Param("tradingDeskOthers") boolean tradingDeskOthers,
                                      @Param("departments") Collection<? extends Department> departments);

    List<DetailReportRow> getDetailReport(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("taskType") String taskType,
                                          @Param("creditDocumentary") Integer creditDocumentary,
                                          @Param("tradingDesk") Set<PipelineTradingDesk> tradingDesk,
                                          @Param("tradingDeskOthers") boolean tradingDeskOthers,
                                          @Param("departments") Collection<? extends Department> departments, @Param("idStatus") Integer idStatus,
                                          @Param("branch") String branch);

    List<TopReportRow> getTopReport(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("taskType") String taskType,
                                    @Param("creditDocumentary") Integer creditDocumentary,
                                    @Param("tradingDesk") Set<PipelineTradingDesk> tradingDesk,
                                    @Param("tradingDeskOthers") boolean tradingDeskOthers,
                                    @Param("departments") Collection<? extends Department> departments,
                                    @Param("topLimit") int topLimit);

    List<TaskTypeStatus> getTaskTypeStatuses(@Param("taskType") String taskType);

    TaskTypeStatus getTaskTypeStatus(@Param("id") Long id);

    List<PipelineTradingDesk> getPipelineTradingDesk();

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    void clearOldClientReport();

    List<Long> getYesterdayTask(@Param("changeDate") Date changeDate);

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    void insertSpoClientReport(SpoClientReport report);

    List<Long> getTaskListPage(@Param("statusids") Long[] statusids, @Param("from") Date from, @Param("to") Date to,
                               @Param("filter") TaskListFilter filter, @Param("listParam") TaskListParam param, @Param("page") Long page);

    Long getTaskListCount(@Param("statusids") Long[] statusids, @Param("from") Date from, @Param("to") Date to,
                          @Param("filter") TaskListFilter filter, @Param("listParam") TaskListParam param);

    /**
     * Возвращает дату создания заявки.
     */
    Date getDealCreateDate(@Param("idMdtask") Long idMdtask);

    /**
     * Возвращает дату изменения заявки.
     */
    Date getDealChangeDate(@Param("idMdtask") Long idMdtask);

    /**
     * Возвращает фио исполнителя по заявке с заданной ролью.
     */
    String getDealTeamMemberByRoleName(@Param("idMdtask") Long idMdtask, @Param("roleName") String roleName);
    List<Sum> getTaskListSum(@Param("statusids") List<Integer> statusids, @Param("from") Date from, @Param("to") Date to,
                             @Param("listParam") TaskListParam param);
    List<Sum> getTaskListCedSum(@Param("statusid") Long statusid, @Param("from") Date from, @Param("to") Date to, @Param("listParam") TaskListParam param);

    /**
     * Возвращает список ставок.
     */
	List<IndRate> getRate(@Param("id") Long id);

    List<BranchStatistic> getBranchStatistic(@Param("statusids") Long[] statusids, @Param("from") Date from, @Param("to") Date to,
                                             @Param("filter") TaskListFilter filter, @Param("listParam") TaskListParam param);

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    void savePipelineSettings(@Param("idUser") Long idUser, @Param("setting") String setting);
    String getPipelineSettings(@Param("idUser") Long idUser);
    String getPipelineSettingsName(@Param("id") Long id);
    String getPipelinePubSettingsName(@Param("name") String name);

    /**
     * Возвращает название именованных сохранённых настроек.
     */
    List<ReportSetting> getNamedPipelineSettings(@Param("idUser") Long idUser);
    List<ReportSetting> getNamedPubPipelineSettings();
    ReportSetting getPipelineSetting(@Param("id") Long id);

    /**
     * Сохраняет текущие настройки пользователя под именем
     * @param idUser - пользователь
     * @param name - имя
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    void savePipelineSettingsByName(@Param("idUser") Long idUser, @Param("name") String name);
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    void delPipelineSettings(@Param("id") Long id);
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    void renamePipelineSettingsByName(@Param("idUser") Long idUser, @Param("name") String name, @Param("newname") String newname);
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    void renamePipelinePubSettingsByName(@Param("name") String name, @Param("newname") String newname);
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    void copyPipelineSettingsByName(@Param("idUser") Long idUser, @Param("name") String name, @Param("newname") String newname, @Param("pub") int pub);
    Date getLastUpdateDate(@Param("idMdtask") Long idMdtask);
}
