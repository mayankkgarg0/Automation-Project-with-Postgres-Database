package com.MRIoA.qa.pageObjects;


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
import com.MRIoA.qa.BaseClass;
import com.MRIoA.qa.utilities.Constants;


public class SignUpFormPage extends BaseClass {
	private static Logger logger = Logger.getLogger(SignUpFormPage.class);
	
	@FindBy(linkText="Sign Up!")
	WebElement signUpLink;
	
	@FindBy(xpath="//input[@placeholder='First Name']")
	WebElement firstName;
	
	@FindBy(xpath="//input[@placeholder='Last Name']")
	WebElement lastName;
	
	@FindBy(xpath="//input[@placeholder='Date of Birth'] | //span[@class='ant-calendar-picker-icon ng-star-inserted']/i[@nztype='calendar']")
	WebElement datePickerIcon;
	
	@FindBy(xpath="//table[@class='ant-calendar-table']/tbody/tr/td[@class='ant-calendar-cell ant-calendar-today ant-calendar-selected-day ng-star-inserted']")
	WebElement dateCalendar;
	
	@FindBy(xpath="*//tc-select[@id='insuranceCompanyName']")
	WebElement insuranceCompanyName;
	
	@FindBy(xpath = "*//tc-select[@id='insuranceCompanyName']//div[@class='options']/div[1]")
	public static WebElement insuranceCompanyNameOptions;
	
	@FindBy(xpath="//tc-input[@formcontrolname='insurancePlanName']/div/div/input[@placeholder='Insurance Plan Name']")
	WebElement insurancePlanName;
	
	@FindBy(xpath="//input[@id='insuranceGroupNumber' or @placeholder='Insurance Group Number']")
	WebElement insuranceGroupNumber;
	
	@FindBy(xpath="//tc-input[@formcontrolname='memberId']/div/div/input[@placeholder='Member ID']")
	WebElement memberID;
	
	@FindBy(xpath="//div[@class='iti__flag-container']/div[@aria-owns='country-listbox']")
	WebElement countryDropDown;
    
	@FindBy(xpath="//input[@formcontrolname='phoneNumber'] | //input[@placeholder='Phone Number']")
	WebElement phoneNumber;
	
	@FindBy(xpath="//tc-input[@formcontrolname='email']/div/div/input[@placeholder='Email Address']")
	WebElement emailAddress;
	
	@FindBy(xpath="//tc-input[@formcontrolname='password']/div/div/input[@placeholder='Password']")
	WebElement password;

	@FindBy(xpath="//tc-input[@formcontrolname='confirmPassword']/div/div/input[@placeholder='Confirm Password']")
	WebElement confirmPassword;
	
	@FindBy(xpath="//button[@id='sign-up-button']")
	WebElement signUpButton;
	
	@FindBy(xpath="*//span[text()='Sign Up with Google']/ancestor::a")
	WebElement googleSignUpButton;
	
	@FindBy(xpath="*//span[@class='check-detector']/preceding-sibling::input[@type='checkbox']")
	WebElement optFor2FA;
/*---------------------Object Repository for Web Elements on Create Service Request screen-------------------*/	
	
	@FindBy(xpath="//tc-input[@formcontrolname='name']")
	WebElement nameCSR;
	
	@FindBy(xpath="//tc-input[@formcontrolname='dob']")
	WebElement dobCSR;
	
	@FindBy(xpath="//tc-input[@formcontrolname='email']")
	WebElement emailAddressCSR;
	
	@FindBy(xpath="//tc-input[@formcontrolname='phoneNumber']")
	WebElement phoneNumberCSR;
	
	@FindBy(xpath="//tc-input[@formcontrolname='memberId']")
	WebElement memberIDCSR;
	
	@FindBy(xpath="//tc-input[@formcontrolname='insurancePlanName']")
	WebElement insuranceNameCSR;
	
/*-------------------------------------------------------------------------------------------------------------*/
	public SignUpFormPage(){
		logger.info("In SignUpFormPage constructor");
		PageFactory.initElements(driver, this); //this is current class object.Instead of 'this' we can also write 'SignUpFormPage.class'. That is all webelements in this class will be initialized by driver.this is an example of Abstraction
	}
	
	/* Method which will be called in every test case to start report generation. This methos needs to be placed in every page object class   */
	public void startTest(String testcaseName) {
		extentTest = extent.startTest(testcaseName);
	}
	
	//Actions:
	/**
	 * Method in 'SignUpFormPage' to verify 'Sign Up' link is displayed or not
	 * @return
	 */
	public boolean isSignUpLinkDisplayed(){
		return signUpLink.isDisplayed();
	}
	
	/**
	 * Method in 'SignUpFormPage' to verify 'Sign Up' link is clickable or not
	 * @return
	 */
	public void clickSignUpLink(){
		signUpLink.click();
	}	
	
	/**
	 * Method in 'SignUpFormPage' to do 'Form Sign Up'
	 * @param role
	 * @param patientNum
	 * @param isSignUpWithGoogle
	 * @param is2FAOpted
	 * @throws InterruptedException
	 */
	public void doFormSignUp(String role,int patientNum,boolean isSignUpWithGoogle, boolean is2FAOpted) throws InterruptedException{
		if(!isSignUpWithGoogle){
		firstName.sendKeys(testProp.getString(role+"_"+patientNum+"_firstName"));
		lastName.sendKeys(testProp.getString(role+"_"+patientNum+"_lastName"));
		emailAddress.sendKeys(testProp.getString(role+"_"+patientNum+"_email_address"));
		password.sendKeys(testProp.getString(role+"_"+patientNum+"_password"));
        Thread.sleep(2000);
        confirmPassword.sendKeys(testProp.getString(role+"_"+patientNum+"_password"));	
        Thread.sleep(2000);
		}
		datePickerIcon.click();
		dateCalendar.click();
		insuranceCompanyName.click();
		insuranceCompanyNameOptions.click();
		insurancePlanName.sendKeys(testProp.getString(role+"_"+patientNum+"_insurancePlanName"));
		insuranceGroupNumber.sendKeys(testProp.getString(role+"_"+patientNum+"_insuranceGroupNumber"));
		Thread.sleep(2000);
		memberID.sendKeys(testProp.getString(role+"_"+patientNum+"_memberID"));
		countryDropDown.click();
		Actions a = new Actions(driver);
        WebElement country = driver.findElement(By.xpath("//li[@data-dial-code='91']"));  //inspect India 
        a.moveToElement(country).build().perform();
        driver.manage().timeouts().implicitlyWait(40, TimeUnit.SECONDS);
        driver.findElement(By.xpath("//li[@data-dial-code='91']")).click();
        Thread.sleep(1000);
        phoneNumber.sendKeys(testProp.getString(role+"_"+patientNum+"_phoneNumber"));
        if(is2FAOpted){
        	optFor2FA.click();
        }  
        signUpButton.click();
        Thread.sleep(2000);
        try{
        WebElement errorMessage = driver.findElement(By.xpath("//span[contains(text(),'Email address already exists')]"));
        if (null != errorMessage && errorMessage.isDisplayed()){
        	logger.debug("Yes, error message has been displayed on UI..Waiting to inspect it");
        	return;
        }
        }catch(NoSuchElementException e){
        	logger.info("No element found for----> Email address already exist error message");
        }
        
        if(is2FAOpted ){
        	logger.info("Waiting for OTP screen");
        	WebDriverWait wait = new WebDriverWait(driver,2);
    		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("*//input[@placeholder='OTP']")));	
        }  
	}	
	
	/**
	 * Method in 'SignUpFormPage' to do 'Google Sign Up'
	 * @param role
	 * @param patientNum
	 * @param is2FAOpted
	 * @throws InterruptedException
	 */
	public void doGoogleSignUp(String role,int patientNum, boolean is2FAOpted) throws InterruptedException{
		String email = testProp.getString(role+"_"+patientNum+"_googleEmailAddress");
		String password = testProp.getString(role+"_"+patientNum+"_googlePassword");
		googleSignUpButton.click();
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
		          logger.info(role+" is providing Google account credentials!!");
		          WebDriverWait explicitWait = new WebDriverWait(driver, 30);
		          explicitWait.ignoring(StaleElementReferenceException.class).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@type='email'] | //input[@aria-label='Email or phone']"))).sendKeys(email);
		          driver.findElement(By.xpath("*//span[text()='Next'] | .//*[@id='identifierNext']")).click();
		          explicitWait.ignoring(StaleElementReferenceException.class).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@type='password'] | //input[@label='Enter your password']"))).sendKeys(password);
		          driver.findElement(By.id("passwordNext")).click();
		          Thread.sleep(5000);
		      }
		  }
		  driver.switchTo().window(parentWindow);	
		doFormSignUp(role,patientNum,true,is2FAOpted);
		  logger.info(role+" is registering by providing details in sign up form!!");
	}
	
	/* Method which will be called in every test case 'After Method' to end report generation. */
	public void endTest() {
		extent.endTest(extentTest);
		extent.flush();
	}
}
