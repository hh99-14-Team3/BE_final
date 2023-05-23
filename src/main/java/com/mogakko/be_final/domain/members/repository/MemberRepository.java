package com.mogakko.be_final.domain.members.repository;

import com.mogakko.be_final.domain.members.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
