package com.MRIoA.qa.testcases;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
import com.relevantcodes.extentreports.LogStatus;

@Listeners(CustomListener.class)
public class SubmittedServiceRequestScenarioTest extends BaseClass{
	
	PatientDashboardPage patientDash;
	LoginPage loginPage;
	static Connection connection;
	EmployeeLoginPage empLoginPage;
	EmployeeDashboardPage empDash;
	
	private static Logger logger =Logger.getLogger(SubmittedServiceRequestScenarioTest.class);
	
	@BeforeClass
	public void getDBConnection(){
		logger.info("In BeforeClass method of "+SubmittedServiceRequestScenarioTest.class.getSimpleName());
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
	
	@Test(priority=24, description="Submit service request")
	public void submitServiceRequestTest() throws SQLException, InterruptedException, TimeoutException{
		int patientNum = 1;
		ROLES patientRole = Constants.ROLES.PATIENT;
		String patient = patientRole.name().toLowerCase();
		String testName = "submitServiceRequestTest";
		//perofrming patient login
		loginPage.attemptPatientLogin(patientNum, connection, testName);
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(Constants.IMPLICIT_WAIT, TimeUnit.SECONDS);
		WebDriverWait wait = new WebDriverWait(driver,20);
		wait.until(ExpectedConditions.urlContains(PATIENT_BASE_URL+"/user/service/dashboard"));
		logger.info("Patient login successfully !!");
		Thread.sleep(2000);
		int serviceRequestNumber=4;
		logger.info("Patient is creating the service request");
		patientDash.createNewServiceRequest(patient,serviceRequestNumber,patientNum);
		wait.until(ExpectedConditions.urlContains("/user/service/dashboard"));
		Thread.sleep(2000);
		logger.info("Service request created successfully !!");
		int row =1;
		String expectedTitle =testProp.getString("request_title_"+serviceRequestNumber);
		String actualTitle = driver.findElement(By.xpath("//table[@class='table-box']/tbody/tr["+row+"]/td[1]")).getText();
		if (expectedTitle.equals(actualTitle)){
			extentTest.log(LogStatus.PASS, "verifyRejectedAndReopenedServiceRequestTest method is passed !!");
			logger.info("New Service Request with title "+expectedTitle+" created successfully on patient dashboard!!");
			Thread.sleep(2000);
		}
		else {
			String message = patientDash.textWhenNoSRIsCreated.getText();
			logger.info(message);
			extentTest.log(LogStatus.FAIL, "submitServiceRequestTest method is failed !!");
			logger.info("New Service Request with title "+expectedTitle+" is not created on patient dashboard !!");
			Assert.fail("Service request not created on patient dashboard !!");	
			}
			/*-----------Open new tab with supervsior  url and switch between these tabs------*/
			Thread.sleep(4000);
			((JavascriptExecutor) driver).executeScript("window.open('"+EMPLOYEE_BASE_URL+"')");
			logger.info("Supervisor url opened in new tab");
			int empNum = 1;
			String empRole = Constants.ROLES.SUPERVISOR.name().toLowerCase();
			boolean is2FAOpted = true;
			empDash.attemptEmployeeLogin(empRole, empNum, is2FAOpted, connection);
			logger.info("Employee login successfully !!");
			wait.until(ExpectedConditions.elementToBeClickable(EmployeeDashboardPage.newReopenedLink)).click();
			driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
			driver.manage().timeouts().pageLoadTimeout(Constants.IMPLICIT_WAIT, TimeUnit.SECONDS);
			wait.until(ExpectedConditions.urlContains(EMPLOYEE_BASE_URL+"/super/new"));
			logger.info("Navigated to New/Reopened Service Requests section");
			Thread.sleep(2000);
			WebElement searchResult = patientDash.searchRequest(expectedTitle,row);
			String titleToBeMatched = driver.findElement(By.xpath("//table[@class='table-box']/tbody/tr["+row+"]/td[1]")).getText();
			if(null==searchResult || !expectedTitle.equalsIgnoreCase(titleToBeMatched)){
				Assert.fail("No record found for searched title "+expectedTitle);
			}
			/*----------------Self assign service request process---------------------*/
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//table[@class='table-box']/tbody/tr["+row+"]/td[6]/button"))).click();
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='modal-window']")));
			logger.info("Modal window title is: "+driver.findElement(By.xpath("//div[@class ='modal-window'] /div/h2")).getText());
			driver.findElement(By.xpath("*//button//span[text()='Accept']/preceding-sibling::span[@class='btn-line']")).click();
			logger.info("Accept button is clicked");
			driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
			wait.until(ExpectedConditions.urlContains(EMPLOYEE_BASE_URL+"/super/new"));
			Thread.sleep(2000);
			
			/*------------------Submit Service Request process---------------------------*/
			driver.navigate().to(EMPLOYEE_BASE_URL+"/super/assigned");
			driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
			driver.manage().timeouts().pageLoadTimeout(Constants.IMPLICIT_WAIT, TimeUnit.SECONDS);
			logger.info("Navigated to Assigned Service Requests section");
			Thread.sleep(2000);
			empDash.submitServiceRequest();	
			Thread.sleep(2000);
			logger.info("Service request submitted successfully !!");
		
			/*---------Navigate to patient screen to verify closed status----------*/
			List<String> tabs = new ArrayList<String>(driver.getWindowHandles()); 
			driver.close();
			driver.switchTo().window(tabs.get(0));
			driver.navigate().refresh();
			driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
			driver.manage().timeouts().pageLoadTimeout(Constants.IMPLICIT_WAIT, TimeUnit.SECONDS);
			wait.until(ExpectedConditions.urlContains(PATIENT_BASE_URL+"/user/service/dashboard"));
			Thread.sleep(2000);	
			WebElement status = driver.findElement(By.xpath("//table[@class='table-box']/tbody/tr["+row+"]/td[3]/tc-badge"));
			String actualstatus = status.getText();
			String expectedStatusClosed = Constants.REQUEST_STATUS.Submitted.name();
			Assert.assertEquals(actualstatus,expectedStatusClosed, expectedStatusClosed+" status not found for "+expectedTitle+" on patient dashboard");
			logger.info("Staus of "+expectedTitle+" is updated as: "+actualstatus);	
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



	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	