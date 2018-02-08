package ru.md.jsp.tag;

import java.util.LinkedHashMap;

public class Quality_categoryTag extends AbstractSelectTag {
    private static final long serialVersionUID = 1L;

    @Override
    public LinkedHashMap<String, String> getHashMap() {
        try{
            LinkedHashMap<String,String> hashmap = new LinkedHashMap<String,String>();
            hashmap.put("", "");
            hashmap.put("I","I");
            hashmap.put("II","II");
            hashmap.put("III","III");
            hashmap.put("IV","IV");
            hashmap.put("V","V");
            return hashmap;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Error("Quality_categoryTag doStartTag error " + ex.getMessage());
        }
    }
}
