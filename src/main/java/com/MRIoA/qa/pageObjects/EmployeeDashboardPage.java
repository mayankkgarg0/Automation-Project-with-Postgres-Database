package com.MRIoA.qa.pageObjects;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchWindowException;
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
import com.MRIoA.qa.utilities.Utilities;
import com.relevantcodes.extentreports.LogStatus;

public class EmployeeDashboardPage extends BaseClass{
	
	private static Logger logger = Logger.getLogger(EmployeeDashboardPage.class);
		
	@FindBy(xpath = "//a[@class='googlehover']/div/span")
	public static WebElement signInWithMicrosoftButton;

	@FindBy(xpath = "//input[@type='email']")
	public WebElement emailAddressField;

	@FindBy(xpath = "//input[@value='Next']")
	public WebElement nextButton;

	@FindBy(xpath = "//input[@type='password']")
	public WebElement passwordField;

	@FindBy(xpath = "//input[@value='Sign in']")
	public WebElement signInButton;

	@FindBy(xpath = "//input[@placeholder='OTP']")
	public WebElement otpField;

	@FindBy(xpath = "//span[@class='btn-line']")
	public WebElement submitButton;
	
	@FindBy(xpath="*//div[@aria-labelledby='dropdownBasic2']")
	WebElement actionsPopUp;
	
	/*-----------------------------Object repository of fields on supervisor dashboard------------------*/
	
	@FindBy(xpath = "*//a//span[contains(text(),'New/Reopened')]")
	public static WebElement newReopenedLink;
	
	@FindBy(xpath = "*//a//span[contains(text(),'Assigned')]")
	public static WebElement assignedLink;
	
	@FindBy(xpath = "*//a//span[contains(text(),'Rejected')]")
	public static WebElement rejectedLink;
	
	@FindBy(xpath = "*//a//span[contains(text(),'Submitted')]")
	public static WebElement submittedLink;
	
	@FindBy(xpath = "*//a//span[contains(text(),'Closed')]")
	public static WebElement closedLink;
	
	@FindBy(xpath = "*//a//span[contains(text(),'Archived')]")
	public static WebElement archivedLink;
	
	@FindBy(xpath= "//div[text()='No Service Request Created']")
	public WebElement textWhenNoSRIsCreated;
	
	
	/*-----------------------------Object repository of fields on 'Building Case' screen-----------------*/
	
	@FindBy(xpath = "*//input[@placeholder='Client Case Id']")
	public static WebElement clientCaseID;
	
	@FindBy(xpath = "*//input[@placeholder='Diagnosis Category']")
	public static WebElement diagnosisCategory;
	
	@FindBy(xpath = "*//tc-select[@id='reviewCategory']")
	public static WebElement reviewCategory;
	
	@FindBy(xpath = "*//tc-select//div[@class='options']/div[6]")
	public static WebElement reviewCategoryOptions;
	
	@FindBy(xpath = "*//tc-select[@id='expediteCode']")
	public static WebElement expediteCode;
	
	@FindBy(xpath = "*//tc-select[@id='expediteCode']//div[@class='options']/div[6]")
	public static WebElement expediteCodeOptions;
	
	@FindBy(xpath = "//textarea[@placeholder=' Handling Notes']")
	public static WebElement handlingNotes;
	
	@FindBy(xpath = "*//input[@placeholder='Preferred Specialty']")
	public static WebElement preferredSpecialty;
	
	@FindBy(xpath = "*//input[@placeholder='Preferred Specialty Alternative']")
	public static WebElement preferredSpecialtyAlternative;
	
	@FindBy(xpath = "//textarea[@placeholder=' Review Instruction']")
	public static WebElement reviewInstruction;
	
	@FindBy(xpath = "//textarea[@placeholder=' Questions For Review']")
	public static WebElement questionsForReview;
	
	@FindBy(xpath = "*//input[@placeholder='CPT Codes']")
	public static WebElement cptCodes;
	
	@FindBy(xpath = "*//input[@placeholder='ICD Codes']")
	public static WebElement icdCodes;
	
	@FindBy(xpath = "*//button//span[contains(text(),' Save and Submit ')]/preceding-sibling::span[@class='btn-line']")
	public static WebElement saveAndSubmitButton;
	
	public EmployeeDashboardPage() {
		logger.info("In EmployeeDashboardPage constructor");
		PageFactory.initElements(driver, this);
	}
	
	/**
	 * Common method to check actions available with service request on employee dashboard
	 * @param searchResult
	 * @throws InterruptedException
	 */
	public void checkActionsOnServiceRequest() throws InterruptedException {
		logger.info("Verify actions available on service request"); 
		List<WebElement> tableHeaders = Utilities.getTableHeaders(driver);
		int tableHeadersCount = tableHeaders.size();
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@class='table-box']/tbody/tr[1]//td["+tableHeadersCount+"]//div/button[@id='dropdownBasic2']/span[@class='btn-line']")));
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//table[@class='table-box']/tbody/tr[1]//td["+tableHeadersCount+"]//div/button[@id='dropdownBasic2']/span[@class='btn-line']"))).click();
		logger.info("Action button has been clicked !!");
		wait.until(ExpectedConditions.visibilityOf(actionsPopUp));
		Actions a = new Actions(driver);
        a.moveToElement(actionsPopUp).build().perform();
        Thread.sleep(4000);
		List<WebElement> getActions = driver.findElements(By.xpath("*//div[@aria-labelledby='dropdownBasic2']/button"));
		logger.info("Count of actions is: " + (getActions.size()));
		logger.info("Actions names is as below: ");
		for(int i=1;i<=getActions.size();i++){
			String action = driver.findElement(By.xpath("*//div[@aria-labelledby='dropdownBasic2']/button["+i+"]")).getText();
			logger.info("Action "+ i+" is: "+action);
		}	
		Thread.sleep(3000);
	}	

	/**
	 * Common method to 'View service request' on employee dashboard
	 * @param searchResult
	 * @throws InterruptedException
	 */
	public void viewServiceRequest(WebElement searchResult) throws InterruptedException{
		logger.info("View service request");    
		List<WebElement> tableHeaders = Utilities.getTableHeaders(driver);
		int tableHeadersCount = tableHeaders.size();
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@class='table-box']/tbody/tr[1]//td["+tableHeadersCount+"]//div/button[@id='dropdownBasic2']/span[@class='btn-line']")));
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//table[@class='table-box']/tbody/tr[1]//td["+tableHeadersCount+"]//div/button[@id='dropdownBasic2']/span[@class='btn-line']"))).click();
		logger.info("Action button has been clicked !!");
		wait.until(ExpectedConditions.visibilityOf(actionsPopUp));
		Actions a = new Actions(driver);
	    a.moveToElement(actionsPopUp).build().perform();
	    Thread.sleep(2000);
	    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("*//div[@aria-labelledby='dropdownBasic2']/button[1]"))).click();
	    logger.info("View action is clicked....Navigating to 'View Request' screen");
	    wait.until(ExpectedConditions.urlContains(EMPLOYEE_BASE_URL+"/super/service-request"));
	    driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
	    driver.manage().timeouts().pageLoadTimeout(Constants.IMPLICIT_WAIT, TimeUnit.SECONDS);
		//Itearting through all the accordians
	    List<WebElement> allinks= driver.findElements(By.cssSelector(".btn-link.ng-star-inserted"));
	    System.out.println("links---------- "+allinks.size());
	    Thread.sleep(2000);
	    for(WebElement w:allinks){
	    	System.out.println("iterating links");
	    	((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", w);
	        String classes = w.getAttribute("class");
	        List<String> classList = Arrays.asList(classes.split(" "));
	        if(classList.contains("collapsed")){
	        	Thread.sleep(3000);
	        	// click to open
	        	w.click();
	        }
	            Thread.sleep(3000);
	            //click to close
	            w.click();  
	    }
		JavascriptExecutor jsExecuter = (JavascriptExecutor)driver;
		//scroll till bottom of the page
		jsExecuter.executeScript("window.scrollTo(0,document.body.scrollHeight)");
		Thread.sleep(2000);
		logger.info("Scroll back to the top of the page");
		jsExecuter.executeScript("window.scrollTo(0,document.body.scrollTop)");
		Thread.sleep(5000);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("*//button[@title='Back']"))).click();
		logger.info("Back button is clicked and navigate back to dashboard");
		wait.until(ExpectedConditions.urlContains(EMPLOYEE_BASE_URL+"/super/new"));	
		Thread.sleep(2000);
	}
	
	/**
	 * Common method to 'Reject service request' on employee dashboard
	 * @param searchResult
	 * @throws InterruptedException 
	 */
	public void rejectServiceRequest() throws InterruptedException{
		logger.info("Reject service request under process !!");    
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//table[@class='table-box']/tbody/tr[1]/td[1]//p[@class='titleLink ']"))).click();
		logger.info("Title link is clicked !!");
		wait.until(ExpectedConditions.urlContains(EMPLOYEE_BASE_URL+"/super/service-request"));	
		Thread.sleep(2000);
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("*//button//span[text()=' Reject ']/preceding-sibling::span"))).click();
		logger.info("Reject request action is clicked !!");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='modal-window']")));
	    logger.info("Modal window title is: "+driver.findElement(By.xpath("//div[@class ='modal-window'] /div/h2")).getText());
	    driver.findElement(By.xpath("//tc-textarea//textarea[@placeholder=' Remarks']")).sendKeys("Documents missing");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("*//button//span[text()='Reject']/preceding-sibling::span"))).click();
		logger.info("Reject button is clicked");
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		wait.until(ExpectedConditions.urlContains(EMPLOYEE_BASE_URL+"/super/assigned"));	
		Thread.sleep(2000);
	}
	
	/**
	 * Common method to 'Reopen service request' on employee dashboard
	 * @param searchResult
	 * @throws InterruptedException 
	 */
	public void reopenServiceRequest() throws InterruptedException{
		logger.info("Reopen service request under process !!");    
	    WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//table[@class='table-box']/tbody/tr[1]/td[1]//p[@class='titleLink ']"))).click();
		logger.info("Title link is clicked !!");
		wait.until(ExpectedConditions.urlContains(EMPLOYEE_BASE_URL+"/super/service-request"));	
		Thread.sleep(2000);
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("*//button//span[text()=' Reopen ']/preceding-sibling::span"))).click();
		logger.info("Reopen button is clicked !!");
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='modal-window']")));
	    logger.info("Reopen action is clicked....Navigating to 'Reopen Request' screen");
	    logger.info("Modal window title is: "+driver.findElement(By.xpath("//div[@class ='modal-window'] /div/h2")).getText());
		driver.findElement(By.xpath("*//button//span[text()='Reopen']/preceding-sibling::span")).click();
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		wait.until(ExpectedConditions.urlContains(EMPLOYEE_BASE_URL+"/super/service-request"));	
		Thread.sleep(2000);
	}
	
	/**
	 * Common method to 'Submit service request' on employee dashboard
	 * @param searchResult
	 * @throws InterruptedException 
	 */
	public void submitServiceRequest() throws InterruptedException{
		logger.info("Submit service request under process !!");    
	    WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//table[@class='table-box']/tbody/tr[1]/td[1]//p[@class='titleLink ']"))).click();
		logger.info("Title link is clicked !!");
		wait.until(ExpectedConditions.urlContains(EMPLOYEE_BASE_URL+"/super/service-request"));	
		Thread.sleep(2000);
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("*//button//span[text()='Build Case ']/preceding-sibling::span"))).click();
		wait.until(ExpectedConditions.urlContains("/super/build-case"));
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(Constants.IMPLICIT_WAIT, TimeUnit.SECONDS);
		logger.info("Build Case button is clicked");
		Thread.sleep(2000);
		wait.until(ExpectedConditions.elementToBeClickable(clientCaseID)).sendKeys(testProp.getString("clientCaseID"));
		wait.until(ExpectedConditions.elementToBeClickable(diagnosisCategory)).sendKeys(testProp.getString("diagnosisCategory"));
		wait.until(ExpectedConditions.elementToBeClickable(reviewCategory)).click();
		wait.until(ExpectedConditions.elementToBeClickable(reviewCategoryOptions)).click();
		wait.until(ExpectedConditions.elementToBeClickable(expediteCode)).click();
		wait.until(ExpectedConditions.elementToBeClickable(expediteCodeOptions)).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tc-textarea[@formcontrolname='handlingNotes']"))).click();
		handlingNotes.sendKeys("\n");
		handlingNotes.sendKeys(testProp.getString("handlingNotes"));
		wait.until(ExpectedConditions.elementToBeClickable(preferredSpecialty)).sendKeys(testProp.getString("preferredSpecialty"));
		wait.until(ExpectedConditions.elementToBeClickable(preferredSpecialtyAlternative)).sendKeys(testProp.getString("preferredSpecialtyAlternative"));
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tc-textarea[@formcontrolname='reviewInstructions']"))).click();
		reviewInstruction.sendKeys("\n");
		reviewInstruction.sendKeys(testProp.getString("reviewInstruction"));
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tc-textarea[@formcontrolname='reviewQuestionsForReview']"))).click();
		questionsForReview.sendKeys("\n");
		questionsForReview.sendKeys(testProp.getString("questionsForReview"));
		Thread.sleep(2000);
		wait.until(ExpectedConditions.elementToBeClickable(cptCodes)).sendKeys(testProp.getString("cptCodes"));
		wait.until(ExpectedConditions.elementToBeClickable(icdCodes)).sendKeys(testProp.getString("icdCodes"));
		wait.until(ExpectedConditions.elementToBeClickable(saveAndSubmitButton)).click();
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		Thread.sleep(4000);	
		wait.until(ExpectedConditions.urlContains(EMPLOYEE_BASE_URL+"/super/service-request"));
		Thread.sleep(2000);	
	}
	
	/**
	 * Common method to 'Archive closed service request' on employee dashboard
	 * @param searchResult
	 * @throws InterruptedException 
	 */
	public void archiveServiceRequest(WebElement searchResult) throws InterruptedException{
		logger.info("Archive service request under process !!");    
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//table[@class='table-box']/tbody/tr[1]/td[1]/a/p[@class='titleLink']"))).click();
		logger.info("Title link is clicked !!");
		wait.until(ExpectedConditions.urlContains(EMPLOYEE_BASE_URL+"/super/service-request"));	
		Thread.sleep(2000);
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("*//button//span[text()=' Archive ']/preceding-sibling::span"))).click();
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='modal-window']")));
		logger.info("Archive button is clicked !!");
	    logger.info("Modal window title is: "+driver.findElement(By.xpath("//div[@class ='modal-window'] /div/h2")).getText());
		driver.findElement(By.xpath("*//button//span[text()='Archive']/preceding-sibling::span")).click();
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		wait.until(ExpectedConditions.urlContains(EMPLOYEE_BASE_URL+"/super/service-request"));	
		Thread.sleep(2000);
	}	
	
	/* Method which will be called in every test case 'After Method' to end report generation. */
	public void endTest() {
		extent.endTest(extentTest);
		extent.flush();
	}
	
	/**
	 * Common method for emp login used while new service request creation and status updation
	 * @param empRole
	 * @param empNum
	 * @param is2FAOpted
	 * @param connection
	 * @throws SQLException
	 * @throws InterruptedException
	 */
	public void attemptEmployeeLogin(String empRole, int empNum, boolean is2FAOpted, Connection connection) throws SQLException, InterruptedException{
		//List<String> is super class of all list i.e. ArrayList, SetList etc. so, we used List<String> instead of ArrayList<String>
		List<String> tabs = new ArrayList<String>(driver.getWindowHandles()); 
		logger.info("Switch to login page of "+ empRole); 
		driver.switchTo().window(tabs.get(1)); 	
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		WebDriverWait wait = new WebDriverWait(driver,20);
		wait.until(ExpectedConditions.urlContains(EMPLOYEE_BASE_URL+"/login"));
		logger.info(empRole + " navigated to Login screen");
		String email = testProp.getString(empRole + "_" + empNum + "_email_address");
		String password = testProp.getString(empRole + "_" + empNum + "_password");
		wait.until(ExpectedConditions.elementToBeClickable(EmployeeDashboardPage.signInWithMicrosoftButton)).click();
		logger.info(empRole + " clicked on Microsoft Sign In button");
		tabs = new ArrayList<String>(driver.getWindowHandles()); 
		driver.switchTo().window(tabs.get(2));
		logger.info("Switch to Microsoft window to provide credentials");
		driver.manage().timeouts().pageLoadTimeout(Constants.IMPLICIT_WAIT, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		try{
			logger.info(empRole + " is providing Microsoft credentials !!");
			wait.ignoring(StaleElementReferenceException.class).until(ExpectedConditions.visibilityOf(emailAddressField)).sendKeys(email);
			nextButton.click();
			wait.ignoring(StaleElementReferenceException.class).until(ExpectedConditions.visibilityOf(passwordField)).sendKeys(password);
			signInButton.click();
		}
		catch (NoSuchWindowException e) {
			logger.info(e.getMessage());
			wait.until(ExpectedConditions.elementToBeClickable(EmployeeDashboardPage.signInWithMicrosoftButton)).click();
			logger.info(empRole + " clicked on Microsoft Sign In button");
			tabs = new ArrayList<String>(driver.getWindowHandles()); 
			driver.switchTo().window(tabs.get(2));
			logger.info("Switch to Microsoft window to provide credentials");
			driver.manage().timeouts().pageLoadTimeout(Constants.IMPLICIT_WAIT, TimeUnit.SECONDS);
			driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
			logger.info(empRole + " is providing Microsoft credentials !!");
			wait.ignoring(StaleElementReferenceException.class).until(ExpectedConditions.visibilityOf(emailAddressField)).sendKeys(email);
			nextButton.click();
			wait.ignoring(StaleElementReferenceException.class).until(ExpectedConditions.visibilityOf(passwordField)).sendKeys(password);
			signInButton.click();
		}
		driver.switchTo().window(tabs.get(1));
		if (is2FAOpted) {
			logger.info("Waiting for OTP screen");
			wait.until(ExpectedConditions.visibilityOf(otpField));
			wait = submitOTP(connection,email,null,driver);
			if(null==wait){
				String errorMessage = "OTP not available for email address: "+email;
				extentTest.log(LogStatus.FAIL, "OTP not available for email address: "+email);
				logger.info("caseManagerLogin_WithValidOTPTest method failed as OTP is not available for email address: "+email);
				Assert.fail(errorMessage);
				}
		}	
			wait.until(ExpectedConditions.urlContains("/super/dashboard"));
			logger.info("Navigated to Employee dashboard section");
			driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
			Thread.sleep(2000);
	}

}
