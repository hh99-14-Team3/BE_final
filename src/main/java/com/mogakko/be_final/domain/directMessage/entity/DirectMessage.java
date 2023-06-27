package com.mogakko.be_final.domain.directMessage.entity;

import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.util.Timestamped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DirectMessage extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    private Members sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", referencedColumnName = "id")
    private Members receiver;

    @Column(length = 500)
    @Lob
    private String content;

    @Column(name = "delete_by_sender")
    private  boolean deleteBySender = false;

    @Column(name = "delete_by_receiver")
    private  boolean deleteByReceiver =false;

    private boolean isRead;

    public DirectMessage(Members sender, Members receiver, String content, boolean isRead) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.isRead = isRead;
    }

    public void markDeleteBySenderTrue() { this.deleteBySender = true; }

    public void markDeleteByReceiverTrue() { this.deleteByReceiver = true; }

    public void markRead() {
        this.isRead = true;
    }
}
