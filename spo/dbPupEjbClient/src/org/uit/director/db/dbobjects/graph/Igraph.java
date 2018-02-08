package org.uit.director.db.dbobjects.graph;

import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: pd199968
 * Date: 10.09.2008
 * Time: 10:39:36
 * To change this template use File | Settings | File Templates.
 */
public interface Igraph<T> {
    // Общие методы

    // Количество узлов
    long numVertices();
    // возращает итератор yзлов
    Iterator vertices();
    Iterator edges();
    // Возращает список смежных вершин
    Iterator adjacentEdgesIt(T uzel);
    //  Обновление графа

    // вводит новый узел!
    boolean insertVertex(T uzel);

    boolean hasVertex(T vershina);

    boolean insertDirectedEdge(T ishodn_uzel,T vhod_uzel);

    // boolean add(long )
}
