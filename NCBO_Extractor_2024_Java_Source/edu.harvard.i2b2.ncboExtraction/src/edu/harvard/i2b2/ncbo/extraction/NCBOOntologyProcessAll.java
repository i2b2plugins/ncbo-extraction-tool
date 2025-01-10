/*
 * Copyright (c) 2006-2025 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips, David Wang
 */

package edu.harvard.i2b2.ncbo.extraction;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;


import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import edu.harvard.i2b2.ncbo.dao.ConceptPersistDao;
import edu.harvard.i2b2.ncbo.model.DBInfoType;
import edu.harvard.i2b2.ncbo.model.ExpandedConceptType;
import edu.harvard.i2b2.ncbo.model.FormatData;
import edu.harvard.i2b2.ncbo.model.RootData;
import edu.harvard.i2b2.ncbo.model.RunData;
import edu.harvard.i2b2.ncbo.util.SymbolHash;
import edu.harvard.i2b2.ncbo.writer.MetadataWriter;
import edu.harvard.i2b2.ncboclient.datavo.ncbo.all.ClassType;
import edu.harvard.i2b2.ncboclient.datavo.ncbo.all.PrefLabelCollectionType;
import edu.harvard.i2b2.ncboclient.datavo.ncbo.all.PropertiesType;

public class NCBOOntologyProcessAll extends AbstractNCBOOntologyTool
{
	/**
	 * URL of the server hosting the REST services for extracting information about the
	 * ontologies
	 */
	protected final static Logger log = Logger.getLogger(NCBOOntologyProcessAll.class);
	protected final static String UNCLASSIFIED_NAME 	= "Unclassified";
	protected final static String UNCLASSIFIED_BASE_CD  = "I2B2UNCLASSIFIED";
	protected final static String UNCLASSIFIED_FULLID   = "http://i2b2.org/ontology/UNCLASSIFIED";
	protected final static String SEARCH_NAME 			= "Search";
	protected final static String SEARCH_BASE_CD  		= "I2B2SEARCH";
	protected final static String SEARCH_BASE_TOOLTIP  	= "Use Search!";
	protected final static String SEARCH_FULLID   		= "http://i2b2.org/ontology/SEARCH";
		
	private DataSource dataSource;
	private DBInfoType dbInfo;
	
	public NCBOOntologyProcessAll(String rootNodeName, String prefix, String pathFormat, String outputFileName, boolean allowSummaryBasecodes) throws Exception
	{
		super( PROCESS_TOOL_NAME );
		
		System.out.println( getToolNameAndVersionStamp() );
		
		RunData.getInstance().setRootNodeName(rootNodeName);
		RunData.getInstance().setFileName(outputFileName);
		RunData.getInstance().setPrefix(prefix);
		RunData.getInstance().setSymbolFormat(pathFormat);
		RunData.getInstance().setAllowSummaryBasecodes(allowSummaryBasecodes);
		
		MetadataWriter writer = new MetadataWriter();
		writer.writeHeaderToFile();
		
		ExpandedConceptType root = new ExpandedConceptType();
		root.setKey("\\");
		//Print out single root node if specified
		if (rootNodeName != null){
			try {
				ClassType node = new ClassType();
			//	List<String> prefLabelList = new ArrayList();
			//	prefLabelList.add(rootNodeName);
				PrefLabelCollectionType prefCollection = new PrefLabelCollectionType();
				prefCollection.getPrefLabel().add(rootNodeName);
				PropertiesType property = new PropertiesType();
				property.setPrefLabelCollection(prefCollection);
				node.setProperties(property);
			
				root = RootData.getInstance().createNode(node);
				writer.writeToFile(root, null, null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.error("Error creating root node " + rootNodeName + " program exiting");
				System.out.println("Error creating root node " + rootNodeName + " program exiting");
				System.exit(1);
				return;
			}
		}
		
		ApplicationContext context =  new FileSystemXmlApplicationContext(
				"file:" //+ appDir + "/"
				+ "./ExtractionApplicationContext.xml");
			
		dbInfo = (DBInfoType) context.getBean("database");
		dataSource = (DataSource) context.getBean("dataSource");
				ConceptPersistDao dao = (ConceptPersistDao) context.getBean("conceptDAO");

		List<ExpandedConceptType> roots =  dao.findRootNodes(dbInfo);
		log.info(roots.size() + " level 1 nodes found");
		System.out.println(roots.size() + " level 1 nodes found");
		Iterator it = roots.iterator();
		while(it.hasNext()){
			ExpandedConceptType ncboRoot = (ExpandedConceptType) it.next();
			
			ncboRoot.setSymbol(FormatData.getInstance().createSymbol(ncboRoot.getSymbol()));
			ncboRoot.setKey(root.getKey() + ncboRoot.getSymbol() + "\\" );
			ncboRoot.setDimcode(ncboRoot.getKey());
			if (rootNodeName != null){
				ncboRoot.setTooltip(root.getTooltip() + " \\ " + ncboRoot.getName());
				ncboRoot.setPath(root.getKey());
			}else{
				ncboRoot.setTooltip(ncboRoot.getName());
				ncboRoot.setPath(null);
			}
			log.info("Writing category " + ncboRoot.getName());
			writer.writeToFile(ncboRoot, dao, dbInfo);
		}

		/* tdw7: new code to deal with nodes that are unreachable nodes from root */
		/* Test to see if there exists unreachable nodes */
		List<ExpandedConceptType> unreachables =  dao.findUnreachableNodes(dbInfo);
		if ( unreachables.size() > 0)
		{
			/* Create UNCLASSIFIED Node */
			ExpandedConceptType unc = makeUnclassifiedFolder( root, rootNodeName );
			log.info("Writing UNCLASSIFIED NODE: " + unc.getName());
			writer.writeToFile(unc, dao, dbInfo);
			
			ExpandedConceptType search = makeSearchFolder( unc );
			log.info("Writing SEARCH NODE: " + search.getName());
			writer.writeToFile(search, dao, dbInfo);

			System.out.println("Unreachable node(s) detected.");
			System.out.println("\t1. Creating UNCLASSIFIED node '" + unc.getName() +"'");
			System.out.println("\t2. Creating SEARCH NODE node '" + search.getName() + "'");
			System.out.println("\t3. Writing " + unreachables.size() + " " + "unreachable nodes...");
			
			log.info("Writing Unreachables (" + unreachables.size() +")" );
			int x = 0;
			for ( ExpandedConceptType unreachable : unreachables )
			{
				// tdw9: should also check unreachable's children?
				unreachable = updateUnreachableConcept( search, unreachable );
				writer.writeToFile(unreachable, dao, dbInfo);
				x++;
			}
			log.info("Finished writing Unreachables (" + x + "/" + unreachables.size() +")" );
			System.out.println("\t4. Finished writing Unreachables (" + x + "/" + unreachables.size() +")" );			
		}
		
		log.info("Processing completed");
		System.out.println("Processing complete");
		System.exit(0);
	}
	
	
	/* TODO: (bugbug) Need to deal with the case that there is no user-specified root node (node) */
	// returns an ExpandedConceptType that is a child of the root
	private ExpandedConceptType makeUnclassifiedFolder( ExpandedConceptType root, String rootNodeName ) throws Exception
	{		
		ExpandedConceptType unc = new ExpandedConceptType();
		unc.setName( UNCLASSIFIED_NAME );
		unc.setBasecode( UNCLASSIFIED_BASE_CD );
		
		// set basic values
		unc.setOperator("LIKE");
		unc.setColumndatatype("T");
		unc.setColumnname("concept_path");
		unc.setFacttablecolumn("concept_cd");
		unc.setSynonymCd("N");
		unc.setTablename("concept_dimension");		
		unc.setVisualattributes("FA");		// set isFolder
		unc.setLevel( 1 );					// set level 1
		unc.setFullId( UNCLASSIFIED_FULLID ); // do not leave fullID as "(NULL)" otherwise all unreachable nodes will be its children when writing out
		
		// compute for other values: symbol, key dimcode, tooltip, path
		unc.setSymbol( "(" + unc.getBasecode() + ") " + unc.getName() ); 		// build symbol from basecode and name
		unc.setSymbol(FormatData.getInstance().createSymbol(unc.getSymbol()));	// set new symbol using hash
		unc.setKey(root.getKey() + unc.getSymbol() + "\\" );					// set key, which also c_fullname
		unc.setDimcode(unc.getKey());											// set dimcode = key
		unc.setSourcesystemCd(root.getSourcesystemCd() ); 						// set source system to be the same as root's (usually USER for user-specified) -- or we could use "I2B2" or "NCBO_Estractor"
		
		if (rootNodeName != null){
			unc.setTooltip(root.getTooltip() + " \\ " + unc.getName());
			unc.setPath(root.getKey());
		}else{
			unc.setTooltip(unc.getName());
			unc.setPath(null);
		}			
		// Note: date is automatically set by getDate() to NOW when writeToFile below is called.
		return unc;
	}
	
	// returns an ExpandedConceptType that is a child of the root
	private ExpandedConceptType makeSearchFolder( ExpandedConceptType unclassified ) throws Exception
	{
		ExpandedConceptType search = new ExpandedConceptType();
		search.setName( SEARCH_NAME );
		search.setBasecode( SEARCH_BASE_CD );
		
		// set basic values
		search.setOperator("LIKE");
		search.setColumndatatype("T");
		search.setColumnname("concept_path");
		search.setFacttablecolumn("concept_cd");
		search.setSynonymCd("N");
		search.setTablename("concept_dimension");		
		search.setVisualattributes("FA");					// set isFolder
		search.setLevel( unclassified.getLevel() + 1 );		// set level 1 more than parent
		search.setFullId( SEARCH_FULLID ); // do not leave fullID as "(NULL)" otherwise all unreachable nodes will be its children when writing out
		
		// compute for other values: symbol, key dimcode, tooltip, path
		search.setSymbol( "(" + search.getBasecode() + ") " + search.getName() ); 	// build symbol from basecode and name
		search.setSymbol(FormatData.getInstance().createSymbol(search.getSymbol()));// set new symbol using hash
		search.setKey(unclassified.getKey() + search.getSymbol() + "\\" );			// set key, which also c_fullname
		search.setDimcode(search.getKey());											// set dimcode = key
		search.setSourcesystemCd(unclassified.getSourcesystemCd() ); 				// set source system to be the same as parent (Unclassified -- usually "USER"
		
		search.setTooltip( unclassified.getTooltip() + " \\ " + search.getName() );
		search.setPath( unclassified.getKey() );
		// Note: date is automatically set by getDate() to NOW when writeToFile below is called.
		return search;
	}	
	
	// modify ExpandedConceptType c to make it a child of parent, then write out
	private ExpandedConceptType updateUnreachableConcept( ExpandedConceptType parent, ExpandedConceptType unreachable ) throws Exception
	{
		unreachable.setLevel( parent.getLevel() + 1 );											// level
		unreachable.setSymbol( FormatData.getInstance().createSymbol(unreachable.getSymbol()) );// update symbol with hash
		unreachable.setKey( parent.getKey() + unreachable.getSymbol() + "\\" );					// key (also c_fullname)
		unreachable.setDimcode( unreachable.getKey() );											// dimcode
		unreachable.setTooltip( parent.getTooltip() + " \\ " + unreachable.getName() );
		unreachable.setPath( parent.getKey() );
		// Note: date is automatically set by getDate() to NOW when writeToFile below is called.
		return unreachable;
	}
	
	/**
	 * -outputFileName : Name of the file where the final data will reside
	 * 
	 */
	public static void main(String[] args)throws Exception {
		
		PropertyConfigurator.configure("./log4j.properties");
		
		boolean runFlag = true;
		boolean allowSummaryBasecodes = false;
		
		Map<String, String> options = new HashMap<String, String>();
		options.putAll(simpleCommandLineParser(args));

		String usage =  "Usage: arguments are \n" + 
		"\t -rootNodeName  If a single root node is desired, list name \n" +
		"\t -prefix  Prefix (scheme) to prepend to basecodes \n" +
		"\t -pathFormat  S[hort] / M[edium] (default) / R[eadable] \n" +
		"\t -outputFileName  Full path name of the file where the final data will reside" ;
	//	"\t -summaryBasecodes  Optional parameter [allow/yes/no]; if omitted or [no] summary basecodes (V00-V20) are not allowed \n" ;
		
		String rootNodeName = options.get("-rootNodeName");
		String prefix = options.get("-prefix");
		String pathFormat = options.get("-pathFormat");
		String outputFileName = options.get("-outputFileName");
		String summaryBasecodes = options.get("-summaryBasecodes");
		
		String reportOutput = AbstractNCBOOntologyTool.PROCESS_TOOL_NAME + " " + VERSION_STAMP + " \n";
		
		reportOutput += "File is '|' delimited; first row specifies column headings \n" +
				" Strings are quoted with the ' \" ' identifier \n";
		
		if(rootNodeName == null || rootNodeName.length() == 0){
			System.out.println("A single root node has not been specified");
			reportOutput = reportOutput + "A single root node was not specified \n";
		} 
		else{
			System.out.println("A single root node name of " + rootNodeName + " has been specified");
			reportOutput = reportOutput + "A single root node name of " + rootNodeName + " was specified \n";
		}
		
		
		if(prefix == null || prefix.length() == 0){
			System.out.println("Basecodes will have no scheme prefix");
			reportOutput = reportOutput + "Prefix: None \n";
		} 
		else{
			if(!(prefix.endsWith(":")))
				prefix = prefix + ":";
			System.out.println("Basecodes will be prepended with " + prefix);
			reportOutput = reportOutput + "Prefix: " + prefix + "\n";
		}
			
		if(pathFormat == null || pathFormat.length() == 0){
			pathFormat = "M";
			reportOutput = reportOutput + "Path format: Medium \n";
			System.out.println("Path format not specified; defaulting to Medium");
		} 	
		else if(pathFormat.toLowerCase().equals("medium") || pathFormat.toLowerCase().equals("m")){
			reportOutput = reportOutput + "Path format: Medium \n";
			pathFormat = "M";
		} 
		else if(pathFormat.toLowerCase().equals("short") || pathFormat.toLowerCase().equals("s")){
			reportOutput = reportOutput + "Path format: Short \n";
			pathFormat = "S";
		} 
		else if(pathFormat.toLowerCase().equals("readable") || pathFormat.toLowerCase().equals("r")){
			reportOutput = reportOutput + "Path format: Readable \n";
			pathFormat = "L";
		} 
		else{
			System.out.println("Path format is unrecognizable; defaulting to medium");
			reportOutput = reportOutput + "Path format: Medium \n";
			pathFormat = "M";
		}
		
		if(outputFileName == null || outputFileName.length() == 0){
			System.out.println("You must specify an output file name \n" );
			//System.out.println(usage);
			runFlag = false;
		}
		
		if(summaryBasecodes == null || summaryBasecodes.length() == 0){
			allowSummaryBasecodes = true;
	//		reportOutput = reportOutput + "Summary basecodes omitted \n";
		}else{
			if(summaryBasecodes.equals("no")){
				allowSummaryBasecodes = false;
				reportOutput = reportOutput + "Summary basecodes omitted \n";
			}
			else if((summaryBasecodes.equals("allow")) || (summaryBasecodes.equals("yes")) || (summaryBasecodes.equals("y"))){
				allowSummaryBasecodes = true;
				reportOutput = reportOutput + "Summary basecodes allowed \n";
			}
			else{
				System.out.println("Summary basecode option not recognized; defaulting to 'allow' \n" );
				reportOutput = reportOutput + "Summary basecodes allowed \n";
				allowSummaryBasecodes = true;
			}
		}
		
		if(runFlag == false){
			log.info(usage);
			System.out.println(usage);
			System.exit(1);
		}	
		else{		
			if(outputFileName != null){
				String reportFileName = "";
				int index = outputFileName.indexOf(".");
				if(index > 0)
					reportFileName = outputFileName.substring(0, index) + ".report";
				else
					reportFileName = outputFileName + ".report";
				try {
					OutputStreamWriter report = new OutputStreamWriter(new FileOutputStream(reportFileName));
					report.write(reportOutput);
					report.close();
				} catch (Exception e) {
					log.error("Problem with report output file; exiting");
					System.out.println("Problem with report output file");
					System.exit(1);
				}
				
			}
			
			System.out.println("Processing data to file: " + outputFileName);
			new NCBOOntologyProcessAll(rootNodeName, prefix, pathFormat, outputFileName, allowSummaryBasecodes);
		}
	}
	
	/**
	   * Simple method which turns an array of command line arguments into a
	   * map, where each token starting with a '-' is a key and the following
	   * non '-' initial token, if there is one, is the value.  For example,
	   * '-size 5 -verbose' will produce a map with entries (-size, 5) and
	   * (-verbose, null).
	   */
	  public static Map<String, String> simpleCommandLineParser(String[] args) {
	    Map<String, String> map = new HashMap<String, String>();
	    for (int i = 0; i <= args.length; i++) {
	      String key = (i > 0 ? args[i-1] : null);
	      String value = (i < args.length ? args[i] : null);
	      if (key == null || key.startsWith("-")) {
	        if (value != null && value.startsWith("-"))
	          value = null;
	        if (key != null || value != null)
	        	map.put(key, value);
	      }
	    }
	    return map;
	  }


}
