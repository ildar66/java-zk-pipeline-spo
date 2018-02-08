package com.vtb.mapping;

import java.util.ArrayList;

import com.vtb.domain.ProcessType;
import com.vtb.exception.MappingException;

public interface ProcessTypeMapper extends Mapper<ProcessType> {
	public ArrayList<ProcessType> findByName(String name, String orderBy) throws MappingException;
}
