package servlets;

import graph.Agent;
import graph.Topic;
import graph.TopicManagerSingleton;
import server.RequestParser;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

public class ResultHandler implements Servlet{


    @Override
    public void handle(RequestParser.RequestInfo ri, OutputStream toClient) throws Exception {

        String result = "";

        Collection<Topic> allTopics = TopicManagerSingleton.get().getTopics();

        for (Topic topic : allTopics) {

            if (topic.getSubscribers().size() == 0) { // Final topic
                if (topic.getPublishers().size() == 1) { // Get its agent
                    Agent agent = topic.getPublishers().get(0);

                    result = topic.name + " : " + agent.getResult();
                    break;
                }
            }

        }

        // Send a plain text response
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/plain\r\n" +
                "Content-Length: " + result.length() + "\r\n" +
                "\r\n" + result;
        toClient.write(response.getBytes());
        toClient.flush();
    }

    @Override
    public void close(){

    }
}
