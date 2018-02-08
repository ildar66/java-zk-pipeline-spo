package ru.md.jsp.tag;

import java.util.LinkedHashMap;

import ru.masterdm.compendium.domain.crm.ComissionSize;
import ru.masterdm.compendium.model.CompendiumCrmActionProcessor;

public class ComissionSizeTag extends AbstractSelectTag {
    private static final long serialVersionUID = 1L;

    @Override
    public LinkedHashMap<String,String> getHashMap() {
        try{
            LinkedHashMap<String,String> hashmap = new LinkedHashMap<String,String>();
            CompendiumCrmActionProcessor processor = 
                (CompendiumCrmActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumCrm");
            ComissionSize[] cz =processor.findComissionSizeList("", "c.name");
            hashmap.put("-1", " ");
            for (ComissionSize entity : cz){
                hashmap.put(entity.getId().toString(), entity.getName());
            }
            return hashmap;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Error("ComissionSizeTag doStartTag error " + ex.getMessage());
        }
    }
}
