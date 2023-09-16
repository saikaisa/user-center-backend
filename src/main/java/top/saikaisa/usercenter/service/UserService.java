package top.saikaisa.usercenter.service;

import top.saikaisa.usercenter.model.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Saikai
* @description 用户服务
* @createDate 2023-09-15 21:16:12
*/
public interface UserService extends IService<User> {

    /**
     *
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return  新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);
}
