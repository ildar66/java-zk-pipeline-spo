package com.vtb.mapping.jpa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Query;

import com.vtb.domain.AttributeTreeReport;
import com.vtb.domain.AttributeTreeReportHeader;
import com.vtb.domain.AttributeTreeReportOperation;

import com.vtb.domain.VtbObject;
import com.vtb.exception.DuplicateKeyException;
import com.vtb.exception.MappingException;
import com.vtb.exception.NoSuchObjectException;
import com.vtb.utils.Utils;

/**
 * RoleTreeReportMapper implementation.
 * @author Michael Kuznetsov
 */
public class AttributeTreeReportMapper extends DomainJPAMapper  
				implements com.vtb.mapping.AttributeTreeReportMapper {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AttributeTreeReport> findAll() throws MappingException {
		throw new NoSuchObjectException("AttributeTreeReportReportMapper.findAll. Method is not implemented ");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public AttributeTreeReport findByPrimaryKey(AttributeTreeReport anObject) throws NoSuchObjectException, MappingException {
		throw new NoSuchObjectException("AttributeTreeReportReportMapper.findByPrimaryKey. Method is not implemented ");	
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void insert(AttributeTreeReport anObject) throws DuplicateKeyException, MappingException {
		throw new NoSuchObjectException("AttributeTreeReportReportMapper.insert. Method is not implemented ");	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(AttributeTreeReport anObject) throws NoSuchObjectException, MappingException {
		throw new NoSuchObjectException("AttributeTreeReportReportMapper.update. Method is not implemented ");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(AttributeTreeReport anObject) throws NoSuchObjectException, MappingException {
		throw new NoSuchObjectException("AttributeTreeReportReportMapper.remove. Method is not implemented ");
	}

	/**
	 * maps to VO
	 */	
	public VtbObject mapHeaders(Object jpa) throws MappingException {
		AttributeTreeReportHeader result = new AttributeTreeReportHeader();		
		result.setProcess_name(Utils.varToString(jpa));
		return result; 
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public AttributeTreeReportHeader getHeaderData(Long processId) 
		throws MappingException {
		String sqlStr = 
		  "select description_process "
		+ "from type_process "
		+ "where id_type_process = ?1 ";

		Query query = null;		
		try {
			query = getEntityMgr().createNativeQuery(sqlStr);
			query.setParameter(1, processId);		
			List<Object> objectList = query.getResultList();			
			// always one row
			Iterator<Object> iterator = objectList.iterator(); 
			Object rs = (Object) iterator.next();
			return (AttributeTreeReportHeader) mapHeaders(rs);
		} catch (Exception e) {
			throw new MappingException(e, ("Exception caught in getHeaderData " + e));			
		}
	}
	
	/**
	 * maps to VO
	 */	
	public VtbObject mapOperations(Object[] jpa) throws MappingException {
		AttributeTreeReportOperation result = new AttributeTreeReportOperation();
		result.setName(Utils.varToString(jpa[0]));
		result.setLevel(Utils.varToString(jpa[1]));
		result.setStatus(Utils.varToString(jpa[2]));
		return result; 
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<AttributeTreeReportOperation> getOperations(Long processId) 
				throws MappingException {
		StringBuilder sb = new StringBuilder(); 
		sb.append("SELECT ")
			.append(" (case ")
			.append("     when level = 1 and u.id_var_root is null then u.name_var ") 
			.append("     when level = 1 and u.id_var_root is not null then '--- ' || u.name_var ")
			.append("     when level = 2 then '------ ' || u.name_var ")
			.append("     else '--------- ' || u.name_var  ")
			.append("   end) special_name_var, ")
			.append("   (case when u.id_var_root is null then 0 else level end) var_level, ")
			.append("   (case when u.active = 1 then 'активный' else 'не активный' end) as state ")
			.append(" FROM ")
			.append("   (SELECT (case when id_var_root is null then id_var else id_var_root end) as special_id_var_root, ") 
			.append("    n.*, v.* ")
			.append("   FROM variables v ")
			.append("   LEFT JOIN var_nodes n ")
			.append("   ON n.id_var_node = v.id_var ")
			.append("   WHERE v.id_type_process = ?1 ")
			.append("   ) u ")
			.append("     CONNECT BY PRIOR u.id_var_node = u.special_id_var_root ")
			.append("     ORDER SIBLINGS BY name_var ");

		String sqlStr = sb.toString();
		Query query = null;		
		try {
			query = getEntityMgr().createNativeQuery(sqlStr);
			query.setParameter(1, processId);
			List<Object[]> objectList = query.getResultList();
			ArrayList<AttributeTreeReportOperation> list = new ArrayList<AttributeTreeReportOperation>();
			for (Iterator<Object[]> iterator = objectList.iterator(); iterator.hasNext();) {
				Object[] rs = (Object[]) iterator.next();
				// Обработать результаты	
				AttributeTreeReportOperation operation = (AttributeTreeReportOperation) mapOperations(rs);
				list.add(operation);
			}
			return list;
		} catch (Exception e) {
			throw new MappingException(e, ("Exception caught in getOperations " + e));			
		}
	}
}
