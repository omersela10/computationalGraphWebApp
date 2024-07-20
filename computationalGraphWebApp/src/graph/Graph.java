package graph;

import java.util.ArrayList;
import java.util.*;

/// Advanced Programming exercise 3
/// Student Name: Ahigad Genish
/// ID : 31628022

import graph.TopicManagerSingleton.TopicManager;

public class Graph extends ArrayList<Node>{

	// Detect Cycle in graph
    public boolean hasCycles() {
       for(Node node : this) {
    	   if(node.hasCycles() == true)
    		   return true;
       }
       return false;
    }
    
    // Method to initialize the graph from the topics
    public void createFromTopics() {

    	this.clear();
    	
    	// Map each seen agent to a node
    	Map<Agent, Node> agentSeen = new HashMap<Agent, Node>();
    	
    	// Iterate over the topics
        for (Topic topic : TopicManagerSingleton.get().getTopics()) {
           
        	// Create topic node
        	Node topicNode = new Node("T" + topic.name);
        	
        	// Iterate over the topic subscribers
        	for(Agent agent : topic.getSubscribers()) {
        		
        		// If not seen this agent yet
        		if(agentSeen.containsKey(agent) == false){
        			
        			// Add this as node to the graph
        			Node agentNode = new Node("A" + agent.getName());
        			this.add(agentNode);
        			// Mark as seen
        			agentSeen.put(agent, agentNode);
        		}
        		// Set agent node as the current topic node neighbor
        		topicNode.addEdge(agentSeen.get(agent));
        		
        	}
        	// Iterate over the topic publishers
        	for(Agent agent : topic.getPublishers()) {
        		
        		// If not seen this agent yet
        		if(agentSeen.containsKey(agent) == false){
        			// Add this as node to the graph
        			Node agentNode = new Node("A" + agent.getName());
        			this.add(agentNode);
        			
        			// Mark as seen
        			agentSeen.put(agent, agentNode);
        		}
        		// Set topic node as the agent node neighbor
        		agentSeen.get(agent).addEdge(topicNode);
        		
        	}
        	// Add topic node to graph
        	this.add(topicNode);
        	
        }

    }


}
