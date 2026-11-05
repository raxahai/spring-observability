package org.practice.spring_observability.service;

import org.practice.spring_observability.dao.entity.User;
import org.practice.spring_observability.dao.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByEmail(String email) {
        User user = userRepository.findByEmail(email);
        logger.debug("User: {}", user);
        return user;
    }
}
