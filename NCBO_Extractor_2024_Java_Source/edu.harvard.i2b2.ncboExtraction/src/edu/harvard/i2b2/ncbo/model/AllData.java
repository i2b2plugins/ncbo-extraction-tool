/*
 * Copyright (c) 2006-2025 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */

package edu.harvard.i2b2.ncbo.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.harvard.i2b2.common.exception.I2B2Exception;
import edu.harvard.i2b2.ncboclient.datavo.ncbo.all.AllTermsType;
import edu.harvard.i2b2.ncboclient.datavo.ncbo.all.ChildrenType;
import edu.harvard.i2b2.ncboclient.datavo.ncbo.all.ClassType;

public class AllData {
	
//	private static final String serverUrl ="http://rest.bioontology.org/bioportal/";

	
	private static AllData thisInstance;
	static {
		thisInstance = new AllData();
	}
		
	public static AllData getInstance() {
		return thisInstance;
	}
	
	// key is id + label..
	
	private List<ExpandedConceptType> allNodes = new ArrayList<ExpandedConceptType>();

	public List<ExpandedConceptType> getAllNodes(){
		return allNodes;
	}
	
	

	
	public void load(AllTermsType allTerms) throws Exception{
		List<ClassType> classes = allTerms.getCollection().getClazz();

		Iterator<ClassType> it = classes.iterator();
		while(it.hasNext()){
			ClassType entry = (ClassType) it.next();
			if(entry == null){
				//	log.error("Service returned null event");
				I2B2Exception e = new I2B2Exception("An error has been returned from the server");
				throw e;
			}
			else{
				
				try {
					allNodes.add(createNode(entry));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}	
	}

	private ExpandedConceptType createNode(ClassType term){
		ExpandedConceptType node = new ExpandedConceptType();

		String basecode = term.getProperties().getNotationCollection().getNotation().get(0);
		if(basecode != null){
			
			if(basecode.contains("#")){
				String[] basecodes = basecode.split("#"); 
				basecode = basecodes[1];
			}
		}
		node.setBasecode(basecode);

		node.setFullId(term.getId());
		//  Some NCBO ontologies have CAPITALIZED names
		//   
		String name = term.getProperties().getPrefLabelCollection().getPrefLabel().get(0);

		node.setName(name);
		
		node.setSymbol("(" + basecode+ ") " + name);
		if (node.getSymbol().length() > 500){	
			node.setSymbol(node.getSymbol().substring(0,498) + "~");
		}
		
		
		if(term.getProperties().getCuiCollection()  != null){
			List<String> cuis =	term.getProperties().getCuiCollection().getCui();
			if(cuis != null && cuis.size() > 0){
				node.setCui(cuis.get(0));
			}
		}
		if(term.getProperties().getTuiCollection()  != null){
			List<String> tuis =	term.getProperties().getTuiCollection().getTui();
			if(tuis != null && tuis.size() > 0){
				node.setTui(tuis.get(0));
			}
		}
		
		
		node.setOperator("LIKE");
		node.setColumndatatype("T");
		node.setColumnname("concept_path");
		node.setFacttablecolumn("concept_cd");
		node.setSynonymCd("N");
		node.setTablename("concept_dimension");
		node.setSourcesystemCd("NCBO_" + RunData.getInstance().getNcboOntologyId());
		
		if(term.getSynonymCollection()  != null){
			List<String> synonyms =	term.getSynonymCollection().getSynonym();
			if(synonyms != null && synonyms.size() > 0){
			Iterator synonymIterator = 	synonyms.iterator();
				while(synonymIterator.hasNext()){
					String synonym = (String) synonymIterator.next();
					synonym = synonym.substring(0,1) + synonym.substring(1).toLowerCase();
					node.setSynonyms(synonym);
				}
			}

		}			
		if(term.getChildrenCollection()  != null){
			List<ChildrenType> children =	term.getChildrenCollection().getChildren();
			if(children != null  && children.size() > 0){
				node.setVisualattributes("FA");
			}
			else{
				node.setVisualattributes("LA");
			}
		}
		if(term.getProperties().getSubClassOfCollection()!= null){
			List<String> subClass =   term.getProperties().getSubClassOfCollection().getSubClassOf();
			if(subClass != null && subClass.size() > 0){
				Iterator parentIterator = term.getProperties().getSubClassOfCollection().getSubClassOf().iterator();
				while(parentIterator.hasNext()){
					String parentFullId = (String) parentIterator.next();
					if (parentFullId.endsWith("THING") || parentFullId.endsWith("owl:Thing")|| parentFullId.endsWith("owl#Thing")){
						node.setParentFullIds(null);
						node.setRoot(true);
					} else{
						node.setParentFullIds(parentFullId);
						node.setRoot(false);
					}
				}
			}
		}

	
	 // this might not be needed..
	    if(node.isRoot() == true){
				node.setLevel(1);
				node.setVisualattributes("CA");
				// this might not be needed..	
			RunData.getInstance().setRootNodes(node);
	    }
		return node;
	}
	
	
	public void clear(){
		allNodes.clear();
	}
}
