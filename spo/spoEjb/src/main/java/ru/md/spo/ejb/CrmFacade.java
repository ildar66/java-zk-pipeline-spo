package ru.md.spo.ejb;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.masterdm.compendium.exception.MappingException;
import ru.md.crm.dbobjects.CRMRating;
import ru.md.crm.dbobjects.FbSpoLogStatusJPA;
import ru.md.crm.dbobjects.FbSpoOpportunityProductNewJPA;
import ru.md.crm.dbobjects.LimitJPA;
import ru.md.crm.dbobjects.LimitQueueJPA;
import ru.md.crm.dbobjects.LimitQueueTO;
import ru.md.crm.dbobjects.NetworkWagerJPA;
import ru.md.crm.dbobjects.ProductQueueJPA;
import ru.md.pup.dbobjects.UserJPA;

import com.vtb.domain.CommissionDeal;
import com.vtb.domain.PaymentSchedule;
import com.vtb.domain.SPOAcceptType;
import com.vtb.domain.Task;
import com.vtb.domain.TaskContractor;
import com.vtb.domain.TaskProduct;
import com.vtb.domain.TaskStatusReturn;
import com.vtb.domain.Trance;

@Stateless
public class CrmFacade implements CrmFacadeLocal {
	private static final Logger LOGGER = LoggerFactory.getLogger(CrmFacade.class.getName());
	
    @PersistenceUnit(unitName="CRMPU")
    private EntityManagerFactory factoryCRM;
    private static transient final SimpleDateFormat dfDT = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    
    @Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public LimitQueueTO getLimitQueueById(String id) {
        LimitQueueTO res = new LimitQueueTO();
        res.limit=factoryCRM.createEntityManager().find(LimitJPA.class, id);
        res.queue=factoryCRM.createEntityManager().find(LimitQueueJPA.class, id);
        return res;
    }
    @Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ProductQueueJPA getProductQueueById(String id) {
        return factoryCRM.createEntityManager().find(ProductQueueJPA.class, id);
    }
    @Override @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateProductQueueStatus(String queueID, SPOAcceptType type,
            String result) {
        LOGGER.info("updating status for product "+queueID+". New status is "+type.name());
        EntityManager em = factoryCRM.createEntityManager();
        ProductQueueJPA queue = em.find(ProductQueueJPA.class, queueID);
        queue.setAccept(type.getId());
        queue.setAcceptDate(new java.util.Date());
        em.flush();
        //добавить в лог запись
        log(new Date(), queueID, result);
    }
    @Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ProductQueueJPA[] getProductSPODELETEQueue(){
        try {
            Query query = factoryCRM.createEntityManager().createQuery("SELECT c FROM ProductQueueJPA c WHERE c.SPODELETE='2' and c.accept='1'");
            List<ProductQueueJPA> listJPA = query.getResultList();
            ProductQueueJPA[] array = new ProductQueueJPA[listJPA.size()];
            return listJPA.toArray(array);
        } catch (Exception e) {
            throw new MappingException(e,
                    ("Exception caught in getCRMProductQueue " + e));
        }
    }
    @Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public LimitQueueTO[] getLimitQueue(SPOAcceptType type) {
        return getLimitQueue(type,"01.01.1982","01.01.2222");
    }

    @SuppressWarnings("unchecked")
    @Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public LimitQueueTO[] getLimitQueue(SPOAcceptType type, String from, String to) {
        if (type == null)
            throw new IllegalArgumentException(
                    "FlexWorkflowFacade.getCRMLimitList. Argument 'type' can't be null");
        EntityManager em = factoryCRM.createEntityManager();
        String sqlStr = "c.accept LIKE ?1";
        if(type == SPOAcceptType.NOTACCEPT) sqlStr="("+sqlStr+" or c.accept is null)";
        sqlStr = "SELECT c FROM LimitQueueJPA c WHERE c.send='1' and sendDate>?2 and sendDate<?3 and " +
            sqlStr+" order by c.sendDate";
        try {
            Query query = em.createQuery(sqlStr);
            query.setParameter(1, type.getId());
            query.setParameter(2, dfDT.parse(from+" 00:00"));
            query.setParameter(3, dfDT.parse(to+" 23:59"));
            List<LimitQueueJPA> listJPA = query.getResultList();
            ArrayList<LimitQueueTO> listTO = new ArrayList<LimitQueueTO>();
            for(LimitQueueJPA jpa : listJPA){
                LimitQueueTO lto = new LimitQueueTO();
                lto.queue=jpa;
                lto.limit=em.find(LimitJPA.class, jpa.getId());
                listTO.add(lto);
            }
            LimitQueueTO[] array = new LimitQueueTO[listTO.size()];
            return listTO.toArray(array);
        } catch (Exception e) {
            throw new MappingException(e,
                    ("Exception caught in getLimitQueue " + e));
        }
    }

    @Override @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateLimitQueueStatus(String queueID, SPOAcceptType type,
            String result) {
        LOGGER.info("updating status for limit "+queueID+". New status is "+type.name());
        LimitQueueJPA queue = factoryCRM.createEntityManager().find(LimitQueueJPA.class, queueID);
        queue.setAccept(type.getId());
        queue.setResult(result);
        queue.setAcceptDate(new java.util.Date());
    }
    @Override @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public ProductQueueJPA[] getProductQueue(SPOAcceptType type) {
        return getProductQueue(type,"01.01.1982","01.01.2222",null);
    }
    @SuppressWarnings("unchecked")
    @Override @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public ProductQueueJPA[] getProductQueue(SPOAcceptType type,
            String from, String to, String userLogin) {
        if (type == null)
            throw new IllegalArgumentException(
                    "FlexWorkflowFacade.getCRMProductQueue. Argument 'type' can't be null");
        cleanBrokenFK();
        
        String sqlStr = "c.accept LIKE ?1";
        if(type == SPOAcceptType.NOTACCEPT) sqlStr="("+sqlStr+" or c.accept is null)";
        sqlStr = "SELECT c FROM ProductQueueJPA c WHERE c.send='1' and sendDate>?2 and sendDate<?3 and " +
                ""+sqlStr;
        if (userLogin != null) sqlStr += " and c.USERCODE='"+userLogin+"'";
        sqlStr += " order by c.sendDate";
        try {
            Query query = factoryCRM.createEntityManager().createQuery(sqlStr);
            query.setParameter(1, type.getId());
            query.setParameter(2, dfDT.parse(from+" 00:00"));
            query.setParameter(3, dfDT.parse(to+" 23:59"));
            List<ProductQueueJPA> listJPA = query.getResultList();
            ProductQueueJPA[] array = new ProductQueueJPA[listJPA.size()];
            return listJPA.toArray(array);
        } catch (Exception e) {
            throw new MappingException(e,
                    ("Exception caught in getCRMProductQueue " + e));
        }
    }
    /**
     * очистить от битых ссылок.
     */
    private void cleanBrokenFK() {
        factoryCRM.createEntityManager().createNativeQuery(
                "update sysdba.FB_SPO_OPPORTUNITY o set o.spoaccept='4' where o.accountid not in " +
                "(select accountid from sysdba.v_Spo_Account)").
                executeUpdate();
    }
    @Override @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void log(Date date, String id, String message) {
        // TODO FB_SPO_OPPORTUNITY_DES_REAZON
        EntityManager em = factoryCRM.createEntityManager();
        FbSpoLogStatusJPA s = new FbSpoLogStatusJPA();
        s.setCRMID(id);
        s.setERR_DATE(date);
        s.setLOG(message);
        s.setSTATUS(2L);
        em.persist(s);
    }
    
    @Override @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void exportLimit(Task task) {
        EntityManager em = factoryCRM.createEntityManager();
        Query query = em.createNativeQuery(
        "insert into sysdba.fb_spo_limit (fb_limitid, curr, act_begin, act_end, use_begin, use_end, currs, use_srok, limitid, limit_vid, status, summa) "
                + " select fb_limitid, ?2, act_begin, act_end, use_begin, use_end, ?3, use_srok, limitid, limit_vid, ?4, ?5 "
                + " from sysdba.v_spo_fb_limit where fb_limitid=?1");
        String status = task.getTaskStatusReturn().getStatusReturn().getType().equals("0")?"3":"2";
        query.setParameter(1, task.getHeader().getCrmid());
        query.setParameter(2, task.getMain().getCurrency2().getCode());
        query.setParameter(3, task.getHeader().getCrmcurrencylist());
        query.setParameter(4, status);
        query.setParameter(5, task.getMain().getSum());
        query.executeUpdate();
        query = em.createNativeQuery("insert into sysdba.FB_SPO_LIMIT_ACCOUNT (FB_LIMITID, ACCOUNTID) values (?, ?)");
        query.setParameter(1, task.getHeader().getCrmid());
        for (TaskContractor tc : task.getContractors()){
            query.setParameter(2, tc.getOrg().getId());
            query.executeUpdate();
        }
    }
    @Override @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void exportProductBeforeKK(Task task) {
        //FB_SPO_BEFORE_KK
        EntityManager em = factoryCRM.createEntityManager();
        
        String sql = "insert into sysdba.fb_spo_before_kk (fb_spo_opportunityid, quantity, " +
        "quantityvydachi, quantity_zad, days, stavrazwrk, wrkliborsrok, productid) values(";
        sql += "'"+task.getHeader().getCrmQueueId()+"', ";
        sql += task.getMain().getSum()+", ";
        sql += task.getMain().getLimitIssueSum()+", ";
        sql += task.getMain().getDebtLimitSum()+", ";
        sql += task.getMain().getPeriod()+", ";
        sql += task.getTaskProcent().getProcent()+", ";
        sql += "'', ";
        String productid = "";
        if (task.getHeader().getOpportunityTypes().size()>0) 
            productid = task.getHeader().getOpportunityTypes().get(0).getId();
        sql += "'"+productid+"') ";
        Query query = em.createNativeQuery(sql);
        query.executeUpdate();
        query = em.createNativeQuery("insert into sysdba.fb_spo_opportunity_comiss (opportunity_comissid, source_name, " +
                "comiss_name, comiss_value, comiss_unit, comiss_base, comiss_periodichnost) values(?, ?, ?, ?, ?, ?, ?)");
        for (CommissionDeal cd : task.getCommissionDealList()){
            query.setParameter(1, task.getHeader().getCrmQueueId());
            query.setParameter(2, "BEFORE_KK");
            query.setParameter(3, cd.getName().getName());
            query.setParameter(4, cd.getValue());
            query.setParameter(5, cd.getCurrency().getCode());
            query.setParameter(6, cd.getCalcBase().getName());
            query.setParameter(7, cd.getProcent_order().getName());
            query.executeUpdate();
        }
        
    }
    @Override @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void exportRating(CRMRating rating) {
        EntityManager em = factoryCRM.createEntityManager();
        Query q = em.createNativeQuery("insert into sysdba.FB_SPO_RATING_NEW (" +
        		"ACCOUNTID,COUNT_RATING_VAL,COUNT_RATING,COUNT_RATING_DATE,COUNT_RATING_QUARTER," +
        		"EXPERT_RATING_VAL,EXPERT_RATING,EXPERT_RATING_DATE,СС_RATING_VAL,CC_RATING,CC_RATING_DATE,opportunityid)" +
        		" values (?,?,?,?,?,?,?,?,?,?,?,?)");
        q.setParameter(1, rating.getAccountid());
        q.setParameter(2, rating.getCount_rating_val());
        q.setParameter(3, rating.getCount_rating());
        q.setParameter(4, rating.getCount_rating_date());
        q.setParameter(5, rating.getCount_rating_quarter());
        q.setParameter(6, rating.getExpert_rating_val());
        q.setParameter(7, rating.getExpert_rating());
        q.setParameter(8, rating.getExpert_rating_date());
        q.setParameter(9, rating.getCc_rating_val());
        q.setParameter(10, rating.getCc_rating());
        q.setParameter(11, rating.getCc_rating_date());
        q.setParameter(12, rating.getOpportunityid());
        q.executeUpdate();
    }
    @Override @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void exportProduct(Task task) {
        TaskStatusReturn status = task.getTaskStatusReturn();
        EntityManager em = factoryCRM.createEntityManager();
        ProductQueueJPA queue = em.find(ProductQueueJPA.class, task.getHeader().getCrmQueueId());
        if(status!=null && status.getStatusReturn()!= null){
            queue.setCALLBACK(status.getStatusReturn().getId());
        }
        queue.setCALLBACKDATE(new java.util.Date());
        TaskProduct ot=(TaskProduct)task.getHeader().getOpportunityTypes().get(0);
        String productid = (ot==null ? "" :ot.getId());

        FbSpoOpportunityProductNewJPA productNew = new FbSpoOpportunityProductNewJPA();
        productNew.getPk().setId(task.getHeader().getCrmQueueId());
        productNew.setUNIT(task.getMain().getCurrency2().getCode());
        productNew.setQUANTITY(task.getMain().getSum());
        productNew.setQUANTITYVYDACHI(task.getMain().getLimitIssueSum());
        productNew.setQUANTITY_ZAD(task.getMain().getDebtLimitSum());
        productNew.setLV(task.getMain().isLimitIssue()?"T":"F");
        productNew.setLZ(task.getMain().isDebtLimit()?"T":"F");
        productNew.setACTIVEBEGIN(task.getMain().getValidfrom());
        productNew.setACTIVEEND(task.getMain().getValidto());
        productNew.setDAYS(task.getMain().getPeriod());
        productNew.setSTAVRAZWRK(task.getTaskProcent().getProcent());
        productNew.setPERIODISP(task.getMain().getUseperiod());//PERIODISP
        productNew.setPOGAS(task.getPrincipalPay().getPeriodOrder().getShortName());//POGAS
        productNew.setDATEKK(task.getTaskStatusReturn().getDateReturn());//DATEKK
        productNew.setSTAVFLOATFIXEDWRK(task.getTaskProcent().getValue());//STAVFLOATFIXEDWRK
        productNew.getPk().setPRODUCTID(productid);
        em.persist(productNew);

        try {
            CrmFacadeLocal crmFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(CrmFacadeLocal.class);
            crmFacadeLocal.exportProductReason(task);
            crmFacadeLocal.exportProductCommission(task);
            crmFacadeLocal.exportProductPaymentSchedule(task, productid);
            crmFacadeLocal.exportProductTrance(task);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
    @Override @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void exportProductCommission(Task task) {
        try {
            EntityManager em = factoryCRM.createEntityManager();
        	for (CommissionDeal cd : task.getCommissionDealList()){
        		Query q = em.createNativeQuery("insert into sysdba.FB_SPO_OPPORTUNITY_COMISS (OPPORTUNITY_COMISSID,SOURCE_NAME,COMISS_NAME," +
                        "COMISS_VALUE,COMISS_UNIT,COMISS_BASE,COMISS_PERIODICHNOST) values (?,?,?,?,?,?,?)");
        		q.setParameter(1, task.getHeader().getCrmQueueId());
        		q.setParameter(2, "PRODUCT_NEW");
        		q.setParameter(3, cd.getName().getId());
        		q.setParameter(4, cd.getValue());
        		q.setParameter(5, cd.getCurrency().getCode());
        		q.setParameter(6, cd.getComissionSize().getId());
        		q.setParameter(7, cd.getProcent_order().getId());
        		q.executeUpdate();
        	}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
		}
    }
    @Override @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void exportProductPaymentSchedule(Task task, String productid) {
        try {
            EntityManager em = factoryCRM.createEntityManager();
            Query query = em.createNativeQuery("insert into sysdba.FB_SPO_GRAFICPOGASH (fb_spo_opportunityid," +
            		"OPPPRODUCTID,STARTDATE,FINISHDATE,SUMMA,UNIT) values(?,?,?,?,?,?)");
        	for (PaymentSchedule ps : task.getPaymentScheduleList()){
        	    query.setParameter(1, task.getHeader().getCrmQueueId());
        	    query.setParameter(2, productid);
        	    query.setParameter(3, ps.getFromDate());
        	    query.setParameter(4, ps.getToDate());
        	    query.setParameter(5, ps.getAmount());
        	    query.setParameter(6, task.getMain().getCurrency2().getCode());
        	    query.executeUpdate();
        	}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
		}
    }
    @Override @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void exportProductTrance(Task task) {
        try {
            EntityManager em = factoryCRM.createEntityManager();
        	long index=1;
        	for (Trance t : task.getTranceList()) {
        		FbSpoOpportunityProductNewJPA tranceNew = new FbSpoOpportunityProductNewJPA();
        		tranceNew.getPk().setFB_TRANCHID(index++);
        		tranceNew.setACTIVEBEGIN(t.getUsedatefrom());
        		tranceNew.setACTIVEEND(t.getUsedate());
        		tranceNew.setQUANTITY(t.getSum());
        		tranceNew.setUNIT(t.getCurrency().getCode());
        		tranceNew.getPk().setId(task.getHeader().getCrmQueueId());
        		em.persist(tranceNew);
        	}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
		}
    }
    @Override @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void exportProductReason(Task task) {
        try {
            EntityManager em = factoryCRM.createEntityManager();
        	Query query = em.createNativeQuery("insert into sysdba.FB_SPO_OPPORTUNITY_DES_REAZON (fb_spo_opportunityid,reazon) values(?,?)");
        	query.setParameter(1, task.getHeader().getCrmQueueId());
        	query.setParameter(2, task.getTaskStatusReturn().getStatusReturn().getDescription());
        	query.executeUpdate();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
		}
    }
    @Override
    public UserJPA[] getUserListForLoadProduct(String productid) {
        return null;
    }
    @Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public NetworkWagerJPA[] getNetworkWagerByProductQueueId(String queueId) {
        EntityManager em = factoryCRM.createEntityManager();
        Query query = em.createNativeQuery("select w.fb_network_wagerid from sysdba.fb_spo_opportunity q "+
                "inner join sysdba.v_spo_opportunity_product o on o.OPPORTUNITYID=q.opportunityid "+
                "inner join sysdba.v_spo_fb_network_wager w on w.oppproductid=o.OPPPRODUCTID "+
                "where q.fb_spo_opportunityid=?");
        query.setParameter(1, queueId);
        @SuppressWarnings({ "unchecked" })
        List<String> list = query.getResultList();
        ArrayList<NetworkWagerJPA> listJPA= new ArrayList<NetworkWagerJPA>();
        for(String id:list)listJPA.add(em.find(NetworkWagerJPA.class, id));
        NetworkWagerJPA[] array = new NetworkWagerJPA[listJPA.size()];
        return listJPA.toArray(array);
    }
    @Override
    public String inLimit(String opportunityId) {
        EntityManager em = factoryCRM.createEntityManager();
        Query query = em.createNativeQuery("select FB_LIMITID from sysdba.V_SPO_FB_OPP_LIMIT o where o.opportunityid=?");
        query.setParameter(1, opportunityId);
        @SuppressWarnings("unchecked")
        List<String> list = query.getResultList();
        for(String id:list) return id;
        return null;
    }
}

