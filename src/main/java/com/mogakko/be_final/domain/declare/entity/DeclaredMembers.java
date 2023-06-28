package com.mogakko.be_final.domain.declare.entity;

import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.util.Timestamped;
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
public class DeclaredMembers extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column
    private String reporterNickname;

    @ManyToOne
    private Members declaredMember;

    @Column
    @Enumerated(EnumType.STRING)
    private DeclaredReason declaredReason;

    @Column
    private String reason;

    @Column
    @Builder.Default
    private boolean isChecked = false;

    public void setChecked() {
        this.isChecked = true;
    }
}
