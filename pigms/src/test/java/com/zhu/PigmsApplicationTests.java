package com.zhu;

import com.github.pagehelper.PageInfo;
import com.zhu.mapper.PigMapper;
import com.zhu.mapper.UserMapper;
import com.zhu.pojo.Pig;
import com.zhu.pojo.User;
import com.zhu.service.pig.PigService;
import com.zhu.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

@SpringBootTest
class PigmsApplicationTests {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PigMapper pigMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private PigService pigService;

    @Test
    void contextLoads() throws SQLException {

        System.out.println(dataSource.getClass());
        System.out.println(dataSource.getConnection());
        List<User> userList = userMapper.queryAllUser();
        for (User user : userList) {
            System.out.println(user);
        }

    }

    @Test
    void contextLoads2() throws SQLException {
        Pig pig = pigMapper.selectByPrimaryKey(1);
        System.out.println(pig);
    }

    @Test
    void contextLoads3() throws SQLException {
        PageInfo<User> userPageInfo = userService.selectAllUserByPageQuery(null, 1, 10);
        List<User> list = userPageInfo.getList();
        for (User user : list) {
            System.out.println(user);
        }
        //System.out.println(userPageInfo);
    }

    @Test
    void contextLoads4() throws SQLException {
        PageInfo<Pig> userPageInfo = pigService.selectAllPigByPageQuery(null, 1, 10);
        List<Pig> list = userPageInfo.getList();
        for (Pig pig : list) {
            System.out.println(pig);
        }
        System.out.println("===========");
        System.out.println(userPageInfo);
    }
}
