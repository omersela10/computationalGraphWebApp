package test;

/// Advanced Programming exercise 4
/// Student Name: Ahigad Genish
/// ID : 31628022

public class IncAgent implements Agent {
	
	// Data Members
    private String[] subs;
    private String[] pubs;
    private Double value;

    // Constructor
    public IncAgent(String[] subs, String[] pubs) {
    	
        this.subs = subs;
        this.pubs = pubs;
        
        subscribeToInputTopic();
        publishToOutputTopic();
    }

    // Methods
    private void subscribeToInputTopic() {
    	if((subs.length > 0) == false)
    		return;
        TopicManagerSingleton.get().getTopic(subs[0]).subscribe(this);
    }

    private void publishToOutputTopic() {
     	if((pubs.length > 0) == false)
    		return;
        TopicManagerSingleton.get().getTopic(pubs[0]).addPublisher(this);
    }

    @Override
    public String getName() {
        return "IncAgent";
    }

    @Override
    public void reset() {
        this.value = 0.0;
    }

    @Override
    public void callback(String topic, Message msg) {
    	
    	if((subs.length > 0) == false)
    		return;
    	
        if (topic.equals(subs[0]) == true) {
        	
            this.value = msg.asDouble;
            publish(this.value + 1);
        }
    }

    private void publish(double result) {
    	
    	if((pubs.length > 0) == false)
    		return;
    	
        TopicManagerSingleton.get().getTopic(pubs[0]).publish(new Message(result));
    }

    @Override
    public void close() {
        
    }
}
