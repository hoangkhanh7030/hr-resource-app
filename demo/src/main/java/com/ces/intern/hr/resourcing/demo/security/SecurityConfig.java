//package com.ces.intern.hr.resourcing.demo.security;
//
//import com.ces.intern.hr.resourcing.demo.security.oauth.AccoutService;
//import com.ces.intern.hr.resourcing.demo.security.oauth.CustomOAuth2Account;
//import com.ces.intern.hr.resourcing.demo.security.oauth.CustomOAuth2AccountService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//@Configuration
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//    @Autowired
//    private CustomOAuth2AccountService customOAuth2AccountService;
//    @Autowired
//    private AccoutService accoutService;
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//                .antMatchers("/","/login","/oauth/**").permitAll()
//                .anyRequest().authenticated()
//                .and()
//                .formLogin().loginPage("/login").permitAll()
//                .and()
//                .oauth2Login()
//                    .loginPage("/login")
//                    .userInfoEndpoint()
//                        .userService(customOAuth2AccountService)
//                .and()
//                .successHandler(new AuthenticationSuccessHandler() {
//                    @Override
//                    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
//                        CustomOAuth2Account oAuth2Account =(CustomOAuth2Account) authentication.getPrincipal();
//                        accoutService.processOAuthPostLogin(oAuth2Account.getEmail());
//                        httpServletResponse.sendRedirect("/index");
//                    }
//                });
//    }
//
//}
