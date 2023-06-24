package com.mogakko.be_final.domain.sse.service;

import com.mogakko.be_final.domain.sse.dto.response.NotificationResponseDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class NotificationProcessor {

    private final Sinks.Many<NotificationResponseDto> processor = Sinks.many().multicast().directBestEffort();

    public void publish(NotificationResponseDto notification) {
        processor.tryEmitNext(notification);
    }

    public Flux<NotificationResponseDto> stream() {
        return processor.asFlux();
    }
}
