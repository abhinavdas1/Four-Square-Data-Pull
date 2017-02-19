package com.abh.db_api;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class Review {
	String text;
	int rating;
	long createdAt;
	String reviewId;
	String venueId;
	User user;
	
	Review()
	{
		user=new User();
	}
	
	public String toString()
	{
		Random rand=new Random();
		
		String temp = text.replaceAll(",", "");
		
		return reviewId + "," + temp +  "," + rand.nextInt(11) + "," + user.id + "," + venueId + "," +  "\n";
	}
	
	public static void saveToFile(List<Review> reviews)
	{

		String fileName="/Users/abhinavdas/Desktop/umn_foursquare_datasets/reviews.csv";
		try {
			FileWriter fw = new FileWriter(new File(fileName),true);
			for(Review r : reviews)
			{
				fw.write(r.toString());
				User.saveToFile(r.user);
			}
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
