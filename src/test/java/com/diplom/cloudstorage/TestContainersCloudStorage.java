package com.diplom.cloudstorage;

import com.diplom.cloudstorage.dtos.AuthRequest;
import com.diplom.cloudstorage.dtos.AuthResponse;
import com.diplom.cloudstorage.entity.User;
import com.diplom.cloudstorage.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestContainersCloudStorage {
    private static final int PORT_DATABASE = 5432;
    private static final int PORT_SERVER = 8080;

    private static final String DATABASE_NAME = "postgres";
    private static final String DATABASE_USERNAME = "postgres";
    private static final String DATABASE_PASSWORD = "postgress";

    private static final Network NETWORK = Network.newNetwork();

    @Container
    @ServiceConnection
    public static PostgreSQLContainer<?> databaseContainer = new PostgreSQLContainer<>("postgres")
            .withNetwork(NETWORK)
            .withExposedPorts(PORT_DATABASE)
            .withDatabaseName(DATABASE_NAME)
            .withUsername(DATABASE_USERNAME)
            .withPassword(DATABASE_PASSWORD);

    @Container
    public static GenericContainer<?> cloudStorageServer = new GenericContainer<>("cloudstorage:1.0")
            .withNetwork(NETWORK)
            .withExposedPorts(PORT_SERVER)
            .withEnv(Map.of("SPRING_DATASOURCE_URL", "jdbc:postgresql://db:" + PORT_DATABASE + "/" + DATABASE_NAME))
            .dependsOn(databaseContainer);

    private static AuthRequest authRequest;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    static void setUp2() {
        authRequest = AuthRequest.builder()
                .login("user@user.user")
                .password("user")
                .build();
    }

    @Test
    void testPostgresContainerIsRunning() {
        Assertions.assertTrue(databaseContainer.isRunning());
    }

    @Test
    void databaseTest() {
        User userActual = new User(1L, "user@user.user", "$2a$12$/esFpvwUNvgUcNnKQ8HX9utoRavht1IMzp2.rXbpFWfv6/s0xDr42");
        Optional<User> userExpected = userRepository.findUsersByLogin("user@user.user");

        Assertions.assertEquals(userExpected.get(), userActual);
    }

    @Test
    void cloudStorageServerTest() {
        HttpEntity<AuthRequest> requestEntity = new HttpEntity<>(authRequest);
        ResponseEntity<AuthResponse> response = restTemplate.exchange("/login", HttpMethod.POST, requestEntity, AuthResponse.class);
        AuthResponse authResponse = response.getBody();

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(authResponse.getToken());
    }
}
