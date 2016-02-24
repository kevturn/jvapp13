package com.kevinturner.jv;

import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;

import java.util.Arrays;
import java.util.Iterator;

public class JVDBManager extends DatabaseManager {
  private DatastoreService conn;
	
	public JVDBManager(){
		super();
		conn = getConnection();
	}
	
	public void courseInsert(Course c){
		
		  
		  Entity course = new Entity("Course");
		  System.out.println(course);
		  course.setProperty("courseName", c.getCourseName());
		  course.setProperty("courseAverage", c.getAverage());
		  course.setProperty("studentID", c.getStudent_id());
		  course.setProperty("id", c.getId());
		  course.setProperty("totalAssignments", c.getAssignments().length);
		 
		  conn.put(course);
	} 
	
	
	public void assignmentInsert(String id, String name, String dueDate, String assignedDate, String type, double grade, double totalPoints, double weight){
	
		Entity assignment = new Entity("Assignment");
		assignment.setProperty("id", id);
		assignment.setProperty("name", name);
	    assignment.setProperty("dueDate", dueDate);
	    assignment.setProperty("assignedDate", assignedDate);
	    assignment.setProperty("grade", grade);
	    assignment.setProperty("totalPoints", totalPoints);
	    assignment.setProperty("weight", weight);
	    assignment.setProperty("type", type);
	    
	    conn.put(assignment);
	}
	
	public Student [] getAllStudents(){
		Query sq = new Query("Student");
		PreparedQuery pq = conn.prepare(sq);
		  Iterator<Entity> it = pq.asIterator();
		  Student [] students = new Student[pq.countEntities()];
		  int i = 0;
		  while(it.hasNext()){
			  Entity es = it.next();
			  String studentID = (String)es.getProperty("studentID");
			  String username = (String)es.getProperty("username");
			  String password = (String)es.getProperty("password");
		      Course [] courses = this.getStudentsCourses(studentID);
		      Student s = new Student(studentID, username, password, courses);
		      students[i++] = s;
		  }
		  return students;
	}
	
	public void deleteCourses(User u){
		Filter userFilter = new FilterPredicate("studentID", FilterOperator.EQUAL, u.getUserID());
		Query q = new Query("Course").setFilter(userFilter);
		PreparedQuery pq = conn.prepare(q);
		Iterator <Entity> it = pq.asIterator();
		while(it.hasNext()){
			conn.delete(it.next().getKey());
		}
	}
	
	public void deleteAnnouncements(){
		Query q = new Query("Announcement");
		PreparedQuery pq = conn.prepare(q);
		Iterator<Entity> it = pq.asIterator();
		while(it.hasNext()){
			conn.delete(it.next().getKey());
		}
	}
	
	public void deleteAssignments(Course c){
		System.out.println(c + "course");
		Filter courseFilter = new FilterPredicate("id", FilterOperator.EQUAL, c.getId());
	    Query q = new Query("Course").setFilter(courseFilter);
	    PreparedQuery pq = conn.prepare(q);
	    Iterator <Entity> it = pq.asIterator();
	    while(it.hasNext()){
	    	  conn.delete(it.next().getKey());
	    }
	}
	
	
	public void insertAnnouncements(String date, String announcement){
		
		
		Entity announcements = new Entity("Announcement");
		announcements.setProperty("date", date);
		announcements.setProperty("announcement", announcement);
		
		conn.put(announcements);
	}
	
	public Announcement [] getAnnouncements(){
	   Query q = new Query("Announcement");
	    PreparedQuery pq = conn.prepare(q);
	  Announcement [] announcements = new Announcement[pq.countEntities()];
	  int i = 0;
	  for(Entity e : pq.asIterable()){
		 String date = (String)e.getProperty("date");
		 String announcement = (String)e.getProperty("announcement");
		 announcements[i++] = new Announcement(announcement, date);
	  }
	  return announcements;
	}
	
	
	
	public boolean registerStudent(Student s){
	   if(this.authenticateStudent(s.getUsername(), s.getPassword()) == null){	
			System.out.println((new Crawler()).logOn(s.getUsername(), s.getPassword()) + "logged on");
			if((new Crawler()).logOn(s.getUsername(), s.getPassword())){
		
			Entity student = new Entity("Student");
			student.setProperty("username", s.getUsername());
			student.setProperty("password", s.getPassword());
			student.setIndexedProperty("studentID", s.getId().toString());
			conn.put(student);
	
			
			Fetcher<Course> fetchGrades = new gradeFetcher(s);
		    Course [] courses = fetchGrades.fetch();
		    for(Course c : courses){
		    	 System.out.println(c);
		    }
		    s.setCourses(courses);
		    insertCourses(courses);
			return true;
			} else {
				return false;
			}
	
	   } 
		return false;
	}
	
	public void insertCourses(Course [] courses){
		for(Course c : courses){
			if(c != null){
			courseInsert(c);
			
		  Assignment [] assign = c.getAssignments();
		  if(assign.length > 0){
		  System.out.println("last: " + assign[assign.length-1]);
		   for(int i = 0; i < assign.length; i++){
			   Assignment a = assign[i];
			  if(a == null)break; 
			   System.out.println(a);
			  assignmentInsert(a.getCourse_id(), a.getName(), a.getDueDate(), a.getAssignedDate(), a.getType(), a.getScore(), a.getTotalPoints(), a.getWeight());
		   }
		  }
		}
		}
	}
	
	public User authenticateStudent(String username, String password){
	
		Filter usernameFilter = new FilterPredicate("username", FilterOperator.EQUAL, username);
		Filter passwordFilter = new FilterPredicate("password", FilterOperator.EQUAL, password);
		Filter composite = CompositeFilterOperator.and(usernameFilter, passwordFilter);
		Query q = new Query("Student").setFilter(composite);
		PreparedQuery pq = conn.prepare(q);
		
	   Iterator<Entity> it = pq.asIterator();
	   if(it.hasNext()){
		   Entity user = it.next();
		 String id =   (String)user.getProperty("studentID");
		   return new User(username, password, id);
	   }
		    
		return null;
	}
	
	
	public Course [] getStudentsCourses(String id){

			System.out.println(id + ": id");
		
			Filter studentFilter = new FilterPredicate("studentID", FilterOperator.EQUAL, id);
			
		    Query q = new Query("Course").setFilter(studentFilter);
		    PreparedQuery pq = conn.prepare(q);
			
			Course [] courses = new Course[7];
		    Assignment [] assignments = null;
		    int x = 0;
		    Iterator<Entity> iterator = (Iterator<Entity>) pq.asIterator();
			while(iterator.hasNext()){
		    	    if(x == 7)break;
		    	    
		    	   Entity course = iterator.next();
		    	  String c_id = (String) course.getProperty("id");
		    	  
		    	  String courseName = (String)course.getProperty("courseName");
		    	  System.out.println("retreived course name");
		    	  double average = (Double)course.getProperty("courseAverage");
		    	  Long long_numberOfAssignments = (Long)course.getProperty("totalAssignments");
		    	  int numberOfAssignments = long_numberOfAssignments.intValue();
		    	  
		    	  assignments = new Assignment[numberOfAssignments];
		    	  
		    	  if(numberOfAssignments != 0){
		    	  Filter assignFilter = new FilterPredicate("id", FilterOperator.EQUAL, c_id);
		    	   Query aq  = new Query("Assignment").setFilter(assignFilter);
		    	  PreparedQuery paq = conn.prepare(aq);
		    	  
		    	  Iterator<Entity> it = paq.asIterator();
		    	  
		    	  int i = 0;
		    	  while(it.hasNext()){
		    		  Entity assignment = it.next();
		    		  
		    		  String name = (String)assignment.getProperty("name");
		    		  String dueDate = (String)assignment.getProperty("dueDate");
		    		  String assignedDate = (String)assignment.getProperty("assignedDate");
		    		  String type = (String)assignment.getProperty("type");
		    		  double grade = (Double)assignment.getProperty("grade");
		    		  double totalPoints = (Double)assignment.getProperty("totalPoints");
		    	      double weight = (Double)assignment.getProperty("weight");
		    	      Assignment a =  new Assignment(c_id, name, dueDate, assignedDate,type, grade, weight, totalPoints);
		    	   
		    	     assignments[i++]  = a;
		    	      
		    	  }
		    	     printArray(assignments);
		    	  }
		    	                 System.out.println(courseName + "coursename");
		    	  courses[x++] = new Course(c_id, courseName, average, assignments, id);
		    	    
		    }
		    
		    return courses;
	
	
	}
	
	void printArray(Object [] arr){
		System.out.println("Assignments");
		for(Object o : arr){
			System.out.println(o);
		}
	}
	
	public Teacher[] getTeachers(){
    
		Query q = new Query("Teacher");
		PreparedQuery pq = conn.prepare(q);
		
		    Teacher [] teachers = new Teacher[1000];
		    
		    Iterator<Entity> it = pq.asIterator();
			 int i = 0;
		    while(it.hasNext()){
		    	 Entity teacher = it.next();
		    	 String name = (String)teacher.getProperty("name");
	         String url = (String)teacher.getProperty("url");
			  teachers[i++] = new Teacher(url, name);
		   
			 
		    }
		    
		    Arrays.sort(teachers, 0, i);
		    return teachers;
	
	
	}
	
	
	public void insertTeacher(Teacher t){
		
		Entity teacher = new Entity("Teacher");
		teacher.setProperty("url", t.getPageUrl());
		teacher.setProperty("name", t.getName());
		
		conn.put(teacher);
		
	}
	
	public void deleteTeachers(){
		Query q = new Query("Teacher");
		PreparedQuery pq = conn.prepare(q);
		Iterator<Entity> it = pq.asIterator();
				while(it.hasNext()){
				   conn.delete(it.next().getKey());
				}
	}
	
    
	
	
}