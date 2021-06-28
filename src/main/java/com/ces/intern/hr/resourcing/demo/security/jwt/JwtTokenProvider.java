package com.ces.intern.hr.resourcing.demo.security.jwt;


import com.ces.intern.hr.resourcing.demo.dto.AccountDTO;
import com.ces.intern.hr.resourcing.demo.security.config.SecurityContact;


import io.jsonwebtoken.*;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;



import java.util.Date;


@Component
@Slf4j
public class JwtTokenProvider {

    public String generateToken(AccountDTO accountDTO) {
        return Jwts.builder()
                .claim("email", accountDTO.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + SecurityContact.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SecurityContact.TOKEN_SECRET)
                .compact();
    }


}
