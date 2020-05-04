package com.intv.tender.tenderapi.filter;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.intv.tender.tenderapi.util.*;

import static java.util.List.of;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private JwtUtil jwtUtil;

    @Autowired
    public JwtRequestFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String autorizationHeader = request.getHeader("Authorization");
        if(autorizationHeader != null && autorizationHeader.startsWith("Bearer "))
        {
            try
            {
                String jwtToken = autorizationHeader.substring("Bearer ".length());

                Claims claims = null;

                claims = jwtUtil.extractAllClaims(jwtToken);

                String username = claims.getSubject();
                String authorities = (String) claims.get("authorities");

                List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
                if(authorities != null && authorities.length() > 0)
                {
                    for(String a : authorities.split(","))
                        grantedAuthorities.add(new SimpleGrantedAuthority(a));
                }

                User user = new User(username, "", grantedAuthorities);

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user, null, grantedAuthorities);

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

            }
            catch (JwtUtil.EJwtTokenParsingError e)
            {
                e.printStackTrace();
            }
        }

        filterChain.doFilter(request, response);
    }
}
