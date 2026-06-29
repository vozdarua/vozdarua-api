package io.vozdarua.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeoResponse(
        @JsonProperty("display_name") String displayName,
        @JsonProperty("lat") String lat,
        @JsonProperty("lon") String lon
) {
}
