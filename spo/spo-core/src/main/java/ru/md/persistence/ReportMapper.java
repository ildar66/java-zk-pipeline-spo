package ru.md.persistence;

import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ru.md.domain.AuditDurationStage;
import ru.md.domain.AuditDurationTasksHistory;
import ru.md.domain.ReportTemplate;
import ru.md.domain.TaskComment;

import java.util.Date;
import java.util.List;

/**
 * Отчёты.
 * @author Andrey Pavlenko
 *
 */
public interface ReportMapper {
	/** возвращает данные для отчета Аудит прохождения заявки */
	List<AuditDurationStage> findAuditDurationStage(@Param("processID") Long processID,
													@Param("from") Date from, @Param("to") Date to);
	List<AuditDurationTasksHistory> getAuditDurationTasksHistory(@Param("processID") Long processID,
																 @Param("idSPG") Long idSPG);
	List<Long> getStandardPeriodValueBySPG(@Param("idVersion") Long idVersion, @Param("stagename") String stagename);
	Long getLastStandardPeriodValueChange(@Param("groupname") String groupname, @Param("idmdtask") Long idmdtask,
										  @Param("iterEnd") Date iterEnd);
    List<TaskComment> getLastTaskComment(@Param("idSPG") Long idSPG, @Param("idmdtask") Long idmdtask,
								   @Param("from") Date from, @Param("to") Date to);
    TaskComment getLastComment(@Param("idmdtask") Long idmdtask);

	/**
	 * Возвращает актуальную версию нормативных сроков на дату
	 * @param idProcessType - тип процесса
	 * @param dt - на дату
     * @return актуальную версию нормативных сроков на дату
     */
	Long getActualSPVersion(@Param("idProcessType") Long idProcessType, @Param("dt") Date dt);

	/**
	 * Вставка строки для клиентской справки.
	 */
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	void insertAuditClientReport(AuditDurationStage audit);
	
	/**
	 * Возвращает список объектов с шаблоном отчёта.
	 * @param systemName - название системы (если null, то выборка идёт только по ключу шаблона)
	 * @param templateKey - ключ шаблона
     * @return {@link ReportTemplate список шаблонов}  отчёта
     */
	List<ReportTemplate> getCompendiumTemplate(@Param("systemName") String systemMame, @Param("templateKey") String templateKey);
}
