package ru.md.jsp.tag;

import java.util.LinkedHashMap;

import ru.masterdm.compendium.domain.crm.PaymentFrequency;
import ru.masterdm.compendium.model.CompendiumCrmActionProcessor;

public class PaymentFrecuencyTag  extends AbstractSelectTag {
    private static final long serialVersionUID = 1L;

    @Override
    public LinkedHashMap<String, String> getHashMap() {
        try{
            LinkedHashMap<String,String> hashmap = new LinkedHashMap<String,String>();
            CompendiumCrmActionProcessor processor  = (CompendiumCrmActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumCrm");
            PaymentFrequency[] cz =  processor.findPaymentFrequencyList(new PaymentFrequency(), null);
            hashmap.put("-1","  ");
            for (PaymentFrequency entity : cz){
                hashmap.put(entity.getId(), entity.getText());
            }
            return hashmap;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Error("PaymentFrecuencyTag doStartTag error " + ex.getMessage());
        }
    }
}
