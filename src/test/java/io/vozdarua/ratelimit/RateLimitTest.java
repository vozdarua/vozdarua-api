package io.vozdarua.ratelimit;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestProfile(RateLimitTest.Profile.class)
class RateLimitTest {

    public static class Profile implements io.quarkus.test.junit.QuarkusTestProfile {
        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of("app.rate-limit.enabled", "true");
        }
    }

    @Test
    void loginIsRateLimitedPerIp() {
        String body = """
                {
                    "email": "nobody@test.com",
                    "password": "wrongpassword"
                }
                """;

        // AuthResource.login is limited to 5 requests/minute per IP (see @RateLimited)
        for (int i = 0; i < 5; i++) {
            given().contentType(ContentType.JSON).body(body)
                    .when().post("/auth/login")
                    .then().statusCode(401);
        }

        given().contentType(ContentType.JSON).body(body)
                .when().post("/auth/login")
                .then().statusCode(429);
    }
}
