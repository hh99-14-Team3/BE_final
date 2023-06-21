package com.mogakko.be_final.domain.directMessage.repository;

import com.mogakko.be_final.domain.directMessage.entity.DirectMessage;
import com.mogakko.be_final.domain.members.entity.Members;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DirectMessageRepository extends JpaRepository<DirectMessage, Long> {
    List<DirectMessage> findAllByReceiverAndDeleteByReceiverFalse(Members receiver);

    List<DirectMessage> findAllBySenderAndDeleteBySenderFalse(Members receiver);

}
