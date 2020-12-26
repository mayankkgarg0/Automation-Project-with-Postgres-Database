package com.MRIoA.qa.testcases;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
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
import com.MRIoA.qa.pageObjects.LoginPage;
import com.MRIoA.qa.utilities.Constants;
import com.MRIoA.qa.utilities.Utilities;
import com.MRIoA.qa.utilities.Constants.ROLES;
import com.relevantcodes.extentreports.LogStatus;

//This is patient login page test
@Listeners(CustomListener.class)
public class LoginPageTest extends BaseClass {
	LoginPage loginPage;
	static Connection connection;
	private static Logger logger = Logger.getLogger(LoginPageTest.class);

	@BeforeClass
	public void getDBConnection() {
		logger.info("In BeforeClass method of " + LoginPageTest.class.getSimpleName());
		connection = DataBaseConnectionFactory.getConnection();
		logger.info("Connection === " + connection); //this will return connection address
	}

	@BeforeMethod
	public void setUp() throws InterruptedException {
		logger.info("<----Initializing the browser---->");
		initializingDriver();
		loginPage = new LoginPage();
	}

	@Test(priority = 6, description="Running validEmailAddress_validPassword_LoginTest with valid OTP")
	public void patientValidEmailAddress_validPassword_LoginTest_with2FA_ValidOTP() throws InterruptedException, SQLException {
		driver.get(PATIENT_BASE_URL);
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		loginPage.startTest("patientValidEmailAddress_validPassword_LoginTest_with2FA_ValidOTP"); 
		int patientNum = 1;
		logger.info("Running patientValidEmailAddress_validPassword_LoginTest_with2FA_ValidOTP method for patient no. "+patientNum);
		ROLES patientRole = Constants.ROLES.PATIENT;
		String patient = patientRole.name().toLowerCase();
		String userEmail = testProp.getString(patient+"_"+patientNum+"_email_address");
		boolean statusof2FA = Utilities.get2FAStatus(connection, userEmail);
		if(statusof2FA){
			loginPage.doLogin(patientRole,patientNum, statusof2FA);
			logger.info("Query executing for OTP generation");
			WebDriverWait wait = submitOTP(connection,userEmail,null,driver);
			if(null==wait){
				String errorMessage = "OTP not available for email address: "+userEmail;
				extentTest.log(LogStatus.FAIL, errorMessage);
				logger.info("patientValidEmailAddress_validPassword_LoginTest_with2FA_ValidOTP method failed as OTP is not available for email address: "+userEmail);
				Assert.fail(errorMessage);
				return;
			}	
		}
		else{
			loginPage.doLogin(patientRole,patientNum, statusof2FA);
		}
		
		SoftAssert softAssert = new SoftAssert();
		logger.info("Validate "+patient+" login successfully and navigate to dashboard or not?");
		WebDriverWait wait1 = new WebDriverWait(driver,2);
		wait1.until(ExpectedConditions.urlContains("/user/service/dashboard"));
		logger.info("Current URL is: "+getCurrentUrl(driver));
		String dashboardUrl = getCurrentUrl(driver);
		String expectedUrl = PATIENT_BASE_URL + "/user/service/dashboard";
		softAssert.assertEquals(dashboardUrl, expectedUrl,"Patient failed to login and not navigated to dashboard !!");
		softAssert.assertAll();
		if (expectedUrl.equals(dashboardUrl)) {
			extentTest.log(LogStatus.PASS, "patientValidEmailAddress_validPassword_LoginTest_with2FA_ValidOTP method is passed !!");
			logger.info(patient+" no. "+patientNum+" navigated to dashboard after successful login !!");
		}
		else{
			extentTest.log(LogStatus.FAIL, "patientValidEmailAddress_validPassword_LoginTest_with2FA_ValidOTP method is failed !!");
			logger.info(patient+" no."+patientNum+" login failed and not navigated to dashboard !!");
		}
	}
	
	@Test(priority = 7, description="Running validEmailAddress_validPassword_LoginTest with invalid OTP")
	public void patientValidEmailAddress_validPassword_LoginTest_with2FA_invalidOTP() throws InterruptedException, SQLException {
		driver.get(PATIENT_BASE_URL);
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		loginPage.startTest("patientValidEmailAddress_validPassword_LoginTest_with2FA_invalidOTP"); 
		int patientNum = 1;
		logger.info("Running patientValidEmailAddress_validPassword_LoginTest_with2FA_invalidOTP method for patient no. "+patientNum);
		ROLES patientRole = Constants.ROLES.PATIENT;
		//String patient = patientRole.name().toLowerCase();
		loginPage.doLogin(patientRole,patientNum, true);
		WebDriverWait wait = submitOTP(connection,null,Integer.valueOf(testProp.getString("invalid_OTP")), driver);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("*//p/span[text()='Incorrect OTP']")));
		String actualMessage = driver.findElement(By.xpath("*//p/span[text()='Incorrect OTP']")).getText();
		String expectedMessage = "Incorrect OTP";
		SoftAssert softAssert = new SoftAssert();
		logger.info("Validate Incorrect OTP error message received or not !!");
		softAssert.assertEquals(actualMessage, expectedMessage, "Correct error message not received !!");	
		softAssert.assertAll();
		if (expectedMessage.equals(actualMessage)) {
			extentTest.log(LogStatus.PASS, "patientValidEmailAddress_validPassword_LoginTest_with2FA_invalidOTP method is passed !!");
			logger.info("Correct error message displayed on OTP screen !!");
		}
		else{
			extentTest.log(LogStatus.FAIL, "patientValidEmailAddress_validPassword_LoginTest_with2FA_invalidOTP method is failed !!");
			logger.info("Incorrect error message displayed on OTP screen !!");
		}	
	}
	
	@Test(priority = 8, description="Running validEmailAddress_validPassword_LoginTest without OTP")
	public void patientValidEmailAddress_validPassword_LoginTest_without2FA() throws InterruptedException, SQLException {
		driver.get(PATIENT_BASE_URL);
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		loginPage.startTest("patientValidEmailAddress_validPassword_LoginTest_without2FA"); 
		//Patient number 2 has registered without OTP
		int patientNum = 2;
		logger.info("Running patientValidEmailAddress_validPassword_LoginTest_without2FA method for patient no. "+patientNum);
		ROLES patientRole = Constants.ROLES.PATIENT;
		String patient = patientRole.name().toLowerCase();
		String userEmail = testProp.getString(patient+"_"+patientNum+"_email_address");
		boolean statusof2FA = Utilities.get2FAStatus(connection, userEmail);
		if(statusof2FA){
			loginPage.doLogin(patientRole,patientNum, statusof2FA);
			logger.info("Query executing for OTP generation");
			WebDriverWait wait = submitOTP(connection,userEmail,null,driver);
			if(null==wait){
				String errorMessage = "OTP not available for email address: "+userEmail;
				extentTest.log(LogStatus.FAIL, errorMessage);
				logger.info("patientValidEmailAddress_validPassword_LoginTest_with2FA_ValidOTP method failed as OTP is not available for email address: "+userEmail);
				Assert.fail(errorMessage);
				return;
			}	
		}
		else{
			loginPage.doLogin(patientRole,patientNum, statusof2FA);
		}
		logger.info("Validating User login successfully and navigate to dashboard or not?");
		logger.info("Current URL is: "+getCurrentUrl(driver));
		String dashboardUrl = getCurrentUrl(driver);
		String expectedUrl = PATIENT_BASE_URL + "/user/service/dashboard";
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(dashboardUrl, expectedUrl,"Patient login failed and not navigated to dashboard!!");
		softAssert.assertAll();
		if (expectedUrl.equals(dashboardUrl)) {
			extentTest.log(LogStatus.PASS, "patientValidEmailAddress_validPassword_LoginTest_without2FA method is passed !!");
			logger.info(patientRole+" no. "+patientNum+" login successfully and navigated to dashboard  !!");
		}
		else{
			extentTest.log(LogStatus.FAIL, "patientValidEmailAddress_validPassword_LoginTest_without2FA method is failed !!");
			logger.info(patientRole+" no."+patientNum+" login failed and not navigated to dashboard !!");
		}
	}

	@Test(priority = 9, description="Running invalidEmailAddress_validPassword_LoginTest")
	public void patientInvalidEmailAddress_validPassword_LoginTest() throws InterruptedException {
		driver.get(PATIENT_BASE_URL);
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		loginPage.startTest("patientInvalidEmailAddress_validPassword_LoginTest"); 
		int patientNum = 5;
		logger.info("Running patientInvalidEmailAddress_validPassword_LoginTest method for patient no. "+patientNum);
		ROLES patientRole = Constants.ROLES.PATIENT;
		loginPage.doLogin(patientRole,patientNum,false);
		Thread.sleep(2000);
		logger.info("Validating error message received or not !!");
		String actualMessage = driver.findElement(By.xpath("//span[contains(text(),'Please enter valid Email Address')]")).getText();
		String expectedMessage = "Email doesn't exist. Please enter valid Email Address";
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(actualMessage, expectedMessage,"Incorrect error message displayed on login screen !!");
		softAssert.assertAll();
		if (expectedMessage.equals(actualMessage)) {
			extentTest.log(LogStatus.PASS, "patientInvalidEmailAddress_validPassword_LoginTest method is passed !!");
			logger.info(patientRole+" no. "+patientNum+" not navigated to dashboard and correct error message displayed on login screen !!");
		}
		else{
			extentTest.log(LogStatus.FAIL, "patientInvalidEmailAddress_validPassword_LoginTest method is failed !!");
			logger.info("Required error message not displayed on login screen !!");
		}
	}

	@Test(priority = 10, description="Running invalidEmailAddress_invalidPassword_LoginTest")
	public void patientInvalidEmailAddress_invalidPassword_LoginTest() throws InterruptedException {
		driver.get(PATIENT_BASE_URL);
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		loginPage.startTest("patientInvalidEmailAddress_invalidPassword_LoginTest"); 
		int patientNum = 5;
		logger.info("Running patientInvalidEmailAddress_invalidPassword_LoginTest method for patient no. "+patientNum);
		ROLES patientRole = Constants.ROLES.PATIENT;
		loginPage.doLogin(patientRole,patientNum,false);
		Thread.sleep(2000);
		logger.info("Validating error message received or not !!");
		String actualMessage = driver.findElement(By.xpath("//span[contains(text(),'Please enter valid Email Address')]")).getText();
		String expectedMessage = "Email doesn't exist. Please enter valid Email Address";
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(actualMessage, expectedMessage,"Incorrect error message displayed");
		softAssert.assertAll();
		if (expectedMessage.equals(actualMessage)) {
			extentTest.log(LogStatus.PASS, "patientInvalidEmailAddress_invalidPassword_LoginTest method is passed !!");
			logger.info(patientRole+" no. "+patientNum+" not navigated to dashboard and correct error message displayed on login screen !!");
		}
		else{
			extentTest.log(LogStatus.FAIL, "patientInvalidEmailAddress_invalidPassword_LoginTest method is failed !!");
			logger.info("Required error message not displayed on login screen !!");
		}
	}
	
	@Test(priority = 11, description="Validate logout test")
	public void patientLogoutTest() throws InterruptedException, SQLException {
		driver.get(PATIENT_BASE_URL);
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		loginPage.startTest("patientLogoutTest"); 
		int patientNum = 2;
		ROLES patientRole = Constants.ROLES.PATIENT;
		String patient = Constants.ROLES.PATIENT.name().toLowerCase();
		String userEmail = testProp.getString(patient+"_"+patientNum+"_email_address");
		boolean statusof2FA = Utilities.get2FAStatus(connection, userEmail);
		if(statusof2FA){
			loginPage.doLogin(patientRole,patientNum, statusof2FA);
			logger.info("Query executing for OTP generation");
			WebDriverWait wait = submitOTP(connection,userEmail,null,driver);
			if(null==wait){
				String errorMessage = "OTP not available for email address: "+userEmail;
				extentTest.log(LogStatus.FAIL, errorMessage);
				logger.info("patientValidEmailAddress_validPassword_LoginTest_with2FA_ValidOTP method failed as OTP is not available for email address: "+userEmail);
				Assert.fail(errorMessage);
				return;
			}	
		}
		else{
			logger.info("Running patientLogoutTest method for"+patient+" no. "+patientNum);
			loginPage.doLogin(patientRole,patientNum, statusof2FA);
		}
		logger.info("Validate "+patient+" login successfully and navigate to dashboard or not?");
		logger.info("Current URL is: "+getCurrentUrl(driver));
		String actualUrl = getCurrentUrl(driver);
		String expectedUrl = PATIENT_BASE_URL+"/user/service/dashboard";
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(actualUrl, expectedUrl,"Patient failed to login and not navigated to dashboard !!");
		loginPage.logout();
		logger.info("Validate "+patient+" log out successfully or not?");
		WebDriverWait wait = new WebDriverWait(driver,3);
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT,TimeUnit.SECONDS);
		wait.until(ExpectedConditions.urlContains("/user/login"));
		logger.info("Url after logout is: "+getCurrentUrl(driver));
		String urlAfterLogout = getCurrentUrl(driver);
		String expectedUrlAfterLogout = PATIENT_BASE_URL + Constants.LOGIN_PAGE_SUFFIX;
		softAssert.assertEquals(urlAfterLogout, expectedUrlAfterLogout,"Patient logout failed !!");
		softAssert.assertAll();
		if (expectedUrlAfterLogout.equals(urlAfterLogout)) {
			extentTest.log(LogStatus.PASS, "patientLogoutTest method is passed !!");
			logger.info(patient+" no."+patientNum+" logout successfully !!");
		}
		else{
			extentTest.log(LogStatus.FAIL, "patientLogoutTest method is failed !!");
			logger.info(patient+" no."+patientNum+" logout failed !!");
		}
	}

	@Test(priority = 12, description = "Validate user navigate to dashboard if registered with Google and 2FA not opted")
	public void patientGoogleSignInTest_without2FA() throws InterruptedException, SQLException {
		driver.get(PATIENT_BASE_URL);
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		loginPage.startTest("patientGoogleSignInTest_without2FA"); 
		int patientNum = 4;
		 ROLES patientRole = Constants.ROLES.PATIENT;
		 String patient = Constants.ROLES.PATIENT.name().toLowerCase();
			String userEmail = testProp.getString(patient+"_"+patientNum+"_email_address");
			boolean statusof2FA = Utilities.get2FAStatus(connection, userEmail);
			if(statusof2FA){
				logger.info("Running patientGoogleSignInTest method for "+patientRole+" no. "+patientNum+" with 2FA opted");
				loginPage.googleSignIn(patientRole,patientNum, statusof2FA);
				logger.info("Query executing for OTP generation");
				WebDriverWait wait = submitOTP(connection,userEmail,null,driver);
				if(null==wait){
					String errorMessage = "OTP not available for email address: "+userEmail;
					extentTest.log(LogStatus.FAIL, errorMessage);
					logger.info("patientValidEmailAddress_validPassword_LoginTest_with2FA_ValidOTP method failed as OTP is not available for email address: "+userEmail);
					Assert.fail(errorMessage);
					return;
				}	
			}
			else{
				logger.info("Running patientGoogleSignInTest_without2FA method for "+patientRole+" no. "+patientNum+" with 2FA not opted");
				loginPage.googleSignIn(patientRole,patientNum, statusof2FA);
			}
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		logger.info("Verify "+patientRole+" navigated to dashboard or not?");
		logger.info("Current URL is: "+getCurrentUrl(driver));
		String dashboardUrl = driver.getCurrentUrl();
		String expectedUrl = PATIENT_BASE_URL+"/user/service/dashboard";
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(dashboardUrl, expectedUrl,"Patient failed to login by Google and not navigated to dashboard");
		softAssert.assertAll();
		if (expectedUrl.equals(dashboardUrl)) {
			extentTest.log(LogStatus.PASS, "patientGoogleSignInTest_without2FA method is passed !!");
			logger.info(patientRole+" no. "+patientNum+" Google Sign In successful and navigated to dashboard !!");
		}
		else{
			extentTest.log(LogStatus.FAIL, "patientGoogleSignInTest_without2FA method is failed !!");
			logger.info(patientRole+" no. "+patientNum+" Google Sign In failed and navigated to dashboard !!");
		}
		
	}
	
	/*@Test(priority = 8, description = "Validate user navigate to dashboard if registered with Google and 2FA opted")
	public void patientGoogleSignInTest_with2FA() throws InterruptedException, SQLException {
		driver.get(PATIENT_BASE_URL);
		loginPage.startTest("patientGoogleSignInTest_with2FA"); 
		int patientNum = 4;
		 ROLES patientRole = Constants.ROLES.PATIENT;
		 String patient = Constants.ROLES.PATIENT.name().toLowerCase();
			String userEmail = testProp.getString(patient+"_"+patientNum+"_email_address");
			boolean statusof2FA = util.get2FAStatus(connection, userEmail);
			if(statusof2FA){
				logger.info("Running patientGoogleSignInTest method for "+patientRole+" no. "+patientNum+" with 2FA opted");
				loginPage.googleSignIn(patientRole,patientNum, statusof2FA);
				logger.info("Query executing for OTP generation");
				WebDriverWait wait = submitOTP(connection,userEmail,null,driver);
				if(null==wait){
					String errorMessage = "OTP not available for email address: "+userEmail;
					extentTest.log(LogStatus.FAIL, errorMessage);
					logger.info("patientValidEmailAddress_validPassword_LoginTest_with2FA_ValidOTP method failed as OTP is not available for email address: "+userEmail);
					Assert.fail(errorMessage);
					return;
				}	
			}
			else{
				logger.info("Running patientGoogleSignInTest_without2FA method for "+patientRole+" no. "+patientNum+" with 2FA not opted");
				loginPage.googleSignIn(patientRole,patientNum, statusof2FA);
			}
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		logger.info("Verify "+patientRole+" navigated to dashboard or not?");
		logger.info("Current URL is: "+getCurrentUrl(driver));
		String dashboardUrl = driver.getCurrentUrl();
		String expectedUrl = PATIENT_BASE_URL+"/user/service/dashboard";
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(dashboardUrl, expectedUrl,"Patient failed to login by Google and not navigated to dashboard");
		softAssert.assertAll();
		if (expectedUrl.equals(dashboardUrl)) {
			extentTest.log(LogStatus.PASS, "patientGoogleSignInTest_without2FA method is passed !!");
			logger.info(patientRole+" no. "+patientNum+" Google Sign In successful and navigated to dashboard !!");
		}
		else{
			extentTest.log(LogStatus.FAIL, "patientGoogleSignInTest_without2FA method is failed !!");
			logger.info(patientRole+" no. "+patientNum+" Google Sign In failed and navigated to dashboard !!");
		}
		
	}*/
	
	/*@Test(priority = 9, description = "Validate user navigate to sign up page if not registered with google")
	public void googleSignIn_withoutSignUp__without2FA_Test() throws InterruptedException {
		int patientNum = 7;
		Roles patientRole = Constants.ROLES.PATIENT; //.name().toLowerCase();
		boolean is2FAOpted = false;
		logger.info("Running googleSignIn_withoutSignUp__withou2FA_Test method for patient no. "+patientNum);
		driver.get(PATIENT_BASE_URL);
		loginPage.googleSignIn(patientRole,patientNum, is2FAOpted);
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		logger.info("Verify "=role+" navigated to dashboard or not?");
		WebDriverWait wait = new WebDriverWait(driver, 30);
		wait.until(ExpectedConditions.urlContains("/user/service/dashboard"));
		String dashboardUrl = loginPage.getCurrentPageUrl();
		Assert.assertEquals(dashboardUrl, PATIENT_BASE_URL + "/user/service/dashboard","Patient failed to login and not navigated to dashboard");
	}*/	
	
	@AfterMethod
	public void tearDown() {
		loginPage.endTest();
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