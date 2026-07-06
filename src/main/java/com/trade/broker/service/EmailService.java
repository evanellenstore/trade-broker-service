package com.trade.broker.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // FreeMarker configuration bean (automatically configured by Spring Boot when using starter-freemarker)
    @Autowired
    private Configuration freemarkerConfig;

    public void sendEmail(String to, String subject, Map<String, Object> model)
            throws MessagingException, IOException, TemplateException {

        // Load the FreeMarker template
        Template template = freemarkerConfig.getTemplate("mailTemplate.ftl");

        // Process the template with the model data to produce the HTML body
        String htmlBody = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

        // Create a MIME message
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);  // 'true' indicates that the text is HTML

        // Optionally, you can set the from address:
        // helper.setFrom("your_email@gmail.com");

        // Send the email
        mailSender.send(message);
    }

	
}

