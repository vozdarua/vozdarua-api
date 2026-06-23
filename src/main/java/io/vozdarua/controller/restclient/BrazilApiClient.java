package io.vozdarua.controller.restclient;

import io.vozdarua.model.dto.CepLocation;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(baseUri = "https://brasilapi.com.br/api/cep/v1")
public interface BrazilApiClient {

    @GET
    @Path("/{cep}")
    Uni<CepLocation> findByCep(@PathParam("cep") String cep);

}
