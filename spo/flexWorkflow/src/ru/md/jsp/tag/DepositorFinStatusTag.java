package ru.md.jsp.tag;

import java.util.LinkedHashMap;

import ru.md.dict.dbobjects.DepositorFinStatusJPA;
import ru.md.helper.TaskHelper;

public class DepositorFinStatusTag extends AbstractSelectTag {
    private static final long serialVersionUID = 1L;

    @Override
    public LinkedHashMap<String,String> getHashMap() {
        try{
            LinkedHashMap<String,String> hashmap = new LinkedHashMap<String,String>();
            hashmap.put("-1", " ");
            for (DepositorFinStatusJPA entity : TaskHelper.dict().findDepositorFinStatus()){
                hashmap.put(entity.getId().toString(), entity.getStatus());
            }
            return hashmap;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Error("DepositorFinStatusTag doStartTag error " + ex.getMessage());
        }
    }
}
