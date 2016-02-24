package com.kevinturner.jv;

import java.util.UUID;


public class Course {
   private String id;
   private String courseName;
   private double average;
   private Assignment [] assignments;
   private String student_id;
     public Course(String id, String name, double average, Assignment [] assignments, String student_id){
    	     this.id = id;
    	     this.courseName = name;
    	     this.average = average;
    	     this.assignments = assignments;
    	     this.student_id = student_id;
     }
     
     public String getCourseName(){
    	   return courseName;
     }
     
     public double getAverage(){
    	   return average;
     }
     
    
     
     public Assignment [] getAssignments(){
    	    return assignments;
     }
     
     
     public String getId(){
    	   return id;
     }
     
     
     @Override public String toString(){
    	
    	     String course = courseName + " " + average + " " +"\n";
    	     if(assignments != null){
    	     for(int i = 0; i < assignments.length; i++){
    	    	     if(assignments[i] == null)break;
    	    	    	   
    	    	     course = course + assignments[i] + "\n";
    	     }
    	     }
    	     return course +" " +this.getStudent_id();
     }

	public String getStudent_id() {
		return student_id;
	}

	
     
  
}
