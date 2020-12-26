package com.MRIoA.qa.pageObjects;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.MRIoA.qa.BaseClass;
import com.MRIoA.qa.utilities.Constants;
import com.MRIoA.qa.utilities.Constants.ROLES;
import com.MRIoA.qa.utilities.Utilities;
import com.relevantcodes.extentreports.LogStatus;

//This is Patient login page
public class LoginPage extends BaseClass {
	private static Logger logger = Logger.getLogger(LoginPage.class);
	
	//Now we are creating Page Factory or Object Repository
	@FindBy(xpath="//div/input[@class='input-control ng-pristine ng-valid ng-touched' or @placeholder='Email Address']")
	WebElement emailAddress;
	
	@FindBy(xpath="//div/input[@class='input-control ng-untouched ng-pristine ng-valid' or @placeholder='Password']")
	WebElement password;
	
	@FindBy(xpath="//div/button/span[@class='btn-line']")
	WebElement loginButton;
	
	@FindBy(linkText="Sign Up!")
	WebElement signUpLink;
	
	@FindBy(linkText="Forgot Password?")
	WebElement forgotPwdLink;
	
	@FindBy(xpath="//div[@class='googlehover img-responsive btn-google']")
	WebElement signInWithGoogleButton;
	
	@FindBy(xpath="//img[@class='logo-img']")
	WebElement appLogo;
	
	@FindBy(xpath="//h1[text()='Service Requests']")
	WebElement heading;
	
	//Initializing object repository
	public LoginPage(){
		logger.info("In loginPage constructor");
		PageFactory.initElements(driver, this); //this is current class object.Instead of 'this' we can also write 'LoginPage.class'. That is all webelements in this class will be initialized by driver.this is an example of Abstraction
	}
	
	/* Method which will be called in every test case to start report generation. This methos needs to be placed in every page object class   */
	public void startTest(String testcaseName) {
		extentTest = extent.startTest(testcaseName);
	}
	
	//Actions:
	/**
	 * Method in 'LoginPage' to verify 'Login page title displayed or not?'
	 * @return
	 */
	public String isLoginPageTitleDisplayed(){
		return driver.getTitle();		
	}
	
	/**
	 * Method in 'LoginPage' to verify 'App logo displayed or not?'
	 * @return
	 */
	public boolean isAppLogoDisplayed(){
		return appLogo.isDisplayed();
	}
	
	/**
	 * Method in 'LoginPage' to get 'Current Page URL'
	 * @return
	 */
	public String getCurrentPageUrl(){
		return driver.getCurrentUrl();
	}
	
	/**
	 * Method in 'LoginPage' to perform 'Login functionality'
	 * @param roleType
	 * @param patientNum
	 * @param is2FAOpted
	 * @throws InterruptedException 
	 */
	public void doLogin(ROLES roleType,int patientNum, boolean is2FAOpted) throws InterruptedException{
		
		String role = roleType.name().toLowerCase();
		
		System.out.println("config field======== "+testProp);
		System.out.println("enail field======== "+emailAddress);
		WebDriverWait wait = new WebDriverWait(driver,10);
		wait.until(ExpectedConditions.elementToBeClickable(emailAddress)).sendKeys(testProp.getString(role+"_"+patientNum+"_email_address"));
		wait.until(ExpectedConditions.elementToBeClickable(password)).sendKeys(testProp.getString(role+"_"+patientNum+"_password"));
		Thread.sleep(2000);
		wait.until(ExpectedConditions.elementToBeClickable(loginButton)).click();	
		logger.info("Login button is clicked");
		 try{
		        WebElement errorMessage = driver.findElement(By.xpath("//span[contains(text(),'Please enter valid Email Address')]"));
		        if (null != errorMessage && errorMessage.isDisplayed()){
		        	logger.debug("Yes, error message has been displayed on UI..Waiting to inspect it");
		        	return;
		        }
		        }catch(NoSuchElementException e){
		        	logger.info("No element found for----> Email address already exist error message");
		        }
		if(is2FAOpted ){
        	logger.info("Waiting for OTP screen");
        	
    		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("*//input[@placeholder='OTP']")));
        }  
		else{
			driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT,TimeUnit.SECONDS);
			wait.until(ExpectedConditions.urlContains("/user/service/dashboard"));
		}
	}
	
	/**
	 * Method in 'LoginPage' to do 'Google Sign In'
	 * @param roleType
	 * @param patientNum
	 * @param is2FAOpted
	 */
	public void googleSignIn(ROLES roleType,int patientNum, boolean is2FAOpted) throws InterruptedException{
	
		String role = roleType.name().toLowerCase();

		String email = testProp.getString(role+"_"+patientNum+"_googleEmailAddress");
		String password = testProp.getString(role+"_"+patientNum+"_googlePassword");
		signInWithGoogleButton.click();
		String parentWindow = driver.getWindowHandle();     
		  logger.info("Parent Window ID is : "+ parentWindow);

		  Set<String> allWindow = driver.getWindowHandles();
		  int count = allWindow.size();
		  logger.info("Total Window : " + count);

		  for(String child:allWindow)
		  {
		      if(!parentWindow.equalsIgnoreCase(child))
		      { 
		          driver.switchTo().window(child);
		          driver.manage().timeouts().pageLoadTimeout(Constants.IMPLICIT_WAIT,TimeUnit.SECONDS);
		          Thread.sleep(3000);
		          WebDriverWait explicitWait = new WebDriverWait(driver, 30);	
		          explicitWait.ignoring(StaleElementReferenceException.class).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@type='email'] | //input[@aria-label='Email or phone']"))).sendKeys(email);
		          driver.findElement(By.xpath("*//span[text()='Next'] | .//*[@id='identifierNext']")).click();
		          explicitWait.ignoring(StaleElementReferenceException.class).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@type='password'] | //input[@label='Enter your password']"))).clear();
		          explicitWait.ignoring(StaleElementReferenceException.class).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@type='password'] | //input[@label='Enter your password']"))).sendKeys(password);
		          driver.findElement(By.id("passwordNext")).click();
		          Thread.sleep(5000);
		      }
		  }
		  driver.switchTo().window(parentWindow);	
		  if(is2FAOpted ){
	        	logger.info("Waiting for OTP screen");
	        	WebDriverWait wait = new WebDriverWait(driver,2);
	    		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("*//input[@placeholder='OTP']")));
	        }  
			else{
				driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT,TimeUnit.SECONDS);
				WebDriverWait wait = new WebDriverWait(driver,10);
				wait.until(ExpectedConditions.urlContains("/user/service/dashboard"));
			}
	}
	
	/**
	 * Method in 'LoginPage' to perform 'Logout functionality'
	 * @throws InterruptedException
	 */
	public void logout() throws InterruptedException{
		WebDriverWait explicitWait = new WebDriverWait(driver, 20);	
		explicitWait.until(ExpectedConditions.elementToBeClickable(By.xpath("*//tc-dropdown-button/div"))).click();
		Actions a = new Actions(driver);
		WebElement logoutButton = driver.findElement(By.linkText("Logout"));
        a.moveToElement(logoutButton).build().perform();
        driver.manage().timeouts().implicitlyWait(40, TimeUnit.SECONDS);
        explicitWait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Logout")));
        explicitWait.until(ExpectedConditions.elementToBeClickable(By.linkText("Logout"))).click();
        logger.info("Logout button is clicked");
	}
	
	/* Method which will be called in every test case 'After Method' to end report generation. */
	public void endTest() {
		extent.endTest(extentTest);
		extent.flush();
	}
	
	/**
	 * Common method for patient login which is used 
	 * @param patientNum
	 * @param connection
	 * @param testName
	 * @throws SQLException
	 * @throws InterruptedException
	 */
	public void attemptPatientLogin( int patientNum,Connection connection, String testName) throws SQLException, InterruptedException{
		
		driver.get(PATIENT_BASE_URL);
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		startTest(testName); 
		logger.info("Running  method "+testName);
		ROLES patientRole = Constants.ROLES.PATIENT;
		String patient = patientRole.name().toLowerCase();
		String userEmail = testProp.getString(patient+"_"+patientNum+"_email_address");
		boolean statusof2FA = Utilities.get2FAStatus(connection, userEmail);
		if(statusof2FA){
			doLogin(patientRole,patientNum, statusof2FA);
			logger.info("Query executing for OTP generation");
			WebDriverWait wait = submitOTP(connection,userEmail,null,driver);
			if(null==wait){
				String errorMessage = "OTP not available for email address: "+userEmail;
				extentTest.log(LogStatus.FAIL, errorMessage);
				logger.info("patientValidEmailAddress_validPassword_LoginTest_with2FA_ValidOTP method failed as OTP is not available for email address: "+userEmail);
				Assert.fail(errorMessage);;
			}	
		}
		else{
			doLogin(patientRole,patientNum, statusof2FA);
		}
	}
}
