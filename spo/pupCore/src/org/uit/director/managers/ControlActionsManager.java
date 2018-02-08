/*
 * Created on 12.02.2008
 * 
 */
package org.uit.director.managers;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ControlActionsManager {

	public class ControlActionsForm {

		String dateLeft = "";

		String dateRight = "";

		Long idUser = new Long(0);

		String ipAddress = "";

		String typeAction = "-";

		/**
		 * @return the dateLeft
		 */
		public String getDateLeft() {
			return dateLeft;
		}

		/**
		 * @return the dateRight
		 */
		public String getDateRight() {
			return dateRight;
		}

		/**
		 * @return the idUser
		 */
		public Long getIdUser() {
			return idUser;
		}

		/**
		 * @return the ipAddress
		 */
		public String getIpAddress() {
			return ipAddress;
		}

		/**
		 * @return the typeAction
		 */
		public String getTypeAction() {
			return typeAction;
		}

		/**
		 * @param dateLeft
		 *            the dateLeft to set
		 */
		public void setDateLeft(String dateLeft) {
			this.dateLeft = dateLeft;
		}

		/**
		 * @param dateRight
		 *            the dateRight to set
		 */
		public void setDateRight(String dateRight) {
			this.dateRight = dateRight;
		}

		/**
		 * @param idUser
		 *            the idUser to set
		 */
		public void setIdUser(Long idUser) {
			this.idUser = idUser;
		}

		/**
		 * @param ipAddress
		 *            the ipAddress to set
		 */
		public void setIpAddress(String ipAddress) {
			this.ipAddress = ipAddress;
		}

		/**
		 * @param typeAction
		 *            the typeAction to set
		 */
		public void setTypeAction(String typeAction) {
			this.typeAction = typeAction;
		}

	}

	List transactionsList;

	ControlActionsForm form;

	List attributesHistory;

	List groupesHistory;

	List messagesHistory;

	List processParametersHistory;

	List processHistory;

	List rolesHistory;

	List stagesHistory;

	List stagesInRoleHistory;

	List tasksHistory;

	List typeProcessHistory;

	List userInRoleHistory;

	List usersHistory;

	List variablesHistory;

	boolean isTransactionsView;

	boolean isActionsView;

	/**
	 * 
	 */
	public ControlActionsManager() {
		form = new ControlActionsForm();
	}

	/**
	 * @return the form
	 */
	public ControlActionsForm getForm() {
		return form;
	}

	/**
	 * @return the transactionsList
	 */
	public List getTransactionsList() {
		return transactionsList;
	}

	/**
	 * @param form
	 *            the form to set
	 */
	public void setForm(ControlActionsForm form) {
		this.form = form;
	}

	/**
	 * @param transactionsList
	 *            the transactionsList to set
	 */
	public void setTransactionsList(List transactionsList) {
		this.transactionsList = transactionsList;
	}

	/**
	 * @return the attributesHistory
	 */
	public List getAttributesHistory() {
		return attributesHistory;
	}

	/**
	 * @return the groupesHistory
	 */
	public List getGroupesHistory() {
		return groupesHistory;
	}

	/**
	 * @return the messagesHistory
	 */
	public List getMessagesHistory() {
		return messagesHistory;
	}

	/**
	 * @return the processHistory
	 */
	public List getProcessHistory() {
		return processHistory;
	}

	/**
	 * @return the processParametersHistory
	 */
	public List getProcessParametersHistory() {
		return processParametersHistory;
	}

	/**
	 * @return the rolesHistory
	 */
	public List getRolesHistory() {
		return rolesHistory;
	}

	/**
	 * @return the stagesHistory
	 */
	public List getStagesHistory() {
		return stagesHistory;
	}

	/**
	 * @return the stagesInRoleHistory
	 */
	public List getStagesInRoleHistory() {
		return stagesInRoleHistory;
	}

	/**
	 * @return the typeProcessHistory
	 */
	public List getTypeProcessHistory() {
		return typeProcessHistory;
	}

	/**
	 * @return the userInRoleHistory
	 */
	public List getUserInRoleHistory() {
		return userInRoleHistory;
	}

	/**
	 * @return the usersHistory
	 */
	public List getUsersHistory() {
		return usersHistory;
	}

	/**
	 * @return the variablesHistory
	 */
	public List getVariablesHistory() {
		return variablesHistory;
	}

	/**
	 * @param attributesHistory
	 *            the attributesHistory to set
	 */
	public void setAttributesHistory(List attributesHistory) {
		this.attributesHistory = attributesHistory;
	}

	/**
	 * @param groupesHistory
	 *            the groupesHistory to set
	 */
	public void setGroupesHistory(List groupesHistory) {
		this.groupesHistory = groupesHistory;
	}

	/**
	 * @param messagesHistory
	 *            the messagesHistory to set
	 */
	public void setMessagesHistory(List messagesHistory) {
		this.messagesHistory = messagesHistory;
	}

	/**
	 * @param processHistory
	 *            the processHistory to set
	 */
	public void setProcessHistory(List processHistory) {
		this.processHistory = processHistory;
	}

	/**
	 * @param processParametersHistory
	 *            the processParametersHistory to set
	 */
	public void setProcessParametersHistory(List processParametersHistory) {
		this.processParametersHistory = processParametersHistory;
	}

	/**
	 * @param rolesHistory
	 *            the rolesHistory to set
	 */
	public void setRolesHistory(List rolesHistory) {
		this.rolesHistory = rolesHistory;
	}

	/**
	 * @param stagesHistory
	 *            the stagesHistory to set
	 */
	public void setStagesHistory(List stagesHistory) {
		this.stagesHistory = stagesHistory;
	}

	/**
	 * @param stagesInRoleHistory
	 *            the stagesInRoleHistory to set
	 */
	public void setStagesInRoleHistory(List stagesInRoleHistory) {
		this.stagesInRoleHistory = stagesInRoleHistory;
	}

	/**
	 * @param typeProcessHistory
	 *            the typeProcessHistory to set
	 */
	public void setTypeProcessHistory(List typeProcessHistory) {
		this.typeProcessHistory = typeProcessHistory;
	}

	/**
	 * @param userInRoleHistory
	 *            the userInRoleHistory to set
	 */
	public void setUserInRoleHistory(List userInRoleHistory) {
		this.userInRoleHistory = userInRoleHistory;
	}

	/**
	 * @param usersHistory
	 *            the usersHistory to set
	 */
	public void setUsersHistory(List usersHistory) {
		this.usersHistory = usersHistory;
	}

	/**
	 * @param variablesHistory
	 *            the variablesHistory to set
	 */
	public void setVariablesHistory(List variablesHistory) {
		this.variablesHistory = variablesHistory;
	}

	/**
	 * @return the isActionsView
	 */
	public boolean isActionsView() {
		return isActionsView;
	}

	/**
	 * @return the isTransactionsView
	 */
	public boolean isTransactionsView() {
		return isTransactionsView;
	}

	/**
	 * @param isActionsView
	 *            the isActionsView to set
	 */
	public void setActionsView(boolean isActionsView) {
		this.isActionsView = isActionsView;
	}

	/**
	 * @param isTransactionsView
	 *            the isTransactionsView to set
	 */
	public void setTransactionsView(boolean isTransactionsView) {
		this.isTransactionsView = isTransactionsView;
	}

	/**
	 * @return the tasksHistory
	 */
	public List getTasksHistory() {
		return tasksHistory;
	}

	/**
	 * @param tasksHistory
	 *            the tasksHistory to set
	 */
	public void setTasksHistory(List tasksHistory) {
		this.tasksHistory = tasksHistory;
	}

	public String getHistoryTableHTML(String keyTable, String nameTable,
			List table) {
		StringBuffer sb = new StringBuffer();

		if (table != null && table.size() > 0) {
			sb.append("<table><caption align=left>Изменения таблицы ").append(
					nameTable).append(
					"</caption><tr><th colspan=\"2\" >Действие</th>");
			Iterator itHead = ((Map) table.get(0)).keySet().iterator();
			while (itHead.hasNext()) {
				String key = (String) itHead.next();
				if (!key.equals("TYPE_SQL")) {
					sb.append("<th>").append(key).append("</th>");
				}
			}
			sb.append("<th>Откат</th></tr>");

			boolean isWithNowDel = false;
			boolean isWithNowAdd = false;
			boolean isWithNowUpdate = false;

			for (int i = 0; i < table.size(); i++) {

				boolean isInsert = false;
				boolean isUpdateBefore = false;
				boolean isUpdateAfter = false;
				boolean isDelete = false;
				sb.append("<tr>");
				Map m = (Map) table.get(i);
				String typeSql = (String) m.get("TYPE_SQL");

				if (typeSql.equals("0")) {
					try {
						Map tmp = (Map) table.get(i + 1);
						if (tmp != null) {
							String tsql = ((String) tmp.get("TYPE_SQL"));
							if (tsql.equals("0")) {
								continue;
							}

							if (tsql.equals("1")) {
								sb.append("<td rowspan=\"2\">Добавление</td>");
								isWithNowAdd = true;
							}

							if (tsql.equals("2")) {

								boolean fl = true;
								isWithNowUpdate = true;
								try {
									Map tmp1 = (Map) table.get(i + 2);
									if (tmp1 != null) {
										if (((String) tmp1.get("TYPE_SQL"))
												.equals("3")) {
											sb
													.append("<td rowspan=\"3\">Изменение</td>");
											fl = false;
										}
									}
								} catch (Exception e) {

								}

								if (fl) {
									sb
											.append("<td rowspan=\"2\">Изменение</td>");
								}
							}

							if (tsql.equals("4")) {
								sb.append("<td rowspan=\"2\">Удаление</td>");
								isWithNowDel = true;
							}

						}
					} catch (Exception e) {

					}

					sb.append("<td>Сейчас</td>");

				}

				if (typeSql.equals("1")) {
					isInsert = true;
					if (isWithNowAdd) {
						sb.append("<td>После</td>");
					} else {
						sb.append("<td colspan=\"2\">Добавление</td>");
					}

				}

				if (typeSql.equals("2")) {
					isUpdateBefore = true;

					if (!isWithNowUpdate) {
						sb.append("<td rowspan=\"2\">Изменение</td>");
					}
					sb.append("<td>До</td>");
				}

				if (typeSql.equals("3")) {
					isUpdateAfter = true;
					sb.append("<td>После</td>");
				}

				if (typeSql.equals("4")) {
					isDelete = true;
					if (isWithNowDel) {
						sb.append("<td>До</td>");
						isWithNowDel = false;
					} else {
						sb.append("<td colspan=\"2\">Удаление</td>");
					}
				}

				Iterator it = m.keySet().iterator();
				while (it.hasNext()) {
					String key = (String) it.next();
					if (!key.equals("TYPE_SQL")) {
						String value = (String) m.get(key);
						sb.append("<td>").append(value).append("</td>");
					}

				}

				sb.append("<td>");

				sb.append("<a href=\"control.action.do?table=").append(nameTable)
						.append("&key=").append(keyTable).append("&idxTable=")
						.append(i).append("&operation=");
				if (isUpdateAfter || isUpdateBefore) {
					sb.append("update\">Обновить");
				} else if (isDelete) {
					sb.append("insert\">Восстановить");
				} else if (isInsert) {
					sb.append("delete\">Удалить");
				}

				sb.append("</a></td>");

				sb.append("</tr>");

			}

			sb.append("</table><br>");

		}
		return sb.toString();

	}

}
