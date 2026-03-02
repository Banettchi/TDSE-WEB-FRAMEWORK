package com.example;

import java.net.URL;
import java.net.MalformedURLException;

public class URLParcel {
    public static void main(String[] args) throws MalformedURLException {
        URL myurl = new URL("http://is.ecuelaing.edu.co:7654/respuestas/respuesta.txt?val=76t=3#pubs");
        System.out.println("Protocol: " + myurl.getProtocol());
        System.out.println("Host: " + myurl.getHost());
        System.out.println("Port: " + myurl.getPort());
        System.out.println("Path: " + myurl.getPath());
        System.out.println("Query: " + myurl.getQuery());
        System.out.println("File: " + myurl.getFile());
        System.out.println("Ref: " + myurl.getRef());
    }
}
