package com.example;

import java.io.*;
import java.net.*;

/**
 * HTTP Server - extends the original skeleton to integrate with WebFramework.
 *
 * Changes from the original:
 * - Port is now configurable (passed by WebFramework.start(port))
 * - Parses the full request (including query params) via HttpRequest
 * - Dispatches to registered lambda routes via WebFramework
 * - Falls back to static file serving from the classpath
 * - Returns 404 for unmatched requests
 */
public class HttpServer {

    public static void start(int port) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port);
            System.exit(1);
        }

        System.out.println("=== Web Framework Server started on port " + port + " ===");

        boolean running = true;

        while (running) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            try {
                handleRequest(clientSocket);
            } catch (Exception e) {
                System.err.println("Error handling request: " + e.getMessage());
            }
        }

        try {
            serverSocket.close();
        } catch (IOException e) {
            System.err.println("Error closing server socket: " + e.getMessage());
        }
    }

    private static void handleRequest(Socket clientSocket) throws IOException {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));

        OutputStream out = clientSocket.getOutputStream();
        HttpResponse response = new HttpResponse(out);

        // --- Read the HTTP request (first line is the important one) ---
        boolean firstLine = true;
        String requestLine = "";
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            System.out.println("Received: " + inputLine);
            if (firstLine) {
                requestLine = inputLine;
                firstLine = false;
            }
            if (!in.ready()) {
                break;
            }
        }

        // --- Parse the request line into HttpRequest ---
        HttpRequest request;
        try {
            request = new HttpRequest(requestLine);
        } catch (Exception e) {
            System.err.println("Bad request: " + e.getMessage());
            response.sendNotFound();
            closeAll(out, in, clientSocket);
            return;
        }

        System.out.println("Method: " + request.getMethod());
        System.out.println("Path:   " + request.getPath());

        // --- Try to find a registered REST route ---
        WebFramework framework = WebFramework.getInstance();
        Route route = framework.findRoute(request.getPath());

        if (route != null) {
            // Invoke the lambda and send the returned string as the response body
            String body = route.handle(request, response);
            if (body != null && !response.isSent()) {
                response.send(body);
            }
        } else {
            // --- Fallback: serve a static file from the classpath ---
            serveStaticFile(request.getPath(), response);
        }

        closeAll(out, in, clientSocket);
    }

    /**
     * Looks up a static file resource from the classpath.
     * Example: path="/index.html", staticRoot="/webroot"
     * → loads classpath resource /webroot/index.html
     */
    private static void serveStaticFile(String path, HttpResponse response) {
        WebFramework framework = WebFramework.getInstance();
        String staticRoot = framework.getStaticFilesRoot();
        String resourcePath = staticRoot + path;

        try (InputStream is = HttpServer.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                System.out.println("Static file not found: " + resourcePath);
                response.sendNotFound();
                return;
            }
            byte[] data = is.readAllBytes();
            String mimeType = getMimeType(path);
            System.out.println("Serving static file: " + resourcePath + " [" + mimeType + "]");
            response.sendFile(data, mimeType);
        } catch (IOException e) {
            System.err.println("Error reading static file: " + e.getMessage());
            response.sendNotFound();
        }
    }

    /** Determines MIME type from file extension. */
    private static String getMimeType(String path) {
        if (path.endsWith(".html") || path.endsWith(".htm"))
            return "text/html; charset=UTF-8";
        if (path.endsWith(".css"))
            return "text/css";
        if (path.endsWith(".js"))
            return "application/javascript";
        if (path.endsWith(".json"))
            return "application/json";
        if (path.endsWith(".png"))
            return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg"))
            return "image/jpeg";
        if (path.endsWith(".gif"))
            return "image/gif";
        if (path.endsWith(".ico"))
            return "image/x-icon";
        if (path.endsWith(".svg"))
            return "image/svg+xml";
        return "application/octet-stream";
    }

    private static void closeAll(OutputStream out, BufferedReader in, Socket socket) {
        try {
            out.close();
        } catch (IOException ignored) {
        }
        try {
            in.close();
        } catch (IOException ignored) {
        }
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }
}
