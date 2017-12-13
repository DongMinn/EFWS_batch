package com.eland.batch.utils;

public class CurrentDate {
	public static java.sql.Date getCurrentDate(){
		java.util.Date today = new java.util.Date();
	    return new java.sql.Date(today.getTime());
	}
}
