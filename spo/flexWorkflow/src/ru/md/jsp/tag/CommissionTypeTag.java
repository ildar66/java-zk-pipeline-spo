package ru.md.jsp.tag;

import java.util.LinkedHashMap;

import ru.masterdm.compendium.domain.crm.CommissionType;
import ru.masterdm.compendium.model.CompendiumCrmActionProcessor;

public class CommissionTypeTag extends AbstractSelectTag {
    private static final long serialVersionUID = 1L;

    @Override
    public LinkedHashMap<String,String> getHashMap() {
        try{
            LinkedHashMap<String,String> hashmap = new LinkedHashMap<String,String>();
            CompendiumCrmActionProcessor processor = 
                (CompendiumCrmActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumCrm");
            CommissionType[] ct =processor.findComissionTypeList("", "c.name");
            hashmap.put("-1", " ");
            for (int i=0;i<ct.length;i++){
                hashmap.put(ct[i].getId(), ct[i].getName());
            }
            return hashmap;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Error("ComissionTypeTag doStartTag error " + ex.getMessage());
        }
    }
}
