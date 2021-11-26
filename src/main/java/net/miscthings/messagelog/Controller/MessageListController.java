package net.miscthings.messagelog.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

@RequiredArgsConstructor
@Controller
@RequestMapping(value = "/messages")
public class MessageListController {

    @GetMapping(value = {"/"})
    public String Cart(HttpServletRequest request, Model model) throws Exception {

        return "page/hello";
    }
}
