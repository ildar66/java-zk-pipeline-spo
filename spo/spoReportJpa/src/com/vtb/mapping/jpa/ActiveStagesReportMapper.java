package com.vtb.mapping.jpa;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Query;

import com.vtb.domain.ActiveStagesReport;
import com.vtb.domain.ActiveStagesReportHeader;
import com.vtb.domain.ActiveStagesReportOperation;
import com.vtb.domain.VtbObject;
import com.vtb.exception.DuplicateKeyException;
import com.vtb.exception.MappingException;
import com.vtb.exception.NoSuchObjectException;
import com.vtb.utils.Utils;

/**
 * ActiveStagesReportMapper implementation.
 * @author Michael Kuznetsov
 */
public class ActiveStagesReportMapper extends DomainJPAMapper implements
		com.vtb.mapping.ActiveStagesReportMapper {
	private final Long EXPERT_DEP = new Long(20L);
	private final long coeff = 100000L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ActiveStagesReport> findAll() throws MappingException {
		throw new NoSuchObjectException("ActiveStagesReportMapper.findAll. Method is not implemented ");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ActiveStagesReport findByPrimaryKey(ActiveStagesReport anObject)
			throws NoSuchObjectException, MappingException {
		throw new NoSuchObjectException(
				"ActiveStagesReportMapper.findByPrimaryKey. Method is not implemented ");
	}

	/**
	 * maps objects
	 */
	public VtbObject map(Object jpaObject) throws MappingException {
		Object[] jpa = (Object[]) jpaObject;
		ActiveStagesReportOperation result = new ActiveStagesReportOperation();

		result.setClaim_name_internal(Utils.varToString(jpa[0]));
		result.setClaim_name_CRM(Utils.varToString(jpa[1]));
		result.generateClaimName();

		result.setId_stage(Utils.varToString(jpa[2]));
		result.setStage_status(Utils.varToString(jpa[3]));

		result.setId_process(Utils.varToString(jpa[4]));
		result.setId_type_process(Utils.varToString(jpa[5]));
		result.setDescription_process(Utils.varToString(jpa[6]));
		result.setDescription_stage(Utils.varToString(jpa[7]));
		result.setId_department(Utils.varToString(jpa[8]));
		result.setDepartment_name(Utils.varToString(jpa[9]));
		result.setUser_name(Utils.varToString(jpa[10]));
		result.setId_task(Utils.varToString(jpa[11]));

		result.setId_status(Utils.varToString(jpa[12]));
		result.setDescription_status(Utils.varToString(jpa[13]));
		result.setDate_from_sort(Utils.varToString(jpa[14]));
		result.setDate_from(Utils.varToString(jpa[15]));
		result.setDate_claimed(Utils.varToString(jpa[16]));
		result.setDate_to(Utils.varToString(jpa[17]));
		result.setPlan_period(Utils.varToString(jpa[18]));
		result.setLimit_type(Utils.varToString(jpa[19]));
		result.setComment_field(Utils.varToString(jpa[20]));
		result.setGroup_key(Utils.varToString(jpa[21]));

		result.setCh_date_from(Utils.varToString(jpa[22]));
		result.setCh_date_to(Utils.varToString(jpa[23]));
		result.setCh_date_claimed(Utils.varToString(jpa[24]));
		result.setFact_period(Utils.varToString(jpa[25]));
		result.setDelinquency(Utils.varToString(jpa[26]));
		result.setInternal_parameter(Utils.varToString(jpa[27]));

		// doesn't work in the query. Do it here instead!
		if (Long.parseLong(result.getDelinquency()) > 0)
			result
					.setComplation_description("с превышением срока на " + result.getDelinquency() + " дн.");
		else
			result.setComplation_description("в срок");

		// Adds error messages if found
		if (result.getClaim_name().equals(""))
			result.addComment("ОШИБКА: номер заявки не определен");
		if (result.getId_stage().equals(""))
			result.addComment("ОШИБКА: номер операции не определен");
		if (result.getId_process().equals(""))
			result.addComment("ОШИБКА: номер процесса не определен");
		if (result.getId_task().equals(""))
			result
					.addComment("ОШИБКА: для операции не определена соответствующая стадия (операция) в описании бизнес-процесса");
		if ((result.getStage_status() == null) || (result.getStage_status().equals("0")))
			result
					.addComment("Операция является устаревшей и не может быть выполнена. В случае необходимости выполнения операции обратитесь к администратору системы для принятия решений о дальнейших действиях");
		if ((result.getDepartment_name() == null) || (result.getDepartment_name().equals("")))
			result
					.addComment("ОШИБКА: подразделение для операции не определено. Пожалуйста, уведомите администратора системы");
		return result;
	}

	/**
	 * map the Header of the report
	 */
	public VtbObject mapHeader(Object[] obj) throws MappingException {
		ActiveStagesReportHeader result = new ActiveStagesReportHeader();
		result.setParam_process_type(Utils.varToString(obj[0]));
		result.setInternal_claim_name(Utils.varToString(obj[1]).toUpperCase());
		result.setCRM_claim_name(Utils.varToString(obj[2]).toUpperCase());
		result.setParam_department(Utils.varToString(obj[3]));
		result.setParam_corresponding_deps(Utils.varToString(obj[4]));
		result.setParam_user_name(Utils.varToString(obj[5]));
		result.setParam_delinquency_descr(Utils.varToString(obj[6]));
		result.generateClaimName();
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void insert(ActiveStagesReport anObject) throws DuplicateKeyException, MappingException {
		throw new NoSuchObjectException("ActiveStagesReportMapper.insert. Method is not implemented ");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(ActiveStagesReport anObject) throws NoSuchObjectException, MappingException {
		throw new NoSuchObjectException("ActiveStagesReportMapper.update. Method is not implemented ");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(ActiveStagesReport anObject) throws NoSuchObjectException, MappingException {
		throw new NoSuchObjectException("ActiveStagesReportMapper.remove. Method is not implemented ");
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ActiveStagesReportOperation> getReportData(Long idTypeProcess, String idClaim,
			Long idDepartment, String correspondingDeps, Long idUser, Long isDelinquency, Long mdtaskId)
			throws MappingException {
		String isDelinquencyStr = isDelinquency.toString();
		StringBuilder sb = new StringBuilder();
		String filterMdtask = "";
		if (mdtaskId != null)
			filterMdtask = "          and (?1 is not null and m.id_mdtask = ?2)  "; // -- номер заявки! "
		else
			filterMdtask = "          and (('-1'= ?1) or TO_CHAR(m.mdtask_number) = ?2)  "; // -- номер
																																											// заявки! "
		sb.append(" select  ")
				// -- Шаг 3. Получим оставшиеся поля "
				.append("    t2.*,  ")
				.append("         fact_period - plan_period as delinquency, ")
				// .append(" case  ")
				// .append("       when fact_period - plan_period > 0 then  'с превышением срока на ' || TO_CHAR(fact_period - plan_period) ||  ' дн.' ")
				// .append("       else ' '     -- в срок ")
				// .append("    end  as complation_description, ") // -- описание, в срок ли завершена
				// операция "

				// compound key
				.append("    t2.id_stage * ")
				.append(String.valueOf(coeff))
				.append(" +  t2.id_department as internal_parameter ")
				.append("from  ")
				.append("  ( ")
				.append("    select  ")
				// -- Шаг 2. Получим вычисляемые поля "
				.append("      t1.*, ")
				// compound key
				.append("      id_stage * ")
				.append(String.valueOf(coeff))
				.append(" +  id_department as group_key, ")
				.append("       TO_CHAR (date_from, 'DD.MM.YYYY HH24:MI:SS') as ch_date_from, ")
				// -- [Дата поступления в текстовом виде] "
				.append("       TO_CHAR (date_to, 'DD.MM.YYYY HH24:MI:SS') as ch_date_to,  ")
				// -- [Дата поступления в текстовом виде] "
				.append("       TO_CHAR (date_claimed, 'DD.MM.YYYY HH24:MI:SS') as ch_date_claimed, ")
				// -- [Дата взятия в обработку в текстовом виде] "
				.append("   EXTRACT  ")
				.append("       (DAY FROM   ( ")
				.append("             COALESCE  ")
				.append("             (     ")
				// -- вернем дату завершения, если она есть, или текущую дату, если даты завершения нет "
				.append("                  t1.date_to, SYSDATE ")
				.append("              ) ")
				.append("              -  ")
				.append("              t1.date_from  ")
				// -- дата поступления на обработку "
				.append("       ) DAY TO SECOND)  + 1  as fact_period ")
				// -- [фактическое число дней обработки операции, округленное в большую сторону] "
				.append("    from  ")
				.append("        (  ")
				// -- Шаг 1. Получим все простые поля и вычислимые на одном шаге из таблиц. "

				.append("        select TO_CHAR(m.mdtask_number) as claim_name,  ")
				.append("          m.crmcode,  ")
				.append("          s.id_stage,      ")
				// -- [Уникальный № этапа] "
				.append("          s.active,      ")
				// -- признак, является ли этап активным "
				.append("          t.id_process,    ")
				// -- id процесса "
				.append("          p.id_type_process, ")
				// -- тип процесса "
				.append("          tpp.description_process, ")
				// -- название процесса "
				.append("          s.description_stage, ")
				// -- [Этап прохождения кредитной заявки (наименование)] название операции "
				.append("          t.id_department, ")
				// -- id подразделения "
				.append("          d.shortname as department_name, ")
				// -- [Отдел] подразделение "
				.append("          u.surname || ' ' || u.name  || ' ' ||  u.patronymic as user_name, ")
				// -- [Ответственное лицо] имя пользователя, кто взял на себя или был назначен на исполнение
				// операции "
				.append("          t.id_task,  ")
				.append("          t.id_status, ts.description_status,  ")
				// -- [Статус этапа] статус операции (на текущий момент??? или хронологический? когда взят,
				// когда завершен и прочее) "

				.append("          ( select te.date_event ")
				.append("             from task_events te ")
				.append("             where te.id_task = t.id_task  and te.id_task_type_event = 1 ")
				.append("           ) as date_from_sort,  ")
				// -- Дата поступления на обработку (для точной сортировки) "

				.append("          ( select te.date_event ")
				.append("                 from task_events te ")
				.append("                 where te.id_task = t.id_task  and te.id_task_type_event = 1 ")
				.append("          ) as date_from,  ")
				// -- [Дата поступления] Дата поступления на обработку "

				.append("          ( select te.date_event ")
				.append("                 from task_events te ")
				.append("                 where te.id_task = t.id_task  and te.id_task_type_event = 2 ")
				.append("          ) as date_claimed, ")
				// -- [Дата начала] "

				.append("          coalesce ( ")
				// -- оказывается, может быть несколько. Например, ОТМЕНА может быть позднее ЗАВЕРШЕН??Я "
				// --- берем всегда дату завершения, если таковая имеется "
				.append("                (select  te.date_event   ")
				.append("                  from task_events te      ")
				.append("                  where te.id_task = t.id_task  and te.id_task_type_event = 3), ")
				// -- либо минимальную из оставшихся дат (не знаю, какая из них имеет более правильный
				// приоритет) "
				.append("                (select  MIN(te.date_event)   ")
				// -- оказывается, может быть несколько. Например, ОТМЕНА может быть позднее ЗАВЕРШЕН??Я "
				.append("                  from task_events te         ")
				// -- придется брать максимальную "
				.append(
						"                  where te.id_task = t.id_task  and te.id_task_type_event IN (4, 5, 6, 7) ")
				.append("                 ) ")
				.append("          )  as date_to,        ")
				// -- [Дата окончания] "

				.append("          coalesce (s.limit_day, 0 ) as plan_period,    ")
				// -- [Контрольный срок] "
				.append("          s.limit_day || ' ' || decode (s.type_limit_day, 1, 'раб.дн.', ")
				.append("                                  0, 'календ.дн.', null) as limit_type, ")
				// -- [Контрольный срок (дн.)] "
				.append("          null as comment_field   ")
				// -- [Примечание] Что за примечание такое?? "

				.append("        from tasks t ")
				.append("           left outer join users u on u.id_user= t.id_user ")
				.append("           inner join mdtask m on m.id_pup_process = t.id_process ")
				.append("           left outer join departments d on d.id_department= t.id_department ")
				.append("           inner join stages s on s.id_stage = t.id_stage_to ")
				.append("           inner join task_status ts on ts.id_status = t.id_status ")
				.append("           inner join processes p on t.id_process = p.id_process ")
				.append(
						"           inner join type_process tpp on t.id_type_process =  tpp.id_type_process ")
				.append("        where 1=1 ")
				.append(filterMdtask)
				// -- номер заявки!
				.append("          and ((-1 = ?3) or p.id_type_process = ?4) ")
				// -- тип процесса "
				.append("          and (t.id_status IN (1,2))                    ")
				.append("          and  ")
				.append("             ( ('on' = ?5)  ")
				// -- Если флажок [включить информацию по всем подразделениям, где проходит заявка]
				// установлен, "
				.append("               or       ")
				// -- то выбрать ВСЕ операции, независимо от подразделения (фильтруется на следующем этапе)
				// "
				.append("               ('off' = ?6 and  t.id_department = ?7 )) ")
				// -- иначе выбрать ТОЛЬКО записи из данного подразделения "
				.append("          and     ")
				.append("             ( (-1 = ?8)  ")
				// -- выбрать операции для всех пользователей "
				.append("                or        ")
				.append("                ( ( t.id_user = ?9)  ")
				// -- или только для ДАННОГО пользователя, "
				.append("                  and   ")
				// -- если только операция может быть назначена пользователю, передаваемому в отчет "
				// -- вообще говоря, эту првоерку можно вынести из SQL на уровень формирования фильтра "
				.append("                  (?10 in (select distinct ur.id_user  ")
				// -- список пользователей, которым можно назначить данную операцию "
				.append("                                   from user_in_role ur ").append(
						"                                   inner join  roles r on ur.id_role = r.id_role ")
				.append("                                   where ur.status = 'Y' ").append(
						"                                   and ur.id_role IN       ").append(
						"                                       (select str.id_role from stages_in_role str ")
				.append("                                     where str.id_stage = t.id_stage_to) ")
				// -- по данной операции "
				.append("                                 )                   ").append(
						"                   ) ").append("                 ) ").append("             ) ")
				.append("        ) t1 ").append("  ) t2 ")

				.append(" where ").append("   1 = 1   ")
				// -- условия связи таблиц "
				// -- t2.group_key = av_usrs.group_key закомментируем. Вернем обратно, если будет
				// необходимость. "
				.append("    and  ").append("     (     ").append(
						"        (('1' = ?11) and (fact_period - plan_period >0)) ") // -- параметр-флажок для
																																					// показа просроченных
																																					// операций. "
				.append("        or (('2' = ?12) and (plan_period - fact_period >= 0)) ") // -- показ
																																									// непросроченных
																																									// операций. "
				.append("        or ('-1' = ?13)                                       ") // -- показ всех
																																									// операций "
				.append("     ) ").append(" order by claim_name, date_from  ");

		String sqlStr = sb.toString();
		Query query = null;
		try {
			query = getEntityMgr().createNativeQuery(sqlStr);
			query.setParameter(1, (mdtaskId != null) ? mdtaskId : idClaim);
			query.setParameter(2, (mdtaskId != null) ? mdtaskId : idClaim);
			query.setParameter(3, idTypeProcess);
			query.setParameter(4, idTypeProcess);
			query.setParameter(5, correspondingDeps);
			query.setParameter(6, correspondingDeps);
			query.setParameter(7, idDepartment);
			query.setParameter(8, idUser);
			query.setParameter(9, idUser);
			query.setParameter(10, idUser);
			query.setParameter(11, isDelinquencyStr);
			query.setParameter(12, isDelinquencyStr);
			query.setParameter(13, isDelinquencyStr);

			List<Object[]> objectList = query.getResultList();
			ArrayList<ActiveStagesReportOperation> list = new ArrayList<ActiveStagesReportOperation>();
			for (Iterator<Object[]> iterator = objectList.iterator(); iterator.hasNext();) {
				Object[] rs = (Object[]) iterator.next();
				// Обработать результаты
				ActiveStagesReportOperation activeStagesReport = (ActiveStagesReportOperation) map((Object) rs);
				list.add(activeStagesReport);
			}
			return list;
		}
		catch (Exception e) {
			throw new MappingException(e, ("Exception caught in getReportData " + e));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getAvailableUsers(ActiveStagesReportOperation operation)
			throws MappingException {
		try {
			List<String> list = getAvailableUsersInDep(operation.getGroup_key());
			if (list.size() == 0) {
				// не найдены возможные исполнители
				operation.addComment("Отсутствуют возможные исполнители по операции в подразделении. "
						+ "Назначьте соответствующие роли сотрудникам подразделения. ");

				// начнем искать в вышестоящих подразделениях
				List<BigDecimal> upperDeps = getAllUpperDepartments(operation.getId_department());
				// почему-то этот список -- только для чтения.
				List<BigDecimal> upperDepsCpy = new ArrayList<BigDecimal>(upperDeps);
				// подмешаем сразу все экспертные подразделения.
				List<BigDecimal> expertsDepIds = getExpertDepartments();
				for (BigDecimal depId : expertsDepIds)
					if (!upperDeps.contains(depId))
						upperDepsCpy.add(depId);

				boolean firstDep = true;
				for (BigDecimal depId : upperDepsCpy) {
					long compoundKey = Long.parseLong(operation.getId_stage()) * coeff + depId.longValue();
					List<String> listUpperDep = getAvailableUsersInDep(String.valueOf(compoundKey));
					if (listUpperDep.size() > 0) {
						if (!firstDep)
							list.add(""); // отбивка пустой строкой.
						list.add("В подразделении " + getDepartmentShortName(depId.longValue()) + ":");
						list.addAll(listUpperDep);
						firstDep = false;
					}
				}
				if (list.size() > 0)
					operation
							.addComment("В списке возможных исполнителей по задаче показаны сотрудники вышестоящих подразделений (или экспертных подразделений), "
									+ "которые могут выполнить данную задачу.");
			}
			return list;
		}
		catch (Exception e) {
			throw new MappingException(e, ("Exception caught in getAvailableUsers " + e));
		}
	}

	/**
	 * Looks for users having definite roles in the chosen department. Throws Exception, if not found.
	 */
	@SuppressWarnings("unchecked")
	private List<String> getAvailableUsersInDep(String compoundKey) throws Exception {
		Long groupKey = Long.parseLong(compoundKey);
		String sqlStr = " select  distinct   str_r.name_role || ': ' || str_u.surname || ' ' || str_u.name  || ' ' ||  str_u.patronymic  as acceptable_user "
				+ " from user_in_role str_ur "
				+ "     inner join  roles str_r on str_ur.id_role = str_r.id_role "
				+ "     inner join users str_u on str_ur.id_user= str_u.id_user " // -- and
																																					// str_u.id_department =
																																					// 304 -- parameter "
				+ "     inner join stages_in_role str_t on str_t.id_role = str_ur.id_role "
				+ "     where  " + "       str_t.id_stage * 100000 +  str_u.id_department = ?1 " // --
																																													// 560200001
																																													// --
																																													// parameter
																																													// "
				+ "       and str_ur.status = 'Y'        " + "     order by acceptable_user ";
		Query query = null;
		query = getEntityMgr().createNativeQuery(sqlStr);
		query.setParameter(1, groupKey);

		List<Object> objectList = query.getResultList();
		ArrayList<String> list = new ArrayList<String>();
		for (Iterator<Object> iterator = objectList.iterator(); iterator.hasNext();) {
			Object rs = (Object) iterator.next();
			String available_user = (String) rs;
			list.add(available_user);
		}
		return list;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAssignedUsers(Long processId, Long statusId, Long stageId)
			throws MappingException {

		String sqlStr = "  select 'назначен: ' ||  r.name_role || ': ' || u.surname || ' ' || u.name  || ' ' ||  u.patronymic  as assigned_user "
				+ "     from  "
				+ "     ( "
				// + "     -- Список назначений "
				+ "       select asg_roles.id_role, assigned.id_user_to "
				+ "       from  "
				+ "         (  " // -- список назначений на роли по данному процессу "
				+ "           select asg.id_role, asg.id_user_to  "
				+ "           from assign asg  "
				+ "           inner join process_events pe "
				+ "           on asg.id_process_event = pe.id_process_event and pe.id_process = ?1 " // --
																																															// id_process
																																															// "
				+ "           where 1 = ?2  " // -- показываем весь отчет только в случае, если статус
																			// операции = 1 (передается как параметр) "
				// + "         -- (положил сюда для оптимизации процесса) "
				+ "         ) assigned "
				+ "       inner join  "
				+ "         (  " // -- список назначений на роли по конкретной операции (стадии) "
				+ "           select sr.id_role from stages_in_role sr "
				+ "           where sr.id_stage = ?3 " // -- id_stage "
				+ "         )  asg_roles "
				+ "       on assigned.id_role = asg_roles.id_role "
				+ "     ) res "
				+ "     inner join roles r on res.id_role = r.id_role "
				+ "     inner join users u on res.id_user_to = u.id_user ";

		Query query = null;
		try {
			query = getEntityMgr().createNativeQuery(sqlStr);
			query.setParameter(1, processId);
			query.setParameter(2, statusId);
			query.setParameter(3, stageId);

			List<Object> objectList = query.getResultList();
			ArrayList<String> list = new ArrayList<String>();
			for (Iterator<Object> iterator = objectList.iterator(); iterator.hasNext();) {
				Object rs = (Object) iterator.next();
				String available_user = (String) rs;
				list.add(available_user);
			}
			return list;
		}
		catch (Exception e) {
			throw new MappingException(e, ("Exception caught in getAssignedUsers " + e));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ActiveStagesReportHeader getHeaderData(Long p_idTypeProcess, String p_idClaim,
			Long p_idDepartment, String correspondingDeps, Long p_idUser, Long isDelinquency,
			Long mdtaskId) throws MappingException {
		try {
			boolean CRMClaim = isCRM(mdtaskId, p_idClaim);
			StringBuilder sb = new StringBuilder();
			sb.append("select decode( ?1, -1,  'Все процессы', ")
			.append("(select t.description_process  from type_process t ")
			.append("where t.id_type_process = ?2)) as process_type2, ")

			// --internalClaimName: внутренний номер заявки
			.append("decode( ?3, '-1', 'Все заявки', ");
			if (CRMClaim)
				sb.append("(select max (m.mdtask_number) from mdtask m ")
				.append("where upper(coalesce (m.crmcode, TO_CHAR(m.mdtask_number))) = upper(?4) )) ")
				.append("as internal_claim_name2, ");
			else
				sb.append(" ?4 ) as internal_claim_name2, ");

			// --CRMClaimName: номер заявки CRM
			sb.append(" decode ( ?5, '-1', 'Все заявки', ");
			if (CRMClaim)
				sb.append("upper(?6) ) as CRM_claim_name, ");
			else
				sb.append("(select upper(coalesce (m.crmcode, TO_CHAR(m.mdtask_number))) from mdtask m ")
				.append("where TO_CHAR(m.mdtask_number) = ?6 )) as CRM_claim_name, ");

			// -- p_idDepartment: департамент
			sb.append("decode ( ?7, -1,  'Все подразделения', ")
			.append("(select t.shortname from departments t where t.id_department = ?8)) as department2, ")

			//-- correspondingDeps: показывать операции по всем подразделениям, где выполняется  заявка\\ только по данному подразделению
			.append("decode ( ?9, ")
			.append("'on',  'по всем подразделениям, где выполняется  заявка', ")
			.append("'off',  'только по данному подразделению', ")
			.append("'только по данному подразделению') as correspondingDeps2, ")

			// -- p_idUser: имя пользователя
			.append("decode ( ?10, -1,  'Все пользователи подразделения', ")
			.append("(select u.surname || ' ' || u.name  || ' ' ||  u.patronymic from users u where u.id_user = ?11)) as user_name2, ")

			// -- isDelinquency: просроченные \\ непросроченные
			.append("decode  ( ?12, 1,  'Просроченные', 2,  'Непросроченные', -1, ")
			.append("'Все (непросроченные и просроченные)', 'Все (непросроченные и просроченные)') as DELINQUENCY_DESCR2 ")
			.append(" from dual ");

			String sqlStr = sb.toString();
			Query query = null;

			query = getEntityMgr().createNativeQuery(sqlStr);

			String mdtaskNumber = null;
			if (mdtaskId != null)
				mdtaskNumber = getNumberByMdtaskId(mdtaskId);
			else
				mdtaskNumber = p_idClaim;
			query.setParameter(1, p_idTypeProcess);
			query.setParameter(2, p_idTypeProcess);
			query.setParameter(3, mdtaskNumber);
			query.setParameter(4, mdtaskNumber);
			query.setParameter(5, mdtaskNumber);
			query.setParameter(6, mdtaskNumber);

			query.setParameter(7, p_idDepartment);
			query.setParameter(8, p_idDepartment);
			query.setParameter(9, correspondingDeps);
			query.setParameter(10, p_idUser);
			query.setParameter(11, p_idUser);
			query.setParameter(12, isDelinquency);

			List<Object[]> objectList = query.getResultList();
			Iterator<Object[]> iterator = objectList.iterator();
			// one row always
			Object[] rs = (Object[]) iterator.next();
			return (ActiveStagesReportHeader) mapHeader(rs);
		}
		catch (Exception e) {
			throw new MappingException(e, ("Exception caught in getHeaderData " + e));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long getProcessTypeId(String claimId) throws MappingException {
		String sqlStr = "select t.id_type_process " + "from processes t "
				+ "inner join mdtask m on t.id_process = m.id_pup_process "
				+ "where TO_CHAR(m.mdtask_number) = ?1 ";
		Query query = null;
		try {
			query = getEntityMgr().createNativeQuery(sqlStr);
			query.setParameter(1, claimId);
			return Utils.objToLong(query.getSingleResult());
		}
		catch (Exception e) {
			throw new MappingException(e, ("Exception caught in getProcessTypeId " + e));
		}
	}

	/**
	 * Answers whether a CRM number was input
	 * @param mdtaskNumber
	 * @return true, if a CRM number.
	 * @throws MappingException
	 */
	private boolean isCRM(Long mdtaskId, String mdtaskNumber) throws MappingException {
		// new implementation. Search in the database.
		boolean isMdTaskuNumber = findInMdTask(mdtaskId, mdtaskNumber) > 0;
		boolean isCRMNumber = findInCRM(mdtaskId, mdtaskNumber) > 0;
		if (isMdTaskuNumber)
			if (isCRMNumber)
				return true; // CRM number has a priority.
			else
				return false; // mdTaskNumber
		else if (isCRMNumber)
			return true; // CRM number.
		else
			return false; // none are found. Think, it's an mdTaskNumber
	}

	/**
	 * Returns a count of mdstask's with a given mdtask number
	 * @param mdtaskNumber
	 * @return found count
	 * @throws MappingException
	 */
	private int findInMdTask(Long mdtaskId, String mdtaskNumber) throws MappingException {
		String sqlStr = "select count (*) from mdtask m where "
				+ (mdtaskId != null ? "m.id_mdtask=?1" : "TO_CHAR(m.mdtask_number)=?1 ");
		Query query = null;
		try {
			query = getEntityMgr().createNativeQuery(sqlStr);
			query.setParameter(1, mdtaskId != null ? mdtaskId : mdtaskNumber);
			return Utils.objToLong(query.getSingleResult()).intValue();
		}
		catch (Exception e) {
			throw new MappingException(e, ("Exception caught in findInMdTask " + e));
		}
	}

	private String getNumberByMdtaskId(Long mdtaskId) throws MappingException {
		String sqlStr = "select TO_CHAR(m.mdtask_number) from mdtask m where m.id_mdtask=?1";
		Query query = null;
		try {
			query = getEntityMgr().createNativeQuery(sqlStr);
			query.setParameter(1, mdtaskId);
			return (String) query.getSingleResult();
		}
		catch (Exception e) {
			throw new MappingException(e, ("Exception caught in findInMdTask " + e));
		}
	}

	/**
	 * Returns a count of crm 's with a given mdtask number
	 * @param mdtaskNumber
	 * @return found count
	 * @throws MappingException
	 */
	private int findInCRM(Long mdtaskId, String mdtaskNumber) throws MappingException {
		String sqlStr = "select count (*) from mdtask m where "
				+ (mdtaskId != null ? "upper(m.crmcode)=(select TO_CHAR(m2.mdtask_number) from mdtask m2 where m2.id_mdtask=?1) "
						: "upper(m.crmcode) = upper(?1) ");
		Query query = null;
		try {
			query = getEntityMgr().createNativeQuery(sqlStr);
			query.setParameter(1, mdtaskId != null ? mdtaskId : mdtaskNumber);
			return Utils.objToLong(query.getSingleResult()).intValue();
		}
		catch (Exception e) {
			throw new MappingException(e, ("Exception caught in findInCRM " + e));
		}
	}

	/**
	 * Returns tree of all ancestors of this department
	 * @param departmentId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private List<BigDecimal> getAllUpperDepartments(String departmentId) throws Exception {
		Query query = getEntityMgr().createNativeQuery(
				"select dp.id_department_par" + " from departments_par dp " + " where level > 0 "
						+ " start with dp.id_department_child = ?1"
						+ " connect by prior dp.id_department_par = dp.id_department_child");
		query.setParameter(1, Long.parseLong(departmentId));
		List<BigDecimal> list = query.getResultList();
		return list;
	}

	/**
	 * Returns tree of all ancestors of this department
	 * @param departmentId
	 * @return
	 * @throws Exception
	 */
	private String getDepartmentShortName(Long departmentId) throws Exception {
		try {
			Query query = getEntityMgr().createNativeQuery(
					"select dep.shortname from departments dep where dep.id_department = ? ");
			query.setParameter(1, departmentId);
			return (String) query.getSingleResult();
		}
		catch (Exception e) {
			return "";
		}
	}

	/**
	 * Returns list of all expert departments in the system
	 */
	@SuppressWarnings("unchecked")
	private List<BigDecimal> getExpertDepartments() throws Exception {
		Query query = getEntityMgr().createNativeQuery(
				"select id_dep from func_group_dep where id_group = " + EXPERT_DEP);
		List<BigDecimal> list = query.getResultList();
		return list;
	}

	@Override
	public Long getProcessTypeIdByMdtaskId(Long mdtaskId) throws MappingException {
		String sqlStr = "select t.id_type_process from processes t "
				+ "inner join mdtask m on t.id_process = m.id_pup_process " + "where m.id_mdtask = ?1 ";
		Query query = null;
		try {
			query = getEntityMgr().createNativeQuery(sqlStr);
			query.setParameter(1, mdtaskId);
			return Utils.objToLong(query.getSingleResult());
		}
		catch (Exception e) {
			throw new MappingException(e, ("Exception caught in getProcessTypeId " + e));
		}
	}

}
