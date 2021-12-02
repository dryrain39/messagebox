package net.miscthings.messagelog.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PostMessage {
    @Getter
    String messageContent;
}
