package io.vozdarua.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeoapifyFeature(GeoapifyProperties properties) {
}
