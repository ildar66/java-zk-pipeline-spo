package ru.masterdm.flexworkflow.core.logic;

import ru.masterdm.flexworkflow.integration.list.EFlexWorkflowSDOType;
import ru.masterdm.integration.CCStatus;
import ru.masterdm.spo.integration.FilialTask;
import ru.masterdm.spo.integration.FilialTaskList;
import ru.masterdm.spo.integration.FilialTaskListFilter;

import commonj.sdo.DataObject;

/**
 * Интерфейс интеграции
 * 
 * @author imatushak@masterdm.ru
 */
public interface IFlexWorkflowIntegration {

	/**
	 * Возвращает список заявок по БП для филиалов.
	 * @param input
	 * @return
	 */
	FilialTaskList getFilialTaskList(FilialTaskListFilter filter);
	
	/**
	 * Возвращает id пользователя, который работает с заявкой. Если заявка ни у кого не в работе, то верну nnull.
	 */
	Long whoWorkWithTask(Long mdtaskid);
	/**
	 * взять заявку в обработку
	 * @param mdtaskid - id заявки
	 */
	void acceptTask(Long mdtaskid);
	/**
	 * Закончить обработку заявки и разблокировать её.
	 */
	void deAcceptTask(Long mdtaskid);
	
	
	/**
	 * Завершить заявку
	 */
	void closeProcess(Long taskId);
	/**
	 * Возвращает {@link DataObject объект} типа
	 * {@link EFlexWorkflowSDOType#TASK_4_RATING} либо, в случае возникновения
	 * ошибки, {@link DataObject объект} типа {@link EFlexWorkflowSDOType#FAULT}
	 * 
	 * @param input
	 *            {@link DataObject объект} типа
	 *            {@link EFlexWorkflowSDOType#MD_TASK_ID_FILTER}
	 * @return {@link DataObject объект} типа
	 *         {@link EFlexWorkflowSDOType#TASK_4_RATING} либо
	 *         {@link DataObject объект} типа {@link EFlexWorkflowSDOType#FAULT}
	 *         Может вернуть <code><b>null</b></code> в случае непредвиденной
	 *         ошибки
	 */
	DataObject getOpportunityInfo(DataObject input);

	/**
	 * Возвращает {@link DataObject объект} типа
	 * {@link EFlexWorkflowSDOType#TASK_4_RATING_LIST} либо, в случае
	 * возникновения ошибки, {@link DataObject объект} типа
	 * {@link EFlexWorkflowSDOType#FAULT}
	 * 
	 * @param input
	 *            {@link DataObject объект} типа
	 *            {@link EFlexWorkflowSDOType#ORGANIZATION_ID_FILTER}
	 * @return {@link DataObject объект} типа
	 *         {@link EFlexWorkflowSDOType#TASK_4_RATING_LIST} либо
	 *         {@link DataObject объект} типа {@link EFlexWorkflowSDOType#FAULT}
	 *         Может вернуть <code><b>null</b></code> в случае непредвиденной
	 *         ошибки
	 */
	DataObject getListOpportunity(DataObject input);

	/**
	 * Возвращает {@link DataObject объект} типа
	 * {@link EFlexWorkflowSDOType#VOID} либо, в случае возникновения ошибки,
	 * {@link DataObject объект} типа {@link EFlexWorkflowSDOType#FAULT}
	 * 
	 * @param input
	 *            {@link DataObject объект} типа
	 *            {@link EFlexWorkflowSDOType#CC_STATUS}
	 * @return {@link DataObject объект} типа {@link EFlexWorkflowSDOType#VOID}
	 *         либо {@link DataObject объект} типа
	 *         {@link EFlexWorkflowSDOType#FAULT} Может вернуть
	 *         <code><b>null</b></code> в случае непредвиденной ошибки
	 */
	DataObject statusNotification(DataObject input);
	
	/**
     * Статус возврата в СПО из СКК. Без SDO, читаем напрямую из DTO-объекта
     * Возвращает {@link EFlexWorkflowSDOType#VOID} 
     * либо, в случае возникновения ошибки,
     * {@link DataObject объект} типа {@link EFlexWorkflowSDOType#FAULT}
     * @param status {@link CCStatus объект} типа
     * @return {@link EFlexWorkflowSDOType#VOID}
     *         либо {@link EFlexWorkflowSDOType#FAULT} 
     * Может вернуть <code><b>null</b></code> в случае непредвиденной ошибки
     */
    EFlexWorkflowSDOType statusNotificationDTO(CCStatus status);

}