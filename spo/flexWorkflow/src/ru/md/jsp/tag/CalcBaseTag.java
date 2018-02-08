package ru.md.jsp.tag;
import java.util.LinkedHashMap;

import ru.masterdm.compendium.domain.spo.CalcBase;
import ru.masterdm.compendium.model.CompendiumSpoActionProcessor;

public class CalcBaseTag extends AbstractSelectTag {
    private static final long serialVersionUID = 1L;

    @Override
    public LinkedHashMap<String,String> getHashMap() {
        try{
            LinkedHashMap<String,String> hashmap = new LinkedHashMap<String,String>();
            CompendiumSpoActionProcessor processor = 
                (CompendiumSpoActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumSpo");
            CalcBase[] cb = processor.findCalcBaseList("", "c.name");
            hashmap.put("-1", " ");
            for (CalcBase entity : cb){
                hashmap.put(entity.getId().toString(), entity.getName());
            }
            return hashmap;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Error("CalcBaseTag doStartTag error " + ex.getMessage());
        }
    }
}
