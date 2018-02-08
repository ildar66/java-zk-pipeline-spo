package org.uit.director.db.dbobjects.graph;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;


/**
 * Created by IntelliJ IDEA.
 * User: pd199968
 * Date: 09.09.2008
 * Time: 17:22:00
 * To change this template use File | Settings | File Templates.
 */
public class SGraph<T>  {
    Map<T, Set<T>> graph;


    public SGraph() {
        graph = new TreeMap<T, Set<T>>();
    }

    public long numVertices() {
        return graph.size();
    }// возращает итератор yзлов

    public Iterator<T> vertices() {
        return graph.keySet().iterator();
    }

    public Iterator edges() {
        return graph.values().iterator();

    }

    // Возращает список смежных вершин
    public Iterator adjacentEdges(T uzel) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean insertVertex(T obj) {
        // добавление  нового УЗЛА!
        if (!graph.containsKey(obj)) {
            graph.put(obj, new TreeSet<T>());
            return true;
        } else {
			return false;
		}
    }

    public boolean insertDirectedEdge(T ishodn_uzel, T vhod_uzel) {
        if (graph.containsKey(ishodn_uzel)) {
            graph.get(ishodn_uzel).add(vhod_uzel);
            return true;
        } else {
			return false;
		}
    }
    
    // проверка если вершина в с списке достижимости
    public boolean has(T ishodn_uzel, T uzel){
     return graph.get(ishodn_uzel).contains(uzel);
    }
    public boolean deletetDirectedEdge(T ishodn_uzel, T vhod_uzel) {
            return graph.get(ishodn_uzel).remove(vhod_uzel);
    }

    // Возращает список достижимы вершин
    public Iterator<T> allEdges(T uzel) {
       return graph.get(uzel).iterator();
    }
}
