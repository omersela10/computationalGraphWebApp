package servlets;

import java.io.*;
import java.util.regex.*;
import server.RequestParser.RequestInfo;

public class HtmlLoader implements Servlet {
    private String htmlDir;

    public HtmlLoader(String htmlFilesPath) {
        this.htmlDir = htmlFilesPath;
    }

    @Override
    public void handle(RequestInfo requestInfo, OutputStream out) throws IOException {
        String uri = requestInfo.getUri();
        if (uri.equals("/app") || uri.equals("/index.html")) {
            serveParsedHtml("index.html", out);
        } else {
            serveStaticFile(uri, out);
        }
    }

    // Parsed main page html
    private void serveParsedHtml(String filename, OutputStream out) throws IOException {
        File file = new File(htmlDir, filename);

        if (file.exists() == false || file.isFile() == false ) {
            send404(out);
            return;
        }

        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
        }

        String content = contentBuilder.toString();

        // Pattern for iframes
        Pattern iframePattern = Pattern.compile("<iframe\\s+[^>]*src=\"([^\"]*)\"[^>]*></iframe>");
        Matcher matcher = iframePattern.matcher(content);

        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String src = matcher.group(1);
            String replacement = loadHtmlContent(src);
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);

        // Update the content with script file content
        content = result.toString();
        content = includeScriptFileContent(content, "C:\\Users\\USER\\git\\computationalGraphWebAppGit\\computationalGraphWebApp\\src\\views\\javascript.js");

        try (PrintWriter writer = new PrintWriter(out)) {
            writer.println("HTTP/1.1 200 OK");
            writer.println("Content-Type: text/html");
            writer.println("Content-Length: " + content.length());
            writer.println();
            writer.println(content);
        }
    }

    // Append scripts
    private String includeScriptFileContent(String htmlContent, String scriptFilePath) throws IOException {
        File scriptFile = new File(scriptFilePath);
        if (scriptFile.exists() == false || scriptFile.isFile() == false) {
            return htmlContent;
        }

        StringBuilder scriptContentBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(scriptFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                scriptContentBuilder.append(line).append("\n");
            }
        }

        String scriptContent = scriptContentBuilder.toString();
        String scriptTag = "<script src=\"javascript.js\"></script>";
        String scriptTagReplacement = "<script>" + scriptContent + "</script>";

        return htmlContent.replace(scriptTag, scriptTagReplacement);
    }

    // Handle parsing any file
    private void serveStaticFile(String uri, OutputStream out) throws IOException {
        String filename = uri.startsWith("/") ? uri.substring(1) : uri;
        File file = new File(htmlDir, filename);

        if (file.exists() == false || file.isFile() == false) {
            send404(out);
            return;
        }

        String contentType;
        if (filename.endsWith(".js") == true) {
            contentType = "application/javascript";
        } else if (filename.endsWith(".html") == true) {
            contentType = "text/html";
        } else {
            contentType = "text/plain";
        }

        try (InputStream in = new FileInputStream(file);
             PrintWriter writer = new PrintWriter(out)) {
            writer.println("HTTP/1.1 200 OK");
            writer.println("Content-Type: " + contentType);
            writer.println("Content-Length: " + file.length());
            writer.println();
            writer.flush();

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }

    // Return html content from given file path
    private String loadHtmlContent(String relativePath) throws IOException {
        File file = new File(htmlDir, relativePath);
        if (file.exists() == false || file.isFile() == false) {
            return "<!-- File not found: " + relativePath + " -->";
        }

        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
        }
        return contentBuilder.toString();
    }

    // Return 404 error
    private void send404(OutputStream out) throws IOException {
        try (PrintWriter writer = new PrintWriter(out)) {
            writer.println("HTTP/1.1 404 Not Found");
            writer.println("Content-Type: text/plain");
            writer.println();
            writer.println("File not found");
        }
    }

    @Override
    public void close() throws IOException {
        // No resources to close in this implementation
    }
}