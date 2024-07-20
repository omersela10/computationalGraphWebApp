package model;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/// Advanced Programming exercise 1
/// Student Name: Ahigad Genish
/// ID : 31628022

public class TopicManagerSingleton {

    public static class TopicManager{

        // Fields
        private static final TopicManager instance = new TopicManager();
        private ConcurrentHashMap<String, Topic> map; // A thread-safe map to store topics

        // Constructor (Private - Singleton design pattern)
        private TopicManager(){
            this.map = new ConcurrentHashMap<String, Topic>();
        }

        // Get topic of given subject, create if not already exists
        public Topic getTopic(String topicName) {
            // Use computeIfAbsent to ensure thread-safe creation of new topics
            return map.computeIfAbsent(topicName, name -> new Topic(name));
        }

        // Return all topics
        public Collection<Topic> getTopics() {
            return map.values();
        }

        // Clear map
        public void clear(){
            map.clear();
        }
    }

    // Getter
    public static TopicManager get(){
        return TopicManager.instance;
    }
    
}
