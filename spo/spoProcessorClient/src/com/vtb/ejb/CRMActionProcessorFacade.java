package com.vtb.ejb;

import javax.ejb.Remote;

import com.vtb.model.CRMActionProcessor;

/**
 * Remote интерфейс
 * @author Какунин Константин Юрьевич
 * создано для jira VTBSPO-252
 */
@Remote
public interface CRMActionProcessorFacade extends CRMActionProcessor {

}
