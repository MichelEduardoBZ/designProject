package br.com.testDesign.services;

import br.com.testDesign.dto.user.EmailDTO;
import br.com.testDesign.dto.user.NewPasswordDTO;
import br.com.testDesign.entities.PasswordRecoverEntity;
import br.com.testDesign.entities.UserEntity;
import br.com.testDesign.repositories.PasswordRecoverRepository;
import br.com.testDesign.repositories.UserRepository;
import br.com.testDesign.services.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Value("${email.password-recover.token.minutes}")
    private Long tokenMinutes;

    @Value("${email.password-recover.uri}")
    private String recoverUri;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordRecoverRepository passwordRecoverRepository;

    @Autowired
    private EmailService emailService;

    @Transactional
    public void createRecoverToken(EmailDTO body) {
        UserEntity user = userRepository.findByEmail(body.getEmail());

        if (user == null) {
            throw new ResourceNotFoundException("Email não encontrado.");
        }

        String token = UUID.randomUUID().toString();

        PasswordRecoverEntity passwordRecover = createPasswordRecover(body, token);
        passwordRecoverRepository.save(passwordRecover);

        emailService.sendEmail(body.getEmail(), "Recuperação de senha", emailContent(token));
    }

    private PasswordRecoverEntity createPasswordRecover(EmailDTO body, String token) {
        PasswordRecoverEntity passwordRecover = new PasswordRecoverEntity();
        passwordRecover.setEmail(body.getEmail());
        passwordRecover.setToken(token);
        passwordRecover.setExpiration(Instant.now().plusSeconds(tokenMinutes * 60L));
        return passwordRecover;
    }

    private String emailContent(String token) {
        StringBuilder sb = new StringBuilder();
        sb.append("Acesse o link para redefinir sua senha!");
        sb.append("\n\n");
        sb.append(recoverUri).append(token);
        sb.append("\n\n");
        sb.append("Validade de ").append(tokenMinutes).append(" minutos.");
        return sb.toString();
    }

    @Transactional
    public void saveNewPassword(@Valid NewPasswordDTO body) {

    List<PasswordRecoverEntity> passwordsRecover = passwordRecoverRepository.searchValidTokens(body.getToken(), Instant.now());

    if (passwordsRecover == null || passwordsRecover.isEmpty()) {
        throw new ResourceNotFoundException("Token inválido");
    }

    UserEntity user = userRepository.findByEmail(passwordsRecover.get(0).getEmail());
    user.setPassword(passwordEncoder.encode(body.getPassword()));

    userRepository.save(user);
    }

    protected UserEntity authenticated() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
            String username = jwtPrincipal.getClaim("username");
            return userRepository.findByEmail(username);
        } catch (Exception e) {
            throw new UsernameNotFoundException("Invalid user");
        }
    }

}
