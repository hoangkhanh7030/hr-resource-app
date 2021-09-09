package com.ces.intern.hr.resourcing.demo.sevice.impl;

import com.ces.intern.hr.resourcing.demo.entity.AccountEntity;
import com.ces.intern.hr.resourcing.demo.entity.AccountWorkspaceRoleEntity;
import com.ces.intern.hr.resourcing.demo.entity.WorkspaceEntity;
import com.ces.intern.hr.resourcing.demo.http.request.ReInviteRequest;
import com.ces.intern.hr.resourcing.demo.http.response.message.MessageResponse;
import com.ces.intern.hr.resourcing.demo.http.response.user.ManageUserResponse;
import com.ces.intern.hr.resourcing.demo.repository.AccoutRepository;
import com.ces.intern.hr.resourcing.demo.repository.AccoutWorkspaceRoleRepository;
import com.ces.intern.hr.resourcing.demo.repository.WorkspaceRepository;
import com.ces.intern.hr.resourcing.demo.sevice.ManageUserService;
import com.ces.intern.hr.resourcing.demo.utils.*;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import static java.util.concurrent.TimeUnit.SECONDS;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Service
public class ManageUserServiceImpl implements ManageUserService {
    private static final int EXPIRATION_DATE = 60;
    private static final String FILE_INVITE = "/file/Invite.txt";
    private static final String LINE = "line.separator";
    private static final String VIEWER = "VIEWER";
    private static final String TITLE = "Invited Workspace";
    private static final String MESSAGE_EXPIRATION = "<br/><br/><i>The invitation will be expired after 2 days and you cannot join with us.</i><br/><br/>\n" +
            "\n" +
            "<i>Thanks from Team Juggle Fish</i>";
    private static final String CREATE_DATE="create_date";

    private final JavaMailSender sender;
    private final AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository;
    private final ModelMapper modelMapper;
    private final AccoutRepository accoutRepository;
    private final WorkspaceRepository workspaceRepository;

    @Autowired
    public ManageUserServiceImpl(AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository,
                                 ModelMapper modelMapper,
                                 AccoutRepository accoutRepository,
                                 JavaMailSender sender,
                                 WorkspaceRepository workspaceRepository) {
        this.accoutWorkspaceRoleRepository = accoutWorkspaceRoleRepository;
        this.modelMapper = modelMapper;
        this.accoutRepository = accoutRepository;
        this.sender = sender;
        this.workspaceRepository=workspaceRepository;
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
    }

    @Override
    public void reSendEmail(ReInviteRequest reInviteRequest, Integer idWorkspace) throws Exception {
        AccountEntity accountEntity = accoutRepository.findById(reInviteRequest.getId()).orElse(null);
        WorkspaceEntity workspaceEntity=workspaceRepository.findById(idWorkspace).orElse(null);
        assert accountEntity != null;
        if (accountEntity.getAuthenticationProvider().getName().equals(AuthenticationProvider.PENDING.getName())) {
            accountEntity.setEmail(reInviteRequest.getEmail());
            accoutRepository.save(accountEntity);
        }
        sendEmails(reInviteRequest.getEmail(), reInviteRequest.getUrl(), MESSAGE_EXPIRATION, FILE_INVITE);
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        final Runnable runnable = new Runnable() {
            int countdownStarter = EXPIRATION_DATE;

            @SneakyThrows
            @Override
            public void run() {
                countdownStarter--;
                System.out.println(countdownStarter);
                if (countdownStarter < 0) {
                    if (accountEntity.getAuthenticationProvider().getName().equals(AuthenticationProvider.PENDING.getName())) {
                        AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = accoutWorkspaceRoleRepository.findByIdAndId(idWorkspace, accountEntity.getId()).orElse(null);
                        assert accountWorkspaceRoleEntity != null;
                        accoutWorkspaceRoleRepository.delete(accountWorkspaceRoleEntity);
                    }
                    assert workspaceEntity != null;
                    String MESSAGE="<b>Hi "+accountEntity.getEmail()+"</b>,<br/>\n" +
                            "<br/>Unfortunately, your invitation has been expired since you haven't log in to confirm yet.<br/><br/>" +
                            "For further information, please contact the admin of "+workspaceEntity.getName()+".<br/><br/>" +
                            "Many thanks from Team Juggle Fish!";
                    sendEmails(reInviteRequest.getEmail(), "", MESSAGE, "");
                    scheduler.shutdown();
                }
            }
        };
        scheduler.scheduleAtFixedRate(runnable, 0, 1, SECONDS);

    }

    private void sendEmails(String email, String url, String message, String file) throws MessagingException, IOException {
        MimeMessage msg = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);
        helper.setTo(email);
        helper.setSubject(TITLE);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream(file))));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        String ls = System.getProperty(LINE);
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        reader.close();
        String content = stringBuilder.toString();
        if (url.isEmpty() && message.isEmpty()) {
            helper.setText(content, true);
        } else if (file.isEmpty()){
            helper.setText(message+url,true);
        }
        else {
            helper.setText(content + url + message, true);
        }

        sender.send(msg);
    }


    @Override
    public void isActive(Integer idAccount, Integer idWorkspace,ReInviteRequest reInviteRequest) throws MessagingException, IOException {
        WorkspaceEntity workspaceEntity=workspaceRepository.findById(idWorkspace).orElse(null);
        AccountEntity accountEntity=accoutRepository.findById(idAccount).orElse(null);
        assert accountEntity != null;
        assert workspaceEntity != null;
        if (accoutWorkspaceRoleRepository.findByIdAndId(idWorkspace, idAccount).isPresent()) {
            AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = accoutWorkspaceRoleRepository.findByIdAndId(idWorkspace, idAccount).get();
            if (accountWorkspaceRoleEntity.getCodeRole().equals(Role.VIEW.getCode())) {
                accountWorkspaceRoleEntity.setCodeRole(Role.INACTIVE.getCode());
                String MESSAGE_ARCHIVE="<b>Hi "+accountEntity.getEmail()+"</b>,<br/>\n" +
                        "<br/>Unfortunately, you have been temporarily archived in "+workspaceEntity.getName()+".<br/><br/>" +
                        "We will soon enable so you can join with us later.<br/><br/>" +
                        "Many thanks from Team Juggle Fish!";
                sendEmails(accountEntity.getEmail(),"",MESSAGE_ARCHIVE,"" );
            } else if (accountWorkspaceRoleEntity.getCodeRole().equals(Role.INACTIVE.getCode())) {
                accountWorkspaceRoleEntity.setCodeRole(Role.VIEW.getCode());
                String MESSAGE_ENABLE="<b>Hi "+accountEntity.getEmail()+"</b>,<br/>\n" +
                        "<br/>We would like to let you know that you have been enabled in "+workspaceEntity.getName()+".<br/><br/>" +
                        "Click the link below to join with us.<br/><br/>" +
                        "Many thanks from Team Juggle Fish!<br/><br/>";
                sendEmails(accountEntity.getEmail(),reInviteRequest.getUrl(),MESSAGE_ENABLE,"" );
            } else {
                accountWorkspaceRoleEntity.setCodeRole(accountWorkspaceRoleEntity.getCodeRole());
            }
            accoutWorkspaceRoleRepository.save(accountWorkspaceRoleEntity);
        }

    }


}
