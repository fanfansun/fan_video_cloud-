package com.imooc.controller;

import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


import com.imooc.pojo.Users;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.service.UserService;
import com.imooc.utils.IMoocJSONResult;
import com.imooc.utils.MD5Utils;

@RestController
public class RegistLoginController extends BasicController {
    // 注入 userService
    // @Resource(name = "userServiceImpl")
    @Autowired
    //@Resource(name = "userServiceImpl")
    private UserService userService;

    @ApiOperation(value = "用户注册", notes = "用户注册的接口")
    @PostMapping("/regist")
    public IMoocJSONResult Regist(@RequestBody Users user) {
        // 1.判断用户名和密码必须不为空
        if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword()))
        {
              return IMoocJSONResult.errorMsg("用户名和密码不能为空");
        }
        // 2.判断用户是否存在
        boolean usernameIsExist = userService.queryUsernameIsExist(user.getUsername());
        // 3.保存用户注册信息
        if (!usernameIsExist) {
            user.setNickname(user.getUsername());
            try {
                user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            user.setFansCounts(0); //设置粉丝数
            user.setReceiveLikeCounts(0); // 收到相同数
            // 对用户对象进行保存
            userService.saveUser(user);

        } else {
          return IMoocJSONResult.errorMsg("用户名已存在，请换一个再试");
        }
        user.setPassword("");
        String uniqueToken = UUID.randomUUID().toString();

        redis.set(USER_REDIS_SESSION+':'+user.getId(),uniqueToken,1000*60*30);

        UsersVO usersV0 = new UsersVO();
        BeanUtils.copyProperties(user, usersV0);
        usersV0.setUserToken(uniqueToken);

        return IMoocJSONResult.ok(usersV0);
    }


    @ApiOperation(value = "用户登录", notes = "用户登录的接口")
    @PostMapping("/login")
    public IMoocJSONResult login(@RequestBody Users users){
        String username = users.getUsername();
        String password = users.getPassword();
        // 1.判断用户名和密码必须不为空
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password))
        {
            return IMoocJSONResult.ok("用户名和密码不能为空");
        }
        // 2.判断用户是否存在
        try {
            System.out.println(MD5Utils.getMD5Str(users.getPassword()));
            Users userResult = userService.queryUserForLogin(username,
                    MD5Utils.getMD5Str(users.getPassword()));
            System.out.println(userResult);
            if (userResult != null)
            {
                userResult.setPassword("");
                return IMoocJSONResult.ok("找到了该用户");
            }
            else {
                return IMoocJSONResult.errorMsg("用户名或密码不正确");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return IMoocJSONResult.ok("函数结束-返回");
    }
}
