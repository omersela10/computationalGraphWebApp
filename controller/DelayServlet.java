package test;

import java.io.*;
import test.RequestParser.RequestInfo;

public class DelayServlet implements Servlet{

	    public DelayServlet() {
	    }

	    @Override
	    public void handle(RequestInfo requestInfo, OutputStream out) throws IOException {
	    	// Introduce a large delay
	        try {
	        	int count = 0;
	        	
	         	for(int i = 0; i < Integer.MAX_VALUE; i++) {
	               	for(int j = 0; j< Integer.MAX_VALUE; j++) {
		         		for(int k = 0; k< Integer.MAX_VALUE; k++) {
			        		count = i;
			        	}
		        	}
		        
	        	}
	 
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        // Send HTTP response
	        PrintWriter writer = new PrintWriter(out);
	        writer.println("HTTP/1.1 200 OK");
	        writer.println("Content-Type: text/plain");
	        writer.println();
	        writer.println("Done");
	        writer.flush();
	    }


		@Override
		public void close() throws IOException {
			// TODO Auto-generated method stub
			
		}
}



