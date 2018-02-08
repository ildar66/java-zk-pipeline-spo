package ru.masterdm.flexworkflow.logic.ejb;

import javax.ejb.Remote;

import ru.masterdm.flexworkflow.core.logic.IFlexWorkflowIntegration;

/**
 * {@link Remote Удаленный} интерфейс интеграции
 * 
 * @author imatushak@masterdm.ru
 */
@Remote
public interface IFlexWorkflowIntegrationRemote extends IFlexWorkflowIntegration {
}
