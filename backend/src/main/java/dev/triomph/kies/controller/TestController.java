package dev.triomph.kies.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://frontend:3000", "http://frontend"}, maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/public")
    public ResponseEntity<Map<String, String>> publicAccess() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Public Content");
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/protected")
    public ResponseEntity<Map<String, String>> protectedAccess() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Protected Content");
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }
}