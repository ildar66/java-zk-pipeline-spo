package com.vtb.mapping;

import java.util.ArrayList;

import com.vtb.domain.StopFactor;
import com.vtb.exception.MappingException;

public interface StopFactorMapper extends Mapper<StopFactor> {
	public ArrayList<StopFactor> findByName(String name, String orderBy) throws MappingException;
}
