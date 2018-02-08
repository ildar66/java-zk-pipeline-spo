package com.vtb.mapping;

import java.util.ArrayList;

import com.vtb.domain.Govern;
import com.vtb.exception.MappingException;

public interface GovernMapper extends Mapper<Govern> {
	public ArrayList<Govern> findListByOrganization(Integer organizationID, String orderBy) throws MappingException;

	public ArrayList<Govern> findListByOrganizationCRM(String orgCrmKey, String orderBy) throws MappingException;
}
