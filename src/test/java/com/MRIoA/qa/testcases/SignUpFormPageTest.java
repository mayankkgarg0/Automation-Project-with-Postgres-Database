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
import com.MRIoA.qa.pageObjects.SignUpFormPage;
import com.MRIoA.qa.utilities.Constants;
import com.relevantcodes.extentreports.LogStatus;

//This listener is used to take screenshot of failed test method
@Listeners(CustomListener.class)
public class SignUpFormPageTest extends BaseClass {
	public static Connection connection;
	SignUpFormPage signUp;
	private static Logger logger = Logger.getLogger(SignUpFormPageTest.class);

	@BeforeClass
	public void getDBConnection() {
		logger.info("In BeforeClass method of " + SignUpFormPageTest.class.getSimpleName());
		connection = DataBaseConnectionFactory.getConnection();
		logger.info("Connection === " + connection);// this will return
													// connection address
	}

	@BeforeMethod
	public void setUp() throws InterruptedException {
		logger.info("<----Initializing the browser---->");
		initializingDriver();
		signUp = new SignUpFormPage();
	}

	@Test(priority = 1, description = "Validate Sign up functionality with form for first time and 2FA opted")
	public void patientFormSignUpSuccessTest_with2FA() throws InterruptedException, SQLException {
		int patientNum = 1;
		String role = Constants.ROLES.PATIENT.name().toLowerCase();
		driver.get(PATIENT_BASE_URL);
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		signUp.startTest("patientFormSignUpSuccessTest_with2FA");
		logger.info(
				"Running patientFormSignUpSuccessTest_with2FA method:---- Validate Sign up functionality for Patient No. "
						+ patientNum + " with form for first time and 2FA opted");
		signUp.clickSignUpLink();
		logger.info("Sign Up link is clicked on login page");
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		logger.info(role + " is registering by providing details in sign up form!!");
		signUp.doFormSignUp(role, patientNum, false, true);
		logger.info("Query executing for OTP generation");
		String userEmail = testProp.getString(role + "_" + patientNum + "_email_address");
		WebDriverWait wait = submitOTP(connection, userEmail, null, driver);
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		SoftAssert softAssert = new SoftAssert();
		if (null == wait) {
			extentTest.log(LogStatus.FAIL, "OTP not available for email address: " + userEmail);
			logger.info("patientFormSignUpSuccessTest_with2FA method failed as OTP is not available for email address: "
					+ userEmail);
			return;
		}
		logger.info("Validate " + role + " registered successfully and navigate to dashboard or not?");
		wait.until(ExpectedConditions.urlContains("/user/service/dashboard"));
		logger.debug("Current url is: " + getCurrentUrl(driver));
		String dashboardUrl = getCurrentUrl(driver);
		String expectedUrl = PATIENT_BASE_URL + "/user/service/dashboard";
		softAssert.assertEquals(dashboardUrl, expectedUrl, "Patient not registered successfully!!");
		softAssert.assertAll();
		if (expectedUrl.equals(dashboardUrl)) {
			extentTest.log(LogStatus.PASS, "patientFormSignUpSuccessTest_with2FA method is passed !!");
			logger.info(role + " no. " + patientNum + " registered and navigated to dashboard successfully !!");
		} else {
			extentTest.log(LogStatus.FAIL, "patientFormSignUpSuccessTest_with2FA method is failed !!");
			logger.info(role + " no. " + patientNum + " unsuccessful registration. Please try again !!");
		}
	}

	// dependsOnMethods has first preference than 'priority'
	@Test(priority = 2,description = "Validate sign up functionality with form for second time with same email address and 2FA opted")
	public void patientFormSignUpFailureTest_with2FA() throws InterruptedException {
		driver.get(PATIENT_BASE_URL);
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		signUp.startTest("patientFormSignUpFailureTest_with2FA");
		int patientNum = 1;
		String role = Constants.ROLES.PATIENT.name().toLowerCase();
		logger.info(
				"Running patientFormSignUpFailureTest_with2FA method:---- Validate Sign up functionality for Patient No. "
						+ patientNum + " with form for second time with same email address and 2FA opted");
		signUp.clickSignUpLink();
		logger.info("Sign Up link is clicked");
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		logger.info(role + " is registering by providing details in sign up form!!");
		signUp.doFormSignUp(role, patientNum, false, true);
		// since error message will be displayed, try block will return back
		// here.
		WebDriverWait wait = new WebDriverWait(driver, 3);
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//span[contains(text(),'Email address already exists')]")));
		String receivedMessage = driver.findElement(By.xpath("//span[contains(text(),'Email address already exists')]"))
				.getText();
		String expectedMessage = "Email address already exists. Please Sign In.";
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(receivedMessage, expectedMessage,
				"Incorrect error message displayed on Sign Up screen !!");
		softAssert.assertAll();
		if (expectedMessage.equals(receivedMessage)) {
			extentTest.log(LogStatus.PASS, "patientFormSignUpFailureTest_with2FA method is passed !!");
			logger.info(role + " no. " + patientNum + " is already registered !! Please login to continue !!");
		} else {
			extentTest.log(LogStatus.FAIL, "patientFormSignUpFailureTest_with2FA method is failed !!");
			logger.info("Required error message doesn't displaye on Sign Up screen !!");
		}
	}

	@Test(priority = 3, description = "Validate Sign up functionality with form for first time and 2FA not opted")
	public void patientFormSignUpSuccessTest_without2FA() throws InterruptedException, SQLException {
		driver.get(PATIENT_BASE_URL);
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		signUp.startTest("patientFormSignUpSuccessTest_without2FA");
		int patientNum = 2;
		String role = Constants.ROLES.PATIENT.name().toLowerCase();
		logger.info(
				"Running patientFormSignUpSuccessTest_without2FA method:---- Validate Sign up functionality for Patient No. "
						+ patientNum + " with form for first time and 2FA not opted");
		signUp.clickSignUpLink();
		logger.info("Sign Up link is clicked");
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		logger.info(role + " is registering by providing details in sign up form!!");
		signUp.doFormSignUp(role, patientNum, false, false);
		WebDriverWait wait = new WebDriverWait(driver, 2);
		logger.info("Validate " + role + " registered successfully and navigate to dashboard or not?");
		wait.until(ExpectedConditions.urlContains("/user/service/dashboard"));
		logger.info("Current url is: " + getCurrentUrl(driver));
		String dashboardUrl = getCurrentUrl(driver);
		String expectedUrl = PATIENT_BASE_URL + "/user/service/dashboard";
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(dashboardUrl, expectedUrl, "Patient not registered successfully!!");
		softAssert.assertAll();
		if (expectedUrl.equals(dashboardUrl)) {
			extentTest.log(LogStatus.PASS, "patientFormSignUpSuccessTest_without2FA method is passed !!");
			logger.info(role + " no. " + patientNum + " registered and navigated to dashboard successfully !!");
		} else {
			extentTest.log(LogStatus.FAIL, "patientFormSignUpSuccessTest_without2FA method is failed !!");
			logger.info(role + " no. " + patientNum + " unsuccessful registration.Please try again !!");
		}
	}

	@Test(priority = 4, description = "Validate sign up functionality with form for second time with same email address and 2FA not opted")
	public void patientFormSignUpFailureTest_without2FA() throws InterruptedException {
		driver.get(PATIENT_BASE_URL);
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		signUp.startTest("patientFormSignUpFailureTest_without2FA");
		int patientNum = 2;
		String role = Constants.ROLES.PATIENT.name().toLowerCase();
		logger.info(
				"Running patientFormSignUpFailureTest_without2FA method:---- Validate Sign up functionality for Patient No. "
						+ patientNum + " with form for second time with same email address and 2FA not opted");
		signUp.clickSignUpLink();
		logger.info("Sign Up link is clicked");
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		logger.info(role + " is registering by providing details in sign up form!!");
		signUp.doFormSignUp(role, patientNum, false, false);
		// since error message will be displayed, try block will return back
		// here.
		WebDriverWait wait = new WebDriverWait(driver, 3);
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//span[contains(text(),'Email address already exists')]")));
		String actualMessage = driver.findElement(By.xpath("//span[contains(text(),'Email address already exists')]"))
				.getText();
		String expectedMessage = "Email address already exists. Please Sign In.";
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(actualMessage, expectedMessage,
				"Incorrect error message displayed on Sign Up screen !!");
		softAssert.assertAll();
		if (expectedMessage.equals(actualMessage)) {
			extentTest.log(LogStatus.PASS, "patientFormSignUpFailureTest_without2FA method is passed !!");
			logger.info(role + " no. " + patientNum + " is already registered. Please login to continue !!");
		} else {
			extentTest.log(LogStatus.FAIL, "patientFormSignUpFailureTest_without2FA method is failed !!");
			logger.info("Required error message doesn't displayed on Sign Up screen !!");
		}
	}

	// Expectation is: Google email address exists.
	@Test(priority = 5, description = "Validate Sign up functionality with Google for first time and 2FA not opted")
	public void patientGoogleSignUpSuccessTest_without2FA() throws InterruptedException {
		driver.get(PATIENT_BASE_URL);
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		signUp.startTest("patientGoogleSignUpSuccessTest_without2FA");
		int patientNum = 4;
		String role = Constants.ROLES.PATIENT.name().toLowerCase();
		boolean is2FAOpted = false;
		logger.info(
				"Running googleSignUpSuccessTest_without2FA method:---- Validate Sign up functionality with Google for Patient No. "
						+ patientNum + " for first time and 2FA not opted");
		signUp.clickSignUpLink();
		logger.info("Sign Up link is clicked");
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		logger.info("Google Sign up button is clicked !!");
		signUp.doGoogleSignUp(role, patientNum, is2FAOpted);
		WebDriverWait wait = new WebDriverWait(driver, 5);
		logger.info("Validate " + role + " registered successfully and navigate to dashboard or not?");
		wait.until(ExpectedConditions.urlContains("/user/service/dashboard"));
		logger.info("Current URL is: " + getCurrentUrl(driver));
		SoftAssert softAssert = new SoftAssert();
		String dashboardUrl = getCurrentUrl(driver);
		String expectedUrl = PATIENT_BASE_URL + "/user/service/dashboard";
		softAssert.assertEquals(dashboardUrl, expectedUrl, "Patient not registered successfully!!");
		softAssert.assertAll();
		if (expectedUrl.equals(dashboardUrl)) {
			extentTest.log(LogStatus.PASS, "patientGoogleSignUpSuccessTest_without2FA method is passed !!");
			logger.info(role + " no. " + patientNum + " registered and navigated to dashboard successfully !!");
		} else {
			extentTest.log(LogStatus.FAIL, "patientGoogleSignUpSuccessTest_without2FA method is failed !!");
			logger.info(role + " no. " + patientNum + " unsuccessful registration.Please try again !!");
		}
	}
	
	/*
	@Test(priority = 6, dependsOnMethods = {
			"patientGoogleSignUpFailureTest_without2FA" }, description = "Validate sign up functionality with Google for second time with same email address and 2FA not opted")
	public void patientGoogleSignUpFailureTest_without2FA() throws InterruptedException {
		driver.get(PATIENT_BASE_URL);
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		signUp.startTest("patientGoogleSignUpFailureTest_without2FA");
		int patientNum = 4;
		String role = Constants.ROLES.PATIENT.name().toLowerCase();
		boolean is2FAOpted = false;
		logger.info(
				"Running googleSignUpFailureTest_without2FA method:---- Validate Sign up functionality with Google for Patient No. "
						+ patientNum + " for second time with same email address and 2FA not opted");
		signUp.clickSignUpLink();
		logger.info("Sign Up link is clicked");
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		logger.info("Google Sign up button is clicked !!");
		signUp.doGoogleSignUp(role, patientNum, is2FAOpted);
		WebDriverWait wait = new WebDriverWait(driver, 3);
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//span[contains(text(),'Email address already exists')]")));
		logger.info("Validate error message received or not ?");
		SoftAssert softAssert = new SoftAssert();
		String actualMessage = driver.findElement(By.xpath("//span[contains(text(),'Email address already exists')]"))
				.getText();
		String expectedMessage = "Email address already exists. Please Sign In.";
		softAssert.assertEquals(actualMessage, expectedMessage, "Incorrect error message displayed on Sign Up page");
		softAssert.assertAll();
		if (expectedMessage.equals(actualMessage)) {
			extentTest.log(LogStatus.PASS, "patientGoogleSignUpFailureTest_without2FA method is passed !!");
			logger.info(role + " no. " + patientNum + " is already registered. Please Sign In to continue !!");
		} else {
			extentTest.log(LogStatus.FAIL, "patientGoogleSignUpFailureTest_without2FA method is failed !!");
			logger.info("Required error message doesn't displayed on Sign Up screen");
		}
	}
*/

	/*
	 * @Test(priority=7, description
	 * ="Validate Sign up functionality with Google for first time and 2FA opted"
	 * ) public void googleSignUpSuccessTest_with2FA() throws
	 * InterruptedException, SQLException{ int patientNum = 6; String role =
	 * Constants.ROLES.PATIENT.name().toLowerCase(); boolean is2FAOpted = true;
	 * logger.
	 * info("Running googleSignUpSuccessTest_with2FA method:---- Validate Sign up functionality with Google for Patient No. "
	 * +patientNum+" for first time and 2FA opted");
	 * driver.get(PATIENT_BASE_URL); signUp.clickSignUpLink();
	 * logger.info("Sign Up link is clicked");
	 * driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT,
	 * TimeUnit.SECONDS); logger.info("Google Sign up button is clicked !!");
	 * signUp.doGoogleSignUp(role,patientNum, is2FAOpted);
	 * logger.info("Query executing for OTP generation"); String userEmail =
	 * testProp.getString(role+"_"+patientNum+"_email_address"); WebDriverWait
	 * wait = submitOTP(connection,userEmail,null);
	 * driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT,
	 * TimeUnit.SECONDS); if(null==wait){
	 * Assert.fail("No action performed on OTP screen !!"); return; }
	 * logger.info("Validate "
	 * role+" registered successfully and navigate to dashboard or not?");
	 * wait.until(ExpectedConditions.urlContains("/user/service/dashboard"));
	 * logger.info(getCurrentUrl()); String dashboardUrl = getCurrentUrl();
	 * Assert.assertEquals(dashboardUrl, PATIENT_BASE_URL +
	 * "/user/service/dashboard","Patient not registered successfully!!");
	 * logger.info(role+" no. "
	 * +patientNum+" navigated to dashboard successfully !!"); }
	 
*/

	@AfterMethod
	public void tearDown() {
		signUp.endTest();
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
