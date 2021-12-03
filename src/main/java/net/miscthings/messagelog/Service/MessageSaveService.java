package net.miscthings.messagelog.Service;

import net.miscthings.messagelog.Entity.Message;
import net.miscthings.messagelog.ExternalService.SendExternalService;
import net.miscthings.messagelog.Repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageSaveService {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private SendExternalService sendExternalService;

    public Message saveAndFlush(Message message){
        sendExternalService.send(message);
        return messageRepository.saveAndFlush(message);
    }
}
