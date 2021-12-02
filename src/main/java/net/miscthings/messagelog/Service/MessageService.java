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

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

    public void publishMessage(String messageContent) {

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

        Optional<Account> account = accountRepository.findById(1L);
        Set<Account> accountSet = new HashSet<>();
        accountSet.add(account.get());

        Message message = new Message();
        message.setMessageContent(messageContent);
        message.setTags(stringTags);
        message.setRelated(accountSet);

        messageRepository.save(message);
    }

    public Set<String> getTagString(String messageContent) {
        Pattern hashPattern = Pattern.compile("(#[0-9a-zA-Z가-힣ㄱ-ㅎ_.]+)");
        Matcher matcher = hashPattern.matcher(messageContent);
        Set<String> tagStringList = new HashSet<>();

        int i = 0;
        while (matcher.find()) {
            System.out.println("------------------------------------");
            System.out.println("Group " + i + ": " + matcher.group(1));
            tagStringList.add(matcher.group(1));
            i++;

        }

        return tagStringList;
    }
}
