package com.vtb.mapping.jpa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Query;

import com.vtb.domain.RoleTreeReport;
import com.vtb.domain.RoleTreeReportDepartment;
import com.vtb.domain.RoleTreeReportHeader;
import com.vtb.domain.RoleTreeReportOperation;
import com.vtb.domain.VtbObject;
import com.vtb.exception.DuplicateKeyException;
import com.vtb.exception.MappingException;
import com.vtb.exception.NoSuchObjectException;
import com.vtb.utils.Utils;

/**
 * RoleTreeReportMapper implementation.
 * @author Michael Kuznetsov
 */
public class RoleTreeReportMapper extends DomainJPAMapper  
				implements com.vtb.mapping.RoleTreeReportMapper {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<RoleTreeReport> findAll() throws MappingException {
		throw new NoSuchObjectException("RoleTreeReportReportMapper.findAll. Method is not implemented ");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public RoleTreeReport findByPrimaryKey(RoleTreeReport anObject) throws NoSuchObjectException, MappingException {
		throw new NoSuchObjectException("RoleTreeReportReportMapper.findByPrimaryKey. Method is not implemented ");	
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void insert(RoleTreeReport anObject) throws DuplicateKeyException, MappingException {
		throw new NoSuchObjectException("RoleTreeReportReportMapper.insert. Method is not implemented ");	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(RoleTreeReport anObject) throws NoSuchObjectException, MappingException {
		throw new NoSuchObjectException("RoleTreeReportReportMapper.update. Method is not implemented ");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(RoleTreeReport anObject) throws NoSuchObjectException, MappingException {
		throw new NoSuchObjectException("RoleTreeReportReportMapper.remove. Method is not implemented ");
	}

	/**
	 * maps to VO
	 */	
	public VtbObject mapHeaders(Object[] jpa) throws MappingException {
		RoleTreeReportHeader result = new RoleTreeReportHeader();		
		result.setProcess_name(Utils.varToString(jpa[0]));
		result.setDepartment_name(Utils.varToString(jpa[1]));
		result.setShowactiveflag(Utils.varToString(jpa[2]));
		return result; 
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public RoleTreeReportHeader getHeaderData(Long processId, Long departmentId, String showUnactive) 
		throws MappingException {
		String sqlStr = 
			  "select " 
			+ "  (select description_process from type_process where id_type_process = ?1)  as description_process, "
			+ "  (select dep.shortname from departments dep where dep.id_department  = ?2)  as department, "
			+ "  decode  ( ?3, "
			+ "           '0',  'только активные роли указанного процесса', "
			+ "           '1',  'все роли указанного процесса', "
			+ "                 'все роли указанного процесса') as showActiveFlag " 
			+ "from dual";
		
		Query query = null;		
		try {
			query = getEntityMgr().createNativeQuery(sqlStr);
			query.setParameter(1, processId);		
			query.setParameter(2, departmentId);
			query.setParameter(3, showUnactive);
			List<Object> objectList = query.getResultList();			
			// always one row
			Iterator<Object> iterator = objectList.iterator(); 
			Object[] rs = (Object[]) iterator.next();
			RoleTreeReportHeader header = (RoleTreeReportHeader) mapHeaders(rs); 
			if ((departmentId != null) && (departmentId.longValue() == -1L)) header.setDepartment_name("Все подразделения");
			return header;
		} catch (Exception e) {
			throw new MappingException(e, ("Exception caught in getHeaderData " + e));			
		}
	}
	
	/**
	 * maps to VO
	 */	
	public VtbObject mapOperations(Object[] jpa) throws MappingException {
		RoleTreeReportOperation result = new RoleTreeReportOperation();
		result.setRoleId(Utils.objToLong(jpa[0])); 
		result.setParentRole(Utils.objToLong(jpa[1]));
		result.setLevel(Utils.objToLong(jpa[2]));
		result.setRole_name(Utils.varToString(jpa[3]));
		result.setTypeProcessId(Utils.objToLong(jpa[4]));
		result.setStatus(Utils.varToString(jpa[5]));
		result.setUsage(Utils.varToString(jpa[6]));
		result.setSortOrder(Utils.objToLong(jpa[7]));
		result.setUsers(null);   // set it somewhere else.
		return result; 
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<RoleTreeReportOperation> getOperations(Long processId, String showUnactive) 
				throws MappingException {
		StringBuilder sb = new StringBuilder(); 
		sb.append("select t.id_role, ")
			.append("     h.role_parent, ") 
			.append("     h.lvl-1 as special_level, LPAD(' ',8*(lvl-1))|| t.name_role as special_name_role, ") 
			.append("     t.id_type_process,  ")
			.append("     (case when t.active = 0 then 'роль сейчас не участвует в бизнес-поцессе: не назначайте на нее пользователей' else 'активна' end) as status, ") 
			.append("     decode ( us.usage,  ")
			.append("              NULL, '<в описании бизнес-процесса отсутствуют назначения на операции для данной роли>',  ' ') as flag_usage, ")     
			.append(" 	  h.sort_order  ")
			.append("from roles t  ")
			.append("    inner join  ")
			.append(" 	 ( ")
					        //-- Шаг 3. Уберем все подыерархии и восстановим значения sort_order и role_parent, сджойнив выборки
					        //-- Таблицы u и v фактически идентичны
			.append(" 	   select u.* ") 
			.append(" 	   from  ")
			.append("        (    select distinct ROLE_PARENT, ROLE_CHILD, LEVEL as LVL, ROWNUM as sort_order ")
			.append("           from role_nodes_full ")
			.append("           CONNECT BY  role_parent = PRIOR role_child) u ") 
			.append(" 	     right outer join  ")
			.append(" 	     (  ")
                                //    -- Шаг 2. Найдем максимальное значение LVL для узла (наиболее нижний уровень во всех иерархиях). 
                                //    -- Тем самым убираем все ПОДЫерархии
			.append(" 	         select v.role_child, max(LVL) as lowest_level ") 
			.append("            from    ")
			.append("              (     ")
									   //  -- ШАГ 1. Получим все иерархии, включая подыерархии. Сразу укажем правильную сортировку
									   //  -- в поле sort_order
			.append(" 	              select distinct ROLE_PARENT, ROLE_CHILD, LEVEL as LVL, ROWNUM as sort_order ")
			.append("                 from role_nodes_full ")
			.append(" 	              CONNECT BY  role_parent = PRIOR role_child) v ")
			.append("            group by v.role_child ")
			.append("        ) m ")
			.append("        on u.role_child = m.role_child and u.lvl = m.lowest_level ")
			.append("    ) h ")
			.append("    on t.id_role = h.role_child and t.id_type_process = ?1 ")
			//.append("    and t.active = 1 ")

			.append(" 	left outer join  ")
			.append("      (   select  st3.id_role, max (st3.id_stage)  as usage ")
			.append("          from stages_in_role st3  ")
			.append("          group by  st3.id_role    ")
			.append("      ) us on t.id_role = us.id_role ")

			.append("where  ")
					     //-- покажем все роли        
			.append("   ( ('1'= ?2 ) ")
			.append("     or  ")
					       //-- покажем только роли, которые используются в процессе (которым назначена зотя бы одна стадия (операция))
			.append("     ( t.id_role in  (select distinct st.id_role  from stages_in_role st))) ")
         			       // покажем только роли с active = 1 (т.е. активные, не устаревшие роли)
			//.append("     and t.active = 1 ")
			.append("order by sort_order ");

		String sqlStr = sb.toString();
		Query query = null;		
		try {
			query = getEntityMgr().createNativeQuery(sqlStr);
			query.setParameter(1, processId);
			query.setParameter(2, showUnactive);
			List<Object[]> objectList = query.getResultList();
			ArrayList<RoleTreeReportOperation> list = new ArrayList<RoleTreeReportOperation>();
			for (Iterator<Object[]> iterator = objectList.iterator(); iterator.hasNext();) {
				Object[] rs = (Object[]) iterator.next();
				// Обработать результаты	
				RoleTreeReportOperation operation = (RoleTreeReportOperation) mapOperations(rs);
				list.add(operation);
			}
			return list;
		} catch (Exception e) {
			throw new MappingException(e, ("Exception caught in getOperations " + e));			
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<String> getUsersForRoles(Long roleId, Long departmentId) 
				throws MappingException {
		String sqlStr =
		  "select u.surname || ' ' || u.name  || ' ' ||  u.patronymic  as user_name "
		+ " from "
		+ " ( select * " 
		+ " from user_in_role ur2 "
		+ " where ur2.id_role = ?1 "
		+ " and ur2.status = 'Y' "
		+ " ) ur "
		+ " inner join users u on ur.id_user = u.id_user and u.id_department = ?2 "
		+ " order by user_name ";
		
		Query query = null;		
		try {
			query = getEntityMgr().createNativeQuery(sqlStr);
			query.setParameter(1, roleId);
			query.setParameter(2, departmentId);
			List<String> objectList = query.getResultList();
			ArrayList<String> list = new ArrayList<String>();
			for (Iterator<String> iterator = objectList.iterator(); iterator.hasNext();) {
				list.add((String)iterator.next());
			}
			return list;
		} catch (Exception e) {
			throw new MappingException(e, ("Exception caught in getUsersForRoles " + e));			
		}
	}
	
	/**
     * maps to VO
     */ 
    public VtbObject mapDepartments(Object[] jpa) throws MappingException {
        RoleTreeReportDepartment result = new RoleTreeReportDepartment();
        String departmentId = Utils.varToString(jpa[0]);
        result.setDepartmentId( (departmentId != null && !departmentId.equals("")) ? Long.parseLong(departmentId) : null ); 
        result.setDepartment_name(Utils.varToString(jpa[1]));
        result.setOperations(null);   // set it somewhere else.
        return result; 
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<RoleTreeReportDepartment> getDepartments(Long departmentId) throws MappingException {
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
            ArrayList<RoleTreeReportDepartment> list = new ArrayList<RoleTreeReportDepartment>();
            for (Iterator<Object[]> iterator = objectList.iterator(); iterator.hasNext();) {
                Object[] rs = (Object[]) iterator.next();
                // Обработать результаты    
                RoleTreeReportDepartment operation = (RoleTreeReportDepartment) mapDepartments(rs);
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
    public List<RoleTreeReportDepartment> getDepartmentsList(String[] departmentsList) throws MappingException {
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
          ArrayList<RoleTreeReportDepartment> list = new ArrayList<RoleTreeReportDepartment>();
          for (Iterator<Object[]> iterator = objectList.iterator(); iterator.hasNext();) {
              Object[] rs = (Object[]) iterator.next();
              // Обработать результаты    
              RoleTreeReportDepartment operation = (RoleTreeReportDepartment) mapDepartments(rs);
              list.add(operation);
          }
          return list;
        } catch (Exception e) {
            throw new MappingException(e, ("Exception caught in getDepartmentsList " + e));         
        }        
    }
}
