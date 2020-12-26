package com.MRIoA.qa.pageObjects;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import com.MRIoA.qa.BaseClass;
import com.MRIoA.qa.utilities.Constants;
import com.MRIoA.qa.utilities.Constants.REQUEST_STATUS;


public class PatientDashboardPage extends BaseClass {
	private static Logger logger = Logger.getLogger(PatientDashboardPage.class);
	
	@FindBy(xpath="*//a//span[contains(text(),'Service Requests')]")
	WebElement serviceRequestLink;
	
	@FindBy(xpath="*//a//span[contains(text(),'Create Service Request')]")
	public WebElement createServiceRequestLink;
	
	@FindBy(xpath= "*//input[contains(@placeholder,'Search')]")
	public WebElement searchFunctioanlity;
	
	@FindBy(xpath= "//div[text()='No Service Request Created']")
	public WebElement textWhenNoSRIsCreated;
	
	/*----------------Object Repository of fields on Create Service Request screen---------------------*/
	
	@FindBy(xpath="//input[@placeholder='Enter Request Title']")
	WebElement requestTitle;
	
	@FindBy(xpath="//button[.//span[contains(text(),' Next ')]]")
	WebElement nextButton;
	
	@FindBy(xpath="//input[@placeholder='Doctor Name']")
	WebElement doctorName;
	
	@FindBy(xpath="//input[@formcontrolname='doctorContactNumber']")
	WebElement doctorContactNumber;
	
	@FindBy(xpath="/html/body/app-root/vertical-layout/div/main/div/service-request/div[2]/form/tc-card[1]/div[2]/div[2]/div/tc-form-group/div/input")
	WebElement uploadButton_Doctor;
	
	@FindBy(xpath="//textarea[@placeholder=' Doctors Comments']")
	WebElement doctorComments;
	
	@FindBy(xpath="//div[@class='tc-radio-handle']/following-sibling::div[text()='Yes']")
	WebElement checkPrimaryCareAsYes;
	
	@FindBy(xpath="//input[@placeholder='Primary Care Provider Name']")
	WebElement careProviderName;
	
	@FindBy(xpath="//input[@formcontrolname='careContactNumber']")
	WebElement careProviderContactNumber;
	
	@FindBy(xpath="/html/body/app-root/vertical-layout/div/main/div/service-request/div[2]/form/tc-card[2]/div[2]/div/div[2]/div/tc-form-group/div/input")
	WebElement uploadButton_CareProvider;
	
	@FindBy(xpath="//textarea[@placeholder=' Care Provided']")
	WebElement careProviderComments;
	
	@FindBy(xpath="//textarea[@placeholder=' Medical Conditions']")
	WebElement medicalCondition;
	
	@FindBy(xpath="//textarea[@placeholder=' Surgical History']")
	WebElement surgicalHistory;
	
	@FindBy(xpath="//textarea[@placeholder=' Medication list']")
	WebElement medicationList; 
	
	@FindBy(xpath="//textarea[@placeholder=' Allergies']")
	WebElement allergies;

	@FindBy(xpath="//textarea[@placeholder=' Family History']")
	WebElement familyHistory;
	
	@FindBy(xpath="/html/body/app-root/vertical-layout/div/main/div/service-request/div[2]/form/tc-card/div[2]/div/div/tc-form-group/tc-radio/tc-radio-option[1]/label/div[1]")
	WebElement smokingStatusAsYes;
	
	@FindBy(xpath="*//tc-select[@formcontrolname='smokingFrom']//div[text()=' Select Year ']")
	WebElement smokingFromDropDown;

	@FindBy(xpath="//div[@class='options']/div[20]")
	WebElement smokingYear;
	
	@FindBy(xpath="//textarea[@placeholder=' Handling Notes']")
	WebElement handlingNotes;
	
	@FindBy(xpath="//textarea[@placeholder=' Review Instruction']")
	WebElement reviewInstruction;
	
	@FindBy(xpath="//textarea[@placeholder=' Questions For Review']")
	WebElement questionsForReview;
	
	@FindBy(xpath="*//div[@class='tc-checkbox-handle']")
	WebElement checkAcceptTerms;
	
	@FindBy(xpath="//button[.//span[contains(text(),' Submit ')]]")
	WebElement submitButton;
	
	@FindBy(id="master") //if default won't work, use id="master"
	WebElement loader;
	
	@FindBy(xpath="*//all-request/tc-card/div//div")
	WebElement messageNoSRAvailable;
	
	@FindBy(xpath="*//div[@aria-labelledby='dropdownBasic2']")
	WebElement actionsPopUp;
	
	/**
	 * Method to wair for the loader to disappear
	 * @param loader
	 */
	public static void loadingWait(WebElement loader) {
	    WebDriverWait wait = new WebDriverWait(driver, 5);
	    //wait.until(ExpectedConditions.visibilityOf(loader)); // wait for loader to appear
	    wait.until(ExpectedConditions.invisibilityOf(loader)); // wait for loader to disappear
	}
	
	public PatientDashboardPage(){
		super();
		logger.info("In PatientDashboardPage constructor");
		PageFactory.initElements(driver, this);	
	}
	
	/* Method which will be called in every test case to start report generation. This methos needs to be placed in every page object class   */
	public void startTest(String testcaseName) {
		extentTest = extent.startTest(testcaseName);
	}	

	//Actions	
	/**
	 * Common method to get 'Table Rows'
	 */
	public void getTableRows(){
		// Finding number of rows in a web table. We need to exclude header to get actual number of data rows
		List<WebElement> allRows= driver.findElements(By.xpath("//table[@class='table-box']/tbody/tr"));
		int rowsCount = allRows.size();
		logger.info("Total rows found in table is: "+rowsCount);
	}
	
	/**
	 * Common method to print 'First Row' of table
	 */
	public void printFirstRow(){
		// Print data displaying in first row of table
		logger.info("Printing column values(data) of first row of table: ");
		List<WebElement> dataOfLastRow= driver.findElements(By.xpath("//table[@class='table-box']/tbody/tr[1]/td"));
		for(WebElement e:dataOfLastRow)
		{
			logger.info(e.getText());
		}
	}
	
	/**
	 * Common method to print 'Last Row' of table
	 */
	public void printLastRow(){
		// Print data displaying in last row of table
		logger.info("Printing column values(data) of last row of table: ");
		List<WebElement> dataOfLastRow= driver.findElements(By.xpath("//table[@class='table-box']/tbody/tr[last()]/td"));
		for(WebElement e:dataOfLastRow)
		{
			logger.info(e.getText());
		}
	}
	
	/**
	 * Common method to print 'Nth Row' of table
	 */
	public void printNthRow(int rowNum){
		// Print data displaying in defined row of table
				logger.info("Printing column values(data) of "+rowNum+ " row of table: ");
				List<WebElement> dataOfLastRow= driver.findElements(By.xpath("//table[@class='table-box']/tbody/tr["+rowNum+"]/td"));
				for(WebElement e:dataOfLastRow)
				{
					System.out.println(e.getText());
				}
	}
	
	/**
	 * Common method to retrieve title of SR for specified status
	 */
	public void retrieveTitlesWithStatus(REQUEST_STATUS reqStatusType){
		String reqStatus = reqStatusType.name();
		// Retrieve request titles with status
		logger.info("Retrieve Service Request(s) title with status : "+reqStatus);
		List<WebElement> getRequestTitles =driver.findElements(By.xpath("//table[@class='table-box']/tbody/tr/td/tc-badge[text()=' "+reqStatus+"']/../../td[1]"));
		logger.info("Count of Request Titles(s) with status 'New' is: "+(getRequestTitles.size()));
		for(WebElement e: getRequestTitles)
		{
			logger.info(e.getText());
		}
	}
	
	/**
	 * Common method to RETRIEVING CELL VALUE OF SPECIFIC COLUMN OF SPECIFIC ROW
	 */
	public static void getColValue(int row, int col)
	{
		
	   logger.info(" ");
	   WebElement colValue= driver.findElement(By.xpath("//table[@class='table-box']/tbody/tr["+row+"]/td["+col+"]"));
	   logger.info(colValue.getText());				
	}
	 
	/**
	 * Common method to retrieve data of rows on page no. 1 only
	 */
	public void getAllRowsData(){
		// Grab the table 
		WebElement table = driver.findElement(By.xpath("//table[@class='table-box']")); 
		// Now get all the TR elements from the table 
		List<WebElement> allRows = table.findElements(By.tagName("tr")); 
		// And iterate over them, getting the cells 
		logger.info("Data for all rows of the table is as below: ");
		for (WebElement row : allRows) { 
		    List<WebElement> cells = row.findElements(By.tagName("td")); 
		    // Print the contents of each cell
		    for (WebElement cell : cells) { 
		        System.out.println(cell.getText());
		    }	
		}
	}
	
	/**
	 * Common method to search SR with specified title
	 * @throws InterruptedException
	 */
	public void searchSpecificServiceRequest(String title) throws InterruptedException{
		searchFunctioanlity.click();
		searchFunctioanlity.sendKeys(title);
		Thread.sleep(6000);
		List<WebElement> allRows= driver.findElements(By.xpath("//table[@class='table-box']/tbody/tr/td[1]"));
		boolean b = allRows.size() >0;
		if (b){
			logger.info("Total rows found in table for the searched title "+ title+ " is: "+(allRows.size()));
		}
		else {
			String message = messageNoSRAvailable.getText();
			logger.info(message);
		}
	}
	
	/**
	 * Common method to 'Create New Service Request'
	 * @param role
	 * @param serviceReqNum
	 * @param patientNum
	 * @throws InterruptedException
	 */
	public void createNewServiceRequest(String role, int serviceReqNum, int patientNum) throws InterruptedException{
		WebDriverWait wait = new WebDriverWait(driver,10);
		wait.until(ExpectedConditions.visibilityOf(createServiceRequestLink));
		wait.until(ExpectedConditions.elementToBeClickable(createServiceRequestLink)).click();
		wait.until(ExpectedConditions.urlContains("/user/service/service-request"));
		//Patient & Insurance screen
		logger.info(role+" no. "+patientNum+" is providing details for creating new service request num "+serviceReqNum);
		System.out.println("value is ====== "+testProp.getString("request_title_"+serviceReqNum));
		requestTitle.sendKeys(testProp.getString("request_title_"+serviceReqNum));
		driver.manage().timeouts().pageLoadTimeout(Constants.IMPLICIT_WAIT, TimeUnit.SECONDS);
		wait.until(ExpectedConditions.elementToBeClickable(nextButton)).click();
		Thread.sleep(2000);
		//Doctor Info & Care
		logger.info(role+" no. "+patientNum+" is providing details of doctor");
		wait.until(ExpectedConditions.visibilityOf(doctorName)).sendKeys(testProp.getString("doctor_name_"+serviceReqNum));
		wait.until(ExpectedConditions.visibilityOf(doctorContactNumber)).sendKeys(testProp.getString("doctor_contactNumber_"+serviceReqNum));
		//upload file
		//wait.until(ExpectedConditions.visibilityOf(uploadButton_Doctor));
		//Utilities.uploadDocument("F:\\Downloads\\samplePdf.pdf");
		wait.until(ExpectedConditions.visibilityOf(doctorComments)).sendKeys(testProp.getString("doctor_Comments_"+serviceReqNum));
		//providing details for primary care
		logger.info(role+" no. "+patientNum+" is providing details of primary care provider");
		wait.until(ExpectedConditions.elementToBeClickable(checkPrimaryCareAsYes)).click();
		Thread.sleep(2000);
		wait.until(ExpectedConditions.visibilityOf(careProviderName)).sendKeys(testProp.getString("primarycare_name_"+serviceReqNum));
		wait.until(ExpectedConditions.visibilityOf(careProviderContactNumber)).sendKeys(testProp.getString("primarycare_number_"+serviceReqNum));
		//upload file
		//wait.until(ExpectedConditions.visibilityOf(uploadButton_CareProvider));
		//Utilities.uploadDocument("F:\\Downloads\\sampleWord.doc");
		wait.until(ExpectedConditions.visibilityOf(careProviderComments)).sendKeys(testProp.getString("primarycare_comments_"+serviceReqNum));
		wait.until(ExpectedConditions.elementToBeClickable(nextButton)).click();
		//Diagnosis/Symptoms/Medical Conditions
		logger.info(role+" no. "+patientNum+" is providing details of Diagnosis/Symptoms");
		wait.until(ExpectedConditions.visibilityOf(medicalCondition)).sendKeys(testProp.getString("medical_conditions_"+serviceReqNum));
		wait.until(ExpectedConditions.visibilityOf(surgicalHistory)).sendKeys(testProp.getString("surgical_history_"+serviceReqNum));
		wait.until(ExpectedConditions.visibilityOf(medicationList)).sendKeys(testProp.getString("medication_list_"+serviceReqNum));
		wait.until(ExpectedConditions.visibilityOf(allergies)).sendKeys(testProp.getString("allergies_"+serviceReqNum));
		wait.until(ExpectedConditions.visibilityOf(familyHistory)).sendKeys(testProp.getString("family_history_"+serviceReqNum));
		wait.until(ExpectedConditions.elementToBeClickable(smokingStatusAsYes)).click();	
		wait.until(ExpectedConditions.elementToBeClickable(smokingFromDropDown)).click();
		wait.until(ExpectedConditions.elementToBeClickable(smokingYear)).click();
		Thread.sleep(2000);
		driver.manage().timeouts().pageLoadTimeout(Constants.IMPLICIT_WAIT, TimeUnit.SECONDS);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tc-textarea[@formcontrolname='handlingNotes']"))).click();
		handlingNotes.sendKeys(testProp.getString("handlingNotes_"+serviceReqNum));
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tc-textarea[@formcontrolname='reviewInstructions']"))).click();
		reviewInstruction.sendKeys(testProp.getString("reviewInstruction_"+serviceReqNum));
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tc-textarea[@formcontrolname='reviewQuestionsForReview']"))).click();
		questionsForReview.sendKeys(testProp.getString("questionsForReview_"+serviceReqNum));
		Thread.sleep(2000);
		wait.until(ExpectedConditions.elementToBeClickable(checkAcceptTerms)).click();
		//It is a modal window, not alert. so we don't need to switch here
		logger.info("Modal window title is: "+driver.findElement(By.className("modal-header")).getText());
		Thread.sleep(2000);
		driver.findElement(By.xpath("*//button//span[text()='Accept']/preceding-sibling::span[@class='btn-line']")).click();
		wait.until(ExpectedConditions.elementToBeClickable(submitButton)).click();
		logger.info("Validating is service request created or not on patient dashboard ?");	
	}
	
	/**
	 * Common method to search the request with same name and pick the top row only from list of same title rows
	 * @param title
	 * @param rowNumber
	 * @throws InterruptedException
	 */
	public WebElement searchRequest(String title, int rowNumber) throws InterruptedException{
		
		int index = rowNumber-1;
			Assert.assertTrue(index>=0,"Row found as index value is: "+index);
		
		WebDriverWait wait = new WebDriverWait(driver,20);
		wait.until(ExpectedConditions.visibilityOf(searchFunctioanlity));
		wait.until(ExpectedConditions.elementToBeClickable(searchFunctioanlity)).click();
		Thread.sleep(2000);
		searchFunctioanlity.sendKeys(title);
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		Thread.sleep(3000);
		try{
			WebElement baseTableBody = driver.findElement(By.xpath("//table[@class='table-box']/tbody"));
		List<WebElement> tableRows = baseTableBody.findElements(By.tagName("tr"));
		if (tableRows !=null && tableRows.size() >=rowNumber ){
			logger.info(tableRows.get(index).getText());
			return tableRows.get(index);
		}
		}
		catch (NoSuchElementException e) {
			logger.info(e.getMessage());
			return null;
		}
		return null;
	}
	
	/**
	 * Common method to check actions available with service request on patient dashboard
	 * @param searchResult
	 * @throws InterruptedException
	 */
	public void checkActionsOnServiceRequest(WebElement searchResult) throws InterruptedException {
		logger.info("Verify actions available on service request"); 
		WebDriverWait wait = new WebDriverWait(driver, 5);
		WebElement element = searchResult.findElement(By.xpath("//td[6]//div/button[@id='dropdownBasic2']/span[@class='btn-line']"));
		wait.until(ExpectedConditions.elementToBeClickable(element)).click();
		logger.info("Action button has been clicked !!");
		wait.until(ExpectedConditions.visibilityOf(actionsPopUp));
		Actions a = new Actions(driver);
        a.moveToElement(actionsPopUp).build().perform();
        Thread.sleep(2000);
        //wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//td[7]/div/div/div/button")));
			List<WebElement> getActions = driver.findElements(By.xpath("*//tr[1]/td[6]//div[@aria-labelledby='dropdownBasic2']/button"));
				logger.info("Count of actions is: " + (getActions.size()));
				logger.info("Actions names is as below: ");
				for(int i=1;i<=getActions.size();i++){
					String action = driver.findElement(By.xpath("*//tr[1]/td[6]//div[@aria-labelledby='dropdownBasic2']/button["+i+"]")).getText();
					logger.info("Action "+ i+" is: "+action);
				}	
				//driver.findElement(By.xpath("*//button[@id='dropdownBasic2' and @aria-expanded='true']/span[@class='btn-line']")).click();
				Thread.sleep(2000);
	}	
	
	/**
	 * Common method to 'View service request' on patient dashboard
	 * @param searchResult
	 * @throws InterruptedException
	 */
	public void viewServiceRequest(WebElement searchResult) throws InterruptedException{
		logger.info("View service request");    
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//td[6]//div/button[@id='dropdownBasic2']/span[@class='btn-line']")));
		searchResult.findElement(By.xpath("//td[6]//div/button[@id='dropdownBasic2']/span[@class='btn-line']")).click();
		logger.info("Action button has been clicked !!");
		wait.until(ExpectedConditions.visibilityOf(actionsPopUp));
		Actions a = new Actions(driver);
	    a.moveToElement(actionsPopUp).build().perform();
	    Thread.sleep(2000);
	    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("*//tr[1]/td[6]//div[@aria-labelledby='dropdownBasic2']/button[1]"))).click();
	    logger.info("View action is clicked....Navigating to 'View Request' screen");
	    wait.until(ExpectedConditions.urlContains(PATIENT_BASE_URL+"/user/service/view-service-request"));
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
		//jsExecuter.executeScript("window.scrollTo(0,document.body.scrollHeight)");
		Thread.sleep(2000);
		logger.info("Scroll back to the top of the page");
		jsExecuter.executeScript("window.scrollTo(0,document.body.scrollTop)");
		Thread.sleep(5000);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("*//button[@title='Back']"))).click();
		logger.info("Back button is clicked and navigate back to dashboard");
		wait.until(ExpectedConditions.urlContains(PATIENT_BASE_URL+"/user/service/dashboard"));	
		Thread.sleep(2000);
	}
	
	/**
	 * Common method to 'Edit service request'
	 * @param searchResult
	 * @throws InterruptedException
	 */
	public void editServiceRequest(WebElement searchResult) throws InterruptedException {
		logger.info("Edit service request"); 
		logger.info("Waiting for 1st row of table to be inspected");
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//td[6]//div/button[@id='dropdownBasic2']/span[@class='btn-line']")));
		searchResult.findElement(By.xpath("//td[6]//div/button[@id='dropdownBasic2']/span[@class='btn-line']")).click();
		logger.info("Action button has been clicked !!");
		wait.until(ExpectedConditions.visibilityOf(actionsPopUp));
		Actions a = new Actions(driver);
		a.moveToElement(actionsPopUp).build().perform();
		Thread.sleep(2000);
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("*//tr[1]/td[6]//div[@aria-labelledby='dropdownBasic2']/button[2]"))).click();
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(Constants.IMPLICIT_WAIT, TimeUnit.SECONDS);
		wait.until(ExpectedConditions.urlContains(PATIENT_BASE_URL+"/user/service/view-service-request"));
		logger.info("Edit action is clicked....Navigating to 'Edit Request' screen");
		Thread.sleep(2000);
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("*//ngb-accordion/div[1]/div"))).click();
	    Thread.sleep(2000);
		WebElement doctorAccordian= driver.findElement(By.xpath("*//ngb-accordion/div[3]/div"));
		wait.until(ExpectedConditions.elementToBeClickable(doctorAccordian)).click();
		logger.info("Doctor Info and Care------accordian is clicked by patient");
		//webelement to be edited
		WebElement doctorName = driver.findElement(By.xpath("*//input[@id='doctorName']"));
		wait.until(ExpectedConditions.visibilityOf(doctorName));
		 wait.until(ExpectedConditions.elementToBeClickable(doctorName));
		doctorName.clear();
		doctorName.sendKeys("Lalit Singh");
	    ((JavascriptExecutor) driver).executeScript("window.scrollTo(0,document.body.scrollTop)");
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("*//button//span[text()=' Save Changes ']/preceding-sibling::span")));
	    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("*//button//span[text()=' Save Changes ']/preceding-sibling::span"))).click();
	 	wait.until(ExpectedConditions.urlContains(PATIENT_BASE_URL+"/user/service/dashboard"));	
	 	driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
	 	Thread.sleep(2000);
	    }
	
	/**
	 * Common method to 'Delete service request'
	 * @param searchResult
	 * @throws InterruptedException
	 */
	public void deleteServiceRequest(WebElement searchResult) throws InterruptedException{
		logger.info("Delete service request");                                                                                                                                           	
		logger.info("Waiting for 1st row of table to be inspected");
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//td[6]//div/button[@id='dropdownBasic2']/span[@class='btn-line']")));
		searchResult.findElement(By.xpath("//td[6]//div/button[@id='dropdownBasic2']/span[@class='btn-line']")).click();
		logger.info("Action button is clicked");
		wait.until(ExpectedConditions.visibilityOf(actionsPopUp));
		Actions a = new Actions(driver);
		a.moveToElement(actionsPopUp).build().perform();
		Thread.sleep(2000);
		//wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@id='dropdownBasic2']/following-sibling::div[contains(@class, 'dropdown-menu show')]/button[3]"))).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("*//div[@aria-labelledby='dropdownBasic2']/button[3]"))).click();
		logger.info("Delete action is clicked");
		Thread.sleep(2000);
		//It is a modal window, not alert. so we don't need to switch here
		logger.info("Modal window title is: "+driver.findElement(By.className("modal-header")).getText());
		driver.findElement(By.xpath("*//button//span[text()=' Yes ']/preceding-sibling::span[@class='btn-line']")).click();
		wait.until(ExpectedConditions.urlContains(PATIENT_BASE_URL+"/user/service/dashboard"));		
		Thread.sleep(2000);
	}

	/* Method which will be called in every test case 'After Method' to end report generation. */
	public void endTest() {
		extent.endTest(extentTest);
		extent.flush();
	}

}




	
	


