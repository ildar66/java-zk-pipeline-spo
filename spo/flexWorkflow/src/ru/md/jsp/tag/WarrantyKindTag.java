package ru.md.jsp.tag;

import java.util.Iterator;
import java.util.LinkedHashMap;

import com.vtb.domain.Warranty;

public class WarrantyKindTag  extends AbstractSelectTag {
    private static final long serialVersionUID = 1L;

    @Override
    public LinkedHashMap<String,String> getHashMap() {
        try{
            LinkedHashMap<String,String> hashmap = new LinkedHashMap<String,String>();
            hashmap.put("-1", " ");
            Iterator<String> it = Warranty.WarrantyKind.iterator();
            while(it.hasNext()){
            	String warrantyKind = it.next();
                hashmap.put(warrantyKind,warrantyKind);
            }
            return hashmap;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Error("WarrantyKindTag doStartTag error " + ex.getMessage());
        }
    }
}
