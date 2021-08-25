package com.ces.intern.hr.resourcing.demo.sevice.impl;

import com.ces.intern.hr.resourcing.demo.entity.AccountEntity;
import com.ces.intern.hr.resourcing.demo.entity.AccountWorkspaceRoleEntity;
import com.ces.intern.hr.resourcing.demo.http.request.ReInviteRequest;
import com.ces.intern.hr.resourcing.demo.http.response.user.ManageUserResponse;
import com.ces.intern.hr.resourcing.demo.repository.AccoutRepository;
import com.ces.intern.hr.resourcing.demo.repository.AccoutWorkspaceRoleRepository;
import com.ces.intern.hr.resourcing.demo.sevice.ManageUserService;
import com.ces.intern.hr.resourcing.demo.utils.AuthenticationProvider;
import com.ces.intern.hr.resourcing.demo.utils.SortPara;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ManageUserServiceImpl implements ManageUserService {
    private static final String VIEWER = "VIEWER";
    private static final String TITLE = "Invited Workspace";
    private final JavaMailSender sender;
    private final AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository;
    private final ModelMapper modelMapper;
    private final AccoutRepository accoutRepository;

    @Autowired
    public ManageUserServiceImpl(AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository,
                                 ModelMapper modelMapper,
                                 AccoutRepository accoutRepository,
                                 JavaMailSender sender) {
        this.accoutWorkspaceRoleRepository = accoutWorkspaceRoleRepository;
        this.modelMapper = modelMapper;
        this.accoutRepository = accoutRepository;
        this.sender = sender;
    }

    @Override
    public List<ManageUserResponse> getAll(Integer idWorkspace, Integer page, Integer size, String searchName,
                                           String sortName, String type) {
        Pageable pageable;
        if (type.equals(SortPara.ASC.getName())) {
            pageable = PageRequest.of(page, size, Sort.by(sortName).ascending());
        } else {
            pageable = PageRequest.of(page, size, Sort.by(sortName).descending());
        }

        Page<AccountEntity> accountPage = accoutRepository.findAllBysearchName(idWorkspace, searchName, pageable);
        List<AccountEntity> accounts = accountPage.getContent();
        List<ManageUserResponse> manageUserResponses = new ArrayList<>();
        for (AccountEntity account : accounts) {
            ManageUserResponse manageUserResponse = new ManageUserResponse();
            manageUserResponse.setId(account.getId());
            manageUserResponse.setFullName(account.getFullname());
            manageUserResponse.setEmail(account.getEmail());
            manageUserResponse.setCreatedDate(account.getCreatedDate());
            manageUserResponse.setRole(VIEWER);
            manageUserResponse.setStatus(account.getAuthenticationProvider().name());
            manageUserResponses.add(manageUserResponse);
        }

        return manageUserResponses;
    }



    @Override
    public void delete(Integer idAccount, Integer idWorkspace) {
        AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = accoutWorkspaceRoleRepository.findByIdAndId(idWorkspace, idAccount).orElse(null);
        assert accountWorkspaceRoleEntity != null;
        accoutWorkspaceRoleRepository.delete(accountWorkspaceRoleEntity);
        AccountEntity accountEntity = accoutRepository.findById(idAccount).orElse(null);
        assert accountEntity != null;
        accoutRepository.delete(accountEntity);
    }

    @Override
    public void sendEmail(ReInviteRequest reInviteRequest) throws MessagingException, IOException {
        AccountEntity accountEntity = accoutRepository.findById(reInviteRequest.getId()).orElse(null);
        assert accountEntity != null;
        if (accountEntity.getAuthenticationProvider().getName().equals(AuthenticationProvider.PENDING.getName())) {
            accountEntity.setEmail(reInviteRequest.getEmail());
            accoutRepository.save(accountEntity);
        }
        MimeMessage msg = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);
        helper.setTo(reInviteRequest.getEmail());
        helper.setSubject(TITLE);
        BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/file/Invite.txt"))));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        String ls = System.getProperty("line.separator");
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        reader.close();
        String content = stringBuilder.toString();
        helper.setText(content + reInviteRequest.getUrl(), true);


        sender.send(msg);

    }

    @Override
    public void isActive(Integer idAccount) {
        AccountEntity accountEntity=accoutRepository.findById(idAccount).orElse(null);
        assert accountEntity != null;
        if (accountEntity.getAuthenticationProvider().getName().equals(AuthenticationProvider.GOOGLE.getName())){
            accountEntity.setAuthenticationProvider(AuthenticationProvider.INACTIVE);
        }else if (accountEntity.getAuthenticationProvider().getName().equals(AuthenticationProvider.INACTIVE.getName())){
            accountEntity.setAuthenticationProvider(AuthenticationProvider.GOOGLE);
        }else {
            accountEntity.setAuthenticationProvider(accountEntity.getAuthenticationProvider());
        }
        accoutRepository.save(accountEntity);
    }


}
