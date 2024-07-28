package graph;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/// Advanced Programming exercise 2
/// Student Name: Ahigad Genish
/// ID : 31628022

public class ParallelAgent implements Agent {

    // Helper pair class
    private static class Pair<T, K>{

        private T  item1;
        private K item2;

        public Pair(T item1, K item2){
            this.item1 = item1;
            this.item2 = item2;
        }
    }

    // Data Members
    private volatile boolean stop;
    private Agent agent;
    private BlockingQueue<Pair<String,Message>> messageQueue;

    // Sentinel value for closing the service
    private static final Pair<String, Message> CLOSE_SENTINEL = new Pair<>(null, null);

    // Constructor
    public ParallelAgent(Agent anyAgent, int capacity){
        this.agent = anyAgent;
        this.messageQueue = new ArrayBlockingQueue<Pair<String,Message>>(capacity);
        this.stop = false;
        startActiveObject();
    }

    // Methods
    @Override
    public String getName() {
        return agent.getName();
    }

    @Override
    public void reset() {
        this.agent.reset();
    }

    @Override
    public void callback(String topic, Message msg)  {
        Pair<String, Message> pair = new Pair<String, Message>(topic, msg);
        addToQueue(pair);
    }

    @Override
    public void close() {
        stop = true;
        addToQueue(CLOSE_SENTINEL);
    }


    // Start active object run in background
    public void startActiveObject() {
        new Thread(() -> {
            while (stop == false) {
                try {
                    Pair<String, Message> topicMessage = this.messageQueue.take();
                    if (topicMessage == CLOSE_SENTINEL) {
                        break;
                    }
                    applyCallbackOnAgent(topicMessage);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }

    // Apply callback on the agent
    private void applyCallbackOnAgent(Pair<String, Message> topicMessage) {
        String topic = topicMessage.item1;
        Message message = topicMessage.item2;
        this.agent.callback(topic, message);
    }

    // Add item to blocking queue
    private void addToQueue(Pair<String, Message> pair) {
        try {
            this.messageQueue.put(pair);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
