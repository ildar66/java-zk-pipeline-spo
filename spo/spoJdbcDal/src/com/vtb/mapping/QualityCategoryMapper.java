package com.vtb.mapping;

import java.util.List;

import com.vtb.domain.QualityCategory;
import com.vtb.exception.MappingException;

public interface QualityCategoryMapper extends Mapper<QualityCategory>{
    public List<QualityCategory> findQualityCategory(String searchStr, String orderBy) throws MappingException;
}
