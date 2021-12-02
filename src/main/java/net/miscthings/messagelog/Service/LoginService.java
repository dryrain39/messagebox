package net.miscthings.messagelog.Service;

import net.miscthings.messagelog.Config.MLogUser;
import net.miscthings.messagelog.Entity.Account;
import net.miscthings.messagelog.Repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoginService implements UserDetailsService {
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findAccountByName(username).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        return new MLogUser(account, account.getName(), account.getPassword(), List.of(new SimpleGrantedAuthority(account.getRole())));
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
