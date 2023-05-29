package com.mogakko.be_final.domain.members.repository;

import com.mogakko.be_final.domain.members.entity.KakaoMembers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KakaoMembersRepository extends JpaRepository<KakaoMembers, Long> {
    Optional<KakaoMembers> findByEmail(String email);

    Optional<KakaoMembers> findByKakaoId(Long kakaoId);

    Optional<KakaoMembers> findByNickname(String nickname);     // TODO : 이 부분 사용하는지 체크해주세요
}
