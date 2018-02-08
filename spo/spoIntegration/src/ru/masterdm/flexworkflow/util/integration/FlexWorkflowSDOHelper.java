package ru.masterdm.flexworkflow.util.integration;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

import ru.masterdm.flexworkflow.integration.list.EFlexWorkflowSDOType;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.SDO;

/**
 * Помощник для работы с {@link DataObject SDO}
 * 
 * @author imatushak@masterdm.ru
 */
public class FlexWorkflowSDOHelper {

	private static final Logger LOGGER = Logger
			.getLogger(FlexWorkflowSDOHelper.class.getName());

	private static final int BUFFER_SIZE = 8192;
	private static final String SCHEMA_LANG = "http://www.w3.org/2001/XMLSchema";
	private static final String XMLNS = "commonj.sdo";
	private static final String XML_ROOT = "root";

	/**
	 * Конструктор
	 */
	private FlexWorkflowSDOHelper() {
	}

	/**
	 * Создает {@link DataObject SDO} по указанному {@link EFlexWorkflowSDOType
	 * типу}
	 * 
	 * @param type
	 *            {@link EFlexWorkflowSDOType тип}
	 * @return {@link DataObject SDO} по указанному {@link EFlexWorkflowSDOType
	 *         типу}. В случае ошибки возвращается <code><b>null</b></code>
	 */
	public static DataObject createSDO(EFlexWorkflowSDOType type) {
		DataObject dataObject = null;

		ByteArrayInputStream bais = null;
		try {
			if (type == null)
				throw new Exception("data object type is null");

			String typeValue = type.getValue();
			HelperContext hc = SDO.getDefaultHelperContext();
            Type sdoType = hc.getTypeHelper().getType(XMLNS, typeValue);
			if (sdoType == null) {
				bais = getXSDAsByteArrayInputStream(type.getXSD().getResource());
				hc.getXSDHelper().define(bais, null);
                sdoType = hc.getTypeHelper().getType(XMLNS, typeValue);
			}

			if (sdoType == null)
				throw new Exception("can't get SDO type by type value '"
						+ typeValue + "'");

			hc.getTypeHelper().getType(XMLNS, typeValue);
            dataObject = hc.getDataFactory().create(sdoType);
		} catch (Throwable e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}

		return dataObject;
	}

	/**
	 * Валидирует {@link DataObject SDO} по указанному
	 * {@link EFlexWorkflowSDOType типу}
	 * 
	 * @param dataObject
	 *            {@link DataObject SDO}
	 * @param type
	 *            {@link EFlexWorkflowSDOType тип}
	 * @return <code><b>true</b></code> если {@link DataObject SDO}
	 *         соответствует указанному {@link EFlexWorkflowSDOType типу}. В
	 *         случае ошибки возвращается <code><b>null</b></code>
	 */
	public static Boolean validateSDO(DataObject dataObject,
			EFlexWorkflowSDOType type) {
		Boolean isValid = null;

		ByteArrayOutputStream baos = null;
		ByteArrayInputStream bais = null;
		try {
		    if (dataObject == null) {
                throw new Exception("data object for validation is null");
            }
            if (type == null) {
                throw new Exception("data object type is null");
            }
            if (!dataObject.getType().getName().equals(type.getValue())) {
                isValid = false;
                
                LOGGER.info("dataObject type '" + dataObject.getType().getName() + "' incompatible with type '" + type.getValue() + "'");
                    
                return isValid;
            }
			SchemaFactory factory = SchemaFactory.newInstance(SCHEMA_LANG);
			Schema schema = factory.newSchema(new StreamSource(
					getXSDAsInputStream(type.getXSD().getResource())));
			Validator validator = schema.newValidator();

			baos = getSDOAsByteArrayOutputStream(dataObject);

			if (baos == null)
				throw new Exception(
						"byte array output stream of data object type '"
								+ type.toString() + "' is null");

			byte[] data = baos.toByteArray();
			bais = new ByteArrayInputStream(data);

			if (bais == null)
				throw new Exception(
						"byte array input stream of data object type '"
								+ type.toString() + "' is null");

			try {
				validator.validate(new StreamSource(bais));
			} catch (SAXException saxe) {
				// LOGGER.log(Level.WARNING, saxe.getMessage(), saxe);

				if (validator.getErrorHandler() != null)
					throw saxe;

				isValid = false;
			}

			if (isValid == null)
				isValid = true;
		} catch (Throwable e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}

		return isValid;
	}

	/**
	 * Возвращает {@link ByteArrayInputStream XSD}
	 * 
	 * @param resource
	 *            ресурс
	 * @return {@link ByteArrayInputStream XSD}
	 * @throws Exception
	 *             ошибка
	 */
	private static ByteArrayInputStream getXSDAsByteArrayInputStream(
			String resource) throws Exception {
		ByteArrayInputStream bais = null;

		InputStream is = null;
		ByteArrayOutputStream baos = null;
		try {
			is = getXSDAsInputStream(resource);

			byte[] buffer = new byte[BUFFER_SIZE];
			int bytesRead = 0;

			baos = new ByteArrayOutputStream();
			while ((bytesRead = is.read(buffer)) != -1)
				baos.write(buffer, 0, bytesRead);

			byte[] data = baos.toByteArray();
			bais = new ByteArrayInputStream(data);
		} catch (Exception e) {
			throw e;
		}

		return bais;
	}

	/**
	 * Возвращает {@link InputStream XSD}
	 * 
	 * @param resource
	 *            ресурс
	 * @return {@link InputStream XSD}
	 * @throws Exception
	 *             ошибка
	 */
	private static InputStream getXSDAsInputStream(String resource)
			throws Exception {
		InputStream is = null;

		try {
			if (resource == null)
				throw new Exception("resource is null");

			is = FlexWorkflowSDOHelper.class.getClassLoader()
					.getResourceAsStream(resource);

			if (is == null)
				throw new Exception("input stream of resource '" + resource
						+ "' is null");
		} catch (Exception e) {
			throw e;
		}

		return is;
	}

	/**
	 * Возвращает SDO как {@link ByteArrayOutputStream XML}
	 * 
	 * @param dataObject
	 *            {@link DataObject SDO}
	 * @return SDO как {@link ByteArrayOutputStream XML}
	 */
	public static ByteArrayOutputStream getSDOAsByteArrayOutputStream(
			DataObject dataObject) {
		ByteArrayOutputStream baos = null;

		try {
			if (dataObject == null)
				throw new Exception("data object is null");

			baos = new ByteArrayOutputStream();
			HelperContext hc = SDO.getDefaultHelperContext();
            hc.getXMLHelper().save(dataObject, null, XML_ROOT, baos);
		} catch (Throwable e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}

		return baos;
	}
	
	public static Date getDate(DataObject dataObject, String xPath) {
	    try {
	        HelperContext hc = SDO.getDefaultHelperContext();
	        if (dataObject.isSet(xPath) && dataObject.get(xPath) != null) {
	            return hc.getDataHelper().toDate((String) dataObject.get(xPath));
	        }  
	    } catch (Exception e) {
	        LOGGER.log(Level.SEVERE, e.getMessage(), e);
	    }
        return null;
    }
}