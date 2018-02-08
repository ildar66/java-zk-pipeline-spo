package ru.md.crm.dbobjects;

import java.io.Serializable;
import java.util.ArrayList;

import ru.masterdm.compendium.domain.Currency;
import ru.masterdm.compendium.domain.crm.Organization;
import ru.masterdm.compendium.domain.spo.ContractorType;

import com.vtb.domain.Task;
import com.vtb.domain.TaskContractor;

public class LimitQueueTO implements Serializable {
    private static final long serialVersionUID = 1L;
    public LimitQueueJPA queue;
    public LimitJPA limit;
    public Task toTask(){
        Task task=new Task();
        //по умолчанию для нового саблимита берем ежемесячные уплата процентов
        task.getHeader().setIdLimitType(1);
        task.getMain().setSum(limit.getSum());
        task.getMain().setCurrency(new Currency(limit.getCurr()));
        ArrayList<ContractorType> ct = new ArrayList<ContractorType>();
        ct.add(new ContractorType(1L));/*всегда заемщик*/
        for (AccountJPA org : limit.getAccounts()){
            task.getContractors().add(new TaskContractor(
                    new Organization(org.getId()), 
                    ct, 
                    null));
        }
        task.getHeader().setCrmid(limit.getLimitid());
        task.getHeader().setCrmcode(limit.getCode());
        task.getHeader().setCrmlimitname(limit.getLimit_name());
        task.getHeader().setCrmcurrencylist(limit.getCurrs());
        task.getHeader().setPlace(null);
        task.getHeader().setCrmstatus(limit.getStatus());
        task.getMain().setRenewable(true);
        task.getMain().setMayBeRenewable(true);
        return task;
    }
}
