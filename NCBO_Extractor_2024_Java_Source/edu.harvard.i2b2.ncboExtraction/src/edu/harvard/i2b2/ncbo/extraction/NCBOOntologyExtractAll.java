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


import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;


import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


import edu.harvard.i2b2.common.util.jaxb.JAXBUtilException;
import edu.harvard.i2b2.ncbo.model.AllData;
import edu.harvard.i2b2.ncbo.model.RootData;
import edu.harvard.i2b2.ncbo.model.RunData;
import edu.harvard.i2b2.ncbo.util.NCBOJAXBUtil;
import edu.harvard.i2b2.ncbo.writer.MetadataWriter;
import edu.harvard.i2b2.ncboclient.datavo.ncbo.all.AllTermsType;

public class NCBOOntologyExtractAll extends AbstractNCBOOntologyTool
{
	/**
	 * URL of the server hosting the REST services for extracting information about the
	 * ontologies
	 */
//	private static final String serverUrl ="http://rest.bioontology.org/bioportal/";
	private static final String serverUrl ="http://data.bioontology.org/ontologies/";

	protected final static Logger log = Logger.getLogger(NCBOOntologyExtractAll.class);

	public NCBOOntologyExtractAll(String ontologyId, String outputFileName, String apikey, String timeout, String startPage) throws Exception
	{
		super( EXTRACT_TOOL_NAME );
		
		log.info( getToolNameAndVersionStamp() );
		
		// input parameters include optional startPage.
	
		RunData.getInstance().setFileName(outputFileName);
		RunData.getInstance().setNcboOntologyId(ontologyId);
		RunData.getInstance().setPrefix(null);
		//reUsed some methods in both extract and process
		//  Summary basecodes are needed in extraction - 
		RunData.getInstance().setAllowSummaryBasecodes(true);
		
		MetadataWriter writer = new MetadataWriter();
		writer.writeHeaderToFile();		
		
/*		//first get Root nodes.
		String rootURL = serverUrl + ontologyId + 
		"/classes/roots?include=properties,synonym&format=xml&apikey=" + apikey;
		
		JAXBElement rootsElement =getAllTerms(rootURL, timeout);
		// not sure what this does because roots get written in the all data below..
		if(rootsElement != null){
			log.info("Obtaining NCBO root nodes");	
			AllTermsType rootTerms  = (AllTermsType) rootsElement.getValue();
			RootData.getInstance().loadRootNodes(rootTerms);
			writer.writeToFile();					
			AllData.getInstance().clear();
		}
		else{
			log.info("Was not able to obtain root nodes; ending extraction");	
			System.exit(1);
			return;
		}
	*/	

		String URL = serverUrl + ontologyId + 
					"/classes?include=properties,synonym,children&format=xml&apikey=" + apikey 
					+ 	"&page=";
		
		int pageNum = Integer.valueOf(startPage);
//		int pageNum = 1;
		int pageLast = -1;
		int lastPageToTimeOut = -1;
		
	
	
		while(pageNum != pageLast + 1){
			String url = URL + pageNum;
			try {
				List<String> arguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
				
				JAXBElement jaxbElement =getAllTerms(url, timeout);
				if(jaxbElement != null){
					AllTermsType allTerms  = (AllTermsType) jaxbElement.getValue();
					pageNum = allTerms.getPage();
					pageLast = allTerms.getPageCount();
					AllData.getInstance().load(allTerms);
					log.info("Writing page: " + pageNum + " of " + pageLast);
					System.out.println("Writing page: " + pageNum + " of " + pageLast);
					writer.writeToFile();					
					AllData.getInstance().clear();
					pageNum++;
				}
				else{
					if(lastPageToTimeOut == pageNum){
						log.info("Page " + pageNum + " timed out twice; ending extraction");
						System.out.println("Page " + pageNum + " timed out twice; ending extraction");
						System.exit(1);
						break;
					}
					else{
						lastPageToTimeOut = pageNum;
						log.info("Page " + pageNum + " timed out; retrying");
					}
						
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				log.info("Program ending -- Exception on page " +	pageNum );
				System.out.println("Program ending -- Exception on page " +	pageNum );			
				System.exit(1);
			}

		}
		log.info("Extraction completed");
		System.out.println("Extraction completed");
		System.exit(0);
		
	}

	private JAXBElement getAllTerms(String url, String timeout){

		JAXBElement terms = null;

		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(url);

		// Provide custom retry handler if necessary
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
				new DefaultHttpMethodRetryHandler(1, false));

		method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 
				new Integer(timeout));
	//			new Integer(300000));
		
		try {
			// Execute the method.
			int statusCode = client.executeMethod(method);
			if (statusCode != HttpStatus.SC_OK) {
				log.error("Method failed: " + method.getStatusLine());
				log.error(method.getResponseBodyAsString());
				log.error("URL = " + url);
				method.releaseConnection();
				return terms;
			}

			// Read the response body.
				
			InputStream streamResponse = method.getResponseBodyAsStream();
//			System.out.println("got term");
			JAXBElement jaxbElement = NCBOJAXBUtil.getJAXBUtil().unMarshalFromInputStream(streamResponse);
	//		System.out.println("converted to jaxb");
			// Deal with the response.
			// Use caution: ensure correct character encoding and is not binary data
			terms = jaxbElement;
			

		} catch (HttpException e) {
			log.error("Fatal protocol violation: " + e.getMessage() + "for " + url);
		//	e.printStackTrace();
		} catch (IOException e) {
			log.error("Fatal transport error: " + e.getMessage() + "for " + url);
		//	e.printStackTrace();
		} catch (JAXBUtilException e) {
			log.error("JAXB error: " + e.getMessage() + "for " + url);
		//	e.printStackTrace();} 
			
		}finally {
			// Release the connection.
			method.releaseConnection();
		}  

		return terms;
	}

	/**
	 *
	 * Arguments are 
	 *	-ont  		bioportal ontology id you wish to extract
	 *  -apikey  	bioportal-assigned api key \n" +
	 *  -outputFileName  Name of the file where the staging data will reside \n";	
	 * 
	 */
	public static void main(String[] args)throws Exception {
		
		PropertyConfigurator.configure("./log4j.properties");
		
		Map<String, String> options = new HashMap<String, String>();

		options.putAll(simpleCommandLineParser(args));

		String ontologyId = options.get("-ont");

		String outputFileName = options.get("-outputFileName");
		String apikey = options.get("-apikey");
		String timeout = options.get("-timeout");
		String startingPage = options.get("-startPage");

		String runFlag = "run";
		
		String usage =  "Usage: arguments are \n" + 
						"\t -ont  		bioportal ontology id you wish to extract \n" +
						"\t -apikey  	bioportal-assigned api key \n" +
						"\t -outputFileName  Name of the file where the staging data will reside \n" +
						"\t -timeout	Optional timeout parameter in ms (defaults to 300000 ms) \n" +
						"\t -startPage  Optional NCBO start page (defaults to 1) \n" ;
						
		if(outputFileName == null || outputFileName.length() == 0){
			log.info("Must specify an output file name \n" );
			System.out.println("Must specify an output file name \n" );
			runFlag = null;
		}
		
		if(ontologyId == null || ontologyId.length() == 0){
			log.info("Must specify an ontology id \n");
			System.out.println("Must specify an ontology id \n");
			runFlag = null;
		}
		
		if(apikey == null || apikey.length() == 0){
			log.info("Must specify an apikey\n");
			System.out.println("Must specify an apikey\n");
			runFlag = null;
		}
		
		if(timeout == null || timeout.length() == 0){
			timeout = "300000";
		}else{
			try {
				new Integer(timeout);
			} catch (Exception e) {
				log.info("Invalid timeout parameter; must be an integer \n");
				System.out.println("Invalid timeout parameter; must be an integer \n");
				runFlag = null;
			}
		}
				

		if(startingPage == null || startingPage.length() == 0){
			startingPage = "1";
			
		}else{
			try {
				new Integer(startingPage);
			} catch (Exception e) {
				log.info("Invalid start page parameter; must be an integer \n");
				System.out.println("Invalid start page parameter; must be an integer \n");
				runFlag = null;
			}
		}
		
		
				
		
		if(runFlag == null){
			log.info(usage);
			System.out.println(usage);
			System.exit(1);
		}else{
			log.info("Extracting nodes to file: " + outputFileName);
			System.out.println("Extracting nodes to file: " + outputFileName);
	
			new NCBOOntologyExtractAll(ontologyId, outputFileName, apikey, timeout, startingPage);
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
