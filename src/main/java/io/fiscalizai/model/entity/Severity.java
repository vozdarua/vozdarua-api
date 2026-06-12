package io.fiscalizai.model.entity;

public enum Severity {
    LOW("Baixo"), MEDIUM("Médio"), HIGH("Alto");

    private final String severity;

    Severity(String severity) {
        this.severity = severity;
    }

    public String getSeverity() {
        return this.severity;
    }

    public static Severity fromSeverityName(String severity) {
        for (Severity s : Severity.values()) {
            if (s.getSeverity().equals(severity)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown severity: " + severity);
    }
}
