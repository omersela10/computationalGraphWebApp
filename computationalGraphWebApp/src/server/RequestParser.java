package server;

import java.io.*;
import java.util.*;

/// Advanced Programming exercise 5
/// Student Name: Ahigad Genish
/// ID : 31628022


/**
 * The {@code RequestParser} class provides utility methods to parse HTTP requests.
 * It extracts information such as HTTP command, URI, parameters, headers, and content
 * from the request.
 * <p>
 * Example usage:
 * <pre>
 * {@code
 * BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
 * RequestParser.RequestInfo requestInfo = RequestParser.parseRequest(reader);
 * }
 * </pre>
 */
public class RequestParser {

	// Parse given request

    /**
     * Parses an HTTP request from the provided {@code BufferedReader}.
     *
     * @param reader the reader to read the request from
     * @return a {@code RequestInfo} object containing parsed request details
     * @throws IOException if an I/O error occurs while reading the request
     */
	public static RequestInfo parseRequest(BufferedReader reader) throws IOException {
        
		String requestLine = reader.readLine();
		
        if (requestLine == null || requestLine.isEmpty() == true) {
            throw new IOException("Empty request line");
        }

        String[] requestParts = requestLine.split(" ");
        
        if (requestParts.length < 3) {
        	
            throw new IOException("Invalid request line: " + requestLine);
        }

        // Initialize HTTP command (e.g POST / GET / DELETE)
        String httpCommand = requestParts[0];
        // Initialize URI 
        String uri = requestParts[1];
        // Initialize URI segments
        String[] uriSegments = uri.split("/");
        // Initialize parameters
        Map<String, String> parameters = new HashMap<>();
        // Initialize header
        Map<String, String> headers = new HashMap<>();
        // Initialize content
        byte[] content = new byte[0];

        // Parse URI and parameters
        int queryIndex = uri.indexOf("?");
        
        // If there are parameters
        if (queryIndex != -1) {
        	
            String queryString = uri.substring(queryIndex + 1);
            String parseUri = uri.substring(0, queryIndex);
            // Update URI segments
            uriSegments = parseUri.split("/");
            
            // Parse parameters
            parameters = parseParameters(queryString, "&");
        }
        
        uriSegments = removeEmptySegments(uriSegments);
        
        // Read headers, additional parameters, and content
        StringBuilder headerReader = new StringBuilder();
        int contentLength = 0;
        
        String line;

   
        // Parse header section
        while (reader.ready() == true) {
        	
        	// Read line and append to string builder
            line = reader.readLine();
            headerReader.append(line).append("\n");
            
          
            // If reach the first '\n' - break
            if (line.isEmpty() == true) {
                break;
            }
            // O.W - parse header
            String[] headerParts = line.split(":", 2);
            
            if (headerParts.length == 2) {
                
            	String headerName = headerParts[0].trim().toLowerCase();
                String headerValue = headerParts[1].trim();
                
                headers.put(headerName, headerValue);
                
                if (headerName.equals("content-length")) {
                    contentLength = Integer.parseInt(headerValue);
                }
            }
        }

        StringBuilder parametersReader = new StringBuilder();
        StringBuilder contentReader = new StringBuilder();
        
        if(contentLength == 0) {
        	
        	if(reader.ready() == true){
        		// Parameter
        		
        	    // Parse parameter section
                while (reader.ready() == true) {
                	
                	// Read line and append to string builder
                    line = reader.readLine();
                    parametersReader.append(line);
                    
                    if (line.isEmpty() == true) {
                    	break;
                    } 
                    else {
                    	parameters.putAll(parseParameters(line, "\n"));
                    }
                }
        	}
        }


        else{

        	// Read all
        	StringBuilder firstReader = new StringBuilder();
        	StringBuilder secondReader = new StringBuilder();
        	
        	// Parse first section
            while (reader.ready() == true) {
            	
            	// Read line and append to string builder
                line = reader.readLine();
               
                if (line.isEmpty() == true) {
                	break;
                } 
                
                firstReader.append(line).append("\n");
            }
            	

        	// Parse second section
            while (reader.ready() == true) {
            	
            	// Read line and append to string builder
                line = reader.readLine();
                
                if (line.isEmpty() == true) {
                	break;
                } 
                
                secondReader.append(line).append("\n");
            }
            
            if(secondReader.length() == 0 && firstReader.length() == 0)
            	throw new IOException("Invalid request format");
            
            else if(secondReader.length() == 0) {
            	contentReader = firstReader;
            }
            else {
            	String[] lines = firstReader.toString().split("\n");
            	
            	for(String parameterLine : lines)
            		parameters.putAll(parseParameters(parameterLine, "\n"));
            
            	contentReader = secondReader;
            
            }
        }
        
        // Convert content to bytes
        content = contentReader.toString().getBytes();
      
        return new RequestInfo(httpCommand, uri, uriSegments, parameters, content);
    }

	// Parse parameters in given string
    private static Map<String, String> parseParameters(String queryString, String delimiter) {
    	
    	// Initialize Map
        Map<String, String> parameters = new HashMap<>();
        // Split by &
        String[] pairs = queryString.split(delimiter);
        
        for (String pair : pairs) {
        	// Split by =
            String[] keyValue = pair.split("=");
            
            // Insert into map
            if (keyValue.length == 2) {
                parameters.put(keyValue[0], keyValue[1]);
            } 
            else if (keyValue.length == 1) {
                parameters.put(keyValue[0], "");
            }
        }
        return parameters;
    }

    // Remove empty segments
    private static String[] removeEmptySegments(String[] segments) {
        return Arrays.stream(segments)
                               .filter(segment -> segment != null && segment.isEmpty() == false)
                               .toArray(String[]::new);
    }
    
	// RequestInfo given internal class
    /**
     * The {@code RequestInfo} class holds information about an HTTP request,
     * including the HTTP command, URI, parameters, headers, and content.
     */
    public static class RequestInfo {
        private final String httpCommand;
        private final String uri;
        private final String[] uriSegments;
        private final Map<String, String> parameters;
        private final byte[] content;

        /**
         * Constructs a {@code RequestInfo} object with the provided details.
         *
         * @param httpCommand the HTTP command (e.g., "GET", "POST")
         * @param uri the request URI
         * @param parameters a map of request parameters
         * @param content the content of the request body
         */
        public RequestInfo(String httpCommand, String uri, String[] uriSegments, Map<String, String> parameters, byte[] content) {
            this.httpCommand = httpCommand;
            this.uri = uri;
            this.uriSegments = uriSegments;
            this.parameters = parameters;
            this.content = content;
        }

        public String getHttpCommand() {
            return httpCommand;
        }

        public String getUri() {
            return uri;
        }

        public String[] getUriSegments() {
            return uriSegments;
        }

        public Map<String, String> getParameters() {
            return parameters;
        }

        public byte[] getContent() {
            return content;
        }
    }
}
