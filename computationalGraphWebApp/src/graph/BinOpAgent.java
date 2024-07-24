package graph;
import java.util.*;
import java.util.function.BinaryOperator;

/// Advanced Programming exercise 3
/// Student Name: Ahigad Genish
/// ID : 31628022
public class BinOpAgent implements Agent {

	// Fields
    private String name;
    private String firstInputTopicName;
    private String secondInputTopicName;
    private String outputTopicName;
    private BinaryOperator<Double> operation;

    private Double firstInput;
    private Double secondInput;

    private String result = "";

    public static final Map<String, BinaryOperator<Double>> operators = new HashMap<>() {{
        put("plus", (x, y) -> x + y);
        put("minus", (x, y) -> x - y);
        put("mul", (x, y) -> x * y);
        put("div", (x, y) -> x / y);
        put("power", (x, y) -> Math.pow(x, y));
    }};

    // Constructors
    public BinOpAgent(String name, String[] inputTopicNames, String[] outputTopicNames, BinaryOperator<Double> operation) {
        
    	this.name = name;
        this.firstInputTopicName = inputTopicNames[0];
        this.secondInputTopicName = inputTopicNames[1];
        this.outputTopicName = outputTopicNames[0];
        this.operation = operation;

        subscribeToInputTopics();
        publishToOutputTopic();
    }
    public BinOpAgent(String name, String[] inputTopicNames, String[] outputTopicNames, String operatorName) {
        this(name, inputTopicNames, outputTopicNames, operators.get(operatorName));
    }
    
    // Subscribe this agent to given input topics
    private void subscribeToInputTopics() {
    	
        Topic firstInput = TopicManagerSingleton.get().getTopic(firstInputTopicName);
        firstInput.subscribe(this);
        
        Topic secondInput = TopicManagerSingleton.get().getTopic(secondInputTopicName);
        secondInput.subscribe(this);
       
    }

    // Add this agent to the given output topic as publisher
	private void publishToOutputTopic() {
		Topic output = TopicManagerSingleton.get().getTopic(outputTopicName);
        output.addPublisher(this);
	}

	// Execute the math operation
    private void executeMathOperation() {

        Double result = operation.apply(firstInput, secondInput);
        this.result = result.toString();
        publish(result);
    }

    // Getters
    public String getName() {
        return this.name;
    }

    // Method to reset the agent's state
    public void reset() {
        this.firstInput= 0.0;
        this.secondInput = 0.0;
    }

    // Callback method to process messages
    public void callback(String topic, Message msg) {
    	
        if (topic.equals(firstInputTopicName) == true) {
            firstInput = msg.asDouble;
        } else if (topic.equals(secondInputTopicName) == true) {
            secondInput = msg.asDouble;
        }

        if(firstInput != null && secondInput != null)
        	executeMathOperation();
    }

    // Method to close the agent and release resources
    public void close() {

    }

    @Override
    public String getResult() {
        return this.result;
    }

    // Method to publish the result to the output topic
    private void publish(Double result) {
        Topic output = TopicManagerSingleton.get().getTopic(outputTopicName);
        output.publish(new Message(result));
    }
}
