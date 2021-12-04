package net.miscthings.messagelog.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.miscthings.messagelog.Entity.StringTag;

import java.util.Set;

@Builder
@Getter
@Setter
public class SearchTag {
    Set<String> tags;
}
