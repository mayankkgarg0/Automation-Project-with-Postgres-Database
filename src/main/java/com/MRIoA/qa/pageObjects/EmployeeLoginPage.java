package com.MRIoA.qa.pageObjects;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.MRIoA.qa.BaseClass;
import com.MRIoA.qa.utilities.Constants;

public class EmployeeLoginPage extends BaseClass {

	private static Logger logger = Logger.getLogger(EmployeeLoginPage.class);

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

	public EmployeeLoginPage() {
		logger.info("In EmployeeLoginPage constructor");
		PageFactory.initElements(driver, this);
	}

	public void startTest(String testcaseName) {
		extentTest = extent.startTest(testcaseName);
	}

	/**
	 * Method in 'EmployeeLoginPage' to do 'Login'
	 * @param empRole
	 * @param empNum
	 * @param is2FAOpted
	 * @throws InterruptedException
	 */
	public void doLogin(String empRole, int empNum, boolean is2FAOpted) throws InterruptedException {

		String email = testProp.getString(empRole + "_" + empNum + "_email_address");
		String password = testProp.getString(empRole + "_" + empNum + "_password");
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.elementToBeClickable(signInWithMicrosoftButton)).click();
		//signInWithMicrosoftButton.click();
		String parentWindow = driver.getWindowHandle();
		logger.info("Parent Window ID is : " + parentWindow);

		Set<String> allWindow = driver.getWindowHandles();
		int count = allWindow.size();
		logger.info("Total Window : " + count);

		for (String child : allWindow) {
			if (!parentWindow.equalsIgnoreCase(child)) {
				driver.switchTo().window(child);
				driver.manage().timeouts().pageLoadTimeout(Constants.IMPLICIT_WAIT, TimeUnit.SECONDS);
				Thread.sleep(3000);
				logger.info(empRole + " is providing Microsoft credentials !!");
				WebDriverWait explicitWait = new WebDriverWait(driver, 30);
				explicitWait.ignoring(StaleElementReferenceException.class)
						.until(ExpectedConditions.visibilityOf(emailAddressField)).sendKeys(email);
				nextButton.click();
				explicitWait.ignoring(StaleElementReferenceException.class)
						.until(ExpectedConditions.visibilityOf(passwordField)).sendKeys(password);
				signInButton.click();
				driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
				Thread.sleep(5000);
			}
		}
		driver.switchTo().window(parentWindow);
		if (is2FAOpted) {
			logger.info("Waiting for OTP screen");
			wait.until(ExpectedConditions.visibilityOf(otpField));
		} else {
			driver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		}
	}
	
	/**
	 * Method in 'EmployeeLoginPage' for 'Logout'
	 * @throws InterruptedException
	 */
	public void logout() throws InterruptedException {
		WebDriverWait explicitWait = new WebDriverWait(driver, 20);
		explicitWait.until(
				ExpectedConditions.elementToBeClickable(By.xpath("*//tc-dropdown-button/div")))
				.click();
		Actions a = new Actions(driver);
		WebElement logoutButton = driver.findElement(By.linkText("Logout"));
		a.moveToElement(logoutButton).build().perform();
		explicitWait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Logout")));
		explicitWait.until(ExpectedConditions.elementToBeClickable(By.linkText("Logout"))).click();
		logger.info("Logout button is clicked");
	}

	public void endTest() {
		extent.endTest(extentTest);
		extent.flush();
	}
}
