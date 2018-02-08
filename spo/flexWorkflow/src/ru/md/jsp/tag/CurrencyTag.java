package ru.md.jsp.tag;

import java.util.LinkedHashMap;

import ru.masterdm.compendium.domain.Currency;
import ru.masterdm.compendium.model.CompendiumCrmActionProcessor;

public class CurrencyTag  extends AbstractSelectTag {
    private static final long serialVersionUID = 1L;
    private boolean withoutprocent=false;
    private boolean withyearprocent=false;
    private boolean with365=false;
    private boolean with_empty_field=false;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public LinkedHashMap getHashMap() {
        try{
            LinkedHashMap<String, String> hashmap = new LinkedHashMap<String, String>();
            CompendiumCrmActionProcessor compenduimCRM = (CompendiumCrmActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumCrm");
            Currency[] currencyList = compenduimCRM.findCurrencyList("", null);
            if(this.with_empty_field) hashmap.put("", "   ");
            for (Currency currency : currencyList){
                hashmap.put(currency.getCode().toUpperCase(), currency.getCode().toUpperCase());
            }
            if(!this.withoutprocent)hashmap.put("%", "%");
            if(this.withyearprocent)hashmap.put("%годовых", "% годовых");
            if(this.with365){
            	hashmap.put("1/365", "1/365");
            	hashmap.put("1/366", "1/366");
            }
            	
            return hashmap;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Error("CurrencyTag doStartTag error " + ex.getMessage());
        }
    }

    public boolean isWithoutprocent() {
        return withoutprocent;
    }

    public void setWithoutprocent(boolean withoutprocent) {
        this.withoutprocent = withoutprocent;
    }

	public boolean isWithyearprocent() {
		return withyearprocent;
	}

	public void setWithyearprocent(boolean withyearprocent) {
		this.withyearprocent = withyearprocent;
	}

	public boolean isWith_empty_field() {
		return with_empty_field;
	}

	public void setWith_empty_field(boolean with_empty_field) {
		this.with_empty_field = with_empty_field;
	}

	public boolean isWith365() {
		return with365;
	}

	public void setWith365(boolean with365) {
		this.with365 = with365;
	}

}
