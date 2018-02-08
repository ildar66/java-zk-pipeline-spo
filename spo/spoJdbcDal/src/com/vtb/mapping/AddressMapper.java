package com.vtb.mapping;

import java.util.ArrayList;

import com.vtb.domain.Address;

import com.vtb.exception.MappingException;

public interface AddressMapper extends Mapper<Address> {
	/**
	 * @param organizationID
	 * @param orderBy
	 * @return
	 * @throws MappingException
	 */
	public ArrayList<Address> findListByOrganization(Integer organizationID, String orderBy) throws MappingException;

	/**
	 * @param orgCrmKey
	 * @param orderBy
	 * @return
	 * @throws MappingException
	 */
	public ArrayList<Address> findListByOrganizationCRM(String orgCrmKey, String orderBy) throws MappingException;
}
