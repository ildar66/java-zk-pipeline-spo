package ru.md.jsp.tag;

import java.util.LinkedHashMap;

import ru.masterdm.compendium.domain.crm.PatternPaidPercentType;
import ru.masterdm.compendium.model.CompendiumCrmActionProcessor;

public class PatternPaidPercentTag  extends AbstractSelectTag {
    private static final long serialVersionUID = 1L;

    @Override
    public LinkedHashMap<String, String> getHashMap() {
        try{
            LinkedHashMap<String,String> hashmap = new LinkedHashMap<String,String>();
            CompendiumCrmActionProcessor processor = 
                (CompendiumCrmActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumCrm");
            PatternPaidPercentType[] list=processor.findPatternPaidPercentTypeList("", "c.name");
            hashmap.put("-1","  ");
            for (int i=0;i<list.length;i++){
                hashmap.put(list[i].getId(), list[i].getName());
            }
            return hashmap;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Error("PatternPaidPercentType doStartTag error " + ex.getMessage());
        }
    }

}
