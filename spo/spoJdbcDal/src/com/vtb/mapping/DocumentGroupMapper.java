package com.vtb.mapping;

import java.util.ArrayList;

import com.vtb.domain.DocumentGroup;
import com.vtb.exception.MappingException;

public interface DocumentGroupMapper extends Mapper<DocumentGroup> {
	public ArrayList<DocumentGroup> findByName(String name, String orderBy) throws MappingException;
}
