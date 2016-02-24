package com.kevinturner.jv;

import java.sql.Connection;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

public abstract class DatabaseManager {
	private DatastoreService  conn;
    
	
	   
	   public DatabaseManager(){
		   establishConnection();
	   }
	   
	   private void establishConnection(){
		  this.conn = DatastoreServiceFactory.getDatastoreService();
	   }
	   
	   
	   public DatastoreService getConnection(){
		   return conn;
	   }
	   
	   
	   
	   
	   
}