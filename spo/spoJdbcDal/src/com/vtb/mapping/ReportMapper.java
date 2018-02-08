package com.vtb.mapping;

import java.util.ArrayList;

import com.vtb.domain.Report;
import com.vtb.exception.MappingException;

public interface ReportMapper extends Mapper<Report> {
	public ArrayList<Report> findByName(String name, String orderBy) throws MappingException;
}
