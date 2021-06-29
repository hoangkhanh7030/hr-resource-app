package com.ces.intern.hr.resourcing.demo.http.response;
import com.ces.intern.hr.resourcing.demo.dto.AccountDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String jwt;
    private AccountDTO accountDTO;
    private Integer status;
}
