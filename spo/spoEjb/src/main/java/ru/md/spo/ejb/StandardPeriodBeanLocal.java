package ru.md.spo.ejb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.ejb.Local;
import javax.servlet.http.HttpServletRequest;

import org.xml.sax.SAXException;

import ru.md.domain.AuditDurationStage;
import ru.md.pup.dbobjects.StageJPA;
import ru.md.spo.dbobjects.StandardPeriodGroupJPA;

import com.vtb.domain.StandardPeriod;
import com.vtb.exception.FactoryException;

/**
 * Интерфейс для работы с нормативными сроками.
 * @author Andrey Pavlenko
 */
@Local
public interface StandardPeriodBeanLocal {
	/**
	 * Проверяет есть ли активные операции на этапе нормативных сроков.
	 */
	boolean isGroupActive(Long groupId, Long idPupProcess);
	/**
	 * Возвращает текущий выбранный критерий для данного этапа данной заявки. 
	 * Если критерий не был выбран или заявка редактировалась вручную, то вернет null. 
	 */
	Long getCurrentValueId(Long groupId, Long idPupProcess);

	/**возвращает данные для отчета сроки прохождения заявки*/
	ArrayList<StandardPeriod> getStandartPeriodReportByNumber(Long mdtaskNumber, boolean onlyExpertise) throws Exception;

	/**возвращает данные для отчета сроки прохождения заявки*/
	ArrayList<StandardPeriod> getStandartPeriodReport(Long mdtaskId) throws Exception;

	/** возвращает этап нормативных сроков */
	StandardPeriodGroupJPA getStandardPeriodGroup(Long id);

    /**
     * Сохраняет версию нормативных сроков.
     * Этот код пишу ночью, так что даже не стыдно, что грубо нарушаю MVC.
     * @author Andrey Pavlenko
     */
    String saveStandardPeriod(HttpServletRequest request) throws FactoryException;
    
    /**пересчитать дедлайны для активных операций заявки по pupid*/
    void recalculateDeadline(Long pupid) throws FactoryException;
    
    /**изменение нормативного срока
     * @param cmnt - коментарий к изменению нормативного срока
     * @param valueid - код записи справочника нормативных сроков
     * @param mdtaskid - id заявки
     * @param userid - кто изменяет нормативный срок
     * @param days - количество дней для ручного изменения нормативного срока.
     * @param grid - этап 
     * Должно быть заполнено одно поле из valueid и days
     * */
    void changeStandardPeriod(Long mdtaskid, Long userid, String cmnt, Long valueid, Long days, Long grid);

    /**
     * запускает таймер, который делает пересчет deadline.
     */
    void startTimer();
    /**
     * Для операции сбора возвращает список этапов, которых жду.
     * @throws SAXException 
     * @throws IOException 
     */
	HashSet<Long> getCollectStages(StageJPA stage) throws Exception;
	/**
	 * Для этапа, возвращает дату начала операции с учётом правил для операции сбора.
	 * @param stage_id - название первой операции этапа
	 */
	Date calculateStartDateCollect(Long stage_id, Date start, Date end, Long processId);

	List<AuditDurationStage> getAuditDurationStagesReport(Date from, Date to, Long processType);

	/**
	 * Создаёт отчёт клиентской справки для отчёта Аудит прохождения этапов
	 */
	void generateAuditClientReport();
	void clearAuditClientReport();
}
