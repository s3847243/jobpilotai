package com.example.jobpilot.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.jobpilot.user.dto.UserDTO;
import com.example.jobpilot.user.dto.UserProfileUpdateRequest;
import com.example.jobpilot.user.model.User;
import com.example.jobpilot.user.repository.UserRepository;
import com.example.jobpilot.user.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void getUserByEmail_shouldReturnUserDTO_whenUserExists() {
        String email = "test@example.com";
        User user = User.builder()
                .userId(UUID.randomUUID())
                .email(email)
                .fullName("Test User")
                .location("Melbourne")
                .jobTitle("Software Engineer")
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        
        UserDTO result = userService.getUserByEmail(email);

        assertNotNull(result);
        assertEquals(user.getUserId(), result.getId());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getFullName(), result.getFullName());
        assertEquals(user.getLocation(), result.getLocation());
        assertEquals(user.getJobTitle(), result.getJobTitle());

        verify(userRepository).findByEmail(email);
    }

    @Test
    void getUserByEmail_shouldThrowException_whenUserNotFound() {
        String email = "missing@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.getUserByEmail(email));
        verify(userRepository).findByEmail(email);
    }

    @Test
void updateProfile_shouldUpdateFieldsAndReturnDTO() {
    User user = User.builder()
            .userId(UUID.randomUUID())
            .fullName("Old Name")
            .location("Old Location")
            .jobTitle("Old Title")
            .phone("1234567890")
            .build();

    UserProfileUpdateRequest request = new UserProfileUpdateRequest();
    request.setName("New Name");
    request.setLocation("New Location");
    request.setJobTitle("New Title");
    request.setPhone("9876543210");

    UserDTO result = userService.updateProfile(user, request);

    assertEquals("New Name", result.getFullName());
    assertEquals("New Location", result.getLocation());
    assertEquals("New Title", result.getJobTitle());

    assertEquals("9876543210", user.getPhone());

    verify(userRepository).save(user);
}

@Test
void updateProfile_shouldNotChangeFieldsIfNull() {
    User user = User.builder()
            .userId(UUID.randomUUID())
            .fullName("Same Name")
            .location("Same Location")
            .jobTitle("Same Title")
            .phone("1234567890")
            .build();

    UserProfileUpdateRequest request = new UserProfileUpdateRequest(); // All null

    UserDTO result = userService.updateProfile(user, request);

    assertEquals("Same Name", result.getFullName());
    assertEquals("Same Location", result.getLocation());
    assertEquals("Same Title", result.getJobTitle());
    assertEquals("1234567890", user.getPhone());

    verify(userRepository).save(user);
}
@Test
void deleteAccount_shouldDeleteUser() {
    User user = User.builder().userId(UUID.randomUUID()).build();

    userService.deleteAccount(user);

    verify(userRepository).delete(user);
}

}
