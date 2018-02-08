package com.vtb.mapping.jdbc;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import ru.masterdm.compendium.domain.crm.Organization;
import ru.masterdm.compendium.domain.spo.ContractorType;
import ru.masterdm.compendium.domain.spo.StandardPriceCondition;
import ru.md.domain.Withdraw;

import com.vtb.domain.Commission;
import com.vtb.domain.CommissionDeal;
import com.vtb.domain.Deposit;
import com.vtb.domain.EarlyPayment;
import com.vtb.domain.Fine;
import com.vtb.domain.Guarantee;
import com.vtb.domain.InterestPay;
import com.vtb.domain.Main;
import com.vtb.domain.OtherCondition;
import com.vtb.domain.PaymentSchedule;
import com.vtb.domain.PrincipalPay;
import com.vtb.domain.SpecialCondition;
import com.vtb.domain.Task;
import com.vtb.domain.TaskContractor;
import com.vtb.domain.TaskDepartment;
import com.vtb.domain.TaskManager;
import com.vtb.domain.TaskProcent;
import com.vtb.domain.TaskProduct;
import com.vtb.domain.TaskStopFactor;
import com.vtb.domain.Trance;
import com.vtb.domain.Warranty;
import com.vtb.exception.MappingException;

/**
* Helper class for performing UPDATE operations of Task object.
* @author Kuznetsov Michael
**/

// TODO : for (int i = 0; i > list.size(); i ++) change to 
// for (Object : List) 
public class TaskMapperSaveHelper {
	
    private static final Logger LOGGER = Logger.getLogger(TaskMapperSaveHelper.class.getName());

    /**
     * Сохраняем в базе данных: Контрагенты
     */
    public static void saveContragents(Connection conn, Task task) throws MappingException {
        try {
            String idrArray = "0";
            for(TaskContractor tc : task.getContractors()) {
            	if (StringUtils.isEmpty(tc.getOrg().getAccountid()))
            		continue;
            	if(tc.getId()!=null)
            		idrArray += ", "+tc.getId();
            }
            PreparedStatement stmn = conn.prepareStatement("DELETE FROM "  + " R_ORG_MDTASK WHERE ID_MDTASK=? and ID_R not in ("+idrArray+")");
            stmn.setObject(1, task.getId_task());
            stmn.execute();
            stmn.close();
            for (int i = 0; i < task.getContractors().size(); i++) {
            	TaskContractor tc = task.getContractors().get(i);
            	if (StringUtils.isEmpty(tc.getOrg().getAccountid()))
            		continue;
            	
            	if(tc.getId()==null){//новая запись. Добавляем
            		stmn = conn.prepareStatement("select R_ORG_MDTASK_SEQ.nextval from dual");
            		ResultSet rs = stmn.executeQuery();
            		Long R_ORG_MDTASK_SEQ=null;
            		while (rs.next()) { R_ORG_MDTASK_SEQ=rs.getLong("nextval"); }
            		tc.setId(R_ORG_MDTASK_SEQ);
            		try {
            			stmn = conn.prepareStatement("INSERT INTO " 
            					+ " R_ORG_MDTASK (ID_R, ID_CRMORG, ID_MDTASK,ratingPKR,ID_CLIENT_ROW,order_disp) " + "VALUES (?,?,?,?,?,?)");
            			stmn.setObject(2, task.getContractors().get(i).getOrg().getAccountid());
            			stmn.setObject(5, task.getContractors().get(i).getOrg().getAccountid());
            			stmn.setObject(1, R_ORG_MDTASK_SEQ);
            			stmn.setObject(3, task.getId_task());
            			stmn.setObject(4, task.getContractors().get(i).getRatingPKR());
            			stmn.setObject(6, i);
            			stmn.execute();
            			stmn.close();
            		} catch (Exception e) {
            			LOGGER.severe("Cant' insert into R_ORG_MDTASK table" + e.getMessage());
            			e.printStackTrace();
            			throw new MappingException(e.getMessage());
            		}
            	} else {//существующая запись. Обновляем
            		stmn = conn.prepareStatement("UPDATE R_ORG_MDTASK set ratingPKR=?,ID_CLIENT_ROW=ID_CRMORG,order_disp=? where ID_R=?");
            		stmn.setObject(3, tc.getId());
            		stmn.setObject(2, i);
            		stmn.setObject(1, task.getContractors().get(i).getRatingPKR());
            		stmn.execute();
            		stmn.close();
            	}
            	//delete r_contractor_type_mdtask
            	stmn = conn.prepareStatement("DELETE FROM r_contractor_type_mdtask WHERE ID_R=?");
            	stmn.setObject(1, tc.getId());
            	stmn.execute();
            	stmn.close();
            	
                for(ContractorType ct : task.getContractors().get(i).getOrgType()){
                    try {
                        stmn = conn.prepareStatement("INSERT INTO " 
                                + " r_contractor_type_mdtask (ID_R, id_contractor_type) " + "VALUES (?,?)");
                        stmn.setObject(2, ct.getId());
                        stmn.setObject(1, tc.getId());
                        stmn.execute();
                        stmn.close();
                    } catch (Exception e) {
                        LOGGER.severe("Cant' insert into r_contractor_type_mdtask table" + e.getMessage());
                        e.printStackTrace();
                        //throw new MappingException(e.getMessage());
                        //для нас сохранение типа контрагента не является критическим
                        //если не получилось по какам-то причинам, то просто пришем в лог
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            throw new MappingException(e.getMessage());
        }
    }

	public static void saveCurrencyList(Connection conn, Task task) throws SQLException, MappingException {
		PreparedStatement stmn;
		// валюта лимита \ сублимита
		    stmn = conn.prepareStatement("DELETE FROM "  + " r_mdtask_currency WHERE ID_MDTASK=?");
		    stmn.setObject(1, task.getId_task());
		    stmn.execute();
		    stmn.close();
		    for (int i = 0; i < task.getCurrencyList().size(); i++) {
		        try {
		            stmn = conn.prepareStatement("INSERT INTO " 
		                    + " r_mdtask_currency (ID_MDTASK, id_currency, FLAG) " + "VALUES (?,?,?)");
		            stmn.setObject(1, task.getId_task());
		            stmn.setObject(2, task.getCurrencyList().get(i).getCurrency().getCode());
		            stmn.setObject(3, task.getCurrencyList().get(i).isFlag());
		            stmn.execute();
		            stmn.close();
		        } catch (Exception e) {
		            LOGGER.severe("Cant' insert into r_mdtask_currency table" + e.getMessage());
		            e.printStackTrace();
		            throw new MappingException(e.getMessage());
		        }
		    }
	}


    /**
     * Сохраняем виды сделок
     * @param conn
     * @param task
     * @throws SQLException
     * @throws MappingException
     */
	public static void saveProductTypes(Connection conn, Task task) throws SQLException, MappingException {
		PreparedStatement stmn;
		// виды сделок (для сделки поля validto, useperiod, descr не задаются (null))
		stmn = conn.prepareStatement("DELETE FROM "  + " R_MDTASK_OPP_TYPE WHERE ID_MDTASK=?");
		stmn.setObject(1, task.getId_task());
		stmn.execute();
		stmn.close();

		for (int i = 0; i < task.getHeader().getOpportunityTypes().size(); i++) {
		    try {
		        TaskProduct op = task.getHeader().getOpportunityTypes().get(i);
		        stmn = conn.prepareStatement("INSERT INTO " 
		                + " R_MDTASK_OPP_TYPE (ID_OPP_TYPE, FLAG, ID_MDTASK, validto, useperiod, descr,PERIODDIMENSION) "
		                + "VALUES (?,'Y',?,?,?,?,?)");
		        stmn.setObject(1, op.getId());
		        stmn.setObject(2, task.getId_task());
		        if (op.getUsedate() != null) stmn.setObject(3, op.getUsedate());
		        else stmn.setNull(3, java.sql.Types.DATE);                
		        if ((op.getPeriod() != null) && (op.getPeriod() > 0)) stmn.setObject(4, op.getPeriod());
		        else stmn.setNull(4, java.sql.Types.NUMERIC);
		        if (op.getDescription() != null) stmn.setObject(5, op.getDescription());
		        else stmn.setNull(5, java.sql.Types.VARCHAR);                
		        if (op.getPeriodDimension() != null) stmn.setObject(6, op.getPeriodDimension());
		        else stmn.setNull(6, java.sql.Types.VARCHAR);                
		        stmn.execute();
		        stmn.close();
		    } catch (Exception e) {
		        LOGGER.severe("Cant' insert into R_MDTASK_OPP_TYPE table" + e.getMessage());
		        e.printStackTrace();
		        throw new MappingException(e.getMessage());
		    }
		}
	}  

    /**
     * Сохраняем в базе данных: Секция 'Стоимостные условия'
     */
    public static void savePriceConditions(Connection conn, Task task) throws MappingException {
        try {
            // процентная ставка
            PreparedStatement stmn = conn.prepareStatement("DELETE FROM PROCENT WHERE ID_MDTASK=?");
            stmn.setObject(1, task.getId_task());
            stmn.execute();
            stmn.close();
            if (task.isLimit() || task.isSubLimit()) {
                // процентная ставка (Лимит)

                // удалим и список стандартных стоимостных условий
                stmn = conn.prepareStatement("DELETE FROM "  + " PROCENT_ST_PRICE_COND WHERE ID_MDTASK=?");
                stmn.setObject(1, task.getId_task());
                stmn.execute();
                stmn.close();            
                
                TaskProcent p = task.getTaskProcent();
                if ((p != null)) { // && (!p.checkForErrors())) {
                    try {
                        stmn = conn.prepareStatement("INSERT INTO " 
                            + " PROCENT (ID_PROCENT, DESCRIPTION, ID_BASE, PROCENT_VALUE, " 
                            + "          PROCENT, RISKPREMIUM, CURRENCY, ID_MDTASK, IS_FIXED, PAY_INT, capital_pay, ktr,PRICEINDCONDITION) "
                            + "VALUES (PROCENT_SEQ.nextval,?,?,?,?,?,?,?,?,?,?,?,?)");
                        stmn.setObject(1, p.getDescription());
                        if (p.getBase() != null) stmn.setObject(2, p.getBase().getId());
                        else stmn.setNull(2, java.sql.Types.NUMERIC);
                        if (p.getValue() != null) stmn.setObject(3, p.getValue());
                        else stmn.setNull(3, java.sql.Types.DOUBLE);
                        stmn.setObject(4, p.getProcent());
                        if (p.getRiskpremium() != null) stmn.setDouble(5, p.getRiskpremium());
                        else stmn.setNull(5, java.sql.Types.DOUBLE);
                        if(p.getCurrency() != null) stmn.setObject(6, p.getCurrency().getCode());
                        else stmn.setNull(6, java.sql.Types.NULL);
                        stmn.setObject(7, task.getId_task());
                        stmn.setObject(8, p.isRateTypeFixed()?"y":"n");
                        stmn.setString(9, p.getPay_int());
                        if ((p.getCapitalPay() != null) && (!p.getCapitalPay().trim().equals(""))) stmn.setObject(10, p.getCapitalPay());
                        else stmn.setNull(10, java.sql.Types.VARCHAR);
                        if ((p.getKTR() != null) && (!p.getKTR().trim().equals(""))) stmn.setObject(11, p.getKTR());
                        else stmn.setNull(11, java.sql.Types.VARCHAR);
                        if ((p.getPriceIndCondition() != null) && (!p.getPriceIndCondition().trim().equals(""))) stmn.setObject(12, p.getPriceIndCondition());
                        else stmn.setNull(12, java.sql.Types.VARCHAR);
                        stmn.execute();
                        stmn.close();
                    } catch (Exception e) {
                        LOGGER.severe("Cant' insert into PROCENT table" + e.getMessage());
                        e.printStackTrace();
                        throw new MappingException(e.getMessage());
                    }
                }
                // вставим измененный список стандартных условий 
                if (p != null) {
                    // положим список в HashMap, чтобы избежать дубликатов.
                    HashMap<Long, String> noDublicates = new HashMap<Long, String>(); 
                    for (StandardPriceCondition st : p.getStandardPriceConditionList()) noDublicates.put(st.getId(), st.getName());

                    Iterator<Map.Entry<Long,String>> it = noDublicates.entrySet().iterator();
                    while (it.hasNext()) {
                        try {
                            Map.Entry<Long,String> pairs = (Map.Entry<Long,String>)it.next();
                            if ((pairs.getKey().longValue() == -1L) || (pairs.getKey().longValue() == 0)) continue;
                            else {
                                stmn = conn.prepareStatement("INSERT INTO " 
                                    + " PROCENT_ST_PRICE_COND (ID_ST_PRICE_COND, ID_MDTASK, ID_PRICE_COND) "
                                    + "VALUES (PROCENT_ST_PRICE_COND_SEQ.nextval,?,?)");
                                stmn.setObject(1, task.getId_task());
                                stmn.setObject(2, pairs.getKey());
                                stmn.execute();
                                stmn.close();
                            }
                        } catch (Exception e) {
                            LOGGER.severe("Cant' insert into PROCENT_ST_PRICE_COND table" + e.getMessage());
                            e.printStackTrace();
                            throw new MappingException(e.getMessage());
                        }
                    }
                }
            }

            //  комиссии Лимит
            if (task.isLimit() || task.isSubLimit()) {
                stmn = conn.prepareStatement("DELETE FROM "  + " COMMISSION WHERE ID_MDTASK=?");
                stmn.setObject(1, task.getId_task());
                stmn.execute();
                stmn.close();

                for (Commission com : task.getCommissionList()) {
                    if (!com.checkForErrors()) {
                        try {   
                            stmn = conn.prepareStatement("INSERT INTO " 
                               + " COMMISSION (ID_COMMISSION, ID_MDTASK, crm_com_type, DESCRIPTION, crm_com_period,currency,commission_value) "
                               + "VALUES (COMMISSION_SEQ.nextval,?,?,?,?,?,?)");
                            stmn.setObject(1, task.getId_task());
                            if (com.getName() != null) stmn.setObject(2, com.getName().getId());
                            else stmn.setString(2,null);
                            stmn.setObject(3, com.getDescription());
                            if (com.getCommissionLimitPayPattern() != null) stmn.setObject(4, com.getCommissionLimitPayPattern().getId());
                            else stmn.setString(4,null);
                            stmn.setObject(5, com.getCurrencyCode());
                            stmn.setObject(6, com.getValue());
                            stmn.execute();
                            stmn.close();
                        } catch (Exception e) {
                            LOGGER.severe("Cant' insert into COMMISSION table" + e.getMessage());
                            e.printStackTrace();
                            throw new MappingException(e.getMessage());
                        }
                    }
                }
            } else {
                // комиссии Сделка
                stmn = conn.prepareStatement("DELETE FROM "  + " COMMISSION WHERE ID_MDTASK=?");
                stmn.setObject(1, task.getId_task());
                stmn.execute();
                stmn.close();

                for (CommissionDeal com : task.getCommissionDealList()) {
                    if (!com.checkForErrors()) {
                        try {
                            stmn = conn.prepareStatement("INSERT INTO " 
                                    + " COMMISSION (ID_COMMISSION, DESCRIPTION, CURRENCY, crm_com_type, COMMISSION_VALUE, " 
                                    +             " ID_MDTASK, crm_com_period, ID_CALC_BASE, crm_com_base, PAY_DESCR) "
                                    + "VALUES (COMMISSION_SEQ.nextval,?,?,?,?,?,?,?,?,?)");
                            stmn.setObject(1, com.getDescription());
                            stmn.setObject(2, com.getCurrency().getCode());
                            stmn.setObject(3, com.getName().getId());
                            stmn.setObject(4, com.getValue());
                            stmn.setObject(5, task.getId_task());
                            if (com.getProcent_order() != null) stmn.setObject(6, com.getProcent_order().getId());
                            else stmn.setString(6,null);
                            if (com.getCalcBase() != null) stmn.setObject(7, com.getCalcBase().getId());
                            else stmn.setNull(7, java.sql.Types.NUMERIC);
                            if (com.getComissionSize() != null) stmn.setObject(8, com.getComissionSize().getId());
                            else stmn.setString(8,null);
                            stmn.setObject(9, com.getPayDescription());
                            stmn.execute();
                            stmn.close();
                        } catch (Exception e) {
                            LOGGER.severe("Cant' insert into COMMISSION table" + e.getMessage());
                            e.printStackTrace();
                            throw new MappingException(e.getMessage());
                        }
                    }
                }
            }

            //  график погашения основного долга
            if (!task.isLimit() || task.isSubLimit()) {
                stmn = conn.prepareStatement("DELETE FROM "  + " PRINCIPAL_PAY WHERE ID_MDTASK=?");
                stmn.setObject(1, task.getId_task());
                stmn.execute();
                stmn.close();
                PrincipalPay pt = task.getPrincipalPay();
                if ((pt != null) && (!pt.checkForErrors())) {
                    try {
                        stmn = conn.prepareStatement("INSERT INTO " 
                            + " PRINCIPAL_PAY (id_prn_pay, id_mdtask, id_period_order, first_pay_dt, final_pay_dt, num_day, is_depended, description, is_first_pay, cmnt, currency) "
                            + "VALUES (INTEREST_PAY_SEQ.nextval,?,?,?,?,?,?,?,?,?,?)");
                        stmn.setObject(1, task.getId_task());
                        String period_order = null;
                        if (pt.getPeriodOrder() != null) period_order = pt.getPeriodOrder().getId();
                        stmn.setObject(2, period_order);
                        stmn.setObject(3, pt.getFirstPayDate());
                        stmn.setObject(4, pt.getFinalPayDate());
                        stmn.setObject(5, pt.getAmount());
                        stmn.setObject(6, pt.isDepended() ? "y" :"n");
                        stmn.setObject(7, pt.getDescription());
                        stmn.setObject(8, pt.isFirstPay() ? "y" :"n");
                        stmn.setObject(9, pt.getComment());
                        stmn.setObject(10, pt.getCurrency());
                        
                        stmn.execute();
                        stmn.close();
                    } catch (Exception e) {
                        LOGGER.severe("Cant' insert into PRINCIPAL_PAY table" + e.getMessage());
                        e.printStackTrace();
                        throw new MappingException(e.getMessage());
                    }
                }
            }

            // график платежей
            stmn = conn.prepareStatement("DELETE FROM "  + " payment_schedule WHERE ID_MDTASK=?");
            stmn.setObject(1, task.getId_task());
            stmn.execute();
            stmn.close();
            for (PaymentSchedule p : task.getPaymentScheduleList()) {
                if(!p.checkForErrors()) {
                    try {
                        stmn = conn.prepareStatement("INSERT INTO " 
                            + " payment_schedule (id_schedule, id_mdtask, amount, from_dt, to_dt,currency,fondrate,period,MANUAL_FONDRATE,TRANCE_ID,pmn_desc,com_base) "
                            + "VALUES (PAYMENT_SCHEDULE_SEQ.nextval,?,?,?,?,?,?,?,?,?,?,?)");
                        stmn.setObject(1, task.getId_task());
                        stmn.setObject(2, p.getAmount());
                        stmn.setObject(3, p.getFromDate());
                        stmn.setObject(4, p.getToDate());
                        stmn.setObject(5, p.getCurrency().getCode());
                        stmn.setObject(6, p.getFONDRATE());
                        stmn.setObject(7, p.getPeriod());
                        stmn.setObject(8, p.isManualFondrate()?"y":"n");
                        stmn.setObject(9, p.getTranceId());
                        stmn.setObject(10, p.getDesc());
                        stmn.setObject(11, p.getComBase());
                        stmn.execute();
                        stmn.close();
                    } catch (Exception e) {
                        LOGGER.severe("Cant' insert into payment_schedule table" + e.getMessage());
                        e.printStackTrace();
                        throw new MappingException(e.getMessage());
                    }
                }
            }
            
            //  график погашения процентов по кредиту
            if (!task.isLimit() || task.isSubLimit()) {
                stmn = conn.prepareStatement("DELETE FROM "  + " INTEREST_PAY WHERE ID_MDTASK=?");
                stmn.setObject(1, task.getId_task());
                stmn.execute();
                stmn.close();
                InterestPay pt = task.getInterestPay();
                if ((pt != null) && (!pt.checkForErrors())) {
                    try {
                        stmn = conn.prepareStatement("INSERT INTO " 
                                + " INTEREST_PAY (id_int_pay, id_mdtask, PAY_INT, first_pay_dt, final_pay_dt, num_day, is_final_pay, description, cmnt, FIRST_DATE_PAY_NOTE) "
                                + "VALUES (INTEREST_PAY_SEQ.nextval,?,?,?,?,?,?,?,?,?)");
                        stmn.setObject(1, task.getId_task());
                        stmn.setObject(2, pt.getPay_int());
                        stmn.setObject(3, pt.getFirstPayDate());
                        stmn.setObject(4, pt.getFinalPayDate());
                        stmn.setObject(5, pt.getNumDay());
                        stmn.setObject(6, pt.isFinalPay() ? "y" :"n");
                        stmn.setObject(7, pt.getDescription());
                        stmn.setObject(8, pt.getComment());
                        stmn.setObject(9, pt.getFirstPayDateComment());
                        stmn.execute();
                        stmn.close();
                    } catch (Exception e) {
                        LOGGER.severe("Cant' insert into INTEREST_PAY table" + e.getMessage());
                        //e.printStackTrace();
                        //throw new MappingException(e.getMessage());
                    }
                }
            }
            
            // штрафные санкции 
            stmn = conn.prepareStatement("DELETE FROM "  + " FINE WHERE ID_MDTASK=? and id_person is null and org is null");
            stmn.setObject(1, task.getId_task());
            stmn.execute();
            stmn.close();
            
                // штрафные санкции
                for (Fine f : task.getFineList()) {
                    if (!f.checkForErrors()) {
                        try {
                            stmn = conn.prepareStatement("INSERT INTO " 
                                + " FINE (ID_FINE, DESCRIPTION, CURRENCY, FINE_VALUE, PUNITIVE_TYPE, ID_MDTASK, id_person, org,FINE_VALUE_TEXT,ID_PUNITIVE_MEASURE,"
                                + "period,periodtype,productrate) "
                                + "VALUES (FINE_SEQ.nextval,?,?,?,?,?, null, null,?,?,?,?,?)");
                            if(f.getId_punitive_measure()!=null && f.getId_punitive_measure().equals(0L))
                            	f.setId_punitive_measure(null);//не понимаю откуда берется 0
                            stmn.setObject(5, task.getId_task());
                            stmn.setObject(1, f.getDescription());
                            stmn.setObject(2, f.getCurrency().getCode());
                            stmn.setObject(3, f.getValue());
                            stmn.setObject(4, f.getPunitiveMeasure());
                            stmn.setObject(6, f.getValueText());
                            stmn.setObject(7, f.getId_punitive_measure());
                            stmn.setObject(8, f.getPeriod());
                            stmn.setObject(9, f.getPeriontype());
                            stmn.setObject(10, f.isProductRateEnlarge()?"y":"n");
                            stmn.execute();
                            stmn.close();
                        } catch (Exception e) {
                            LOGGER.severe("Cant' insert into FINE table" + e.getMessage());
                            e.printStackTrace();
                            throw new MappingException(e.getMessage());
                        }
                    }
                }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            throw new MappingException(e.getMessage());
        }
    }  


    private static String getAccountId(Organization org){
        if (org == null) return null;
        if (org.getAccountid()==null) return null;
        if (org.getAccountid().startsWith("null")) return null;
        return org.getAccountid();
    }
    
    private static void saveFinesForWarranty(Connection conn, Task task) throws MappingException {
    	PreparedStatement stmn; 
    	try {
	        // штрафные санкции 
	        stmn = conn.prepareStatement("DELETE FROM "  + " FINE WHERE ID_MDTASK=? and ((id_person is not null) or (org is not null))");
	        stmn.setObject(1, task.getId_task());
	        stmn.execute();
	        stmn.close();
    	} catch (Exception e) {
            LOGGER.severe("Cant' delete from FINE table" + e.getMessage());
            e.printStackTrace();
            throw new MappingException(e.getMessage());
        }
	       
    	for (Warranty w : task.getSupply().getWarranty()) {
    		for (Fine f : w.getFineList()) {
                try {
                    stmn = conn.prepareStatement("INSERT INTO " 
                        + " FINE (ID_FINE, DESCRIPTION, CURRENCY, FINE_VALUE, PUNITIVE_TYPE, ID_MDTASK, id_person, org,FINE_VALUE_TEXT,ID_PUNITIVE_MEASURE,"
                        + "period,periodtype,productrate) "
                        + "VALUES (FINE_SEQ.nextval,?,?,?,?,?,?,?,?,?,?,?,?)");
                    stmn.setObject(1, f.getDescription());
                    stmn.setObject(2, f.getCurrency().getCode());
                    stmn.setObject(3, f.getValue());
                    stmn.setObject(4, f.getPunitiveMeasure());
                    stmn.setObject(5, task.getId_task());
                    stmn.setObject(6, f.getIdPerson());
                    stmn.setObject(7, f.getIdOrg());
                    stmn.setObject(8, f.getValueText());
                    stmn.setObject(9, f.isProductRateEnlarge()?"y":"n");
                    Long id_punitive_measure = f.getId_punitive_measure();
                    if(id_punitive_measure!=null && id_punitive_measure.equals(0L)) id_punitive_measure = null;
                    stmn.setObject(9, id_punitive_measure);
                    stmn.setObject(10, f.getPeriod());
                    stmn.setObject(11, f.getPeriontype());
                    stmn.setObject(12, f.isProductRateEnlarge()?"y":"n");
                    stmn.execute();
                    stmn.close();
                } catch (Exception e) {
                    LOGGER.severe("Cant' insert into FINE table" + e.getMessage());
                    LOGGER.severe("idOrg=" + f.getIdOrg());
                    LOGGER.severe("idPerson=" + f.getIdPerson());
                    e.printStackTrace();
                }
           }
    	}
    }
    
    /**
     * Сохраняем в базе данных: Секция   'Обеспечение' 
     */
    public static void saveSupply(Connection conn, Task task) throws MappingException {
        try {
            //поручители
            PreparedStatement stmn = conn.prepareStatement("DELETE FROM warranty WHERE ID_MDTASK=?");
            stmn.setObject(1, task.getId_task());
            stmn.execute();
            stmn.close();
            for(Warranty w : task.getSupply().getWarranty()){
                try {
                    // на полную сумму обязательств. Дублируем значение.
                    if (w.isFullSum()) { 
                        if (task.getMain().getSum() != null) w.setSum(task.getMain().getSum().doubleValue());
                        else w.setSum(null);
                    }
                    stmn = conn.prepareStatement("INSERT INTO warranty (id,org, id_person, main, currency, " +
                            "depositor_fin_status, liquidity_level, description, id_ob_kind, WSUM, ID_MDTASK" +
                            ",fullsum,responsibility,fine,wadd,kind,supplyvalue,fromdate,todate,PERIOD,PERIODDIMENSION)" +
                            "VALUES (warranty_seq.nextval,?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,?,?,?)");
                    Long idPerson = w.getPerson()==null?null:w.getPerson().getId();
                    stmn.setObject(1, getAccountId(w.getOrg()));
                    stmn.setObject(2, idPerson);
                    stmn.setObject(3, w.isMain()?"Y":"n");
                    stmn.setObject(4, w.getCurrency().getCode());
                    stmn.setObject(5, w.getDepositorFinStatus().getId()==null||w.getDepositorFinStatus().getId().longValue()==0||w.getDepositorFinStatus().getId().longValue()==-1?
                            null:w.getDepositorFinStatus().getId());
                    stmn.setObject(6, (w.getLiquidityLevel().getId()==null||w.getLiquidityLevel().getId().longValue()==0||w.getLiquidityLevel().getId().longValue()==-1)
                            ?null:w.getLiquidityLevel().getId());
                    stmn.setObject(7, w.getDescription());
                    stmn.setObject(8, w.getOb().getId());
                    stmn.setObject(9, w.getSum());
                    stmn.setObject(10, task.getId_task());
                    stmn.setObject(11, w.isFullSum() ? "y" : "n");
                    stmn.setObject(12, w.getResponsibilityCodes());
                    stmn.setObject(13, w.getFine());
                    stmn.setObject(14, w.getAdd());
                    stmn.setObject(15, w.getKind());
                    stmn.setObject(16, w.getSupplyvalue());
                    stmn.setObject(17, w.getFromdate());
                    stmn.setDate(18, w.getTodateSQL());
                    stmn.setObject(19, w.getPeriod());
                    stmn.setObject(20, w.getPeriodDimension());
                    stmn.execute();
                    stmn.close();
                } catch (Exception e) {
                    LOGGER.severe("Cant' insert into warranty table" + e.getMessage());
                    e.printStackTrace();
                    throw new MappingException(e.getMessage());
                }
            }

            saveFinesForWarranty(conn, task);
            
            //гарантии
            stmn = conn.prepareStatement("DELETE FROM garant WHERE ID_MDTASK=?");
            stmn.setObject(1, task.getId_task());
            stmn.execute();
            stmn.close();
            for(Guarantee guarantee : task.getSupply().getGuarantee()){
                try {
                    stmn = conn.prepareStatement("INSERT INTO garant (id,org, id_person, main, currency, " +
                            "depositor_fin_status, liquidity_level, description, id_ob_kind, SUM, ID_MDTASK,MAXDATE,FULLSUM," +
                            "supplyvalue,fromdate,todate,PERIOD,PERIODDIMENSION)" +
                            "VALUES (garant_seq.nextval,?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?)");
                    Long idPerson = guarantee.getPerson()==null?null:guarantee.getPerson().getId();
                    stmn.setObject(1, getAccountId(guarantee.getOrg()));
                    stmn.setObject(2, idPerson);
                    stmn.setObject(3, guarantee.isMain()?"Y":"n");
                    stmn.setObject(4, guarantee.getCurrency().getCode());
                    stmn.setObject(5, guarantee.getDepositorFinStatus().getId()==null||guarantee.getDepositorFinStatus().getId().longValue()==0||guarantee.getDepositorFinStatus().getId().longValue()==-1?
                            null:guarantee.getDepositorFinStatus().getId());
                    stmn.setObject(6, guarantee.getLiquidityLevel().getId()==null||guarantee.getLiquidityLevel().getId().longValue()==0||guarantee.getLiquidityLevel().getId().longValue()==-1?
                            null:guarantee.getLiquidityLevel().getId());
                    stmn.setObject(7, guarantee.getDescription());
                    stmn.setObject(8, guarantee.getOb().getId());
                    stmn.setObject(9, guarantee.getSum());
                    stmn.setObject(10, task.getId_task());
                    stmn.setObject(11, guarantee.getDate());
                    stmn.setObject(12, guarantee.isFullSum()?"y":"n");
                    stmn.setObject(13, guarantee.getSupplyvalue());
                    stmn.setObject(14, guarantee.getFromdate());
                    stmn.setObject(15, guarantee.getTodate());
                    stmn.setObject(16, guarantee.getPeriod());
                    stmn.setObject(17, guarantee.getPeriodDimension());
                    stmn.execute();
                    stmn.close();
                } catch (Exception e) {
                    LOGGER.severe("Cant' insert into garant table" + e.getMessage());
                    e.printStackTrace();
                    throw new MappingException(e.getMessage());
                }
            }
            //залоги
            stmn = conn.prepareStatement("DELETE FROM deposit WHERE ID_MDTASK=?");
            stmn.setObject(1, task.getId_task());
            stmn.execute();
            stmn.close();
            for(Deposit d : task.getSupply().getDeposit()){
                try {
                    stmn = conn.prepareStatement("insert into deposit (id,id_mdtask, is_main, type, zalog_description, " +
                            "opp_description, id_crmorg, id_ob_kind, depositor_fin_status, liquidity_level, zalog_market, " +
                            "zalog_terminate, zalog, discount, issuer, order_description, zalog_object,cond,weight,maxpart,supplyvalue," +
                            "fromdate,todate,PERIOD,PERIODDIMENSION,id_person,IS_POSLED)" +
                            "VALUES (deposit_seq.nextval,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,?,?)");
                    stmn.setObject(1, task.getId_task());
                    stmn.setObject(2, d.isMain()?"Y":"n");
                    stmn.setObject(3, d.getType());
                    stmn.setObject(4, d.getZalogDescription());
                    stmn.setObject(5, d.getOppDescription());
                    String orgid=null;
                    if(d.getOrg()!=null) orgid = d.getOrg().getAccountid();
                    stmn.setObject(6, orgid);
                    stmn.setObject(7, d.getOb().getId());
                    stmn.setObject(8, d.getDepositorFinStatus().getId()==null||d.getDepositorFinStatus().getId().longValue()==0||d.getDepositorFinStatus().getId().longValue()==-1?
                            null:d.getDepositorFinStatus().getId());
                    stmn.setObject(9, d.getLiquidityLevel().getId()==null||d.getLiquidityLevel().getId().longValue()==0||d.getLiquidityLevel().getId().longValue()==-1?
                            null:d.getLiquidityLevel().getId());
                    stmn.setObject(10, d.getZalogMarket());
                    stmn.setObject(11, d.getZalogTerminate());
                    stmn.setObject(12, d.getZalog());
                    stmn.setObject(13, d.getDiscount());
                    stmn.setObject(14, d.getIssuer().getAccountid());
                    stmn.setObject(15, d.getOrderDescription());
                    stmn.setObject(16, d.getZalogObject().getId());
                    stmn.setObject(17, d.getCond());
                    stmn.setObject(18, d.getWeight());
                    stmn.setObject(19, d.getMaxpart());
                    stmn.setObject(20, d.getSupplyvalue());
                    stmn.setObject(21, d.getFromdate());
                    stmn.setObject(22, d.getTodate());
                    stmn.setObject(23, d.getPeriod());
                    stmn.setObject(24, d.getPeriodDimension());
                    stmn.setObject(25, d.getPerson()==null?null:d.getPerson().getId());
                    stmn.setObject(26, d.isPosled()?"y":"n");
                    stmn.execute();
                    stmn.close();
                } catch (Exception e) {
                    LOGGER.severe("Cant' insert into deposit table" + e.getMessage());
                    e.printStackTrace();
                    throw new MappingException(e.getMessage());
                }
            }
            //Дополнительные атрибуты залогов
            stmn = conn.prepareStatement("DELETE FROM deposit_keyvalue WHERE ID_MDTASK=?");
            stmn.setObject(1, task.getId_task());
            stmn.execute();
            stmn.close();
            Iterator<Entry<String,String>> it = task.getSupply().getDepositKeyValue().entrySet().iterator();
            while(it.hasNext()) {
                try {
                    String key = it.next().getKey();
                    String value = task.getSupply().getDepositKeyValue().get(key);
                    stmn = conn.prepareStatement("insert into deposit_keyvalue (id_mdtask, key, val)" +
                            "VALUES (?, ?, ?)");
                    stmn.setObject(1, task.getId_task());
                    stmn.setObject(2, key);
                    stmn.setObject(3, value);
                    stmn.execute();
                    stmn.close();
                } catch (Exception e) {
                    LOGGER.severe("Cant' insert into deposit_keyvalue table" + e.getMessage());
                    e.printStackTrace();
                    throw new MappingException(e.getMessage());
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            throw new MappingException(e.getMessage());
        }
    }  

    /**
     * Сохраняем в базе данных: комментарии 
     */
    public static void saveComments(Connection conn, Task task) throws MappingException {
        try {
            PreparedStatement stmn = conn.prepareStatement("DELETE FROM "  + " TASKCOMMENT WHERE ID_MDTASK=?");
            stmn.setObject(1, task.getId_task());
            stmn.execute();
            stmn.close();
            for (int i = 0; i < task.getComment().size(); i++) {
                com.vtb.domain.Comment c = task.getComment().get(i);
                try {
                    stmn = conn.prepareStatement("INSERT INTO " 
                            + " TASKCOMMENT (ID_TASKCOMMENT, COMMENT_BODY, ID_MDTASK, WHO, ID_STAGE, WHEN, COMMENT_BODY_HTML) " + "VALUES (TASKCOMMENT_SEQ.nextval, ?, ?, ?, ?, ?, ?)");
                    stmn.setObject(2, task.getId_task());
                    stmn.setObject(1, c.getBody());
                    if (c.getAuthor() == null) {
                        stmn.setObject(3, null);
                    } else {
                        stmn.setObject(3, c.getAuthor().getId());
                    }
                    if(c.getStageid() == null || c.getStageid().longValue()==0){
                        stmn.setObject(4, null);
                    } else {
                        stmn.setObject(4, c.getStageid());
                    }
                    stmn.setTimestamp(5, c.getWhen());
                    stmn.setObject(6, c.getBodyHtml());

                    stmn.execute();
                    stmn.close();
                } catch (Exception e) {
                    LOGGER.severe("Cant' insert into TASKCOMMENT table" + e.getMessage());
                    e.printStackTrace();
                    throw new MappingException(e.getMessage());
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            throw new MappingException(e.getMessage());
        }
    }  

    /**
     * Сохраняем в базе данных: специальные и остальные условия сделки\лимита
     */
    public static void saveSpecialOtherConditions(Connection conn, Task task) throws MappingException {
        try {
            // other condition
        	String idrArray = "0";
            for(OtherCondition c : task.getOtherCondition())
            	if(c.getId()!=null)
            		idrArray += ", "+c.getId();
            PreparedStatement stmn = conn.prepareStatement("DELETE FROM "  + 
            		" CPS_DEAL_CONDITION WHERE ID_SYSTEM_MODULE=1 and ID_CREDIT_DEAL=? and ID_DEAL_CONDITION not in ("+idrArray+")");
            stmn.setObject(1, task.getId_task());
            stmn.execute();
            stmn.close();
            for (OtherCondition c : task.getOtherCondition()) {
                try {
                	if(c.getId()==null){
	                    stmn = conn.prepareStatement("INSERT INTO " 
	                            + " CPS_DEAL_CONDITION (ID_DEAL_CONDITION, ID_CONDITION_TYPE, NAME, ID_CREDIT_DEAL, ID_CONDITION,ID_SYSTEM_MODULE,SUPPLY_CODE) "
	                            + "VALUES (CPS_DEAL_CONDITION_SEQ.nextval,?,?,?,?,1,?)");
	                    stmn.setObject(1, c.getType());
	                    stmn.setObject(2, c.getBody());
	                    stmn.setObject(3, task.getId_task());
	                    if ((c.getIdCondition() != null) && (!c.getIdCondition().equals(0L))) stmn.setObject(4, c.getIdCondition());
	                    else stmn.setNull(4, java.sql.Types.NUMERIC);
	                    stmn.setObject(5,c.getSupplyCode());
	                    stmn.execute();
	                    stmn.close();
                	} else {
                		stmn = conn.prepareStatement("update " 
	                            + " CPS_DEAL_CONDITION set NAME=?, ID_CONDITION=? "
	                            + " where ID_DEAL_CONDITION=?");
	                    stmn.setObject(1, c.getBody());
	                    if ((c.getIdCondition() != null) && (!c.getIdCondition().equals(0L))) stmn.setObject(2, c.getIdCondition());
	                    else stmn.setNull(2, java.sql.Types.NUMERIC);
	                    stmn.setObject(3, c.getId());
	                    stmn.execute();
	                    stmn.close();
                	}
                } catch (Exception e) {
                    LOGGER.severe("Cant' insert into CPS_DEAL_CONDITION table" + e.getMessage());
                    e.printStackTrace();
                    throw new MappingException(e.getMessage());
                }
            }

            // specialcondition
            stmn = conn.prepareStatement("DELETE FROM specialcondition WHERE ID_MDTASK=?");
            stmn.setObject(1, task.getId_task());
            stmn.execute();
            stmn.close();
            for (SpecialCondition s : task.getSpecialCondition()) {
                try {
                    stmn = conn.prepareStatement("INSERT INTO specialcondition (id,type, description, ID_MDTASK) "
                            + "VALUES (specialcondition_seq.nextval,?,?,?)");
                    stmn.setObject(3, task.getId_task());
                    stmn.setObject(2, s.getBody());
                    stmn.setObject(1, s.getType());
                    stmn.execute();
                    stmn.close();
                } catch (Exception e) {
                    LOGGER.severe("Cant' insert into specialcondition table" + e.getMessage());
                    e.printStackTrace();
                    throw new MappingException(e.getMessage());
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            throw new MappingException(e.getMessage());
        }
    }

    /**
     * Сохраняем в базе данных:  ответственные подразделения
     */
    public static void saveDepartments(Connection conn, Task task) throws MappingException {
        try {
            // другие департаменты
            PreparedStatement stmn = conn.prepareStatement("DELETE FROM start_department WHERE ID_MDTASK=?");
            stmn.setObject(1, task.getId_task());
            stmn.execute();
            stmn.close();
            for (int i = 0; i < task.getHeader().getOtherDepartments().size(); i++) {
                Long nextid=new Long(0);
                stmn = conn.prepareStatement("select start_department_SEQ.nextval as id from dual");
                ResultSet r = stmn.executeQuery();
                if(r.next()){
                    nextid=r.getLong("id");
                    task.getHeader().getOtherDepartments().get(i).setId(nextid);
                }
                stmn.close();
                 try {   
                    stmn = conn.prepareStatement("INSERT INTO " 
                            + " start_department (id_start_department, id_department, ID_MDTASK) "
                            + "VALUES (?,?,?)");
                    stmn.setObject(3, task.getId_task());
                    stmn.setObject(2, task.getHeader().getOtherDepartments().get(i).getDep().getId());
                    stmn.setObject(1, nextid);
                    stmn.execute();
                    stmn.close();
                } catch (Exception e) {
                    LOGGER.severe("Cant' insert into start_department table" + e.getMessage());
                    e.printStackTrace();
                    throw new MappingException(e.getMessage());
                }
            }
            stmn = conn.prepareStatement("DELETE FROM manager WHERE ID_MDTASK=?");
            stmn.setObject(1, task.getId_task());
            stmn.execute();
            stmn.close();

            //менеджеры из других департаментов
            for(TaskDepartment td : task.getHeader().getOtherDepartments()){
                for (TaskManager tm : td.getManagers()) {
                    try {
                        stmn = conn.prepareStatement("INSERT INTO " 
                                + " manager (id_manager, ID_user, ID_MDTASK,id_start_department) "
                                + "VALUES (manager_SEQ.nextval,?,?,?)");
                        stmn.setObject(2, task.getId_task());
                        stmn.setObject(1, tm.getUser().getId());
                        stmn.setObject(3, td.getId());
                        stmn.execute();
                        stmn.close();
                    } catch (Exception e) {
                        LOGGER.severe("Cant' insert into manager table" + e.getMessage());
                        e.printStackTrace();
                        throw new MappingException(e.getMessage());
                    }
                }
            }

            //менеджеры из основного
            for (int i = 0; i < task.getHeader().getManagers().size(); i++) {
                try {
                    stmn = conn.prepareStatement("INSERT INTO " 
                            + " manager (id_manager, ID_user, ID_MDTASK) "
                            + "VALUES (manager_SEQ.nextval,?,?)");
                    stmn.setObject(2, task.getId_task());
                    stmn.setObject(1, task.getHeader().getManagers().get(i).getUser().getId());
                    stmn.execute();
                    stmn.close();
                } catch (Exception e) {
                    LOGGER.severe("Cant' insert into manager table" + e.getMessage());
                    e.printStackTrace();
                    throw new MappingException(e.getMessage());
                }
            }

            //места проведения лимита, но не сделки
            stmn = conn.prepareStatement("DELETE FROM "  + " place WHERE ID_MDTASK=?");
            stmn.setObject(1, task.getId_task());
            stmn.execute();
            stmn.close();
            for (int i = 0; i < task.getHeader().getPlaces().size(); i++) {
                try {
                    stmn = conn.prepareStatement("INSERT INTO " 
                            + " place (id_place, id_department, ID_MDTASK) "
                            + "VALUES (place_SEQ.nextval,?,?)");
                    stmn.setObject(2, task.getId_task());
                    stmn.setObject(1, task.getHeader().getPlaces().get(i).getId());
                    stmn.execute();
                    stmn.close();
                } catch (Exception e) {
                    LOGGER.severe("Cant' insert into place table" + e.getMessage());
                    e.printStackTrace();
                    throw new MappingException(e.getMessage());
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            throw new MappingException(e.getMessage());
        }
    }

    /**
     * Сохраняем в базе данных: EarlyPayment 
     */
    public static void saveEarlyPayments(Connection conn, Task task) throws MappingException {
        try {
            // EarlyPayment
            PreparedStatement stmn = conn.prepareStatement("DELETE FROM "  + " EARLY_PAYMENT WHERE ID_MDTASK=?");
            stmn.setObject(1, task.getId_task());
            stmn.execute();
            stmn.close();
            for (EarlyPayment s : task.getEarlyPaymentList()) {
                try {
                    stmn = conn.prepareStatement("INSERT INTO " 
                            + " EARLY_PAYMENT (ID_EARLY_PAYMENT, permission, commission, condition, ID_MDTASK, PERIOD_TYPE, DAYS_BEFORE_NOTIFY_BANK) "
                            + "VALUES (EARLY_PAYMENT_SEQ.nextval,?,?,?,?,?,?)");
                    stmn.setObject(4, task.getId_task());
                    stmn.setObject(1, s.getPermission());
                    stmn.setObject(2, s.getCommission());
                    stmn.setObject(3, s.getCondition());
                    stmn.setObject(5, s.getPeriodType());
                    stmn.setObject(6, s.getDaysBeforeNotifyBank());
                    stmn.execute();
                    stmn.close();
                } catch (Exception e) {
                    LOGGER.severe("Cant' insert into EARLY_PAYMENT table" + e.getMessage());
                    e.printStackTrace();
                    throw new MappingException(e.getMessage());
                }
            }            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            throw new MappingException(e.getMessage());
        }
    }

    /**
     * Сохраняем в базе данных: Справка согласования с экспертными подразделениями
     */
    public static void saveExpertsData(Connection conn, Task task) throws MappingException {
        try {
            PreparedStatement stmn = conn.prepareStatement("DELETE FROM "  + " DEP_RESOLUTION WHERE ID_MDTASK=?");
            stmn.setObject(1, task.getId_task());
            stmn.execute();
            stmn.close();
            for (int i = 0; i < task.getDepartmentAgreements().size(); i++) {
                try {
                    stmn = conn.prepareStatement("INSERT INTO " 
                            + " DEP_RESOLUTION (ID_DEP_RESOLUTION, ID_MDTASK, DEPARTMENT, REMARK, COM) "
                            + "VALUES (DEP_RESOLUTION_SEQ.nextval,?,?,?,?)");
                    stmn.setObject(1, task.getId_task());
                    stmn.setObject(2, task.getDepartmentAgreements().get(i).getDepartment());
                    stmn.setObject(3, task.getDepartmentAgreements().get(i).getRemark());
                    stmn.setObject(4, task.getDepartmentAgreements().get(i).getComment());
                    stmn.execute();
                    stmn.close();
                } catch (Exception e) {
                    LOGGER.severe("Cant' insert into DEP_RESOLUTION table" + e.getMessage());
                    e.printStackTrace();
                    throw new MappingException(e.getMessage());
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            throw new MappingException(e.getMessage());
        }
    }

    /**
     * Сохраняем в базе данных: стоп-факторы 
     */
    public static void saveStopFactors(Connection conn, Task task) throws MappingException {
        try {
            PreparedStatement stmn = conn.prepareStatement("DELETE FROM "  + " R_MDTASK_STOPFACTOR WHERE ID_MDTASK=?");
            stmn.setObject(1, task.getId_task());
            stmn.execute();
            stmn.close();
            // кладем клиентские 
            for (TaskStopFactor t : task.getTaskClientStopFactorList()) {
                if (!t.checkForErrors()) {
                    try {
                        stmn = conn.prepareStatement("INSERT INTO " 
                                + " R_MDTASK_STOPFACTOR (id,ID_MDTASK, ID_STOPFACTOR, FLAG) " + "VALUES (r_mdtask_stopfactor_seq.nextval,?,?,?)");
                        stmn.setObject(1, task.getId_task());
                        stmn.setObject(2, t.getStopFactor().getId());
                        stmn.setObject(3, t.isFlag());
                        stmn.execute();
                        stmn.close();
                    } catch (Exception e) {
                        LOGGER.severe("Cant' insert into R_MDTASK_STOPFACTOR table" + e.getMessage());
                        e.printStackTrace();
                        throw new MappingException(e.getMessage());
                    }
                }
            }
            // кладем стоп-факторы службы безопасности 
            for (TaskStopFactor tt : task.getTaskSecurityStopFactorList()) {
                if (!tt.checkForErrors()) {
                    try {
                        stmn = conn.prepareStatement("INSERT INTO " 
                                + " R_MDTASK_STOPFACTOR (id,ID_MDTASK, ID_STOPFACTOR, FLAG) " + "VALUES (r_mdtask_stopfactor_seq.nextval,?,?,?)");
                        stmn.setObject(1, task.getId_task());
                        stmn.setObject(2, tt.getStopFactor().getId());
                        stmn.setObject(3, tt.isFlag());
                        stmn.execute();
                        stmn.close();
                    } catch (Exception e) {
                        LOGGER.severe("Cant' insert into R_MDTASK_STOPFACTOR table" + e.getMessage());
                        e.printStackTrace();
                        throw new MappingException(e.getMessage());
                    }
                }
            }
            // кладем стоп-факторы 3
            for (TaskStopFactor tt : task.getTaskStopFactor3List()) {
                if (!tt.checkForErrors()) {
                    try {
                        stmn = conn.prepareStatement("INSERT INTO " 
                                + " R_MDTASK_STOPFACTOR (id,ID_MDTASK, ID_STOPFACTOR, FLAG) " + "VALUES (r_mdtask_stopfactor_seq.nextval,?,?,?)");
                        stmn.setObject(1, task.getId_task());
                        stmn.setObject(2, tt.getStopFactor().getId());
                        stmn.setObject(3, tt.isFlag());
                        stmn.execute();
                        stmn.close();
                    } catch (Exception e) {
                        LOGGER.severe("Cant' insert into R_MDTASK_STOPFACTOR table" + e.getMessage());
                        e.printStackTrace();
                        throw new MappingException(e.getMessage());
                    }
               }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            throw new MappingException(e.getMessage());
        }
    }

    /**
     * Сохраняем в базе данных: транши
     */
    public static void saveTranches(Connection conn, Task task) throws MappingException {
        try {
            String ids2NotDelete = "0";
            for(Trance trance : task.getTranceList()){
                if(trance.getId()!=null){
                    ids2NotDelete += ", "+trance.getId();
                }
            }
            PreparedStatement stmn = conn.prepareStatement("DELETE FROM trance WHERE ID_MDTASK=? and id not in ("+ids2NotDelete+")");
            stmn.setObject(1, task.getId_task());
            stmn.execute();
            stmn.close();
            for (int i = 0; i < task.getTranceList().size(); i++) {
                try {
                    Trance trance = task.getTranceList().get(i);
                    String sql=null;
                    if(trance.getId()==null){
                    	ResultSet r = conn.prepareStatement("SELECT trance_seq.NEXTVAL FROM dual").executeQuery();
                    	if(r.next())
                    		trance.setId(r.getLong(1));
                        sql = "INSERT INTO trance(id_mdtask,num,id) "
                        + "VALUES (?,?,?)";
                    } else {
                        sql = "update trance set id_mdtask=?,num=? where id=?";
                    }
                    stmn = conn.prepareStatement(sql);
                    stmn.setObject(1, task.getId_task());
                    stmn.setObject(2, i+1);
                        stmn.setObject(3, trance.getId());
                    stmn.executeUpdate();
                    if(trance.getId()!=null){
                    	stmn = conn.prepareStatement("DELETE FROM withdraw WHERE id_trance=?");
                    	stmn.setObject(1, trance.getId());
                    	stmn.execute();
                    	stmn = conn.prepareStatement("insert into withdraw(id,id_mdtask,id_trance,sum,currency,usedatefrom,usedateto,quarter,"
                    			+ "month,year,hyear, sum_scope, PERIOD_DIMENSION_FROM, PERIOD_DIMENSION_BEFORE, FROM_PERIOD, BEFORE_PERIOD) "
                    			+ "values(WITHDRAW_SEQ.nextval,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                    	stmn.setObject(1, task.getId_task());
                    	stmn.setObject(2, trance.getId());
                    	for(Withdraw w : trance.getWithdraws()){
                    		stmn.setObject(3, w.getSum());
                    		stmn.setObject(4, w.getCurrency());
                    		stmn.setObject(5, w.getUsedatefrom());
                    		stmn.setObject(6, w.getUsedateto());
                    		stmn.setObject(7, w.getQuarter());
                    		stmn.setObject(8, w.getMonth());
                    		stmn.setObject(9, w.getYear());
                    		stmn.setObject(10, w.getHyear());
                    		stmn.setObject(11, w.getSumScope());
                    		stmn.setObject(12, w.getPeriodDimensionFrom());
                    		stmn.setObject(13, w.getPeriodDimensionBefore());
                    		stmn.setObject(14, w.getFromPeriod());
                    		stmn.setObject(15, w.getBeforePeriod());
                    		stmn.executeUpdate();
                    	}
                    }
                } catch (Exception e) {
                    LOGGER.severe("Cant' insert into trance table" + e.getMessage());
                    e.printStackTrace();
                    throw new MappingException(e.getMessage());
                }
            }
            stmn = conn.prepareStatement("DELETE FROM withdraw WHERE id_trance is null and id_mdtask=?");
            stmn.setObject(1, task.getId_task());
            stmn.execute();
            stmn = conn.prepareStatement("insert into withdraw(id,id_mdtask,id_trance,sum,currency,usedatefrom,"
            		+ "usedateto,quarter,month,year,hyear, sum_scope, PERIOD_DIMENSION_FROM, PERIOD_DIMENSION_BEFORE, FROM_PERIOD, BEFORE_PERIOD) "
            		+ "values(WITHDRAW_SEQ.nextval,?,null,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            stmn.setObject(1, task.getId_task());
            for(Withdraw w : task.getWithdraws()){
            	stmn.setObject(2, w.getSum());
            	stmn.setObject(3, w.getCurrency());
            	stmn.setObject(4, w.getUsedatefrom());
            	stmn.setObject(5, w.getUsedateto());
            	stmn.setObject(6, w.getQuarter());
            	stmn.setObject(7, w.getMonth());
            	stmn.setObject(8, w.getYear());
            	stmn.setObject(9, w.getHyear());
            	stmn.setObject(10, w.getSumScope());
            	stmn.setObject(11, w.getPeriodDimensionFrom());
            	stmn.setObject(12, w.getPeriodDimensionBefore());
            	stmn.setObject(13, w.getFromPeriod());
        		stmn.setObject(14, w.getBeforePeriod());
            	stmn.executeUpdate();
            }
            
            stmn.close();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            throw new MappingException(e.getMessage());
        }
    }

    /**
     * Сохраняем в базе данных: самые основные и общие параметры сделки 
     */
    public static void saveParameters(Connection conn, Task task) throws MappingException {
        try {
            final String CMD_UPDATE = "update mdtask set MDTASK_SUM=?, CURRENCY=?, LIMIT_ISSUE_SUM=?, DEBT_LIMIT_SUM=?, IS_LIMIT_SUM=?, IS_DEBT_SUM=?, "
            		+ "IRREGULAR=?, PRODUCT_NAME=?, MDTASK_NUMBER=?, "
                    + "INITDEPARTMENT=?,SUBPLACE=?, MANAGER=?, PLACE=?, ADDSUPPLY=?, "
                    + "VALIDFROM=?,VALIDTO=?, USEDATEFROM=?, USEDATE=?, PROPOSED_DT_SIGNING=?, PERIOD=?, PERIOD_COMMENT=?, USEPERIOD=?, USEPERIODTYPE=?,"
                    + "DESCRIPTION=?, ID_LIMIT_TYPE=?, "
                    + "QUALITY_CATEGORY=?, QUALITY_CATEGORY_DESC=?, DISCHARGE=?, OPPORTUNITYID=?, "
                    + "TEMP_MEETINGDATE=?, meeting_proposed_date=?, TEMP_RESOLUTION=?, "
                    +"deleted=?, id_operationtype=?, CRMID=?, crm_queue_id=?, CRMLIMITNAME=?, CRMCODE=?, CURRENCYLIST=?,CRMINLIMIT=?, "
                    +"STATUSRETURN=?, STATUSRETURNTEXT=?, STATUSRETURNDATE=?, STATUSRETURNWHO=?, EXCHANGE_RATE=?, "
                    +"PREAMBULO=?, ID_QUESTION_TYPE=?, IS_SUPPLY_EXIST=?, IS_3FACES=?,guarantee_cond=?,d_cond=?, extra_sum_info=?, "
                    +"redistrib_residues=?, is_project_fin=?, cfactor=?, trance_comment=?, "
                    +"IS_GUARANTEE=?, CONTRACT=?, WARRANTY_ITEM=?, BENEFICIARY=?,BENEFICIARY_OGRN=?, credit_decision_project=?, " +
                    "changed_conditions=?, country=?, PERIODDIMENSION=?,PERIOD_DAYS=?, TARGET_TYPE_COMMENT=?, PROJECT_NAME=? "
                    + " where ID_MDTASK=?";

            LOGGER.info("mdtask sql update string is "+CMD_UPDATE);
            PreparedStatement stmn = conn.prepareStatement(CMD_UPDATE);
            int queryParam = 1;
            Main main = task.getMain();
            main.correctQuantityData();
            stmn.setObject(queryParam++, main.getSum());
            String currencycode = null;
            if (main.getCurrency2() != null)
                currencycode = main.getCurrency2().getCode();
            stmn.setObject(queryParam++, currencycode);
            stmn.setObject(queryParam++, main.getLimitIssueSum());
            stmn.setObject(queryParam++, main.getDebtLimitSum());
            stmn.setString(queryParam++, main.isLimitIssue() ? "y" :"n");
            stmn.setString(queryParam++, main.isDebtLimit() ? "y" : "n");
            stmn.setString(queryParam++, main.isIrregular() ? "y" : "n");
            stmn.setString(queryParam++, main.getProduct_name());
            
            stmn.setObject(queryParam++, task.getHeader().getNumber());
            stmn.setInt(queryParam++, task.getHeader().getStartDepartment().getId().intValue());
            stmn.setObject(queryParam++, task.getHeader().getSubplace());
            stmn.setObject(queryParam++, task.getHeader().getManager());
            if(task.getHeader().getPlace() == null 
                    || task.getHeader().getPlace().getId() == null
                    || task.getHeader().getPlace().getId().intValue()==0){
                stmn.setObject(queryParam++, null);
            } else {
                stmn.setInt(queryParam++, task.getHeader().getPlace().getId().intValue());
            }
            stmn.setObject(queryParam++, task.getSupply().getAdditionSupply());
            
            stmn.setObject(queryParam++, main.getValidfrom());
            stmn.setObject(queryParam++, main.getValidto());
            stmn.setObject(queryParam++, main.getUsedatefrom());
            stmn.setObject(queryParam++, main.getUsedate());
            stmn.setObject(queryParam++, main.getProposedDateSigningAgreement());        
            stmn.setObject(queryParam++, main.getPeriod());
            if (main.getPeriodComment() != null) stmn.setObject(queryParam++, main.getPeriodComment());
            else stmn.setNull(queryParam++, Types.VARCHAR);
            stmn.setObject(queryParam++, main.getUseperiod());
            if (main.getUseperiodtype() != null) stmn.setObject(queryParam++, main.getUseperiodtype());
            else stmn.setNull(queryParam++, Types.VARCHAR);
            
            stmn.setObject(queryParam++, task.getDescription());
            Integer idLimitType=task.getHeader().getIdLimitType();
            stmn.setObject(queryParam++, ((idLimitType==null)||idLimitType.equals(0))?null:idLimitType);
            
            stmn.setObject(queryParam++, task.getGeneralCondition().getQuality_category());
            stmn.setObject(queryParam++, task.getGeneralCondition().getQuality_category_desc());
            
            stmn.setObject(queryParam++, task.getGeneralCondition().getDischarge());
            stmn.setObject(queryParam++, task.getOPPORTUNITYID());
            //временные
            stmn.setObject(queryParam++, task.getTemp().getMeetingDate());
            stmn.setDate(queryParam++, task.getTemp().getPlanMeetingDate());
            stmn.setObject(queryParam++, task.getTemp().getResolution());
            stmn.setString(queryParam++, task.isDeleted()?"Y":"N");
            stmn.setObject(queryParam++, 
                    (task.getHeader().getOperationtype().getId()==null||task.getHeader().getOperationtype().getId().longValue()==0
                     ||task.getHeader().getOperationtype().getId().longValue()==-1)
                    ?null:task.getHeader().getOperationtype().getId());
            stmn.setObject(queryParam++, task.getHeader().getCrmid());
            stmn.setObject(queryParam++, task.getHeader().getCrmQueueId());
            stmn.setObject(queryParam++, task.getHeader().getCrmlimitname());
            stmn.setObject(queryParam++, task.getHeader().getCrmcode());
            stmn.setObject(queryParam++, task.getHeader().getCrmcurrencylist());
            stmn.setObject(queryParam++, task.getInLimit());
            if(task.getTaskStatusReturn().getStatusReturn()!=null
                    &&task.getTaskStatusReturn().getStatusReturn().getId()!=null
                    &&!task.getTaskStatusReturn().getStatusReturn().getId().equals("0"))
                {stmn.setObject(queryParam++, task.getTaskStatusReturn().getStatusReturn().getId());
                LOGGER.info(task.getTaskStatusReturn().getStatusReturn().getId());}
            else
                {stmn.setObject(queryParam++, null);}
            stmn.setObject(queryParam++, task.getTaskStatusReturn().getStatusReturnText());
            if(task.getTaskStatusReturn().getDateReturn()!=null)
                stmn.setObject(queryParam++, new java.sql.Timestamp(task.getTaskStatusReturn().getDateReturn().getTime()));
            else
                {stmn.setObject(queryParam++, null);}
            if(task.getTaskStatusReturn().getIdUser()!=null && !task.getTaskStatusReturn().getIdUser().equals(0l))
                stmn.setObject(queryParam++, task.getTaskStatusReturn().getIdUser());
            else
                stmn.setObject(queryParam++, null);
            stmn.setObject(queryParam++, main.getExchangeRate());
            stmn.setObject(queryParam++, task.getCcPreambulo());
            stmn.setObject(queryParam++, task.getCcQuestionType()==null?null:task.getCcQuestionType().getId());
            stmn.setObject(queryParam++, task.getSupply().isExist()?"y":"n");
            stmn.setObject(queryParam++, task.isFaces3()?"y":"n");
            stmn.setObject(queryParam++, task.getSupply().getGuaranteeCondition());
            stmn.setObject(queryParam++, task.getSupply().getDCondition());
            if (task.isSubLimit()) stmn.setObject(queryParam++, main.getExtraSumInfo());
            else stmn.setNull(queryParam++, Types.VARCHAR);
            if (task.isLimit()) stmn.setObject(queryParam++, main.isRedistribResidues()?"y":"n");
            else stmn.setObject(queryParam++, "n");
            stmn.setObject(queryParam++, main.isProjectFin()?"y":"n");
            stmn.setObject(queryParam++, task.getSupply().getCfact());
            stmn.setObject(queryParam++, task.getTranceComment());
            stmn.setObject(queryParam++, main.isGuaranteeType() ? "y" : "n");
            stmn.setObject(queryParam++, main.getContract());
            stmn.setObject(queryParam++, main.getWarrantyItem());
            stmn.setObject(queryParam++, main.getBeneficiary());
            stmn.setObject(queryParam++, main.getBeneficiaryOGRN());
            stmn.setObject(queryParam++, task.getCreditDecisionProject());
            if (main.getChangedConditions() != null) stmn.setObject(queryParam++, main.getChangedConditions());
            else stmn.setNull(queryParam++, Types.VARCHAR);
            stmn.setObject(queryParam++, main.getCountry());
            stmn.setString(queryParam++, main.getPeriodDimension());
            stmn.setLong(queryParam++, main.getPeriodInDay());
            stmn.setString(queryParam++, main.getTargetTypeComment());
            
            stmn.setString(queryParam++, main.getProjectName());
            stmn.setLong(queryParam++, task.getId_task());
            stmn.executeUpdate();
            stmn.close();
            
            if(task.getAuthorizedBody()!=null && task.getAuthorizedBody().longValue()!=0){
                stmn = conn.prepareStatement("update "  + " mdtask set "
                        +"ID_AUTHORIZEDBODY=? "
                        + " where ID_MDTASK=?");
                stmn.setObject(1, task.getAuthorizedBody());
                stmn.setLong(2, task.getId_task());
                stmn.executeUpdate();
                stmn.close();
            }
            conn.prepareStatement("update mdtask SET TASKTYPE='"+task.getHeader().getTasktype()
                    +"' WHERE ID_MDTASK="+task.getId_task()).executeUpdate();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            throw new MappingException(e.getMessage());
        }
    }

    /**
     * @param  conn Connection to database
     * @param  taskId task Id
     * @return 1 if true, 0 if false, -1 if null
     */
    private static int checkBooleanField(Connection conn, Long taskId, String sql) {
        PreparedStatement st = null; ResultSet r = null;
        try {
            st = conn.prepareStatement(sql);
            st.setObject(1, taskId);
            r = st.executeQuery();
            if (r.next()) {
                String res = r.getString("value");
                if (res == null) return -1; 
                return res.equalsIgnoreCase("y") ? 1 : 0;
            }
            return -1;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return -1;
        } finally {
            try{ if(r != null) r.close();} catch(SQLException se){}
            try{ if(st != null) st.close();} catch(SQLException se){}
        }
    }
    
    /**
     * Clears flag isRenewable for all sublimits
     * @param  conn Connection to database
     * @param  parentId parent mdtask Id
     */
    private static void clearRenewable(Connection conn, TaskMapper taskMapper, Long parentId) {
        final String sql =
            "UPDATE mdtask m "
            + " set  m.is_renewable = 'n', m.may_be_renewable = 'n' "
            + " where m.ID_MDTASK = ?";
        setOrClearRenewable(conn, taskMapper, parentId, sql);
    }
    
    /**
     * Sets flag mayBeRenewable for all sublimits
     * @param  conn Connection to database
     * @param  parentId parent mdtask Id
     */
    private static void setMayBeRenewable(Connection conn, TaskMapper taskMapper, Long parentId) {
        final String sql =
            "UPDATE mdtask m "
            + " set  m.may_be_renewable = 'y' "
            + " where m.ID_MDTASK = ?";
        setOrClearRenewable(conn, taskMapper, parentId, sql);
    }
    
    /**
     * Sets flag mayBeRenewable for all sublimits
     * @param  conn Connection to database
     * @param  parentId parent mdtask Id
     * @param  sql passed sql string
     */
    private static void setOrClearRenewable(Connection conn, TaskMapper taskMapper, Long parentId, String sql) {
        if (parentId == null) return;
        // идем вниз по иерархии и устанвливаем или сбрасываем у нижестоящих лимитов\сублимитов флаг MayBeRenewable (и Renewable).
        try {
            // получаем список детей
            ArrayList<Long> sublimits = taskMapper.findTaskByParent(parentId, false);
            // сбрасываем или устанавливаем для каждого флаг MayBeRenewable и Renewable и запускаем процесс рекурсивно
            for (Long childId : sublimits) {
                PreparedStatement stmn = conn.prepareStatement(sql);
                try {
                    stmn.setObject(1, childId);
                    stmn.executeUpdate();
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "SQL Exception in method setOrClearRenewable#UPDATE ", e);
                } finally {
                    stmn.close();
                }
                setOrClearRenewable(conn, taskMapper, childId, sql);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "SQL Exception in method setOrClearRenewable ", e);
        }        
    }
    
    /**
     * 
     * @param conn Connection to database
     * @param taskId task Id
     * @return value of the field may_be_renewable in the database
     */
    private static int checkMayBeRenewable(Connection conn, Long taskId) {
        return checkBooleanField(conn, taskId, "SELECT m.may_be_renewable as value FROM MDTASK m where m.ID_MDTASK=? " );
    }
    
    /**
     * @param  conn Connection to database
     * @param  taskId task Id
     * @return value of the field is_renewable in the database
     */
    private static int checkRenewable(Connection conn, Long taskId) {
        return checkBooleanField(conn, taskId, "SELECT m.is_renewable as value FROM MDTASK m where m.ID_MDTASK=? ");
    }
    
    /**
     * Обработка установки \ сброса значений флагов 'Возобновляемый' (isRenewable()) и 'может ли быть возобновляемым' (isMayBeRenewable())
     * Запускается также процедура установки \ сброса значений для сублимитов 
     * @param conn Connection to the databse
     * @param task task element
     */
    public static void updateLimitRenewable (Connection conn, TaskMapper taskMapper, Task task) {
        // установим Возобновляемый лимит, если нужно.
        Main main = task.getMain();

        // прочитать и установить значение main.isMayBeRenewable() из базы. Потому что значение могло и устареть уже
        // и могло быть переустановлено в вышестоящих лимитах \ сублимитах.
        // значение флага не могу самовольно задавать. Только вышестоящие сублимиты или при создании данного сублимита
        int mayBeRenewableSaved = checkMayBeRenewable(conn, task.getId_task());
        
        switch (mayBeRenewableSaved) {
            // если есть возможность редактировать флажок 'Возобновляемый'
            case 1: 
                if(!main.isRenewable()) {
                    // флажок isRenewable() был снят.
                    // выясним, сейчас ли флажок был снят (при редактировании) или ранее.
                    int isRenewableSaved = checkRenewable(conn, task.getId_task());
                    // если флажок был снят сейчас, а не в прошлый раз, то запускаем процедуру обнуления возобновляемости 
                    // (isRenewable()) у всех сублимитов
                    if (isRenewableSaved == 1) clearRenewable(conn, taskMapper, task.getId_task());                    
                } else {
                    // флажок isRenewable() был установлен (сейчас или в прошлый раз, неважно).
                    // запускаем процедуру установки возможности возобновляемости isMayBeRenewable()у всех сублимитов
                    //FIXME портит транзакцию. Нужно найти требования и разобраться что тут происходит
                    //setMayBeRenewable(conn, taskMapper, task.getId_task());
                }
                main.setMayBeRenewable(true); 
                break;         

            // нет возможности редактировать флажок 'Возобновляемый'. Тогда сбрасываем в false
            case 0: 
                main.setRenewable(false);
                main.setMayBeRenewable(false); 
                break;

            // если мы просто создаем лимт или сублимит и их раньше не было в базе. Тогда в базу кладутся данные из main,
            // заполненные ранее (при создании лимита или сублимита)
            case -1: 
                break;
        }

        // запишем изменения в базу
        PreparedStatement stmn = null;
        try {
            stmn = conn.prepareStatement(
                "UPDATE mdtask m "
                + " set m.may_be_renewable = ?, m.is_renewable = ? "
                + " where m.ID_MDTASK = ?"
            );
            stmn.setObject(1, task.getMain().isMayBeRenewable()?"y":"n");
            stmn.setObject(2, task.getMain().isRenewable()?"y":"n");            
            stmn.setLong(3, task.getId_task());
            stmn.executeUpdate();
            stmn.close();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            try{ if(stmn != null) stmn.close();} catch(SQLException se){}
        }            
    }    
}
