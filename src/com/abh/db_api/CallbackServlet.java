package com.abh.db_api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



@WebServlet("/CallbackServlet")
public class CallbackServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter out = response.getWriter();
		ArrayList<Long> VenueIDs=ExtractVenueIds();
		
		for(int i=0; i< VenueIDs.size(); i++)
		{
			
			String endPoint = "/v2/venues/"+ VenueIDs.get(i) +"/";
			Map<String, String> parameters = new HashMap<>();
			parameters.put("client_id", "4VG5K5FOKQ5R0SKKVIYRIX4GQCWNN2I3JXXVZUDXCZPHZBUU");
			parameters.put("client_secret", "Z2YPXBAOMHEQNTXSTZVM5OBX2ZNGOQDWDGW4YKELIGVYSDV1");
			parameters.put("v", "20170204");				
			
			
			String result = HttpRequestHandler.execute(endPoint, parameters, "GET");
			
			
			try{
				
				JSONParser parser = new JSONParser();
				if(result==null)
					continue;
				JSONObject jsonToken = (JSONObject)parser.parse(result);
				JSONObject meta = (JSONObject) jsonToken.get("meta");
				if(meta.get("code").equals("400"))
					continue;
				JSONObject res = (JSONObject) jsonToken.get("response");
				ExtractVenueDetails(res);
				
				
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			System.out.println(i);
		}
	}
	
	
	public ArrayList<Long> ExtractVenueIds()
	{
		
		ArrayList < Long > result = new ArrayList < Long > ();
		String filepath = "/Users/abhinavdas/Desktop/umn_foursquare_datasets/venues.csv";
		String numFile = "/Users/abhinavdas/Desktop/umn_foursquare_datasets/num.txt";
		Scanner scan;
		
		
		try {
			
			int count = 0;
			scan = new Scanner(new File(numFile));
            String[] inpline = scan.nextLine().split(" ");
            count = Integer.parseInt(inpline[inpline.length-1]);
			
			int j = 1;
			
			
			
			scan = new Scanner(new File(filepath));
			for(int i=0; i<count; i++)
				scan.nextLine();
			
			
			while (scan.hasNextLine() && j<=4800) {
		        String line = scan.nextLine();
		        String[] lineArray = line.split(",");
		        result.add(Long.parseLong(lineArray[0]));
		        j++;
		    }
			
			
			count = count + 4800;
			
			FileWriter fw = new FileWriter(new File(numFile),true);
			fw.write( " " + count);
			fw.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	    
	    return result;
	}
	
	
	public void ExtractVenueDetails(JSONObject obj)
	{
		JSONObject venue = null,loc = null,stats = null,tips = null,contact=null;
		JSONArray categories = null;
		Venue tempVen = new Venue();
		
		
		if(obj.containsKey("venue"))
			 venue = (JSONObject) obj.get("venue");
		if(venue.containsKey("location"))		
			 loc = (JSONObject) venue.get("location");
		if(venue.containsKey("tips"))
			 tips = (JSONObject) venue.get("tips");
		if(venue.containsKey("stats"))
			 stats = (JSONObject) venue.get("stats");
		if(venue.containsKey("contact"))
			 contact = (JSONObject) venue.get("contact");
		
		if(contact.containsKey("phone"))
			tempVen.phone = (String) contact.get("phone");
		
		if(venue.containsKey("name"))
			tempVen.name = (String) venue.get("name");
		if(venue.containsKey("id"))
			tempVen.id = (String) venue.get("id");
			
		if(loc.containsKey("address"))
			tempVen.address = (String) loc.get("address");
		if(loc.containsKey("lat"))
			tempVen.latitude = (Double) loc.get("lat");
		if(loc.containsKey("lng"))
			tempVen.longitude = (Double) loc.get("lng");
		if(loc.containsKey("postalCode"))
			tempVen.postalCode = (String) loc.get("postalCode");
		if(loc.containsKey("city"))
			tempVen.city = (String) loc.get("city");
		if(loc.containsKey("state"))
			tempVen.state = (String) loc.get("state");
		if(loc.containsKey("country"))
			tempVen.country = (String) loc.get("country");
			
		if(venue.containsKey("canonicalUrl"))		
			tempVen.url = (String) venue.get("canonicalUrl");
			
		if(venue.containsKey("rating"))			
			tempVen.rating = (double) venue.get("rating");
			
		if(stats.containsKey("checkinsCount"))							
			tempVen.checkinCount = (long) stats.get("checkinsCount");
		if(stats.containsKey("tipCount"))									
			tempVen.tipCount = (long) stats.get("tipCount");
		if(stats.containsKey("usersCount"))								
			tempVen.userCount = (long) stats.get("usersCount");
		
		
		if(venue.containsKey("categories"))
		{
			categories=(JSONArray) venue.get("categories");
			for(int i=0; i<categories.size(); i++)
			{
				JSONObject tempCat = (JSONObject) categories.get(i);
				Category cat = new Category();
				cat.id = (String) tempCat.get("id");
				cat.name = (String) tempCat.get("name");
				tempVen.cat.add(cat);
			}
			for(Category c : tempVen.cat)
			{
				System.out.println(c.name);
			}
		}
		
		
		JSONArray group=(JSONArray) tips.get("groups");
		for(int i=0; i<group.size(); i++)
		{
			JSONObject tempGroup = (JSONObject) group.get(i);
			String type = (String) tempGroup.get("type");
			if(!type.equals("others"))
				continue;
			else
			{
				JSONArray items=(JSONArray) tempGroup.get("items");
				for(int m=0; m<items.size(); m++)
				{
					
					JSONObject tempReview = (JSONObject) items.get(m);
					Review rev = new Review();
					rev.text = (String) tempReview.get("text");
					rev.reviewId = (String) tempReview.get("id");
					rev.createdAt = (long) tempReview.get("createdAt");
					
					JSONObject user =  (JSONObject) tempReview.get("user");
					rev.user.firstName = (String) user.get("firstName");
					rev.user.lastName = (String) user.get("lastName");
					rev.user.id = (String) user.get("id");
					rev.venueId = tempVen.id;
					
					tempVen.reviews.add(rev);
							
				}
			}
					
			
		}
		
		Venue.saveToFile(tempVen);
		
	}
	
}
