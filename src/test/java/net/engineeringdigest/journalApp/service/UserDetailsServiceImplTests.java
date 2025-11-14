package net.engineeringdigest.journalApp.service;

import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;

import static org.mockito.Mockito.*;

@ActiveProfiles("dev")
public class UserDetailsServiceImplTests {
    @InjectMocks
    private UserDetailServiceImpl userDetailServiceImpl;

    @Mock
    private UserRepository userRepository;

    // Initialize mocks before each test
    @BeforeEach
    void setUp () {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void loadUserByUsernameTest() {
        when(userRepository.findByUsername(ArgumentMatchers.anyString())).
                thenReturn(User.builder().username("Gasly").password("nbzxcv&^8z*&)(@$%jlkjl").roles(new ArrayList<>()).build());

        UserDetails user = userDetailServiceImpl.loadUserByUsername("Gasly");

        Assertions.assertNotNull(user);
    }
}
