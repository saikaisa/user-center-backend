package top.saikaisa.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.saikaisa.usercenter.model.domain.User;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Saikai
 * @description 用户服务
 * @createDate 2023-09-15 21:16:12
 */
public interface UserService extends IService<User> {
    /**
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @param invitationCode 邀请码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String invitationCode);

    /**
     *
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     *
     * @param originUser 原始用户信息
     * @return 脱敏后的用户信息
     */
    User getSafetyUser(User originUser);

    /**
     * 用户注销
     *
     * @param request
     * @return 1
     */
    int userLogout(HttpServletRequest request);
}
