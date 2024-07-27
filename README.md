# Computational Graph Server

This project implements a computational graph server. It provides an interface to upload configuration files or mathematical expressions, manage topics and agents, and display the resulting computational graph.

## Client Side

The main page consists of three sections: 
- A form to upload configuration files or enter mathematical expressions, 
- A graph display, 
- A table to display topic messages and graph result.

**To run the app on the client side, open your browser and enter the URL: `http://localhost:8080/app`.**

### HTML and CSS

Defines the layout and style of the web interface. 
It features a responsive design to ensure optimal viewing and interaction across various devices.

### JavaScript

JavaScript functions handle file uploads, parsing expressions, sending topics, and fetching results.

## Server Side

### HTTP Server

The HTTP server listens on port 8080 and handles requests to upload files, expressions, and manage topics. 
The server is a RESTful API implementation from scratch in Java.

**To run the server, open the `Main.java` file in the `src` folder in a Java IDE and run it.**

### Servlets

#### ConfLoader Servlet

- **Method:** POST
- **URI:** `/upload`
- **Description:** Handles configuration file uploads and updates the computational graph.

#### ExpressionHandler Servlet

- **Method:** POST
- **URI:** `/uploadExpression`
- **Description:** Processes mathematical expressions, converts them to configuration files, and updates the computational graph.

#### HtmlLoader Servlet

- **Method:** GET
- **URI:** `/app`
- **Description:** Serves HTML files and injects JavaScript as needed.

#### ResultHandler Servlet

- **Method:** GET
- **URI:** `/result`
- **Description:** Responds to requests for the current result of the computation.

#### TopicDisplayer Servlet

- **Method:** GET
- **URI:** `/publish`
- **Description:** Displays current messages for each topic and updates them based on incoming messages.

### Graph Model

#### Agent Interface

Defines the basic structure for agents in the computational graph.

#### BinOpAgent

Represents a binary operation agent.

#### UnOpAgent

Represents a unary operation agent.

#### Graph

Represents the computational graph and manages nodes and edges.

#### Message

Defines the structure of messages passed between topics.

## Example of Usage

[Link to a video that presents the app]

[Link to a PowerPoint that explains the app]

## Authors

Omer Sela and Ahigad Genish
