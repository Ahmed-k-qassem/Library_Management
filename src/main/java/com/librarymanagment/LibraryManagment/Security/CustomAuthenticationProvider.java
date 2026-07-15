package com.librarymanagment.LibraryManagment.Security;

import com.librarymanagment.LibraryManagment.Entities.User;
import com.librarymanagment.LibraryManagment.Services.JpaUserDetailsService;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CustomAuthenticationProvider implements AuthenticationProvider {

    private JpaUserDetailsService userDetailsService;
    private PasswordEncoder passwordEncoder;



    @Override
    public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserSecurity user = userDetailsService.loadUserByUsername(authentication.getName());
        String rawPassword = authentication.getCredentials().toString();
        return checkPassword(rawPassword,user,passwordEncoder);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }


    private Authentication checkPassword(String rawPassword, UserSecurity user, PasswordEncoder passwordEncoder) {
        if(passwordEncoder.matches(rawPassword, user.getPassword())){
            return new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities());
        } else{
            throw new BadCredentialsException("Invalid username or password");
        }
    }
}
