package br.com.testDesign.services;

import br.com.testDesign.dto.user.UserInsertDTO;
import br.com.testDesign.entities.UserEntity;
import br.com.testDesign.repositories.UserRepository;
import br.com.testDesign.services.validation.user.UserInsertValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserInsertValidatorTests {

    private UserRepository userRepository;
    private ConstraintValidatorContext context;
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilder;

    private UserInsertValidator validator;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        context = mock(ConstraintValidatorContext.class);
        violationBuilder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        nodeBuilder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class);

        validator = new UserInsertValidator();

        // Injeta o mock no campo privado repository
        ReflectionTestUtils.setField(validator, "repository", userRepository);

        // Configura mocks do ConstraintValidatorContext para aceitar violação
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
        when(nodeBuilder.addConstraintViolation()).thenReturn(context);
    }

    @Test
    void isValidShouldReturnFalseWhenEmailAlreadyExists() {
        UserInsertDTO dto = new UserInsertDTO();
        dto.setEmail("existing@example.com");

        UserEntity existingUser = new UserEntity();
        existingUser.setEmail("existing@example.com");

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(existingUser);

        boolean valid = validator.isValid(dto, context);

        assertFalse(valid);
        verify(context).buildConstraintViolationWithTemplate("Email já existe");
        verify(violationBuilder).addPropertyNode("email");
        verify(nodeBuilder).addConstraintViolation();
    }

    @Test
    void isValidShouldReturnTrueWhenEmailDoesNotExist() {
        UserInsertDTO dto = new UserInsertDTO();
        dto.setEmail("new@example.com");

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(null);

        boolean valid = validator.isValid(dto, context);

        assertTrue(valid);
        verify(context, never()).buildConstraintViolationWithTemplate(anyString());
    }

}
