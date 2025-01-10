/*
 * Copyright (c) 2006-2025 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */
package edu.harvard.i2b2.ncbo.writer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import edu.harvard.i2b2.ncbo.dao.ConceptPersistDao;
import edu.harvard.i2b2.ncbo.model.AllData;
import edu.harvard.i2b2.ncbo.model.DBInfoType;
import edu.harvard.i2b2.ncbo.model.ExpandedConceptType;
import edu.harvard.i2b2.ncbo.model.FormatData;
import edu.harvard.i2b2.ncbo.model.RunData;


public class MetadataWriter extends TableWriter{

	private HashMap<String, Object> entryMap = new HashMap<String, Object>();
	private Writer out = null;
	
	public MetadataWriter(){
		String filename = RunData.getInstance().getFileName();
		if(filename != null){
//			log.info(filename);
			try {
				out = new OutputStreamWriter(new FileOutputStream(filename));
			} catch (FileNotFoundException e) {
				log.error("Problem writing to file " + filename + " exiting");
				System.out.println("Problem writing to file " + filename + " exiting");
				System.exit(1);
			}
		}
	}

	

	public void writeHeaderToFile() throws Exception{
		String tableFile = RunData.getInstance().getFileName();

		if(tableFile == null)
			return;
		
		if(out == null)
			return;

		try {
	//		if(resultFile == null)
	//			resultFile = new RandomAccessFile(tableFile, "rw");

			ArrayList<String> columnsSelected = FormatData.getInstance().getColumns();

			Iterator<String> it1 = columnsSelected.iterator();
			String header = "";
			while(it1.hasNext()){
				if(header.equals(""))
					header = (String)it1.next();
				else
					header = header + "|" +  (String)it1.next();
			}
			header = header + "\n";
		
			out.write(header);
			
//			append(resultFile, header);
		} catch (Exception e1) {
			log.error("Error with result file " + tableFile + " exiting");
			System.out.println("Error with result file " + tableFile + " exiting");
			out.close();
			System.exit(1);
				
		}finally{
//			out.close();
		}
		
	}

	public void writeToErrorLogFile(String message) {
		String tableFile = RunData.getInstance().getFileName();

		if(tableFile == null)
			return;

		try {
			if(errorFile == null)
				errorFile = new RandomAccessFile(tableFile+".log", "rw");
			message = message +"\n";
			append(errorFile, message);
		} catch (Exception e1) {
			log.error("Error with error log file");	
		} finally{
			closeErrorFile();
		}
		
	}


	@Override
	public void writeToFile() throws Exception{
		String tableFile = RunData.getInstance().getFileName();
		// user can cancel out of overwrite dialog causing null file name.
		// if so, then skip this file.
		if(tableFile == null)
			return;
		if(out == null)
			return;
		try {
			List<ExpandedConceptType> allNodes = AllData.getInstance().getAllNodes();
			Iterator<ExpandedConceptType> it3 = allNodes.iterator();
			while (it3.hasNext()){
				ExpandedConceptType node = (ExpandedConceptType) it3.next();
				if(node != null){
					String output = "";
					if(!(node.getParentFullIds().isEmpty())){
						Iterator<String> parIt = node.getParentFullIds().iterator();
						while(parIt.hasNext()){
							String parent = (String) parIt.next();
							output = getOutput(node) +  "|\""+ parent + "\"|\"@\"" + " \r\n";
							out.write(output);
							if(!(node.getSynonyms().isEmpty())){
								Iterator<String> synIt = node.getSynonyms().iterator();
								while(synIt.hasNext()){
									output = getSynonymOutput(node, (String) synIt.next()) + "|\""+ parent + "\"|\"@\"" + " \r\n";
									out.write(output);
									//				append(resultFile, synOut);
									//				out.write(synOut);
								}
							}
						}
					}else{  // node is root
					//	node.setLevel(1);
					//	node.setVisualattributes("CA");
						output = getOutput(node) + "|\"(null)\"|\"@\""+ " \r\n";
						out.write(output);
						if(!(node.getSynonyms().isEmpty())){
							Iterator<String> synIt = node.getSynonyms().iterator();
							while(synIt.hasNext()){
								output = getSynonymOutput(node, (String) synIt.next()) + "|\"(null)\"|\"@\""+ " \r\n";
								out.write(output);
								//				append(resultFile, synOut);
								//				out.write(synOut);
							}
						}
					}
				//out.flush();
				}
				out.flush();
			}
			

		} catch (Exception e1) {
			log.error("Error with result file " + tableFile);
			System.out.println("Error with result file " + tableFile );
			out.close();
			throw e1;

		}finally {
			closeFile();
		}
		
	}
	
	
	public void writeToFile(ExpandedConceptType root, ConceptPersistDao dao, DBInfoType dbInfo) throws Exception{

		if(out == null)
			return;
		try {
			if(root != null){

				String output = getOutput(root) + "|\"(null)\"|\"@\""+ " \r\n";
			
				out.write(output);
				out.flush();
				if(root.getVisualattributes().startsWith("L"))
					return;
				if(root.getSynonymCd().equals("Y"))
					return;
				if(dao == null)
					return;
			}
			else{
				return;
			}

			List<ExpandedConceptType> children = dao.findChildNodes(root.getFullId(),dbInfo);

			Iterator it = children.iterator();
			while(it.hasNext()){
				ExpandedConceptType child = (ExpandedConceptType) it.next();
				child.setPath(root.getKey());				
				child.setSymbol(FormatData.getInstance().createSymbol(child.getSymbol()));
				child.setKey(child.getPath() + child.getSymbol() + "\\" );
				child.setDimcode(child.getKey());
				
				child.setLevel(root.getLevel() + 1);
				if(root.getTooltip() != null){
					if(root.getTooltip().length() - root.getTooltip().lastIndexOf("\n") > 120) {
						child.setTooltip(root.getTooltip() + " \n \\ " + child.getName());
					}
					else{
						child.setTooltip(root.getTooltip() + " \\ " + child.getName());
					}		
				}else
					child.setTooltip(child.getName());
				if(child.getTooltip().length() > 300){
					String[] tooltip = child.getTooltip().split("\n");
					int numLines = tooltip.length;
					if(numLines > 2){
						String truncatedTooltip = tooltip[0] + " \n" + 
								 " ... " + tooltip[numLines-1] ;
						child.setTooltip(truncatedTooltip);
					}
//					log.info("truncated tooltip is " + truncatedTooltip + " for fullname: " + child.getKey() );
				}
				writeToFile(child, dao, dbInfo);
			}
	
		} catch (Exception e1) {
			log.error("Error with result file " );
			System.out.println("Error with result file");
			out.close();
			throw e1;

		}finally {
			closeFile();
		}
		
	}





}
