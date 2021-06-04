package com.monitor;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Monitor {

    private static HttpServer httpServer;
    public static Connection sqlconnection;
    // Setup HTTP Server
    private static void setupHttpServer() throws Exception {
        httpServer = HttpServer.create(new InetSocketAddress(Constants.HTTP_SERVER_PORT), 0);

        // Defining http endpoint for "/events"
        httpServer.createContext(Constants.HTTP_ENDPOINT_EVENTS, new MonitorEventsHandler());

        // Defining http endpoint for "/config"
        httpServer.createContext(Constants.HTTP_ENDPOINT_CONFIG, new MonitorConfigHandler());

        // Defining http endpoint for "/alerts"
        httpServer.createContext(Constants.HTTP_ENDPOINT_ALERTS, new MonitorAlertsHandler());

        httpServer.setExecutor(null);
    }

    private static void setupDatabase() throws Exception {
        sqlconnection = DriverManager.getConnection(Constants.JDBC_URL);
        Statement statement = sqlconnection.createStatement();

        //statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + Constants.MONITOR_DB);
        statement.executeUpdate(" CREATE TABLE IF NOT EXISTS conditions (" +
            " hostname      TEXT, " +
            " metricName    TEXT, " +
            " comparator    TEXT, " +
            " actions       TEXT, " +
            " PRIMARY KEY (hostname, metricName)" +
            " );");

        statement.executeUpdate(" CREATE TABLE IF NOT EXISTS alerts (" +
            " timestamp     INTEGER, " +
            " alert         TEXT, " +
            " PRIMARY KEY (timestamp)" +
            " );");

        statement.close();
    }

    public static void sendHttpResponse(HttpExchange exchange, int code, String response) throws IOException {
        exchange.sendResponseHeaders(code, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    public static void main(String[] args) throws Exception {
        setupHttpServer();

        setupDatabase();
        httpServer.start();
    }
}
