package com.ces.intern.hr.resourcing.demo.security;

import com.ces.intern.hr.resourcing.demo.dto.AccountDTO;
import com.ces.intern.hr.resourcing.demo.entity.AccountEntity;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.http.response.LoginResponse;
import com.ces.intern.hr.resourcing.demo.http.response.MessageResponse;
import com.ces.intern.hr.resourcing.demo.repository.AccoutRepository;
import com.ces.intern.hr.resourcing.demo.security.config.SecurityContact;
import com.ces.intern.hr.resourcing.demo.security.filter.AuthorizationFilter;
import com.ces.intern.hr.resourcing.demo.security.jwt.JwtTokenProvider;
import com.ces.intern.hr.resourcing.demo.security.jwtAccount.CustomAccountService;
import com.ces.intern.hr.resourcing.demo.security.oauth.AccoutService;
import com.ces.intern.hr.resourcing.demo.security.oauth.CustomOAuth2Account;
import com.ces.intern.hr.resourcing.demo.security.oauth.CustomOAuth2AccountService;
import com.ces.intern.hr.resourcing.demo.utils.ExceptionMessage;
import com.ces.intern.hr.resourcing.demo.utils.Status;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;


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
                .antMatchers("oauth2/login/**","/login/google").permitAll()
                .antMatchers(HttpMethod.POST, SecurityContact.SIGN_UP_URL).permitAll()
                .antMatchers(HttpMethod.POST, SecurityContact.SIGN_IN_URL).permitAll()
                .anyRequest().authenticated()
                .and()
                .oauth2Login()
                .userInfoEndpoint()
                .userService(customOAuth2AccountService)
                .and()
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                        CustomOAuth2Account oAuth2Account = (CustomOAuth2Account) authentication.getPrincipal();
                        accoutService.processOAuthPostLogin(oAuth2Account.getEmail(), oAuth2Account.getName(), oAuth2Account.getAvatar());
                        AccountEntity accountEntity = accoutRepository.findByEmail(oAuth2Account.getEmail())
                                .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
                        AccountDTO accountDTO = modelMapper.map(accountEntity, AccountDTO.class);
                        String jwt = tokenProvider.generateToken(accountDTO);
                        String json = new Gson().toJson(new LoginResponse(jwt,accountDTO,Status.SUCCESS.getCode()));
                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        response.getWriter().write(json);








                    }
                });
        http.addFilter(new AuthorizationFilter(authenticationManager()));

    }



}
