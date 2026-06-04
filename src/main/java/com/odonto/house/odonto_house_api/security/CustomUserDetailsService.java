package com.odonto.house.odonto_house_api.security;

import com.odonto.house.odonto_house_api.model.User;
import com.odonto.house.odonto_house_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Load user with roles eagerly fetched to avoid lazy loading issues
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
        
        // Force initialization of roles - access the collection to trigger loading
        user.getRoles().size();
        
        return new CustomUserDetails(user);
    }
}