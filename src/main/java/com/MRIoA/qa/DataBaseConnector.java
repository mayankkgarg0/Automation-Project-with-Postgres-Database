package com.MRIoA.qa;

/*----------------------------------not in use-----this is extra file. Please ignore*/


/*import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class DataBaseConnector {

    private static String dbusername;
    private static String dbpassword;

    //Should be defined as jdbc:mysql://host:port/database name
    private static String databaseURLDEV= "jdbc:postgresql://35.247.118.219:5432/postgres";
    private static String databaseURLQA= "jdbc:postgresql://stagehost:2020/easyDB";
    private static String databaseURLPRODUCTION= "jdbc:postgresql://prodhost:2020/easyDB";


    public static String executeSQLQuery(String testEnv, String sqlQuery) {
        String connectionUrl="";
        Connection connection;
        String resultValue = "";
        ResultSet rs;

        //To connect with QA Database
        if(testEnv.equalsIgnoreCase("QA")){
            connectionUrl = databaseURLQA;
            dbusername = "root";
            dbpassword = "root";
        }
        //To connect with Stage Database
        else if(testEnv.equalsIgnoreCase("DEV")) {
            connectionUrl = databaseURLDEV;
            dbusername = "mrioa_dev";
            dbpassword = "medi@@gility";
        }

        //To connect with Production Database
        else if(testEnv.equalsIgnoreCase("PRODUCTION")) {
            connectionUrl = databaseURLPRODUCTION;
            dbusername = "root";
            dbpassword = "prodpassword";
        }
        try {
            Class.forName("org.postgresql.Driver");
        }catch(ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            connection = DriverManager.getConnection(connectionUrl,dbusername,dbpassword);
            if(connection!=null) {
                logger.info("Connected to the database...");
            }else {
                logger.info("Database connection failed to "+testEnv+" Environment");
            }
            Statement stmt = connection.createStatement();
            rs=stmt.executeQuery(sqlQuery);

            try {
                while(rs.next()){
                	logger.info(rs);
                	resultValue+=rs.toString();
//                    resultValue = rs.getString(1).toString();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            catch (NullPointerException err) {
                logger.info("No Records obtained for this specific query");
                err.printStackTrace();
            }
            connection.close();

        }catch(SQLException sqlEx) {
            logger.info( "SQL Exception:" +sqlEx.getStackTrace());
        }
        return resultValue;
    }


    public static ArrayList<String> executeSQLQuery_List(String testEnv, String sqlQuery) {
        String connectionUrl="";
        Connection connection = null;
        ArrayList<String> resultValue = new ArrayList<String>();
        ResultSet resultSet;

        //To connect with QA Database
        if(testEnv.equalsIgnoreCase("QA")){
            connectionUrl = databaseURLQA;
            dbusername = "root";
            dbpassword = "root";
        }

        //To connect with Stage Database
        else if(testEnv.equalsIgnoreCase("DEV")) {
            connectionUrl = databaseURLDEV;
            dbusername = "mrioa_dev";
            dbpassword = "medi@@gility";
        }

        //To connect with Production Database
        else if(testEnv.equalsIgnoreCase("PRODUCTION")) {
            connectionUrl = databaseURLPRODUCTION;
            dbusername = "root";
            dbpassword = "prodpassword";
        }

        try {
            Class.forName("org.postgresql.Driver");
        }catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
logger.info("trying");
        try {
        	try{
            connection = DriverManager.getConnection(connectionUrl,dbusername,dbpassword);
        	}catch(Exception e){
        		logger.info("connection exp");
        		e.printStackTrace();
        	}
            
            
            if(connection!=null) {
                logger.info("Connected to the database");
            }else {
                logger.info("Failed to connect to "+testEnv+" database");
            }
            Statement statement = connection.createStatement();
            resultSet=statement.executeQuery(sqlQuery);

            try {
                while(resultSet.next()){
                    int columnCount = resultSet.getMetaData().getColumnCount();
                    StringBuilder stringBuilder = new StringBuilder();
                    for(int iCounter=1;iCounter<=columnCount; iCounter++){
                        stringBuilder.append(resultSet.getString(iCounter).trim()+" ");
                    }
                    String reqValue = stringBuilder.substring(0, stringBuilder.length()-1);
                    resultValue.add(reqValue);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            catch (NullPointerException ex) {
                logger.info("No Records found for this specific query" +ex.getStackTrace());
            }
            finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException ex) {
                    	ex.printStackTrace();
                        logger.info( "SQL Exception:" +ex.getStackTrace());
                    }
                }
            }

        }catch(SQLException sqlEx) {
            logger.info( "SQL Exception:" +sqlEx.getStackTrace());
        }
        return resultValue;
    }
    public static void main(String[] args) {
    	
    	ArrayList<String> responseList = DataBaseConnector.executeSQLQuery_List("DEV", "select * from second_opinion.service_request where id=285");
	
    	for (String response : responseList) {
			
    		logger.info(response);
		}

    	
    	
    	while(rs.next()){
            //Retrieve by column name
            int id  = rs.getInt("id");
            int age = rs.getInt("age");
            String first = rs.getString("first");
            String last = rs.getString("last");

            //Display values
            System.out.print("ID: " + id);
            System.out.print(", Age: " + age);
            System.out.print(", First: " + first);
            logger.info(", Last: " + last);
    	String response = DataBaseConnector.executeSQLQuery("DEV", "select * from second_opinion.email_templates where id = 1");
logger.info(response);
    
    }
}*/