package ru.masterdm.flexworkflow.logic.ejb;

import javax.ejb.Local;

import ru.masterdm.flexworkflow.core.logic.IFlexWorkflowIntegration;

/**
 * {@link Local локальный} интерфейс интеграции
 * 
 * @author imatushak@masterdm.ru
 */
@Local
public interface IFlexWorkflowIntegrationLocal extends IFlexWorkflowIntegration {
}
