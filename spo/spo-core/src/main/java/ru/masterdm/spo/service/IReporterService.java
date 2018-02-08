package ru.masterdm.spo.service;

import java.util.List;

/**
 * Сервис генерации отчёта по ключу отчёта и контейнеру с данными.
 * @author slysenkov
 */
public interface IReporterService {
    /**
     * Генерация отчёта Excel по ключу отчёта и контейнеру с данными.
     * @param reportKey - ключ отчёта
     * @param dataContainer - котейнер с данными для отчёта
     */
    byte[] buidReport(String reportKey, Object dataContainer);

    /**
     * Генерация отчёта Excel с сохранением в формате PDF, по ключу отчёта и контейнеру с данными.
     * @param reportKey - ключ отчёта
     * @param dataContainer - котейнер с данными для отчёта
     */
	byte[] buildPdfFromExcelReport(String reportKey, Object dataContainer);

    /**
     * Генерация отчёта Word с помощью движка Aspose ReportingEngine.
     * @param reportKey - ключ отчёта
     * @param dataContainer - котейнер с данными для отчёта
     * @return сгенерированный отчёт в виде массива байтов
     */
	<T> byte[] buidWordReport(String reportKey, T dataContainer) throws Exception;

	/**
     * Генерация отчёта Word с помощью движка Aspose ReportingEngine.
     * @param reportKey - ключ отчёта
     * @param dataContainer - котейнер с данными для отчёта
     * @param classes - список известных классов для генератора отчётов
     * @return сгенерированный отчёт в виде массива байтов
     */
	<T> byte[] buidWordReport(String reportKey, T dataContainer, List<Class<?>> classes) throws Exception;
}
