package servlets;

import configs.GraphConfig;
import graph.Graph;
import graph.TopicManagerSingleton;
import server.RequestParser;
import server.RequestParser.RequestInfo;
import java.util.regex.Pattern;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ConfLoader implements Servlet {

    @Override
    public void handle(RequestInfo ri, OutputStream toClient) throws IOException {

        byte[] content = ri.getContent();
        // Extract content excluding multipart boundaries
        String cleanedContent = extractFileContent(content);

        // Define the path where you want to save the uploaded file
        Path dirPath = Paths.get("C:\\Users\\USER\\git\\computationalGraphWebAppGit\\computationalGraphWebApp\\config_files");

        if (Files.exists(dirPath) == false) {
            Files.createDirectories(dirPath);
        }

        Path filePath = dirPath.resolve("uploadedFile");
        Files.write(filePath, cleanedContent.getBytes());

        resetGraphAndFields();

        GraphConfig graphConfig = new GraphConfig();
        graphConfig.setConfFile(String.valueOf(filePath));
        graphConfig.create();



        Graph theGraph = new Graph();
        theGraph.createFromTopics();

        if(theGraph.hasCycles() == true) {
            invalidParameterResponse(toClient);
            return;
        }

        String xmlParse = null;
        try {
            xmlParse = theGraph.generateXML();
        } catch (Exception e) {
            throw new IOException(e);
        }


        String response = "HTTP/1.1 200 OK\r\n\r\n" + xmlParse;
        toClient.write(response.getBytes());
    }

    @Override
    public void close() throws IOException {
        // Cleanup resources if necessary
    }

    private String extractFileContent(byte[] content) throws IOException {
        // Convert byte array to string
        String contentString = new String(content, "UTF-8");

        // Regex pattern to match and exclude boundary strings
        Pattern boundaryPattern = Pattern.compile("--(.*?)\\r?\\n");
        String cleanedContent = boundaryPattern.matcher(contentString).replaceAll("");

        // Remove any extra boundary markers or metadata
        return cleanedContent.trim();
    }

    private void resetGraphAndFields(){
        TopicManagerSingleton.get().clear();
        TopicDisplayer.topicToCurrentMessage.clear();
    }

    private void invalidParameterResponse(OutputStream toClient) throws IOException {
        String errorResponse = "Error : Cycle detected in the given graph";
        byte[] responseBytes = errorResponse.getBytes();

        // Setting HTTP response headers manually
        String headers = "HTTP/1.1 400 Bad Request\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: " + responseBytes.length + "\r\n" +
                "Connection: close\r\n\r\n";

        toClient.write(headers.getBytes());
        toClient.write(responseBytes);
        toClient.flush();
    }
}
