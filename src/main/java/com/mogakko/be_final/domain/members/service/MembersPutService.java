package com.mogakko.be_final.domain.members.service;

import com.mogakko.be_final.S3.S3Uploader;
import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.util.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.mogakko.be_final.exception.ErrorCode.DUPLICATE_NICKNAME;

@Service
@RequiredArgsConstructor
public class MembersPutService {

    private final MembersRepository membersRepository;
    private final S3Uploader s3Uploader;

    // 마이페이지 - 프로필 사진, 닉네임 수정
    @Transactional
    public ResponseEntity<Message> profileUpdate(MultipartFile imageFile, String nickname, Members member) throws IOException {
        if (member.getMemberStatusCode().equals(MemberStatusCode.BASIC)) {
            member.changeMemberStatusCode(MemberStatusCode.NORMAL);
        }

        if (imageFile != null) {
            s3Uploader.delete(member.getProfileImage());
            String profileImage = s3Uploader.uploadFile(imageFile);
            member.updateProfileImage(profileImage);
        }

        if (nickname != null) {
            if (membersRepository.findByNickname(nickname).isPresent()) throw new CustomException(DUPLICATE_NICKNAME);
            else member.updateNickname(nickname);
        }

        membersRepository.save(member);
        return new ResponseEntity<>(new Message("프로필 정보 변경 성공", null), HttpStatus.OK);
    }


    // 마이페이지 - 프로필 사진 삭제
    public ResponseEntity<Message> profileDelete(Members member) {
        member.deleteProfile(member.getNickname());
        membersRepository.save(member);
        return new ResponseEntity<>(new Message("프로필 사진 삭제 성공", null), HttpStatus.OK);
    }

    // 튜토리얼 확인용
    @Transactional
    public ResponseEntity<Message> tutorialCheck(Members member) {
        member.setTutorialCheck();
        membersRepository.save(member);
        return new ResponseEntity<>(new Message("튜토리얼 확인 요청 성공", null), HttpStatus.OK);
    }
}
