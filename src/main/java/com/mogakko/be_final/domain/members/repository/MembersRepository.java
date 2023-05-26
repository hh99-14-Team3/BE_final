package com.mogakko.be_final.domain.members.repository;

import com.mogakko.be_final.domain.members.entity.Members;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MembersRepository extends JpaRepository<Members, Long> {
    Optional<Members> findByEmail(String email);

//    Optional<Members> findByKakaoId(Long kakaoId);
    Optional<Members> findByNickname(String nickname);


}

