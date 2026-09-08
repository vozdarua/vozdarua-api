package io.vozdarua.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeoapifyProperties(
        String name,
        String city,
        @JsonProperty("state_code") String stateCode,
        Double lat,
        Double lon
) {
}
