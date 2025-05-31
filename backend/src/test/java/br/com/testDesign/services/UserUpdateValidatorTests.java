package br.com.testDesign.services;

import br.com.testDesign.dto.user.UserUpdateDTO;
import br.com.testDesign.entities.UserEntity;
import br.com.testDesign.repositories.UserRepository;
import br.com.testDesign.services.validation.user.UserUpdateValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.HandlerMapping;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class UserUpdateValidatorTests {

    private UserRepository userRepository;
    private HttpServletRequest request;
    private ConstraintValidatorContext context;
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilder;

    private UserUpdateValidator validator;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        request = mock(HttpServletRequest.class);
        context = mock(ConstraintValidatorContext.class);
        violationBuilder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        nodeBuilder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class);

        // Cria spy da classe real
        validator = Mockito.spy(new UserUpdateValidator());

        // Injeta os mocks nos campos privados com ReflectionTestUtils
        ReflectionTestUtils.setField(validator, "request", request);
        ReflectionTestUtils.setField(validator, "repository", userRepository);

        // Configura ConstraintValidatorContext para o fluxo funcionar
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
        when(nodeBuilder.addConstraintViolation()).thenReturn(context);
    }

    @Test
    void isValidShouldReturnFalseWhenEmailExistsForAnotherUser() {
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setEmail("existing@example.com");

        UserEntity foundUser = new UserEntity();
        foundUser.setId(2L); // ID diferente do URI (1)

        Map<String, String> uriVars = new HashMap<>();
        uriVars.put("userId", "1");

        // Usando spy para retornar o map da URI
        doReturn(uriVars).when(request).getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        when(userRepository.findByEmail(dto.getEmail())).thenReturn(foundUser);

        boolean valid = validator.isValid(dto, context);

        assertFalse(valid);
        verify(context).buildConstraintViolationWithTemplate("Email já existe");
        verify(violationBuilder).addPropertyNode("email");
        verify(nodeBuilder).addConstraintViolation();
    }

    @Test
    void isValidShouldReturnTrueWhenEmailExistsForSameUser() {
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setEmail("existing@example.com");

        UserEntity foundUser = new UserEntity();
        foundUser.setId(1L); // Tem que ser o mesmo ID da URI

        Map<String, String> uriVars = new HashMap<>();
        uriVars.put("userId", "1");

        doReturn(uriVars).when(request).getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        when(userRepository.findByEmail(dto.getEmail())).thenReturn(foundUser);

        boolean valid = validator.isValid(dto, context);

        assertTrue(valid);
        verify(context, never()).buildConstraintViolationWithTemplate(anyString());
    }

    @Test
    void isValidShouldReturnTrueWhenEmailNotExists() {
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setEmail("new@example.com");

        Map<String, String> uriVars = new HashMap<>();
        uriVars.put("userId", "1");

        doReturn(uriVars).when(request).getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        when(userRepository.findByEmail(dto.getEmail())).thenReturn(null);

        boolean valid = validator.isValid(dto, context);

        assertTrue(valid);
        verify(context, never()).buildConstraintViolationWithTemplate(anyString());
    }

}
