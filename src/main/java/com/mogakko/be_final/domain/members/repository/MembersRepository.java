package com.mogakko.be_final.domain.members.repository;

import com.mogakko.be_final.domain.members.dto.response.MemberResponseDto;
import com.mogakko.be_final.domain.members.dto.response.MemberSimpleResponseDto;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MembersRepository extends JpaRepository<Members, Long> {
    Optional<Members> findByEmail(String email);

    Optional<Members> findBySocialUidAndSocialType(String socialUid, SocialType socialType);

    Optional<Members> findByNickname(String nickname);

    Optional<String> findByGithubId(String githubId);

    Optional<Members> findByFriendCode(Integer friendCode);

    Boolean existsByFriendCode(Integer friendCode);

    Boolean existsByNickname(String nickname);

    @Query("SELECT m FROM Members m WHERE m.nickname LIKE %:nickname%")
    List<Members> findByNicknameLike(@Param("nickname") String nickname);

    Optional<Members> findByGithubStateCode(String state);
}

