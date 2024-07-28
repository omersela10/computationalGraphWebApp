package graph;

import java.util.ArrayList;
import java.util.List;

/// Advanced Programming exercise 1
/// Student Name: Ahigad Genish
/// ID : 31628022

public class Topic {

    // Fields
    public final String name;
    private List<Agent> subscribers;
    private List<Agent> publishers;

    private String result = "";

    // Constructor
    Topic(String name){
        this.name = name;
        this.subscribers = new ArrayList<Agent>();
        this.publishers = new ArrayList<Agent>();
    }

    // Methods
    public void subscribe(Agent agent){
        this.subscribers.add(agent);
    }
    public void unsubscribe(Agent agent){
        this.subscribers.remove(agent);
    }

    public void publish(Message message){

        // Update result
        setResult(message.asText);

        for(Agent agent : this.subscribers){
            agent.callback(this.name, message);
        }
    }

    public void addPublisher(Agent agent){
        this.publishers.add(agent);
    }

    public void removePublisher(Agent agent){
        this.publishers.remove(agent);

    }

    public List<Agent> getPublishers(){
        return this.publishers;
    }
    public List<Agent> getSubscribers(){
        return this.subscribers;
    }

    public String getResult() {
        return this.result;
    }

    public void setResult(String anyResult){
        this.result = anyResult;
    }
}
