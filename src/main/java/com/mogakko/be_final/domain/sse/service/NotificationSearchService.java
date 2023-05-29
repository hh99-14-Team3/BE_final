package com.mogakko.be_final.domain.sse.service;

import com.mogakko.be_final.domain.sse.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationSearchService {
    private final NotificationRepository notificationRepository;

    // TODO:구현예정입니다. 알람 조회 요청이 오면 타입별로 검색해서 보내줄서비스를 생각해놔서 만들어놨어요

}
