package com.MRIoA.qa.utilities;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import com.MRIoA.qa.DataBaseConnectionFactory;

//In utilities class, we define common methods that will be used throughout the application
public class Utilities {
	
	private static Logger logger = Logger.getLogger(Utilities.class);
	
	static{
		PropertyConfigurator.configure("log4j.properties");
	}
	/**
	 * Common method used to 'Decode the String'
	 * @param password
	 * @return
	 */
	public static String decodeString(String password){
		Base64.Decoder decoder = Base64.getDecoder();  
        // Decoding string  
        String dStr = new String(decoder.decode(password)); 
        logger.info(dStr);
        return dStr;
	}
	
	/**
	 * Common method used to 'Encode' the string
	 * @param input
	 * @return
	 */
	public static String encodeString(String input){
		Encoder encoder = Base64.getEncoder();  
        // encoded string  
        String encodedStr = new String(encoder.encode(input.getBytes())); 
        logger.info(encodedStr);
        return encodedStr;
	}
	
	/**
	 * Common method to set query which will be used in submitOTP method to generate OTP
	 * @param emailaddress
	 * @return
	 */
	public static String prepareOTPQuery(String emailaddress){
		String query = "select otp from second_opinion.user_otp where user_id = (select id from second_opinion.user_details where email = '"+emailaddress+"') order by created_at desc limit 1";
		return query;
		
	}
	
	/**
	 * Common method to set query for 2FA status which will be used in 'get2FAStatus method' to know about OTP status of login user 
	 * @param emailaddress
	 * @return
	 */
	public static String prepare2FAStatusQuery(String emailaddress){
		String query = "select two_factor_authenticator_flag from second_opinion.user_details where email = '"+emailaddress+"'";
		return query;	
	}
	
	/**
	 * Common method to set query for Case ID which will be used in for closing 'Submitted service request' with the help of rest-assured 
	 * @param emailaddress
	 * @return
	 */
	public static String prepareCaseIDQuery(String title){
		String query = "select case_id from second_opinion.cases where service_request_id =(select id from second_opinion.service_request where request_title = '"+title+"'order by created_at desc limit 1)";
		return query;	
	}

	/**
	 * Common method to verify if patient has opted 2FA. If yes, navigate to OTP page after login else navigate to dashboard.
	 * @param connection
	 * @param userEmail
	 * @return
	 * @throws SQLException
	 * @throws InterruptedException
	 */
	public static  boolean get2FAStatus(Connection connection, String userEmail) throws SQLException, InterruptedException {
				
				boolean status2FA;
				String query2FA = Utilities.prepare2FAStatusQuery(userEmail);
				ResultSet rs = DataBaseConnectionFactory.executeSQLQuery(connection, query2FA);
				try {
					// check if queryOutput has 0 record by using isBeforeFirst()
					logger.info("rs.isBeforeFirst() " + rs.isBeforeFirst());
					if (null == rs || !rs.isBeforeFirst()) {
						logger.info("2FA is not opted for email address:--- " + userEmail);
						Assert.fail("2FA is not opted for email address:--- " + userEmail);
						return false;
					} else {
						//if rs has atleast 1 record, hence to go to first row by using rs.first()
						// Note:- we could have also used while(rs.next()) here
						//since we need only 1st row, hence we are not using while loop below
						rs.next(); // this will move cursor to first row---> i.e. the row from where we want to fetch the result							
					}
				} catch (SQLException e1) {
					Assert.fail("error while getting 2fa status--- " + e1.getMessage());
					e1.printStackTrace();
					return false;
				}
				// fetch 2FA status from the first row and assign to status2FA variable
				status2FA = rs.getBoolean("two_factor_authenticator_flag");
				logger.debug("2FA status for"+userEmail+" is: "+ status2FA);
				return status2FA;	
		}
		
	/**
	 * Common method to upload the document (Robot class is used to upload file)
	 * @param filePath
	 */
	public static void uploadDocument(String filePath){
		try {
			Robot robot = new Robot();
			robot.setAutoDelay(2000);
			
			//copy filepath into the clipboard
			StringSelection path = new StringSelection(filePath);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(path, null);
			
			//pressing control+v
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_V);
			
			//releasing contrl+v 
			robot.keyRelease(KeyEvent.VK_CONTROL);
			robot.keyRelease(KeyEvent.VK_V);
			
			//Pressing and releasing enter key
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);	
			
		} catch (AWTException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * Common method to get size of table headers in employee dashboard.
	 * @param driver
	 * @return
	 */
	public static List<WebElement> getTableHeaders(WebDriver driver){
		// Printing table headers on patient dashboard assuming first row as header
		List<WebElement> allHeadersOfTable= driver.findElements(By.xpath("//table[@class='table-box']/thead/tr/th"));
		logger.info("Total headers/columns found in table is: "+allHeadersOfTable.size());
		return allHeadersOfTable;
	}
	
//Extent Reports is a HTML Reporting library which is used to create interactive HTML report of your test session
//to add extent report, we require: 2.9
// 1. extent report maven dependency to be added
// 2. extent-config.xml (configuration file to be kept in project home directory
// 3. listener class is required (renamed as 'Reporting.java')-- this is utility file

}
