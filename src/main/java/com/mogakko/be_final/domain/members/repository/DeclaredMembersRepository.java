package com.mogakko.be_final.domain.members.repository;

import com.mogakko.be_final.domain.members.entity.DeclaredMembers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeclaredMembersRepository extends JpaRepository<DeclaredMembers, Long> {

    Optional<DeclaredMembers> findByDeclaredMemberId(Long id);

}
