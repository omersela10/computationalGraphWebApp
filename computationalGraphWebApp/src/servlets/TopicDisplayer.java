package servlets;

import graph.Agent;
import graph.Message;
import graph.Topic;
import graph.TopicManagerSingleton;
import server.RequestParser;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

public class TopicDisplayer implements Servlet {

    // Share member
    public static Map<String, String> topicToCurrentMessage = new HashMap<>();

    // Constructor
    public TopicDisplayer() {

    }

    @Override
    public void handle(RequestParser.RequestInfo ri, OutputStream toClient) throws Exception {

        Map<String, String> parameters = ri.getParameters();

        // Get the topic ang message from parameters
        String topicName = parameters.get("topicName");
        String message = parameters.get("message");

        // All topics
        Collection<Topic> allTopics = TopicManagerSingleton.get().getTopics();

        boolean found = false;

        // Look for this topic
        for (Topic topic : allTopics) {
            if (topic.name.equals(topicName) && topic.getPublishers().size() == 0) {
                found = true;
                break;
            }
        }

        if (found == false) {
            // If invalid topic asked for
            invalidParameterResponse(toClient, topicName);
            return;
        }

        // Publish new message
        Topic theTopic = TopicManagerSingleton.get().getTopic(topicName);
        theTopic.publish(new Message(message));

        // Insert and update into map
        this.topicToCurrentMessage.put(topicName, message);

        // Update all topics value
        for (Topic topic : allTopics) {

            // If it's source node and not seen this topic yet
            if (topic.getPublishers().size() == 0 && this.topicToCurrentMessage.containsKey(topic.name) == false) {
                this.topicToCurrentMessage.put(topic.name, "");
            }
            for (Agent agent : topic.getPublishers()) {

                // Get new result from agent
                String currentValue = agent.getResult();
                this.topicToCurrentMessage.put(topic.name, currentValue);
            }
        }

        // Generate the new body content
        StringBuilder newBodyContent = new StringBuilder();
        newBodyContent.append("<div>\n")
                .append("    <h1>Topic Messages</h1>\n")
                .append("    <table>\n")
                .append("        <thead>\n")
                .append("            <tr>\n")
                .append("                <th>Topic</th>\n")
                .append("                <th>Message</th>\n")
                .append("            </tr>\n")
                .append("        </thead>\n")
                .append("        <tbody>\n");

        for (Map.Entry<String, String> entry : topicToCurrentMessage.entrySet()) {
            newBodyContent.append("            <tr>\n")
                    .append("                <td>").append(entry.getKey()).append("</td>\n")
                    .append("                <td>").append(entry.getValue()).append("</td>\n")
                    .append("            </tr>\n");
        }

        newBodyContent.append("        </tbody>\n")
                .append("    </table>\n")
                .append("</div>\n");


        // Write the HTTP response headers
        String responseContent = newBodyContent.toString();
        byte[] responseBytes = responseContent.getBytes();

        // Setting HTTP response headers manually
        String headers = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: " + responseBytes.length + "\r\n" +
                "Connection: close\r\n\r\n";

        toClient.write(headers.getBytes());
        toClient.write(responseBytes);
        toClient.flush();
    }

    // Return invalid 404 response
    private void invalidParameterResponse(OutputStream toClient, String topicName) throws IOException {
        String errorResponse = "<div>Invalid Topic: " + topicName + "</div>";
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


    @Override
    public void close() throws IOException {

    }
}
