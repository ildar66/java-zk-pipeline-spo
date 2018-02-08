package com.vtb.model;

import com.vtb.domain.SpoHistory;
import com.vtb.exception.DuplicateKeyException;
import com.vtb.exception.MappingException;
import com.vtb.exception.NoSuchObjectException;

/**
 * Интерфейс процессора для исполнения заявок 
 * @author Какунин Константин Юрьевич
 * создано для bug (VTBSPO-252)
 */
public interface CRMActionProcessor {

    public void insertSpoHistory(SpoHistory spoHistory) throws DuplicateKeyException, MappingException;

    public SpoHistory findSpoHistoryByPrimaryKey(SpoHistory spoHistory) throws NoSuchObjectException, MappingException;

    public void removeSpoHistory(SpoHistory purple) throws MappingException;
}
