package servlets;

import configs.ExpressionParser;
import configs.GraphConfig;
import graph.Graph;
import graph.TopicManagerSingleton;
import server.RequestParser;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ExpressionHandler implements Servlet{
    @Override
    public void handle(RequestParser.RequestInfo ri, OutputStream toClient) throws Exception {
        // Get the request body as a string
        String requestBody = new String(ri.getContent());


        // Extract the expression using string manipulation or regular expressions (if needed)
        String expression =  requestBody.split("\n")[0];

        try {
            // Parse and convert the expression
            List<String> parsedExpression = ExpressionParser.parseExpression(expression);
            String config = ExpressionParser.convertToConfiguration(parsedExpression);

            // Save the configuration to a file
            Path dirPath = Paths.get("./config_files");

            if (Files.exists(dirPath) == false) {
                Files.createDirectories(dirPath);
            }

            Path filePath = dirPath.resolve("uploadedFile");
            Files.write(filePath, config.getBytes());
            resetGraphAndFields();

            GraphConfig graphConfig = new GraphConfig();
            graphConfig.setConfFile(String.valueOf(filePath));
            graphConfig.create();
        }
        catch (Exception e){
            invalidParameterResponse(toClient, "Invalid expression");
            return;
        }

        Graph theGraph = new Graph();
        theGraph.createFromTopics();

        if(theGraph.hasCycles() == true){
            invalidParameterResponse(toClient , "Cycle detected in the given graph");
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

    }

    private void resetGraphAndFields(){
        TopicManagerSingleton.get().clear();
        TopicDisplayer.topicToCurrentMessage.clear();
    }

    private void invalidParameterResponse(OutputStream toClient, String errorMessage) throws IOException {
        String errorResponse = "Error : " + errorMessage;
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
