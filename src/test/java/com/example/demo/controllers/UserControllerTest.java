package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;

    private UserRepository userRepo = mock(UserRepository.class);

    private CartRepository cartRepo = mock(CartRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.inject(userController, "userRepository", userRepo);
        TestUtils.inject(userController, "cartRepository", cartRepo);
        TestUtils.inject(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void create_user_happy_path() throws Exception {
        when(encoder.encode("testpassword")).thenReturn("thisIsHashed");

        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("testpassword");
        r.setConfirmPassword("testpassword");

        final ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("test", u.getUsername());
        assertEquals("thisIsHashed", u.getPassword());
    }

    @Test
    public void findByIdTest() {
        when(userRepo.findById(0L)).thenReturn(getMockUser());
        ResponseEntity<User> response = userController.findById(0L);
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0L, user.getId());
    }

    @Test
    public void findByUserName() {
        when(userRepo.findByUsername("test")).thenReturn(getMockUser().get());
        ResponseEntity<User> response = userController.findByUserName("test");

        User user = response.getBody();
        assertNotNull(user);
        assertEquals("test", user.getUsername());
    }

    private Optional<User> getMockUser() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("testpassword");
        return Optional.of(user);
    }
}
