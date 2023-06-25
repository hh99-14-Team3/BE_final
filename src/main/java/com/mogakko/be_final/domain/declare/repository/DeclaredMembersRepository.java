package com.mogakko.be_final.domain.declare.repository;

import com.mogakko.be_final.domain.declare.entity.DeclaredMembers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeclaredMembersRepository extends JpaRepository<DeclaredMembers, Long> {
}
