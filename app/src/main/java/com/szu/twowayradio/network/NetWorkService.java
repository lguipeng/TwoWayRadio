package com.szu.twowayradio.network;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class NetWorkService {
	
	 private static NetWorkService service;
	 private final ExecutorService pool;
	 private final static int DEFAULT_SIZE=5;
	 
	 public NetWorkService()
	 {
		 this(DEFAULT_SIZE);
	 }
     public NetWorkService(int poolSize)
     {
    	 pool=Executors.newFixedThreadPool(poolSize);    	 
     }
     
     public static NetWorkService getInstance(int poolSize)
     {
    	 if(service==null)
    	 {
    		 service=new NetWorkService(poolSize);
    	 }
    	 return service;
     }
     
     public static NetWorkService getDefaultInstance(int poolSize)
     {
    	 if(service==null)
    	 {
    		 service=NetWorkService.getDefaultInstance(poolSize);
    	 }
    	 return service;
     }
     
     public void shutDown()
     {
    	 pool.shutdown(); // Disable new tasks from being submitted
    	   try {
    	     // Wait a while for existing tasks to terminate
    	     if (!pool.awaitTermination(60, TimeUnit.SECONDS)) 
    	        pool.shutdownNow(); // Cancel currently executing tasks
    	       // Wait a while for tasks to respond to being cancelled
    	       if (!pool.awaitTermination(60, TimeUnit.SECONDS))
    	           System.err.println("Pool did not terminate");
    	     
    	   } catch (InterruptedException ie) {
    	     // (Re-)Cancel if current thread also interrupted
    	     pool.shutdownNow();
    	     // Preserve interrupt status
    	     Thread.currentThread().interrupt();
    	   }

     }
	 public ExecutorService getPool() {
		return pool;
	}
     
}
