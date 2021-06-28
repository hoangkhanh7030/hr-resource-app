package com.ces.intern.hr.resourcing.demo;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContextAware;

public class ApplicationContext implements ApplicationContextAware {
    public static org.springframework.context.ApplicationContext CONTEXT;

    @Override
    public void setApplicationContext(org.springframework.context.ApplicationContext applicationContext) throws BeansException {
        CONTEXT = applicationContext;


    }

}
