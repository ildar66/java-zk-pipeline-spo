package org.uit.director.db.dbobjects.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: pd199968
 * Date: 09.09.2008
 * Time: 10:49:30
 * To change this template use File | Settings | File Templates.
 */
//import java.util.;
public class LGraph {
    static class Arc{
        int end;
        public Arc(int e){end = e;}
        public boolean equals(Arc arc){
            return arc.end == end;
        }


    }
    //Object
    //Object
    List<Integer>[] graph;
    //List my = new ArrayList();

    public LGraph(int n){
        graph =  new List[n];
        for(int i = 0;i < graph.length;i++){
            graph[i] = new ArrayList();
        }
    }
   // protected InspectablegRAPH
   public void add(int u,int v){
        graph[u].add(v);
    }

}

