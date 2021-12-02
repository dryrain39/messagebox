package net.miscthings.messagelog.Service;

import net.miscthings.messagelog.Entity.Account;
import net.miscthings.messagelog.Entity.Message;
import net.miscthings.messagelog.Entity.StringTag;
import net.miscthings.messagelog.Repository.AccountRepository;
import net.miscthings.messagelog.Repository.MessageRepository;
import net.miscthings.messagelog.Repository.StringTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private StringTagRepository stringTagRepository;

    @Autowired
    private AccountRepository accountRepository;

    public Page<Message> listMessage(Account account, PageRequest pageRequest) {
        final Page<Message> all = messageRepository.findAllByRelated(account, pageRequest);
        System.out.println("all = " + all);
        return all;
    }

    public void publishMessage(Account account, String messageContent) {
        messageContent = messageContent.toLowerCase(Locale.ROOT);
        Set<String> tagStringSet = getTagString(messageContent);
        System.out.println("getTagString() = " + getTagString(messageContent));

        Set<StringTag> stringTags = new HashSet<>();

        for (String tagString : tagStringSet) {
            List<StringTag> foundTags = stringTagRepository.findAllByTagContent(tagString);
            stringTags.addAll(foundTags);
            if (foundTags.size() == 0) {
                StringTag st = new StringTag();
                st.setTagType(0);
                st.setTagContent(tagString);
                stringTags.add(st);
            }
        }

        if (stringTags.size() > 0) {
            stringTagRepository.saveAll(stringTags);
        }

        Set<Account> accountSet = new HashSet<>();
        accountSet.add(account);
        accountSet.addAll(getMention(messageContent));


        Message message = new Message();
        message.setMessageContent(messageContent);
        message.setTags(stringTags);
        message.setRelated(accountSet);

        messageRepository.save(message);
    }

    public Set<String> getTagString(String messageContent) {
        Pattern hashPattern = Pattern.compile("#([a-z가-힣ㄱ-ㅎ][0-9a-z가-힣ㄱ-ㅎ_.]+)");

        return regexTextSearch(messageContent, hashPattern);
    }

    public Set<Account> getMention(String messageContent) {
        Pattern hashPattern = Pattern.compile("@([a-z][a-z0-9_]+)");

        Set<String> searchSet = regexTextSearch(messageContent, hashPattern);

        Set<Account> allByNameIn = accountRepository.findAllByNameIn(searchSet);
        System.out.println("allByNameIn = " + allByNameIn);
        System.out.println("searchSet = " + searchSet);

        return allByNameIn;
    }

    private Set<String> regexTextSearch(String messageContent, Pattern hashPattern) {
        Matcher matcher = hashPattern.matcher(messageContent);
        Set<String> searchSet = new HashSet<>();

        int i = 0;
        while (matcher.find()) {
            searchSet.add(matcher.group(1));
            i++;
        }

        return searchSet;
    }
}
