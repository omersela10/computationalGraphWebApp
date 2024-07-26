package graph;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.*;

/// Advanced Programming exercise 3
/// Student Name: Ahigad Genish
/// ID : 31628022

import graph.TopicManagerSingleton.TopicManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;



public class Graph extends ArrayList<Node>{

	// Data members
	private Integer agentCounter = 1;


	// Methods

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
					Node agentNode = new Node("A" + this.agentCounter.toString() + " " + agent.getName());
        			this.add(agentNode);

					agentCounter += 1;
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
					Node agentNode = new Node("A" + this.agentCounter.toString() + " " + agent.getName());
        			this.add(agentNode);

					agentCounter += 1;
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

	// Method to generate XML from the graph
	public String generateXML() throws Exception {
		// Create a DocumentBuilderFactory
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// Create a new Document
		Document doc = docBuilder.newDocument();

		// Create the root element
		Element rootElement = doc.createElement("graph");
		doc.appendChild(rootElement);

		// Iterate through nodes in the graph
		for (Node node : this) {
			// Create node element
			Element nodeElement = doc.createElement("node");
			nodeElement.setAttribute("id", node.getName());
			rootElement.appendChild(nodeElement);

			// Add edges
			for (Node neighbor : node.getEdges()) {
				Element edgeElement = doc.createElement("edge");
				edgeElement.setAttribute("to", neighbor.getName());
				nodeElement.appendChild(edgeElement);
			}
		}

		// Convert the Document to a String
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		DOMSource source = new DOMSource(doc);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		transformer.transform(source, result);

		return writer.toString();
	}




}
