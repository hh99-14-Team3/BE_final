package com.mogakko.be_final.domain.friendship.entity;

import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.util.Timestamped;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@AllArgsConstructor
public class Friendship extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    private Members sender;

    @Column(nullable = false)
    @ManyToOne
    @JoinColumn(name = "receiver_id", referencedColumnName = "id")
    private Members receiver;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FriendshipStatus status;

}
