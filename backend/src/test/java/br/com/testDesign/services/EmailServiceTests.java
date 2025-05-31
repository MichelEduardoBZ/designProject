package br.com.testDesign.services;

import br.com.testDesign.services.exceptions.EmailException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class EmailServiceTests {

    @InjectMocks
    private EmailService emailService;

    @Mock
    private JavaMailSender emailSender;

    private final String to = "destinatario@teste.com";
    private final String subject = "Assunto Teste";
    private final String body = "Corpo do email";
    private final String emailFrom = "remetente@teste.com";

    @BeforeEach
    public void setUp() {
        emailService.setEmailFrom(emailFrom);
        emailService.setEmailSender(emailSender);
    }

    @Test
    public void sendEmailShouldSendSuccessfully() {
        doNothing().when(emailSender).send(Mockito.any(SimpleMailMessage.class));

        assertDoesNotThrow(() -> emailService.sendEmail(to, subject, body));
        verify(emailSender, times(1)).send(Mockito.any(SimpleMailMessage.class));
    }

    @Test
    public void sendEmailShouldThrowEmailExceptionWhenMailFails() {
        doThrow(new MailException("Erro simulado") {}).when(emailSender).send(Mockito.any(SimpleMailMessage.class));

        assertThrows(EmailException.class, () -> emailService.sendEmail(to, subject, body));
        verify(emailSender, times(1)).send(Mockito.any(SimpleMailMessage.class));
    }
}
