package top.saikaisa.usercenter.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.saikaisa.usercenter.model.domain.User;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    void testAddUser() {
        User user = new User();
        user.setUsername("testSaikai");
        user.setUserAccount("123");
        user.setAvatarUrl("https://pics.saikaisa.top/avatar.jpg");
        user.setGender(0);
        user.setPassword("xxx");
        user.setPhone("123");
        user.setEmail("456");


        boolean result = userService.save(user);
        System.out.println(user.getId());
        assertTrue(result);
    }

    @Test
    void userRegister() {
        String userAccount = "Saikai";
        String userPassword = "";
        String checkPassword = "1234567";
        String invitationCode = "123456";
        long result = userService.userRegister(userAccount, userPassword, checkPassword, invitationCode);
        assertEquals(-1, result);

        userAccount = "sa";
        userPassword = "123456";
        result = userService.userRegister(userAccount, userPassword, checkPassword, invitationCode);
        assertEquals(-1, result);

        userAccount = "Saikai";
        result = userService.userRegister(userAccount, userPassword, checkPassword, invitationCode);
        assertEquals(-1, result);

        userAccount = "Sai kai";
        userPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword, invitationCode);
        assertEquals(-1, result);

        userAccount = "Saikai";
        checkPassword = "123456789";
        result = userService.userRegister(userAccount, userPassword, checkPassword, invitationCode);
        assertEquals(-1, result);

        userAccount = "testSaikai";
        checkPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword, invitationCode);
        assertEquals(-1, result);

        userAccount = "Saikai";
        result = userService.userRegister(userAccount, userPassword, checkPassword, invitationCode);
        assertTrue(result > 0);

    }
}