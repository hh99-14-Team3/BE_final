package com.mogakko.be_final.domain.members.repository;

import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MembersRepository extends JpaRepository<Members, Long> {
    Optional<Members> findByEmail(String email);
    Optional<Members> findBySocialUidAndSocialType(String socialUid, SocialType socialType);
    Optional<Members> findByNickname(String nickname);
}

