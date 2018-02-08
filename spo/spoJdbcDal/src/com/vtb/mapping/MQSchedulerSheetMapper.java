package com.vtb.mapping;

import java.util.ArrayList;

import com.vtb.domain.MQSchedulerSheet;
import com.vtb.exception.MappingException;

public interface MQSchedulerSheetMapper extends Mapper<MQSchedulerSheet> {
	public ArrayList<MQSchedulerSheet> findList(Integer departmentKey, String status, String orderBy) throws MappingException;
}
