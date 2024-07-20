package server;

import server.RequestParser.RequestInfo;
import servlets.Servlet;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;


/// Advanced Programming exercise 5
/// Student Name: Ahigad Genish
/// ID : 31628022

public class MyHTTPServer extends Thread implements HTTPServer{
    
	// Data Members
	private int port;
	private int numberOfThreads = 0;
	private ServerSocket serverSocket;
    private ExecutorService threadPool;
    
	private ConcurrentHashMap<String, Servlet> getHttpCommandMap = new ConcurrentHashMap<String, Servlet>();
	private ConcurrentHashMap<String, Servlet> postHttpCommandMap = new ConcurrentHashMap<String, Servlet>();
	private ConcurrentHashMap<String, Servlet> deleteHttpCommandMap = new ConcurrentHashMap<String, Servlet>();
	
	// Constructor
    public MyHTTPServer(int port,int nThreads){
    	this.port = port;
    	this.numberOfThreads = nThreads;
    }

    // Methods
    
    // Add servlet into the map
    public void addServlet(String httpCommand, String uri, Servlet s){
        switch(httpCommand.toUpperCase()) {
            case "GET":
                this.getHttpCommandMap.put(uri, s);
                break;
            case "POST":
                this.postHttpCommandMap.put(uri, s);
                break;
            case "DELETE":
                this.deleteHttpCommandMap.put(uri, s);
                break;
            default:
                throw new IllegalArgumentException("Unsupported HTTP command: " + httpCommand);
        }
    }

    // Remove servlet from the map
    public void removeServlet(String httpCommand, String uri){
        switch(httpCommand.toUpperCase()) {
            case "GET":
                this.getHttpCommandMap.remove(uri);
                break;
            case "POST":
                this.postHttpCommandMap.remove(uri);
                break;
            case "DELETE":
                this.deleteHttpCommandMap.remove(uri);
                break;
            default:
                throw new IllegalArgumentException("Unsupported HTTP command: " + httpCommand);
        }
    }

    // Run server method (apply by start)
    public void run(){
    	 try {
             serverSocket = new ServerSocket(port);
             threadPool = Executors.newFixedThreadPool(numberOfThreads);
             System.out.println("HTTP server started on port " + port);
             
             while (serverSocket.isClosed() == false) {
                 try {
                	 Thread.sleep(1000);
                     Socket clientSocket = serverSocket.accept();
                     // Handle client
                     threadPool.execute(() -> handleRequest(clientSocket));
                 } catch (IOException e) {
                     if (serverSocket.isClosed() == true) {
                         System.out.println("Server socket closed.");
                         break;
                     }
                     e.printStackTrace();
                 } catch (InterruptedException e) {
					e.printStackTrace();
				}
             }
         } catch (IOException e) {
             e.printStackTrace();
         }
    }

    // Close server method
    public void close() {
    	// Shut down
    	threadPool.shutdown();
    	 // Wait for all tasks to finish or timeout after 60 seconds
        try {
        	// Await termination of running tasks or force shut down if not finish after 60 seconds
			if (threadPool.awaitTermination(60, TimeUnit.SECONDS) == true) 
				threadPool.shutdownNow(); // Force shutdown if tasks are not finished
			
			// Close server socket
			if(serverSocket != null)
				serverSocket.close();
			
			// Close all servlets
			closeServlets();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        System.out.println("HTTP server shut down.");
    }
    
    // Close servlets
    private void closeServlets() throws IOException {
		
    	// Close get http servlets
    	for(Servlet servlet : getHttpCommandMap.values()) {
			servlet.close();
		}
    	// Close post http servlets
		for(Servlet servlet : postHttpCommandMap.values()) {
			servlet.close();
		}
		// Close delete http servlets
		for(Servlet servlet : deleteHttpCommandMap.values()) {
			servlet.close();
		}
		
	}

    // Handle client request
	private void handleRequest(Socket clientSocket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream out = clientSocket.getOutputStream()) {
            // Parse request
            RequestInfo requestInfo = RequestParser.parseRequest(reader);
            Servlet servlet = null;
            
            // Match the URI to the servlet with the longest prefix
            switch(requestInfo.getHttpCommand().toUpperCase()) {
                case "GET":
                    servlet = matchUriToServlet(getHttpCommandMap, requestInfo.getUri());
                    break;
                case "POST":
                    servlet = matchUriToServlet(postHttpCommandMap, requestInfo.getUri());
                    break;
                case "DELETE":
                    servlet = matchUriToServlet(deleteHttpCommandMap, requestInfo.getUri());
                    break;
            }

            if (servlet != null) {
                servlet.handle(requestInfo, out);
            } else {
                out.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

	// Return the match servlet to the given uri
    private Servlet matchUriToServlet(ConcurrentHashMap<String, Servlet> commandMap, String uri) {
        
    	Servlet matchedServlet = null;
        int longestMatchLength = -1;

        for (String key : commandMap.keySet()) {
        	
            if (uri.startsWith(key) == true && key.length() > longestMatchLength) {
                matchedServlet = commandMap.get(key);
                longestMatchLength = key.length();
            }
        }

        return matchedServlet;
    }

}