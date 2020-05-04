package com.intv.tender.tenderapi;

import com.intv.tender.tenderapi.db.repository.TenderRepository;
import com.intv.tender.tenderapi.db.repository.UserRepository;
import com.intv.tender.tenderapi.services.MyUserDetailsService;
import com.intv.tender.tenderapi.services.TenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.sql.SQLException;

import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class MyUserDetailsServiceTests {

    UserRepository userRepository;
    MyUserDetailsService myUserDetailsService;

    @BeforeEach
    public void setup() {

        userRepository = mock(UserRepository.class);
        myUserDetailsService = spy(new MyUserDetailsService(userRepository));
    }

    @Test
    public void loadUserByUsername_validParams() {

        try {

            when(userRepository.loadUserByUsername(anyString())).thenReturn(new User("userId", "{noop}" + "pass", of(new SimpleGrantedAuthority("someRole"))));

            myUserDetailsService.loadUserByUsername("someUser");

        } catch (SQLException e) {
            fail();
        }
    }
    @Test
    public void loadUserByUsername_repoThrows() {

        try {

            when(userRepository.loadUserByUsername(anyString())).thenThrow(new SQLException());

            myUserDetailsService.loadUserByUsername("someUser");

        } catch (UsernameNotFoundException e) {
            return;
        } catch (SQLException e) {
            fail();
        }

        fail();
    }
}
