package com.MRIoA.qa.utilities;
 
public class SystemConstants{
	public static final String TEST_ENV = System.getenv("MRIOA_TEST_ENV");
	public static final String TEST_BROWSER = System.getenv("MRIOA_TEST_BROWSER");	
  
/*--------------------------------DEV Environment Variables---------------------------------------------------------*/

	/* App related configs start*/
	public static final String MRIOA_PATIENT_BASE_URL_DEV = System.getenv("MRIOA_PATIENT_BASE_URL_DEV");
	public static final String MRIOA_EMPLOYEE_BASE_URL_DEV = System.getenv("MRIOA_EMPLOYEE_BASE_URL_DEV");
	
	/* DB related configs start*/
	public static final String MRIOA_DB_URL_DEV = System.getenv("MRIOA_DB_URL_DEV");
	public static final String MRIOA_DB_USERNAME_DEV = System.getenv("MRIOA_DB_USERNAME_DEV");
	public static final String MRIOA_DB_PWD_DEV = System.getenv("MRIOA_DB_PWD_DEV");

/*--------------------------------QA Environment Variables---------------------------------------------------------*/
	
	
	
	
}