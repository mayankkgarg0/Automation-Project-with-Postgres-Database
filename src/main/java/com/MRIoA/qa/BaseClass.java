package com.MRIoA.qa;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import com.MRIoA.qa.utilities.Constants;
import com.MRIoA.qa.utilities.SystemConstants;
import com.MRIoA.qa.utilities.Utilities;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

//Base class is parent class for all pages/.java classes
public class BaseClass {
	// global variables
	public static WebDriver driver;
	public static PropertiesConfiguration testProp;
	public static String PATIENT_BASE_URL;
	public static String EMPLOYEE_BASE_URL;
	public static Logger logger = Logger.getLogger(BaseClass.class);
	//public static Sheet testDataSheet = null;
	
	/*---------- Extent Report Configuration starts -------------------*/
	
	@SuppressWarnings("all")
	public static ExtentReports extent = extent = new ExtentReports(
			System.getProperty("user.dir") +"\\test-output\\MRIOA Patient Care Report.html", true);
	public static ExtentTest extentTest;
	
	/**
	 * Method in Base Class which is used to provide status of test case in report generated i.e. PASS,FAIL,SKIP
	 * @param status
	 * @param description
	 */
	public void Report(String status, String description) {
		if (status.equalsIgnoreCase("Pass")) {
			extentTest.log(LogStatus.PASS, description);
		} else if (status.equalsIgnoreCase("Fail")) {
			extentTest.log(LogStatus.FAIL, description);
		} else if (status.equalsIgnoreCase("Unknown")) {
			extentTest.log(LogStatus.FAIL, description);
		} else if (status.equalsIgnoreCase("Skip")) {
			extentTest.log(LogStatus.SKIP, description);
		} else
			extentTest.log(LogStatus.INFO, description);
	}
	/*---------- Extent Report Configuration ends -------------------*/
	
	/**
	 * static block will be load once when any class that extents Base class will be loaded. This is used to load Properties Config file
	 */
	static{
		//testDataSheet = readExcel("src\\main\\java\\com\\MRIoA\\qa\\testdata\\TestData.xlsx");
		//load Property file using PropertyConfigurator
		PropertyConfigurator.configure("log4j.properties");
		logger.info("Running Base Class static block");
            try {
            	testProp = new PropertiesConfiguration("src\\main\\java\\com\\MRIoA\\qa\\testdata\\TestData.properties");
            	
			} catch (ConfigurationException e) {
				e.printStackTrace();
			}
	}

	/**
	 * Common method in Base Class to initialize the WebDriver
	 * @throws InterruptedException
	 */
	public static void initializingDriver() throws InterruptedException {

		String testEnv = SystemConstants.TEST_ENV;

		if (null == testEnv || testEnv.isEmpty()) {
			throw new RuntimeException("Test Environment is missing under System Environments");
		}

		String browserName = SystemConstants.TEST_BROWSER;
		if (null == browserName || browserName.isEmpty()) {
			browserName = "Chrome";
		}

		if (Constants.BROWSER.CHROME.name().toLowerCase().equalsIgnoreCase(browserName)) {
			System.setProperty("webdriver.chrome.silentOutput", "true");//to avoid chrome warning displayed in console on every test case run before browser launch
			System.setProperty("webdriver.chrome.driver", "drivers\\chromedriver.exe");
			// to open chrome browser in incognito mode
			ChromeOptions options = new ChromeOptions();
			options.addArguments("incognito");
			options.addArguments("enable-automation");
			options.addArguments("disable-infobars");
			//options.setPageLoadStrategy(PageLoadStrategy.NONE);//to avoid chrome warnings
			driver = new ChromeDriver(options);

		} else if (Constants.BROWSER.FIREFOX.name().toLowerCase().equalsIgnoreCase(browserName)) {
			System.setProperty("webdriver.gecko.driver", "drivers\\geckodriver.exe");
			// to open firefox browser in incognito mode
			FirefoxOptions firefox_options = new FirefoxOptions();
			firefox_options.addArguments("-private-window");
			driver = new FirefoxDriver(firefox_options);

		} else if (Constants.BROWSER.IE.name().toLowerCase().equalsIgnoreCase(browserName)) {
			System.setProperty("webdriver.gecko.driver", "drivers\\IEDriverServer.exe");
			// to open firefox browser in incognito mode
			DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
			capabilities.setCapability(InternetExplorerDriver.FORCE_CREATE_PROCESS, true);
			capabilities.setCapability(InternetExplorerDriver.IE_SWITCHES, "-private");
			driver = new InternetExplorerDriver(capabilities);
		}

		driver.manage().window().maximize();
		driver.manage().deleteAllCookies();
		driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(Constants.IMPLICIT_WAIT, TimeUnit.SECONDS);

		if (Constants.ENVIRONMENT.DEV.name().equalsIgnoreCase(testEnv)) {
			PATIENT_BASE_URL = SystemConstants.MRIOA_PATIENT_BASE_URL_DEV;
			EMPLOYEE_BASE_URL = SystemConstants.MRIOA_EMPLOYEE_BASE_URL_DEV;
		} else if (Constants.ENVIRONMENT.QA.name().equalsIgnoreCase(testEnv)) {
			// TODO
			// PATIENT_BASE_URL = SystemConstants.MRIOA_PATIENT_BASE_URL_QA;
			// EMPLOYEE_BASE_URL = SystemConstants.MRIOA_EMPLOYEE_BASE_URL_QA;
		}
	}
	
	/**
	 * Method in Base Class to get current page URL 
	 * @param driver
	 * @return
	 */
	public String getCurrentUrl(WebDriver driver) {
		return driver.getCurrentUrl();
	}

	/**
	 * Method in Base Class to "Take Screenshot of Failed Test Cases"
	 * @param testMethodName
	 * @param driver
	 */
	public static void captureFailedTestCaseScreenshot(String testMethodName, WebDriver driver){
		TakesScreenshot ts = (TakesScreenshot)driver;
		File srcFile = ts.getScreenshotAs(OutputType.FILE);
		File targetFile = new File("screenshots/"+testMethodName+".jpg");
		try {
			FileUtils.copyFile(srcFile, targetFile);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}	
	
	/**
	 *Common method in Base Class to retrieve OTP on opting 2FA (which is used in 'LoginPageTest')
	 * @param connection
	 * @param userEmail
	 * @param userDefinedOTP
	 * @param driver
	 * @return
	 */
	public WebDriverWait submitOTP(Connection connection, String userEmail, Integer userDefinedOTP, WebDriver driver ) throws SQLException, InterruptedException {
	//in the method argument, we have used 'Integer' instead of int because we are passing null value to userdefinedOTP.
		//it is possible only in wrapper class to set default value as null. With int, default value can be 0 not null	
		int otp;

		if (null == userDefinedOTP) {
			// fetch otp from db against specified user email
			String otpQuery = Utilities.prepareOTPQuery(userEmail);
			ResultSet rs = DataBaseConnectionFactory.executeSQLQuery(connection, otpQuery);
			try {
				// check if queryOutput has 0 record by using isBeforeFirst()
				logger.info("rs.isBeforeFirst() " + rs.isBeforeFirst());
				if (null == rs || !rs.isBeforeFirst()) {
					logger.info("OTP not found for email address:--- " + userEmail);
					Assert.fail("OTP not found for email address:--- " + userEmail);
					return null;
				} else {
					//if rs has atleast 1 record, hence we can go to first row by using rs.first()
					// Note:- we could have also used while(rs.next()) here
					//since we have only one row due to query applied, hence we are not using while loop below
					rs.next(); // this will move cursor to first row---> i.e. the row from where we want to fetch the result							
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			// fetch OTP from the first row and assign to otp variable
			otp = rs.getInt("otp");
			logger.debug("OTP generated is " + otp);
			
		} else {
			// use the otp received in method parameter as this is invalid OTP
			otp = userDefinedOTP;
		}
		WebDriverWait wait = new WebDriverWait(driver,5);
		WebElement otpTextField = driver.findElement(By.xpath("*//input[@placeholder='OTP']"));
		wait.until(ExpectedConditions.visibilityOf(otpTextField));
		otpTextField.sendKeys(otp + ""); // cast user_otp to String
		Thread.sleep(2000);
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[@class='btn-line']"))).click();
		return wait;
	}

	/*----------------method to retrive data from excel sheet i.e. data driven framework-------------------------*/
	/*public static Sheet readExcel(String fileName) {
		Sheet sheet = null; //if return statement doesn't return anything, than return null
		try {
			//create object of any file i.e. xls or xlsx 
			Workbook wb = WorkbookFactory.create(new File(fileName));
			sheet = wb.getSheetAt(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sheet;
	}*/
	

}
