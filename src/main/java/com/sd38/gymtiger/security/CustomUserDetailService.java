package com.sd38.gymtiger.security;

import com.sd38.gymtiger.repository.AccountRepository;
import com.sd38.gymtiger.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.findFirstByEmail(email);
        if (account != null) {
            return new CustomUser(account);
        }
        throw new UsernameNotFoundException("User not found");
    }
}
