package com.kevinturner.jv;

import java.util.*;
import java.io.*;


import org.jsoup.nodes.*;


public class gradeFetcher extends Fetcher<Course>{
	private String username;
	private String password;
	private Student s;
	
	
	public gradeFetcher(Student s){
		super();
		this.username = s.getUsername();
		this.password = s.getPassword();
		this.s = s;
	}
	
	
	
	@Override public Course [] fetch(){
		if(username != null && password != null){
			Crawler c = new Crawler("https://home-access.cfisd.net/HomeAccess/Content/Student/Assignments.aspx");
			if(c.logOn(username, password)){
				try {
				   Document doc = c.crawl();
				   System.out.println(doc);
				   Parser p = new Parser(doc);
				   Course [] cs = p.courses(s);
				   System.out.println("courses " + cs);
				   return cs;
		           
				} catch (IOException e){
					System.out.println(e);
					System.exit(0);
				}
				
				
			
			} 
			
		}
	  return null;
	}
	
	public gradeFetcher(String username, String password){
		 this.username = username;
		 this.password = password;
	}
	
	public static void printArray(Object [] arr){
		 for(Object o : arr){
			 System.out.println(o);
		 }
	}
	
	public static void main(String [] args){
		gradeFetcher gf = new gradeFetcher("s650665", "kai66wen");
		Course [] c = gf.fetch();
		printArray(c);
	}
}