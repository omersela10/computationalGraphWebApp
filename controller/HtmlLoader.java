package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;

import test.RequestParser.RequestInfo;

public class HtmlLoader implements Servlet {
    private String basePath;

    public HtmlLoader(String basePath) {
        this.basePath = basePath;
    }

    @Override
    public void handle(RequestInfo requestInfo, OutputStream out) throws IOException {
        // Log request details
        System.out.println("Handling request for: " + requestInfo.getUri());

        // Log parameters
        requestInfo.getParameters().forEach((key, value) -> 
            System.out.println("Parameter: " + key + " = " + value)
        );

        // Log content
        System.out.println("Content: " + new String(requestInfo.getContent()));

        // Wrap OutputStream in PrintWriter
        try (PrintWriter writer = new PrintWriter(out)) {
            // Load HTML file
            File file = new File(getClass().getResource("").getPath() , basePath);
            if (file.exists() == true && file.isFile() == true) {
                writer.println("HTTP/1.1 200 OK");
                writer.println("Content-Type: text/html");
                writer.println("Content-Length: " + file.length());
                writer.println(); // Blank line between headers and body

                // Read and send the HTML content
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        writer.println(line);
                    }
                }
            } else {
                writer.println("HTTP/1.1 404 Not Found");
                writer.println("Content-Type: text/plain");
                writer.println();
                writer.println("File not found");
            }
        }
    }


	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}
}

