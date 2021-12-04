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

import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class StringTagService {
    @Autowired
    private StringTagRepository stringTagRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MessageSaveService messageSaveService;

    public Set<StringTag> searchTag(String query) {
        return stringTagRepository.findStringTagsByTagContentContains(query);
    }

}
