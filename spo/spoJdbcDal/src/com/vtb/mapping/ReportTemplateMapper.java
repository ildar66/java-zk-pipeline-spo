package com.vtb.mapping;

import java.util.List;
import java.util.Map;

import com.vtb.domain.ReportTemplate;
import com.vtb.exception.MappingException;
import com.vtb.exception.NoSuchObjectException;

/**
 * ReportTemplateMapper
 * @author Michael Kuznetsov 
 */
public interface ReportTemplateMapper extends com.vtb.mapping.Mapper<ReportTemplate> {
	
	/**
	 * Retrieve a reportTemplate by filename. Only one value should be!
	 * @param filename
	 * @return ReportTemplate value objects, if found. Null otherwise. Exception in case of error (two records or more ) 
	 * @throws MappingException
	 */
	ReportTemplate findByFilename(String filename) throws MappingException;
	
	/**
	 * Retrieve a list of reportTemplate by type.
	 * @param type
	 * @return list of ReportTemplate 
	 * @throws MappingException
	 */
	List<ReportTemplate> findByType(String type) throws NoSuchObjectException, MappingException;
	
	/**
	 * Finds map of <russian names, english names> for field names, used in transformation  
	 * @return composed map
	 * @throws NoSuchObjectException
	 * @throws MappingException
	 */
	Map<String, String> getRussianNamesFields() throws NoSuchObjectException, MappingException;
}