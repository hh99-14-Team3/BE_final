package com.mogakko.be_final.domain.members.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

public class MailUtils {
    private final JavaMailSender mailSender;
    private final MimeMessage message;
    private final MimeMessageHelper messageHelper;

    public MailUtils(JavaMailSender mailSender) throws MessagingException {
        this.mailSender = mailSender;
        this.message = this.mailSender.createMimeMessage();
        this.messageHelper = new MimeMessageHelper(message, true, "UTF-8");
    }


    public MimeMessage createMessage(String subject, String htmlContent, String fromEmail, String fromName, String to) throws MessagingException, UnsupportedEncodingException {
        System.out.println(fromEmail);
        messageHelper.setSubject(subject);
        messageHelper.setText(htmlContent, true);
        messageHelper.setFrom(fromEmail, fromName);
        messageHelper.setTo(to);
        return this.message;
    }

    public void send() {
        mailSender.send(message);
    }
}
