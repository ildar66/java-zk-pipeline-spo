package com.vtb.mapping;

import java.util.ArrayList;

import com.vtb.domain.RatingType;
import com.vtb.exception.MappingException;

public interface RatingTypeMapper extends Mapper<RatingType> {
	public ArrayList<RatingType> findByName(String name, String orderBy) throws MappingException;
}
