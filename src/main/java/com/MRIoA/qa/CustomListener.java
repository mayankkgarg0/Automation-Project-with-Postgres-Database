package com.MRIoA.qa;

import org.apache.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class CustomListener extends BaseClass implements ITestListener{
	private static Logger logger = Logger.getLogger(CustomListener.class);

	@Override
	public void onFinish(ITestContext arg0) {
		// TODO Auto-generated method stub	
	}

	@Override
	public void onStart(ITestContext arg0) {
		// TODO Auto-generated method stub	
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onTestFailure(ITestResult testResult) {
		logger.info("Failed Test Case Screenshot!!! In Process");
		String methodName = testResult.getMethod().getMethodName();
		captureFailedTestCaseScreenshot(methodName, driver);
		logger.info("Failed Test Case Screenshot Saved Successfully!!");
	}

	@Override
	public void onTestSkipped(ITestResult arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onTestStart(ITestResult arg0) {
		// TODO Auto-generated method stub	
	}

	@Override
	public void onTestSuccess(ITestResult arg0) {
		// TODO Auto-generated method stub		
	}
	

}
