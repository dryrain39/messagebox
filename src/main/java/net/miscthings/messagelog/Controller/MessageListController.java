package net.miscthings.messagelog.Controller;

import lombok.RequiredArgsConstructor;
import net.miscthings.messagelog.Config.MLogUser;
import net.miscthings.messagelog.DTO.PostMessage;
import net.miscthings.messagelog.Entity.Account;
import net.miscthings.messagelog.Entity.Message;
import net.miscthings.messagelog.Service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Controller
@RequestMapping(value = "/v/")
public class MessageListController {
    @Autowired
    private MessageService messageService;

    @GetMapping(value = {"/message_list"})
    public String messageList(@AuthenticationPrincipal MLogUser mLogUser, Model model) throws Exception {
        Account account = mLogUser.getAccount();
        System.out.println("account.getUserId() = " + account.getUserId());
        Page<Message> messages = messageService.listMessage(account, PageRequest.of(0, 20, Sort.by("messageId").descending()));
        model.addAttribute("messages", messages);
        return "/page/hello";
    }

    @PostMapping(value = {"/submit_message"})
    public String submitMessage(HttpServletRequest request, Model model,
                                @RequestParam String messageContent) throws Exception {

        messageService.publishMessage(messageContent);

        return "redirect:/v/message_list";
    }


}
