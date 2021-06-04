package com.monitor;

public enum ComparatorType {
    LESS_THAN (" less_than "),
    LESS_THAN_EQUAL_TO (" less than equal to "),
    GREATER_THAN (" greater than "),
    GREATER_THAN_EQUAL_TO (" greater than equal to "),
    EQUAL_TO (" equal to "),
    NOT_EQUAL_TO (" not equal to "),
    BETWEEN_VALUES (" between ");

    private String name;

    ComparatorType(String name) { this.name = name; }
    public String toString() { return name; }
}
