package io.vozdarua.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeoapifyPlacesResponse(List<GeoapifyFeature> features) {
}
