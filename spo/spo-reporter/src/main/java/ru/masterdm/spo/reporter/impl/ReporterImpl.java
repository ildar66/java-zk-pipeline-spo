package ru.masterdm.spo.reporter.impl;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.CodeSource;
import java.util.Date;
import java.util.List;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLClassLoader;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.aspose.cells.CellsHelper;
import com.aspose.cells.SaveFormat;
import com.solidfire.gson.Gson;

import ru.masterdm.reportsystem.ReportBuilderFactory;
import ru.masterdm.reportsystem.core.IReportBuilder;
import ru.masterdm.reportsystem.core.domain.IReport;
import ru.masterdm.reportsystem.list.EBuilderType;
import ru.masterdm.spo.reporter.Reporter;

/**
 * Сервис-обёртка для генератора отчётов ReportSystem.
 * Сервис изолирует Aspose.cells версии 8.3.1 и Aspose.words версии 14.11.0 от остального проекта
 * @author slysenkov
 */

@Component
public class ReporterImpl implements Reporter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReporterImpl.class);

    /**
     * Тестовый метод, возвращает текущую дату.
     * @return текущая дата
     */
	@Override
	public String produceString() {
		return new Date().toString();
	}

    /**
     * Генерация отчёта.
     * @param dataContainer - объект-контейнер отчёта,
     *                        объект должен содержать поле reportTemplate с байтовым массивом шаблона отчёта
     * @return сгенерированный отчёт в виде массива байтов
     */
	@Override
	public byte[] buildExcelReport(Object dataContainer) {
		byte[] result = null;
		if (dataContainer == null)
			return null;
		try {
			IReportBuilder reportBuilder = ReportBuilderFactory.newInstance(EBuilderType.ASPOSE_EXTENDED_EXCEL);
			if (reportBuilder != null) {
				IReport report = reportBuilder.build(dataContainer);
				if (report != null)
					result = report.getData();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

    /**
     * Генерация отчёта Excel.
     * @param dataContainer - объект-контейнер отчёта
     * @param template - байтовый массив шаблона отчёта
     * @return сгенерированный отчёт в виде массива байтов
     */
	@Override
	public byte[] buildExcelReport(Object dataContainer, byte[] template) {
		byte[] result = null;
		if (dataContainer == null)
			return null;
		try {
			IReportBuilder reportBuilder = ReportBuilderFactory.newInstance(EBuilderType.ASPOSE_EXTENDED_EXCEL);
			if (reportBuilder != null) {
				IReport report = reportBuilder.build(dataContainer, template, SaveFormat.XLSX);
				if (report != null)
					result = report.getData();
				LOGGER.info("=========================================== (xlsx) aspose cells version = " + CellsHelper.getVersion());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

    /**
     * Генерация отчёта отчёта Excel, сщхранение в формате pdf.
     * @param dataContainer - объект-контейнер отчёта
     * @param template - байтовый массив шаблона отчёта
     * @return сгенерированный отчёт в виде массива байтов
     */
	@Override
	public byte[] buildPdfFromExcelReport(Object dataContainer, byte[] template) {
		byte[] result = null;
		if (dataContainer == null)
			return null;
		try {
			IReportBuilder reportBuilder = ReportBuilderFactory.newInstance(EBuilderType.ASPOSE_EXTENDED_EXCEL);
			if (reportBuilder != null) {
				IReport report = reportBuilder.build(dataContainer, template, SaveFormat.PDF);
				if (report != null)
					result = report.getData();
				LOGGER.info("=========================================== (pdf) aspose cells version = " + CellsHelper.getVersion());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
     * Копирование экземпляров класса, загруженных разными загрузчиками классов.
     * @param src - исходный объект
     * @param clazz - класс для результирующего объекта
     * @return экземпляр результирующего объекта
     * @throws Exception e
     */
	private Object copyObject(Object src, Class<?> clazz) throws Exception {
		Object result = null;
        Gson gson = new Gson();
        String json = gson.toJson(src);
		//LOGGER.info("================= json = " + json);

		result = gson.fromJson(json, clazz);
		return result;
	}
	
	/**
     * Получение метода заданного класса по имени и типам параметров.
     * @param loader - ClassLoader для загрузки класса в котором находится метод
     * @param className - имя класса для загрузки
     * @param methodName - имя метода
     * @param args - список классов аргументов метода
     * @return метод заданного класса по имени и типам параметров
     * @throws Exception e
     */
	private Method getTargetMethod(ClassLoader loader, String className, String methodName, Class<?>...args) throws Exception {
		return getTargetMethod(loader.loadClass(className), methodName, args);
	}

	/**
     * Получение метода заданного класса по имени и типам параметров.
     * @param clazz - класс для получения метода
     * @param methodName - имя метода
     * @param args - список классов аргументов метода
     * @return метод заданного класса по имени и типам параметров
     * @throws Exception e
     */
	private Method getTargetMethod(Class <?> clazz, String methodName, Class<?>...args) throws Exception {
		Method result = null;
		Method[] allMethods = clazz.getDeclaredMethods();
		LOGGER.info("============================= before " + methodName);
		boolean skipMethod; 
		for (Method m : allMethods) {
			skipMethod = false;
			String mname = m.getName();
			if (!mname.equals(methodName))
				continue;
			Type[] pType = m.getGenericParameterTypes();
			if (pType.length != args.length)
				continue;

			int i = 0;
			//LOGGER.info("============================= method " + methodName);
			for (Class<?> c : args) {
				//LOGGER.info("============================= i " + i + ", pType[i] = " + pType[i] + ", c = " + c);
				if (c != null && c != this.getClass()) {
					if (pType[i] != c) {
						skipMethod = true;
						//LOGGER.info("============================= skip ");
						break;
					}
				}
				i++;
			}
			if (skipMethod)
				continue;

/*			LOGGER.info("============================= parameter types for " + mname);
			if (pType.length != 0) {
					for (Type t : pType) {
						LOGGER.info("============================= p " + t.toString());
					}
			}
*/
			result = m;
			break;
		}
		LOGGER.info("============================= after " + methodName + ", m = " + result);
		return result;
	}
	
	/**
     * Генерация отчёта Word.
     * @param dataContainer - объект-контейнер отчёта
     * @param template - байтовый массив шаблона отчёта
     * @return сгенерированный отчёт в виде массива байтов
     * @throws Exception e
     */
	@Override
	public <T> byte[] buildWordReport(T dataContainer, byte[] template) throws Exception {
		return buildWordReport(dataContainer, template, null);
	}
	
	/**
     * Генерация отчёта Word.
     * @param dataContainer - объект-контейнер отчёта
     * @param template - байтовый массив шаблона отчёта
     * @param classes - список известных классов для генератора отчётов
     * @return сгенерированный отчёт в виде массива байтов
     * @throws Exception e
     */
	@Override
	public <T> byte[] buildWordReport(T dataContainer, byte[] template, List<Class<?>> classes) throws Exception {
		if (dataContainer == null)
			return null;

		URL dsUrl = dataContainer.getClass().getProtectionDomain().getCodeSource().getLocation();
	    URL[] dsUrls = new URL[]{dsUrl};
	    ClassLoader dsUrlClasLoader = new URLClassLoader(dsUrls);

		CodeSource thisCodeSource = this.getClass().getProtectionDomain().getCodeSource();

		URL codeURL = thisCodeSource.getLocation();

		URL[] urls = new URL[]{codeURL};
	    ClassLoader urlClasLoader = new URLClassLoader(urls, dsUrlClasLoader);

		Class<?> dsClass = dsUrlClasLoader.loadClass(dataContainer.getClass().getName());
		
		Object ds = copyObject(dataContainer, dsClass);
		LOGGER.info("=========================================== ds after copy = " + ds);

		String dsClassName = dataContainer.getClass().getSimpleName();
		dsClassName = dsClassName.substring(0, 1).toLowerCase() + ((dsClassName.length() > 1) ? dsClassName.substring(1) : "");
		
		Class<?> e = urlClasLoader.loadClass("com.aspose.words.ReportingEngine");
		Object engine = e.newInstance();

		ByteArrayInputStream bais = new ByteArrayInputStream(template);

	    Class<?> d = urlClasLoader.loadClass("com.aspose.words.Document");
		
		Object doc = d.getConstructor(InputStream.class).newInstance(bais);

		if (classes != null) {
			Object knownTypes = e.getDeclaredMethod("getKnownTypes").invoke(engine);
		
			Method addMethod = getTargetMethod(urlClasLoader, "com.aspose.words.KnownTypeSet", "add", this.getClass());
		
			for (Class<?> clazz:classes) {
				addMethod.invoke(knownTypes, clazz);
				LOGGER.info("============================= after add invocation, clazz = " + clazz);
			}
		}
		
		Method buildMethod = getTargetMethod(e, "buildReport", null, Object.class, String.class);
		buildMethod.invoke(engine, doc, ds, dsClassName);
		LOGGER.info("============================= after buildReport invocation");

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		Method saveMethod = getTargetMethod(d, "save", OutputStream.class, int.class);
		saveMethod.invoke(doc, baos, com.aspose.words.SaveFormat.DOCX);
		
		LOGGER.info("=========================================== after doc.save");

		return baos.toByteArray();
	}
}
