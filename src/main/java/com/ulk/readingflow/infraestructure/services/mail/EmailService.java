package com.ulk.readingflow.infraestructure.services.mail;

import com.ulk.readingflow.api.exceptions.MailException;
import com.ulk.readingflow.api.v1.payloads.responses.dtos.RequestedBookDTO;
import com.ulk.readingflow.domain.enumerations.SystemMessages;
import com.ulk.readingflow.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
public class EmailService {

    @Value("${mail.smtp.host}")
    private String smtpHost;

    @Value("${mail.smtp.port}")
    private String smtpPort;

    @Value("${mail.smtp.auth}")
    private String smtpAuth;

    @Value("${mail.smtp.starttls.enable}")
    private String smtpStarttlsEnable;

    @Value("${dev.email}")
    private String developerMail;

    @Value("${email.username}")
    private String emailUsername;

    @Value("${email.password}")
    private String emailPassword;

    @Value("${mail.debug}")
    private Boolean mailDebug;

    private final EmailTemplateService templateService;
    private final MessageUtils messageUtils;

    @Autowired
    public EmailService(EmailTemplateService templateService, MessageUtils messageUtils) {
        this.templateService = templateService;
        this.messageUtils = messageUtils;
    }

    public void sendMail(RequestedBookDTO requestedBookDTO, MultipartFile bookFile) {
        sendMail(requestedBookDTO, this.developerMail, bookFile);
    }

    public void sendMail(RequestedBookDTO requestedBookDTO,
                         String cc,
                         MultipartFile bookFile) {

        Properties props = new Properties();
        props.put("mail.smtp.host", this.smtpHost);
        props.put("mail.smtp.port", this.smtpPort);
        props.put("mail.smtp.auth", this.smtpAuth);
        props.put("mail.smtp.starttls.enable", this.smtpStarttlsEnable);

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailUsername, emailPassword);
                    }
                });

        session.setDebug(this.mailDebug);

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(this.emailUsername));

            if (null == bookFile) {
                message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc));
                message.setSubject("Solicitação " + requestedBookDTO.getMailStatus() + " com Sucesso!");
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(requestedBookDTO.getOwner().getEmail()));

                Map<String, Object> variables = new HashMap<>();
                variables.put("username", requestedBookDTO.getOwner().getUsername());
                variables.put("status", requestedBookDTO.getMailStatus());
                variables.put("bookTitle", requestedBookDTO.getBookTitle());
                variables.put("bookAuthor", requestedBookDTO.getAuthorName());
                if (requestedBookDTO.getAssigned() != null) {
                    variables.put("responsible", requestedBookDTO.getAssigned().getUsername());
                }

                String htmlContent = this.templateService.processTemplate("requestedBook.html", variables);
                message.setContent(htmlContent, "text/html; charset=utf-8");
            } else {
                message.setSubject(requestedBookDTO.getBookTitle());
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(requestedBookDTO.getOwner().getKindleMail()));

                MimeBodyPart bodyPart = new MimeBodyPart();

                String body = "Livro: " + requestedBookDTO.getBookTitle() + " solicitado em: " + requestedBookDTO.getRequestedDate();
                bodyPart.setText(body);

                MimeBodyPart attachment = new MimeBodyPart();
                attachment.setFileName(bookFile.getOriginalFilename());
                attachment.setContent(bookFile.getBytes(), bookFile.getContentType());

                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(bodyPart);
                multipart.addBodyPart(attachment);

                message.setContent(multipart);
            }

            Transport.send(message);

        } catch (MessagingException | IOException e) {
           throw new MailException(this.messageUtils.getMessage(SystemMessages.ERROR_MAILSERVICE));
        }
    }
}
