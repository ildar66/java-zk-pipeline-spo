package org.uit.director.action;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.contexts.WPC;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.db.ejb.DBFlexWorkflowCommon;
import org.uit.director.managers.ControlActionsManager;
import org.uit.director.managers.ControlActionsManager.ControlActionsForm;

/**
 * @version 1.0
 * @author
 */
public class ControlAction extends Action

{

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String target = "errorPage";

		String dateLeft = request.getParameter("leftDate");
		String dateRight = request.getParameter("rightDate");
		String idUser = request.getParameter("idUser");
		String ipAddress = request.getParameter("ipAddress");
		String typeAction = request.getParameter("typeAction");
		String idTransaction = request.getParameter("idTransaction");
		String table = request.getParameter("table");
		
		WorkflowSessionContext wsc = (WorkflowSessionContext) request
		.getSession().getAttribute("workflowContext");
		
		if (table != null) {
			String key = request.getParameter("key");
			String idxTable = request.getParameter("idxTable");			
			String operation = request.getParameter("operation");
			executeRollBack(wsc, table, key, operation, Integer.parseInt(idxTable));
			
		}

		

		if (wsc != null) {

			ControlActionsManager cam = wsc.getControlActionsManager();
			if (cam == null)
				cam = new ControlActionsManager();

			ControlActionsForm formCam = cam.getForm();
			formCam.setDateLeft(dateLeft == null ? "" : dateLeft);
			formCam.setDateRight(dateRight == null ? "" : dateRight);
			formCam.setIdUser(idUser == null ? null : Long.valueOf(idUser));
			formCam.setIpAddress(ipAddress == null ? "" : ipAddress);
			formCam.setTypeAction(typeAction == null ? "-" : typeAction);

			try {

				DBFlexWorkflowCommon dbflexWF = wsc.getDbManager()
						.getDbFlexDirector();

				boolean isActions = idTransaction != null
						&& !idTransaction.equals("");

				if (isActions) {
					getActions(idTransaction, cam, dbflexWF);
				} else {
					getTransactions(cam, dbflexWF);
				}

				wsc.setControlActionsManager(cam);

				target = "control";

			} catch (Exception e) {
				e.printStackTrace();
				wsc.setErrorMessage(e.getMessage());
				target = "errorPage";
			}
		}

		return mapping.findForward(target);

	}

	private void executeRollBack(WorkflowSessionContext wsc, String table, String key, String operation, int idxTable) {
		
		String keys[];
		StringTokenizer tok = new StringTokenizer(key, ",");
		keys = new String[tok.countTokens()];
		int i = 0;
		while (tok.hasMoreTokens()) {
			keys[i++] = tok.nextToken();			
		}
		
		List tableList = null;
		ControlActionsManager cam = wsc.getControlActionsManager();
		
		if (table.equals("ATTRIBUTES")) tableList = cam.getAttributesHistory();		
		Map map = (Map) tableList.get(idxTable);
		
		
		
		
		
		
		
		
	}

	private void getTransactions(ControlActionsManager cam,
			DBFlexWorkflowCommon dbflexWF) throws SQLException, ParseException, RemoteException {

		ControlActionsForm fc = cam.getForm();

		boolean isFullDates = true;

		String dateLeft = new String(fc.getDateLeft());
		String dateRight = new String(fc.getDateRight());

		if (dateLeft.equals("") || dateRight.equals("")) {
			isFullDates = false;
		} else {		
			
			dateLeft = WPC.getInstance().formatDateToDateTimeDBLeft(dateLeft);			
			dateRight = WPC.getInstance().formatDateToDateTimeDBRight(dateRight); 
				

		}

		boolean isFullUser = !fc.getIdUser().equals("");
		boolean isFullIP = !fc.getIpAddress().equals("");
		boolean isFullType = !fc.getTypeAction().equals("-");

		boolean isOneData = isFullDates || isFullUser || isFullIP || isFullType;

		StringBuffer sql = new StringBuffer();
		sql.append("select * from transaction t");
		if (isOneData)
			sql.append(" where ");

		boolean firstOperand = true;

		if (isFullDates) {
			sql.append("t.date_transaction between '").append(dateLeft).append(
					"' and '").append(dateRight).append("'");
			firstOperand = false;
		}

		if (isFullUser) {

			if (!firstOperand) {
				sql.append(" and ");
			} else {
				firstOperand = false;
			}
			sql.append(" t.id_user='").append(fc.getIdUser())
					.append("'");
			firstOperand = false;
		}

		if (isFullIP) {

			if (!firstOperand) {
				sql.append(" and ");
			} else {
				firstOperand = false;
			}
			sql.append(" t.ip_address='").append(
					fc.getIpAddress().toLowerCase()).append("'");
		}

		if (isFullType) {
			if (!firstOperand) {
				sql.append(" and ");
			}
			sql.append(" t.name_action='").append(fc.getTypeAction()).append(
					"'");
		}

		List res = dbflexWF.execQuery(sql.toString());
		cam.setTransactionsList(res);
		cam.setTransactionsView(true);
		cam.setActionsView(false);
	}

	private void getActions(String idTransaction, ControlActionsManager cam,
			DBFlexWorkflowCommon dbflexWF) throws SQLException, RemoteException {

		StringBuffer sb = new StringBuffer();

		sb
				.append("with h as")
				.append(
						"( select TYPE_SQL, ID_USER, MAIL_USER, IP_USER from USERS_HISTORY where ID_TRANSACTION=")
				.append(idTransaction)
				.append(
						") select TYPE_SQL, ID_USER, MAIL_USER, IP_USER from h union all select distinct 0 TYPE_SQL, u.ID_USER, u.MAIL_USER, u.IP_USER ")
				.append("from h join DB2ADMIN.USERS u on u.ID_USER=h.ID_USER");

		cam.setUsersHistory(dbflexWF.execQuery(sb.toString()));

		sb = new StringBuffer();

		sb
				.append("with h as(")
				.append("select  TYPE_SQL,ID,ID_VAR,ID_PROCESS,VALUE_VAR ")
				.append("from ATTRIBUTES_HISTORY ")
				.append("where ID_TRANSACTION=")
				.append(idTransaction)
				.append(") select TYPE_SQL,ID,ID_VAR,ID_PROCESS,VALUE_VAR ")
				.append("from h ")
				.append("union all ")
				.append(
						"select distinct 0 TYPE_SQL, U.ID,U.ID_VAR,U.ID_PROCESS,U.VALUE_VAR ")
				.append("from h").append(
						" join DB2ADMIN.ATTRIBUTES u on u.ID=h.ID ").append(
						"order by id, TYPE_SQL");

		cam.setAttributesHistory(dbflexWF.execQuery(sb.toString()));

		sb = new StringBuffer();

		sb.append("with h as(").append("select  TYPE_SQL,ID_GROUP,NAME_GROUP ")
				.append("from DB2ADMIN.GROUPS_HISTORY ").append(
						"where ID_TRANSACTION=").append(idTransaction).append(
						") select TYPE_SQL,ID_GROUP,NAME_GROUP ").append(
						"from h ").append("union all ").append(
						"select distinct 0 TYPE_SQL, u.ID_GROUP,u.NAME_GROUP ")
				.append("from h").append(
						" join DB2ADMIN.GROUPS u on u.ID_GROUP=h.ID_GROUP ")
				.append("order by ID_GROUP, TYPE_SQL");

		cam.setGroupesHistory(dbflexWF.execQuery(sb.toString()));

		sb = new StringBuffer();

		sb
				.append("with h as(")
				.append(
						"select  TYPE_SQL,ID_MESSAGE,ID_PROCESS,DATE_MESSAGE,ID_STAGE_FROM,ID_STAGE_TO,USER_NAME,MASSAGE,ID_TYPE_PROCESS_TO,")
				.append("ID_ROLE_TO,ID_USER_TO ")
				.append("from DB2ADMIN.MESSAGES_HISTORY ")
				.append("where ID_TRANSACTION=")
				.append(idTransaction)
				.append(
						") select TYPE_SQL,ID_MESSAGE,ID_PROCESS,DATE_MESSAGE,ID_STAGE_FROM,ID_STAGE_TO,USER_NAME,MASSAGE,ID_TYPE_PROCESS_TO,")
				.append("ID_ROLE_TO,ID_USER_TO ")
				.append("from h ")
				.append("union all ")
				.append(
						"select distinct 0 TYPE_SQL, u.ID_MESSAGE,u.ID_PROCESS,u.DATE_MESSAGE,u.ID_STAGE_FROM,u.ID_STAGE_TO,u.USER_NAME,varchar(u.MASSAGE),u.ID_TYPE_PROCESS_TO,")
				.append("u.ID_ROLE_TO,u.ID_USER_TO ")
				.append("from h")
				.append(
						" join DB2ADMIN.MESSAGES u on u.ID_MESSAGE=h.ID_MESSAGE ")
				.append("order by ID_MESSAGE, TYPE_SQL");

		cam.setMessagesHistory(dbflexWF.execQuery(sb.toString()));

		sb = new StringBuffer();

		sb
				.append("with h as(")
				.append(
						"select  TYPE_SQL,ID_TYPE_PROCESS,TYPE_PARAMETER,VALUE_VAR ")
				.append("from DB2ADMIN.PROCESS_PARAMETERS_HISTORY ")
				.append("where ID_TRANSACTION=")
				.append(idTransaction)
				.append(
						") select TYPE_SQL,ID_TYPE_PROCESS,TYPE_PARAMETER,VALUE_VAR ")
				.append("from h ")
				.append("union all ")
				.append(
						"select 0 TYPE_SQL,u.ID_TYPE_PROCESS,u.TYPE_PARAMETER,u.VALUE_VAR ")
				.append("from h")
				.append(
						" join DB2ADMIN.PROCESS_PARAMETERS u on u.ID_TYPE_PROCESS=h.ID_TYPE_PROCESS and u.TYPE_PARAMETER=h.TYPE_PARAMETER ")
				.append("order by ID_TYPE_PROCESS, TYPE_SQL");

		cam.setProcessParametersHistory(dbflexWF.execQuery(sb.toString()));

		sb = new StringBuffer();

		sb
				.append("with h as(")
				.append(
						"select  TYPE_SQL,ID_PROCESS,DATEOFCOMMING,DATEOFCOMPLETION,ID_STATUS,ID_TYPE_PROCESS ")
				.append("from DB2ADMIN.PROCESSES_HISTORY ")
				.append("where ID_TRANSACTION=")
				.append(idTransaction)
				.append(
						") select TYPE_SQL,ID_PROCESS,DATEOFCOMMING,DATEOFCOMPLETION,ID_STATUS,ID_TYPE_PROCESS ")
				.append("from h ")
				.append("union all ")
				.append(
						"select distinct 0 TYPE_SQL,u.ID_PROCESS,u.DATEOFCOMMING,u.DATEOFCOMPLETION,u.ID_STATUS,u.ID_TYPE_PROCESS ")
				.append("from h")
				.append(
						" join DB2ADMIN.PROCESSES u on u.ID_PROCESS=h.ID_PROCESS ")
				.append("order by ID_PROCESS, TYPE_SQL");

		cam.setProcessHistory(dbflexWF.execQuery(sb.toString()));

		sb = new StringBuffer();

		sb
				.append("with h as(")
				.append(
						"select  TYPE_SQL,ID_ROLE,NAME_ROLE,ID_TYPE_PROCESS,ACTIVE ")
				.append("from DB2ADMIN.ROLES_HISTORY ")
				.append("where ID_TRANSACTION=")
				.append(idTransaction)
				.append(
						") select TYPE_SQL,ID_ROLE,NAME_ROLE,ID_TYPE_PROCESS,ACTIVE ")
				.append("from h ")
				.append("union all ")
				.append(
						"select distinct 0 TYPE_SQL,u.ID_ROLE,u.NAME_ROLE,u.ID_TYPE_PROCESS,u.ACTIVE ")
				.append("from h").append(
						" join DB2ADMIN.ROLES u on u.ID_ROLE=h.ID_ROLE ")
				.append("order by ID_ROLE, TYPE_SQL");

		cam.setRolesHistory(dbflexWF.execQuery(sb.toString()));

		sb = new StringBuffer();

		sb
				.append("with h as(")
				.append(
						"select  TYPE_SQL,ID_STAGE,DESCRIPTION_STAGE,ORDER_VIEW,ORDER_EXPAND,ORDER_EDIT,LIMIT_DAY,TYPE_LIMIT_DAY,")
				.append(
						"ATTENTION_DAY,ACTION_CLASS_ON_ENTRY,ACTION_CLASS_ON_EXIT,ID_TYPE_PROCESS,ACTIVE ")
				.append("from DB2ADMIN.STAGES_HISTORY ")
				.append("where ID_TRANSACTION=")
				.append(idTransaction)
				.append(
						") select TYPE_SQL,ID_STAGE,DESCRIPTION_STAGE,ORDER_VIEW,ORDER_EXPAND,ORDER_EDIT,LIMIT_DAY,TYPE_LIMIT_DAY,")
				.append(
						"ATTENTION_DAY,ACTION_CLASS_ON_ENTRY,ACTION_CLASS_ON_EXIT,ID_TYPE_PROCESS,ACTIVE ")
				.append("from h ")
				.append("union all ")
				.append(
						"select 0 TYPE_SQL,u.ID_STAGE,u.DESCRIPTION_STAGE,u.ORDER_VIEW,u.ORDER_EXPAND,u.ORDER_EDIT,u.LIMIT_DAY,")
				.append("u.TYPE_LIMIT_DAY,")
				.append(
						"u.ATTENTION_DAY,u.ACTION_CLASS_ON_ENTRY,u.ACTION_CLASS_ON_EXIT,u.ID_TYPE_PROCESS,u.ACTIVE ")
				.append("from h").append(
						" join DB2ADMIN.STAGES u on u.ID_STAGE=h.ID_STAGE ")
				.append("order by ID_STAGE, TYPE_SQL");

		cam.setStagesHistory(dbflexWF.execQuery(sb.toString()));

		sb = new StringBuffer();

		sb
				.append("with h as(")
				.append("select TYPE_SQL,ID_ROLE,ID_STAGE ")
				.append("from DB2ADMIN.STAGES_IN_ROLE_HISTORY ")
				.append("where ID_TRANSACTION=")
				.append(idTransaction)
				.append(") select TYPE_SQL,ID_ROLE,ID_STAGE ")
				.append("from h ")
				.append("union all ")
				.append("select distinct 0 TYPE_SQL,u.ID_ROLE,u.ID_STAGE ")
				.append("from h")
				.append(
						" join DB2ADMIN.STAGES_IN_ROLE u on u.ID_ROLE=h.ID_ROLE and u.ID_STAGE=h.ID_STAGE ")
				.append("order by ID_ROLE, ID_STAGE, TYPE_SQL");

		cam.setStagesInRoleHistory(dbflexWF.execQuery(sb.toString()));

		sb = new StringBuffer();

		sb
				.append("with h as(")
				.append(
						"select TYPE_SQL,ID_TASK,ID_TYPE_PROCESS,ID_PROCESS,ID_STAGE_TO,ID_STAGE_FROM,TASK_STATUS,")
				.append(
						"DATEOFCOMMING,DATEOFTAKING,DATEOFCOMPLATION,ID_USER,TYPE_COMPLATION ")
				.append("from DB2ADMIN.TASKS_HISTORY ")
				.append("where ID_TRANSACTION=")
				.append(idTransaction)
				.append(
						") select TYPE_SQL,ID_TASK,ID_TYPE_PROCESS,ID_PROCESS,ID_STAGE_TO,ID_STAGE_FROM,TASK_STATUS,")
				.append(
						"DATEOFCOMMING,DATEOFTAKING,DATEOFCOMPLATION,ID_USER,TYPE_COMPLATION ")
				.append("from h ")
				.append("union all ")
				.append(
						"select distinct 0 TYPE_SQL,u.ID_TASK,u.ID_TYPE_PROCESS,u.ID_PROCESS,u.ID_STAGE_TO,u.ID_STAGE_FROM,u.TASK_STATUS,")
				.append(
						"u.DATEOFCOMMING,u.DATEOFTAKING,u.DATEOFCOMPLATION,u.ID_USER,u.TYPE_COMPLATION ")
				.append("from h").append(
						" join DB2ADMIN.TASKS u on u.ID_TASK=h.ID_TASK ")
				.append("order by ID_TASK,TYPE_SQL");

		cam.setTasksHistory(dbflexWF.execQuery(sb.toString()));

		sb = new StringBuffer();
		sb
				.append("with h as(")
				.append(
						"select TYPE_SQL,ID_TYPE_PROCESS,DESCRIPTION_PROCESS,LIMIT_DAY,SCHEMA_VAR,ID_GROUP,SCHEMA_IMAGE  ")
				.append("from DB2ADMIN.TYPE_PROCESS_HISTORY ")
				.append("where ID_TRANSACTION=")
				.append(idTransaction)
				.append(
						") select TYPE_SQL,ID_TYPE_PROCESS,DESCRIPTION_PROCESS,LIMIT_DAY,SCHEMA_VAR,ID_GROUP,SCHEMA_IMAGE ")
				.append("from h ")
				.append("union all ")
				.append(
						"select 0 TYPE_SQL,u.ID_TYPE_PROCESS,u.DESCRIPTION_PROCESS,u.LIMIT_DAY,u.SCHEMA_VAR,u.ID_GROUP,u.SCHEMA_IMAGE ")
				.append("from h")
				.append(
						" join DB2ADMIN.TYPE_PROCESS u on u.ID_TYPE_PROCESS=h.ID_TYPE_PROCESS ")
				.append("order by ID_TYPE_PROCESS, TYPE_SQL");

		cam.setTypeProcessHistory(dbflexWF.execQuery(sb.toString()));

		sb = new StringBuffer();
		sb
				.append("with h as(")
				.append("select TYPE_SQL,ID_ROLE,ID_USER ")
				.append("from DB2ADMIN.USER_IN_ROLE_HISTORY ")
				.append("where ID_TRANSACTION=")
				.append(idTransaction)
				.append(") select TYPE_SQL,ID_ROLE,ID_USER ")
				.append("from h ")
				.append("union all ")
				.append("select distinct 0 TYPE_SQL,u.ID_ROLE,u.ID_USER ")
				.append("from h")
				.append(
						" join DB2ADMIN.USER_IN_ROLE u on u.ID_user=h.ID_USER and u.ID_ROLE=h.ID_ROLE ")
				.append("order by ID_USER, ID_ROLE, TYPE_SQL");

		cam.setUserInRoleHistory(dbflexWF.execQuery(sb.toString()));
		
		sb = new StringBuffer();
		sb
				.append("with h as(")
				.append("select TYPE_SQL,ID_VAR,NAME_VAR,TYPE_VAR,ADDITION_VAR,ID_TYPE_PROCESS,VALUE_BY_DEFAULT,IS_ID,IS_MAIN,ACTIVE ")
				.append("from DB2ADMIN.VARIABLES_HISTORY ")
				.append("where ID_TRANSACTION=")
				.append(idTransaction)
				.append(") select TYPE_SQL,ID_VAR,NAME_VAR,TYPE_VAR,ADDITION_VAR,ID_TYPE_PROCESS,VALUE_BY_DEFAULT,IS_ID,IS_MAIN,ACTIVE ")
				.append("from h ")
				.append("union all ")
				.append("select distinct 0 TYPE_SQL,u.ID_VAR,u.NAME_VAR,u.TYPE_VAR,u.ADDITION_VAR,u.ID_TYPE_PROCESS,u.VALUE_BY_DEFAULT,u.IS_ID,u.IS_MAIN,u.ACTIVE ")
				.append("from h")
				.append(
						" join DB2ADMIN.VARIABLES u on u.ID_VAR=h.ID_VAR ")
				.append("order by ID_VAR,TYPE_SQL");

		cam.setVariablesHistory(dbflexWF.execQuery(sb.toString()));
		
		

		cam.setTransactionsView(false);
		cam.setActionsView(true);

	}
}
