package net.miscthings.messagelog.Controller;

import lombok.RequiredArgsConstructor;
import net.miscthings.messagelog.Config.MLogUser;
import net.miscthings.messagelog.DTO.PostMessage;
import net.miscthings.messagelog.Entity.Account;
import net.miscthings.messagelog.Entity.Message;
import net.miscthings.messagelog.Service.MessageService;
import net.miscthings.messagelog.TextEngine.TextEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Controller
@RequestMapping(value = "/v/")
public class MessageListController {
    @Autowired
    private MessageService messageService;

    @Autowired
    private TextEngine textEngine;

    @GetMapping(value = {"/"})
    public String messageList(@AuthenticationPrincipal MLogUser mLogUser, Model model,
                              @RequestParam(required = false) Integer p) throws Exception {
        Account account = mLogUser.getAccount();
        p = Optional.ofNullable(p).orElse(0);
        int pageSize = 20;

        PageRequest pageRequest = PageRequest.of(
                p,
                pageSize,
                Sort.by("lastModifiedAt").descending());

        Page<Message> messages = messageService.listMessage(account, pageRequest);

        for (Message message : messages) {
            message.setMessageContent(textEngine.prettyText(message.getMessageContent()));
        }

        model.addAttribute("messages", messages);
        model.addAttribute("nextPage", "?p=" + (p + 1));
        model.addAttribute("prevPage", "?p=" + (p - 1));
        model.addAttribute("prevPageDisabled", (p - 1) < 0);
        model.addAttribute("nextPageDisabled", messages.getTotalElements() < pageSize);
        return "/page/hello";
    }

    @GetMapping(value = {"/edit"})
    public String editMessage(@AuthenticationPrincipal MLogUser mLogUser, Model model,
                              @RequestParam() Long idx) throws Exception {
        Account account = mLogUser.getAccount();

        Optional<Message> message = messageService.getMessageById(account, idx);

        if (message.isEmpty()) {
            return "redirect:/v/";
        }

        model.addAttribute("message", message.get());
        return "/page/edit";
    }

    @GetMapping(value = {"/quit"})
    public String quitMessage(@AuthenticationPrincipal MLogUser mLogUser, Model model,
                              @RequestParam() Long idx) throws Exception {
        Account account = mLogUser.getAccount();

        Optional<Message> message = messageService.getMessageById(account, idx);

        if (message.isEmpty()) {
            return "redirect:/v/";
        }

        model.addAttribute("message", message.get());
        return "/page/quit";
    }

    @PostMapping(value = {"/submit_message"})
    public String submitMessage(@AuthenticationPrincipal MLogUser mLogUser, Model model,
                                @RequestParam String messageContent) throws Exception {
        Account account = mLogUser.getAccount();
        messageService.publishMessage(account, messageContent);

        return "redirect:/v/";
    }

    @PostMapping(value = {"/edit_message"})
    public String editMessage(@AuthenticationPrincipal MLogUser mLogUser, Model model,
                              @RequestParam String messageContent,
                              @RequestParam Long idx
    ) throws Exception {
        Account account = mLogUser.getAccount();
        boolean editSuccess = messageService.editMessage(account, idx, messageContent);
        return editSuccess ? "redirect:/v/" : "redirect:/v/edit?idx=" + idx;
    }

    @PostMapping(value = {"/quit_message"})
    public String postQuitMessage(@AuthenticationPrincipal MLogUser mLogUser, Model model,
                                  @RequestParam Long idx,
                                  @RequestParam String submit
    ) throws Exception {
        Account account = mLogUser.getAccount();
        if(submit.equals("NO")){
            return "redirect:/v/";
        }
        boolean editSuccess = messageService.quitMessage(account, idx);
        return editSuccess ? "redirect:/v/" : "redirect:/v/quit?idx=" + idx;
    }


}
