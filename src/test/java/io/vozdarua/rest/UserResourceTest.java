package io.vozdarua.rest;

import io.vozdarua.model.entity.Roles;
import io.vozdarua.model.entity.User;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserResourceTest {

    private Long regularUserId;
    private Long anotherUserId;
    private Long adminUserId;

    @BeforeEach
    @Transactional
    void setup() {
        User.deleteAll();

        // Create regular user
        User regularUser = new User();
        regularUser.phone = "11999999999";
        regularUser.email = "user@example.com";
        regularUser.password = "password123";
        regularUser.role = Roles.USER;
        regularUser.persist();
        regularUserId = regularUser.id;

        // Create another regular user
        User anotherUser = new User();
        anotherUser.phone = "11988888888";
        anotherUser.email = "another@example.com";
        anotherUser.password = "password456";
        anotherUser.role = Roles.USER;
        anotherUser.persist();
        anotherUserId = anotherUser.id;

        // Create admin user
        User adminUser = new User();
        adminUser.phone = "11977777777";
        adminUser.email = "admin@example.com";
        adminUser.password = "admin123";
        adminUser.role = Roles.ADMIN;
        adminUser.persist();
        adminUserId = adminUser.id;
    }

    @Test
    @Order(1)
    void testCreateUser() {
        String userJson = """
            {
                "phone": "11944444444",
                "email": "newuser1@example.com",
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
                .body("token", notNullValue());
    }

    @Test
    @Order(2)
    void testCreateUserDifferentPhone() {
        String userJson = """
            {
                "phone": "11933333333",
                "email": "newuser2@example.com",
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
                .body("token", notNullValue());
    }

    @Test
    @Order(3)
    void testCreateUserDifferentPassword() {
        String userJson = """
            {
                "phone": "11922222222",
                "email": "newuser3@example.com",
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
                .body("token", notNullValue());
    }

    @Test
    @Order(4)
    void testCreateMultipleUsers() {
        String user1Json = """
            {
                "phone": "11911111111",
                "email": "newuser4@example.com",
                "password": "pass1"
            }
            """;

        String user2Json = """
            {
                "phone": "11900000000",
                "email": "newuser5@example.com",
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
        Assertions.assertEquals(5, count); // 3 from setup + 2 new ones
    }

    // ==================== UPDATE TESTS ====================

    @Test
    @Order(5)
    @TestSecurity(user = "user@example.com", roles = {"USER"})
    void testUpdateOwnAccount() {
        String updatedUserJson = """
            {
                "phone": "11999999000",
                "email": "user@example.com",
                "password": "newpassword123"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(updatedUserJson)
                .when()
                .put("/user/" + regularUserId)
                .then()
                .statusCode(201)
                .body("phone", equalTo("11999999000"))
                .body("email", equalTo("user@example.com"));
    }

    @Test
    @Order(6)
    @TestSecurity(user = "user@example.com", roles = {"USER"})
    void testUserCannotUpdateOtherUserAccount() {
        String updatedUserJson = """
            {
                "phone": "11900000000",
                "email": "another@example.com",
                "password": "hackedpassword"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(updatedUserJson)
                .when()
                .put("/user/" + anotherUserId)
                .then()
                .statusCode(400);
    }

    @Test
    @Order(7)
    @TestSecurity(user = "admin@example.com", roles = {"ADMIN"})
    void testAdminCanUpdateOtherUserAccount() {
        String updatedUserJson = """
            {
                "phone": "11900000000",
                "email": "user@example.com",
                "password": "resetpassword"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(updatedUserJson)
                .when()
                .put("/user/" + regularUserId)
                .then()
                .statusCode(201)
                .body("phone", equalTo("11900000000"))
                .body("email", equalTo("user@example.com"));
    }

    @Test
    @Order(8)
    @TestSecurity(user = "admin@example.com", roles = {"ADMIN"})
    void testAdminCanUpdateOwnAccount() {
        String updatedUserJson = """
            {
                "phone": "11966666666",
                "email": "admin@example.com",
                "password": "newadminpass"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(updatedUserJson)
                .when()
                .put("/user/" + adminUserId)
                .then()
                .statusCode(201)
                .body("phone", equalTo("11966666666"))
                .body("email", equalTo("admin@example.com"));
    }

    @Test
    @Order(9)
    @TestSecurity(user = "user@example.com", roles = {"USER"})
    void testUpdateNonExistentUser() {
        String updatedUserJson = """
            {
                "phone": "11999999000",
                "email": "user@example.com",
                "password": "newpassword123"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(updatedUserJson)
                .when()
                .put("/user/99999")
                .then()
                .statusCode(404)
                .body("error", notNullValue());
    }

    // ==================== DELETE TESTS ====================

    @Test
    @Order(10)
    @TestSecurity(user = "user@example.com", roles = {"USER"})
    void testUserCanDeleteOwnAccount() {
        given()
                .when()
                .delete("/user/" + regularUserId)
                .then()
                .statusCode(204);

        // Verify deletion
        User deletedUser = User.findById(regularUserId);
        Assertions.assertNull(deletedUser);
    }

    @Test
    @Order(11)
    @TestSecurity(user = "user@example.com", roles = {"USER"})
    void testUserCannotDeleteOtherUserAccount() {
        given()
                .when()
                .delete("/user/" + anotherUserId)
                .then()
                .statusCode(400);

        // Verify user still exists
        User user = User.findById(anotherUserId);
        Assertions.assertNotNull(user);
        Assertions.assertEquals("another@example.com", user.email);
    }

    @Test
    @Order(12)
    @TestSecurity(user = "admin@example.com", roles = {"ADMIN"})
    void testAdminCanDeleteOtherUserAccount() {
        given()
                .when()
                .delete("/user/" + regularUserId)
                .then()
                .statusCode(204);

        // Verify deletion
        User deletedUser = User.findById(regularUserId);
        Assertions.assertNull(deletedUser);
    }

    @Test
    @Order(13)
    @TestSecurity(user = "admin@example.com", roles = {"ADMIN"})
    void testAdminCanDeleteOwnAccount() {
        given()
                .when()
                .delete("/user/" + adminUserId)
                .then()
                .statusCode(204);

        // Verify deletion
        User deletedUser = User.findById(adminUserId);
        Assertions.assertNull(deletedUser);
    }

    @Test
    @Order(14)
    @TestSecurity(user = "user@example.com", roles = {"USER"})
    void testDeleteNonExistentUser() {
        given()
                .when()
                .delete("/user/99999")
                .then()
                .statusCode(404)
                .body("error", notNullValue());
    }

    @Test
    @Order(15)
    @TestSecurity(user = "another@example.com", roles = {"USER"})
    void testAnotherUserCannotDeleteFirstUser() {
        given()
                .when()
                .delete("/user/" + regularUserId)
                .then()
                .statusCode(400);

        // Verify user still exists
        User user = User.findById(regularUserId);
        Assertions.assertNotNull(user);
        Assertions.assertEquals("user@example.com", user.email);
    }

    @Test
    @Order(16)
    @TestSecurity(user = "another@example.com", roles = {"USER"})
    void testAnotherUserCannotUpdateFirstUser() {
        String updatedUserJson = """
            {
                "phone": "11900000000",
                "email": "user@example.com",
                "password": "hackedpassword"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(updatedUserJson)
                .when()
                .put("/user/" + regularUserId)
                .then()
                .statusCode(400);

        // Verify user data unchanged
        User user = User.findById(regularUserId);
        Assertions.assertNotNull(user);
        Assertions.assertEquals("11999999999", user.phone);
        Assertions.assertEquals("user@example.com", user.email);
    }
}
