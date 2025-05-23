package com.example.authservice.services;


import com.example.authservice.exceptions.UserAlreadyExistsException;
import com.example.authservice.exceptions.UserNotFoundException;
import com.example.authservice.exceptions.WrongPasswordException;
import com.example.authservice.models.Session;
import com.example.authservice.models.User;
import com.example.authservice.repositories.SessionRepository;
import com.example.authservice.repositories.UserRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class AuthService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;



    private SessionRepository sessionRepository;

    public AuthService(UserRepository userRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder,
                       SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.sessionRepository = sessionRepository;
    }

    public boolean signUp(String email, String password) throws UserAlreadyExistsException {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("User with email: " + email + " already exists");
        }

        User user = new User();

        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));

        userRepository.save(user);

        return true;
    }

    public String login(String email, String password) throws UserNotFoundException, WrongPasswordException {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User with email: " + email + " not found.");
        }

        boolean matches = bCryptPasswordEncoder.matches(
                password,
                userOptional.get().getPassword()
        );

        if (matches) {

            return "token";
        } else {
            throw new WrongPasswordException("Wrong password.");
        }
    }




}
