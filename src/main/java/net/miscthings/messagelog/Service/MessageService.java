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
import java.util.stream.Collectors;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private StringTagRepository stringTagRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MessageSaveService messageSaveService;

    public Page<Message> listMessage(Account account, PageRequest pageRequest) {
        final Page<Message> all = messageRepository.findAllByRelated(account, pageRequest);
        System.out.println("all = " + all);
        return all;
    }

    public Optional<Message> getMessageById(Account account, Long idx) {
        return messageRepository.findByMessageIdAndRelated(idx, account);
    }

    public boolean quitMessage(Account account, Long idx) {
        Optional<Message> optionalMessage = messageRepository.findByMessageIdAndRelated(idx, account);
        if (optionalMessage.isEmpty()) {
            return false;
        }

        Message message = optionalMessage.get();
        Optional<Account> optionalCurrentAccount = accountRepository.findById(account.getUserId());
        if(optionalCurrentAccount.isEmpty()) return false;

        message.getRelated().remove(optionalCurrentAccount.get());

        if(message.getRelated().size() == 0){
            deleteMessage(message);
        }else{
            messageSaveService.saveAndFlush(message);
        }

        return true;
    }

    public boolean editMessage(Account account, Long idx, String messageContent) {
        messageContent = messageContent.toLowerCase(Locale.ROOT);
        messageContent = messageContent.strip();

        if (messageContent.length() == 0) {
            return false;
        }

        Optional<Message> optionalMessage = messageRepository.findByMessageIdAndRelated(idx, account);
        if (optionalMessage.isEmpty()) {
            return false;
        }

        Message message = optionalMessage.get();
        // ???????????? ????????? ????????? ?????????
        message.setMessageContent(messageContent);

        // stringTag ??????, ??????
        // ??? ??????????????? ????????? ??????
        Set<String> tagStringSet = getTagString(messageContent);
        // db ?????? stringTag ??? ??????
        Set<StringTag> newStringTags = stringTagRepository.findAllByTagContentIn(tagStringSet);
        // db ?????? ?????? ????????? ?????? tagStringSet = (tagStringSet - newStringTags)
        tagStringSet.removeAll(newStringTags.stream().map(StringTag::getTagContent).collect(Collectors.toSet()));

        // ???????????? ?????? ?????? ?????? db??? ?????? ??????
        for (String tagString : tagStringSet) {
            StringTag st = new StringTag();
            st.setTagType(0);
            st.setTagContent(tagString);
            newStringTags.add(st);
        }

        stringTagRepository.saveAllAndFlush(newStringTags);

        // ????????? ???????????? ?????? ????????? ??????. ????????? ?????????.
        message.setTags(newStringTags);

        // ?????? ??????, ??????
        // ??? ??????????????? ?????? ??????
        Set<Account> mentionSet = getMention(messageContent);
        // ?????? ????????? ??????????????? ?????? ????????????
        mentionSet.add(account);
        // ??????
        message.setRelated(mentionSet);

        messageSaveService.saveAndFlush(message);

        return true;
    }

    public void deleteMessage(Message message){
        Set<StringTag> stringTags = new HashSet<>();
        Set<Account> accountSet = new HashSet<>();

        message.setTags(stringTags);
        message.setRelated(accountSet);
        messageSaveService.saveAndFlush(message);
        messageRepository.delete(message);
        messageRepository.flush();
    }

    public void publishMessage(Account account, String messageContent) {
        messageContent = messageContent.toLowerCase(Locale.ROOT);
        messageContent = messageContent.strip();

        if (messageContent.length() == 0) {
            return;
        }

        Set<String> tagStringSet = getTagString(messageContent);
        System.out.println("getTagString() = " + getTagString(messageContent));

        Set<StringTag> stringTags = new HashSet<>();

        for (String tagString : tagStringSet) {
            Set<StringTag> foundTags = stringTagRepository.findAllByTagContent(tagString);
            stringTags.addAll(foundTags);
            if (foundTags.size() == 0) {
                StringTag st = new StringTag();
                st.setTagType(0);
                st.setTagContent(tagString);
                stringTags.add(st);
            }
        }

        if (stringTags.size() > 0) {
            stringTagRepository.saveAllAndFlush(stringTags);
        }

        Set<Account> accountSet = new HashSet<>();
        accountSet.add(account);
        accountSet.addAll(getMention(messageContent));


        Message message = new Message();
        message.setMessageContent(messageContent);
        message.setTags(stringTags);
        message.setRelated(accountSet);

        messageSaveService.saveAndFlush(message);
    }

    public Set<String> getTagString(String messageContent) {
        Pattern hashPattern = Pattern.compile("#([a-z???-??????-???][0-9a-z???-??????-???_.]+)");

        return regexTextSearch(messageContent, hashPattern);
    }

    public Set<Account> getMention(String messageContent) {
        Pattern hashPattern = Pattern.compile("@([a-z][a-z0-9_]+)");

        Set<String> searchSet = regexTextSearch(messageContent, hashPattern);

        Set<Account> allByNameIn = accountRepository.findAllByNameIn(searchSet);

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
