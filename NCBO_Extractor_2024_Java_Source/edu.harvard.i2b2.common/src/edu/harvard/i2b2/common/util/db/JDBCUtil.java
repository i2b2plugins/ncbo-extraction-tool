/*
 * Copyright (c) 2006-2025 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Rajesh Kuttan, Lori Phillips
 */

package edu.harvard.i2b2.common.util.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.RowSet;

public class JDBCUtil {

	/**
	 * Function to convert clob to string
	 * @param clob
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public static String  getClobString(Clob clob) throws SQLException, IOException { 
		BufferedReader stringReader = new BufferedReader(clob.getCharacterStream());
		String singleLine = null;
		StringBuffer strBuff = new StringBuffer();
			while ((singleLine = stringReader.readLine()) != null) { 
				strBuff.append(singleLine);
			}
		return strBuff.toString();
	}
	
	/**
	 * Use this function to escape single quote string
	 * For example: Hi' Hello --> Hi'' Hello
	 * @param value string 
	 * @return single quote escaped string
	 */
	public static String escapeSingleQuote(String value) { 
		String escapedValue = null;
		if (value != null) { 
			escapedValue = value.replaceAll("'", "\\''");
		}
		return escapedValue;
	}
	
	/**
	 * Helper function to cloase jdbc resources 
	 * @param rowSet
	 * @param stmt
	 * @param conn
	 * @throws SQLException
	 */
	public static void closeJdbcResource(RowSet rowSet,Statement stmt, Connection conn) throws SQLException { 
		if (rowSet != null) { 
			rowSet.close();
		}
		if (stmt != null) { 
			stmt.close();
		}
		if (conn != null) {
			conn.close();
		}
	}
}
