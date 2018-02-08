package ru.masterdm.spo.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.md.domain.ReportTemplate;
import ru.md.persistence.ReportMapper;
import ru.masterdm.spo.reporter.Reporter;

/**
 * Сервис генерации отчёта по ключу отчёта и контейнеру с данными.
 * @author slysenkov
 */
@Service
public class ReporterService implements IReporterService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReporterService.class);

    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private Reporter reporter;

    /**
     * Генерация отчёта Excel по ключу отчёта и контейнеру с данными.
     * @param reportKey - ключ отчёта
     * @param dataContainer - котейнер с данными для отчёта
     */
    @Override
    public byte[] buidReport(String reportKey, Object dataContainer) {
    	byte[] result = null;
    	try {
		List<ReportTemplate> temlateList = reportMapper.getCompendiumTemplate(null, reportKey);
		if (temlateList == null || temlateList.size() == 0)
			return null;

		ReportTemplate rt = temlateList.get(0);
		byte[] template = rt.getDocPattern();
        result = reporter.buildExcelReport(dataContainer, template);
        LOGGER.info("====================== report created");

    	} catch (Exception e) {
			e.printStackTrace();
		}
    	return result;
    }

    /**
     * Генерация отчёта Excel с сохранением в формате PDF, по ключу отчёта и контейнеру с данными.
     * @param reportKey - ключ отчёта
     * @param dataContainer - котейнер с данными для отчёта
     */
    @Override
    public byte[] buildPdfFromExcelReport(String reportKey, Object dataContainer) {
    	byte[] result = null;
    	try {
		List<ReportTemplate> temlateList = reportMapper.getCompendiumTemplate(null, reportKey);
		if (temlateList == null || temlateList.size() == 0)
			return null;

		ReportTemplate rt = temlateList.get(0);
		byte[] template = rt.getDocPattern();
        result = reporter.buildPdfFromExcelReport(dataContainer, template);
        LOGGER.info("====================== report created");

    	} catch (Exception e) {
			e.printStackTrace();
		}
    	return result;
    }
    
	/**
     * Генерация отчёта Word по ключу отчёта и контейнеру с данными.
     * @param reportKey - ключ отчёта
     * @param dataContainer - котейнер с данными для отчёта
     * @return сгенерированный отчёт в виде массива байтов
     */
	@Override
	public <T> byte[] buidWordReport(String reportKey, T dataContainer) throws Exception {
		return buidWordReport(reportKey, dataContainer, null);
	}

	/**
     * Генерация отчёта Word по ключу отчёта и контейнеру с данными.
     * @param reportKey - ключ отчёта
     * @param dataContainer - котейнер с данными для отчёта
     * @param classes - список известных классов для генератора отчётов
     * @return сгенерированный отчёт в виде массива байтов
     */
	@Override
	public <T> byte[] buidWordReport(String reportKey, T dataContainer, List<Class<?>> classes) throws Exception {
    	byte[] result = null;
		List<ReportTemplate> temlateList = reportMapper.getCompendiumTemplate(null, reportKey);
		if (temlateList == null || temlateList.size() == 0)
			return null;

		ReportTemplate rt = temlateList.get(0);
		byte[] template = rt.getDocPattern();
        result = reporter.buildWordReport(dataContainer, template, classes);
        LOGGER.info("====================== report created");

    	return result;
	}
}
