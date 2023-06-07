package com.mogakko.be_final.domain.directMessage.entity;

import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.util.Timestamped;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DirectMessage extends Timestamped {

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

    @Column(length = 320)
    //320byte로 설정해 두었지만 최대 100자입니다. 입력제한을 300byte로 해야할듯합니다.
    private String content;

    private boolean isRead;

    public DirectMessage(Members sender, Members receiver, String content, boolean isRead) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.isRead = isRead;
    }

    public void markRead() {
        this.isRead = true;
    }
}
