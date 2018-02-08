package ru.md.spo.ejb;

import javax.ejb.Local;

import ru.md.spo.report.TaskReport;

/**
 * ААА 21. ДО. Сервис начитки объектов.
 * @author Andrey Pavlenko
 */
@Local
public interface ReportBeanLocal {
    TaskReport getTaskReport(Long idMdtask);
}
