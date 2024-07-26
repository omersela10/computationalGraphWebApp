

// Java Script file contains .js functions

// Upload file function
function uploadFile() {
    const formData = new FormData(document.getElementById('uploadForm'));

    // Perform the POST request
    fetch('http://localhost:8080/upload?${params}', {
        method: 'POST',
        body: formData
    })
    .then(response => response.text()) // Assume the server returns text data
    .then(data => {
         // Log
        console.log("The response from upload is", data);
        // Get the canvas element
        const canvas = document.getElementById("graphCanvas");

        // Clear any existing content
        const ctx = canvas.getContext("2d");
        ctx.clearRect(0, 0, canvas.width, canvas.height);
        canvas.innerHTML = '';

        // Display the server response in the canvas
        if (data.includes("Error")) {
           // Update the title to show the error message
           graphTitle.textContent = data;

           console.log("Error detected in response:", data);
           return;
       }

        graphTitle.textContent = "Graph";

        // Parse the uploaded XML data
        const graph = parseXML(data);

        drawGraph(graph, ctx);
    })
    .catch(error => console.error('Error:', error));
}


// Parse expression function
function parseExpression(event) {
    event.preventDefault(); // Prevent form from submitting the traditional way
    const formData = new FormData(document.getElementById('expressionForm'));

   const body = `expression=${encodeURIComponent(expression)}`;
   const url = 'http://localhost:8080/uploadExpression';
   console.log("Request URL:", url);

   fetch(url, {
      method: 'POST',
      body: formData
    })
    .then(response => response.text())
    .then(data => {
        // Log
        console.log("The response from upload is", data);
        // Get the canvas element
        const canvas = document.getElementById("graphCanvas");

        // Clear any existing content
        const ctx = canvas.getContext("2d");
        ctx.clearRect(0, 0, canvas.width, canvas.height);
        canvas.innerHTML = '';

        // Display the server response in the canvas

        if (data.includes("Error")) {
           // Update the title to show the error message
           graphTitle.textContent = data;

           console.log("Error detected in response:", data);
           return;
       }

        graphTitle.textContent = "Graph";

        // Parse the uploaded XML data
        const graph = parseXML(data);

        drawGraph(graph, ctx);
    })
    .catch(error => console.error('Error:', error));
}

// Listener to the submit button of the expression form to raise the parseExpression function
document.addEventListener('DOMContentLoaded', function () {
    const expressionForm = document.getElementById('expressionForm');
    expressionForm.addEventListener('submit', parseExpression);
});

// Send topic function
function sendTopic(event) {
    event.preventDefault(); // Prevent the form from submitting the default way

    const form = document.getElementById('topicForm');
    const formData = new FormData(form);
    const params = new URLSearchParams(formData).toString();

    // Perform the GET request with query parameters
    fetch(`http://localhost:8080/publish?${params}`, {
        method: 'GET'
    })
    .then(response => response.text()) // Assuming the server returns HTML content
    .then(data => {
        // Get the tableDiv element
        const tableDiv = document.getElementById("tableDiv");

        // Display the server response in the tableDiv
        // Clear any existing content
        tableDiv.innerHTML = '';

        // Update the tableDiv with the new content
        tableDiv.innerHTML = data; // Insert HTML content directly

        // Log
        console.log("The response from publish is ", data);
    })
    .catch(error => console.error('Error:', error));
}

// Listener to the submit button of the upload form to raise the parseExpression function
document.addEventListener('DOMContentLoaded', function () {
    const uploadForm = document.getElementById('uploadForm');

    uploadForm.addEventListener('submit', function (event) {
        event.preventDefault();
        uploadFile();
    });
});


// Event listener to the DOM
document.addEventListener("DOMContentLoaded", function () {
    const canvas = document.getElementById("graphCanvas");
    const ctx = canvas.getContext("2d");

    // Parse XML and build graph data
    function parseXML(xmlString) {
        const parser = new DOMParser();
        const xmlDoc = parser.parseFromString(xmlString, "application/xml");
        const nodes = xmlDoc.getElementsByTagName("node");
        const edges = xmlDoc.getElementsByTagName("edge");

        const graph = {
            nodes: {},
            edges: []
        };

        Array.from(nodes).forEach(node => {
            const id = node.getAttribute("id");
            const type = id.startsWith("T") ? "rectangle" : "circle";
            graph.nodes[id] = {
                type: type,
                x: Math.random() * canvas.width,
                y: Math.random() * canvas.height
            };
        });

        Array.from(edges).forEach(edge => {
            const fromNodeId = edge.parentElement.getAttribute("id");
            const toNodeId = edge.getAttribute("to");
            graph.edges.push({ from: fromNodeId, to: toNodeId });
        });

        return graph;
    }

    // Draw the graph
    function drawGraph(graph) {
        const canvas = document.getElementById('graphCanvas');
        const ctx = canvas.getContext('2d');
        ctx.clearRect(0, 0, canvas.width, canvas.height);
        const centerX = canvas.width / 2;
        const centerY = canvas.height / 2;
        const radius = Math.min(centerX, centerY) * 0.8; // Adjust radius as needed
        const numNodes = Object.keys(graph.nodes).length;
        const angleStep = (Math.PI * 2) / numNodes;

        // Calculate node positions
        let angle = 0;
        for (const [id, node] of Object.entries(graph.nodes)) {
            node.x = centerX + radius * Math.cos(angle);
            node.y = centerY + radius * Math.sin(angle);
            angle += angleStep;
        }

        // Draw nodes with modified labels
        for (const [id, node] of Object.entries(graph.nodes)) {
            let label = id;
            if (id.startsWith("T")) {
                label = id.slice(1); // Remove leading "T"
            } else if (id.startsWith("A")) {
                label = id.replace("A", "Agent"); // Remove leading "A" and add "Agent"
            }

            ctx.beginPath();
            if (node.type === "rectangle") {
                ctx.rect(node.x - 50, node.y - 25, 100, 50); // Enlarged rectangle
                ctx.strokeStyle = "#00F";
                ctx.stroke();
                ctx.fillStyle = "#CCF";
                ctx.fill();
            } else {
                const circleRadius = 60; // Enlarged circle radius
                ctx.arc(node.x, node.y, circleRadius, 0, Math.PI * 2);
                ctx.strokeStyle = "#F00";
                ctx.stroke();
                ctx.fillStyle = "#FCC";
                ctx.fill();
            }

            ctx.font = "14px Arial"; // Slightly larger font
            ctx.fillStyle = "#000";
            ctx.textAlign = "center";
            ctx.fillText(label, node.x, node.y + 5);
        }

        // Draw edges with arrows
        graph.edges.forEach(edge => {
            const from = graph.nodes[edge.from];
            const to = graph.nodes[edge.to];
            const dx = to.x - from.x;
            const dy = to.y - from.y;
            const length = Math.sqrt(dx * dx + dy * dy);

            // Calculate new start and end points with a 35px reduction each side
            const reduction = 35; // Increased reduction to account for larger nodes
            const scale = (length - 2 * reduction) / length;
            const startX = from.x + dx * reduction / length;
            const startY = from.y + dy * reduction / length;
            const endX = to.x - dx * reduction / length;
            const endY = to.y - dy * reduction / length;

            // Draw edge line
            ctx.beginPath();
            ctx.moveTo(startX, startY);
            ctx.lineTo(endX, endY);
            ctx.strokeStyle = "blue";
            ctx.lineWidth = 4;
            ctx.stroke();

            // Draw arrowhead
            const arrowSize = 15;
            const arrowAngle = Math.PI / 6;
            const angle = Math.atan2(dy, dx);
            ctx.beginPath();
            ctx.moveTo(endX, endY);
            ctx.lineTo(
                endX - arrowSize * Math.cos(angle - arrowAngle),
                endY - arrowSize * Math.sin(angle - arrowAngle)
            );
            ctx.lineTo(
                endX - arrowSize * Math.cos(angle + arrowAngle),
                endY - arrowSize * Math.sin(angle + arrowAngle)
            );
            ctx.closePath();
            ctx.fillStyle = "blue";
            ctx.fill();
        });
    }

    // Expose functions for external use
    window.parseXML = parseXML;
    window.drawGraph = drawGraph;
});


