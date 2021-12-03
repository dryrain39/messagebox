package net.miscthings.messagelog.ExternalService;

import net.miscthings.messagelog.Entity.Account;
import net.miscthings.messagelog.Entity.Message;
import net.miscthings.messagelog.Repository.AccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.xml.transform.OutputKeys;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

@Service
@EnableAsync
public class TgSend {

    @Value("${tgSend.botId:null}")
    private String botId;
    @Value("${tgSend.chatId:0}")
    private String chatId;

    private Long tgAccountId = null;

    public TgSend(AccountRepository accountRepository) {
        Optional<Account> optionalTgAccount = accountRepository.findAccountByName("tg");
        optionalTgAccount.ifPresent(account -> this.tgAccountId = account.getUserId());
    }

    @Async
    public void send(Message message) {
        if (tgAccountId == null) {
            return;
        }

        if (message.getRelated().stream().anyMatch(account -> Objects.equals(account.getUserId(), this.tgAccountId))) {
            try {
                String messageText = URLEncoder.encode(message.getMessageContent(), StandardCharsets.UTF_8);
                URL url = new URL("https://api.telegram.org/" + botId + "/sendMessage?chat_id=" + chatId + "&text=" + messageText);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
