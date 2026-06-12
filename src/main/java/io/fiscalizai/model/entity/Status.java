package io.fiscalizai.model.entity;

public enum Status {

    ACCEPT("Aceito"), RESOLVED("Resolvido"), ANALYZING("Em análise"), OPEN("Aberto");

    private final String status;

    Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public static Status fromStatusName(String status) {
        for (Status s : Status.values()) {
            if (s.getStatus().equals(status)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown Status: " + status);
    }
}
