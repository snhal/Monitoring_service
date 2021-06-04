package com.monitor;

public class Constants {
    public final static int HTTP_SERVER_PORT = 50000;
    public final static String HTTP_ENDPOINT_EVENTS = "/events";
    public final static String HTTP_ENDPOINT_CONFIG = "/config";
    public final static String HTTP_ENDPOINT_ALERTS = "/alerts";

    public final static String DB_FILE = "monitor.db";
    public final static String JDBC_URL = "jdbc:sqlite:" + DB_FILE;
}
