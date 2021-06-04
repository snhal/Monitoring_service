package com.monitor;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MonitorConfigHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = "OK";
        JSONArray jsonConfig = null;
        try {
            String rawConfig = new BufferedReader(new InputStreamReader(exchange.getRequestBody())).readLine();
            jsonConfig = new JSONArray(rawConfig);
        } catch (Exception e) {
            Monitor.sendHttpResponse(exchange,400, "Invalid Config JSON\n");
        }

        if (jsonConfig == null) {
            Monitor.sendHttpResponse(exchange, 400, "null Config JSON\n");
        }

        for (int i = 0; i < jsonConfig.length(); i++) {
            JSONObject jsonConfigObject = jsonConfig.getJSONObject(i);

            Config config = null;
            try {
                Gson gson = new Gson();
                config = gson.fromJson(jsonConfigObject.toString(), Config.class);
                System.out.println("Received config item: " + config.toString());
            } catch (Exception e) {
                Monitor.sendHttpResponse(exchange, 400 /* Bad Request */, "Bad JSON Config: " + e.getMessage() +
                        jsonConfigObject.toString());
            }

            try {
                if (config != null)
                    config.save();
            } catch (Exception e) {
                Monitor.sendHttpResponse(exchange, 500 /* Internal Server Error */, "Couldn't save config: " + e.getMessage() +
                    config.toString());
            }
        }
        Monitor.sendHttpResponse(exchange,200, response);
    }
}
