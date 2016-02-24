package com.kevinturner.jv;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.http.*;


@SuppressWarnings("serial")
public class JvappServlet extends HttpServlet {
	JVDBManager db = new JVDBManager();
	
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
	   int type = Integer.parseInt(req.getParameter("type"));
	   switch(type){
	     case 0:
		   updateGrades();
		   break;
	     case 1:
	    	    updateAnnouncements();
		   break;
	   }
	}
	
	
	public void updateAnnouncements(){
		db.deleteAnnouncements();
		Fetcher<Announcement> af = new announcementsFetcher();
		Announcement [] announcements = af.fetch();
		if(announcements.length > 0){
			for(Announcement a : announcements){
				db.insertAnnouncements(a.getDate(), a.getAnnouncement());
			}
		}
	}
	
	/*
	public void updateGrades(){
		Student [] allStudents  = db.getAllStudents();
		for(Student s : allStudents){
			Course [] courses = s.getCourses();
			Fetcher <Course> gradeFetcher = new gradeFetcher(s);
			Course [] newCourses = gradeFetcher.fetch();
			for(int i = 0; i < courses.length; i++){
				Assignment [] assignments = courses[i].getAssignments();
				Assignment [] newAssignments = newCourses[i].getAssignments();
				for(Assignment as : newAssignments){
					if(!exists(as, assignments))
						db.insertCourses(newCourses);
				}
			}
		}
	}
	
	*/
	
	
	private boolean exists(Assignment a, Assignment[] array){
		for(Assignment assign : array){
			if(assign.equals(a))
				return true;
		}
		
		return false;
	}
	
	
	public void updateGrades(){
	   Student [] allStudents = db.getAllStudents();
	   for(Student s : allStudents){
		  Course [] courses = s.getCourses();
		   for(Course c: courses){
			   if(c != null)
			     db.deleteAssignments(c);
		   }
		   db.deleteCourses(s.getUser());
		   Fetcher <Course> gf = new gradeFetcher(s);
		   Course [] newCourses = gf.fetch();
		   db.insertCourses(newCourses);
	   }
	   
	
	}
	

	
}
