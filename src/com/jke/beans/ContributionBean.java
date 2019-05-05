/*******************************************************************************
 * Licensed Materials - Property of IBM
 * (c) Copyright IBM Corporation 2010. All Rights Reserved. 
 * 
 * Note to U.S. Government Users Restricted Rights:  Use, 
 * duplication or disclosure restricted by GSA ADP Schedule 
 * Contract with IBM Corp.
 *******************************************************************************/

package com.jke.beans;

public class ContributionBean {
	private int accountNumber;
	private String organization;
	private double percentage;
	private String Date;

	public ContributionBean() {}

	public ContributionBean(int accountNumber, String organization, double percentage, String date) {
		super();
		this.accountNumber= accountNumber;
		this.organization= organization;
		this.percentage= percentage;
		Date= date;
	}

	public int getAccountNumber() {
		return accountNumber;
	}

	public String getOrganization() {
		return organization;
	}

	public double getPercentage() {
		return percentage;
	}

	public String getDate() {
		return Date;
	}

	public void setAccountNumber(int accountNumber) {
		this.accountNumber= accountNumber;
	}

	public void setOrganization(String organization) {
		this.organization= organization;
	}

	public void setPercentage(double percentage) {
		this.percentage= percentage;
	}

	public void setDate(String date) {
		Date= date;
	}
	
	public void validate() throws IllegalArgumentException {
		if (accountNumber < 0) {
			System.err.println("Potential unauthorized attempt to steal money!");
			throw new IllegalArgumentException("Invalid account number: "
					+ accountNumber);
		} else if (percentage < 1) {
			throw new IllegalArgumentException("Invalid donation amount: "
					+ accountNumber + " percent");
		}  
	}
}