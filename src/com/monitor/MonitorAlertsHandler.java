package com.monitor;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.text.*;

public class MonitorAlertsHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));

        System.out.println("URI: " + exchange.getRequestURI().toString());
        String httpQuery = exchange.getRequestURI().getQuery();
        Map<String, String> result = new HashMap<String, String>();

        if (httpQuery == null) {
            if (!result.containsKey("startTime"))
                result.put("startTime", "0");
            if (!result.containsKey("endTime"))
                result.put("endTime", Long.toString(System.currentTimeMillis()));

        } else {
            // http
            for (String param : httpQuery.split("&")) {
                String pair[] = param.split("=");
                if (pair.length > 1) {
                    result.put(pair[0], pair[1]);
                }
            }
        }
        System.out.println("Query params: " + result.toString());

        String dbQuery = "SELECT timestamp, alert FROM alerts WHERE timestamp >= " +
                result.get("startTime") + " AND timestamp <= " + result.get("endTime");

        String response = "";
        try {
            Statement statement = Monitor.sqlconnection.createStatement();
            ResultSet results = statement.executeQuery(dbQuery);
            while (results.next()) {
                response += format.format(new Date(results.getLong("timestamp"))) +
                        " Alert: " + results.getString("alert") + "\n";
            }
        } catch (SQLException e) {
            throw new IOException("SQL Error: " + e.getMessage());
        }
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
