package io.vozdarua.rest;

import io.vozdarua.model.entity.Address;
import io.vozdarua.model.entity.City;
import io.vozdarua.model.entity.Issue;
import io.vozdarua.model.entity.State;
import io.vozdarua.model.entity.Status;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItem;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CityResourceTest {

    // City/State are seeded reference data (V1.0.4/V1.0.5), not test-owned - never
    // delete them here, only Issue/Address which belong to each test.
    @BeforeEach
    @Transactional
    void setup() {
        Issue.deleteAll();
        Address.deleteAll();
    }

    @Test
    @Order(1)
    void testSearchRequiresSearchTerm() {
        given()
                .when()
                .get("/cities")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(2)
    void testSearchReturnsMatchingCities() {
        given()
                .queryParam("search", "José dos Campos")
                .when()
                .get("/cities")
                .then()
                .statusCode(200)
                .body("name", hasItem("São José dos Campos"));
    }

    @Test
    @Order(3)
    void testSearchFiltersByStateId() {
        Long spStateId = State.<State>find("uf", "SP").firstResult().id;

        given()
                .queryParam("search", "Santos")
                .queryParam("stateId", spStateId)
                .when()
                .get("/cities")
                .then()
                .statusCode(200)
                .body("state.uf", everyItem(equalTo("SP")));
    }

    @Test
    @Order(4)
    void testRankingReturnsTopCitiesByIssueCount() {
        // Setup runs in its own @Transactional method (commits on return) rather than
        // annotating the test method itself - otherwise the HTTP call below would run
        // inside the same still-open transaction and see none of this data (separate
        // connection/thread), same pitfall the other REST test classes avoid.
        seedRankingIssues();

        given()
                .when()
                .get("/cities/ranking")
                .then()
                .statusCode(200)
                .body("[0].name", equalTo("São José dos Campos"))
                .body("[0].uf", equalTo("SP"))
                .body("[0].total", equalTo(3))
                .body("[0].resolved", equalTo(1));
    }

    @Transactional
    void seedRankingIssues() {
        City sjc = City.<City>find("name = ?1 and state.uf = ?2", "São José dos Campos", "SP").firstResult();
        City salvador = City.<City>find("name = ?1 and state.uf = ?2", "Salvador", "BA").firstResult();

        // Status is canonical reference data (V1.0.1) - reuse the existing "Resolvido" row
        // instead of creating a stray duplicate (name is unique) that would corrupt it.
        Status resolved = Status.<Status>find("name", "Resolvido").firstResultOptional().orElseGet(() -> {
            Status s = new Status();
            s.name = "Resolvido";
            s.persist();
            return s;
        });

        for (int i = 0; i < 3; i++) {
            persistIssueForCity(sjc, i == 0 ? resolved : null);
        }
        persistIssueForCity(salvador, null);
    }

    private void persistIssueForCity(City city, Status status) {
        Address address = new Address();
        address.latitude = -23.0;
        address.longitude = -46.0;
        address.cityRef = city;
        address.stateRef = city.state;
        address.persist();

        Issue issue = new Issue();
        issue.address = address;
        issue.status = status;
        issue.persist();
    }
}
