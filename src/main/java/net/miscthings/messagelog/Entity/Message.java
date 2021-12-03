package net.miscthings.messagelog.Entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@Setter
@Table(name = "message", indexes = @Index(name = "lastModifiedAtDescIndex", columnList = "lastModifiedAt DESC"))
public class Message extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long messageId;

    @ManyToMany
    @JoinTable(name = "message_account")
    private Set<Account> related = new HashSet<>();

    @ManyToMany()
    @JoinTable(name = "message_string_tag")
    private Set<StringTag> tags = new HashSet<>();

    @Column(columnDefinition="TEXT")
    private String messageContent;
}
