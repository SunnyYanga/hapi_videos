package com.yang.controller;

import com.yang.pojo.Users;
import com.yang.pojo.vo.UsersVo;
import com.yang.service.UserService;
import com.yang.utils.IMoocJSONResult;
import com.yang.utils.MD5Utils;
import io.swagger.annotations.ApiImplicitParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * @author yg
 * @date 2020/8/11 19:50
 */
@RestController
public class RegistLoginController extends BasicController {

    @Autowired
    private UserService userService;

    @PostMapping("/regist")
    public IMoocJSONResult regist(@RequestBody Users users) throws Exception {
        // 1.判断用户名和密码不为空
        if (StringUtils.isEmpty(users.getUsername()) || StringUtils.isEmpty(users.getPassword())) {
            return IMoocJSONResult.errorMsg("用户名和密码不能为空!");
        }
        // 2.判断用户名是否存在
        if (userService.queryUserNameIsExist(users.getUsername())) {
            return IMoocJSONResult.errorMsg("用户名已经存在!");
        }

        // 3.保存
        users.setNickname(users.getUsername());
        users.setPassword(MD5Utils.getMD5Str(users.getPassword()));
        users.setFansCounts(0);
        users.setReceiveLikeCounts(0);
        users.setFollowCounts(0);
        userService.saveUser(users);

        users.setPassword("");
        UsersVo usersVo = setUserRedisSessionToken(users);

        return IMoocJSONResult.ok(usersVo);
    }

    public UsersVo setUserRedisSessionToken(Users users) {
        String uniqueToken = UUID.randomUUID().toString();
        redisOperator.set(USER_REDIS_SESSION + ":" + users.getId(), uniqueToken,  30 * 60 * 30);
        UsersVo usersVo = new UsersVo();
        BeanUtils.copyProperties(users, usersVo);
        usersVo.setUserToken(uniqueToken);
        return usersVo;
    }

    @PostMapping("/login")
    public IMoocJSONResult login(@RequestBody Users users) throws Exception {
        // 1.判断用户名和密码不为空
        if (StringUtils.isEmpty(users.getUsername()) || StringUtils.isEmpty(users.getUsername())) {
            return IMoocJSONResult.errorMsg("用户名和密码不能为空!");
        }

        // 2.查询
        Users user = userService.getUser(users.getUsername(), users.getPassword());
        if (user == null) {
            return IMoocJSONResult.errorMsg("用户名或密码不正确!");
        }
        user.setPassword("");
        UsersVo usersVo = setUserRedisSessionToken(user);
        return IMoocJSONResult.ok(usersVo);
    }

    @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "string", paramType = "query")
    @PostMapping("/logout")
    public IMoocJSONResult logout(String userId) {
        redisOperator.del(USER_REDIS_SESSION + ":" + userId);
        return IMoocJSONResult.ok();
    }
}
