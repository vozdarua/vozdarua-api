package io.fiscalizai.rest;

import io.fiscalizai.model.entity.Issue;
import io.fiscalizai.model.entity.Severity;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SeverityResourceTest {

    @BeforeEach
    @Transactional
    void setup() {
        // Delete in order to avoid foreign key constraint violations
        Issue.deleteAll();
        Severity.deleteAll();
    }

    @Test
    @Order(1)
    void testListSeveritiesEmpty() {
        given()
                .when()
                .get("/severity")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(2)
    void testListSeveritiesWithData() {
        createTestSeverity("Baixo", "");
        createTestSeverity("Médio", "");
        createTestSeverity("Alto", "");

        given()
                .when()
                .get("/severity")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(3))
                .body("[0].name", notNullValue())
                .body("[1].name", notNullValue())
                .body("[2].name", notNullValue());
    }

    @Test
    @Order(3)
    void testListSeveritiesVerifyNames() {
        createTestSeverity("Baixo", "");
        createTestSeverity("Médio", "");
        createTestSeverity("Alto", "");

        given()
                .when()
                .get("/severity")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(3))
                .body("[0].name", equalTo("Baixo"))
                .body("[1].name", equalTo("Médio"))
                .body("[2].name", equalTo("Alto"));
    }

    @Test
    @Order(4)
    void testListSeveritiesWithIcon() {
        createTestSeverity("Alto", "⚠️");

        given()
                .when()
                .get("/severity")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(1))
                .body("[0].name", equalTo("Alto"))
                .body("[0].icon", equalTo("⚠️"));
    }

    @Transactional
    void createTestSeverity(String name, String icon) {
        Severity severity = new Severity();
        severity.name = name;
        severity.icon = icon;
        severity.persist();
    }
}
