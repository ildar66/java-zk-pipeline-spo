package com.vtb.mapping.jpa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Query;

import com.vtb.domain.CRMClaimsReport;
import com.vtb.domain.CRMClaimsReportHeader;
import com.vtb.domain.CRMClaimsReportOperation;
import com.vtb.domain.VtbObject;
import com.vtb.exception.DuplicateKeyException;
import com.vtb.exception.MappingException;
import com.vtb.exception.NoSuchObjectException;
import com.vtb.utils.Utils;

/**
 * CRMClaimsReportMapper implementation.
 * @author Michael Kuznetsov
 */
public class CRMClaimsReportMapper extends DomainJPAMapper  
				implements com.vtb.mapping.CRMClaimsReportMapper {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<CRMClaimsReport> findAll() throws MappingException {
		throw new NoSuchObjectException("CRMClaimsReportReportMapper.findAll. Method is not implemented ");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CRMClaimsReport findByPrimaryKey(CRMClaimsReport anObject) throws NoSuchObjectException, MappingException {
		throw new NoSuchObjectException("CRMClaimsReportReportMapper.findByPrimaryKey. Method is not implemented ");	
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void insert(CRMClaimsReport anObject) throws DuplicateKeyException, MappingException {
		throw new NoSuchObjectException("CRMClaimsReportReportMapper.insert. Method is not implemented ");	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(CRMClaimsReport anObject) throws NoSuchObjectException, MappingException {
		throw new NoSuchObjectException("CRMClaimsReportReportMapper.update. Method is not implemented ");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(CRMClaimsReport anObject) throws NoSuchObjectException, MappingException {
		throw new NoSuchObjectException("CRMClaimsReportReportMapper.remove. Method is not implemented ");
	}

	
	/**
	 * maps to VO
	 */	
	public VtbObject mapOperations(Object[] jpa) throws MappingException {
		CRMClaimsReportOperation result = new CRMClaimsReportOperation();
		result.setCrmCode(Utils.varToString(jpa[0]));
		result.setSpoSendDate(Utils.varToString(jpa[2]));
		result.setSpoAcceptDate(Utils.varToString(jpa[3]));
		result.setStatusCode((String) jpa[6]);
		result.setUserLogin(Utils.varToString(jpa[4]));
		result.setSpoNumber(Utils.varToString(jpa[1]));
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<CRMClaimsReportOperation> getOperations(CRMClaimsReportHeader parameters) 
				throws MappingException {
		StringBuilder sb = new StringBuilder(); 
		sb.append("SELECT t.crmcode,t.mdtask_number, fb.SPOSENDDATE, fb.SPOACCEPTDATE, ui.USERCODE,fb.FB_SPO_OPPORTUNITYID,fb.spoaccept ")
        .append(" FROM  sysdba.FB_SPO_OPPORTUNITY@CRM_LINK fb ")
        .append(" inner join mdtask t on t.OPPORTUNITYID=fb.FB_SPO_OPPORTUNITYID ")
        .append(" INNER JOIN sysdba.V_SPO_OPPORTUNITY@CRM_LINK opp  ON opp.OPPORTUNITYID = fb.OPPORTUNITYID ")
        .append(" INNER JOIN sysdba.v_spo_userinfo@CRM_LINK ui      ON ui.USERID = opp.accountmanagerid ")
        .append(" where fb.sposend='1' and ")
	    .append(" fb.SPOACCEPTDATE between to_date(?1, 'DD.MM.YYYY HH24:MI') AND to_date(?2, 'DD.MM.YYYY HH24:MI') ")
	    .append(" AND fb.SPOSENDDATE between to_date(?3, 'DD.MM.YYYY HH24:MI') AND to_date(?4, 'DD.MM.YYYY HH24:MI')");
		
		String sqlStr = sb.toString();
		Query query = null;		
		try {
			query = getEntityMgr().createNativeQuery(sqlStr);
			query.setParameter(1, parameters.getAcceptLeftDate()+" 00:00");
			query.setParameter(2, parameters.getAcceptRightDate()+" 23:59");
			query.setParameter(3, parameters.getSendLeftDate()+" 00:00");
			query.setParameter(4, parameters.getSendRightDate()+" 23:59");
			
			List<Object[]> objectList = query.getResultList();
			ArrayList<CRMClaimsReportOperation> list = new ArrayList<CRMClaimsReportOperation>();
			for (Iterator<Object[]> iterator = objectList.iterator(); iterator.hasNext();) {
				Object[] rs = (Object[]) iterator.next();
				// Обработать результаты	
				CRMClaimsReportOperation operation = (CRMClaimsReportOperation) mapOperations(rs);
				list.add(operation);
			}
			return list;
		} catch (Exception e) {
			throw new MappingException(e, ("Exception caught in getOperations " + e));			
		}
	}
}
