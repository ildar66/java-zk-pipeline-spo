package com.vtb.mapping.jpa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Query;

import com.vtb.domain.NewDocumentsByClaimsReport;
import com.vtb.domain.NewDocumentsByClaimsReportClaim;
import com.vtb.domain.NewDocumentsByClaimsReportHeader;
import com.vtb.domain.NewDocumentsByClaimsReportOperation;

import com.vtb.domain.VtbObject;
import com.vtb.exception.DuplicateKeyException;
import com.vtb.exception.MappingException;
import com.vtb.exception.NoSuchObjectException;
import com.vtb.utils.Utils;

/**
 * NewDocumentsByClaimsReportMapper implementation.
 * @author Michael Kuznetsov
 */
public class NewDocumentsByClaimsReportMapper extends DomainJPAMapper  
				implements com.vtb.mapping.NewDocumentsByClaimsReportMapper {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<NewDocumentsByClaimsReport> findAll() throws MappingException {
		throw new NoSuchObjectException("NewDocumentsByClaimsReportReportMapper.findAll. Method is not implemented ");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public NewDocumentsByClaimsReport findByPrimaryKey(NewDocumentsByClaimsReport anObject) throws NoSuchObjectException, MappingException {
		throw new NoSuchObjectException("NewDocumentsByClaimsReportReportMapper.findByPrimaryKey. Method is not implemented ");	
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void insert(NewDocumentsByClaimsReport anObject) throws DuplicateKeyException, MappingException {
		throw new NoSuchObjectException("NewDocumentsByClaimsReportReportMapper.insert. Method is not implemented ");	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(NewDocumentsByClaimsReport anObject) throws NoSuchObjectException, MappingException {
		throw new NoSuchObjectException("NewDocumentsByClaimsReportReportMapper.update. Method is not implemented ");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(NewDocumentsByClaimsReport anObject) throws NoSuchObjectException, MappingException {
		throw new NoSuchObjectException("NewDocumentsByClaimsReportReportMapper.remove. Method is not implemented ");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public String getHeaderData(Long departmentId) 
		throws MappingException {
		String sqlStr = 
		    "select "
		  + "  decode  ( ?1, "
		  + "	  -1,  'Все подразделения', "
		  + "	 (select t.shortname from departments t where t.id_department = ?2)) as department_name "
		  + "from dual ";
		Query query = null;		
		try {
			query = getEntityMgr().createNativeQuery(sqlStr);
			query.setParameter(1, departmentId);
			query.setParameter(2, departmentId);
			List<Object> objectList = query.getResultList();			
			// always one row
			Iterator<Object> iterator = objectList.iterator(); 
			Object rs = (Object) iterator.next();
			return (String) rs;
		} catch (Exception e) {
			throw new MappingException(e, ("Exception caught in getHeaderData " + e));			
		}
	}
	
	/**
	 * maps to VO
	 */	
	public VtbObject mapClaims(Object jpa) throws MappingException {
		NewDocumentsByClaimsReportClaim result = new NewDocumentsByClaimsReportClaim();
		String claimId = Utils.varToString(jpa);
		result.setClaimId( (claimId != null && !claimId.equals("")) ? Long.parseLong(claimId) : null ); 
		result.setOperations(null); // set it somewhere else.
		return result; 
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<NewDocumentsByClaimsReportClaim> getClaims(NewDocumentsByClaimsReportHeader header, Long departmentId) 
				throws MappingException {
		String sqlStr = 
	   	  "select distinct order_number "
		+ "from appfiles f "
		+ "  left join  "
		+ "  ( "
		+ "    select mdtask_number as order_number, fullname as department, m.id_pup_process "
		+ "    from mdtask m "
		+ "    inner join departments d on d.id_department = m.initdepartment "
		+ "    where (d.id_department = ?1 or -1 = ?2) "
		+ "  ) av on f.id_owner = trim(to_char(av.id_pup_process, '99999999999999')) "
		+ "where f.owner_type = 0  "
		+ "  and f.date_of_addition between to_date(?3, 'DD.MM.YYYY') and to_date(?4, 'DD.MM.YYYY')+1 "
		+ "order by order_number ";

		Query query = null;		
		try {
			query = getEntityMgr().createNativeQuery(sqlStr);
			query.setParameter(1, departmentId);
			query.setParameter(2, departmentId);
			query.setParameter(3, header.getSendLeftDate());
			query.setParameter(4, header.getSendRightDate());
			List<Object> objectList = query.getResultList();
			ArrayList<NewDocumentsByClaimsReportClaim> list = new ArrayList<NewDocumentsByClaimsReportClaim>();
			for (Iterator<Object> iterator = objectList.iterator(); iterator.hasNext();) {
				Object rs = (Object) iterator.next();
				// Обработать результаты	
				NewDocumentsByClaimsReportClaim claim = (NewDocumentsByClaimsReportClaim) mapClaims(rs);
				list.add(claim);
			}
			return list;
		} catch (Exception e) {
			throw new MappingException(e, ("Exception caught in getDepartments " + e));			
		}
	}
	
	/**
	 * maps to VO
	 */	
	public VtbObject mapOperations(Object jpaObject) throws MappingException {
		Object[] jpa = (Object[]) jpaObject;
		NewDocumentsByClaimsReportOperation result = new NewDocumentsByClaimsReportOperation();
		result.setFiletype(Utils.varToString(jpa[0]));
		result.setFilename(Utils.varToString(jpa[1]));
		result.setDateOfAddition(Utils.varToString(jpa[2]));
		result.setDepartment(Utils.varToString(jpa[3]));

		String claimId = Utils.varToString(jpa[4]);
		result.setClaimId((claimId != null && !claimId.equals("")) ? Long.parseLong(claimId) : null);

		result.setStatus(Utils.varToString(jpa[5]));		   
		return result; 
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<NewDocumentsByClaimsReportOperation> getOperationsForClaim(
			NewDocumentsByClaimsReportHeader header, Long departmentId, Long claimId) throws MappingException {
		String sqlStr =
		"select f.filetype, "
		+ "     f.filename, " 
		+ "     f.date_of_addition, "
		+ "     department, "
		+ "     order_number, "
		+ "     (case  "
		+ "        when length(f.filedata) = 0 then 'не доставлено' "
		+ "           else 'доставлено'  "
		+ "	       end) as status "
		+ "from appfiles f "
		+ "	left join  "
		+ "	  ( "
		+ "	    select mdtask_number as order_number, fullname as department, m.id_pup_process "
		+ "	    from mdtask m "
		+ "	    inner join departments d on d.id_department = m.initdepartment "
		+ "	    where (d.id_department = ?1 or -1 = ?2)  "
		+ "	  ) av on f.id_owner = trim(to_char(av.id_pup_process, '99999999999999')) "
		+ "where f.owner_type = 0  "
		+ "	    and f.date_of_addition between to_date(?3, 'DD.MM.YYYY') and to_date(?4, 'DD.MM.YYYY')+1 "
		+ "	    and av.order_number = ?5 "
		+ "order by order_number, filetype, filename, date_of_addition, department, status ";
		
		Query query = null;		
		try {
			query = getEntityMgr().createNativeQuery(sqlStr);
			query.setParameter(1, departmentId);
			query.setParameter(2, departmentId);
			query.setParameter(3, header.getSendLeftDate());
			query.setParameter(4, header.getSendRightDate());
			query.setParameter(5, claimId);
			
			List<Object[]> objectList = query.getResultList();
			ArrayList<NewDocumentsByClaimsReportOperation> list = new ArrayList<NewDocumentsByClaimsReportOperation>();
			for (Iterator<Object[]> iterator = objectList.iterator(); iterator.hasNext();) {
				Object[] rs = (Object[]) iterator.next();
				// Обработать результаты	
				NewDocumentsByClaimsReportOperation operation = (NewDocumentsByClaimsReportOperation) mapOperations((Object)rs);
				list.add(operation);
			}
			return list;
		} catch (Exception e) {
			throw new MappingException(e, ("Exception caught in getUsersOfDepartment " + e));			
		}
	}
}
