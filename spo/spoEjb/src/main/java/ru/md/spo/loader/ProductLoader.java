package ru.md.spo.loader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import ru.masterdm.compendium.domain.Currency;
import ru.masterdm.compendium.domain.crm.ComissionSize;
import ru.masterdm.compendium.domain.crm.CommissionType;
import ru.masterdm.compendium.domain.crm.Organization;
import ru.masterdm.compendium.domain.crm.PatternPaidPercentType;
import ru.masterdm.compendium.domain.crm.TargetType;
import ru.masterdm.compendium.domain.spo.CRMRepayment;
import ru.masterdm.compendium.domain.spo.ContractorType;
import ru.masterdm.compendium.model.CompendiumCrmActionProcessor;
import ru.masterdm.compendium.model.CompendiumSpoActionProcessor;
import ru.md.crm.dbobjects.NetworkWagerJPA;
import ru.md.spo.dbobjects.FactPercentJPA;
import ru.md.spo.dbobjects.TaskJPA;
import ru.md.spo.ejb.CrmFacadeLocal;
import ru.md.spo.ejb.TaskFacadeLocal;

import com.vtb.domain.CommissionDeal;
import com.vtb.domain.CrmComiss;
import com.vtb.domain.CrmGraph;
import com.vtb.domain.EarlyPayment;
import com.vtb.domain.Main;
import com.vtb.domain.OtherCondition;
import com.vtb.domain.PaymentSchedule;
import com.vtb.domain.SpoAccount;
import com.vtb.domain.SpoOpportunity;
import com.vtb.domain.SpoOpportunityProduct;
import com.vtb.domain.Task;
import com.vtb.domain.TaskContractor;
import com.vtb.domain.TaskProduct;

/**
 * Загрузка сделки из CRM.
 * @author Andrey Pavlenko
 *
 */
public class ProductLoader {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ProductLoader.class.getName());
    private SpoOpportunity spoOpportunity = null;//таблица обмена статусами
    private SpoAccount accountVO = null;//контрагент
    private SpoOpportunityProduct productVO = null;//продукт сделки
    private String errorMessage="";//Сообщение об ошибке
    
    public Task toTask() throws Exception{
        CompendiumCrmActionProcessor compenduimCRM = (CompendiumCrmActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumCrm");
        CompendiumSpoActionProcessor compenduimSPO = (CompendiumSpoActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumSpo");
        Task task = new Task(null);                
        task.getHeader().setProcessType("Сделка");
        ArrayList<ContractorType> ct = new ArrayList<ContractorType>();
        ct.add(new ContractorType(1L));/*всегда заемщик*/
        task.getContractors().add(new TaskContractor(new Organization(accountVO.getAccountID()),ct, null));
        Main main = task.getMain();
        main.setSum(productVO.getQuantity());
        main.setCurrency(new Currency(productVO.getUnit()));
        main.setLimitIssueSum(productVO.getQuantityVydachi());    // сумма лимита выдачи
        main.setDebtLimitSum(productVO.getQuantityZad());     // сумма лимита задолженности
        main.setLimitIssue(productVO.isLV());        // флаг, вводить ли лимит выдачи (LV in CRM)
        main.setDebtLimit(productVO.isLZ());         // флаг, вводить ли лимит задолженности (LZ in CRM)
        main.correctQuantityData();
        main.setPeriod(new Integer(productVO.getDays()));
        //период использования - Fb_opportunity_product.peiodisp
        try{
            /*TODO if(productVO.getPERIODISP() != null)
                task.getMain().setUseperiod(Integer.valueOf(productVO.getPERIODISP()));*/
        } catch(NumberFormatException e){
            //throw new ModelException("Неправильный формат периода использования " + productVO.getPERIODISP());
        }
        task.setOPPORTUNITYID(spoOpportunity.getOpportunityID());
        task.getHeader().setCrmcode(productVO.getId());
        //Процентные ставки
        try {
            CrmFacadeLocal crm = com.vtb.util.EjbLocator.getInstance().getReference(CrmFacadeLocal.class);
            NetworkWagerJPA[] wagers = crm.getNetworkWagerByProductQueueId(task.getHeader().getCrmQueueId());
            TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
            TaskJPA taskJPA = taskFacadeLocal.getTask(task.getId_task());
            for(NetworkWagerJPA wager : wagers){
                FactPercentJPA p = new FactPercentJPA();
                
                p.setEnd_date(wager.getEND_DATE());
                p.setStart_date(wager.getSTART_DATE());
                p.setFondrate(wager.getSTAVFLOATFIXEDWRK()==null?null:wager.getSTAVFLOATFIXEDWRK().doubleValue());
                p.setTask(taskJPA);
                taskFacadeLocal.persist(p);
                taskJPA.getFactPercents().add(p);
            }
        } catch(Exception e) {
            LOGGER.log(Level.WARNING, "Ошибка загрузки процентной ставки: "+e.getMessage(), e);
            e.printStackTrace();
        }
        //условия досрочного погашения
        task.getEarlyPaymentList().add(new EarlyPayment(
                !productVO.isDOSROCH_POGAS()?"3":(productVO.isBANK_ACPT()?"2":"1"), 
                "", 
                "", 
                "Не указано", "", (long) 0));
        //комиссии
        for(CrmComiss comiss : productVO.getComissList()) {
            ComissionSize base = new ComissionSize(comiss.getComiss_base());
            PatternPaidPercentType pppt = new PatternPaidPercentType(comiss.getComiss_periodichnost());
            try {
                base = compenduimCRM.findComissionSizeList(base.getId(), null)[0];
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, e.getMessage(), e);
            }
            try {
                pppt = compenduimCRM.findPatternPaidPercentTypeList(pppt.getId(), null)[0];
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, e.getMessage(), e);
            }
            if (comiss.getComiss_unit()!=null && comiss.getComiss_unit().startsWith("%"))
                comiss.setComiss_unit("%");
            if (comiss.getComiss_unit()!=null && comiss.getComiss_unit().length()>3){
                LOGGER.log(Level.WARNING,"Некорректная валюта комиссии. Пропускаем загрузку этой комиссии. " + 
                        comiss.getComiss_unit());
            } else {
                task.getCommissionDealList().add(
                        new CommissionDeal(
                                null, comiss.getNotes(),new Currency(comiss.getComiss_unit()), 
                                new CommissionType(comiss.getComiss_code()), comiss.getComiss_value(),
                                pppt, null, base, "")
                 );
            }
        }
        //условия
        Set<String> set = productVO.getConditionMap().keySet();
        Iterator<String> iter = set.iterator();
        while (iter.hasNext()) {
            String code = (String) iter.next();
            if(code.length()<4)continue;//VTBSPO-1121
            if(code.substring(2, 4).equalsIgnoreCase("dr"))
                task.getOtherCondition().add(new OtherCondition(4L, productVO.getConditionMap().get(code)));
            if(code.substring(3,4).equalsIgnoreCase("d"))
                task.getOtherCondition().add(new OtherCondition(3L, productVO.getConditionMap().get(code)));
            if(code.substring(3,4).equalsIgnoreCase("o"))
                task.getOtherCondition().add(new OtherCondition(1L, productVO.getConditionMap().get(code)));
        }
        main.setValidto(productVO.getActiveEnd());
        main.setExchangeRate(productVO.getUNITCOURSE());
        task.setInLimit(productVO.getLimitid());
        String productName = productVO.getProductname(); //название продукта
        task.getHeader().setCrmcode(productVO.getNum());
        try{
            TaskFacadeLocal taskFacad = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
            String id = taskFacad.getProductTypeIdByName(productName);
            if (id!=null)
                task.getHeader().getOpportunityTypes().add(new TaskProduct(id));
        } catch(Exception e){
            LOGGER.log(Level.WARNING,"В справочниках СПО не существует вида продукта "+productName);
        }
        //график платежей
        if(productVO.getPOGAS()!=null){
            List<CRMRepayment> crmRepaymentList = compenduimSPO.findCRMRepaymentList(productVO.getPOGAS(), null);
            if (crmRepaymentList.size() !=1 ) {
                LOGGER.log(Level.WARNING,"В справочнике 'Периодичность погашения основного долга' не существует поля  "+productVO.getPOGAS());
            } else {
                task.getPrincipalPay().setPeriodOrder(crmRepaymentList.get(0));
            }
            Double amount = Double.valueOf(0);
            java.sql.Date firstPayDate=null;
            java.sql.Date finalPayDate=null;
            for(CrmGraph graph : productVO.getGraphList()){
                if(graph.getFinalPayDate()==null || graph.getFirstPayDate()==null || graph.getAmount()== null)
                    continue;
                PaymentSchedule p = new PaymentSchedule();
                p.setAmount(graph.getAmount());
                p.setFromDate(graph.getFirstPayDate());
                p.setToDate(graph.getFinalPayDate());
                p.setCurrency(new Currency(graph.getUnit()));
                task.getPaymentScheduleList().add(p);
                amount += p.getAmount();
                if (firstPayDate==null || firstPayDate.after(p.getFromDate()))
                    firstPayDate=p.getFromDate();
                if (finalPayDate==null || finalPayDate.before(p.getToDate()))
                    finalPayDate=p.getToDate();
            }
            task.getPrincipalPay().setFinalPayDate(finalPayDate);
            task.getPrincipalPay().setFirstPayDate(firstPayDate);
            task.getPrincipalPay().setAmount(amount);
        }
        main.setRenewable(true);
        main.setMayBeRenewable(true);
        // цели кредитования
        if (productVO.getSpravparam().length() > 0) {
            TargetType[] ttList = compenduimCRM.findTargetTypes(productVO.getSpravparam(), null);
            if (ttList == null || ttList.length == 0){
                LOGGER.log(Level.WARNING, "Не могу загрузить цели кредитования. " + "В справочниках CRM нет цели кредитования '"
                        + productVO.getSpravparam() + "'");
            } else {
                //task.getTarget().add(new TaskTarget(null, true, ttList[0]));
                //task.getMain().getOtherGoals().add(productVO.getSpravparam());
            }
        }

        return task;
    }
    public ProductLoader(SpoOpportunity spoOpportunity) {
        super();
        this.spoOpportunity = spoOpportunity;
    }
    /** контрагент */
    public SpoAccount getAccountVO() {
        return accountVO;
    }

    public void setAccountVO(SpoAccount accountVO) {
        this.accountVO = accountVO;
    }

    /** таблица обмена статусами */
    public SpoOpportunity getSpoOpportunity() {
        return spoOpportunity;
    }

    /** продукт сделки */
    public SpoOpportunityProduct getProductVO() {
        return productVO;
    }

    public void setProductVO(SpoOpportunityProduct productVO) {
        this.productVO = productVO;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param errorMessage the errorMessage to set
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
