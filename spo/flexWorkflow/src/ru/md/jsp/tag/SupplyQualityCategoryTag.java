package ru.md.jsp.tag;

import java.util.LinkedHashMap;

import ru.masterdm.compendium.model.CompendiumActionProcessor;

public class SupplyQualityCategoryTag extends AbstractSelectTag {
    private static final long serialVersionUID = 1L;

    @Override
    public LinkedHashMap<String,String> getHashMap() {
        try{
            LinkedHashMap<String,String> hashmap = new LinkedHashMap<String,String>();
            CompendiumActionProcessor processor = 
                (CompendiumActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("Compendium");

            for (ru.masterdm.compendium.domain.spo.QualityCategory entity : processor.findQualityCategoryList("", null)){
                hashmap.put(entity.getId().toString(), entity.getName());
            }
            return hashmap;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Error("SupplyType doStartTag error " + ex.getMessage());
        }
    }
}
