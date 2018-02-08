package com.vtb.mapping;

import java.util.ArrayList;

import com.vtb.domain.Region;
import com.vtb.exception.MappingException;

public interface RegionMapper extends Mapper<Region> {
	public ArrayList<Region> findByName(String name, String orderBy) throws MappingException;
}
