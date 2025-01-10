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

public class RunData {

	private String fileName = null;
	private String dirName = null;
	private String ncboOntologyId = null;
	private String rootNodeName = null;
	private String prefix = null;
	private String symbolFormat = null;
	private boolean allowSummaryBasecodes = false;
	


	private char delimiters;  
	private Boolean header = false;

	private List<ExpandedConceptType> rootNodes = new ArrayList<ExpandedConceptType>();
//	private List<ExpandedConceptType> childNodes = new ArrayList<ExpandedConceptType>();
	
	private static RunData thisInstance;
	static {
		thisInstance = new RunData();
	}
		
	public static RunData getInstance() {
		return thisInstance;
	}


	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getNcboOntologyId() {
		return ncboOntologyId;
	}


	public void setNcboOntologyId(String ncboOntologyId) {
		this.ncboOntologyId = ncboOntologyId;
	}

	public String getRootNodeName() {
		return rootNodeName;
	}


	public void setRootNodeName(String name) {
		this.rootNodeName = name;
	}
	
	public String getPrefix() {
		return prefix;
	}


	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public String getSymbolFormat() {
		return symbolFormat;
	}


	public void setSymbolFormat(String symbolFormat) {
		this.symbolFormat = symbolFormat;
	}
	
	
	public String getDirName() {
		return dirName;
	}

	public void setDirName(String dirName) {
		this.dirName = dirName;
	}

	public char getDelimiters() {
		return delimiters;
	}

	public void setDelimiters(char delimiters) {
		this.delimiters = delimiters;
	}


	public Boolean getHeader() {
		return header;
	}

	public void setHeader(Boolean header) {
		this.header = header;
	}
	
	public void setRootNodes(ExpandedConceptType node) {
		this.rootNodes.add(node);
	}
	public boolean isAllowSummaryBasecodes() {
		return allowSummaryBasecodes;
	}

	public void setAllowSummaryBasecodes(boolean allowSummaryBasecodes) {
		this.allowSummaryBasecodes = allowSummaryBasecodes;
	}
	
}
