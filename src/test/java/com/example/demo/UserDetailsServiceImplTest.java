package com.example.demo;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.security.UserDetailsServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserDetailsServiceImplTest {

    private UserDetailsServiceImpl userDetailsService;

    private UserRepository userRepo = mock(UserRepository.class);

    @Before
    public void setUp() {
        userDetailsService = new UserDetailsServiceImpl();
        TestUtils.inject(userDetailsService, "userRepository", userRepo);
    }

    @Test(expected = UsernameNotFoundException.class)
    public void loadUserByUsername() {
        when(userRepo.findByUsername("testuser")).thenReturn(getMockUser());

        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());

        userDetailsService.loadUserByUsername("user");
    }

    private User getMockUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("testpassword");
        return user;
    }
}
