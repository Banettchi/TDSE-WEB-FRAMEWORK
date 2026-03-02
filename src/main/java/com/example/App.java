package com.example;

/**
 * Example application demonstrating the WebFramework.
 *
 * Serves:
 * - REST: GET http://localhost:8080/App/hello?name=Pedro → "Hello Pedro"
 * - REST: GET http://localhost:8080/App/pi → "3.141592653589793"
 * - HTML: GET http://localhost:8080/index.html → static page
 */
public class App {

    public static void main(String[] args) {
        // Define the folder containing static files (in src/main/resources/)
        WebFramework.staticfiles("/webroot");

        // Register REST services using lambda functions
        WebFramework.get("/App/hello", (req, resp) -> "Hello " + req.getValues("name"));

        WebFramework.get("/App/pi", (req, resp) -> {
            return String.valueOf(Math.PI);
        });

        WebFramework.get("/App/epsilon", (req, resp) -> {
            return String.valueOf(Math.E);
        });

        // Start the server on port 8080
        WebFramework.start(8080);
    }
}
