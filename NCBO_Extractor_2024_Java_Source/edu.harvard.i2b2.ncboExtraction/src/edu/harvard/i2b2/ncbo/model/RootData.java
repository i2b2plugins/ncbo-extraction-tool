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
import edu.harvard.i2b2.ncboclient.datavo.ncbo.all.ClassType;


public class RootData {
	
//	private static final String serverUrl ="http://rest.bioontology.org/bioportal/";

	
	private static RootData thisInstance;
	static {
		thisInstance = new RootData();
	}
		
	public static RootData getInstance() {
		return thisInstance;
	}
		
	public void loadRootNodes(AllTermsType roots) throws Exception{
		List<ClassType> classes =roots.getCollection().getClazz();

		Iterator<ClassType> it = classes.iterator();
		while(it.hasNext()){
			ClassType entry = (ClassType) it.next();
			if(entry == null){
				//	log.error("Service returned null event");
				I2B2Exception e = new I2B2Exception("An error has been returned from the server");
				throw e;
			}
			else{
				RunData.getInstance().setRootNodes(createNode(entry));
//				AllData.getInstance().getAllNodes().add(createNode(entry));
			}
			
		}		
			
		/*	version 1 code
			
		List<EntryType> entries = roots.getData().getClassBean().getRelations().getEntry();
		Iterator<EntryType> it = entries.iterator();
		while(it.hasNext()){
			EntryType entry = (EntryType) it.next();
			if(entry == null){
				//	log.error("Service returned null event");
				I2B2Exception e = new I2B2Exception("An error has been returned from the server");
				throw e;
			}
			else if(entry.getString().equals("SubClass")){
				List<ClassBeanType> rootList = entry.getList().getClassBean();
				Iterator<ClassBeanType> itRoot = rootList.iterator();
				while (itRoot.hasNext()){
					ClassBeanType root = (ClassBeanType)itRoot.next();
					RunData.getInstance().setRootNodes(createNode(root));
					AllData.getInstance().getAllNodes().add(createNode(root));
				}
			}
		}	
		*/
	}

	public ExpandedConceptType createNode(ClassType term) throws Exception{
		ExpandedConceptType node = new ExpandedConceptType();
		
		String basecode = null;
		if(term.getProperties().getNotationCollection() != null){
			basecode = term.getProperties().getNotationCollection().getNotation().get(0);
			if(basecode != null){
				if(basecode.contains("#")){
					String[] basecodes = basecode.split("#"); 
					basecode = basecodes[1];
				}
			}
		}
		//First send name for the case where
			// USER specifies a single root node in the option paramater.
		String name = term.getProperties().getPrefLabelCollection().getPrefLabel().get(0);
		//
		//  Some NCBO ontologies have CAPITALIZED names
		//      that we dont want   
		name = name.substring(0,1) +
				name.substring(1).toLowerCase();
		node.setName(name);

		node.setBasecode(basecode);
		node.setFullId(term.getId());
	
		if(basecode == null)
				node.setSymbol(name);
		else
			node.setSymbol("(" + basecode + ") " + name);
		node.setOperator("LIKE");
		node.setColumndatatype("T");
		node.setColumnname("concept_path");
		node.setFacttablecolumn("concept_cd");
		node.setSynonymCd("N");
		node.setTablename("concept_dimension");
		// This is the NCBO extraction path where we set root nodes
		if(RunData.getInstance().getNcboOntologyId() != null){
	//		node.setName(term.getLabel());
			node.setSourcesystemCd("NCBO_" + RunData.getInstance().getNcboOntologyId());
			node.setLevel(1);
			node.setVisualattributes("FA");
		}
		// This is the user specified single root node from Process path
		else{
			node.setSourcesystemCd("USER");
			node.setLevel(0);
			node.setVisualattributes("CA");
			try {
				node.setSymbol(FormatData.getInstance().createSymbol(node.getName()));
			} catch (Exception e) {
				System.out.println("Problem with hash function to create symbol for label " + node.getName());
				throw e;
			}
			node.setKey("\\"+node.getSymbol()+ "\\");
			node.setDimcode("\\"+node.getSymbol()+ "\\");
		}
		node.setTooltip( node.getName() );

		return node;
	}
		
}
