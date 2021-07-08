package com.ces.intern.hr.resourcing.demo.security.config;

public class SecurityContact {
    public static final long EXPIRATION_TIME = 864000000; // 10 days

    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_IN_URL = "/api/v1/login";
    public static final String SIGN_UP_URL = "/api/v1/signup";
    public static final String GOOGLE_URL ="/api/v1/auth/google";
    public static final String TOKEN_SECRET = "hoangkhanhadsfasdfadsfadsfaasdfasdfadsfasdfasdfa";
    public static final String HEADER_USERID = "AccountId";
}
