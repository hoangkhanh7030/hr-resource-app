package com.ces.intern.hr.resourcing.demo.security;

import com.ces.intern.hr.resourcing.demo.repository.AccoutRepository;
import com.ces.intern.hr.resourcing.demo.security.config.SecurityContact;
import com.ces.intern.hr.resourcing.demo.security.filter.AuthorizationFilter;
import com.ces.intern.hr.resourcing.demo.security.jwt.JwtTokenProvider;
import com.ces.intern.hr.resourcing.demo.security.jwtAccount.CustomAccountService;
import com.ces.intern.hr.resourcing.demo.security.oauth.AccoutService;
import com.ces.intern.hr.resourcing.demo.security.oauth.CustomOAuth2AccountService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@EnableWebSecurity
@Configuration

public class SecurityConfigApp extends WebSecurityConfigurerAdapter {

    private final CustomOAuth2AccountService customOAuth2AccountService;
    private final AccoutService accoutService;
    private final CustomAccountService accService;
    private final AccoutRepository accoutRepository;
    private final ModelMapper modelMapper;
    private final JwtTokenProvider tokenProvider;

    @Autowired
    public SecurityConfigApp(CustomOAuth2AccountService customOAuth2AccountService,
                             AccoutService accoutService,
                             CustomAccountService accService,
                             AccoutRepository accoutRepository,
                             ModelMapper modelMapper,
                             JwtTokenProvider tokenProvider) {
        this.customOAuth2AccountService = customOAuth2AccountService;
        this.accoutService = accoutService;
        this.accService = accService;
        this.accoutRepository = accoutRepository;
        this.modelMapper = modelMapper;
        this.tokenProvider = tokenProvider;
    }


    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(accService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, SecurityContact.SIGN_UP_URL).permitAll()
                .antMatchers(HttpMethod.POST, SecurityContact.SIGN_IN_URL).permitAll()
                .antMatchers(HttpMethod.POST, SecurityContact.GOOGLE_URL).permitAll()
                .anyRequest().authenticated();

        http.addFilter(new AuthorizationFilter(authenticationManager()));

    }


}
