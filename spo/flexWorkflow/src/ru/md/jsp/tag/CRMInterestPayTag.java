package ru.md.jsp.tag;
import java.util.LinkedHashMap;
import java.util.List;

import ru.masterdm.compendium.domain.spo.CRMInterestPay;
import ru.masterdm.compendium.model.CompendiumSpoActionProcessor;

public class CRMInterestPayTag extends AbstractSelectTag {
    private static final long serialVersionUID = 1L;

    @Override
    public LinkedHashMap<String,String> getHashMap() {
        try{
            LinkedHashMap<String,String> hashmap = new LinkedHashMap<String,String>();
            CompendiumSpoActionProcessor processor = 
                (CompendiumSpoActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumSpo");
            List<CRMInterestPay> cb = processor.findCRMInterestPayList("", "c.name");
            hashmap.put("-1", " ");
            for (CRMInterestPay entity : cb){
                if (entity.getIsActive().intValue()==0) continue;
                hashmap.put(entity.getId().toString(), entity.getName());
            }
            return hashmap;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Error("CalcBaseTag doStartTag error " + ex.getMessage());
        }
    }
}
