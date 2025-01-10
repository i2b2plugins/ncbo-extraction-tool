/*
 * Copyright (c) 2006-2025 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */

package edu.harvard.i2b2.ncbo.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import edu.harvard.i2b2.common.exception.I2B2DAOException;
import edu.harvard.i2b2.common.exception.I2B2Exception;


import edu.harvard.i2b2.ncbo.model.DBInfoType;
import edu.harvard.i2b2.ncbo.model.ExpandedConceptType;
import edu.harvard.i2b2.ncbo.model.RunData;

import edu.harvard.i2b2.ncbo.util.StringUtil;


public class ConceptPersistDao  {

	private static Log log = LogFactory.getLog(ConceptPersistDao.class);

	private SimpleJdbcTemplate jt;
	
	public void setDataSource(DataSource ds){
		this.jt = new SimpleJdbcTemplate(ds);
	}

	public List<ExpandedConceptType> findRootNodes(DBInfoType dbInfo) {
		
		String metadataSchema = dbInfo.getDb_fullSchema();
		String stagingTable = dbInfo.getStagingTable();
	//	setDataSource(dbInfo.getDb_dataSource());

		// Lori's original
		//String rootsSql = "select * from " +  metadataSchema +  stagingTable + " where c_hlevel = 1 and c_synonym_cd = 'N'" + "and c_basecode not like 'MTHU%'";
		
		
		// tdw9: commenting out MTHU% part to deal with OMIM
		String rootsSql = "select * from " +  metadataSchema +  stagingTable + " where c_hlevel = 1 and c_synonym_cd = 'N'"; // + "and c_basecode not like 'MTHU%'";
		
	//	System.out.println(rootsSql);
		
		List<ExpandedConceptType> queryResult = null;
		try {
			queryResult = jt.query(rootsSql, getMapper());
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			throw e;
		}
		log.debug("Root Nodes Result Size = " + queryResult.size());

		return queryResult;
	}
	
	public List<ExpandedConceptType> findUnreachableNodes(DBInfoType dbInfo) 
	{
		String metadataSchema = dbInfo.getDb_fullSchema();
		String stagingTable = dbInfo.getStagingTable();
		String sql = "select * from " +  metadataSchema +  stagingTable + " where parent_fullId = '(null)'"; // looking for nodes who do not have parents;
		List<ExpandedConceptType> queryResult = null;
		try {
			queryResult = jt.query(sql, getMapper());
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			throw e;
		}
		log.debug("Unreachable Nodes Result Size = " + queryResult.size());
		return queryResult;
	}

	public List<ExpandedConceptType> findChildNodes(String fullId,  DBInfoType dbInfo) {

		String metadataSchema = dbInfo.getDb_fullSchema();
		String table = dbInfo.getStagingTable();


		String childrenSql = "select  * from " +  metadataSchema +  table + " where parent_FullId = ? " ;

		List<ExpandedConceptType> queryResult = null;
		try {
			queryResult = jt.query(childrenSql, getMapper(), fullId);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			throw e;
		}
		log.debug("result size = " + queryResult.size());

		return queryResult;
	}

	public int addNode(final ExpandedConceptType addChildType, DBInfoType dbInfo) throws I2B2DAOException, I2B2Exception{

		String metadataSchema = dbInfo.getDb_fullSchema();
		
		String tableName = dbInfo.getTargetTable();

		int numRowsAdded = -1;
		try {
			Date today = Calendar.getInstance().getTime();
			String xml = null;

				String addSql = "insert into " + metadataSchema+tableName  + 
				"(c_hlevel, c_fullname, c_name, c_synonym_cd, c_visualattributes, c_basecode, c_facttablecolumn, c_tablename, c_columnname, c_columndatatype, c_operator, c_dimcode, c_tooltip, update_date, sourcesystem_cd, c_symbol, c_path, i_full_id) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				log.info(addSql);
				numRowsAdded = jt.update(addSql, 
						addChildType.getLevel(), StringUtil.getPath(addChildType.getKey()),addChildType.getName(), addChildType.getSynonymCd(), 
						addChildType.getVisualattributes(), addChildType.getBasecode(), addChildType.getFacttablecolumn() ,addChildType.getTablename() ,
						addChildType.getColumnname() , addChildType.getColumndatatype() ,addChildType.getOperator() ,addChildType.getDimcode() ,
						addChildType.getTooltip(), today, addChildType.getSourcesystemCd(), addChildType.getSymbol(), addChildType.getPath(), addChildType.getFullId());
			
		} catch (DataAccessException e) {
			log.error("Dao addChild failed");
			log.error(e.getMessage());
			throw new I2B2DAOException("Data access error " , e);
		}

		log.debug("Number of rows added: " + numRowsAdded);

		return numRowsAdded;

	}

	
	private ParameterizedRowMapper<ExpandedConceptType> getMapper( ){

		ParameterizedRowMapper<ExpandedConceptType> mapper = new ParameterizedRowMapper<ExpandedConceptType>() {
			public ExpandedConceptType mapRow(ResultSet rs, int rowNum) throws SQLException {
				ExpandedConceptType child = new ExpandedConceptType();	          
				child.setName(rs.getString("c_name"));
				child.setBasecode(rs.getString("c_basecode"));
				child.setLevel(rs.getInt("c_hlevel"));
				child.setKey(rs.getString("c_fullname"));  
				child.setSynonymCd(rs.getString("c_synonym_cd"));
				child.setVisualattributes(rs.getString("c_visualattributes"));
				child.setTooltip(rs.getString("c_tooltip"));
				child.setFacttablecolumn(rs.getString("c_facttablecolumn" ));
				child.setTablename(rs.getString("c_tablename")); 
				child.setColumnname(rs.getString("c_columnname")); 
				child.setColumndatatype(rs.getString("c_columndatatype")); 
				child.setOperator(rs.getString("c_operator")); 
				child.setDimcode(rs.getString("c_dimcode")); 
				child.setSourcesystemCd(rs.getString("sourcesystem_cd"));
				child.setSymbol(rs.getString("c_symbol"));
				child.setPath(rs.getString("c_path"));
				child.setFullId(rs.getString("i_full_id"));
				return child;
			}
		};
		return mapper;
	}
}
