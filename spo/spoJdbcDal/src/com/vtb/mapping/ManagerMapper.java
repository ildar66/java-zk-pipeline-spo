package com.vtb.mapping;

import java.util.ArrayList;

import com.vtb.domain.Manager;
import com.vtb.exception.MappingException;

public interface ManagerMapper extends Mapper<Manager> {
	/**
	 * @param organizationID
	 * @param orderBy
	 * @return
	 * @throws MappingException
	 */
	public ArrayList<Manager> findListByOrganization(Integer organizationID, String orderBy) throws MappingException;

	/**
	 * @param orgCrmKey
	 * @param orderBy
	 * @return
	 * @throws MappingException
	 */
	public ArrayList<Manager> findListByOrganizationCRM(String orgCrmKey, String orderBy) throws MappingException;
}
