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
        // 메시지에 수정된 콘텐츠 넣어줌
        message.setMessageContent(messageContent);

        // stringTag 비교, 수정
        // 새 콘텐츠에서 태그를 뽑음
        Set<String> tagStringSet = getTagString(messageContent);
        // db 에서 stringTag 를 찾음
        Set<StringTag> newStringTags = stringTagRepository.findAllByTagContentIn(tagStringSet);
        // db 에서 찾은 값들을 지움 tagStringSet = (tagStringSet - newStringTags)
        tagStringSet.removeAll(newStringTags.stream().map(StringTag::getTagContent).collect(Collectors.toSet()));

        // 디비에서 찾지 못한 값을 db에 새로 만듬
        for (String tagString : tagStringSet) {
            StringTag st = new StringTag();
            st.setTagType(0);
            st.setTagContent(tagString);
            newStringTags.add(st);
        }

        stringTagRepository.saveAllAndFlush(newStringTags);

        // 태그가 빠졌다고 지울 필요는 없음. 관계만 재설정.
        message.setTags(newStringTags);

        // 멘션 비교, 수정
        // 새 콘텐츠에서 멘션 뽑음
        Set<Account> mentionSet = getMention(messageContent);
        // 본인 계정도 기본적으로 추가 해줘야함
        mentionSet.add(account);
        // 적용
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
        Pattern hashPattern = Pattern.compile("#([a-z가-힣ㄱ-ㅎ][0-9a-z가-힣ㄱ-ㅎ_.]+)");

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
