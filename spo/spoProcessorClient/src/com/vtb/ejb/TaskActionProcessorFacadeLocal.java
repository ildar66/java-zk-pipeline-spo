package com.vtb.ejb;

import javax.ejb.Local;

import com.vtb.model.TaskActionProcessor;

/**
 * Local interface for Enterprise Bean: TaskActionProcessorFacade
 */
@Local
public interface TaskActionProcessorFacadeLocal extends TaskActionProcessor {}
