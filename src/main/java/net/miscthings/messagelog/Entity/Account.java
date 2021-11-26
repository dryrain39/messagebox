package net.miscthings.messagelog.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Getter
@Setter
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long userId;

    @Column(unique = true)
    private String name;

    private String password;

    @ManyToOne()
    private StringTag personalTag;
}
