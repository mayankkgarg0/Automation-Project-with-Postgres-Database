package com.MRIoA.qa.testcases;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
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
import com.MRIoA.qa.pageObjects.PatientEditProfilePage;
import com.MRIoA.qa.utilities.Constants;
import com.MRIoA.qa.utilities.Constants.ROLES;
import com.MRIoA.qa.utilities.Utilities;
import com.relevantcodes.extentreports.LogStatus;

@Listeners(CustomListener.class)
public class PatientEditProfilePageTest extends BaseClass{
	
	PatientEditProfilePage profilePage;
	LoginPage loginPage;
	static Connection connection;
	private static Logger logger = Logger.getLogger(PatientEditProfilePageTest.class);
	
	@BeforeClass
	public void getDBConnection() {
		logger.info("In BeforeClass method of " + PatientEditProfilePageTest.class.getSimpleName());
		connection = DataBaseConnectionFactory.getConnection();
		logger.info("Connection === " + connection); //this will return connection address
	}

	@BeforeMethod
	public void setUp() throws InterruptedException {
		logger.info("<----Initializing the browser---->");
		initializingDriver();
		loginPage = new LoginPage();
		profilePage = new PatientEditProfilePage();
	}
	
	@Test(priority = 19, description="Running doProfileEditTest")
	public void doProfileEditTest() throws SQLException, InterruptedException, ConfigurationException{
		driver.get(PATIENT_BASE_URL);
		profilePage.startTest("doProfileEditTest"); 
		int patientNum = 1;
		ROLES patientRole = Constants.ROLES.PATIENT;
		String patient = patientRole.name().toLowerCase();
		logger.info("Running doProfileEditTest for patient no. "+patientNum);
		String userEmail = testProp.getString(patient+"_"+patientNum+"_email_address");
		boolean statusof2FA = Utilities.get2FAStatus(connection, userEmail);
		String expectedUrl = PATIENT_BASE_URL+"/user/service/dashboard";
		if(statusof2FA){
			loginPage.doLogin(patientRole,patientNum, statusof2FA);
			logger.info("Query executing for OTP generation");
			WebDriverWait wait = submitOTP(connection,userEmail,null,driver);
			if(null==wait){
				String errorMessage = "OTP not available for email address: "+userEmail;
				extentTest.log(LogStatus.FAIL, errorMessage);
				logger.info("OTP not found for email address: "+userEmail);
				Assert.fail(errorMessage);
				return;
			}	
		}
		else{
			loginPage.doLogin(patientRole,patientNum, statusof2FA);
		}
		logger.info("Validate "+patient+" login successfully and navigate to dashboard or not?");
		WebDriverWait wait1 = new WebDriverWait(driver,2);
		wait1.until(ExpectedConditions.urlContains(expectedUrl));
		String actualUrl = driver.getCurrentUrl();
		if(actualUrl.equals(expectedUrl)){
			logger.info(patient+" no."+patientNum+" navigated to dashboard after successful login !!");
		}
		else{
			Assert.fail();
			extentTest.log(LogStatus.FAIL, "doProfileEditTest method faied duw to unsuccessful Patient login !!");
			logger.info(patient+" no."+patientNum+" profile not updated due to patient unsuccessful login !!");
		}
		
		/*-----------Navigate to 'Create Service Request' screen to fetch 'Personal Information' and 'Insurance Information' fields data before performing edit profile functionality-------*/
		driver.navigate().to(PATIENT_BASE_URL+"/user/service/service-request");
		wait1.until(ExpectedConditions.urlContains("/user/service/service-request"));
		Thread.sleep(5000);
		String initialName = profilePage.nameCSR.getAttribute("value");
		String initialDOB = profilePage.dobCSR.getAttribute("value");
		String initialPhoneNumber = profilePage.phoneNumberCSR.getAttribute("value");
		String initialMemberID = profilePage.memberIDCSR.getAttribute("value");
		String initialInsurancePlanName = profilePage.insurancePlanNameCSR.getAttribute("value");
		String initialInsuranceGroupNumber = profilePage.insuranceGroupNumberCSR.getAttribute("value");
		
		/*-----------Display 'Personal Information' and 'Insurance Information' 
		 * on console before edit profile functionality-----------------*/
		
		logger.info("Initial Name displayed is: "+initialName);
		logger.info("Initial DOB displayed is: "+initialDOB);
		logger.info("Initial Phone Number displayed is: "+initialPhoneNumber);
		logger.info("Initial Member ID displayed is: "+initialMemberID);
		logger.info("Initial Insurance Name displayed is: "+initialInsurancePlanName);
		logger.info("Initial Insurance Group Number displayed is: "+initialInsuranceGroupNumber);
		
		/*-----------Navigate to 'Edit Profile Page----------------------------*/
		profilePage.goToEditProfilePage();
        
       /* -----------Again navigate to 'Create Service Request' screen to fetch
		 *  'Personal Information' and 'Insurance Information' fields data after performing edit profile functionality-------*/
		
        driver.navigate().to(PATIENT_BASE_URL+"/user/service/service-request");
		wait1.until(ExpectedConditions.urlContains("/user/service/service-request"));
		
		String updatedName = profilePage.nameCSR.getAttribute("value");
		String updatedDOB = profilePage.dobCSR.getAttribute("value");
		String updatedPhoneNumber = profilePage.phoneNumberCSR.getAttribute("value");
		String updatedMemberID = profilePage.memberIDCSR.getAttribute("value");
		String updatedInsuranceName = profilePage.insurancePlanNameCSR.getAttribute("value");
		String updatedInsuranceGroupNumber = profilePage.insuranceGroupNumberCSR.getAttribute("value");
		
		/*-----------Display 'Personal Information' and 'Insurance Information' 
		 * on console after edit profile functionality-----------------*/
		
		logger.info("Updated Name displayed is: "+updatedName);
		logger.info("Updated DOB displayed is: "+updatedDOB);
		logger.info("Updated Phone Number displayed is: "+updatedPhoneNumber);
		logger.info("Updated Member ID displayed is: "+updatedMemberID);
		logger.info("Updated Insurance Name displayed is: "+updatedInsuranceName);
		logger.info("Updated Insurance Group Number displayed is: "+updatedInsuranceGroupNumber);
		
		/*--------Maintaining new edit profile details from Test Config file into String for comparison and switch with patient details-------*/
		String editedFirstName = testProp.getString("edited_firstName");
		String editedLastName = testProp.getString("edited_lastName");
		String editedFullName = editedFirstName+" "+editedLastName;
		String editedInsuranceName = testProp.getString("edited_insurancePlanName");
		String editedInsuranceGroupNumber = testProp.getString("edited_insuranceGroupNumber");
		String editedMemberID = testProp.getString("edited_memberID");
		String editedPhoneNumber = testProp.getString("edited_phoneNumber");
		
        /*-------------Doing comparison of values---------------*/
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(updatedName, editedFullName,"User name not updated in profile !!");
		softAssert.assertEquals(updatedInsuranceName, editedInsuranceName,"Insurance name not updated in profile !!");
		softAssert.assertEquals(updatedInsuranceGroupNumber, editedInsuranceGroupNumber,"Insurance Group Number not updated in profile !!");
		softAssert.assertEquals(updatedMemberID, editedMemberID,"Member ID not updated in profile !!");
		softAssert.assertTrue(updatedPhoneNumber.contains(editedPhoneNumber), "Phone Number not updated in profile !!");
		softAssert.assertAll();
		if (updatedName.equals(editedFullName)&&updatedMemberID.equals(editedMemberID)&&updatedInsuranceName.equals(editedInsuranceName)&&updatedInsuranceGroupNumber.equals(editedInsuranceGroupNumber)&&updatedPhoneNumber.contains(editedPhoneNumber)) {
			extentTest.log(LogStatus.PASS, "doProfileEditTest method is passed !!");
			logger.info(patient+" no. "+patientNum+" profile updated successfully !!");
			
			/*------------Update Test Data Configuration file after edit profile success-------------------*/
			
			testProp.setProperty("edited_firstName",testProp.getString(patient+"_"+patientNum+"_firstName"));
			testProp.setProperty(patient+"_"+patientNum+"_firstName",editedFirstName);
			
			testProp.setProperty("edited_lastName",testProp.getString(patient+"_"+patientNum+"_lastName"));
			testProp.setProperty(patient+"_"+patientNum+"_lastName",editedLastName);
			
			testProp.setProperty("edited_phoneNumber",testProp.getString(patient+"_"+patientNum+"_phoneNumber"));
			testProp.setProperty(patient+"_"+patientNum+"_phoneNumber",editedPhoneNumber);
			
			testProp.setProperty("edited_memberID",testProp.getString(patient+"_"+patientNum+"_memberID"));
			testProp.setProperty(patient+"_"+patientNum+"_memberID",editedMemberID);
			
			testProp.setProperty("edited_insurancePlanName",testProp.getString(patient+"_"+patientNum+"_insurancePlanName"));
			testProp.setProperty(patient+"_"+patientNum+"_insurancePlanName",editedInsuranceName);
			
			testProp.setProperty("edited_insuranceGroupNumber",testProp.getString(patient+"_"+patientNum+"_insuranceGroupNumber"));
			testProp.setProperty(patient+"_"+patientNum+"_insuranceGroupNumber",editedInsuranceGroupNumber);
			
			testProp.save();
			
		}
		else{
			extentTest.log(LogStatus.FAIL, "doProfileEditTest method is failed !!");
			logger.info(patient+" no."+patientNum+" profile not updated !!");
		}	
	}		

	@Test(priority = 20, description="Running doPasswordUpdateTest")
	public void doPasswordUpdateTest() throws SQLException, InterruptedException, ConfigurationException{
		driver.get(PATIENT_BASE_URL);
		profilePage.startTest("doPasswordUpdateTest"); 
		int patientNum = 1;
		ROLES patientRole = Constants.ROLES.PATIENT;
		String patient = patientRole.name().toLowerCase();
		logger.info("Running doPasswordUpdateTest for patient no. "+patientNum);
		String userEmail = testProp.getString(patient+"_"+patientNum+"_email_address");
		boolean statusof2FA = Utilities.get2FAStatus(connection, userEmail);
		String expectedUrl = PATIENT_BASE_URL+"/user/service/dashboard";
		if(statusof2FA){
			loginPage.doLogin(patientRole,patientNum, statusof2FA);
			logger.info("Query executing for OTP generation");
			WebDriverWait wait = submitOTP(connection,userEmail,null,driver);
			if(null==wait){
				String errorMessage = "OTP not available for email address: "+userEmail;
				extentTest.log(LogStatus.FAIL, errorMessage);
				logger.info("OTP not found for email address: "+userEmail);
				Assert.fail(errorMessage);
				return;
			}	
		}
		else{
			loginPage.doLogin(patientRole,patientNum, statusof2FA);
		}
		logger.info("Validate "+patient+" login successfully and navigate to dashboard or not?");
		WebDriverWait wait1 = new WebDriverWait(driver,10);
		wait1.until(ExpectedConditions.urlContains(expectedUrl));
		String actualUrl = driver.getCurrentUrl();
		if(!actualUrl.equals(expectedUrl)){
			Assert.assertEquals(actualUrl,expectedUrl,"Patient failed to login !!");
			extentTest.log(LogStatus.FAIL, "doProfileEditTest method is failed due to unsuccessful login !!");
			logger.info(patient+" no."+patientNum+" profile not updated due to unsuccessful login !!");
		}
		
		/*-----------Navigate to 'Edit Profile Page----------------------------*/
		Thread.sleep(2000);
		WebDriverWait wait = new WebDriverWait(driver, 20);	
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("*//tc-dropdown-button[@class='tc-dropdown-link']"))).click();
		Actions a = new Actions(driver);
		WebElement editProfileLink = driver.findElement(By.xpath("*//span[@class='icon icofont-edit']/ancestor::a"));
        a.moveToElement(editProfileLink).build().perform();
        Thread.sleep(1000);
        driver.manage().timeouts().implicitlyWait(40, TimeUnit.SECONDS);
        wait.until(ExpectedConditions.visibilityOf(editProfileLink));
        editProfileLink.click();
		wait.until(ExpectedConditions.urlContains("/user/service/edit-account"));
		logger.info("Edit profile link is clicked and navigated to Edit Profile page");
		
		/*---------------Providing New values to 'Password fields------------*/
		Thread.sleep(5000);
		WebElement element = driver.findElement(By.xpath("//input[@id='current_password']"));
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
		Thread.sleep(500); 
		
		element.sendKeys(testProp.getString(patient+"_"+patientNum+"_password"));
		profilePage.newPasswordEPP.sendKeys(testProp.getString("edited_password"));
		profilePage.confirmPasswordEPP.sendKeys(testProp.getString("edited_password"));
        profilePage.updatePasswordButtonEPP.click();
        wait1.until(ExpectedConditions.urlContains("/user/login"));
        
       /* ------------Update Test Data Configuration file after successful password updation-------------------*/
        String newPassword = testProp.getString("edited_password");
        
        testProp.setProperty("edited_password",testProp.getString(patient+"_"+patientNum+"_password"));
		testProp.setProperty(patient+"_"+patientNum+"_password",newPassword);
		
		testProp.save();
		
        /*-------------Perform Login functionality with updated password---------------*/
		boolean is2FAOpted = Utilities.get2FAStatus(connection, userEmail);
		if(statusof2FA){
			loginPage.doLogin(patientRole,patientNum, is2FAOpted);
			logger.info("Query executing for OTP generation");
			WebDriverWait wait2 = submitOTP(connection,userEmail,null,driver);
			if(null==wait2){
				String errorMessage = "OTP not available for email address: "+userEmail;
				extentTest.log(LogStatus.FAIL, errorMessage);
				logger.info("OTP not found for email address: "+userEmail);
				Assert.fail(errorMessage);
				return;
			}	
		}
		else{
			loginPage.doLogin(patientRole,patientNum, statusof2FA);
		}
		logger.info("Validate "+patient+" login successfully and navigate to dashboard or not?");
		wait1.until(ExpectedConditions.urlContains(expectedUrl));
		SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(actualUrl, expectedUrl,"Patient failed to login and not navigated to dashboard !!");
		softAssert.assertAll();
		if(actualUrl.equals(expectedUrl)) {
			extentTest.log(LogStatus.PASS, "doPasswordUpdateTest method is passed !!");
			logger.info(patient+" no. "+patientNum+" profile updated successfully !!");
		}
		else{
			extentTest.log(LogStatus.FAIL, "doPasswordUpdateTest method is failed !!");
			logger.info(patient+" no."+patientNum+" profile not updated !!");
		}	
	}		
		
		@AfterMethod
		public void tearDown(){
			profilePage.endTest();
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
