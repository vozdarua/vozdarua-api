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
    private Long sjcCityId;
    private Long spStateId;
    private Long rioCityId;

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

        // City/State are seeded reference data (V1.0.5), not test-owned - look up the
        // rows the tests below need instead of creating/deleting them.
        State spState = State.<State>find("uf", "SP").firstResult();
        spStateId = spState.id;
        sjcCityId = City.<City>find("name = ?1 and state = ?2", "São José dos Campos", spState).firstResult().id;
        rioCityId = City.<City>find("name = ?1 and state.uf = ?2", "Rio de Janeiro", "RJ").firstResult().id;

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
                .body("content", hasSize(1))
                .body("content[0].description", equalTo("Buraco grande na rua"))
                .body("totalElements", equalTo(1))
                .body("totalPages", equalTo(1));
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
    void testListIssuesFilteredByCategoryId() {
        createTestIssueViaAPI(false);

        given()
                .queryParam("categoryId", categoryId)
                .when()
                .get("/issues")
                .then()
                .statusCode(200)
                .body("content", hasSize(1))
                .body("content[0].category.id", equalTo(categoryId.intValue()));
    }

    @Test
    @Order(10)
    void testListIssuesFilteredByStatusId() {
        createTestIssueViaAPI(false);

        given()
                .queryParam("statusId", statusOpenId)
                .when()
                .get("/issues")
                .then()
                .statusCode(200)
                .body("content", hasSize(1))
                .body("content[0].status.name", equalTo("Aberto"));
    }

    @Test
    @Order(11)
    void testListIssuesFilteredBySeverityId() {
        createTestIssueViaAPI(false);

        given()
                .queryParam("severityId", severityHighId)
                .when()
                .get("/issues")
                .then()
                .statusCode(200)
                .body("content", hasSize(1))
                .body("content[0].severity.name", equalTo("Alto"));
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
    void testListIssuesFilteredByStateId() {
        createTestIssueViaAPI(false);

        given()
                .queryParam("stateId", spStateId)
                .when()
                .get("/issues")
                .then()
                .statusCode(200)
                .body("content", hasSize(1))
                .body("content[0].address.state", equalTo("SP"));
    }

    @Test
    @Order(15)
    void testListIssuesFilteredByNeighborhood() {
        createTestIssueViaAPI(false);

        given()
                .queryParam("neighborhood", "Cidade Morumbi")
                .when()
                .get("/issues")
                .then()
                .statusCode(200)
                .body("content", hasSize(1))
                .body("content[0].address.neighborhood", equalTo("Cidade Morumbi"));
    }

    // O ponto central do TODO: dois filtros combinados numa chamada só, algo que os
    // antigos endpoints dedicados (/category/{id}, /address, etc.) não permitiam.
    @Test
    @Order(16)
    void testListIssuesCombinesCategoryAndCityFilters() {
        createTestIssueViaAPI(false); // São José dos Campos, categoryId
        createTwoIssuesForAdmin(); // "Admin City" (cityRef nunca resolve), mesma categoryId

        given()
                .queryParam("categoryId", categoryId)
                .queryParam("cityId", sjcCityId)
                .when()
                .get("/issues")
                .then()
                .statusCode(200)
                .body("content", hasSize(1))
                .body("content[0].address.city", equalTo("São José dos Campos"));
    }

    @Test
    @Order(17)
    @Transactional
    void testListIssuesCombinesCityAndNeighborhoodFilters() {
        createTestIssueViaAPI(false); // SJC, "Cidade Morumbi"

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

        given()
                .queryParam("cityId", sjcCityId)
                .when()
                .get("/issues")
                .then()
                .statusCode(200)
                .body("content", hasSize(2));

        given()
                .queryParam("cityId", sjcCityId)
                .queryParam("neighborhood", "Centro")
                .when()
                .get("/issues")
                .then()
                .statusCode(200)
                .body("content", hasSize(1))
                .body("content[0].address.neighborhood", equalTo("Centro"));
    }

    // Mudança de comportamento deliberada: filtro sem match agora é 200 + content vazio,
    // não 404 - necessário pra paginação fazer sentido (uma página fora do fim não é "not found").
    @Test
    @Order(18)
    void testListIssuesNoMatchReturnsEmptyContentNot404() {
        createTestIssueViaAPI(false);

        given()
                .queryParam("cityId", rioCityId)
                .when()
                .get("/issues")
                .then()
                .statusCode(200)
                .body("content", hasSize(0))
                .body("totalElements", equalTo(0));
    }

    @Test
    @Order(19)
    void testListIssuesPagination() {
        createTestIssueViaAPI(false);
        createTestIssueViaAPI(false);
        createTestIssueViaAPI(false);

        given()
                .queryParam("size", 2)
                .when()
                .get("/issues")
                .then()
                .statusCode(200)
                .body("content", hasSize(2))
                .body("totalElements", equalTo(3))
                .body("totalPages", equalTo(2));

        given()
                .queryParam("size", 2)
                .queryParam("page", 1)
                .when()
                .get("/issues")
                .then()
                .statusCode(200)
                .body("content", hasSize(1));
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
                .body("status.name", equalTo("Resolvido"))
                .body("confirmResolve", equalTo(1));
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
                .body("[0].total", equalTo(3))
                .body("[0].email", equalTo("test..."))
                .body("[1].total", equalTo(2))
                .body("[1].email", equalTo("admin2..."));
    }

    @Test
    @Order(28)
    @TestSecurity(user = "admin@example.com", roles = {"ADMIN"})
    void testDeleteIssueWithCommentsAttached() {
        Long issueId = createTestIssueViaAPI(true);
        persistCommentFor(issueId);

        given()
                .when()
                .delete("/issues/" + issueId)
                .then()
                .statusCode(204);

        given()
                .when()
                .get("/issues/" + issueId)
                .then()
                .statusCode(404);
    }

    @Test
    @Order(29)
    void testCreateIssueResolvesCityRef() {
        Long issueId = createTestIssueViaAPI(false);

        given()
                .when()
                .get("/issues/" + issueId)
                .then()
                .statusCode(200)
                .body("address.city", equalTo("São José dos Campos"))
                .body("address.cityRef.name", equalTo("São José dos Campos"))
                .body("address.cityRef.state.uf", equalTo("SP"))
                .body("address.stateRef.uf", equalTo("SP"));
    }

    @Test
    @Order(30)
    public void testCreateIssueWithUnknownCityStillSucceeds() {
        String issueJson = String.format("""
            {
                "description": "Buraco em cidade desconhecida",
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
                     "city": "Cidade Que Não Existe No Ibge",
                     "state": "SP"
                }
            }
            """, severityHighId, statusOpenId, categoryId, userId);

        given()
                .contentType(ContentType.JSON)
                .body(issueJson)
                .when()
                .post("/issues")
                .then()
                .statusCode(201)
                .body("address.city", equalTo("Cidade Que Não Existe No Ibge"))
                .body("address.cityRef", nullValue());
    }

    @Test
    @Order(31)
    void testListIssuesFilteredByCityId() {
        createTestIssueViaAPI(false); // São José dos Campos - resolves a cityRef
        createTwoIssuesForAdmin(); // "Admin City" - doesn't match any seeded City

        given()
                .queryParam("cityId", sjcCityId)
                .when()
                .get("/issues")
                .then()
                .statusCode(200)
                .body("content", hasSize(1))
                .body("content[0].address.city", equalTo("São José dos Campos"));
    }

    @Test
    @Order(32)
    void testRankingFilteredByCityId() {
        createTestIssueViaAPI(false); // São José dos Campos - resolves a cityRef
        createTwoIssuesForAdmin(); // "Admin City" - doesn't match any seeded City

        given()
                .queryParam("cityId", sjcCityId)
                .when()
                .get("/issues/ranking")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].email", equalTo("test..."))
                .body("[0].total", equalTo(1));
    }

    @Test
    @Order(33)
    void testMetricsRequiresCityId() {
        given()
                .when()
                .get("/issues/metrics")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(34)
    void testMetricsForCity() {
        createTestIssueViaAPI(false); // Aberto, Alto, "Cidade Morumbi"

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
            """, severityMediumId, statusResolvedId, categoryId, userId);

        given()
                .contentType(ContentType.JSON)
                .body(secondIssueJson)
                .when()
                .post("/issues")
                .then()
                .statusCode(201);

        given()
                .queryParam("cityId", sjcCityId)
                .when()
                .get("/issues/metrics")
                .then()
                .statusCode(200)
                .body("total", equalTo(2))
                .body("byCategory.size()", equalTo(1))
                .body("byCategory[0].name", equalTo("Buraco no asfalto"))
                .body("byCategory[0].count", equalTo(2))
                .body("byNeighborhood.size()", equalTo(2))
                .body("bySeverity.find { it.name == 'Alto' }.count", equalTo(1))
                .body("bySeverity.find { it.name == 'Médio' }.count", equalTo(1))
                .body("byStatus.find { it.name == 'Aberto' }.count", equalTo(1))
                .body("byStatus.find { it.name == 'Resolvido' }.count", equalTo(1));
    }

    @Test
    @Order(35)
    void testMetricsFilteredByNeighborhood() {
        createTestIssueViaAPI(false); // "Cidade Morumbi"

        given()
                .queryParam("cityId", sjcCityId)
                .queryParam("neighborhood", "Bairro Sem Ocorrências")
                .when()
                .get("/issues/metrics")
                .then()
                .statusCode(200)
                .body("total", equalTo(0));
    }

    @Transactional
    void persistCommentFor(Long issueId) {
        Comment comment = new Comment();
        comment.text = "Também aconteceu comigo";
        comment.issue = Issue.findById(issueId);
        comment.persist();
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
