package com.mogakko.be_final.domain.sse.dto;

import com.mogakko.be_final.domain.sse.entity.Notification;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Schema(description = "알림 Dto")
@Getter
public class NotificationResponseDto {
    private Long id;
    private String content;
    private String url;
    private Boolean isRead;
    private String createdAt;


    public NotificationResponseDto() {
    }

    public NotificationResponseDto(Notification notification) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.id = notification.getId();
        this.content = notification.getContent();
        this.url = notification.getUrl();
        this.isRead = notification.getIsRead();
        this.createdAt = notification.getCreatedAt() != null
                ? notification.getCreatedAt().format(formatter)
                : null;
    }

    public static class Builder {
        private Long id;
        private String content;
        private String url;
        private Boolean isRead;
        private String createdAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder isRead(Boolean isRead) {
            this.isRead = isRead;
            return this;
        }

        public Builder createdAt(String createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public NotificationResponseDto build() {
            NotificationResponseDto dto = new NotificationResponseDto();
            dto.id = this.id;
            dto.content = this.content;
            dto.url = this.url;
            dto.isRead = this.isRead;
            dto.createdAt = this.createdAt;
            return dto;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
