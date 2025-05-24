package br.com.testDesign.services.validation.user;

import br.com.testDesign.dto.user.UserUpdateDTO;
import br.com.testDesign.entities.UserEntity;
import br.com.testDesign.repositories.UserRepository;
import br.com.testDesign.resources.exceptions.FieldMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserUpdateValidator implements ConstraintValidator<UserUpdateValid, UserUpdateDTO> {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private UserRepository repository;

    @Override
    public void initialize(UserUpdateValid ann) {
    }

    @Override
    public boolean isValid(UserUpdateDTO dto, ConstraintValidatorContext context) {
        List<FieldMessage> errors = new ArrayList<>();

        @SuppressWarnings("unchecked")
        var uriVars = (Map <String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        validateEmailExists(uriVars, dto, errors);

        for (FieldMessage e : errors) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage())
                    .addPropertyNode(e.getFieldName())
                    .addConstraintViolation();
        }

        return errors.isEmpty();
    }

    private void validateEmailExists(Map <String, String> uriVars, UserUpdateDTO dto, List<FieldMessage> errors) {
        UserEntity user = repository.findByEmail(dto.getEmail());
        long userId = Long.parseLong(uriVars.get("userId"));

        if (user != null && userId != user.getId() ) {
            errors.add(new FieldMessage("email", "Email já existe"));
        }
    }
}