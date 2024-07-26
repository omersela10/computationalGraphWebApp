package configs;

// Config interface

public interface Config {
    void create();
    String getName();
    int getVersion();
    void close();
}
