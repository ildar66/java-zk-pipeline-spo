package org.uit.director.db.dbobjects.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Created by IntelliJ IDEA. User: pd199968 Date: 09.09.2008 Time: 10:33:25 To
 * change this template use File | Settings | File Templates.
 */
public class MGraph<T> implements Igraph<T>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Map<T, TreeMap<T, T>> graph;

	T root;

	public MGraph() {
		graph = new TreeMap<T, TreeMap<T, T>>();

	}

	public MGraph(List<Map<String, T>> inputList, String keyPar,
			String keyChild, String keyOrder) {
		graph = new TreeMap<T, TreeMap<T, T>>();
		insertFromList(inputList, keyPar, keyChild, keyOrder);
	}

	public boolean insertFromList(List<Map<String, T>> inputList,
			String keyPar, String keyChild, String keyOrder) {
		Iterator<Map<String, T>> itList = inputList.iterator();
		Map<String, T> map;
		while (itList.hasNext()) {
			map = itList.next();
			// String keyPar = "id_var_parrent";
			T parrent = map.get(keyPar);
			// String keyChild = "id_var_child";
			T child = map.get(keyChild);
			// String keyOrder = "order";
			T order = map.get(keyOrder);
			insertVertex(parrent);
			insertVertex(child);
			graph.get(parrent).put(order, child);

		}
		return true;
	}

	public long numVertices() {
		return graph.size();
	}

	// Возращает существует ли вершина
	public boolean hasVertex(T vershina) {
		return graph.containsKey(vershina);
	}

	// возращает итератор yзлов
	public Iterator<T> vertices() {
		return graph.keySet().iterator();
	}

	public Iterator edges() {
		return graph.values().iterator();

	}

	// Возращает список смежных вершин
	public Iterator<T> adjacentEdgesIt(T uzel) {
		return graph.get(uzel).values().iterator();
	}

	public Collection<T> adjacentEdgesColl(T uzel) {
		return graph.get(uzel).values();
	}

	// добавление нового УЗЛА!
	public boolean insertVertex(T obj) {

		if (!graph.containsKey(obj)) {
			graph.put(obj, new TreeMap<T, T>());
			return true;
		} else {
			return false;
		}
	}

	public boolean insertDirectedEdge(T ishodn_uzel, T vhod_uzel) {
		if (graph.containsKey(ishodn_uzel)) {
			// graph.get(ishodn_uzel).put(graph.get(ishodn_uzel).firstKey(),vhod_uzel);
			return true;
		} else {
			return false;
		}
	}

	// / Возратить список Достижимости
	public List<Object[]> getVarNodes() {
		List<Object[]> list = new ArrayList<Object[]>();
		SGraph<T> sGraph = new SGraph<T>();

		// Скопировать все вершины!!
		Iterator<T> iterator = this.vertices();
		while (iterator.hasNext()) {
			sGraph.insertVertex(iterator.next());
		}
		// / Сам цикл работы!!!
		Iterator<T> it = vertices();
		while (it.hasNext()) {
			T value = it.next();
			recursive(value, value, sGraph);
		}

		// Доставание всех объектовж
		iterator = sGraph.vertices();
		Object[] o;
		while (iterator.hasNext()) {
			T uzel = iterator.next();
			Iterator<T> it2 = sGraph.allEdges(uzel);
			while (it2.hasNext()) {
				o = new Object[3];
				o[0] = uzel;
				o[1] = it2.next();
				o[2] = "ID_TRANSACTION";
				list.add(o);
			}
		}

		return list;
	}

	// Возратить подграффф!
	public MGraph getSubMgraph(T id, List<Map<String, T>> inputList) {
		MGraph<T> mgraph = new MGraph<T>();
		mgraph.root = id;
		Iterator<Map<String, T>> itList = inputList.iterator();
		Map<String, T> map;
		while (itList.hasNext()) {
			map = itList.next();
			T parrent = map.get("id_var_root");
			T child = map.get("node");
			mgraph.graph.put(parrent, graph.get(parrent));
			mgraph.graph.put(child, graph.get(child));
		}
		return mgraph;
	}

	public MGraph<T> getSubMgraph(T id) {
		MGraph<T> mgraph = new MGraph<T>();
		mgraph.root = id;
		Set<T> set = new TreeSet<T>();
		recursiveMgraph(id, set);
		Iterator<T> it = set.iterator();
		while (it.hasNext()) {
			T l = it.next();
			mgraph.graph.put(l, this.graph.get(l));
		}
		return mgraph;
	}

	public void recursive(T versina, T ishodn_vershina, SGraph<T> sGraph) {

		if (sGraph.has(ishodn_vershina, ishodn_vershina)) {
			sGraph.deletetDirectedEdge(ishodn_vershina, ishodn_vershina);
			return;
		}
		Iterator<T> iterator = adjacentEdgesIt(versina);
		while (iterator.hasNext()) {
			T value = iterator.next();
			if (!sGraph.has(ishodn_vershina, value)) {
				sGraph.insertDirectedEdge(ishodn_vershina, value);
				recursive(value, ishodn_vershina, sGraph);
			}
		}
	}

	public boolean recursiveMgraph(T versina, Set<T> setVertex) {

		if (setVertex.contains(versina)) {
			return false;
		}
		setVertex.add(versina);

		if (hasVertex(versina)) {
			Iterator<T> iterator = adjacentEdgesIt(versina);
			while (iterator.hasNext()) {
				T value = iterator.next();
				boolean has = recursiveMgraph(value, setVertex);
				/*
				 * if (!has) { setVertex.add(value); }
				 */
			}
		} else {
			return false;
		}

		return true;

	}

}
