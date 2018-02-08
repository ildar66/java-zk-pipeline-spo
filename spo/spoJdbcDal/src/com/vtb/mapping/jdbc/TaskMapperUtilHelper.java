package com.vtb.mapping.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vtb.domain.Main;
import com.vtb.domain.Task;
import com.vtb.exception.MappingException;

/**
* Helper class for performing useful helper methods for Task.
* Should be in Session Facade implementation really. But we have strange architecture. 
* @author Kuznetsov Michael
**/
public class TaskMapperUtilHelper {

    private static final Logger LOGGER = Logger.getLogger(TaskMapperUtilHelper.class.getName());

    /**
     * Сохраняем в базе данных: самые основные и общие параметры сделки 
     */
    public static void saveParameters(Connection conn, Task task) throws MappingException {
        try {
            final String CMD_UPDATE = "update mdtask set MDTASK_SUM=?, CURRENCY=?, LIMIT_ISSUE_SUM=?, DEBT_LIMIT_SUM=?, IS_LIMIT_SUM=?, IS_DEBT_SUM=?, MDTASK_NUMBER=?, "
                    + "INITDEPARTMENT=?, MANAGER=?, PLACE=?, ADDSUPPLY=?, "
                    + "VALIDFROM=?,VALIDTO=?, USEDATEFROM=?, USEDATE=?, PROPOSED_DT_SIGNING=?, PERIOD=?, PERIOD_COMMENT=?, USEPERIOD=?, USEPERIODTYPE=?, "
                    + "IS_GUARANTEE=?, CONTRACT=?, WARRANTY_ITEM=?, BENEFICIARY=? "
                    + "where ID_MDTASK=?";

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
            
            stmn.setObject(queryParam++, task.getHeader().getNumber());
            stmn.setInt(queryParam++, task.getHeader().getStartDepartment().getId().intValue());
            stmn.setObject(queryParam++, task.getHeader().getManager());
            if(task.getHeader().getPlace() == null){
                stmn.setObject(queryParam++, null);
            }else{
                stmn.setInt(queryParam++, task.getHeader().getPlace().getId().intValue());
            }
            stmn.setObject(queryParam++, task.getSupply().getAdditionSupply());
            stmn.setLong(queryParam++, task.getId_task());
            
            stmn.executeUpdate();
            stmn.close();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            throw new MappingException(e.getMessage());
        }
    }
}
