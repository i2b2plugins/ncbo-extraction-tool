/*
    Copyright (c) 2006-2025 Massachusetts General Hospital 
    All rights reserved. This program and the accompanying materials 
    are made available under the terms of the i2b2 Software License v2.1 
    which accompanies this distribution. 
    
    Contributors: David Wang

	SQL commands used to create the staging and metadata table.
	
	A staging table is where the result of NCBOOntologyExtractAll is to be imported to.
	A metadata table is where the result of NCBOOntologyProcessll is to be imported to.
	
	Notes:
	 1. The actual i2b2 metadata table does not require the following columns: i_snomed_ct, i_snomed_rt, i_cuit, c_tui_ i_ctv3, i_full_id, or parent_fullId
	 2. In an i2b2 metadata table, the update_date column should be of type datetime . Here we use varchar(50) to simplify the import process.
	 3. Column datatypes between staging and metadata tables are slightly different.
*/

/* Create a staging table to hold result of running NCBOOntologyExtractAll */
CREATE TABLE [NCBO_DB].[DBO].[STAGING_TEMPLATE] 
   ("c_hlevel"			INT				NULL, 
	"c_fullname"		VARCHAR(700)	NULL,
	"c_name"			VARCHAR(2000)	NOT NULL, 
	"c_synonym_cd"		CHAR(1)			NOT NULL, 
	"c_visualattributes"CHAR(3)			NOT NULL, 
	"c_basecode"		VARCHAR(50)		NULL, 
	"c_facttablecolumn"	VARCHAR(50)		NOT NULL, 
	"c_tablename"		VARCHAR(50)		NOT NULL, 
	"c_columnname"		VARCHAR(50)		NOT NULL, 
	"c_columndatatype"	VARCHAR(50)		NOT	NULL, 
	"c_operator"		VARCHAR(10)		NOT NULL, 
	"c_dimcode"			VARCHAR(700)	NULL, 
	"c_tooltip"			VARCHAR(700)	NULL,
	"sourcesystem_cd"	VARCHAR(50)		NULL,
	"c_symbol"			VARCHAR(2000)	NOT NULL,
	"c_path"			VARCHAR(700)	NULL,
	"i_snomed_ct"		VARCHAR(50)		NULL,
	"i_snomed_rt"		VARCHAR(50)		NULL,
  	"i_cui"				VARCHAR(50)		NULL,
	"i_tui"				VARCHAR(50)		NULL,
	"i_ctv3"			VARCHAR(50)		NULL,
	"i_full_id"			VARCHAR(100)	NULL,
	"update_date"		VARCHAR(50)		NOT NULL,
	"parent_fullId"		VARCHAR(50) 	NULL,
	"m_applied_path"	VARCHAR(900)	NOT NULL
   );

/* Create a metadata table to hold result of running NCBOOntologyProcessAll */
CREATE TABLE [NCBO_DB].[DBO].[MED_TEMPLATE]
   ("c_hlevel"			INT				NOT NULL, 
    "c_fullname"		VARCHAR(700)    NOT NULL, 
    "c_name"			VARCHAR(2000)   NOT NULL, 
    "c_synonym_cd"		CHAR(1)			NOT NULL, 
    "c_visualattributes"CHAR(3)			NOT NULL, 
    "c_basecode"		VARCHAR(50)		NULL, 
    "c_facttablecolumn" VARCHAR(50)		NOT NULL, 
    "c_tablename"		VARCHAR(50)		NOT NULL, 
    "c_columnname"		VARCHAR(50)		NOT NULL, 
    "c_columndatatype"	VARCHAR(50)		NOT NULL, 
    "c_operator"		VARCHAR(10)		NOT NULL, 
    "c_dimcode"			VARCHAR(700)    NOT NULL, 
    "c_tooltip"			VARCHAR(900)    NULL,
    "sourcesystem_cd"	VARCHAR(50)		NULL,
    "c_symbol"			VARCHAR(50)		NOT NULL,
    "c_path"			VARCHAR(700)    NULL,
    "i_snomed_ct"		VARCHAR(50)     NOT NUll,
    "i_snomed_rt"		VARCHAR(50)     NOT NUll,
    "i_cui"				VARCHAR(50)     NOT NUll,
    "i_tui"				VARCHAR(50)     NOT NUll,
    "i_ctv3"			VARCHAR(50)     NOT NUll,
    "i_full_id"			VARCHAR(100)    NOT NUll,
	"update_date"		VARCHAR(50)		NOT NULL,
    "parent_fullId"		VARCHAR(50)		NOT NUll, 
    "m_applied_path"	VARCHAR(900)	NOT NULL 
   );