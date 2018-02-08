package com.vtb.ejb;

import com.vtb.domain.SpoHistory;
import com.vtb.exception.DuplicateKeyException;
import com.vtb.exception.MappingException;
import com.vtb.exception.NoSuchObjectException;
import com.vtb.mapping.Mapper;
import com.vtb.mapping.MapperFactory;

public class CRMActionProcessorFacadeBean implements CRMActionProcessorFacade, CRMActionProcessorFacadeLocal {

    private static final long serialVersionUID = 1L;

    public void insertSpoHistory(SpoHistory spoHistory) throws DuplicateKeyException, MappingException {
        Mapper<SpoHistory> mapper = MapperFactory.getSystemMapperFactory().getMapper(SpoHistory.class);
        mapper.insert(spoHistory);
    }

    public SpoHistory findSpoHistoryByPrimaryKey(SpoHistory spoHistory) throws NoSuchObjectException, MappingException {
        Mapper<SpoHistory> mapper = MapperFactory.getSystemMapperFactory().getMapper(SpoHistory.class);
        return mapper.findByPrimaryKey(spoHistory);
    }

    public void removeSpoHistory(SpoHistory spoHistory) throws MappingException {
        Mapper<SpoHistory> mapper = MapperFactory.getSystemMapperFactory().getMapper(SpoHistory.class);
        mapper.remove(spoHistory);
    }

}
