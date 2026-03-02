package com.example;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.IOException;

/**
 * Represents an outgoing HTTP response.
 * Wraps the output stream and provides helper methods to send responses.
 */
public class HttpResponse {

    private final OutputStream outputStream;
    private boolean sent = false;

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    /**
     * Sends a 200 OK response with plain text body.
     */
    public void send(String body) {
        if (sent)
            return;
        sent = true;
        String response = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/plain\r\n"
                + "Content-Length: " + body.getBytes().length + "\r\n"
                + "\r\n"
                + body;
        writeRaw(response.getBytes());
    }

    /**
     * Sends a 200 OK response with an HTML body.
     */
    public void sendHtml(String htmlBody) {
        if (sent)
            return;
        sent = true;
        String response = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html; charset=UTF-8\r\n"
                + "Content-Length: " + htmlBody.getBytes().length + "\r\n"
                + "\r\n"
                + htmlBody;
        writeRaw(response.getBytes());
    }

    /**
     * Sends a 200 OK response with binary file data and the given MIME type.
     */
    public void sendFile(byte[] data, String mimeType) {
        if (sent)
            return;
        sent = true;
        String header = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: " + mimeType + "\r\n"
                + "Content-Length: " + data.length + "\r\n"
                + "\r\n";
        try {
            outputStream.write(header.getBytes());
            outputStream.write(data);
            outputStream.flush();
        } catch (IOException e) {
            System.err.println("Error sending file: " + e.getMessage());
        }
    }

    /**
     * Sends a 404 Not Found response.
     */
    public void sendNotFound() {
        if (sent)
            return;
        sent = true;
        String body = "404 Not Found";
        String response = "HTTP/1.1 404 Not Found\r\n"
                + "Content-Type: text/plain\r\n"
                + "Content-Length: " + body.getBytes().length + "\r\n"
                + "\r\n"
                + body;
        writeRaw(response.getBytes());
    }

    /** Returns true if a response has already been sent. */
    public boolean isSent() {
        return sent;
    }

    private void writeRaw(byte[] data) {
        try {
            outputStream.write(data);
            outputStream.flush();
        } catch (IOException e) {
            System.err.println("Error writing response: " + e.getMessage());
        }
    }
}
