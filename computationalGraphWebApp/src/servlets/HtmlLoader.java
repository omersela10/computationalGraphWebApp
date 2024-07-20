package servlets;

import java.io.*;
import java.nio.file.*;
import java.util.regex.*;

import server.RequestParser.RequestInfo;

public class HtmlLoader implements Servlet {
    private String htmlDir;

    public HtmlLoader(String htmlFilesPath) {
        this.htmlDir = htmlFilesPath;
    }

    @Override
    public void handle(RequestInfo requestInfo, OutputStream out) throws IOException {

        serveParsedHtml("index.html", out);
    }

    private void serveParsedHtml(String filename, OutputStream out) throws IOException {

        File file = new File(htmlDir, filename);

        if (file.exists() == false || file.isFile() == false) {
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
        Pattern iframePattern = Pattern.compile("<iframe\\s+[^>]*src=\"([^\"]*)\"[^>]*></iframe>");
        Matcher matcher = iframePattern.matcher(content);

        StringBuffer result = new StringBuffer();
        while (matcher.find() == true) {
            String src = matcher.group(1);
            String replacement = loadHtmlContent(src);
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);

        try (PrintWriter writer = new PrintWriter(out)) {
            writer.println("HTTP/1.1 200 OK");
            writer.println("Content-Type: text/html");
            writer.println("Content-Length: " + result.length());
            writer.println();
            writer.println(result.toString());
        }
    }

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
