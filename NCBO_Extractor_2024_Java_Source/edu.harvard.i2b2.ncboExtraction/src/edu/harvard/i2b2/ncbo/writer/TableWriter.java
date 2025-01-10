/*
 * Copyright (c) 2006-2025 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips, David Wang
 */

package edu.harvard.i2b2.ncbo.writer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.log4j.Logger;


import edu.harvard.i2b2.ncbo.model.ExpandedConceptType;
import edu.harvard.i2b2.ncbo.model.RunData;

public abstract class TableWriter {


	protected final static Logger log = Logger.getLogger(TableWriter.class);

	protected RandomAccessFile resultFile = null;
	protected RandomAccessFile errorFile = null;

	private  String getZeroString(int i) {
		return ((i < 10) ? "0" + i : "" + i);
	}





	public void append(RandomAccessFile file, String outString)
	throws IOException {
		try {
			file.seek(file.length());
			file.writeBytes(outString);
		} catch (IOException e) {

			throw new IOException("trouble writing to random access file.");
		}
		return;
	}

	public void closeFile(){
		if(resultFile != null){
			try {
				resultFile.close();
			} catch (IOException e) {
				resultFile = null;
			} finally {
				resultFile = null;
			}
		}
	}

	public void closeErrorFile(){
		if(errorFile != null){
			try {
				errorFile.close();
			} catch (IOException e) {
				errorFile = null;
			} finally {
				errorFile = null;
			}
		}
	}


	public String handle(XMLGregorianCalendar date){
		if(date == null)
			return "";

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date.toGregorianCalendar().getTime());	

	}

	public Object handle(Object data){
		if(data == null)
			return "";
		else
			return data;    	
	}

	public int handleNumeric(int number){
		return number;
	}

	/*
	public String handleCUI(ListType cuiList){
		if(cuiList.getString().isEmpty())
			return "";
		else
			return cuiList.getString().get(0);
	}
	*/



	String getOutput(ExpandedConceptType node){

		String basecode = node.getBasecode(); 		
		if((basecode != null) && (basecode.length() > 0))
		{
			if((RunData.getInstance().getPrefix() != null) && (RunData.getInstance().getPrefix().length() > 0))
			{
				// tw7: only attach prefix if basecode does not already start with with it. e.g. prefix = "HP", basecode = "HP:123345"				
				if ( !basecode.startsWith( RunData.getInstance().getPrefix()) )
					basecode = RunData.getInstance().getPrefix() + basecode;
				// do nothing else	
			}
			if(RunData.getInstance().isAllowSummaryBasecodes() == false)
			{
				if(basecode.contains("-"))
					basecode = "(null)";
			}

		}
		else//(basecode == null)
			basecode = "(null)";

		String path = node.getPath(); 		
		if ((path == null) || (path.length() == 0)){
			path = "(null)";
		}

		String output = node.getLevel() + "|\""
						+ removeNewlineChars( node.getKey()) + "\"|\""
						+ removeNewlineChars( node.getName()) + "\"|\""
						+ removeNewlineChars( node.getSynonymCd()) + "\"|\""
						+ removeNewlineChars( node.getVisualattributes()) + "\"|\""
						+ removeNewlineChars( basecode ) + "\"|\"" 
						+ removeNewlineChars( node.getFacttablecolumn() ) + "\"|\""
						+ removeNewlineChars( node.getTablename() ) + "\"|\""
						+ removeNewlineChars( node.getColumnname() ) + "\"|\""
						+ removeNewlineChars( node.getColumndatatype() ) + "\"|\""
						+ removeNewlineChars( node.getOperator() ) + "\"|\""
						+ removeNewlineChars( node.getDimcode() ) + "\"|\""
						+ removeNewlineChars( node.getTooltip() ) + "\"|\""
						+ removeNewlineChars( node.getSourcesystemCd() ) + "\"|\""
						+ removeNewlineChars( node.getSymbol().replaceAll(" ", "_") ) + "\"|\""					// tdw9: remove space
						+ removeNewlineChars( path ) + "\"|\"" 
						+ removeNewlineChars( node.getSnomed_ct() ) + "\"|\""
						+ removeNewlineChars( node.getSnomed_rt() ) + "\"|\""
						+ removeNewlineChars( node.getCui() ) + "\"|\""
						+ removeNewlineChars( node.getTui() ) + "\"|\""
						+ removeNewlineChars( node.getCtv3() ) + "\"|\""
						+ removeNewlineChars( node.getFullId() ) + "\"|"
						+ removeNewlineChars( node.getDate() );
		return output;
	}

	// tdw9: when writing out to file, ensure str does not contain newline chars \n or \r
	String removeNewlineChars( String str )
	{
		if ( str == null ) return str;
		return str.replaceAll("(\\r|\\n)", "");
	}

	String getSynonymOutput(ExpandedConceptType node, String name){

		String basecode = node.getBasecode(); 		
		if((basecode != null) && (basecode.length() > 0)){
			if((RunData.getInstance().getPrefix() != null) && (RunData.getInstance().getPrefix().length() > 0)){
				basecode = RunData.getInstance().getPrefix() + basecode + "\"|\"";;
			}
			if(RunData.getInstance().isAllowSummaryBasecodes() == false){
				if(basecode.contains("-"))
					basecode = "(null)";
			}

		}
		else//(basecode == null)
			basecode = "(null)";

		String path = node.getPath(); 		
		if((path == null) || (path.length() == 0)){
			path = "(null)";
		}

		String output = node.getLevel() + "|\""
		+ node.getKey() + "\"|\""
		+ name + "\"|\""
		+ "Y" + "\"|\""
		+ node.getVisualattributes() + "\"|\""
		+ basecode  + "\"|\"" 
		+ node.getFacttablecolumn() + "\"|\""
		+ node.getTablename() + "\"|\""
		+ node.getColumnname() + "\"|\""
		+ node.getColumndatatype() + "\"|\""
		+ node.getOperator() + "\"|\""
		+ node.getDimcode()  + "\"|\""
		+ node.getTooltip()  + "\"|\""
		+ node.getSourcesystemCd()+ "\"|\""
		+ node.getSymbol() + "\"|\""
		+ path + "\"|\"" 
		+ node.getSnomed_ct() + "\"|\""
		+ node.getSnomed_rt() + "\"|\""
		+ node.getCui()  + "\"|\""
		+ node.getTui() + "\"|\""
		+ node.getCtv3() + "\"|\""
		+ node.getFullId() + "\"|"
		+ node.getDate() ;

		return output;
	}



	public void writeToFile() throws Exception{

	}

}
