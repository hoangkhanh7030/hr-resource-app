package com.ces.intern.hr.resourcing.demo.security;

import com.ces.intern.hr.resourcing.demo.dto.AccountDTO;
import com.ces.intern.hr.resourcing.demo.entity.AccountEntity;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.repository.AccoutRepository;
import com.ces.intern.hr.resourcing.demo.security.config.SecurityContact;
import com.ces.intern.hr.resourcing.demo.security.filter.AuthorizationFilter;
import com.ces.intern.hr.resourcing.demo.security.jwt.JwtTokenProvider;
import com.ces.intern.hr.resourcing.demo.security.jwtAccount.CustomAccountService;
import com.ces.intern.hr.resourcing.demo.security.oauth.AccoutService;
import com.ces.intern.hr.resourcing.demo.security.oauth.CustomOAuth2Account;
import com.ces.intern.hr.resourcing.demo.security.oauth.CustomOAuth2AccountService;
import com.ces.intern.hr.resourcing.demo.utils.ExceptionMessage;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
                             JwtTokenProvider tokenProvider){
        this.customOAuth2AccountService=customOAuth2AccountService;
        this.accoutService=accoutService;
        this.accService=accService;
        this.accoutRepository=accoutRepository;
        this.modelMapper=modelMapper;
        this.tokenProvider = tokenProvider;
    }



    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return super.authenticationManagerBean();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Override
    protected void configure(AuthenticationManagerBuilder auth)throws Exception{
        auth.userDetailsService(accService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .cors()
                .and()
                .authorizeRequests()
                .antMatchers("/login","/signup","oauth2/login/**").permitAll()
                .antMatchers(HttpMethod.POST,SecurityContact.SIGN_UP_URL).permitAll()
                .antMatchers(HttpMethod.POST,SecurityContact.SIGN_IN_URL).permitAll()
                .anyRequest().authenticated()
                .and().addFilter(new AuthorizationFilter(authenticationManager()))
                .cors()
                .and()
                .oauth2Login()
                    .userInfoEndpoint()
                        .userService(customOAuth2AccountService)
                .and()
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
                        CustomOAuth2Account oAuth2Account =(CustomOAuth2Account) authentication.getPrincipal();
                        accoutService.processOAuthPostLogin(oAuth2Account.getEmail(),oAuth2Account.getName(),oAuth2Account.getAvatar());
                        AccountEntity accountEntity = accoutRepository.findByEmail(oAuth2Account.getEmail())
                                .orElseThrow(()->new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
                        AccountDTO accountDTO=modelMapper.map(accountEntity,AccountDTO.class);
                        List<String> jwt =tokenProvider.generateToken(accountDTO);
                        httpServletResponse.addHeader(SecurityContact.HEADER_STRING,SecurityContact.TOKEN_PREFIX+jwt.get(0));
                        httpServletResponse.addHeader(SecurityContact.HEADER_USERID,jwt.get(1));
                    }
                });


    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        final CorsConfiguration configuration  = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList(SecurityContact.HEADER_STRING,SecurityContact.HEADER_USERID));
        final UrlBasedCorsConfigurationSource sourse = new UrlBasedCorsConfigurationSource();
        sourse.registerCorsConfiguration("/**", configuration);
        return sourse;

    }

}
