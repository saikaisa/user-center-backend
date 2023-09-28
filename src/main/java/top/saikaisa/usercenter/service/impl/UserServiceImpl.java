package top.saikaisa.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;
import org.springframework.stereotype.Service;
import top.saikaisa.usercenter.common.ErrorCode;
import top.saikaisa.usercenter.exception.BusinessException;
import top.saikaisa.usercenter.mapper.InvitationlibMapper;
import top.saikaisa.usercenter.mapper.UserMapper;
import top.saikaisa.usercenter.model.domain.Invitationlib;
import top.saikaisa.usercenter.model.domain.User;
import top.saikaisa.usercenter.service.UserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.*;

import static top.saikaisa.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author Saikai
 * @description 用户服务实现类
 * @createDate 2023-09-15 21:16:12
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private InvitationlibMapper invitationlibMapper;

    // 盐值
    private static final String SALT = "saikai";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String invitationCode) {
        // 1.1 校验
        // 这里使用 apache 的 commons-lang3 包中的 StringUtils 类，便捷地校验字符串是否为空
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, invitationCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度不足");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度不足");
        }

        // 1.2 账户不能包含特殊字符
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%…… &*（）——+|{}【】‘；：”“’。，、？]";
        /*
         * matcher的作用是把一个字符串与正则表达式进行匹配，匹配机制是通过将正则表达式编译成一个Pattern对象，
         * 然后用Pattern对象的matcher方法创建一个Matcher对象，最后调用Matcher对象的方法进行匹配。
         * find() 的返回值是一个boolean类型的值，当匹配成功时返回true，否则返回false。
         */
        Matcher matcher = Pattern.compile(regEx).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号不能包含特殊字符");
        }

        // 1.3 密码和校验密码必须一致
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码和校验密码不一致");
        }

        // 1.4 账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已存在");
        }

        // 1.5 邀请码必须正确
        QueryWrapper<Invitationlib> invQueryWrapper = new QueryWrapper<>();
        invQueryWrapper.eq("invitationCode", invitationCode);
        // 是否重复或者已被使用
        Invitationlib invitationlib = invitationlibMapper.selectOne(invQueryWrapper);
        if (invitationlib == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邀请码不存在");
        }
        if (invitationlib.getIsUsed() == 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邀请码已被使用");
        }
        // 如果邀请码可用，则将其设置为已使用
        invitationlib.setIsUsed(1);
        // 这里要更新一下，才能将 isUsed 的值更新到数据库中
        invitationlibMapper.updateById(invitationlib);

        // 2. 加密
        String md5Password = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        // 3. 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUsername(userAccount);
        user.setAvatarUrl("https://img.51miz.com/Element/00/88/60/42/3cb805be_E886042_a75650be.png");
        user.setPassword(md5Password);
        user.setInvitationCode(invitationCode);
        // 将 user 对象插入到数据库中
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败");
        }

        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1.1 校验
        // 这里使用 apache 的 commons-lang3 包中的 StringUtils 类，便捷地校验字符串是否为空
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码不能为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度不足");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度不足");
        }
        // 1.2 账户不能包含特殊字符
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%…… &*（）——+|{}【】‘；：”“’。，、？]";
        /*
         * matcher的作用是把一个字符串与正则表达式进行匹配，匹配机制是通过将正则表达式编译成一个Pattern对象，
         * 然后用Pattern对象的matcher方法创建一个Matcher对象，最后调用Matcher对象的方法进行匹配。
         * find() 的返回值是一个boolean类型的值，当匹配成功时返回true，否则返回false。
         */
        Matcher matcher = Pattern.compile(regEx).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号不能包含特殊字符");
        }

        // 2. 校验密码
        String md5Password = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("password", md5Password);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword.");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码错误");
        }

        // 3. 脱敏
        User safetyUser = getSafetyUser(user);

        // 4. 记录用户登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);

        return safetyUser;
    }

    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    @Override
    public User getSafetyUser(User originUser){
        if (originUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_NULL_ERROR, "请求的用户不存在");
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setUserStatus(0);
        safetyUser.setInvitationCode(originUser.getInvitationCode());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setUpdateTime(originUser.getUpdateTime());
        return safetyUser;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }
}




