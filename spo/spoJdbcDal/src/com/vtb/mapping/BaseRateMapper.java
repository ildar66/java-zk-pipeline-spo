package com.vtb.mapping;

import java.util.ArrayList;

import com.vtb.domain.BaseRate;
import com.vtb.exception.MappingException;

public interface BaseRateMapper extends Mapper<BaseRate> {
	public ArrayList<BaseRate> findByName(String name, String orderBy) throws MappingException;
}
