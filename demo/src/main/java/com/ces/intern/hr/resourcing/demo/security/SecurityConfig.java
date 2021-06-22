package com.ces.intern.hr.resourcing.demo.security;

import com.ces.intern.hr.resourcing.demo.security.config.SecurityContact;
import com.ces.intern.hr.resourcing.demo.security.filter.AuthorizationFilter;
import com.ces.intern.hr.resourcing.demo.security.jwtAccount.CustomAccountService;
import com.ces.intern.hr.resourcing.demo.security.oauth.AccoutService;
import com.ces.intern.hr.resourcing.demo.security.oauth.CustomOAuth2Account;
import com.ces.intern.hr.resourcing.demo.security.oauth.CustomOAuth2AccountService;
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


@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private CustomOAuth2AccountService customOAuth2AccountService;
    @Autowired
    private AccoutService accoutService;
    @Autowired
    private CustomAccountService accService;

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
                .antMatchers("/login","oauth2/login/**").permitAll()
                .antMatchers(HttpMethod.POST,SecurityContact.SIGN_UP_URL).permitAll()
                .antMatchers(HttpMethod.POST,SecurityContact.SIGN_IN_URL).permitAll()
                .anyRequest().authenticated()
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

                    }
                });
        http.addFilter(new AuthorizationFilter(authenticationManager()));

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
