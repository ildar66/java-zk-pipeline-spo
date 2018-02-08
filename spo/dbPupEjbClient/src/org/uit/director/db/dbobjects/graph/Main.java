package org.uit.director.db.dbobjects.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: pd199968
 * Date: 08.09.2008
 * Time: 15:12:28
 * To change this template use File | Settings | File Templates.
 */


public    class Main {

    static List<Map<String, String>> init1(){
    	
        List<Map<String, String>> inputList= new ArrayList<Map<String, String>>();
        
            HashMap<String, String> var = new HashMap<String, String>();
            var.put("id_var_parrent","1");
            var.put("id_var_child","2");
            var.put("order","1");
            inputList.add(var);

            var = new HashMap<String, String>();
            var.put("id_var_parrent","1");
            var.put("id_var_child","3");
            var.put("order","3");
            inputList.add(var);

            var = new HashMap<String, String>();
            var.put("id_var_parrent","1");
            var.put("id_var_child","4");
            var.put("order","2");
            inputList.add(var);

            var = new HashMap<String, String>();
            var.put("id_var_parrent","2");
            var.put("id_var_child","5");
            var.put("order","1");
            inputList.add(var);

            var = new HashMap<String, String>();
            var.put("id_var_parrent","3");
            var.put("id_var_child","5");
            var.put("order","1");
            inputList.add(var);
            
            var = new HashMap<String, String>();
            var.put("id_var_parrent","4");
            var.put("id_var_child","3");
            var.put("order","1");
            inputList.add(var);
            
            var = new HashMap<String, String>();
            var.put("id_var_parrent","5");
            var.put("id_var_child","6");
            var.put("order","1");
            inputList.add(var);
            
            var = new HashMap<String, String>();
            var.put("id_var_parrent","6");
            var.put("id_var_child","4");
            var.put("order","1");
            inputList.add(var);
            
            
            var = new HashMap<String, String>();
            var.put("id_var_parrent","7");
            var.put("id_var_child","8");
            var.put("order","1");
            inputList.add(var);
            
            var = new HashMap<String, String>();
            var.put("id_var_parrent","9");
            var.put("id_var_child","10");
            var.put("order","1");
            inputList.add(var);
            
            var = new HashMap<String, String>();
            var.put("id_var_parrent","9");
            var.put("id_var_child","11");
            var.put("order","2");
            inputList.add(var);
            
            var = new HashMap<String, String>();
            var.put("id_var_parrent","9");
            var.put("id_var_child","12");
            var.put("order","3");
            inputList.add(var);
            
            var = new HashMap<String, String>();
            var.put("id_var_parrent","11");
            var.put("id_var_child","12");
            var.put("order","1");
            inputList.add(var);
          
            
        return inputList;
    }
    static List<Map<String, String>> init_Nodes(){
        List<Map<String, String>> inputList= new ArrayList();
            HashMap<String, String> var = new HashMap<String, String>();
            var.put("id_var_root","11");
            var.put("node","12");
            inputList.add(var);

            var = new HashMap<String, String>();
            var.put("id_var_root","11");
            var.put("node","13");
            inputList.add(var);
        return inputList;
    }
    public static void main( String args[] ) {      // метод класса


    	
        MGraph<String> mgraph = new MGraph<String>(init1(), "id_var_parrent", "id_var_child", "order");
        
        MGraph gg = mgraph.getSubMgraph("4");
        
        
        Iterator it = mgraph.adjacentEdgesIt("1");
        while(it.hasNext()) {
        	Object o = it.next();
        }
        
        
        it = mgraph.edges();
        while(it.hasNext()) {
        	Object o = it.next();
        }
        
//        List<Object[]> vars = mgraph.getVarNodes();        
        
        
        // List list =  mgraph.getVarNodes();
        //mgraph.getSubMgraph(new Long(11),init_Nodes());
//        MGraph<String> my = mgraph.getSubMgraph("11");


//        Iterator<Long> it = mgraph.adjacentEdges(new Long(11));
//        Long l;
//        while(it.hasNext()){
//            l = it.next();
//        }
//       // mgraph.
         // System.out.println("!!!");


//        Iterator<Long> it1 = sgraph.vertices();
//        while (it1.hasNext()){
//            Long uzel = it1.next();
//            Iterator<Long> it2 = sgraph.ollEdges(uzel);
//            while(it2.hasNext()){
//                System.out.println(uzel +" -> "+it2.next());
//            }
//
//        }

    }
}
