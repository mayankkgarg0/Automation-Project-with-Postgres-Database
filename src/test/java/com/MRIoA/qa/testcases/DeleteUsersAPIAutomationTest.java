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
import org.testng.annotations.Test;
import com.MRIoA.qa.BaseClass;
import com.MRIoA.qa.DataBaseConnectionFactory;
import com.MRIoA.qa.pageObjects.LoginPage;
import com.MRIoA.qa.utilities.Constants;
import com.relevantcodes.extentreports.LogStatus;
import static io.restassured.RestAssured.*;

public class DeleteUsersAPIAutomationTest extends BaseClass {
	LoginPage loginPage;
	static Connection connection;
	private static Logger logger = Logger.getLogger(DeleteUsersAPIAutomationTest.class);
	
	public DeleteUsersAPIAutomationTest(){
		logger.info("In DeleteUsersAPIAutomationTest constructor");
	}
	
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
	
	@Test(priority=26, description="Delete users after test suite run")
	public static void deleteUsersAPIAutomation () throws InterruptedException{
		
		/*----1st Step: Create different variables and specify the value to be deleted.----------*/
		logger.info("Users deletion through API automation under process !!");
		String emailAddressUser1 = testProp.getString("patient_1_email_address");
		String emailAddressUser2 = testProp.getString("patient_2_email_address");
		String emailAddressUser3 = testProp.getString("patient_3_email_address");
	  //String emailAddressUser4 = testProp.getString("patient_4_googleEmailAddress");
		
		/*----2nd Step: Specify a Request pointing to the Service Endpoint.----*/
		//RestAssured.baseURI = EMPLOYEE_BASE_URL+"/api/v1/employee/user-management";
		//RestAssured.given().
		when().
			post(EMPLOYEE_BASE_URL+"/api/v1/employee/user-management/delete/"+emailAddressUser1).
		then().
			statusCode(200);
		logger.info("Patient with email address:--->"+emailAddressUser1+" deleted successfully !!");
		
		//RestAssured.given().
		when().
			post(EMPLOYEE_BASE_URL+"/api/v1/employee/user-management/delete/"+emailAddressUser2).
		then().
			statusCode(200);
		logger.info("Patient with email address:--->"+emailAddressUser2+" deleted successfully !!");
		
		//RestAssured.given().
		when().
			post(EMPLOYEE_BASE_URL+"/api/v1/employee/user-management/delete/"+emailAddressUser3).
		then().
			statusCode(200);
		logger.info("Patient with email address:--->"+emailAddressUser3+" deleted successfully !!");
	}
	
	@Test(priority = 27, description="LoginTest after user deletion")
	public void LoginTest_AfterUserDeletion() throws InterruptedException {
		driver.get(PATIENT_BASE_URL);
		WebDriverWait wait = new WebDriverWait(driver,10);
		wait.until(ExpectedConditions.urlContains("/user/login"));
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(Constants.IMPLICIT_WAIT, TimeUnit.SECONDS);
		loginPage.startTest("LoginTest_AfterUserDeletion"); 
		int patientNum = 1;
		logger.info("Running LoginTest_AfterUserDeletion method for patient no. "+patientNum);
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div/input[@class='input-control ng-pristine ng-valid ng-touched' or @placeholder='Email Address']"))).sendKeys(testProp.getString("patient_"+patientNum+"_email_address"));
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div/input[@class='input-control ng-untouched ng-pristine ng-valid' or @placeholder='Password']"))).sendKeys(testProp.getString("patient_"+patientNum+"_password"));
		Thread.sleep(2000);
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div/button/span[@class='btn-line']"))).click();	
		logger.info("Login button is clicked");
		wait.until(ExpectedConditions.urlContains("/user/login"));
		String actualUrl = driver.getCurrentUrl();
		String expectedUrl =PATIENT_BASE_URL+"/user/login";
		if (actualUrl.equals(expectedUrl)) {
			extentTest.log(LogStatus.PASS, "LoginTest_AfterUserDeletion method is passed !!");
			logger.info("Patient no. "+patientNum+" not navigated to dashboard !!");
		}
		else{
			extentTest.log(LogStatus.FAIL, "LoginTest_AfterUserDeletion method is failed !!");
			logger.info("Patient no. "+patientNum+" not navigated to dashboard !!");
		}
	}
	
	@AfterMethod
	public void tearDown() {
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
