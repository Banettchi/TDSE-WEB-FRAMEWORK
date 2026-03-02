package com.example;

import org.junit.Test;
import static org.junit.Assert.*;
import java.net.URISyntaxException;

/**
 * Automated tests for the Web Framework components.
 */
public class WebFrameworkTest {

    // -------------------------------------------------------
    // HttpRequest tests
    // -------------------------------------------------------

    @Test
    public void testParsePath() throws URISyntaxException {
        HttpRequest req = new HttpRequest("GET /App/hello HTTP/1.1");
        assertEquals("/App/hello", req.getPath());
    }

    @Test
    public void testParseQueryParam() throws URISyntaxException {
        HttpRequest req = new HttpRequest("GET /App/hello?name=Pedro HTTP/1.1");
        assertEquals("Pedro", req.getValues("name"));
    }

    @Test
    public void testParseMultipleQueryParams() throws URISyntaxException {
        HttpRequest req = new HttpRequest("GET /App/hello?name=Pedro&age=20 HTTP/1.1");
        assertEquals("Pedro", req.getValues("name"));
        assertEquals("20", req.getValues("age"));
    }

    @Test
    public void testMissingQueryParamReturnsEmpty() throws URISyntaxException {
        HttpRequest req = new HttpRequest("GET /App/hello HTTP/1.1");
        assertEquals("", req.getValues("name"));
    }

    @Test
    public void testParseMethod() throws URISyntaxException {
        HttpRequest req = new HttpRequest("GET /App/pi HTTP/1.1");
        assertEquals("GET", req.getMethod());
    }

    // -------------------------------------------------------
    // Route / WebFramework tests
    // -------------------------------------------------------

    @Test
    public void testRouteHandlerHello() throws URISyntaxException {
        Route route = new Route("/App/hello",
                (req, res) -> "Hello " + req.getValues("name"));

        HttpRequest req = new HttpRequest("GET /App/hello?name=Pedro HTTP/1.1");
        String result = route.handle(req, null);
        assertEquals("Hello Pedro", result);
    }

    @Test
    public void testRouteHandlerPi() throws URISyntaxException {
        Route route = new Route("/App/pi",
                (req, res) -> String.valueOf(Math.PI));

        HttpRequest req = new HttpRequest("GET /App/pi HTTP/1.1");
        String result = route.handle(req, null);
        assertEquals(String.valueOf(Math.PI), result);
    }

    @Test
    public void testRouteHandlerEpsilon() throws URISyntaxException {
        Route route = new Route("/App/epsilon",
                (req, res) -> String.valueOf(Math.E));

        HttpRequest req = new HttpRequest("GET /App/epsilon HTTP/1.1");
        String result = route.handle(req, null);
        assertEquals(String.valueOf(Math.E), result);
    }

    @Test
    public void testWebFrameworkFindRouteFound() throws URISyntaxException {
        // Register a test route on the singleton
        WebFramework.get("/test/route", (req, res) -> "ok");
        Route found = WebFramework.getInstance().findRoute("/test/route");
        assertNotNull(found);
    }

    @Test
    public void testWebFrameworkFindRouteNotFound() {
        Route found = WebFramework.getInstance().findRoute("/nonexistent");
        assertNull(found);
    }

    @Test
    public void testStaticFilesRootDefault() {
        // staticfiles() correctly updates the root
        WebFramework.staticfiles("/webroot");
        assertEquals("/webroot", WebFramework.getInstance().getStaticFilesRoot());
    }
}
