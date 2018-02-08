package com.vtb.mapping.jpa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Query;

import com.vtb.domain.VariablesToStageReport;
import com.vtb.domain.VariablesToStageReportHeader;
import com.vtb.domain.VariablesToStageReportStage;
import com.vtb.domain.VtbObject;
import com.vtb.exception.DuplicateKeyException;
import com.vtb.exception.MappingException;
import com.vtb.exception.NoSuchObjectException;
import com.vtb.utils.Utils;

/**
 * VariablesToOperationReportMapper implementation.
 * @author Michael Kuznetsov
 */
public class VariablesToStageReportMapper extends DomainJPAMapper  
				implements com.vtb.mapping.VariablesToStageReportMapper {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<VariablesToStageReport> findAll() throws MappingException {
		throw new NoSuchObjectException("VariablesToOperationReportMapper.findAll. Method is not implemented ");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public VariablesToStageReport findByPrimaryKey(VariablesToStageReport anObject) throws NoSuchObjectException, MappingException {
		throw new NoSuchObjectException("VariablesToStageReportMapper.findByPrimaryKey. Method is not implemented ");	
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void insert(VariablesToStageReport anObject) throws DuplicateKeyException, MappingException {
		throw new NoSuchObjectException("VariablesToStageReportMapper.insert. Method is not implemented ");	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(VariablesToStageReport anObject) throws NoSuchObjectException, MappingException {
		throw new NoSuchObjectException("VariablesToStageReportMapper.update. Method is not implemented ");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(VariablesToStageReport anObject) throws NoSuchObjectException, MappingException {
		throw new NoSuchObjectException("VariablesToStageReportMapper.remove. Method is not implemented ");
	}

	
	/**
	 * maps to VO
	 */	
	public VtbObject map(Object jpaObject) throws MappingException {
		Object[] jpa = (Object[]) jpaObject;
		VariablesToStageReportStage result = new VariablesToStageReportStage();
		String stageId = Utils.varToString(jpa[0]);
		result.setStageId( (stageId != null && !stageId.equals("")) ? Long.parseLong(stageId) : null ); 
		result.setStage_name(Utils.varToString(jpa[1]));
		result.setVariables(null);   // set it somewhere else.
		return result; 
	}

	/**
	 * maps to VO
	 */	
	public VtbObject mapHeaders(Object jpaObject) throws MappingException {
		Object jpa = jpaObject;
		VariablesToStageReportHeader result = new VariablesToStageReportHeader();		
		result.setProcess_name(Utils.varToString(jpa));
		return result; 
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public VariablesToStageReportHeader getHeaderData(Long processId) throws MappingException {
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
			return (VariablesToStageReportHeader) mapHeaders(rs);
		} catch (Exception e) {
			throw new MappingException(e, ("Exception caught in getHeaderData " + e));			
		}
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<VariablesToStageReportStage> getReportData(Long processId, Long stageId)  throws MappingException {
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
			ArrayList<VariablesToStageReportStage> list = new ArrayList<VariablesToStageReportStage>();
			for (Iterator<Object[]> iterator = objectList.iterator(); iterator.hasNext();) {
				Object[] rs = (Object[]) iterator.next();
				// Обработать результаты	
				VariablesToStageReportStage operation = (VariablesToStageReportStage) map((Object)rs);
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
	public List<String> getVariables(Long stageId) throws MappingException {
		String sqlStr =
			"select distinct v.name_var " 
			+ "from stages s "
			+ "  left join stages_in_role sr on sr.id_stage = s.id_stage " 
			+ "       left join stages_permissions sp on (s.id_stage = sp.id_stage and sp.id_permission = 3) " 
			+ "       left join variables v on sp.id_var = v.id_var "
			+ "where (s.id_stage = ?1) and s.active = 1 "
			+ "order by v.name_var ";

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
