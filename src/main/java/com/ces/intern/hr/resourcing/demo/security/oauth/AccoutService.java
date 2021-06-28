package com.ces.intern.hr.resourcing.demo.security.oauth;

import com.ces.intern.hr.resourcing.demo.entity.AccountEntity;
import com.ces.intern.hr.resourcing.demo.utils.AuthenticationProvider;
import com.ces.intern.hr.resourcing.demo.repository.AccoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AccoutService {
    @Autowired
    private AccoutRepository accoutRepository;
    public void processOAuthPostLogin(String email,String name,String avatar){
        AccountEntity existAccout =accoutRepository.getAccountEntitiesByEmail(email);
        if(existAccout==null){
            AccountEntity accountEntity = new AccountEntity();
            accountEntity.setEmail(email);
            accountEntity.setAuthenticationProvider(AuthenticationProvider.GOOGLE);
            accountEntity.setFullname(name);
            accountEntity.setAvatar(avatar);
            Date date = new Date();
            accountEntity.setCreatedDate(date);
            accountEntity.setModifiedDate(date);
            accountEntity=accoutRepository.save(accountEntity);
            accountEntity.setModifiedBy(accountEntity.getId());
            accountEntity.setCreatedBy(accountEntity.getId());
            accoutRepository.save(accountEntity);

        }
    }
}
