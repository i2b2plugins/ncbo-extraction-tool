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

import edu.harvard.i2b2.ontclient.datavo.vdo.ConceptType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ExpandedConceptType extends ConceptType{

	private String symbol;
	private String path;
	private String snomed_ct;
	private String snomed_rt;
	private	String cui;
	private String tui;
	private String ctv3;
	private String full_id;

	private List<String> parentFullIds = new ArrayList<String>();
	private List<String> synonyms = new ArrayList<String>();
	private List<String> rootPaths = new ArrayList<String>();
	private String updateDate;
	private boolean root;

	
	public ExpandedConceptType() {
		initializeStrings();	
	}
	
	public String getFullId() {
		return full_id;
	}
	public void setFullId(String fullId) {
		this.full_id = fullId;
	}

	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getSnomed_ct() {
		return snomed_ct;
	}
	public void setSnomed_ct(String snomedCt) {
		snomed_ct = snomedCt;
	}
	public String getSnomed_rt() {
		return snomed_rt;
	}
	public void setSnomed_rt(String snomedRt) {
		snomed_rt = snomedRt;
	}
	public String getCui() {
		return cui;
	}
	public void setCui(String cui) {
		this.cui = cui;
	}
	public String getTui() {
		return tui;
	}
	public void setTui(String tui) {
		this.tui = tui;
	}
	public String getCtv3() {
		return ctv3;
	}
	public void setCtv3(String ctv3) {
		this.ctv3 = ctv3;
	}

	public List<String> getSynonyms() {
		return synonyms;
	}
	public void setSynonyms(String syn) {
		this.synonyms.add(syn);
	}
	
	public List<String> getParentFullIds() {
		return parentFullIds;
	}
	public void setParentFullIds(String parent) {
		this.parentFullIds.add(parent);
	}

	
	public List<String> getRootPaths() {
		return rootPaths;
	}
	public void setRootPaths(String path) {
		this.rootPaths.add(path);
	}

	
    public String getDate(){  
    	Date date = new Date();
    
      	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd:HH:mm:ss");     
    	  return sdf.format(date);	
    }
    
    public boolean isRoot(){
    	return root;
    }
    
    public void setRoot(boolean noParentFound){
    	root = noParentFound;
    }
    
    public void initializeStrings(){
    	this.setKey("(null)");
    	this.setBasecode("(null)");
    	this.setCtv3("(null)");
    	this.setCui("(null)");
    	this.setDimcode("(null)");
    	this.setName("(null)");
    	this.setPath("(null)");
    	this.setSnomed_ct("(null)");
    	this.setSnomed_rt("(null)");
    	this.setSymbol("(null)");
    	this.setTooltip("(null)");
    	this.setTui("(null)");
    	this.setFullId("(null)");
    	this.setComment("(null)");
    	this.setSourcesystemCd("(null)");
    	this.setValuetypeCd("(null)");
    }
}


	
	
	
	

