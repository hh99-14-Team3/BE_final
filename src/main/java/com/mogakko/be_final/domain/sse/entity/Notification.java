package com.mogakko.be_final.domain.sse.entity;

import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.util.Timestamped;
import com.mysql.cj.protocol.ColumnDefinition;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = true)
@Getter
public class Notification extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private NotificationContent content;

    @Embedded
    private RelatedUrl url;

    @Column(nullable = false)
    private Boolean isRead;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    private Members sender;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", referencedColumnName = "id")
    private Members receiver;

    @Builder
    public Notification(Members sender,Members receiver, NotificationType notificationType, String content, String url) {
        this.sender = sender;
        this.receiver = receiver;
        this.notificationType = notificationType;
        this.content = new NotificationContent(content);
        this.url = new RelatedUrl(url);
        this.isRead = false;
    }

    public String getContent() {
        return content.getContent();
    }

    public String getUrl() {
        return url.getUrl();
    }

    public void read() {
        isRead = true;
    }
}

