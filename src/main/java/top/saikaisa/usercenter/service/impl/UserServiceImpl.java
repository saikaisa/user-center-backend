package top.saikaisa.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;
import top.saikaisa.usercenter.model.User;
import top.saikaisa.usercenter.mapper.UserMapper;
import org.springframework.stereotype.Service;
import top.saikaisa.usercenter.service.UserService;

import javax.annotation.Resource;
import java.util.regex.*;

/**
* @author Saikai
* @description 用户服务实现类
* @createDate 2023-09-15 21:16:12
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1.1 校验
        // 这里使用 apache 的 commons-lang3 包中的 StringUtils 类，便捷地校验字符串是否为空
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return -1;
        }
        if (userAccount.length() < 4) {
            return -1;
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            return -1;
        }

        // 1.2 账户不能包含特殊字符
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%…… &*（）——+|{}【】‘；：”“’。，、？]";
        /*
         * matcher的作用是把一个字符串与正则表达式进行匹配，匹配机制是通过将正则表达式编译成一个Pattern对象，
         * 然后用Pattern对象的matcher方法创建一个Matcher对象，最后调用Matcher对象的方法进行匹配。
         * find() 的返回值是一个boolean类型的值，当匹配成功时返回true，否则返回false。
         */
        Matcher matcher = Pattern.compile(regEx).matcher(userAccount);
        if(matcher.find()){
            return -1;
        }

        // 1.3 密码和校验密码必须一致
        if (!userPassword.equals(checkPassword)) {
            return -1;
        }

        // 1.4 账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            return -1;
        }

        // 2. 加密
        final String SALT = "saikai";
        String md5Password = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        // 3. 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setPassword(md5Password);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            return -1;
        }

        return user.getId();
    }
}




