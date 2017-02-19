package com.abh.db_api;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Venue {
	String id;
	String name;
	String phone;
	String address;
	double latitude;
	double longitude;
	String city;
	String state;
	String country;
	String postalCode;
	String url;
	List<Category> cat;
	List<Review> reviews;
	String description;
	double rating;
	
	long checkinCount;
	long userCount;
	long tipCount;
	
	Venue()
	{
		cat=new ArrayList<Category>();
		reviews= new ArrayList<Review>();
	}
	
	public String toString()
	{
		return name + "," + id + "," + phone + "," + latitude + "," + longitude + "," + city + "," + state + "," + country + "," + postalCode + "," + url + "," + description + "," + rating + "," + checkinCount + "," + userCount + "," + tipCount  + "\n";
	}

	public static void saveToFile(Venue v) {
		
		String fileName="/Users/abhinavdas/Desktop/umn_foursquare_datasets/finalVenues.csv";
		try {
			FileWriter fw = new FileWriter(new File(fileName),true);
			fw.write(v.toString());
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Category.saveToFile(new ArrayList<Category>(v.cat),v.id);
		Review.saveToFile(new ArrayList<Review>(v.reviews));
		
	}

}
