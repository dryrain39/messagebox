package net.miscthings.messagelog.Repository;

import net.miscthings.messagelog.Entity.Message;
import net.miscthings.messagelog.Entity.StringTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StringTagRepository extends JpaRepository<StringTag, Long> {
    List<StringTag> findAllByTagContent(String tagContent);

}
