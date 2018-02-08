package ru.md.jsp.tag;

import java.util.LinkedHashMap;

import ru.masterdm.compendium.domain.rating.LiquidityLevel;
import ru.masterdm.compendium.model.CompendiumRatingActionProcessor;

public class LiquidityLevelTag extends AbstractSelectTag {
    private static final long serialVersionUID = 1L;

    @Override
    public LinkedHashMap<String,String> getHashMap() {
        try{
            LinkedHashMap<String,String> hashmap = new LinkedHashMap<String,String>();
            CompendiumRatingActionProcessor compenduim = (CompendiumRatingActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumRating");
            hashmap.put("-1", " ");
            for (LiquidityLevel ll : compenduim.findLiquidityLevelPage(new LiquidityLevel(), 0, 9000, "").getList()){
                hashmap.put(ll.getId().toString(), ll.getName());
            }
            return hashmap;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Error("LiquidityLevelTag doStartTag error " + ex.getMessage());
        }
    }
}
