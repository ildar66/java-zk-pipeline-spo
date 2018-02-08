package com.vtb.mapping;

import java.util.ArrayList;

import ru.masterdm.compendium.domain.crm.PatternPaidPercentType;

import com.vtb.exception.MappingException;

public interface PatternPaidPercentTypeMapper extends Mapper<PatternPaidPercentType> {
	public ArrayList<PatternPaidPercentType> findByName(String name, String orderBy) throws MappingException;
}
