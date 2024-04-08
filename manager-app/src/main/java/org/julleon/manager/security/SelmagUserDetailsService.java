package org.julleon.manager.security;

import lombok.RequiredArgsConstructor;
import org.julleon.manager.entity.Authority;
import org.julleon.manager.repository.SelmagUserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SelmagUserDetailsService implements UserDetailsService {

    private final SelmagUserRepository selmagUserRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return selmagUserRepository
                .findByUsername(username)
                .map(selmagUser ->
                        User.builder()
                                .username(selmagUser.getUsername())
                                .password(selmagUser.getPassword())
                                .authorities(
                                        selmagUser.getAuthorities().stream()
                                                .map(Authority::getAuthority)
                                                .map(SimpleGrantedAuthority::new)
                                                .toList())
                                .build())
                .orElseThrow(
                        () -> new UsernameNotFoundException("User with username:%s not found".formatted(username)));
    }
}
