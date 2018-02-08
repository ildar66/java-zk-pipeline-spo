package com.vtb.mapping.jdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import ru.masterdm.compendium.domain.Currency;
import ru.masterdm.compendium.domain.Department;
import ru.masterdm.compendium.domain.User;
import ru.masterdm.compendium.domain.cc.CcResolutionStatus;
import ru.masterdm.compendium.domain.cc.QuestionType;
import ru.masterdm.compendium.domain.crm.ComissionSize;
import ru.masterdm.compendium.domain.crm.CommissionType;
import ru.masterdm.compendium.domain.crm.Ensuring;
import ru.masterdm.compendium.domain.crm.Organization;
import ru.masterdm.compendium.domain.crm.PatternPaidPercentType;
import ru.masterdm.compendium.domain.crm.StatusReturn;
import ru.masterdm.compendium.domain.spo.CRMRepayment;
import ru.masterdm.compendium.domain.spo.CalcBase;
import ru.masterdm.compendium.domain.spo.ContractorType;
import ru.masterdm.compendium.domain.spo.Person;
import ru.masterdm.compendium.domain.spo.StandardPriceCondition;
import ru.masterdm.compendium.domain.spo.StopFactor;
import ru.masterdm.compendium.domain.spo.StopFactorType;
import ru.masterdm.compendium.model.ActionProcessorFactory;
import ru.masterdm.compendium.model.CompendiumSpoActionProcessor;
import ru.md.domain.OtherGoal;
import ru.md.domain.Withdraw;

import com.vtb.domain.BaseRate;
import com.vtb.domain.CdRiskpremium;
import com.vtb.domain.Comment;
import com.vtb.domain.Commission;
import com.vtb.domain.CommissionDeal;
import com.vtb.domain.Contract;
import com.vtb.domain.DepartmentAgreement;
import com.vtb.domain.Deposit;
import com.vtb.domain.DepositorFinStatus;
import com.vtb.domain.EarlyPayment;
import com.vtb.domain.FactPercent;
import com.vtb.domain.Fine;
import com.vtb.domain.Forbidden;
import com.vtb.domain.GeneralCondition;
import com.vtb.domain.Guarantee;
import com.vtb.domain.InterestPay;
import com.vtb.domain.LiquidityLevel;
import com.vtb.domain.Main;
import com.vtb.domain.OperationType;
import com.vtb.domain.Operator;
import com.vtb.domain.OtherCondition;
import com.vtb.domain.PaymentSchedule;
import com.vtb.domain.PrincipalPay;
import com.vtb.domain.PromissoryNote;
import com.vtb.domain.SpecialCondition;
import com.vtb.domain.SupplyType;
import com.vtb.domain.Task;
import com.vtb.domain.TaskContractor;
import com.vtb.domain.TaskCurrency;
import com.vtb.domain.TaskDepartment;
import com.vtb.domain.TaskHeader;
import com.vtb.domain.TaskManager;
import com.vtb.domain.TaskProcent;
import com.vtb.domain.TaskProduct;
import com.vtb.domain.TaskStopFactor;
import com.vtb.domain.Trance;
import com.vtb.domain.Warranty;
import com.vtb.util.TaskInfoLimitTreeBuilder;

import ru.masterdm.compendium.domain.Currency;
import ru.masterdm.compendium.domain.Department;
import ru.masterdm.compendium.domain.User;
import ru.masterdm.compendium.domain.cc.CcResolutionStatus;
import ru.masterdm.compendium.domain.cc.QuestionType;
import ru.masterdm.compendium.domain.crm.ComissionSize;
import ru.masterdm.compendium.domain.crm.CommissionType;
import ru.masterdm.compendium.domain.crm.Ensuring;
import ru.masterdm.compendium.domain.crm.Organization;
import ru.masterdm.compendium.domain.crm.PatternPaidPercentType;
import ru.masterdm.compendium.domain.crm.StatusReturn;
import ru.masterdm.compendium.domain.spo.CRMRepayment;
import ru.masterdm.compendium.domain.spo.CalcBase;
import ru.masterdm.compendium.domain.spo.ContractorType;
import ru.masterdm.compendium.domain.spo.Person;
import ru.masterdm.compendium.domain.spo.StandardPriceCondition;
import ru.masterdm.compendium.domain.spo.StopFactor;
import ru.masterdm.compendium.domain.spo.StopFactorType;
import ru.masterdm.compendium.model.ActionProcessorFactory;
import ru.masterdm.compendium.model.CompendiumSpoActionProcessor;
import ru.md.domain.Withdraw;

/**
* Helper class for performing READ operations of Task object
* @author Kuznetsov Michael
**/
public class TaskMapperReadHelper {

    private static final Logger LOGGER = Logger.getLogger(TaskMapperReadHelper.class.getName());

    protected static final String CMD_FIND_BY_KEY = "SELECT m.PARENTID, m.ID_PUP_PROCESS, m.ADDSUPPLY, m.MDTASK_NUMBER,m.tasktype, m.PRIORITY, "
        + "m.INITDEPARTMENT, m.MANAGER, m.PLACE, m.MDTASK_SUM, m.CURRENCY, m.LIMIT_ISSUE_SUM, m.DEBT_LIMIT_SUM, m.IS_LIMIT_SUM, m.IS_DEBT_SUM,m.IRREGULAR,m.PRODUCT_NAME, "
        + "m.VALIDFROM, m.USEDATEFROM, m.USEDATE, m.PERIOD, m.PERIOD_COMMENT, m.PROPOSED_DT_SIGNING, m.USEPERIOD, m.USEPERIODTYPE, "
        + "m.DESCRIPTION, m.ID_AUTHORIZEDBODY ,m.QUALITY_CATEGORY, m.QUALITY_CATEGORY_DESC, m.LOAN_CLASS, "
        + "m.DISCHARGE,  m.OPPORTUNITYID, m.\"DELETED\", "
        + "m.EXCHANGE_RATE, m.generalcondition,m.subplace, "
        + "m.CRMID, m.CRMLIMITNAME, m.CRMCODE, m.CURRENCYLIST, m.ID_QUESTION_TYPE, m.PREAMBULO, "
        + "m.TEMP_RESOLUTION, m.TEMP_MEETINGDATE, m.meeting_proposed_date, "
        + "m.IS_3FACES, m.is_renewable, m.may_be_renewable, m.extra_sum_info, m.redistrib_residues, m.is_project_fin, "
        + "tp.description_process,m.CRMINLIMIT, m.guarantee_cond, m.d_cond, "
        + "m.STATUSRETURN, m.STATUSRETURNTEXT, m.STATUSRETURNDATE,STATUSRETURNWHO, "
        + "m.IS_SUPPLY_EXIST, m.cfactor, m.trance_comment, "
        + "m.IS_GUARANTEE, m.CONTRACT, m.WARRANTY_ITEM, m.BENEFICIARY, m.BENEFICIARY_OGRN, "
        + "ca.cc_cache_date, ca.cc_cache_protocol, ca.cc_cache_statusid,ca.id_report, m.crm_queue_id, m.TARGET_TYPE_COMMENT "
        + "FROM MDTASK m "
        + "left outer join md_cc_cache ca on ca.id_mdtask= m.id_mdtask "
        + "left outer join processes p on p.id_process= m.id_pup_process "
        + "left outer join type_process tp on tp.id_type_process= p.id_type_process "
        + "where m.ID_MDTASK=? ";

    /**
     * Читаем контрагентов из базы данных
     */
    public static void readContragents(Connection conn, Task task) {
        try {
            PreparedStatement st = conn.prepareStatement(
                    "SELECT r.ID_R, r.ID_CRMORG,RATINGPKR "
                  + "FROM R_ORG_MDTASK r where ID_MDTASK=? order by order_disp");
            st.setObject(1, task.getId_task());
            ResultSet r = st.executeQuery();
            while (r.next()) {
                try {
                    Organization org = new Organization(r.getString("ID_CRMORG"));
                    org.setOrganizationName("<Контрагент не найден>");
                    task.getContractors().add(new TaskContractor(org, null, r.getLong("ID_R"),r.getString("RATINGPKR")));
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                    e.printStackTrace();
                }
            }
            r.close();
            st.close();

            // первый конграгент в списке -- всегда основной заемщик
            if (task.getContractors().size() > 0)
                task.getContractors().get(0).setMainBorrower(true);

            // названия,тип и рейтинг контрагентов
            // делаю отдельный запрос на каждого контрагента, т.к. это работает гораздо быстрее,
            // чем join через dblink
            st = conn.prepareStatement("select organizationname,CLIENTCATEGORY from  v_organisation where crmid=?");
            for (int i = 0; i < task.getContractors().size(); i++) {
                // название
                st.setObject(1, task.getContractors().get(i).getOrg().getAccountid());
                r = st.executeQuery();
                if (r.next()) {
                    task.getContractors().get(i).getOrg().setOrganizationName(r.getString("organizationname"));
                    task.getContractors().get(i).getOrg().setClientCategory(r.getString("CLIENTCATEGORY"));
                }
                r.close();
            }
            st.close();
            st = conn.prepareStatement("select id_contractor_type from r_contractor_type_mdtask where id_r=?");
            for (int i = 0; i < task.getContractors().size(); i++) {
                //тип
                st.setObject(1, task.getContractors().get(i).getId());
                r = st.executeQuery();
                task.getContractors().get(i).setOrgType(new ArrayList<ContractorType>());
                while (r.next()) {
                    task.getContractors().get(i).getOrgType().add(new ContractorType(r.getLong("id_contractor_type")));
                }
                r.close();

            }
            st.close();
            // первый конграгент в списке -- всегда основной заемщик. Дублируем его информацию (только для нужд отчетов,
            // ничего туда не заполняем при заполнении формы!)
            if (task.getContractors().size() > 0)
                task.setMainBorrower(task.getContractors().get(0));
            else task.setMainBorrower(null);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /**
     * Читаем основные параметры из базы данных
     */
    public static void readParameters(Connection conn, Task task) {
        try {
            PreparedStatement st = conn.prepareStatement(
                 "select t.PARENTID, t.crmid, t.CRMCODE, t.CRMINLIMIT, t.MDTASK_NUMBER,t.tasktype, t.mdtask_sum, t.currency, t.initdepartment,"
                +"tp.description_process,t.ID_PUP_PROCESS,t.PERIOD,t.PERIODDIMENSION, t.validto, t.id_operationtype, "
                +"sk.name as skname,t.OPPORTUNITYID, t.STATUSRETURN, p.id_type_process,subplace, "
                +"t.is_renewable, t.may_be_renewable, t.EXCHANGE_RATE, pr.IS_FIXED, t.id_limit_type, lt.name_limit_type,crm_queue_id, "
              	+"t.LIMIT_ISSUE_SUM, t.DEBT_LIMIT_SUM, t.IS_LIMIT_SUM, t.IS_DEBT_SUM, t.IRREGULAR,t.PRODUCT_NAME, asp.name_source, t.credit_decision_project,  "
              	+"t.changed_conditions, t.country, t.version, t.TARGET_TYPE_COMMENT "
              	+"from  mdtask t "
                +"left outer join  processes p on p.id_process= t.id_pup_process "
                +"left outer join  type_process tp on tp.id_type_process= p.id_type_process "
                +"left outer join CR_SDELKA_TYPE sk on t.id_operationtype=sk.id "
                +"left outer join PROCENT pr on pr.id_mdtask=t.ID_MDTASK "
                +"left outer join limit_type lt on t.id_limit_type = lt.id_limit_type "
                +"left outer join CD_ACREDETIV_SOURCE_PAYMENT asp on t.acredetiv_source = asp.id_source "
                +"where t.ID_MDTASK=?");
            st.setObject(1, task.getId_task());
            Main main = task.getMain();
            TaskHeader header = task.getHeader();
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                main.setExchangeRate(rs.getBigDecimal("EXCHANGE_RATE"));
                header.setCrmQueueId(rs.getString("crm_queue_id"));
                header.setCrmid(rs.getString("CRMID"));
                header.setCrmcode(rs.getString("CRMCODE"));
                header.setNumber(rs.getLong("MDTASK_NUMBER"));
                header.setTasktype(rs.getString("tasktype"));
                header.setStartDepartment(new Department(rs.getInt("INITDEPARTMENT")));
                header.setSubplace(rs.getLong("subplace"));
                header.setVersion(rs.getLong("VERSION"));
                main.setSum(getBigDecimal(rs,"MDTASK_SUM"));
                main.setLimitIssueSum(getBigDecimal(rs,"LIMIT_ISSUE_SUM"));
                main.setDebtLimitSum(getBigDecimal(rs,"DEBT_LIMIT_SUM"));
                main.setLimitIssue("y".equalsIgnoreCase(rs.getString("IS_LIMIT_SUM")));
                main.setDebtLimit("y".equalsIgnoreCase(rs.getString("IS_DEBT_SUM")));
                main.setIrregular("y".equalsIgnoreCase(rs.getString("IRREGULAR")));
                main.setProduct_name(rs.getString("PRODUCT_NAME"));
                main.setCurrency(new Currency(rs.getString("CURRENCY")));
                main.setDescriptionProcess(rs.getString("description_process"));
                main.setIdProcessType(rs.getLong("id_type_process"));
                task.setId_pup_process_type(rs.getInt("id_type_process"));
                task.setId_pup_process(rs.getLong("ID_PUP_PROCESS"));
                task.setParent(rs.getLong("PARENTID"));
                if (rs.wasNull()) {
                    task.setParent(null);
                }
                main.setPeriod(rs.getInt("PERIOD"));
                main.setPeriodDimension(rs.getString("PERIODDIMENSION"));
                main.setValidto(rs.getDate("VALIDTO"));
                header.setOperationtype(new OperationType(rs.getLong("id_operationtype"), rs.getString("skname")));
                task.setInLimit(rs.getString("CRMINLIMIT"));
                task.setOPPORTUNITYID(rs.getString("OPPORTUNITYID"));
                main.setAcredetivSourcePayment(rs.getString("name_source"));
                if (rs.getString("is_renewable") == null) main.setRenewable(true);
                else main.setRenewable(rs.getString("is_renewable").equals("y") ? true : false);
                if (rs.getString("may_be_renewable") == null) main.setMayBeRenewable(true);
                else main.setMayBeRenewable(rs.getString("may_be_renewable").equals("y") ? true : false);
                boolean rateTypeFixed;
                if (rs.getString("IS_FIXED") == null) rateTypeFixed = true;
                else rateTypeFixed = rs.getString("IS_FIXED").equalsIgnoreCase("y");
                task.setTaskProcent(new TaskProcent(null,null,null,null,null,null,null,rateTypeFixed));

                header.setIdLimitType(getInt(rs, "ID_LIMIT_TYPE"));
                header.setLimitTypeName(rs.getString("name_limit_type"));
                task.getTaskStatusReturn().setStatusReturn(new StatusReturn(rs.getString("STATUSRETURN"),"",""));
                task.setCreditDecisionProject(rs.getString("credit_decision_project"));
                main.setChangedConditions(rs.getString("changed_conditions"));
                main.setCountry(rs.getString("country"));
            }
            rs.close();
            st.close();

            st = conn.prepareStatement("select a.value_var from  variables  v inner join " +
                    " attributes a on a.id_var=v.id_var where v.name_var=? and a.id_process=?");
            st.setObject(1, "Статус");
            st.setObject(2, task.getId_pup_process());
            ResultSet r = st.executeQuery();
            while (r.next()) {
                task.getHeader().setStatus(r.getString("value_var"));
            }
            r.close();
            st.setObject(1, "Приоритет");
            st.setObject(2, task.getId_pup_process());
            r = st.executeQuery();
            while (r.next()) {
                task.getHeader().setPriority(r.getString("value_var"));
            }
            r.close();
            st.setObject(1, "Тип кредитной заявки");
            st.setObject(2, task.getId_pup_process());
            r = st.executeQuery();
            while (r.next()) {
                task.getHeader().setProcessType(r.getString("value_var"));
            }
            r.close();
            st.close();

            //названия департаментов
            if (task.getHeader().getStartDepartment() != null) {
                st = conn.prepareStatement("select d.shortname from departments d where d.id_department=?");
                st.setObject(1, task.getHeader().getStartDepartment().getId());
                r = st.executeQuery();
                while (r.next()) {
                    task.getHeader().getStartDepartment().setShortName(r.getString("shortname"));
                }
                r.close();
                st.close();
            }
            task.getHeader().generateCombinedNumber();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /**
     * Читаем из базы данных параметры для полной заявки
     */
    public static boolean readParametersFull(Connection conn, Task task) {
        try {
            boolean isExist = false;
            PreparedStatement st = conn.prepareStatement(CMD_FIND_BY_KEY);
            st.setObject(1, task.getId_task());
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                isExist = true;
                task.setId_pup_process(rs.getLong("ID_PUP_PROCESS"));
                task.getTaskStatusReturn().setStatusReturn(new StatusReturn(rs.getString("STATUSRETURN"),"",""));
                task.getTaskStatusReturn().setStatusReturnText(rs.getString("STATUSRETURNTEXT"));
                task.getTaskStatusReturn().setDateReturn(rs.getDate("STATUSRETURNDATE"));
                task.getTaskStatusReturn().setIdUser(rs.getLong("STATUSRETURNWHO"));
                task.setParent(rs.getLong("PARENTID"));
                if (rs.wasNull()) {
                    task.setParent(null);
                }
                task.setInLimit(rs.getString("CRMINLIMIT"));
                task.setDescription(rs.getString("DESCRIPTION"));
                task.setAuthorizedBody(rs.getInt("ID_AUTHORIZEDBODY"));
                task.setCcQuestionType(new QuestionType(rs.getInt("ID_QUESTION_TYPE")));
                task.setCcPreambulo(rs.getString("PREAMBULO"));
                task.setOPPORTUNITYID(rs.getString("OPPORTUNITYID"));
                task.setDeleted(rs.getString("deleted").equals('Y'));
                task.setFaces3(rs.getString("IS_3FACES").equalsIgnoreCase("y"));
                task.setTranceComment(rs.getString("trance_comment"));
                task.getMain().setGenCondLimit(rs.getString("generalcondition"));
                // обеспечение
                task.getSupply().setAdditionSupply(rs.getString("ADDSUPPLY"));
                task.getSupply().setExist(rs.getString("IS_SUPPLY_EXIST").equalsIgnoreCase("y"));
                task.getSupply().setGuaranteeCondition(rs.getString("guarantee_cond"));
                task.getSupply().setDCondition(rs.getString("d_cond"));
                task.getSupply().setCfact(rs.getObject("cfactor")==null?null:rs.getDouble("cfactor"));
                //временные
                task.getTemp().setMeetingDate(rs.getDate("TEMP_MEETINGDATE"));
                task.getTemp().setPlanMeetingDate(rs.getDate("meeting_proposed_date"));
                task.getTemp().setResolution(rs.getString("TEMP_RESOLUTION"));
                task.getCcStatus().setMeetingDate(rs.getDate("cc_cache_date"));
                task.getCcStatus().setProtocol(rs.getString("cc_cache_protocol"));
                task.getCcStatus().setStatus(new CcResolutionStatus(rs.getLong("cc_cache_statusid"),null,null,null));
                task.getCcStatus().setId_template(rs.getLong("id_report"));
                // заголовок
                TaskHeader header = task.getHeader();
                header.setManager(rs.getString("MANAGER"));
                header.setPlace(new Department(rs.getInt("PLACE")));
                header.setStartDepartment(new Department(rs.getInt("INITDEPARTMENT")));
                header.setSubplace(rs.getLong("subplace"));
                header.setCrmQueueId(rs.getString("crm_queue_id"));
                header.setCrmid(rs.getString("CRMID"));
                header.setCrmcode(rs.getString("CRMCODE"));
                header.setCrmlimitname(rs.getString("CRMLIMITNAME"));
                header.setCrmcurrencylist(rs.getString("CURRENCYLIST"));
                header.setNumber(rs.getLong("MDTASK_NUMBER"));
                header.setTasktype(rs.getString("tasktype"));

                /*******************************************************************************************************
                 *                                   Секция 'Общие условия'                                            *
                 *******************************************************************************************************/

                GeneralCondition generalCondition = task.getGeneralCondition();
                generalCondition.setLoan_class(rs.getString("LOAN_CLASS"));
                generalCondition.setDischarge(rs.getString("DISCHARGE"));

                /*******************************************************************************************************
                 *                                   Секция 'Основные параметры'. Та часть, что в общем запросе        *
                 *******************************************************************************************************/
                if (task.isLimit() || task.isSubLimit()) {
                    // лимит \ сублимит
                    Main main = task.getMain();
                    main.setSum(rs.getBigDecimal("MDTASK_SUM"));
                    main.setCurrency(new Currency(rs.getString("CURRENCY")));
                    main.setExchangeRate(rs.getBigDecimal("EXCHANGE_RATE"));
                    main.setTargetTypeComment(rs.getString("TARGET_TYPE_COMMENT"));
                    main.setValidfrom(rs.getDate("VALIDFROM"));
                    main.setUsedatefrom(rs.getDate("USEDATEFROM"));
                    main.setUsedate(rs.getDate("USEDATE"));
                    main.setPeriod(getInt(rs,"PERIOD"));
                    main.setUseperiod(getInt(rs,"USEPERIOD"));

                    main.setDescriptionProcess(rs.getString("description_process"));

                    boolean renewable, mayBeRenewable, projectFin, redistribResidues;
                    if (rs.getString("is_renewable") == null) renewable = true;
                    else renewable = rs.getString("is_renewable").equalsIgnoreCase("y");
                    main.setRenewable(renewable);
                    if (rs.getString("may_be_renewable") == null) mayBeRenewable = true;
                    else mayBeRenewable = rs.getString("may_be_renewable").equalsIgnoreCase("y");
                    main.setMayBeRenewable(mayBeRenewable);

                    if (rs.getString("is_project_fin") == null) projectFin = false;
                    else projectFin = rs.getString("is_project_fin").equalsIgnoreCase("y");
                    main.setProjectFin(projectFin);

                    if (task.isLimit()) {
                        if (rs.getString("redistrib_residues") == null) redistribResidues = false;
                        else redistribResidues = rs.getString("redistrib_residues").equalsIgnoreCase("y");
                        main.setRedistribResidues(redistribResidues);
                    }
                    if (task.isSubLimit()) main.setExtraSumInfo(rs.getString("extra_sum_info"));

                    generalCondition.setQuality_category(rs.getString("QUALITY_CATEGORY"));
                    generalCondition.setQuality_category_desc(rs.getString("QUALITY_CATEGORY_DESC"));
                    main.setProposedDateSigningAgreement(rs.getDate("PROPOSED_DT_SIGNING"));
                } else {
                    // сделка
                    Main main = task.getMain();
                    main.setGuaranteeType("y".equalsIgnoreCase(rs.getString("IS_GUARANTEE")));
                    main.setContract(rs.getString("CONTRACT"));
                    main.setWarrantyItem(rs.getString("WARRANTY_ITEM"));
                    main.setBeneficiary(rs.getString("BENEFICIARY"));
                    main.setBeneficiaryOGRN(rs.getString("BENEFICIARY_OGRN"));

                    main.setPeriod(getInt(rs,"PERIOD"));
                    main.setPeriodComment(rs.getString("PERIOD_COMMENT"));
                    main.setTargetTypeComment(rs.getString("TARGET_TYPE_COMMENT"));

                    main.setSum(getBigDecimal(rs,"MDTASK_SUM"));
                    main.setLimitIssueSum(getBigDecimal(rs,"LIMIT_ISSUE_SUM"));
                    main.setDebtLimitSum(getBigDecimal(rs,"DEBT_LIMIT_SUM"));
                    main.setLimitIssue("y".equalsIgnoreCase(rs.getString("IS_LIMIT_SUM")));
                    main.setDebtLimit("y".equalsIgnoreCase(rs.getString("IS_DEBT_SUM")));
                    main.setIrregular("y".equalsIgnoreCase(rs.getString("IRREGULAR")));
                    main.setProduct_name(rs.getString("PRODUCT_NAME"));
                    main.correctQuantityData();
                    main.setCurrency(new Currency(rs.getString("CURRENCY")));
                    main.setExchangeRate(rs.getBigDecimal("EXCHANGE_RATE"));

                    main.setUseperiod(rs.getObject("USEPERIOD") == null ? null : rs.getInt("USEPERIOD"));
                    main.setUsedate(rs.getDate("USEDATE"));
                    main.setUseperiodtype(rs.getString("USEPERIODTYPE"));

                    boolean projectFin = false;
                    if (rs.getString("is_project_fin") == null) projectFin = false;
                    else projectFin = rs.getString("is_project_fin").equalsIgnoreCase("y");
                    main.setProjectFin(projectFin);

                    main.setProposedDateSigningAgreement(rs.getDate("PROPOSED_DT_SIGNING"));

                    main.setValidfrom(rs.getDate("VALIDFROM"));
                    main.setUsedatefrom(rs.getDate("USEDATEFROM"));

                    main.setDescriptionProcess(rs.getString("description_process"));

                    generalCondition.setQuality_category(rs.getString("QUALITY_CATEGORY"));
                    generalCondition.setQuality_category_desc(rs.getString("QUALITY_CATEGORY_DESC"));
                }
            }
            rs.close();
            st.close();
            return isExist;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Читаем из базы данных секцию 'Договоры'.
     */
    public static void readContract(Connection conn, Task task){
    	final String sql = "select c.contract from contract c where ID_MDTASK = ? ";
    	try{
    		PreparedStatement st = conn.prepareStatement(sql);
    		st.setObject(1, task.getId_task());
    		ResultSet r = st.executeQuery();
    		while (r.next()) {
                try {
                	task.getContractList().add(
                        new Contract(r.getString("contract"))
                    );
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                    e.printStackTrace();
                }
            }
            r.close();
            st.close();
    	}catch (Exception e){
    		LOGGER.log(Level.SEVERE, e.getMessage(), e);
    		e.printStackTrace();
    	}
    }

    /**
     * Читаем из базы данных секцию 'Транши'.
     */
    public static void readTranches(Connection conn, Task task) {
        try {
            PreparedStatement st = conn.prepareStatement(
                "select id, sum, currency, usedatefrom, usedateto from trance"
               +" where ID_MDTASK=? order by id");
            st.setObject(1, task.getId_task());
            ResultSet r = st.executeQuery();
            while (r.next()) {
                try {
                    task.getTranceList().add(
                        new Trance(r.getLong("id"), r.getBigDecimal("sum"),new Currency(r.getString("currency")),
                            r.getDate("usedatefrom"),r.getDate("usedateto")
                    ));
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                    e.printStackTrace();
                }
            }
            r.close();
            st.close();
            //читаем выдачи
            st = conn.prepareStatement("select id,sum,currency,usedatefrom,usedateto,quarter,month,year,hyear, SUM_SCOPE, "
            		+ "PERIOD_DIMENSION_FROM, PERIOD_DIMENSION_BEFORE, FROM_PERIOD, BEFORE_PERIOD "
            		+ "from withdraw where id_trance is null and id_mdtask = ?");
            st.setObject(1, task.getId_task());
            r = st.executeQuery();
            List<Withdraw> taskwithdraws = new ArrayList<Withdraw>();
            while (r.next()) {
            	taskwithdraws.add(new Withdraw(task.getId_task(), null, r.getDouble("sum"), r.getString("currency"), 
            			r.getDate("usedatefrom"), r.getDate("usedateto"), r.getLong("month"), 
            			r.getLong("quarter"),r.getLong("year"),r.getLong("hyear"), r.getString("SUM_SCOPE"), 
            			r.getString("PERIOD_DIMENSION_FROM"), r.getString("PERIOD_DIMENSION_BEFORE"),
            			r.getLong("FROM_PERIOD"), r.getLong("BEFORE_PERIOD")));
            }
            task.setWithdraws(taskwithdraws);
            for(Trance trance : task.getTranceList()){
            	st = conn.prepareStatement("select id,sum,currency,usedatefrom,usedateto,quarter,month,year,hyear,SUM_SCOPE, "
            			+ "PERIOD_DIMENSION_FROM, PERIOD_DIMENSION_BEFORE, FROM_PERIOD, BEFORE_PERIOD "
            			+ "from withdraw where id_trance = ?");
            	st.setObject(1, trance.getId());
            	r = st.executeQuery();
            	taskwithdraws = new ArrayList<Withdraw>();
            	while (r.next()) {
            		taskwithdraws.add(new Withdraw(task.getId_task(), null, r.getDouble("sum"), r.getString("currency"), 
            				r.getDate("usedatefrom"), r.getDate("usedateto"), r.getLong("month"), 
            				r.getLong("quarter"),r.getLong("year"),r.getLong("hyear"), r.getString("SUM_SCOPE"), 
            				r.getString("PERIOD_DIMENSION_FROM"), r.getString("PERIOD_DIMENSION_BEFORE"),
            				r.getLong("FROM_PERIOD"), r.getLong("BEFORE_PERIOD")));
            	}
            	trance.setWithdraws(taskwithdraws);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /**
     * Начитываем виды сделок
     * @param conn
     * @param task
     * @return
     */
    public static ArrayList<TaskProduct> readProductTypes(Connection conn, Task task) {
    	ArrayList<TaskProduct> result = new ArrayList<TaskProduct>();
    	try {
            PreparedStatement st = conn.prepareStatement(
                   "select r.id_opp_type, r.validto, r.useperiod, r.descr, t.name, t.family,r.PERIODDIMENSION  "
                  + "from R_MDTASK_OPP_TYPE r "
                  + "left outer join v_spo_product t on r.id_opp_type = t.productId"
                  +" where ID_MDTASK=? order by t.name");
                st.setObject(1, task.getId_task());
                ResultSet r = st.executeQuery();
                while (r.next()) {
                    if ((r.getString("id_opp_type") != null) && (!r.getString("id_opp_type").equals("")))
                    try {
                    	result.add(
                            new TaskProduct(
                                r.getString("id_opp_type"), r.getString("name"), r.getString("family"),
                                r.getDate("validto"),
                                getInt(r, "useperiod"),
                                r.getString("descr"),
                                r.getString("PERIODDIMENSION")
                        ));
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        e.printStackTrace();
                    }
                }
                r.close();
                st.close();
    	} catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
        }
    	return result;
    }

    /**
     * Начитываем виды сделок для родителя
     * @param conn
     * @param taskMapper
     * @param task
     * @return
     */
    private static ArrayList<TaskProduct> readParentProductTypes(Connection conn, TaskMapper taskMapper, Task task) {
    	ArrayList<TaskProduct> result = new ArrayList<TaskProduct>();
    	if (!(task.isLimit() || task.isSubLimit())) {
    		Task loopTask = task;
    		//Найдем виды сделок для вышележащего лимита или сублимита
            while (result.isEmpty() && (loopTask.getParent() != null)) {
            	Task parent = new Task(loopTask.getParent());
            	readParameters(conn, parent);
            	result = readProductTypes(conn, parent);
                loopTask = parent;
            }
		}
    	return result;
    }


    /**
     * Читаем из базы данных секцию 'Основные параметры (основная часть данных)'
     */
    public static void readMainParameters(Connection conn, TaskMapper taskMapper, Task task) {
        try {
        	PreparedStatement st;
        	ResultSet r;

        	//Виды сделок
        	task.getHeader().setOpportunityTypes(readProductTypes(conn, task));
        	task.getHeader().setParentOpportunityTypes(readParentProductTypes(conn, taskMapper, task));

            // Иные цели
            st = conn.prepareStatement("SELECT ID_TARGET, DESCR, ID_CRM_TARGET_TYPE " 
                    + "FROM r_mdtask_otherGoals where ID_MDTASK=? order by id_target");
            st.setObject(1, task.getId_task());
            r = st.executeQuery();
            while (r.next()) {
                try {
                	String idTargetString = r.getString("ID_TARGET");
                	Long idTarget = idTargetString != null ? new Long(idTargetString.trim()) : null;                	
                    task.getMain().getOtherGoals().add(new OtherGoal(idTarget, r.getString("DESCR"), r.getString("ID_CRM_TARGET_TYPE")));
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                    e.printStackTrace();
                }
            }
            r.close();
            st.close();
            
           // Запрещается предоставление денежных средств на любую из нижеуказанных целей (прямо или косвенно, через третьих лиц)
            st = conn.prepareStatement("SELECT id_target, DESCR " 
                    + "FROM r_mdtask_forbidden where ID_MDTASK=? order by id_target");
            st.setObject(1, task.getId_task());
            r = st.executeQuery();
            while (r.next()) {
                try {
                    task.getMain().getForbiddens().add(new Forbidden(r.getString("DESCR")));
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                    e.printStackTrace();
                }
            }
            r.close();
            st.close();
            
            if (task.isLimit() || task.isSubLimit()) {

                // валюты Лимита \ Сублимита
                // загружаем все сохраненные валюты
                st = conn.prepareStatement("SELECT id_currency,FLAG FROM V_CD_CURRENCY inner join r_mdtask_currency on id_currency=code where ID_MDTASK=?");
                st.setObject(1, task.getId_task());
                r = st.executeQuery();
                while (r.next()) {
                    try {
                        task.getCurrencyList().add(
                                new TaskCurrency (null, r.getBoolean("FLAG"),
                                    new Currency(r.getString("id_currency")))
                                );
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        e.printStackTrace();
                    }
                }
                r.close();
                st.close();

                // Порядок принятия решений о проведении операций
                st = conn.prepareStatement("SELECT descr "
                        + "FROM r_mdtask_oper_decision where ID_MDTASK=?");
                st.setObject(1, task.getId_task());
                r = st.executeQuery();
                while (r.next()) {
                    try {
                    	if (task.getSimpleOperationDecisionList() == null)
                    		task.setSimpleOperationDecisionList(new ArrayList<String>());
                        task.getSimpleOperationDecisionList().add(r.getString("descr"));
                    } catch (Exception e) {
                        //LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        e.printStackTrace();
                    }
                }
                r.close();
                st.close();
            } else {
             // загружаем все сохраненные валюты
                st = conn.prepareStatement("SELECT id_currency,FLAG FROM V_CD_CURRENCY inner join r_mdtask_currency on id_currency=code where ID_MDTASK=?");
                st.setObject(1, task.getId_task());
                r = st.executeQuery();
                while (r.next()) {
                    try {
                        task.getCurrencyList().add(
                                new TaskCurrency (null, r.getBoolean("FLAG"),
                                    new Currency(r.getString("id_currency")))
                                );
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        e.printStackTrace();
                    }
                }
                r.close();
                st.close();

                // иерархия лимита-сублимитов, на которые ссылается сделка.
                if(task.getInLimit() != null){
                    TaskInfoLimitTreeBuilder builder = new TaskInfoLimitTreeBuilder(task.getInLimit(), true);
                    builder.makeOutput();
                    task.getMain().setLimitTreeList(builder.getLimitTreeList());
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
        }
    }


    /**
     * фактические значения процентной ставки начитываем
     * @param conn
     * @param task
     */
    private static void readFactProcent(Connection conn, Task task) {
        PreparedStatement st; ResultSet r;
        try {
            task.getFactPercentList().clear();
            st = conn.prepareStatement(
                    "SELECT f.id, f.start_date, f.end_date, f.fondrate,  f.riskpremium, "
            		+ " f.rate2, f.rate3, f.rate4, f.rate5, f.rate6, f.rate7, f.rate8, f.rate9, f.rate10, f.rate11, "
            		+ " f.supply, f.riskpremium_change, f.tranceid, f.rating_fondrate, f.rating_riskpremium, f.rating_rate3, f.rating_c1, f.rating_c2, f.rating_calc, f.rating_ktr, "
            		+ " pt.premium_name, f.premiumvalue, f.premiumcurr, f.premiumtext, pt.value as premium_type_value, "
            		+ " rs.description, rs.value "
                     +" FROM factpercent f"
                     +" left outer join cd_riskpremium rs on rs.id = f.riskpremiumtype "
                     +" left outer join cd_premium_type pt on pt.id_premium = f.premiumtype "
                     +" where ID_MDTASK=?"
                     +" order by start_date, end_date");
            st.setObject(1, task.getId_task());
            r = st.executeQuery();
            while(r.next()) {
                try {
                    FactPercent fp = new FactPercent();
                    fp.setId(getLong(r, "id"));
                    fp.setRiskpremium(getDouble(r, "riskpremium"));
                    fp.setEnd_date(r.getDate("end_date"));
                    fp.setStart_date(r.getDate("start_date"));
                    fp.setFondrate(getDouble(r, "fondrate"));
                    fp.setRate2(getDouble(r, "rate2"));
                    fp.setRate3(getDouble(r, "rate3"));
                    fp.setRate4(getDouble(r, "rate4"));
                    fp.setRate5(getDouble(r, "rate5"));
                    fp.setRate6(getDouble(r, "rate6"));
                    fp.setRate7(getDouble(r, "rate7"));
                    fp.setRate8(getDouble(r, "rate8"));
                    fp.setRate9(getDouble(r, "rate9"));
                    fp.setRate10(getDouble(r, "rate10"));
                    fp.setRate11(getDouble(r, "rate11"));
                    fp.setSupply(r.getString("supply"));

                    fp.setRating_fondrate(getDouble(r, "rating_fondrate"));
                    fp.setRating_riskpremium(getDouble(r, "rating_riskpremium"));
                    fp.setRating_rate3(getDouble(r, "rating_rate3"));
                    fp.setRating_с1(getDouble(r, "rating_c1"));
                    fp.setRating_с2(getDouble(r, "rating_c2"));
                    fp.setRating_calc(getDouble(r, "rating_calc"));
                    fp.setRating_ktr(getDouble(r, "rating_ktr"));

                    CdRiskpremium riskpremiumtype = new CdRiskpremium();
                    riskpremiumtype.setDescription(r.getString("description"));
                    riskpremiumtype.setValue(r.getString("value"));
                    fp.setRiskpremiumtype(riskpremiumtype);

                    fp.setRiskpremium_change(getDouble(r, "riskpremium_change"));
                    fp.setTranceId(getLong(r, "tranceId"));

                    task.getFactPercentList().add(fp);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                    e.printStackTrace();
                }
            }
            r.close();
            st.close();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
        }
        ArrayList<FactPercent> factPercentList=new ArrayList<FactPercent>();
        for(FactPercent fp : task.getFactPercentList()){
            fp.getCalcRate1();//side effect
            fp.getCalcRate2();//side effect
            fp.getCalcRate3();//side effect
            factPercentList.add(fp);
        }//отдельный массив нужен для того чтобы не было ConcurrentModificationException
        task.setFactPercentList(factPercentList);
    }

    /**
     * Читаем из базы данных секцию 'Стоимостные условия'
     */
    @SuppressWarnings("deprecation")
    public static void readPriceConditions(Connection conn, Task task) {
        PreparedStatement st; ResultSet r;
        try {
        	// фактические значения ставки
        	readFactProcent(conn, task);

        	// процентная ставка (Лимит)
            if (task.isLimit() || task.isSubLimit()) {
                st = conn.prepareStatement(
                    "SELECT p.ID_PROCENT, p.DESCRIPTION, p.ID_BASE, p.PROCENT_VALUE, p.PROCENT, p.RISKPREMIUM, p.CURRENCY, p.IS_FIXED, "
                     + "    p.ID_PROCENT_ORDER, p.capital_pay, p.ktr, "
                     + "    cdr.description as riskpremium_descr, cdr.value as riskpremium_default_value, m.turnover, "
                     + "	cdm.description as turnover_descr, "
                     + "	m.RISKPREMIUM_CHANGE,p.PRICEINDCONDITION,pay_int "
                     + " FROM MDTASK m "
                     + " left outer join PROCENT p on m.ID_MDTASK = p.ID_MDTASK "
                     + " left outer join cd_riskpremium cdr on m.riskpremium = cdr.id "
                     + " left outer join cd_credit_turnover_premium cdm on m.turnover_premium = cdm.id"
                     + " where m.ID_MDTASK=?"
                     + " order by p.ID_PROCENT");
                st.setObject(1, task.getId_task());
                r = st.executeQuery();
                if(r.next()) {
                    try {
                        boolean rateTypeFixed;
                        if (r.getString("IS_FIXED") == null) rateTypeFixed = true;
                        else rateTypeFixed = r.getString("IS_FIXED").equalsIgnoreCase("y");
                        task.setTaskProcent(
                            new TaskProcent(
                                getLong(r,"ID_PROCENT"),
                                r.getString("DESCRIPTION"),
                                new BaseRate(getInt(r,"ID_BASE")),
                                getDouble(r,"PROCENT_VALUE"),
                                getDouble(r,"PROCENT"),
                                getDouble(r,"RISKPREMIUM"),
                                new Currency(r.getString("CURRENCY")),
                                rateTypeFixed,
                                r.getString("riskpremium_descr"),
                                r.getString("riskpremium_default_value"),
                                getDouble(r,"turnover"),
                        		r.getString("turnover_descr"),
                        		getDouble(r, "riskpremium_change"),
                        		r.getString("capital_pay"),
                        		r.getString("ktr"),r.getString("PRICEINDCONDITION"),r.getString("pay_int")));
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        e.printStackTrace();
                    }
                }
                r.close();
                st.close();
                // получим стандартные стоимостные условия.
                task.getTaskProcent().getStandardPriceConditionList().clear();
                st = conn.prepareStatement(
                        "SELECT c.ID_PRICE_COND, s.NAME_STANDARD_PRICE_CONDITION "
                         +" FROM PROCENT_ST_PRICE_COND c"
                         +" left outer join STANDARD_PRICE_CONDITION s on c.ID_PRICE_COND = s.ID_STANDARD_PRICE_CONDITION "
                         +" where ID_MDTASK=?"
                         +" order by s.NAME_STANDARD_PRICE_CONDITION");

                st.setObject(1, task.getId_task());
                r = st.executeQuery();
                while(r.next()) {
                    try {
                        Long id = getLong(r,"ID_PRICE_COND");
                        if ((id.longValue() != 0) && (id.longValue() != -1))
                            task.getTaskProcent().getStandardPriceConditionList().add(
                               new StandardPriceCondition(id,r.getString("NAME_STANDARD_PRICE_CONDITION")));
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        e.printStackTrace();
                    }
                }
                r.close();
                st.close();

            }
            // комиссии Лимит
            if(task.isLimit() || task.isSubLimit()) {
                st = conn.prepareStatement("SELECT com.ID_COMMISSION, com.DESCRIPTION, com.crm_com_type, " +
                        "pt.short_text ptid, pt.text ptname,com.currency,com.commission_value " +
                        "FROM COMMISSION com  " +
                        "left outer join CRM_COM_PERIOD pt on pt.short_text = com.crm_com_period " +
                        "where ID_MDTASK=? " +
                        "order by com.ID_COMMISSION");
                st.setObject(1, task.getId_task());
                r = st.executeQuery();
                while (r.next()) {
                    try {
                        PatternPaidPercentType pt =
                            new PatternPaidPercentType(r.getString("ptid"), r.getString("ptname"));
                        task.getCommissionList().add(
                            new Commission(getLong(r,"ID_COMMISSION"),r.getString("DESCRIPTION"),
                                new Currency(r.getString("CURRENCY")),
                                new CommissionType(r.getString("crm_com_type")),
                                getDouble(r,"COMMISSION_VALUE"), null,
                                pt));
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        e.printStackTrace();
                    }
                }
                r.close();
                st.close();

            } else {
            // комиссии Сделка
                st = conn.prepareStatement("SELECT com.ID_COMMISSION, com.DESCRIPTION, com.CURRENCY, com.COMMISSION_VALUE, com.PAY_DESCR, " +
                        "pt.short_text ptid, pt.text ptname,cb.ID_CALC_BASE, cb.NAME_CALC_BASE, " +
                        "cz.short_text czid, cz.text czname, ct.short_text ctid, ct.TEXT ctname " +
                        "FROM COMMISSION com  " +
                        "left outer join CRM_COM_PERIOD pt on pt.short_text = com.crm_com_period " +
                        "left outer join CALC_BASE cb on cb.ID_CALC_BASE = com.ID_CALC_BASE  " +
                        "left outer join CRM_COM_BASE cz on cz.short_text = com.crm_com_base " +
                        "left outer join CRM_COM_TYPE ct on ct.short_text = com.crm_com_type  " +
                        "where com.ID_MDTASK=? " +
                        "order by com.ID_COMMISSION");
                st.setObject(1, task.getId_task());
                r = st.executeQuery();
                while (r.next()) {
                    try {
                        PatternPaidPercentType pt = null;
                        CalcBase cb = null;
                        ComissionSize cz = null;
                        CommissionType ct = null;
                        if (r.getString("ptname") != null)
                            pt = new PatternPaidPercentType(r.getString("ptid"), r.getString("ptname"));
                        else
                            pt = new PatternPaidPercentType("","");
                        if (r.getString("NAME_CALC_BASE") != null)
                            cb = new CalcBase(getLong(r,"ID_CALC_BASE"), r.getString("NAME_CALC_BASE"));
                        else
                            cb = new CalcBase(new Long(-1L), " ");
                        if (r.getString("czname") != null)
                            cz = new ComissionSize(r.getString("czid"), r.getString("czname"));
                        else
                            cz = new ComissionSize("", " ");
                        if (r.getString("ctname") != null)
                            ct = new CommissionType(r.getString("ctid"), r.getString("ctname"));
                        else
                            ct = new CommissionType("", " ");
                        CommissionDeal com = new CommissionDeal(getLong(r,"ID_COMMISSION"),r.getString("DESCRIPTION"),
                                new Currency(r.getString("CURRENCY")),
                                ct,
                                getDouble(r,"COMMISSION_VALUE"), pt, cb, cz,
                                r.getString("PAY_DESCR"));
                        task.getCommissionDealList().add(com);
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        e.printStackTrace();
                    }
                }
                r.close();
                st.close();
            }

            // график погашения основного долга
            if(!task.isLimit() || task.isSubLimit()) {
                st = conn.prepareStatement(
                        "select pp.id_prn_pay, pp.id_mdtask, pp.first_pay_dt, pp.final_pay_dt, pp.num_day, pp.is_depended, "
                        +" pp.description, pp.is_first_pay, pp.currency, "
                        + " crm.itemn_id, crm.text, crm.shorttext, pp.cmnt "
                        + " from PRINCIPAL_PAY pp"
                        + " left outer join crm_repayment crm on crm.itemn_id = pp.id_period_order"
                        + " where ID_MDTASK = ?"
                        + " order by pp.id_prn_pay");
                st.setObject(1, task.getId_task());
                r = st.executeQuery();
                if (r.next()) {
                    try {
                        CRMRepayment pt = null;
                        if (r.getString("itemn_id") != null) {
                            pt = new CRMRepayment(r.getString("itemn_id"), r.getString("text"));
                            pt.setName(r.getString("text"));
                            pt.setShortName(r.getString("shorttext"));
                        }
                        boolean isDepended = r.getString("is_depended").equalsIgnoreCase("y");
                        boolean isFirstPay = r.getString("is_first_pay").equalsIgnoreCase("y");
                        
                        task.setPrincipalPay(
                                new PrincipalPay(getLong(r,"id_prn_pay"), pt,
                                        r.getDate("first_pay_dt"), r.getDate("final_pay_dt"),
                                        getDouble(r,"num_day"), 
                                        isDepended, 
                                        r.getString("DESCRIPTION"), 
                                        isFirstPay, r.getString("cmnt"),
                                		r.getString("currency")) 
                        );
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        e.printStackTrace();
                    }
                } else { // insert null interest pay.
                    task.setPrincipalPay(new PrincipalPay(null));
                }
                r.close();
                st.close();
            }

            // график платежей
            st = conn.prepareStatement(
                "select t.id_schedule, t.id_mdtask, t.amount, t.from_dt, t.to_dt,t.currency,fondrate,period,MANUAL_FONDRATE,TRANCE_ID,pmn_desc,t.com_base "
                + " from payment_schedule t "
                + " where t.id_mdtask = ? "
                + " order by t.from_dt, t.to_dt ");
            st.setObject(1, task.getId_task());
            r = st.executeQuery();
            while (r.next()) {
                try {
                    task.getPaymentScheduleList().add(
                            new PaymentSchedule(getLong(r,"id_schedule"),getDouble(r,"amount"),getDouble(r,"fondrate"),
                                                r.getDate("from_dt"), r.getDate("to_dt"),r.getString("currency"),r.getLong("period"),
                                                r.getString("MANUAL_FONDRATE").equals("y"),r.getLong("TRANCE_ID"),r.getString("pmn_desc"),
                                                r.getString("com_base"))
                    );
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                    e.printStackTrace();
                }
            }
            r.close();
            st.close();
            // график погашения процентов
            if(!task.isLimit() || task.isSubLimit()) {
                st = conn.prepareStatement(
                        "select ip.id_int_pay, ip.id_mdtask, ip.first_pay_dt, ip.final_pay_dt, ip.num_day, ip.is_final_pay, ip.description, PAY_INT, cmnt, ip.FIRST_DATE_PAY_NOTE "
                        + " from INTEREST_PAY ip"
                        + " where ID_MDTASK = ?"
                        + " order by ip.id_int_pay");
                st.setObject(1, task.getId_task());//PAY_INT
                r = st.executeQuery();
                if (r.next()) {
                    try {
                        boolean isFinalPay = r.getString("is_final_pay").equalsIgnoreCase("y");
                        task.setInterestPay(
                                new InterestPay(getLong(r,"id_int_pay"), r.getDate("first_pay_dt"), r.getDate("final_pay_dt"),
                                        getLong(r,"num_day"), isFinalPay, r.getString("DESCRIPTION"), r.getString("PAY_INT"),r.getString("cmnt"), r.getString("FIRST_DATE_PAY_NOTE"))                            
                        );
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        e.printStackTrace();
                    }
                } else { // insert null interest pay.
                    task.setInterestPay(new InterestPay(null));
                }
                r.close();
                st.close();
            }

            // штрафные санкции
            st = conn.prepareStatement("SELECT ID_FINE, DESCRIPTION, CURRENCY, FINE_VALUE, PUNITIVE_TYPE,FINE_VALUE_TEXT,ID_PUNITIVE_MEASURE,"
            		+ "period,periodtype,productrate  "
                    + "FROM FINE where ID_MDTASK=? and id_person is null and org is null"
                    + " order by ID_FINE");
            st.setObject(1, task.getId_task());
            r = st.executeQuery();
            while (r.next()) {
                try {
                    task.getFineList().add(
                            new Fine(getLong(r,"ID_FINE"), r.getString("DESCRIPTION"),
                                    new Currency(r.getString("CURRENCY")), getDouble(r,"FINE_VALUE"), r.getString("PUNITIVE_TYPE"),
                                    null,null,r.getString("FINE_VALUE_TEXT"),r.getLong("ID_PUNITIVE_MEASURE"),
                                    r.getLong("period"),r.getString("periodtype"),r.getString("productrate").equalsIgnoreCase("y")
                            	));
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                    e.printStackTrace();
                }
            }
            r.close();
            st.close();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /**
     * Читаем из базы данных. Секция 'Стоп-факторы'
     */
    public static void readStopFactors(Connection conn, Task task) {
        try {
            // стоп-факторы клиентские
            PreparedStatement st = conn.prepareStatement(
                 "SELECT ID_STOPFACTOR, FLAG, sf.description, sf.author "
               + "FROM R_MDTASK_STOPFACTOR r "
               + "inner join  stopfactor sf on r.id_stopfactor= sf.code and sf.author = 'c' "
               + "where ID_MDTASK=?");
            st.setObject(1, task.getId_task());
            ResultSet r = st.executeQuery();
            while (r.next()) {
                try {
                    CompendiumSpoActionProcessor compenduim =
                       (CompendiumSpoActionProcessor) ActionProcessorFactory.getActionProcessor("CompendiumSpo");
                    StopFactorType type = new StopFactorType("c");
                    StopFactorType foundType = compenduim.findStopFactorType(type);
                    StopFactor stop = new StopFactor(r.getString("ID_STOPFACTOR"),
                            r.getString("description"), foundType);
                    task.getTaskClientStopFactorList().add(
                            new TaskStopFactor(r.getBoolean("FLAG"), stop));
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                    e.printStackTrace();
                }
            }
            r.close();
            st.close();
            // стоп-факторы службы безоспасности
            st = conn.prepareStatement(
                    "SELECT ID_STOPFACTOR, FLAG, sf.description, sf.author "
                    + "FROM R_MDTASK_STOPFACTOR r "
                    + "inner join  stopfactor sf on r.id_stopfactor= sf.code and sf.author = 's' "
                    + "where ID_MDTASK=?");
            st.setObject(1, task.getId_task());
            r = st.executeQuery();
            while (r.next()) {
                try {
                    CompendiumSpoActionProcessor compenduim =
                        (CompendiumSpoActionProcessor) ActionProcessorFactory.getActionProcessor("CompendiumSpo");
                    StopFactorType type = new StopFactorType("s");
                    StopFactorType foundType = compenduim.findStopFactorType(type);
                    StopFactor stop = new StopFactor(r.getString("ID_STOPFACTOR"),
                            r.getString("description"), foundType);
                    task.getTaskSecurityStopFactorList().add(
                            new TaskStopFactor(r.getBoolean("FLAG"), stop));
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                    e.printStackTrace();
                }
            }
            r.close();
            st.close();
            // стоп-факторы 3
            st = conn.prepareStatement(
                 "SELECT ID_STOPFACTOR, FLAG, sf.description, sf.author "
               + "FROM R_MDTASK_STOPFACTOR r "
               + "inner join  stopfactor sf on r.id_stopfactor= sf.code and sf.author = 'd' "
               + "where ID_MDTASK=?");
            st.setObject(1, task.getId_task());
            r = st.executeQuery();
            while (r.next()) {
                try {
                    CompendiumSpoActionProcessor compenduim =
                       (CompendiumSpoActionProcessor) ActionProcessorFactory.getActionProcessor("CompendiumSpo");
                    StopFactorType type = new StopFactorType("d");
                    StopFactorType foundType = compenduim.findStopFactorType(type);
                    StopFactor stop = new StopFactor(r.getString("ID_STOPFACTOR"),
                            r.getString("description"), foundType);
                    task.getTaskStopFactor3List().add(
                            new TaskStopFactor(r.getBoolean("FLAG"), stop));
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                    e.printStackTrace();
                }
            }
            r.close();
            st.close();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /**
     * Читаем из базы данных секцию 'Ответственные подразделения'
     */
    public static void readDepartments(Connection conn, Task task) {
        try {
            // другие департаменты
            PreparedStatement st = conn.prepareStatement(
                "select id_start_department, d.id_department, d.code, d.shortname from  "
              + " start_department sd inner join " +
                " departments d on sd.id_department=d.id_department where ID_MDTASK=?");
            st.setObject(1, task.getId_task());
            ResultSet r = st.executeQuery();
            while (r.next()) {
                try {
                    task.getHeader().getOtherDepartments().
                    add(new TaskDepartment(r.getLong("id_start_department"),
                            new Department(r.getInt("id_department"), r.getString("code"), r.getString("shortname"), "")));
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                    e.printStackTrace();
                }
            }
            r.close();
            st.close();

            // место проведения сделки
            st = conn.prepareStatement("select id_place, d.id_department, d.code, d.shortname from  "
                    + " place sd inner join " +
                    " departments d on sd.id_department=d.id_department where ID_MDTASK=?");
            st.setObject(1, task.getId_task());
            r = st.executeQuery();
            while (r.next()) {
                try {
                    task.getHeader().getPlaces().
                    add(new Department(r.getInt("id_department"), r.getString("code"), r.getString("shortname"), ""));
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                    e.printStackTrace();
                }
            }
            r.close();
            st.close();

            //менеджеры
            st = conn.prepareStatement("select m.id_manager, m.ID_user,m.id_start_department,"+
                    "u.surname||' '||u.name||' '||u.patronymic as fio "+
                    "from manager m left outer join users u on u.id_user=m.id_user "+
                    "where m.id_mdtask=? and id_start_department is null");
            st.setObject(1, task.getId_task());
            r = st.executeQuery();
            while (r.next()) {
                try {
                    task.getHeader().getManagers().add(new TaskManager(
                            r.getString("fio"),
                            new User(r.getInt("ID_user")),
                            r.getLong("id_manager")));
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                    e.printStackTrace();
                }
            }
            r.close();
            st.close();

            //менеджеры из других департаментов
            for(TaskDepartment td:task.getHeader().getOtherDepartments()){
                st = conn.prepareStatement("select m.id_manager, m.ID_user,m.id_start_department,"+
                        "u.surname||' '||u.name||' '||u.patronymic as fio "+
                        "from manager m left outer join users u on u.id_user=m.id_user "+
                        "where m.id_mdtask=? and id_start_department=?");
                st.setObject(1, task.getId_task());
                st.setObject(2, td.getId());
                r = st.executeQuery();
                while (r.next()) {
                    try {
                        td.getManagers().add(new TaskManager(
                                r.getString("fio"),
                                new User(r.getInt("ID_user")),
                                r.getLong("id_manager")));
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        e.printStackTrace();
                    }
                }
                r.close();
                st.close();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /**
     * Читаем штрафные санкции для поручителей.
     * @param conn
     * @param task
     */
    private static void readFinesForWarranty(Connection conn, Task task) {
    	PreparedStatement st; ResultSet r;
    	for (Warranty w : task.getSupply().getWarranty()) {
            try {
                Long personid = w.getPerson().getId();
                if(personid!=null && personid.longValue()==0)
                    personid=null;
                String orgid = w.getOrg().getId();
	    		st = conn.prepareStatement("SELECT ID_FINE, DESCRIPTION, CURRENCY, FINE_VALUE, PUNITIVE_TYPE, id_person, org,FINE_VALUE_TEXT,ID_PUNITIVE_MEASURE,"
	    				+ "period,periodtype,productrate  "
	                    + "FROM FINE where ID_MDTASK=? "
	                    + (personid != null ? " and (id_person=?) " : "")
	                    + (orgid != null ? " and (org=?) " : "")
	                    + " order by ID_FINE");
	            st.setObject(1, task.getId_task());
	            int param = 2;
	            if (personid != null) st.setObject(param++, w.getPerson().getId());
	            if (orgid != null) st.setObject(param++, w.getOrg().getId());

	            r = st.executeQuery();
	            while (r.next()) {
	                try {
	                    w.getFineList().add(
	                            new Fine(getLong(r,"ID_FINE"), r.getString("DESCRIPTION"), new Currency(r.getString("CURRENCY")),
	                                    getDouble(r,"FINE_VALUE"), r.getString("PUNITIVE_TYPE"), r.getLong("id_person"),
	                                    r.getString("org"),r.getString("FINE_VALUE_TEXT"),r.getLong("ID_PUNITIVE_MEASURE"),
	                                    r.getLong("period"),r.getString("periodtype"),r.getString("productrate").equalsIgnoreCase("y")
	                            ));
	                } catch (Exception e) {
	                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
	                    e.printStackTrace();
	                }
	            }
	            r.close();
	            st.close();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
                e.printStackTrace();
            }
            w.generatefineListAsString();
    	}
    }
    /**
     * if the value is SQL NULL, the ResultSet.getLong returned is 0. We need null.
     */
    private static Long resultSetGetLong(String columnLabel, ResultSet rSet) throws SQLException{
    	Object o = rSet.getObject(columnLabel);
    	if(o==null)
    		return null;
    	return rSet.getLong(columnLabel);
    }
    /**
     * получить залоги.
     */
    @SuppressWarnings("deprecation")
    public static void readSupply(Connection conn, Task task) {
        try {
            //поручительства
            PreparedStatement st = conn.prepareStatement(
                    "select c.org, c.id_person, c.main, c.currency, c.fullsum, c.fromdate, c.todate, c.supplyvalue, " +
                    "c.wsum, c.depositor_fin_status, c.liquidity_level, c.id_ob_kind, c.description, " +
                    "c.responsibility, c.fine, c.wadd, c.kind, c.PERIODDIMENSION,c.PERIOD, " +
                    "k.name as kname, df.status as DEPOSITOR_FIN_STATUS_VALUE,ll.name as liq_name  from warranty c"
                    + " left outer join CR_OB_TYPE k on c.id_ob_kind = k.id "
                    + " left outer join liquidity_level ll on c.LIQUIDITY_LEVEL = ll.id "
                    + " left outer join depositor_fin_status df on c.DEPOSITOR_FIN_STATUS = df.id "
                    + " where ID_MDTASK = ?");
            st.setObject(1, task.getId_task());
            ResultSet r = st.executeQuery();
            while (r.next()) {
                Warranty w = new Warranty();
                w.setOrg(new Organization(r.getString("org")));
                w.setSupplyvalue(r.getDouble("supplyvalue"));
                w.setTodate(r.getDate("todate"));
                w.setFromdate(r.getDate("fromdate"));
                w.setPeriod(resultSetGetLong("period",r));
                w.setPeriodDimension(r.getString("PERIODDIMENSION"));
                w.setPerson(new Person(r.getObject("id_person") == null ? null : r.getLong("id_person")));
                w.setMain(r.getString("main").equalsIgnoreCase("Y"));
                w.setCurrency(new Currency(r.getString("CURRENCY")));
                w.setFullSum(r.getString("fullsum").equalsIgnoreCase("Y"));
                w.setSum(r.getObject("WSUM") == null ? null :r.getDouble("WSUM"));
                // на полную сумму обязательств. Дублируем значение.
                if (w.isFullSum()) {
                    if (task.getMain().getSum() != null) w.setSum(task.getMain().getSum().doubleValue());
                    else w.setSum(null);
                }
                w.setResponsibility(r.getString("responsibility"));
                w.setFine(r.getString("fine"));
                w.setAdd(r.getString("wadd"));
                w.setKind(r.getString("kind"));
                if ("0".equals(w.getKind())) w.setKind("");
                w.setDepositorFinStatus(
                        new DepositorFinStatus(r.getLong("DEPOSITOR_FIN_STATUS"),r.getString("DEPOSITOR_FIN_STATUS_VALUE")));
                w.setLiquidityLevel(
                        new LiquidityLevel(r.getLong("LIQUIDITY_LEVEL"),r.getString("liq_name")));
                w.setDescription(r.getString("description"));
                w.setOb(
                    new SupplyType(r.getLong("id_ob_kind"), r.getString("kname")));
                task.getSupply().getWarranty().add(w);
            }
            r.close();
            st.close();

            readFinesForWarranty(conn, task);

            //гарантии
            st = conn.prepareStatement("select c.org, c.id_person, c.main, c.currency, c.fromdate, c.todate, c.supplyvalue, " +
                    "c.depositor_fin_status, c.liquidity_level, ll.name as liq_name, c.description, c.id_ob_kind, c.SUM,  "
                    + "c.id_ob_kind, k.name as kname, df.status as DEPOSITOR_FIN_STATUS_VALUE,c.MAXDATE,c.FULLSUM, c.PERIODDIMENSION,c.PERIOD "
                    + " from garant c"
                    + " left outer join CR_OB_TYPE k on c.id_ob_kind = k.id "
                    + " left outer join liquidity_level ll on c.LIQUIDITY_LEVEL = ll.id "
                    + " left outer join depositor_fin_status df on c.DEPOSITOR_FIN_STATUS = df.id "
                    + " where ID_MDTASK = ?");
            st.setObject(1, task.getId_task());
            r = st.executeQuery();
            while (r.next()) {
                Guarantee guarantee = new Guarantee();
                guarantee.setOrg(new Organization(r.getString("org")));
                guarantee.setPerson(new Person(r.getObject("id_person") == null ? null : r.getLong("id_person")));
                guarantee.setMain(r.getString("main").equalsIgnoreCase("Y"));
                guarantee.setCurrency(new Currency(r.getString("CURRENCY")));
                guarantee.setSupplyvalue(r.getDouble("supplyvalue"));
                guarantee.setTodate(r.getDate("todate"));
                guarantee.setFromdate(r.getDate("fromdate"));
                guarantee.setDepositorFinStatus(
                        new DepositorFinStatus(r.getLong("DEPOSITOR_FIN_STATUS"),r.getString("DEPOSITOR_FIN_STATUS_VALUE")));
                guarantee.setLiquidityLevel(
                        new LiquidityLevel(r.getLong("LIQUIDITY_LEVEL"),r.getString("liq_name")));
                guarantee.setDescription(r.getString("description"));
                guarantee.setOb(
                    new SupplyType(r.getLong("id_ob_kind"), r.getString("kname")));
                guarantee.setSum(r.getBigDecimal("SUM"));
                guarantee.setDate(r.getDate("MAXDATE"));
                guarantee.setPeriod(resultSetGetLong("period",r));
                guarantee.setPeriodDimension(r.getString("PERIODDIMENSION"));
                String fullsumm = r.getString("FULLSUM");
                guarantee.setFullSum(fullsumm!=null&&fullsumm.equalsIgnoreCase("y"));
                task.getSupply().getGuarantee().add(guarantee);
            }
            r.close();
            st.close();

            //залоги
            st = conn.prepareStatement("select is_main,c.IS_POSLED, type, zalog_description, opp_description, id_crmorg, id_ob_kind,  " +
                    "depositor_fin_status, liquidity_level, zalog_market, zalog_terminate, zalog, c.PERIODDIMENSION,c.PERIOD,  " +
                    "discount, issuer, order_description, zalog_object, c.fromdate, c.todate, c.supplyvalue,c.ID_PERSON, " +
                    "k.name as kname, df.status as DEPOSITOR_FIN_STATUS_VALUE,ll.name as liq_name, e.TEXT,cond,weight,maxpart  " +
                    "from deposit c " +
                    "left outer join CR_OB_TYPE k on c.id_ob_kind = k.id " +
                    "left outer join liquidity_level ll on c.LIQUIDITY_LEVEL = ll.id " +
                    "left outer join depositor_fin_status df on c.DEPOSITOR_FIN_STATUS = df.id " +
                    "left outer join V_CRM_ENSURING e on e.ITEMN_ID=c.zalog_object "+
                    "where id_mdtask=?");
                    st.setObject(1, task.getId_task());
            r = st.executeQuery();
            while (r.next()) {
                Deposit d = new Deposit();
                d.setOrg(new Organization(r.getString("id_crmorg")));
                d.setPerson(new Person(resultSetGetLong("ID_PERSON", r)));
                d.setMain(r.getString("is_main").equalsIgnoreCase("Y"));
                d.setPosled(r.getString("IS_POSLED").equalsIgnoreCase("Y"));
                d.setDepositorFinStatus(
                        new DepositorFinStatus(r.getLong("depositor_fin_status"),r.getString("DEPOSITOR_FIN_STATUS_VALUE")));
                d.setLiquidityLevel(
                        new LiquidityLevel(r.getLong("liquidity_level"),r.getString("liq_name")));
                d.setOppDescription(r.getString("opp_description"));
                d.setOrderDescription(r.getString("order_description"));
                d.setZalogDescription(r.getString("zalog_description"));
                d.setSupplyvalue(r.getDouble("supplyvalue"));
                d.setTodate(r.getDate("todate"));
                d.setFromdate(r.getDate("fromdate"));
                d.setPeriod(resultSetGetLong("period",r));
                d.setPeriodDimension(r.getString("PERIODDIMENSION"));
                d.setType(r.getString("type"));
                d.setOb(
                    new SupplyType(r.getLong("id_ob_kind"), r.getString("kname")));
                d.setZalogMarket(r.getBigDecimal("zalog_market"));
                d.setZalogTerminate(r.getBigDecimal("zalog_terminate"));
                d.setZalog(r.getBigDecimal("zalog"));
                d.setDiscount(r.getBigDecimal("discount"));
                d.setIssuer(new Organization(r.getString("issuer")));
                d.setZalogObject(new Ensuring(r.getString("zalog_object"),r.getString("TEXT"),""));
                d.setCond(r.getString("cond"));
                d.setWeight(r.getBigDecimal("weight"));
                d.setMaxpart(r.getDouble("maxpart"));
                if(r.getBigDecimal("maxpart")==null)
                	d.setMaxpart(null);
                task.getSupply().getDeposit().add(d);
            }
            r.close();
            st.close();


            //вексель
            st = conn.prepareStatement("select holder, c.val, c.currency, c.perc, c.place, c.maxdate " +
                    				   " from promissory_note c " +
                    				   " where c.id_mdtask=?");
            st.setObject(1, task.getId_task());
            r = st.executeQuery();
            while (r.next()) {
            	PromissoryNote d = new PromissoryNote(
            		r.getString("holder"),
            		r.getDouble("val"),
            		r.getString("currency"),
            		r.getDouble("perc"),
            		r.getString("place"),
            		r.getDate("maxdate")
            	);
                task.getPromissoryNoteList().add(d);
            }
            r.close();
            st.close();




            st=conn.prepareStatement("select key, val from deposit_keyvalue where id_mdtask=?");
            st.setObject(1, task.getId_task());
            r = st.executeQuery();
            while (r.next()) {
                task.getSupply().getDepositKeyValue().put(r.getString("key"), r.getString("val"));
            }
            r.close();
            st.close();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /**
     * Читаем из базы данных Комментарии
     */
    public static void readComments(Connection conn, Task task) {
        try {
            PreparedStatement st = conn.prepareStatement(
                "SELECT WHO, ID_TASKCOMMENT, s.description_stage, COMMENT_BODY, COMMENT_BODY_HTML, tc.ID_STAGE, u.SURNAME, u.NAME, u.PATRONYMIC, WHEN FROM  TASKCOMMENT tc "
                + "left outer join  users u ON tc.WHO = u.ID_USER  "
                +" left outer join  stages s on s.id_stage=tc.id_stage "
                +" where ID_MDTASK=? and COMMENT_BODY is not null order by WHEN");
            st.setObject(1, task.getId_task());
            ResultSet r = st.executeQuery();
            while (r.next()) {
                try {
                    Operator o = new Operator(r.getInt("WHO"), r.getString("SURNAME"));
                    o.setFieldFA(r.getString("SURNAME"));
                    o.setFieldIM(r.getString("NAME"));
                    o.setFieldOT(r.getString("PATRONYMIC"));
                    Comment c =  new Comment(r.getLong("ID_TASKCOMMENT"),
                            r.getString("COMMENT_BODY"),
                            r.getString("COMMENT_BODY_HTML"),
                            o,
                            r.getInt("ID_STAGE"),
                            r.getTimestamp("WHEN"));
                    c.setStagename(r.getString("description_stage"));
                    task.getComment().add(c);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                    e.printStackTrace();
                }
            }
            r.close();
            st.close();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /**
     * Читаем из базы данных специальные и другие УСЛОВИЯ
     */
    public static void readSpecialOtherConditions(Connection conn, Task task) {
        try {
            // other condition
            PreparedStatement st = conn.prepareStatement(
                    "select ID_DEAL_CONDITION, ID_CONDITION_TYPE, NAME, ID_CONDITION,SUPPLY_CODE "
                    + " from CPS_DEAL_CONDITION where ID_SYSTEM_MODULE=1 and  ID_CREDIT_DEAL=? "
                    + " order by ID_DEAL_CONDITION");
            st.setObject(1, task.getId_task());
            ResultSet r = st.executeQuery();
            while (r.next()) {
                try {
                    task.getOtherCondition().add(
                            new OtherCondition(r.getLong("ID_CONDITION_TYPE"),
                                               r.getLong("ID_CONDITION"),
                                               r.getString("NAME"),
                                               r.getLong("ID_DEAL_CONDITION"),
                                               r.getString("SUPPLY_CODE")));
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                    e.printStackTrace();
                }
            }
            r.close();
            st.close();

            // specialcondition
            st = conn.prepareStatement("select description, type from specialcondition where ID_MDTASK=?");
            st.setObject(1, task.getId_task());
            r = st.executeQuery();
            while (r.next()) {
                try {
                    task.getSpecialCondition().add(
                            new SpecialCondition(r.getString("type"), r.getString("description")));
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                    e.printStackTrace();
                }
            }
            r.close();
            st.close();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
        }
    }


    /**
     * Читаем из базы данных все остальное (мусорка)
     */
    public static void readAllOthers(Connection conn, Task task) {
        try {
         // EarlyPayment
            PreparedStatement st = conn.prepareStatement(
                "select c.ID_EARLY_PAYMENT, c.permission, d.name_early_payment_condition, c.commission, c.condition, c.PERIOD_TYPE, c.DAYS_BEFORE_NOTIFY_BANK  " 
                + " from EARLY_PAYMENT c"
                + " left outer join EARLY_PAYMENT_CONDITION d on c.permission = d.id_early_payment_condition"
                + " where ID_MDTASK=?"
            );
            st.setObject(1, task.getId_task());
            ResultSet r = st.executeQuery();
            while (r.next()) {
                try {
                    task.getEarlyPaymentList().add(
                            new EarlyPayment(r.getString("permission"), r.getString("name_early_payment_condition"), 
                                    r.getString("commission"), 
                                    (r.getString("condition")==null)?"":r.getString("condition"), 
                                    r.getString("PERIOD_TYPE"), 
                                    (r.getObject("DAYS_BEFORE_NOTIFY_BANK") == null) ? null : r.getLong("DAYS_BEFORE_NOTIFY_BANK") ));
                    
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                    e.printStackTrace();
                }
            }
            r.close();
            st.close();

            // Справка согласования с экспертными подразделениями
            st = conn.prepareStatement("SELECT DEPARTMENT, REMARK, COM FROM  DEP_RESOLUTION "
                    + "  where ID_MDTASK=? order by DEPARTMENT");
            st.setObject(1, task.getId_task());
            r = st.executeQuery();
            while (r.next()) {
                try {
                    task.getDepartmentAgreements().add(
                            new DepartmentAgreement(r.getString("DEPARTMENT"),
                                    r.getString("REMARK"),
                                    r.getString("COM")));
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                    e.printStackTrace();
                }
            }
            r.close();
            st.close();

            // количество прикрепленных документов
            st = conn.prepareStatement("select count(*) from  appfiles "
                    + "  where id_owner=?");
            String owner = task.isSubLimit()?
                    "sublimit"+task.getId_task().toString():
                        task.getId_pup_process().toString();
            st.setObject(1, owner);
            r = st.executeQuery();
            if (r.next()) {
                task.setDocuments_count(r.getLong(1));
            }
            r.close();
            st.close();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /**
     * Return BigDecimal value of the ResultSet column (standard returns 0.0, if in the tabel is NULL value)
     * @param rs RsultSet
     * @param name name of the column in the ResultSet
     * @return
     * @throws SQLException
     */
    private static BigDecimal getBigDecimal (ResultSet rs, String name) throws SQLException {
        if ((rs == null) || (name == null)) return null;
        if (rs.getString(name) == null) return null;
        if (rs.getString(name).equals("")) return null;
        return rs.getBigDecimal(name);
    }

    /**
     * Return Double value of the ResultSet column (standard returns 0.0, if in the tabel is NULL value)
     * @param rs RsultSet
     * @param name name of the column in the ResultSet
     * @return
     * @throws SQLException
     */
    private static Double getDouble (ResultSet rs, String name) throws SQLException {
        if ((rs == null) || (name == null)) return null;
        if (rs.getString(name) == null) return null;
        if (rs.getString(name).equals("")) return null;
        return rs.getDouble(name);
    }

    /**
     * Return Long value of the ResultSet column (standard returns 0.0, if in the tabel is NULL value)
     * @param rs RsultSet
     * @param name name of the column in the ResultSet
     * @return
     * @throws SQLException
     */
    private static Long getLong (ResultSet rs, String name) throws SQLException {
        if ((rs == null) || (name == null)) return null;
        if (rs.getString(name) == null) return null;
        if (rs.getString(name).equals("")) return null;
        return rs.getLong(name);
    }

    /**
     * Return Integer value of the ResultSet column (standard returns 0.0, if in the tabel is NULL value)
     * @param rs RsultSet
     * @param name name of the column in the ResultSet
     * @return
     * @throws SQLException
     */
    private static Integer getInt(ResultSet rs, String name) throws SQLException {
        if ((rs == null) || (name == null)) return null;
        if (rs.getString(name) == null) return null;
        if (rs.getString(name).equals("")) return null;
        return rs.getInt(name);
    }

    /**
     * Finds a list of currency for a given mdTask
     * @return
     */
    private static List<Currency> findCurrencyList(Connection conn, Long mdTaskId) {
        List<Currency> currencyList = new ArrayList<Currency>();
        try {
         // загружаем все сохраненные валюты
            PreparedStatement st = conn.prepareStatement("SELECT id_currency FROM r_mdtask_currency where ID_MDTASK=?");
            st.setObject(1, mdTaskId);
            ResultSet r = st.executeQuery();
            while (r.next()) {
                try {
                    currencyList.add(new Currency(r.getString("id_currency")));
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                    e.printStackTrace();
                }
            }
            r.close();
            st.close();
        }  catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
        }
        return currencyList;
    }
}
