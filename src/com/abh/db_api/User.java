package com.abh.db_api;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class User {
	String id;
	String firstName;
	String lastName;
	
	public static void saveToFile(User u)
	{
		String fileName="/Users/abhinavdas/Desktop/umn_foursquare_datasets/userFile.csv";
		try {
			FileWriter fw = new FileWriter(new File(fileName),true);
			fw.write(u.toString());
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public String toString()
	{
		return id + "," + firstName + "," + lastName + "\n";
	}
}
