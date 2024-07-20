package configs;

import graph.Agent;
import graph.ParallelAgent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;


/// Advanced Programming exercise 4
/// Student Name: Ahigad Genish
/// ID : 31628022

public class GenericConfig implements Config {
	
	// Data Members
    private String configFile;
    private List<ParallelAgent> agents = new ArrayList<>();

    // Methods
    @Override
    public void create() {

        try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
        	
        	// Read File
            List<String> lines = new ArrayList<>();
            String line;
            
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            
            // Check valid input
            if (lines.size() % 3 != 0) {
                throw new IllegalArgumentException("Invalid config file format.");
            }

            // Iterate over the lines
            for (int i = 0; i < lines.size(); i += 3) {
            	
            	// Parse lines
                String className = lines.get(i);
                String[] subs = lines.get(i + 1).split(",");
                String[] pubs = lines.get(i + 2).split(",");
                
                // Create instance
                Class<?> agentClass = Class.forName(className);
                Constructor<?> constructor = agentClass.getConstructor(String[].class, String[].class);
                
                Agent agent = (Agent) constructor.newInstance((Object) subs, (Object) pubs);
                agents.add(new ParallelAgent(agent, 10));
            }
        } catch (IOException | ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "GenericConfig";
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public void close() {
    	
        for (ParallelAgent agent : agents) {
            agent.close();
        }
    }

	public void setConfFile(String configFile) {
        this.configFile = configFile;
    }
}