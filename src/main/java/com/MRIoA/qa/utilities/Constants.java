package com.MRIoA.qa.utilities;

public class Constants {

	public static final long PAGE_LOAD_TIMEOUT= 60;
	public static final long IMPLICIT_WAIT = 50;
	public static final String LOGIN_PAGE_SUFFIX = "/user/login";
	public static enum ROLES {PATIENT,SUPERVISOR,CASEMANAGER}	
	public static enum ENVIRONMENT {DEV,QA,PROD}	
	public static enum BROWSER {CHROME,FIREFOX,IE}
	public static enum REQUEST_STATUS {New,Assigned,Rejected,Reopened,Submitted,Closed}
}