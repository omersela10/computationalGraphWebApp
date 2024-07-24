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
        // Log and process the expression
        System.out.println("Received expression: " + expression);

        // Parse and convert the expression
        List<String> parsedExpression = ExpressionParser.parseExpression(expression);
        System.out.println("Parsed Expression: " + parsedExpression);
        String config = ExpressionParser.convertToConfiguration(parsedExpression);

        // Save the configuration to a file
        Path dirPath = Paths.get("C:\\Users\\USER\\git\\computationalGraphWebAppGit\\computationalGraphWebApp\\config_files");

        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        Path filePath = dirPath.resolve("uploadedFile");
        Files.write(filePath, config.getBytes());

        resetGraphAndFields();

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

    }

    private void resetGraphAndFields(){
        TopicManagerSingleton.get().clear();
        TopicDisplayer.topicToCurrentMessage.clear();
    }

}
