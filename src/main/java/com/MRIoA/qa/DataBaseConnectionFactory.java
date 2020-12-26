package com.MRIoA.qa;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.MRIoA.qa.utilities.Constants;
import com.MRIoA.qa.utilities.SystemConstants;

public class DataBaseConnectionFactory {
	private static Logger logger = Logger.getLogger(DataBaseConnectionFactory.class);
	
	static{
		PropertyConfigurator.configure("log4j.properties");
	}
	
	/**
	 * Common method to establish connection with Database
	 * @return
	 */
    public static Connection getConnection(){
    	String dbusername;
    	String dbpassword;
    	String connectionUrl=null;
        Connection sqlConnection = null; 
            
     	String testEnv = SystemConstants.TEST_ENV;
     	
		if(null==testEnv || testEnv.isEmpty()){
			throw new RuntimeException("Test Environment is missing under System Environments");
		}

		//To connect with DEV Database
		if(Constants.ENVIRONMENT.DEV.name().equalsIgnoreCase(testEnv)) {
             connectionUrl = SystemConstants.MRIOA_DB_URL_DEV;
             dbusername = SystemConstants.MRIOA_DB_USERNAME_DEV;          
             dbpassword = SystemConstants.MRIOA_DB_PWD_DEV;           
         }
     /*    //To connect with QA Database
         else if(Constants.ENVIRONMENT.QA.name().equalsIgnoreCase(testEnv)){
            connectionUrl = databaseURLQA;
            dbusername = "root";
            dbpassword = "root";
        }
        //To connect with Production Database
        else if(Constants.ENVIRONMENT.PROD.name().equalsIgnoreCase(testEnv)) {
            connectionUrl = databaseURLPRODUCTION;
            dbusername = "root";
            dbpassword = "prodpassword";
        }        
*/
         else{
 			throw new RuntimeException("No Configuration found for the provided Test Environment "+testEnv);
         }
        try {
        	Class.forName("org.postgresql.Driver");//to load the driver
        	//open a connection
            sqlConnection = DriverManager.getConnection(connectionUrl,dbusername,dbpassword);
            if(sqlConnection!=null) {
                logger.info("Connected to PostgreSQL server "+testEnv+" database");
            }else {
                throw new RuntimeException("Database connection failed to connect "+testEnv+" Environment");              
            }
        }
           catch(ClassNotFoundException e) {
        	   e.printStackTrace();
                logger.info( "Exception:" +e.getStackTrace());		
			}    
        catch(SQLException e) {
        	e.printStackTrace();
        	logger.info( "Exception:" +e.getStackTrace());		
        }    
      return sqlConnection;
    }
    
    /**
     * Common method to retrieve the resultset after executing SQL query
     * @param sqlConnection
     * @param sqlQuery
     * @return
     */
    public static ResultSet executeSQLQuery( Connection sqlConnection, String sqlQuery) {
        ResultSet rs = null;
    
        logger.debug("sqlQuery === "+sqlQuery);
            Statement stmt;
			try {
				stmt = sqlConnection.createStatement(); //createStatement is an interface in java which helps to execute any query
			
            rs=stmt.executeQuery(sqlQuery);
                
			} catch (SQLException e) {
				e.printStackTrace();
			} 
            return rs;
    }
    
    public static void main(String[] args) throws SQLException {
		
    	String sqlQuery = "select created_at, otp from second_opinion.user_otp where user_id = (select id from second_opinion.user_details where email = 'shatakshi3@mailinator.com') order by created_at desc limit 1";
    	ResultSet rs = DataBaseConnectionFactory.executeSQLQuery(getConnection(), sqlQuery);
    	logger.info("queryOutput.isBeforeFirst() "+rs.isBeforeFirst());
		if(null == rs || !rs.isBeforeFirst()){ 
			logger.info("OTP is not found for email address:");	
		}
    	while(rs.first()){
    		int otp = rs.getInt("otp");
    		logger.info("OTP is:"+otp);
    	}
	}


}