package com.sjsu.demosignup;



import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class KeycloakService {
//    @Value("${keycloak.auth-server-url}")
//    private String keycloakServer;
//
//    @Value("${keycloak.realm}")
//    private String realm;
//
    @Value("${keycloak.resource}")
    private String loginClientId;
//
    @Value("${keycloak.credentials.secret}")
    private String clientSecret;
    @Value("${server-url}")
    private String keycloakServer;

    @Value("${realm}")
    private String realm;

    @Value("${client-id}")
    private String clientId;

    @Value("${grant-type}")
    private String grantType;

    @Value("${name}")
    private String username;

    @Value("${password}")
    private String password;

    private Keycloak getInstance() {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakServer)
                .realm(realm)
                .clientId(clientId)
                .grantType(grantType)
                .username(username)
                .password(password)
                .build();
    }

    // Method to register a new user
    public void registerUser(User user) {
        try {

            Keycloak keycloak = getInstance();
            UserRepresentation userRepresentation = new UserRepresentation();
            userRepresentation.setEnabled(true);
            userRepresentation.setUsername(user.getUsername());
            userRepresentation.setEmail(user.getEmail());


            // Set password credential
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(user.getPassword());
            credential.setTemporary(false); // Set to true if you want the password to be temporary
            userRepresentation.setCredentials(Collections.singletonList(credential));

            // Create user with password in Keycloak
            Response response= keycloak.realm(realm).users().create(userRepresentation);
            System.out.println(response.getStatus());
            if (response.getStatus() != 201) {
                throw new RuntimeException("Failed to create user: " + response.getStatusInfo().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to authenticate a user and return JWT
    public String loginUser(String username, String password) {
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(keycloakServer)
                .realm(realm)
                .clientId(loginClientId)
                .clientSecret(clientSecret)
                .grantType("password")
                .username(username)
                .password(password)
                .build();

        AccessTokenResponse response = keycloak.tokenManager().getAccessToken();
        return response.getToken();
    }

    // Method to logout a user
    public void logoutUser(String username) {
        Keycloak clientkeycloak = KeycloakBuilder.builder()
                .serverUrl(keycloakServer)
                .realm(realm)
                .clientId(loginClientId)
                .clientSecret(clientSecret)
                .grantType("client_credentials")
                .build();

        // Get user by username
        List<UserRepresentation> users = clientkeycloak.realm(realm).users().search(username);
        if (!users.isEmpty()) {
            String userId = users.get(0).getId();

            // Logout user by ending all sessions
            clientkeycloak.realm(realm).users().get(userId).logout();
        }
    }

}
