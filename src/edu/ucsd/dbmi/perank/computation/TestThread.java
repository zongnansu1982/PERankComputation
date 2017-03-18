package edu.ucsd.dbmi.perank.computation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestThread {
	
	public static void main(String[] args) throws Exception {
		ExecutorService pool = Executors.newFixedThreadPool(10);
		
		for(int i=0; i<10000; i++){
			pool.submit(new TestRunnable( i));
		}
		pool.shutdown();
		while(!pool.awaitTermination(10, TimeUnit.SECONDS))
        {
			 System.err.println(System.currentTimeMillis());
        }
		
		if(pool.isTerminated()){
			System.out.println("end");
		}
	}
}
