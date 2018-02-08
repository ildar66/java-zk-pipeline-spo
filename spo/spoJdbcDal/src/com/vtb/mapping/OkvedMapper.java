package com.vtb.mapping;

import java.util.ArrayList;

import com.vtb.custom.OkvedTO;
import com.vtb.domain.Okved;
import com.vtb.exception.MappingException;

public interface OkvedMapper extends Mapper<Okved> {
	public ArrayList<Okved> findListByOrganization(Integer organizationID, String orderBy) throws MappingException;

	public ArrayList<Okved> findList(String name, String orderBy) throws MappingException;

	public ArrayList<Okved> findHierarchyList(Integer parentID, String orderBy) throws MappingException;

	public ArrayList<OkvedTO> findListTO(String name, String orderBy) throws MappingException;

	public ArrayList<OkvedTO> findListTO_ByOrganization(Integer organizationID, String orderBy) throws MappingException;

}
