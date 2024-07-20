package servlets;

import configs.GraphConfig;
import graph.Graph;
import server.RequestParser;
import server.RequestParser.RequestInfo;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ConfLoader implements Servlet {
    @Override
    public void handle(RequestInfo ri, OutputStream toClient) throws IOException {

        byte[] content = ri.getContent();

        // Define the path where you want to save the uploaded file
        Path dirPath = Paths.get("C:\\Users\\USER\\git\\computationalGraphWebAppGit\\computationalGraphWebApp\\config_files");

        if (Files.exists(dirPath) == false) {
            Files.createDirectories(dirPath);
        }

        Path filePath = dirPath.resolve("uploadedFile");
        Files.write(filePath, content);


        GraphConfig graphConfig = new GraphConfig();
        graphConfig.setConfFile(String.valueOf(filePath));
        graphConfig.create();

        Graph theGraph = new Graph();
        theGraph.createFromTopics();

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

}
