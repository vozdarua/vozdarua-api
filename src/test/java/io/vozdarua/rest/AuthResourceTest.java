package io.vozdarua.rest;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.vozdarua.model.entity.Roles;
import io.vozdarua.model.entity.User;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
class AuthResourceTest {

    @BeforeEach
    @Transactional
    void setup() {
        User.deleteAll();

        User admin = new User();
        admin.phone = "11999999999";
        admin.email = "admin@test.com";
        admin.password = BcryptUtil.bcryptHash("admin123");
        admin.role = Roles.ADMIN;
        admin.persist();

        User user = new User();
        user.phone = "11988888888";
        user.email = "user@test.com";
        user.password = BcryptUtil.bcryptHash("user123");
        user.role = Roles.USER;
        user.persist();
    }

    @Test
    void testLoginSuccess() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "email": "admin@test.com",
                    "password": "admin123"
                }
                """)
        .when()
            .post("/auth/login")
        .then()
            .statusCode(200)
            .body("token", notNullValue());
    }

    @Test
    void testLoginInvalidPassword() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "email": "admin@test.com",
                    "password": "wrongpassword"
                }
                """)
        .when()
            .post("/auth/login")
        .then()
            .statusCode(401);
    }

    @Test
    void testLoginUserNotFound() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "email": "nonexistent@test.com",
                    "password": "somepassword"
                }
                """)
        .when()
            .post("/auth/login")
        .then()
            .statusCode(401);
    }

    @Test
    void testLoginEmptyEmail() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "email": "",
                    "password": "admin123"
                }
                """)
        .when()
            .post("/auth/login")
        .then()
            .statusCode(400);
    }

    @Test
    void testLoginNullEmail() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "email": null,
                    "password": "admin123"
                }
                """)
        .when()
            .post("/auth/login")
        .then()
            .statusCode(400);
    }

    @Test
    void testLoginEmptyPassword() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "email": "admin@test.com",
                    "password": ""
                }
                """)
        .when()
            .post("/auth/login")
        .then()
            .statusCode(400);
    }

    @Test
    void testLoginNullPassword() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "email": "admin@test.com",
                    "password": null
                }
                """)
        .when()
            .post("/auth/login")
        .then()
            .statusCode(400);
    }

    @Test
    void testLoginWithUserRole() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "email": "user@test.com",
                    "password": "user123"
                }
                """)
        .when()
            .post("/auth/login")
        .then()
            .statusCode(200)
            .body("token", notNullValue());
    }

    @Test
    void testLoginNullBody() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .post("/auth/login")
        .then()
            .statusCode(400);
    }
}
