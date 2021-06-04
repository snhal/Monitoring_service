package com.monitor;

import com.google.gson.annotations.SerializedName;

import java.sql.Statement;

public class Config {
    @SerializedName("comparator")
    private String comparator;
    //private ComparatorType comparatorType;

    @SerializedName("metric")
    private String metricName;

    @SerializedName("name")
    private String hostname;

    @SerializedName("actions")
    private String actions;
    //@SerializedName("threshold")
    //private double threshold;

    public String toString() {
        return "Config: Host: " + hostname + " type: " + metricName + " type: " + comparator.toString() + "\n";
    }

    public void save() throws Exception {
        Statement statement = Monitor.sqlconnection.createStatement();

        String escapedComparator = comparator.replaceAll("\"", "\\\"");
        String escapedActions = actions.replaceAll("\"", "\\\"");
        // If row with hostname + metricName does not exist, it will be inserted, else
        // it will be updated with new comparator and actions
        String query = "REPLACE INTO conditions (hostname, metricname, " +
                "comparator, actions) VALUES ('" + hostname + "', '" + metricName + "', '" +
                escapedComparator + "', '" + escapedActions + "');";
        System.out.println(query);
        statement.executeUpdate(query);

    }
}
