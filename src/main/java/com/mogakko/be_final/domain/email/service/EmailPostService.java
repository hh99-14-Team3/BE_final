package com.mogakko.be_final.domain.email.service;

import com.mogakko.be_final.domain.email.dto.request.EmailConfirmRequestDto;
import com.mogakko.be_final.domain.email.entity.ConfirmationToken;
import com.mogakko.be_final.domain.email.repository.ConfirmationTokenRepository;
import com.mogakko.be_final.domain.members.dto.request.ChangePwRequestDto;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.util.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static com.mogakko.be_final.exception.ErrorCode.EMAIL_NOT_FOUND;
import static com.mogakko.be_final.exception.ErrorCode.USER_NOT_FOUND;


@Service
@RequiredArgsConstructor
public class EmailPostService {
    private final JavaMailSender emailSender;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final MembersRepository memberRepository;
    private final ConfirmationTokenService confirmationTokenService;
    private final MembersRepository membersRepository;
    private final PasswordEncoder passwordEncoder;

    private MimeMessage createMessage(String receiverEmail) throws Exception {
        Members findMember = memberRepository.findByEmail(receiverEmail).orElseThrow(
                () -> new CustomException(EMAIL_NOT_FOUND));
        MimeMessage message = emailSender.createMimeMessage();
        ConfirmationToken emailConfirmationToken = ConfirmationToken.createConfirmationToken(findMember.getEmail());
        confirmationTokenRepository.save(emailConfirmationToken);

        message.addRecipients(MimeMessage.RecipientType.TO, receiverEmail);
        message.setSubject("[모각코ON:] 새로운 비밀번호를 설정해 주세요.");

        String emailMsg = "";
        emailMsg += "<div style=\"padding: 26px 18px;\">";
        emailMsg += "<img src=\"로고이미지 같은거 넣기\" style=\"width: 105px; height: 31px;\" loading=\"lazy\">";
        emailMsg += "<h1 style=\"margin-top: 23px; margin-bottom: 9px; color: #222222; font-size: 19px; line-height: 25px; letter-spacing: -0.27px;\">새 비밀번호 설정</h1>";
        emailMsg += "<div style=\"margin-top: 7px; margin-bottom: 22px; color: #222222;\">";
        emailMsg += "<p style=\"margin-block-start: 0; margin-block-end: 0; margin-inline-start: 0; margin-inline-end: 0; line-height: 1.47; letter-spacing: -0.22px; font-size: 15px; margin: 8px 0 0;\">안녕하세요, [모각코ON:] 입니다.</p>";
        emailMsg += "<p style=\"margin-block-start: 0; margin-block-end: 0; margin-inline-start: 0; margin-inline-end: 0; line-height: 1.47; letter-spacing: -0.22px; font-size: 15px; margin: 8px 0 0;\">아래 버튼을 눌러 새 비밀번호를 설정해 주세요.</p>";
        emailMsg += "<p style=\"margin-block-start: 0; margin-block-end: 0; margin-inline-start: 0; margin-inline-end: 0; line-height: 1.47; letter-spacing: -0.22px; font-size: 15px; margin: 8px 0 0;\">";
        emailMsg += "<a href=\"https://codingking.store/members/updatePassword?token=" + emailConfirmationToken.getId() + "\" style=\"text-decoration: none; color: white; display: inline-block; font-size: 15px; font-weight: 500; font-stretch: normal; font-style: normal; line-: normal; letter-spacing: normal; border-radius: 2px; background-color: #141517; margin: 24px 0 19px; padding: 11px 6px;\" rel=\"noreferrer noopener\" target=\"_blank\">비밀번호 변경하기</a>";
        emailMsg += "</p>";
        emailMsg += "<p style=\"margin-block-start: 0; margin-block-end: 0; margin-inline-start: 0; margin-inline-end: 0; line-height: 1.47; letter-spacing: -0.22px; font-size: 15px; margin: 20px 0;\">";
        emailMsg += "감사합니다.<br>";
        emailMsg += "모각코ON: 드림";
        emailMsg += "</p>";
        emailMsg += "<hr style=\"display: block; height: 1px; background-color: #ebebeb; margin: 14px 0; padding: 0; border-width: 0;\">";
        emailMsg += "<div>";
        emailMsg += "<div>";
        emailMsg += "<p style=\"margin-block: 0; margin-inline: 0; font-weight: normal; font-size: 14px; font-stretch: normal; font-style: normal; line-height: 1.43; letter-spacing: normal; color: #8a8a8a; margin: 5px 0 0;\">본 메일은 발신전용 메일로 회신되지 않습니다. 본 메일과 관련되어 궁금하신 점이나 불편한 사항은 고객센터에 문의해 주시기 바랍니다.</p>";
        emailMsg += "</div>";
        emailMsg += "<div>";
        emailMsg += "<p style=\"margin-block: 0; margin-inline: 0; font-weight: normal; font-size: 14px; font-stretch: normal; font-style: normal; line-height: 1.43; letter-spacing: normal; color: #8a8a8a; margin: 5px 0 0;\"> [모각코ON:] | 항해99 14기 3조 | cs@mogakko.co.kr<br>";
        emailMsg += "전화번호: 010-0000-0000 | E-mail: <br>";
        emailMsg += "Copyright © 2023 by <b>MogakkoON:, Inc.</b> All rights reserved.</p>";
        emailMsg += "</div>";
        emailMsg += "</div>";
        emailMsg += "</div>";
        message.setText(emailMsg, "utf-8", "html");
        message.setFrom(new InternetAddress("bhjun95@gmail.com", "모각코ON:"));

        return message;
    }

    public ResponseEntity<Message> sendSimpleMessage(EmailConfirmRequestDto requestDto) throws Exception {
        MimeMessage message = createMessage(requestDto.getEmail());
        try {
            emailSender.send(message);
        } catch (MailException es) {
            es.printStackTrace();
            throw new IllegalArgumentException();
        }
        return new ResponseEntity<>(new Message("이메일을 성공적으로 보냈습니다.", null), HttpStatus.OK);
    }


    //이메일 검증 후 비밀번호 변경
    @Transactional
    public ResponseEntity<Message> confirmEmailToFindPassword(String token, ChangePwRequestDto requestDto) {
        ConfirmationToken findConfirmationToken = confirmationTokenService.findByIdAndExpired(token);
        Members findMember = membersRepository.findByEmail(findConfirmationToken.getEmail()).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND));
        String password = passwordEncoder.encode(requestDto.getPassword());

        findConfirmationToken.useToken();

        findMember.changePassword(password);
        return new ResponseEntity<>(new Message("비밀번호 변경 성공", null), HttpStatus.OK);
    }
}
