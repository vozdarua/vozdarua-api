package io.vozdarua.controller.restclient.dto;

public record ResendPayload(String from, String[] to, String subject, String html) {}
