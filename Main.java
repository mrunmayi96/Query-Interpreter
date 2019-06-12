package com;

import java.sql.SQLException;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main 
{
	
	@SuppressWarnings("unused")
	public static void main(String[] args) throws ClassNotFoundException, SQLException 
	{
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Menu m = new Menu();
	}

}
