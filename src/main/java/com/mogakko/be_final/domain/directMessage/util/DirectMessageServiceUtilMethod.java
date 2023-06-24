package com.mogakko.be_final.domain.directMessage.util;

import com.mogakko.be_final.domain.directMessage.entity.DirectMessage;
import com.mogakko.be_final.domain.directMessage.repository.DirectMessageRepository;
import com.mogakko.be_final.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.mogakko.be_final.exception.ErrorCode.MESSAGE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class DirectMessageServiceUtilMethod {

    private DirectMessageRepository directMessageRepository;

    public DirectMessage findDirectMessageById(Long id) {
        return directMessageRepository.findById(id).orElseThrow(
                () -> new CustomException(MESSAGE_NOT_FOUND)
        );
    }
}
