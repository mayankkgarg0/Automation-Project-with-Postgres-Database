package com.MRIoA.qa.pageObjects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import com.MRIoA.qa.BaseClass;
import com.MRIoA.qa.utilities.Constants;
import com.MRIoA.qa.utilities.Constants.ROLES;

public class ForgotAndResetPasswordPage extends BaseClass{
	private static Logger logger = Logger.getLogger(ForgotAndResetPasswordPage.class);
	
	@FindBy(linkText="Forgot Password?")
	WebElement forgotPwdLink;
	
	@FindBy(linkText="Login")
	WebElement loginLink;
	
	@FindBy(linkText="Sign Up!")
	WebElement signupLink;
	
	@FindBy(xpath="//input[@placeholder='Email Address']")
	WebElement emailAddress;
	
	@FindBy(xpath="*//span[text()=' Reset ']/ancestor::button")
	WebElement resetButton;
	
	@FindBy(xpath="*//tc-form-group[1]/tc-input/div/div/input[@placeholder='New Password']")
	WebElement newPasswordField;
	
	@FindBy(xpath="*//tc-form-group[2]/tc-input/div/div/input[@placeholder='Confirm Password']")
	WebElement confirmPasswordField;
	
	@FindBy(xpath="*//span[text()=' Submit ']/ancestor::button")
	WebElement submitButton;
	
	public ForgotAndResetPasswordPage(){
		logger.info("In ForgotResetPasswordPage constructor");
		PageFactory.initElements(driver, this); 
	}
	
	public void startTest(String testcaseName) {
		extentTest = extent.startTest(testcaseName);
	}
	
	/**
	 * Method in 'ForgotResetPasswordPage' to perform Forgot & Reset pwd functionality with invalid email address
	 * @param roleType
	 * @param patientNum
	 */
	public void doForgotPwd_InvalidEmailAddress(ROLES roleType, int patientNum){
		String role = roleType.name().toLowerCase();
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOf(forgotPwdLink));
		forgotPwdLink.click();
		wait.until(ExpectedConditions.urlContains(PATIENT_BASE_URL + "/user/forgot-password"));
		emailAddress.sendKeys(testProp.getString(role + "_" + patientNum + "_email_address"));
		wait.until(ExpectedConditions.elementToBeClickable(resetButton)).click();
	}
	
	/**
	 * Method in 'ForgotResetPasswordPage' to perform Forgot & Reset pwd functionality with valid email address
	 * @param roleType
	 * @param patientNum
	 */
	public void doForgotPassword_ValidEmailAddress(ROLES roleType, int patientNum) {
		String role = roleType.name().toLowerCase();
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOf(forgotPwdLink));
		forgotPwdLink.click();
		wait.until(ExpectedConditions.urlContains(PATIENT_BASE_URL + "/user/forgot-password"));
		emailAddress.sendKeys(testProp.getString(role + "_" + patientNum + "_email_address"));
		wait.until(ExpectedConditions.elementToBeClickable(resetButton)).click();
	}
	
	/**
	 * Common method in 'ForgotResetPasswordPage' to click on reset password link
	 * @param textLike
	 * @throws InterruptedException
	 */
	public void clickLinkByHref(String textLike) throws InterruptedException {
	    List<WebElement> anchors = driver.findElements(By.tagName("a"));
	    Iterator<WebElement> i = anchors.iterator();
	    boolean found = false;
	    while(i.hasNext()) {
	        WebElement anchor = i.next();
	        
	        if(null!=anchor.getText() && anchor.getText().contains(textLike)) {
	        	found = true;
	        	System.out.println("Reset password link inspected !!");
	        	Thread.sleep(5000);
	        	WebDriverWait wait = new WebDriverWait(driver, 10);
	        	wait.until(ExpectedConditions.elementToBeClickable(anchor)).click();
	            System.out.println("Reset password link clicked !!");
	            List<String> tabs = new ArrayList<String>(driver.getWindowHandles()); 
				driver.switchTo().window(tabs.get(1));
				logger.info("waiting for reset pwd page to appear");
				driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
				driver.manage().timeouts().pageLoadTimeout(Constants.IMPLICIT_WAIT, TimeUnit.SECONDS);
	            wait.until(ExpectedConditions.urlContains("/user/update-password"));
	            break;
	        }
	    }    
	    if(!found){
	    	Assert.fail("Reset password link not found !!");
	    }
	}
	
	/**
	 * Common method in 'ForgotResetPasswordPage' to open reset password e-mail
	 * @throws InterruptedException 
	 */
	public void openResetPwdMail() throws InterruptedException {
		System.out.println("Waiting for inboxpane");
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.urlContains("inboxpane"));
		System.out.println("Encountered inboxpane");
		
		List<WebElement> email = driver.findElements(By.xpath("*//table/tbody/tr/td[4]"));
		for(WebElement emailsub : email){
		    if(emailsub.getText().contains("Reset password")){
		    	System.out.println("Clicking mail");
		    	emailsub.findElement(By.tagName("a")).click();
		    	System.out.println("Mail clicked");

		    	break;
		        }
		    }
		System.out.println("Waiting for msgpain");
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		wait.until(ExpectedConditions.urlContains("msgpane"));
		System.out.println("Encountered msgpane");
		
		/*------------------Navigate to iframe tag of Mail body--------------------------*/
		driver.switchTo().frame("msg_body");
		WebElement link = driver.findElement(By.xpath("//html/body/table/tbody/tr[2]/td/table/tbody/tr/td/div/a"));
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", link);
		Thread.sleep(500);
		try {
		clickLinkByHref("Reset Password");
		
		  ArrayList<String> tabs = new ArrayList<String> (driver.getWindowHandles());
		  System.out.println("tabs "+tabs.size()); 
		  
		  driver.switchTo().window(tabs.get(1)); //switches to new tab opened after clicking reset pwd link in received mail
		  driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
		e.printStackTrace();
		}
	}
	
	/**
	 * Common method in 'ForgotResetPasswordPage' to reset the password
	 */
	public void resetPassword() {
		logger.info("trying to reset password");
		WebDriverWait wait = new WebDriverWait(driver, 20);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("*//tc-form-group[1]/tc-input/div/div/input[@placeholder='New Password']")));

		logger.debug("Entering New password");
		newPasswordField.sendKeys(testProp.getString("reset_newPassword"));
		logger.debug("trying to Enter Confirm password");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("*//tc-form-group[2]/tc-input/div/div/input[@placeholder='Confirm Password']")));
		logger.debug("Entering Confirm password");		
		confirmPasswordField.sendKeys(testProp.getString("reset_newPassword"));
		logger.debug("trying to Click on Submit button");
		submitButton.click();
		logger.info("Submit button is clicked");
		
	}
	
	public void endTest() {
		extent.endTest(extentTest);
		extent.flush();
	}

}
