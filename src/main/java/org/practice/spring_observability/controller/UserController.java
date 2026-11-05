package org.practice.spring_observability.controller;

import org.practice.spring_observability.dao.entity.User;
import org.practice.spring_observability.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> findByEmail(@RequestBody Map<String, Object> body) {
        String email = (String) body.get("email");
        logger.info("Entered findByEmail method with email: {}", email);
        return ResponseEntity.ok(userService.findByEmail(email));
    }
}
