package org.cheng.meepo.task.scheduler.taskmanager;

/**
 * 
 * Created by ChengLi on 2016/6/19.
 *
 */
public class Version {
	
   private final static String version="agz-task-1.0.0";
   
   public static String getVersion(){
	   return version;
   }
   public static boolean isCompatible(String dataVersion){
	   return version.compareTo(dataVersion) >= 0;
   }
   
}
