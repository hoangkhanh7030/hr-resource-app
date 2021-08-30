package com.ces.intern.hr.resourcing.demo.controller;

import com.ces.intern.hr.resourcing.demo.entity.AccountEntity;
import com.ces.intern.hr.resourcing.demo.entity.AccountWorkspaceRoleEntity;
import com.ces.intern.hr.resourcing.demo.entity.WorkspaceEntity;
import com.ces.intern.hr.resourcing.demo.http.request.InviteRequest;
import com.ces.intern.hr.resourcing.demo.http.response.message.MessageResponse;
import com.ces.intern.hr.resourcing.demo.repository.AccoutRepository;
import com.ces.intern.hr.resourcing.demo.repository.AccoutWorkspaceRoleRepository;
import com.ces.intern.hr.resourcing.demo.repository.WorkspaceRepository;
import com.ces.intern.hr.resourcing.demo.utils.AuthenticationProvider;
import com.ces.intern.hr.resourcing.demo.utils.ResponseMessage;
import com.ces.intern.hr.resourcing.demo.utils.Role;
import com.ces.intern.hr.resourcing.demo.utils.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;


import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;

@RestController
@RequestMapping(value = "/api/v1/workspaces")
public class EmailController {
    private static final String MESSAGE="<br/><br/><i>The invitation will be expired after 2 days and you cannot join with us.</i><br/><br/>\n" +
            "\n" +
            "<i>Thanks from Team Juggle Fish</i>";
    private static final String TITLE = "Invited Workspace";
    private final JavaMailSender sender;
    private final AccoutRepository accoutRepository;
    private final AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository;
    private final WorkspaceRepository workspaceRepository;

    @Autowired
    private EmailController(JavaMailSender sender,
                            AccoutRepository accoutRepository,
                            AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository,
                            WorkspaceRepository workspaceRepository) {
        this.sender = sender;
        this.accoutRepository = accoutRepository;
        this.accoutWorkspaceRoleRepository = accoutWorkspaceRoleRepository;
        this.workspaceRepository = workspaceRepository;
    }


    @PostMapping("/{idWorkspace}/invitedemail")
    private MessageResponse sendEmail(@RequestBody InviteRequest inviteRequest,
                                      @PathVariable Integer idWorkspace) throws Exception {
        MimeMessage msg = sender.createMimeMessage();
        String[] arr = new String[inviteRequest.getEmail().size()];
        inviteRequest.getEmail().toArray(arr);
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);
        helper.setTo(arr);
        helper.setSubject(TITLE);
        BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/file/Invite.txt")));
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

        helper.setText(content + inviteRequest.getUrl()+MESSAGE, true);

        WorkspaceEntity workspaceEntity = workspaceRepository.findById(idWorkspace).orElse(null);


        if (!inviteRequest.getEmail().isEmpty()) {
            for (String email : inviteRequest.getEmail()) {
                if (accoutRepository.findByEmail(email).isPresent()) {
                    AccountEntity accountEntity = accoutRepository.findByEmail(email).get();
                    if (accoutWorkspaceRoleRepository.findByIdAndId(idWorkspace, accountEntity.getId()).isPresent()) {
                        return new MessageResponse(ResponseMessage.EMAIL_INVITE,Status.FAIL.getCode());
                    } else {
                        AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = new AccountWorkspaceRoleEntity();
                        accountWorkspaceRoleEntity.setAccountEntity(accountEntity);
                        accountWorkspaceRoleEntity.setWorkspaceEntity(workspaceEntity);
                        accountWorkspaceRoleEntity.setCodeRole(Role.VIEW.getCode());
                        accoutWorkspaceRoleRepository.save(accountWorkspaceRoleEntity);
                    }
                } else {
                    AccountEntity accountEntity = new AccountEntity();
                    accountEntity.setEmail(email);
                    accountEntity.setAuthenticationProvider(AuthenticationProvider.PENDING);
                    accountEntity.setCreatedDate(new Date());
                    accoutRepository.save(accountEntity);
                    AccountEntity account = accoutRepository.findByEmailAndProvider(email).orElse(null);
                    AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = new AccountWorkspaceRoleEntity();
                    accountWorkspaceRoleEntity.setAccountEntity(account);
                    accountWorkspaceRoleEntity.setWorkspaceEntity(workspaceEntity);
                    accountWorkspaceRoleEntity.setCodeRole(Role.VIEW.getCode());
                    accoutWorkspaceRoleRepository.save(accountWorkspaceRoleEntity);

                }
            }
        } else return new MessageResponse(ResponseMessage.IS_EMPTY, Status.FAIL.getCode());
        try {
            sender.send(msg);
            final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            final Runnable runnable = new Runnable() {
                int countdownStarter = 172800;
                @Override
                public void run() {
                    countdownStarter--;
                    if(countdownStarter<0){
                        for (String email : inviteRequest.getEmail()){
                            AccountEntity accountEntity=accoutRepository.findByEmail(email).orElse(null);
                            if (accountEntity.getAuthenticationProvider().getName().equals(AuthenticationProvider.PENDING.getName())){
                                AccountWorkspaceRoleEntity accountWorkspaceRoleEntity=accoutWorkspaceRoleRepository.findByIdAndId(idWorkspace,accountEntity.getId()).orElse(null);
                                accoutWorkspaceRoleRepository.delete(accountWorkspaceRoleEntity);
                            }
                        }
                        scheduler.shutdown();
                    }
                }
            };
            scheduler.scheduleAtFixedRate(runnable, 0, 1, SECONDS);
            return new MessageResponse(ResponseMessage.EMAIL_SENDT, Status.SUCCESS.getCode());

        } catch (Exception e) {
            return new MessageResponse(ResponseMessage.EMAIL_ERROR + e, Status.FAIL.getCode());
        }
    }

}
