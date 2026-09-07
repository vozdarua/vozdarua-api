package io.vozdarua.rest;

import io.vozdarua.model.entity.*;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
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
    private Long adminId;
    private Long severityHighId;
    private Long severityMediumId;
    private Long statusOpenId;
    private Long statusAnalyzingId;
    private Long statusResolvedId;

    @BeforeEach
    @Transactional
    void setup() {
        // Clean up
        Issue.deleteAll();
        Category.deleteAll();
        User.deleteAll();
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

        Status statusResolved = new Status();
        statusResolved.name = "Resolvido";
        statusResolved.persist();
        statusResolvedId = statusResolved.id;

        // Create test category
        Category category = new Category();
        category.name = "Buraco no asfalto";
        category.description = "Problemas com asfalto";
        category.persist();
        categoryId = category.id;

        // Create test user
        User user = new User();
        user.phone = "11999999999";
        user.password = "test123";
        user.email = "test@example.com";
        user.role = Roles.USER;
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
        Long issueId = createTestIssueViaAPI(false);

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
        Long issueId = createTestIssueViaAPI(false);

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
    @TestSecurity(user = "test@example.com", roles = {"USER"})
    void testUpdateIssue() {
        Long issueId = createTestIssueViaAPI(false);

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
    @TestSecurity(user = "test@example.com", roles = {"USER"})
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
    @TestSecurity(user = "admin@example.com", roles = {"ADMIN"})
    void testDeleteIssue() {
        Long issueId = createTestIssueViaAPI(true);

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
    @TestSecurity(user = "admin@example.com", roles = {"ADMIN"})
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
        createTestIssueViaAPI(false);

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
        createTestIssueViaAPI(false);

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
        createTestIssueViaAPI(false);

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
    @TestSecurity(user = "admin@example.com", roles = {"ADMIN"})
    void testListIssuesByReporter() {
        Long adminId = createIssueInDatabaseForAdmin();

        given()
                .when()
                .get("/issues/reporter/" + adminId)
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].anonymous", Matchers.equalTo(false))
                .body("[0].reporter", notNullValue());
    }

    @Transactional
    Long createIssueInDatabaseForAdmin() {

        User admin = new User();
        admin.phone = "11888888888";
        admin.password = "admin123";
        admin.email = "admin@example.com";
        admin.role = Roles.ADMIN;
        admin.persist();

        Issue issue = new Issue();
        issue.reporter = admin;
        issue.description = "Buraco grande na rua";
        issue.severity = Severity.findById(severityHighId);
        issue.status = Status.findById(statusOpenId);
        issue.category = Category.findById(categoryId);
        issue.confirmIssue = 0;
        issue.anonymous = false;

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

        issue.address = address;
        issue.persist();

        return admin.id;
    }

    @Test
    @Order(13)
    void testConfirmIssue() {
        Long issueId = createTestIssueViaAPI(false);
        given()
                .contentType(ContentType.JSON)
                .when()
                .put("/issues/" + issueId + "/confirm/")
                .then()
                .statusCode(200)
                .body("confirmIssue", equalTo(1));
    }

    @Test
    @Order(14)
    void testListIssuesByAddressWithCity() {
        createTestIssueViaAPI(false);

        given()
                .queryParam("city", "São José dos Campos")
                .when()
                .get("/issues/address")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].address.city", equalTo("São José dos Campos"));
    }

    @Test
    @Order(15)
    void testListIssuesByAddressWithState() {
        createTestIssueViaAPI(false);

        given()
                .queryParam("state", "SP")
                .when()
                .get("/issues/address")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].address.state", equalTo("SP"));
    }

    @Test
    @Order(16)
    void testListIssuesByAddressWithNeighborhood() {
        createTestIssueViaAPI(false);

        given()
                .queryParam("neighborhood", "Cidade Morumbi")
                .when()
                .get("/issues/address")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].address.neighborhood", equalTo("Cidade Morumbi"));
    }

    @Test
    @Order(17)
    void testListIssuesByAddressWithCityAndState() {
        createTestIssueViaAPI(false);

        given()
                .queryParam("city", "São José dos Campos")
                .queryParam("state", "SP")
                .when()
                .get("/issues/address")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].address.city", equalTo("São José dos Campos"))
                .body("[0].address.state", equalTo("SP"));
    }

    @Test
    @Order(18)
    void testListIssuesByAddressWithCityAndNeighborhood() {
        createTestIssueViaAPI(false);

        given()
                .queryParam("city", "São José dos Campos")
                .queryParam("neighborhood", "Cidade Morumbi")
                .when()
                .get("/issues/address")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].address.city", equalTo("São José dos Campos"))
                .body("[0].address.neighborhood", equalTo("Cidade Morumbi"));
    }

    @Test
    @Order(19)
    void testListIssuesByAddressWithStateAndNeighborhood() {
        createTestIssueViaAPI(false);

        given()
                .queryParam("state", "SP")
                .queryParam("neighborhood", "Cidade Morumbi")
                .when()
                .get("/issues/address")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].address.state", equalTo("SP"))
                .body("[0].address.neighborhood", equalTo("Cidade Morumbi"));
    }

    @Test
    @Order(20)
    void testListIssuesByAddressWithAllParameters() {
        createTestIssueViaAPI(false);

        given()
                .queryParam("city", "São José dos Campos")
                .queryParam("state", "SP")
                .queryParam("neighborhood", "Cidade Morumbi")
                .when()
                .get("/issues/address")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].address.city", equalTo("São José dos Campos"))
                .body("[0].address.state", equalTo("SP"))
                .body("[0].address.neighborhood", equalTo("Cidade Morumbi"));
    }

    @Test
    @Order(21)
    void testListIssuesByAddressNotFound() {
        createTestIssueViaAPI(false);

        given()
                .queryParam("city", "Rio de Janeiro")
                .when()
                .get("/issues/address")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(22)
    void testListIssuesByAddressNoParameters() {
        given()
                .when()
                .get("/issues/address")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(23)
    void testListIssuesByAddressEmptyParameters() {
        given()
                .queryParam("city", "")
                .queryParam("state", "")
                .queryParam("neighborhood", "")
                .when()
                .get("/issues/address")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(24)
    @Transactional
    void testListIssuesByAddressMultipleResults() {
        // Create first issue
        createTestIssueViaAPI(false);

        // Create second issue with same city but different neighborhood
        String secondIssueJson = String.format("""
            {
                "description": "Outro problema na cidade",
                "severity": {"id": %d},
                "status": {"id": %d},
                "confirmIssue": 0,
                "category": {"id": %d},
                "reporter": {"id": %d},
                "address": {
                     "latitude": -23.5505,
                     "longitude": -46.6333,
                     "cep": "12236-421",
                     "street": "Rua Outra",
                     "number": "100",
                     "neighborhood": "Centro",
                     "city": "São José dos Campos",
                     "state": "SP"
                }
            }
            """, severityMediumId, statusOpenId, categoryId, userId);

        given()
                .contentType(ContentType.JSON)
                .body(secondIssueJson)
                .when()
                .post("/issues")
                .then()
                .statusCode(201);

        // Query by city should return both
        given()
                .queryParam("city", "São José dos Campos")
                .when()
                .get("/issues/address")
                .then()
                .statusCode(200)
                .body("$", hasSize(2));

        // Query by specific neighborhood should return only one
        given()
                .queryParam("neighborhood", "Centro")
                .when()
                .get("/issues/address")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].address.neighborhood", equalTo("Centro"));
    }

    @Test
    @Order(25)
    public void testCreateAnonymousIssue() {
        String issueJson = String.format("""
            {
                "description": "Buraco grande na rua",
                "severity": {"id": %d},
                "status": {"id": %d},
                "confirmIssue": 0,
                "category": {"id": %d},
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
            """, severityHighId, statusOpenId, categoryId);

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
                .body("anonymous", Matchers.equalTo(true))
                .body("id", notNullValue())
                .extract()
                .path("id");
        createdIssueId = id.longValue();
    }

    @Test
    @Order(26)
    void testResolveIssue() {
        Long issueId = createTestIssueViaAPI(false);
        given()
                .contentType(ContentType.JSON)
                .when()
                .put("/issues/" + issueId + "/resolve/")
                .then()
                .statusCode(200)
                .body("status.id", equalTo(statusResolvedId.intValue()))
                .body("status.name", equalTo("Resolvido"));
    }

    @Test
    @Order(27)
    void testRanking() {
        // Create 3 issues for regular user
        createTestIssueViaAPI(false);
        createTestIssueViaAPI(false);
        createTestIssueViaAPI(false);

        // Create 2 issues for admin in database
        Long adminId = createTwoIssuesForAdmin();

        given()
                .when()
                .get("/issues/ranking")
                .then()
                .statusCode(200)
                .body("$", hasSize(2))
                .body("[0].issueCount", equalTo(3))
                .body("[0].email", equalTo("test..."))
                .body("[1].issueCount", equalTo(2))
                .body("[1].email", equalTo("admin2..."));
    }

    @Transactional
    Long createTwoIssuesForAdmin() {
        User admin = new User();
        admin.phone = "11888888888";
        admin.password = "admin123";
        admin.email = "admin2@example.com";
        admin.role = Roles.ADMIN;
        admin.persist();

        for (int i = 0; i < 2; i++) {
            Issue issue = new Issue();
            issue.reporter = admin;
            issue.description = "Admin issue " + i;
            issue.severity = Severity.findById(severityHighId);
            issue.status = Status.findById(statusOpenId);
            issue.category = Category.findById(categoryId);
            issue.confirmIssue = 0;
            issue.anonymous = false;

            Address address = new Address();
            address.latitude = -23.5505;
            address.longitude = -46.6333;
            address.cep = "12236-420";
            address.street = "Rua Admin";
            address.number = "200";
            address.neighborhood = "Admin";
            address.city = "Admin City";
            address.state = "SP";
            address.persist();

            issue.address = address;
            issue.persist();
        }

        return admin.id;
    }

    Long createTestIssueViaAPI(boolean isAdmin) {
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
            """, severityHighId, statusOpenId, categoryId, isAdmin ? adminId : userId);

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
