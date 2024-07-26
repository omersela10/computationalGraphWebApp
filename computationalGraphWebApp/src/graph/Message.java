package graph;

import java.util.Date;

/// Advanced Programming exercise 1
/// Student Name: Ahigad Genish
/// ID : 31628022

public class Message {

    // Fields
    public final byte[] data;
    public final String asText;
    public final double asDouble;
    public final Date date;


    // Constructor
    public Message(String newMessage) {
        this.asText = newMessage;

        // Attempt to parse the string as a double, if it fails, set asDouble to NaN
        double tempDouble;
        try {
            tempDouble = Double.parseDouble(newMessage);
        } catch (NumberFormatException e) {
            tempDouble = Double.NaN;
        }

        this.asDouble = tempDouble;
        this.date = new Date(); // Current date and time
        this.data = newMessage.getBytes();
    }


    // Constructor for byte array input
    public Message(byte[] newMessage)  {
        this(new String(newMessage));
    }

    // Constructor for double input
    public Message(double newMessage) {
        this(Double.toString(newMessage));
    }


}
