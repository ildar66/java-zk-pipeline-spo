package com.vtb.mapping.jdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vtb.domain.CrmComiss;
import com.vtb.domain.CrmGraph;
import com.vtb.domain.SpoOpportunityProduct;
import com.vtb.exception.MappingException;

public class SpoOpportunityProductMapper extends JDBCMapperCRM<SpoOpportunityProduct> {
    private static final Logger LOGGER = Logger.getLogger(SpoOpportunityProductMapper.class.getName());
	private static final String _loadString = 
	    "SELECT opp.OPPORTUNITYID, " +
		"opprod.QUANTITY, " +
		"opprod.UNIT, fbopprod.DAYS, uinfo.lastname||' '|| uinfo.firstname||' '||uinfo.middlename as manager, prod.name, "+
		"fbopprod.COMNEISP * 100,fbopprod.COMUPR * 100,fbopprod.COMSCHET * 100,fbopprod.COMOPP,fbopprod.COMBVS, "+
		"spofbopp.ACTIVEBEGIN, spofbopp.ACTIVEEND,  uinfo.USERCODE, " +
		"fbopprod.SUBLIMNUMID , uinfo.lastname, uinfo.firstname, spofbopp.NUM, "+
		"fbopprod.POGAS, " + 
		"fbopprod.COM_DOSR_POGAS,fbopprod.DOSROCH_POGAS, fbopprod.BANK_ACPT, " +
		"fbopprod.CONDITIONS, fbopprod.UNITCOURSE, SPRAVPARAM, " +	
		"fbopprod.LV, fbopprod.LZ,  fbopprod.QUANTITYVYDACHI, fbopprod.QUANTITY_ZAD " +
		"FROM sysdba.V_SPO_OPPORTUNITY opp left outer join sysdba.v_spo_userinfo uinfo on uinfo.userid=accountmanagerid  " +
		"left outer join sysdba.v_spo_fb_opportunity spofbopp on spofbopp.OPPORTUNITYID = opp.OPPORTUNITYID, " +
		"sysdba.V_SPO_FB_OPPORTUNITY_PRODUCT fbopprod, " +
		"sysdba.V_SPO_OPPORTUNITY_PRODUCT opprod left outer join sysdba.v_spo_product prod on prod.productid=opprod.productid " +
		"WHERE opp.OPPORTUNITYID = opprod.OPPORTUNITYID " + 
		"AND opprod.OPPPRODUCTID = fbopprod.OPPPRODUCTID " + 
		"AND opp.OPPORTUNITYID = ? ";

	
	@Override
	protected Object createImpl(Connection conn, SpoOpportunityProduct domainObject) throws SQLException, MappingException {
		throw new MappingException("Insert not valid for this type");
	}

	@Override
	protected SpoOpportunityProduct findByPrimaryKeyImpl(Connection conn, SpoOpportunityProduct domainObjectWithKeyValues) throws SQLException,
			MappingException {
		SpoOpportunityProduct domainObject = null;
		String aId = domainObjectWithKeyValues.getId();
		PreparedStatement ps = conn.prepareStatement(_loadString);
		ps.setObject(1, aId);
		ResultSet rs = ps.executeQuery();
		if(rs.next()){
			domainObject = activate(rs);
		}
		try{
    		//подгрузить условия
			try {
	    		if(domainObject.getCONDITIONS()!=null && domainObject.getCONDITIONS().length()>0) {
		    		ps = conn.prepareStatement("select text from sysdba.V_SPO_FB_CONDITIONS where code=?");
		    		for(String code : domainObject.getCONDITIONS().split(";")){
		    		    ps.setObject(1, code.trim());
		    	        rs = ps.executeQuery();
		    	        if(rs.next()){
		    	            domainObject.getConditionMap().put(code.trim(), rs.getString("text"));
		    	        }
		    		}
	    		}
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "OpportunityId: '" + aId + "'. Ошибка в загрузке условий из CRM: " + e.getMessage(), e);
			}
			
    		//подгрузить график погашения
			try {
	    		ps = conn.prepareStatement("select g.summa,g.startdate,g.finishdate,g.UNIT from sysdba.V_SPO_FB_GRAFICPOGASH g "+ 
	    		        "inner join sysdba.V_SPO_OPPORTUNITY_PRODUCT opprod on opprod.OPPPRODUCTID=g.oppproductid "+
	    		        "where opprod.OPPORTUNITYID=?");
	    		ps.setObject(1, aId);
	    		rs = ps.executeQuery();
	    		while(rs.next()){
	    		    CrmGraph graph = new CrmGraph();
	    		    graph.setFirstPayDate(rs.getDate("startdate"));
	    		    graph.setFinalPayDate(rs.getDate("finishdate"));
	    		    graph.setAmount(rs.getDouble("summa"));
	    		    graph.setUnit(rs.getString("UNIT"));
	    		    domainObject.getGraphList().add(graph);
	            }
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "OpportunityId: '" + aId + "'. Ошибка в загрузке графика погашения из CRM: " + e.getMessage(), e);
			}
			
    		//подгрузить комиссии
			try {
	    		ps = conn.prepareStatement("select c.comiss_code,c.comiss_value,c.comiss_unit,c.comiss_base,c.comiss_periodichnost,c.notes "+ 
	    		        "from sysdba.V_SPO_FB_OPPORTUNITY_COMISS c  "+
	    		        "inner join sysdba.V_SPO_OPPORTUNITY_PRODUCT opprod on opprod.OPPPRODUCTID=c.oppproductid "+
	    		        "where opprod.OPPORTUNITYID=?");
	            ps.setObject(1, aId);
	            rs = ps.executeQuery();
	            while(rs.next()){
	                CrmComiss comiss = new CrmComiss();
	                comiss.setComiss_base(rs.getString("comiss_base"));
	                comiss.setComiss_code(rs.getString("comiss_code"));
	                comiss.setComiss_periodichnost(rs.getString("comiss_periodichnost"));
	                comiss.setComiss_unit(rs.getString("comiss_unit"));
	                comiss.setComiss_value(rs.getDouble("comiss_value"));
	                comiss.setNotes(rs.getString("notes"));
	                domainObject.getComissList().add(comiss);
	            }
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "OpportunityId: '" + aId + "'. Ошибка в загрузке комиссий из CRM: " + e.getMessage(), e);
			}
						
			//Сделка в рамках лимита
			try {
                ps = conn.prepareStatement("select FB_LIMITID from sysdba.V_SPO_FB_OPP_LIMIT where OPPORTUNITYID=?");
                ps.setObject(1, aId);
                rs = ps.executeQuery();
                if(rs.next()){
                    domainObject.setLimitid(rs.getString("FB_LIMITID"));
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "OpportunityId: '" + aId + "'. Ошибка в загрузке информации о сделки в лимите из CRM: " 
                        + e.getMessage(), e);
            }
		}catch(Exception e){
		    LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return domainObject;
	}

	private SpoOpportunityProduct activate(ResultSet rs)  throws SQLException {
		int i = 1;
		SpoOpportunityProduct product = new SpoOpportunityProduct(rs.getString(i++));
		product.setQuantity((BigDecimal)rs.getObject(i++));
		product.setUnit(rs.getString(i++));
		product.setDays(rs.getString(i++));
		product.setManager(rs.getString(i++));
		
		product.setProductname(rs.getString(i++));
		product.setCOMNEISP(rs.getString(i++));
		product.setCOMUPR(rs.getString(i++));
		product.setCOMSCHET(rs.getString(i++));
		product.setCOMOPP(rs.getString(i++));
		product.setCOMBVS(rs.getString(i++));
		
		product.setActiveBegin(rs.getDate(i++));
		product.setActiveEnd(rs.getDate(i++));
		
		product.setUserlogin(rs.getString(i++));
		product.setLimitid(rs.getString(i++));
		product.setUserName(rs.getString("lastname")+" "+rs.getString("firstname"));
		product.setNum(rs.getString("NUM"));//отображаемый номер сделки VTBSPO-1034
		product.setPOGAS(rs.getString("POGAS"));
		product.setCONDITIONS(rs.getString("CONDITIONS"));
		product.setUNITCOURSE(rs.getBigDecimal("UNITCOURSE"));
		
		product.setDOSROCH_POGAS(rs.getString("DOSROCH_POGAS"));
		product.setBANK_ACPT(rs.getString("BANK_ACPT"));
		product.setCOM_DOSR_POGAS(rs.getDouble("COM_DOSR_POGAS"));
		product.setSpravparam(rs.getString("SPRAVPARAM"));

		product.setLV("T".equalsIgnoreCase(rs.getString("LV")));
		product.setLZ("T".equalsIgnoreCase(rs.getString("LZ")));
		product.setQuantityVydachi(rs.getBigDecimal("QUANTITYVYDACHI"));
		product.setQuantityZad(rs.getBigDecimal("QUANTITY_ZAD"));
		return product;
	}

	@Override
	protected void removeImpl(Connection conn, SpoOpportunityProduct domainObject) throws SQLException, MappingException {
		throw new MappingException("Remote not valid for this type");
	}

	@Override
	protected void updateImpl(Connection conn, SpoOpportunityProduct anObject) throws SQLException, MappingException {
		throw new MappingException("Update not valid for this type");
	}

	public List<SpoOpportunityProduct> findAll() throws MappingException {
		throw new MappingException("findAll not valid for this type");
	}

}
