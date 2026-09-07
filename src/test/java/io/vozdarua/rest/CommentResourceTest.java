package io.vozdarua.rest;

import io.vozdarua.model.entity.*;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CommentResourceTest {

    private Long issueId;

    @BeforeEach
    @Transactional
    void setup() {
        Comment.deleteAll();
        Issue.deleteAll();
        Category.deleteAll();
        User.deleteAll();
        Address.deleteAll();
        Severity.deleteAll();
        Status.deleteAll();

        User user = new User();
        user.phone = "11999999999";
        user.password = "test123";
        user.email = "test@example.com";
        user.role = Roles.USER;
        user.persist();

        Severity severity = new Severity();
        severity.name = "Alto";
        severity.persist();

        Status status = new Status();
        status.name = "Aberto";
        status.persist();

        Category category = new Category();
        category.name = "Buraco no asfalto";
        category.description = "Problemas com asfalto";
        category.persist();

        Address address = new Address();
        address.latitude = -23.5505;
        address.longitude = -46.6333;
        address.cep = "12236-420";
        address.street = "Rua Joana Soares Ferreira";
        address.number = "662";
        address.neighborhood = "Cidade Morumbi";
        address.city = "São José dos Campos";
        address.state = "SP";
        address.persist();

        Issue issue = new Issue();
        issue.description = "Buraco grande na rua";
        issue.severity = severity;
        issue.status = status;
        issue.category = category;
        issue.address = address;
        issue.confirmIssue = 0;
        issue.anonymous = true;
        issue.persist();
        issueId = issue.id;

        User admin = new User();
        admin.phone = "11888888888";
        admin.password = "admin123";
        admin.email = "admin@example.com";
        admin.role = Roles.ADMIN;
        admin.persist();
    }

    @Test
    @Order(1)
    void testCreateAnonymousComment() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"text\": \"Isso aqui piorou muito\"}")
                .when()
                .post("/issues/" + issueId + "/comments")
                .then()
                .statusCode(201)
                .body("text", equalTo("Isso aqui piorou muito"))
                .body("authorEmail", nullValue())
                // author/ipAddress/userAgent are @SecureField(ADMIN) -> hidden for anonymous callers
                .body("author", nullValue())
                .body("ipAddress", nullValue());
    }

    @Test
    @Order(2)
    @TestSecurity(user = "test@example.com", roles = {"USER"})
    void testCreateAuthenticatedComment() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"text\": \"Confirmo, também vi isso\"}")
                .when()
                .post("/issues/" + issueId + "/comments")
                .then()
                .statusCode(201)
                .body("text", equalTo("Confirmo, também vi isso"))
                // authorEmail is public and set even though the User relation itself stays hidden
                .body("authorEmail", equalTo("test@example.com"))
                .body("author", nullValue());
    }

    @Test
    @Order(3)
    void testCreateCommentIssueNotFound() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"text\": \"Comentário\"}")
                .when()
                .post("/issues/99999/comments")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(4)
    void testListComments() {
        createComment("Primeiro comentário");
        createComment("Segundo comentário");

        given()
                .when()
                .get("/issues/" + issueId + "/comments")
                .then()
                .statusCode(200)
                .body("$", hasSize(2))
                .body("[0].text", equalTo("Primeiro comentário"))
                .body("[1].text", equalTo("Segundo comentário"));
    }

    @Test
    @Order(5)
    @TestSecurity(user = "admin@example.com", roles = {"ADMIN"})
    void testListCommentsAdminSeesTrackingData() {
        createComment("Comentário anônimo");

        given()
                .when()
                .get("/issues/" + issueId + "/comments")
                .then()
                .statusCode(200)
                .body("[0].ipAddress", notNullValue());
    }

    @Test
    @Order(6)
    void testDeleteCommentRequiresAuth() {
        Long commentId = createComment("Comentário a apagar");

        given()
                .when()
                .delete("/issues/" + issueId + "/comments/" + commentId)
                .then()
                .statusCode(401);
    }

    @Test
    @Order(7)
    @TestSecurity(user = "test@example.com", roles = {"USER"})
    void testDeleteCommentForbiddenForNonAdmin() {
        Long commentId = createComment("Comentário a apagar");

        given()
                .when()
                .delete("/issues/" + issueId + "/comments/" + commentId)
                .then()
                .statusCode(403);
    }

    @Test
    @Order(8)
    @TestSecurity(user = "admin@example.com", roles = {"ADMIN"})
    void testDeleteCommentAsAdmin() {
        Long commentId = createComment("Comentário a apagar");

        given()
                .when()
                .delete("/issues/" + issueId + "/comments/" + commentId)
                .then()
                .statusCode(204);

        given()
                .when()
                .get("/issues/" + issueId + "/comments")
                .then()
                .statusCode(200)
                .body("$", hasSize(0));
    }

    @Test
    @Order(9)
    @TestSecurity(user = "admin@example.com", roles = {"ADMIN"})
    void testDeleteCommentNotFound() {
        given()
                .when()
                .delete("/issues/" + issueId + "/comments/99999")
                .then()
                .statusCode(404);
    }

    private Long createComment(String text) {
        Integer id = given()
                .contentType(ContentType.JSON)
                .body(String.format("{\"text\": \"%s\"}", text))
                .when()
                .post("/issues/" + issueId + "/comments")
                .then()
                .statusCode(201)
                .extract()
                .path("id");
        return id.longValue();
    }
}
