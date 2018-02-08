package com.vtb.mapping;

import java.util.ArrayList;

import com.vtb.custom.OrganizationGroupTO;
import com.vtb.domain.OrganizationGroup;
import com.vtb.exception.MappingException;
import com.vtb.exception.NoSuchObjectException;

public interface OrganizationGroupMapper extends Mapper<OrganizationGroup> {
	/**
	 * 
	 * @param name
	 * @param orderBy
	 * @return
	 * @throws MappingException
	 */
	public ArrayList<OrganizationGroup> findList(String name, String orderBy) throws MappingException;
	
	/**
	 * 
	 * @param organizationID
	 * @param orderBy
	 * @return
	 */
	public ArrayList<OrganizationGroup> findListByOrganization(Integer organizationID, String orderBy) throws MappingException;
	
	/**
	 * 
	 * @param organizationID
	 * @param orderBy
	 * @return
	 */
	public ArrayList<OrganizationGroupTO> findListTO_ByOrganization(Integer organizationID, String orderBy) throws MappingException;
	
	/**
	 * 
	 * @param orgCrmID
	 * @param orderBy
	 * @return
	 */
	public ArrayList<OrganizationGroupTO> findListTO_ByOrganizationCRM(String orgCrmID, String orderBy) throws MappingException;	
	
	/**
	 * 
	 * @param name
	 * @param orderBy
	 * @return
	 * @throws MappingException
	 */
	public ArrayList<OrganizationGroupTO> findListTO(String name, String orderBy) throws MappingException;

	/**
	 * Retrieve a single object matching this object.
	 *
	 * @return VtbObject
	 */
	public OrganizationGroup findByCrmKey(OrganizationGroup anObject) throws NoSuchObjectException, MappingException;
}
