package graph;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

public class UnOpAgent implements Agent{
    // Fields
    private String name;
    private String inputTopicName;
    private String outputTopicName;
    private UnaryOperator<Double> operation;

    private String result = "";
    private Double input;

    public static final Map<String, UnaryOperator<Double>> operators = new HashMap<>() {{
        put("inc", x -> x + 1);
        put("dec", x -> x - 1);
        put("squareroot", x -> Math.sqrt(x)); // Note the corrected spelling to "squareroot"
        put("ln", x -> Math.log(x));
        put("log10", x -> Math.log10(x));
        put("exponent", x -> Math.exp(x));
    }};

    // Constructor
    public UnOpAgent(String name, String[] inputTopicNames, String[] outputTopicNames, UnaryOperator<Double> operation) {

        this.name = name;
        this.inputTopicName = inputTopicNames[0];
        this.outputTopicName = outputTopicNames[0];
        this.operation = operation;

        subscribeToInputTopics();
        publishToOutputTopic();
    }

    public UnOpAgent(String name, String[] inputTopicNames, String[] outputTopicNames, String operatorName){
        this(name, inputTopicNames, outputTopicNames, operators.get(operatorName));
    }

    // Subscribe this agent to given input topics
    private void subscribeToInputTopics() {

        Topic input = TopicManagerSingleton.get().getTopic(inputTopicName);
        input.subscribe(this);
    }

    // Add this agent to the given output topic as publisher
    private void publishToOutputTopic() {
        Topic output = TopicManagerSingleton.get().getTopic(outputTopicName);
        output.addPublisher(this);
    }

    // Execute the math operation
    private void executeMathOperation() {

        Double result = operation.apply(input);
        this.result = result.toString();
        publish(result);
    }

    // Getters
    public String getName() {
        return this.name;
    }

    // Method to reset the agent's state
    public void reset() {
        this.input = 0.0;
    }

    // Callback method to process messages
    public void callback(String topic, Message msg) {

        if (topic.equals(inputTopicName) == true)
            input = msg.asDouble;

        if(input != null)
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
