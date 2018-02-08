package com.vtb.mapping.jpa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Query;

import com.vtb.domain.JournalOfOperationsReport;
import com.vtb.domain.JournalOfOperationsReportHeader;
import com.vtb.domain.JournalOfOperationsReportOperation;
import com.vtb.domain.VtbObject;
import com.vtb.exception.DuplicateKeyException;
import com.vtb.exception.MappingException;
import com.vtb.exception.NoSuchObjectException;
import com.vtb.utils.Utils;

/**
 * JournalOfOperationsReportMapper implementation.
 * @author Michael Kuznetsov
 */
public class JournalOfOperationsReportMapper extends DomainJPAMapper  
				implements com.vtb.mapping.JournalOfOperationsReportMapper {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<JournalOfOperationsReport> findAll() throws MappingException {
		throw new NoSuchObjectException("JournalOfOperationsReportMapper.findAll. Method is not implemented ");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public JournalOfOperationsReport findByPrimaryKey(JournalOfOperationsReport anObject) throws NoSuchObjectException, MappingException {
		throw new NoSuchObjectException("JournalOfOperationsReportMapper.findByPrimaryKey. Method is not implemented ");	
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void insert(JournalOfOperationsReport anObject) throws DuplicateKeyException, MappingException {
		throw new NoSuchObjectException("JournalOfOperationsReportMapper.insert. Method is not implemented ");	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(JournalOfOperationsReport anObject) throws NoSuchObjectException, MappingException {
		throw new NoSuchObjectException("JournalOfOperationsReportMapper.update. Method is not implemented ");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(JournalOfOperationsReport anObject) throws NoSuchObjectException, MappingException {
		throw new NoSuchObjectException("JournalOfOperationsReportMapper.remove. Method is not implemented ");
	}

	
	/**
	 * maps to VO
	 */	
	public VtbObject map(Object jpaObject) throws MappingException {
		Object[] jpa = (Object[]) jpaObject;
		JournalOfOperationsReportOperation result = new JournalOfOperationsReportOperation();
		result.setClaim_name_internal(Utils.varToString(jpa[0]).toUpperCase());
        result.setClaim_name_CRM(Utils.varToString(jpa[1]).toUpperCase());
        result.generateClaimName();
		
		result.setId_stage(Utils.varToString(jpa[2]));
		result.setId_process(Utils.varToString(jpa[3]));			
		result.setDescription_stage(Utils.varToString(jpa[4]));
		result.setDepartment_name(Utils.varToString(jpa[5]));
		
		result.setUser_name(Utils.varToString(jpa[6]));
		result.setId_task(Utils.varToString(jpa[7]));
		result.setDescription_status(Utils.varToString(jpa[8]));
		result.setDate_from_sort(Utils.varToString(jpa[9]));
		result.setDate_from(Utils.varToString(jpa[10]));

		result.setDate_claimed(Utils.varToString(jpa[11]));
		result.setDate_to (Utils.varToString(jpa[12]));
		result.setPlan_period(Utils.varToString(jpa[13]));
		result.setLimit_type(Utils.varToString(jpa[14]));
		result.setFact_period(Utils.varToString(jpa[15]));
		
		result.setComment_field(Utils.varToString(jpa[16]));		
		result.setDelinquency(Utils.varToString(jpa[17]));
		
		// doesn't work in the query. Do it here instead!
		if (Long.parseLong(result.getDelinquency()) > 0 )  
			result.setComplation_description("с превышением срока на " + result.getDelinquency() + " дн.");
		else result.setComplation_description ("в срок");
		return result; 
	}


	/**
	 * maps to VO
	 */	
	public VtbObject mapHeaders(Object jpaObject) throws MappingException {
		Object[] jpa = (Object[]) jpaObject;
		JournalOfOperationsReportHeader result = new JournalOfOperationsReportHeader();		
		result.setDescription_status(Utils.varToString(jpa[0]));
		result.setDelinquency_descr(Utils.varToString(jpa[1]));
		result.setInternal_claim_name(Utils.varToString(jpa[2]).toUpperCase());
		result.setCRM_claim_name(Utils.varToString(jpa[3]).toUpperCase());
		result.generateClaimName();
		return result; 
	}

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public JournalOfOperationsReportHeader getHeaderData(String mdtaskNumber, Long operationStatus,	String isDelinquency) 
		throws MappingException {
	    try {
    	    boolean CRMClaim = isCRM(mdtaskNumber);
    	    String sqlStr = 
    			  " select "
    			+ "   coalesce (  "
    			+ "    (select t.description_status from task_status t "
    			+ "       where t.id_status = ?1), "
    			+ "       TO_NCHAR('Отображать все статусы'))   as DESCRIPTION_STATUS, "
    			+ "   decode  ( ?2,  "
    			+ "           'on',  'Просроченные',  "
    			+ "           'off', 'Все', 'Все') as DELINQUENCY_DESCR, ";
    		if (CRMClaim)
    		    sqlStr = sqlStr 
    		    + "    (select max (m.mdtask_number) " 
    		    + "       from mdtask m"
                + "    where upper(coalesce (m.crmcode, TO_CHAR(m.mdtask_number))) = upper(?3) "
                + "                                   ) as internal_claim_name, ";
            else 
                sqlStr = sqlStr + "   upper(?3) as internal_claim_name,  ";
    
    		if (CRMClaim)
    		    sqlStr = sqlStr
                + "    upper(?4) as CRM_claim_name ";
            else 
                sqlStr = sqlStr
                + "  (select upper(coalesce (m.crmcode, TO_CHAR(m.mdtask_number))) "
                + "          from mdtask m"    
                + "    where TO_CHAR(m.mdtask_number) = ?4 "
                + "                                   ) as CRM_claim_name ";
    		sqlStr = sqlStr + " from dual ";
    		Query query = null;		
		
			query = getEntityMgr().createNativeQuery(sqlStr);
			query.setParameter(1, operationStatus);		
			query.setParameter(2, isDelinquency);		
			query.setParameter(3, mdtaskNumber);
			query.setParameter(4, mdtaskNumber);
			
			List<Object[]> objectList = query.getResultList();			
			// always one record.			
			Iterator<Object[]> iterator = objectList.iterator();
			Object[] rs = (Object[]) iterator.next();
			// Обработать результаты	
			return (JournalOfOperationsReportHeader) mapHeaders((Object)rs);
		} catch (Exception e) {
			throw new MappingException(e, ("Exception caught in getReportData " + e));			
		}
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<JournalOfOperationsReportOperation> getReportData(String mdtaskNumber, Long operationStatus, String isDelinquency) 
				throws MappingException {
	    StringBuilder sb = new StringBuilder();
		sb.append("select t1.*, ") //   -- Шаг 2. Получим вычисляемые поля
		  .append("       t1.fact_period - t1.plan_period as delinquency, ")
		  .append("		  case  ")
		  .append("		     when t1.fact_period - t1.plan_period > 0 then  'с превышением срока на ' || TO_CHAR(t1.fact_period - t1.plan_period) || ' дн.' ")
		  .append("       else ' '  ") //    -- в срок
		  .append("       end  as complation_description  ") //   -- описание, в срок ли завершена операция
			
		  .append("from  ") 
		  .append(" (  ")  //   -- Шаг 1.  Получим все простые поля и вычислимые на одном шаге из таблиц.

		  .append("		select TO_CHAR(m.mdtask_number) as claim_name, ") 
		  .append("     m.crmcode,    ")  
		  .append("     s.id_stage,    ")  //     -- [Уникальный № этапа] ") 
		  .append("     t.id_process,   ")  //    -- id процесса  (входной параметр??? Вообще-то у нас входной -- это номер заявки!)
		  .append("     s.description_stage, ") // -- [Этап прохождения кредитной заявки (наименование)] название операции 
		  .append("     d.shortname as department_name,  ") //-- [Отдел] подразделение 
		  .append("     u.surname || ' ' || u.name  || ' ' ||  u.patronymic as user_name,  ") // -- [Ответственное лицо] имя пользователя, кто взял на себя или был назначен на исполнение операции
		  .append("     t.id_task, ts.description_status,  ") // -- [Статус этапа]  статус операции (на текущий момент???  или хронологический? когда взят, когда завершен и  прочее)
		  .append("     ( select te.date_event  ") 
		  .append("       from task_events te  ")
		  .append("       where te.id_task = t.id_task  and te.id_task_type_event = 1 ")
		  .append("      ) as date_from_sort,  ") // -- Дата поступления на обработку (для точной сортировки)
		  .append("     TO_CHAR (  ") 
		  .append("           ( select te.date_event  ")
		  .append("            from task_events te ")
		  .append("            where te.id_task = t.id_task  and te.id_task_type_event = 1 ")
		  .append("           ), 'DD.MM.YYYY HH24:MI:SS') as date_from,  ") // -- [Дата поступления] Дата поступления на обработку
		  .append("     TO_CHAR (  ")
		  .append("         ( select te.date_event ")
		  .append("             from task_events te ")
		  .append("            where te.id_task = t.id_task  and te.id_task_type_event = 2 ")
		  .append("         ), 'DD.MM.YYYY HH24:MI:SS') as date_claimed,  ") // -- [Дата начала]
		  .append("     TO_CHAR (  ")
		  .append("     	coalesce (  ") //-- оказывается, может быть несколько. Например, ОТМЕНА может быть позднее ЗАВЕРШЕНИЯ
		//					---  берем всегда дату завершения, если таковая имеется
		  .append("             	(select  te.date_event ")  
		  .append("     		     from task_events te      ")          
		  .append("						where te.id_task = t.id_task  and te.id_task_type_event = 3), ")
		  //.append("								-- либо минимальную из оставшихся дат (не знаю, какая из них имеет более правильный приоритет)
		  .append("					(select  MIN(te.date_event)  ") // -- оказывается, может быть несколько. Например, ОТМЕНА может быть позднее ЗАВЕРШЕНИЯ
		  .append("					   from task_events te    ")      //      -- придется брать максимальную
		  .append("					   where te.id_task = t.id_task  and te.id_task_type_event IN (4, 5, 6, 7) ")
		  .append("		 		    ) ")
		  .append("		    ), 'DD.MM.YYYY HH24:MI:SS') as date_to, ")  //-- [Дата окончания]
		  .append("		    s.limit_day as plan_period, ")              // -- [Контрольный срок]
		  .append("		     s.limit_day || ' ' || decode (s.type_limit_day, 1, 'раб.дн.',  ")
		  .append("                                0, 'календ.дн.', null) as limit_type,  ") // -- [Контрольный срок (дн.)]
		  .append("     EXTRACT  ")
		  .append("       (DAY FROM   ( ") 
		  .append("           COALESCE  ")
		  .append("           (    ") //  -- вернем дату завершения, если она есть
		  .append("             (   ")
		  .append("                select te.date_event  ")    
		  .append("                from task_events te ")
		  .append("                inner join task_type_events tte on te.id_task_type_event = tte.id_task_type_event ")
		  .append("                where te.id_task = t.id_task  and te.id_task_type_event IN (3, 4, 5, 6, 7) ")
		  .append("              ), ")
		  .append("              SYSDATE  ") //   -- или текущую дату, если даты завершения нет
		  .append("           ) ")
		  .append("           - ")
		  .append("           (   ")
		  .append("              select te.date_event  ") //  -- дата поступления на обработку
		  .append("              from task_events te ")
		  .append("              where te.id_task = t.id_task  and te.id_task_type_event = 1 ")
		  .append("            ) ")
		  .append("        ) DAY TO SECOND)  + 1  as fact_period,  ") // -- [фактическое число дней обработки операции, округленное в большую сторону]
		  .append("     null as comment_field   ") //        -- [Примечание] Что за примечание такое??
		  .append("	    from tasks t  ")
		  .append("     left outer join users u on u.id_user= t.id_user ")
		  .append("     inner join mdtask m on m.id_pup_process = t.id_process ")
		  .append("     left outer join departments d on d.id_department= t.id_department ")
		  .append("     inner join stages s on s.id_stage = t.id_stage_to ")
		  .append("     inner join task_status ts on ts.id_status = t.id_status ")
		  .append("     where TO_CHAR(m.mdtask_number) = ?1   ") //        -- parameter
		  .append("       and (t.id_status = ?2  or  -1 = ?3 )  ") //  -- parameter id статуса, и еще раз он же.
		  .append("  ) t1  ")
		  .append("where (( 'on' = ?4 ) and (t1.fact_period - t1.plan_period >0))  ") //  -- параметр-флажок для показа просроченных или всех операций.
		  .append("or    ( 'off'= ?5 ) ")
		  .append("order by date_from_sort ");
			/*************************************************************************************************************
			Журнал учета прохождения кредитных сделок
	
			Данный скрипт  отображается все операции по выбранной заявке, как законченные, так и находящиеся в работе,
			с указанием подразделения и ФИО пользователя, назначенного на операцию 
	
			Отображается текущий статус заявки, не исторический разворот! Поэтому не может быть отчетом ХРОНОЛОГИЧЕСКИМ!!!
			Но создать на его основе можно и нужно.
	
			@param :  id заявки (номер заявки)
			@param :  id статуса операции
			@param :  все тот же id статуса операции (для сравнения с -1, когда нужно выбрать ВСЕ статусы операции)
			@param :  флаг, показывать или нет просроченные операции.
	
			Проверить, когда не все даты заполнены (статус, например, таков). Отобразятся  пробелы, и это нормально.
			2655, 3291
	
			Когда несколько пользователей обрабатывали один и тот же этап, то как это отобразить? [вогда возвраты этапов были, переназначения и т.п.]
			3474, 3482, 2484
	
			Когда все этапы были закончены:
			3141  -- Закрывается операция ''Информирование о принятом решении'. Есть информация о том, что ПОЗЖЕ срока.
			Но не используется. 
	
			TODO: при необходимости -- оптимизировать запрос, не вычисляя многократно даты начала и оконччания, а сохраняя из во вложенных запросах!!!
	
			*************************************************************************************************************/
		String sqlStr = sb.toString();

		Query query = null;		
		try {
			query = getEntityMgr().createNativeQuery(sqlStr);
			query.setParameter(1, mdtaskNumber);
			query.setParameter(2, operationStatus);
			query.setParameter(3, operationStatus);
			query.setParameter(4, isDelinquency);
			query.setParameter(5, isDelinquency);

			List<Object[]> objectList = query.getResultList();
			ArrayList<JournalOfOperationsReportOperation> list = new ArrayList<JournalOfOperationsReportOperation>();
			for (Iterator<Object[]> iterator = objectList.iterator(); iterator.hasNext();) {
				Object[] rs = (Object[]) iterator.next();
				// Обработать результаты	
				JournalOfOperationsReportOperation operation = (JournalOfOperationsReportOperation) map((Object)rs);
				list.add(operation);
			}
			return list;
		} catch (Exception e) {
			throw new MappingException(e, ("Exception caught in getReportData " + e));			
		}
	}
	
    /**
     * Answers whether a CRM number was input 
     * @param mdtaskNumber
     * @return true, if a CRM number.
     * @throws MappingException 
     */
    private boolean isCRM(String mdtaskNumber) throws MappingException {
    // find out whether CRM or internal mdTaskNumber was input.
        //old implementation
//        try {
//            Long.parseLong(mdtaskNumber);
//            return false;
//        } catch (NumberFormatException e) {
//            // CRM number was input.
//            return true;
//        }
        
        // new implementation. Search in the database.
        boolean isMdTaskuNumber = findInMdTask(mdtaskNumber) > 0;
        boolean isCRMNumber = findInCRM(mdtaskNumber) > 0;
        if (isMdTaskuNumber)
            if (isCRMNumber) return true; // CRM number has a priority.
            else return false;            // mdTaskNumber
        else
            if (isCRMNumber) return true; // CRM number.
            else return false;            // none are found. Think, it's an mdTaskNumber
    }
    
    /**
     * Returns a count of mdstask's with a given mdtask number 
     * @param mdtaskNumber
     * @return found count
     * @throws MappingException 
     */
    private int findInMdTask(String mdtaskNumber) throws MappingException {
        String sqlStr =
            "select count (*)" 
            + "from mdtask m " 
            + "where TO_CHAR(m.mdtask_number) = ?1 ";
        Query query = null;     
        try {
            query = getEntityMgr().createNativeQuery(sqlStr);
            query.setParameter(1, mdtaskNumber);
            return Utils.objToLong( query.getSingleResult()).intValue(); 
        } catch (Exception e) {
            throw new MappingException(e, ("Exception caught in findInMdTask " + e));
        }
    }
    
    /**
     * Returns a count of crm 's with a given mdtask number 
     * @param mdtaskNumber
     * @return found count
     * @throws MappingException 
     */
    private int findInCRM(String mdtaskNumber) throws MappingException {
        String sqlStr =
            "select count (*)" 
            + "from mdtask m " 
            + "where upper(m.crmcode) = upper(?1) ";
        Query query = null;     
        try {
            query = getEntityMgr().createNativeQuery(sqlStr);
            query.setParameter(1, mdtaskNumber);
            return Utils.objToLong( query.getSingleResult()).intValue(); 
        } catch (Exception e) {
            throw new MappingException(e, ("Exception caught in findInCRM " + e));
        }
    }
}
