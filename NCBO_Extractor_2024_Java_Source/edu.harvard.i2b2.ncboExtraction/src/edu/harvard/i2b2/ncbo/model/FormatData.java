/*
 * Copyright (c) 2006-2025 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips, David Wang
 */

package edu.harvard.i2b2.ncbo.model;

import java.util.ArrayList;

import edu.harvard.i2b2.ncbo.util.SymbolHash;


public class FormatData {

//	private Boolean header = false;
	private static FormatData thisInstance;
	static {
		thisInstance = new FormatData();
	}
		
	public static FormatData getInstance() {
		return thisInstance;
	}
	
	public ArrayList<String> getColumns() {
	
		ArrayList<String> columns = new ArrayList();
		columns.add("c_hlevel");
		columns.add("c_fullname");
		columns.add("c_name");
		columns.add("c_synonym_cd");
		columns.add("c_visualattributes");
		columns.add("c_basecode");
		columns.add("c_facttablecolumn");
		columns.add("c_tablename");
		columns.add("c_columnname");
		columns.add("c_columndatatype");
		columns.add("c_operator");
		columns.add("c_dimcode");
		columns.add("c_tooltip");
		columns.add("sourcesystem_cd");
		columns.add("c_symbol");
		columns.add("c_path");
		columns.add("i_snomed_ct");
		columns.add("i_snomed_rt");
		columns.add("i_cui");
		columns.add("i_tui");
		columns.add("i_ctv3");
		columns.add("i_full_id");
		columns.add("update_date");
		columns.add("parent_fullId");
		columns.add("m_applied_path");
		
		return columns;
		
	}
	
	public String createSymbol(String symbol) throws Exception{

		String hash = SymbolHash.getInstance().createFourCharTerm(symbol);
		String truncSymbol = hash;

		if(RunData.getInstance().getSymbolFormat().equals("S")){
			return truncSymbol;
		}
		
		else if (RunData.getInstance().getSymbolFormat().equals("L")){

			if(symbol.length() > 33) {
				truncSymbol = symbol.substring(0,27).trim() + "~" + hash;
				return truncSymbol;
			}

		}
		else{
			if(symbol.length() > 20) {
				truncSymbol = symbol.substring(0,14).trim() + "~" + hash;
				return truncSymbol;
			}
		}
		return symbol;
	}
	
	
	
	
//	public Boolean isHeaderRow() {
//		return header;
//	}

//	public void setHeaderRow(Boolean header) {
//		this.header = header;
//	}


}
