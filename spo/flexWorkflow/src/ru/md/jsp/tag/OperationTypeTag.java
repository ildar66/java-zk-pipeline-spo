package ru.md.jsp.tag;

import java.util.LinkedHashMap;

import ru.md.dict.dbobjects.OperationTypeJPA;
import ru.md.helper.TaskHelper;

public class OperationTypeTag extends AbstractSelectTag {
    private static final long serialVersionUID = 1L;

    @Override
    public LinkedHashMap<String,String> getHashMap() {
        try{
            LinkedHashMap<String,String> hashmap = new LinkedHashMap<String,String>();
            hashmap.put("-1", " ");
            for (OperationTypeJPA entity : TaskHelper.dict().findOperationType()){
                hashmap.put(entity.getId().toString(), entity.getName());
            }
            return hashmap;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Error("OperationTypeTag doStartTag error " + ex.getMessage());
        }
    }
}
