package net.miscthings.messagelog.Config;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        HttpSession session = request.getSession();
        System.out.println("authentication.getPrincipal() = " + authentication.getPrincipal());
        session.setAttribute("greeting", authentication.getName() + "님 반갑습니다.");
        response.sendRedirect("/v/message_list/");
    }
}
