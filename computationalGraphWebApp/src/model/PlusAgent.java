package model;

/// Advanced Programming exercise 4
/// Student Name: Ahigad Genish
/// ID : 31628022

public class PlusAgent implements Agent {
	
	// Data Members
    private String[] subs;
    private String[] pubs;
    private double x = 0;
    private double y = 0;

    // Constructor
    public PlusAgent(String[] subs, String[] pubs) {
        this.subs = subs;
        this.pubs = pubs;

    	
        subscribeToInputTopics();
        publishToOutputTopic();
    }

    // Methods
    
    private void subscribeToInputTopics() {
    	
        if((subs.length > 1) == false)
    		return;
        
        TopicManagerSingleton.get().getTopic(subs[0]).subscribe(this);
        TopicManagerSingleton.get().getTopic(subs[1]).subscribe(this);
    }

    private void publishToOutputTopic() {
    	
    	if((pubs.length > 0) == false)
    		return;
    	TopicManagerSingleton.get().getTopic(pubs[0]).addPublisher(this);
    }

    @Override
    public String getName() {
        return "PlusAgent";
    }

    @Override
    public void reset() {
        this.x = 0;
        this.y = 0;
    }

    @Override
    public void callback(String topic, Message msg) {
    	
    	if((subs.length > 1) == false)
    		return;
    	
        if (topic.equals(subs[0]) == true) {
            this.x = msg.asDouble;
        } else if (topic.equals(subs[1]) == true) {
            this.y = msg.asDouble;
        }
        if (Double.isNaN(x) == false && Double.isNaN(y) == false) {
            publish(this.x + this.y);
        }
    }

    private void publish(double result) {
    	if(pubs.length > 0)
    		TopicManagerSingleton.get().getTopic(pubs[0]).publish(new Message(result));
    }

    @Override
    public void close() {
       
    }
}

