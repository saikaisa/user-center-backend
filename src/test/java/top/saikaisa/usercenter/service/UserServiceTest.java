package top.saikaisa.usercenter.service;
import java.util.Date;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.saikaisa.usercenter.model.User;

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
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        assertEquals(-1, result);

        userAccount = "sa";
        userPassword = "123456";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        assertEquals(-1, result);

        userAccount = "Saikai";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        assertEquals(-1, result);

        userAccount = "Sai kai";
        userPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        assertEquals(-1, result);

        userAccount = "Saikai";
        checkPassword = "123456789";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        assertEquals(-1, result);

        userAccount = "testSaikai";
        checkPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        assertEquals(-1, result);

        userAccount = "Saikai";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        assertTrue(result > 0);

    }
}