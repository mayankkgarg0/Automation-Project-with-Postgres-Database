package com.MRIoA.qa.testcases;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.MRIoA.qa.BaseClass;
import com.MRIoA.qa.CustomListener;
import com.MRIoA.qa.DataBaseConnectionFactory;
import com.MRIoA.qa.pageObjects.EmployeeDashboardPage;
import com.MRIoA.qa.pageObjects.EmployeeLoginPage;
import com.MRIoA.qa.pageObjects.LoginPage;
import com.MRIoA.qa.pageObjects.PatientDashboardPage;
import com.MRIoA.qa.utilities.Constants;
import com.MRIoA.qa.utilities.Constants.ROLES;
import com.MRIoA.qa.utilities.Utilities;
import com.relevantcodes.extentreports.LogStatus;

@Listeners(CustomListener.class)
public class NewServiceRequestScenarioTest extends BaseClass{
	
	PatientDashboardPage patientDash;
	LoginPage loginPage;
	Utilities util;
	static Connection connection;
	DataBaseConnectionFactory dbConnection;
	EmployeeLoginPage empLoginPage;
	EmployeeDashboardPage empDash;
	
	private static Logger logger = Logger.getLogger(NewServiceRequestScenarioTest.class);
	
	@BeforeClass
	public void getDBConnection(){
		logger.info("In BeforeClass method of "+NewServiceRequestScenarioTest.class.getSimpleName());
		connection = DataBaseConnectionFactory.getConnection();
		logger.info("Connection === "+connection);
	}
	
	@BeforeMethod
	public void setUp() throws InterruptedException{
		logger.info("<----Initializing the browser---->");
		initializingDriver();
		patientDash = new PatientDashboardPage();
		loginPage = new LoginPage();	
		empLoginPage = new EmployeeLoginPage();
		empDash = new EmployeeDashboardPage();
	}
	
	@Test(priority=21, description="Create New Service Request")
	public void verifyNewServiceRequestTest() throws SQLException, InterruptedException, TimeoutException{
		int patientNum = 2;
		ROLES patientRole = Constants.ROLES.PATIENT;
		String patient = patientRole.name().toLowerCase();
		String testName = "verifyNewServiceRequest";
		//perofrming patient login
		loginPage.attemptPatientLogin(patientNum, connection, testName);
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(Constants.IMPLICIT_WAIT, TimeUnit.SECONDS);
		WebDriverWait wait = new WebDriverWait(driver,20);
		wait.until(ExpectedConditions.urlContains(PATIENT_BASE_URL+"/user/service/dashboard"));
		int serviceRequestNumber=1;
		logger.info("Patient is creating new service request");
		patientDash.createNewServiceRequest(patient,serviceRequestNumber,patientNum);
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(Constants.IMPLICIT_WAIT, TimeUnit.SECONDS);
		wait.until(ExpectedConditions.urlContains("/user/service/dashboard"));
		Thread.sleep(2000);
		int row =1;
		String expectedTitle =testProp.getString("request_title_"+serviceRequestNumber);
		String actualTitle = driver.findElement(By.xpath("//table[@class='table-box']/tbody/tr["+row+"]/td[1]")).getText();
		if (expectedTitle.equals(actualTitle)){
			extentTest.log(LogStatus.PASS, "verifyNewServiceRequestTest method is passed !!");
			logger.info("New Service Request with title "+expectedTitle+" created successfully on patient dashboard!!");
			Thread.sleep(2000);
		}
		
		else {
			String message = patientDash.textWhenNoSRIsCreated.getText();
			logger.info(message);
			extentTest.log(LogStatus.FAIL, "verifyNewServiceRequestTest method is failed !!");
			logger.info("New Service Request with title "+expectedTitle+" is not created on patient dashboard !!");
			Assert.fail("New Service Request with title "+expectedTitle+" is not created on patient dashboard !!");
			Thread.sleep(2000);
			}
			
			/*--------------Search table row 1 for performing further actions-------------------*/
			WebElement searchResult = patientDash.searchRequest(expectedTitle,row);
			if(null==searchResult){
				Assert.fail("No record found for searched title "+expectedTitle);
			}
			/*---------Verify actions available on New Service Request [placed at 1st row in table]------------*/
			patientDash.checkActionsOnServiceRequest(searchResult);
			
			/*--------------View Service Request [placed at 1st row in table]----------------------------*/
			//page is refreshed due to stale element exception as element was not attached to the HTML DOM
			driver.navigate().refresh();
			driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
			wait.until(ExpectedConditions.urlContains("/user/service/dashboard"));
			logger.info("Refreshing the page and searching title again to perform View Request functionality !!");
			Thread.sleep(2000);
			searchResult = patientDash.searchRequest(expectedTitle,row);
			patientDash.viewServiceRequest(searchResult);
			
			/*--------------Edit Service Request [placed at 1st row in table]----------------------------*/
			driver.navigate().refresh();
			driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
			wait.until(ExpectedConditions.urlContains("/user/service/dashboard"));
			logger.info("Refreshing the page and searching title again to perform Edit Request functionality !!");
			Thread.sleep(2000);
			searchResult = patientDash.searchRequest(expectedTitle,row);
			patientDash.editServiceRequest(searchResult);
			
			/*--------------Delete Service Request [placed at 1st row in table]-----------------------*/
			driver.navigate().to(PATIENT_BASE_URL+"/user/service/dashboard");
			driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
			wait.until(ExpectedConditions.urlContains("/user/service/dashboard"));
			Thread.sleep(2000);
			searchResult = patientDash.searchRequest(expectedTitle,row);
			patientDash.deleteServiceRequest(searchResult);
			
			/*-----------Again creating New Service Request on patient dashboard after deletion----------*/
			Thread.sleep(2000);
			patientDash.createNewServiceRequest(patient,serviceRequestNumber,patientNum);	
			wait.until(ExpectedConditions.urlContains(PATIENT_BASE_URL+"/user/service/dashboard"));	
			logger.info("New Service request got created on "+patient+" dashboard");
			
			//Open new blank window:
			//((JavascriptExecutor) driver).executeScript("window.open()");
			
			/*-----------Open new tab with supervsior url and switch between these tabs------*/
			
			Thread.sleep(4000);
			logger.info("Employee url opened in new window");
			((JavascriptExecutor) driver).executeScript("window.open('"+EMPLOYEE_BASE_URL+"')");
			int empNum = 1;
			String empRole = Constants.ROLES.SUPERVISOR.name().toLowerCase();
			boolean is2FAOpted = true;
			empDash.attemptEmployeeLogin(empRole, empNum, is2FAOpted, connection);
			logger.info("Employee login successfully !!");
			wait.until(ExpectedConditions.elementToBeClickable(EmployeeDashboardPage.newReopenedLink)).click();
			logger.info("Navigated to New/Reopened Service Requests section");
			wait.until(ExpectedConditions.urlContains(EMPLOYEE_BASE_URL+"/super/new"));
			driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
			Thread.sleep(2000);
			searchResult = patientDash.searchRequest(expectedTitle,row);
			String titleToBeMatched = driver.findElement(By.xpath("//table[@class='table-box']/tbody/tr["+row+"]/td[1]")).getText();
			if(null==searchResult || !expectedTitle.equalsIgnoreCase(titleToBeMatched)){
				Assert.fail("No record found for searched title "+expectedTitle+" at row "+row);	
			}
			else if(expectedTitle.equalsIgnoreCase(titleToBeMatched)){
				/*---------Verify actions available on New Service Request [placed at 1st row in table]---------*/
				driver.navigate().refresh();
				Thread.sleep(2000);
				wait.until(ExpectedConditions.urlContains(EMPLOYEE_BASE_URL+"/super/new"));
				searchResult = patientDash.searchRequest(expectedTitle,row);
				empDash.checkActionsOnServiceRequest();
					
				/*--------------View Service Request [placed at 1st row in table]----------------------------*/
				driver.navigate().refresh();
				Thread.sleep(2000);
				wait.until(ExpectedConditions.urlContains(EMPLOYEE_BASE_URL+"/super/new"));
				searchResult = patientDash.searchRequest(expectedTitle,row);
				empDash.viewServiceRequest(searchResult);	
				driver.close();
				}
			}
	
	@AfterMethod
	public void tearDown(){
		patientDash.endTest();
		logger.info("Closing the browser");
		driver.quit();
	}
	
	@AfterClass
	public void closeDBConnection() {
		logger.info("DB connection closure under proces!!!");
		try {
			if (null != connection) {
				connection.close();
				logger.info("Connection closed successfully !!!!!");
			}
		} catch (SQLException e) {
			logger.info("Connection not closed !!!!!");
			e.printStackTrace();
		}
	}
	
	
}
