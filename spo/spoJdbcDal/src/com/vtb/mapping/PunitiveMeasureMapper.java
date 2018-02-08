package com.vtb.mapping;

import java.util.ArrayList;

import com.vtb.domain.PunitiveMeasure;
import com.vtb.exception.MappingException;

public interface PunitiveMeasureMapper extends Mapper<PunitiveMeasure> {
	public ArrayList<PunitiveMeasure> findByName(String name, String orderBy) throws MappingException;
}
