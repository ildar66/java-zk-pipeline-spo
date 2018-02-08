package ru.md.jsp.tag;

import java.util.LinkedHashMap;

import ru.masterdm.spo.utils.SBeanLocator;
import ru.md.domain.Product;
import ru.md.helper.TaskHelper;
import ru.md.persistence.ProductMapper;
import ru.md.spo.dbobjects.ProductTypeJPA;

public class ProductTypeTag extends AbstractSelectTag {
    private static final long serialVersionUID = 1L;
    private Long task;
    
    @Override
    public LinkedHashMap<String, String> getHashMap() {
        try{
        	LinkedHashMap<String,String> hashmap = new LinkedHashMap<String,String>();
        	hashmap.put("-1", " ");
            ProductMapper mapper = (ProductMapper) SBeanLocator.singleton().getBean("productMapper");
       		for (Product entity : mapper.getProducts()) hashmap.put(entity.getProductid(), entity.getName());
            return hashmap;

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Error("ProductTypeTag doStartTag error " + ex.getMessage());
        }
    }

    public Long getTask() {
        return task;
    }

    public void setTask(Long task) {
        this.task = task;
    }
}
