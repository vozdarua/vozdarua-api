package io.fiscalizai.rest;

import io.fiscalizai.model.entity.*;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IssueResourceTest {

    private Long createdIssueId;
    private Long categoryId;
    private Long userId;
    private Long severityHighId;
    private Long severityMediumId;
    private Long statusOpenId;
    private Long statusAnalyzingId;

    @BeforeEach
    @Transactional
    void setup() {
        // Clean up
        Issue.deleteAll();
        Category.deleteAll();
        FiscalizaiUser.deleteAll();
        Address.deleteAll();
        Severity.deleteAll();
        Status.deleteAll();

        // Create test severities
        Severity severityHigh = new Severity();
        severityHigh.name = "Alto";
        severityHigh.persist();
        severityHighId = severityHigh.id;

        Severity severityMedium = new Severity();
        severityMedium.name = "Médio";
        severityMedium.persist();
        severityMediumId = severityMedium.id;

        // Create test statuses
        Status statusOpen = new Status();
        statusOpen.name = "Aberto";
        statusOpen.persist();
        statusOpenId = statusOpen.id;

        Status statusAnalyzing = new Status();
        statusAnalyzing.name = "Em análise";
        statusAnalyzing.persist();
        statusAnalyzingId = statusAnalyzing.id;

        // Create test category
        Category category = new Category();
        category.name = "Buraco no asfalto";
        category.description = "Problemas com asfalto";
        category.persist();
        categoryId = category.id;

        // Create test user
        FiscalizaiUser user = new FiscalizaiUser();
        user.phone = "11999999999";
        user.password = "test123";
        user.persist();
        userId = user.id;
    }

    @Test
    @Order(1)
    public void testCreateIssue() {
        String issueJson = String.format("""
            {
                "description": "Buraco grande na rua",
                "severity": {"id": %d},
                "status": {"id": %d},
                "confirmIssue": 0,
                "category": {"id": %d},
                "reporter": {"id": %d},
                "address": {
                     "latitude": -23.5505,
                     "longitude": -46.6333,
                     "cep": "12236-420",
                     "street": "Rua Joana Soares Ferreira",
                     "number": "662",
                     "neighborhood": "Cidade Morumbi",
                     "city": "São José dos Campos",
                     "state": "SP"
                }
            }
            """, severityHighId, statusOpenId, categoryId, userId);

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(issueJson)
                .when()
                .post("/issues")
                .then()
                .statusCode(201)
                .body("description", equalTo("Buraco grande na rua"))
                .body("severity.name", equalTo("Alto"))
                .body("status.name", equalTo("Aberto"))
                .body("confirmIssue", equalTo(0))
                .body("id", notNullValue())
                .extract()
                .path("id");
        createdIssueId = id.longValue();
    }

    @Test
    @Order(2)
    void testListAllIssues() {
        // Create test issue first
        Long issueId = createTestIssueViaAPI();

        given()
                .when()
                .get("/issues")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].description", equalTo("Buraco grande na rua"));
    }

    @Test
    @Order(3)
    void testGetIssueById() {
        Long issueId = createTestIssueViaAPI();

        given()
                .when()
                .get("/issues/" + issueId)
                .then()
                .statusCode(200)
                .body("id", equalTo(issueId.intValue()))
                .body("description", equalTo("Buraco grande na rua"))
                .body("severity.name", equalTo("Alto"));
    }

    @Test
    @Order(4)
    void testGetIssueByIdNotFound() {
        given()
                .when()
                .get("/issues/99999")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(5)
    void testUpdateIssue() {
        Long issueId = createTestIssueViaAPI();

        String updatedJson = String.format("""
            {
                "description": "Buraco muito grande na rua",
                "severity": {"id": %d},
                "status": {"id": %d},
                "confirmIssue": 5,
                "category": {"id": %d},
                "reporter": {"id": %d},
                "address": {
                     "latitude": -23.5505,
                     "longitude": -46.6333,
                     "cep": "12236-420",
                     "street": "Rua Joana Soares Ferreira",
                     "number": "662",
                     "neighborhood": "Cidade Morumbi",
                     "city": "São José dos Campos",
                     "state": "SP"
                }
            }
            """, severityHighId, statusAnalyzingId, categoryId, userId);

        given()
                .contentType(ContentType.JSON)
                .body(updatedJson)
                .when()
                .put("/issues/" + issueId)
                .then()
                .statusCode(200)
                .body("description", equalTo("Buraco muito grande na rua"))
                .body("status.name", equalTo("Em análise"))
                .body("confirmIssue", equalTo(5));
    }

    @Test
    @Order(6)
    void testUpdateIssueNotFound() {
        String updatedJson = String.format("""
            {
                "description": "Updated description",
                "severity": {"id": %d},
                "status": {"id": %d},
                "confirmIssue": 0,
                "category": {"id": %d},
                "reporter": {"id": %d},
                "address": {
                     "latitude": -23.5505,
                     "longitude": -46.6333,
                     "cep": "12236-420",
                     "street": "Rua Joana Soares Ferreira",
                     "number": "662",
                     "neighborhood": "Cidade Morumbi",
                     "city": "São José dos Campos",
                     "state": "SP"
                }
            }
            """, severityMediumId, statusOpenId, categoryId, userId);

        given()
                .contentType(ContentType.JSON)
                .body(updatedJson)
                .when()
                .put("/issues/99999")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(7)
    void testDeleteIssue() {
        Long issueId = createTestIssueViaAPI();

        given()
                .when()
                .delete("/issues/" + issueId)
                .then()
                .statusCode(204);

        // Verify it's deleted
        given()
                .when()
                .get("/issues/" + issueId)
                .then()
                .statusCode(404);
    }

    @Test
    @Order(8)
    void testDeleteIssueNotFound() {
        given()
                .when()
                .delete("/issues/99999")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(9)
    void testListIssuesByCategory() {
        createTestIssueViaAPI();

        given()
                .when()
                .get("/issues/category/" + categoryId)
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].category.id", equalTo(categoryId.intValue()));
    }

    @Test
    @Order(10)
    void testListIssuesByStatus() {
        createTestIssueViaAPI();

        given()
                .when()
                .get("/issues/status/" + statusOpenId)
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].status.name", equalTo("Aberto"));
    }

    @Test
    @Order(11)
    void testListIssuesBySeverity() {
        createTestIssueViaAPI();

        given()
                .when()
                .get("/issues/severity/" + severityHighId)
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].severity.name", equalTo("Alto"));
    }

    @Test
    @Order(12)
    void testListIssuesByReporter() {
        createTestIssueViaAPI();

        given()
                .when()
                .get("/issues/reporter/" + userId)
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].reporter.id", equalTo(userId.intValue()));
    }

    @Test
    @Order(13)
    void testConfirmIssue() {
        Long issueId = createTestIssueViaAPI();
        given()
                .contentType(ContentType.JSON)
                .when()
                .put("/issues/confirm/" + issueId)
                .then()
                .statusCode(200)
                .body("confirmIssue", equalTo(1));
    }

    Long createTestIssueViaAPI() {
        String issueJson = String.format("""
            {
                "description": "Buraco grande na rua",
                "severity": {"id": %d},
                "status": {"id": %d},
                "confirmIssue": 0,
                "category": {"id": %d},
                "reporter": {"id": %d},
                "address": {
                     "latitude": -23.5505,
                     "longitude": -46.6333,
                     "cep": "12236-420",
                     "street": "Rua Joana Soares Ferreira",
                     "number": "662",
                     "neighborhood": "Cidade Morumbi",
                     "city": "São José dos Campos",
                     "state": "SP"
                }
            }
            """, severityHighId, statusOpenId, categoryId, userId);

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(issueJson)
                .when()
                .post("/issues")
                .then()
                .statusCode(201)
                .extract()
                .path("id");
        return id.longValue();
    }
}
