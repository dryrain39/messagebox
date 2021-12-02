package net.miscthings.messagelog.Repository;

import net.miscthings.messagelog.Entity.Account;
import net.miscthings.messagelog.Entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
//    List<Account> findByMessageId(Long messageId);

    Optional<Account> findAccountByName(String name);

    Set<Account> findAllByNameIn(Collection<String> stringCollection);
}
