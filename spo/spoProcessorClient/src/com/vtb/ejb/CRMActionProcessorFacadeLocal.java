package com.vtb.ejb;

import javax.ejb.Local;

import com.vtb.model.CRMActionProcessor;

/**
 * Local интерфейс
 * @author Какунин Константин Юрьевич
 * создано для jira VTBSPO-252
 */
@Local
public interface CRMActionProcessorFacadeLocal extends CRMActionProcessor {}