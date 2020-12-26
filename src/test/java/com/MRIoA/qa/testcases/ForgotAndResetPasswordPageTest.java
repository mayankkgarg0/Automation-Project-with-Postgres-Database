package com.MRIoA.qa.testcases;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration.ConfigurationException;
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
import com.MRIoA.qa.pageObjects.ForgotAndResetPasswordPage;
import com.MRIoA.qa.pageObjects.LoginPage;
import com.MRIoA.qa.utilities.Constants;
import com.MRIoA.qa.utilities.Utilities;
import com.MRIoA.qa.utilities.Constants.ROLES;
import com.relevantcodes.extentreports.LogStatus;

@Listeners(CustomListener.class)
public class ForgotAndResetPasswordPageTest extends BaseClass{
	private static Logger logger = Logger.getLogger(ForgotAndResetPasswordPageTest.class);
	ForgotAndResetPasswordPage forgotResetPwd;
	static Connection connection;
	LoginPage loginPage;
	
	@BeforeClass
	public void getDBConnection() {
		logger.info("In BeforeClass method of " + ForgotAndResetPasswordPageTest.class.getSimpleName());
		connection = DataBaseConnectionFactory.getConnection();
		logger.info("Connection === " + connection); //this will return connection address
	}

	@BeforeMethod
	public void setUp() throws InterruptedException {
		logger.info("<----Initializing the browser---->");
		initializingDriver();
		forgotResetPwd = new ForgotAndResetPasswordPage();
		loginPage = new LoginPage();
	}
	
	@Test(priority=17, description="invalidEmailAddressEnteredTest")
	public void doResetPwdWith_InvalidEmailAddressTest() throws InterruptedException{
		driver.get(PATIENT_BASE_URL);
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		forgotResetPwd.startTest("invalidEmailAddressEnteredTest"); 
		logger.info("Running invalidEmailAddressEnteredTest");
		int patientNum = 5;
		ROLES patientRole = Constants.ROLES.PATIENT;
		forgotResetPwd.doForgotPassword_ValidEmailAddress(patientRole,patientNum);
		Thread.sleep(2000);
		WebDriverWait wait = new WebDriverWait(driver, 20);	
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("*//p/span[contains(text(),'Please sign up')]")));
		String actualMessage = driver.findElement(By.xpath("//p/span[contains(text(),'Please sign up')]")).getText();
		String expectedMessage = "Email Address doesn't exist. Please sign up";
		SoftAssert softAssert = new SoftAssert();
		logger.info("Validate invalid email address error message received or not !!");
		softAssert.assertEquals(actualMessage, expectedMessage, "Expected error message not received !!");	
		softAssert.assertAll();
		if (expectedMessage.equals(actualMessage)) {
			extentTest.log(LogStatus.PASS, "invalidEmailAddressEnteredTest method is passed !!");
			logger.info("Expected error message displayed on Forgot Password screen !!");
		}
		else{
			extentTest.log(LogStatus.FAIL, "invalidEmailAddressEnteredTest method is failed !!");
			logger.info("Incorrect error message displayed on Forgot Password screen !!");
		}	
	}
	
	
	@Test(priority=18, description="validEmailAddressEnteredTest")
	public void doResetPasswordWith_ValidEmailAddressTest() throws InterruptedException, SQLException{
		driver.get(PATIENT_BASE_URL);
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		forgotResetPwd.startTest("validEmailAddressEnteredTest"); 
		logger.info("Running validEmailAddressEnteredTest");
		int patientNum = 2;
		ROLES patientRole = Constants.ROLES.PATIENT;
		String role = patientRole.name().toLowerCase();
		forgotResetPwd.doForgotPassword_ValidEmailAddress(patientRole,patientNum);
		Thread.sleep(2000);
		WebDriverWait wait = new WebDriverWait(driver, 20);	
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("*//p/span[contains(text(),'Reset password link sent to your Email Address.')]")));
		String actualMessage = driver.findElement(By.xpath("*//p/span[contains(text(),'Reset password link sent to your Email Address.')]")).getText();
		String expectedMessage = driver.findElement(By.xpath("*//p/span[contains(text(),'Reset password link sent to your Email Address.')]")).getText();
		SoftAssert softAssert = new SoftAssert();
		logger.info("Validate success message received or not !!");
		softAssert.assertEquals(actualMessage, expectedMessage, "Success message not received !!");	
		softAssert.assertAll();
		if (expectedMessage.equals(actualMessage)) {
			extentTest.log(LogStatus.PASS, "validEmailAddressEnteredTest method is passed !!");
			logger.info("Success message displayed on Forgot Password screen !!");
		}
		else{
			extentTest.log(LogStatus.FAIL, "validEmailAddressEnteredTest method is failed !!");
			logger.info("Wrong message displayed on Forgot Password screen !!");
		}	
		
		/*----------Navigating to mailinator----------*/
		driver.navigate().to("https://www.mailinator.com");
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@placeholder='Enter Public Mailinator Inbox']")));
		driver.findElement(By.xpath("//input[@placeholder='Enter Public Mailinator Inbox']")).sendKeys(testProp.getString(role + "_" + patientNum + "_email_address"));
		driver.findElement(By.xpath("*//button[@id='go-to-public']")).click();
		Thread.sleep(2000);
		
		/*---------------Click on required email subject and get reset password link------------*/
		
		forgotResetPwd.openResetPwdMail();
		logger.info("Navigated to Reset Password screen");
		forgotResetPwd.resetPassword();
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//span[contains(text(),'Password successfully updated.')]")));
		driver.navigate().to(PATIENT_BASE_URL+"/user/login");
		String resetPass = testProp.getString("reset_newPassword");
		testProp.setProperty("reset_newPassword",testProp.getString(role+"_"+patientNum+"_password"));
		testProp.setProperty(role+"_"+patientNum+"_password",resetPass);
		try {
			testProp.save();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		wait.until(ExpectedConditions.urlContains("/user/login"));
		String userEmail = testProp.getString(role+"_"+patientNum+"_email_address");
		boolean statusof2FA = Utilities.get2FAStatus(connection, userEmail);
		if(statusof2FA){
			loginPage.doLogin(patientRole,patientNum, statusof2FA);
			logger.info("Query executing for OTP generation");
			WebDriverWait wait1 = submitOTP(connection,userEmail,null,driver);
			if(null==wait1){
				String errorMessage = "OTP not available for email address: "+userEmail;
				extentTest.log(LogStatus.FAIL, errorMessage);
				logger.info("patientValidEmailAddress_validPassword_LoginTest_with2FA_ValidOTP method failed as OTP is not available for email address: "+userEmail);
				Assert.fail(errorMessage);
				return;
			}	
		}
		else{
			loginPage.doLogin(patientRole,patientNum, statusof2FA);
			System.out.println("Login successful after reset password functionality.....!!");
		}	
	}
		
	@AfterMethod
	public void tearDown() {
		forgotResetPwd.endTest();
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
