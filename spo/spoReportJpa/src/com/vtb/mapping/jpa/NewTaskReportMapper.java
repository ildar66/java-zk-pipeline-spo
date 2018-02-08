package com.vtb.mapping.jpa;

import java.util.List;

import javax.persistence.Query;

import ru.masterdm.compendium.domain.VtbObject;

import com.vtb.domain.NewTaskReport;
import com.vtb.domain.NewTaskReportRecord;
import com.vtb.exception.DuplicateKeyException;
import com.vtb.exception.MappingException;
import com.vtb.exception.NoSuchObjectException;
import com.vtb.utils.Utils;

/**
 * NewTaskReportMapper implementation.
 * @author Michael Kuznetsov
 */
public class NewTaskReportMapper extends DomainJPAMapper 
	implements com.vtb.mapping.NewTaskReportMapper {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<NewTaskReport> findAll() throws MappingException {
		throw new NoSuchObjectException("NewTaskReportMapper.findAll. Method is not implemented ");	
	}

	
	/**
	 * Retrieve a single object matching this object.
	 *
	 * @return VtbObject
	 */
	public NewTaskReport findByPrimaryKey(NewTaskReport anObject) throws NoSuchObjectException, MappingException {
		throw new NoSuchObjectException("NewTaskReportMapper.findByPrimaryKey. Method is not implemented ");	
	}
	
	/**
	 * maps to VO
	 */	
	public VtbObject map(Object jpaObject) throws MappingException {
		Object[] jpa = (Object[]) jpaObject;
		NewTaskReportRecord result = new NewTaskReportRecord();		
		result.setClaim_name_internal(Utils.varToString(jpa[0]));
        result.setClaim_name_CRM(Utils.varToString(jpa[1]));
        result.generateClaimName();

        result.setDescriptionProcess (Utils.varToString(jpa[2]));
		result.setOperationDescription(Utils.varToString(jpa[3]));
		return result; 
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void insert(NewTaskReport anObject) throws DuplicateKeyException, MappingException {
		throw new NoSuchObjectException("NewTaskReportMapper.insert. Method is not implemented ");	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(NewTaskReport anObject) throws NoSuchObjectException, MappingException {
		throw new NoSuchObjectException("NewTaskReportMapper.update. Method is not implemented ");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(NewTaskReport anObject) throws NoSuchObjectException, MappingException {
		throw new NoSuchObjectException("NewTaskReportMapper.remove. Method is not implemented ");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public NewTaskReportRecord getRecord(Long taskId) throws MappingException {
		String sqlStr =
	         "select distinct TO_CHAR(m.mdtask_number) as claim_name, "
                + "  m.crmcode,  "
			    + "   tp.description_process, s.description_stage "
				+ "from tasks t "
				+ "INNER JOIN stages s ON s.id_stage = t.id_stage_to "
				+ "inner join type_process tp on tp.id_type_process = t.id_type_process "
				+ "inner join mdtask m on m.id_pup_process = t.id_process "
				+ "where t.id_task = ?1 ";
		Query query = null;		
		try {
			query = getEntityMgr().createNativeQuery(sqlStr);
			query.setParameter(1, taskId);

			Object object = query.getSingleResult();
			NewTaskReportRecord record = (NewTaskReportRecord) map((Object[])object);
			return record;
		} catch (Exception e) {
			throw new MappingException(e, ("Exception caught in getRecord " + e));			
		}
	}
}
