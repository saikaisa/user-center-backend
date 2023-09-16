package top.saikaisa.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.saikaisa.usercenter.model.domain.User;
import top.saikaisa.usercenter.model.domain.request.UserLoginRequest;
import top.saikaisa.usercenter.model.domain.request.UserRegisterRequest;
import top.saikaisa.usercenter.service.UserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static top.saikaisa.usercenter.constant.UserConstant.ADMIN_RULE;
import static top.saikaisa.usercenter.constant.UserConstant.USER_LOGIN_STATE;


/**
 * @Author Saikai
 * @description 用户接口
 * @createDate 2023-09-16 11:58:26
 */
@RestController     // 适用于编写 restful 风格的 api，返回值默认为 json 类型
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public Long userResister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            return null;
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        // 这里再加一层校验是因为 controller 层是对参数本身的校验，而 service 层是对业务逻辑的校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return null;
        }
        return userService.userRegister(userAccount, userPassword, checkPassword);
    }

    @PostMapping("/login")
    public User userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            return null;
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        // 这里再加一层校验是因为 controller 层是对参数本身的校验，而 service 层是对业务逻辑的校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        return userService.userLogin(userAccount, userPassword, request);
    }

    // 只涉及获取用户信息，不涉及修改，所以使用 get 请求
    @GetMapping("/search")
    public List<User> searchUsers(String username, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return new ArrayList<>();
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            userQueryWrapper.like("username",username);
        }
        List<User> userList = userService.list(userQueryWrapper);
        // 返回脱敏后的数据，这里如果之后逻辑复杂就要放到 Service 层了
        return userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
    }

    @PostMapping("/delete")
    public boolean deleteUsers(@RequestBody Long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return false;
        }
        if (id <= 0) {
            return false;
        }
        return userService.removeById(id);
    }

    /**
     * 判断是否为管理员
     * @param request
     * @return
     */
    private boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj  = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == ADMIN_RULE;
    }
}
