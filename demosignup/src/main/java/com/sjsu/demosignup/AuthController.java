package com.sjsu.demosignup;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class AuthController {
    @Autowired
    private KeycloakService keycloakService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        System.out.println("hello"+user.toString());
        keycloakService.registerUser(user);
        return ResponseEntity.ok("User registered");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> credentials) {
        String token = keycloakService.loginUser(credentials.get("username"), credentials.get("password"));
        return ResponseEntity.ok(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        keycloakService.logoutUser(username);
        return ResponseEntity.ok("Logged out successfully");
    }
}

