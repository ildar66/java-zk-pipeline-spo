package com.vtb.mapping;

import java.util.ArrayList;

import com.vtb.domain.Industry;
import com.vtb.exception.MappingException;

public interface IndustryMapper extends Mapper<Industry> {
	public ArrayList<Industry> findByName(String name, String orderBy) throws MappingException;
}
