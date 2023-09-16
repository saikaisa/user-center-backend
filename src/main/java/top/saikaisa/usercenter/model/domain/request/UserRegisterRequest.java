package top.saikaisa.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author Saikai
 * @description 用户注册请求体
 * @createDate 2023-09-16 12:07:12
 */
@Data
public class UserRegisterRequest implements Serializable {
    // 生成序列化 id
    private static final long serialVersionUID = 8953267214891L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;
}
