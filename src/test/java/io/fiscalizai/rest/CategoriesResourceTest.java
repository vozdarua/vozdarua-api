package io.fiscalizai.rest;

import io.fiscalizai.model.entity.Category;
import io.fiscalizai.model.entity.Issue;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CategoriesResourceTest {

    @BeforeEach
    @Transactional
    void setup() {
        // Delete in order to avoid foreign key constraint violations
        Issue.deleteAll();
        Category.deleteAll();
    }

    @Test
    @Order(1)
    void testListCategoriesEmpty() {
        given()
                .when()
                .get("/categories")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(2)
    void testListCategoriesWithData() {
        createTestCategory("Buraco no asfalto", "🕳️", "Cratera na rua");
        createTestCategory("Calçada danificada", "🚶", "Piso quebrado");

        given()
                .when()
                .get("/categories")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(2))
                .body("[0].name", notNullValue())
                .body("[0].icon", notNullValue())
                .body("[1].name", notNullValue());
    }

    @Test
    @Order(3)
    void testListCategoriesWithIcon() {
        createTestCategory("Buraco no asfalto", "🕳️", "Cratera na rua");

        given()
                .when()
                .get("/categories")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(1))
                .body("[0].name", equalTo("Buraco no asfalto"))
                .body("[0].icon", equalTo("🕳️"))
                .body("[0].description", equalTo("Cratera na rua"));
    }

    @Test
    @Order(4)
    void testListCategoriesWithTags() {
        createTestCategoryWithTags("Buraco no asfalto", "🕳️", "Cratera na rua",
                                   List.of("buraco", "asfalto", "cratera"));

        given()
                .when()
                .get("/categories")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(1))
                .body("[0].tags", hasSize(3))
                .body("[0].tags[0]", equalTo("buraco"));
    }

    @Transactional
    void createTestCategory(String name, String icon, String description) {
        Category category = new Category();
        category.name = name;
        category.icon = icon;
        category.description = description;
        category.persist();
    }

    @Transactional
    void createTestCategoryWithTags(String name, String icon, String description, List<String> tags) {
        Category category = new Category();
        category.name = name;
        category.icon = icon;
        category.description = description;
        category.tags = tags;
        category.persist();
    }
}
