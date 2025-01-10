/*
 * Copyright (c) 2006-2025 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */
package edu.harvard.i2b2.ncbo.util;

import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import edu.harvard.i2b2.common.exception.I2B2Exception;
import edu.harvard.i2b2.common.util.ServiceLocator;

/**
 * This is the Ontology service's main utility class This utility class provides
 * support for fetching resources like datasouce, to read application
 * properties, to get ejb home,etc. $Id: OntologyUtil.java,v 1.15 2009/01/08
 * 19:27:01 lcp5 Exp $
 * 
 * @author rkuttan
 */
public class DataSourceUtil {

	/** spring bean name for datasource **/
	private static final String DATASOURCE_BEAN_NAME = "dataSource";
	
	/** spring bean name for database **/
	private static final String DATABASE_BEAN_NAME = "database";


	/** class instance field **/
	private static DataSourceUtil thisInstance = null;

	/** service locator field **/
	private static ServiceLocator serviceLocator = null;

	/** field to store application properties **/
	private static Properties appProperties = null;

	/** log **/
	protected final Log log = LogFactory.getLog(getClass());

	/** field to store app datasource **/
	private DataSource dataSource = null;

	/** single instance of spring bean factory **/
	private BeanFactory beanFactory = null;

	/**
	 * Private constructor to make the class singleton
	 */
	private DataSourceUtil() {
	}

	/**
	 * Return this class instance
	 * 
	 * @return OntologyUtil
	 */
	public static DataSourceUtil getInstance() {
		if (thisInstance == null) {
			thisInstance = new DataSourceUtil();
		}

		serviceLocator = ServiceLocator.getInstance();

		return thisInstance;
	}

	/**
	 * Return the ontology spring config
	 * 
	 * @return
	 */
	public BeanFactory getSpringBeanFactory() {
		if (beanFactory == null) {
			String appDir = null;

	/*		try {
				// read application directory property file via classpath
				Properties loadProperties = ServiceLocator
						.getProperties(APPLICATION_DIRECTORY_PROPERTIES_FILENAME);
				// read directory property
				appDir = loadProperties.getProperty(APPLICATIONDIR_PROPERTIES);
			} catch (I2B2Exception e) {
				log.error(APPLICATION_DIRECTORY_PROPERTIES_FILENAME
						+ "could not be located from classpath ");
			}
*/
//			if (appDir != null) {
			if(true) {
				FileSystemXmlApplicationContext ctx = new FileSystemXmlApplicationContext(
						"file:" //+ appDir + "/"
								+ "./ExtractionApplicationContext.xml");
				beanFactory = ctx.getBeanFactory();
			} else {
				FileSystemXmlApplicationContext ctx = new FileSystemXmlApplicationContext(
						"classpath:" + "ExtractionApplicationContext.xml");
				beanFactory = ctx.getBeanFactory();
			}
		}

		return beanFactory;
	}

	

	/**
	 * Return app server datasource
	 * 
	 * @return datasource
	 * @throws I2B2Exception
	 * @throws SQLException
	 */
	public DataSource getDataSource(String dataSourceName) throws I2B2Exception {
		// DataSource dataSource = (DataSource) getSpringBeanFactory()
		// .getBean(DATASOURCE_BEAN_NAME);

		dataSource = (DataSource) serviceLocator
				.getAppServerDataSource(dataSourceName);
		return dataSource;

	}

	
	// ---------------------
	// private methods here
	// ---------------------

	/**
	 * Load application property file into memory
	 */
/*	private String getPropertyValue(String propertyName) throws I2B2Exception {
		if (appProperties == null) {
			// read application directory property file
			Properties loadProperties = ServiceLocator
					.getProperties(APPLICATION_DIRECTORY_PROPERTIES_FILENAME);

			// read application directory property
			String appDir = loadProperties
					.getProperty(APPLICATIONDIR_PROPERTIES);

			if (appDir == null) {
				throw new I2B2Exception("Could not find "
						+ APPLICATIONDIR_PROPERTIES + "from "
						+ APPLICATION_DIRECTORY_PROPERTIES_FILENAME);
			}

			String appPropertyFile = appDir + "/"
					+ APPLICATION_PROPERTIES_FILENAME;

			try {
				FileSystemResource fileSystemResource = new FileSystemResource(
						appPropertyFile);
				PropertiesFactoryBean pfb = new PropertiesFactoryBean();
				pfb.setLocation(fileSystemResource);
				pfb.afterPropertiesSet();
				appProperties = (Properties) pfb.getObject();
			} catch (IOException e) {
				throw new I2B2Exception("Application property file("
						+ appPropertyFile
						+ ") missing entries or not loaded properly");
			}

			if (appProperties == null) {
				throw new I2B2Exception("Application property file("
						+ appPropertyFile
						+ ") missing entries or not loaded properly");
			}
		}

		String propertyValue = appProperties.getProperty(propertyName);

		if ((propertyValue != null) && (propertyValue.trim().length() > 0)) {
			;
		} else {
			throw new I2B2Exception("Application property file("
					+ APPLICATION_PROPERTIES_FILENAME + ") missing "
					+ propertyName + " entry");
		}

		return propertyValue;
	}  */
}
