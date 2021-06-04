package com.monitor;

import com.google.gson.annotations.SerializedName;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Event {

    @SerializedName("name")
    private String hostname;

    @SerializedName("type")
    private String metricName;

    @SerializedName("value")
    private double value;

    public String toString() {
        return "Event: Host: " + hostname + " type: " + metricName + " value: " + value + "\n";
    }

    public String processEvent() throws SQLException {
        String query = "SELECT comparator, actions FROM conditions WHERE hostname = '" + hostname +
                "' AND metricname = '" + metricName + "';";
        Statement statement = Monitor.sqlconnection.createStatement();
        ResultSet results = statement.executeQuery(query);

        String comparator = null;
        String actions = null;
        while (results.next()) {
            comparator = results.getString("comparator");
            actions = results.getString("actions");
            break;
        }

        if (comparator == null)
            return "";

        JSONObject jsonObject = new JSONObject(comparator);
        String compareOp = jsonObject.getString("compare");

        boolean shouldAlert = false;
        switch (compareOp) {
            case "less than":
                shouldAlert = compare_less_than(jsonObject);
                break;
            case "greater than":
            default:
                shouldAlert = false;
        }

        if (shouldAlert) {
            JSONObject actionObject = new JSONObject(actions);
            String action = "***Alerting on: " + this.toString();
            for (String actionKey : actionObject.keySet()) {
                switch (actionKey) {
                    case "email": action += "Email '" + actionObject.get(actionKey) +
                            "' Subject: '" + metricName + " usage': " + value;
                        break;
                    case "slack": action += "Slack '" + actionObject.get(actionKey) +
                            "' channel '" + metricName + " usage': " + value;
                        break;
                    default: action += "No action specified";
                }
            }
            System.out.println(action);
            String insertAlert = "INSERT INTO alerts (timestamp, alert) VALUES (" +
                    System.currentTimeMillis() + ", '" + action.replaceAll("'", "''") + "');";
            statement.executeUpdate(insertAlert);
            return action;
        }
        return "";
    }

    private boolean compare_less_than(JSONObject object) {
        Double threshold = object.getDouble("value");
        System.out.println("Comparing " + threshold + " with event val: " + value);
        if (value < threshold)
            return false;
        return true;
    }
}
