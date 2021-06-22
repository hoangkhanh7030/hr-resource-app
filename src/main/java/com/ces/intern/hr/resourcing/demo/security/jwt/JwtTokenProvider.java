package com.ces.intern.hr.resourcing.demo.security.jwt;

import com.ces.intern.hr.resourcing.demo.ApplicationContext;
import com.ces.intern.hr.resourcing.demo.dto.AccountDTO;
import com.ces.intern.hr.resourcing.demo.security.config.SecurityContact;

import com.ces.intern.hr.resourcing.demo.sevice.AccountService;
import io.jsonwebtoken.*;

import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class JwtTokenProvider {

    public List<String> generateToken(AccountDTO accountDTO){
        String token = Jwts.builder()
                .claim("email",accountDTO.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+SecurityContact.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256,SecurityContact.TOKEN_SECRET)
                .compact();
        return Arrays.asList(token,accountDTO.getId()+"");
    }



}
