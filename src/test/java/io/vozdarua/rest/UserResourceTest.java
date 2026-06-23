package io.vozdarua.rest;

import io.vozdarua.model.entity.User;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserResourceTest {

    @BeforeEach
    @Transactional
    void setup() {
        User.deleteAll();
    }

    @Test
    @Order(1)
    void testCreateUser() {
        String userJson = """
            {
                "phone": "11999999999",
                "password": "test123"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(userJson)
                .when()
                .post("/user")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("phone", equalTo("11999999999"))
                .body("password", equalTo("test123"));
    }

    @Test
    @Order(2)
    void testCreateUserDifferentPhone() {
        String userJson = """
            {
                "phone": "11988888888",
                "password": "test456"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(userJson)
                .when()
                .post("/user")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("phone", equalTo("11988888888"))
                .body("password", equalTo("test456"));
    }

    @Test
    @Order(3)
    void testCreateUserDifferentPassword() {
        String userJson = """
            {
                "phone": "11977777777",
                "password": "test789"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(userJson)
                .when()
                .post("/user")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("phone", equalTo("11977777777"))
                .body("password", equalTo("test789"));
    }

    @Test
    @Order(4)
    void testCreateMultipleUsers() {
        String user1Json = """
            {
                "phone": "11966666666",
                "password": "pass1"
            }
            """;

        String user2Json = """
            {
                "phone": "11955555555",
                "password": "pass2"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(user1Json)
                .when()
                .post("/user")
                .then()
                .statusCode(201);

        given()
                .contentType(ContentType.JSON)
                .body(user2Json)
                .when()
                .post("/user")
                .then()
                .statusCode(201);

        long count = User.count();
        Assertions.assertEquals(2, count);
    }
}
