package com;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyConnection 
{
	public static String db_name;
	
	public static Connection connectDB() throws ClassNotFoundException, SQLException
	{	
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost/"+db_name,"root","password");
		System.out.println("Connection Established with DB: "+db_name);
		return conn;		
	}


}
