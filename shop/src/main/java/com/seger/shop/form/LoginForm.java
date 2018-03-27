package com.seger.shop.form;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotEmpty;

/**
 * 登录表单
 *
 * @author: seger.lin
 */

@Data
public class LoginForm {
    @NotEmpty(message = "用户账号必填")
    private String userName;

    @NotEmpty(message = "用户密码必填")
    private String userPwd;
}
