package net.miscthings.messagelog.Entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import net.miscthings.messagelog.Entity.Message;
import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "string_tag")

public class StringTag {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long tagId;

    @Column(unique = true)
    private String tagContent;

    private int tagType = 0;

    @ManyToMany(mappedBy = "tags")
    private Set<Message> message;
}
