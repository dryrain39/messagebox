package net.miscthings.messagelog.Controller;

import lombok.RequiredArgsConstructor;
import net.miscthings.messagelog.Config.MLogUser;
import net.miscthings.messagelog.DTO.SearchTag;
import net.miscthings.messagelog.Entity.StringTag;
import net.miscthings.messagelog.Service.StringTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/tag/")
public class TagController {
    @Autowired
    private StringTagService stringTagService;

    @GetMapping(value = {"/search"})
    public SearchTag messageList(@AuthenticationPrincipal MLogUser mLogUser, Model model,
                                 @RequestParam String q) throws Exception {
        Set<StringTag> stringTags = stringTagService.searchTag(q);
        return SearchTag.builder().tags(
                stringTags.stream().map(StringTag::getTagContent).collect(Collectors.toSet())
        ).build();
    }


}
