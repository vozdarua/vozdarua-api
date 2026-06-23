package io.vozdarua.rest;

import io.vozdarua.model.entity.Issue;
import io.vozdarua.model.entity.Status;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StatusResourceTest {

    @BeforeEach
    @Transactional
    void setup() {
        // Delete in order to avoid foreign key constraint violations
        Issue.deleteAll();
        Status.deleteAll();
    }

    @Test
    @Order(1)
    void testListStatusEmpty() {
        given()
                .when()
                .get("/status")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(2)
    void testListStatusWithData() {
        createTestStatus("Aberto", "");
        createTestStatus("Em análise", "");
        createTestStatus("Aceito", "");
        createTestStatus("Resolvido", "");

        given()
                .when()
                .get("/status")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(4))
                .body("[0].name", notNullValue())
                .body("[1].name", notNullValue())
                .body("[2].name", notNullValue())
                .body("[3].name", notNullValue());
    }

    @Test
    @Order(3)
    void testListStatusVerifyNames() {
        createTestStatus("Aberto", "");
        createTestStatus("Em análise", "");
        createTestStatus("Aceito", "");
        createTestStatus("Resolvido", "");

        given()
                .when()
                .get("/status")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(4))
                .body("[0].name", equalTo("Aberto"))
                .body("[1].name", equalTo("Em análise"))
                .body("[2].name", equalTo("Aceito"))
                .body("[3].name", equalTo("Resolvido"));
    }

    @Test
    @Order(4)
    void testListStatusWithIcon() {
        createTestStatus("Aceito", "✅");

        given()
                .when()
                .get("/status")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(1))
                .body("[0].name", equalTo("Aceito"))
                .body("[0].icon", equalTo("✅"));
    }

    @Transactional
    void createTestStatus(String name, String icon) {
        Status status = new Status();
        status.name = name;
        status.icon = icon;
        status.persist();
    }
}
