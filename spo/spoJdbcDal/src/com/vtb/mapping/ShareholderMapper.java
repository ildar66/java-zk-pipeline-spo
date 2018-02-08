package com.vtb.mapping;

import java.util.ArrayList;

import com.vtb.domain.Shareholder;
import com.vtb.exception.MappingException;

public interface ShareholderMapper extends Mapper<Shareholder> {
	/**
	 * @param organizationID
	 * @param orderBy
	 * @return
	 * @throws MappingException
	 */
	public ArrayList<Shareholder> findListByOrganization(Integer organizationID, String orderBy) throws MappingException;

	/**
	 * @param orgCrmKey
	 * @param orderBy
	 * @return
	 * @throws MappingException
	 */
	public ArrayList<Shareholder> findListByOrganizationCRM(String orgCrmKey, String orderBy) throws MappingException;
}
