package org.uit.director.action;


import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.vtb.domain.CommissionDeal;
import com.vtb.domain.Deposit;
import com.vtb.domain.Guarantee;
import com.vtb.domain.Task;
import com.vtb.domain.Warranty;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.TaskActionProcessor;

import ru.masterdm.compendium.mapping.jdbc.JDBCMapper;
import ru.masterdm.spo.utils.Formatter;
import ru.md.domain.Withdraw;
import ru.md.spo.dbobjects.FactPercentJPA;
import ru.md.spo.dbobjects.TaskJPA;
import ru.md.spo.ejb.TaskFacadeLocal;

/**
 * Action для вывода отчета во фрейме
 *
 */
public class ReportPrintAccessAction extends Action {
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("Отчет Excel по контролю за введенными данными по Сделке");
		TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
		TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
		File file = new File(getServlet().getServletContext().getRealPath("/access_report.xls"));
		FileInputStream fis = new FileInputStream(file);
		System.out.println("access.xls size " + file.length());
		HSSFWorkbook wb = new HSSFWorkbook(fis, false);
		//заголовок
		wb.getSheetAt(0).getRow(0).getCell(0).setCellValue("Кредитный портфель из СПО по состоянию на " + dateFormat.format(new Date()));
		//connection
		Connection connection = JDBCMapper.getConnection();
		//переменные аля C++
		PreparedStatement st = null;
		ResultSet rs = null;
		PreparedStatement st1 = null;
		ResultSet rs1 = null;
		//сам отчёт
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ");
		sb.append("M.ID_MDTASK,M.MDTASK_NUMBER, ");
		sb.append("ORG.ORGANIZATIONNAME, ");
		sb.append("ORG.INN, ");
		sb.append("DNUM.OFFICIAL_NUMBER, ");
		sb.append("DNUM.OFFICIAL_DATE, ");
		sb.append("M.MDTASK_SUM, ");
		sb.append("M.CURRENCY, ");
		sb.append("M.VALIDTO, ");
		sb.append("CASE ");
		sb.append("WHEN UPPER(M.IS_DEBT_SUM) = 'Y' THEN M.DEBT_LIMIT_SUM ");
		sb.append("WHEN UPPER(M.IS_LIMIT_SUM) = 'Y' THEN M.LIMIT_ISSUE_SUM ");
		sb.append("ELSE M.MDTASK_SUM ");
		sb.append("END MDTASK_SUM_, ");
		sb.append("m.period, ");
		sb.append("m.perioddimension, ");
		sb.append("m.period_comment, ");
		sb.append("M.QUALITY_CATEGORY, ");
		sb.append("(SELECT SHORTNAME FROM DEPARTMENTS WHERE ID_DEPARTMENT = M.INITDEPARTMENT) AS INIT_DEPARTMENT_SHORT_NAME, ");
		sb.append("ORG.industryname, ");
		sb.append("APPROVED_RATING ");
		sb.append("FROM MDTASK M ");
		sb.append("LEFT JOIN V_CPS_CREDIT_DEAL_NUMBER DNUM ON DNUM.ID_MDTASK = M.ID_MDTASK ");
		sb.append("LEFT JOIN ( ");
		sb.append("SELECT ID_MDTASK, ID_CRMORG ");
		sb.append("FROM ( ");
		sb.append("SELECT ID_MDTASK, ID_CRMORG, ROW_NUMBER() OVER(PARTITION BY ID_MDTASK ORDER BY ID_R) RN ");
		sb.append("FROM R_ORG_MDTASK TASK_ORG ");
		sb.append(") ZROM WHERE RN = 1 ");
		sb.append(") ROM ON M.ID_MDTASK = ROM.ID_MDTASK ");
		sb.append("LEFT JOIN V_ORGANISATION ORG ON ORG.crmid = ROM.ID_CRMORG ");
		sb.append("LEFT JOIN (");
		sb.append("SELECT PARTNER_ID ID_CONTRACTOR, RATING APPROVED_RATING ");
		sb.append("FROM ( ");
		sb.append("SELECT PARTNER_ID, RATING, ROW_NUMBER() OVER(PARTITION BY PARTNER_ID ORDER BY MEETING_DATE DESC) RN ");
		sb.append("FROM CR_APPROVED_RATING ");
		sb.append(") WHERE RN = 1 ");
		sb.append(") RAT ON ROM.ID_CRMORG = RAT.ID_CONTRACTOR ");
		sb.append("WHERE M.IS_IMPORTED IS NOT NULL  ");//and m.mdtask_number=203442
		st = connection.prepareStatement(sb.toString());
		rs = st.executeQuery();
		Sheet sheet = wb.getSheetAt(0);
		int startRow = 4;
		while (rs.next()) {
			Row row = sheet.createRow(startRow++);
			row.setHeightInPoints((5*sheet.getDefaultRowHeightInPoints()));
			Cell cell = row.createCell(0);
			String value = rs.getString("ORGANIZATIONNAME");
			if (value != null) {
				cell.setCellValue(value.trim());
			}
			cell = row.createCell(1);
			value = rs.getString("INN");
			if (value != null) {
				cell.setCellValue(value.trim());
			}
			cell = row.createCell(2);
			value = rs.getString("OFFICIAL_NUMBER");
			if (value != null) {
				cell.setCellValue(value.trim());
			}
			cell = row.createCell(3);
			Date dateValue = rs.getDate("OFFICIAL_DATE");
			if (dateValue != null) {
				cell.setCellValue(formatDate(dateValue, dateFormat));
			}
			cell = row.createCell(5);
			value = rs.getString("CURRENCY");
			if (value != null) {
				cell.setCellValue(value.trim());
			}
			cell = row.createCell(8);
			dateValue = rs.getDate("OFFICIAL_DATE");
			if (dateValue != null) {
				cell.setCellValue(formatDate(dateValue, dateFormat));
			}
			cell = row.createCell(9);
			value = rs.getString("MDTASK_SUM_");
			if (value != null) {
				cell.setCellValue(value.trim());
			}
			cell = row.createCell(45);
			value = rs.getString("INIT_DEPARTMENT_SHORT_NAME");
			if (value != null) {
				cell.setCellValue(value.trim());
			}
			cell = row.createCell(46);
			value = rs.getString("industryname");
			if (value != null) {
				cell.setCellValue(value.trim());
			}
			cell = row.createCell(52);
			value = rs.getString("APPROVED_RATING");
			if (value != null) {
				cell.setCellValue(value.trim());
			}
			cell = row.createCell(54);
			value = rs.getString("MDTASK_NUMBER");
			if (value != null) {
				cell.setCellValue(value.trim());
			}
			Long mdtaskId = rs.getLong("ID_MDTASK");
			Task task = processor.getTask(new Task(new Long(mdtaskId)));
			TaskJPA taskJPA = taskFacadeLocal.getTask(mdtaskId);
			//Дата окончания использования
			cell = row.createCell(14);
			dateValue = rs.getDate("VALIDTO");
			String field14 = formatDate(dateValue, dateFormat);
			field14 += " "+Formatter.format(task.getMain().getUseperiod());
			field14 += " "+Formatter.str(task.getMain().getUseperiodtype());
			cell.setCellValue(field14);

			//процентная ставка
			cell = row.createCell(18);
			String val = "";
			for(FactPercentJPA fp : taskJPA.getFactPercents())
				val += Formatter.format(fp.getRate4())+"\n";
			cell.setCellValue(val);
			//комиссии
			cell = row.createCell(23);
			val = "";
			for(CommissionDeal c : task.getCommissionDealList())
				val += c.toString()+"\n";
			cell.setCellValue(val);
			//Категория качества
			cell = row.createCell(36);
			val = "";
			val += Formatter.str(task.getGeneralCondition().getQuality_category())+" "+Formatter.str(task.getGeneralCondition().getQuality_category_desc());
			cell.setCellValue(val);
			
			sb = new StringBuffer();
			sb.append("select amount, ");
			sb.append("currency, ");
			sb.append("from_dt, ");
			sb.append("to_dt ");
			sb.append("from payment_schedule ");
			sb.append("where id_mdtask = ?");
			st1 = connection.prepareStatement(sb.toString());
			st1.setLong(1, mdtaskId);
			rs1 = st1.executeQuery();
			String res = "";
			while (rs1.next()) {
				dateValue = rs1.getDate("from_dt");
				if (dateValue != null) {
					res += formatDate(dateValue, dateFormat);
				}
				res += " - ";
				dateValue = rs1.getDate("to_dt");
				if (dateValue != null) {
					res += formatDate(dateValue, dateFormat);
				}
				res += " - ";
				value = rs1.getString("amount");
				if (value != null) {
					res += value.trim();
				}
				res += " - ";
				value = rs1.getString("currency");
				if (value != null) {
					res += value.trim();
				}
				res += "\n";
			}
			if (res != "") {
				cell = row.createCell(10);
				cell.setCellValue(res.trim());
				CellStyle cs = wb.createCellStyle();
				cs.setWrapText(true);
				cell.setCellStyle(cs);
				sheet.autoSizeColumn((short)10);
			}
			rs1.close();
			st1.close();
			//График использования 
			res = "";
			for(Withdraw w : task.getWithdraws()){
				w.generateFormatetDates(taskJPA.getTrance_period_format());
				res +=  w.getSumCurrency()+" ("+ w.getDates()+")\n";
			}
			if (res != "") {
				cell = row.createCell(15);
				cell.setCellValue(res.trim());
				CellStyle cs = wb.createCellStyle();
			    cs.setWrapText(true);
			    cell.setCellStyle(cs);
			    sheet.autoSizeColumn((short)15);
			}
			//График погашения процентов
			res = Formatter.str(task.getInterestPay().getPay_int())+
					" c "+Formatter.str(task.getInterestPay().getFirstPayDateFormatted())+" по "+
					Formatter.str(task.getInterestPay().getFinalPayDateFormatted())+" "+Formatter.str(task.getInterestPay().getDescription());
			if (res != "") {
				cell = row.createCell(16);
				cell.setCellValue(res.trim());
				CellStyle cs = wb.createCellStyle();
				cs.setWrapText(true);
				cell.setCellStyle(cs);
				sheet.autoSizeColumn((short)16);
			}
			//Обеспечение ссуды 
			res = "";
			for(Deposit d : task.getSupply().getDeposit())
				res += "Залог "+d.getContractorName()+" "+d.getZalogDescription()+"\n";
			//Поручительство
			for(Warranty w : task.getSupply().getWarranty())
				res += "Поручительство "+w.getContractorName()+" "+w.getFormatedSum()+" "+
			(w.getCurrency()==null?"":w.getCurrency().getCode())
				+"\n";
			//Гарантия
			for(Guarantee g : task.getSupply().getGuarantee())
				res += "Гарантия "+g.getContractorName()+" "+Formatter.format(g.getSum())+
						(g.getCurrency()==null?"":g.getCurrency().getCode())+"\n";
			if (res != "") {
				cell = row.createCell(38);
				cell.setCellValue(res.trim());
				CellStyle cs = wb.createCellStyle();
				cs.setWrapText(true);
				cell.setCellStyle(cs);
				sheet.autoSizeColumn((short)38);
			}
			
			
			//штрафы
			sb = new StringBuffer();
			sb.append("select description,PUNITIVE_TYPE, ");
			sb.append("fine_value_Text, ");
			sb.append("fine_value, ");
			sb.append("currency ");
			sb.append("from fine ");
			sb.append("where id_mdtask = ?");
			sb.append("order by description ");
			st1 = connection.prepareStatement(sb.toString());
			st1.setLong(1, mdtaskId);
			rs1 = st1.executeQuery();
			res = "";
			while (rs1.next()) {
				value = rs1.getString("PUNITIVE_TYPE");
				if (value != null) {
					res += value.trim();
				}
				res += " - ";
				value = rs1.getString("description");
				if (value != null) {
					res += value.trim();
				}
				res += " - ";
				value = rs1.getString("fine_value_Text");
				if (value != null) {
					res += value.trim();
				}
				res += " - ";
				value = rs1.getString("fine_value");
				if (value != null) {
					res += value.trim();
				}
				res += " - ";
				value = rs1.getString("currency");
				if (value != null) {
					res += value.trim();
				}
				res += "\n";
			}
			if (res != "") {
				cell = row.createCell(22);
				cell.setCellValue(res.trim());
				CellStyle cs = wb.createCellStyle();
			    cs.setWrapText(true);
			    cell.setCellStyle(cs);
			    sheet.autoSizeColumn((short)22);
			}
			rs1.close();
			st1.close();
			
			sb = new StringBuffer();
			sb.append("select descr ");
			sb.append("from r_mdtask_otherGoals ");
			sb.append("where id_mdtask = ?");
			sb.append("order by descr ");
			st1 = connection.prepareStatement(sb.toString());
			st1.setLong(1, mdtaskId);
			rs1 = st1.executeQuery();
			res = "";
			while (rs1.next()) {
				value = rs1.getString("descr");
				if (value != null) {
					res += value.trim();
				}
				res += "\n";
			}
			if (res != "") {
				cell = row.createCell(47);
				cell.setCellValue(res.trim());
				CellStyle cs = wb.createCellStyle();
			    cs.setWrapText(true);
			    cell.setCellStyle(cs);
			    sheet.autoSizeColumn((short)47);
			}
			rs1.close();
			st1.close();
			//) Коллегиальный орган, принявший решение
			cell = row.createCell(48);
			cell.setCellValue("решение не принято");
			CellStyle cs = wb.createCellStyle();
			cs.setWrapText(true);
			cell.setCellStyle(cs);
			sheet.autoSizeColumn((short)48);
			//статус
			sb = new StringBuffer();
			sb.append("select a.value_var from mdtask t inner join attributes a on "
					+ "a.id_process=t.id_pup_process inner join variables v on v.id_var=a.id_var "
					+ "where t.id_mdtask=? and v.name_var like 'Статус'");
			st1 = connection.prepareStatement(sb.toString());
			st1.setLong(1, mdtaskId);
			rs1 = st1.executeQuery();
			res = "";
			while (rs1.next()) {
				value = rs1.getString("value_var");
				if (value != null) {
					res += value.trim();
				}
				res += "\n";
			}
			if (res != "") {
				cell = row.createCell(53);
				cell.setCellValue(res.trim());
				cs = wb.createCellStyle();
			    cs.setWrapText(true);
			    cell.setCellStyle(cs);
			    sheet.autoSizeColumn((short)53);
			}
			rs1.close();
			st1.close();
			//ОСУКЗО
			sb = new StringBuffer();
			sb.append("SELECT DISTINCT U.FULLNAME FROM CPS_SECTION_MEMBER SM ");
			sb.append("INNER JOIN CPS_ROLE R ON R.ID_ROLE = SM.ROLE_ID ");
			sb.append("INNER JOIN C_ACT_ID_USER U ON U.ID_USER = SM.USER_ID ");
			sb.append("WHERE R.NAME_ IN ('Работник мидл-офиса (мониторинг)', 'Работник мидл-офиса (выдача)') ");
			sb.append("AND EXISTS (SELECT 1 FROM CPS_SECTION_BINDING SB WHERE SB.SECTION_BINDING_ID = SM.SECTION_BINDING_ID AND SB.MDTASK_ID = ? AND SB.DEAL_CONCLUSION_ID IS NULL) ");
			sb.append("ORDER BY U.FULLNAME ");
			st1 = connection.prepareStatement(sb.toString());
			st1.setLong(1, mdtaskId);
			rs1 = st1.executeQuery();
			res = "";
			while (rs1.next()) {
				value = rs1.getString("FULLNAME");
				if (value != null) {
					res += value.trim();
				}
				res += "\n";
			}
			if (res != "") {
				cell = row.createCell(49);
				cell.setCellValue(res.trim());
				cs = wb.createCellStyle();
				cs.setWrapText(true);
				cell.setCellStyle(cs);
				sheet.autoSizeColumn((short)49);
			}
			rs1.close();
			st1.close();
			//ООКЗО
			sb = new StringBuffer();
			sb.append("SELECT DISTINCT U.FULLNAME FROM CPS_SECTION_MEMBER SM ");
			sb.append("INNER JOIN CPS_ROLE R ON R.ID_ROLE = SM.ROLE_ID ");
			sb.append("INNER JOIN C_ACT_ID_USER U ON U.ID_USER = SM.USER_ID ");
			sb.append("WHERE R.NAME_ IN ('Работник мидл-офиса (КОД)') ");
			sb.append("AND EXISTS (SELECT 1 FROM CPS_SECTION_BINDING SB WHERE SB.SECTION_BINDING_ID = SM.SECTION_BINDING_ID AND SB.MDTASK_ID = ? AND SB.DEAL_CONCLUSION_ID IS NULL) ");
			sb.append("ORDER BY U.FULLNAME ");
			st1 = connection.prepareStatement(sb.toString());
			st1.setLong(1, mdtaskId);
			rs1 = st1.executeQuery();
			res = "";
			while (rs1.next()) {
				value = rs1.getString("FULLNAME");
				if (value != null) {
					res += value.trim();
				}
				res += "\n";
			}
			if (res != "") {
				cell = row.createCell(50);
				cell.setCellValue(res.trim());
				cs = wb.createCellStyle();
			    cs.setWrapText(true);
			    cell.setCellStyle(cs);
			    sheet.autoSizeColumn((short)50);
			}
			rs1.close();
			st1.close();
		}
		rs.close();
		st.close();
		//выплюнуть в сервлет
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition","attachment; filename=report.xls");
		wb.write(response.getOutputStream());
        return null;
    }
	private static String formatDate(Date dt, SimpleDateFormat format) {
		try {
			return format.format(dt);
		} catch (Exception e) {
			return "";
		}
	}
}
