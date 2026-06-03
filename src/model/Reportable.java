package model;

/** Implemented by entities that can produce a formatted report for display. */
public interface Reportable {
    String generateReport();
}