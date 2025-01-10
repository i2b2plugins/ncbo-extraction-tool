/*
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

public class DBInfoType{
    private String hive;
    private String projectId;
    private String ownerId;
	private String db_fullSchema;
	private String db_dataSource;
	private String db_serverType;
	private String stagingTable;
	private String targetTable;
	
	public DBInfoType() {
	}

	public String getDb_dataSource() {
		return db_dataSource;
	}

	public void setDb_dataSource(String db_dataSource) {
		this.db_dataSource = db_dataSource;
	}

	public String getDb_fullSchema() {
		return db_fullSchema;
	}

	public void setDb_fullSchema(String db_fullSchema) {
		if(db_fullSchema.endsWith("."))
			this.db_fullSchema = db_fullSchema;
		else
			this.db_fullSchema = db_fullSchema+".";
	}

	public String getDb_serverType() {
		return db_serverType;
	}

	public void setDb_serverType(String db_serverType) {
		this.db_serverType = db_serverType;
	}

	public String getHive() {
		return hive;
	}

	public void setHive(String hive) {
		this.hive = hive;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getStagingTable() {
		return stagingTable;
	}

	public void setStagingTable(String tableName) {
		this.stagingTable = tableName;
	}

	public String getTargetTable() {
		return targetTable;
	}

	public void setTargetTable(String tableName) {
		this.targetTable = tableName;
	}
}
