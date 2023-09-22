package top.saikaisa.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.saikaisa.usercenter.common.BaseResponse;
import top.saikaisa.usercenter.common.ErrorCode;
import top.saikaisa.usercenter.common.ResultUtils;
import top.saikaisa.usercenter.exception.BusinessException;
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
    public BaseResponse<Long> userResister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String invitationCode = userRegisterRequest.getInvitationCode();
        // 这里再加一层校验是因为 controller 层是对参数本身的校验，而 service 层是对业务逻辑的校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, invitationCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword, invitationCode);
        return ResultUtils.success(result);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        // 这里再加一层校验是因为 controller 层是对参数本身的校验，而 service 层是对业务逻辑的校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_NULL_ERROR, "请求的对象不存在");
        }
        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    // 获取当前登录用户的信息
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        // 判断 session 缓存里的用户是否为空
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        }
        long userId = currentUser.getId();
        // TODO：校验用户是否合法
        User user = userService.getById(userId);
        /* 在 getSafetyUser() 这个方法里还进行了对 user 空值的判断，因为 session 缓存里的用户可能已经过期了。
         * 即 session 中的用户 currentUser 还在，但是数据库中该用户已被删除，查出的 user 为空，
         * 此时如果直接调用该方法对 user 进行脱敏则会报错。
         */
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    // 只涉及获取用户信息，不涉及修改，所以使用 get 请求
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            userQueryWrapper.like("username", username);
        }
        List<User> userList = userService.list(userQueryWrapper);
        // 返回脱敏后的数据，这里如果之后逻辑复杂就要放到 Service 层了
        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(list);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUsers(@RequestBody Long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 判断是否为管理员
     *
     * @param request
     * @return
     */
    private boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == ADMIN_RULE;
    }
}
