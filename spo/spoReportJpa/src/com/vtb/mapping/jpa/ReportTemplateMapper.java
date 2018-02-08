package com.vtb.mapping.jpa;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import ru.masterdm.compendium.domain.VtbObject;

import com.vtb.domain.ReportTemplate;
import com.vtb.domain.ReportTemplate.ReportTemplateTypeEnum;
import com.vtb.exception.DuplicateKeyException;
import com.vtb.exception.MappingException;
import com.vtb.exception.NoSuchObjectException;
import com.vtb.mapping.entities.report.ReportTemplateJPA;

/**
 * ReportTemplate Mapper implementation.
 * @author Michael Kuznetsov 
 */
public class ReportTemplateMapper extends DomainJPAMapper 
		implements com.vtb.mapping.ReportTemplateMapper {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ReportTemplate> findAll() throws MappingException {
		throw new MappingException( "ReportTemplateMapper.findAll is not supported!");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ReportTemplate findByPrimaryKey(ReportTemplate vo) throws NoSuchObjectException, MappingException {
		if (vo == null) throw new IllegalArgumentException("ReportTemplateMapper.findJPAObjectMatching. Argument can't be null" );
		if (vo.getId() == null)  throw new IllegalArgumentException("ReportTemplateMapper.findJPAObjectMatching. PrimaryKey is null" );
		Object obj = getEntityMgr().find(ReportTemplateJPA.class, (long)vo.getId());		
		if (obj == null) throw new NoSuchObjectException("ReportTemplateMapper.findJPAObjectMatching. Object " + vo.toString() + "is not found in the database" );
		return (ReportTemplate)map(obj);
	}

	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ReportTemplate> findByType(String type) throws NoSuchObjectException, MappingException {
		String sqlStr = 
			"select c from ReportTemplateJPA c"
			  + "  join c.type tp " 
			  + "  where tp.id like ?1 and lower(c.system) = 'спо'";
		
		Query query = null;		
		try {
			query = getEntityMgr().createQuery(sqlStr);
			query.setParameter(1, "%" + type + "%");		
			List<ReportTemplateJPA> objectList = query.getResultList();			
			List<ReportTemplate> list = new ArrayList<ReportTemplate>();
			// NOT FOUND
			if (objectList.size() == 0) return null;
			for (ReportTemplateJPA rs : objectList) {				
				list.add( (ReportTemplate)map((Object)rs));
			}
			return list;
		} catch (Exception e) {
			throw new MappingException(e, ("Exception caught in findByType " + e));			
		}
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ReportTemplate findByFilename(String filename) throws NoSuchObjectException, MappingException {
		String sqlStr = 
		    "select c from ReportTemplateJPA c"
		  + "  where c.filename like ?1 ";
		
		Query query = null;
		try {
			query = getEntityMgr().createQuery(sqlStr);
			query.setParameter(1, filename);		
			List<ReportTemplateJPA> objectList = (List<ReportTemplateJPA>)query.getResultList();			
			Iterator<ReportTemplateJPA> iterator = objectList.iterator(); 
			// NOT FOUND
			if (!iterator.hasNext()) return null;
			ReportTemplateJPA rs = (ReportTemplateJPA) iterator.next();
			if (iterator.hasNext()) throw new MappingException("Two or more records in the result set"); 
			else 
				return (ReportTemplate)map((Object)rs);			
		} catch (Exception e) {
			throw new MappingException(e, ("Exception caught in findByFilename " + e));			
		}
	}
	
	/**
	 * maps the object
	 * @param jpaObject
	 * @return
	 * @throws MappingException
	 */
	public VtbObject map(Object jpaObject) throws MappingException {
		if (jpaObject == null) throw new IllegalArgumentException("ReportTemplateMapper.map. Argument can't be null" );
		ReportTemplateJPA jpa = (ReportTemplateJPA)jpaObject;
		boolean fullHierarchy = "Y".equalsIgnoreCase(jpa.getFullHierarchy());
		ReportTemplate vo = new ReportTemplate((long)jpa.getIdDocTemplate(), jpa.getName(), 
				jpa.getText(), jpa.getType().getId(), jpa.getFilename(), fullHierarchy);
		vo.setDocPattern(jpa.getDocPattern());
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void insert(ReportTemplate anObject) throws DuplicateKeyException, MappingException {
		throw new MappingException( "ReportTemplateMapper.insert is not supported!");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(ReportTemplate anObject) throws NoSuchObjectException, MappingException {
		throw new MappingException( "ReportTemplateMapper.insert is not supported!");		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(ReportTemplate anObject) throws NoSuchObjectException, MappingException {
		throw new MappingException( "ReportTemplateMapper.remove is not supported!");		
	}
	
	/**
	 * Tests whether the template is of type PRINT_DOC
	 * @param name name of the template type
	 * @return true if the template is of type PRINT_DOC, false otherwise
	 */
	private boolean isPrintDocType(String name) {
		return name.equals(ReportTemplateTypeEnum.PRINT_DOC_REPORT_TYPE.getValue());
	}

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, String> getRussianNamesFields() throws NoSuchObjectException, MappingException {
        try {
            Query query = getEntityMgr().createNativeQuery("select template_code_rus, template_code  from attributes_required where template_code is not null");
            List<Object[]> objectList = query.getResultList();         
            Map<String, String> map = new HashMap<String, String>();
            for (Object[] array : objectList) map.put((String)array[0], (String)array[1]);
            return map;         
        } catch (Exception e) {
            throw new MappingException(e, ("Exception caught in getRussianNamesFields " + e));         
        }
    }
}
