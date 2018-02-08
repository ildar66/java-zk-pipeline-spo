package com.vtb.mapping;

import java.util.ArrayList;

import com.vtb.domain.SpoHistory;
import com.vtb.exception.MappingException;

public interface SpoHistoryMapper extends Mapper<SpoHistory> {
	public ArrayList<SpoHistory> findByName(String aFilter, String orderBy) throws MappingException;
}
