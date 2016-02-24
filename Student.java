package com.kevinturner.jv;

import java.util.UUID;


public class Student {
   private String username;
   private String password;
   private String id;
   private Course [] courses;
   
    public Student(String id2, String username, String password, Course [] courses){
    	   this.id = id2;
    	   this.username = username;
    	   this.password = password;
    	   this.courses = courses;
    }
    
    public String getId(){
    	
    	  return id;
    }
    
    public User getUser(){
    	   return new User(username, password, id);
    }
    
    public void setCourses(Course [] courses){
    	  this.courses = courses;
    }
    
    public String getUsername(){
    	  return username;
    }
    
    public String getPassword(){
    	 return password;
    }
    
    public Course [] getCourses(){
    	 return courses;
    }
    
    @Override public String toString(){
    	   String student = username + "\n";
    	    for(int i = 0; i < courses.length; i++){
    	    	   student = student + courses[i].toString() + "\n\n";
    	    }
    	    return student;
    }
}
