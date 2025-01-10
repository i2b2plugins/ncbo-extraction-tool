/*
 * Copyright (c) 2006-2025 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		David Wang
 */

package edu.harvard.i2b2.ncbo.extraction;

public abstract class AbstractNCBOOntologyTool 
{
	
	protected static final String EXTRACT_TOOL_NAME = "NCBO Extraction tool"; // name for ExtratAll
	protected static final String PROCESS_TOOL_NAME = "NCBO Processing tool"; // name for processAll
	protected static final String VERSION_STAMP 	= "Version 2.1 Dec 2024"; // Version Control

	protected String toolName;
	
	public AbstractNCBOOntologyTool( String name )
	{
		this.toolName = name;
	}
	
	public String getToolName()
	{ return this.toolName; }
	
	public String getToolNameAndVersionStamp()
	{ return this.toolName + " " + VERSION_STAMP; }
}
