package net.miscthings.messagelog.TextEngine;

import org.springframework.stereotype.Service;

@Service
public class TextEngine {
    public String prettyText(String content){
        content = content.replaceAll("->", "&rarr;");
        content = content.replaceAll("<-", "&larr;");
        content = content.replaceAll("=>", "&rArr;");
        content = content.replaceAll("<=", "&lArr;");

        return content;
    }
}
