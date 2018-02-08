package com.vtb.mapping.jpa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Query;

import com.vtb.domain.RolesToStageReport;
import com.vtb.domain.RolesToStageReportHeader;
import com.vtb.domain.RolesToStageReportStage;
import com.vtb.domain.VtbObject;
import com.vtb.exception.DuplicateKeyException;
import com.vtb.exception.MappingException;
import com.vtb.exception.NoSuchObjectException;
import com.vtb.utils.Utils;

/**
 * RolesToStageReportMapper implementation.
 * @author Michael Kuznetsov
 */
public class RolesToStageReportMapper extends DomainJPAMapper  
				implements com.vtb.mapping.RolesToStageReportMapper {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<RolesToStageReport> findAll() throws MappingException {
		throw new NoSuchObjectException("RolesToStageReportMapper.findAll. Method is not implemented ");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public RolesToStageReport findByPrimaryKey(RolesToStageReport anObject) throws NoSuchObjectException, MappingException {
		throw new NoSuchObjectException("RolesToStageReportMapper.findByPrimaryKey. Method is not implemented ");	
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void insert(RolesToStageReport anObject) throws DuplicateKeyException, MappingException {
		throw new NoSuchObjectException("RolesToStageReportMapper.insert. Method is not implemented ");	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(RolesToStageReport anObject) throws NoSuchObjectException, MappingException {
		throw new NoSuchObjectException("RolesToStageReportMapper.update. Method is not implemented ");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(RolesToStageReport anObject) throws NoSuchObjectException, MappingException {
		throw new NoSuchObjectException("RolesToStageReportMapper.remove. Method is not implemented ");
	}

	/**
	 * maps to VO
	 */	
	public VtbObject map(Object jpaObject) throws MappingException {
		Object[] jpa = (Object[]) jpaObject;
		RolesToStageReportStage result = new RolesToStageReportStage();
		String operationId = Utils.varToString(jpa[0]);
		result.setOperationId( (operationId != null && !operationId.equals("")) ? Long.parseLong(operationId) : null ); 
		result.setOperation_name(Utils.varToString(jpa[1]));
		result.setRoles(null);   // set it somewhere else.
		return result; 
	}

	/**
	 * maps to VO
	 */	
	public VtbObject mapHeaders(Object jpaObject) throws MappingException {
		Object jpa = jpaObject;
		RolesToStageReportHeader result = new RolesToStageReportHeader();		
		result.setProcess_name(Utils.varToString(jpa));
		return result; 
	}

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public RolesToStageReportHeader getHeaderData(Long processId) 
		throws MappingException {
		// TODO : maybe, transform into JPA query!
		String sqlStr = 
		  "select description_process " 
		+ "  from type_process " 
		+ "  where id_type_process = ?1 ";  

		Query query = null;		
		try {
			query = getEntityMgr().createNativeQuery(sqlStr);
			query.setParameter(1, processId);		
			List<Object> objectList = query.getResultList();			
			// always one row
			Iterator<Object> iterator = objectList.iterator(); 
			Object rs = (Object) iterator.next();
			return (RolesToStageReportHeader) mapHeaders(rs);
		} catch (Exception e) {
			throw new MappingException(e, ("Exception caught in getHeaderData " + e));			
		}
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<RolesToStageReportStage> getReportData(Long processId, Long stageId) 
				throws MappingException {
		String sqlStr =
		"select distinct s.id_stage, s.description_stage "
		+ "from stages s "
		+ "where (s.id_stage = ?1 or -1 = ?2) and s.active = 1 and s.id_type_process = ?3 "
		+ "order by s.description_stage ";

		Query query = null;		
		try {
			query = getEntityMgr().createNativeQuery(sqlStr);
			query.setParameter(1, stageId);
			query.setParameter(2, stageId);
			query.setParameter(3, processId);

			List<Object[]> objectList = query.getResultList();
			ArrayList<RolesToStageReportStage> list = new ArrayList<RolesToStageReportStage>();
			for (Iterator<Object[]> iterator = objectList.iterator(); iterator.hasNext();) {
				Object[] rs = (Object[]) iterator.next();
				// Обработать результаты	
				RolesToStageReportStage operation = (RolesToStageReportStage) map((Object)rs);
				list.add(operation);
			}
			return list;
		} catch (Exception e) {
			throw new MappingException(e, ("Exception caught in getReportData " + e));			
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getRoles(Long stageId) throws MappingException {
		String sqlStr =
			"select distinct r.name_role "
			+ "from stages s  "
			+ "       left join stages_in_role sr on sr.id_stage = s.id_stage " 
			+ "       left join roles r on r.id_role = sr.id_role  and r.active = 1 "
			+ "       left join stages_permissions sp on (s.id_stage = sp.id_stage and sp.id_permission = 3) " 
			+ "where (s.id_stage = ?1)  and s.active = 1 "
			+ "order by r.name_role ";

		Query query = null;		
		try {
			query = getEntityMgr().createNativeQuery(sqlStr);
			query.setParameter(1, stageId);
			
			List<Object> objectList = query.getResultList();
			ArrayList<String> list = new ArrayList<String>();
			for (Iterator<Object> iterator = objectList.iterator(); iterator.hasNext();) {
				Object rs = (Object) iterator.next();	
				String available_user = (String)rs;
				list.add(available_user);
			}
			return list;
		} catch (Exception e) {
			throw new MappingException(e, ("Exception caught in getVariables " + e));
		}
	}
}
