package com.ces.intern.hr.resourcing.demo.security.jwtAccount;

import com.ces.intern.hr.resourcing.demo.entity.AccountEntity;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.repository.AccoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomAccountService implements UserDetailsService {
    @Autowired
    private AccoutRepository accoutRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AccountEntity accountEntity= accoutRepository.findByEmail(email).orElse(null);
        if (accountEntity==null){
            throw new UsernameNotFoundException(email);
        }
        return new CustomAccountDetails(accountEntity);
    }

    public UserDetails loadUserById(Integer id) throws NotFoundException{
        AccountEntity accountEntity =accoutRepository.findById(id).orElseThrow(
                ()->new UsernameNotFoundException("User not found with id:" + id)
        );
        return new CustomAccountDetails(accountEntity);

    }
}
