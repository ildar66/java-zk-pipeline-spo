package ru.masterdm.spo.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;
import org.springframework.scheduling.annotation.Async;

import ru.md.domain.Department;
import ru.md.domain.DepartmentExt;
import ru.md.domain.IndRate;
import ru.md.domain.User;
import ru.md.domain.dashboard.DashboardEvent;
import ru.md.domain.dashboard.DetailReportRow;
import ru.md.domain.dashboard.MainReportRow;
import ru.md.domain.dashboard.PipelineTradingDesk;
import ru.md.domain.dashboard.Sum;
import ru.md.domain.dashboard.TaskTypeStatus;
import ru.md.domain.dashboard.TopReportRow;

/**
 * Created by Andrey Pavlenko on 11.08.16.
 */
public interface IDashboardService {

    /**
     * Записать актуальную информацию по заявке.
     * @param idMdtask - номер заявки
     */
    void logTask(Long idMdtask);

    /**
     * Пересчитывает старые заявки, по которым нет даных.
     */
    void recalculateOldTasks();

    DashboardEvent getCurrentStatus(Long idMdtask);

    void clearOldClientReport();

    void generatePipelineClientReport();

    List<MainReportRow> getMainReport(Date startDate, Date endDate, String taskType, Integer creditDocumentary, Set<PipelineTradingDesk> tradingDesks,
                                      boolean tradingDeskOthers,
                                      Collection<? extends Department> departments);

    List<DetailReportRow> getDetailReport(Date startDate, Date endDate, String taskType, Integer creditDocumentary,
                                          Set<PipelineTradingDesk> tradingDesks,
                                          boolean tradingDeskOthers,
                                          Collection<? extends Department> departments,
                                          Integer idStatus, String branch);

    List<TopReportRow> getTopReport(Date startDate, Date endDate, String taskType, Integer creditDocumentary,
                                    Set<PipelineTradingDesk> tradingDesks,
                                    boolean tradingDeskOthers,
                                    Collection<? extends Department> departments);

    List<TaskTypeStatus> getTaskTypeStatusesInOrder(String taskType);

    List<PipelineTradingDesk> getPipelineTradingDesk();

    List<DepartmentExt> getDepartmentsExtForTree(User user);

    /**
     * Возвращает сумму как её нужно отображать на экране
     * Формат суммы:
     * Если сумма равна  1 000 000 000 000 (1 триллион) – выводим 1 000 млрд. руб.
     * Если сумма равна  1 000 000 000 (1 миллиард) – выводим 1 млрд. руб.
     * Если сумма равна 500 000 000 (500 миллионов рублей) – выводим 0,5 млрд. руб.
     * Если сумма равна 100 000 000 (100 миллионов рублей) – выводим 0,1 млрд. руб.
     * Если сумма равна 1 000 000 (1 миллион рублей) – выводим 1 млн. руб.
     * Если сумма равна 500 000 (500 тысяч рублей) – выводим 0,5 млн. руб.
     * Если сумма равна 100 000 (100 тысяч рублей) – выводим 0,1 млн. руб.
     */
    String getRurSumFormated(Double res);

    /**
     * Возвращает сумму как её нужно отображать в отчёте, в млн., в валюте сделки
     */
    String getNativeCurrencySumFormated(Double res);

    /**
     * Перевод суммы в рубли по курсу на дату.
     */
    Double toRur(Sum s, Date to);

    /**
     * Получение текста категории продукта в родительном падеже.
     */
    String getTitleTypeByCode(String code);

    /**
     * Заголовок для списка.
     */
    String getTitle(Long statusid, String taskType, Date from, Date to, Long creditDocumentary,
                    Long[] departments, Long[] tradingDeskSelected, Boolean isTradingDeskOthers,
                    Boolean mainPeriod, String industry);
    List<Integer> getStatusidsByTaskType(String taskType);

    /**
     * Заголовок для файла отчёта (без расширения).
     */
    String getFileName(Long statusid, String taskType, Date from, Date to, Long creditDocumentary,
                       Long[] departments, Long[] tradingDeskSelected, Boolean isTradingDeskOthers,
                       Boolean mainPeriod);

    /**
     * Возвращает дату создания заявки.
     */
	Date getDealCreateDate(Long idMdtask);

    /**
     * Возвращает дату изменения заявки.
     */
	Date getDealChangeDate(Long idMdtask);

    /**
     * Возвращает фио исполнителя по заявке с заданной ролью.
     */
	String getDealTeamMemberByRoleName(Long idMdtask, String roleName);
	
    /**
     * Возвращает список ставок.
     */
	List<IndRate> getRate(Long id);
    String getOrganizationBranch(String organizationId);

    @Async
    void savePipelineSettings(Long idUser, String settingJson);

}
