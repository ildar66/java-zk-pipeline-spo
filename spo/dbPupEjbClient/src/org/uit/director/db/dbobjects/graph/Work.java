package org.uit.director.db.dbobjects.graph;

import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: pd199968
 * Date: 10.09.2008
 * Time: 8:33:00
 * To change this template use File | Settings | File Templates.
 */
public class Work {
    SGraph sgraph;
    MGraph mgraph;

    public Work(SGraph sgraph, MGraph mgraph) {
        this.sgraph = sgraph;
        this.mgraph = mgraph;
        copyVertices();
    }


    public SGraph to_SGraph_from_LGraph() {
        Iterator<Long> iterator = mgraph.vertices();
        while (iterator.hasNext()) {
            Long value = iterator.next();
            recursive(value,value,sgraph);
        }
        return sgraph;
    }

    // Скопировать вершины
    public void copyVertices() {
        Iterator<Long> iterator = mgraph.vertices();  
        while (iterator.hasNext()) {
            sgraph.insertVertex(iterator.next());
        }
    }

    public void recursive(Long versina, Long ishodn_vershina,SGraph sGraph) {
       if( sgraph.has(new Long(ishodn_vershina),new Long(ishodn_vershina))){
            sgraph.deletetDirectedEdge(new Long(ishodn_vershina),new Long(ishodn_vershina));
             return;
       }
        Iterator<Long> iterator = mgraph.adjacentEdgesIt(versina);
        while (iterator.hasNext()) {
            Long value = iterator.next();
            sgraph.insertDirectedEdge(ishodn_vershina, value);
            recursive(value, ishodn_vershina,sgraph);
        }
    }


}
