/*******************************************************************************
 * Licensed Materials - Property of IBM
 * (c) Copyright IBM Corporation 2010. All Rights Reserved. 
 * 
 * Note to U.S. Government Users Restricted Rights:  Use, 
 * duplication or disclosure restricted by GSA ADP Schedule 
 * Contract with IBM Corp.
 *******************************************************************************/
package com.jke.db.data;

/**
 * Overrides the Derby specific column creation of database tables.
 * 
 * @author Florian Rosenberg
 */
public class MySQLGenerateData extends GenerateData {

	@Override
	protected String getTransactionTableColumnString() {
		return "(TransactionID INTEGER AUTO_INCREMENT, Type VARCHAR(30), AccountNumber INTEGER, Source VARCHAR(50), Amount DOUBLE, PostBalance DOUBLE, Date VARCHAR(20), ContributionID INTEGER, CHECK(((Type = 'Donation') AND (ContributionID IS NOT NULL)) OR ((Type <> 'Donation') AND (ContributionID IS NULL))), PRIMARY KEY (TransactionID))"; 
	}

	
}
