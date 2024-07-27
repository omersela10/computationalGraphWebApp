package server;

import servlets.Servlet;

// HTTP Server interface

/**
 * The {@code HTTPServer} interface defines the basic methods required
 * for implementing an HTTP server.
 * <p>
 * Implementing classes should provide functionality for adding, removing,
 * starting, and stopping servlets associated with specific HTTP commands and URIs.
 */
public interface HTTPServer extends Runnable{

    /**
     * Adds a servlet to handle requests for a specific HTTP command and URI.
     *
     * @param httpCommand the HTTP command (e.g., "GET", "POST", "DELETE")
     * @param uri the URI that the servlet will handle
     * @param s the servlet to handle the requests
     */
    public void addServlet(String httpCommand, String uri, Servlet s);
    /**
     * Removes the servlet handling requests for a specific HTTP command and URI.
     *
     * @param httpCommand the HTTP command (e.g., "GET", "POST", "DELETE")
     * @param uri the URI that the servlet was handling
     */
    public void removeServlet(String httpCommand, String uri);
    /**
     * Starts the HTTP server.
     */
    public void start();
    /**
     * Closes the HTTP server, stopping it from accepting new connections.
     */
    public void close();
}
