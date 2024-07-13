package test;

/// Advanced Programming exercise 1
/// Student Name: Ahigad Genish
/// ID : 31628022

public interface Agent {
    String getName();
    void reset();
    void callback(String topic, Message msg);
    void close();
}
