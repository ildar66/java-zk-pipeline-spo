/*
 * Created on 22.09.2008
 * 
 */
package org.uit.director.db.dbobjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.uit.director.db.dbobjects.graph.MGraph;

public class AttributeStruct extends BasicAttribute{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	HashMap<Long, Attribute> attributes;

	MGraph<Long> structure;

	Long idRoot;

	
	/**
	 * 
	 */
	public AttributeStruct() {
		super();
		structure = new MGraph<Long>();
		attributes = new HashMap<Long, Attribute>();
	}

	

	/**
	 * @param attributes
	 * @param structure
	 * @param idRoot
	 */
	public AttributeStruct(HashMap<Long, Attribute> attributes, MGraph<Long> structure, Long idRoot) {
		super();
		this.attributes = attributes;
		this.structure = structure;
		this.idRoot = idRoot;
	}



	public void initStructure(List<Map<String, Long>> inputList, String keyPar,
			String keyChild, String keyOrder) {		
		structure = new MGraph<Long>(inputList, keyPar, keyChild, keyOrder);
	}
	
	
	public void initAttributes(List<Attribute> attrs) {
		
		for (Attribute a: attrs) {
			attributes.put(a.getId(), a);
		}
	}
	
	public Attribute getAttribute() {
		return attributes.get(idRoot);
	}
	
	public List<AttributeStruct> getChildsAttributesStruct() {
		
		List<AttributeStruct> res = new ArrayList<AttributeStruct>();		
		Iterator<Long> it = structure.adjacentEdgesIt(idRoot);
		
		while(it.hasNext()) {
			Long ch = it.next();
			MGraph<Long> subStruct = structure.getSubMgraph(ch);
			AttributeStruct chStruct = new AttributeStruct(attributes, subStruct, ch);
			res.add(chStruct);
		}		
		
		return res;
	}
	
	public boolean hasChilds() {
		if (structure.adjacentEdgesIt(idRoot).hasNext()) {
			return true;
		}
		return false;
	}
	
	public int numChilds() {		
		return structure.adjacentEdgesColl(idRoot).size();
	}
	

}
