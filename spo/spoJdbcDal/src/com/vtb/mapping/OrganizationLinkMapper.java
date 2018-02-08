package com.vtb.mapping;

import com.vtb.exception.MappingException;

public interface OrganizationLinkMapper {

	/**
	 * для взаимодействия с системой CRM
	 * 
	 * @param groupKey
	 * @param organizationKey
	 * @throws MappingException
	 */
	public abstract void addLinkGroupToOrganization(String groupKey, String organizationKey) throws MappingException;

	public abstract void deleteLinkGroupToOrganization(String groupKey, String organizationKey) throws MappingException;

	/**
	 * для взаимодействия с системой CRM
	 * 
	 * @param groupCrmKey
	 * @param organizationKey
	 * @throws MappingException
	 */
	public abstract void addLinkGroupCrmToOrganization(String groupCrmKey, String organizationKey)
			throws MappingException;

	public abstract void deleteLinkGroupCrmToOrganization(String groupCrmKey, String organizationKey)
			throws MappingException;
	
	/**
	 * 
	 * @param okvedKey
	 * @param organizationKey
	 * @throws MappingException
	 */
	public abstract void addLinkOkvedToOrganization(String okvedKey, String organizationKey) throws MappingException;

	public abstract void deleteLinkOkvedToOrganization(String okvedKey, String organizationKey) throws MappingException;

	/**
	 * 
	 * @param okvedCrmKey
	 * @param organizationKey
	 * @throws MappingException
	 */
	public abstract void addLinkOkvedCrmToOrganization(String okvedCrmKey, String organizationKey) throws MappingException;

	public abstract void deleteLinkOkvedCrmToOrganization(String okvedCrmKey, String organizationKey) throws MappingException;
	
	/**
	 * для взаимодействия с системой CRM
	 * 
	 * @param groupKey
	 * @param organizationCrmKey
	 * @throws MappingException
	 */
	public abstract void addLinkGroupToOrganizationCRM(String groupKey, String organizationCrmKey)
			throws MappingException;

	public abstract void deleteLinkGroupToOrganizationCRM(String groupKey, String organizationCrmKey)
			throws MappingException;

	/**
	 * для взаимодействия с системой CRM
	 * 
	 * @param groupCrmKey
	 * @param organizationCrmKey
	 * @throws MappingException
	 */
	public abstract void addLinkGroupCrmToOrganizationCRM(String groupCrmKey, String organizationCrmKey)
			throws MappingException;

	public abstract void deleteLinkGroupCrmToOrganizationCRM(String groupCrmKey, String organizationCrmKey)
			throws MappingException;

}