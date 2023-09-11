package top.saikaisa.usercenter;

import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import top.saikaisa.usercenter.mapper.UserMapper;
import top.saikaisa.usercenter.model.User;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SampleTest {

    // 注意这里是 Resource，不是 Autowired
    @Resource
    private UserMapper userMapper;

    // @Test 要导入 org.junit.Test 库
    @Test
    public void testSelect() {
        System.out.println(("----- selectAll method test ------"));
        List<User> userList = userMapper.selectList(null);
        Assert.assertEquals(5, userList.size());
        userList.forEach(System.out::println);
    }

}