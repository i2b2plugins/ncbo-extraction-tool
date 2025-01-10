"""
    Copyright (c) 2006-2025 Massachusetts General Hospital 
    All rights reserved. This program and the accompanying materials 
    are made available under the terms of the i2b2 Software License v2.1 
    which accompanies this distribution. 
    
    Contributors: David Wang

README:
    This is a sample python script that inserts lines from the 
    files NCBOOntologyProcessAll.java produce
    into a designated SQLServer database table called a 
    metadata table for i2b2. This may be a good option if the
    default import wizard for SQLServer fails to delineate 
    columns in your input file well.

    This script requires pyodbc and an odbc driver for SQLServer
    See (https://learn.microsoft.com/en-us/sql/connect/odbc/microsoft-odbc-driver-for-sql-server?view=sql-server-ver16)
"""

import pyodbc
import datetime

"""
    Test if a table specified by {schema_name.table_name} exists in a DB. 
    Closes the cursor after the query.
    Param: conn: connection to DB
    Param: schema_name: name of the schema
    Param: table_name:  name of the table
    Returns:    True if specified table exists.
                False if specified table does not exist.
"""
def tableExists( conn, schema_name, table_name ):
    query = """
        SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = ? and table_name = ?
        """
    # Use cursor.execute to execute query
    cursor = conn.cursor()
    cursor.execute(query, schema_name, table_name)

    returnVal = False   # table does not exist by default. Overwrite if otherwise
    # Use cursor.fetchone to retrieve result
    if cursor.fetchone()[0] == 1:
        returnVal = True
    cursor.close()
    return returnVal

"""
  1. Trim stringVal off leading and trailing white spaces.
  2. If stringVal is surrounded by double quotes, remove them.
"""
def cleanVal( stringVal ):
    stringVal = stringVal.strip()
    if stringVal[0] == '\"' and stringVal[-1] == '\"':
        stringVal = stringVal[1:-1]
    return stringVal

"""
Parses a delimited NCBO metadata file, often the result of NCBOOntologyProcessAll command
    and Writes the rows into a SQLServer DB table.

Assumes the first row contains the column headers.
:param file_path: Path to the delimited file
:param delimiter: The delimiter character (e.g., ',' for comma-delimited, '\t' for tab-delimited, '|' for pipe-delimited)
:param SERVER:      server name of the SQLServer to connect to
:param DATABASE:    name of Database to connect to
:param USERNAME:    user ID
:param PASSWORD:    user password
:param schemaName:  schema name 
:param tableName:   table name
:return: nothing
"""
def parse_final_file(file_path, delimiter, SERVER, DATABASE, USERNAME, PASSWORD, schemaName, tableName,): 
    print(f"{datetime.datetime.now()} \t Start Parsing file [{file_path}] and Inserting to [{DATABASE}].[{schemaName}].[{tableName}] on [{SERVER}] as [{USERNAME}]:")

    debug   = False
    
    connectionString = f'DRIVER={{ODBC Driver 17 for SQL Server}};SERVER={SERVER};DATABASE={DATABASE};UID={USERNAME};PWD={PASSWORD}'    
    conn = pyodbc.connect(connectionString);        # make the connection
    if (not tableExists):                           # proceed only if target table exists
        print(f"Target Table {schemaName}.{tableName} does not exist. No data is inserted. Script Exiting.")
        conn.close()
        return 

    cursor = conn.cursor()
    
    # Create an insertion SQL statement for insertion 
    query = """
    INSERT INTO """ + schemaName + '.' + tableName +  """
        VALUES(?,?,?,?,?,?, ?,?,?,?,?,?, ?,?,?,?, ?,?,?,?,?,?, ?,?,?);
    """
    count = 0
    with open(file_path, 'r') as f:
        headers = f.readline().strip().split(delimiter)                          # Read the first line to get column headers
        for line in f:
            values = line.strip().split(delimiter)                               # Split each line by the specified delimiter            
            row_dict = {header: value for header, value in zip(headers, values)} # Create a dictionary for each row

            # Perform insertion
            cursor.execute(query, cleanVal(row_dict['c_hlevel']), cleanVal(row_dict['c_fullname']), cleanVal(row_dict['c_name']), cleanVal(row_dict['c_synonym_cd']), \
                                  cleanVal(row_dict['c_visualattributes']), cleanVal(row_dict['c_basecode']), cleanVal(row_dict['c_facttablecolumn']), \
                                  cleanVal(row_dict['c_tablename']), cleanVal(row_dict['c_columnname']), cleanVal(row_dict['c_columndatatype']), \
                                  cleanVal(row_dict['c_operator']), cleanVal(row_dict['c_dimcode']), cleanVal(row_dict['c_tooltip']), \
                                  cleanVal(row_dict['sourcesystem_cd']), cleanVal(row_dict['c_symbol']), cleanVal(row_dict['c_path']), '(null)','(null)','(null)',\
                                  '(null)','(null)','(null)', cleanVal(row_dict['update_date']), cleanVal(row_dict['parent_fullId']), cleanVal(row_dict['m_applied_path']))
            count = count+1

    print(f"{datetime.datetime.now()} \t Total row(s) inserted: {count}")

    conn.commit()
    conn.close()
    return

"""
    - Main Function -
    Specify the required fields in the function.
    Please also ensure the desitnation table exists and 
    access permissions are configured for the 
    specified database user.
"""
def doTableInsertion():
    SERVER      = 'localhost'       # server name, can accept 'localhost'
    DATABASE    = 'DB_NAME'         
    USERNAME    = 'USER_NAME'
    PASSWORD    = 'USER_PASSWORD'
    schemaName  = 'SCHEMA_NAME'
    tableName   = 'TABLE_NAME'
    inputFileName = 'INPUT_FILE_NAME'

    parse_final_file( inputFileName, '|', SERVER, DATABASE, USERNAME, PASSWORD, schemaName, tableName)
    return


############################
#
# Main Script Starts Here
#
############################
doTableInsertion()