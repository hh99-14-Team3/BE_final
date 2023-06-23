package com.mogakko.be_final.domain.members.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeclaredMembers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column
    private String reportedNickname;

    @ManyToOne
    private Members declaredMember;

    @Column
    @Enumerated(EnumType.STRING)
    private DeclaredReason declaredReason;

    @Column
    private String reason;

}
