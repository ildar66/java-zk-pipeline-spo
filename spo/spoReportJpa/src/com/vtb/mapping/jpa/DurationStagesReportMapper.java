package com.vtb.mapping.jpa;

import java.util.List;

import com.vtb.domain.DurationStagesReport;
import com.vtb.domain.StandardPeriod;
import com.vtb.exception.DuplicateKeyException;
import com.vtb.exception.MappingException;
import com.vtb.exception.NoSuchObjectException;

/**
 * DurationStagesReportMapper implementation.
 * @author Michael Kuznetsov
 */
public class DurationStagesReportMapper extends DomainJPAMapper  implements com.vtb.mapping.DurationStagesReportMapper {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DurationStagesReport findByPrimaryKey(DurationStagesReport anObject) throws NoSuchObjectException, MappingException {
		throw new NoSuchObjectException("DurationStagesReportMapper.findByPrimaryKey. Method is not implemented ");	
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void insert(DurationStagesReport anObject) throws DuplicateKeyException, MappingException {
		throw new NoSuchObjectException("DurationStagesReportMapper.insert. Method is not implemented ");	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(DurationStagesReport anObject) throws NoSuchObjectException, MappingException {
		throw new NoSuchObjectException("DurationStagesReportMapper.update. Method is not implemented ");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(DurationStagesReport anObject) throws NoSuchObjectException, MappingException {
		throw new NoSuchObjectException("DurationStagesReportMapper.remove. Method is not implemented ");
	}


	@Override
	public List<DurationStagesReport> findAll() throws MappingException {
		throw new NoSuchObjectException("DurationStagesReportMapper.findByPrimaryKey. Method is not implemented ");
	}


	@Override
	public List<StandardPeriod> getReportData(String mdtaskNumber) throws MappingException {
		//TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
		return null;
	}
}
