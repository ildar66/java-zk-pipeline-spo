package ru.masterdm.spo.reporter;

import java.util.List;

/**
 * Сервис-обёртка для генератора отчётов ReportSystem.
 * Сервис изолирует Aspose.cells версии 8.3.1 и Aspose.words версии 14.11.0 от остального проекта
 * @author slysenkov
 */
public interface Reporter {
    /**
     * Тестовый метод, возвращает текущую дату.
     * @return текущая дата
     */
	String produceString();

    /**
     * Генерация отчёта.
     * @param dataContainer - объект-контейнер отчёта,
     *                        объект должен содержать поле reportTemplate с байтовым массивом шаблона отчёта
     * @return сгенерированный отчёт в виде массива байтов
     */
	byte[] buildExcelReport(Object dataContainer);

    /**
     * Генерация отчёта Excel.
     * @param dataContainer - объект-контейнер отчёта
     * @param template - байтовый массив шаблона отчёта
     * @return сгенерированный отчёт в виде массива байтов
     */
	byte[] buildExcelReport(Object dataContainer, byte[] template);

	/**
     * Генерация отчёта Word.
     * @param dataContainer - объект-контейнер отчёта
     * @param template - байтовый массив шаблона отчёта
     * @return сгенерированный отчёт в виде массива байтов
     * @throws Exception e
     */
	<T> byte[] buildWordReport(T dataContainer, byte[] template) throws Exception;

	/**
     * Генерация отчёта Word.
     * @param dataContainer - объект-контейнер отчёта
     * @param template - байтовый массив шаблона отчёта
     * @param classes - список известных классов для генератора отчётов
     * @return сгенерированный отчёт в виде массива байтов
     * @throws Exception e
     */
	<T> byte[] buildWordReport(T dataContainer, byte[] template, List<Class<?>> classes) throws Exception;

    /**
     * Генерация отчёта отчёта Excel, сщхранение в формате pdf.
     * @param dataContainer - объект-контейнер отчёта
     * @param template - байтовый массив шаблона отчёта
     * @return сгенерированный отчёт в виде массива байтов
     */
	byte[] buildPdfFromExcelReport(Object dataContainer, byte[] template);
}
