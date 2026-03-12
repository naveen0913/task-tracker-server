package com.task.management.service;

import com.task.management.DTO.SignupRequest;
import com.task.management.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    AuthService authService;

    @Test
    void createNewUserTest(){
        System.out.println("hello");
        SignupRequest signupRequest = new SignupRequest();
        authService.signup(signupRequest);
    }
}
