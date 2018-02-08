package ru.md.jsp.tag;

import java.util.LinkedHashMap;

import ru.masterdm.compendium.model.CompendiumSpoActionProcessor;

public class LimitTypeTag extends AbstractSelectTag {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    @Override
    public LinkedHashMap<String, String> getHashMap() {
        try{
            LinkedHashMap<String,String> hashmap = new LinkedHashMap<String,String>();
            CompendiumSpoActionProcessor processor = 
                (CompendiumSpoActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumSpo");

            hashmap.put("-1", "    ");
            for (ru.masterdm.compendium.domain.spo.LimitType entity : processor.findLimitTypeList("", null)){
                hashmap.put(entity.getId().toString(), entity.getName());
            }
            return hashmap;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Error("LimitType doStartTag error " + ex.getMessage());
        }
    }

}
