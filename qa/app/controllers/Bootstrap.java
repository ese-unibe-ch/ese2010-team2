package controllers;

import models.User;
import play.jobs.*;
 
@OnApplicationStart
public class Bootstrap extends Job {
    
    public void doJob() {
    		@SuppressWarnings("unused")
			User user = new User("admin", "admin@admin.ch", "admin");
    }
    
}

