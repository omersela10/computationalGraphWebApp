package model;

import java.util.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/// Advanced Programming exercise 3
/// Student Name: Ahigad Genish
/// ID : 31628022

public class Node {
	
    // Data Members
    private String name;
    private List<Node> edges;
    private Message msg;

    // Constructor
    public Node(String name){
        this.name = name;
        this.edges = new ArrayList<Node>();
    }

    // Methods

    // Getters & Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Node> getEdges() {
        return edges;
    }

    public void setEdges(List<Node> edges) {
        this.edges = edges;
    }

    public Message getMsg() {
        return msg;
    }

    public void setMsg(Message msg) {
        this.msg = msg;
    }


    public void addEdge(Node newNode){
        this.edges.add(newNode);
    }

    // Return if the current node is in some cycle in the graph
    public boolean hasCycles() {

        Set<Node> visited = new HashSet<>();
        Set<Node> recursionStack = new HashSet<>();

        // Detect cycle by dfs
        return dfs(this, visited, recursionStack);
    }

    private boolean dfs(Node currentNode, Set<Node> visited, Set<Node> recursionStack) {

        // Mark the current node as visited and add it to the recursion stack
        visited.add(currentNode);
        recursionStack.add(currentNode);

        // Traverse each neighbor
        for (Node neighbor : currentNode.getEdges()) {
            // If the neighbor has not been visited, recurse on the neighbor
            if (visited.contains(neighbor) == false) {
                if (dfs(neighbor, visited, recursionStack) == true) {
                    return true;
                }
            }
            // If the neighbor is in the recursion stack, a cycle is detected
            else if (recursionStack.contains(neighbor) == true) {
                return true;
            }
        }

        // Remove the current node from the recursion stack
        recursionStack.remove(currentNode);
        return false;
    }

}