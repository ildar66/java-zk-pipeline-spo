package com.vtb.mapping;

import java.util.ArrayList;

import com.vtb.custom.OperatorTO;
import com.vtb.domain.Operator;
import com.vtb.exception.MappingException;
import com.vtb.exception.NoSuchOperatorException;

public interface OperatorMapper extends Mapper<Operator> {
	
    public Integer findOperatorsForMessageCount(Integer departmentId, String sNamePattern) throws MappingException;
    
	public ArrayList<Operator> findOperatorsForMessage(
    		Integer departmentId, String sNamePattern, Integer start, Integer end) throws MappingException;
	
	public ArrayList<Operator> findByName(Integer departmentKey, String likeName, String orderBy) throws MappingException;

	public Operator findOperatorByLogin(String aLogin) throws NoSuchOperatorException, MappingException;

	public ArrayList<Operator> findByFilter(Integer departmentKey, int searchFilter, String searchStr, String orderBy) throws MappingException;
	
	public ArrayList<OperatorTO> findListByFilter(int searchFilter, String searchStr, String orderBy) throws MappingException;	
}
