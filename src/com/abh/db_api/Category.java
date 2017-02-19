package com.abh.db_api;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Category {
	String id;
	String name;
	
	public static void saveToFile(List<Category> cats,String id)
	{
		
		String fileName="/Users/abhinavdas/Desktop/umn_foursquare_datasets/categories.csv";
		try {
			FileWriter fw = new FileWriter(new File(fileName),true);
			for(Category e : cats)
			{
				fw.write(e.toString());
			}
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		fileName="/Users/abhinavdas/Desktop/umn_foursquare_datasets/categoryVenue.csv";
		try {
			FileWriter fw = new FileWriter(new File(fileName),true);
			for(Category e : cats)
			{
				fw.write(e.id + "," + id + "\n");
			}
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		

	}
	
	public String toString()
	{
		return this.id + "," + this.name + "\n";
	}
	
}
