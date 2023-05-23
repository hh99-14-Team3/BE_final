package com.mogakko.be_final.domain.members.service;

import com.mogakko.be_final.domain.members.entity.EmailVerification;
import com.mogakko.be_final.domain.members.repository.EmailVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Random;

@Service("mss")
@RequiredArgsConstructor
public class MailSendService {
    private final JavaMailSenderImpl mailSender;
    private final EmailVerificationRepository emailVerificationRepository;

    private String getKey(int size) {
        return getAuthCode(size);
    }

    private String getAuthCode(int size) {
        Random random = new Random();
        StringBuffer buffer = new StringBuffer();
        int num;

        while(buffer.length() < size) {
            num = random.nextInt(10);
            buffer.append(num);
        }

        return buffer.toString();
    }

    public String sendAuthMail(String email) {
        String authKey = getKey(6);

        System.out.println(email);

        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);
        EmailVerification emailVerification = new EmailVerification(email, authKey, expirationTime);
        emailVerificationRepository.save(emailVerification);

        try {
            MailUtils sendMail = new MailUtils(mailSender);
            String content = new StringBuilder()
                    .append("<h1>[Email authentication]</h1>")
                    .append("<p>Click the link below to complete email verification.</p>")
                    .append("<a href='http://localhost:8080/api/members/signUpConfirm?email=")
                    .append(email)
                    .append("&authKey=")
                    .append(authKey)
                    .append("' target='_blenk'>Verify Email Verification</a>")
                    .toString();
            sendMail.createMessage("Verification of member registration email", content, "bhjun95@gmail", "Admin", email);
            sendMail.send();
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return authKey;
    }
}