package ru.masterdm.integration;

import javax.ejb.Remote;

/**
 * SPO service for Credit committee remote interface.
 * @author Andrey Pavlenko
 */
@Remote
public interface SPOCCIntegrationFacadeRemote {

    /**
     * changed status notification.
     * @param status -  {@link CCStatus} Credit committee mdtask status
     * @param mdtaskid - origonal mdtaskid (not clone)
     * @throws Exception
     */
    void statusNotification(CCStatus status, Long mdtaskid) throws Exception;

    /**
     * unit test method.
     */
    CCStatus getStatus(Long mdtaskid) throws Exception;
}
