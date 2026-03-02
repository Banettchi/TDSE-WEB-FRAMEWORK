package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Central singleton that acts as the web framework registry.
 *
 * Usage by application developers:
 * WebFramework.staticfiles("/webroot");
 * WebFramework.get("/App/hello", (req, res) -> "Hello " +
 * req.getValues("name"));
 * WebFramework.start(8080);
 *
 * Internally used by HttpServer to resolve routes and serve static files.
 */
public class WebFramework {

    // --- Singleton instance ---
    private static final WebFramework INSTANCE = new WebFramework();

    private final List<Route> routes = new ArrayList<>();
    private String staticFilesRoot = "/webroot"; // default

    private WebFramework() {
    }

    public static WebFramework getInstance() {
        return INSTANCE;
    }

    // -----------------------------------------------------------------------
    // Public API for application developers
    // -----------------------------------------------------------------------

    /**
     * Registers a GET route with a lambda handler.
     *
     * Example:
     * get("/App/hello", (req, res) -> "Hello " + req.getValues("name"));
     */
    public static void get(String path, BiFunction<HttpRequest, HttpResponse, String> handler) {
        INSTANCE.routes.add(new Route(path, handler));
    }

    /**
     * Sets the root folder (in the classpath) where static files are located.
     *
     * Example:
     * staticfiles("/webroot");
     * The server will look for files under target/classes/webroot/
     */
    public static void staticfiles(String folder) {
        INSTANCE.staticFilesRoot = folder;
    }

    /**
     * Starts the HTTP server on the given port.
     * Delegates to HttpServer.
     */
    public static void start(int port) {
        HttpServer.start(port);
    }

    // -----------------------------------------------------------------------
    // Internal API used by HttpServer
    // -----------------------------------------------------------------------

    /**
     * Tries to find a registered route matching the given path.
     * Returns the Route if found, null otherwise.
     */
    public Route findRoute(String path) {
        for (Route route : routes) {
            if (route.getPath().equals(path)) {
                return route;
            }
        }
        return null;
    }

    /**
     * Returns the configured static files root folder (classpath-relative).
     * Example: "/webroot"
     */
    public String getStaticFilesRoot() {
        return staticFilesRoot;
    }
}
