//package com.ces.intern.hr.resourcing.demo.security.oauth;
//
//import com.ces.intern.hr.resourcing.demo.entity.AccountEntity;
//import com.ces.intern.hr.resourcing.demo.entity.AuthenticationProvider;
//import com.ces.intern.hr.resourcing.demo.repository.AccoutRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class AccoutService {
//    @Autowired
//    private AccoutRepository accoutRepository;
//    public void processOAuthPostLogin(String email){
//        AccountEntity existAccout =accoutRepository.getAccountEntitiesByEmail(email);
//        if(existAccout==null){
//            AccountEntity accountEntity = new AccountEntity();
//            accountEntity.setEmail(email);
//            accountEntity.setAuthenticationProvider(AuthenticationProvider.GOOGLE);
//            accoutRepository.save(accountEntity);
//        }
//    }
//}
