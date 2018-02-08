package com.vtb.mapping;

import java.util.ArrayList;

import ru.masterdm.compendium.domain.Currency;

import com.vtb.domain.CommissionType;
import com.vtb.exception.MappingException;

public interface CommissionTypeMapper extends Mapper<CommissionType> {
	
    public ArrayList<CommissionType> findByName(String name, String orderBy) throws MappingException;
    
    /**
     * retrieval of  list of currencies that are chosen in the parent task.    
     * @param parentTaskId parent task identifier
     * @return ArrayList<Currency>
     * @throws MappingException
     * !!!PUT IT HERE BECAUSE CURRENCYMAPPER LOOKS TO CRM SCHEME, NOT VTB_ADMIN!!!
     */
    public ArrayList<Currency> findParentCurrency(Long parentTaskId) throws MappingException;

}
