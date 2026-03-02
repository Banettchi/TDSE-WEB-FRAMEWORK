package com.example;

import java.util.function.BiFunction;

/**
 * Represents a registered GET route in the framework.
 * Associates a URL path with a lambda handler function.
 *
 * Example registration:
 * get("/App/hello", (req, res) -> "Hello " + req.getValues("name"));
 */
public class Route {

    private final String path;
    private final BiFunction<HttpRequest, HttpResponse, String> handler;

    public Route(String path, BiFunction<HttpRequest, HttpResponse, String> handler) {
        this.path = path;
        this.handler = handler;
    }

    /** Returns the path this route matches (e.g., "/App/hello") */
    public String getPath() {
        return path;
    }

    /**
     * Invokes the lambda handler with the given request and response.
     * Returns the response body string, or null if the handler sent the response
     * directly.
     */
    public String handle(HttpRequest req, HttpResponse res) {
        return handler.apply(req, res);
    }
}
