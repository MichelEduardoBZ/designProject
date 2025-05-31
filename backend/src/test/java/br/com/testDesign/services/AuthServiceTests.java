package br.com.testDesign.services;

import br.com.testDesign.dto.user.EmailDTO;
import br.com.testDesign.dto.user.NewPasswordDTO;
import br.com.testDesign.entities.PasswordRecoverEntity;
import br.com.testDesign.entities.UserEntity;
import br.com.testDesign.repositories.PasswordRecoverRepository;
import br.com.testDesign.repositories.UserRepository;
import br.com.testDesign.services.exceptions.ResourceNotFoundException;
import br.com.testDesign.tests.UserFactory;
import br.com.testDesign.util.CustomUserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class AuthServiceTests {

    private String existingEmail;
    private String nonExistingEmail;
    private String validToken;
    private String invalidToken;

    private UserEntity user;
    private PasswordRecoverEntity recoverEntity;

    @InjectMocks
    private AuthService service;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordRecoverRepository passwordRecoverRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private CustomUserUtil customUserUtil;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        existingEmail = "michel@gmail.com";
        nonExistingEmail = "naoexiste@gmail.com";
        validToken = UUID.randomUUID().toString();
        invalidToken = "invalid-token";

        user = UserFactory.createCustomClientUser(1L, existingEmail);
        recoverEntity = new PasswordRecoverEntity();
        recoverEntity.setEmail(existingEmail);
        recoverEntity.setToken(validToken);
        recoverEntity.setExpiration(Instant.now().plusSeconds(900L));

        service.setTokenMinutes(15L);
        service.setRecoverUri("http://localhost/recover/");
    }

    @Test
    public void createRecoverTokenShouldSendEmailWhenEmailExists() {
        EmailDTO dto = new EmailDTO(existingEmail);

        when(userRepository.findByEmail(existingEmail)).thenReturn(user);

        service.createRecoverToken(dto);

        verify(passwordRecoverRepository).save(any(PasswordRecoverEntity.class));
        verify(emailService).sendEmail(eq(existingEmail), eq("Recuperação de senha"), contains("http://localhost/recover/"));
    }

    @Test
    public void createRecoverTokenShouldThrowExceptionWhenEmailNotFound() {
        EmailDTO dto = new EmailDTO(nonExistingEmail);

        when(userRepository.findByEmail(nonExistingEmail)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> service.createRecoverToken(dto));

        verify(passwordRecoverRepository, never()).save(any());
        verify(emailService, never()).sendEmail(any(), any(), any());
    }

    @Test
    public void saveNewPasswordShouldUpdatePasswordWhenTokenIsValid() {
        NewPasswordDTO dto = new NewPasswordDTO();
        dto.setToken(validToken);
        dto.setPassword("novaSenha");

        when(passwordRecoverRepository.searchValidTokens(eq(validToken), any())).thenReturn(List.of(recoverEntity));
        when(userRepository.findByEmail(existingEmail)).thenReturn(user);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("senhaCriptografada");

        service.saveNewPassword(dto);

        verify(userRepository).save(user);
        assertEquals("senhaCriptografada", user.getPassword());
    }

    @Test
    public void saveNewPasswordShouldThrowExceptionWhenTokenIsInvalid() {
        NewPasswordDTO dto = new NewPasswordDTO();
        dto.setToken(invalidToken);

        when(passwordRecoverRepository.searchValidTokens(eq(invalidToken), any())).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> service.saveNewPassword(dto));

        verify(userRepository, never()).save(any());
    }

    @Test
    public void authenticatedShouldReturnUserWhenExists() {
        when(customUserUtil.getLoggedUsername()).thenReturn(existingEmail);
        when(userRepository.findByEmail(existingEmail)).thenReturn(user);

        UserEntity result = service.authenticated();

        assertNotNull(result);
        assertEquals(existingEmail, result.getUsername());
    }

    @Test
    public void authenticatedShouldThrowExceptionWhenErrorOccurs() {
        when(customUserUtil.getLoggedUsername()).thenThrow(ClassCastException.class);

        assertThrows(UsernameNotFoundException.class, () -> service.authenticated());
    }
}
