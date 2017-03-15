package org.weijie.wallethub.mytimesheet.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
	private static String url = "jdbc:mysql://rds-mysql-mytimesheet.cviv7aaop6st.us-east-1.rds.amazonaws.com:3306/TimesheetDB";    
    private static String driverName = "com.mysql.jdbc.Driver";   
    private static String username = "Airin1990";   
    private static String password = "west521life";
    private static Connection con;

    public static Connection getConnection() {
        try {
            Class.forName(driverName);
            try {
                con = DriverManager.getConnection(url, username, password);
            } catch (SQLException ex) {
                // log an exception. fro example:
                System.out.println("Failed to create the database connection."); 
            }
        } catch (ClassNotFoundException ex) {
            // log an exception. for example:
            System.out.println("Driver not found."); 
        }
        return con;
    }
}
