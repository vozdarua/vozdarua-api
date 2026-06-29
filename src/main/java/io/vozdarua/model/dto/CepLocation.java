package io.vozdarua.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CepLocation(String cep, String state, String city, String neighborhood, String street) {

    public CepLocation withState(String state) {
        return new CepLocation(this.cep, state, this.city, this.neighborhood, this.street);
    }
}