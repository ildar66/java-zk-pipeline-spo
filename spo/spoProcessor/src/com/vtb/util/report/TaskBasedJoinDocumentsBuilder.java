package com.vtb.util.report;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aspose.words.Document;
import com.aspose.words.LoadFormat;
import com.aspose.words.SaveFormat;
import com.vtb.mapping.ReportTemplateMapper;
import com.vtb.report.renderer.MSWordRenderer;
import com.vtb.report.renderer.ReportRenderer;
import com.vtb.util.report.utils.Task2ReportDataHelper;

/**
 * Этот builder строит отчет на основе данных заявки + переданных через Map значений 
 * и добавляет его к концу уже существующего документа (byte[])  
 * @author mkuznetcov
 */
public class TaskBasedJoinDocumentsBuilder extends TaskBasedReportWordBuilder {
	private static final Logger LOGGER = LoggerFactory.getLogger(TaskBasedJoinDocumentsBuilder.class.getName());
    /**
     * Generic constructor. 
     * @param reportName report Name
     * @param reportMapper mapper
     * @param dynamicEncoding encoding
     * @throws Exception throws Exception 
     */
    public TaskBasedJoinDocumentsBuilder(String reportName, ReportTemplateMapper reportMapper, boolean dynamicEncoding) throws Exception {
        super(reportName, reportMapper, dynamicEncoding);
    }

    /**
     * Генерация документа а основе данных заявки + переданных через Map значений 
     * и добавляет его к концу уже существующего документа (byte[]) 
     * ВНИМАНИЕ! если в параметрах сделки ReportTemplateParams.MDTASK_ID.getValue() 
     *   == 0L или -1L, то данные по сделке вычисляться НЕ БУДУТ!!!
     * @param parameters параметры сделки, а также дополнительные подмешиваемые параметры.
     * @param sourceDocument документ, к которому добавляется создаваемый документ
     * @return ReportRenderer с объединенными документами
     * @throws Exception бросаемое исключение
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public ReportRenderer buildReport(Map parameters, byte[] sourceDocument) throws Exception {
        // сначала инициализируем предка дополнительными параметрами
        extraParams = new HashMap<String, String>();
        Map<String, String[]> params = (Map<String, String[]>) parameters; 
        for (Entry<String, String[]> entry : params.entrySet()) {
            extraParams.put(entry.getKey(), entry.getValue()[0]);
        }
        
        // строим отчет на основе Task и переданных параметров.
        MSWordRenderer renderer = (MSWordRenderer) super.buildReport(parameters);
        if (sourceDocument != null) {
            // добавим сгенерированный документ в конец существующего
            ByteArrayInputStream stream = new ByteArrayInputStream(renderer.getReportBytes());
            Document doc = new Document(stream);
            stream.close();
            
            stream = new ByteArrayInputStream(sourceDocument);
            Document sourceDoc = new Document(stream);
            stream.close();
            
            Task2ReportDataHelper.appendDoc(doc, sourceDoc);
            
	        // выясним формат исходного файла по его содержимому.
	        int sourceDocFormat = getLoadFormat(sourceDocument);
            String fileExtension = getFileExtensionFromAsposeCode(sourceDocFormat);
	        LOGGER.info("fileExtension: " + fileExtension);
	        renderer.setReportExtension(fileExtension);
	        
	        // сохраним файл в исходном формате
		    ByteArrayOutputStream out = new ByteArrayOutputStream();                
		    int saveFormat = getSaveFormatFromAsposeCode(sourceDocFormat);
		    LOGGER.info("saveFormat: " + saveFormat);
			doc.save(out, saveFormat);
		    out.close();
		    renderer.setReportBytes(out.toByteArray());
        } else {
        	// есть только подпись, самого файла  нет. 
        	// Генерируемые документы в super.build() всегда дают doc-файлы.
        	renderer.setReportExtension("doc");
        }
        return renderer;
    }
    
    /**
     * Detects the real type of doc file format. DOC or DOCX, or DOTM, or RTF.
     * Return value of LoadFormat enumeration   
     * @param content content of MSWord document
     * @return LoadFormat enumeration value as real type of doc file format.
     * @throws Exception
     */
    public static int getLoadFormat(byte[] content) throws Exception {
    	ByteArrayInputStream stream = new ByteArrayInputStream(content);
    	try {
	    	Document doc = new Document(stream);
	    	int res = doc.getOriginalLoadFormat();
	    	LOGGER.info("source file load format: " + res);
	    	return res; //doc.getOriginalLoadFormat();
    	}  finally {
    		try {
				if (stream != null) stream.close();
			} catch (IOException e) {}
    	}
    }

    /**
     * Checks, whether the given format code equals to DOC or DOCX format
     * @param format aspose.LoadFormat enumeration value
     * @return whether the given format code equals to DOC or DOCX format
     */
    public static boolean isDOCorDOCXFormat(int format) {
    	return (LoadFormat.DOC == format) || (LoadFormat.DOCX == format);
    }

    /**
     * Checks, whether the given format code equals to DOC format
     * @param format aspose.LoadFormat enumeration value
     * @return whether the given format code equals to DOC format
     */
    public static boolean isDOCFormat(int format) {
    	return LoadFormat.DOC == format;
    }

    /**
     * Checks, whether the given format code equals to DOCX format
     * @param format aspose.LoadFormat enumeration value
     * @return whether the given format code equals to DOCX format
     */
    public static boolean isDOCXFormat(int format) {
    	return LoadFormat.DOCX == format;
    }

    /**
     * Returns file extension from aspose int enumeration value. 
     * @param type aspose int enumeration value for doc file.
     * @return file extension 
     */
    public static String getFileExtensionFromAsposeCode(int type) {
		switch (type) {
			case LoadFormat.DOC : return "doc"; 
			case LoadFormat.DOCX : return "docx";
			case LoadFormat.RTF : return "rtf";
			case LoadFormat.HTML : return "html";
			case LoadFormat.ODT : return "odt";
			default : return null;
		}
	}
    
    /**
     * Returns file extension from aspose int enumeration value. 
     * @param type aspose int enumeration value for doc file.
     * @return file extension 
     */
    public static int getSaveFormatFromAsposeCode(int type) {
		switch (type) {
			case LoadFormat.DOC : return SaveFormat.DOC; 
			case LoadFormat.DOCX : return SaveFormat.DOCX;
			case LoadFormat.RTF : return SaveFormat.RTF;
			case LoadFormat.HTML : return SaveFormat.HTML;
			case LoadFormat.ODT : return SaveFormat.ODT;
			default : return -1;
		}
	}

}
