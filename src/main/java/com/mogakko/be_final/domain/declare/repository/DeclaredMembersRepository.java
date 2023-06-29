package com.mogakko.be_final.domain.declare.repository;

import com.mogakko.be_final.domain.declare.entity.DeclaredMembers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeclaredMembersRepository extends JpaRepository<DeclaredMembers, Long> {
}
