package com.monitor;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import org.json.*;
import com.google.gson.Gson;

public class MonitorEventsHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = "";
        JSONArray jsonEvents = null;
        try {
            String rawEvents = new BufferedReader(new InputStreamReader(exchange.getRequestBody())).readLine();
            jsonEvents = new JSONArray(rawEvents);
        } catch (Exception e) {
            Monitor.sendHttpResponse(exchange,400, "Invalid Events JSON\n");
        }

        if (jsonEvents == null) {
            Monitor.sendHttpResponse(exchange, 400, "null Events JSON\n");
        }

        for (int i = 0; i < jsonEvents.length(); i++) {
            JSONObject jsonEventObject = jsonEvents.getJSONObject(i);
            try {
                Gson gson = new Gson();
                Event event = gson.fromJson(jsonEventObject.toString(), Event.class);
                //System.out.println("Received event: " + event.toString());
                response += event.processEvent();
            } catch (Exception e) {
                Monitor.sendHttpResponse(exchange, 400, "Bad JSON Event: " + e.getMessage() + ": " + jsonEventObject.toString());
            }
        }
        Monitor.sendHttpResponse(exchange,200, response);
    }
}
