package com.vtb.mapping.jpa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Query;

import com.vtb.domain.RolesOfUsersReport;
import com.vtb.domain.RolesOfUsersReportDepartment;
import com.vtb.domain.RolesOfUsersReportHeader;
import com.vtb.domain.RolesOfUsersReportUser;
import com.vtb.domain.VtbObject;
import com.vtb.exception.DuplicateKeyException;
import com.vtb.exception.MappingException;
import com.vtb.exception.NoSuchObjectException;
import com.vtb.utils.Utils;

/**
 * RolesOfUsersReportMapper implementation.
 * @author Michael Kuznetsov
 */
public class RolesOfUsersReportMapper extends DomainJPAMapper  
				implements com.vtb.mapping.RolesOfUsersReportMapper {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<RolesOfUsersReport> findAll() throws MappingException {
		throw new NoSuchObjectException("RolesOfUsersReportMapper.findAll. Method is not implemented ");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public RolesOfUsersReport findByPrimaryKey(RolesOfUsersReport anObject) throws NoSuchObjectException, MappingException {
		throw new NoSuchObjectException("RolesOfUsersReportMapper.findByPrimaryKey. Method is not implemented ");	
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void insert(RolesOfUsersReport anObject) throws DuplicateKeyException, MappingException {
		throw new NoSuchObjectException("RolesOfUsersReportMapper.insert. Method is not implemented ");	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(RolesOfUsersReport anObject) throws NoSuchObjectException, MappingException {
		throw new NoSuchObjectException("RolesOfUsersReportMapper.update. Method is not implemented ");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(RolesOfUsersReport anObject) throws NoSuchObjectException, MappingException {
		throw new NoSuchObjectException("RolesOfUsersReportMapper.remove. Method is not implemented ");
	}

	/**
	 * maps to VO
	 */	
	public VtbObject mapDepartments(Object[] jpa) throws MappingException {
		RolesOfUsersReportDepartment result = new RolesOfUsersReportDepartment();
		String departmentId = Utils.varToString(jpa[0]);
		result.setDepartmentId( (departmentId != null && !departmentId.equals("")) ? Long.parseLong(departmentId) : null ); 
		result.setDepartment_name(Utils.varToString(jpa[1]));
		result.setUsers(null);   // set it somewhere else.
		return result; 
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<RolesOfUsersReportDepartment> getDepartments(Long departmentId) 
				throws MappingException {
		String sqlStr =
		  "select d.id_department, d.shortname from departments d "
		+ "where d.id_department = ?1 or -1 = ?2 "
		+ "order by d.shortname ";
		Query query = null;		
		try {
			query = getEntityMgr().createNativeQuery(sqlStr);
			query.setParameter(1, departmentId);
			query.setParameter(2, departmentId);
			List<Object[]> objectList = query.getResultList();
			ArrayList<RolesOfUsersReportDepartment> list = new ArrayList<RolesOfUsersReportDepartment>();
			for (Iterator<Object[]> iterator = objectList.iterator(); iterator.hasNext();) {
				Object[] rs = (Object[]) iterator.next();
				// Обработать результаты	
				RolesOfUsersReportDepartment operation = (RolesOfUsersReportDepartment) mapDepartments(rs);
				list.add(operation);
			}
			return list;
		} catch (Exception e) {
			throw new MappingException(e, ("Exception caught in getDepartments " + e));			
		}
	}
	
	/**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<RolesOfUsersReportDepartment> getDepartmentsList(String[] departmentsList) throws MappingException {
        StringBuilder inString = new StringBuilder();
        for (String dep : departmentsList) inString.append(dep + ",");
        inString.replace(inString.length()-1, inString.length(), "");
        String sqlStr =
            "select d.id_department, d.shortname from departments d "
              + " where d.id_department in (" + inString.toString() + " )"
              + " and d.id_department <> -1 "
              + " order by d.shortname ";
              Query query = null;     
        try {
          query = getEntityMgr().createNativeQuery(sqlStr);
          List<Object[]> objectList = query.getResultList();
          ArrayList<RolesOfUsersReportDepartment> list = new ArrayList<RolesOfUsersReportDepartment>();
          for (Iterator<Object[]> iterator = objectList.iterator(); iterator.hasNext();) {
              Object[] rs = (Object[]) iterator.next();
              // Обработать результаты    
              RolesOfUsersReportDepartment operation = (RolesOfUsersReportDepartment) mapDepartments(rs);
              list.add(operation);
          }
          return list;
        } catch (Exception e) {
            throw new MappingException(e, ("Exception caught in getDepartmentsList " + e));         
        }        
    }
	
	/**
	 * maps to VO
	 */	
	public VtbObject mapUsers(Object jpaObject, String departmentName) throws MappingException {
		Object[] jpa = (Object[]) jpaObject;
		RolesOfUsersReportUser result = new RolesOfUsersReportUser();
		String userId = Utils.varToString(jpa[0]);
		result.setUserId((userId != null && !userId.equals("")) ? Long.parseLong(userId) : null);
		result.setUser_fio(Utils.varToString(jpa[1]));
		result.setUser_login(Utils.varToString(jpa[2]));
		result.setRoles(null); // set it somewhere else.
		result.setDepartment_name(departmentName);   
		return result; 
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<RolesOfUsersReportUser> getUsersOfDepartment(Long departmentId, String departmentName) 
				throws MappingException {
		String sqlStr =
		  "select u.id_user, " 
		+ "  u.surname || ' ' || u.name || ' ' || u.patronymic as fio, "
		+ "  u.login "
		+ " from users u "
		+ "where u.id_department = ?1 and u.is_active = 1 "
		+ "	order by fio ";
		Query query = null;		
		try {
			query = getEntityMgr().createNativeQuery(sqlStr);
			query.setParameter(1, departmentId);
			List<Object[]> objectList = query.getResultList();
			ArrayList<RolesOfUsersReportUser> list = new ArrayList<RolesOfUsersReportUser>();
			for (Iterator<Object[]> iterator = objectList.iterator(); iterator.hasNext();) {
				Object[] rs = (Object[]) iterator.next();
				// Обработать результаты	
				RolesOfUsersReportUser operation = (RolesOfUsersReportUser) mapUsers((Object)rs, departmentName);
				list.add(operation);
			}
			return list;
		} catch (Exception e) {
			throw new MappingException(e, ("Exception caught in getUsersOfDepartment " + e));			
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<String> getRoles(Long processId, Long userId, Long roleId) throws MappingException {
		String sqlStr =
		   "select distinct r.name_role "
		 + "from roles r "
		 + "inner join user_in_role ur on r.id_role = ur.id_role and ur.id_user = ?1 and ur.status = 'Y' " 
		 + "where (r.id_role = ?2 or  -1 = ?3) and r.active = 1 "
		 + "and r.id_type_process = ?4 "
		 + "order by r.name_role ";

		Query query = null;		
		try {
			query = getEntityMgr().createNativeQuery(sqlStr);
			query.setParameter(1, userId);
			query.setParameter(2, roleId);
			query.setParameter(3, roleId);
			query.setParameter(4, processId);
			
			List<Object> objectList = query.getResultList();
			ArrayList<String> list = new ArrayList<String>();
			for (Iterator<Object> iterator = objectList.iterator(); iterator.hasNext();) {
				list.add((String) iterator.next());
			}
			return list;
		} catch (Exception e) {
			throw new MappingException(e, ("Exception caught in getRoles " + e));			
		}
	}

	/**
	 * maps to VO
	 */	
	public VtbObject mapHeaders(Object[] jpa) throws MappingException {
		RolesOfUsersReportHeader result = new RolesOfUsersReportHeader();		
		result.setProcess_name(Utils.varToString(jpa[0]));
		result.setDepartment_name(Utils.varToString(jpa[1]));
		result.setRole_name(Utils.varToString(jpa[2]));
		return result; 
	}

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public RolesOfUsersReportHeader getHeaderData(Long processId, Long departmentId, Long roleId) 
		throws MappingException {
		// TODO : maybe, transform into JPA query!
		String sqlStr = 
		    "select "
		  + "  decode  ( ?1, "
		  + "	  -1,  'Все процессы', "
		  + "	 (select t.description_process  from type_process t  where t.id_type_process = ?2)) as process_type, "
		  + "  decode  ( ?3, "
		  + "	  -1,  'Все подразделения', "
		  + "	 (select t.shortname from departments t where t.id_department = ?4)) as department_name, "
		  + "  decode  ( ?5, "
		  + "	  -1,  'Все роли', "
		  + "	 (select r.name_role from roles r where r.id_role = ?6)) as role_name "
		  + "from dual ";
		Query query = null;		
		try {
			query = getEntityMgr().createNativeQuery(sqlStr);
			query.setParameter(1, processId);		
			query.setParameter(2, processId);
			query.setParameter(3, departmentId);
			query.setParameter(4, departmentId);
			query.setParameter(5, roleId);	
			query.setParameter(6, roleId);
			List<Object> objectList = query.getResultList();			
			// always one row
			Iterator<Object> iterator = objectList.iterator(); 
			Object[] rs = (Object[]) iterator.next();
			return (RolesOfUsersReportHeader) mapHeaders(rs);
		} catch (Exception e) {
			throw new MappingException(e, ("Exception caught in getHeaderData " + e));			
		}
	}
}
