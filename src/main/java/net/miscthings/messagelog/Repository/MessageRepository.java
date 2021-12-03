package net.miscthings.messagelog.Repository;

import net.miscthings.messagelog.Entity.Account;
import net.miscthings.messagelog.Entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Optional<List<Message>> findByMessageId(Long messageId);
    Page<Message> findAllByRelated(Account account, Pageable pageable);
    Optional<Message> findByMessageIdAndRelated(Long messageId, Account account);
}
