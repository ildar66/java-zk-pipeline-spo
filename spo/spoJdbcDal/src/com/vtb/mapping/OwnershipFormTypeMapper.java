package com.vtb.mapping;

import java.util.ArrayList;

import com.vtb.domain.OwnershipFormType;
import com.vtb.exception.MappingException;

/**
 * @author IShaffigulin
 * 
 * Mapper "Организационно-правовые формы".
 */
public interface OwnershipFormTypeMapper extends Mapper<OwnershipFormType> {
	public ArrayList<OwnershipFormType> findByName(String name, String orderBy) throws MappingException;
}
