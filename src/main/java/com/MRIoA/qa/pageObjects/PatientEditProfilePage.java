package com.MRIoA.qa.pageObjects;

import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.MRIoA.qa.BaseClass;

public class PatientEditProfilePage extends BaseClass{
	
private static Logger logger = Logger.getLogger(LoginPage.class);
	
	//Now we are creating Page Factory or Object Repository

	@FindBy(xpath="*//span[text()='Create Service Request']/ancestor::a")
	public WebElement createServiceRequestLink;
	
	@FindBy(xpath="*//tc-dropdown-button[@class='tc-dropdown-link']")
	public WebElement downwardArrow;
	
	@FindBy(xpath="*//span[@class='icon icofont-edit']/ancestor::a")
	public WebElement editProfileLink;	 

/*--------------Object Repository of Profile fields on Create Service Request page------------------*/
	
	@FindBy(xpath="//input[@id='name']")
	public WebElement nameCSR;
	
	@FindBy(xpath="//input[@id='dob']")
	public WebElement dobCSR;
	
	@FindBy(xpath="//input[@id='phoneNo']")
	public WebElement phoneNumberCSR;
	
	@FindBy(xpath="//input[@id='memberId']")
	public WebElement memberIDCSR;
	
	@FindBy(xpath="//input[@id='insuranceCompanyName']")
	public WebElement insuranceCompanyNameCSR;
	
	@FindBy(xpath="//input[@id='insurancePlanName']")
	public WebElement insurancePlanNameCSR;
	
	@FindBy(xpath="//input[@id='insuranceGroupNumber']")
	public WebElement insuranceGroupNumberCSR;
	
	/*--------------Object Repository of Profile fields on Edit Profile page------------------*/
	
	@FindBy(xpath="//input[@id='firstName']")
	public WebElement firstNameEPP;
	
	@FindBy(xpath="//input[@id='lastName']")
	public WebElement lastNameEPP;
	
	@FindBy(xpath="//nz-date-picker[@id='dob']")
	public WebElement dobEPP;
	
	@FindBy(xpath="*//div[@class='ant-calendar-header']")
	public WebElement dobCalenderHeader;
	
	@FindBy(xpath="*//div[@class='ant-calendar-body']")
	public WebElement dobCalenderBody;
	
	@FindBy(xpath="//table[@class='ant-calendar-table']/tbody/tr/td[@class='ant-calendar-cell ant-calendar-today ant-calendar-selected-day ng-star-inserted']")
	WebElement dateCalendar;
	
	@FindBy(xpath="//div[@class='iti__flag-container']")
	public WebElement countryCodeDropDownEPP;
	
	@FindBy(xpath="//div[@class='iti__flag-container']//ul/li[@data-dial-code='1']")
	public WebElement selectUSFromCountryCodeEPP;
	
	@FindBy(xpath="//input[@id='phone_no']")
	public WebElement phoneNumberEPP;
	
	@FindBy(xpath="//input[@id='memberId']")
	public WebElement memberIDEPP;
	
	@FindBy(xpath="//input[@id='insuranceName']")
	public WebElement insurancePlanNameEPP;
	
	@FindBy(xpath="//input[@id='insuranceGroupNumber']")
	public WebElement insuranceGroupNumberEPP;
	
	@FindBy(xpath="*//tc-select[@id='insuranceCompanyName']")
	WebElement insuranceCompanyNameEPP;
	
	@FindBy(xpath = "*//tc-select[@id='insuranceCompanyName']//div[@class='options']/div[1]")
	public static WebElement insuranceCompanyNameOptionsEPP;
	
	@FindBy(xpath="//div[@class='tc-switcher-handle']")
	public WebElement opt2FAButtonEPP;
	
	@FindBy(xpath="*//button/span[contains(text(),' Save Changes ')]/preceding-sibling::span[@class='btn-line']")
	public WebElement saveChangesButtonEPP;
	
	/*----------------------Object Repository of Update Password fields*/
	@FindBy(xpath="//input[@id='current_password']")
	public WebElement currentPasswordEPP;
	
	@FindBy(xpath="//input[@id='new_password']")
	public WebElement newPasswordEPP;
	
	@FindBy(xpath="//input[@id='confirmPassword']")
	public WebElement confirmPasswordEPP;
	
	@FindBy(xpath="*//span[text()='Update Password']/ancestor::button")
	public WebElement updatePasswordButtonEPP;
	
	/*--------------------------------------------------------------------------------------------*/
	
	//Initializing page factory
	public PatientEditProfilePage(){
		logger.info("In  PatientEditProfilePage constructor");
		PageFactory.initElements(driver, this); 
	}
	
	/* Method which will be called in every test case to start report generation. This methos needs to be placed in every page object class   */
	public void startTest(String testcaseName) {
		extentTest = extent.startTest(testcaseName);
	}
	
	//Actions:
	/**
	 * Method in 'PatientEditProfilePage' to 'Edit Personal & Insurance Details'
	 * @throws InterruptedException
	 */
	public void goToEditProfilePage() throws InterruptedException{
		WebDriverWait wait = new WebDriverWait(driver, 20);	
		wait.until(ExpectedConditions.elementToBeClickable(downwardArrow)).click();
		Actions a = new Actions(driver);
        a.moveToElement(editProfileLink).build().perform();
        Thread.sleep(1000);
        driver.manage().timeouts().implicitlyWait(40, TimeUnit.SECONDS);
        wait.until(ExpectedConditions.visibilityOf(editProfileLink)).click();
		wait.until(ExpectedConditions.urlContains("/user/service/edit-account"));
		logger.info("Edit profile link is clicked");
		logger.info("Personal details edition under process...!!");
		
		/*---------------Providing New values to 'Personal and Insurance fields------------*/
		Thread.sleep(10000);
		wait.until(ExpectedConditions.elementToBeClickable(firstNameEPP)).clear();
		wait.until(ExpectedConditions.elementToBeClickable(firstNameEPP)).sendKeys(testProp.getString("edited_firstName"));
		wait.until(ExpectedConditions.elementToBeClickable(lastNameEPP)).clear();
		wait.until(ExpectedConditions.elementToBeClickable(lastNameEPP)).sendKeys(testProp.getString("edited_lastName"));
		wait.until(ExpectedConditions.elementToBeClickable(dobEPP)).click();
		wait.until(ExpectedConditions.visibilityOf(dobCalenderHeader));
        a.moveToElement(dobCalenderHeader).build().perform();
        driver.findElement(By.xpath("//a[@title='Previous month (PageUp)']")).click();
        Thread.sleep(2000);
		wait.until(ExpectedConditions.visibilityOf(dobCalenderBody));
        a.moveToElement(dobCalenderBody).build().perform();
		driver.findElement(By.xpath("//table/tbody/tr[3]/td[4]/div")).click();
		wait.until(ExpectedConditions.elementToBeClickable(phoneNumberEPP)).clear();
		wait.until(ExpectedConditions.elementToBeClickable(phoneNumberEPP)).sendKeys(testProp.getString("edited_phoneNumber"));
		wait.until(ExpectedConditions.elementToBeClickable(memberIDEPP)).clear();
		wait.until(ExpectedConditions.elementToBeClickable(memberIDEPP)).sendKeys(testProp.getString("edited_memberID"));
		wait.until(ExpectedConditions.elementToBeClickable(insurancePlanNameEPP)).clear();
		wait.until(ExpectedConditions.elementToBeClickable(insurancePlanNameEPP)).sendKeys(testProp.getString("edited_insurancePlanName"));
		wait.until(ExpectedConditions.elementToBeClickable(insuranceGroupNumberEPP)).clear();
		wait.until(ExpectedConditions.elementToBeClickable(insuranceGroupNumberEPP)).sendKeys(testProp.getString("edited_insuranceGroupNumber"));
        opt2FAButtonEPP.click();
        wait.until(ExpectedConditions.elementToBeClickable(saveChangesButtonEPP)).click();
        Thread.sleep(1000);
		}
	
	/* Method which will be called in every test case 'After Method' to end report generation. */
	public void endTest() {
		extent.endTest(extentTest);
		extent.flush();
	}

}
