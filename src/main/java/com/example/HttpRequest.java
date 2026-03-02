package com.example;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents an incoming HTTP request.
 * Parses the request line to extract the path and query parameters.
 */
public class HttpRequest {

    private final String method;
    private final String path;
    private final Map<String, String> queryParams;

    public HttpRequest(String requestLine) throws URISyntaxException {
        String[] tokens = requestLine.split(" ");
        this.method = tokens[0];
        String rawUri = tokens[1];

        URI uri = new URI(rawUri);
        this.path = uri.getPath();
        this.queryParams = new HashMap<>();

        String query = uri.getQuery(); // e.g. "name=Pedro&age=20"
        if (query != null) {
            for (String pair : query.split("&")) {
                String[] kv = pair.split("=", 2);
                if (kv.length == 2) {
                    queryParams.put(kv[0], kv[1]);
                } else if (kv.length == 1) {
                    queryParams.put(kv[0], "");
                }
            }
        }
    }

    /** Returns the HTTP method (GET, POST, etc.) */
    public String getMethod() {
        return method;
    }

    /** Returns the request path without query string (e.g. /App/hello) */
    public String getPath() {
        return path;
    }

    /**
     * Returns the value of a query parameter by key.
     * Example: for ?name=Pedro, getValues("name") returns "Pedro".
     * Returns empty string if the key is not found.
     */
    public String getValues(String key) {
        return queryParams.getOrDefault(key, "");
    }

    /** Returns all query parameters as a Map */
    public Map<String, String> getQueryParams() {
        return queryParams;
    }
}
