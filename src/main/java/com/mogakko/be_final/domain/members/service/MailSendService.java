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
                    .append("<h1>[이메일 인증!]</h1>")
                    .append("<p>아래의 링크를 클릭해서 인증을 완료하세요.</p>")
                    .append("<a href='http://localhost:8080/api/members/signup/confirm?email=")
                    .append(email)
                    .append("&authKey=")
                    .append(authKey)
                    .append("' target='_blenk'>인증 링크</a>")
                    .toString();
            sendMail.createMessage("회원가입 인증 메일", content, "bhjun95@gmail", "Admin", email);
            sendMail.send();
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return authKey;
    }
}