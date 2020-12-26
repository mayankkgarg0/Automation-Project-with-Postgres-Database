package com.MRIoA.qa.testcases;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import com.MRIoA.qa.BaseClass;
import com.MRIoA.qa.CustomListener;
import com.MRIoA.qa.DataBaseConnectionFactory;
import com.MRIoA.qa.pageObjects.EmployeeLoginPage;
import com.MRIoA.qa.utilities.Constants;
import com.relevantcodes.extentreports.LogStatus;

@Listeners(CustomListener.class)
public class EmployeeLoginPageTest extends BaseClass{
	
	private static Logger logger = Logger.getLogger(EmployeeLoginPageTest.class);
	public static Connection connection;
	EmployeeLoginPage empLoginPage;
	
	@BeforeClass
	public void getDBConnection() {
		logger.info("In BeforeClass method of " + EmployeeLoginPageTest.class.getSimpleName());
		connection = DataBaseConnectionFactory.getConnection();
		logger.info("Connection === " + connection);//this will return connection address
	}
	
	@BeforeMethod
	public void setUp() throws InterruptedException{
		logger.info("<----Initializing the browser---->");
		initializingDriver();
		empLoginPage = new EmployeeLoginPage();		
	}
	
	@Test(priority=13, description="Validate Case manager login with valid OTP")
	public void caseManagerLogin_WithValidOTPTest() throws InterruptedException, SQLException{
		driver.get(EMPLOYEE_BASE_URL);
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		empLoginPage.startTest("caseManagerLogin_WithValidOTPTestTest"); 
		logger.info("Running caseManagerLogin_WithValidOTPTest method");
		int empNum = 1;
		String empRole = Constants.ROLES.CASEMANAGER.name().toLowerCase();
		boolean is2FAOpted = true;
		empLoginPage.doLogin(empRole, empNum, is2FAOpted);
			String userEmail = testProp.getString(empRole+"_"+empNum+"_email_address");
			WebDriverWait wait = submitOTP(connection,userEmail,null,driver);
			SoftAssert softAssert = new SoftAssert();
			if(null==wait){
				extentTest.log(LogStatus.FAIL, "OTP not available for email address: "+userEmail);
				logger.info("caseManagerLogin_WithValidOTPTest method failed as OTP is not available for email address: "+userEmail);
				return;
			}
			logger.info("Validate "+empRole+" login successfully and navigate to New Service Requests dashboard or not?");
			wait.until(ExpectedConditions.urlContains("/case/new"));
			logger.info(getCurrentUrl(driver));
			String dashboardUrl = getCurrentUrl(driver);
			String expectedUrl = EMPLOYEE_BASE_URL + "/case/new";
			softAssert.assertEquals(dashboardUrl, expectedUrl,empRole+" failed to login and not navigated to dashboard!!");
			softAssert.assertAll();	
			if (expectedUrl.equals(dashboardUrl)) {
				extentTest.log(LogStatus.PASS, "caseManagerLogin_WithValidOTPTest passed");
				logger.info(empRole+" navigated to New Service Requests successfully !!");
			}
			else{
				extentTest.log(LogStatus.FAIL, "caseManagerLogin_WithValidOTPTest failed");
				logger.info(empRole+" failed to navigated to New Service Requests dashboard !!");
			}			
	}
	
	@Test(priority = 14, description="Validate Case manager login with invalid OTP")
	public void caseManagerLogin_WithInvalidOTPTest() throws InterruptedException, SQLException {
		driver.get(EMPLOYEE_BASE_URL);
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		empLoginPage.startTest("caseManagerLogin_WithInvalidOTPTest"); 
		int empNum = 1;
		String empRole = Constants.ROLES.CASEMANAGER.name().toLowerCase();
		boolean is2FAOpted = true;
		empLoginPage.doLogin(empRole, empNum, is2FAOpted);
		WebDriverWait wait = submitOTP(connection,null,Integer.valueOf(testProp.getString("invalid_OTP")), driver);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("*//p/span[text()='Incorrect OTP']")));
		String actualMessage = driver.findElement(By.xpath("*//p/span[text()='Incorrect OTP']")).getText();
		String expectedMessage = "Incorrect OTP";
		SoftAssert softAssert = new SoftAssert();
		logger.info("Validate Incorrect OTP error message received or not !!");
		softAssert.assertEquals(actualMessage, expectedMessage, "Correct error message not received !!");	
		softAssert.assertAll();
		if (expectedMessage.equals(actualMessage)) {
			extentTest.log(LogStatus.PASS, "caseManagerLogin_WithInvalidOTPTest method is passed !!");
			logger.info("Correct error message displayed on OTP screen !!");
		}
		else{
			extentTest.log(LogStatus.FAIL, "caseManagerLogin_WithInvalidOTPTest method is failed !!");
			logger.info("Incorrect error message displayed on OTP screen !!");
		}	
	}
	
	@Test(priority=15, description="Validate Supervisor login")
	public void supervisorLogin_WithValidOTPTest() throws InterruptedException, SQLException{
		driver.get(EMPLOYEE_BASE_URL);
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		empLoginPage.startTest("supervisorLogin_WithValidOTPTest"); 
		logger.info("Running supervisorLogin_WithValidOTPTest");
		int empNum = 1;
		String empRole = Constants.ROLES.SUPERVISOR.name().toLowerCase();
		boolean is2FAOpted = true;
		empLoginPage.doLogin(empRole, empNum, is2FAOpted);
			String userEmail = testProp.getString(empRole+"_"+empNum+"_email_address");
			WebDriverWait wait = submitOTP(connection,userEmail,null,driver);
			SoftAssert softAssert = new SoftAssert();
			if(null==wait){
				extentTest.log(LogStatus.FAIL, "OTP not available for email address: "+userEmail);
				logger.info("caseManagerLogin_WithValidOTPTest method failed as OTP is not available for email address: "+userEmail);
				return;
			}
			logger.info("Validate "+empRole+" login successfully and navigate to dashboard or not?");
			wait.until(ExpectedConditions.urlContains("/super/dashboard"));
			logger.info(getCurrentUrl(driver));
			String dashboardUrl = getCurrentUrl(driver);
			String expectedUrl = EMPLOYEE_BASE_URL+"/super/dashboard";
			softAssert.assertEquals(dashboardUrl, expectedUrl,empRole+" failed to login and not navigated to dashboard !!");
			softAssert.assertAll();	
			if (expectedUrl.equals(dashboardUrl)) {
				extentTest.log(LogStatus.PASS, "supervisorLogin_WithValidOTPTest passed");
				logger.info(empRole+" login successfully and navigated to dashboard !!");
			}
			else{
				extentTest.log(LogStatus.FAIL, "supervisorLogin_WithValidOTPTest failed");
				logger.info(empRole+" login failed and not navigated to dashboard !!");
			}	
	}
	
	@Test(priority=16, description="Validate logout functionality")
	public void casemanager_logoutTest() throws InterruptedException, SQLException{
		driver.get(EMPLOYEE_BASE_URL);
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		empLoginPage.startTest("casemanager_logoutTest"); 
		logger.info("Running casemanager_logoutTest method");
		int empNum = 1;
		String empRole = Constants.ROLES.CASEMANAGER.name().toLowerCase();
		boolean is2FAOpted = true;
		empLoginPage.doLogin(empRole, empNum, is2FAOpted);
			String userEmail = testProp.getString(empRole+"_"+empNum+"_email_address");
			WebDriverWait wait = submitOTP(connection,userEmail,null,driver);
			if(null==wait){
				extentTest.log(LogStatus.FAIL, "OTP not available for email address: "+userEmail);
				logger.info("caseManagerLogin_WithValidOTPTest method failed as OTP is not available for email address: "+userEmail);
				return;
			}
			logger.info("Validate "+empRole+" login successfully and navigate to dashboard or not?");
			wait.until(ExpectedConditions.urlContains("/case/new"));
			logger.info(getCurrentUrl(driver));
			empLoginPage.logout();
			SoftAssert softAssert = new SoftAssert();
			String currentUrl = getCurrentUrl(driver);
			String expectedUrl = EMPLOYEE_BASE_URL+"/login";
			softAssert.assertEquals(currentUrl, expectedUrl,empRole+" logout failed !!");
			softAssert.assertAll();	
			if (expectedUrl.equals(currentUrl)) {
				extentTest.log(LogStatus.PASS, "casemanager_logoutTest passed");
				logger.info(empRole+" logout successfully !!");
			}
			else{
				extentTest.log(LogStatus.FAIL, "casemanager_logoutTest failed");
				logger.info(empRole+" logout failed !!");
			}	
		}			
			
	@AfterMethod
	public void tearDown(){
		empLoginPage.endTest();
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
