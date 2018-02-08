package com.vtb.mapping.jpa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Query;

import com.vtb.domain.NewDocumentsByOrgsReport;
import com.vtb.domain.NewDocumentsByOrgsReportOrg;
import com.vtb.domain.NewDocumentsByOrgsReportHeader;
import com.vtb.domain.NewDocumentsByOrgsReportOperation;

import com.vtb.domain.VtbObject;
import com.vtb.exception.DuplicateKeyException;
import com.vtb.exception.MappingException;
import com.vtb.exception.NoSuchObjectException;
import com.vtb.utils.Utils;

/**
 * NewDocumentsByOrgsReportMapper implementation.
 * @author Michael Kuznetsov
 */
public class NewDocumentsByOrgsReportMapper extends DomainJPAMapper  
				implements com.vtb.mapping.NewDocumentsByOrgsReportMapper {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<NewDocumentsByOrgsReport> findAll() throws MappingException {
		throw new NoSuchObjectException("NewDocumentsByOrgsReportReportMapper.findAll. Method is not implemented ");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public NewDocumentsByOrgsReport findByPrimaryKey(NewDocumentsByOrgsReport anObject) throws NoSuchObjectException, MappingException {
		throw new NoSuchObjectException("NewDocumentsByOrgsReportReportMapper.findByPrimaryKey. Method is not implemented ");	
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void insert(NewDocumentsByOrgsReport anObject) throws DuplicateKeyException, MappingException {
		throw new NoSuchObjectException("NewDocumentsByOrgsReportReportMapper.insert. Method is not implemented ");	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(NewDocumentsByOrgsReport anObject) throws NoSuchObjectException, MappingException {
		throw new NoSuchObjectException("NewDocumentsByOrgsReportReportMapper.update. Method is not implemented ");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(NewDocumentsByOrgsReport anObject) throws NoSuchObjectException, MappingException {
		throw new NoSuchObjectException("NewDocumentsByOrgsReportReportMapper.remove. Method is not implemented ");
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
	public VtbObject mapOrganizations(Object jpa) throws MappingException {
		NewDocumentsByOrgsReportOrg result = new NewDocumentsByOrgsReportOrg();
		result.setOrganizationName(Utils.varToString(jpa));
		result.setOperations(null); // set it somewhere else.
		return result; 
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<NewDocumentsByOrgsReportOrg> getOrganizations(NewDocumentsByOrgsReportHeader header, Long departmentId) 
				throws MappingException {
		String sqlStr = 
			  "select distinct vc.organizationname " 
			+ "from appfiles f "
			+ " 	inner join v_crm_organisation vc on trim(vc.accountid) = f.id_owner " // 'A6UJ9A004QPW' 
			+ "		left join  "
			+ "		  (select d.fullname as department, m.id_pup_process " 
			+ "		   from mdtask m "
			+ "		   inner join departments d on d.id_department = m.initdepartment "
			+ "		   where (d.id_department = ?1 or -1 = ?2 ) "
			+ "		   ) av on f.id_owner = trim(to_char(av.id_pup_process, '99999999999999')) "
			+ "where f.owner_type = 1 " // 0   
			+ "    and f.date_of_addition between to_date(?3, 'DD.MM.YYYY') and to_date(?4, 'DD.MM.YYYY')+1 "
			+ "order by organizationname ";

		Query query = null;		
		try {
			query = getEntityMgr().createNativeQuery(sqlStr);
			query.setParameter(1, departmentId);
			query.setParameter(2, departmentId);
			query.setParameter(3, header.getSendLeftDate());
			query.setParameter(4, header.getSendRightDate());
			List<Object> objectList = query.getResultList();
			ArrayList<NewDocumentsByOrgsReportOrg> list = new ArrayList<NewDocumentsByOrgsReportOrg>();
			for (Iterator<Object> iterator = objectList.iterator(); iterator.hasNext();) {
				Object rs = (Object) iterator.next();
				// Обработать результаты	
				NewDocumentsByOrgsReportOrg claim = (NewDocumentsByOrgsReportOrg) mapOrganizations(rs);
				list.add(claim);
			}
			return list;
		} catch (Exception e) {
			throw new MappingException(e, ("Exception caught in getOrganizations " + e));			
		}
	}
	
	/**
	 * maps to VO
	 */	
	public VtbObject mapOperations(Object jpaObject) throws MappingException {
		Object[] jpa = (Object[]) jpaObject;
		NewDocumentsByOrgsReportOperation result = new NewDocumentsByOrgsReportOperation();
		result.setFiletype(Utils.varToString(jpa[0]));
		result.setFilename(Utils.varToString(jpa[1]));
		result.setDateOfAddition(Utils.varToString(jpa[2]));
		result.setOrganizationName(Utils.varToString(jpa[3]));
		result.setDepartment(Utils.varToString(jpa[4]));		
		result.setStatus(Utils.varToString(jpa[5]));		   
		return result; 
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<NewDocumentsByOrgsReportOperation> getOperationsForOrganization(
			NewDocumentsByOrgsReportHeader header, Long departmentId, String org_name) throws MappingException {
		String sqlStr =
			"select distinct f.filetype, "
			+ "     f.filename, " 
			+ "		f.date_of_addition, "
			+ "		vc.organizationname, "
			+ "		department, "
			+ "		(case  "
			+ "		     when length(f.filedata) = 0 then 'не доставлено' "
			+ "		     else 'доставлено'  "
			+ "		end) as status "
			+ "from appfiles f "
			+ " 	inner join v_crm_organisation vc on trim(vc.accountid) = f.id_owner "  // 'A6UJ9A004QPW' 
			+ "		left join  "

			// ATTENTION!!! The WORING CODE BELOW (MDTASK NEVER HAS THE FOLLOWING CODES)!!! TODO : CHANGE!!! 
			+ "		  (select d.fullname as department, m.id_pup_process " 
			+ "		   from mdtask m "
			+ "		   inner join departments d on d.id_department = m.initdepartment "
			+ "		   where (d.id_department = ?1 or -1 = ?2 ) "
			+ "		   ) av on f.id_owner = trim(to_char(av.id_pup_process, '99999999999999')) "
			+ "where f.owner_type = 1 " // 0   
			+ "    and f.date_of_addition between to_date(?3, 'DD.MM.YYYY') and to_date(?4, 'DD.MM.YYYY')+1 "
			+ "    and  vc.organizationname = ?5 "    
			+ "order by organizationname, filetype, filename, date_of_addition, department, status ";

		Query query = null;		
		try {
			query = getEntityMgr().createNativeQuery(sqlStr);
			query.setParameter(1, departmentId);
			query.setParameter(2, departmentId);
			query.setParameter(3, header.getSendLeftDate());
			query.setParameter(4, header.getSendRightDate());
			query.setParameter(5, org_name);
			
			List<Object[]> objectList = query.getResultList();
			ArrayList<NewDocumentsByOrgsReportOperation> list = new ArrayList<NewDocumentsByOrgsReportOperation>();
			for (Iterator<Object[]> iterator = objectList.iterator(); iterator.hasNext();) {
				Object[] rs = (Object[]) iterator.next();
				// Обработать результаты	
				NewDocumentsByOrgsReportOperation operation = (NewDocumentsByOrgsReportOperation) mapOperations((Object)rs);
				list.add(operation);
			}
			return list;
		} catch (Exception e) {
			throw new MappingException(e, ("Exception caught in getOperationsForOrganization " + e));			
		}
	}
}
