package com.vtb.mapping;

import java.util.ArrayList;

import com.vtb.domain.LimitType;
import com.vtb.exception.MappingException;

public interface LimitTypeMapper extends Mapper<LimitType> {
	public ArrayList<LimitType> findByName(String name, String orderBy) throws MappingException;
}
