package br.com.testDesign.services.validation.user;

import br.com.testDesign.dto.user.UserInsertDTO;
import br.com.testDesign.entities.UserEntity;
import br.com.testDesign.repositories.UserRepository;
import br.com.testDesign.resources.exceptions.FieldMessage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class UserInsertValidator implements ConstraintValidator<UserInsertValid, UserInsertDTO> {

    @Autowired
    private UserRepository repository;

    @Override
    public void initialize(UserInsertValid ann) {
    }

    @Override
    public boolean isValid(UserInsertDTO dto, ConstraintValidatorContext context) {
        List<FieldMessage> errors = new ArrayList<>();

        validateEmailExists(dto, errors);

        for (FieldMessage e : errors) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage())
                    .addPropertyNode(e.getFieldName())
                    .addConstraintViolation();
        }

        return errors.isEmpty();
    }

    private void validateEmailExists(UserInsertDTO dto, List<FieldMessage> errors) {
        UserEntity user = repository.findByEmail(dto.getEmail());

        if (user != null) {
            errors.add(new FieldMessage("email", "Email já existe"));
        }
    }
}