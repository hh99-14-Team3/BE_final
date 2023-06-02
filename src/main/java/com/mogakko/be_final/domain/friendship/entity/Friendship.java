package com.mogakko.be_final.domain.friendship.entity;

import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.util.Timestamped;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@AllArgsConstructor
public class Friendship extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    private Members sender;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne
    @JoinColumn(name = "receiver_id", referencedColumnName = "id")
    private Members receiver;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FriendshipStatus status;

    public Friendship(Members sender, Members receiver, FriendshipStatus status) {
        this.sender = sender;
        this.receiver = receiver;
        this.status = status;
    }

    public void accept() {
        this.status = FriendshipStatus.ACCEPT;
    }

    public void refuse() {
        this.status = FriendshipStatus.REFUSE;
    }

}
